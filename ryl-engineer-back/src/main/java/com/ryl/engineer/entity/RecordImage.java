package com.ryl.engineer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务步骤记录的图片实体类
 */
@Data
@TableName("record_image")
public class RecordImage {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的任务步骤记录ID (step_record.id)
     */
    @TableField("record_id")
    private Long recordId;

    /**
     * 任务ID
     */
    @TableField("task_id")
    private String taskId;
    
    /**
     * 图片存储的相对路径
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
} 