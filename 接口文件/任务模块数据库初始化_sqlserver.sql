-- 任务模块数据库设计
USE ryl_engineer;
GO

-- 任务表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[task]') AND type in (N'U'))
BEGIN
CREATE TABLE task (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL, /* 任务编号（格式：类型缩写-年份-序号，如RP-2024-001） */
    title VARCHAR(100) NOT NULL, /* 任务标题 */
    task_type VARCHAR(20) NOT NULL, /* 任务类型（repair/maintenance/recycle/leasing/training/verification/selection/installation） */
    customer_id BIGINT, /* 客户ID */
    customer VARCHAR(100), /* 客户名称 */
    customer_level CHAR(1), /* 客户级别 */
    location VARCHAR(255), /* 地址 */
    contact_person VARCHAR(50), /* 联系人 */
    contact_phone VARCHAR(20), /* 联系电话 */
    sales_id BIGINT, /* 销售人员ID */
    priority VARCHAR(10) DEFAULT 'normal', /* 优先级（low/normal/high） */
    status VARCHAR(20) DEFAULT 'pending', /* 任务状态（draft/pending/in-progress/completed/cancelled） */
    is_external_task TINYINT DEFAULT 0, /* 是否为系统外任务（0-否，1-是） */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    start_date DATETIME2, /* 开始日期 */
    deadline DATETIME2, /* 截止日期 */
    completed_date DATETIME2, /* 完成时间 */
    device_name VARCHAR(100), /* 设备名称 */
    device_model VARCHAR(50), /* 设备型号 */
    device_brand VARCHAR(50), /* 设备品牌 */
    device_sn VARCHAR(50), /* 设备序列号 */
    description NVARCHAR(MAX), /* 描述 */
    fault_description NVARCHAR(MAX), /* 故障描述 */
    quantity INT DEFAULT 1, /* 数量 */
    attachments NVARCHAR(MAX), /* 附件信息 */
    verification_type VARCHAR(10), /* 验证类别（IQ/OQ/PQ） */
    purpose VARCHAR(255), /* 用途（选型任务） */
    requirement_description NVARCHAR(MAX), /* 需求描述（选型任务） */
    appointment_time DATETIME2, /* 预约时间（培训任务） */
    current_step INT DEFAULT 0, /* 当前步骤 */
    last_valid_step INT, /* 上一个有效步骤索引 */
    budget DECIMAL(10, 2), /* 预算金额 */ 
    price DECIMAL(10, 2), /* 实际价格 */
    need_site_visit TINYINT DEFAULT NULL, /* 是否需要上门（0-不需要，1-需要） */
    CONSTRAINT uk_task_id UNIQUE (task_id)
);

CREATE INDEX idx_task_type ON task(task_type);
CREATE INDEX idx_customer_id ON task(customer_id);
CREATE INDEX idx_status ON task(status);
CREATE INDEX idx_create_time ON task(create_time);
END
GO

-- 客户表 - 检查是否已存在，不重复创建
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[customer]') AND type in (N'U'))
BEGIN
    PRINT '客户表不存在，请先执行combined_tables_sqlserver.sql脚本创建客户表'
    -- 不再创建表，而是提示用户先执行另一个脚本
END
GO

-- 任务协作者表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[task_collaborator]') AND type in (N'U'))
BEGIN
CREATE TABLE task_collaborator (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL, /* 任务ID */
    user_id BIGINT NOT NULL, /* 协作者ID */
    user_name VARCHAR(50) NOT NULL, /* 协作者姓名 */
    role VARCHAR(20) DEFAULT 'collaborator', /* 角色（collaborator-协作者, observer-观察者） */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    CONSTRAINT uk_task_user UNIQUE (task_id, user_id),
    CONSTRAINT FK_task_collaborator_user FOREIGN KEY (user_id) REFERENCES [user](id)
);

CREATE INDEX idx_task_id_collaborator ON task_collaborator(task_id);
CREATE INDEX idx_user_id_collaborator ON task_collaborator(user_id);
END
GO

