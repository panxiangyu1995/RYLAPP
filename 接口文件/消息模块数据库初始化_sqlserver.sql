-- 消息模块数据库设计
USE ryl_engineer;
GO

-- 聊天会话表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[chat_conversation]') AND type in (N'U'))
BEGIN
CREATE TABLE chat_conversation (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    conversation_id VARCHAR(36) NOT NULL, /* 会话ID（UUID） */
    conversation_type VARCHAR(20) NOT NULL, /* 会话类型（single-单聊，group-群聊） */
    name VARCHAR(100), /* 会话名称（群聊时使用） */
    avatar VARCHAR(255), /* 会话头像（群聊时使用） */
    creator_id BIGINT, /* 创建者ID */
    last_message_content VARCHAR(255), /* 最后一条消息内容 */
    last_message_time DATETIME2, /* 最后一条消息时间 */
    last_message_sender_id BIGINT, /* 最后一条消息发送者ID */
    member_count INT DEFAULT 0, /* 成员数量 */
    is_task_related TINYINT DEFAULT 0, /* 是否与任务相关（0-否，1-是） */
    related_task_id VARCHAR(20), /* 关联任务ID */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    status VARCHAR(20) DEFAULT 'active', /* 状态（active-活跃, deleted-已删除） */
    CONSTRAINT uk_conversation_id UNIQUE (conversation_id)
);

CREATE INDEX idx_creator_id ON chat_conversation(creator_id);
CREATE INDEX idx_conversation_type ON chat_conversation(conversation_type);
CREATE INDEX idx_related_task_id ON chat_conversation(related_task_id);
END
GO

-- 会话成员表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[chat_conversation_member]') AND type in (N'U'))
BEGIN
CREATE TABLE chat_conversation_member (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    conversation_id VARCHAR(36) NOT NULL, /* 会话ID */
    user_id BIGINT NOT NULL, /* 用户ID */
    nickname VARCHAR(50), /* 在会话中的昵称 */
    role VARCHAR(20) DEFAULT 'member', /* 角色（owner-群主，admin-管理员，member-成员） */
    unread_count INT DEFAULT 0, /* 未读消息数 */
    is_mute TINYINT DEFAULT 0, /* 是否静音（0-否，1-是） */
    is_sticky TINYINT DEFAULT 0, /* 是否置顶（0-否，1-是） */
    join_time DATETIME2 DEFAULT GETDATE(), /* 加入时间 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_conversation_user UNIQUE (conversation_id, user_id),
    CONSTRAINT FK_chat_conversation_member_user FOREIGN KEY (user_id) REFERENCES [user](id)
);

CREATE INDEX idx_conversation_member_conversation_id ON chat_conversation_member(conversation_id);
CREATE INDEX idx_conversation_member_user_id ON chat_conversation_member(user_id);
END
GO

-- 聊天消息表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[chat_message]') AND type in (N'U'))
BEGIN
CREATE TABLE chat_message (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    message_id VARCHAR(36) NOT NULL, /* 消息ID（UUID） */
    conversation_id VARCHAR(36) NOT NULL, /* 会话ID */
    sender_id BIGINT NOT NULL, /* 发送者ID */
    content NVARCHAR(MAX), /* 消息内容 */
    content_type VARCHAR(20) DEFAULT 'text', /* 内容类型（text-文本，image-图片，file-文件，audio-语音，video-视频） */
    send_time DATETIME2 DEFAULT GETDATE(), /* 发送时间 */
    status VARCHAR(20) DEFAULT 'sent', /* 状态（sending-发送中，sent-已发送，delivered-已送达，read-已读） */
    reply_to_message_id VARCHAR(36), /* 回复的消息ID */
    is_deleted TINYINT DEFAULT 0, /* 是否已删除（0-否，1-是） */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    CONSTRAINT uk_message_id UNIQUE (message_id),
    CONSTRAINT FK_chat_message_sender FOREIGN KEY (sender_id) REFERENCES [user](id)
);

CREATE INDEX idx_message_conversation_id ON chat_message(conversation_id);
CREATE INDEX idx_message_sender_id ON chat_message(sender_id);
CREATE INDEX idx_message_send_time ON chat_message(send_time);
END
GO

