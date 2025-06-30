package com.ryl.engineer.test;

/**
 * 运行所有数据库测试的主类
 */
public class RunDatabaseTests {

    public static void main(String[] args) {
        System.out.println("=== 开始数据库连接测试 ===");
        System.out.println("\n1. 直接JDBC连接测试");
        System.out.println("------------------------------");
        SqlServerConnectionTest.main(args);
        
        System.out.println("\n\n2. Spring Boot数据源测试");
        System.out.println("------------------------------");
        SpringDataSourceTest.main(args);
        
        System.out.println("\n=== 所有数据库测试完成 ===");
    }
} 