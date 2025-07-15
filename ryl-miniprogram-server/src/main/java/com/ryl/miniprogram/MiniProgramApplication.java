package com.ryl.miniprogram;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

/**
 * 瑞屹林微信小程序后端服务启动类
 */
@SpringBootApplication
@MapperScan("com.ryl.miniprogram.mapper")
public class MiniProgramApplication {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    public static void main(String[] args) {
        SpringApplication.run(MiniProgramApplication.class, args);
    }
    
    /**
     * 应用启动时创建上传目录
     */
    @Bean
    public CommandLineRunner initUploadDirectory() {
        return args -> {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 创建头像目录
            File avatarDir = new File(uploadPath + "/avatars");
            if (!avatarDir.exists()) {
                avatarDir.mkdirs();
            }
            
            System.out.println("上传目录已创建: " + uploadDir.getAbsolutePath());
        };
    }
} 