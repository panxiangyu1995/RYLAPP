# 瑞屹林项目数据库初始化指南

## 核心理念：最终状态作为唯一真实来源

本指南旨在为开发人员提供一套清晰、准确的数据库设置流程。我们摒弃了繁杂的历史变更记录，直接展示了数据库在当前版本下**最终应达到的状态**。

这里提供的 `CREATE TABLE` 语句是所有初始化脚本和修复脚本叠加作用后的最终结果，您可以将其作为最权威的参考。数据库与Java实体类的字段映射关系也已经过严格校验，确保了与后端代码的完全一致。

## 数据库概览

- **系统**: SQL Server
- **数据库名称**: `ryl_engineer`

**核心说明**: 本项目的用户体系是统一的，小程序的用户信息直接存储在客户表 (`customer`) 中，不区分普通用户、管理员或工程师角色。

## 初始化脚本执行流程

要完整地初始化数据库，请遵循以下两个阶段的顺序。

### 阶段一：基础架构初始化

这些脚本负责创建项目所需的核心表结构和基础数据。请**严格按照文件名编号顺序**执行。

1.  `1用户模块数据库初始化_sqlserver.sql`
2.  `2先执行这个再执行联系人模块.sql`
3.  `联系人模块数据库初始化_sqlserver.sql`
4.  `任务模块数据库初始化_sqlserver.sql`
5.  `消息模块数据库初始化_sqlserver.sql`
6.  `仓库模块数据库初始化_sqlserver.sql`

### 阶段二：应用功能增强与修复

在基础架构之上，这些脚本为特定的应用程序功能（如微信小程序登录、账号密码体系）添加了必要的字段，并修正了在开发过程中发现的表结构问题。

**建议按以下顺序执行**，以确保所有依赖关系都得到满足：

1.  **`customer_table_complete_fix.sql`**: 为 `customer` 表全面添加小程序所需的字段（如 openid, nickname, avatar_url 等）。这是一个复合脚本，包含了早期多个修复脚本的功能。
2.  **`add_account_password_fields.sql`**: 为 `customer` 表添加 `username` 和 `password` 字段，以支持传统的账号密码登录功能。
3.  **`add_device_type_to_task.sql`**: 为 `task` 表添加 `device_type` 字段，以支持按仪器类型派发任务。
4.  **`add_technical_category_to_user.sql`**: 为 `user` 表添加 `technical_category` 字段，以支持按工程师技术能力派发任务。
5.  **`task_id_field_fix.sql`**: 修正 `task` 相关表中 `task_id` 字段的类型和约束，以解决外键关联问题。
6.  **`task_image_update.sql`**: 更新 `task_image` 表结构，使其更符合业务需求。
7.  **`fix_username_to_contact.sql`**: (可选) 一个修复脚本，用于将旧数据从 `username` 字段迁移到 `contact` 字段。对于新初始化的数据库，此脚本通常不是必需的。
8.  **`小程序测试数据.sql`**: (可选) 插入一套完整的测试数据，包括客户、设备、任务等，便于开发和调试。

## 重要数据表最终结构

以下是核心数据表在执行完**所有**上述脚本后的最终定义。

### 用户表 (user)

APP内部员工表（工程师、销售、管理员等）。

```sql
CREATE TABLE [user] (
    [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
    [work_id] VARCHAR(50) NOT NULL,                    -- 工号
    [name] VARCHAR(50) NOT NULL,                       -- 姓名
    [mobile] VARCHAR(20) NOT NULL,                     -- 手机号
    [password] VARCHAR(255) NOT NULL,                  -- 密码
    [department] VARCHAR(50),                          -- 部门
    [location] VARCHAR(50),                            -- 工作地点
    [avatar] VARCHAR(255),                             -- 头像URL
    [status] TINYINT DEFAULT 1,                        -- 状态（0-禁用，1-启用）
    [technical_category] VARCHAR(255) NULL,            -- 技术分类 (新增，逗号分隔)
    [token] VARCHAR(255),                              -- 登录令牌
    [token_expire] DATETIME2,                          -- 令牌过期时间
    [last_login_time] DATETIME2,                       -- 最后登录时间
    [create_time] DATETIME2 DEFAULT GETDATE(),         -- 创建时间
    [update_time] DATETIME2 DEFAULT GETDATE(),         -- 更新时间
    CONSTRAINT [uk_work_id] UNIQUE ([work_id]),
    CONSTRAINT [uk_mobile] UNIQUE ([mobile])
);
```

