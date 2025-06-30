# 更新日志

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