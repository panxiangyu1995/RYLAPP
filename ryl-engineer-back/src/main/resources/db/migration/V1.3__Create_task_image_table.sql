-- 创建任务图片表
CREATE TABLE task_image (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    task_id NVARCHAR(50) NOT NULL,
    image_url NVARCHAR(512),
    image_type INT,
    sort INT,
    create_time DATETIME2 DEFAULT GETDATE(),
    update_time DATETIME2
);

-- 为task_id创建索引以提高查询性能
CREATE INDEX idx_task_image_task_id ON task_image(task_id); 