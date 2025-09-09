package config.databaseUtils.impl;// DatabaseConnectionImpl.java
import config.databaseUtils.DatabaseConnection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnectionImpl implements DatabaseConnection {

    private Connection connection;
    private Properties dbProperties;

    public DatabaseConnectionImpl() {
        loadDatabaseProperties();
    }

    /**
     * 加载数据库配置文件
     */
    private void loadDatabaseProperties() {
        dbProperties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config/database.properties")) {
            if (input == null) {
                throw new RuntimeException("无法找到数据库配置文件: config/database.properties");
            }
            dbProperties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("读取数据库配置文件失败", e);
        }
    }

    @Override
    public Connection connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }

            String driver = dbProperties.getProperty("db.driver");
            String url = dbProperties.getProperty("db.url");
            String username = dbProperties.getProperty("db.username");
            String password = dbProperties.getProperty("db.password");

            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
            return connection;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到数据库驱动类", e);
        } catch (SQLException e) {
            throw new RuntimeException("数据库连接失败", e);
        }
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("关闭数据库连接时出错: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    @Override
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * 获取数据库配置属性
     * @return 配置属性
     */
    protected Properties getDbProperties() {
        return dbProperties;
    }
}
