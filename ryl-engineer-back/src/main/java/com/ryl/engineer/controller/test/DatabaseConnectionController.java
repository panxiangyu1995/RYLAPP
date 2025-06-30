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
@RequestMapping("/api/v1/test/db-connection")
public class DatabaseConnectionController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 测试数据库连接
     */
    @GetMapping("/status")
    public Map<String, Object> testConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取数据库版本信息
            String dbVersion = jdbcTemplate.queryForObject("SELECT @@VERSION", String.class);
            result.put("status", "success");
            result.put("message", "数据库连接成功");
            result.put("dbVersion", dbVersion);
            
            // 获取服务器名称
            String serverName = jdbcTemplate.queryForObject("SELECT @@SERVERNAME", String.class);
            result.put("serverName", serverName);
            
            // 获取数据库名称
            String dbName = jdbcTemplate.queryForObject("SELECT DB_NAME()", String.class);
            result.put("databaseName", dbName);
            
            // 获取表数量
            Integer tableCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE'", 
                    Integer.class);
            result.put("tableCount", tableCount);
            
            // 获取部分表名
            List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                    "SELECT TOP 10 TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' ORDER BY TABLE_NAME");
            result.put("tables", tables);
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "数据库连接失败: " + e.getMessage());
            result.put("errorType", e.getClass().getName());
            result.put("stackTrace", e.getStackTrace()[0].toString());
        }
        
        return result;
    }
} 