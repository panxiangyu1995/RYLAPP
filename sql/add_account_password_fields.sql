-- 描述: 为 customer 表添加账号密码登录所需的字段
-- 此脚本为customer表添加用户名、密码、盐值和注册时间字段

USE ryl_engineer;
GO

-- 检查并添加 username 列
IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'username' AND Object_ID = Object_ID(N'dbo.customer'))
BEGIN
    ALTER TABLE [dbo].[customer] ADD [username] NVARCHAR(50) NULL;
    EXEC sp_addextendedproperty 'MS_Description', N'登录用户名', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'username';
END
GO

-- 检查并添加 password 列
IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'password' AND Object_ID = Object_ID(N'dbo.customer'))
BEGIN
    ALTER TABLE [dbo].[customer] ADD [password] NVARCHAR(255) NULL;
    EXEC sp_addextendedproperty 'MS_Description', N'加密存储的密码', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'password';
END
GO

-- 检查并添加 salt 列
IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'salt' AND Object_ID = Object_ID(N'dbo.customer'))
BEGIN
    ALTER TABLE [dbo].[customer] ADD [salt] NVARCHAR(32) NULL;
    EXEC sp_addextendedproperty 'MS_Description', N'密码加密盐值', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'salt';
END
GO

-- 检查并添加 register_time 列
IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'register_time' AND Object_ID = Object_ID(N'dbo.customer'))
BEGIN
    ALTER TABLE [dbo].[customer] ADD [register_time] DATETIME2 NULL DEFAULT GETDATE();
    EXEC sp_addextendedproperty 'MS_Description', N'注册时间', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'register_time';
END
GO

-- 为username添加唯一索引，确保用户名唯一
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'UX_customer_username' AND object_id = OBJECT_ID('dbo.customer'))
BEGIN
    CREATE UNIQUE INDEX UX_customer_username ON [dbo].[customer] (username) WHERE username IS NOT NULL;
END
GO

PRINT '账号密码登录字段添加完成'; 