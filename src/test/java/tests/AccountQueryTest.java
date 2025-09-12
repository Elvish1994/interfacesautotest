package tests;

import config.databaseUtils.DatabaseConnection;
import config.databaseUtils.DatabaseOperations;
import config.databaseUtils.impl.DatabaseConnectionImpl;
import config.databaseUtils.impl.DatabaseOperationsImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;
import java.util.List;
import java.util.Map;


public class AccountQueryTest {

    private DatabaseOperations databaseOperations;

    @BeforeMethod
    public void setUp() {
        // 创建数据库连接
        DatabaseConnection connection = new DatabaseConnectionImpl();

        // 使用数据库连接创建 DatabaseOperations 实例
        databaseOperations = new DatabaseOperationsImpl(connection);
    }

    @Test
    public void testQueryAccountData() {
        // 验证databaseOperations是否已正确初始化
        if (databaseOperations == null) {
            throw new IllegalStateException("DatabaseOperations未初始化，请检查setUp方法");
        }

        String sql = "SELECT id, site_id, account_type, account_data, from_type, relate_tag, remark, create_time, account_no, type_name FROM testa_51zhideai_.account LIMIT 10";

        // 执行查询
        List<Map<String, Object>> results = databaseOperations.queryForList(sql);

        // 验证查询结果
        assert results != null : "查询结果不应为null";
        System.out.println("查询到 " + results.size() + " 条记录");

        // 打印返回结果
        System.out.println("查询结果:");
        for (int i = 0; i < results.size(); i++) {
            Map<String, Object> row = results.get(i);
            System.out.println("记录 " + (i+1) + ": " + row);
        }
    }

    @Test
    public void testConnectionStatus() {
        if (databaseOperations != null) {
            boolean connected = databaseOperations.isConnected();
            System.out.println("数据库连接状态: " + (connected ? "已连接" : "未连接"));
        } else {
            System.out.println("DatabaseOperations未初始化");
        }
    }

    @AfterClass
    public void tearDown() {
        // 关闭数据库连接
        if (databaseOperations != null && databaseOperations.isConnected()) {
            databaseOperations.close();
            System.out.println("数据库连接已关闭");
        }
    }
}
