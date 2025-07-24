-- SQL Server 修复脚本：为现有角色的 code 字段添加 'ROLE_' 前缀
-- 本脚本可重复执行，不会重复添加前缀。
USE ryl_engineer;
GO

-- 更新管理员角色
UPDATE dbo.role
SET code = 'ROLE_ADMIN'
WHERE code = 'ADMIN' AND LEFT(code, 5) != 'ROLE_';

-- 更新工程师角色
UPDATE dbo.role
SET code = 'ROLE_ENGINEER'
WHERE code = 'ENGINEER' AND LEFT(code, 5) != 'ROLE_';

-- 更新销售角色
UPDATE dbo.role
SET code = 'ROLE_SALES'
WHERE code = 'SALES' AND LEFT(code, 5) != 'ROLE_';

-- 更新仓库管理员角色
UPDATE dbo.role
SET code = 'ROLE_WAREHOUSE'
WHERE code = 'WAREHOUSE' AND LEFT(code, 5) != 'ROLE_';

PRINT '数据库角色 code 前缀修复完成。';
GO