-- 任务工时记录表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[task_work_hour]') AND type in (N'U'))
BEGIN
CREATE TABLE task_work_hour (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL, /* 任务ID */
    user_id BIGINT NOT NULL, /* 用户ID */
    user_name VARCHAR(50) NOT NULL, /* 用户姓名 */
    work_date DATE NOT NULL, /* 工作日期 */
    hours DECIMAL(5, 2) NOT NULL, /* 工时（小时） */
    work_content NVARCHAR(MAX), /* 工作内容 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT FK_task_work_hour_user FOREIGN KEY (user_id) REFERENCES [user](id)
);

CREATE INDEX idx_task_id_work_hour ON task_work_hour(task_id);
CREATE INDEX idx_user_id_work_hour ON task_work_hour(user_id);
CREATE INDEX idx_work_date ON task_work_hour(work_date);
END
GO

-- 任务状态历史表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[task_status_history]') AND type in (N'U'))
BEGIN
CREATE TABLE task_status_history (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL, /* 任务ID */
    status VARCHAR(20) NOT NULL, /* 状态 */
    comment NVARCHAR(MAX), /* 状态变更说明 */
    updated_by BIGINT NOT NULL, /* 更新人ID */
    updated_by_name VARCHAR(50) NOT NULL, /* 更新人姓名 */
    timestamp DATETIME2 DEFAULT GETDATE() /* 更新时间 */
);

CREATE INDEX idx_task_id_history ON task_status_history(task_id);
CREATE INDEX idx_timestamp ON task_status_history(timestamp);
END
GO

-- 任务图片表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[task_image]') AND type in (N'U'))
BEGIN
CREATE TABLE task_image (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL, /* 任务ID */
    image_url VARCHAR(255) NOT NULL, /* 图片URL */
    create_time DATETIME2 DEFAULT GETDATE() /* 创建时间 */
);

CREATE INDEX idx_task_id_image ON task_image(task_id);
END
GO

-- 任务工程师关联表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[task_engineer]') AND type in (N'U'))
BEGIN
CREATE TABLE task_engineer (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL, /* 任务ID */
    engineer_id BIGINT NOT NULL, /* 工程师ID */
    is_owner TINYINT DEFAULT 0, /* 是否为任务负责人（0-否，1-是） */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    CONSTRAINT uk_task_engineer UNIQUE (task_id, engineer_id)
);

CREATE INDEX idx_task_id_engineer ON task_engineer(task_id);
CREATE INDEX idx_engineer_id ON task_engineer(engineer_id);
END
GO

-- 任务流程表，用于确定任务当前的步骤进行到哪一步了
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[task_flow]') AND type in (N'U'))
BEGIN
CREATE TABLE task_flow (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL, /* 任务ID */
    task_type VARCHAR(20) NOT NULL, /* 任务类型 */
    current_step INT DEFAULT 0, /* 当前步骤索引 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_task_id_flow UNIQUE (task_id)
);

CREATE INDEX idx_task_type_flow ON task_flow(task_type);
END
GO

-- 步骤记录描述表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[step_record]') AND type in (N'U'))
BEGIN
CREATE TABLE step_record (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL, /* 任务ID */
    step_id BIGINT NOT NULL, /* 步骤ID */
    step_index INT NOT NULL, /* 步骤索引 */
    description NVARCHAR(MAX) NOT NULL, /* 记录描述 */
    creator_id BIGINT NOT NULL, /* 创建人ID */
    creator_name VARCHAR(50) NOT NULL, /* 创建人姓名 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE() /* 更新时间 */
);

CREATE INDEX idx_task_id_record ON step_record(task_id);
CREATE INDEX idx_step_id ON step_record(step_id);
END
GO

-- 记录图片表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[record_image]') AND type in (N'U'))
BEGIN
CREATE TABLE record_image (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    record_id BIGINT NOT NULL, /* 记录ID */
    image_url VARCHAR(255) NOT NULL, /* 图片URL */
    create_time DATETIME2 DEFAULT GETDATE() /* 创建时间 */
);

CREATE INDEX idx_record_id_image ON record_image(record_id);
END
GO

-- 记录附件表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[record_file]') AND type in (N'U'))
BEGIN
CREATE TABLE record_file (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    record_id BIGINT NOT NULL, /* 记录ID */
    file_name VARCHAR(100) NOT NULL, /* 文件名 */
    file_url VARCHAR(255) NOT NULL, /* 文件URL */
    file_type VARCHAR(20), /* 文件类型 */
    file_size BIGINT, /* 文件大小(字节) */
    create_time DATETIME2 DEFAULT GETDATE() /* 创建时间 */
);

CREATE INDEX idx_record_id_file ON record_file(record_id);
END
GO

