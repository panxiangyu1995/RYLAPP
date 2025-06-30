-- 任务模块数据库设计
USE ryl_engineer;

-- 客户表
CREATE TABLE IF NOT EXISTS customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '客户名称',
    level CHAR(1) NOT NULL DEFAULT 'C' COMMENT '客户级别（A/B/C）',
    contact VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(255) COMMENT '地址',
    email VARCHAR(100) COMMENT '邮箱',
    department VARCHAR(50) COMMENT '部门',
    position VARCHAR(50) COMMENT '职位',
    sales_id BIGINT COMMENT '负责销售ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_sales_id (sales_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- 任务表
CREATE TABLE IF NOT EXISTS task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL COMMENT '任务编号（格式：类型缩写-年份-序号，如RP-2024-001）',
    title VARCHAR(100) NOT NULL COMMENT '任务标题',
    task_type VARCHAR(20) NOT NULL COMMENT '任务类型（repair/maintenance/recycle/leasing/training/verification/selection/installation）',
    customer_id BIGINT COMMENT '客户ID',
    customer VARCHAR(100) COMMENT '客户名称',
    customer_level CHAR(1) COMMENT '客户级别',
    location VARCHAR(255) COMMENT '地址',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    sales_id BIGINT COMMENT '销售人员ID',
    priority VARCHAR(10) DEFAULT 'normal' COMMENT '优先级（low/normal/high）',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '任务状态（draft/pending/in-progress/completed/cancelled）',
    is_external_task TINYINT DEFAULT 0 COMMENT '是否为系统外任务（0-否，1-是）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    start_date DATETIME COMMENT '开始日期',
    deadline DATETIME COMMENT '截止日期',
    completed_date DATETIME COMMENT '完成时间',
    device_name VARCHAR(100) COMMENT '设备名称',
    device_model VARCHAR(50) COMMENT '设备型号',
    device_brand VARCHAR(50) COMMENT '设备品牌',
    device_sn VARCHAR(50) COMMENT '设备序列号',
    description TEXT COMMENT '描述',
    fault_description TEXT COMMENT '故障描述',
    quantity INT DEFAULT 1 COMMENT '数量',
    attachments TEXT COMMENT '附件信息',
    verification_type VARCHAR(10) COMMENT '验证类别（IQ/OQ/PQ）',
    purpose VARCHAR(255) COMMENT '用途（选型任务）',
    requirement_description TEXT COMMENT '需求描述（选型任务）',
    appointment_time DATETIME COMMENT '预约时间（培训任务）',
    current_step INT DEFAULT 0 COMMENT '当前步骤',
    last_valid_step INT COMMENT '上一个有效步骤索引',
    budget DECIMAL(10, 2) COMMENT '预算金额', 
    price DECIMAL(10, 2) COMMENT '实际价格',
    need_site_visit TINYINT DEFAULT NULL COMMENT '是否需要上门（0-不需要，1-需要）',
    UNIQUE KEY uk_task_id (task_id),
    INDEX idx_task_type (task_type),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- 任务状态历史表
CREATE TABLE IF NOT EXISTS task_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL COMMENT '任务ID',
    status VARCHAR(20) NOT NULL COMMENT '状态',
    comment TEXT COMMENT '状态变更说明',
    updated_by BIGINT NOT NULL COMMENT '更新人ID',
    updated_by_name VARCHAR(50) NOT NULL COMMENT '更新人姓名',
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_task_id (task_id),
    INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务状态历史表';

-- 任务图片表
CREATE TABLE IF NOT EXISTS task_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL COMMENT '任务ID',
    image_url VARCHAR(255) NOT NULL COMMENT '图片URL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务图片表';

