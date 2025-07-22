package com.ryl.engineer.controller;

import com.ryl.engineer.common.Result;
import com.ryl.engineer.service.TaskFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 任务流程控制器，负责处理任务步骤的流转
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/task-flow")
public class TaskFlowController {

    @Autowired
    private TaskFlowService taskFlowService;

    /**
     * 将任务推进到下一步
     * @param taskId 任务ID
     * @return 操作结果
     */
    @PostMapping("/{taskId}/next")
    public Result<?> nextStep(@PathVariable String taskId) {
        log.info("请求将任务 {} 推进到下一步", taskId);
        boolean success = taskFlowService.nextStep(taskId);
        if (success) {
            return Result.success("成功推进到下一步");
        }
        return Result.error(400, "无法推进任务，请检查任务状态或当前步骤。");
    }

    /**
     * 将任务回退到上一步
     * @param taskId 任务ID
     * @return 操作结果
     */
    @PostMapping("/{taskId}/prev")
    public Result<?> prevStep(@PathVariable String taskId) {
        log.info("请求将任务 {} 回退到上一步", taskId);
        boolean success = taskFlowService.prevStep(taskId);
        if (success) {
            return Result.success("成功回退到上一步");
        }
        return Result.error(400, "无法回退任务，请检查任务状态或当前步骤。");
    }
} 