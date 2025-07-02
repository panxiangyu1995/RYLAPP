package com.ryl.engineer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.dto.EngineerDTO;
import com.ryl.engineer.dto.TaskDTO;
import com.ryl.engineer.dto.TaskFlowDTO;
import com.ryl.engineer.dto.TaskStepDTO;
import com.ryl.engineer.dto.request.CreateTaskRequest;
import com.ryl.engineer.dto.request.TaskFlowStatusRequest;
import com.ryl.engineer.dto.request.TaskQueryRequest;
import com.ryl.engineer.dto.request.TaskStepUpdateRequest;
import com.ryl.engineer.entity.Task;
import com.ryl.engineer.entity.TaskEngineer;
import com.ryl.engineer.entity.TaskImage;
import com.ryl.engineer.entity.TaskStep;
import com.ryl.engineer.entity.TaskActivity;
import com.ryl.engineer.entity.TaskTransferHistory;
import com.ryl.engineer.entity.TaskStatusHistory;
import com.ryl.engineer.entity.User;
import com.ryl.engineer.mapper.TaskEngineerMapper;
import com.ryl.engineer.mapper.TaskImageMapper;
import com.ryl.engineer.mapper.TaskMapper;
import com.ryl.engineer.mapper.TaskStepMapper;
import com.ryl.engineer.mapper.TaskActivityMapper;
import com.ryl.engineer.mapper.TaskTransferHistoryMapper;
import com.ryl.engineer.mapper.TaskStatusHistoryMapper;
import com.ryl.engineer.mapper.UserMapper;
import com.ryl.engineer.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务服务实现类
 */
