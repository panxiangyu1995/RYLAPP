package com.ryl.engineer.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQL Server数据库连接测试类
 * 用于验证SQL Server数据库连接和数据查询功能是否正常
 */
public class SqlServerConnectionTest {

    // 数据库连接参数
    private static final String DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String URL = "jdbc:sqlserver://DESKTOP-SOU1KQ1;databaseName=ryl_engineer;encrypt=true;trustServerCertificate=true";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "zxcv1234";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            System.out.println("=== SQL Server数据库连接测试 ===");
            
            // 1. 加载驱动
            System.out.println("正在加载SQL Server JDBC驱动...");
            Class.forName(DRIVER_CLASS);
            System.out.println("SQL Server JDBC驱动加载成功");
            
            // 2. 建立连接
            System.out.println("正在连接到数据库...");
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("数据库连接成功");
            
            // 3. 创建Statement对象
            stmt = conn.createStatement();
            
            // 4. 执行SQL查询 - 测试用户表
            System.out.println("\n测试查询用户表：");
            rs = stmt.executeQuery("SELECT TOP 5 id, username, real_name, role FROM dbo.[user]");
            
            // 5. 处理结果集
            System.out.println("用户表查询结果：");
            System.out.println("----------------------------------");
            System.out.println("ID\t用户名\t\t姓名\t\t角色");
            System.out.println("----------------------------------");
            
            while (rs.next()) {
                long id = rs.getLong("id");
                String username = rs.getString("username");
                String realName = rs.getString("real_name");
                String role = rs.getString("role");
                
                System.out.println(id + "\t" + username + "\t\t" + realName + "\t\t" + role);
            }
            
            // 关闭第一个结果集
            if (rs != null) {
                rs.close();
            }
            
            // 6. 执行SQL查询 - 测试任务表
            System.out.println("\n测试查询任务表：");
            rs = stmt.executeQuery("SELECT TOP 5 task_id, title, task_type, status FROM dbo.task");
            
            // 7. 处理结果集
            System.out.println("任务表查询结果：");
            System.out.println("------------------------------------------");
            System.out.println("任务ID\t\t标题\t\t类型\t\t状态");
            System.out.println("------------------------------------------");
            
            while (rs.next()) {
                String taskId = rs.getString("task_id");
                String title = rs.getString("title");
                String taskType = rs.getString("task_type");
                String status = rs.getString("status");
                
                System.out.println(taskId + "\t" + title + "\t\t" + taskType + "\t\t" + status);
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("加载JDBC驱动失败: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("数据库操作失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 8. 关闭资源
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                System.out.println("\n数据库连接已关闭");
            } catch (SQLException e) {
                System.err.println("关闭数据库资源失败: " + e.getMessage());
            }
        }
    }
} 