package com.ryl.engineer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用程序启动类
 */
@SpringBootApplication
@MapperScan({"com.ryl.engineer.mapper", "com.ryl.engineer.warehouse.mapper"})
public class RylEngineerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RylEngineerApplication.class, args);
    }
} 