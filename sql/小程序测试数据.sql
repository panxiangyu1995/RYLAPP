-- 瑞屹林小程序测试数据
-- 描述: 为微信小程序测试环境准备示例数据
-- 注意: 执行此脚本前，确保已初始化数据库结构

USE ryl_engineer;
GO

-- 清理之前的测试数据（如果需要）
-- 注意：以下部分在初次导入数据时可以注释掉，如果需要重新导入数据，可以取消注释
/*
DELETE FROM record_image WHERE record_id IN (SELECT id FROM step_record WHERE task_id LIKE 'WX%');
DELETE FROM step_record WHERE task_id LIKE 'WX%';
DELETE FROM task_image WHERE task_id LIKE 'WX%';
DELETE FROM task_engineer WHERE task_id LIKE 'WX%';
DELETE FROM task_status_history WHERE task_id LIKE 'WX%';
DELETE FROM task_flow WHERE task_id LIKE 'WX%';
DELETE FROM task WHERE task_id LIKE 'WX%';
DELETE FROM device WHERE customer_id IN (SELECT id FROM customer WHERE wechat_openid LIKE 'test_openid%');
DELETE FROM customer WHERE wechat_openid LIKE 'test_openid%';
*/

-- 首先检查和修复可能存在的冲突
-- 检查工程师表中是否已存在测试用手机号，如果存在则先更新
IF EXISTS (SELECT * FROM [user] WHERE mobile = '13800138001')
BEGIN
    PRINT '检测到工程师手机号13800138001冲突，更新为新的手机号';
    UPDATE [user] SET mobile = '13800138901' WHERE mobile = '13800138001';
END

IF EXISTS (SELECT * FROM [user] WHERE mobile = '13900139001')
BEGIN
    PRINT '检测到工程师手机号13900139001冲突，更新为新的手机号';
    UPDATE [user] SET mobile = '13900139901' WHERE mobile = '13900139001';
END

-- 声明全局变量，用于整个脚本使用
DECLARE @engineer1_id BIGINT, @engineer2_id BIGINT
DECLARE @engineer1_name NVARCHAR(50), @engineer2_name NVARCHAR(50)
DECLARE @customerA_id BIGINT, @customerB_id BIGINT, @customerC_id BIGINT

-- 1. 创建测试客户数据（微信小程序用户）
-- 检查是否已存在测试用户
IF NOT EXISTS (SELECT * FROM customer WHERE wechat_openid = 'test_openid_123456')
BEGIN
    INSERT INTO customer (
        name, level, contact, phone, address, email, 
        wechat_openid, wechat_unionid, wechat_avatar_url
    ) VALUES 
    (
        '测试客户A', 'B', '张测试', '13800138000', '上海市浦东新区测试路123号', 'test_a@example.com',
        'test_openid_123456', 'test_unionid_123456', 'https://example.com/avatar.jpg'
    ),
    (
        '测试客户B', 'C', '李测试', '13900139000', '北京市海淀区测试路456号', 'test_b@example.com',
        'test_openid_234567', 'test_unionid_234567', 'https://example.com/avatar2.jpg'
    ),
    (
        '测试客户C', 'A', '王测试', '13700137000', '深圳市南山区测试路789号', 'test_c@example.com',
        'test_openid_345678', 'test_unionid_345678', 'https://example.com/avatar3.jpg'
    );
    
    PRINT '已插入3条测试客户数据';
END
ELSE
BEGIN
    PRINT '测试客户数据已存在，跳过客户数据创建';
END

-- 2. 创建测试设备数据
-- 获取客户ID
SELECT @customerA_id = id FROM customer WHERE wechat_openid = 'test_openid_123456';
SELECT @customerB_id = id FROM customer WHERE wechat_openid = 'test_openid_234567';
SELECT @customerC_id = id FROM customer WHERE wechat_openid = 'test_openid_345678';

