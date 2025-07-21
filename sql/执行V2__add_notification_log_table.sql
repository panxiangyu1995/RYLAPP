-- 任务图片表结构更新脚本
USE ryl_engineer;
GO

CREATE TABLE notification_log (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    task_id VARCHAR(50) NOT NULL,
    customer_id BIGINT NOT NULL,
    template_id VARCHAR(100) NOT NULL,
    status VARCHAR(10) NOT NULL, -- 'SENT', 'FAILED'
    response_code VARCHAR(20),
    response_message NVARCHAR(500),
    create_time DATETIME2 DEFAULT GETDATE(),
    update_time DATETIME2 DEFAULT GETDATE()
);
CREATE INDEX idx_task_id_template_id ON notification_log(task_id, template_id); 