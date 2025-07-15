-- 描述: 为 customer 表添加微信小程序集成所需的所有字段（合并版本）
-- 此脚本包含 小程序修复add_wechat_fields_to_customer.sql 和 customer_table_fix_missing_fields.sql 的所有内容

USE ryl_engineer;
GO

-- =================== 添加基本微信字段 ===================

-- 检查并添加 wechat_openid 列
IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'wechat_openid' AND Object_ID = Object_ID(N'dbo.customer'))
BEGIN
    ALTER TABLE [dbo].[customer] ADD [wechat_openid] NVARCHAR(128) NULL;
    EXEC sp_addextendedproperty 'MS_Description', N'微信小程序用户的唯一标识OpenID', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'wechat_openid';
END
GO

-- 检查并添加 wechat_unionid 列
IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'wechat_unionid' AND Object_ID = Object_ID(N'dbo.customer'))
BEGIN
    ALTER TABLE [dbo].[customer] ADD [wechat_unionid] NVARCHAR(128) NULL;
    EXEC sp_addextendedproperty 'MS_Description', N'微信开放平台下的唯一应用标识UnionID', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'wechat_unionid';
END
GO
    
-- 检查并添加 wechat_avatar_url 列
IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'wechat_avatar_url' AND Object_ID = Object_ID(N'dbo.customer'))
BEGIN
    ALTER TABLE [dbo].[customer] ADD [wechat_avatar_url] NVARCHAR(512) NULL;
    EXEC sp_addextendedproperty 'MS_Description', N'用户的微信头像URL', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'wechat_avatar_url';
END
GO

-- 为 openid 添加唯一索引以防止重复用户，仅对非NULL值生效
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'UQ_customer_wechat_openid')
BEGIN
    CREATE UNIQUE INDEX UQ_customer_wechat_openid ON [dbo].[customer]([wechat_openid]) WHERE [wechat_openid] IS NOT NULL;
END
GO

-- =================== 添加其他缺失字段 ===================

-- 检查并添加 remark 列
IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'remark' AND Object_ID = Object_ID(N'dbo.customer'))
BEGIN
    ALTER TABLE [dbo].[customer] ADD [remark] NVARCHAR(500) NULL;
    EXEC sp_addextendedproperty 'MS_Description', N'备注', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'remark';
END
GO

-- 检查并添加 nickname 列
IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'nickname' AND Object_ID = Object_ID(N'dbo.customer'))
BEGIN
    ALTER TABLE [dbo].[customer] ADD [nickname] NVARCHAR(100) NULL;
    EXEC sp_addextendedproperty 'MS_Description', N'微信昵称', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'nickname';
END
GO

-- 检查并添加 gender 列
IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'gender' AND Object_ID = Object_ID(N'dbo.customer'))
BEGIN
    ALTER TABLE [dbo].[customer] ADD [gender] INT NULL;
    EXEC sp_addextendedproperty 'MS_Description', N'性别（0未知，1男，2女）', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'gender';
END
GO

-- 检查并添加 country 列
IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'country' AND Object_ID = Object_ID(N'dbo.customer'))
BEGIN
    ALTER TABLE [dbo].[customer] ADD [country] NVARCHAR(50) NULL;
    EXEC sp_addextendedproperty 'MS_Description', N'国家', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'country';
END
GO

-- 检查并添加 province 列
IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'province' AND Object_ID = Object_ID(N'dbo.customer'))
BEGIN
    ALTER TABLE [dbo].[customer] ADD [province] NVARCHAR(50) NULL;
    EXEC sp_addextendedproperty 'MS_Description', N'省份', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'province';
END
GO

-- 检查并添加 city 列
IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'city' AND Object_ID = Object_ID(N'dbo.customer'))
BEGIN
    ALTER TABLE [dbo].[customer] ADD [city] NVARCHAR(50) NULL;
    EXEC sp_addextendedproperty 'MS_Description', N'城市', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'city';
END
GO

-- 检查并添加 language 列
IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'language' AND Object_ID = Object_ID(N'dbo.customer'))
BEGIN
    ALTER TABLE [dbo].[customer] ADD [language] NVARCHAR(20) NULL;
    EXEC sp_addextendedproperty 'MS_Description', N'语言', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'language';
END
GO

-- 检查并添加 session_key 列
IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'session_key' AND Object_ID = Object_ID(N'dbo.customer'))
BEGIN
    ALTER TABLE [dbo].[customer] ADD [session_key] NVARCHAR(255) NULL;
    EXEC sp_addextendedproperty 'MS_Description', N'微信会话密钥', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'session_key';
END
GO

-- 检查并添加 last_login_time 列
IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'last_login_time' AND Object_ID = Object_ID(N'dbo.customer'))
BEGIN
    ALTER TABLE [dbo].[customer] ADD [last_login_time] DATETIME2 NULL;
    EXEC sp_addextendedproperty 'MS_Description', N'最后登录时间', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'last_login_time';
END
GO

-- 添加字段映射注释，方便开发人员了解Java对象属性与数据库字段的对应关系
IF NOT EXISTS (SELECT * FROM fn_listextendedproperty('MS_Description', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'name') WHERE value LIKE 'Java%')
BEGIN
    EXEC sp_addextendedproperty 'MS_Description', N'Java属性映射: customerName', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'name';
    EXEC sp_addextendedproperty 'MS_Description', N'Java属性映射: contactPerson', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'contact';
    EXEC sp_addextendedproperty 'MS_Description', N'Java属性映射: contactPhone', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'phone';
    EXEC sp_addextendedproperty 'MS_Description', N'Java属性映射: openid', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'wechat_openid';
    EXEC sp_addextendedproperty 'MS_Description', N'Java属性映射: unionid', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'wechat_unionid';
    EXEC sp_addextendedproperty 'MS_Description', N'Java属性映射: avatarUrl', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'wechat_avatar_url';
END
GO

-- 提供帮助信息，记录在描述中
PRINT '已成功添加customer表所有微信小程序所需字段';
PRINT '现在customer表的结构与Java中的Customer实体类匹配'; 