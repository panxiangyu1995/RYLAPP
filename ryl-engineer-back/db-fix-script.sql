-- 协助请求模块数据库修复脚本
USE ryl_engineer;
GO

-- 创建新的协助请求表（如果不存在）
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[assistance_request]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[assistance_request] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [request_id] VARCHAR(30) NOT NULL,
        [description] NVARCHAR(MAX) NOT NULL,
        [requester_id] BIGINT NOT NULL,
        [requester_name] VARCHAR(50),
        [engineer_id] BIGINT,
        [task_id] VARCHAR(20),
        [urgency] VARCHAR(10) DEFAULT 'medium',
        [status] VARCHAR(20) DEFAULT 'pending',
        [response] NVARCHAR(MAX),
        [create_time] DATETIME2 DEFAULT GETDATE(),
        [update_time] DATETIME2 DEFAULT GETDATE(),
        [handle_time] DATETIME2
    );
    
    -- 添加唯一约束
    ALTER TABLE [dbo].[assistance_request] ADD CONSTRAINT [uk_request_id] UNIQUE ([request_id]);
    
    PRINT '协助请求表创建成功';
END
ELSE
BEGIN
    PRINT '协助请求表已存在，跳过创建';
END
GO

-- 创建新的协助请求接收者表（如果不存在）
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[assistance_request_recipient]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[assistance_request_recipient] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [request_id] BIGINT NOT NULL,
        [recipient_id] BIGINT NOT NULL,
        [is_read] TINYINT DEFAULT 0,
        [read_time] DATETIME2,
        [create_time] DATETIME2 DEFAULT GETDATE(),
        [update_time] DATETIME2 DEFAULT GETDATE()
    );
    
    -- 添加外键约束
    ALTER TABLE [dbo].[assistance_request_recipient] ADD CONSTRAINT [FK_assistance_request_recipient_request] 
    FOREIGN KEY ([request_id]) REFERENCES [dbo].[assistance_request]([id]);
    
    PRINT '协助请求接收者表创建成功';
END
ELSE
BEGIN
    PRINT '协助请求接收者表已存在，跳过创建';
END
GO

-- 获取表中的列名信息，用于确认正确的列名
PRINT '检查表结构...';
DECLARE @has_data BIT = 0;

-- 检查表是否有数据
IF EXISTS (SELECT TOP 1 1 FROM [dbo].[assistance_request])
BEGIN
    SET @has_data = 1;
    PRINT '表中已有数据，跳过测试数据插入';
END
ELSE
BEGIN
    -- 获取表结构信息
    DECLARE @columns TABLE (column_name VARCHAR(100));
    INSERT INTO @columns
    SELECT COLUMN_NAME
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'assistance_request';
    
    -- 输出列名信息
    PRINT '表 assistance_request 的列名:';
    DECLARE @col VARCHAR(100);
    DECLARE column_cursor CURSOR FOR SELECT column_name FROM @columns;
    OPEN column_cursor;
    FETCH NEXT FROM column_cursor INTO @col;
    WHILE @@FETCH_STATUS = 0
    BEGIN
        PRINT '- ' + @col;
        FETCH NEXT FROM column_cursor INTO @col;
    END
    CLOSE column_cursor;
    DEALLOCATE column_cursor;

    -- 插入测试数据
    PRINT '插入测试数据...';
    BEGIN TRY
        -- 尝试简化的INSERT语句，避免列名错误
        INSERT INTO [dbo].[assistance_request]
            ([request_id], [description], [requester_id], [engineer_id], 
             [task_id], [urgency], [status], [create_time], [update_time])
        VALUES
            ('REQ20250630001', '需要协助处理气相色谱仪电源模块故障，客户现场急需', 2, 3, 
             'RP-2025-001', 'high', 'pending', GETDATE(), GETDATE());
        
        -- 获取刚插入的ID
        DECLARE @requestId BIGINT = SCOPE_IDENTITY();
        
        -- 添加接收者关系
        INSERT INTO [dbo].[assistance_request_recipient]
            ([request_id], [recipient_id], [is_read], [create_time], [update_time])
        VALUES
            (@requestId, 4, 0, GETDATE(), GETDATE());
        
        PRINT '测试数据插入成功';
    END TRY
    BEGIN CATCH
        PRINT '测试数据插入错误: ' + ERROR_MESSAGE();
    END CATCH
END
GO

PRINT '数据库修复完成';
GO

-- 4. 如果使用表名不一致问题的备用解决方案：创建视图
-- 注意：以下仅作为备用方案，优先使用Mapper文件修改方案
-- CREATE OR REPLACE VIEW announcement AS SELECT * FROM system_announcement; 