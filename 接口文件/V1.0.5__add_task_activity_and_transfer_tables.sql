-- 创建任务活动记录表
CREATE TABLE task_activity (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    task_id VARCHAR(50) NOT NULL,
    activity_type VARCHAR(50) NOT NULL,
    content NVARCHAR(500) NULL,
    operator_id BIGINT NULL,
    operator_name NVARCHAR(100) NULL,
    create_time DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_task_activity_task FOREIGN KEY (task_id) REFERENCES task (task_id)
);

-- 创建索引
CREATE INDEX IDX_task_activity_task_id ON task_activity (task_id);
CREATE INDEX IDX_task_activity_type ON task_activity(activity_type);

-- 创建任务转出历史表
CREATE TABLE task_transfer_history (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    task_id VARCHAR(50) NOT NULL,
    from_engineer_id BIGINT NULL,
    to_engineer_id BIGINT NOT NULL,
    transfer_time DATETIME NOT NULL,
    note NVARCHAR(500) NULL,
    create_time DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_task_transfer_history_task FOREIGN KEY (task_id) REFERENCES task (task_id)
);

-- 创建索引
CREATE INDEX IDX_task_transfer_history_task_id ON task_transfer_history (task_id);
CREATE INDEX IDX_task_transfer_history_to_engineer_id ON task_transfer_history (to_engineer_id); 