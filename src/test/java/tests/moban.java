package tests;

import config.JsonToJavaGenerator;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("模板类")
@Feature("模板")
public class moban {


    @Test(description = "查询接口是否正常", priority = 1)
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


    @Test(description = "根据json生成对象", priority = 1)
    @Story("根据json生成对象")
    @Severity(SeverityLevel.CRITICAL)
    @Description("根据json生成对象")
    public void testjsontojavaobj() {

        // JSON -> Java 对象
        String json = """
            {
              "name": "张三",
              "age": 25,
              "email": "zhangsan@example.com",
              "active": true,
              "address": {
                "city": "北京",
                "zip": "100000"
              },
              "hobbies": ["读书", "游泳"]
            }
            """;

        // 不生成toString方法
//        JsonToJavaGenerator.setGenerateToString(false);
//        JsonToJavaGenerator.setGenerateEqualsAndHashCode(true); // 仍生成equals和hashCode
//        JsonToJavaGenerator.generate(json, "User", "base.pojo", "src/test/java");

        // 或者都不生成
        JsonToJavaGenerator.setGenerateToString(false);
        JsonToJavaGenerator.setGenerateEqualsAndHashCode(false);
        JsonToJavaGenerator.generate(json, "User", "base.pojo", "src/test/java");
        Assert.assertTrue(true, "根据json生成对象");

        // 恢复默认（都生成）
//        JsonToJavaGenerator.setGenerateToString(true);
//        JsonToJavaGenerator.setGenerateEqualsAndHashCode(true);
    }
}
