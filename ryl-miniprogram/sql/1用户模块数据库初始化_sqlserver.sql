-- 用户模块数据库设计
USE master;
GO

-- 创建数据库
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'ryl_engineer')
BEGIN
    CREATE DATABASE ryl_engineer;
END
GO

USE ryl_engineer;
GO

-- 用户表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[user]') AND type in (N'U'))
BEGIN
CREATE TABLE [user] (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    work_id VARCHAR(50) NOT NULL, /* 工号 */
    name VARCHAR(50) NOT NULL, /* 姓名 */
    mobile VARCHAR(20) NOT NULL, /* 手机号 */
    password VARCHAR(255) NOT NULL, /* 密码 */
    department VARCHAR(50), /* 部门 */
    location VARCHAR(50), /* 工作地点 */
    avatar VARCHAR(255), /* 头像URL */
    status TINYINT DEFAULT 1, /* 状态（0-禁用，1-启用） */
    token VARCHAR(255), /* 登录令牌 */
    token_expire DATETIME2, /* 令牌过期时间 */
    last_login_time DATETIME2, /* 最后登录时间 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_work_id UNIQUE (work_id),
    CONSTRAINT uk_mobile UNIQUE (mobile)
);

CREATE INDEX idx_status ON [user](status);
END
GO

-- 角色表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[role]') AND type in (N'U'))
BEGIN
CREATE TABLE [role] (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(20) NOT NULL, /* 角色编码 */
    name VARCHAR(50) NOT NULL, /* 角色名称 */
    description VARCHAR(255), /* 角色描述 */
    status TINYINT DEFAULT 1, /* 状态（0-禁用，1-启用） */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_role_code UNIQUE (code)
);

CREATE INDEX idx_role_status ON [role](status);
END
GO

-- 权限表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[permission]') AND type in (N'U'))
BEGIN
CREATE TABLE permission (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    permission_code VARCHAR(50) NOT NULL, /* 权限编码 */
    permission_name VARCHAR(50) NOT NULL, /* 权限名称 */
    resource_type VARCHAR(20) NOT NULL, /* 资源类型（menu-菜单, button-按钮, api-接口） */
    resource_path VARCHAR(100), /* 资源路径 */
    parent_id BIGINT, /* 父权限ID */
    sort_order INT DEFAULT 0, /* 排序顺序 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_permission_code UNIQUE (permission_code)
);

CREATE INDEX idx_resource_type ON permission(resource_type);
CREATE INDEX idx_parent_id_permission ON permission(parent_id);
END
GO

-- 角色权限关联表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[role_permission]') AND type in (N'U'))
BEGIN
CREATE TABLE role_permission (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    role_id BIGINT NOT NULL, /* 角色ID */
    permission_id BIGINT NOT NULL, /* 权限ID */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    CONSTRAINT uk_role_permission UNIQUE (role_id, permission_id),
    CONSTRAINT FK_role_permission_role FOREIGN KEY (role_id) REFERENCES role(id),
    CONSTRAINT FK_role_permission_permission FOREIGN KEY (permission_id) REFERENCES permission(id)
);

CREATE INDEX idx_role_id ON role_permission(role_id);
CREATE INDEX idx_permission_id ON role_permission(permission_id);
END
GO

-- 用户角色关联表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[user_role]') AND type in (N'U'))
BEGIN
CREATE TABLE user_role (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL, /* 用户ID */
    role_id BIGINT NOT NULL, /* 角色ID */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id),
    CONSTRAINT FK_user_role_user FOREIGN KEY (user_id) REFERENCES [user](id),
    CONSTRAINT FK_user_role_role FOREIGN KEY (role_id) REFERENCES [role](id)
);

CREATE INDEX idx_user_id ON user_role(user_id);
CREATE INDEX idx_role_id ON user_role(role_id);
END
GO

-- 登录日志表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[login_log]') AND type in (N'U'))
BEGIN
CREATE TABLE login_log (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL, /* 用户ID */
    username VARCHAR(50) NOT NULL, /* 用户名 */
    login_time DATETIME2 DEFAULT GETDATE(), /* 登录时间 */
    login_ip VARCHAR(50), /* 登录IP */
    login_device VARCHAR(100), /* 登录设备 */
    login_location VARCHAR(100), /* 登录地点 */
    status TINYINT NOT NULL, /* 状态（0-失败，1-成功） */
    remark VARCHAR(255), /* 备注 */
    CONSTRAINT FK_login_log_user FOREIGN KEY (user_id) REFERENCES [user](id)
);

