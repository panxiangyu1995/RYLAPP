-- SQL Server版本的客户表和设备表创建脚本
USE ryl_engineer;
GO


-- Add technical_category column to the user table
ALTER TABLE [user]
ADD technical_category VARCHAR(255) NULL;
GO 