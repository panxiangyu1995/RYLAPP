-- SQL Server版本的客户表和设备表创建脚本
USE ryl_engineer;
GO

-- 客户表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[customer]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[customer] (
        [id] BIGINT IDENTITY(1,1) NOT NULL,
        [name] NVARCHAR(100) NOT NULL,
        [level] CHAR(1) NOT NULL DEFAULT 'C',
        [contact] NVARCHAR(50) NULL,
        [phone] NVARCHAR(20) NULL,
        [address] NVARCHAR(255) NULL,
        [email] NVARCHAR(100) NULL,
        [department] NVARCHAR(50) NULL,
        [position] NVARCHAR(50) NULL,
        [sales_id] BIGINT NULL,
        [create_time] DATETIME2 DEFAULT GETDATE(),
        [update_time] DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT [PK_customer] PRIMARY KEY CLUSTERED ([id] ASC)
    );
    
    -- 添加注释
    EXEC sp_addextendedproperty 'MS_Description', N'客户表', 'SCHEMA', 'dbo', 'TABLE', 'customer';
    EXEC sp_addextendedproperty 'MS_Description', N'客户ID', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'id';
    EXEC sp_addextendedproperty 'MS_Description', N'公司名', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'name';
    EXEC sp_addextendedproperty 'MS_Description', N'客户级别（A/B/C）', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'level';
    EXEC sp_addextendedproperty 'MS_Description', N'客户姓名', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'contact';
    EXEC sp_addextendedproperty 'MS_Description', N'联系电话', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'phone';
    EXEC sp_addextendedproperty 'MS_Description', N'地址', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'address';
    EXEC sp_addextendedproperty 'MS_Description', N'邮箱', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'email';
    EXEC sp_addextendedproperty 'MS_Description', N'部门', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'department';
    EXEC sp_addextendedproperty 'MS_Description', N'职位', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'position';
    EXEC sp_addextendedproperty 'MS_Description', N'负责销售ID', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'sales_id';
    EXEC sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'create_time';
    EXEC sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', 'dbo', 'TABLE', 'customer', 'COLUMN', 'update_time';
    
    -- 创建索引
    CREATE INDEX [IX_customer_name] ON [dbo].[customer]([name]);
    CREATE INDEX [IX_customer_sales_id] ON [dbo].[customer]([sales_id]);
END
GO

-- 插入测试数据
IF NOT EXISTS (SELECT * FROM customer WHERE name = '上海医疗器械有限公司')
BEGIN
    INSERT INTO customer (name, level, contact, phone, address, department, position) VALUES
    ('上海医疗器械有限公司', 'A', '张经理', '13800138001', '上海市浦东新区张江高科技园区', '设备部', '经理'),
    ('北京科研仪器研究所', 'B', '李研究员', '13900139002', '北京市海淀区中关村', '研究部', '主任'),
    ('广州医学检验中心', 'A', '王主任', '13800138003', '广州市黄埔区科学城', '检验科', '主任');
END
GO

