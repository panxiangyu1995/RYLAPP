package com.ryl.miniprogram;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;

/**
 * 瑞屹林微信小程序后端服务启动类
 */
@SpringBootApplication
@MapperScan("com.ryl.miniprogram.mapper")
@EnableAsync
public class MiniProgramApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniProgramApplication.class, args);
    }

    @Bean
    public CommandLineRunner databaseConnectionTester(DataSource dataSource) {
        return args -> {
            System.out.println("==========================================================");
            System.out.println("====== EXECUTING DATABASE CONNECTION TEST RUNNER... ======");
            try (Connection connection = dataSource.getConnection()) {
                System.out.println("====== DATABASE CONNECTION TEST SUCCESSFUL! ======");
                System.out.println("====== Connection Valid: " + connection.isValid(10));
                System.out.println("==========================================================");
            } catch (Exception e) {
                System.err.println("====== DATABASE CONNECTION TEST FAILED!      ======");
                e.printStackTrace(System.err);
                System.err.println("==========================================================");
            }
        };
    }
} 