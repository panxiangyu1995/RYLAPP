package com.ryl.engineer.service.impl;

import com.ryl.engineer.dto.FileStorageInfo;
import com.ryl.engineer.dto.request.TaskStepRecordRequest;
import com.ryl.engineer.entity.Task;
import com.ryl.engineer.entity.TaskStep;
import com.ryl.engineer.entity.TaskStepAttachment;
import com.ryl.engineer.entity.TaskStepRecord;
import com.ryl.engineer.mapper.TaskMapper;
import com.ryl.engineer.mapper.TaskStepAttachmentMapper;
import com.ryl.engineer.mapper.TaskStepMapper;
import com.ryl.engineer.mapper.TaskStepRecordMapper;
import com.ryl.engineer.service.FileService;
import com.ryl.engineer.service.TaskFlowService;
import com.ryl.engineer.utils.UserContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class TaskFlowServiceImpl implements TaskFlowService {

    private final FileService fileService;
    private final TaskStepRecordMapper taskStepRecordMapper;
    private final TaskStepAttachmentMapper taskStepAttachmentMapper;
    private final TaskMapper taskMapper;
    private final TaskStepMapper taskStepMapper;

    public TaskFlowServiceImpl(FileService fileService,
                               TaskStepRecordMapper taskStepRecordMapper,
                               TaskStepAttachmentMapper taskStepAttachmentMapper,
                               TaskMapper taskMapper,
                               TaskStepMapper taskStepMapper) {
        this.fileService = fileService;
        this.taskStepRecordMapper = taskStepRecordMapper;
        this.taskStepAttachmentMapper = taskStepAttachmentMapper;
        this.taskMapper = taskMapper;
        this.taskStepMapper = taskStepMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTaskFlowRecord(String taskId, TaskStepRecordRequest request, MultipartFile[] files) throws IOException {
        // 1. 创建并保存工作记录
        TaskStepRecord record = new TaskStepRecord();
        record.setTaskId(taskId);
        record.setStepId(request.getStepId());
        record.setContent(request.getContent());
        record.setSpentTime(request.getSpentTime());
        record.setOperatorId(UserContextHolder.getUserId()); // 从上下文中获取当前用户ID
        record.setCreateTime(LocalDateTime.now());
        taskStepRecordMapper.insert(record);

        // 2. 如果有附件，则上传并关联
        if (files != null && files.length > 0) {
            Long recordId = record.getId(); // 获取刚插入记录的ID

            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }
                // 3. 调用统一文件服务上传文件
                FileStorageInfo storageInfo = fileService.uploadFile(file, "task-attachments");

                // 4. 创建并保存附件记录
                TaskStepAttachment attachment = new TaskStepAttachment();
                attachment.setRecordId(recordId);
                attachment.setFileName(storageInfo.getOriginalFileName());
                attachment.setFileUrl(storageInfo.getRelativePath()); // 存储相对路径
                attachment.setFileType(storageInfo.getFileType());
                attachment.setFileSize(storageInfo.getFileSize());
                attachment.setCreateTime(LocalDateTime.now());
                taskStepAttachmentMapper.insert(attachment);
            }
        }
    }

    @Override
    @Transactional
    public boolean nextStep(String taskId) {
        Task task = taskMapper.selectOne(Wrappers.<Task>lambdaQuery().eq(Task::getTaskId, taskId));
        if (task == null || "completed".equals(task.getStatus()) || "cancelled".equals(task.getStatus())) {
            log.warn("任务 {} 状态异常或不存在，无法推进。", taskId);
            return false;
        }

        List<TaskStep> steps = taskStepMapper.findByTaskId(taskId);
        int currentStepIndex = task.getCurrentStep();

        if (currentStepIndex >= steps.size() - 1) {
            log.warn("任务 {} 已处于最后一步，无法推进。", taskId);
            return false;
        }

        TaskStep currentStep = steps.get(currentStepIndex);
        LocalDateTime now = LocalDateTime.now();

        // 检查当前步骤是否是上门决策步骤
        if ("site-visit-decision".equals(currentStep.getStepKey())) {
            // 是决策步骤，根据 needSiteVisit 字段决定下一步
            log.info("处理上门决策步骤的下一步逻辑, needSiteVisit: {}", task.getNeedSiteVisit());
            
            // 首先将决策步骤本身标记为完成
            currentStep.setStatus("completed");
            currentStep.setEndTime(now);
            taskStepMapper.updateById(currentStep);

            int nextStepIndex;
            if (task.getNeedSiteVisit() != null && task.getNeedSiteVisit() == 1) {
                // 需要上门，正常进入下一步
                nextStepIndex = currentStepIndex + 1;
            } else {
                // 不需要上门（线上协助），跳到“服务评价”步骤
                // 我们约定“服务评价”是倒数第二个步骤
                nextStepIndex = steps.size() - 2; 

                // 将中间所有步骤标记为 "skipped"
                for (int i = currentStepIndex + 1; i < nextStepIndex; i++) {
                    TaskStep skippedStep = steps.get(i);
                    skippedStep.setStatus("skipped");
                    skippedStep.setStartTime(now);
                    skippedStep.setEndTime(now);
                    taskStepMapper.updateById(skippedStep);
                    log.info("任务 {}, 步骤 {} 已被跳过。", taskId, skippedStep.getTitle());
                }
            }
            
            // 确保下一步索引在有效范围内
            if (nextStepIndex >= steps.size()) {
                log.error("计算出的下一步索引 {} 超出范围，任务流程可能配置错误。", nextStepIndex);
                return false;
            }

            // 更新任务的当前步骤索引
            task.setCurrentStep(nextStepIndex);
            taskMapper.updateById(task);

            // 将计算出的下一步骤标记为进行中
            TaskStep nextStep = steps.get(nextStepIndex);
            nextStep.setStatus("in-progress");
            nextStep.setStartTime(now);
            taskStepMapper.updateById(nextStep);
            
            log.info("任务 {} 从决策步骤推进到步骤 {}: {}", taskId, nextStepIndex, nextStep.getTitle());

        } else {
            // 不是决策步骤，执行默认的“下一步”逻辑
            currentStep.setStatus("completed");
            currentStep.setEndTime(now);
            taskStepMapper.updateById(currentStep);

            TaskStep nextStep = steps.get(currentStepIndex + 1);
            nextStep.setStatus("in-progress");
            nextStep.setStartTime(now);
            taskStepMapper.updateById(nextStep);

            task.setCurrentStep(currentStepIndex + 1);
            taskMapper.updateById(task);

            log.info("任务 {} 成功从步骤 {} 推进到步骤 {}", taskId, currentStepIndex, currentStepIndex + 1);
        }

        return true;
    }

    @Override
    @Transactional
    public boolean prevStep(String taskId) {
        Task task = taskMapper.selectOne(Wrappers.<Task>lambdaQuery().eq(Task::getTaskId, taskId));
        if (task == null || "completed".equals(task.getStatus()) || "cancelled".equals(task.getStatus())) {
            log.warn("任务 {} 状态异常或不存在，无法回退。", taskId);
            return false;
        }

        List<TaskStep> steps = taskStepMapper.findByTaskId(taskId);
        int currentStepIndex = task.getCurrentStep();

        if (currentStepIndex <= 0) {
            log.warn("任务 {} 已处于第一步，无法回退。", taskId);
            return false;
        }

        TaskStep currentStep = steps.get(currentStepIndex);
        currentStep.setStatus("pending");
        // We don't reset the start_date for the current step on regression
        taskStepMapper.updateById(currentStep);

        TaskStep prevStep = steps.get(currentStepIndex - 1);
        prevStep.setStatus("in-progress");
        // We don't reset the completed_date of the previous step, to keep history
        taskStepMapper.updateById(prevStep);

        task.setCurrentStep(currentStepIndex - 1);
        taskMapper.updateById(task);

        log.info("任务 {} 成功从步骤 {} 回退到步骤 {}", taskId, currentStepIndex, currentStepIndex - 1);
        return true;
    }
} 