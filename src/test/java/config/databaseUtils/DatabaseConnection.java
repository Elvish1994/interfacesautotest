package config.databaseUtils;

// DatabaseConnection.java
public interface DatabaseConnection {
    /**
     * 建立数据库连接
     * @return 数据库连接对象
     */
    java.sql.Connection connect();
    
    /**
     * 关闭数据库连接
     */
    void close();
    
    /**
     * 检查连接是否有效
     * @return 连接状态
     */
    boolean isConnected();
}
