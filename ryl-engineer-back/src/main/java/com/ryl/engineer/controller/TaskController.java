package com.ryl.engineer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.common.Result;
import com.ryl.engineer.dto.TaskDTO;
import com.ryl.engineer.dto.TaskDetailDTO;
import com.ryl.engineer.dto.TaskFlowDTO;
import com.ryl.engineer.dto.TaskStepDefinitionDTO;
import com.ryl.engineer.dto.request.CreateTaskRequest;
import com.ryl.engineer.dto.request.RejectTaskRequest;
import com.ryl.engineer.dto.request.TaskFlowStatusRequest;
import com.ryl.engineer.dto.request.TaskQueryRequest;
import com.ryl.engineer.dto.request.TaskStepUpdateRequest;
import com.ryl.engineer.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 任务控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 创建任务
     * @param request 创建任务请求
     * @return 任务ID
     */
    @PostMapping
    public Result<String> createTask(@RequestBody @Valid CreateTaskRequest request) {
        try {
            // 打印请求体，方便调试
            log.info("收到创建任务请求: {}", objectMapper.writeValueAsString(request));
            log.info("日期时间字段 - 开始时间: {}, 结束时间: {}", request.getStartTime(), request.getEndTime());
            
            String taskId = taskService.createTask(request);
            log.info("任务创建成功，ID: {}", taskId);
            
            return Result.success("创建任务成功", taskId);
        } catch (JsonProcessingException e) {
            log.error("解析请求JSON失败", e);
            return Result.error(400, "请求格式错误: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("参数校验失败", e);
            return Result.error(400, "参数校验失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("创建任务失败", e);
            return Result.error(500, "创建任务失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页查询任务列表
     * @param request 查询条件
     * @return 任务分页列表
     */
    @GetMapping
    public Result<PageResult<TaskDetailDTO>> getTaskPage(TaskQueryRequest request) {
        PageResult<TaskDetailDTO> page = taskService.getTaskPage(request);
        return Result.success(page);
    }
    
    /**
     * 获取任务详情
     * @param taskId 任务ID
     * @return 任务详情
     */
    @GetMapping("/{taskId}")
    public Result<TaskDTO> getTaskDetail(@PathVariable("taskId") String taskId) {
        TaskDTO task = taskService.getTaskDetail(taskId);
        if (task == null) {
            return Result.error(404, "任务不存在");
        }
        return Result.success(task);
    }
    
    /**
     * 获取任务流程
     * @param taskId 任务ID
     * @return 任务流程
     */
    @GetMapping("/{taskId}/flow")
    public Result<TaskFlowDTO> getTaskFlow(@PathVariable("taskId") String taskId) {
        TaskFlowDTO flow = taskService.getTaskFlow(taskId);
        if (flow == null) {
            return Result.error(404, "任务流程不存在");
        }
        return Result.success(flow);
    }
    
    /**
     * 为来自小程序的任务初始化步骤
     * @param taskId 任务ID
     * @param steps 步骤定义列表
     * @return 是否成功
     */
    @PostMapping("/{taskId}/initialize-steps")
    public Result<Boolean> initializeSteps(
            @PathVariable("taskId") String taskId,
            @RequestBody @Valid List<TaskStepDefinitionDTO> steps) {
        try {
            boolean success = taskService.initializeTaskSteps(taskId, steps);
            if (success) {
                return Result.success("任务步骤初始化成功", true);
            } else {
                return Result.error(400, "任务步骤初始化失败，可能任务不存在或已初始化。");
            }
        } catch (Exception e) {
            log.error("初始化任务步骤时发生异常, taskId: {}", taskId, e);
            return Result.error(500, "服务器内部错误: " + e.getMessage());
        }
    }
    
    /**
     * 获取任务状态历史
     * @param taskId 任务ID
     * @return 状态历史列表
     */
    @GetMapping("/{taskId}/status-history")
    public Result<List<Map<String, Object>>> getTaskStatusHistory(@PathVariable("taskId") String taskId) {
        List<Map<String, Object>> history = taskService.getTaskStatusHistory(taskId);
        return Result.success(history);
    }
    
    /**
     * 更新任务步骤
     * @param request 更新步骤请求
     * @return 是否成功
     */
    @PostMapping("/steps")
    public Result<Boolean> updateTaskStep(@RequestBody @Valid TaskStepUpdateRequest request) {
        boolean success = taskService.updateTaskStep(request);
        if (!success) {
            return Result.error(400, "更新任务步骤失败");
        }
        return Result.success("更新任务步骤成功", true);
    }
    
    /**
     * 更新任务
     * @param taskId 任务ID
     * @param taskDTO 任务信息
     * @return 是否成功
     */
    @PutMapping("/{taskId}")
    public Result<Boolean> updateTask(@PathVariable("taskId") String taskId, @RequestBody TaskDTO taskDTO) {
        boolean success = taskService.updateTask(taskId, taskDTO);
        if (!success) {
            return Result.error(400, "更新任务失败");
        }
        return Result.success("更新任务成功", true);
    }
    
    /**
     * 取消任务
     * @param taskId 任务ID
     * @param reason 取消原因
     * @return 是否成功
     */
    @PostMapping("/{taskId}/cancel")
    public Result<Boolean> cancelTask(@PathVariable("taskId") String taskId, @RequestParam("reason") String reason) {
        boolean success = taskService.cancelTask(taskId, reason);
        if (!success) {
            return Result.error(400, "取消任务失败");
        }
        return Result.success("取消任务成功", true);
    }
    
    /**
     * 分配工程师
     * @param taskId 任务ID
     * @param engineerIds 工程师ID列表
     * @return 是否成功
     */
    @PostMapping("/{taskId}/engineers")
    public Result<Boolean> assignEngineers(@PathVariable("taskId") String taskId, @RequestBody List<Long> engineerIds) {
        boolean success = taskService.assignEngineers(taskId, engineerIds);
        if (!success) {
            return Result.error(400, "分配工程师失败");
        }
        return Result.success("分配工程师成功", true);
    }
    
    /**
     * 获取任务统计信息
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getTaskStatistics() {
        Map<String, Object> statistics = taskService.getTaskStatistics();
        return Result.success(statistics);
    }
    
    /**
     * 决定任务是否需要上门
     * @param taskId 任务ID
     * @param data 包含stepIndex、requiresVisit和visitAppointmentTime(可选)的数据对象
     * @return 是否成功
     */
    @PostMapping("/{taskId}/decide-visit")
    public Result<Boolean> decideSiteVisit(
            @PathVariable("taskId") String taskId,
            @RequestBody Map<String, Object> data) {
        log.info("收到任务上门决策请求: taskId={}, data={}", taskId, data);
        
        try {
            Integer stepIndex = data.get("stepIndex") instanceof Number ? 
                ((Number) data.get("stepIndex")).intValue() : null;
            Boolean requiresVisit = (Boolean) data.get("requiresVisit");
            String visitAppointmentTime = data.get("visitAppointmentTime") != null ? 
                data.get("visitAppointmentTime").toString() : null;
            
            if (stepIndex == null) {
                return Result.error(400, "步骤索引不能为空");
            }
            
            if (requiresVisit == null) {
                return Result.error(400, "上门决策不能为空");
            }
            
            // 更新任务上门决策
            boolean success = taskService.updateTaskSiteVisitDecision(taskId, stepIndex, requiresVisit, visitAppointmentTime);
            
            if (!success) {
                log.error("更新任务上门决策失败: taskId={}", taskId);
                return Result.error(400, "更新任务上门决策失败");
            }
            
            log.info("更新任务上门决策成功: taskId={}, requiresVisit={}", taskId, requiresVisit);
            return Result.success("更新任务上门决策成功", true);
        } catch (Exception e) {
            log.error("更新任务上门决策异常: taskId={}, 错误信息={}", taskId, e.getMessage(), e);
            return Result.error(500, "更新任务上门决策异常: " + e.getMessage());
        }
    }

    /**
     * 重置任务的上门决策
     * @param taskId 任务ID
     * @return 是否成功
     */
    @PostMapping("/{taskId}/reset-decision")
    public Result<Boolean> resetSiteVisitDecision(@PathVariable("taskId") String taskId) {
        log.info("收到重置任务上门决策请求: taskId={}", taskId);
        try {
            boolean success = taskService.resetTaskSiteVisitDecision(taskId);
            if (!success) {
                log.error("重置任务上门决策失败: taskId={}", taskId);
                return Result.error(400, "重置任务上门决策失败");
            }
            log.info("重置任务上门决策成功: taskId={}", taskId);
            return Result.success("重置任务上门决策成功", true);
        } catch (Exception e) {
            log.error("重置任务上门决策时发生异常: taskId={}, 错误: {}", taskId, e.getMessage(), e);
            return Result.error(500, "服务器内部错误: " + e.getMessage());
        }
    }
    
    /**
     * 确认任务报价
     * @param taskId 任务ID
     * @param data 包含confirmed的数据对象
     * @return 是否成功
     */
    @PostMapping("/{taskId}/confirm-price")
    public Result<Boolean> confirmTaskPrice(
            @PathVariable("taskId") String taskId,
            @RequestBody Map<String, Object> data) {
        log.info("收到确认任务报价请求: taskId={}, data={}", taskId, data);
        
        try {
            Boolean confirmed = (Boolean) data.get("confirmed");
            
            if (confirmed == null) {
                return Result.error(400, "确认状态不能为空");
            }
            
            // 更新任务报价确认状态
            boolean success = taskService.updateTaskPriceConfirmation(taskId, confirmed);
            
            if (!success) {
                log.error("更新任务报价确认状态失败: taskId={}", taskId);
                return Result.error(400, "更新任务报价确认状态失败");
            }
            
            log.info("更新任务报价确认状态成功: taskId={}, confirmed={}", taskId, confirmed);
            return Result.success("更新任务报价确认状态成功", true);
        } catch (Exception e) {
            log.error("更新任务报价确认状态异常: taskId={}, 错误信息={}", taskId, e.getMessage(), e);
            return Result.error(500, "更新任务报价确认状态异常: " + e.getMessage());
        }
    }
    
    /**
     * 设置任务报价
     * @param taskId 任务ID
     * @param data 包含price和stepIndex的数据对象
     * @return 是否成功
     */
    @PostMapping("/{taskId}/set-price")
    public Result<Boolean> setTaskPrice(
            @PathVariable("taskId") String taskId,
            @RequestBody Map<String, Object> data) {
        log.info("收到设置任务报价请求: taskId={}, data={}", taskId, data);
        
        try {
            Double price = data.get("price") instanceof Number ? 
                ((Number) data.get("price")).doubleValue() : null;
            Integer stepIndex = data.get("stepIndex") instanceof Number ? 
                ((Number) data.get("stepIndex")).intValue() : null;
            
            if (price == null) {
                return Result.error(400, "报价不能为空");
            }
            
            if (stepIndex == null) {
                return Result.error(400, "步骤索引不能为空");
            }
            
            // 更新任务报价
            boolean success = taskService.updateTaskPrice(taskId, stepIndex, price);
            
            if (!success) {
                log.error("更新任务报价失败: taskId={}", taskId);
                return Result.error(400, "更新任务报价失败");
            }
            
            log.info("更新任务报价成功: taskId={}, price={}", taskId, price);
            return Result.success("更新任务报价成功", true);
        } catch (Exception e) {
            log.error("更新任务报价异常: taskId={}, 错误信息={}", taskId, e.getMessage(), e);
            return Result.error(500, "更新任务报价异常: " + e.getMessage());
        }
    }
    
    /**
     * 获取任务报价确认状态
     * @param taskId 任务ID
     * @return 报价确认状态
     */
    @GetMapping("/{taskId}/price-confirmation")
    public Result<Map<String, Object>> getTaskPriceConfirmation(@PathVariable("taskId") String taskId) {
        try {
            Map<String, Object> result = taskService.getTaskPriceConfirmation(taskId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取任务报价确认状态异常: taskId={}, 错误信息={}", taskId, e.getMessage(), e);
            return Result.error(500, "获取任务报价确认状态异常: " + e.getMessage());
        }
    }
    
    /**
     * 通知客户报价
     * @param taskId 任务ID
     * @return 是否成功
     */
    @PostMapping("/{taskId}/notify-customer")
    public Result<Boolean> notifyCustomer(@PathVariable("taskId") String taskId) {
        try {
            boolean success = taskService.notifyCustomerOfPrice(taskId);
            if (!success) {
                return Result.error(400, "通知客户失败");
            }
            return Result.success("通知客户成功", true);
        } catch (Exception e) {
            log.error("通知客户异常: taskId={}, 错误信息={}", taskId, e.getMessage(), e);
            return Result.error(500, "通知客户异常: " + e.getMessage());
        }
    }

    /**
     * 确认收款
     * @param taskId 任务ID
     * @return 是否成功
     */
    @PostMapping("/{taskId}/confirm-payment")
    public Result<Boolean> confirmPayment(@PathVariable("taskId") String taskId) {
        try {
            boolean success = taskService.confirmPayment(taskId);
            if (!success) {
                return Result.error(400, "确认收款失败");
            }
            return Result.success("确认收款成功", true);
        } catch (Exception e) {
            log.error("确认收款异常: taskId={}, 错误信息={}", taskId, e.getMessage(), e);
            return Result.error(500, "确认收款异常: " + e.getMessage());
        }
    }
    
    /**
     * 转出任务
     * @param taskId 任务ID
     * @param data 包含engineerId和note的数据
     * @return 是否成功
     */
    @PostMapping("/{taskId}/transfer")
    public Result<Boolean> transferTask(
            @PathVariable("taskId") String taskId,
            @RequestBody Map<String, Object> data) {
        try {
            log.info("收到任务转出请求: taskId={}, data={}", taskId, data);
            
            // 从请求体中获取engineerId, note 和 operatorId
            if (data.get("engineerId") == null || data.get("operatorId") == null) {
                return Result.error(400, "新负责人ID和操作员ID不能为空");
            }
            Long engineerId = Long.valueOf(data.get("engineerId").toString());
            Long operatorId = Long.valueOf(data.get("operatorId").toString());
            String note = data.get("note") != null ? data.get("note").toString() : null;
            Boolean isOperatorAdmin = (Boolean) data.getOrDefault("isOperatorAdmin", false);
            
            boolean success = taskService.transferTask(taskId, engineerId, note, operatorId, isOperatorAdmin);
            if (!success) {
                log.error("转出任务失败: taskId={}, engineerId={}", taskId, engineerId);
                return Result.error(400, "转出任务失败");
            }
            
            log.info("转出任务成功: taskId={}, engineerId={}", taskId, engineerId);
            return Result.success("转出任务成功", true);
        } catch (Exception e) {
            log.error("转出任务异常: taskId={}, 错误信息={}", taskId, e.getMessage(), e);
            return Result.error(500, "转出任务异常: " + e.getMessage());
        }
    }
    
    /**
     * 更新任务状态
     * @param taskId 任务ID
     * @param requestBody 请求体，包含status和note
     * @return 是否成功
     */
    @PutMapping("/{taskId}/status")
    public Result<Boolean> updateTaskStatus(
            @PathVariable("taskId") String taskId,
            @RequestBody Map<String, String> requestBody) {
        log.info("收到更新任务状态请求: taskId={}, requestBody={}", taskId, requestBody);
        
        String status = requestBody.get("status");
        String note = requestBody.get("note");
        
        if (status == null || status.isEmpty()) {
            return Result.error(400, "状态不能为空");
        }
        
        try {
            boolean success = taskService.updateTaskStatus(taskId, status, note);
            if (!success) {
                return Result.error(400, "更新任务状态失败");
            }
            return Result.success("更新任务状态成功", true);
        } catch (Exception e) {
            log.error("更新任务状态异常: {}", e.getMessage(), e);
            return Result.error(500, "更新任务状态异常: " + e.getMessage());
        }
    }

    /**
     * 工程师接受任务
     * @param id 任务ID
     * @return Result
     */
    @PostMapping("/{id}/accept")
    public Result<?> acceptTask(@PathVariable("id") Long id) {
        try {
            taskService.acceptTask(id);
            return Result.success("任务已接受");
        } catch (Exception e) {
            log.error("接受任务失败, 任务ID: {}", id, e);
            return Result.error(500, e.getMessage());
        }
    }

    /**
     * 工程师拒绝任务
     * @param id 任务ID
     * @param request 拒绝请求体
     * @return Result
     */
    @PostMapping("/{id}/reject")
    public Result<?> rejectTask(@PathVariable("id") Long id, @RequestBody RejectTaskRequest request) {
        try {
            taskService.rejectTask(id, request);
            return Result.success("任务已拒绝并处理");
        } catch (Exception e) {
            log.error("拒绝任务失败, 任务ID: {}", id, e);
            return Result.error(500, e.getMessage());
        }
    }
} 