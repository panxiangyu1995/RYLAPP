<<<<<<< HEAD
USE ryl_engineer;
GO

ALTER TABLE [user] ADD security_password VARCHAR(255) NULL; 
=======
ALTER TABLE `user` ADD `security_password` VARCHAR(255) NULL COMMENT '安全密码（二级密码）'; 
>>>>>>> 8e0b977fbfaaab4ad2c076f29a8b7d83856e226f
