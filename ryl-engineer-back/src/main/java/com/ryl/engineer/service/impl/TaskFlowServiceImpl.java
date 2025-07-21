package com.ryl.engineer.service.impl;

import com.ryl.engineer.dto.FileStorageInfo;
import com.ryl.engineer.dto.request.TaskStepRecordRequest;
import com.ryl.engineer.entity.TaskStepAttachment;
import com.ryl.engineer.entity.TaskStepRecord;
import com.ryl.engineer.mapper.TaskStepAttachmentMapper;
import com.ryl.engineer.mapper.TaskStepRecordMapper;
import com.ryl.engineer.service.FileService;
import com.ryl.engineer.service.TaskFlowService;
import com.ryl.engineer.utils.UserContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class TaskFlowServiceImpl implements TaskFlowService {

    private final FileService fileService;
    private final TaskStepRecordMapper taskStepRecordMapper;
    private final TaskStepAttachmentMapper taskStepAttachmentMapper;

    public TaskFlowServiceImpl(FileService fileService,
                               TaskStepRecordMapper taskStepRecordMapper,
                               TaskStepAttachmentMapper taskStepAttachmentMapper) {
        this.fileService = fileService;
        this.taskStepRecordMapper = taskStepRecordMapper;
        this.taskStepAttachmentMapper = taskStepAttachmentMapper;
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
} 