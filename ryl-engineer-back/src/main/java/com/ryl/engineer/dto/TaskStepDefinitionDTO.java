package com.ryl.engineer.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * 用于在创建任务时定义单个步骤模板的数据传输对象
 */
@Data
public class TaskStepDefinitionDTO {

    @NotNull(message = "步骤索引不能为空")
    @PositiveOrZero(message = "步骤索引必须为非负整数")
    private Integer stepIndex;

    @NotBlank(message = "步骤标题不能为空")
    private String title;
} 