package com.ryl.miniprogram.service;

import com.ryl.miniprogram.entity.Task;
import com.ryl.miniprogram.entity.TaskFlow;
import com.ryl.miniprogram.entity.TaskStep;
import com.ryl.miniprogram.vo.StepRecordVO;

import java.util.List;

/**
 * 任务流程服务接口
 */
public interface TaskFlowService {
    
    /**
     * 获取任务当前步骤
     *
     * @param taskId 任务ID
     * @return 当前步骤信息
     */
    TaskStep getCurrentStep(String taskId);
    
    /**
     * 更新任务步骤
     *
     * @param taskId    任务ID
     * @param stepIndex 步骤索引
     * @param operatorId 操作人ID
     * @param operatorType 操作人类型
     * @param operatorName 操作人名称
     * @param description 步骤描述
     * @return 是否更新成功
     */
    boolean updateTaskStep(String taskId, Integer stepIndex, Long operatorId, Integer operatorType, String operatorName, String description);
    
    /**
     * 获取任务流程历史
     *
     * @param taskId 任务ID
     * @return 任务步骤列表
     */
    List<TaskStep> getTaskFlowHistory(String taskId);

    /**
     * 获取包含详情（图片、附件）的任务流程历史
     *
     * @param taskId 任务ID
     * @return 包含详情的任务步骤列表
     */
    List<StepRecordVO> getTaskFlowHistoryWithDetails(String taskId);

    /**
     * 创建任务流程
     *
     * @param taskId 任务ID
     * @param taskType 任务类型
     * @return 创建的任务流程
     */
    TaskFlow createTaskFlow(String taskId, String taskType);
    
    /**
     * 获取任务流程
     *
     * @param taskId 任务ID
     * @return 任务流程
     */
    TaskFlow getTaskFlow(String taskId);
}