@Service
@Slf4j
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    
    @Autowired
    private TaskMapper taskMapper;
    
    @Autowired
    private TaskStepMapper taskStepMapper;
    
    @Autowired
    private TaskImageMapper taskImageMapper;
    
    @Autowired
    private TaskEngineerMapper taskEngineerMapper;
    
    @Autowired
    private TaskActivityMapper taskActivityMapper;
    
    @Autowired
    private TaskTransferHistoryMapper taskTransferHistoryMapper;
    
    @Autowired
    private TaskStatusHistoryMapper taskStatusHistoryMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Value("${app.upload.base-path:}")
    private String baseUploadPath;
    
    @Value("${app.upload.task-image-path:task/images}")
    private String taskImagePath;
    
    @Value("${app.upload.task-image-url-prefix:http://localhost:8089/api/v1/uploads/task/images}")
    private String taskImageUrlPrefix;
    
    /**
     * 创建任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createTask(CreateTaskRequest request) {
        try {
            log.info("开始创建任务，类型: {}, 标题: {}", request.getTaskType(), request.getTitle());
            log.debug("任务详情: 开始时间={}, 结束时间={}", request.getStartTime(), request.getEndTime());
            
            // 1. 创建任务对象
            Task task = new Task();
            
            // 2. 生成任务编号：类型缩写-年份-序号，如RP-2024-001
            String taskType = request.getTaskType();
            log.debug("处理任务类型: {}", taskType);
            
            String typePrefix = getTaskTypePrefix(taskType);
            String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
            
            // 查询当前年份最大序号
            LambdaQueryWrapper<Task> queryWrapper = Wrappers.lambdaQuery(Task.class)
                    .like(Task::getTaskId, typePrefix + "-" + year)
                    .orderByDesc(Task::getTaskId);
            Task latestTask = taskMapper.selectOne(queryWrapper);
            
            int sequence = 1;
            if (latestTask != null && latestTask.getTaskId() != null) {
                String[] parts = latestTask.getTaskId().split("-");
                if (parts.length == 3) {
                    try {
                        sequence = Integer.parseInt(parts[2]) + 1;
                    } catch (NumberFormatException e) {
                        log.error("解析任务序号失败", e);
                    }
                }
            }
            
            String taskId = String.format("%s-%s-%03d", typePrefix, year, sequence);
            task.setTaskId(taskId);
            log.debug("生成任务ID: {}", taskId);
            
            // 3. 设置基本信息
            log.debug("设置任务基本信息");
            task.setTitle(request.getTitle());
            task.setTaskType(request.getTaskType());
            task.setPriority(request.getPriority());
            try {
                log.debug("设置任务时间，开始: {}, 结束: {}", request.getStartTime(), request.getEndTime());
                if (request.getStartTime() == null) {
                    throw new IllegalArgumentException("开始时间不能为空");
                }
                if (request.getEndTime() == null) {
                    throw new IllegalArgumentException("结束时间不能为空");
                }
                task.setStartDate(request.getStartTime());
                task.setDeadline(request.getEndTime());
            } catch (Exception e) {
                log.error("设置任务时间失败", e);
                throw new IllegalArgumentException("任务时间格式错误: " + e.getMessage());
            }
            task.setIsExternalTask(request.getIsExternalTask() ? 1 : 0);
            task.setStatus("pending");
            task.setCreateTime(LocalDateTime.now());
            task.setCurrentStep(0);
            
            // 4. 设置客户信息
            if (request.getCustomer() != null) {
                log.debug("设置客户信息: {}", request.getCustomer().getName());
                if (request.getCustomer().getId() != null) {
                    task.setCustomerId(request.getCustomer().getId());
                }
                task.setCustomer(request.getCustomer().getName());
                task.setCustomerLevel(request.getCustomer().getLevel());
                task.setLocation(request.getCustomer().getAddress());
                task.setContactPerson(request.getCustomer().getContact());
                task.setContactPhone(request.getCustomer().getPhone());
            }
            
            // 5. 设置销售信息
            if (request.getSales() != null && request.getSales().getId() != null) {
                task.setSalesId(request.getSales().getId());
            }
            
            // 6. 设置设备和描述信息
            task.setDeviceName(request.getDeviceName());
            task.setDeviceModel(request.getDeviceModel());
            task.setDeviceBrand(request.getDeviceBrand());
            task.setDescription(request.getDescription());
            task.setFaultDescription(request.getFaultDescription());
            task.setQuantity(request.getQuantity());
            task.setAttachments(request.getAttachments());
            
            // 7. 设置任务类型特定字段
            if ("verification".equals(taskType)) {
                task.setVerificationType(request.getVerificationType());
            } else if ("selection".equals(taskType)) {
                task.setPurpose(request.getPurpose());
                task.setRequirementDescription(request.getRequirementDescription());
            } else if ("training".equals(taskType)) {
                task.setAppointmentTime(request.getAppointmentTime());
            }
            
            // 8. 保存任务
            taskMapper.insert(task);
            
            // 9. 保存任务图片
            if (request.getImages() != null && !request.getImages().isEmpty()) {
                List<TaskImage> images = request.getImages().stream().map(url -> {
                    TaskImage image = new TaskImage();
                    image.setTaskId(taskId);
                    image.setImageUrl(url);
                    image.setCreateTime(LocalDateTime.now());
                    return image;
                }).collect(Collectors.toList());
                
                taskImageMapper.batchInsert(images);
            }
            
            // 10. 保存任务工程师关联
            if (request.getAssignedEngineers() != null && !request.getAssignedEngineers().isEmpty()) {
                List<TaskEngineer> engineers = new ArrayList<>();
                
                // 如果只有一个工程师，则设为负责人
                boolean hasSingleEngineer = request.getAssignedEngineers().size() == 1;
                
                for (Long engineerId : request.getAssignedEngineers()) {
                    TaskEngineer engineer = new TaskEngineer();
                    engineer.setTaskId(taskId);
                    engineer.setEngineerId(engineerId);
                    engineer.setIsOwner(hasSingleEngineer ? 1 : 0);  // 如果只有一个工程师，设为负责人
                    engineer.setCreateTime(LocalDateTime.now());
                    engineers.add(engineer);
                }
                
                taskEngineerMapper.batchInsert(engineers);
            }
            
            // 11. 创建初始步骤
            createInitialTaskSteps(taskId, taskType);
            
            return taskId;
        } catch (Exception e) {
            log.error("创建任务失败", e);
            throw new RuntimeException("创建任务失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 分页查询任务列表
     */
    @Override
    public PageResult<TaskDTO> getTaskPage(TaskQueryRequest request) {
        // 参数校验
        if (request.getPage() == null || request.getPage() < 1) {
            request.setPage(1);
        }
        if (request.getSize() == null || request.getSize() < 1) {
            request.setSize(10);
        }
        
        // 开启分页
        Page<Object> page = PageHelper.startPage(request.getPage(), request.getSize());
        
        // 构建查询条件
        LambdaQueryWrapper<Task> queryWrapper = Wrappers.lambdaQuery(Task.class);
        
        // 添加过滤条件
        if (StringUtils.hasText(request.getStatus())) {
            queryWrapper.eq(Task::getStatus, request.getStatus());
        }
        
        if (StringUtils.hasText(request.getTaskType())) {
            queryWrapper.eq(Task::getTaskType, request.getTaskType());
        }
        
        if (StringUtils.hasText(request.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Task::getTitle, request.getKeyword())
                    .or()
                    .like(Task::getCustomer, request.getKeyword())
                    .or()
                    .like(Task::getDeviceName, request.getKeyword())
            );
        }
        
        if (StringUtils.hasText(request.getPriority())) {
            queryWrapper.eq(Task::getPriority, request.getPriority());
        }
        
        if (request.getCustomerId() != null) {
            queryWrapper.eq(Task::getCustomerId, request.getCustomerId());
        }
        
        if (request.getSalesId() != null) {
            queryWrapper.eq(Task::getSalesId, request.getSalesId());
        }
        
        if (StringUtils.hasText(request.getStartDate())) {
            queryWrapper.ge(Task::getCreateTime, request.getStartDate() + " 00:00:00");
        }
        
        if (StringUtils.hasText(request.getEndDate())) {
            queryWrapper.le(Task::getCreateTime, request.getEndDate() + " 23:59:59");
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(Task::getCreateTime);
        
        // 执行查询
        List<Task> tasks;
        if (request.getEngineerId() != null) {
            // 如果指定了工程师ID，需要联表查询
            tasks = taskMapper.findByEngineerId(request.getEngineerId());
        } else {
            tasks = taskMapper.selectList(queryWrapper);
        }
        
        // 转换为DTO
        List<TaskDTO> taskDTOs = tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
        
        // 构建分页结果
        return new PageResult<TaskDTO>(page.getTotal(), taskDTOs, request.getPage(), request.getSize());
    }
    
    /**
     * 获取任务详情
     */
    @Override
    public TaskDTO getTaskDetail(String taskId) {
        // 查询任务基本信息
        Task task = taskMapper.selectOne(
                Wrappers.lambdaQuery(Task.class).eq(Task::getTaskId, taskId)
        );
        
        if (task == null) {
            return null;
        }
        
        // 查询任务图片
        List<TaskImage> images = taskImageMapper.findByTaskId(taskId);
        
        // 查询任务工程师
        List<TaskEngineer> engineers = taskEngineerMapper.findByTaskId(taskId);
        
        // 转换为DTO
        TaskDTO taskDTO = convertToDTO(task);
        
        // 设置图片
        if (images != null && !images.isEmpty()) {
            taskDTO.setImages(images.stream()
                    .map(TaskImage::getImageUrl)
                    .collect(Collectors.toList()));
        }
        
        // 设置工程师信息（在实际项目中需要从用户表查询工程师详细信息）
        if (engineers != null && !engineers.isEmpty()) {
            // 模拟工程师信息，实际项目中应该从用户表查询
            taskDTO.setEngineers(engineers.stream().map(e -> {
                EngineerDTO engineerDTO = new EngineerDTO();
                engineerDTO.setId(e.getEngineerId());
                engineerDTO.setName("工程师" + e.getEngineerId()); // 实际应从用户表查询
                engineerDTO.setAvatar("/img/default-avatar.png"); // 实际应从用户表查询
                engineerDTO.setDepartment("技术部"); // 实际应从用户表查询
                engineerDTO.setMobile("13800138000"); // 实际应从用户表查询
                engineerDTO.setIsOwner(e.getIsOwner() == 1);
                engineerDTO.setOnline(true); // 实际应从在线状态表查询
                return engineerDTO;
            }).collect(Collectors.toList()));
        }
        
        return taskDTO;
    }
    
    // 将实体转换为DTO
    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        BeanUtils.copyProperties(task, dto);
        
        // 根据状态生成状态文本
        String statusText;
        switch (task.getStatus()) {
            case "draft":
                statusText = "草稿";
                break;
            case "pending":
                statusText = "待处理";
                break;
            case "in-progress":
                statusText = "进行中";
                break;
            case "completed":
                statusText = "已完成";
                break;
            case "cancelled":
                statusText = "已取消";
                break;
            default:
                statusText = "未知状态";
        }
        dto.setStatusText(statusText);
        
        // 转换布尔值
        dto.setIsExternalTask(task.getIsExternalTask() == 1);
        dto.setNeedSiteVisit(task.getNeedSiteVisit() != null && task.getNeedSiteVisit() == 1);
        
        return dto;
    }
    
    // 获取任务类型前缀
    private String getTaskTypePrefix(String taskType) {
        switch (taskType) {
            case "repair":
                return "RP";
            case "maintenance":
                return "MT";
            case "recycle":
                return "RC";
            case "leasing":
                return "LS";
            case "training":
                return "TR";
            case "verification":
                return "VF";
            case "selection":
                return "SL";
            case "installation":
                return "IN";
            default:
                return "TK";
        }
    }
    
    // 创建初始任务步骤
    private void createInitialTaskSteps(String taskId, String taskType) {
        List<TaskStep> steps = new ArrayList<>();
        
        // 通用初始步骤
        steps.add(createStep(taskId, 0, "已接单", "请保持电话畅通，我们稍后会联系您", "pending"));
        steps.add(createStep(taskId, 1, "判断是否需要上门", "需要确认是否需要上门服务", "pending"));
        
        // 根据任务类型添加特定步骤
        switch (taskType) {
            case "repair":
                steps.add(createStep(taskId, 2, "检修完成", "维修评估", "pending"));
                steps.add(createStep(taskId, 3, "维修方案和报价", "提供维修方案和报价", "pending"));
                steps.add(createStep(taskId, 4, "维修中", "工程师维修中", "pending"));
                steps.add(createStep(taskId, 5, "验证报告", "已完成维修验证", "pending"));
                break;
            case "maintenance":
                steps.add(createStep(taskId, 2, "检修完成", "保养评估", "pending"));
                steps.add(createStep(taskId, 3, "保养方案和报价", "提供保养方案和报价", "pending"));
                steps.add(createStep(taskId, 4, "保养中", "工程师保养中", "pending"));
                steps.add(createStep(taskId, 5, "验证报告", "已完成保养验证", "pending"));
                break;
            case "training":
                steps.add(createStep(taskId, 2, "培训准备完成", "培训评估", "pending"));
                steps.add(createStep(taskId, 3, "培训方案和报价", "提供培训方案和报价", "pending"));
                steps.add(createStep(taskId, 4, "培训中", "工程师培训中", "pending"));
                steps.add(createStep(taskId, 5, "验证报告", "已完成培训验证", "pending"));
                break;
            case "verification":
                steps.add(createStep(taskId, 2, "验证准备完成", "验证评估", "pending"));
                steps.add(createStep(taskId, 3, "验证方案和报价", "提供验证方案和报价", "pending"));
                steps.add(createStep(taskId, 4, "验证中", "工程师验证中", "pending"));
                steps.add(createStep(taskId, 5, "验证报告", "已完成验证报告", "pending"));
                break;
            case "selection":
                steps.add(createStep(taskId, 2, "选型分析完成", "选型评估", "pending"));
                steps.add(createStep(taskId, 3, "选型方案和报价", "提供选型方案和报价", "pending"));
                steps.add(createStep(taskId, 4, "选型进行中", "工程师选型中", "pending"));
                steps.add(createStep(taskId, 5, "验证报告", "已完成选型验证", "pending"));
                break;
            case "installation":
                steps.add(createStep(taskId, 2, "安装准备完成", "安装评估", "pending"));
                steps.add(createStep(taskId, 3, "安装方案和报价", "提供安装方案和报价", "pending"));
                steps.add(createStep(taskId, 4, "安装中", "工程师安装中", "pending"));
                steps.add(createStep(taskId, 5, "验证报告", "已完成安装验证", "pending"));
                break;
            case "recycle":
                steps.add(createStep(taskId, 2, "回收评估完成", "回收评估", "pending"));
                steps.add(createStep(taskId, 3, "回收方案和报价", "提供回收方案和报价", "pending"));
                steps.add(createStep(taskId, 4, "回收中", "工程师回收中", "pending"));
                steps.add(createStep(taskId, 5, "验证报告", "已完成回收验证", "pending"));
                break;
            case "leasing":
                steps.add(createStep(taskId, 2, "租赁评估完成", "租赁评估", "pending"));
                steps.add(createStep(taskId, 3, "租赁方案和报价", "提供租赁方案和报价", "pending"));
                steps.add(createStep(taskId, 4, "租赁中", "租赁中", "pending"));
                steps.add(createStep(taskId, 5, "验证报告", "已完成租赁验证", "pending"));
                break;
        }
        
        // 通用结束步骤
        steps.add(createStep(taskId, 6, "服务评价", "请对我们的服务进行评价", "pending"));
        steps.add(createStep(taskId, 7, "订单已完成", "订单已完成", "pending"));
        
        // 批量插入步骤
        for (TaskStep step : steps) {
            taskStepMapper.insert(step);
        }
    }
    
    // 创建步骤辅助方法
    private TaskStep createStep(String taskId, int stepIndex, String title, String description, String status) {
        TaskStep step = new TaskStep();
        step.setTaskId(taskId);
        step.setStepIndex(stepIndex);
        step.setTitle(title);
        step.setDescription(description);
        step.setStatus(status);
        step.setCreateTime(LocalDateTime.now());
        step.setUpdateTime(LocalDateTime.now());
        return step;
    }
    
    /**
     * 获取任务流程
     */
    @Override
    public TaskFlowDTO getTaskFlow(String taskId) {
        // 1. 查询任务信息
        Task task = taskMapper.selectOne(
                Wrappers.lambdaQuery(Task.class).eq(Task::getTaskId, taskId)
        );
        
        if (task == null) {
            return null;
        }
        
        // 2. 查询任务步骤
        List<TaskStep> steps = taskStepMapper.findByTaskId(taskId);
        
        if (steps == null || steps.isEmpty()) {
            return null;
        }
        
        // 3. 构建流程DTO
        TaskFlowDTO flowDTO = new TaskFlowDTO();
        flowDTO.setTaskId(taskId);
        flowDTO.setTitle(task.getTitle());
        flowDTO.setCurrentStep(task.getCurrentStep());
        flowDTO.setTaskType(task.getTaskType());
        flowDTO.setStatus(task.getStatus());
        
        // 4. 构建步骤DTO列表
        List<TaskStepDTO> stepDTOs = steps.stream().map(step -> {
            TaskStepDTO dto = new TaskStepDTO();
            // 转换属性名称
            dto.setIndex(step.getStepIndex());
            dto.setName(step.getTitle());
            dto.setStatus(step.getStatus());
            dto.setStartTime(step.getStartTime());
            dto.setEndTime(step.getEndTime());
            dto.setDescription(step.getDescription());
            
            // 设置步骤状态文本
            String statusText;
            switch (step.getStatus()) {
                case "pending":
                    statusText = "待处理";
                    break;
                case "in-progress":
                    statusText = "进行中";
                    break;
                case "completed":
                    statusText = "已完成";
                    break;
                case "skipped":
                    statusText = "已跳过";
                    break;
                default:
                    statusText = "未知状态";
            }
            
            // 添加到操作列表
            List<String> actions = new ArrayList<>();
            if ("pending".equals(step.getStatus())) {
                actions.add("start");
                actions.add("skip");
            } else if ("in-progress".equals(step.getStatus())) {
                actions.add("complete");
                actions.add("cancel");
            }
            dto.setActions(actions);
            
            // 设置是否当前步骤到表单数据中
            Map<String, Object> formData = new HashMap<>();
            formData.put("isCurrent", step.getStepIndex() == task.getCurrentStep());
            formData.put("statusText", statusText);
            dto.setFormData(formData);
            
            return dto;
        }).collect(Collectors.toList());
        
        flowDTO.setSteps(stepDTOs);
        
        return flowDTO;
    }
    
    /**
     * 获取任务状态历史
     */
    @Override
    public List<Map<String, Object>> getTaskStatusHistory(String taskId) {
        return baseMapper.getTaskStatusHistory(taskId);
    }
    
    /**
     * 更新任务步骤
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTaskStep(TaskStepUpdateRequest request) {
        // 1. 查询任务信息
        Task task = taskMapper.selectOne(
                Wrappers.lambdaQuery(Task.class).eq(Task::getTaskId, request.getTaskId())
        );
        
        if (task == null) {
            return false;
        }
        
        // 2. 查询步骤信息
        TaskStep step = taskStepMapper.selectOne(
                Wrappers.lambdaQuery(TaskStep.class)
                        .eq(TaskStep::getTaskId, request.getTaskId())
                        .eq(TaskStep::getStepIndex, request.getStepIndex())
        );
        
        if (step == null) {
            return false;
        }
        
        // 3. 根据操作类型更新步骤状态
        String status;
        switch (request.getAction()) {
            case "start":
                status = "in-progress";
                step.setStartTime(LocalDateTime.now());
                break;
            case "complete":
                status = "completed";
                step.setEndTime(LocalDateTime.now());
                break;
            case "skip":
                status = "skipped";
                break;
            case "cancel":
                status = "pending";
                break;
            default:
                return false;
        }
        
        step.setStatus(status);
        if (request.getDescription() != null) {
            step.setDescription(request.getDescription());
        }
        // 设置操作人信息，在实际项目中应该从安全上下文中获取
        // step.setOperatorId(currentUserId);
        // step.setOperator(currentUserName);
        
        // 更新图片信息
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            String imagesJson = String.join(",", request.getImages());
            step.setOptions(imagesJson); // 暂时使用options字段存储图片信息
        }
        
        step.setUpdateTime(LocalDateTime.now());
        taskStepMapper.updateById(step);
        
        // 4. 如果状态是已完成且是当前步骤，则更新任务当前步骤
        if ("completed".equals(status) && task.getCurrentStep() == request.getStepIndex()) {
            // 查询下一个步骤
            TaskStep nextStep = taskStepMapper.selectOne(
                    Wrappers.lambdaQuery(TaskStep.class)
                            .eq(TaskStep::getTaskId, request.getTaskId())
                            .eq(TaskStep::getStepIndex, request.getStepIndex() + 1)
            );
            
            if (nextStep != null) {
                task.setCurrentStep(nextStep.getStepIndex());
                
                // 如果是最后一步完成，则更新任务状态为已完成
                if (nextStep.getStepIndex() == 7) { // 假设最后一步是步骤7
                    task.setStatus("completed");
                    task.setCompletedDate(LocalDateTime.now());
                } else {
                    task.setStatus("in-progress");
                }
                
                taskMapper.updateById(task);
            }
        }
        
        return true;
    }
    
    /**
     * 上传任务图片
     */
    @Override
    public String uploadTaskImage(String taskId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        try {
            // 1. 检查文件夹是否存在，不存在则创建
            if (baseUploadPath == null || baseUploadPath.isEmpty()) {
                baseUploadPath = "D:/uploads"; // 默认上传路径
                log.warn("未配置上传路径，使用默认路径: {}", baseUploadPath);
            }
            
            File uploadDir = new File(baseUploadPath + File.separator + taskImagePath);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    log.error("创建上传目录失败: {}", uploadDir.getAbsolutePath());
                    throw new RuntimeException("创建上传目录失败");
                }
                log.info("创建上传目录: {}", uploadDir.getAbsolutePath());
            }
            
            // 2. 生成文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                originalFilename = "unknown.jpg";
            }
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + suffix;
            
            // 3. 保存文件
            File destFile = new File(uploadDir.getAbsolutePath() + File.separator + newFilename);
            file.transferTo(destFile);
            log.info("文件已保存到: {}", destFile.getAbsolutePath());
            
            // 4. 生成访问URL
            String imageUrl = taskImageUrlPrefix + "/" + newFilename;
            
            // 5. 如果taskId不为空，保存到数据库
            if (taskId != null) {
                TaskImage taskImage = new TaskImage();
                taskImage.setTaskId(taskId);
                taskImage.setImageUrl(imageUrl);
                taskImage.setCreateTime(LocalDateTime.now());
                taskImageMapper.insert(taskImage);
            }
            
            return imageUrl;
        } catch (IOException e) {
            log.error("上传图片失败", e);
            throw new RuntimeException("上传图片失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 批量上传任务图片
     */
    @Override
    public List<String> batchUploadTaskImages(String taskId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<String> imageUrls = new ArrayList<>();
        List<TaskImage> taskImages = new ArrayList<>();
        
        try {
            // 1. 检查文件夹是否存在，不存在则创建
            if (baseUploadPath == null || baseUploadPath.isEmpty()) {
                baseUploadPath = "D:/uploads"; // 默认上传路径
                log.warn("未配置上传路径，使用默认路径: {}", baseUploadPath);
            }
            
            File uploadDir = new File(baseUploadPath + File.separator + taskImagePath);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    log.error("创建上传目录失败: {}", uploadDir.getAbsolutePath());
                    throw new RuntimeException("创建上传目录失败");
                }
                log.info("创建上传目录: {}", uploadDir.getAbsolutePath());
            }
            
            // 2. 处理每个文件
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                
                // 3. 生成文件名
                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null) {
                    originalFilename = "unknown.jpg";
                }
                String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
                String newFilename = UUID.randomUUID().toString() + suffix;
                
                // 4. 保存文件
                File destFile = new File(uploadDir.getAbsolutePath() + File.separator + newFilename);
                file.transferTo(destFile);
                log.info("文件已保存到: {}", destFile.getAbsolutePath());
                
                // 5. 生成访问URL
                String imageUrl = taskImageUrlPrefix + "/" + newFilename;
                imageUrls.add(imageUrl);
                
                // 6. 创建TaskImage对象
                if (taskId != null) {
                    TaskImage taskImage = new TaskImage();
                    taskImage.setTaskId(taskId);
                    taskImage.setImageUrl(imageUrl);
                    taskImage.setCreateTime(LocalDateTime.now());
                    taskImages.add(taskImage);
                }
            }
            
            // 7. 批量保存到数据库
            if (!taskImages.isEmpty()) {
                taskImageMapper.batchInsert(taskImages);
            }
            
            return imageUrls;
        } catch (IOException e) {
            log.error("批量上传图片失败", e);
            throw new RuntimeException("批量上传图片失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 更新任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTask(String taskId, TaskDTO taskDTO) {
        // 1. 查询任务信息
        Task task = taskMapper.selectOne(
                Wrappers.lambdaQuery(Task.class).eq(Task::getTaskId, taskId)
        );
        
        if (task == null) {
            return false;
        }
        
        // 2. 更新基本信息
        if (taskDTO.getTitle() != null) {
            task.setTitle(taskDTO.getTitle());
        }
        if (taskDTO.getPriority() != null) {
            task.setPriority(taskDTO.getPriority());
        }
        if (taskDTO.getStartDate() != null) {
            task.setStartDate(taskDTO.getStartDate());
        }
        if (taskDTO.getDeadline() != null) {
            task.setDeadline(taskDTO.getDeadline());
        }
        if (taskDTO.getDescription() != null) {
            task.setDescription(taskDTO.getDescription());
        }
        if (taskDTO.getFaultDescription() != null) {
            task.setFaultDescription(taskDTO.getFaultDescription());
        }
        if (taskDTO.getDeviceName() != null) {
            task.setDeviceName(taskDTO.getDeviceName());
        }
        if (taskDTO.getDeviceModel() != null) {
            task.setDeviceModel(taskDTO.getDeviceModel());
        }
        if (taskDTO.getDeviceBrand() != null) {
            task.setDeviceBrand(taskDTO.getDeviceBrand());
        }
        if (taskDTO.getQuantity() != null) {
            task.setQuantity(taskDTO.getQuantity());
        }
        if (taskDTO.getAttachments() != null) {
            task.setAttachments(taskDTO.getAttachments());
        }
        if (taskDTO.getNeedSiteVisit() != null) {
            task.setNeedSiteVisit(taskDTO.getNeedSiteVisit() ? 1 : 0);
        }
        
        // 3. 更新任务
        taskMapper.updateById(task);
        
        // 4. 如果有图片更新，则更新图片
        if (taskDTO.getImages() != null && !taskDTO.getImages().isEmpty()) {
            // 删除原有图片
            taskImageMapper.delete(
                    Wrappers.lambdaQuery(TaskImage.class).eq(TaskImage::getTaskId, taskId)
            );
            
            // 添加新图片
            List<TaskImage> images = taskDTO.getImages().stream().map(url -> {
                TaskImage image = new TaskImage();
                image.setTaskId(taskId);
                image.setImageUrl(url);
                image.setCreateTime(LocalDateTime.now());
                return image;
            }).collect(Collectors.toList());
            
            taskImageMapper.batchInsert(images);
        }
        
        return true;
    }
    
    /**
     * 取消任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelTask(String taskId, String reason) {
        // 1. 查询任务信息
        Task task = taskMapper.selectOne(
                Wrappers.lambdaQuery(Task.class).eq(Task::getTaskId, taskId)
        );
        
        if (task == null) {
            return false;
        }
        
        // 2. 更新任务状态
        task.setStatus("cancelled");
        // 使用description字段存储取消原因
        task.setDescription(reason);
        // 使用completedDate字段存储取消时间
        task.setCompletedDate(LocalDateTime.now());
        
        // 3. 更新任务
        taskMapper.updateById(task);
        
        return true;
    }
    
    /**
     * 分配工程师
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignEngineers(String taskId, List<Long> engineerIds) {
        // 1. 查询任务信息
        Task task = taskMapper.selectOne(
                Wrappers.lambdaQuery(Task.class).eq(Task::getTaskId, taskId)
        );
        
        if (task == null || engineerIds == null || engineerIds.isEmpty()) {
            return false;
        }
        
        // 2. 删除原有工程师关联
        taskEngineerMapper.delete(
                Wrappers.lambdaQuery(TaskEngineer.class).eq(TaskEngineer::getTaskId, taskId)
        );
        
        // 3. 添加新工程师关联
        List<TaskEngineer> engineers = new ArrayList<>();
        
        // 如果只有一个工程师，则设为负责人
        boolean hasSingleEngineer = engineerIds.size() == 1;
        
        for (Long engineerId : engineerIds) {
            TaskEngineer engineer = new TaskEngineer();
            engineer.setTaskId(taskId);
            engineer.setEngineerId(engineerId);
            engineer.setIsOwner(hasSingleEngineer ? 1 : 0);  // 如果只有一个工程师，设为负责人
            engineer.setCreateTime(LocalDateTime.now());
            engineers.add(engineer);
        }
        
        taskEngineerMapper.batchInsert(engineers);
        
        return true;
    }
    
    /**
     * 获取任务统计信息
     */
    @Override
    public Map<String, Object> getTaskStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 1. 查询任务总数
        int totalTasks = taskMapper.selectCount(null).intValue();
        statistics.put("totalTasks", totalTasks);
        
        // 2. 查询各状态任务数量
        int pendingTasks = taskMapper.selectCount(
                Wrappers.lambdaQuery(Task.class).eq(Task::getStatus, "pending")
        ).intValue();
        int inProgressTasks = taskMapper.selectCount(
                Wrappers.lambdaQuery(Task.class).eq(Task::getStatus, "in-progress")
        ).intValue();
        int completedTasks = taskMapper.selectCount(
                Wrappers.lambdaQuery(Task.class).eq(Task::getStatus, "completed")
        ).intValue();
        int cancelledTasks = taskMapper.selectCount(
                Wrappers.lambdaQuery(Task.class).eq(Task::getStatus, "cancelled")
        ).intValue();
        
        statistics.put("pendingTasks", pendingTasks);
        statistics.put("inProgressTasks", inProgressTasks);
        statistics.put("completedTasks", completedTasks);
        statistics.put("cancelledTasks", cancelledTasks);
        
        // 3. 查询各类型任务数量
        int repairTasks = taskMapper.selectCount(
                Wrappers.lambdaQuery(Task.class).eq(Task::getTaskType, "repair")
        ).intValue();
        int maintenanceTasks = taskMapper.selectCount(
                Wrappers.lambdaQuery(Task.class).eq(Task::getTaskType, "maintenance")
        ).intValue();
        int verificationTasks = taskMapper.selectCount(
                Wrappers.lambdaQuery(Task.class).eq(Task::getTaskType, "verification")
        ).intValue();
        int trainingTasks = taskMapper.selectCount(
                Wrappers.lambdaQuery(Task.class).eq(Task::getTaskType, "training")
        ).intValue();
        int selectionTasks = taskMapper.selectCount(
                Wrappers.lambdaQuery(Task.class).eq(Task::getTaskType, "selection")
        ).intValue();
        int installationTasks = taskMapper.selectCount(
                Wrappers.lambdaQuery(Task.class).eq(Task::getTaskType, "installation")
        ).intValue();
        int recycleTasks = taskMapper.selectCount(
                Wrappers.lambdaQuery(Task.class).eq(Task::getTaskType, "recycle")
        ).intValue();
        int leasingTasks = taskMapper.selectCount(
                Wrappers.lambdaQuery(Task.class).eq(Task::getTaskType, "leasing")
        ).intValue();
        
        statistics.put("repairTasks", repairTasks);
        statistics.put("maintenanceTasks", maintenanceTasks);
        statistics.put("verificationTasks", verificationTasks);
        statistics.put("trainingTasks", trainingTasks);
        statistics.put("selectionTasks", selectionTasks);
        statistics.put("installationTasks", installationTasks);
        statistics.put("recycleTasks", recycleTasks);
        statistics.put("leasingTasks", leasingTasks);
        
        // 4. 查询优先级任务数量
        int highPriorityTasks = taskMapper.selectCount(
                Wrappers.lambdaQuery(Task.class).eq(Task::getPriority, "high")
        ).intValue();
        int mediumPriorityTasks = taskMapper.selectCount(
                Wrappers.lambdaQuery(Task.class).eq(Task::getPriority, "medium")
        ).intValue();
        int lowPriorityTasks = taskMapper.selectCount(
                Wrappers.lambdaQuery(Task.class).eq(Task::getPriority, "low")
        ).intValue();
        
        statistics.put("highPriorityTasks", highPriorityTasks);
        statistics.put("mediumPriorityTasks", mediumPriorityTasks);
        statistics.put("lowPriorityTasks", lowPriorityTasks);
        
        return statistics;
    }
    
    /**
     * 更新任务流程状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTaskFlowStatus(TaskFlowStatusRequest request) {
        log.info("收到更新任务流程状态请求: taskId={}, currentStepIndex={}, nextStepIndex={}, action={}", 
            request.getTaskId(), request.getCurrentStepIndex(), request.getNextStepIndex(), request.getAction());
            
        try {
            // 1. 查询任务信息
            Task task = taskMapper.selectOne(
                Wrappers.<Task>lambdaQuery()
                    .eq(Task::getTaskId, request.getTaskId())
            );
            
            if (task == null) {
                log.error("任务不存在: taskId={}", request.getTaskId());
                return false;
            }
            
            // 2. 查询并更新任务当前步骤
            if (!request.getCurrentStepIndex().equals(request.getNextStepIndex())) {
                // 更新任务的当前步骤
                task.setCurrentStep(request.getNextStepIndex());
                taskMapper.updateById(task);
                log.info("更新任务当前步骤: 从{}到{}", request.getCurrentStepIndex(), request.getNextStepIndex());
            }
            
            // 4. 查询当前步骤和下一步骤
            TaskStep currentStep = taskStepMapper.selectOne(
                Wrappers.<TaskStep>lambdaQuery()
                    .eq(TaskStep::getTaskId, request.getTaskId())
                    .eq(TaskStep::getStepIndex, request.getCurrentStepIndex())
            );
            
            if (currentStep == null) {
                log.error("当前步骤不存在: taskId={}, stepIndex={}", request.getTaskId(), request.getCurrentStepIndex());
                return false;
            }
            
            TaskStep nextStep = null;
            if (!request.getCurrentStepIndex().equals(request.getNextStepIndex())) {
                nextStep = taskStepMapper.selectOne(
                    Wrappers.<TaskStep>lambdaQuery()
                        .eq(TaskStep::getTaskId, request.getTaskId())
                        .eq(TaskStep::getStepIndex, request.getNextStepIndex())
                );
                
                if (nextStep == null) {
                    log.error("下一步骤不存在: taskId={}, stepIndex={}", request.getTaskId(), request.getNextStepIndex());
                    return false;
                }
            }
            
            // 5. 根据操作类型处理步骤状态
            String action = request.getAction();
            LocalDateTime now = LocalDateTime.now();
            
            if ("update".equals(action)) {
                // 前进到下一步
                if (request.getNextStepIndex() > request.getCurrentStepIndex()) {
                    // 将当前步骤标记为完成
                    currentStep.setStatus("completed");
                    currentStep.setEndTime(now);
                    taskStepMapper.updateById(currentStep);
                    
                    // 将下一步骤标记为进行中
                    if (nextStep != null) {
                        nextStep.setStatus("in-progress");
                        nextStep.setStartTime(now);
                        taskStepMapper.updateById(nextStep);
                    }
                } 
                // 返回到上一步
                else if (request.getNextStepIndex() < request.getCurrentStepIndex()) {
                    // 将当前步骤标记为待处理
                    currentStep.setStatus("pending");
                    currentStep.setStartTime(null);
                    taskStepMapper.updateById(currentStep);
                    
                    // 将上一步骤标记为进行中
                    if (nextStep != null) {
                        nextStep.setStatus("in-progress");
                        if (nextStep.getStartTime() == null) {
                            nextStep.setStartTime(now);
                        }
                        nextStep.setEndTime(null);
                        taskStepMapper.updateById(nextStep);
                    }
                }
            } else if ("complete".equals(action)) {
                // 完成当前步骤
                currentStep.setStatus("completed");
                currentStep.setEndTime(now);
                taskStepMapper.updateById(currentStep);
                
                // 如果有下一步，将下一步标记为进行中
                if (nextStep != null) {
                    nextStep.setStatus("in-progress");
                    nextStep.setStartTime(now);
                    taskStepMapper.updateById(nextStep);
                }
            } else if ("skip".equals(action)) {
                // 跳过当前步骤
                currentStep.setStatus("skipped");
                currentStep.setEndTime(now);
                taskStepMapper.updateById(currentStep);
                
                // 如果有下一步，将下一步标记为进行中
                if (nextStep != null) {
                    nextStep.setStatus("in-progress");
                    nextStep.setStartTime(now);
                    taskStepMapper.updateById(nextStep);
                }
            } else {
                log.error("不支持的操作类型: {}", action);
                return false;
            }
            
            log.info("任务流程状态更新成功: taskId={}, action={}, currentStep={}, nextStep={}", 
                request.getTaskId(), action, request.getCurrentStepIndex(), request.getNextStepIndex());
            
            return true;
        } catch (Exception e) {
            log.error("更新任务流程状态异常: taskId={}, 错误信息={}", request.getTaskId(), e.getMessage(), e);
            throw e; // 抛出异常以触发事务回滚
        }
    }
    
    /**
     * 更新任务上门决策（包含约定上门时间）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTaskSiteVisitDecision(String taskId, Integer stepIndex, Boolean requiresVisit, String visitAppointmentTime) {
        log.info("更新任务上门决策: taskId={}, stepIndex={}, requiresVisit={}, visitAppointmentTime={}", 
                taskId, stepIndex, requiresVisit, visitAppointmentTime);
        
        try {
            // 1. 查询任务信息
            Task task = taskMapper.selectOne(
                Wrappers.<Task>lambdaQuery()
                    .eq(Task::getTaskId, taskId)
            );
            
            if (task == null) {
                log.error("任务不存在: taskId={}", taskId);
                return false;
            }
            
            // 2. 更新任务上门决策
            task.setNeedSiteVisit(requiresVisit ? 1 : 0);
            
            // 3. 设置约定上门时间（如果需要上门且提供了时间）
            if (requiresVisit && StringUtils.hasText(visitAppointmentTime)) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                    LocalDateTime appointmentTime = LocalDateTime.parse(visitAppointmentTime, formatter);
                    task.setVisitAppointmentTime(appointmentTime);
                    log.info("设置约定上门时间: {}", appointmentTime);
                } catch (Exception e) {
                    log.error("解析约定上门时间失败: {}", e.getMessage());
                    // 解析失败不影响其他操作，继续执行
                }
            }
            
            taskMapper.updateById(task);
            
            // 4. 更新任务流程
            // 4.1 获取当前步骤和下一步骤
            List<TaskStep> steps = taskStepMapper.selectList(
                Wrappers.<TaskStep>lambdaQuery()
                    .eq(TaskStep::getTaskId, taskId)
                    .orderByAsc(TaskStep::getStepIndex)
            );
            
            if (steps.isEmpty()) {
                log.error("任务步骤不存在: taskId={}", taskId);
                return false;
            }
            
            // 4.2 将当前步骤标记为已完成
            TaskStep currentStep = steps.get(stepIndex);
            currentStep.setStatus("completed");
            currentStep.setEndTime(LocalDateTime.now());
            taskStepMapper.updateById(currentStep);
            
            // 4.3 确定下一步骤
            int nextStepIndex;
            if (requiresVisit) {
                // 如果需要上门，进入下一步
                nextStepIndex = stepIndex + 1;
            } else {
                // 如果不需要上门，跳到服务评价步骤
                nextStepIndex = steps.size() - 2; // 倒数第二步通常是服务评价
                
                // 将中间跳过的步骤标记为skipped
                for (int i = stepIndex + 1; i < nextStepIndex; i++) {
                    TaskStep skippedStep = steps.get(i);
                    skippedStep.setStatus("skipped");
                    skippedStep.setStartTime(LocalDateTime.now());
                    skippedStep.setEndTime(LocalDateTime.now());
                    taskStepMapper.updateById(skippedStep);
                }
            }
            
            // 确保nextStepIndex在有效范围内
            nextStepIndex = Math.min(nextStepIndex, steps.size() - 1);
            nextStepIndex = Math.max(nextStepIndex, 0);
            
            // 4.4 更新任务的当前步骤
            task.setCurrentStep(nextStepIndex);
            taskMapper.updateById(task);
            
            // 4.5 将下一步骤标记为进行中
            TaskStep nextStep = steps.get(nextStepIndex);
            nextStep.setStatus("in-progress");
            nextStep.setStartTime(LocalDateTime.now());
            taskStepMapper.updateById(nextStep);
            
            log.info("更新任务上门决策成功: taskId={}, requiresVisit={}, nextStepIndex={}", taskId, requiresVisit, nextStepIndex);
            return true;
        } catch (Exception e) {
            log.error("更新任务上门决策异常: taskId={}, 错误信息={}", taskId, e.getMessage(), e);
            throw e;
        }
    }
    
    // 原有的updateTaskSiteVisitDecision方法调用新方法
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTaskSiteVisitDecision(String taskId, Integer stepIndex, Boolean requiresVisit) {
        return updateTaskSiteVisitDecision(taskId, stepIndex, requiresVisit, null);
    }
    
    /**
     * 转出任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean transferTask(String taskId, Long engineerId, String note) {
        log.info("转出任务: taskId={}, engineerId={}, note={}", taskId, engineerId, note);
        
        try {
            // 1. 查询任务信息
            Task task = taskMapper.selectOne(
                Wrappers.<Task>lambdaQuery()
                    .eq(Task::getTaskId, taskId)
            );
            
            if (task == null) {
                log.error("任务不存在: taskId={}", taskId);
                return false;
            }
            
            // 2. 查询接收工程师信息
            User engineer = userMapper.selectById(engineerId);
            if (engineer == null) {
                log.error("接收工程师不存在: engineerId={}", engineerId);
                return false;
            }
            
            // 3. 记录原始负责人
            Long originalEngineerId = null;
            
            // 查询当前负责人
            TaskEngineer originalOwner = taskEngineerMapper.selectOne(
                Wrappers.<TaskEngineer>lambdaQuery()
                    .eq(TaskEngineer::getTaskId, taskId)
                    .eq(TaskEngineer::getIsOwner, 1)
            );
            
            if (originalOwner != null) {
                originalEngineerId = originalOwner.getEngineerId();
                
                // 取消原负责人的所有者标识
                originalOwner.setIsOwner(0);
                taskEngineerMapper.updateById(originalOwner);
            }
            
            // 4. 检查接收工程师是否已关联此任务
            TaskEngineer existingEngineer = taskEngineerMapper.selectOne(
                Wrappers.<TaskEngineer>lambdaQuery()
                    .eq(TaskEngineer::getTaskId, taskId)
                    .eq(TaskEngineer::getEngineerId, engineerId)
            );
            
            if (existingEngineer != null) {
                // 已存在，设置为负责人
                existingEngineer.setIsOwner(1);
                taskEngineerMapper.updateById(existingEngineer);
            } else {
                // 不存在，添加新关联并设置为负责人
                TaskEngineer newOwner = new TaskEngineer();
                newOwner.setTaskId(taskId);
                newOwner.setEngineerId(engineerId);
                newOwner.setIsOwner(1);
                newOwner.setCreateTime(LocalDateTime.now());
                taskEngineerMapper.insert(newOwner);
            }
            
            // 5. 记录任务转出历史
            TaskTransferHistory transferHistory = new TaskTransferHistory();
            transferHistory.setTaskId(taskId);
            transferHistory.setFromEngineerId(originalEngineerId);
            transferHistory.setToEngineerId(engineerId);
            transferHistory.setTransferTime(new Date());
            transferHistory.setNote(note);
            transferHistory.setCreateTime(new Date());
            
            // 保存转出历史记录
            taskTransferHistoryMapper.insert(transferHistory);
            
            // 6. 添加任务动态记录
            TaskActivity activity = new TaskActivity();
            activity.setTaskId(taskId);
            activity.setActivityType("transfer");
            activity.setContent("任务已转出给工程师: " + engineer.getName());
            activity.setOperatorId(originalEngineerId);
            activity.setOperatorName(engineer.getName());
            activity.setCreateTime(new Date());
            taskActivityMapper.insert(activity);
            
            log.info("转出任务成功: taskId={}, 从工程师ID={} 转给工程师ID={}", taskId, originalEngineerId, engineerId);
            return true;
        } catch (Exception e) {
            log.error("转出任务异常: taskId={}, engineerId={}, 错误信息={}", taskId, engineerId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 更新任务状态
     *
     * @param taskId 任务ID
     * @param status 新状态
     * @param note 状态变更说明
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTaskStatus(String taskId, String status, String note) {
        log.info("更新任务状态: taskId={}, status={}, note={}", taskId, status, note);
        
        // 1. 获取当前任务
        Task task = getOne(Wrappers.lambdaQuery(Task.class).eq(Task::getTaskId, taskId));
        if (task == null) {
            log.error("任务不存在: {}", taskId);
            return false;
        }
        
        // 如果状态相同，直接返回成功
        if (status.equals(task.getStatus())) {
            log.info("任务状态相同，无需更新: taskId={}, status={}", taskId, status);
            return true;
        }
        
        // 2. 更新任务状态
        task.setStatus(status);
        boolean updated = updateById(task);
        if (!updated) {
            log.error("更新任务状态失败: taskId={}, status={}", taskId, status);
            return false;
        }
        
        // 3. 记录状态变更历史
        try {
            TaskStatusHistory history = new TaskStatusHistory();
            history.setTaskId(taskId);
            history.setStatus(status);
            history.setComment(note);
            history.setTimestamp(LocalDateTime.now());
            
            // 获取当前用户信息 - 暂时使用系统用户，实际应从SecurityContext获取
            history.setUpdatedBy(0L);
            history.setUpdatedByName("系统");
            
            taskStatusHistoryMapper.insert(history);
        } catch (Exception e) {
            log.error("记录任务状态历史失败", e);
            // 这里选择不回滚事务，因为主要状态已经更新成功
        }
        
        // 4. 根据任务流程阶段，更新相应的流程状态
        updateFlowStatusBasedOnTaskStatus(task, status);
        
        return true;
    }
    
    /**
     * 根据任务状态更新流程状态
     * @param task 任务
     * @param newStatus 新状态
     */
    private void updateFlowStatusBasedOnTaskStatus(Task task, String newStatus) {
        try {
            TaskFlowDTO flowDTO = getTaskFlow(task.getTaskId());
            if (flowDTO == null || flowDTO.getSteps() == null || flowDTO.getSteps().isEmpty()) {
                log.warn("无法根据任务状态更新流程状态，任务流程不存在: {}", task.getTaskId());
                return;
            }
            
            List<TaskStepDTO> steps = flowDTO.getSteps();
            int currentStepIndex = task.getCurrentStep() != null ? task.getCurrentStep() : 0;
            
            switch (newStatus) {
                case "pending":
                    // 如果是待处理，确保第一个步骤是进行中
                    if (steps.size() > 0) {
                        TaskStep firstStep = getTaskStepByTaskIdAndIndex(task.getTaskId(), 0);
                        if (firstStep != null && !"in-progress".equals(firstStep.getStatus())) {
                            firstStep.setStatus("in-progress");
                            taskStepMapper.updateById(firstStep);
                        }
                    }
                    break;
                case "in-progress":
                    // 如果是进行中，确保当前步骤是进行中
                    if (currentStepIndex >= 0 && currentStepIndex < steps.size()) {
                        TaskStep currentStep = getTaskStepByTaskIdAndIndex(task.getTaskId(), currentStepIndex);
                        if (currentStep != null && !"in-progress".equals(currentStep.getStatus())) {
                            currentStep.setStatus("in-progress");
                            taskStepMapper.updateById(currentStep);
                        }
                    }
                    break;
                case "completed":
                    // 如果是已完成，确保所有步骤都是已完成状态
                    for (int i = 0; i < steps.size(); i++) {
                        TaskStep step = getTaskStepByTaskIdAndIndex(task.getTaskId(), i);
                        if (step != null && !"completed".equals(step.getStatus()) && !"skipped".equals(step.getStatus())) {
                            step.setStatus("completed");
                            taskStepMapper.updateById(step);
                        }
                    }
                    break;
                case "cancelled":
                    // 如果是已取消，流程状态保持不变
                    break;
                default:
                    log.warn("未知任务状态，不更新流程状态: {}", newStatus);
            }
        } catch (Exception e) {
            log.error("根据任务状态更新流程状态失败", e);
        }
    }
    
    /**
     * 根据任务ID和步骤索引获取步骤
     * @param taskId 任务ID
     * @param stepIndex 步骤索引
     * @return 步骤
     */
    private TaskStep getTaskStepByTaskIdAndIndex(String taskId, Integer stepIndex) {
        return taskStepMapper.selectOne(
            Wrappers.lambdaQuery(TaskStep.class)
                .eq(TaskStep::getTaskId, taskId)
                .eq(TaskStep::getStepIndex, stepIndex)
        );
    }
    
    /**
     * 更新任务报价
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTaskPrice(String taskId, Integer stepIndex, Double price) {
        log.info("更新任务报价: taskId={}, stepIndex={}, price={}", taskId, stepIndex, price);
        
        try {
            // 1. 查询任务信息
            Task task = taskMapper.selectOne(
                Wrappers.<Task>lambdaQuery()
                    .eq(Task::getTaskId, taskId)
            );
            
            if (task == null) {
                log.error("任务不存在: taskId={}", taskId);
                return false;
            }
            
            // 2. 更新报价
            task.setPrice(new BigDecimal(price));
            // 重置报价确认状态
            task.setPriceConfirmed(0);
            taskMapper.updateById(task);
            
            // 3. 更新步骤记录
            TaskStep currentStep = getTaskStepByTaskIdAndIndex(taskId, stepIndex);
            if (currentStep == null) {
                log.error("任务步骤不存在: taskId={}, stepIndex={}", taskId, stepIndex);
                return false;
            }
            
            // 4. 记录活动
            TaskActivity activity = new TaskActivity();
            activity.setTaskId(taskId);
            activity.setActivityType("price_update");
            activity.setContent("工程师已设置报价: " + price);
            activity.setCreateTime(new Date());
            taskActivityMapper.insert(activity);
            
            log.info("更新任务报价成功: taskId={}, price={}", taskId, price);
            return true;
        } catch (Exception e) {
            log.error("更新任务报价异常: taskId={}, 错误信息={}", taskId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 获取任务报价确认状态
     */
    @Override
    public Map<String, Object> getTaskPriceConfirmation(String taskId) {
        log.info("获取任务报价确认状态: taskId={}", taskId);
        
        try {
            // 1. 查询任务信息
            Task task = taskMapper.selectOne(
                Wrappers.<Task>lambdaQuery()
                    .eq(Task::getTaskId, taskId)
            );
            
            if (task == null) {
                log.error("任务不存在: taskId={}", taskId);
                throw new IllegalArgumentException("任务不存在");
            }
            
            // 2. 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("taskId", task.getTaskId());
            result.put("price", task.getPrice());
            result.put("priceConfirmed", task.getPriceConfirmed() != null && task.getPriceConfirmed() == 1);
            
            return result;
        } catch (Exception e) {
            log.error("获取任务报价确认状态异常: taskId={}, 错误信息={}", taskId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 更新任务报价确认状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTaskPriceConfirmation(String taskId, Boolean confirmed) {
        log.info("更新任务报价确认状态: taskId={}, confirmed={}", taskId, confirmed);
        
        try {
            // 1. 查询任务信息
            Task task = taskMapper.selectOne(
                Wrappers.<Task>lambdaQuery()
                    .eq(Task::getTaskId, taskId)
            );
            
            if (task == null) {
                log.error("任务不存在: taskId={}", taskId);
                return false;
            }
            
            // 2. 更新报价确认状态
            task.setPriceConfirmed(confirmed ? 1 : 0);
            taskMapper.updateById(task);
            
            // 3. 记录活动
            TaskActivity activity = new TaskActivity();
            activity.setTaskId(taskId);
            activity.setActivityType("price_confirmation");
            activity.setContent(confirmed ? "客户已确认报价" : "客户取消了报价确认");
            activity.setCreateTime(new Date());
            taskActivityMapper.insert(activity);
            
            log.info("更新任务报价确认状态成功: taskId={}, confirmed={}", taskId, confirmed);
            return true;
        } catch (Exception e) {
            log.error("更新任务报价确认状态异常: taskId={}, 错误信息={}", taskId, e.getMessage(), e);
            throw e;
        }
    }
} 