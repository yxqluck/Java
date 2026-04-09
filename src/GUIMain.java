import java.sql.SQLException;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.CardLayout;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

public class GUIMain extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField name;
	private JTextField password;

	/**
	 * Create the panel.
	 */
	public GUIMain() {
		setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 450, 56);
		panel.setBackground(new Color(240, 240, 240));
		add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("档案管理系统");
		lblNewLabel.setBounds(153, 18, 144, 28);
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 24));
		panel.add(lblNewLabel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(0, 55, 450, 196);
		add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("用户名：");
		lblNewLabel_1.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(56, 62, 64, 15);
		panel_1.add(lblNewLabel_1);
		
		name = new JTextField();
		name.setBounds(130, 59, 262, 21);
		panel_1.add(name);
		name.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("密 码：");
		lblNewLabel_2.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel_2.setBounds(56, 110, 64, 14);
		panel_1.add(lblNewLabel_2);
		
		password = new JTextField();
		password.setBounds(130, 107, 262, 20);
		panel_1.add(password);
		password.setColumns(10);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(0, 251, 450, 49);
		add(panel_2);
		
		JButton btnNewButton = new JButton("登录");
		btnNewButton.setFont(new Font("宋体", Font.PLAIN, 18));
		btnNewButton.setBounds(10, 10, 192, 28);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
                    AbstractUser user=DataProcessing.searchUser(name.getText(), password.getText());
                    if(user==null){
//                        System.out.println("用户名或密码错误");
//                        System.out.println("已自动退出登录界面");
                        JOptionPane.showMessageDialog(null,
                                "用户名或密码错误",
                                "登录失败",
                                JOptionPane.ERROR_MESSAGE);
                    }else{
//                        System.out.println("登录成功");
                    	JOptionPane.showMessageDialog(null,
                                "登录成功",
                                "提示",
                                JOptionPane.INFORMATION_MESSAGE);
                    	// 1. 添加新窗口
                    	switch(user.getRole()) {
                    	case "administrator":
                    		AdministratorGUI admin = new AdministratorGUI(user);
                        	admin.setVisible(true);
                        	break;
                    	case "operator":
	                    	OperatorGUI opera = new OperatorGUI(user);
	                    	opera.setVisible(true);
	                    	break;
                    	case "browser":
                    		BrowserGUI brow = new BrowserGUI(user);
	                    	brow.setVisible(true);
	                    	break;
                    	}
                    	
                        
                        // 2. 关闭当前登录窗口
                        JFrame nowFrame = (JFrame) getTopLevelAncestor();
                        nowFrame.dispose();
                        
//                        user.showMenu();
                    }
                } catch (SQLException e1) {
//                    System.out.println(e1.getMessage());
                	JOptionPane.showMessageDialog(null,
                            "数据库错误：" + e1.getMessage(),
                            "错误",
                            JOptionPane.ERROR_MESSAGE);
                }
			}
		});
		panel_2.setLayout(null);
		panel_2.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("退出");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnNewButton_1.setFont(new Font("宋体", Font.PLAIN, 18));
		btnNewButton_1.setBounds(248, 10, 192, 28);
		panel_2.add(btnNewButton_1);

	}
}

