package config.databaseUtils.impl;

import config.databaseUtils.DatabaseConnection;
import config.databaseUtils.DatabaseOperations;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库操作工具实现类
 * 实现DatabaseOperations接口的所有方法
 */
public class DatabaseOperationsImpl implements DatabaseOperations {

    private DatabaseConnection databaseConnection;
    private Connection connection;

    public DatabaseOperationsImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        this.connection = databaseConnection.connect();
    }

    @Override
    public Map<String, Object> queryForObject(String sql, Object... params) {
        List<Map<String, Object>> result = queryForList(sql, params);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql, Object... params) {
        List<Map<String, Object>> result = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setParameters(pstmt, params);
            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询数据失败: " + e.getMessage(), e);
        }

        return result;
    }

    @Override
    public int insert(String sql, Object... params) {
        return executeUpdate(sql, params);
    }

    @Override
    public int update(String sql, Object... params) {
        return executeUpdate(sql, params);
    }

    @Override
    public int delete(String sql, Object... params) {
        return executeUpdate(sql, params);
    }

    @Override
    public int[] batchInsert(String sql, List<Object[]> batchArgs) {
        return executeBatch(sql, batchArgs);
    }

    @Override
    public int[] batchUpdate(String sql, List<Object[]> batchArgs) {
        return executeBatch(sql, batchArgs);
    }

    @Override
    public int[] batchDelete(String sql, List<Object[]> batchArgs) {
        return executeBatch(sql, batchArgs);
    }

    @Override
    public int execute(String sql, Object... params) {
        return executeUpdate(sql, params);
    }

    @Override
    public boolean isConnected() {
        return databaseConnection.isConnected();
    }

    @Override
    public void close() {
        databaseConnection.close();
    }

    /**
     * 执行更新操作（插入、更新、删除）
     * @param sql SQL语句
     * @param params 参数
     * @return 影响行数
     */
    private int executeUpdate(String sql, Object... params) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setParameters(pstmt, params);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("执行SQL失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行批量操作
     * @param sql SQL语句
     * @param batchArgs 批量参数
     * @return 每批影响行数数组
     */
    private int[] executeBatch(String sql, List<Object[]> batchArgs) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (Object[] params : batchArgs) {
                setParameters(pstmt, params);
                pstmt.addBatch();
            }
            return pstmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("批量执行SQL失败: " + e.getMessage(), e);
        }
    }

    /**
     * 设置PreparedStatement参数
     * @param pstmt PreparedStatement对象
     * @param params 参数数组
     * @throws SQLException SQL异常
     */
    private void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
        }
    }
}
