package com.ryl.engineer.test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.List;
import java.util.Map;

/**
 * Spring Boot 数据源测试类
 * 使用Spring配置的数据源测试数据库连接和查询
 * 可以直接运行此类来测试数据库连接
 * 注意：此类不再使用@SpringBootApplication注解，避免与主应用类冲突
 */
// @SpringBootApplication 移除此注解，避免与主应用类冲突
@ComponentScan(basePackages = "com.ryl.engineer")
public class SpringDataSourceTest {

    public static void main(String[] args) {
        // 使用主应用类启动
        SpringApplication.run(com.ryl.engineer.RylEngineerApplication.class, args);
    }

    @Bean
    public CommandLineRunner testDataSource(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        return args -> {
            System.out.println("\n=== Spring Boot 数据源测试 ===");
            
            try (Connection connection = dataSource.getConnection()) {
                // 1. 获取数据库连接信息
                DatabaseMetaData metaData = connection.getMetaData();
                System.out.println("数据库连接成功!");
                System.out.println("数据库产品名称: " + metaData.getDatabaseProductName());
                System.out.println("数据库产品版本: " + metaData.getDatabaseProductVersion());
                System.out.println("数据库驱动名称: " + metaData.getDriverName());
                System.out.println("数据库驱动版本: " + metaData.getDriverVersion());
                System.out.println("数据库URL: " + metaData.getURL());
                System.out.println("数据库用户名: " + metaData.getUserName());
                
                // 2. 测试查询用户表
                System.out.println("\n=== 测试查询用户表 ===");
                List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT TOP 5 id, username, real_name FROM user_info");
                
                if (users.isEmpty()) {
                    System.out.println("没有查询到用户数据!");
                } else {
                    System.out.println("用户数据:");
                    System.out.println("---------------------------");
                    System.out.println("ID\t用户名\t\t真实姓名");
                    System.out.println("---------------------------");
                    
                    for (Map<String, Object> user : users) {
                        System.out.println(user.get("id") + "\t" + user.get("username") + "\t\t" + user.get("real_name"));
                    }
                }
                
                // 3. 测试查询任务表
                System.out.println("\n=== 测试查询任务表 ===");
                List<Map<String, Object>> tasks = jdbcTemplate.queryForList("SELECT TOP 5 id, title, status FROM task");
                
                if (tasks.isEmpty()) {
                    System.out.println("没有查询到任务数据!");
                } else {
                    System.out.println("任务数据:");
                    System.out.println("---------------------------");
                    System.out.println("ID\t任务标题\t\t状态");
                    System.out.println("---------------------------");
                    
                    for (Map<String, Object> task : tasks) {
                        System.out.println(task.get("id") + "\t" + task.get("title") + "\t\t" + task.get("status"));
                    }
                }
                
                // 4. 获取表列表
                System.out.println("\n=== 数据库表列表 ===");
                List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                        "SELECT table_name FROM information_schema.tables WHERE table_type = 'BASE TABLE' AND table_catalog = 'ryl_engineer'");
                
                System.out.println("数据库中的表:");
                System.out.println("---------------------------");
                for (Map<String, Object> table : tables) {
                    System.out.println(table.get("table_name"));
                }
                
                System.out.println("\n✓ Spring Boot 数据源测试成功!");
            } catch (Exception e) {
                System.err.println("❌ 数据库连接或查询失败: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
} 