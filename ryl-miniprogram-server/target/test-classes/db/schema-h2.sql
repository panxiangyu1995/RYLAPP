-- 创建客户表
CREATE TABLE customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    openid VARCHAR(100) NOT NULL,
    unionid VARCHAR(100),
    nick_name VARCHAR(100),
    avatar_url VARCHAR(500),
    gender INT,
    country VARCHAR(100),
    province VARCHAR(100),
    city VARCHAR(100),
    language VARCHAR(50),
    phone VARCHAR(20),
    create_time DATETIME,
    update_time DATETIME,
    deleted INT DEFAULT 0
);

-- 创建任务表
CREATE TABLE task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    status INT DEFAULT 0,
    customer_id BIGINT NOT NULL,
    create_time DATETIME,
    update_time DATETIME,
    deleted INT DEFAULT 0
);

-- 创建任务评论表
CREATE TABLE task_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    create_time DATETIME,
    update_time DATETIME,
    deleted INT DEFAULT 0
); 