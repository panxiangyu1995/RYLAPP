# RYL Engineer Backend

科研仪器维修公司内部工程师使用的后端系统。

## 技术栈

- Spring Boot 2.7.18
- MyBatis 3.5.15
- PageHelper 6.1.0
- MySQL

## 项目结构

```
ryl-engineer-back
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── ryl
│   │   │           └── engineer
│   │   │               ├── common      // 通用类
│   │   │               ├── config      // 配置类
│   │   │               ├── controller  // 控制器
│   │   │               ├── entity      // 实体类
│   │   │               ├── mapper      // MyBatis映射接口
│   │   │               └── service     // 服务接口及实现
│   │   │                   └── impl
│   │   └── resources
│   │       ├── mapper      // MyBatis XML映射文件
│   │       ├── static      // 静态资源
│   │       └── templates   // 模板文件
│   └── test                // 测试代码
└── pom.xml
```

## 开发环境

- JDK 1.8+
- Maven 3.9.9+
- MySQL 5.7+

## 运行方法

1. 克隆项目到本地
2. 创建MySQL数据库：`ryl_engineer`
3. 修改`application.yml`中的数据库连接信息
4. 执行命令：`mvn spring-boot:run`
5. 访问：`http://localhost:8080/api/test/hello`

## API文档

待完善... 