### 客户表 (customer)

这是项目的核心用户表，集成了基础客户信息、微信小程序信息以及账号密码体系。

```sql
CREATE TABLE [dbo].[customer] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,                -- 客户ID
    [name] NVARCHAR(100) NOT NULL,                     -- 公司名
    [level] CHAR(1) NOT NULL DEFAULT 'C',              -- 客户级别（A/B/C）
    [contact] NVARCHAR(50) NULL,                       -- 客户姓名 (联系人)
    [phone] NVARCHAR(20) NULL,                         -- 联系电话
    [address] NVARCHAR(255) NULL,                      -- 地址
    [email] NVARCHAR(100) NULL,                        -- 邮箱
    [department] NVARCHAR(50) NULL,                    -- 部门
    [position] NVARCHAR(50) NULL,                      -- 职位
    [sales_id] BIGINT NULL,                            -- 负责销售ID
    [remark] NVARCHAR(500) NULL,                       -- 备注
    [create_time] DATETIME2 DEFAULT GETDATE(),         -- 创建时间
    [update_time] DATETIME2 DEFAULT GETDATE(),         -- 更新时间
    
    -- 微信小程序相关字段
    [wechat_openid] NVARCHAR(128) NULL,                -- 微信OpenID (唯一)
    [wechat_unionid] NVARCHAR(128) NULL,               -- 微信UnionID
    [wechat_avatar_url] NVARCHAR(512) NULL,            -- 微信头像URL
    [nickname] NVARCHAR(100) NULL,                     -- 微信昵称
    [gender] INT NULL,                                 -- 性别（0未知，1男，2女）
    [country] NVARCHAR(50) NULL,                       -- 国家
    [province] NVARCHAR(50) NULL,                      -- 省份
    [city] NVARCHAR(50) NULL,                          -- 城市
    [language] NVARCHAR(20) NULL,                      -- 语言
    [session_key] NVARCHAR(255) NULL,                  -- 微信会话密钥
    [last_login_time] DATETIME2 NULL,                  -- 最后登录时间

    -- 账号密码登录相关字段
    [username] NVARCHAR(50) NULL,                      -- 登录用户名 (唯一)
    [password] NVARCHAR(255) NULL,                     -- 加密存储的密码
    [salt] NVARCHAR(32) NULL,                          -- 密码加密盐值
    [register_time] DATETIME2 NULL DEFAULT GETDATE(),  -- 注册时间

    CONSTRAINT [PK_customer] PRIMARY KEY CLUSTERED ([id] ASC),
    CONSTRAINT [UQ_customer_openid] UNIQUE NONCLUSTERED ([wechat_openid] ASC) WHERE [wechat_openid] IS NOT NULL,
    CONSTRAINT [UQ_customer_username] UNIQUE NONCLUSTERED ([username] ASC) WHERE [username] IS NOT NULL
);
```

### 设备表 (device)

存储客户的仪器设备信息：

