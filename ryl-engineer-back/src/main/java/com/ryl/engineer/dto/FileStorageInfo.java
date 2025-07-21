package com.ryl.engineer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传后返回的存储信息DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileStorageInfo {

    /**
     * 完整的文件访问URL
     * 例如: http://localhost:8089/api/v1/uploads/avatars/2024/05/22/uuid.jpg
     */
    private String fullUrl;

    /**
     * 存储在数据库中的相对路径
     * 例如: /uploads/avatars/2024/05/22/uuid.jpg
     */
    private String relativePath;

    /**
     * 文件的原始名称
     * 例如: my_avatar.jpg
     */
    private String originalFileName;

    /**
     * 文件类型 (后缀)
     * 例如: jpg
     */
    private String fileType;

    /**
     * 文件大小，单位 KB
     */
    private long fileSize;
} 