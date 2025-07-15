-- 联系人模块数据库设计
USE ryl_engineer;
GO

-- 用户状态表（用于工程师状态显示）
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[user_status]') AND type in (N'U'))
BEGIN
CREATE TABLE user_status (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL, /* 用户ID */
    status VARCHAR(20) NOT NULL DEFAULT 'offline', /* 状态（online/busy/offline） */
    last_active_time DATETIME2 DEFAULT GETDATE(), /* 最后活跃时间 */
    current_task_id VARCHAR(20), /* 当前任务ID */
    current_task_name VARCHAR(100), /* 当前任务名称 */
    task_type VARCHAR(20), /* 当前任务类型（repair/maintenance/training等） */
    task_status VARCHAR(20), /* 当前任务状态（pending/in-progress/completed等） */
    task_priority VARCHAR(10), /* 当前任务优先级（low/normal/high） */
    task_deadline DATETIME2, /* 当前任务截止时间 */
    task_customer_id BIGINT, /* 任务关联的客户ID */
    task_customer_name VARCHAR(100), /* 任务关联的客户名称 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_user_status_user_id UNIQUE (user_id),
    CONSTRAINT FK_user_status_user FOREIGN KEY (user_id) REFERENCES [user](id)
);

CREATE INDEX idx_user_status_status ON user_status(status);
CREATE INDEX idx_user_status_task_id ON user_status(current_task_id);
CREATE INDEX idx_user_status_task_type ON user_status(task_type);
CREATE INDEX idx_user_status_task_status ON user_status(task_status);
END
GO

-- 工程师协作关系表（记录工程师之间的协作关系）
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[engineer_collaboration]') AND type in (N'U'))
BEGIN
CREATE TABLE engineer_collaboration (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    engineer_id BIGINT NOT NULL, /* 工程师ID */
    collaborator_id BIGINT NOT NULL, /* 协作工程师ID */
    relation_type VARCHAR(20) NOT NULL, /* 协作关系类型（mentor/teammate/trainee等） */
    collaboration_count INT DEFAULT 0, /* 协作次数 */
    last_collaboration_task_id VARCHAR(20), /* 最近协作的任务ID */
    last_collaboration_time DATETIME2, /* 最近协作时间 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_engineer_collaborator UNIQUE (engineer_id, collaborator_id),
    CONSTRAINT FK_engineer_collaboration_engineer FOREIGN KEY (engineer_id) REFERENCES [user](id),
    CONSTRAINT FK_engineer_collaboration_collaborator FOREIGN KEY (collaborator_id) REFERENCES [user](id)
);

CREATE INDEX idx_engineer_id ON engineer_collaboration(engineer_id);
CREATE INDEX idx_collaborator_id ON engineer_collaboration(collaborator_id);
CREATE INDEX idx_relation_type ON engineer_collaboration(relation_type);
END
GO

-- 用户客户关系表（用于客户管理权限控制）
-- 首先检查customer表是否存在
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[customer]') AND type in (N'U'))
BEGIN
    IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[user_customer_relation]') AND type in (N'U'))
    BEGIN
        CREATE TABLE user_customer_relation (
            id BIGINT IDENTITY(1,1) PRIMARY KEY,
            user_id BIGINT NOT NULL, /* 用户ID */
            customer_id BIGINT NOT NULL, /* 客户ID */
            relation_type VARCHAR(20) NOT NULL, /* 关系类型（owner-负责人, engineer-工程师, sales-销售） */
            is_creator TINYINT DEFAULT 0, /* 是否为创建者（0-否，1-是） */
            create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
            update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
            CONSTRAINT uk_user_customer UNIQUE (user_id, customer_id),
            CONSTRAINT FK_user_customer_relation_user FOREIGN KEY (user_id) REFERENCES [user](id),
            CONSTRAINT FK_user_customer_relation_customer FOREIGN KEY (customer_id) REFERENCES customer(id)
        );

        CREATE INDEX idx_user_customer_relation_user_id ON user_customer_relation(user_id);
        CREATE INDEX idx_user_customer_relation_customer_id ON user_customer_relation(customer_id);
        CREATE INDEX idx_user_customer_relation_type ON user_customer_relation(relation_type);
        CREATE INDEX idx_user_customer_is_creator ON user_customer_relation(is_creator);
    END
END
ELSE
BEGIN
    PRINT '警告: customer表不存在，请先执行combined_tables_sqlserver.sql脚本创建客户表'
END
GO