-- 为每个客户创建测试设备
IF NOT EXISTS (SELECT * FROM device WHERE customer_id = @customerA_id AND device_name = '显微镜')
BEGIN
    -- 客户A的设备
    INSERT INTO device (
        customer_id, device_name, device_type, brand, model, 
        quantity, fault_description
    ) VALUES
    (
        @customerA_id, '显微镜', '光学设备', '蔡司', 'Primo Star', 
        1, '对焦系统失灵'
    ),
    (
        @customerA_id, '离心机', '实验设备', 'Thermo', 'SL16R', 
        1, NULL
    );
    
    -- 客户B的设备
    INSERT INTO device (
        customer_id, device_name, device_type, brand, model,
        quantity, appointment_time, fault_description
    ) VALUES
    (
        @customerB_id, '质谱仪', '分析设备', 'Thermo', 'Orbitrap', 
        1, '2023-12-15 14:00:00', NULL
    );
    
    -- 客户C的设备
    INSERT INTO device (
        customer_id, device_name, device_type, brand, model,
        quantity, verification_type
    ) VALUES
    (
        @customerC_id, '高效液相色谱仪', '分析设备', '安捷伦', '1260 Infinity II',
        1, '性能验证'
    ),
    (
        @customerC_id, '质谱联用仪', '分析设备', '安捷伦', '6495C',
        1, NULL
    );
    
    PRINT '已插入5条测试设备数据';
END
ELSE
BEGIN
    PRINT '测试设备数据已存在，跳过设备数据创建';
END

-- 3. 创建测试工程师数据
-- 先检查是否已存在测试工程师
IF NOT EXISTS (SELECT * FROM [user] WHERE name = '张工程师')
BEGIN
    INSERT INTO [user] (
        work_id, name, mobile, password, department, location, status
    ) VALUES
    (
        'WX001', '张工程师', '13800138001', 
        '123456', '技术部', '上海', 1
    ),
    (
        'WX002', '李工程师', '13900139001', 
        '123456', '技术部', '北京', 1
    );
    
    PRINT '已插入2条测试工程师数据';
END
ELSE
BEGIN
    PRINT '测试工程师数据已存在，跳过工程师数据创建';
END

-- 获取工程师ID和姓名
SELECT @engineer1_id = id, @engineer1_name = name FROM [user] WHERE work_id = 'WX001';
SELECT @engineer2_id = id, @engineer2_name = name FROM [user] WHERE work_id = 'WX002';

-- 打印调试信息，确认变量获取成功
PRINT '工程师1 ID: ' + CAST(@engineer1_id AS NVARCHAR(20)) + ', 姓名: ' + @engineer1_name;
PRINT '工程师2 ID: ' + CAST(@engineer2_id AS NVARCHAR(20)) + ', 姓名: ' + @engineer2_name;
PRINT '客户A ID: ' + CAST(@customerA_id AS NVARCHAR(20));
PRINT '客户B ID: ' + CAST(@customerB_id AS NVARCHAR(20));
PRINT '客户C ID: ' + CAST(@customerC_id AS NVARCHAR(20));

