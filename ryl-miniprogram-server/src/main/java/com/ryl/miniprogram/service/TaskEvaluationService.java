package com.ryl.miniprogram.service;

import com.ryl.miniprogram.entity.TaskEvaluation;

/**
 * 任务评价服务接口
 */
public interface TaskEvaluationService {
    
    /**
     * 添加评价
     *
     * @param taskEvaluation 评价信息
     * @return 是否添加成功
     */
    boolean addEvaluation(TaskEvaluation taskEvaluation);
    
    /**
     * 获取任务评价
     *
     * @param taskId 任务ID
     * @return 评价信息
     */
    TaskEvaluation getEvaluationByTaskId(String taskId);
    
    /**
     * 获取客户的评价列表
     *
     * @param customerId 客户ID
     * @return 评价列表
     */
    java.util.List<TaskEvaluation> getEvaluationsByCustomerId(Long customerId);
}