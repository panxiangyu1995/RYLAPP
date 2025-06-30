package com.ryl.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * Flyway配置类
 * 用于SQL Server数据库迁移
 */
@Configuration
public class FlywayConfiguration {

    @Autowired
    private Environment env;

    /**
     * 配置Flyway实例
     * @param dataSource 数据源
     * @return Flyway实例
     */
    @Bean(initMethod = "migrate")
    @DependsOn("dataSource")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations(env.getProperty("spring.flyway.locations"))
                .baselineOnMigrate(true)
                .outOfOrder(true)
                .load();
    }
} 