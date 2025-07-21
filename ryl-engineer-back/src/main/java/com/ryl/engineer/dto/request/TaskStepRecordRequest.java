package com.ryl.engineer.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TaskStepRecordRequest {

    @NotNull(message = "步骤ID不能为空")
    private Long stepId;

    @NotBlank(message = "工作内容不能为空")
    private String content;

    private Integer spentTime; // 花费时间 (分钟)
} 