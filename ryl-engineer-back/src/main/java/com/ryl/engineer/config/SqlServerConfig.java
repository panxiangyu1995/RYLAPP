package com.ryl.engineer.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * SQL Server数据库配置
 */
@Configuration
public class SqlServerConfig {

    /**
     * 数据源属性配置
     */
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 配置数据源
     */
    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .build();
    }

    /**
     * 为MyBatis配置SQL Server特定属性
     */
    @Bean
    public Properties sqlServerProperties() {
        Properties props = new Properties();
        
        // SQL Server特定配置
        props.setProperty("nullCatalogMeansCurrent", "true");
        props.setProperty("sendTimeAsDateTime", "false");
        
        return props;
    }
} 