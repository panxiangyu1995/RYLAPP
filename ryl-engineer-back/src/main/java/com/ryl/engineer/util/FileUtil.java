package com.ryl.engineer.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件工具类
 */
@Component
public class FileUtil {
    
    @Value("${file.upload.path:/uploads}")
    private String uploadPath;
    
    @Value("${file.upload.url-prefix:http://localhost:8089}")
    private String urlPrefix;
    
    /**
     * 上传文件
     * @param file 文件
     * @param subDir 子目录
     * @return 文件URL
     * @throws IOException IO异常
     */
    public String uploadFile(MultipartFile file, String subDir) throws IOException {
        // 生成日期目录
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        
        // 生成完整目录路径
        String dirPath = uploadPath + "/" + subDir + "/" + datePath;
        Path dir = Paths.get(dirPath);
        
        // 创建目录
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        
        // 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // 生成新的文件名
        String newFilename = UUID.randomUUID().toString() + extension;
        
        // 完整文件路径
        String filePath = dirPath + "/" + newFilename;
        
        // 保存文件
        File dest = new File(filePath);
        file.transferTo(dest);
        
        // 返回文件URL
        return urlPrefix + "/uploads/" + subDir + "/" + datePath + "/" + newFilename;
    }
    
    /**
     * 删除文件
     * @param url 文件URL
     * @throws IOException IO异常
     */
    public void deleteFile(String url) throws IOException {
        // 从URL中提取文件路径
        String filePath = url.replace(urlPrefix, "");
        
        // 构建文件路径
        Path path = Paths.get(uploadPath + filePath);
        
        // 删除文件
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }
} 