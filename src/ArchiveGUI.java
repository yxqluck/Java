import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.awt.event.ActionEvent;

 class UploadArchive extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	String filepath;
	String fileName;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					UploadArchive frame = new UploadArchive();
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
	public UploadArchive() {
		
		setTitle("上传档案");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("档案号：");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel.setBounds(59, 28, 73, 30);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("创建者：");
		lblNewLabel_1.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(59, 68, 73, 27);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("描述：");
		lblNewLabel_2.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel_2.setBounds(59, 105, 73, 26);
		contentPane.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("文件名：");
		lblNewLabel_3.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel_3.setBounds(59, 141, 73, 25);
		contentPane.add(lblNewLabel_3);
		
		textField = new JTextField();
		textField.setBounds(142, 33, 177, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setBounds(142, 71, 177, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		textField_2 = new JTextField();
		textField_2.setBounds(142, 108, 177, 20);
		contentPane.add(textField_2);
		textField_2.setColumns(10);
		
		textField_3 = new JTextField();
		textField_3.setBounds(141, 143, 178, 20);
		contentPane.add(textField_3);
		textField_3.setColumns(10);
		
		JButton btnNewButton = new JButton("上传");
		
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				JFileChooser fileChooser = new JFileChooser();
				
				// 弹出文件选择窗口
				int result = fileChooser.showOpenDialog(null);
				
				// 如果用户选择了文件（不是取消）
				if (result == JFileChooser.APPROVE_OPTION) {
					// 获取选中的文件
					File file = fileChooser.getSelectedFile();
					// 获取文件完整路径
					String filePath = file.getAbsolutePath();
					
					// 这里可以把路径显示到文本框里
					// 假设你的文本框名字叫 textField
					fileName=file.getName();
					textField_3.setText(fileName);
					
					System.out.println("选中文件：" + filePath);
					filepath=filePath;
				}
			}
		});
		btnNewButton.setBounds(329, 142, 65, 22);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("确认上传");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Operator.uploadArchive(textField.getText(), textField_1.getText(), textField_2.getText(), textField_3.getText(), filepath)) {
					
            		try {
						if(filepath != null && !filepath.isEmpty()) {
							if(Main.client.uploadFile(filepath,fileName)) {
								JOptionPane.showMessageDialog(null, "上传成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
							} else {
								JOptionPane.showMessageDialog(null, "网络上传失败！", "提示", JOptionPane.ERROR_MESSAGE);
							}
						} else {
							JOptionPane.showMessageDialog(null, "请先选择文件！", "提示", JOptionPane.WARNING_MESSAGE);
						}
					} catch (Exception e0) {
						JOptionPane.showMessageDialog(null, "网络错误：" + e0.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
					}
					
					
				}else {
					JOptionPane.showMessageDialog(null, "数据库上传失败！", "提示", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnNewButton_1.setBounds(159, 192, 117, 39);
		contentPane.add(btnNewButton_1);
		// 窗口关闭时关闭连接
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				Main.client.closeConnection();
			}
		});
	}
}

 
 class DownloadArchive extends JFrame {

		private static final long serialVersionUID = 1L;
		private JPanel contentPane;
		private JTable table;
		private DefaultTableModel tableModel;

//		public static void main(String[] args) {
//			EventQueue.invokeLater(new Runnable() {
//				public void run() {
//					try {
//						DownloadArchive frame = new DownloadArchive();
//						frame.setVisible(true);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			});
//		}

		public DownloadArchive() {
			setTitle("下载档案");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 750, 400);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);

			// 表格
			table = new JTable() {
				// ✅ 核心：禁止选中第一行
				@Override
				public void changeSelection(int row, int column, boolean toggle, boolean extend) {
					if (row == 0) { // 第一行（索引0）禁止选中
						return;
					}
					super.changeSelection(row, column, toggle, extend);
				}
			};
			table.setBounds(40, 30, 650, 250);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 只允许选一行

			tableModel = new DefaultTableModel(
				new Object[][] {},
				new String[] { "档案号", "创建者", "创建时间", "文件名", "描述" }
			) {
				Class[] columnTypes = new Class[] { String.class, String.class, String.class, String.class, String.class };
				public Class getColumnClass(int columnIndex) { return columnTypes[columnIndex]; }
				
				// ✅ 所有单元格都不可编辑
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			table.setModel(tableModel);
			contentPane.add(table);

			// ✅ 去掉你手动加的第一行，用自带表头即可
			 addRow("档案号", "创建者", "创建时间", "文件名", "描述");

			// ====================== 按钮 ======================
			JButton btnNewButton = new JButton("下载选中文件");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int selectedRow = table.getSelectedRow();
					if (selectedRow == -1 || selectedRow == 0) { // 双重保险：禁止第一行
						JOptionPane.showMessageDialog(null, "请选中有效数据行");
						return;
					}

					String archiveId = (String) table.getValueAt(selectedRow, 0);
 					String fileName = (String) table.getValueAt(selectedRow, 3);
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int result = fileChooser.showOpenDialog(null);

					if (result == JFileChooser.APPROVE_OPTION) {
						File folder = fileChooser.getSelectedFile();
						String folderPath = folder.getAbsolutePath();

						try {
							AbstractUser.downloadArchive(archiveId, folderPath);
							
							if(Main.client.downloadFile(fileName, folderPath)) {
								JOptionPane.showMessageDialog(null, "下载成功！");
							} else {
								JOptionPane.showMessageDialog(null, "网络下载失败！", "错误", JOptionPane.ERROR_MESSAGE);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
							JOptionPane.showMessageDialog(null, "下载失败：" + ex.getMessage());
						}
					}
				}
			});
			btnNewButton.setFont(new Font("宋体", Font.PLAIN, 14));
			btnNewButton.setBounds(267, 290, 200, 45);
			contentPane.add(btnNewButton);
			// 窗口关闭时关闭连接
			addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent windowEvent) {
					Main.client.closeConnection();
				}
			});
			// 加载数据
			loadDataFromDB();
		}

		private void loadDataFromDB() {
			try {
				Collection<Archive> archiveList = DataProcessing.getAllArchives();

				for (Archive archive : archiveList) {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					String createTimeStr = archive.getTimestamp().format(formatter);

					addRow(
						archive.getArchiveId(),
						archive.getCreator(),
						createTimeStr,
						archive.getFileName(),
						archive.getDescription()
					);
				}
			} catch (SQLException e_list) {
				e_list.printStackTrace();
				JOptionPane.showMessageDialog(this,
					"数据库加载失败：" + e_list.getMessage(),
					"错误",
					JOptionPane.ERROR_MESSAGE
				);
			}
		}

		private void addRow(String archiveNo, String creator, String createTime, String fileName, String desc) {
			tableModel.addRow(new Object[] { archiveNo, creator, createTime, fileName, desc });
		}
	}
 