```sql
CREATE TABLE [dbo].[device] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,               -- 设备ID
    [customer_id] BIGINT NOT NULL,                    -- 客户ID
    [device_name] NVARCHAR(100) NOT NULL,             -- 仪器名称
    [device_type] NVARCHAR(50) NOT NULL,              -- 仪器类型
    [brand] NVARCHAR(50) NOT NULL,                    -- 品牌
    [model] NVARCHAR(100) NOT NULL,                   -- 型号
    [quantity] INT NOT NULL DEFAULT 1,                -- 数量
    [fault_description] NVARCHAR(MAX) NULL,           -- 故障描述
    [fault_images] NVARCHAR(MAX) NULL,                -- 故障拍照路径
    [accessories] NVARCHAR(MAX) NULL,                 -- 附件
    [appointment_time] DATETIME2 NULL,                -- 预约时间
    [verification_type] NVARCHAR(20) NULL,            -- 验证类别
    [verification_images] NVARCHAR(MAX) NULL,         -- 验证附图路径
    [purpose] NVARCHAR(200) NULL,                     -- 仪器用途
    [requirement_description] NVARCHAR(MAX) NULL,     -- 需求描述
    [installation_images] NVARCHAR(MAX) NULL,         -- 安装拍照路径
    [note] NVARCHAR(MAX) NULL,                        -- 设备备注
    [note_editor] NVARCHAR(50) NULL,                  -- 备注编辑人
    [note_update_time] DATETIME2 NULL,                -- 备注更新时间
    [create_time] DATETIME2 NOT NULL DEFAULT GETDATE(), -- 创建时间
    [update_time] DATETIME2 NOT NULL DEFAULT GETDATE(), -- 更新时间
    CONSTRAINT [PK_device] PRIMARY KEY CLUSTERED ([id] ASC)
);
```

### 任务表 (task)

存储各类服务任务信息。注意 `task_id` 是业务主键，`id` 是物理主键。

```sql
CREATE TABLE task (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,             -- 物理自增主键
    task_id VARCHAR(64) NOT NULL,                    -- 任务编号 (业务主键, e.g., RP-2022-001)
    title VARCHAR(255) NOT NULL,                     -- 任务标题
    task_type VARCHAR(20) NOT NULL,                  -- 任务类型 (repair, maintenance, etc.)
    customer_id BIGINT,                             -- 客户ID
    customer VARCHAR(100),                          -- 客户名称
    customer_level CHAR(1),                         -- 客户级别
    location VARCHAR(255),                          -- 任务地点
    contact_person VARCHAR(50),                     -- 联系人
    contact_phone VARCHAR(20),                      -- 联系电话
    priority VARCHAR(10) DEFAULT 'normal',           -- 优先级 (high, normal, low)
    status VARCHAR(20) DEFAULT 'pending',            -- 任务状态 (pending, in-progress, etc.)
    description TEXT,                               -- 任务详细描述
    create_time DATETIME2 NOT NULL,                 -- 创建时间
    start_date DATETIME2,                           -- 开始日期
    deadline DATETIME2,                             -- 结束日期（截止时间）
    completed_date DATETIME2,                       -- 完成日期
    current_step INT DEFAULT 0,                     -- 当前步骤
    device_name VARCHAR(100),                       -- 设备名称
    device_model VARCHAR(100),                      -- 设备型号
    device_brand VARCHAR(50),                       -- 设备品牌
    device_type VARCHAR(50),                        -- 仪器类型 (新增)
    device_sn VARCHAR(100),                         -- 设备序列号
    fault_description TEXT,                         -- 故障描述
    quantity INT DEFAULT 1,                         -- 设备数量
    appointment_time DATETIME2,                     -- 预约时间
    verification_type VARCHAR(20),                  -- 验证类型 (IQ, OQ, PQ)
    purpose VARCHAR(255),                           -- 设备用途
    requirement_description TEXT,                   -- 需求描述
    attachments VARCHAR(MAX),                       -- 附件信息 (路径)
    CONSTRAINT UQ_task_id UNIQUE (task_id)
);
```

### 任务图片表 (task_image)

存储任务相关的图片。`task_id` 字段类型已修正为 `VARCHAR(64)` 以匹配 `task` 表。

```sql
CREATE TABLE task_image (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,             -- 图片ID
    task_id VARCHAR(64) NOT NULL,                    -- 任务ID (关联 task.task_id)
    image_url VARCHAR(255) NOT NULL,                 -- 图片URL
    image_type INT,                                  -- 图片类型 (0:任务, 1:设备, 2:结果)
    sort INT,                                        -- 排序
    create_time DATETIME2 DEFAULT GETDATE(),         -- 创建时间
    update_time DATETIME2,                           -- 更新时间
    CONSTRAINT FK_task_image_task FOREIGN KEY (task_id) REFERENCES task(task_id)
);
```

