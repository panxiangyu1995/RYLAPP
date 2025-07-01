# 更新日志

## 0.1.7 - 2025-07-01
### 修复
- 修复了工程师详情页面获取已完成任务列表的SQL语法错误
  - 问题: SQL语句中使用了方括号 `]` 而不是圆括号 `)` 结束子查询，导致SQL Server报"DESC附近有语法错误"
  - 解决方法: 重写了获取已完成任务的SQL查询，确保使用正确的语法并使用`sr.task_id = t.task_id`进行关联
  - 经验: SQL语句应该仔细检查括号匹配和表关联条件，特别是在复制修改SQL片段时

## 0.1.6 - 2025-07-02
### 优化
- 将测试API的实现方式集成到主要API中，使用JdbcTemplate直接查询数据库
- 移除了前端对测试API的依赖，统一使用主要API
- 简化了前端代码，移除了不必要的测试功能
- 保留了刷新功能，方便用户手动刷新联系人列表

### 修复
- 完全解决了"其它联系人"页面不显示数据的问题
- 统一了前后端数据交互方式，确保数据正确显示
- 移除了不必要的分页逻辑，简化了数据处理流程

## 0.1.5 - 2025-07-02
### 优化
- 修改了前端"其它联系人"页面的数据加载逻辑，直接使用测试端点获取数据
- 移除了分页逻辑，简化了数据处理流程
- 隐藏了测试按钮，将其功能集成到主要的联系人加载逻辑中

### 修复
- 解决了"其它联系人"页面不显示数据的问题
- 统一了前后端数据格式，确保数据正确显示

## 0.1.4 - 2025-07-01
### 优化
- 进一步简化了ContactsRelationMapper.xml中的SQL查询，使用子查询解决ORDER BY语法问题
- 移除了Mapper接口中不必要的userId参数，简化方法签名
- 添加了更详细的日志记录，便于诊断问题
- 添加了测试端点，用于直接查询非工程师用户数据

### 修复
- 解决了SQL Server中子查询和ORDER BY组合使用的语法问题
- 修复了参数不匹配导致的潜在问题
- 添加了前端测试功能，便于验证API返回数据

## 0.1.3 - 2025-07-01
### 优化
- 修复了ContactsRelationMapper.xml中的SQL Server兼容性问题，使用TOP 100 PERCENT解决ORDER BY语法错误
- 将查询中的`last_active_time`字段改为`last_login_time`，与数据库表结构保持一致
- 移除了对当前用户ID的过滤条件，符合"所有用户都看到相同联系人列表"的业务需求
- 增强了SQL异常日志记录，便于诊断问题

### 修复
- 解决了"其它联系人"功能返回空列表的问题
- 修复了SQL Server中ORDER BY子句在GROUP BY查询中的语法限制问题
- 优化了错误处理机制，提供更详细的SQL异常信息

## 0.1.2 - 2025-06-30
### 优化
- 修改了ContactsRelationMapper.xml中的selectOtherContacts查询，确保与SQL Server方言兼容
- 调整了SQL查询语法，使用SQL Server兼容的字符串连接方式（'%' + #{keyword} + '%'）
- 增加了对department字段的NULL值检查，避免空值导致的查询错误
- 在ContactsServiceImpl中增强了日志记录，添加了详细的执行步骤和参数信息
- 添加了更强大的异常处理机制，确保在出现错误时返回空列表而不是抛出异常

### 修复
- 修复了"其它联系人"功能在SQL Server环境下的兼容性问题
- 优化了convertUserToContactDTO方法，使用lastLoginTime字段作为最后活跃时间
- 增强了空值处理，避免因空值导致的NullPointerException

## 0.1.1 - 2025-06-30
### 优化
- 重构了联系人模块的"其它联系人"功能，移除了对不存在的contacts_relation表的依赖
- 修改了ContactsRelationMapper.xml中的selectOtherContacts查询，直接从用户表和角色表获取数据
- 更新了ContactsRelationMapper接口，将selectOtherContacts方法的返回类型修改为User列表
- 调整了ContactsServiceImpl中的getOtherContactsList方法，适配新的数据查询方式

### 修复
- 解决了"其它联系人"功能返回500内部服务器错误的问题
- 简化了联系人数据模型，符合"只要是APP的用户就在联系人列表内"的需求

## 0.1.0 - 2025-06-18
### 添加
- 创建了缺失的MyBatis Mapper XML文件：
  - ChatConversationMapper.xml
  - ChatConversationMemberMapper.xml
  - ChatMessageMapper.xml
  - ChatMessageReadMapper.xml
  - AssistanceRequestMapper.xml
- 修复了AnnouncementMapper.xml中的参数不匹配问题，将type参数改为keyword，并添加了selectRecentList方法

### 修复
- 解决了"Invalid bound statement (not found)"错误，通过添加缺失的Mapper XML文件
- 修复了"Parameter 'type' not found. Available parameters are [keyword, param1]"错误，通过修改AnnouncementMapper.xml

### 目的
- 使聊天、公告和协助请求功能的API正常工作
- 确保前端请求到后端API时能够正常查询数据库

## 项目更新日志

### 版本 0.0.1
- 2024-07-25: 项目初始结构搭建
- 2024-07-26: 基础业务功能实现
- 2024-07-27: 单元测试编写
- 2024-07-28: 性能优化与代码重构

### 版本 0.0.2
- 2024-07-29: 数据库迁移从MySQL到SQL Server 2019
  - 更新数据库连接配置
  - 添加SQL Server JDBC驱动依赖
  - 创建SQL Server配置类解决Lombok兼容性问题
  - 创建Flyway SQL Server配置
  - 添加数据库诊断工具
  - 更新MyBatis SQL方言配置
  - 修改实体类和Mapper文件兼容SQL Server语法
  - 创建测试连接端点验证SQL Server连接 