-- 任务图片表结构更新脚本
USE ryl_engineer;
GO

-- 为task_image表添加缺失的字段
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[task_image]') AND name = 'image_type')
BEGIN
    ALTER TABLE task_image ADD image_type VARCHAR(20);
    PRINT '已添加 image_type 字段到 task_image 表';
END
ELSE
BEGIN
    PRINT 'image_type 字段已存在于 task_image 表中';
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[task_image]') AND name = 'sort')
BEGIN
    ALTER TABLE task_image ADD sort INT DEFAULT 0;
    PRINT '已添加 sort 字段到 task_image 表';
END
ELSE
BEGIN
    PRINT 'sort 字段已存在于 task_image 表中';
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[task_image]') AND name = 'update_time')
BEGIN
    ALTER TABLE task_image ADD update_time DATETIME2 DEFAULT GETDATE();
    PRINT '已添加 update_time 字段到 task_image 表';
END
ELSE
BEGIN
    PRINT 'update_time 字段已存在于 task_image 表中';
END
GO

-- 创建任务附件表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[task_attachment]') AND type in (N'U'))
BEGIN
CREATE TABLE task_attachment (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    task_id VARCHAR(50) NOT NULL, /* 任务ID */
    file_name VARCHAR(100) NOT NULL, /* 文件名 */
    file_url VARCHAR(255) NOT NULL, /* 文件URL */
    file_type VARCHAR(20), /* 文件类型 */
    file_size BIGINT, /* 文件大小(字节) */
    sort INT DEFAULT 0, /* 排序 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE() /* 更新时间 */
);

CREATE INDEX idx_task_id_attachment ON task_attachment(task_id);
PRINT '已创建 task_attachment 表及索引';
END
ELSE
BEGIN
    PRINT 'task_attachment 表已存在';
END
GO

-- 更新完成后的确认信息
PRINT '数据库结构更新完成';
GO 