-- 消息已读表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[chat_message_read]') AND type in (N'U'))
BEGIN
CREATE TABLE chat_message_read (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    message_id VARCHAR(36) NOT NULL, /* 消息ID */
    user_id BIGINT NOT NULL, /* 已读用户ID */
    read_time DATETIME2 DEFAULT GETDATE(), /* 已读时间 */
    CONSTRAINT uk_message_read_user UNIQUE (message_id, user_id),
    CONSTRAINT FK_chat_message_read_user FOREIGN KEY (user_id) REFERENCES [user](id)
);

CREATE INDEX idx_message_read_message_id ON chat_message_read(message_id);
CREATE INDEX idx_message_read_user_id ON chat_message_read(user_id);
END
GO

-- 系统公告表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[system_announcement]') AND type in (N'U'))
BEGIN
CREATE TABLE system_announcement (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    title VARCHAR(100) NOT NULL, /* 公告标题 */
    content NVARCHAR(MAX) NOT NULL, /* 公告内容 */
    sender_id BIGINT NOT NULL, /* 发布人ID */
    sender_name VARCHAR(50) NOT NULL, /* 发布人姓名 */
    importance VARCHAR(10) DEFAULT 'normal', /* 重要程度（normal-普通, important-重要, urgent-紧急） */
    status VARCHAR(20) DEFAULT 'active', /* 状态（active-活跃, inactive-非活跃） */
    start_time DATETIME2 NOT NULL, /* 生效时间 */
    end_time DATETIME2, /* 结束时间（NULL表示永久有效） */
    is_popup TINYINT DEFAULT 0, /* 是否弹窗显示（0-否, 1-是） */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT FK_system_announcement_sender FOREIGN KEY (sender_id) REFERENCES [user](id)
);

CREATE INDEX idx_sender_id_announcement ON system_announcement(sender_id);
CREATE INDEX idx_status_announcement ON system_announcement(status);
CREATE INDEX idx_start_time ON system_announcement(start_time);
CREATE INDEX idx_end_time ON system_announcement(end_time);
END
GO

-- 系统公告阅读表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[announcement_read]') AND type in (N'U'))
BEGIN
CREATE TABLE announcement_read (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    announcement_id BIGINT NOT NULL, /* 公告ID */
    user_id BIGINT NOT NULL, /* 用户ID */
    read_time DATETIME2 DEFAULT GETDATE(), /* 阅读时间 */
    CONSTRAINT uk_announcement_user UNIQUE (announcement_id, user_id),
    CONSTRAINT FK_announcement_read_announcement FOREIGN KEY (announcement_id) REFERENCES system_announcement(id),
    CONSTRAINT FK_announcement_read_user FOREIGN KEY (user_id) REFERENCES [user](id)
);

CREATE INDEX idx_announcement_id ON announcement_read(announcement_id);
CREATE INDEX idx_user_id_announcement_read ON announcement_read(user_id);
END
GO

-- 协助请求表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[assistance_request]') AND type in (N'U'))
BEGIN
CREATE TABLE assistance_request (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    request_id VARCHAR(30) NOT NULL, /* 请求ID */
    requester_id BIGINT NOT NULL, /* 请求者ID */
    engineer_id BIGINT NOT NULL, /* 工程师ID */
    task_id VARCHAR(20), /* 相关任务ID */
    urgency VARCHAR(10) DEFAULT 'medium', /* 紧急程度（low-低, medium-中, high-高） */
    status VARCHAR(20) DEFAULT 'pending', /* 状态（pending-待处理, accepted-已接受, rejected-已拒绝, cancelled-已取消, completed-已完成） */
    description NVARCHAR(MAX) NOT NULL, /* 问题描述 */
    response NVARCHAR(MAX), /* 回复内容 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    handle_time DATETIME2, /* 处理时间 */
    CONSTRAINT uk_request_id UNIQUE (request_id),
    CONSTRAINT FK_assistance_request_requester FOREIGN KEY (requester_id) REFERENCES [user](id),
    CONSTRAINT FK_assistance_request_engineer FOREIGN KEY (engineer_id) REFERENCES [user](id)
);

CREATE INDEX idx_requester_id_request ON assistance_request(requester_id);
CREATE INDEX idx_engineer_id ON assistance_request(engineer_id);
CREATE INDEX idx_task_id_assistance ON assistance_request(task_id);
CREATE INDEX idx_status_assistance ON assistance_request(status);
END
GO

