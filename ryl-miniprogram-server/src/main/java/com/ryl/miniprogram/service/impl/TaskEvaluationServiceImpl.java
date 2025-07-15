package com.ryl.miniprogram.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryl.miniprogram.entity.TaskEvaluation;
import com.ryl.miniprogram.mapper.TaskEvaluationMapper;
import com.ryl.miniprogram.service.TaskEvaluationService;
import com.ryl.miniprogram.service.TaskFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 任务评价服务实现类
 */
@Service
public class TaskEvaluationServiceImpl extends ServiceImpl<TaskEvaluationMapper, TaskEvaluation> implements TaskEvaluationService {
    
    @Autowired
    private TaskFlowService taskFlowService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addEvaluation(TaskEvaluation taskEvaluation) {
        // 检查是否已评价
        TaskEvaluation existEvaluation = getEvaluationByTaskId(taskEvaluation.getTaskId());
        if (existEvaluation != null) {
            return false;
        }
        
        // 保存评价
        boolean result = this.save(taskEvaluation);
        
        if (result) {
            // 计算平均评分
            int avgRating = Math.round((taskEvaluation.getServiceAttitude() + 
                                     taskEvaluation.getServiceQuality() + 
                                     taskEvaluation.getOverallRating()) / 3.0f);
                                     
            // 更新任务流程状态为已评价（最后一步）
            taskFlowService.updateTaskStep(
                    taskEvaluation.getTaskId(),
                    5, // 评价步骤（最后一步）
                    taskEvaluation.getCustomerId(),
                    1, // 客户类型
                    "客户",
                    "客户评价：" + avgRating + "分"
            );
        }
        
        return result;
    }
    
    @Override
    public TaskEvaluation getEvaluationByTaskId(String taskId) {
        LambdaQueryWrapper<TaskEvaluation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskEvaluation::getTaskId, taskId);
        
        return this.getOne(queryWrapper);
    }
    
    @Override
    public List<TaskEvaluation> getEvaluationsByCustomerId(Long customerId) {
        LambdaQueryWrapper<TaskEvaluation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskEvaluation::getCustomerId, customerId)
                .orderByDesc(TaskEvaluation::getCreateTime);
        
        return this.list(queryWrapper);
    }
}