# 研仪联工程师APP项目修复记录

## 已完成的修复

### 1. 联系人页面前端500错误修复（版本0.2.1）
- 修改了ContactsServiceImpl类的三个方法，添加了空检查和异常处理：
  - getOtherContacts方法：添加了null检查和try-catch块
  - getEngineerStatusByLocation方法：添加了try-catch块和对engineers为null的处理
  - getCustomerManagement方法：添加了try-catch块和对customers为null的处理
- 同时优化了getEngineerContacts和getUserWorkLocations方法，添加了异常处理和空值检查
- 提高了系统容错性，确保即使出现异常也能返回合理的空结果而不是500错误

### 2. CustomerServiceImpl类字段映射错误修复（版本0.2.2）
- 修复了CustomerServiceImpl类中Customer实体类字段名不匹配的问题
- 修改了以下字段映射关系，使其与Customer实体类保持一致：
  - companyName → name
  - contactName → contact
  - 移除了Customer实体中不存在的status字段设置
  - 使用salesId替代createdBy字段
- 提高了代码稳定性和可维护性

## 项目技术栈

### 前端
- 目录：D:\Android\rylappfront\ryl-engineer-app
- 技术：Vue3、Vue Router、Pinia
- 不使用Tailwind CSS样式
- 使用开源图标库制作UI

### 后端
- 目录：D:\Android\rylappfront\ryl-engineer-back
- 技术：Spring Boot 2.7.18、MyBatis 3.5.15、PageHelper 6.1.0
- 数据库：SQL Server

## 项目规范

### API规范
- 基础路径格式：`/api/v1/{模块}/{功能}`
- 前后端端口一致：8089
- 统一响应格式：
```json
{
  "code": 200,       // 状态码，200表示成功，非200表示失败
  "message": "操作成功", // 响应消息
  "data": {}         // 响应数据，根据接口不同而变化
}
```

### 代码规范
- 前后端以及数据库字段命名使用驼峰命名法且名称要一致
- API路径要一致，前后端路径都按参考登录模块的API路径来设置
- 前后端期望的数据格式要一致，包括内容

## 常见问题及解决方案

### 1. 前端500错误处理
- 后端API应添加完善的异常处理，确保即使出现异常也返回合理的空结果
- 对可能为null的列表进行检查后再操作，避免空指针异常
- 使用try-catch块包装可能出现异常的代码，确保API能正常响应

### 2. 实体类与DTO映射问题
- 在设计实体类和DTO时，应尽量保持字段名称一致，减少映射错误
- 如果必须使用不同的字段名，应在转换方法中添加清晰的注释说明映射关系
- 修改实体类后，应全面检查所有使用该实体类的服务类和映射文件

### 3. SQL Server分页查询问题
- SQL Server在子查询中使用ORDER BY子句时，必须同时指定TOP、OFFSET或FOR XML
- 使用PageHelper进行分页查询时，可以通过PageHelper.orderBy()方法设置排序
- 对于复杂查询，考虑使用"TOP 100 PERCENT"语法或完全展开SQL查询