-- 创建设备表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[device]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[device] (
        [id] BIGINT IDENTITY(1,1) NOT NULL,
        [customer_id] BIGINT NOT NULL,
        [device_name] NVARCHAR(100) NOT NULL,
        [device_type] NVARCHAR(50) NOT NULL,
        [brand] NVARCHAR(50) NOT NULL,
        [model] NVARCHAR(100) NOT NULL,
        [quantity] INT NOT NULL DEFAULT 1,
        [fault_description] NVARCHAR(MAX) NULL,
        [fault_images] NVARCHAR(MAX) NULL,
        [accessories] NVARCHAR(MAX) NULL,
        [appointment_time] DATETIME2 NULL,
        [verification_type] NVARCHAR(20) NULL,
        [verification_images] NVARCHAR(MAX) NULL,
        [purpose] NVARCHAR(200) NULL,
        [requirement_description] NVARCHAR(MAX) NULL,
        [installation_images] NVARCHAR(MAX) NULL,
        [note] NVARCHAR(MAX) NULL,
        [note_editor] NVARCHAR(50) NULL,
        [note_update_time] DATETIME2 NULL,
        [create_time] DATETIME2 NOT NULL DEFAULT GETDATE(),
        [update_time] DATETIME2 NOT NULL DEFAULT GETDATE(),
        CONSTRAINT [PK_device] PRIMARY KEY CLUSTERED ([id] ASC)
    );
    
    -- 添加注释
    EXEC sp_addextendedproperty 'MS_Description', N'客户设备表', 'SCHEMA', 'dbo', 'TABLE', 'device';
    EXEC sp_addextendedproperty 'MS_Description', N'设备ID', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'id';
    EXEC sp_addextendedproperty 'MS_Description', N'客户ID', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'customer_id';
    EXEC sp_addextendedproperty 'MS_Description', N'仪器名称', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'device_name';
    EXEC sp_addextendedproperty 'MS_Description', N'仪器类型', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'device_type';
    EXEC sp_addextendedproperty 'MS_Description', N'品牌', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'brand';
    EXEC sp_addextendedproperty 'MS_Description', N'型号', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'model';
    EXEC sp_addextendedproperty 'MS_Description', N'数量', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'quantity';
    EXEC sp_addextendedproperty 'MS_Description', N'故障描述（维修、保养）', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'fault_description';
    EXEC sp_addextendedproperty 'MS_Description', N'故障拍照（维修、保养，多张图片路径用逗号分隔）', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'fault_images';
    EXEC sp_addextendedproperty 'MS_Description', N'附件（回收、租赁）', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'accessories';
    EXEC sp_addextendedproperty 'MS_Description', N'预约时间（培训）', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'appointment_time';
    EXEC sp_addextendedproperty 'MS_Description', N'验证类别（验证：IQ安装验证、OQ运行验证、PQ性能验证）', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'verification_type';
    EXEC sp_addextendedproperty 'MS_Description', N'验证附图（验证，多张图片路径用逗号分隔）', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'verification_images';
    EXEC sp_addextendedproperty 'MS_Description', N'仪器用途（选型）', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'purpose';
    EXEC sp_addextendedproperty 'MS_Description', N'需求描述（选型）', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'requirement_description';
    EXEC sp_addextendedproperty 'MS_Description', N'安装拍照（安装，多张图片路径用逗号分隔）', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'installation_images';
    EXEC sp_addextendedproperty 'MS_Description', N'设备备注', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'note';
    EXEC sp_addextendedproperty 'MS_Description', N'备注编辑人', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'note_editor';
    EXEC sp_addextendedproperty 'MS_Description', N'备注更新时间', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'note_update_time';
    EXEC sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'create_time';
    EXEC sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', 'dbo', 'TABLE', 'device', 'COLUMN', 'update_time';
    
    -- 创建索引
    CREATE INDEX [IX_device_customer_id] ON [dbo].[device]([customer_id]);
    
    -- 添加外键约束
    ALTER TABLE [dbo].[device] WITH CHECK ADD CONSTRAINT [FK_device_customer] FOREIGN KEY([customer_id])
    REFERENCES [dbo].[customer] ([id]) ON DELETE CASCADE;
    
    ALTER TABLE [dbo].[device] CHECK CONSTRAINT [FK_device_customer];
END
GO

-- 插入示例设备数据
IF NOT EXISTS (SELECT * FROM device WHERE device_name = '气相色谱仪')
BEGIN
    INSERT INTO device (customer_id, device_name, device_type, brand, model, quantity, fault_description) VALUES
    (1, '气相色谱仪', '分析仪器', 'Agilent', 'GC-2000', 1, '无法正常启动，可能是电源模块故障'),
    (2, '液相色谱仪', '分析仪器', 'Waters', 'LC-3000', 1, '需要年度保养'),
    (3, '质谱仪', '分析仪器', 'Thermo Fisher', 'MS-6000', 1, NULL);
END
GO 