package com.ryl.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 任务评论实体类
 */
@Data
@TableName("task_comment")
public class TaskComment {
    /**
     * 评论ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 任务ID
     */
    @TableField("task_id")
    private String taskId;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 评论人ID
     */
    private Long userId;
    
    /**
     * 评论人类型（0：系统用户，1：客户）
     */
    private Integer userType;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 