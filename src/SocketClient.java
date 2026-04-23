import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketClient {
   // 定义缓冲区大小，用于文件读写操作
   private static final int BUFFER_SIZE = 8192;

   private String chatServer; // 此应用程序的主机服务器地址
   private int serverPort; // 服务器端口号
   private Socket client; // 用于与服务器通信的套接字
   private DataOutputStream output; // 发送到服务器的数据输出流
   private DataInputStream input; // 从服务器接收的数据输入流

   /**
    * 构造函数，初始化服务器地址和端口
    *
    * @param host 服务器主机地址
    * @param port 服务器端口号
    */
   public SocketClient(String host, int port) {
      this.chatServer = host;
      this.serverPort = port;
   }

   /**
    * 建立与服务器的连接，并初始化输入输出流
    *
    * @throws IOException 如果连接失败或IO错误
    */
   public void connect() throws IOException {
      // 创建socket连接
      client = new Socket(chatServer, serverPort);
      // 初始化输出流
      output = new DataOutputStream(client.getOutputStream());
      // 初始化输入流
      input = new DataInputStream(client.getInputStream());
   }

   /**
    * 确保当前客户端处于可用连接状态；如果没有连接则重新建立连接。
    *
    * @throws IOException 如果重新连接失败
    */
   private void ensureConnected() throws IOException {
      if (client == null || client.isClosed() || output == null || input == null) {
         connect();
      }
   }

   /**
    * 关闭连接，包括输入流、输出流和socket
    * 忽略关闭过程中可能发生的异常
    */
   public void closeConnection() {
      try {
         if (output != null) {
            output.close();
         }
      } catch (IOException ignored) {
      }

      try {
         if (input != null) {
            input.close();
         }
      } catch (IOException ignored) {
      }

      try {
         if (client != null && !client.isClosed()) {
            client.close();
         }
      } catch (IOException ignored) {
      }

      output = null;
      input = null;
      client = null;
   }

   /**
    * 上传文件到服务器
    *
    * @param localFilePath 本地文件路径
    * @param remoteFileName 远程文件名，如果为空则使用本地文件名
    * @return 如果上传成功返回true，否则返回false
    * @throws IOException 如果发生IO错误
    */
   public boolean uploadFile(String localFilePath, String remoteFileName) throws IOException {
      File file = new File(localFilePath);
      // 检查文件是否存在且为普通文件
      if (!file.exists() || !file.isFile()) {
         return false;
      }

      // 如果未指定远程文件名，则使用本地文件名
      if (remoteFileName == null || remoteFileName.trim().isEmpty()) {
         remoteFileName = file.getName();
      }

      ensureConnected();
      try {
         // 发送上传命令
         output.writeUTF("UPLOAD");
         // 发送文件名
         output.writeUTF(remoteFileName);
         // 发送文件大小
         output.writeLong(file.length());

         // 读取本地文件并发送数据
         try (BufferedInputStream fileInput = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            // 循环读取文件内容并写入输出流
            while ((bytesRead = fileInput.read(buffer)) != -1) {
               output.write(buffer, 0, bytesRead);
            }
         }

         // 刷新输出流，确保所有数据已发送
         output.flush();

         // 读取服务器响应
         String serverResponse = input.readUTF();
         // 判断服务器是否返回成功标志
         return "OK".equalsIgnoreCase(serverResponse);
      } catch (IOException e) {
         closeConnection();
         throw e;
      }
   }

   /**
    * 从服务器下载文件
    *
    * @param remoteFileName 远程文件名
    * @param localSavePath 本地保存路径（可以是目录或完整文件路径）
    * @return 如果下载成功返回true，否则返回false
    * @throws IOException 如果发生IO错误
    */
   public boolean downloadFile(String remoteFileName, String localSavePath) throws IOException {
      // 检查远程文件名是否有效
      if (remoteFileName == null || remoteFileName.trim().isEmpty()) {
         return false;
      }

      File destination = new File(localSavePath);
      // 如果指定的是目录，则在该目录下创建以远程文件名命名的文件
      if (destination.isDirectory()) {
         destination = new File(destination, remoteFileName);
      }

      ensureConnected();
      try {
         // 发送下载命令
         output.writeUTF("DOWNLOAD");
         // 发送文件名
         output.writeUTF(remoteFileName);
         output.flush();

         // 读取服务器响应
         String serverResponse = input.readUTF();
         // 如果服务器返回非OK，则下载失败
         if (!"OK".equalsIgnoreCase(serverResponse)) {
            return false;
         }

         // 读取文件大小
         long fileLength = input.readLong();
         
         // 创建文件输出流用于保存下载的文件
         try (BufferedOutputStream fileOutput = new BufferedOutputStream(new FileOutputStream(destination))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            long remaining = fileLength; // 剩余需要读取的字节数
            
            // 循环读取数据直到文件全部接收完毕
            while (remaining > 0) {
               // 计算本次最多读取的字节数
               int bytesRead = input.read(buffer, 0, (int) Math.min(buffer.length, remaining));
               if (bytesRead == -1) {
                  break; // 如果到达流末尾，跳出循环
               }
               // 将读取的数据写入本地文件
               fileOutput.write(buffer, 0, bytesRead);
               remaining -= bytesRead; // 更新剩余字节数
            }
            fileOutput.flush(); // 刷新缓冲区
         }

         // 检查文件是否成功创建
         return destination.exists();
      } catch (IOException e) {
         closeConnection();
         throw e;
      }
   }
}