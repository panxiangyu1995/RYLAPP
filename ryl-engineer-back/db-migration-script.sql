-- 数据迁移脚本
-- 版本: 0.1.0
-- 日期: 2024-08-08
-- 描述: 数据迁移脚本模板，用于在表结构变更时确保现有数据不会丢失

USE ryl_engineer;

-- 示例：假设我们要将system_announcement表重命名为announcement
-- 1. 创建新表
-- CREATE TABLE IF NOT EXISTS `announcement` (
--   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
--   `title` varchar(100) NOT NULL COMMENT '公告标题',
--   `content` text NOT NULL COMMENT '公告内容',
--   `publisher_id` bigint NOT NULL COMMENT '发布者ID',
--   `publish_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
--   `is_top` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否置顶（0否，1是）',
--   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
--   PRIMARY KEY (`id`),
--   KEY `idx_publisher_id` (`publisher_id`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统公告表';

-- 2. 复制数据
-- INSERT INTO `announcement` SELECT * FROM `system_announcement`;

-- 3. 验证数据
-- SELECT COUNT(*) FROM `system_announcement`;
-- SELECT COUNT(*) FROM `announcement`;
-- 确认两个数字相同

-- 4. 备份旧表（可选）
-- RENAME TABLE `system_announcement` TO `system_announcement_backup`;

-- 5. 替换旧表
-- RENAME TABLE `announcement` TO `system_announcement`;

-- 6. 确认新表运行正常后删除备份表（可选，谨慎操作）
-- DROP TABLE IF EXISTS `system_announcement_backup`;

-- 注意事项：
-- 1. 执行脚本前务必备份数据库
-- 2. 每个步骤执行后应验证结果
-- 3. 建议分阶段执行，并在测试环境验证
-- 4. 保留备份表直到确认新表正常工作 