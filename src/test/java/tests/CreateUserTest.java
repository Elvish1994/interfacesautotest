package tests;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("用户管理模块")
@Feature("用户生命周期")
public class CreateUserTest {

    @Test(description = "创建一个新用户", priority = 1)
    @Story("作为管理员，我可以创建新用户")
    @Severity(SeverityLevel.CRITICAL)
    @Description("验证创建用户API的正确性")
    public void testCreateUser() {
        // 模拟创建用户操作
        System.out.println("执行：创建用户");

        // 添加步骤
        Allure.step("步骤1: 发送创建用户请求");
        Allure.step("步骤2: 验证响应状态码为201");
        Allure.step("步骤3: 验证返回的用户信息");

        // 模拟断言
        Assert.assertTrue(true, "用户创建成功");
    }
}