### 记录图片表（record_image）

存储步骤记录相关的图片：

```sql
CREATE TABLE record_image (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,             -- 图片ID
    record_id BIGINT NOT NULL,                       -- 记录ID
    image_url VARCHAR(255) NOT NULL,                 -- 图片URL
    create_time DATETIME2 DEFAULT GETDATE()          -- 创建时间
);
```

### 记录文件表（record_file）

存储步骤记录相关的附件文件：

```sql
CREATE TABLE record_file (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,             -- 文件ID
    relation_id BIGINT NOT NULL,                     -- 关联ID（任务ID、步骤ID等）
    relation_type INT NOT NULL,                      -- 关联类型（0：任务，1：步骤，2：评价）
    file_name VARCHAR(100) NOT NULL,                 -- 文件名
    file_path VARCHAR(255) NOT NULL,                 -- 文件路径
    file_type VARCHAR(20),                           -- 文件类型
    file_size BIGINT,                                -- 文件大小(字节)
    upload_user_id BIGINT,                           -- 上传人ID
    upload_user_type INT,                            -- 上传人类型（0：系统用户，1：客户）
    create_time DATETIME2 DEFAULT GETDATE()          -- 创建时间
);
```

### 步骤记录表（step_record）

存储任务步骤的详细记录：

```sql
CREATE TABLE step_record (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    task_id VARCHAR(20) NOT NULL,                    -- 任务ID
    step_id BIGINT NOT NULL,                         -- 步骤ID
    step_index INT NOT NULL,                         -- 步骤索引
    description NVARCHAR(MAX) NOT NULL,              -- 记录描述
    creator_id BIGINT NOT NULL,                      -- 创建人ID
    creator_name VARCHAR(50) NOT NULL,               -- 创建人姓名
    create_time DATETIME2 DEFAULT GETDATE(),         -- 创建时间
    update_time DATETIME2 DEFAULT GETDATE()          -- 更新时间
);
```

### 任务流程表（task_flow）

记录任务的进度流程：

```sql
CREATE TABLE task_flow (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,             -- 自增主键
    task_id VARCHAR(64) NOT NULL,                    -- 任务ID，注意这里使用的是字符串类型
    task_type VARCHAR(20) NOT NULL,                  -- 任务类型
    current_step INT DEFAULT 0,                      -- 当前步骤
    create_time DATETIME2 NOT NULL DEFAULT GETDATE(), -- 创建时间
    update_time DATETIME2 NOT NULL DEFAULT GETDATE(), -- 更新时间
    CONSTRAINT FK_task_flow_task FOREIGN KEY (task_id) REFERENCES task(task_id)
);
```

### 服务评价表（service_evaluation）

存储客户对服务的评价：

```sql
CREATE TABLE service_evaluation (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,             -- 评价ID
    task_id VARCHAR(20) NOT NULL,                    -- 任务ID
    customer_id BIGINT NOT NULL,                     -- 客户ID
    service_attitude INT,                            -- 服务态度评分（1-5）
    service_quality INT,                             -- 服务质量评分（1-5）
    overall_rating INT,                              -- 总体评价评分（1-5）
    comment TEXT,                                    -- 评价内容
    create_time DATETIME2 DEFAULT GETDATE()          -- 创建时间
);
```

### 任务评论表（task_comment）

存储任务相关的评论：

```sql
CREATE TABLE task_comment (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,              -- 评论ID
    task_id VARCHAR(64) NOT NULL,                     -- 任务ID，注意这是字符串类型
    content TEXT NOT NULL,                            -- 评论内容
    user_id BIGINT NOT NULL,                          -- 评论人ID
    user_type INT NOT NULL,                           -- 评论人类型（0：系统用户，1：客户）
    create_time DATETIME2 NOT NULL DEFAULT GETDATE(), -- 创建时间
    update_time DATETIME2 NOT NULL DEFAULT GETDATE(), -- 更新时间
    CONSTRAINT FK_task_comment_task FOREIGN KEY (task_id) REFERENCES task(task_id)
);
```

