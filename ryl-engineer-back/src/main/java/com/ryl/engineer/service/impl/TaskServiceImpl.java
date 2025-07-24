package com.ryl.engineer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.dto.TaskDetailDTO;
import com.ryl.engineer.dto.AttachmentDTO;
import com.ryl.engineer.dto.EngineerDTO;
import com.ryl.engineer.dto.TaskDTO;
import com.ryl.engineer.dto.TaskFlowDTO;
import com.ryl.engineer.dto.TaskStepDTO;
import com.ryl.engineer.dto.TaskStepDefinitionDTO;
import com.ryl.engineer.dto.request.CreateTaskRequest;
import com.ryl.engineer.dto.request.TaskFlowStatusRequest;
import com.ryl.engineer.dto.request.TaskQueryRequest;
import com.ryl.engineer.dto.request.TaskStepUpdateRequest;
import com.ryl.engineer.entity.Task;
import com.ryl.engineer.entity.TaskEngineer;
import com.ryl.engineer.entity.TaskImage;
import com.ryl.engineer.entity.TaskAttachment;
import com.ryl.engineer.entity.TaskStep;
import com.ryl.engineer.entity.TaskActivity;
import com.ryl.engineer.entity.TaskStepAttachment;
import com.ryl.engineer.entity.TaskStepRecord;
import com.ryl.engineer.entity.RecordImage;
import com.ryl.engineer.entity.TaskTransferHistory;
import com.ryl.engineer.entity.TaskStatusHistory;
import com.ryl.engineer.entity.User;
import com.ryl.engineer.mapper.TaskEngineerMapper;
import com.ryl.engineer.mapper.TaskImageMapper;
import com.ryl.engineer.mapper.TaskMapper;
import com.ryl.engineer.mapper.TaskAttachmentMapper;
import com.ryl.engineer.mapper.TaskStepAttachmentMapper;
import com.ryl.engineer.mapper.TaskStepMapper;
import com.ryl.engineer.mapper.TaskStepRecordMapper;
import com.ryl.engineer.mapper.RecordImageMapper;
import com.ryl.engineer.mapper.TaskActivityMapper;
import com.ryl.engineer.mapper.TaskTransferHistoryMapper;
import com.ryl.engineer.mapper.TaskStatusHistoryMapper;
import com.ryl.engineer.mapper.UserMapper;
import com.ryl.common.constant.TaskStatusConstants;
import com.ryl.engineer.service.ChatService;
import com.ryl.engineer.service.TaskService;
import com.ryl.engineer.util.FileUrlConverter;
import com.ryl.engineer.vo.EngineerSimpleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
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
    private TaskAttachmentMapper taskAttachmentMapper;

    @Autowired
    private TaskStepMapper taskStepMapper;

    @Autowired
    private TaskStepRecordMapper taskStepRecordMapper;

    @Autowired
    private TaskStepAttachmentMapper taskStepAttachmentMapper;

    @Autowired
    private RecordImageMapper recordImageMapper;
    
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
    
    @Autowired
    private ChatService chatService;

    @Autowired
    private FileUrlConverter fileUrlConverter;
    
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
            task.setIsExternalTask(1); // 所有通过此API创建的任务都标记为系统外任务
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
            task.setQuantity(request.getQuantity());
            // task.setAttachments(request.getAttachments()); // 旧字段，不再使用
            
            // 7. 设置任务类型特定字段
            if ("verification".equals(taskType)) {
                task.setVerificationType(request.getVerificationType());
            } else if ("selection".equals(taskType)) {
                task.setPurpose(request.getPurpose());
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
            
            // 11. 根据前端传递的定义创建任务步骤
            if (request.getSteps() != null && !request.getSteps().isEmpty()) {
                List<TaskStep> stepsToCreate = new ArrayList<>();
                for (com.ryl.engineer.dto.TaskStepDefinitionDTO stepDef : request.getSteps()) {
                    TaskStep newStep = new TaskStep();
                    newStep.setTaskId(taskId);
                    newStep.setStepIndex(stepDef.getStepIndex());
                    newStep.setTitle(stepDef.getTitle());

                    // 如果是决策步骤，则设置特殊的stepKey
                    if ("判断是否需要上门".equals(stepDef.getTitle())) {
                        newStep.setStepKey("site-visit-decision");
                    }

                    // 初始状态默认为 'pending', 第一个步骤为 'in-progress'
                    newStep.setStatus(stepDef.getStepIndex() == 0 ? "in-progress" : "pending");
                    newStep.setCreateTime(LocalDateTime.now());
                    newStep.setUpdateTime(LocalDateTime.now());
                    stepsToCreate.add(newStep);
                }
                // 使用 taskStepMapper 逐条插入
                for (TaskStep stepToCreate : stepsToCreate) {
                    taskStepMapper.insert(stepToCreate);
                }
            }

            return taskId;
        } catch (Exception e) {
            log.error("创建任务失败", e);
            throw new RuntimeException("创建任务失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public boolean initializeTaskSteps(String taskId, List<TaskStepDefinitionDTO> steps) {
        // 1. 检查任务是否存在
        Task task = taskMapper.selectOne(Wrappers.<Task>lambdaQuery().eq(Task::getTaskId, taskId));
        if (task == null) {
            log.warn("尝试为不存在的任务初始化步骤, taskId: {}", taskId);
            return false;
        }

        // 2. 检查任务是否已经有步骤
        long existingStepsCount = taskStepMapper.selectCount(Wrappers.<TaskStep>lambdaQuery().eq(TaskStep::getTaskId, taskId));
        if (existingStepsCount > 0) {
            log.warn("任务 {} 已有步骤，无需再次初始化。", taskId);
            return false;
        }

        // 3. 批量创建新步骤
        if (steps != null && !steps.isEmpty()) {
            List<TaskStep> stepsToCreate = new ArrayList<>();
            for (TaskStepDefinitionDTO stepDef : steps) {
                TaskStep newStep = new TaskStep();
                newStep.setTaskId(taskId);
                newStep.setStepIndex(stepDef.getStepIndex());
                newStep.setTitle(stepDef.getTitle());

                // 如果是决策步骤，则设置特殊的stepKey
                if ("判断是否需要上门".equals(stepDef.getTitle())) {
                    newStep.setStepKey("site-visit-decision");
                }

                // 初始状态默认为 'pending', 第一个步骤为 'in-progress'
                newStep.setStatus(stepDef.getStepIndex() == 0 ? "in-progress" : "pending");
                newStep.setCreateTime(LocalDateTime.now());
                newStep.setUpdateTime(LocalDateTime.now());
                stepsToCreate.add(newStep);
            }
            // 逐条插入
            for (TaskStep stepToCreate : stepsToCreate) {
                taskStepMapper.insert(stepToCreate);
            }
            log.info("成功为任务 {} 初始化了 {} 个步骤。", taskId, stepsToCreate.size());
        }
        
        return true;
    }

    /**
     * 分页查询任务列表
     */
    @Override
    public PageResult<TaskDetailDTO> getTaskPage(TaskQueryRequest request) {
        // 1. 参数校验和分页对象创建
        if (request.getPage() == null || request.getPage() < 1) {
            request.setPage(1);
        }
        if (request.getSize() == null || request.getSize() <= 0) {
            request.setSize(20);
        }
        Page<Task> page = new Page<>(request.getPage(), request.getSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Task::getCreateTime);

        // 按工程师ID筛选
        if (request.getEngineerId() != null) {
            List<String> taskIdsForEngineer = taskEngineerMapper.selectList(
                    new LambdaQueryWrapper<TaskEngineer>()
                            .eq(TaskEngineer::getEngineerId, request.getEngineerId())
                            .select(TaskEngineer::getTaskId)
            ).stream().map(TaskEngineer::getTaskId).distinct().collect(Collectors.toList());

            if (taskIdsForEngineer.isEmpty()) {
                // 返回一个空的TaskDetailDTO分页结果，并保持分页信息
                Page<TaskDetailDTO> emptyPage = new Page<>(request.getPage(), request.getSize(), 0);
                return PageResult.fromPage(emptyPage);
            }
            queryWrapper.in(Task::getTaskId, taskIdsForEngineer);
        }

        // 其他查询条件
        if (StringUtils.hasText(request.getStatus())) {
            queryWrapper.eq(Task::getStatus, request.getStatus());
        }
        if (StringUtils.hasText(request.getTaskType())) {
            queryWrapper.eq(Task::getTaskType, request.getTaskType());
        }
        if (StringUtils.hasText(request.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper.like(Task::getTitle, request.getKeyword())
                    .or().like(Task::getTaskId, request.getKeyword()));
        }

        // 3. 执行主分页查询
        taskMapper.selectPage(page, queryWrapper);
        List<Task> tasks = page.getRecords();

        if (tasks.isEmpty()) {
            // 返回一个空的TaskDetailDTO分页结果，并保持分页信息
            Page<TaskDetailDTO> emptyPage = new Page<>(request.getPage(), request.getSize(), 0);
            return PageResult.fromPage(emptyPage);
        }

        // 4. 二次查询：批量获取所有相关任务的工程师信息
        List<String> taskIds = tasks.stream().map(Task::getTaskId).collect(Collectors.toList());
        List<EngineerSimpleVO> allEngineers = taskEngineerMapper.findEngineersByTaskIds(taskIds);

        // 将工程师信息按taskId分组
        Map<String, List<EngineerSimpleVO>> engineersByTaskId = allEngineers.stream()
                .collect(Collectors.groupingBy(EngineerSimpleVO::getTaskId));

        // 5. 组装TaskDetailDTO
        List<TaskDetailDTO> detailDTOs = tasks.stream().map(task -> {
            TaskDetailDTO dto = new TaskDetailDTO();
            BeanUtils.copyProperties(task, dto);
            dto.setEngineers(engineersByTaskId.getOrDefault(task.getTaskId(), Collections.emptyList()));
            return dto;
        }).collect(Collectors.toList());

        // 6. 构建并返回最终的分页结果
        Page<TaskDetailDTO> resultPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resultPage.setRecords(detailDTOs);

        return PageResult.fromPage(resultPage);
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

        // 查询任务附件
        List<TaskAttachment> attachments = taskAttachmentMapper.selectList(
                Wrappers.<TaskAttachment>lambdaQuery().eq(TaskAttachment::getTaskId, taskId)
        );
        
        // 查询任务工程师
        List<TaskEngineer> engineers = taskEngineerMapper.findByTaskId(taskId);
        
        // 转换为DTO
        TaskDTO taskDTO = convertToDTO(task);
        
        // 设置图片
        if (images != null && !images.isEmpty()) {
            taskDTO.setImageUrls(images.stream()
                    .map(image -> fileUrlConverter.toFullUrl(image.getImageUrl()))
                    .collect(Collectors.toList()));
        }

        // 设置附件
        if (attachments != null && !attachments.isEmpty()) {
            taskDTO.setAttachments(attachments.stream().map(attachment ->
                AttachmentDTO.builder()
                    .id(attachment.getId())
                    .fileName(attachment.getFileName())
                    .fileUrl(fileUrlConverter.toFullUrl(attachment.getFileUrl()))
                    .fileType(attachment.getFileType())
                    .fileSize(attachment.getFileSize())
                    .build()
            ).collect(Collectors.toList()));
        }
        
        // 设置工程师信息
        if (engineers != null && !engineers.isEmpty()) {
            taskDTO.setEngineers(engineers.stream().map(e -> {
                EngineerDTO engineerDTO = new EngineerDTO();
                engineerDTO.setId(e.getEngineerId());
                
                // 从用户表查询真实的工程师信息
                User engineerUser = userMapper.selectById(e.getEngineerId());
                if (engineerUser != null) {
                    engineerDTO.setName(engineerUser.getName());
                    engineerDTO.setAvatar(fileUrlConverter.toFullUrl(engineerUser.getAvatar()));
                    engineerDTO.setDepartment(engineerUser.getDepartment());
                    engineerDTO.setMobile(engineerUser.getMobile());
                } else {
                    // 如果找不到用户，提供默认值
                    engineerDTO.setName("工程师" + e.getEngineerId());
                    engineerDTO.setAvatar("/img/default-avatar.png");
                    engineerDTO.setDepartment("未知");
                    engineerDTO.setMobile("未知");
                }

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

        String status = task.getStatus();
        String statusText;

        // 增加对status为null的健壮性处理
        if (status == null) {
            statusText = "待处理"; // 为null的旧数据默认显示为待处理
        } else {
            // 根据状态生成状态文本
            switch (status) {
                case "draft":
                    statusText = "草稿";
                    break;
                case "pending":
                    statusText = "待处理";
                    break;
                case TaskStatusConstants.PENDING_CONFIRMATION:
                    statusText = "待确认";
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
        }
        dto.setStatusText(statusText);

        // 转换布尔值
        dto.setIsExternalTask(task.getIsExternalTask() != null && task.getIsExternalTask() == 1);
        dto.setNeedSiteVisit(task.getNeedSiteVisit() != null && task.getNeedSiteVisit() == 1);

        // 显式设置报价和付款相关字段
        dto.setPrice(task.getPrice());
        dto.setPriceConfirmed(task.getPriceConfirmed());
        dto.setIsPaid(task.getIsPaid());
        
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
    
    /**
     * 获取任务流程
     */
    @Override
    public TaskFlowDTO getTaskFlow(String taskId) {
        // 1. 查询任务信息
        Task task = taskMapper.selectOne(
                Wrappers.<Task>lambdaQuery().eq(Task::getTaskId, taskId)
        );
        
        if (task == null) {
            log.warn("请求任务流程失败：找不到任务，ID = {}", taskId);
            return null;
        }
        
        // 2. 查询该任务的所有步骤定义
        List<TaskStep> steps = taskStepMapper.selectList(
            Wrappers.<TaskStep>lambdaQuery()
                   .eq(TaskStep::getTaskId, taskId)
                   .orderByAsc(TaskStep::getStepIndex)
        );
        
        // 3. 构建流程DTO
        TaskFlowDTO flowDTO = new TaskFlowDTO();
        flowDTO.setTaskId(taskId);
        flowDTO.setTitle(task.getTitle());
        flowDTO.setCurrentStep(task.getCurrentStep());
        flowDTO.setTaskType(task.getTaskType());
        flowDTO.setStatus(task.getStatus());

        // 如果没有步骤（来自小程序端的任务），则直接返回基础信息
        if (steps == null || steps.isEmpty()) {
            log.warn("任务 {} 没有任何步骤，可能来自小程序端。返回一个空的步骤列表。", taskId);
            flowDTO.setSteps(new ArrayList<>());
            return flowDTO;
        }
        
        // 4. 构建并聚合每个步骤的DTO
        List<TaskStepDTO> stepDTOs = steps.stream().map(step -> {
            TaskStepDTO dto = new TaskStepDTO();
            dto.setId(step.getId()); // 传递步骤的数据库ID
            dto.setStepKey(step.getStepKey()); // 传递业务标识符
            dto.setIndex(step.getStepIndex());
            dto.setName(step.getTitle());
            dto.setStatus(step.getStatus());
            dto.setStartTime(step.getStartTime());
            dto.setEndTime(step.getEndTime());

            // 5. 查询该步骤最新的一个记录（使用分页，兼容SQL Server）
            Page<TaskStepRecord> page = new Page<>(1, 1); // 查询第一页，每页一条
            LambdaQueryWrapper<TaskStepRecord> recordQuery = Wrappers.<TaskStepRecord>lambdaQuery()
                    .eq(TaskStepRecord::getStepId, step.getId())
                    .orderByDesc(TaskStepRecord::getCreateTime);
            
            Page<TaskStepRecord> resultPage = taskStepRecordMapper.selectPage(page, recordQuery);
            
            TaskStepRecord lastRecord = null;
            if (resultPage != null && !resultPage.getRecords().isEmpty()) {
                lastRecord = resultPage.getRecords().get(0);
            }

            // 6. 如果有记录，则填充描述、图片和文件
            if (lastRecord != null) {
                dto.setRecordContent(lastRecord.getContent());
                
                // 7. 分别查询与该记录关联的图片和附件
                // 查询图片
                List<RecordImage> images = recordImageMapper.selectList(
                    Wrappers.<RecordImage>lambdaQuery()
                        .eq(RecordImage::getRecordId, lastRecord.getId())
                );
                if (images != null && !images.isEmpty()) {
                    dto.setImages(images.stream().map(image -> fileUrlConverter.toFullUrl(image.getImageUrl())).collect(Collectors.toList()));
                }

                // 查询非图片附件
                List<TaskStepAttachment> attachments = taskStepAttachmentMapper.selectList(
                    Wrappers.<TaskStepAttachment>lambdaQuery()
                            .eq(TaskStepAttachment::getRecordId, lastRecord.getId())
                );
                
                if (attachments != null && !attachments.isEmpty()) {
                    // (如果TaskStepDTO需要)可以同样方式填充其他文件
                    List<AttachmentDTO> fileDTOs = attachments.stream()
                        .map(a -> AttachmentDTO.builder()
                                .id(a.getId())
                                .fileName(a.getFileName())
                                .fileUrl(fileUrlConverter.toFullUrl(a.getFileUrl()))
                                .fileType(a.getFileType())
                                .fileSize(a.getFileSize())
                                .build())
                        .collect(Collectors.toList());
                    dto.setAttachments(fileDTOs);
                }
            }
            
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
                // actions.add("cancel"); // 根据业务逻辑决定是否需要
            }
            dto.setActions(actions);
            
            // 设置是否当前步骤到表单数据中
            Map<String, Object> formData = new HashMap<>();
            formData.put("isCurrent", step.getStepIndex().equals(task.getCurrentStep()));
            formData.put("statusText", statusText);
            dto.setFormData(formData);
            
            return dto;
        }).collect(Collectors.toList());
        
        flowDTO.setSteps(stepDTOs);
        
        return flowDTO;
    }

    /* isImage方法不再需要，因为后端已经分离了数据
    private boolean isImage(String fileType) {
        if (fileType == null || fileType.isEmpty()) {
            return false;
        }
        String lowerCaseType = fileType.toLowerCase();
        return lowerCaseType.equals("jpg") || lowerCaseType.equals("jpeg") || 
               lowerCaseType.equals("png") || lowerCaseType.equals("gif") ||
               lowerCaseType.equals("bmp");
    }
    */
    
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
        if (taskDTO.getDeviceName() != null) {
            task.setDeviceName(taskDTO.getDeviceName());
        }
        if (taskDTO.getDeviceModel() != null) {
            task.setDeviceModel(taskDTO.getDeviceModel());
        }
        if (taskDTO.getDeviceBrand() != null) {
            task.setDeviceBrand(taskDTO.getDeviceBrand());
        }
        if (taskDTO.getDescription() != null) {
            task.setDescription(taskDTO.getDescription());
        }
        if (taskDTO.getQuantity() != null) {
            task.setQuantity(taskDTO.getQuantity());
        }
        // 旧的 attachments 字段不再更新
        // if (taskDTO.getAttachments() != null) {
        //     task.setAttachments(taskDTO.getAttachments());
        // }
        if (taskDTO.getNeedSiteVisit() != null) {
            task.setNeedSiteVisit(taskDTO.getNeedSiteVisit() ? 1 : 0);
        }
        
        // 3. 更新任务
        taskMapper.updateById(task);
        
        // 4. 如果有图片更新，则更新图片
        if (taskDTO.getImageUrls() != null && !taskDTO.getImageUrls().isEmpty()) {
            // 删除原有图片
            taskImageMapper.delete(
                    Wrappers.lambdaQuery(TaskImage.class).eq(TaskImage::getTaskId, taskId)
            );
            
            // 添加新图片
            List<TaskImage> images = taskDTO.getImageUrls().stream().map(url -> {
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
        log.info("仅保存上门决策: taskId={}, requiresVisit={}, visitAppointmentTime={}",
                taskId, requiresVisit, visitAppointmentTime);

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

            // 2. 更新任务的“是否需要上门”决策
            task.setNeedSiteVisit(requiresVisit ? 1 : 0);

            // 3. 如果需要上门，则更新或清空约定上门时间
            if (requiresVisit) {
                if (StringUtils.hasText(visitAppointmentTime)) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                        LocalDateTime appointmentTime = LocalDateTime.parse(visitAppointmentTime, formatter);
                        task.setVisitAppointmentTime(appointmentTime);
                        log.info("设置约定上门时间: {}", appointmentTime);
                    } catch (Exception e) {
                        log.error("解析约定上门时间失败: {}, 保持数据库原值", e.getMessage());
                        // 解析失败时，可以选择不更新时间，或者设置为null
                        // task.setVisitAppointmentTime(null);
                    }
                } else {
                    // 如果标记需要上门但没有提供时间，可以选择清空或不处理
                     task.setVisitAppointmentTime(null);
                }
            }


            // 4. 保存对Task的更新
            taskMapper.updateById(task);

            log.info("成功保存上门决策: taskId={}, requiresVisit={}", taskId, requiresVisit);
            return true;
        } catch (Exception e) {
            log.error("保存上门决策异常: taskId={}, 错误信息={}", taskId, e.getMessage(), e);
            throw e; // 抛出异常以触发事务回滚
        }
    }

    // 原有的updateTaskSiteVisitDecision方法调用新方法
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTaskSiteVisitDecision(String taskId, Integer stepIndex, Boolean requiresVisit) {
        return updateTaskSiteVisitDecision(taskId, stepIndex, requiresVisit, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetTaskSiteVisitDecision(String taskId) {
        log.info("开始重置任务的上门决策: taskId={}", taskId);
        try {
            Task task = taskMapper.selectOne(
                Wrappers.<Task>lambdaQuery().eq(Task::getTaskId, taskId)
            );
            if (task == null) {
                log.error("重置决策失败：找不到任务, taskId={}", taskId);
                return false;
            }

            task.setNeedSiteVisit(null);
            task.setVisitAppointmentTime(null);

            taskMapper.updateById(task);

            log.info("成功重置任务的上门决策: taskId={}", taskId);
            return true;
        } catch (Exception e) {
            log.error("重置任务上门决策时发生异常: taskId={}, 错误: {}", taskId, e.getMessage(), e);
            throw e; // 让事务回滚
        }
    }
    
    /**
     * 转出任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean transferTask(String taskId, Long engineerId, String note, Long operatorId, Boolean isOperatorAdmin) {
        log.info("转出任务: taskId={}, engineerId={}, note={}, operatorId={}, isAdmin={}", taskId, engineerId, note, operatorId, isOperatorAdmin);

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
            User operator = userMapper.selectById(operatorId);
            String operatorName = (operator != null) ? operator.getName() : "未知操作员";
            activity.setContent("任务由 " + operatorName + " 转出给工程师: " + engineer.getName());
            activity.setOperatorId(operatorId);
            activity.setOperatorName(operatorName);
            activity.setCreateTime(new Date());
            taskActivityMapper.insert(activity);

            // 7. 发送通知
            // 通知新负责人
            try {
                // 查询任务图片作为预览
                List<TaskImage> taskImages = taskImageMapper.selectList(
                        Wrappers.<TaskImage>lambdaQuery()
                                .eq(TaskImage::getTaskId, taskId)
                );
                List<String> previewImageUrls = taskImages.stream()
                                                        .map(image -> fileUrlConverter.toFullUrl(image.getImageUrl())) // 修正
                                                        .limit(3) // 在Java流中限制数量
                                                        .collect(Collectors.toList());

                // 构建结构化的任务卡片消息
                Map<String, Object> messageCard = new HashMap<>();
                messageCard.put("type", "task_card");
                messageCard.put("taskId", task.getTaskId());
                messageCard.put("title", "您有一个新指派的任务");
                messageCard.put("taskTitle", task.getTitle());
                messageCard.put("customerName", task.getCustomer());
                messageCard.put("deviceName", task.getDeviceName());
                messageCard.put("description", task.getDescription());
                messageCard.put("previewImages", previewImageUrls);

                ObjectMapper objectMapper = new ObjectMapper();
                String messageContent = objectMapper.writeValueAsString(messageCard);
                
                chatService.sendSystemMessage(engineerId, messageContent);

            } catch (Exception e) {
                log.error("构建并发送任务卡片消息失败, taskId: {}. 回退到发送纯文本消息。", taskId, e);
                // Fallback to plain text message
                String newEngineerMessage = String.format("您被指派了一个新的任务：%s (%s)", task.getTitle(), task.getTaskId());
                chatService.sendSystemMessage(engineerId, newEngineerMessage);
            }


            // 如果是管理员操作，且原负责人存在且不是自己，则通知原负责人
            if (Boolean.TRUE.equals(isOperatorAdmin) && originalEngineerId != null && !originalEngineerId.equals(operatorId)) {
                User originalEngineer = userMapper.selectById(originalEngineerId);
                if (originalEngineer != null) {
                    String oldEngineerMessage = String.format("您负责的任务 %s (%s) 已被管理员 %s 转派给 %s。",
                            task.getTitle(), task.getTaskId(), operatorName, engineer.getName());
                    chatService.sendSystemMessage(originalEngineerId, oldEngineerMessage);
                }
            }
            
            log.info("转出任务成功: taskId={}, 从工程师ID={} 转给工程师ID={}", taskId, originalEngineerId, engineerId);
            return true;
        } catch (Exception e) {
            log.error("转出任务异常: taskId={}, engineerId={}, 错误信息={}", taskId, engineerId, e.getMessage(), e);
            throw e;
        }
    }

    @Autowired
    private RestTemplate restTemplate;

    @Value("${miniprogram.server.url}")
    private String miniprogramServerUrl;

    @Override
    @Transactional
    public boolean notifyCustomerOfPrice(String taskId) {
        Task task = taskMapper.selectOne(Wrappers.<Task>lambdaQuery().eq(Task::getTaskId, taskId));
        if (task == null || task.getCustomerId() == null) {
            log.error("任务或客户ID为空，无法通知客户");
            return false;
        }

        String url = miniprogramServerUrl + "/api/task/notify-price";
        // Create request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("customerId", task.getCustomerId());
        requestBody.put("taskId", task.getTaskId());
        requestBody.put("price", task.getPrice());
        
        try {
            restTemplate.postForObject(url, requestBody, String.class);
            return true;
        } catch (Exception e) {
            log.error("调用小程序API失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean confirmPayment(String taskId) {
        Task task = taskMapper.selectOne(Wrappers.<Task>lambdaQuery().eq(Task::getTaskId, taskId));
        if (task == null) {
            log.error("任务不存在: {}", taskId);
            return false;
        }

        task.setIsPaid(1);
        taskMapper.updateById(task);

        // 记录活动
        TaskActivity activity = new TaskActivity();
        activity.setTaskId(taskId);
        activity.setActivityType("payment_confirmed");
        activity.setContent("已确认收款");
        activity.setCreateTime(new Date());
        taskActivityMapper.insert(activity);
        
        return true;
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

    @Override
    @Transactional
    public void acceptTask(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在");
        }
        if (!TaskStatusConstants.PENDING_CONFIRMATION.equals(task.getStatus())) {
            throw new IllegalStateException("任务状态不正确，无法接受");
        }

        task.setStatus("进行中");
        taskMapper.updateById(task);

        // 可选：记录状态变更历史
        log.info("工程师已接受任务 #{}", task.getTaskId());
    }

    @Override
    @Transactional
    public void rejectTask(Long taskId, com.ryl.engineer.dto.request.RejectTaskRequest request) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在");
        }
        if (!TaskStatusConstants.PENDING_CONFIRMATION.equals(task.getStatus())) {
            throw new IllegalStateException("任务状态不正确，无法拒绝");
        }

        log.info("工程师拒绝了任务 #{}，原因: {}", task.getTaskId(), request.getReason());

        if ("admin".equalsIgnoreCase(request.getTransferTarget())) {
            // 转派给管理员
            task.setEngineerId(null);
            task.setStatus("pending"); // 重置为待处理/待分配
            taskMapper.updateById(task);

            // 通知所有管理员
            List<User> admins = userMapper.findUsersByRole("ROLE_ADMIN");
            if (admins != null && !admins.isEmpty()) {
                String adminMessage = String.format("任务 #%s (%s) 已被工程师拒绝，原因：%s。请尽快重新指派。",
                        task.getTaskId(), task.getTitle(), request.getReason());
                for (User admin : admins) {
                    chatService.sendSystemMessage(admin.getId(), adminMessage);
                }
            }
        } else {
            // 转派给其他工程师
            try {
                Long newEngineerId = Long.parseLong(request.getTransferTarget());
                task.setEngineerId(newEngineerId.intValue());
                // 状态保持 "待确认"
                taskMapper.updateById(task);

                // 向新工程师发送通知
                String messageContent = String.format(
                    "{\"text\":\"您有一个新的任务（%s）待处理，请及时确认。\",\"actions\":[{\"label\":\"接受\",\"type\":\"accept-task\",\"taskId\":\"%d\"},{\"label\":\"拒绝\",\"type\":\"reject-task\",\"taskId\":\"%d\"}]}",
                    task.getTitle(),
                    task.getId(),
                    task.getId()
                );
                chatService.sendSystemMessage(newEngineerId, messageContent);

            } catch (NumberFormatException e) {
                log.error("无效的转派目标工程师ID: {}", request.getTransferTarget());
                throw new IllegalArgumentException("无效的转派目标");
            }
        }
    }
} 