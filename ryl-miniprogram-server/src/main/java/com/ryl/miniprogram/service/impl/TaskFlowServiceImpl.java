package com.ryl.miniprogram.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryl.miniprogram.entity.RecordFile;
import com.ryl.miniprogram.entity.RecordImage;
import com.ryl.miniprogram.entity.TaskFlow;
import com.ryl.miniprogram.entity.TaskStep;
import com.ryl.miniprogram.mapper.RecordFileMapper;
import com.ryl.miniprogram.mapper.RecordImageMapper;
import com.ryl.miniprogram.mapper.TaskFlowMapper;
import com.ryl.miniprogram.mapper.TaskStepMapper;
import com.ryl.miniprogram.service.TaskFlowService;
import com.ryl.miniprogram.vo.FileVO;
import com.ryl.miniprogram.vo.ImageVO;
import com.ryl.miniprogram.vo.StepRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务流程服务实现类
 */
@Service
public class TaskFlowServiceImpl extends ServiceImpl<TaskFlowMapper, TaskFlow> implements TaskFlowService {
    
    @Autowired
    private TaskStepMapper taskStepMapper;
    
    @Autowired
    private RecordImageMapper recordImageMapper;

    @Autowired
    private RecordFileMapper recordFileMapper;
    
    @Override
    public TaskStep getCurrentStep(String taskId) {
        // 获取任务流程
        TaskFlow taskFlow = this.getTaskFlow(taskId);
        if (taskFlow == null) {
            return null;
        }
        
        // 获取当前步骤信息
        Integer currentStep = taskFlow.getCurrentStep();
        if (currentStep == null) {
            return null;
        }
        
        // 查询当前步骤记录
        LambdaQueryWrapper<TaskStep> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskStep::getTaskId, taskId)
                .eq(TaskStep::getStepIndex, currentStep)
                .orderByDesc(TaskStep::getCreateTime)
                .last("LIMIT 1");
        
        return taskStepMapper.selectOne(queryWrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTaskStep(String taskId, Integer stepIndex, Long operatorId, Integer operatorType, String operatorName, String description) {
        // 获取任务流程
        TaskFlow taskFlow = getTaskFlow(taskId);
        if (taskFlow == null) {
            return false;
        }
        
        // 验证步骤索引
        if (stepIndex < 0 || stepIndex > 5) { // 假设最大步骤为5
            return false;
        }
        
        // 更新任务流程
        taskFlow.setCurrentStep(stepIndex);
        if (stepIndex == 5) { // 假设5是最后一步
            taskFlow.setStatus(1); // 已完成 - 这个字段已在TaskFlow实体类中标记为@TableField(exist = false)
        }
        Date now = new Date();
        taskFlow.setUpdateTime(now);
        this.updateById(taskFlow);
        
        // 创建步骤记录
        TaskStep taskStep = new TaskStep();
        taskStep.setTaskId(taskId);
        taskStep.setStepId(taskFlow.getId());
        taskStep.setStepIndex(stepIndex);
        taskStep.setDescription(description); // 直接使用传入的描述
        taskStep.setCreatorId(operatorId);
        taskStep.setCreatorName(operatorName);
        taskStep.setCreateTime(now);
        
        taskStepMapper.insert(taskStep);
        
        return true;
    }
    
    @Override
    public List<TaskStep> getTaskFlowHistory(String taskId) {
        LambdaQueryWrapper<TaskStep> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskStep::getTaskId, taskId)
                .orderByAsc(TaskStep::getStepIndex);
        
        return taskStepMapper.selectList(queryWrapper);
    }

    @Override
    public List<StepRecordVO> getTaskFlowHistoryWithDetails(String taskId) {
        List<TaskStep> taskSteps = getTaskFlowHistory(taskId);
        if (taskSteps == null || taskSteps.isEmpty()) {
            return new ArrayList<>();
        }

        return taskSteps.stream().map(taskStep -> {
            StepRecordVO vo = new StepRecordVO();
            BeanUtils.copyProperties(taskStep, vo);

            // 查询图片
            List<RecordImage> images = recordImageMapper.selectByRelationIdAndType(taskStep.getId(), 1); // relationType 1 for step
            if (images != null) {
                vo.setImages(images.stream().map(image -> {
                    ImageVO imageVO = new ImageVO();
                    imageVO.setId(image.getId().intValue());
                    imageVO.setImageUrl(image.getImageUrl());
                    return imageVO;
                }).collect(Collectors.toList()));
            }

            // 查询附件
            List<RecordFile> files = recordFileMapper.selectByRelationIdAndType(taskStep.getId(), 1); // relationType 1 for step
            if (files != null) {
                vo.setFiles(files.stream().map(file -> {
                    FileVO fileVO = new FileVO();
                    fileVO.setId(file.getId().intValue());
                    fileVO.setFileName(file.getFileName());
                    fileVO.setFileUrl(file.getFilePath()); // 注意：实体中的是 filePath
                    fileVO.setFileType(file.getFileType());
                    fileVO.setFileSize(file.getFileSize());
                    return fileVO;
                }).collect(Collectors.toList()));
            }

            return vo;
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskFlow createTaskFlow(String taskId, String taskType) {
        // 检查是否已存在
        TaskFlow existingFlow = getTaskFlow(taskId);
        if (existingFlow != null) {
            return existingFlow;
        }
        
        // 创建新流程
        TaskFlow taskFlow = new TaskFlow();
        taskFlow.setTaskId(taskId);
        taskFlow.setTaskType(taskType);
        taskFlow.setCurrentStep(0);
        taskFlow.setStatus(0); // 进行中 - 这个字段已在TaskFlow实体类中标记为@TableField(exist = false)
        Date now = new Date();
        taskFlow.setCreateTime(now);
        taskFlow.setUpdateTime(now);
        
        this.save(taskFlow);
        
        // 创建初始步骤记录
        TaskStep taskStep = new TaskStep();
        taskStep.setTaskId(taskId);
        taskStep.setStepId(taskFlow.getId());
        taskStep.setStepIndex(0);
        taskStep.setDescription("任务已创建，等待处理");
        taskStep.setCreatorId(0L); // 系统创建
        taskStep.setCreatorName("系统");
        taskStep.setCreateTime(now);
        
        taskStepMapper.insert(taskStep);
        
        return taskFlow;
    }
    
    @Override
    public TaskFlow getTaskFlow(String taskId) {
        LambdaQueryWrapper<TaskFlow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskFlow::getTaskId, taskId);
        
        return this.getOne(queryWrapper);
    }
    
    /**
     * 根据步骤索引获取步骤描述
     *
     * @param stepIndex 步骤索引
     * @return 步骤描述
     */
    private String getStepDescription(Integer stepIndex) {
        switch (stepIndex) {
            case 0:
                return "任务已创建，等待处理";
            case 1:
                return "任务已接收，正在处理";
            case 2:
                return "任务正在处理中";
            case 3:
                return "任务处理完成，等待确认";
            case 4:
                return "任务已确认完成";
            case 5:
                return "任务已评价，流程结束";
            default:
                return "未知步骤描述";
        }
    }
}