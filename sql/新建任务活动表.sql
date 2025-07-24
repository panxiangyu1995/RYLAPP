-- Create task_activity table
USE ryl_engineer;
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='task_activity' and xtype='U')
CREATE TABLE task_activity (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    task_id NVARCHAR(255) NOT NULL,
    activity_type NVARCHAR(255) NOT NULL,
    content NVARCHAR(MAX) NOT NULL,
    operator_id BIGINT,
    operator_name NVARCHAR(255),
    create_time DATETIME DEFAULT GETDATE()
);
GO