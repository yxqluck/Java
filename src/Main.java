import java.sql.SQLException;
import java.util.Scanner;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
//        while(true){
//            System.out.println("欢迎使用档案管理系统");
//            System.out.println("1.登录");
//            System.out.println("2.退出");
//            System.out.println("请输入您的选择:");
//            Scanner sc=new Scanner(System.in);
//            switch(sc.nextInt()){
//                case 1:
                    try {
                        DataProcessing.connectToDatabase();
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
//                        break;
                    }
//                    System.out.println("请登录");
//                    System.out.println("请输入用户名:");
//                    String name=sc.next();
//                    System.out.println("请输入密码:");
//                    String password=sc.next();
//                    try {
//                        AbstractUser user=DataProcessing.searchUser(name, password);
//                        if(user==null){
//                            System.out.println("用户名或密码错误");
//                            System.out.println("已自动退出登录界面");
//                        }else{
//                            System.out.println("登录成功");
//                            user.showMenu();
//                        }
//                    } catch (SQLException e) {
//                        System.out.println(e.getMessage());
//                    }
//                    break;
//                case 2:
//                    sc.close();
//                    AbstractUser.exitSystem();
//                default:
//                    System.out.println("输入错误");
//                    System.out.println("请重新输入");
//            }
//                
//        }
		JFrame frame = new JFrame("档案管理系统");
				
				// 把你的登录面板放进去
				GUIMain loginPanel = new GUIMain();
				frame.add(loginPanel);
				
				// 设置窗口大小（和你面板大小一致）
				frame.setSize(450, 330);
				// 居中显示
				frame.setLocationRelativeTo(null);
				// 点击关闭退出程序
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				// 禁止拉伸
				frame.setResizable(false);
				
				// 显示窗口
				frame.setVisible(true);
		
		
    }
}
