-- 为 task 表添加 device_type 字段
-- 这个字段用于存储任务关联设备所属的仪器类型，是实现任务自动派发的关键依据。
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[task]') AND name = 'device_type')
BEGIN
    ALTER TABLE [dbo].[task]
    ADD [device_type] VARCHAR(50) NULL;
    PRINT 'Column [device_type] added to table [task].'
END
ELSE
BEGIN
    PRINT 'Column [device_type] already exists in table [task].'
END
GO 