package com.ryl.engineer.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 任务流程状态更新请求DTO
 */
@Data
public class TaskFlowStatusRequest {
    /**
     * 任务ID
     */
    @NotBlank(message = "任务ID不能为空")
    private String taskId;
    
    /**
     * 当前步骤索引
     */
    @NotNull(message = "当前步骤索引不能为空")
    private Integer currentStepIndex;
    
    /**
     * 下一步步骤索引
     */
    @NotNull(message = "下一步步骤索引不能为空")
    private Integer nextStepIndex;
    
    /**
     * 操作类型(update/complete/skip)
     */
    @NotBlank(message = "操作类型不能为空")
    private String action;
    
    /**
     * 备注说明
     */
    private String remark;
} 