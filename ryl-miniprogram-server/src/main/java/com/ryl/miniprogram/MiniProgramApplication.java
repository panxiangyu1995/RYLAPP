package com.ryl.miniprogram;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.File;

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
    
} 