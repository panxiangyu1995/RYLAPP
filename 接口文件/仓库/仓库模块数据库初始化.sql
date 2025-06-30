-- 仓库模块数据库设计
USE ryl_engineer;

-- 仓库表
CREATE TABLE IF NOT EXISTS warehouse (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL COMMENT '仓库编码',
    name VARCHAR(50) NOT NULL COMMENT '仓库名称',
    location VARCHAR(100) NOT NULL COMMENT '仓库地点',
    description VARCHAR(255) COMMENT '仓库描述',
    status TINYINT DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表';

-- 物品分类表
CREATE TABLE IF NOT EXISTS item_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL COMMENT '分类编码',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT COMMENT '父分类ID',
    level INT DEFAULT 1 COMMENT '分类级别',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_code (code),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物品分类表';

-- 物品表
CREATE TABLE IF NOT EXISTS warehouse_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_code VARCHAR(50) NOT NULL COMMENT '物品编号',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    name VARCHAR(100) NOT NULL COMMENT '物品名称',
    model VARCHAR(100) COMMENT '规格型号',
    manufacturer VARCHAR(100) COMMENT '厂家/品牌',
    quantity INT DEFAULT 0 COMMENT '数量',
    cost DECIMAL(10, 2) COMMENT '成本',
    arrival_date DATE COMMENT '到货日期',
    area VARCHAR(50) COMMENT '区域',
    description TEXT COMMENT '描述/备注',
    status TINYINT DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_item_code (item_code),
    INDEX idx_category_id (category_id),
    INDEX idx_warehouse_id (warehouse_id),
    FOREIGN KEY (category_id) REFERENCES item_category(id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouse(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物品表';

-- 仪器详情表
CREATE TABLE IF NOT EXISTS instrument_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL COMMENT '物品ID',
    is_new TINYINT DEFAULT 1 COMMENT '是否全新（1-是，0-否）',
    production_date DATE COMMENT '生产日期',
    recovery_unit VARCHAR(100) COMMENT '回收/购买单位（二手仪器）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_item_id (item_id),
    FOREIGN KEY (item_id) REFERENCES warehouse_item(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仪器详情表';

-- 配件详情表
CREATE TABLE IF NOT EXISTS part_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL COMMENT '物品ID',
    part_number VARCHAR(50) COMMENT '货号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_item_id (item_id),
    FOREIGN KEY (item_id) REFERENCES warehouse_item(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配件详情表';

-- 耗材详情表
CREATE TABLE IF NOT EXISTS consumable_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL COMMENT '物品ID',
    part_number VARCHAR(50) COMMENT '货号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_item_id (item_id),
    FOREIGN KEY (item_id) REFERENCES warehouse_item(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='耗材详情表';

-- 固定资产详情表
CREATE TABLE IF NOT EXISTS asset_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL COMMENT '物品ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_item_id (item_id),
    FOREIGN KEY (item_id) REFERENCES warehouse_item(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='固定资产详情表';

-- 出入库记录表
CREATE TABLE IF NOT EXISTS stock_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_code VARCHAR(50) NOT NULL COMMENT '记录编号',
    item_id BIGINT NOT NULL COMMENT '物品ID',
    record_type TINYINT NOT NULL COMMENT '记录类型（1-入库，2-出库）',
    quantity INT NOT NULL COMMENT '数量',
    operator_id BIGINT NOT NULL COMMENT '操作人ID',
    operator_name VARCHAR(50) NOT NULL COMMENT '操作人姓名',
    operation_time DATETIME NOT NULL COMMENT '操作时间',
    task_id VARCHAR(20) COMMENT '关联任务ID',
    description VARCHAR(255) COMMENT '描述/备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_record_code (record_code),
    INDEX idx_item_id (item_id),
    INDEX idx_operator_id (operator_id),
    INDEX idx_task_id (task_id),
    FOREIGN KEY (item_id) REFERENCES warehouse_item(id),
    FOREIGN KEY (operator_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出入库记录表';

-- 物品申请表
CREATE TABLE IF NOT EXISTS item_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_code VARCHAR(50) NOT NULL COMMENT '申请编号',
    requester_id BIGINT NOT NULL COMMENT '申请人ID',
    requester_name VARCHAR(50) NOT NULL COMMENT '申请人姓名',
    request_type TINYINT NOT NULL COMMENT '申请类型（1-使用申请，2-采购申请）',
    status TINYINT DEFAULT 0 COMMENT '状态（0-待审批，1-已同意，2-已拒绝）',
    item_id BIGINT COMMENT '物品ID（使用申请）',
    item_name VARCHAR(100) COMMENT '物品名称（采购申请可能是新物品）',
    item_model VARCHAR(100) COMMENT '规格型号',
    quantity INT NOT NULL COMMENT '数量',
    task_id VARCHAR(20) COMMENT '关联任务ID',
    purpose VARCHAR(255) COMMENT '用途',
    urgency TINYINT DEFAULT 1 COMMENT '紧急程度（1-普通，2-紧急，3-特急）',
    approver_id BIGINT COMMENT '审批人ID',
    approver_name VARCHAR(50) COMMENT '审批人姓名',
    approval_time DATETIME COMMENT '审批时间',
    approval_comment VARCHAR(255) COMMENT '审批意见',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_request_code (request_code),
    INDEX idx_requester_id (requester_id),
    INDEX idx_item_id (item_id),
    INDEX idx_task_id (task_id),
    INDEX idx_status (status),
    FOREIGN KEY (requester_id) REFERENCES user(id),
    FOREIGN KEY (approver_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物品申请表';

-- 盘库记录表
CREATE TABLE IF NOT EXISTS inventory_check (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    check_code VARCHAR(50) NOT NULL COMMENT '盘库编号',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    checker_id BIGINT NOT NULL COMMENT '盘库人ID',
    checker_name VARCHAR(50) NOT NULL COMMENT '盘库人姓名',
    check_time DATETIME NOT NULL COMMENT '盘库时间',
    status TINYINT DEFAULT 0 COMMENT '状态（0-进行中，1-已完成）',
    description VARCHAR(255) COMMENT '描述/备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_check_code (check_code),
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_checker_id (checker_id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouse(id),
    FOREIGN KEY (checker_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘库记录表';

-- 盘库明细表
CREATE TABLE IF NOT EXISTS inventory_check_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    check_id BIGINT NOT NULL COMMENT '盘库ID',
    item_id BIGINT NOT NULL COMMENT '物品ID',
    system_quantity INT NOT NULL COMMENT '系统数量',
    actual_quantity INT NOT NULL COMMENT '实际数量',
    difference INT DEFAULT 0 COMMENT '差异数量',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_check_id (check_id),
    INDEX idx_item_id (item_id),
    FOREIGN KEY (check_id) REFERENCES inventory_check(id),
    FOREIGN KEY (item_id) REFERENCES warehouse_item(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘库明细表';

-- 插入初始仓库数据
INSERT INTO warehouse (code, name, location, description) VALUES
('TZ', '泰州仓', '泰州', '泰州公司仓库'),
('SZ', '苏州仓', '苏州', '苏州公司仓库'),
('WH', '武汉仓', '武汉', '武汉公司仓库'),
('SC', '四川仓', '四川', '四川公司仓库');

-- 插入初始物品分类数据
INSERT INTO item_category (code, name, level, sort_order) VALUES
('INSTRUMENT', '仪器库', 1, 1),
('PART', '配件库', 1, 2),
('CONSUMABLE', '耗材库', 1, 3),
('ASSET', '固定资产库', 1, 4);

-- 插入子分类示例
INSERT INTO item_category (code, name, parent_id, level, sort_order) VALUES
('INSTRUMENT_NEW', '全新仪器', 1, 2, 1),
('INSTRUMENT_USED', '二手仪器', 1, 2, 2),
('PART_ELECTRONIC', '电子配件', 2, 2, 1),
('PART_MECHANICAL', '机械配件', 2, 2, 2),
('CONSUMABLE_CHEMICAL', '化学耗材', 3, 2, 1),
('CONSUMABLE_LAB', '实验室耗材', 3, 2, 2),
('ASSET_OFFICE', '办公设备', 4, 2, 1),
('ASSET_LAB', '实验室设备', 4, 2, 2);

-- 插入示例物品数据 (仪器)
INSERT INTO warehouse_item (item_code, category_id, warehouse_id, name, model, manufacturer, quantity, cost, arrival_date, area, description)
VALUES
('INS-2024-001', 1, 1, '气相色谱仪', 'GC-2000', 'Agilent', 2, 150000.00, '2024-01-15', 'A区', '高精度气相色谱仪'),
('INS-2024-002', 1, 2, '液相色谱仪', 'LC-3000', 'Waters', 1, 180000.00, '2024-02-20', 'B区', '高效液相色谱仪'),
('INS-2024-003', 1, 3, '质谱仪', 'MS-6000', 'Thermo Fisher', 1, 250000.00, '2024-03-10', 'A区', '高分辨质谱仪');

-- 插入仪器详情
INSERT INTO instrument_details (item_id, is_new, production_date)
VALUES
(1, 1, '2023-12-01'),
(2, 1, '2024-01-15'),
(3, 0, '2022-06-20');

-- 插入示例物品数据 (配件)
INSERT INTO warehouse_item (item_code, category_id, warehouse_id, name, model, manufacturer, quantity, cost, arrival_date, area, description)
VALUES
('PART-2024-001', 2, 1, 'GC色谱柱', 'DB-5MS', 'Agilent', 10, 3500.00, '2024-01-20', 'C区', '30m x 0.25mm x 0.25μm'),
('PART-2024-002', 2, 2, 'LC色谱柱', 'C18', 'Waters', 5, 4200.00, '2024-02-25', 'D区', '150mm x 4.6mm x 5μm'),
('PART-2024-003', 2, 3, '质谱离子源', 'ESI-5000', 'Thermo Fisher', 2, 15000.00, '2024-03-15', 'B区', '电喷雾离子源');

-- 插入配件详情
INSERT INTO part_details (item_id, part_number)
VALUES
(4, 'AG-DB5-30'),
(5, 'WA-C18-150'),
(6, 'TF-ESI-5K');

-- 插入示例物品数据 (耗材)
INSERT INTO warehouse_item (item_code, category_id, warehouse_id, name, model, manufacturer, quantity, cost, arrival_date, area, description)
VALUES
('CON-2024-001', 3, 1, '样品瓶', '2mL', 'Agilent', 500, 5.00, '2024-01-25', 'E区', '透明玻璃样品瓶'),
('CON-2024-002', 3, 2, '进样针', '10μL', 'Hamilton', 100, 25.00, '2024-02-28', 'F区', '高精度进样针'),
('CON-2024-003', 3, 3, '色谱纸', 'A4', 'Whatman', 1000, 0.50, '2024-03-20', 'E区', '高质量记录纸');

-- 插入耗材详情
INSERT INTO consumable_details (item_id, part_number)
VALUES
(7, 'AG-SV-2ML'),
(8, 'HM-SYR-10'),
(9, 'WM-PAP-A4');

-- 插入示例物品数据 (固定资产)
INSERT INTO warehouse_item (item_code, category_id, warehouse_id, name, model, manufacturer, quantity, cost, arrival_date, area, description)
VALUES
('AST-2024-001', 4, 1, '实验台', 'LT-2000', 'LabTech', 5, 8000.00, '2024-01-30', 'G区', '标准实验台'),
('AST-2024-002', 4, 2, '通风柜', 'FC-1500', 'SafeLab', 2, 12000.00, '2024-03-01', 'H区', '化学品通风柜'),
('AST-2024-003', 4, 3, '纯水系统', 'WP-Ultra', 'Millipore', 1, 35000.00, '2024-03-25', 'G区', '超纯水系统');

-- 插入固定资产详情
INSERT INTO asset_details (item_id)
VALUES
(10),
(11),
(12);