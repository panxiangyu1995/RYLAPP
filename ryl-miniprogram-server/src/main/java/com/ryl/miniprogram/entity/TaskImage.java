package com.ryl.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 任务图片实体类
 */
@Data
@TableName("task_image")
public class TaskImage {
    /**
     * 图片ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 任务ID
     */
    @TableField("task_id")
    private String taskId;
    
    /**
     * 图片URL
     */
    @TableField("image_url")
    private String imageUrl;
    
    /**
     * 图片类型（0：任务图片，1：设备图片，2：结果图片）
     */
    @TableField("image_type")
    private Integer imageType;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}