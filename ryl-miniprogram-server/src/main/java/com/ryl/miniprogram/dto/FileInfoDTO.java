package com.ryl.miniprogram.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileInfoDTO {
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 原始文件名
     */
    private String originalFileName;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * 文件URL
     */
    private String fileUrl;
} 