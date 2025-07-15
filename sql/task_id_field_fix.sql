-- 修改task表的task_id字段长度
USE ryl_engineer;
GO

-- 检查task表是否存在
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[task]') AND type in (N'U'))
BEGIN
    -- 检查约束
    DECLARE @constraint_name NVARCHAR(256)
    SELECT @constraint_name = name FROM sys.key_constraints
    WHERE parent_object_id = OBJECT_ID('task') AND [type] = 'UQ'
    
    -- 如果存在唯一约束，先删除它
    IF @constraint_name IS NOT NULL
    BEGIN
        DECLARE @sql NVARCHAR(MAX) = N'ALTER TABLE task DROP CONSTRAINT ' + @constraint_name
        EXEC sp_executesql @sql
        PRINT '已删除唯一约束: ' + @constraint_name
    END
    
    -- 修改字段长度
    ALTER TABLE task ALTER COLUMN task_id VARCHAR(50) NOT NULL;
    PRINT '已将task表的task_id字段长度从VARCHAR(20)修改为VARCHAR(50)'
    
    -- 重新创建唯一约束
    IF @constraint_name IS NOT NULL
    BEGIN
        ALTER TABLE task ADD CONSTRAINT uk_task_id UNIQUE (task_id);
        PRINT '已重新创建唯一约束: uk_task_id'
    END
    
    -- 检查引用task_id的其他表，并更新它们的字段长度
    
    -- 任务协作者表
    IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[task_collaborator]') AND type in (N'U'))
    BEGIN
        ALTER TABLE task_collaborator ALTER COLUMN task_id VARCHAR(50) NOT NULL;
        PRINT '已将task_collaborator表的task_id字段长度从VARCHAR(20)修改为VARCHAR(50)'
    END
    
    -- 任务工时记录表
    IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[task_work_hour]') AND type in (N'U'))
    BEGIN
        ALTER TABLE task_work_hour ALTER COLUMN task_id VARCHAR(50) NOT NULL;
        PRINT '已将task_work_hour表的task_id字段长度从VARCHAR(20)修改为VARCHAR(50)'
    END
    
    -- 任务状态历史表
    IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[task_status_history]') AND type in (N'U'))
    BEGIN
        ALTER TABLE task_status_history ALTER COLUMN task_id VARCHAR(50) NOT NULL;
        PRINT '已将task_status_history表的task_id字段长度从VARCHAR(20)修改为VARCHAR(50)'
    END
    
    -- 任务图片表
    IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[task_image]') AND type in (N'U'))
    BEGIN
        ALTER TABLE task_image ALTER COLUMN task_id VARCHAR(50) NOT NULL;
        PRINT '已将task_image表的task_id字段长度从VARCHAR(20)修改为VARCHAR(50)'
    END
    
    -- 任务工程师关联表
    IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[task_engineer]') AND type in (N'U'))
    BEGIN
        ALTER TABLE task_engineer ALTER COLUMN task_id VARCHAR(50) NOT NULL;
        PRINT '已将task_engineer表的task_id字段长度从VARCHAR(20)修改为VARCHAR(50)'
    END
    
    -- 任务流程表
    IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[task_flow]') AND type in (N'U'))
    BEGIN
        ALTER TABLE task_flow ALTER COLUMN task_id VARCHAR(50) NOT NULL;
        PRINT '已将task_flow表的task_id字段长度从VARCHAR(20)修改为VARCHAR(50)'
    END
    
    -- 步骤记录描述表
    IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[step_record]') AND type in (N'U'))
    BEGIN
        ALTER TABLE step_record ALTER COLUMN task_id VARCHAR(50) NOT NULL;
        PRINT '已将step_record表的task_id字段长度从VARCHAR(20)修改为VARCHAR(50)'
    END
    
    -- 服务评价表
    IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[service_evaluation]') AND type in (N'U'))
    BEGIN
        ALTER TABLE service_evaluation ALTER COLUMN task_id VARCHAR(50) NOT NULL;
        PRINT '已将service_evaluation表的task_id字段长度从VARCHAR(20)修改为VARCHAR(50)'
    END
    
    -- 任务协作邀请表
    IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[collaboration_invite]') AND type in (N'U'))
    BEGIN
        ALTER TABLE collaboration_invite ALTER COLUMN task_id VARCHAR(50) NOT NULL;
        PRINT '已将collaboration_invite表的task_id字段长度从VARCHAR(20)修改为VARCHAR(50)'
    END
    
    -- 任务转出记录表
    IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[task_transfer]') AND type in (N'U'))
    BEGIN
        ALTER TABLE task_transfer ALTER COLUMN task_id VARCHAR(50) NOT NULL;
        PRINT '已将task_transfer表的task_id字段长度从VARCHAR(20)修改为VARCHAR(50)'
    END
    
    PRINT '所有相关表的task_id字段长度均已从VARCHAR(20)修改为VARCHAR(50)'
END
ELSE
BEGIN
    PRINT 'task表不存在，请先创建表'
END
GO 