-- 1. 更新 contact 字段为 username 字段的值（所有记录，会覆盖现有 contact 值）
UPDATE customer SET contact = username WHERE username IS NOT NULL;

-- 2. 删除依赖于username列的唯一索引
DROP INDEX UX_customer_username ON customer;

-- 3. 删除 username 字段
ALTER TABLE customer DROP COLUMN username; 