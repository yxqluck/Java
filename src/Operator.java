import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Scanner;
/**
 * Operator类继承AbstractUser类
 * 实现了父类的showMenu()方法
 * 新增了uploadFile()方法
 */
public class Operator extends AbstractUser { 
	public Operator(String name,String password,String role){
		super(name,password,role);
	}
	@Override
        @SuppressWarnings({"UseSpecificCatch", "CallToPrintStackTrace"})
	public void showMenu(){ 
		while(true){
			System.out.println("欢迎操作员 " + getName() + " 登录系统");
			System.out.println("1.上传档案");
			System.out.println("2.下载档案");
			System.out.println("3.档案列表");
			System.out.println("4.修改个人密码");
			System.out.println("5.退出登录");
			System.out.println("请输入您的选择：");
			Scanner sc=new Scanner(System.in);
			switch (sc.nextInt()){
				case 1: 
					
					break;
				case 2: 
					System.out.println("请输入档案号:");
					String id=sc.next();
					try {
						downloadArchive(id,this.downloadDir);
					} catch (Exception e) {
					}
					
					break;
				case 3: 
				try {
					listAllArchives();
				} catch (SQLException e) {
				}
					
					break;
				case 4: 
					System.out.println("请输入新密码:");
					String newpossword=sc.next();
					try {
						changeSelfInfo(newpossword);
					} catch (SQLException e) {
					}
					
					break;
				case 5: 
					System.out.println("已退出登录");
                    return;
				default: 
					System.out.println("输入错误，请重新输入");
					break;

			}
		}
	}
    /**
     * 上传文件
     * @return 上传是否成功
     */
	public static boolean uploadArchive(String archiveId,String creator,String description,String fileName,String filePath){
		final int bufferSize = 8192;

//		Scanner sc=new Scanner(System.in);
//		System.out.println("请输入档案号:");
//		String archiveId=sc.next();
//		System.out.println("请输入创建者:"); 
//		String creator=sc.next();
//		System.out.println("请输入描述:");
//		String description=sc.next();
//		System.out.println("请输入文件名:");
//		String fileName=sc.next();
//
//		System.out.println("请输入文件路径:");
//		String filePath=sc.next();

		if (filePath == null || filePath.trim().isEmpty()) {
            System.err.println("目标路径不能为空");
            return false;
        }

		LocalDateTime timestamp=LocalDateTime.now();
		
		Archive archive=new Archive(archiveId, creator, timestamp, description, fileName);

		try {
			if(DataProcessing.insertArchive(archive)){
				

				try {
					// 显示档案详细信息
					System.out.println("\n=== 正在上传档案 ===");
					System.out.println("档案号：" + archive.getArchiveId());
					System.out.println("文件名：" + archive.getFileName());
					System.out.println("描述：" + archive.getDescription());
					System.out.println("创建者：" + archive.getCreator());

					System.out.println("档案源文件路径：" + filePath);
					System.out.println("目标文件路径：" + archiveDir);

					// 构建档案文件的完整路径
					File archiveFile = new File(filePath);

					// 安全检查：验证文件是否存在且为有效文件
					if (!archiveFile.exists() || !archiveFile.isFile()) {
						System.err.println("\n警告：档案文件不存在：" + archiveFile.getAbsolutePath());
						return false;
					}

					// 创建目标目录（如果不存在）
					File targetDir = new File(archiveDir);

					// 构建目标文件的完整路径
					File targetFile = new File(targetDir, archiveFile.getName());

					// 使用 try-with-resources 确保资源正确关闭
					// 通过缓冲流实现文件的高效复制
					byte[] buffer = new byte[bufferSize];
					try (BufferedInputStream inFile = new BufferedInputStream(new FileInputStream(archiveFile));
						BufferedOutputStream outFile = new BufferedOutputStream(new FileOutputStream(targetFile))) {
						
						int bytesRead;
						while ((bytesRead = inFile.read(buffer)) != -1) {
							outFile.write(buffer, 0, bytesRead);
						}
					}

					System.out.println("文件上传成功：" + targetFile.getAbsolutePath());
					return true;
				} catch (IOException e) {
					// 捕获 IO 异常
					System.err.println("上传过程中发生 IO 错误：" + e.getMessage());
					return false;
				} catch (Exception e) {
					// 捕获未知异常并返回失败
					System.err.println("上传过程中发生未知错误：" + e.getMessage());
					return false;
				}

			}else{
				return false;
			}
		} catch (SQLException e) {
			System.out.println("数据库连接异常"+e.getMessage());
			return false;
		}
	}
}
