package com.ryl.engineer.controller;

import com.ryl.engineer.common.Result;
import com.ryl.engineer.dto.request.TaskStepRecordRequest;
import com.ryl.engineer.service.TaskFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/flow")
@Slf4j
@Validated
public class TaskFlowController {

    private final TaskFlowService taskFlowService;

    public TaskFlowController(TaskFlowService taskFlowService) {
        this.taskFlowService = taskFlowService;
    }

    @PostMapping(value = "/records", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Void> addTaskFlowRecord(@PathVariable String taskId,
                                          @RequestPart("record") @Valid TaskStepRecordRequest request,
                                          @RequestPart(value = "files", required = false) MultipartFile[] files) {
        try {
            taskFlowService.addTaskFlowRecord(taskId, request, files);
            return Result.success();
        } catch (Exception e) {
            log.error("添加工作记录失败, taskId: {}", taskId, e);
            return Result.error("添加工作记录失败: " + e.getMessage());
        }
    }
} 