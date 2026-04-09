import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户数据处理
 * 用户数据的增删改查
 *
 * @author gongjing
 */
public  class DataProcessing {
	private static Connection connection = null;
	static final double EXCEPTION_CONNECT_PROBABILITY=0.1;
	static final double EXCEPTION_DISCONNECT_PROBABILITY=0.1;

    final static String ROLE_ADMINISTRATOR = "administrator";
    final static String ROLE_OPERATOR = "operator";
    final static String ROLE_BROWSER = "browser";

    // JDBC 配置
    private static final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/archive_management?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "69104625yxq";

    /**
     * 连接数据库
	 *
	 * @throws SQLException SQL 异常
	*/
	public static  void connectToDatabase() throws SQLException{
        // 避免重复初始化
        if (connection != null && !connection.isClosed()) {
            return;
        }

        try {
            Class.forName(DRIVER_NAME);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("数据库连接成功");
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC 驱动加载失败：" + e.getMessage());
        }
	}

    /**
     * 关闭数据库连接
     *
     * @throws SQLException 数据库断开异常
     */
    public static void disconnectFromDataBase() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            try {
                connection.close();
                System.out.println("数据库连接已关闭");
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

	/**
	 * 通过用户名查询用户
     *
     * @param name 用户名
     * @return 用户对象 AbstractUser，如果不存在则返回 null
     * @throws SQLException 数据库未连接异常
     */
    public static AbstractUser searchUser(String name) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Not Connected to Database");
        }

        // 空值和格式检查
        if (name == null || name.trim().isEmpty()) {
            System.err.println("查询失败：用户名为空");
            return null;
        }

