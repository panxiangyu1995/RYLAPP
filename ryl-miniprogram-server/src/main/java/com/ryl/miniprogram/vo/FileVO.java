package com.ryl.miniprogram.vo;

import lombok.Data;

/**
 * 文件视图对象
 */
@Data
public class FileVO {
    private Integer id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
} 