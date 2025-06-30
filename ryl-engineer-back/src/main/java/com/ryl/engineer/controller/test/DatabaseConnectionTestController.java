package com.ryl.engineer.controller.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库连接测试控制器
 */
@RestController
@RequestMapping("/api/v1/test/db")
public class DatabaseConnectionTestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 测试数据库连接
     * @return 连接测试结果
     */
    @GetMapping("/connection")
    public Map<String, Object> testConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 测试连接
            String dbInfo = jdbcTemplate.queryForObject(
                "SELECT @@version AS version", 
                (rs, rowNum) -> rs.getString("version")
            );
            
            result.put("success", true);
            result.put("message", "数据库连接成功");
            result.put("dbInfo", dbInfo);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "数据库连接失败: " + e.getMessage());
            result.put("error", e.getClass().getName());
        }
        
        return result;
    }
    
    /**
     * 测试查询用户表
     * @return 用户表查询结果
     */
    @GetMapping("/users")
    public Map<String, Object> testUsers() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> users = jdbcTemplate.queryForList(
                "SELECT TOP 5 id, username, real_name, role FROM dbo.[user]"
            );
            
            result.put("success", true);
            result.put("message", "查询用户表成功");
            result.put("data", users);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询用户表失败: " + e.getMessage());
            result.put("error", e.getClass().getName());
        }
        
        return result;
    }
    
    /**
     * 测试查询任务表
     * @return 任务表查询结果
     */
    @GetMapping("/tasks")
    public Map<String, Object> testTasks() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> tasks = jdbcTemplate.queryForList(
                "SELECT TOP 5 task_id, title, task_type, status FROM dbo.task"
            );
            
            result.put("success", true);
            result.put("message", "查询任务表成功");
            result.put("data", tasks);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询任务表失败: " + e.getMessage());
            result.put("error", e.getClass().getName());
        }
        
        return result;
    }
    
    /**
     * 测试查询所有表
     * @return 所有表查询结果
     */
    @GetMapping("/tables")
    public Map<String, Object> testTables() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_type = 'BASE TABLE'"
            );
            
            result.put("success", true);
            result.put("message", "查询所有表成功");
            result.put("data", tables);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询所有表失败: " + e.getMessage());
            result.put("error", e.getClass().getName());
        }
        
        return result;
    }
} 