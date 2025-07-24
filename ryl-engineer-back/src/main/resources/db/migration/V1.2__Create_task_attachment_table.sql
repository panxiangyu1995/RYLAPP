-- 创建任务附件表
CREATE TABLE task_attachment (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    task_id NVARCHAR(50) NOT NULL,
    file_name NVARCHAR(255) NOT NULL,
    file_url NVARCHAR(512),
    file_type NVARCHAR(50),
    file_size BIGINT,
    sort INT,
    create_time DATETIME2 DEFAULT GETDATE(),
    update_time DATETIME2
);

-- 为task_id创建索引以提高查询性能
CREATE INDEX idx_task_attachment_task_id ON task_attachment(task_id); 