CREATE INDEX idx_user_id_login_log ON login_log(user_id);
CREATE INDEX idx_login_time ON login_log(login_time);
CREATE INDEX idx_status_login_log ON login_log(status);
END
GO

-- 操作日志表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[operation_log]') AND type in (N'U'))
BEGIN
CREATE TABLE operation_log (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL, /* 用户ID */
    username VARCHAR(50) NOT NULL, /* 用户名 */
    operation_time DATETIME2 DEFAULT GETDATE(), /* 操作时间 */
    module VARCHAR(50) NOT NULL, /* 操作模块 */
    operation VARCHAR(50) NOT NULL, /* 操作类型 */
    method VARCHAR(100) NOT NULL, /* 操作方法 */
    params NVARCHAR(MAX), /* 操作参数 */
    operation_ip VARCHAR(50), /* 操作IP */
    operation_result VARCHAR(10), /* 操作结果（success-成功, fail-失败） */
    error_msg NVARCHAR(MAX), /* 错误信息 */
    CONSTRAINT FK_operation_log_user FOREIGN KEY (user_id) REFERENCES [user](id)
);

CREATE INDEX idx_user_id_operation_log ON operation_log(user_id);
CREATE INDEX idx_operation_time ON operation_log(operation_time);
CREATE INDEX idx_module ON operation_log(module);
CREATE INDEX idx_operation ON operation_log(operation);
END
GO

-- 用户设置表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[user_setting]') AND type in (N'U'))
BEGIN
CREATE TABLE user_setting (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL, /* 用户ID */
    setting_key VARCHAR(50) NOT NULL, /* 设置键 */
    setting_value NVARCHAR(MAX), /* 设置值 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_user_setting_key UNIQUE (user_id, setting_key),
    CONSTRAINT FK_user_setting_user FOREIGN KEY (user_id) REFERENCES [user](id)
);

CREATE INDEX idx_user_id_setting ON user_setting(user_id);
CREATE INDEX idx_setting_key ON user_setting(setting_key);
END
GO

-- 用户令牌表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[user_token]') AND type in (N'U'))
BEGIN
CREATE TABLE user_token (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL, /* 用户ID */
    token VARCHAR(255) NOT NULL, /* 令牌 */
    expire_time DATETIME2 NOT NULL, /* 过期时间 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_token UNIQUE (token),
    CONSTRAINT FK_user_token_user FOREIGN KEY (user_id) REFERENCES [user](id)
);

CREATE INDEX idx_user_id_token ON user_token(user_id);
CREATE INDEX idx_expire_time ON user_token(expire_time);
END
GO

-- 车辆信息表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[vehicle_info]') AND type in (N'U'))
BEGIN
CREATE TABLE vehicle_info (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL, /* 用户ID */
    plate_number VARCHAR(20) NOT NULL, /* 车牌号 */
    vehicle_type VARCHAR(20) NOT NULL, /* 车辆类型（公车、私车） */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT FK_vehicle_info_user FOREIGN KEY (user_id) REFERENCES [user](id)
);

CREATE INDEX idx_user_id_vehicle ON vehicle_info(user_id);
END
GO

-- 创建车辆打卡记录表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[vehicle_record]') AND type in (N'U'))
BEGIN
CREATE TABLE vehicle_record (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL, /* 用户ID */
    task_id varchar(64) NOT NULL, /* 任务ID */
    task_name varchar(255) NOT NULL, /* 任务名称 */
    type TINYINT NOT NULL, /* 打卡类型（1-出发打卡，2-到达打卡，3-返程打卡） */
    check_in_time DATETIME2 NOT NULL, /* 打卡时间 */
    location varchar(255) NOT NULL, /* 打卡地址 */
    longitude varchar(32) NOT NULL, /* 经度 */
    latitude varchar(32) NOT NULL, /* 纬度 */
    photos varchar(1024), /* 照片URL，多个照片以逗号分隔 */
    distance decimal(10,2), /* 里程数（公里） */
    transport_type varchar(20) NOT NULL, /* 交通工具类型（company-公车, private-私车, public-公共交通） */
    vehicle_id BIGINT, /* 车辆ID */
    remark varchar(512), /* 备注 */
    create_time DATETIME2 NOT NULL, /* 创建时间 */
    update_time DATETIME2 NOT NULL, /* 更新时间 */
    CONSTRAINT FK_vehicle_record_user FOREIGN KEY (user_id) REFERENCES [user](id)
);

