package com.ryl.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * MyBatis配置类
 * 用于配置SQL Server方言和其他数据库相关设置
 */
@Configuration
@MapperScan("com.ryl.engineer.mapper")
public class MyBatisConfig {
    
    @Autowired
    private Environment env;
    
    /**
     * 配置SqlSessionFactory
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        
        // 设置实体类别名包
        sessionFactory.setTypeAliasesPackage(env.getProperty("mybatis.type-aliases-package"));
        
        // 设置mapper.xml文件位置
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(env.getProperty("mybatis.mapper-locations"));
            sessionFactory.setMapperLocations(resources);
        } catch (IOException e) {
            throw new RuntimeException("无法找到mapper.xml文件", e);
        }
        
        // 配置MyBatis属性
        Properties properties = new Properties();
        properties.setProperty("database", "SQLSERVER");
        properties.setProperty("dialect", "sqlserver");
        sessionFactory.setConfigurationProperties(properties);
        
        // 自定义MyBatis配置
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCallSettersOnNulls(true);
        configuration.setUseGeneratedKeys(true);
        
        sessionFactory.setConfiguration(configuration);
        
        return sessionFactory.getObject();
    }
} 