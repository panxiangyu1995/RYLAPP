# 测试说明文档

## 测试环境准备

1. 确保MySQL数据库已启动，并已创建`ryl_engineer`数据库
2. 执行`用户认证模块初始化.sql`脚本，初始化数据库表和测试数据
3. 确认`application-test.yml`中的数据库连接配置正确

## 测试分类

### 1. 数据库连接测试

位置：`com.ryl.engineer.db.DatabaseConnectionTest`

测试内容：
- 数据库连接是否正常
- JDBC基本查询功能
- MyBatis Mapper的CRUD操作

运行方法：
```bash
# 运行所有测试
mvn test -Dtest=DatabaseConnectionTest

# 运行单个测试方法
mvn test -Dtest=DatabaseConnectionTest#testDatabaseConnection
```

### 2. 服务层测试

位置：`com.ryl.engineer.service.UserServiceTest`

测试内容：
- 用户登录功能
- 用户注册功能
- 忘记密码功能
- 重置密码功能

运行方法：
```bash
# 运行所有测试
mvn test -Dtest=UserServiceTest

# 运行单个测试方法
mvn test -Dtest=UserServiceTest#testLogin
```

### 3. 控制器层测试

位置：`com.ryl.engineer.controller.UserControllerTest`

测试内容：
- 登录接口
- 注册接口
- 忘记密码接口
- 重置密码接口

运行方法：
```bash
# 运行所有测试
mvn test -Dtest=UserControllerTest

# 运行单个测试方法
mvn test -Dtest=UserControllerTest#testLogin
```

## 运行所有测试

```bash
# 运行所有测试
mvn test

# 运行特定包下的测试
mvn test -Dtest="com.ryl.engineer.db.*"
```

## 测试注意事项

1. 服务层和数据库连接测试会实际操作数据库，请确保数据库中有测试数据
2. 控制器测试使用了Mock对象，不会实际操作数据库
3. 带有`@Transactional`注解的测试会自动回滚，不会影响数据库中的数据
4. 如果测试失败，请检查数据库连接配置和测试数据是否正确

## 测试数据

默认测试账号：
- 工号：admin
- 密码：123456
- 手机号：13800138000 