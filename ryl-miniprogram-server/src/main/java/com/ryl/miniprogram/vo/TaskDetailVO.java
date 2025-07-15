package com.ryl.miniprogram.vo;

import com.ryl.miniprogram.entity.Task;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 任务详情视图对象，包含所有步骤和相关信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskDetailVO extends Task {
    private List<StepRecordVO> steps;
} 