## 数据库与Java代码映射关系

### customer表映射关系

| 数据库字段 | Java属性 |
|------------|----------|
| id | id |
| name | customerName |
| level | level |
| contact | contactPerson |
| phone | contactPhone |
| email | email |
| address | address |
| department | department |
| position | position |
| sales_id | salesId |
| remark | remark |
| create_time | createTime |
| update_time | updateTime |
| wechat_openid | openid |
| wechat_unionid | unionid |
| wechat_avatar_url | avatarUrl |
| nickname | nickname |
| gender | gender |
| country | country |
| province | province |
| city | city |
| language | language |
| session_key | sessionKey |
| last_login_time | lastLoginTime |
| username | username |
| password | password |
| salt | salt |
| register_time | registerTime |

### task表映射关系

| 数据库字段 | Java属性 |
|------------|----------|
| id | id |
| task_id | taskId |
| title | title |
| description | content |
| task_type | taskType |
| status | status |
| priority | priority |
| start_date | startDate |
| deadline | deadline |
| completed_date | completedDate |
| customer_id | customerId |
| customer | customerName |
| contact_person | contactPerson |
| contact_phone | contactPhone |
| location | location |
| device_name | deviceName |
| device_model | deviceModel |
| device_brand | deviceBrand |
| device_type | deviceType |
| device_sn | deviceSn |
| fault_description | faultDescription |
| quantity | quantity |
| attachments | attachments |
| verification_type | verificationType |
| purpose | purpose |
| requirement_description | requirementDescription |
| appointment_time | appointmentTime |
| current_step | currentStep |
| create_time | createTime |
| 非数据库字段 | createUserId (@TableField(exist = false)) |
| 非数据库字段 | assigneeId (@TableField(exist = false)) |
| 非数据库字段 | updateTime (@TableField(exist = false)) |
| 非数据库字段 | description (@TableField(exist = false)) |

### task_comment表映射关系

| 数据库字段 | Java属性 |
|------------|----------|
| id | id |
| task_id | taskId (String类型, 关联 task.task_id) |
| content | content |
| user_id | userId |
| user_type | userType |
| create_time | createTime |
| update_time | updateTime |

### task_flow表映射关系

| 数据库字段 | Java属性 |
|------------|----------|
| id | id |
| task_id | taskId (String类型, 关联 task.task_id) |
| task_type | taskType |
| current_step | currentStep |
| create_time | createTime |
| update_time | updateTime |
| 非数据库字段 | status (@TableField(exist = false)) |
| 非数据库字段 | totalSteps (@TableField(exist = false)) |

### service_evaluation表映射关系（对应TaskEvaluation实体类）

| 数据库字段 | Java属性 |
|------------|----------|
| id | id |
| task_id | taskId (String类型, 关联 task.task_id) |
| customer_id | customerId |
| service_attitude | serviceAttitude |
| service_quality | serviceQuality |
| overall_rating | overallRating |
| comment | comment |
| create_time | createTime |

### step_record表映射关系（对应TaskStep实体类）

| 数据库字段 | Java属性 |
|------------|----------|
| id | id |
| task_id | taskId (String类型, 关联 task.task_id) |
| step_id | stepId |
| step_index | stepIndex |
| description | description |
| creator_id | creatorId |
| creator_name | creatorName |
| create_time | createTime |
| update_time | updateTime |
| 非数据库字段 | remark (@TableField(exist = false)) |

### task_image表映射关系

| 数据库字段 | Java属性 |
|------------|----------|
| id | id |
| task_id | taskId (String类型, 关联 task.task_id) |
| image_url | imageUrl |
| image_type | imageType |
| sort | sort |
| create_time | createTime |
| update_time | updateTime |

### record_file表映射关系

| 数据库字段 | Java属性 |
|------------|----------|
| id | id |
| relation_id | relationId |
| relation_type | relationType |
| file_name | fileName |
| file_path | filePath |
| file_size | fileSize |
| file_type | fileType |
| upload_user_id | uploadUserId |
| upload_user_type | uploadUserType |
| create_time | createTime |