# 数据库脚本说明

本目录包含项目所需的数据库脚本。请按照以下顺序执行脚本：

## 初始化脚本

1. `combined_tables_sqlserver.sql` - 基础表结构初始化脚本
2. `任务模块数据库初始化_sqlserver.sql` - 任务模块表结构初始化脚本

## 更新脚本

1. `task_image_update.sql` - 任务图片表结构更新脚本（2025-07-08）
   - 为 `task_image` 表添加缺失的字段：`image_type`, `sort`, `update_time`
   - 创建新的 `task_attachment` 表，用于存储任务附件

## 执行方法

### 使用SQL Server Management Studio (SSMS)

1. 打开SSMS并连接到数据库服务器
2. 打开脚本文件
3. 点击"执行"按钮或按F5键执行脚本
4. 检查消息窗口中的输出，确认脚本执行成功

### 使用命令行

```bash
sqlcmd -S [服务器名称] -d [数据库名称] -U [用户名] -P [密码] -i [脚本文件路径]
```

例如：

```bash
sqlcmd -S localhost -d ryl_engineer -U sa -P password -i task_image_update.sql
```

## 注意事项

1. 执行脚本前请先备份数据库
2. 脚本中已包含检查逻辑，避免重复创建表或添加已存在的字段
3. 如遇到权限问题，请确保使用具有足够权限的数据库账号执行脚本
4. 执行完脚本后，请重启应用服务器以确保应用能够正确识别新的数据库结构 