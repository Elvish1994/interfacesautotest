# autointerface

基于 TestNG + RestAssured 的接口自动化测试框架

## ✨ 特性

- 支持数据驱动测试（Data-Driven Testing）
- 使用 TestNG 进行测试管理
- 使用 RestAssured 发起 HTTP 请求
- 集成 Allure 生成美观测试报告

## 📦 安装
allure-2.35.1.zip 解压，配置环境变量，例如:C:\tools\allure-2.35.1\bin

### 生成并打开报告（推荐）
allure serve target/allure-results

### 或先生成再打开
allure generate target/allure-results -o target/allure-report --clean
allure open target/allure-report


