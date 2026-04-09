import java.sql.SQLException;
import java.util.Scanner;

/**
 * Administrator类继承AbstractUser类
 * 实现了父类的showMenu()方法
 * 新增了addUser()、delUser()、changeUserInfo()、listUser()方法
 */
public class Administrator extends AbstractUser { 
	public Administrator(String name,String password,String role){
		super(name,password,role);
	}
	@Override
        @SuppressWarnings("UseSpecificCatch")
	public void showMenu(){ 
		while(true){
			System.out.println("欢迎管理员 " + getName() + " 登录系统");
			System.out.println("1.新增用户");
			System.out.println("2.删除用户");
			System.out.println("3.修改用户");
			System.out.println("4.用户列表");
			System.out.println("5.下载档案");
			System.out.println("6.档案列表");
			System.out.println("7.修改个人密码");
			System.out.println("8.退出登录");
			System.out.println("请输入您的选择：");
			Scanner sc=new Scanner(System.in);
			switch (sc.nextInt()) { 
				case 1: 
					addUser();
					break;
					
				case 2: 
					delUser();
					break;
					
				case 3: 
					changeUserInfo();
					break;

				case 4: 
					listUser();
					break;
					
				case 5: 
					System.out.println("请输入档案号:");
					String id=sc.next();
					
					try {
						downloadArchive(id,this.downloadDir);
					} catch (Exception e) {
					}
					
					break;

				case 6: 
					try {
						listAllArchives();
					} catch (Exception e) {
					}
					break;

				case 7: 
					System.out.println("请输入新密码:");
					String newpossword=sc.next();
					try {
						changeSelfInfo(newpossword);
					} catch (SQLException e) {
						System.out.println("数据库查询错误"+e.getMessage());
					}
					break;

				case 8: 
					System.out.println("已退出登录");
                    return;
					
				default: 
					System.out.println("输入错误，请重新输入");
					break;
			}
		}
	}
    /**
     * 新增用户
     * @return 是否新增成功
     */
	public boolean addUser(){
		Scanner sc=new Scanner(System.in);
		System.out.println("请输入用户名:");
		String newname=sc.next();
		System.out.println("请输入密码:");
		String newpassword=sc.next();
		System.out.println("请输入用户角色:");
		String newrole=sc.next();
        try {
            return DataProcessing.insertUser(newname, newpassword, newrole);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
		
	}
    /**
     * 删除用户
     * @return 是否删除成功
     */
	public boolean delUser(){
		Scanner sc=new Scanner(System.in);
		System.out.println("请输入用户名:");
		String deletename=sc.next();
        try {
            return DataProcessing.deleteUser(deletename);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
	}
    /**
     * 修改用户信息
     * @return 是否修改成功
     */
	public boolean changeUserInfo(){
		Scanner sc=new Scanner(System.in);
		System.out.println("请输入用户名:");
		String changename=sc.next();
		System.out.println("请输入新密码:");
		String changepassword=sc.next();
		System.out.println("请输入用户的新角色:");
		String changerole=sc.next();
        try {
            return DataProcessing.updateUser(changename, changepassword, changerole);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
		
	}
        @SuppressWarnings({"UseSpecificCatch", "CallToPrintStackTrace"})
	public boolean listUser(){
        try {
            if(DataProcessing.getAllUsers().isEmpty()){
                System.out.println("没有用户");
                return false;
		    }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
		try {
            for(AbstractUser user:DataProcessing.getAllUsers()){
                System.out.println(user.getName()+" "+user.getRole());
            }
            try {
                System.out.println("输入回车键返回");
                System.in.read();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
		
	}
}
