-- 修改 customer 表，允许 name 字段为空
USE ryl_engineer;
GO

ALTER TABLE customer
ALTER COLUMN name NVARCHAR(255) NULL; 