package com.ryl.miniprogram.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 文件存储配置
 */
@Configuration
@ConfigurationProperties(prefix = "file")
@Data
public class FileStorageConfig {
    
    /**
     * 文件上传目录
     */
    private String uploadDir;
    
    /**
     * 允许的文件类型
     */
    private String allowedFileTypes;
    
    /**
     * 最大文件大小（字节）
     */
    private long maxFileSize;
} 