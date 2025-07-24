package com.ryl.miniprogram.controller;

import com.ryl.miniprogram.dto.TaskDTO;
import com.ryl.miniprogram.entity.Task;
import com.ryl.miniprogram.entity.TaskEvaluation;
import com.ryl.miniprogram.entity.TaskStep;
import com.ryl.miniprogram.service.FileService;
import com.ryl.miniprogram.service.TaskEvaluationService;
import com.ryl.miniprogram.service.TaskFlowService;
import com.ryl.miniprogram.service.TaskService;
import com.ryl.miniprogram.vo.PageVO;
import com.ryl.miniprogram.vo.ResultVO;
import com.ryl.miniprogram.vo.TaskDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import com.ryl.miniprogram.vo.StepRecordVO;

/**
 * 任务相关接口控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/task")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private TaskFlowService taskFlowService;
    
    @Autowired
    private FileService fileService;
    
    @Autowired
    private TaskEvaluationService taskEvaluationService;
    
    /**
     * 获取任务详情
     */
    @GetMapping("/{id}")
    public ResultVO<?> getTaskDetail(@PathVariable Long id) {
        TaskDetailVO taskDetailVO = taskService.getTaskDetailById(id);
        if (taskDetailVO == null) {
            return ResultVO.failed("任务不存在");
        }
        log.info("获取任务详情成功, taskId: {}", taskDetailVO.getTaskId());
        return ResultVO.success(taskDetailVO);
    }
    
    /**
     * 创建任务
     */
    @PostMapping("/create")
    public ResultVO<?> createTask(@RequestBody @Validated TaskDTO taskDTO, HttpServletRequest request) {
        log.info("接收到任务创建请求，taskId: {}, title: {}, taskType: {}", taskDTO.getTaskId(), taskDTO.getTitle(), taskDTO.getTaskType());
        
        try {
            // 使用 jackson 将对象转换为 JSON 字符串
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(taskDTO);
            log.info("完整请求体: {}", requestBody);
        } catch (Exception e) {
            log.error("序列化请求体失败: {}", e.getMessage());
        }
        Long userId = (Long) request.getAttribute("userId");
        
        // 创建任务
        Task task = new Task();
        
        // 手动复制属性
        task.setId(taskDTO.getId());
        task.setTaskId(taskDTO.getTaskId()); // 设置任务编号，确保不为null
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setTaskType(taskDTO.getTaskType());
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());
        task.setStartDate(taskDTO.getStartDate());
        task.setDeadline(taskDTO.getDeadline());
        // 删除对remark字段的设置
        
        // 设置任务类型
        task.setTaskType(taskDTO.getTaskType());
        
        // 记录日志检查taskId是否正确设置
        log.info("准备保存的任务信息, taskId: {}, title: {}", task.getTaskId(), task.getTitle());
        
        // 设置客户信息
        if (taskDTO.getCustomer() != null) {
            // 如果有客户ID，则设置客户ID
            if (taskDTO.getCustomer().getId() != null) {
                task.setCustomerId(taskDTO.getCustomer().getId());
            } else {
                task.setCustomerId(userId);
            }
            
            // 设置客户名称
            task.setCustomerName(taskDTO.getCustomer().getName());
            
            // 设置客户级别，默认为"C"
            task.setCustomerLevel("C");
            
            // 设置联系人
            task.setContactPerson(taskDTO.getCustomer().getContact());
            
            // 设置联系电话
            task.setContactPhone(taskDTO.getCustomer().getPhone());
            
            // 设置地址
            task.setLocation(taskDTO.getCustomer().getAddress());
        } else {
            task.setCustomerId(userId);
            // 默认客户级别为"C"
            task.setCustomerLevel("C");
        }
        
        // 设置设备信息
        if (taskDTO.getDevice() != null) {
            task.setDeviceName(taskDTO.getDevice().getName());
            task.setDeviceBrand(taskDTO.getDevice().getBrand());
            task.setDeviceModel(taskDTO.getDevice().getModel());
            task.setDeviceType(taskDTO.getDevice().getType()); // 新增：设置仪器类型
            // 移除故障描述和需求描述字段，避免与description重复
            task.setQuantity(taskDTO.getDevice().getQuantity());
            task.setVerificationType(taskDTO.getDevice().getVerificationType());
            task.setPurpose(taskDTO.getDevice().getPurpose());
            task.setAppointmentTime(taskDTO.getDevice().getAppointmentTime());
        }
        
        task.setStatus("pending"); // 默认待处理状态
        boolean result = taskService.save(task);
        
        if (result) {
            // 创建任务流程
            taskFlowService.createTaskFlow(task.getTaskId(), task.getTaskType());
            
            // 处理设备图片上传（如果有）
            if (taskDTO.getDevice() != null) {
                // 设备信息已经在 DTO 中，可以在后续处理中使用
                log.info("设备信息: {}", taskDTO.getDevice());
            }
            
            return ResultVO.success(task.getId(), "创建成功");
        } else {
            return ResultVO.failed("创建失败");
        }
    }
    
    /**
     * 获取当前用户当天提交订单数
     */
    @GetMapping("/daily-count")
    public ResultVO<?> getDailyCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        int count = taskService.countDailyTasks(userId);
        return ResultVO.success(count);
    }
    
    /**
     * 更新任务
     */
    @PostMapping("/update")
    public ResultVO<?> updateTask(@RequestBody @Validated TaskDTO taskDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Task existTask = taskService.getById(taskDTO.getId());
        if (existTask == null) {
            return ResultVO.failed("任务不存在");
        }
        if (!existTask.getCustomerId().equals(userId)) {
            return ResultVO.forbidden("无权修改他人任务");
        }
        
        Task task = new Task();
        BeanUtils.copyProperties(taskDTO, task);
        boolean result = taskService.update(task);
        return result ? ResultVO.success(null, "更新成功") : ResultVO.failed("更新失败");
    }
    
    
    /**
     * 更新任务状态
     */
    @PostMapping("/status/{id}/{status}")
    public ResultVO<?> updateTaskStatus(@PathVariable Long id, @PathVariable String status, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Task existTask = taskService.getById(id);
        if (existTask == null) {
            return ResultVO.failed("任务不存在");
        }
        if (!existTask.getCustomerId().equals(userId)) {
            return ResultVO.forbidden("无权修改他人任务");
        }
        
        boolean result = taskService.updateStatus(id, status);
        return result ? ResultVO.success(null, "更新成功") : ResultVO.failed("更新失败");
    }
    
    /**
     * 获取当前用户的任务列表
     */
    @GetMapping("/list")
    public ResultVO<?> getTaskList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Task> list = taskService.listByCustomerId(userId);
        return ResultVO.success(list);
    }
    
    /**
     * 分页获取任务列表
     */
    @GetMapping("/page")
    public ResultVO<?> getTaskPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<Task> pageVO = taskService.listByPage(pageNum, pageSize);
        return ResultVO.success(pageVO);
    }
    
    /**
     * 根据状态获取任务列表
     */
    @GetMapping("/status/{status}")
    public ResultVO<?> getTaskByStatus(@PathVariable String status) {
        List<Task> list = taskService.listByStatus(status);
        return ResultVO.success(list);
    }
    
    /**
     * 获取任务当前步骤
     */
    @GetMapping("/{id}/step")
    public ResultVO<?> getTaskCurrentStep(@PathVariable Long id) {
        TaskStep currentStep = taskFlowService.getCurrentStep(String.valueOf(id));
        return ResultVO.success(currentStep);
    }
    
    /**
     * 获取任务步骤历史
     */
    @GetMapping("/{id}/steps")
    public ResultVO<?> getTaskSteps(@PathVariable Long id) {
        List<StepRecordVO> steps = taskFlowService.getTaskFlowHistoryWithDetails(String.valueOf(id));
        return ResultVO.success(steps);
    }
    
    /**
     * 客户确认任务报价
     */
    @PostMapping("/{taskId}/confirm-price")
    public ResultVO<?> confirmTaskPrice(@PathVariable String taskId, HttpServletRequest request) {
        // 可选：在这里添加一层权限校验，确保是任务关联的客户在操作
        Long userId = (Long) request.getAttribute("userId");
        Task task = taskService.getById(Long.parseLong(taskId)); // 假设 task_id 是数字，如果不是需要调整
        if (task == null || !task.getCustomerId().equals(userId)) {
            return ResultVO.forbidden("无权操作");
        }

        taskService.confirmPrice(taskId);
        return ResultVO.success(null, "报价确认成功");
    }

    /**
     * 更新任务步骤
     */
    @PostMapping("/{id}/step/{stepIndex}")
    public ResultVO<?> updateTaskStep(
            @PathVariable Long id,
            @PathVariable Integer stepIndex,
            @RequestParam(required = false) String remark,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String userName = (String) request.getAttribute("userName");
        
        boolean result = taskFlowService.updateTaskStep(String.valueOf(id), stepIndex, userId, 1, userName, remark);
        return result ? ResultVO.success(null, "更新成功") : ResultVO.failed("更新失败");
    }
    
    /**
     * 提交任务评价
     */
    @PostMapping("/{id}/evaluation")
    public ResultVO<?> submitEvaluation(
            @PathVariable Long id,
            @RequestBody TaskEvaluation evaluation,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Task existTask = taskService.getById(id);
        if (existTask == null) {
            return ResultVO.failed("任务不存在");
        }
        if (!existTask.getCustomerId().equals(userId)) {
            return ResultVO.forbidden("无权修改他人任务");
        }
        
        evaluation.setTaskId(String.valueOf(id));
        evaluation.setCustomerId(userId);
        taskEvaluationService.addEvaluation(evaluation);
        
        return ResultVO.success(null, "评价成功");
    }
    
    /**
     * 获取任务评价
     */
    @GetMapping("/{id}/evaluation")
    public ResultVO<?> getTaskEvaluation(@PathVariable Long id) {
        TaskEvaluation evaluation = taskEvaluationService.getEvaluationByTaskId(String.valueOf(id));
        return ResultVO.success(evaluation);
    }
    
    /**
     * 接收来自工程师APP的报价通知
     */
    @PostMapping("/notify-price")
    public ResultVO<?> notifyPrice(@RequestBody Map<String, Object> payload) {
        log.info("接收到报价通知请求: {}", payload);
        try {
            Long customerId = Long.valueOf(payload.get("customerId").toString());
            String taskId = (String) payload.get("taskId");
            java.math.BigDecimal price = new java.math.BigDecimal(payload.get("price").toString());

            taskService.notifyCustomerOfPrice(customerId, taskId, price);

            return ResultVO.success(null, "通知成功");
        } catch (Exception e) {
            log.error("处理报价通知失败", e);
            return ResultVO.failed("处理报价通知失败: " + e.getMessage());
        }
    }
} 