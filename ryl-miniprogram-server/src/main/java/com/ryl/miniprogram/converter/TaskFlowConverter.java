package com.ryl.miniprogram.converter;

import com.ryl.miniprogram.entity.TaskFlow;
import com.ryl.miniprogram.entity.TaskStep;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务流程数据转换器
 */
@Component
public class TaskFlowConverter {
    
    /**
     * 将任务流程和步骤转换为前端需要的格式
     *
     * @param taskFlow  任务流程
     * @param taskSteps 任务步骤列表
     * @return 前端格式的任务流程数据
     */
    public Map<String, Object> toFlowData(TaskFlow taskFlow, List<TaskStep> taskSteps) {
        Map<String, Object> result = new HashMap<>();
        
        if (taskFlow != null) {
            result.put("id", taskFlow.getId());
            result.put("taskId", taskFlow.getTaskId());
            result.put("currentStep", taskFlow.getCurrentStep());
            result.put("totalSteps", taskFlow.getTotalSteps());
            result.put("status", taskFlow.getStatus());
            result.put("createTime", taskFlow.getCreateTime());
            result.put("updateTime", taskFlow.getUpdateTime());
        }
        
        List<Map<String, Object>> steps = new ArrayList<>();
        if (taskSteps != null && !taskSteps.isEmpty()) {
            for (TaskStep step : taskSteps) {
                Map<String, Object> stepMap = new HashMap<>();
                stepMap.put("id", step.getId());
                stepMap.put("stepIndex", step.getStepIndex());
                stepMap.put("stepName", getStepName(step.getStepIndex()));
                stepMap.put("stepDescription", step.getDescription());
                stepMap.put("operatorName", step.getCreatorName());
                stepMap.put("remark", step.getRemark());
                stepMap.put("createTime", step.getCreateTime());
                steps.add(stepMap);
            }
        }
        
        result.put("steps", steps);
        return result;
    }
    
    /**
     * 生成任务流程状态描述
     *
     * @param taskFlow 任务流程
     * @return 状态描述
     */
    public String getFlowStatusDescription(TaskFlow taskFlow) {
        if (taskFlow == null) {
            return "未开始";
        }
        
        switch (taskFlow.getStatus()) {
            case 0:
                return "处理中";
            case 1:
                return "已完成";
            case 2:
                return "已取消";
            default:
                return "未知状态";
        }
    }
    
    /**
     * 生成任务步骤状态
     *
     * @param taskFlow  任务流程
     * @param stepIndex 步骤索引
     * @return 步骤状态（0：未开始，1：进行中，2：已完成）
     */
    public int getStepStatus(TaskFlow taskFlow, int stepIndex) {
        if (taskFlow == null) {
            return 0;
        }
        
        if (stepIndex < taskFlow.getCurrentStep()) {
            return 2; // 已完成
        } else if (stepIndex == taskFlow.getCurrentStep()) {
            return 1; // 进行中
        } else {
            return 0; // 未开始
        }
    }
    
    /**
     * 根据步骤索引获取步骤名称
     *
     * @param stepIndex 步骤索引
     * @return 步骤名称
     */
    private String getStepName(Integer stepIndex) {
        switch (stepIndex) {
            case 0:
                return "创建任务";
            case 1:
                return "接收任务";
            case 2:
                return "处理中";
            case 3:
                return "完成处理";
            case 4:
                return "确认完成";
            case 5:
                return "评价";
            default:
                return "未知步骤";
        }
    }
}