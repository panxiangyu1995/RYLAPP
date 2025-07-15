package com.ryl.miniprogram.service.impl;

import com.ryl.miniprogram.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 文件存储服务实现
 */
@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {
    
    @Value("${file.upload.path:uploads}")
    private String uploadPath;
    
    @Value("${file.upload.url-prefix:}")
    private String urlPrefix;
    
    /**
     * 存储头像
     */
    @Override
    public String storeAvatar(MultipartFile file, Long userId) throws Exception {
        // 创建目录
        String avatarDir = uploadPath + "/avatars";
        Path dirPath = Paths.get(avatarDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        
        // 生成文件名
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = "avatar_" + userId + "_" + UUID.randomUUID().toString().replaceAll("-", "") + extension;
        
        // 保存文件
        Path targetPath = dirPath.resolve(filename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        // 返回文件URL
        if (urlPrefix.endsWith("/")) {
            return urlPrefix + "avatars/" + filename;
        } else {
            return urlPrefix + "/avatars/" + filename;
        }
    }
} 