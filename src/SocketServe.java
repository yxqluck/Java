import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServe {
    // 服务器监听的端口号
    private static final int PORT = 12345;
    // 文件传输缓冲区大小
    private static final int BUFFER_SIZE = 8192;
    // 服务器存储文件的目录名称
    private static final String STORAGE_DIR ="..\\archive_files";

    /**
     * 程序主入口，启动服务器
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        new SocketServe().runServer();
    }

    /**
     * 运行服务器主循环，监听客户端连接
     */
    public void runServer() {
        // 创建存储目录，如果不存在则创建
        File storageDir = new File(STORAGE_DIR);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        int i=0;
        // 使用 try-with-resources 确保 ServerSocket 在结束时关闭
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("SocketServe 已启动，监听端口 " + PORT);

            // 无限循环，持续接受客户端连接
            while (true) {
                try {
                    i++;
                    // 阻塞等待客户端连接
                    Socket connection = serverSocket.accept();
                    // 为每个客户端连接创建独立线程处理
                    new CreateSocketThread(connection).start();
                    System.out.println("启动线程 " + i);
                } catch (IOException e) {
                    System.err.println("处理客户端连接时发生错误：" + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("无法启动服务器：" + e.getMessage());
        }
    }

    /**
     * 处理单个客户端的连接请求
     *
     * @param connection 客户端的 Socket 连接
     */
    static void handleClient(Socket connection) {
        // 使用 try-with-resources 自动关闭 socket 和流
        try (Socket socket = connection;
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

            while (true) {
                // 读取客户端发送的命令；客户端关闭连接时退出循环
                String command;
                try {
                    command = input.readUTF();
                } catch (IOException eof) {
                    break;
                }

                // 根据命令类型执行相应操作
                if ("UPLOAD".equalsIgnoreCase(command)) {
                    // 处理文件上传
                    receiveUpload(input, output);
                } else if ("DOWNLOAD".equalsIgnoreCase(command)) {
                    // 处理文件下载
                    sendDownload(input, output);
                } else {
                    // 未知命令，返回错误信息
                    output.writeUTF("ERROR");
                    output.writeUTF("Unknown command: " + command);
                    output.flush();
                }
            }
        } catch (IOException e) {
            // System.err.println("客户端通信失败：" + e.getMessage());
        }
    }

    /**
     * 接收客户端上传的文件
     *
     * @param input  输入流，用于读取客户端数据
     * @param output 输出流，用于向客户端发送响应
     * @throws IOException 如果发生IO错误
     */
    private static void receiveUpload(DataInputStream input, DataOutputStream output) throws IOException {
        // 读取文件名
        String requestedName = input.readUTF();
        // 获取安全文件名，防止路径遍历攻击（只保留文件名部分）
        String safeFileName = new File(requestedName).getName();
        // 读取文件大小
        long fileLength = input.readLong();

        // 构建目标文件路径
        File target = new File(STORAGE_DIR, safeFileName);
        
        // 写入文件内容
        try (BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(target))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            long remaining = fileLength; // 剩余需要读取的字节数
            
            // 循环读取数据直到文件全部接收完毕
            while (remaining > 0) {
                // 计算本次最多读取的字节数
                int read = input.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                if (read == -1) {
                    // 如果流意外结束，抛出异常
                    throw new IOException("Unexpected end of stream during upload");
                }
                // 将读取的数据写入本地文件
                fileOut.write(buffer, 0, read);
                remaining -= read; // 更新剩余字节数
            }
            fileOut.flush(); // 刷新缓冲区
        }

        // 向客户端发送成功响应
        output.writeUTF("OK");
        output.flush();
        System.out.println("文件上传成功: " + safeFileName + " (" + fileLength + " 字节)"); // 添加成功打印信息
    }

    /**
     * 向客户端发送文件（下载）
     *
     * @param input  输入流，用于读取客户端请求
     * @param output 输出流，用于向客户端发送文件数据
     * @throws IOException 如果发生IO错误
     */
    private static void sendDownload(DataInputStream input, DataOutputStream output) throws IOException {
        // 读取请求的文件名
        String requestedName = input.readUTF();
        // 获取安全文件名，防止路径遍历攻击
        String safeFileName = new File(requestedName).getName();
        // 构建源文件路径
        File sourceFile = new File(STORAGE_DIR, safeFileName);

        // 检查文件是否存在且为普通文件
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            output.writeUTF("ERROR");
            output.writeUTF("File not found: " + safeFileName);
            return;
        }

        // 发送成功响应
        output.writeUTF("OK");
        // 发送文件大小
        output.writeLong(sourceFile.length());
        output.flush();

        // 读取文件并发送给客户端
        try (BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(sourceFile))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int count;
            // 循环读取文件内容并写入输出流
            while ((count = fileIn.read(buffer)) != -1) {
                output.write(buffer, 0, count);
            }
            output.flush(); // 刷新缓冲区，确保所有数据已发送
        }
        System.out.println("文件下载成功: " + safeFileName + " (" + sourceFile.length() + " 字节)"); // 添加成功打印信息
    }
}

class CreateSocketThread extends Thread{
    private final Socket connection;

    public CreateSocketThread(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        SocketServe.handleClient(connection);
    }
}