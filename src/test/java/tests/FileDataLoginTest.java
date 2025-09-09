// FileDataLoginTest.java
package tests;


import config.datafeomcsvtxt.FileDataProviderFactory;
import config.datafeomcsvtxt.imp.FileDataProvider;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static io.restassured.RestAssured.given;

public class FileDataLoginTest {

    private FileDataProviderFactory fileDataProvider;

    @BeforeClass
    public void setup() throws Exception {
        // API配置
        Properties prop = new Properties();
        InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
        prop.load(input);
        String baseUrl = prop.getProperty("base.url");
        RestAssured.baseURI = baseUrl;

        // 初始化文件数据提供者
        fileDataProvider = new FileDataProvider();
    }

    @Test
    public void testLoginWithCSVData() {
        // 从CSV文件读取测试数据
        List<Map<String, Object>> testDataList = fileDataProvider.readFromCSV("testdata/login_data.csv");

        for (Map<String, Object> rowData : testDataList) {
            String username = (String) rowData.get("username");
            String password = (String) rowData.get("password");

            // 处理数字类型的转换
            Object codeObj = rowData.get("expected_code");
            int expectedCode = 0;
            if (codeObj instanceof Number) {
                expectedCode = ((Number) codeObj).intValue();
            } else if (codeObj instanceof String) {
                expectedCode = Integer.parseInt((String) codeObj);
            }

            String expectedMsg = (String) rowData.get("expected_msg");

            testLogin(username, password, expectedCode, expectedMsg);
        }
    }

    @Test
    public void testLoginWithTXTData() {
        // 从TXT文件读取测试数据
        List<Map<String, Object>> testDataList = fileDataProvider.readFromTXT("testdata/login_data.txt");

        for (Map<String, Object> rowData : testDataList) {
            String username = (String) rowData.get("username");
            String password = (String) rowData.get("password");

            // 处理数字类型的转换
            Object codeObj = rowData.get("expected_code");
            int expectedCode = 0;
            if (codeObj instanceof Number) {
                expectedCode = ((Number) codeObj).intValue();
            } else if (codeObj instanceof String) {
                expectedCode = Integer.parseInt((String) codeObj);
            }

            String expectedMsg = (String) rowData.get("expected_msg");

            testLogin(username, password, expectedCode, expectedMsg);
        }
    }

    @Test
    public void testLoginWithAutoDetect() {
        // 自动检测文件类型并读取数据
        List<Map<String, Object>> testDataList = fileDataProvider.readFromFile("testdata/login_data.csv");

        for (Map<String, Object> rowData : testDataList) {
            String username = (String) rowData.get("username");
            String password = (String) rowData.get("password");

            // 处理数字类型的转换
            Object codeObj = rowData.get("expected_code");
            int expectedCode = 0;
            if (codeObj instanceof Number) {
                expectedCode = ((Number) codeObj).intValue();
            } else if (codeObj instanceof String) {
                expectedCode = Integer.parseInt((String) codeObj);
            }

            String expectedMsg = (String) rowData.get("expected_msg");

            testLogin(username, password, expectedCode, expectedMsg);
        }
    }

    private void testLogin(String username, String password, int expectedCode, String expectedMsg) {
        Map<String, Object> loginData = new HashMap<>();
        // 处理null值和empty值
        if ("null".equals(username) || "null".equals(password)) {
            loginData.put("username", null);
            loginData.put("password", null);
        } else {
            loginData.put("username", username);
            loginData.put("password", password);
        }

        Response response = given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(loginData)
                .when()
                .post("/login")
                .then()
                .log().ifError()
                .extract().response();

        int actualCode = response.jsonPath().getInt("code");
        String actualMsg = response.jsonPath().getString("msg");

        Assert.assertEquals(actualCode, expectedCode, "返回业务码不匹配");
        Assert.assertTrue(actualMsg.contains(expectedMsg), "返回消息不包含预期内容");

        System.out.printf("✅ 用户名=%s | 密码=%s | 预期code=%d | 实际=%d | 消息='%s'%n",
                username, password, expectedCode, actualCode, actualMsg);
    }
}