-- 4. 创建测试任务数据
IF NOT EXISTS (SELECT * FROM task WHERE task_id = 'WX-RP-2023-001')
BEGIN
    -- 维修任务
    INSERT INTO task (
        task_id, title, task_type, customer_id, customer, customer_level,
        location, contact_person, contact_phone, priority, status,
        create_time, start_date, device_name, device_model, device_brand,
        fault_description, quantity, current_step
    ) VALUES
    (
        'WX-RP-2023-001', '维修显微镜', 'repair', @customerA_id, '测试客户A', 'B',
        '上海市浦东新区测试路123号', '张测试', '13800138000', 'high', 'in-progress',
        DATEADD(DAY, -3, GETDATE()), DATEADD(DAY, -2, GETDATE()), '显微镜', 'Primo Star', '蔡司',
        '对焦系统失灵', 1, 2
    );
    
    -- 保养任务
    INSERT INTO task (
        task_id, title, task_type, customer_id, customer, customer_level,
        location, contact_person, contact_phone, priority, status,
        create_time, device_name, device_model, device_brand,
        description, quantity, current_step
    ) VALUES
    (
        'WX-MT-2023-001', '离心机定期保养', 'maintenance', @customerA_id, '测试客户A', 'B',
        '上海市浦东新区测试路123号', '张测试', '13800138000', 'normal', 'pending',
        DATEADD(DAY, -1, GETDATE()), '离心机', 'SL16R', 'Thermo',
        '按照计划进行季度保养', 1, 1
    );
    
    -- 培训任务
    INSERT INTO task (
        task_id, title, task_type, customer_id, customer, customer_level,
        location, contact_person, contact_phone, priority, status,
        create_time, device_name, device_model, device_brand,
        description, quantity, appointment_time, current_step
    ) VALUES
    (
        'WX-TR-2023-001', '质谱仪使用培训', 'training', @customerB_id, '测试客户B', 'C',
        '北京市海淀区测试路456号', '李测试', '13900139000', 'normal', 'pending',
        DATEADD(DAY, -5, GETDATE()), '质谱仪', 'Orbitrap', 'Thermo',
        '需要安排人员进行质谱仪使用培训', 1, '2023-12-15 14:00:00', 0
    );
    
    -- 验证任务
    INSERT INTO task (
        task_id, title, task_type, customer_id, customer, customer_level,
        location, contact_person, contact_phone, priority, status,
        create_time, device_name, device_model, device_brand,
        description, quantity, verification_type, current_step
    ) VALUES
    (
        'WX-VR-2023-001', '高效液相色谱仪验证', 'verification', @customerC_id, '测试客户C', 'A',
        '深圳市南山区测试路789号', '王测试', '13700137000', 'high', 'completed',
        DATEADD(DAY, -10, GETDATE()), '高效液相色谱仪', '1260 Infinity II', '安捷伦',
        '需要进行仪器验证确保准确性', 1, '性能验证', 4
    );
    
    -- 选型任务
    INSERT INTO task (
        task_id, title, task_type, customer_id, customer, customer_level,
        location, contact_person, contact_phone, priority, status,
        create_time, device_name, device_model, device_brand,
        purpose, requirement_description, quantity, current_step
    ) VALUES
    (
        'WX-SL-2023-001', '实验室设备选型咨询', 'selection', @customerC_id, '测试客户C', 'A',
        '深圳市南山区测试路789号', '王测试', '13700137000', 'normal', 'in-progress',
        DATEADD(DAY, -15, GETDATE()), '质谱联用仪', '6495C', '安捷伦',
        '蛋白质组学研究', '需要高灵敏度、高分辨率的质谱仪，预算100万以内', 1, 2
    );
    
    PRINT '已插入5条测试任务数据';
END
ELSE
BEGIN
    PRINT '测试任务数据已存在，跳过任务数据创建';
END

-- 5. 创建任务工程师关联
IF NOT EXISTS (SELECT * FROM task_engineer WHERE task_id = 'WX-RP-2023-001')
BEGIN
    -- 确保工程师ID不为NULL
    IF @engineer1_id IS NOT NULL AND @engineer2_id IS NOT NULL
    BEGIN
        INSERT INTO task_engineer (task_id, engineer_id, is_owner, create_time) VALUES
        ('WX-RP-2023-001', @engineer1_id, 1, GETDATE()),
        ('WX-MT-2023-001', @engineer1_id, 1, GETDATE()),
        ('WX-TR-2023-001', @engineer2_id, 1, GETDATE()),
        ('WX-VR-2023-001', @engineer2_id, 1, GETDATE()),
        ('WX-SL-2023-001', @engineer1_id, 1, GETDATE()),
        ('WX-SL-2023-001', @engineer2_id, 0, GETDATE());
        
        PRINT '已插入6条任务工程师关联数据';
    END
    ELSE
    BEGIN
        PRINT '错误：工程师ID为NULL，跳过任务工程师关联数据创建';
        PRINT '工程师1 ID: ' + ISNULL(CAST(@engineer1_id AS NVARCHAR(20)), 'NULL');
        PRINT '工程师2 ID: ' + ISNULL(CAST(@engineer2_id AS NVARCHAR(20)), 'NULL');
    END
END
ELSE
BEGIN
    PRINT '任务工程师关联数据已存在，跳过关联数据创建';
END

