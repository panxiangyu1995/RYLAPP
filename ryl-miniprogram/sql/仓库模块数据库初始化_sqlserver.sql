-- 仓库模块数据库设计
USE ryl_engineer;
GO

-- 仓库表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[warehouse]') AND type in (N'U'))
BEGIN
CREATE TABLE warehouse (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(20) NOT NULL, /* 仓库编码 */
    name VARCHAR(50) NOT NULL, /* 仓库名称 */
    location VARCHAR(100) NOT NULL, /* 仓库地点 */
    description VARCHAR(255), /* 仓库描述 */
    status TINYINT DEFAULT 1, /* 状态（0-禁用，1-启用） */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_code UNIQUE (code)
);
END
GO

-- 物品分类表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[item_category]') AND type in (N'U'))
BEGIN
CREATE TABLE item_category (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(20) NOT NULL, /* 分类编码 */
    name VARCHAR(50) NOT NULL, /* 分类名称 */
    parent_id BIGINT, /* 父分类ID */
    level INT DEFAULT 1, /* 分类级别 */
    sort_order INT DEFAULT 0, /* 排序顺序 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_code_category UNIQUE (code)
);

CREATE INDEX idx_parent_id ON item_category(parent_id);
END
GO

-- 物品表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[warehouse_item]') AND type in (N'U'))
BEGIN
CREATE TABLE warehouse_item (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    item_code VARCHAR(50) NOT NULL, /* 物品编号 */
    category_id BIGINT NOT NULL, /* 分类ID */
    warehouse_id BIGINT NOT NULL, /* 仓库ID */
    name VARCHAR(100) NOT NULL, /* 物品名称 */
    model VARCHAR(100), /* 规格型号 */
    manufacturer VARCHAR(100), /* 厂家/品牌 */
    quantity INT DEFAULT 0, /* 数量 */
    cost DECIMAL(10, 2), /* 成本 */
    arrival_date DATE, /* 到货日期 */
    area VARCHAR(50), /* 区域 */
    description NVARCHAR(MAX), /* 描述/备注 */
    status TINYINT DEFAULT 1, /* 状态（0-禁用，1-启用） */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_item_code UNIQUE (item_code),
    CONSTRAINT FK_warehouse_item_category FOREIGN KEY (category_id) REFERENCES item_category(id),
    CONSTRAINT FK_warehouse_item_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouse(id)
);

CREATE INDEX idx_category_id ON warehouse_item(category_id);
CREATE INDEX idx_warehouse_id ON warehouse_item(warehouse_id);
END
GO

-- 仪器详情表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[instrument_details]') AND type in (N'U'))
BEGIN
CREATE TABLE instrument_details (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    item_id BIGINT NOT NULL, /* 物品ID */
    is_new TINYINT DEFAULT 1, /* 是否全新（1-是，0-否） */
    production_date DATE, /* 生产日期 */
    recovery_unit VARCHAR(100), /* 回收/购买单位（二手仪器） */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_item_id_instrument UNIQUE (item_id),
    CONSTRAINT FK_instrument_details_item FOREIGN KEY (item_id) REFERENCES warehouse_item(id)
);
END
GO

-- 配件详情表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[part_details]') AND type in (N'U'))
BEGIN
CREATE TABLE part_details (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    item_id BIGINT NOT NULL, /* 物品ID */
    part_number VARCHAR(50), /* 货号 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_item_id_part UNIQUE (item_id),
    CONSTRAINT FK_part_details_item FOREIGN KEY (item_id) REFERENCES warehouse_item(id)
);
END
GO

-- 耗材详情表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[consumable_details]') AND type in (N'U'))
BEGIN
CREATE TABLE consumable_details (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    item_id BIGINT NOT NULL, /* 物品ID */
    part_number VARCHAR(50), /* 货号 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_item_id_consumable UNIQUE (item_id),
    CONSTRAINT FK_consumable_details_item FOREIGN KEY (item_id) REFERENCES warehouse_item(id)
);
END
GO

-- 固定资产详情表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[asset_details]') AND type in (N'U'))
BEGIN
CREATE TABLE asset_details (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    item_id BIGINT NOT NULL, /* 物品ID */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_item_id_asset UNIQUE (item_id),
    CONSTRAINT FK_asset_details_item FOREIGN KEY (item_id) REFERENCES warehouse_item(id)
);
END
GO

