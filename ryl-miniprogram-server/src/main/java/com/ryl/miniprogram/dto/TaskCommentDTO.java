package com.ryl.miniprogram.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 任务评论DTO
 */
@Data
public class TaskCommentDTO {
    
    /**
     * 评论ID，更新时必填
     */
    private Long id;
    
    /**
     * 任务ID
     */
    @NotNull(message = "任务ID不能为空")
    private String taskId;
    
    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;
} 