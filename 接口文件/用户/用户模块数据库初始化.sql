-- 用户认证模块数据库设计
-- 创建数据库
CREATE DATABASE IF NOT EXISTS ryl_engineer;
USE ryl_engineer;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    work_id VARCHAR(50) NOT NULL COMMENT '工号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    mobile VARCHAR(20) NOT NULL COMMENT '手机号',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    department VARCHAR(50) COMMENT '部门',
    location VARCHAR(50) COMMENT '工作地点',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    token VARCHAR(255) COMMENT '登录令牌',
    token_expire DATETIME COMMENT '令牌过期时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_time DATETIME COMMENT '最后登录时间',
    UNIQUE KEY uk_work_id (work_id),
    UNIQUE KEY uk_mobile (mobile)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 车辆信息表
CREATE TABLE IF NOT EXISTS vehicle_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    plate_number VARCHAR(20) NOT NULL COMMENT '车牌号',
    vehicle_type VARCHAR(20) NOT NULL COMMENT '车辆类型（公车、私车）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆信息表';

-- 角色表
CREATE TABLE IF NOT EXISTS role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(255) COMMENT '角色描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (role_id) REFERENCES role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 权限表
CREATE TABLE IF NOT EXISTS permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '权限名称',
    code VARCHAR(50) NOT NULL COMMENT '权限编码',
    description VARCHAR(255) COMMENT '权限描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role(id),
    FOREIGN KEY (permission_id) REFERENCES permission(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 创建车辆打卡记录表
CREATE TABLE IF NOT EXISTS `vehicle_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `task_id` varchar(64) NOT NULL COMMENT '任务ID',
  `task_name` varchar(255) NOT NULL COMMENT '任务名称',
  `type` TINYINT NOT NULL COMMENT '打卡类型（1-出发打卡，2-到达打卡，3-返程打卡）',
  `check_in_time` datetime NOT NULL COMMENT '打卡时间',
  `location` varchar(255) NOT NULL COMMENT '打卡地址',
  `longitude` varchar(32) NOT NULL COMMENT '经度',
  `latitude` varchar(32) NOT NULL COMMENT '纬度',
  `photos` varchar(1024) DEFAULT NULL COMMENT '照片URL，多个照片以逗号分隔',
  `distance` decimal(10,2) DEFAULT NULL COMMENT '里程数（公里）',
  `transport_type` varchar(20) NOT NULL COMMENT '交通工具类型（company-公车, private-私车, public-公共交通）',
  `vehicle_id` BIGINT DEFAULT NULL COMMENT '车辆ID',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_check_in_time` (`check_in_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆打卡记录表';

-- 插入初始角色数据
INSERT INTO role (name, code, description) VALUES
('系统管理员', 'ROLE_ADMIN', '系统管理员，拥有所有权限'),
('工程师', 'ROLE_ENGINEER', '工程师角色，负责维修和保养'),
('仓库管理员', 'ROLE_WAREHOUSE', '仓库管理员，负责库存管理'),
('客户经理', 'ROLE_CUSTOMER', '客户经理，负责客户关系管理');

-- 插入初始权限数据
INSERT INTO permission (name, code, description) VALUES
('用户管理', 'USER_MANAGE', '用户的增删改查'),
('角色管理', 'ROLE_MANAGE', '角色的增删改查'),
('权限管理', 'PERMISSION_MANAGE', '权限的增删改查'),
('仓库管理', 'WAREHOUSE_MANAGE', '仓库的增删改查'),
('设备管理', 'EQUIPMENT_MANAGE', '设备的增删改查'),
('任务管理', 'TASK_MANAGE', '任务的增删改查'),
('客户管理', 'CUSTOMER_MANAGE', '客户的增删改查'),
('报表统计', 'REPORT_VIEW', '查看报表统计');

-- 角色权限关联
-- 管理员拥有所有权限
INSERT INTO role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8);

-- 工程师拥有设备和任务管理权限
INSERT INTO role_permission (role_id, permission_id) VALUES
(2, 5), (2, 6);

-- 仓库管理员拥有仓库管理权限
INSERT INTO role_permission (role_id, permission_id) VALUES
(3, 4);

-- 客户经理拥有客户管理和报表查看权限
INSERT INTO role_permission (role_id, permission_id) VALUES
(4, 7), (4, 8);

-- 插入初始用户数据（使用明文密码"123456"）
INSERT INTO user (work_id, name, mobile, password, department, location, avatar, status) VALUES
('admin', '系统管理员', '13800138000', '123456', '管理部', '总部', 'https://example.com/avatars/admin.png', 1),
('RYL001', '张三', '13900139001', '123456', '技术部', '上海', 'https://example.com/avatars/zhangsan.png', 1),
('RYL002', '李四', '13900139002', '123456', '仓库部', '北京', 'https://example.com/avatars/lisi.png', 1),
('RYL003', '王五', '13900139003', '123456', '销售部', '广州', 'https://example.com/avatars/wangwu.png', 1);

-- 用户角色关联
INSERT INTO user_role (user_id, role_id) VALUES
(1, 1), -- 管理员拥有管理员角色
(2, 2), -- 张三拥有工程师角色
(3, 3), -- 李四拥有仓库管理员角色
(4, 4); -- 王五拥有客户经理角色

-- 插入示例车辆信息
INSERT INTO vehicle_info (user_id, plate_number, vehicle_type) VALUES
(2, '沪A12345', '公车'),
(2, '沪B67890', '私车');

-- 添加示例车辆打卡记录
INSERT INTO `vehicle_record` (`user_id`, `task_id`, `task_name`, `type`, `check_in_time`, `location`, `longitude`, `latitude`, `photos`, `distance`, `transport_type`, `vehicle_id`, `remark`, `create_time`, `update_time`)
VALUES
  (2, 'RP-2024-001', '气相色谱仪维修', 1, '2024-03-15 08:30:00', '公司（广州市黄埔区开发区科学城）', '113.5603', '23.1647', 'photo1.jpg', 0.00, 'company', 1, '出发前往客户现场', NOW(), NOW()),
  (2, 'RP-2024-001', '气相色谱仪维修', 2, '2024-03-15 09:25:00', '上海市浦东新区张江高科技园区', '121.5934', '31.2193', 'photo2.jpg,photo3.jpg', 25.50, 'company', 1, '到达客户现场', NOW(), NOW()),
  (2, 'IN-2024-002', '液相色谱仪安装', 1, '2024-03-20 09:00:00', '公司（广州市黄埔区开发区科学城）', '113.5603', '23.1647', 'photo5.jpg', 0.00, 'private', 2, '出发前往北京客户现场', NOW(), NOW()),
  (2, 'MT-2024-003', '质谱仪年度保养', 1, '2024-03-01 08:15:00', '公司（广州市黄埔区开发区科学城）', '113.5603', '23.1647', NULL, 0.00, 'public', NULL, '出发前往广州客户现场', NOW(), NOW()),
  (2, 'MT-2024-003', '质谱仪年度保养', 2, '2024-03-01 09:30:00', '广州市黄埔区科学城', '113.5295', '23.1647', 'photo10.jpg,photo11.jpg', 3.50, 'public', NULL, '到达客户现场', NOW(), NOW()),
  (2, 'MT-2024-003', '质谱仪年度保养', 3, '2024-03-01 18:45:00', '离开客户现场', '113.5297', '23.1649', 'photo12.jpg', 3.50, 'public', NULL, '保养完成返程', NOW(), NOW()); 