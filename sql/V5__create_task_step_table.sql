-- SQL Server版本的客户表和设备表创建脚本
USE ryl_engineer;
GO
-- Flyway migration script V5 - Corrected and Completed
-- Creates the task_step table that fully matches the TaskStep.java entity.

-- 任务具体步骤实例表
-- This table tracks the status of each step for a specific task.
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[task_step]') AND type in (N'U'))
BEGIN
CREATE TABLE task_step (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    task_id VARCHAR(50) NOT NULL,                           -- 关联的任务ID
    step_index INT NOT NULL,                                -- 步骤顺序索引
    title VARCHAR(100) NOT NULL,                            -- 步骤标题
    status VARCHAR(20) DEFAULT 'pending' NOT NULL,          -- 步骤状态
    start_time DATETIME2,                                   -- 开始时间
    end_time DATETIME2,                                     -- 结束时间
    estimated_time VARCHAR(50),                             -- 预计工时
    operator_id BIGINT,                                     -- 操作人ID
    operator VARCHAR(100),                                  -- 操作人姓名
    options NVARCHAR(MAX),                                  -- 步骤可选项（JSON格式）
    create_time DATETIME2 NOT NULL DEFAULT GETDATE(),       -- 创建时间
    update_time DATETIME2 NOT NULL DEFAULT GETDATE(),       -- 更新时间
    CONSTRAINT fk_task_step_task FOREIGN KEY (task_id) REFERENCES task(task_id) ON DELETE CASCADE,
    CONSTRAINT uk_task_step_task_step UNIQUE (task_id, step_index)
);
CREATE INDEX idx_task_id_step ON task_step(task_id);
END
GO 