-- 服务评价表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[service_evaluation]') AND type in (N'U'))
BEGIN
CREATE TABLE service_evaluation (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL, /* 任务ID */
    customer_id BIGINT, /* 客户ID */
    service_attitude INT, /* 服务态度评分（1-5） */
    service_quality INT, /* 服务质量评分（1-5） */
    overall_rating INT, /* 总体评价评分（1-5） */
    comment NVARCHAR(MAX), /* 评价内容 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    CONSTRAINT uk_task_id_evaluation UNIQUE (task_id)
);

CREATE INDEX idx_customer_id_evaluation ON service_evaluation(customer_id);
END
GO

-- 任务协作邀请表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[collaboration_invite]') AND type in (N'U'))
BEGIN
CREATE TABLE collaboration_invite (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL, /* 任务ID */
    inviter_id BIGINT NOT NULL, /* 邀请人ID */
    engineer_id BIGINT NOT NULL, /* 被邀请工程师ID */
    status VARCHAR(20) DEFAULT 'pending', /* 状态（pending/accepted/rejected） */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_task_engineer_invite UNIQUE (task_id, engineer_id)
);

CREATE INDEX idx_task_id_invite ON collaboration_invite(task_id);
CREATE INDEX idx_engineer_id_invite ON collaboration_invite(engineer_id);
END
GO

-- 任务转出记录表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[task_transfer]') AND type in (N'U'))
BEGIN
CREATE TABLE task_transfer (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL, /* 任务ID */
    from_engineer_id BIGINT NOT NULL, /* 转出工程师ID */
    to_engineer_id BIGINT NOT NULL, /* 接收工程师ID */
    transfer_note NVARCHAR(MAX), /* 转出说明 */
    status VARCHAR(20) DEFAULT 'pending', /* 状态（pending/accepted/rejected） */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE() /* 更新时间 */
);

CREATE INDEX idx_task_id_transfer ON task_transfer(task_id);
CREATE INDEX idx_from_engineer ON task_transfer(from_engineer_id);
CREATE INDEX idx_to_engineer ON task_transfer(to_engineer_id);
END
GO

-- 插入示例任务数据
IF NOT EXISTS (SELECT * FROM task WHERE task_id = 'RP-2024-001')
BEGIN
    SET IDENTITY_INSERT task ON;
    INSERT INTO task (id, task_id, title, task_type, customer_id, customer, customer_level, location, contact_person, contact_phone, sales_id, priority, status, 
                 start_date, deadline, device_name, device_model, device_brand, description, fault_description, current_step) VALUES
    (1, 'RP-2024-001', '气相色谱仪维修', 'repair', 1, '上海医疗器械有限公司', 'A', '上海市浦东新区张江高科技园区', '张经理', '13800138001', 
     1, 'high', 'in-progress', '2024-05-20 09:00:00', '2024-05-25 18:00:00', '气相色谱仪', 'GC-2000', 'Agilent', 
     '客户反映设备无法正常启动', '设备开机后报错代码E-231，无法进入系统', 2),
     
    (2, 'MT-2024-001', '液相色谱仪保养', 'maintenance', 2, '北京科研仪器研究所', 'B', '北京市海淀区中关村', '李研究员', '13900139002', 
     1, 'normal', 'pending', '2024-05-22 13:00:00', '2024-05-23 18:00:00', '液相色谱仪', 'LC-3000', 'Waters', 
     '定期保养', '年度常规保养', 0),
     
    (3, 'TR-2024-001', '质谱仪操作培训', 'training', 3, '广州医学检验中心', 'A', '广州市黄埔区科学城', '王主任', '13800138003', 
     2, 'normal', 'pending', '2024-05-25 09:00:00', '2024-05-25 18:00:00', '质谱仪', 'MS-6000', 'Thermo Fisher', 
     '新员工培训', NULL, 0),
     
    (4, 'SL-2024-001', '高效液相色谱仪选型', 'selection', 4, '深圳电子科技有限公司', 'C', '深圳市南山区高新区', '赵工程师', '13900139004', 
     2, 'low', 'pending', '2024-05-26 09:00:00', '2024-06-10 18:00:00', NULL, NULL, NULL, 
     '帮助客户选择合适的高效液相色谱仪', NULL, 0);
    SET IDENTITY_INSERT task OFF;
END
GO

