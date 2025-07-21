-- 任务图片表结构更新脚本
USE ryl_engineer;
GO
-- V4: Add task_id to attachment tables and clean up orphan records
-- This script replaces the failed V3 script with a more robust version.

-- Step 1: Add a nullable task_id column to record_image
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[record_image]') AND name = 'task_id')
BEGIN
    ALTER TABLE [dbo].[record_image] ADD task_id VARCHAR(50) NULL;
END
GO

-- Step 2: Add a nullable task_id column to record_file
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[record_file]') AND name = 'task_id')
BEGIN
    ALTER TABLE [dbo].[record_file] ADD task_id VARCHAR(50) NULL;
END
GO

-- Step 3: Safely backfill task_id for existing records in record_image
-- Only update records that can be successfully joined with step_record
UPDATE ri
SET ri.task_id = sr.task_id
FROM [dbo].[record_image] ri
JOIN [dbo].[step_record] sr ON ri.record_id = sr.id
WHERE ri.task_id IS NULL AND sr.task_id IS NOT NULL;
GO

-- Step 4: Safely backfill task_id for existing records in record_file
UPDATE rf
SET rf.task_id = sr.task_id
FROM [dbo].[record_file] rf
JOIN [dbo].[step_record] sr ON rf.record_id = sr.id
WHERE rf.task_id IS NULL AND sr.task_id IS NOT NULL;
GO

-- Step 5: Clean up orphan records from record_image (records that couldn't be backfilled)
-- This is crucial to enforce the NOT NULL constraint later
DELETE FROM [dbo].[record_image] WHERE task_id IS NULL;
GO

-- Step 6: Clean up orphan records from record_file
DELETE FROM [dbo].[record_file] WHERE task_id IS NULL;
GO

-- Step 7: Alter the task_id column to be NOT NULL in record_image
ALTER TABLE [dbo].[record_image] ALTER COLUMN task_id VARCHAR(50) NOT NULL;
GO

-- Step 8: Alter the task_id column to be NOT NULL in record_file
ALTER TABLE [dbo].[record_file] ALTER COLUMN task_id VARCHAR(50) NOT NULL;
GO

-- Step 9: Create an index on task_id for record_image to improve query performance
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_record_image_task_id' AND object_id = OBJECT_ID('[dbo].[record_image]'))
BEGIN
    CREATE INDEX idx_record_image_task_id ON [dbo].[record_image](task_id);
END
GO

-- Step 10: Create an index on task_id for record_file
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_record_file_task_id' AND object_id = OBJECT_ID('[dbo].[record_file]'))
BEGIN
    CREATE INDEX idx_record_file_task_id ON [dbo].[record_file](task_id);
END
GO 