        String sql = "SELECT name, password, role FROM users WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String userName = rs.getString("name");
                    String password = rs.getString("password");
                    String role = rs.getString("role");
                    return createUserByRole(userName, password, role);
                }
            }
        }
        return null;
	}
	
	/**
     * 通过用户名和密码查询用户，用于登录验证
     *
     * @param name 用户名
     * @param password 密码
     * @return 验证成功返回用户对象，验证失败返回 null
     * @throws SQLException 数据库未连接异常
     */
    public static AbstractUser searchUser(String name, String password) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Not Connected to Database");
        }

        // 空值和格式检查
        if (name == null || name.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            System.err.println("登录失败：用户名或密码为空");
            return null;
        }

        String sql = "SELECT name, password, role FROM users WHERE name = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name.trim());
            stmt.setString(2, password.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String userName = rs.getString("name");
                    String userPassword = rs.getString("password");
                    String role = rs.getString("role");
                    return createUserByRole(userName, userPassword, role);
                }
            }
        }
        return null;
    }
	
	/**
	 * 获取所有用户
     *
     * @return 用户对象的集合
     * @throws SQLException 数据库未连接异常
     */
    public static Collection<AbstractUser> getAllUsers() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Not Connected to Database");
        }

        List<AbstractUser> userList = new ArrayList<>();
        String sql = "SELECT name, password, role FROM users";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                String password = rs.getString("password");
                String role = rs.getString("role");
                AbstractUser user = createUserByRole(name, password, role);
                if (user != null) {
                    userList.add(user);
                }
            }
        }
        return userList;
    }

	/**
	 * 更新用户信息
     *
     * @param user 用户对象
     * @return boolean 更新是否成功
     * @throws SQLException 数据库未连接异常
     */
    public static boolean updateUser(AbstractUser user) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Not Connected to Database");
        }

        String name = user.getName();
        String password = user.getPassword();
        String role = user.getRole();

        String sql = "UPDATE users SET password = ?, role = ? WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, password);
            stmt.setString(2, role);
            stmt.setString(3, name);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * 更新用户信息
     *
     * @param name 用户名（作为唯一标识，不可修改）
     * @param password 新密码
     * @param role 新角色
     * @return boolean 更新是否成功
     * @throws SQLException 数据库未连接异常
     */
    public static boolean updateUser(String name, String password, String role) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("数据库未连接");
        }

        // 空值和格式检查
        if (name == null || name.trim().isEmpty()) {
            System.err.println("更新失败：用户名不能为空");
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            System.err.println("更新失败：密码不能为空");
            return false;
        }

        if (role == null || role.trim().isEmpty()) {
            System.err.println("更新失败：角色不能为空");
            return false;
        }

        String trimmedName = name.trim();
        String trimmedPassword = password.trim();
        String trimmedRole = role.trim();

        String sql = "UPDATE users SET password = ?, role = ? WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, trimmedPassword);
            stmt.setString(2, trimmedRole);
            stmt.setString(3, trimmedName);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("用户信息更新成功");
                return true;
            } else {
                System.err.println("更新失败：用户名不存在 - " + trimmedName);
                return false;
            }
        }
    }

    /**
     * 根据角色创建对应的用户对象
     *
     * @param name 用户名
     * @param password 密码
     * @param role 用户角色
     * @return 对应的用户对象，如果角色无效则返回 null
     */
    private static AbstractUser createUserByRole(String name, String password, String role) {
        if (ROLE_ADMINISTRATOR.equalsIgnoreCase(role)) {
            return new Administrator(name, password, role);
        } else if (ROLE_OPERATOR.equalsIgnoreCase(role)) {
            return new Operator(name, password, role);
        } else if (ROLE_BROWSER.equalsIgnoreCase(role)) {
            return new Browser(name, password, role);
        } else {
            System.err.println("创建失败：无效的角色 - " + role);
            return null;
        }
    }

	/**
     * 新增用户
     *
     * @param user 用户对象
     * @return boolean 新增是否成功
	 * @throws SQLException SQL 异常
	*/
	public static boolean insertUser(AbstractUser user) throws SQLException{
	    if (connection == null || connection.isClosed()) {
	        throw new SQLException("Not Connected to Database");
		}

        String name = user.getName();
        String password = user.getPassword();
        String role = user.getRole();

        String sql = "INSERT INTO users (name, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, password);
            stmt.setString(3, role);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry
                System.err.println("新增失败：用户已存在");
            }
            throw e;
        }
	}

    /**
     * 新增用户
     *
     * @param name 用户名
     * @param password 密码
     * @param role 用户角色
     * @return boolean 新增是否成功
     * @throws SQLException SQL 异常
     */
    public static boolean insertUser(String name, String password, String role) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("数据库未连接");
        }

        if (name == null || name.trim().isEmpty()) {
            System.err.println("新增失败：用户名不能为空");
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            System.err.println("新增失败：密码不能为空");
            return false;
        }

        if (role == null || role.trim().isEmpty()) {
            System.err.println("新增失败：角色不能为空");
            return false;
        }

        String trimmedName = name.trim();
        String trimmedPassword = password.trim();
        String trimmedRole = role.trim();

        String sql = "INSERT INTO users (name, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, trimmedName);
            stmt.setString(2, trimmedPassword);
            stmt.setString(3, trimmedRole);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("用户新增成功：" + trimmedName);
                return true;
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry
                System.err.println("新增失败：用户已存在 - " + trimmedName);
            }
            throw e;
        }
        return false;
    }

	/**
     * 删除用户
     *
     * @param name 用户名
     * @return boolean  删除是否成功
	 * @throws SQLException SQL 异常
	*/
	public static boolean deleteUser(String name) throws SQLException{
		if (connection == null || connection.isClosed()) {
	        throw new SQLException("Not Connected to Database");
		}
        // 空值检查
        if (name == null) {
            System.err.println("删除失败：用户名不能为空");
            return false;
        }

        String sql = "DELETE FROM users WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name.trim());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
	}

    /**
     * 通过档案号查找档案
     *
     * @param archiveId 档案号
     * @return 档案对象 Archive，如果不存在则返回 null
     * @throws SQLException 数据库未连接异常
     */
    public static Archive searchArchive(String archiveId) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Not Connected to Database");
        }

        // 空值和格式检查
        if (archiveId == null || archiveId.trim().isEmpty()) {
            System.err.println("查找失败：档案号为空");
            return null;
        }

        String sql = "SELECT archiveId, creator, timestamp, description, fileName FROM archives WHERE archiveId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, archiveId.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("archiveId");
                    String creator = rs.getString("creator");
                    LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                    String description = rs.getString("description");
                    String fileName = rs.getString("fileName");
                    return new Archive(id, creator, timestamp, description, fileName);
                }
            }
        }
        return null;
    }

    /**
     * 新增档案
     *
     * @param archiveId 档案号
     * @param creator 档案创建者
     * @param timestamp 时间戳
     * @param description 档案描述
     * @param fileName 文件名
     * @return boolean 新增是否成功
     * @throws SQLException SQL 异常
     */
    public static boolean insertArchive(String archiveId, String creator, LocalDateTime timestamp, 
                                       String description, String fileName) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Not Connected to Database");
        }

        // 空值和格式检查
        if (archiveId == null || archiveId.trim().isEmpty() ||
            creator == null || creator.trim().isEmpty() ||
            fileName == null || fileName.trim().isEmpty()) {
            System.err.println("新增失败：档案号、创建者或文件名为空");
            return false;
        }

        String trimmedArchiveId = archiveId.trim();

        String sql = "INSERT INTO archives (archiveId, creator, timestamp, description, fileName) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, trimmedArchiveId);
            stmt.setString(2, creator.trim());
            stmt.setTimestamp(3, Timestamp.valueOf(timestamp));
            stmt.setString(4, description != null ? description.trim() : "");
            stmt.setString(5, fileName.trim());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry
                System.err.println("新增失败：档案号已存在");
            }
            throw e;
        }
    }

    /**
     * 新增档案
     *
     * @param archive 档案
     * @return boolean 新增是否成功
     * @throws SQLException SQL 异常
     */
    public static boolean insertArchive(Archive archive) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Not Connected to Database");
        }

        String archiveId = archive.getArchiveId();

        String sql = "INSERT INTO archives (archiveId, creator, timestamp, description, fileName) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, archiveId);
            stmt.setString(2, archive.getCreator());
            stmt.setTimestamp(3, Timestamp.valueOf(archive.getTimestamp()));
            stmt.setString(4, archive.getDescription());
            stmt.setString(5, archive.getFileName());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry
                System.err.println("新增失败：档案号已存在");
            }
            throw e;
        }
    }

    /**
     * 获取所有档案
     *
     * @return 档案对象的集合
     * @throws SQLException 数据库未连接异常
     */
    public static Collection<Archive> getAllArchives() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Not Connected to Database");
        }

        List<Archive> archiveList = new ArrayList<>();
        String sql = "SELECT archiveId, creator, timestamp, description, fileName FROM archives";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("archiveId");
                String creator = rs.getString("creator");
                LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                String description = rs.getString("description");
                String fileName = rs.getString("fileName");
                Archive archive = new Archive(id, creator, timestamp, description, fileName);
                archiveList.add(archive);
            }
        }
        return archiveList;
    }

    /**
     * 删除档案
     *
     * @param archiveId 档案号
     * @return boolean 删除是否成功
     * @throws SQLException SQL 异常
     */
    public static boolean deleteArchive(String archiveId) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Not Connected to Database");
        }

        // 空值检查
        if (archiveId == null || archiveId.trim().isEmpty()) {
            System.err.println("删除失败：档案号不能为空");
            return false;
        }

        String sql = "DELETE FROM archives WHERE archiveId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, archiveId.trim());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("删除成功");
                return true;
            } else {
                System.err.println("删除失败：档案不存在");
                return false;
            }
        }
    }

    /**
     * 更新档案信息
     *
     * @param archiveId 档案号
     * @param creator 档案创建者
     * @param timestamp 时间戳
     * @param description 档案描述
     * @param fileName 文件名
     * @return boolean 更新是否成功
     * @throws SQLException 数据库未连接异常
     */
    public static boolean updateArchive(String archiveId, String creator, LocalDateTime timestamp, 
                                       String description, String fileName) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Not Connected to Database");
        }

        // 空值和格式检查
        if (archiveId == null || archiveId.trim().isEmpty() ||
            creator == null || creator.trim().isEmpty() ||
            fileName == null || fileName.trim().isEmpty()) {
            System.err.println("更新失败：档案号、创建者或文件名为空");
            return false;
        }

        String trimmedArchiveId = archiveId.trim();

        String sql = "UPDATE archives SET creator = ?, timestamp = ?, description = ?, fileName = ? WHERE archiveId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, creator.trim());
            stmt.setTimestamp(2, Timestamp.valueOf(timestamp));
            stmt.setString(3, description != null ? description.trim() : "");
            stmt.setString(4, fileName.trim());
            stmt.setString(5, trimmedArchiveId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("更新成功");
                return true;
            } else {
                System.err.println("更新失败：档案号不存在");
                return false;
            }
        }
    }

    /**
     * 更新档案信息
     *
     * @param archive 档案
     * @return boolean 更新是否成功
     * @throws SQLException 数据库未连接异常
     */
    public static boolean updateArchive(Archive archive) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Not Connected to Database");
        }

        String archiveId = archive.getArchiveId();

        String sql = "UPDATE archives SET creator = ?, timestamp = ?, description = ?, fileName = ? WHERE archiveId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, archive.getCreator());
            stmt.setTimestamp(2, Timestamp.valueOf(archive.getTimestamp()));
            stmt.setString(3, archive.getDescription());
            stmt.setString(4, archive.getFileName());
            stmt.setString(5, archiveId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("更新成功");
                return true;
            } else {
                System.err.println("更新失败：档案号不存在");
                return false;
            }
        }
    }
}