package com.ryl.engineer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务步骤记录的附件实体类
 */
@Data
@TableName("record_file")
public class TaskStepAttachment {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的任务步骤记录ID (task_step_record.id)
     */
    private Long recordId;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 文件存储的相对路径
     */
    private String fileUrl;

    /**
     * 文件类型 (后缀)
     */
    private String fileType;

    /**
     * 文件大小 (单位: KB)
     */
    private Long fileSize;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
} 