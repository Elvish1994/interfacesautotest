# 目录结构
├── src/
│   ├── main/
│   │   └── java/
│   │       └── utils/           # 工具类：加密、随机数据、日期等
│   ├── test/
│   │   ├── java/
│   │   │   ├── config/          # 配置类读取 properties/yaml
│   │   │   ├── api/             # 封装的 API 类（如 LoginAPI, OrderAPI）
│   │   │   ├── tests/           # 测试用例类
│   │   │   └── base/            # 基础类（如 BaseTest）
│   │   ├── resources/
│   │   │   ├── config/
│   │   │   │   ├── test.properties
│   │   │   ├── data/
│   │   │   │   ├── test_data.json
│   │   │   │   └── test_data.yaml
│   │   │   └── log4j2.xml       # 日志配置
│   │   └── testng.xml            # TestNG 配置文件
├── target/                       # 编译输出
├── reports/
│   └── allure-results/           # Allure 报告结果
├── pom.xml                       # Maven 依赖管理
└── README.md