-- 协助请求接收者表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[assistance_request_recipient]') AND type in (N'U'))
BEGIN
CREATE TABLE assistance_request_recipient (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    request_id BIGINT NOT NULL, /* 请求ID */
    recipient_id BIGINT NOT NULL, /* 接收者ID */
    is_read TINYINT DEFAULT 0, /* 是否已读（0-未读，1-已读） */
    read_time DATETIME2, /* 阅读时间 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_request_recipient UNIQUE (request_id, recipient_id),
    CONSTRAINT FK_assistance_request_recipient_request FOREIGN KEY (request_id) REFERENCES assistance_request(id),
    CONSTRAINT FK_assistance_request_recipient_recipient FOREIGN KEY (recipient_id) REFERENCES [user](id)
);

CREATE INDEX idx_request_id_recipient ON assistance_request_recipient(request_id);
CREATE INDEX idx_recipient_id ON assistance_request_recipient(recipient_id);
END
GO

-- 插入初始测试数据
-- 聊天会话数据
IF NOT EXISTS (SELECT * FROM chat_conversation WHERE conversation_id = '11111111-1111-1111-1111-111111111111')
BEGIN
    INSERT INTO chat_conversation (conversation_id, conversation_type, name, create_time) VALUES
    ('11111111-1111-1111-1111-111111111111', 'single', NULL, DATEADD(DAY, -7, GETDATE())),
    ('22222222-2222-2222-2222-222222222222', 'single', NULL, DATEADD(DAY, -5, GETDATE())),
    ('33333333-3333-3333-3333-333333333333', 'group', '技术支持群', DATEADD(DAY, -3, GETDATE()));
END
GO

-- 会话成员数据
IF NOT EXISTS (SELECT * FROM chat_conversation_member WHERE conversation_id = '11111111-1111-1111-1111-111111111111' AND user_id = 2)
BEGIN
    INSERT INTO chat_conversation_member (conversation_id, user_id, role) VALUES
    ('11111111-1111-1111-1111-111111111111', 2, 'member'),
    ('11111111-1111-1111-1111-111111111111', 3, 'member'),
    ('22222222-2222-2222-2222-222222222222', 2, 'member'),
    ('22222222-2222-2222-2222-222222222222', 4, 'member'),
    ('33333333-3333-3333-3333-333333333333', 2, 'owner'),
    ('33333333-3333-3333-3333-333333333333', 3, 'member'),
    ('33333333-3333-3333-3333-333333333333', 4, 'member');
END
GO

-- 聊天消息数据
IF NOT EXISTS (SELECT * FROM chat_message WHERE message_id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa')
BEGIN
    INSERT INTO chat_message (message_id, conversation_id, sender_id, content, content_type, send_time, status) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 2, '你好，有关于任务RP-2024-001的问题', 'text', DATEADD(DAY, -7, GETDATE()), 'read'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '11111111-1111-1111-1111-111111111111', 3, '好的，请问有什么可以帮到你？', 'text', DATEADD(DAY, -7, DATEADD(MINUTE, 5, GETDATE())), 'read'),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 2, '明天的培训任务准备好了吗？', 'text', DATEADD(DAY, -5, GETDATE()), 'read'),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', '22222222-2222-2222-2222-222222222222', 4, '已经准备好了，资料已经发到你邮箱', 'text', DATEADD(DAY, -5, DATEADD(MINUTE, 10, GETDATE())), 'read'),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '33333333-3333-3333-3333-333333333333', 2, '大家好，这是技术支持群', 'text', DATEADD(DAY, -3, GETDATE()), 'read'),
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', '33333333-3333-3333-3333-333333333333', 3, '收到', 'text', DATEADD(DAY, -3, DATEADD(MINUTE, 2, GETDATE())), 'read'),
    ('gggggggg-gggg-gggg-gggg-gggggggggggg', '33333333-3333-3333-3333-333333333333', 4, '收到', 'text', DATEADD(DAY, -3, DATEADD(MINUTE, 5, GETDATE())), 'read');
END
GO

-- 消息已读数据
IF NOT EXISTS (SELECT * FROM chat_message_read WHERE message_id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa' AND user_id = 3)
BEGIN
    INSERT INTO chat_message_read (message_id, user_id, read_time) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 3, DATEADD(DAY, -7, DATEADD(MINUTE, 2, GETDATE()))),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 2, DATEADD(DAY, -7, DATEADD(MINUTE, 6, GETDATE()))),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 4, DATEADD(DAY, -5, DATEADD(MINUTE, 5, GETDATE()))),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', 2, DATEADD(DAY, -5, DATEADD(MINUTE, 12, GETDATE()))),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 3, DATEADD(DAY, -3, DATEADD(MINUTE, 1, GETDATE()))),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 4, DATEADD(DAY, -3, DATEADD(MINUTE, 3, GETDATE()))),
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', 2, DATEADD(DAY, -3, DATEADD(MINUTE, 3, GETDATE()))),
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', 4, DATEADD(DAY, -3, DATEADD(MINUTE, 4, GETDATE()))),
    ('gggggggg-gggg-gggg-gggg-gggggggggggg', 2, DATEADD(DAY, -3, DATEADD(MINUTE, 6, GETDATE()))),
    ('gggggggg-gggg-gggg-gggg-gggggggggggg', 3, DATEADD(DAY, -3, DATEADD(MINUTE, 7, GETDATE())));
END
GO

-- 更新会话最后一条消息
IF EXISTS (SELECT * FROM chat_conversation WHERE conversation_id = '11111111-1111-1111-1111-111111111111')
BEGIN
    UPDATE chat_conversation SET 
        last_message_content = '好的，请问有什么可以帮到你？',
        last_message_time = DATEADD(DAY, -7, DATEADD(MINUTE, 5, GETDATE())),
        last_message_sender_id = 3
    WHERE conversation_id = '11111111-1111-1111-1111-111111111111';
END
GO

IF EXISTS (SELECT * FROM chat_conversation WHERE conversation_id = '22222222-2222-2222-2222-222222222222')
BEGIN
    UPDATE chat_conversation SET 
        last_message_content = '已经准备好了，资料已经发到你邮箱',
        last_message_time = DATEADD(DAY, -5, DATEADD(MINUTE, 10, GETDATE())),
        last_message_sender_id = 4
    WHERE conversation_id = '22222222-2222-2222-2222-222222222222';
END
GO

IF EXISTS (SELECT * FROM chat_conversation WHERE conversation_id = '33333333-3333-3333-3333-333333333333')
BEGIN
    UPDATE chat_conversation SET 
        last_message_content = '收到',
        last_message_time = DATEADD(DAY, -3, DATEADD(MINUTE, 5, GETDATE())),
        last_message_sender_id = 4
    WHERE conversation_id = '33333333-3333-3333-3333-333333333333';
END
GO

-- 添加系统公告
IF NOT EXISTS (SELECT * FROM system_announcement WHERE title = '系统维护通知')
BEGIN
    INSERT INTO system_announcement (title, content, sender_id, sender_name, importance, status, start_time, end_time, is_popup) VALUES
    ('系统维护通知', '系统将于2024年6月10日凌晨2:00-4:00进行例行维护，届时系统将暂停服务，请提前做好工作安排。', 1, '系统管理员', 'normal', 'active', '2024-06-01 00:00:00', '2024-06-10 04:00:00', 0),
    ('新功能上线公告', '设备检修模块已上线，新增设备档案、检修记录、报警提示等功能，欢迎使用并提供反馈意见。', 1, '系统管理员', 'important', 'active', '2024-06-02 00:00:00', NULL, 1),
    ('安全警示', '近期有不法分子以系统技术支持名义进行诈骗，请各位同事提高警惕，不要泄露个人账号密码信息。', 1, '系统管理员', 'urgent', 'active', '2024-06-03 00:00:00', '2024-07-03 00:00:00', 1);
END
GO

-- 添加公告阅读记录
IF NOT EXISTS (SELECT * FROM announcement_read WHERE announcement_id = 1 AND user_id = 2)
BEGIN
    INSERT INTO announcement_read (announcement_id, user_id, read_time) VALUES
    (1, 2, DATEADD(DAY, -1, GETDATE())),
    (1, 3, DATEADD(DAY, -1, GETDATE())),
    (2, 2, DATEADD(HOUR, -12, GETDATE())),
    (3, 4, DATEADD(HOUR, -6, GETDATE()));
END
GO

-- 添加初始协助请求
IF NOT EXISTS (SELECT * FROM assistance_request WHERE request_id = 'REQ20240601001')
BEGIN
    INSERT INTO assistance_request (request_id, requester_id, engineer_id, task_id, urgency, status, description, create_time) VALUES
    ('REQ20240601001', 2, 3, 'RP-2024-001', 'high', 'pending', '需要协助处理气相色谱仪电源模块故障，客户现场急需', DATEADD(HOUR, -6, GETDATE())),
    ('REQ20240601002', 3, 2, 'MT-2024-001', 'medium', 'accepted', '请协助确认液相色谱仪所需的保养配件清单', DATEADD(HOUR, -12, GETDATE()));
END
GO