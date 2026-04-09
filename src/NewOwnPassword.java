import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class NewOwnPassword extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField new_password;
	private JButton btnNewButton;
	private AbstractUser now_user;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					NewOwnPassword frame = new NewOwnPassword();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public NewOwnPassword(AbstractUser now_user) {
		this.now_user=now_user;
		setTitle("修改个人密码");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // 关闭只关这个窗口，不关整个程序
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("新密码：");
		lblNewLabel_1.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(50, 101, 71, 14);
		contentPane.add(lblNewLabel_1);
		
		new_password = new JTextField();
		new_password.setColumns(10);
		new_password.setBounds(146, 98, 232, 21);
		contentPane.add(new_password);
		
		btnNewButton = new JButton("确认修改");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(!DataProcessing.updateUser(now_user.getName(), new_password.getText(), now_user.getRole())) {
						JOptionPane.showMessageDialog(null, 
			                    "修改失败！请检查信息是否正确", 
			                    "失败", 
			                    JOptionPane.ERROR_MESSAGE);
					}else {
						JOptionPane.showMessageDialog(null, 
			                    "修改成功！", 
			                    "成功", 
			                    JOptionPane.INFORMATION_MESSAGE);
			                
			                // 可选：关闭当前窗口
			                dispose();
					}
				}catch(SQLException e_new) {
					JOptionPane.showMessageDialog(null,
                            "数据库错误：" + e_new.getMessage(),
                            "错误",
                            JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnNewButton.setBounds(120, 188, 204, 50);
		contentPane.add(btnNewButton);
		
		
		// ===================== 核心修复：实时判断 =====================
		// 监听输入框变化
		DocumentListener listener = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) { checkEnable(); }
			@Override
			public void removeUpdate(DocumentEvent e) { checkEnable(); }
			@Override
			public void changedUpdate(DocumentEvent e) { checkEnable(); }
		};
		new_password.getDocument().addDocumentListener(listener);
		
		// 初始检查一次
		checkEnable();
	}
	
	// 判断按钮是否可用
	private void checkEnable() {
		String pwd = new_password.getText().trim();
		
		// 用户名、密码非空，且角色不能是空字符串
		boolean canUse = !pwd.isEmpty() ;
		
		btnNewButton.setEnabled(canUse);
	}

}