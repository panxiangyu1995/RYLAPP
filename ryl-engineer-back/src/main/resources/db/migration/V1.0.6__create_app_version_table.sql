-- SQL Server版本的客户表和设备表创建脚本
USE ryl_engineer;
GO

-- ----------------------------
-- Table structure for app_version
-- ----------------------------
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[app_version]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[app_version](
        [id] [bigint] IDENTITY(1,1) NOT NULL,
        [version_code] [int] NOT NULL,
        [version_name] [varchar](50) COLLATE Chinese_PRC_CI_AS NOT NULL,
        [update_log] [varchar](500) COLLATE Chinese_PRC_CI_AS NULL,
        [download_url] [varchar](255) COLLATE Chinese_PRC_CI_AS NOT NULL,
        [is_forced] [bit] NOT NULL DEFAULT ((0)),
        [created_at] [datetime2](7) NOT NULL DEFAULT (getdate()),
        PRIMARY KEY CLUSTERED
        (
            [id] ASC
        )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
    ) ON [PRIMARY]
END;
GO

-- ----------------------------
-- Records of app_version
-- ----------------------------
INSERT INTO [dbo].[app_version] ([version_code], [version_name], [update_log], [download_url], [is_forced], [created_at])
VALUES (1, N'1.0.0', N'首次发布', N'https://example.com/download/v1.0.0', 0, GETDATE());

GO 