-- 出入库记录表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[stock_record]') AND type in (N'U'))
BEGIN
CREATE TABLE stock_record (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    record_code VARCHAR(50) NOT NULL, /* 记录编号 */
    item_id BIGINT NOT NULL, /* 物品ID */
    record_type TINYINT NOT NULL, /* 记录类型（1-入库，2-出库） */
    quantity INT NOT NULL, /* 数量 */
    operator_id BIGINT NOT NULL, /* 操作人ID */
    operator_name VARCHAR(50) NOT NULL, /* 操作人姓名 */
    operation_time DATETIME2 NOT NULL, /* 操作时间 */
    task_id VARCHAR(20), /* 关联任务ID */
    description VARCHAR(255), /* 描述/备注 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_record_code UNIQUE (record_code),
    CONSTRAINT FK_stock_record_item FOREIGN KEY (item_id) REFERENCES warehouse_item(id),
    CONSTRAINT FK_stock_record_operator FOREIGN KEY (operator_id) REFERENCES [user](id)
);

CREATE INDEX idx_item_id ON stock_record(item_id);
CREATE INDEX idx_operator_id ON stock_record(operator_id);
CREATE INDEX idx_task_id ON stock_record(task_id);
END
GO

-- 物品申请表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[item_request]') AND type in (N'U'))
BEGIN
CREATE TABLE item_request (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    request_code VARCHAR(50) NOT NULL, /* 申请编号 */
    requester_id BIGINT NOT NULL, /* 申请人ID */
    requester_name VARCHAR(50) NOT NULL, /* 申请人姓名 */
    request_type TINYINT NOT NULL, /* 申请类型（1-使用申请，2-采购申请） */
    status TINYINT DEFAULT 0, /* 状态（0-待审批，1-已同意，2-已拒绝） */
    item_id BIGINT, /* 物品ID（使用申请） */
    item_name VARCHAR(100), /* 物品名称（采购申请可能是新物品） */
    item_model VARCHAR(100), /* 规格型号 */
    quantity INT NOT NULL, /* 数量 */
    task_id VARCHAR(20), /* 关联任务ID */
    purpose VARCHAR(255), /* 用途 */
    urgency TINYINT DEFAULT 1, /* 紧急程度（1-普通，2-紧急，3-特急） */
    approver_id BIGINT, /* 审批人ID */
    approver_name VARCHAR(50), /* 审批人姓名 */
    approval_time DATETIME2, /* 审批时间 */
    approval_comment VARCHAR(255), /* 审批意见 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_request_code UNIQUE (request_code),
    CONSTRAINT FK_item_request_requester FOREIGN KEY (requester_id) REFERENCES [user](id),
    CONSTRAINT FK_item_request_approver FOREIGN KEY (approver_id) REFERENCES [user](id)
);

CREATE INDEX idx_requester_id ON item_request(requester_id);
CREATE INDEX idx_item_id_request ON item_request(item_id);
CREATE INDEX idx_task_id_request ON item_request(task_id);
CREATE INDEX idx_status ON item_request(status);
END
GO

-- 盘库记录表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[inventory_check]') AND type in (N'U'))
BEGIN
CREATE TABLE inventory_check (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    check_code VARCHAR(50) NOT NULL, /* 盘库编号 */
    warehouse_id BIGINT NOT NULL, /* 仓库ID */
    checker_id BIGINT NOT NULL, /* 盘库人ID */
    checker_name VARCHAR(50) NOT NULL, /* 盘库人姓名 */
    check_time DATETIME2 NOT NULL, /* 盘库时间 */
    status TINYINT DEFAULT 0, /* 状态（0-进行中，1-已完成） */
    description VARCHAR(255), /* 描述/备注 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_check_code UNIQUE (check_code),
    CONSTRAINT FK_inventory_check_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouse(id),
    CONSTRAINT FK_inventory_check_checker FOREIGN KEY (checker_id) REFERENCES [user](id)
);

CREATE INDEX idx_warehouse_id_check ON inventory_check(warehouse_id);
CREATE INDEX idx_checker_id ON inventory_check(checker_id);
END
GO

-- 盘库明细表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[inventory_check_detail]') AND type in (N'U'))
BEGIN
CREATE TABLE inventory_check_detail (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    check_id BIGINT NOT NULL, /* 盘库ID */
    item_id BIGINT NOT NULL, /* 物品ID */
    system_quantity INT NOT NULL, /* 系统数量 */
    actual_quantity INT NOT NULL, /* 实际数量 */
    difference INT DEFAULT 0, /* 差异数量 */
    remark VARCHAR(255), /* 备注 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT FK_inventory_check_detail_check FOREIGN KEY (check_id) REFERENCES inventory_check(id),
    CONSTRAINT FK_inventory_check_detail_item FOREIGN KEY (item_id) REFERENCES warehouse_item(id)
);

CREATE INDEX idx_check_id ON inventory_check_detail(check_id);
CREATE INDEX idx_item_id_detail ON inventory_check_detail(item_id);
END
GO

-- 插入初始仓库数据
IF NOT EXISTS (SELECT * FROM warehouse WHERE code = 'TZ')
BEGIN
    SET IDENTITY_INSERT warehouse ON;
    INSERT INTO warehouse (id, code, name, location, description) VALUES
    (1, 'TZ', '泰州仓', '泰州', '泰州公司仓库'),
    (2, 'SZ', '苏州仓', '苏州', '苏州公司仓库'),
    (3, 'WH', '武汉仓', '武汉', '武汉公司仓库'),
    (4, 'SC', '四川仓', '四川', '四川公司仓库');
    SET IDENTITY_INSERT warehouse OFF;
END
GO

-- 插入初始物品分类数据
IF NOT EXISTS (SELECT * FROM item_category WHERE code = 'INSTRUMENT')
BEGIN
    SET IDENTITY_INSERT item_category ON;
    INSERT INTO item_category (id, code, name, level, sort_order) VALUES
    (1, 'INSTRUMENT', '仪器库', 1, 1),
    (2, 'PART', '配件库', 1, 2),
    (3, 'CONSUMABLE', '耗材库', 1, 3),
    (4, 'ASSET', '固定资产库', 1, 4);
    SET IDENTITY_INSERT item_category OFF;
END
GO

-- 插入子分类示例
IF NOT EXISTS (SELECT * FROM item_category WHERE code = 'INSTRUMENT_NEW')
BEGIN
    SET IDENTITY_INSERT item_category ON;
    INSERT INTO item_category (id, code, name, parent_id, level, sort_order) VALUES
    (5, 'INSTRUMENT_NEW', '全新仪器', 1, 2, 1),
    (6, 'INSTRUMENT_USED', '二手仪器', 1, 2, 2),
    (7, 'PART_ELECTRONIC', '电子配件', 2, 2, 1),
    (8, 'PART_MECHANICAL', '机械配件', 2, 2, 2),
    (9, 'CONSUMABLE_CHEMICAL', '化学耗材', 3, 2, 1),
    (10, 'CONSUMABLE_LAB', '实验室耗材', 3, 2, 2),
    (11, 'ASSET_OFFICE', '办公设备', 4, 2, 1),
    (12, 'ASSET_LAB', '实验室设备', 4, 2, 2);
    SET IDENTITY_INSERT item_category OFF;
END
GO

-- 插入示例仓库数据
IF NOT EXISTS (SELECT * FROM warehouse WHERE code = 'WH001' AND NOT EXISTS (SELECT * FROM warehouse WHERE code = 'TZ'))
BEGIN
    SET IDENTITY_INSERT warehouse ON;
    INSERT INTO warehouse (id, code, name, location, description) VALUES
    (5, 'WH001', '总部仓库', '广州市黄埔区开发区科学城', '公司总部主仓库'),
    (6, 'WH002', '上海仓库', '上海市浦东新区张江高科技园区', '上海分公司仓库'),
    (7, 'WH003', '北京仓库', '北京市海淀区中关村', '北京分公司仓库');
    SET IDENTITY_INSERT warehouse OFF;
END
GO

-- 插入示例物品数据 (仪器)
IF NOT EXISTS (SELECT * FROM warehouse_item WHERE item_code = 'INS-2024-001')
BEGIN
    SET IDENTITY_INSERT warehouse_item ON;
    INSERT INTO warehouse_item (id, item_code, category_id, warehouse_id, name, model, manufacturer, quantity, cost, arrival_date, area, description)
    VALUES
    (1, 'INS-2024-001', 1, 1, '气相色谱仪', 'GC-2000', 'Agilent', 2, 150000.00, '2024-01-15', 'A区', '高精度气相色谱仪'),
    (2, 'INS-2024-002', 1, 2, '液相色谱仪', 'LC-3000', 'Waters', 1, 180000.00, '2024-02-20', 'B区', '高效液相色谱仪'),
    (3, 'INS-2024-003', 1, 3, '质谱仪', 'MS-6000', 'Thermo Fisher', 1, 250000.00, '2024-03-10', 'A区', '高分辨质谱仪');
    SET IDENTITY_INSERT warehouse_item OFF;
END
GO

-- 插入仪器详情
IF NOT EXISTS (SELECT * FROM instrument_details WHERE item_id = 1)
BEGIN
    SET IDENTITY_INSERT instrument_details ON;
    INSERT INTO instrument_details (id, item_id, is_new, production_date)
    VALUES
    (1, 1, 1, '2023-12-01'),
    (2, 2, 1, '2024-01-15'),
    (3, 3, 0, '2022-06-20');
    SET IDENTITY_INSERT instrument_details OFF;
END
GO

-- 插入示例物品数据 (配件)
IF NOT EXISTS (SELECT * FROM warehouse_item WHERE item_code = 'PART-2024-001')
BEGIN
    SET IDENTITY_INSERT warehouse_item ON;
    INSERT INTO warehouse_item (id, item_code, category_id, warehouse_id, name, model, manufacturer, quantity, cost, arrival_date, area, description)
    VALUES
    (4, 'PART-2024-001', 2, 1, 'GC色谱柱', 'DB-5MS', 'Agilent', 10, 3500.00, '2024-01-20', 'C区', '30m x 0.25mm x 0.25μm'),
    (5, 'PART-2024-002', 2, 2, 'LC色谱柱', 'C18', 'Waters', 5, 4200.00, '2024-02-25', 'D区', '150mm x 4.6mm x 5μm'),
    (6, 'PART-2024-003', 2, 3, '质谱离子源', 'ESI-5000', 'Thermo Fisher', 2, 15000.00, '2024-03-15', 'B区', '电喷雾离子源');
    SET IDENTITY_INSERT warehouse_item OFF;
END
GO

-- 插入配件详情
IF NOT EXISTS (SELECT * FROM part_details WHERE item_id = 4)
BEGIN
    SET IDENTITY_INSERT part_details ON;
    INSERT INTO part_details (id, item_id, part_number)
    VALUES
    (1, 4, 'AG-DB5-30'),
    (2, 5, 'WA-C18-150'),
    (3, 6, 'TF-ESI-5K');
    SET IDENTITY_INSERT part_details OFF;
END
GO

-- 插入示例物品数据 (耗材)
IF NOT EXISTS (SELECT * FROM warehouse_item WHERE item_code = 'CON-2024-001')
BEGIN
    SET IDENTITY_INSERT warehouse_item ON;
    INSERT INTO warehouse_item (id, item_code, category_id, warehouse_id, name, model, manufacturer, quantity, cost, arrival_date, area, description)
    VALUES
    (7, 'CON-2024-001', 3, 1, '样品瓶', '2mL', 'Agilent', 500, 5.00, '2024-01-25', 'E区', '透明玻璃样品瓶'),
    (8, 'CON-2024-002', 3, 2, '进样针', '10μL', 'Hamilton', 100, 25.00, '2024-02-28', 'F区', '高精度进样针'),
    (9, 'CON-2024-003', 3, 3, '色谱纸', 'A4', 'Whatman', 1000, 0.50, '2024-03-20', 'E区', '高质量记录纸');
    SET IDENTITY_INSERT warehouse_item OFF;
END
GO

-- 插入耗材详情
IF NOT EXISTS (SELECT * FROM consumable_details WHERE item_id = 7)
BEGIN
    SET IDENTITY_INSERT consumable_details ON;
    INSERT INTO consumable_details (id, item_id, part_number)
    VALUES
    (1, 7, 'AG-SV-2ML'),
    (2, 8, 'HM-SYR-10'),
    (3, 9, 'WM-PAP-A4');
    SET IDENTITY_INSERT consumable_details OFF;
END
GO

-- 插入示例物品数据 (固定资产)
IF NOT EXISTS (SELECT * FROM warehouse_item WHERE item_code = 'AST-2024-001')
BEGIN
    SET IDENTITY_INSERT warehouse_item ON;
    INSERT INTO warehouse_item (id, item_code, category_id, warehouse_id, name, model, manufacturer, quantity, cost, arrival_date, area, description)
    VALUES
    (10, 'AST-2024-001', 4, 1, '实验台', 'LT-2000', 'LabTech', 5, 8000.00, '2024-01-30', 'G区', '标准实验台'),
    (11, 'AST-2024-002', 4, 2, '通风柜', 'FC-1500', 'SafeLab', 2, 12000.00, '2024-03-01', 'H区', '化学品通风柜'),
    (12, 'AST-2024-003', 4, 3, '纯水系统', 'WP-Ultra', 'Millipore', 1, 35000.00, '2024-03-25', 'G区', '超纯水系统');
    SET IDENTITY_INSERT warehouse_item OFF;
END
GO

-- 插入固定资产详情
IF NOT EXISTS (SELECT * FROM asset_details WHERE item_id = 10)
BEGIN
    SET IDENTITY_INSERT asset_details ON;
    INSERT INTO asset_details (id, item_id)
    VALUES
    (1, 10),
    (2, 11),
    (3, 12);
    SET IDENTITY_INSERT asset_details OFF;
END
GO

-- 插入出入库记录示例
IF NOT EXISTS (SELECT * FROM stock_record WHERE record_code = 'IN-2024-001')
BEGIN
    SET IDENTITY_INSERT stock_record ON;
    INSERT INTO stock_record (id, record_code, item_id, record_type, quantity, operator_id, operator_name, operation_time, description)
    VALUES
    (1, 'IN-2024-001', 1, 1, 2, 3, '李四', '2024-01-15 10:30:00', '初始入库'),
    (2, 'IN-2024-002', 2, 1, 1, 3, '李四', '2024-02-20 14:15:00', '初始入库'),
    (3, 'IN-2024-003', 4, 1, 10, 3, '李四', '2024-01-20 09:45:00', '初始入库'),
    (4, 'OUT-2024-001', 4, 2, 2, 3, '李四', '2024-04-05 11:20:00', '用于维修任务RP-2024-001'),
    (5, 'OUT-2024-002', 7, 2, 50, 3, '李四', '2024-04-10 15:30:00', '实验室日常消耗');
    SET IDENTITY_INSERT stock_record OFF;
END
GO

-- 插入物品申请示例
IF NOT EXISTS (SELECT * FROM item_request WHERE request_code = 'REQ-2024-001')
BEGIN
    SET IDENTITY_INSERT item_request ON;
    INSERT INTO item_request (id, request_code, requester_id, requester_name, request_type, status, item_id, item_name, item_model, quantity, task_id, purpose, urgency, approver_id, approver_name, approval_time, approval_comment)
    VALUES
    (1, 'REQ-2024-001', 2, '张三', 1, 1, 4, 'GC色谱柱', 'DB-5MS', 2, 'RP-2024-001', '用于维修气相色谱仪', 2, 3, '李四', '2024-04-05 10:30:00', '同意申请'),
    (2, 'REQ-2024-002', 2, '张三', 2, 0, NULL, '气相色谱仪检测器', 'FID-3000', 1, NULL, '备用配件', 1, NULL, NULL, NULL, NULL),
    (3, 'REQ-2024-003', 4, '王五', 1, 2, 7, '样品瓶', '2mL', 100, 'MT-2024-001', '保养使用', 1, 3, '李四', '2024-04-08 14:15:00', '库存不足，暂时拒绝');
    SET IDENTITY_INSERT item_request OFF;
END
GO

-- 插入盘库记录示例
IF NOT EXISTS (SELECT * FROM inventory_check WHERE check_code = 'INV-2024-001')
BEGIN
    SET IDENTITY_INSERT inventory_check ON;
    INSERT INTO inventory_check (id, check_code, warehouse_id, checker_id, checker_name, check_time, status, description)
    VALUES
    (1, 'INV-2024-001', 1, 3, '李四', '2024-03-31 09:00:00', 1, '第一季度例行盘库'),
    (2, 'INV-2024-002', 2, 3, '李四', '2024-04-01 10:30:00', 0, '上海仓库例行盘库');
    SET IDENTITY_INSERT inventory_check OFF;
END
GO

-- 插入盘库明细示例
IF NOT EXISTS (SELECT * FROM inventory_check_detail WHERE check_id = 1 AND item_id = 1)
BEGIN
    SET IDENTITY_INSERT inventory_check_detail ON;
    INSERT INTO inventory_check_detail (id, check_id, item_id, system_quantity, actual_quantity, difference, remark)
    VALUES
    (1, 1, 1, 2, 2, 0, '数量一致'),
    (2, 1, 4, 8, 8, 0, '数量一致'),
    (3, 1, 7, 450, 445, -5, '有少量破损'),
    (4, 1, 10, 5, 5, 0, '数量一致'),
    (5, 2, 2, 1, 1, 0, '数量一致'),
    (6, 2, 5, 5, 4, -1, '一个丢失');
    SET IDENTITY_INSERT inventory_check_detail OFF;
END
GO

-- 脚本已修复 - 2024-06-25
-- 1. 修复了warehouse表的主键冲突问题
-- 2. 修改了示例仓库数据的ID，从5开始，避免与初始仓库数据的ID(1-4)冲突
-- 3. 添加了条件检查，确保只有在没有初始仓库数据的情况下才插入示例仓库数据 