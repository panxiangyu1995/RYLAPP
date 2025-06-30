package com.ryl.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * SQL Server数据库配置类
 * 用于解决SQL Server与Spring Boot集成的可能问题
 */
@Configuration
public class SqlServerConfiguration {

    @Autowired
    private Environment env;

    /**
     * 自定义数据源配置
     * 解决可能的Lombok与SQL Server集成问题
     */
    @Bean
    public DataSource dataSource() throws SQLException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        
        // 如果使用SQL身份验证，则需要设置用户名和密码
        if (env.getProperty("spring.datasource.username") != null) {
            dataSource.setUsername(env.getProperty("spring.datasource.username"));
        }
        if (env.getProperty("spring.datasource.password") != null) {
            dataSource.setPassword(env.getProperty("spring.datasource.password"));
        }
        
        return dataSource;
    }
} 