-- 6. 创建任务流程数据
IF NOT EXISTS (SELECT * FROM task_flow WHERE task_id = 'WX-RP-2023-001')
BEGIN
    INSERT INTO task_flow (task_id, task_type, current_step, create_time, update_time) VALUES
    ('WX-RP-2023-001', 'repair', 2, DATEADD(DAY, -3, GETDATE()), DATEADD(DAY, -1, GETDATE())),
    ('WX-MT-2023-001', 'maintenance', 1, DATEADD(DAY, -1, GETDATE()), DATEADD(DAY, -1, GETDATE())),
    ('WX-TR-2023-001', 'training', 0, DATEADD(DAY, -5, GETDATE()), DATEADD(DAY, -5, GETDATE())),
    ('WX-VR-2023-001', 'verification', 4, DATEADD(DAY, -10, GETDATE()), DATEADD(DAY, -2, GETDATE())),
    ('WX-SL-2023-001', 'selection', 2, DATEADD(DAY, -15, GETDATE()), DATEADD(DAY, -3, GETDATE()));
    
    PRINT '已插入5条任务流程数据';
END
ELSE
BEGIN
    PRINT '任务流程数据已存在，跳过流程数据创建';
END

-- 7. 创建任务步骤记录
IF NOT EXISTS (SELECT * FROM step_record WHERE task_id = 'WX-RP-2023-001')
BEGIN
    -- 确保工程师ID和姓名不为NULL
    IF @engineer1_id IS NOT NULL AND @engineer2_id IS NOT NULL AND @engineer1_name IS NOT NULL AND @engineer2_name IS NOT NULL
    BEGIN
        -- 维修任务步骤
        INSERT INTO step_record (
            task_id, step_id, step_index, description, 
            creator_id, creator_name, create_time
        ) VALUES
        -- 维修任务步骤
        ('WX-RP-2023-001', 1, 0, '客户提交了维修显微镜的请求', @engineer1_id, @engineer1_name, DATEADD(DAY, -3, GETDATE())),
        ('WX-RP-2023-001', 2, 1, '工程师已接受任务，安排上门维修', @engineer1_id, @engineer1_name, DATEADD(DAY, -2, GETDATE())),
        ('WX-RP-2023-001', 3, 2, '工程师已到达现场，正在检查设备故障', @engineer1_id, @engineer1_name, DATEADD(DAY, -1, GETDATE())),
        
        -- 保养任务步骤
        ('WX-MT-2023-001', 1, 0, '客户提交了离心机定期保养的请求', @engineer1_id, @engineer1_name, DATEADD(DAY, -1, GETDATE())),
        ('WX-MT-2023-001', 2, 1, '工程师已接受任务，等待安排时间', @engineer1_id, @engineer1_name, DATEADD(DAY, -1, GETDATE())),
        
        -- 培训任务步骤  
        ('WX-TR-2023-001', 1, 0, '客户提交了质谱仪使用培训的请求', @engineer2_id, @engineer2_name, DATEADD(DAY, -5, GETDATE())),
        
        -- 验证任务步骤
        ('WX-VR-2023-001', 1, 0, '客户提交了高效液相色谱仪验证的请求', @engineer2_id, @engineer2_name, DATEADD(DAY, -10, GETDATE())),
        ('WX-VR-2023-001', 2, 1, '工程师已接受任务，安排验证时间', @engineer2_id, @engineer2_name, DATEADD(DAY, -9, GETDATE())),
        ('WX-VR-2023-001', 3, 2, '工程师已到达现场，开始进行验证', @engineer2_id, @engineer2_name, DATEADD(DAY, -5, GETDATE())),
        ('WX-VR-2023-001', 4, 3, '验证工作进行中，已完成50%', @engineer2_id, @engineer2_name, DATEADD(DAY, -3, GETDATE())),
        ('WX-VR-2023-001', 5, 4, '验证工作已完成，设备性能良好', @engineer2_id, @engineer2_name, DATEADD(DAY, -2, GETDATE())),
        
        -- 选型任务步骤
        ('WX-SL-2023-001', 1, 0, '客户提交了设备选型咨询的请求', @engineer1_id, @engineer1_name, DATEADD(DAY, -15, GETDATE())),
        ('WX-SL-2023-001', 2, 1, '工程师已接受任务，正在研究需求', @engineer1_id, @engineer1_name, DATEADD(DAY, -14, GETDATE())),
        ('WX-SL-2023-001', 3, 2, '工程师正在比对多款设备，准备建议方案', @engineer2_id, @engineer2_name, DATEADD(DAY, -3, GETDATE()));
        
        PRINT '已插入14条任务步骤记录';
    END
    ELSE
    BEGIN
        PRINT '错误：工程师ID或姓名为NULL，跳过任务步骤记录创建';
        PRINT '工程师1 ID: ' + ISNULL(CAST(@engineer1_id AS NVARCHAR(20)), 'NULL') + ', 姓名: ' + ISNULL(@engineer1_name, 'NULL');
        PRINT '工程师2 ID: ' + ISNULL(CAST(@engineer2_id AS NVARCHAR(20)), 'NULL') + ', 姓名: ' + ISNULL(@engineer2_name, 'NULL');
    END
