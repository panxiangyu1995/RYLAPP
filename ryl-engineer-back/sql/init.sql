-- Add is_paid column to task table
ALTER TABLE task ADD is_paid TINYINT DEFAULT 0 NOT NULL;
GO 