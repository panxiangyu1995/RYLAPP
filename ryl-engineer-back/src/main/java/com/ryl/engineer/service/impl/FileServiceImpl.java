package com.ryl.engineer.service.impl;

import com.ryl.engineer.dto.FileStorageInfo;
import com.ryl.engineer.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Value("${ryl.file.upload-path}")
    private String uploadPath;

    @Value("${ryl.file.url-prefix}")
    private String urlPrefix;

    @Override
    public FileStorageInfo uploadFile(MultipartFile file, String subDir) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 1. 生成基于日期的目录路径 (yyyy/MM/dd)
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        // 2. 构建完整的物理存储目录 (根路径 + 业务子目录 + 日期路径)
        Path fullDirectoryPath = Paths.get(uploadPath, subDir, datePath);

        // 3. 如果目录不存在，则创建它
        if (!Files.exists(fullDirectoryPath)) {
            Files.createDirectories(fullDirectoryPath);
            log.info("创建目录: {}", fullDirectoryPath.toAbsolutePath());
        }

        // 4. 生成新的唯一文件名
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = StringUtils.getFilenameExtension(originalFileName);
        String newFileName = UUID.randomUUID().toString().replace("-", "") + "." + fileExtension;

        // 5. 构建文件的完整物理路径并保存
        Path destinationFilePath = fullDirectoryPath.resolve(newFileName);
        file.transferTo(destinationFilePath.toFile());
        log.info("文件已保存至: {}", destinationFilePath.toAbsolutePath());

        // 6. 构建对外访问的URL
        // 相对路径，用于存库和静态资源映射，必须与WebMvcConfig中的handler匹配
        String relativePath = "/api/v1/uploads/" + subDir + "/" + datePath + "/" + newFileName;
        // 完整URL，用于返回给前端
        String fullUrl = urlPrefix + relativePath;

        // 7. 封装并返回文件信息
        return new FileStorageInfo(
                fullUrl,
                relativePath,
                originalFileName,
                fileExtension,
                file.getSize() / 1024 // 文件大小转换为KB
        );
    }

    @Override
    public org.springframework.core.io.Resource loadFileAsResource(String relativePath) {
        try {
            // 从相对路径中移除URL前缀部分 (例如 /api/v1)
            String cleanRelativePath = relativePath.replaceFirst("^/api/v1", "");

            Path filePath = Paths.get(uploadPath).resolve(cleanRelativePath).normalize();
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                log.warn("无法读取文件或文件不存在: {}", filePath);
                throw new RuntimeException("文件未找到: " + relativePath);
            }
        } catch (java.net.MalformedURLException ex) {
            log.error("路径格式错误: {}", relativePath, ex);
            throw new RuntimeException("文件路径格式错误: " + relativePath, ex);
        }
    }
} 