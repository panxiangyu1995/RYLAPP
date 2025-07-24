package com.ryl.engineer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 附件信息数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDTO {

    /**
     * 附件ID
     */
    private Long id;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 文件类型 (例如 'pdf', 'docx')
     */
    private String fileType;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;
} 