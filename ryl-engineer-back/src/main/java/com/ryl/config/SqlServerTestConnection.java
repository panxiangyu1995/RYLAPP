package com.ryl.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQL Server测试连接控制器
 * 用于验证SQL Server连接和基本查询
 */
@RestController
@RequestMapping("/api/v1/test")
@Slf4j
public class SqlServerTestConnection {

    @Autowired
    private DataSource dataSource;

    /**
     * 测试数据库连接
     * @return 连接状态信息
     */
    @GetMapping("/db-connection")
    public Map<String, Object> testConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = dataSource.getConnection()) {
            result.put("status", "success");
            result.put("message", "数据库连接成功");
            result.put("databaseProductName", conn.getMetaData().getDatabaseProductName());
            result.put("databaseProductVersion", conn.getMetaData().getDatabaseProductVersion());
        } catch (SQLException e) {
            result.put("status", "error");
            result.put("message", "数据库连接失败: " + e.getMessage());
            result.put("errorCode", e.getErrorCode());
            result.put("sqlState", e.getSQLState());
        }
        
        return result;
    }
    
    /**
     * 测试数据库查询
     * @return 查询结果列表
     */
    @GetMapping("/db-query")
    public Map<String, Object> testQuery() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 执行简单查询，获取SQL Server系统信息
            try (ResultSet rs = stmt.executeQuery("SELECT @@VERSION AS version, @@SERVERNAME AS server_name, DB_NAME() AS database_name")) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("version", rs.getString("version"));
                    row.put("serverName", rs.getString("server_name"));
                    row.put("databaseName", rs.getString("database_name"));
                    data.add(row);
                }
            }
            
            result.put("status", "success");
            result.put("message", "查询执行成功");
            result.put("data", data);
            
        } catch (SQLException e) {
            result.put("status", "error");
            result.put("message", "查询执行失败: " + e.getMessage());
            result.put("errorCode", e.getErrorCode());
            result.put("sqlState", e.getSQLState());
        }
        
        return result;
    }
} 