-- SQL Server�汾�Ŀͻ�����豸�����ű�
USE ryl_engineer;
GO


-- Add technical_category column to the user table
ALTER TABLE [user]
ADD technical_category VARCHAR(255) NULL;
GO 