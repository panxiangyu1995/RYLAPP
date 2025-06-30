-- 数据库修复脚本
-- 版本: 0.1.0
-- 日期: 2024-08-08
-- 描述: 解决表名不匹配和缺失表的问题

USE ryl_engineer;

-- 1. 创建缺失的assistance_request_recipient表
CREATE TABLE IF NOT EXISTS `assistance_request_recipient` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `assistance_request_id` bigint NOT NULL COMMENT '协助请求ID',
  `recipient_id` bigint NOT NULL COMMENT '接收者ID',
  `is_read` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已读（0未读，1已读）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_assistance_request_id` (`assistance_request_id`),
  KEY `idx_recipient_id` (`recipient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='协助请求接收者表';

-- 2. 如果使用表名不一致问题的备用解决方案：创建视图
-- 注意：以下仅作为备用方案，优先使用Mapper文件修改方案
-- CREATE OR REPLACE VIEW announcement AS SELECT * FROM system_announcement; 