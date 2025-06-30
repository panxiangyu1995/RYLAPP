package com.ryl.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 最终SQL Server连接测试类
 * 尝试所有可能的连接方式
 */
public class FinalConnectionTest {

    public static void main(String[] args) {
        System.out.println("开始最终SQL Server连接测试...");
        
        // 测试Microsoft JDBC驱动 + Windows身份验证 + trustServerCertificate=true
        testMicrosoftJDBC("=== Microsoft JDBC + Windows身份验证 + trustServerCertificate=true ===", 
            "jdbc:sqlserver://DESKTOP-SOU1KQ1;databaseName=ryl_engineer;encrypt=true;integratedSecurity=true;trustServerCertificate=true", 
            null, null);
            
        // 测试Microsoft JDBC驱动 + SQL身份验证
        testMicrosoftJDBC("=== Microsoft JDBC + SQL身份验证 ===", 
            "jdbc:sqlserver://DESKTOP-SOU1KQ1;databaseName=ryl_engineer;encrypt=true;trustServerCertificate=true", 
            "sa", "zxcv1234");
            
        // 测试JTDS驱动 + Windows身份验证
        testJTDS("=== JTDS + Windows身份验证 ===", 
            "jdbc:jtds:sqlserver://DESKTOP-SOU1KQ1/ryl_engineer;domain=DESKTOP-SOU1KQ1;useNTLMv2=true;instance=MSSQLSERVER", 
            null, null);
            
        // 测试JTDS驱动 + SQL身份验证
        testJTDS("=== JTDS + SQL身份验证 ===", 
            "jdbc:jtds:sqlserver://DESKTOP-SOU1KQ1/ryl_engineer", 
            "sa", "zxcv1234");
            
        System.out.println("测试完成。");
    }
    
    /**
     * 使用Microsoft JDBC驱动测试连接
     */
    private static void testMicrosoftJDBC(String title, String url, String user, String password) {
        System.out.println("\n" + title);
        
        try {
            // 加载驱动
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("驱动加载成功");
            
            // 建立连接
            System.out.println("连接URL: " + url);
            if (user != null) {
                System.out.println("用户名: " + user);
                System.out.println("密码: ******");
            } else {
                System.out.println("使用Windows身份验证");
            }
            
            Connection conn;
            if (user != null) {
                conn = DriverManager.getConnection(url, user, password);
            } else {
                conn = DriverManager.getConnection(url);
            }
            
            System.out.println("连接成功！");
            
            // 获取数据库信息
            System.out.println("数据库产品名称: " + conn.getMetaData().getDatabaseProductName());
            System.out.println("数据库版本: " + conn.getMetaData().getDatabaseProductVersion());
            
            // 测试查询
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT @@SERVERNAME AS server_name")) {
                if (rs.next()) {
                    System.out.println("服务器名称: " + rs.getString("server_name"));
                }
            }
            
            conn.close();
            System.out.println("连接已关闭");
            
        } catch (ClassNotFoundException e) {
            System.out.println("驱动加载失败: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("连接失败: " + e.getMessage());
            System.out.println("SQL状态: " + e.getSQLState());
            System.out.println("错误码: " + e.getErrorCode());
        }
    }
    
    /**
     * 使用JTDS驱动测试连接
     */
    private static void testJTDS(String title, String url, String user, String password) {
        System.out.println("\n" + title);
        
        try {
            // 加载驱动
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            System.out.println("驱动加载成功");
            
            // 建立连接
            System.out.println("连接URL: " + url);
            if (user != null) {
                System.out.println("用户名: " + user);
                System.out.println("密码: ******");
            } else {
                System.out.println("使用Windows身份验证");
            }
            
            Connection conn;
            if (user != null) {
                conn = DriverManager.getConnection(url, user, password);
            } else {
                conn = DriverManager.getConnection(url);
            }
            
            System.out.println("连接成功！");
            
            // 获取数据库信息
            System.out.println("数据库产品名称: " + conn.getMetaData().getDatabaseProductName());
            System.out.println("数据库版本: " + conn.getMetaData().getDatabaseProductVersion());
            
            // 测试查询
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT @@SERVERNAME AS server_name")) {
                if (rs.next()) {
                    System.out.println("服务器名称: " + rs.getString("server_name"));
                }
            }
            
            conn.close();
            System.out.println("连接已关闭");
            
        } catch (ClassNotFoundException e) {
            System.out.println("驱动加载失败: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("连接失败: " + e.getMessage());
            System.out.println("SQL状态: " + e.getSQLState());
            System.out.println("错误码: " + e.getErrorCode());
        }
    }
} 