# 研仪联微信小程序后端服务

## 项目介绍
研仪联微信小程序后端服务，基于Spring Boot 2.7.18开发，提供微信小程序的API接口服务。

## 技术栈
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.3
- PageHelper 5.3.2
- SQL Server
- JWT
- WxJava SDK

## 项目结构
```
ryl-miniprogram-server/
├── src/main/java/com/ryl/miniprogram/
│   ├── config/           # 配置类
│   ├── controller/       # 控制器
│   ├── service/          # 服务层
│   ├── mapper/           # MyBatis映射接口
│   ├── entity/           # 实体类
│   ├── dto/              # 数据传输对象
│   ├── vo/               # 视图对象
│   ├── enums/            # 枚举类
│   ├── exception/        # 异常处理
│   ├── util/             # 工具类
│   ├── security/         # 安全相关
│   └── MiniProgramApplication.java # 启动类
├── src/main/resources/
│   ├── mapper/           # MyBatis XML映射文件
│   ├── application.yml   # 应用配置
│   ├── application-dev.yml # 开发环境配置
│   └── application-prod.yml # 生产环境配置
```

## 功能模块
1. 微信登录认证
2. 用户信息管理
3. 任务管理
4. 任务评论

## 运行说明
1. 配置数据库连接信息
   - 修改`application-dev.yml`中的数据库连接信息
2. 配置微信小程序信息
   - 修改`application.yml`中的微信小程序配置信息
3. 运行项目
   ```bash
   mvn spring-boot:run
   ```
   mvn clean install -DskipTests构建跳过测试
4. 访问API
   - 接口基础路径：http://localhost:8085/api/v1
   - 微信登录：POST /wechat/login
   - 用户信息：GET /user/info
   - 任务列表：GET /task/list

## API文档
详细API文档请参考`小程序API文档.md` 