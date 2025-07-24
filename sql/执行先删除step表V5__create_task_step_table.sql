-- SQL Server版本的客户表和设备表创建脚本
USE ryl_engineer;
GO
-- Flyway migration script V5 - Corrected and Completed
-- Creates the task_step table that fully matches the TaskStep.java entity.

-- 任务具体步骤实例表
-- This table tracks the status of each step for a specific task.
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[task_step]') AND type in (N'U'))
BEGIN
-- V5: 创建任务步骤表，并添加 step_key 字段
CREATE TABLE task_step
(
    id            BIGINT PRIMARY KEY IDENTITY (1,1),                           -- 主键ID
    task_id       VARCHAR(50) NOT NULL,                                      -- 关联的任务ID
    step_index    INT          NOT NULL,                                     -- 步骤顺序索引
    title         NVARCHAR(100) NOT NULL,                                    -- 步骤标题
    status        NVARCHAR(20) NOT NULL,                                     -- 步骤状态 (pending, in-progress, completed, skipped)
    step_key      NVARCHAR(50) NULL,                                         -- 步骤的业务标识符，用于前端识别特殊步骤
    start_time    DATETIME,                                                  -- 开始时间
    end_time      DATETIME,                                                  -- 完成时间
    estimated_time INT,                                                      -- 预计工时（分钟）
    operator_id   BIGINT,                                                    -- 操作员ID
    operator      NVARCHAR(50),                                              -- 操作员姓名
    options       NVARCHAR(MAX),                                             -- 预留字段，可存储JSON格式的额外选项
    create_time   DATETIME     DEFAULT GETDATE(),                            -- 创建时间
    update_time   DATETIME     DEFAULT GETDATE(),                            -- 更新时间
    CONSTRAINT fk_task_step_task_id FOREIGN KEY (task_id) REFERENCES task (task_id) ON DELETE CASCADE
);
END
GO 