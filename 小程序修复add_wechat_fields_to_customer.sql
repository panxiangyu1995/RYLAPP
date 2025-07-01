    -- 文件路径: /dbschema/migrations/V1.0.6__add_wechat_fields_to_customer.sql
    -- 描述: 为 customer 表添加微信小程序集成所需字段

    USE ryl_engineer;
    GO

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