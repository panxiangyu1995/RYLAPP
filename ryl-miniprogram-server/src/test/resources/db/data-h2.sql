-- 插入测试客户数据
INSERT INTO customer (id, openid, nick_name, avatar_url, gender, country, province, city, language, phone, create_time, update_time)
VALUES 
(1, 'test_openid_1', '测试用户1', 'https://example.com/avatar1.png', 1, '中国', '广东', '深圳', 'zh_CN', '13800138001', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(2, 'test_openid_2', '测试用户2', 'https://example.com/avatar2.png', 2, '中国', '广东', '广州', 'zh_CN', '13800138002', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- 插入测试任务数据
INSERT INTO task (id, title, description, status, customer_id, create_time, update_time)
VALUES 
(1, '测试任务1', '这是测试任务1的内容', 0, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(2, '测试任务2', '这是测试任务2的内容', 1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(3, '测试任务3', '这是测试任务3的内容', 0, 2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- 插入测试任务评论数据
INSERT INTO task_comment (id, task_id, customer_id, content, create_time, update_time)
VALUES 
(1, 1, 1, '这是测试评论1', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(2, 1, 2, '这是测试评论2', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(3, 2, 1, '这是测试评论3', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()); 