END
ELSE
BEGIN
    PRINT '任务步骤记录已存在，跳过步骤记录创建';
END

-- 8. 创建任务状态历史
IF NOT EXISTS (SELECT * FROM task_status_history WHERE task_id = 'WX-RP-2023-001')
BEGIN
    -- 确保工程师ID和姓名不为NULL
    IF @engineer1_id IS NOT NULL AND @engineer2_id IS NOT NULL AND @engineer1_name IS NOT NULL AND @engineer2_name IS NOT NULL
    BEGIN
        INSERT INTO task_status_history (task_id, status, comment, updated_by, updated_by_name, timestamp) VALUES
        -- 维修任务状态历史
        ('WX-RP-2023-001', 'pending', '任务已创建，等待工程师接受', @engineer1_id, @engineer1_name, DATEADD(DAY, -3, GETDATE())),
        ('WX-RP-2023-001', 'in-progress', '工程师已接受任务，开始处理', @engineer1_id, @engineer1_name, DATEADD(DAY, -2, GETDATE())),
        
        -- 保养任务状态历史
        ('WX-MT-2023-001', 'pending', '任务已创建，等待工程师接受', @engineer1_id, @engineer1_name, DATEADD(DAY, -1, GETDATE())),
        
        -- 培训任务状态历史
        ('WX-TR-2023-001', 'pending', '任务已创建，等待工程师接受', @engineer2_id, @engineer2_name, DATEADD(DAY, -5, GETDATE())),
        
        -- 验证任务状态历史
        ('WX-VR-2023-001', 'pending', '任务已创建，等待工程师接受', @engineer2_id, @engineer2_name, DATEADD(DAY, -10, GETDATE())),
        ('WX-VR-2023-001', 'in-progress', '工程师已接受任务，开始处理', @engineer2_id, @engineer2_name, DATEADD(DAY, -9, GETDATE())),
        ('WX-VR-2023-001', 'completed', '任务已完成，验证结果良好', @engineer2_id, @engineer2_name, DATEADD(DAY, -2, GETDATE())),
        
        -- 选型任务状态历史
        ('WX-SL-2023-001', 'pending', '任务已创建，等待工程师接受', @engineer1_id, @engineer1_name, DATEADD(DAY, -15, GETDATE())),
        ('WX-SL-2023-001', 'in-progress', '工程师已接受任务，开始处理', @engineer1_id, @engineer1_name, DATEADD(DAY, -14, GETDATE()));
        
        PRINT '已插入9条任务状态历史记录';
    END
    ELSE
    BEGIN
        PRINT '错误：工程师ID或姓名为NULL，跳过任务状态历史记录创建';
        PRINT '工程师1 ID: ' + ISNULL(CAST(@engineer1_id AS NVARCHAR(20)), 'NULL') + ', 姓名: ' + ISNULL(@engineer1_name, 'NULL');
        PRINT '工程师2 ID: ' + ISNULL(CAST(@engineer2_id AS NVARCHAR(20)), 'NULL') + ', 姓名: ' + ISNULL(@engineer2_name, 'NULL');
    END
END
ELSE
BEGIN
    PRINT '任务状态历史记录已存在，跳过历史记录创建';
END

PRINT '所有微信小程序测试数据已创建完成!';
GO