
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Collection;
import java.awt.event.ActionEvent;

class NewUser extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField new_name;
	private JTextField new_password;
	private JComboBox<String> new_role;
	private JButton btnNewButton;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					NewUser frame = new NewUser();
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
	public NewUser() {
		setTitle("新增用户");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // 关闭只关这个窗口，不关整个程序
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		new_name = new JTextField();
		new_name.setBounds(146, 46, 232, 21);
		contentPane.add(new_name);
		new_name.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("用户名：");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel.setBounds(50, 49, 71, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("密码：");
		lblNewLabel_1.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(50, 101, 71, 14);
		contentPane.add(lblNewLabel_1);
		
		new_password = new JTextField();
		new_password.setColumns(10);
		new_password.setBounds(146, 98, 232, 21);
		contentPane.add(new_password);
		
		JLabel lblNewLabel_2 = new JLabel("角色：");
		lblNewLabel_2.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel_2.setBounds(50, 148, 71, 14);
		contentPane.add(lblNewLabel_2);
		
		new_role = new JComboBox<>();
		new_role.setModel(new DefaultComboBoxModel<>(new String[] {"", "administrator", "operator", "browser"}));
		new_role.setBounds(146, 144, 119, 22);
		contentPane.add(new_role);
		
		btnNewButton = new JButton("新增用户");
		
		btnNewButton.setBounds(146, 207, 160, 31);
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
		new_name.getDocument().addDocumentListener(listener);
		new_password.getDocument().addDocumentListener(listener);
		
		// 监听下拉框选择
		new_role.addActionListener(e -> checkEnable());
		
		// 初始检查一次
		checkEnable();
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(!DataProcessing.insertUser(new_name.getText(), new_password.getText(), (String)new_role.getSelectedItem())) {
						JOptionPane.showMessageDialog(null, 
			                    "新增用户失败！请检查信息是否重复", 
			                    "失败", 
			                    JOptionPane.ERROR_MESSAGE);
					}else {
						JOptionPane.showMessageDialog(null, 
			                    "新增用户成功！", 
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
	}
	
	// 判断按钮是否可用
	private void checkEnable() {
		String name = new_name.getText().trim();
		String pwd = new_password.getText().trim();
		String role = (String) new_role.getSelectedItem();
		
		// 用户名、密码非空，且角色不能是空字符串
		boolean canUse = !name.isEmpty() && !pwd.isEmpty() && role != null && !role.trim().isEmpty();
		
		btnNewButton.setEnabled(canUse);
	}

}

class DelUser extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField del_name;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					DelUser frame = new DelUser();
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
	public DelUser() {
		setTitle("删除用户");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // 关闭只关这个窗口，不关整个程序
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("用户名：");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel.setBounds(52, 77, 60, 24);
		contentPane.add(lblNewLabel);
		
		del_name = new JTextField();
		del_name.setBounds(135, 79, 209, 20);
		contentPane.add(del_name);
		del_name.setColumns(10);
		
		JButton btnNewButton = new JButton("确认删除");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(!DataProcessing.deleteUser(del_name.getText())) {
						JOptionPane.showMessageDialog(null, 
			                    "删除用户失败！请检查信息是否正确", 
			                    "失败", 
			                    JOptionPane.ERROR_MESSAGE);
					}else {
						JOptionPane.showMessageDialog(null, 
			                    "删除用户成功！", 
			                    "成功", 
			                    JOptionPane.INFORMATION_MESSAGE);
			                
			                // 可选：关闭当前窗口
			                dispose();
					}
				}catch(SQLException e_del) {
					JOptionPane.showMessageDialog(null,
                            "数据库错误：" + e_del.getMessage(),
                            "错误",
                            JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnNewButton.setBounds(135, 174, 177, 38);
		contentPane.add(btnNewButton);

	}
}

class ChangeUser extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField new_name;
	private JTextField new_password;
	private JComboBox<String> new_role;
	private JButton btnNewButton;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					ChangeUser frame = new ChangeUser();
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
	public ChangeUser() {
		setTitle("修改用户");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // 关闭只关这个窗口，不关整个程序
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		new_name = new JTextField();
		new_name.setBounds(146, 46, 232, 21);
		contentPane.add(new_name);
		new_name.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("用户名：");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel.setBounds(50, 49, 71, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("新密码：");
		lblNewLabel_1.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(50, 101, 71, 14);
		contentPane.add(lblNewLabel_1);
		
		new_password = new JTextField();
		new_password.setColumns(10);
		new_password.setBounds(146, 98, 232, 21);
		contentPane.add(new_password);
		
		JLabel lblNewLabel_2 = new JLabel("新角色：");
		lblNewLabel_2.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel_2.setBounds(50, 148, 71, 14);
		contentPane.add(lblNewLabel_2);
		
		new_role = new JComboBox<>();
		new_role.setModel(new DefaultComboBoxModel<>(new String[] {"", "administrator", "operator", "browser"}));
		new_role.setBounds(146, 144, 119, 22);
		contentPane.add(new_role);
		
		btnNewButton = new JButton("修改用户");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(!DataProcessing.updateUser(new_name.getText(), new_password.getText(), (String)new_role.getSelectedItem())) {
						JOptionPane.showMessageDialog(null, 
			                    "修改用户失败！请检查信息是否正确", 
			                    "失败", 
			                    JOptionPane.ERROR_MESSAGE);
					}else {
						JOptionPane.showMessageDialog(null, 
			                    "修改用户成功！", 
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
		btnNewButton.setBounds(146, 207, 160, 31);
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
		new_name.getDocument().addDocumentListener(listener);
		new_password.getDocument().addDocumentListener(listener);
		
		// 监听下拉框选择
		new_role.addActionListener(e -> checkEnable());
		
		// 初始检查一次
		checkEnable();
	}
	
	// 判断按钮是否可用
	private void checkEnable() {
		String name = new_name.getText().trim();
		String pwd = new_password.getText().trim();
		String role = (String) new_role.getSelectedItem();
		
		// 用户名、密码非空，且角色不能是空字符串
		boolean canUse = !name.isEmpty() && !pwd.isEmpty() && role != null && !role.trim().isEmpty();
		
		btnNewButton.setEnabled(canUse);
	}

}

class ListUser extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	
	// ====================== 关键：表格模型（用来动态加数据） ======================
	private DefaultTableModel tableModel;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					ListUser frame = new ListUser();
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
	public ListUser() {
		setTitle("用户列表");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // 关闭只关这个窗口，不关整个程序
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		table = new JTable();
		
		// ====================== 初始化空表格（无固定行，动态添加） ======================
		tableModel = new DefaultTableModel(
			new Object[][] {
				// 这里原来的固定空行全部删除了！现在是空表格
			},
			new String[] {
				"\u7528\u6237\u540D", "\u5BC6\u7801", "\u89D2\u8272"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		};
		table.setModel(tableModel);
		
		table.getColumnModel().getColumn(1).setPreferredWidth(163);
		table.setBounds(49, 34, 339, 192);
		contentPane.add(table);
		
		addRow("用户名","密码","角色");
		try{
			Collection<AbstractUser> userCollection = DataProcessing.getAllUsers();
			for(AbstractUser user : userCollection ) {
				addRow(user.getName(),user.getPassword(),user.getRole());
			}
		}catch(SQLException e_list){
			JOptionPane.showMessageDialog(null,
                    "数据库错误：" + e_list.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
		}
		
		// ====================== ✅ 你自己添加数据的位置（在这里写！） ======================
		// 格式：addRow("用户名", "密码", "角色");
		// 想加几行加几行，不需要提前设置行数！
//		
//		addRow("张三", "123456", "管理员");
//		addRow("李四", "654321", "普通用户");
//		addRow("王五", "888888", "操作员");
		
		
		// ====================== 结束添加数据 ======================
	}
	
	// ====================== 工具方法：添加一行数据 ======================
	private void addRow(String username, String password, String role) {
		tableModel.addRow(new Object[] {username, password, role});
	}
}