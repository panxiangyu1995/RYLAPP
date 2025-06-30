-- 个人信息相关数据库设计
USE ryl_engineer;

-- 确保用户表存在并包含所需字段
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

-- 创建用户统计信息表
CREATE TABLE IF NOT EXISTS user_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    task_count INT DEFAULT 0 COMMENT '任务总数',
    completed_count INT DEFAULT 0 COMMENT '已完成任务数',
    total_score DECIMAL(5,2) DEFAULT 0 COMMENT '总评分',
    rating_count INT DEFAULT 0 COMMENT '评分次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户统计信息表';

-- 创建修改密码历史表
CREATE TABLE IF NOT EXISTS password_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    password VARCHAR(255) NOT NULL COMMENT '历史密码',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='密码历史表';

-- 创建登录日志表
CREATE TABLE IF NOT EXISTS login_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    login_time DATETIME NOT NULL COMMENT '登录时间',
    logout_time DATETIME COMMENT '登出时间',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    device_info VARCHAR(255) COMMENT '设备信息',
    status TINYINT DEFAULT 1 COMMENT '状态（0-失败，1-成功）',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    INDEX idx_user_id (user_id),
    INDEX idx_login_time (login_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- 插入用户统计信息示例数据
INSERT INTO user_stats (user_id, task_count, completed_count, total_score, rating_count)
SELECT id, 
       FLOOR(RAND() * 10) as task_count, 
       FLOOR(RAND() * 5) as completed_count, 
       ROUND(RAND() * 5, 1) as total_score, 
       FLOOR(RAND() * 10) as rating_count
FROM user
ON DUPLICATE KEY UPDATE 
    update_time = CURRENT_TIMESTAMP;

-- 添加用户头像默认路径更新
UPDATE user SET avatar = CONCAT('https://example.com/avatars/default_', id, '.png')
WHERE avatar IS NULL OR avatar = '';

-- 确保所有用户的token_expire字段有值（模拟数据）
UPDATE user SET 
    token = CONCAT('eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.', work_id),
    token_expire = DATE_ADD(NOW(), INTERVAL 7 DAY)
WHERE token IS NULL OR token = '';

-- 创建触发器：更新用户统计数据（完成率）
DELIMITER //
CREATE TRIGGER IF NOT EXISTS update_task_completion_rate
AFTER UPDATE ON task
FOR EACH ROW
BEGIN
    DECLARE engineer_id BIGINT;
    
    -- 如果任务状态变为已完成，更新相关工程师的统计信息
    IF NEW.status = 'completed' AND OLD.status != 'completed' THEN
        -- 查找此任务的工程师ID
        SELECT user_id INTO engineer_id
        FROM task_engineer
        WHERE task_id = NEW.task_id
        LIMIT 1;
        
        -- 更新工程师统计信息
        IF engineer_id IS NOT NULL THEN
            UPDATE user_stats
            SET completed_count = completed_count + 1,
                update_time = CURRENT_TIMESTAMP
            WHERE user_id = engineer_id;
        END IF;
    END IF;
END //
DELIMITER ;

-- 创建存储过程：定期刷新用户统计数据
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS refresh_user_stats()
BEGIN
    -- 对每个用户，重新计算任务总数和已完成任务数
    UPDATE user_stats us
    JOIN (
        SELECT te.user_id, 
               COUNT(DISTINCT t.task_id) AS total_tasks,
               SUM(CASE WHEN t.status = 'completed' THEN 1 ELSE 0 END) AS completed_tasks
        FROM task_engineer te
        JOIN task t ON te.task_id = t.task_id
        GROUP BY te.user_id
    ) stats ON us.user_id = stats.user_id
    SET us.task_count = stats.total_tasks,
        us.completed_count = stats.completed_tasks,
        us.update_time = CURRENT_TIMESTAMP;
END //
DELIMITER ; 