CREATE INDEX idx_user_id_record ON vehicle_record(user_id);
CREATE INDEX idx_task_id_record ON vehicle_record(task_id);
CREATE INDEX idx_check_in_time ON vehicle_record(check_in_time);
END
GO

-- 插入初始角色数据
IF NOT EXISTS (SELECT * FROM [role] WHERE code = 'ADMIN')
BEGIN
    SET IDENTITY_INSERT [role] ON;
    INSERT INTO [role] (id, code, name, description) VALUES
    (1, 'ADMIN', '系统管理员', '系统管理员，拥有所有权限'),
    (2, 'ENGINEER', '工程师', '负责执行维修、安装等任务'),
    (3, 'SALES', '销售', '负责客户关系和销售管理'),
    (4, 'WAREHOUSE', '仓库管理员', '负责仓库和物料管理');
    SET IDENTITY_INSERT [role] OFF;
END
GO

-- 插入初始权限数据
IF NOT EXISTS (SELECT * FROM permission WHERE permission_code = 'system:admin')
BEGIN
    SET IDENTITY_INSERT permission ON;
    -- 系统管理权限
    INSERT INTO permission (id, permission_code, permission_name, resource_type, resource_path, parent_id, sort_order) VALUES
    (1, 'system:admin', '系统管理', 'menu', '/system', NULL, 1),
    (2, 'system:user', '用户管理', 'menu', '/system/user', 1, 1),
    (3, 'system:role', '角色管理', 'menu', '/system/role', 1, 2),
    (4, 'system:permission', '权限管理', 'menu', '/system/permission', 1, 3),
    (5, 'system:log', '日志管理', 'menu', '/system/log', 1, 4),
    
    -- 用户管理按钮权限
    (6, 'system:user:list', '用户列表', 'button', NULL, 2, 1),
    (7, 'system:user:add', '添加用户', 'button', NULL, 2, 2),
    (8, 'system:user:edit', '编辑用户', 'button', NULL, 2, 3),
    (9, 'system:user:delete', '删除用户', 'button', NULL, 2, 4),
    (10, 'system:user:reset', '重置密码', 'button', NULL, 2, 5),
    
    -- 角色管理按钮权限
    (11, 'system:role:list', '角色列表', 'button', NULL, 3, 1),
    (12, 'system:role:add', '添加角色', 'button', NULL, 3, 2),
    (13, 'system:role:edit', '编辑角色', 'button', NULL, 3, 3),
    (14, 'system:role:delete', '删除角色', 'button', NULL, 3, 4),
    (15, 'system:role:permission', '分配权限', 'button', NULL, 3, 5),
    
    -- 权限管理按钮权限
    (16, 'system:permission:list', '权限列表', 'button', NULL, 4, 1),
    (17, 'system:permission:add', '添加权限', 'button', NULL, 4, 2),
    (18, 'system:permission:edit', '编辑权限', 'button', NULL, 4, 3),
    (19, 'system:permission:delete', '删除权限', 'button', NULL, 4, 4),
    
    -- 日志管理按钮权限
    (20, 'system:log:login', '登录日志', 'button', NULL, 5, 1),
    (21, 'system:log:operation', '操作日志', 'button', NULL, 5, 2),
    
    -- 任务管理权限
    (22, 'task:admin', '任务管理', 'menu', '/task', NULL, 2),
    (23, 'task:list', '任务列表', 'menu', '/task/list', 22, 1),
    (24, 'task:add', '创建任务', 'button', NULL, 23, 1),
    (25, 'task:edit', '编辑任务', 'button', NULL, 23, 2),
    (26, 'task:delete', '删除任务', 'button', NULL, 23, 3),
    (27, 'task:assign', '分配任务', 'button', NULL, 23, 4),
    (28, 'task:detail', '任务详情', 'menu', '/task/detail', 22, 2),
    (29, 'task:report', '任务报告', 'menu', '/task/report', 22, 3),
    
    -- 仓库管理权限
    (30, 'warehouse:admin', '仓库管理', 'menu', '/warehouse', NULL, 3),
    (31, 'warehouse:item', '物品管理', 'menu', '/warehouse/item', 30, 1),
    (32, 'warehouse:item:add', '添加物品', 'button', NULL, 31, 1),
    (33, 'warehouse:item:edit', '编辑物品', 'button', NULL, 31, 2),
    (34, 'warehouse:item:delete', '删除物品', 'button', NULL, 31, 3),
    (35, 'warehouse:stock', '出入库管理', 'menu', '/warehouse/stock', 30, 2),
    (36, 'warehouse:stock:in', '入库操作', 'button', NULL, 35, 1),
    (37, 'warehouse:stock:out', '出库操作', 'button', NULL, 35, 2),
    (38, 'warehouse:inventory', '库存盘点', 'menu', '/warehouse/inventory', 30, 3),
    
    -- 消息管理权限
    (39, 'message:admin', '消息管理', 'menu', '/message', NULL, 4),
    (40, 'message:chat', '聊天消息', 'menu', '/message/chat', 39, 1),
    (41, 'message:announcement', '系统公告', 'menu', '/message/announcement', 39, 2),
    (42, 'message:announcement:add', '发布公告', 'button', NULL, 41, 1),
    (43, 'message:announcement:edit', '编辑公告', 'button', NULL, 41, 2),
    (44, 'message:announcement:delete', '删除公告', 'button', NULL, 41, 3),
    
    -- 个人中心权限
    (45, 'profile:view', '个人中心', 'menu', '/profile', NULL, 5),
    (46, 'profile:edit', '编辑资料', 'button', NULL, 45, 1),
    (47, 'profile:password', '修改密码', 'button', NULL, 45, 2);
    SET IDENTITY_INSERT permission OFF;
