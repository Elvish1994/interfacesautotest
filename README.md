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



# 执行过程轨迹

## 第一步:
###  1、获取文件的对应的地址url
###  2、找到对应配置的数据结构类

## 第二步:
###  1、读取excel文件的sheet页，名为数据准备
###  2、除去行首，依次执行下面的sql语句
###  3、执行失败或者异常，测试用例失败


## 第三步:
###  1、读取excel文件的sheet2页，名为测试用例
###   2、根据测试用例001作为入参，根据第一行的字段名称来组成数据结构，没有值的用null来组成
###   3、和sheet3响应的测试用例001的结果有的就进行比对，如果全部相同，则测试用例通过，没有则失败
###   4、一条测试用例，就是一个测试用例方法。

