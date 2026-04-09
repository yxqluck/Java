import java.sql.SQLException;
import java.util.Scanner;

/**
 * Browser类继承AbstractUser类
 * 实现了父类的showMenu()方法
 */
public class Browser extends AbstractUser {
	public Browser(String name,String password,String role){
		super(name,password,role);
	}
	@Override
        @SuppressWarnings({"UseSpecificCatch", "CallToPrintStackTrace"})
	public void showMenu(){ 
		while(true){
			System.out.println("欢迎浏览者 " + getName() + " 登录系统");
			System.out.println("1.下载档案");
			System.out.println("2.档案列表");
			System.out.println("3.修改个人密码");
			System.out.println("4.退出登录");
			System.out.println("请输入您的选择：");
			Scanner sc=new Scanner(System.in);
			switch(sc.nextInt()){
				case 1: 
					System.out.println("请输入档案号:");
					String id=sc.next();
					try {
						downloadArchive(id,this.downloadDir);
					} catch (Exception e) {
					}
					break;
				case 2: 
				try {
					listAllArchives();
				} catch (Exception e) {
				}
					break;
				case 3: 
					System.out.println("请输入新密码:");
					String newpossword=sc.next();
					try {
						changeSelfInfo(newpossword);
					} catch (SQLException e) {
						System.out.println("数据库查询错误"+e.getMessage());
					}
					
					break;
				case 4: 
					System.out.println("已退出登录");
                    return;
				default: 
					System.out.println("输入错误，请重新输入");
					break;
			}
			
		}
	}
}