-- 插入初始测试数据
-- 用户状态数据
IF NOT EXISTS (SELECT * FROM user_status WHERE user_id = 2)
BEGIN
    INSERT INTO user_status (user_id, status, last_active_time, current_task_id, current_task_name, task_type, task_status, task_priority, task_deadline, task_customer_id, task_customer_name) VALUES
    (2, 'online', GETDATE(), 'RP-2024-001', '气相色谱仪维修', 'repair', 'in-progress', 'high', '2024-05-25 18:00:00', 1, '上海医疗器械有限公司'),
    (3, 'busy', DATEADD(MINUTE, -30, GETDATE()), 'MT-2024-001', '液相色谱仪保养', 'maintenance', 'pending', 'normal', '2024-05-23 18:00:00', 2, '北京科研仪器研究所'),
    (4, 'offline', DATEADD(HOUR, -2, GETDATE()), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
END
GO

-- 工程师协作关系数据
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[engineer_collaboration]') AND type in (N'U'))
BEGIN
    PRINT '警告: engineer_collaboration表不存在，无法插入示例数据'
END
ELSE
BEGIN
    IF NOT EXISTS (SELECT * FROM engineer_collaboration WHERE engineer_id = 2 AND collaborator_id = 3)
    BEGIN
        INSERT INTO engineer_collaboration (engineer_id, collaborator_id, relation_type, collaboration_count, last_collaboration_task_id, last_collaboration_time) VALUES
        (2, 3, 'teammate', 5, 'RP-2024-001', DATEADD(DAY, -1, GETDATE())),
        (2, 4, 'mentor', 2, 'MT-2023-010', DATEADD(MONTH, -1, GETDATE())),
        (3, 2, 'teammate', 5, 'RP-2024-001', DATEADD(DAY, -1, GETDATE())),
        (4, 2, 'trainee', 2, 'MT-2023-010', DATEADD(MONTH, -1, GETDATE()));
    END
END
GO

-- 用户客户关系数据
-- 检查customer表和user_customer_relation表是否都存在
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[user_customer_relation]') AND type in (N'U'))
BEGIN
    -- 检查customer表是否存在
    IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[customer]') AND type in (N'U'))
    BEGIN
        -- 检查customer表中是否有相应的记录
        DECLARE @customer1Exists BIT = 0, @customer2Exists BIT = 0, @customer3Exists BIT = 0
        
        IF EXISTS (SELECT * FROM customer WHERE id = 1) SET @customer1Exists = 1
        IF EXISTS (SELECT * FROM customer WHERE id = 2) SET @customer2Exists = 1
        IF EXISTS (SELECT * FROM customer WHERE id = 3) SET @customer3Exists = 1
        
        -- 示例数据：客户1由销售（用户4）创建并负责，工程师（用户2）提供支持
        IF @customer1Exists = 1 
        BEGIN
            IF NOT EXISTS (SELECT * FROM user_customer_relation WHERE user_id = 4 AND customer_id = 1)
                INSERT INTO user_customer_relation (user_id, customer_id, relation_type, is_creator) VALUES 
                (4, 1, 'sales', 1);    -- 销售创建并负责客户1
                
            IF NOT EXISTS (SELECT * FROM user_customer_relation WHERE user_id = 2 AND customer_id = 1)
                INSERT INTO user_customer_relation (user_id, customer_id, relation_type, is_creator) VALUES 
                (2, 1, 'engineer', 0); -- 工程师负责客户1的技术支持
        END
        
        -- 示例数据：客户2由工程师（用户3）创建并负责，销售（用户4）分配为销售负责人
        IF @customer2Exists = 1 
        BEGIN
            IF NOT EXISTS (SELECT * FROM user_customer_relation WHERE user_id = 3 AND customer_id = 2)
                INSERT INTO user_customer_relation (user_id, customer_id, relation_type, is_creator) VALUES 
                (3, 2, 'engineer', 1);  -- 工程师创建并负责客户2
                
            IF NOT EXISTS (SELECT * FROM user_customer_relation WHERE user_id = 4 AND customer_id = 2)
                INSERT INTO user_customer_relation (user_id, customer_id, relation_type, is_creator) VALUES 
                (4, 2, 'sales', 0);     -- 销售负责客户2
        END
        
        -- 示例数据：客户3由系统管理员创建，尚未分配销售和工程师
        IF @customer3Exists = 1 
        BEGIN
            IF NOT EXISTS (SELECT * FROM user_customer_relation WHERE user_id = 1 AND customer_id = 3)
                INSERT INTO user_customer_relation (user_id, customer_id, relation_type, is_creator) VALUES 
                (1, 3, 'admin', 1);     -- 管理员创建客户3，尚未分配
        END
    END
    ELSE
    BEGIN
        PRINT '警告: customer表不存在，无法插入user_customer_relation数据。请先执行combined_tables_sqlserver.sql脚本。'
    END
END 