END
GO

-- 为角色分配权限
IF NOT EXISTS (SELECT * FROM role_permission WHERE role_id = 1 AND permission_id = 1)
BEGIN
    -- 管理员角色权限
    INSERT INTO role_permission (role_id, permission_id) VALUES
    -- 系统管理权限
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
    (1, 6), (1, 7), (1, 8), (1, 9), (1, 10),
    (1, 11), (1, 12), (1, 13), (1, 14), (1, 15),
    (1, 16), (1, 17), (1, 18), (1, 19),
    (1, 20), (1, 21),
    -- 任务管理权限
    (1, 22), (1, 23), (1, 24), (1, 25), (1, 26), (1, 27), (1, 28), (1, 29),
    -- 仓库管理权限
    (1, 30), (1, 31), (1, 32), (1, 33), (1, 34), (1, 35), (1, 36), (1, 37), (1, 38),
    -- 消息管理权限
    (1, 39), (1, 40), (1, 41), (1, 42), (1, 43), (1, 44),
    -- 个人中心权限
    (1, 45), (1, 46), (1, 47);
    
    -- 工程师角色权限
    INSERT INTO role_permission (role_id, permission_id) VALUES
    -- 任务管理权限（部分）
    (2, 22), (2, 23), (2, 28), (2, 29),
    -- 仓库管理权限（部分）
    (2, 30), (2, 31), (2, 35), (2, 36), (2, 37),
    -- 消息管理权限（部分）
    (2, 39), (2, 40), (2, 41),
    -- 个人中心权限
    (2, 45), (2, 46), (2, 47);
    
    -- 销售角色权限
    INSERT INTO role_permission (role_id, permission_id) VALUES
    -- 任务管理权限（部分）
    (3, 22), (3, 23), (3, 24), (3, 28),
    -- 消息管理权限（部分）
    (3, 39), (3, 40), (3, 41),
    -- 个人中心权限
    (3, 45), (3, 46), (3, 47);
    
    -- 仓库管理员角色权限
    INSERT INTO role_permission (role_id, permission_id) VALUES
    -- 仓库管理权限
    (4, 30), (4, 31), (4, 32), (4, 33), (4, 34), (4, 35), (4, 36), (4, 37), (4, 38),
    -- 消息管理权限（部分）
    (4, 39), (4, 40), (4, 41),
    -- 个人中心权限
    (4, 45), (4, 46), (4, 47);
END
GO

