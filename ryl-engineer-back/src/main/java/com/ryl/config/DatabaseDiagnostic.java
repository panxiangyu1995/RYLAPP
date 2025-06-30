package com.ryl.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * 数据库诊断类
 * 用于在应用启动时诊断数据库连接问题
 */
@Configuration
@Slf4j
public class DatabaseDiagnostic {

    @Autowired
    private Environment env;

    /**
     * 在应用启动时运行数据库诊断
     */
    @Bean
    public CommandLineRunner databaseConnectionDiagnostic(DataSource dataSource) {
        return args -> {
            log.info("执行数据库连接诊断...");
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                log.info("=== 数据库连接成功 ===");
                log.info("数据库URL: {}", metaData.getURL());
                log.info("数据库产品名称: {}", metaData.getDatabaseProductName());
                log.info("数据库产品版本: {}", metaData.getDatabaseProductVersion());
                log.info("驱动名称: {}", metaData.getDriverName());
                log.info("驱动版本: {}", metaData.getDriverVersion());
                log.info("JDBC主版本: {}", metaData.getJDBCMajorVersion());
                log.info("JDBC次版本: {}", metaData.getJDBCMinorVersion());
            } catch (SQLException e) {
                log.error("=== 数据库连接失败 ===");
                log.error("错误码: {}", e.getErrorCode());
                log.error("SQL状态: {}", e.getSQLState());
                log.error("错误信息: {}", e.getMessage());
                
                // 提供可能的解决方案
                log.info("\n可能的解决方案:");
                log.info("1. 确认SQL Server服务正在运行");
                log.info("2. 验证连接字符串格式是否正确: {}", env.getProperty("spring.datasource.url"));
                log.info("3. 确认Windows身份验证已正确配置");
                log.info("4. 检查SQL Server是否允许Windows身份验证");
                log.info("5. 如果使用SQL身份验证，确保用户名和密码正确");
                log.info("6. 确认防火墙允许SQL Server端口（默认1433）的通信");
                log.info("7. 检查SQL Server错误日志获取更多信息");
            }
        };
    }
} 