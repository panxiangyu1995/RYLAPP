package com.ryl.engineer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 任务图片实体类
 */
@Data
@TableName("task_image")
public class TaskImage {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
} 