-- 插入初始用户数据
IF NOT EXISTS (SELECT * FROM [user] WHERE work_id = 'admin')
BEGIN
    SET IDENTITY_INSERT [user] ON;
    INSERT INTO [user] (id, work_id, name, mobile, password, department, location, avatar, status) VALUES
    (1, 'admin', '系统管理员', '13800138000', '123456', '管理部', '总部', 'https://example.com/avatars/admin.png', 1),
    (2, 'RYL001', '张三', '13900139001', '123456', '技术部', '上海', 'https://example.com/avatars/zhangsan.png', 1),
    (3, 'RYL002', '李四', '13900139002', '123456', '仓库部', '北京', 'https://example.com/avatars/lisi.png', 1),
    (4, 'RYL003', '王五', '13900139003', '123456', '销售部', '广州', 'https://example.com/avatars/wangwu.png', 1);
    SET IDENTITY_INSERT [user] OFF;
END
GO

-- 插入用户角色关联数据
IF NOT EXISTS (SELECT * FROM user_role WHERE user_id = 1 AND role_id = 1)
BEGIN
    INSERT INTO user_role (user_id, role_id) VALUES
    (1, 1), -- 管理员-系统管理员
    (2, 2), -- 张三-工程师
    (3, 4), -- 李四-仓库管理员
    (4, 3); -- 王五-销售
END
GO

-- 插入初始用户设置
IF NOT EXISTS (SELECT * FROM user_setting WHERE user_id = 1 AND setting_key = 'theme')
BEGIN
    INSERT INTO user_setting (user_id, setting_key, setting_value) VALUES
    (1, 'theme', 'light'),
    (1, 'notification', 'all'),
    (2, 'theme', 'dark'),
    (2, 'notification', 'important'),
    (3, 'theme', 'light'),
    (3, 'notification', 'all');
END
GO

-- 插入示例车辆信息
IF NOT EXISTS (SELECT * FROM vehicle_info WHERE plate_number = '沪A12345')
BEGIN
    INSERT INTO vehicle_info (user_id, plate_number, vehicle_type) VALUES
    (2, '沪A12345', '公车'),
    (2, '沪B67890', '私车');
END
GO

-- 添加示例车辆打卡记录
IF NOT EXISTS (SELECT * FROM vehicle_record WHERE task_id = 'RP-2024-001' AND type = 1)
BEGIN
    INSERT INTO vehicle_record (user_id, task_id, task_name, type, check_in_time, location, longitude, latitude, photos, distance, transport_type, vehicle_id, remark, create_time, update_time)
    VALUES
    (2, 'RP-2024-001', '气相色谱仪维修', 1, '2024-03-15 08:30:00', '公司（广州市黄埔区开发区科学城）', '113.5603', '23.1647', 'photo1.jpg', 0.00, 'company', 1, '出发前往客户现场', GETDATE(), GETDATE()),
    (2, 'RP-2024-001', '气相色谱仪维修', 2, '2024-03-15 09:25:00', '上海市浦东新区张江高科技园区', '121.5934', '31.2193', 'photo2.jpg,photo3.jpg', 25.50, 'company', 1, '到达客户现场', GETDATE(), GETDATE()),
    (2, 'IN-2024-002', '液相色谱仪安装', 1, '2024-03-20 09:00:00', '公司（广州市黄埔区开发区科学城）', '113.5603', '23.1647', 'photo5.jpg', 0.00, 'private', 2, '出发前往北京客户现场', GETDATE(), GETDATE()),
    (2, 'MT-2024-003', '质谱仪年度保养', 1, '2024-03-01 08:15:00', '公司（广州市黄埔区开发区科学城）', '113.5603', '23.1647', NULL, 0.00, 'public', NULL, '出发前往广州客户现场', GETDATE(), GETDATE()),
    (2, 'MT-2024-003', '质谱仪年度保养', 2, '2024-03-01 09:30:00', '广州市黄埔区科学城', '113.5295', '23.1647', 'photo10.jpg,photo11.jpg', 3.50, 'public', NULL, '到达客户现场', GETDATE(), GETDATE()),
    (2, 'MT-2024-003', '质谱仪年度保养', 3, '2024-03-01 18:45:00', '离开客户现场', '113.5297', '23.1649', 'photo12.jpg', 3.50, 'public', NULL, '保养完成返程', GETDATE(), GETDATE());
END
GO 