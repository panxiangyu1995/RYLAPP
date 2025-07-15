package com.ryl.miniprogram.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储服务接口
 */
public interface FileStorageService {
    
    /**
     * 存储头像
     * @param file 头像文件
     * @param userId 用户ID
     * @return 头像访问URL
     */
    String storeAvatar(MultipartFile file, Long userId) throws Exception;
} 