-- 任务工程师关联表
CREATE TABLE IF NOT EXISTS task_engineer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL COMMENT '任务ID',
    engineer_id BIGINT NOT NULL COMMENT '工程师ID',
    is_owner TINYINT DEFAULT 0 COMMENT '是否为任务负责人（0-否，1-是）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_task_engineer (task_id, engineer_id),
    INDEX idx_task_id (task_id),
    INDEX idx_engineer_id (engineer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务工程师关联表';

-- 任务流程表
CREATE TABLE IF NOT EXISTS task_flow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL COMMENT '任务ID',
    task_type VARCHAR(20) NOT NULL COMMENT '任务类型',
    current_step INT DEFAULT 0 COMMENT '当前步骤索引',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_task_id (task_id),
    INDEX idx_task_type (task_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务流程表';

-- 任务步骤表
CREATE TABLE IF NOT EXISTS task_step (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL COMMENT '任务ID',
    step_index INT NOT NULL COMMENT '步骤索引',
    title VARCHAR(100) NOT NULL COMMENT '步骤标题',
    description TEXT COMMENT '步骤描述',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态（pending/in-progress/completed/skipped）',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    estimated_time VARCHAR(50) COMMENT '预计时间',
    operator_id BIGINT COMMENT '操作人ID',
    operator VARCHAR(50) COMMENT '操作人',
    options TEXT COMMENT '步骤可选项（JSON格式）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_task_step (task_id, step_index),
    INDEX idx_task_id (task_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务步骤表';

-- 步骤记录表
CREATE TABLE IF NOT EXISTS step_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL COMMENT '任务ID',
    step_id BIGINT NOT NULL COMMENT '步骤ID',
    step_index INT NOT NULL COMMENT '步骤索引',
    description TEXT NOT NULL COMMENT '记录描述',
    creator_id BIGINT NOT NULL COMMENT '创建人ID',
    creator_name VARCHAR(50) NOT NULL COMMENT '创建人姓名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_task_id (task_id),
    INDEX idx_step_id (step_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='步骤记录表';

-- 记录图片表
CREATE TABLE IF NOT EXISTS record_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_id BIGINT NOT NULL COMMENT '记录ID',
    image_url VARCHAR(255) NOT NULL COMMENT '图片URL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_record_id (record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记录图片表';

-- 记录附件表
CREATE TABLE IF NOT EXISTS record_file (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_id BIGINT NOT NULL COMMENT '记录ID',
    file_name VARCHAR(100) NOT NULL COMMENT '文件名',
    file_url VARCHAR(255) NOT NULL COMMENT '文件URL',
    file_type VARCHAR(20) COMMENT '文件类型',
    file_size BIGINT COMMENT '文件大小(字节)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_record_id (record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记录附件表';

-- 服务评价表
CREATE TABLE IF NOT EXISTS service_evaluation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL COMMENT '任务ID',
    customer_id BIGINT COMMENT '客户ID',
    service_attitude INT COMMENT '服务态度评分（1-5）',
    service_quality INT COMMENT '服务质量评分（1-5）',
    overall_rating INT COMMENT '总体评价评分（1-5）',
    comment TEXT COMMENT '评价内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_task_id (task_id),
    INDEX idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务评价表';

-- 任务协作邀请表
CREATE TABLE IF NOT EXISTS collaboration_invite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL COMMENT '任务ID',
    inviter_id BIGINT NOT NULL COMMENT '邀请人ID',
    engineer_id BIGINT NOT NULL COMMENT '被邀请工程师ID',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态（pending/accepted/rejected）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_task_id (task_id),
    INDEX idx_engineer_id (engineer_id),
    UNIQUE KEY uk_task_engineer (task_id, engineer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务协作邀请表';

-- 任务转出记录表
CREATE TABLE IF NOT EXISTS task_transfer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL COMMENT '任务ID',
    from_engineer_id BIGINT NOT NULL COMMENT '转出工程师ID',
    to_engineer_id BIGINT NOT NULL COMMENT '接收工程师ID',
    transfer_note TEXT COMMENT '转出说明',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态（pending/accepted/rejected）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_task_id (task_id),
    INDEX idx_from_engineer (from_engineer_id),
    INDEX idx_to_engineer (to_engineer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务转出记录表';

-- 任务备注表
CREATE TABLE IF NOT EXISTS task_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL COMMENT '任务ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(50) NOT NULL COMMENT '用户姓名',
    content TEXT NOT NULL COMMENT '备注内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_task_id (task_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务备注表';

-- 插入示例客户数据
INSERT INTO customer (name, level, contact, phone, address, email, department, position) VALUES
('上海医疗器械有限公司', 'A', '张经理', '13800138001', '上海市浦东新区张江高科技园区', 'zhang@example.com', '设备部', '经理'),
('北京科研仪器研究所', 'B', '李研究员', '13900139002', '北京市海淀区中关村', 'li@example.com', '研究部', '主任'),
('广州医学检验中心', 'A', '王主任', '13800138003', '广州市黄埔区科学城', 'wang@example.com', '检验科', '主任'),
('深圳电子科技有限公司', 'C', '赵工程师', '13900139004', '深圳市南山区高新区', 'zhao@example.com', '研发部', '工程师');

-- 插入示例任务数据
INSERT INTO task (task_id, title, task_type, customer_id, customer, customer_level, location, contact_person, contact_phone, sales_id, priority, status, 
                 start_date, deadline, device_name, device_model, device_brand, description, fault_description, current_step) VALUES
('RP-2024-001', '气相色谱仪维修', 'repair', 1, '上海医疗器械有限公司', 'A', '上海市浦东新区张江高科技园区', '张经理', '13800138001', 
 1, 'high', 'in-progress', '2024-05-20 09:00:00', '2024-05-25 18:00:00', '气相色谱仪', 'GC-2000', 'Agilent', 
 '客户反映设备无法正常启动', '设备开机后报错代码E-231，无法进入系统', 2),
 
('MT-2024-001', '液相色谱仪保养', 'maintenance', 2, '北京科研仪器研究所', 'B', '北京市海淀区中关村', '李研究员', '13900139002', 
 1, 'normal', 'pending', '2024-05-22 13:00:00', '2024-05-23 18:00:00', '液相色谱仪', 'LC-3000', 'Waters', 
 '定期保养', '年度常规保养', 0),
 
('TR-2024-001', '质谱仪操作培训', 'training', 3, '广州医学检验中心', 'A', '广州市黄埔区科学城', '王主任', '13800138003', 
 2, 'normal', 'pending', '2024-05-25 09:00:00', '2024-05-25 18:00:00', '质谱仪', 'MS-6000', 'Thermo Fisher', 
 '新员工培训', NULL, 0),
 
('SL-2024-001', '高效液相色谱仪选型', 'selection', 4, '深圳电子科技有限公司', 'C', '深圳市南山区高新区', '赵工程师', '13900139004', 
 2, 'low', 'pending', '2024-05-26 09:00:00', '2024-06-10 18:00:00', NULL, NULL, NULL, 
 '帮助客户选择合适的高效液相色谱仪', NULL, 0);

-- 插入示例任务工程师关联数据
INSERT INTO task_engineer (task_id, engineer_id, is_owner) VALUES
('RP-2024-001', 2, 1),
('MT-2024-001', 2, 1),
('TR-2024-001', 2, 1),
('SL-2024-001', 2, 0),
('SL-2024-001', 3, 1);

-- 插入示例任务流程数据
INSERT INTO task_flow (task_id, task_type, current_step) VALUES
('RP-2024-001', 'repair', 2),
('MT-2024-001', 'maintenance', 0),
('TR-2024-001', 'training', 0),
('SL-2024-001', 'selection', 0);

-- 为RP-2024-001任务插入步骤数据
INSERT INTO task_step (task_id, step_index, title, description, status, start_time, end_time, operator_id, operator) VALUES
('RP-2024-001', 0, '已接单', '请保持电话畅通，我们稍后会联系您', 'completed', '2024-05-20 09:10:00', '2024-05-20 09:30:00', 2, '张三'),
('RP-2024-001', 1, '判断是否需要上门', '需要确认是否需要上门服务', 'completed', '2024-05-20 09:30:00', '2024-05-20 10:00:00', 2, '张三'),
('RP-2024-001', 2, '检修完成', '维修评估', 'in-progress', '2024-05-20 10:00:00', NULL, 2, '张三'),
('RP-2024-001', 3, '维修方案和报价', '提供维修方案和报价', 'pending', NULL, NULL, NULL, NULL),
('RP-2024-001', 4, '维修中', '工程师维修中', 'pending', NULL, NULL, NULL, NULL),
('RP-2024-001', 5, '验证报告', '已完成维修验证', 'pending', NULL, NULL, NULL, NULL),
('RP-2024-001', 6, '服务评价', '请对我们的服务进行评价', 'pending', NULL, NULL, NULL, NULL),
('RP-2024-001', 7, '订单已完成', '订单已完成', 'pending', NULL, NULL, NULL, NULL);

-- 为MT-2024-001任务插入步骤数据
INSERT INTO task_step (task_id, step_index, title, description, status) VALUES
('MT-2024-001', 0, '已接单', '请保持电话畅通，我们稍后会联系您', 'pending'),
('MT-2024-001', 1, '判断是否需要上门', '需要确认是否需要上门服务', 'pending'),
('MT-2024-001', 2, '检修完成', '保养评估', 'pending'),
('MT-2024-001', 3, '保养方案和报价', '提供保养方案和报价', 'pending'),
('MT-2024-001', 4, '保养中', '工程师保养中', 'pending'),
('MT-2024-001', 5, '验证报告', '已完成保养验证', 'pending'),
('MT-2024-001', 6, '服务评价', '请对我们的服务进行评价', 'pending'),
('MT-2024-001', 7, '订单已完成', '订单已完成', 'pending');

-- 为TR-2024-001任务插入步骤数据
INSERT INTO task_step (task_id, step_index, title, description, status) VALUES
('TR-2024-001', 0, '已接单', '请保持电话畅通，我们稍后会联系您', 'pending'),
('TR-2024-001', 1, '判断是否需要上门', '需要确认是否需要上门服务', 'pending'),
('TR-2024-001', 2, '培训准备完成', '培训评估', 'pending'),
('TR-2024-001', 3, '培训方案和报价', '提供培训方案和报价', 'pending'),
('TR-2024-001', 4, '培训中', '工程师培训中', 'pending'),
('TR-2024-001', 5, '验证报告', '已完成培训验证', 'pending'),
('TR-2024-001', 6, '服务评价', '请对我们的服务进行评价', 'pending'),
('TR-2024-001', 7, '订单已完成', '订单已完成', 'pending');

-- 为SL-2024-001任务插入步骤数据
INSERT INTO task_step (task_id, step_index, title, description, status) VALUES
('SL-2024-001', 0, '已接单', '请保持电话畅通，我们稍后会联系您', 'pending'),
('SL-2024-001', 1, '判断是否需要上门', '需要确认是否需要上门服务', 'pending'),
('SL-2024-001', 2, '选型分析完成', '选型评估', 'pending'),
('SL-2024-001', 3, '选型方案和报价', '提供选型方案和报价', 'pending'),
('SL-2024-001', 4, '选型进行中', '工程师选型中', 'pending'),
('SL-2024-001', 5, '验证报告', '已完成选型验证', 'pending'),
('SL-2024-001', 6, '服务评价', '请对我们的服务进行评价', 'pending'),
('SL-2024-001', 7, '订单已完成', '订单已完成', 'pending');

-- 插入示例步骤记录
INSERT INTO step_record (task_id, step_id, step_index, description, creator_id, creator_name) VALUES
('RP-2024-001', 1, 0, '已接收任务，将尽快处理', 2, '张三'),
('RP-2024-001', 2, 1, '与客户沟通后，确认需要上门维修', 2, '张三'),
('RP-2024-001', 3, 2, '初步检查发现可能是主板故障', 2, '张三');

-- 插入示例记录图片
INSERT INTO record_image (record_id, image_url) VALUES
(3, 'https://example.com/images/RP-2024-001/board1.jpg'),
(3, 'https://example.com/images/RP-2024-001/board2.jpg');

-- 插入示例记录附件
INSERT INTO record_file (record_id, file_name, file_url, file_type, file_size) VALUES
(3, '故障分析报告.pdf', 'https://example.com/files/RP-2024-001/fault_analysis.pdf', 'application/pdf', 1024000);

-- 插入示例任务状态历史数据
INSERT INTO task_status_history (task_id, status, comment, updated_by, updated_by_name, timestamp) VALUES
('RP-2024-001', 'pending', '任务创建', 1, '管理员', '2024-05-20 08:30:00'),
('RP-2024-001', 'in-progress', '开始处理任务', 2, '张三', '2024-05-20 09:15:00'),
('RP-2024-001', 'waiting-parts', '等待零部件到货', 2, '张三', '2024-05-20 14:00:00'),
('RP-2024-001', 'in-progress', '零部件已到货，继续维修', 2, '张三', '2024-05-21 10:30:00'); 