-- 插入示例任务工程师关联数据
IF NOT EXISTS (SELECT * FROM task_engineer WHERE task_id = 'RP-2024-001' AND engineer_id = 2)
BEGIN
    INSERT INTO task_engineer (task_id, engineer_id, is_owner) VALUES
    ('RP-2024-001', 2, 1),
    ('MT-2024-001', 2, 1),
    ('TR-2024-001', 2, 1),
    ('SL-2024-001', 2, 0),
    ('SL-2024-001', 3, 1);
END
GO

-- 插入示例任务流程数据
IF NOT EXISTS (SELECT * FROM task_flow WHERE task_id = 'RP-2024-001')
BEGIN
    INSERT INTO task_flow (task_id, task_type, current_step) VALUES
    ('RP-2024-001', 'repair', 2),
    ('MT-2024-001', 'maintenance', 0),
    ('TR-2024-001', 'training', 0),
    ('SL-2024-001', 'selection', 0);
END
GO

-- 插入示例任务协作者
IF NOT EXISTS (SELECT * FROM task_collaborator WHERE task_id = 'RP-2024-001' AND user_id = 3)
BEGIN
    INSERT INTO task_collaborator (task_id, user_id, user_name, role) VALUES
    ('RP-2024-001', 3, '李工程师', 'collaborator'),
    ('RP-2024-001', 4, '王工程师', 'observer'),
    ('MT-2024-001', 2, '张工程师', 'collaborator'),
    ('MT-2024-001', 3, '李工程师', 'collaborator');
END
GO

-- 添加任务工时记录
IF NOT EXISTS (SELECT * FROM task_work_hour WHERE task_id = 'RP-2024-001' AND user_id = 2)
BEGIN
    INSERT INTO task_work_hour (task_id, user_id, user_name, work_date, hours, work_content) VALUES
    ('RP-2024-001', 2, '张工程师', CONVERT(DATE, DATEADD(DAY, -1, GETDATE())), 2.5, '准备工具和备件，联系客户确认上门时间'),
    ('MT-2024-001', 3, '李工程师', CONVERT(DATE, GETDATE()), 1.5, '准备保养所需工具和耗材');
END
GO

-- 插入示例任务状态历史数据
IF NOT EXISTS (SELECT * FROM task_status_history WHERE task_id = 'RP-2024-001' AND status = 'pending')
BEGIN
    INSERT INTO task_status_history (task_id, status, comment, updated_by, updated_by_name, timestamp) VALUES
    ('RP-2024-001', 'pending', '任务创建', 1, '管理员', '2024-05-20 08:30:00'),
    ('RP-2024-001', 'in-progress', '开始处理任务', 2, '张三', '2024-05-20 09:15:00'),
    ('RP-2024-001', 'waiting-parts', '等待零部件到货', 2, '张三', '2024-05-20 14:00:00'),
    ('RP-2024-001', 'in-progress', '零部件已到货，继续维修', 2, '张三', '2024-05-21 10:30:00');
END
GO

-- 为步骤记录表插入测例数据
IF NOT EXISTS (SELECT * FROM step_record WHERE task_id = 'RP-2024-001' AND step_index = 0)
BEGIN
    INSERT INTO step_record (task_id, step_id, step_index, description, creator_id, creator_name) VALUES
    ('RP-2024-001', 1, 0, '已接收任务，将尽快处理', 2, '张三'),
    ('RP-2024-001', 2, 1, '与客户沟通后，确认需要上门维修', 2, '张三'),
    ('RP-2024-001', 3, 2, '初步检查发现可能是主板故障', 2, '张三');
END
GO

-- 插入示例记录图片
IF NOT EXISTS (SELECT * FROM record_image WHERE record_id = 3)
BEGIN
    INSERT INTO record_image (record_id, image_url) VALUES
    (3, 'https://example.com/images/RP-2024-001/board1.jpg'),
    (3, 'https://example.com/images/RP-2024-001/board2.jpg');
END
GO

-- 插入示例记录附件
IF NOT EXISTS (SELECT * FROM record_file WHERE record_id = 3)
BEGIN
    INSERT INTO record_file (record_id, file_name, file_url, file_type, file_size) VALUES
    (3, '故障分析报告.pdf', 'https://example.com/files/RP-2024-001/fault_analysis.pdf', 'application/pdf', 1024000);
END
GO 