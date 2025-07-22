package com.ryl.engineer.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 任务步骤更新请求DTO
 */
@Data
public class TaskStepUpdateRequest {
    /**
     * 任务ID
     */
    @NotBlank(message = "任务ID不能为空")
    private String taskId;
    
    /**
     * 步骤索引
     */
    @NotNull(message = "步骤索引不能为空")
    private Integer stepIndex;
    
    /**
     * 操作类型(start/complete/cancel/skip)
     */
    @NotBlank(message = "操作类型不能为空")
    private String action;
    
    /**
     * 步骤结果
     */
    private String result;
    
    /**
     * 步骤相关图片URL数组
     */
    private List<String> images;
    
    /**
     * 步骤表单数据
     */
    private Map<String, Object> formData;
} 