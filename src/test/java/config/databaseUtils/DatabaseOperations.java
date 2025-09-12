package config.databaseUtils;

import java.util.List;
import java.util.Map;

/**
 * 数据库操作工具接口
 * 提供基本的CRUD操作和批量操作功能
 */
public  interface DatabaseOperations {

    /**
     * 查询单条记录
     * @param sql 查询SQL语例
     * @param params 查询参数
     * @return 查询结果Map
     */
    Map<String, Object> queryForObject(String sql, Object... params);

    /**
     * 查询多条记录
     * @param sql 查询SQL语例
     * @param params 查询参数
     * @return 查询结果列表
     */
    List<Map<String, Object>> queryForList(String sql, Object... params);

    /**
     * 插入数据
     * @param sql 插入SQL语句
     * @param params 插入参数
     * @return 影响行数
     */
    int insert(String sql, Object... params);

    /**
     * 更新数据
     * @param sql 更新SQL语句
     * @param params 更新参数
     * @return 影响行数
     */
    int update(String sql, Object... params);

    /**
     * 删除数据
     * @param sql 删除SQL语句
     * @param params 删除参数
     * @return 影响行数
     */
    int delete(String sql, Object... params);

    /**
     * 批量插入数据
     * @param sql 插入SQL语句
     * @param batchArgs 批量参数列表
     * @return 每批影响行数数组
     */
    int[] batchInsert(String sql, List<Object[]> batchArgs);

    /**
     * 批量更新数据
     * @param sql 更新SQL语句
     * @param batchArgs 批量参数列表
     * @return 每批影响行数数组
     */
    int[] batchUpdate(String sql, List<Object[]> batchArgs);

    /**
     * 批量删除数据
     * @param sql 删除SQL语句
     * @param batchArgs 批量参数列表
     * @return 每批影响行数数组
     */
    int[] batchDelete(String sql, List<Object[]> batchArgs);

    /**
     * 执行任意SQL语句
     * @param sql SQL语句
     * @param params 参数
     * @return 影响行数
     */
    int execute(String sql, Object... params);

    /**
     * 检查连接是否有效
     * @return 连接状态
     */
    boolean isConnected();

    /**
     * 关闭数据库连接
     */
    void close();
}
