package com.ryl.engineer.service;

import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.dto.TaskDTO;
import com.ryl.engineer.dto.TaskDetailDTO;
import com.ryl.engineer.dto.TaskFlowDTO;
import com.ryl.engineer.dto.TaskStepDefinitionDTO;
import com.ryl.engineer.dto.request.CreateTaskRequest;
import com.ryl.engineer.dto.request.RejectTaskRequest;
import com.ryl.engineer.dto.request.TaskFlowStatusRequest;
import com.ryl.engineer.dto.request.TaskQueryRequest;
import com.ryl.engineer.dto.request.TaskStepUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 任务服务接口
 */
public interface TaskService {
    
    /**
     * 创建任务
     * @param request 创建任务请求
     * @return 任务ID
     */
    String createTask(CreateTaskRequest request);

    /**
     * 为来自小程序的、尚未初始化的任务创建步骤
     * @param taskId 需要初始化步骤的任务ID
     * @param steps  步骤定义的列表
     * @return 如果操作成功，返回 true
     */
    boolean initializeTaskSteps(String taskId, List<TaskStepDefinitionDTO> steps);
    
    /**
     * 分页查询任务列表
     * @param request 查询条件
     * @return 任务分页列表
     */
    PageResult<TaskDetailDTO> getTaskPage(TaskQueryRequest request);
    
    /**
     * 获取任务详情
     * @param taskId 任务ID
     * @return 任务详情
     */
    TaskDTO getTaskDetail(String taskId);
    
    /**
     * 获取任务流程
     * @param taskId 任务ID
     * @return 任务流程
     */
    TaskFlowDTO getTaskFlow(String taskId);
    
    /**
     * 获取任务状态历史
     * @param taskId 任务ID
     * @return 状态历史列表
     */
    List<Map<String, Object>> getTaskStatusHistory(String taskId);
    
    /**
     * 更新任务步骤
     * @param request 更新步骤请求
     * @return 是否成功
     */
    boolean updateTaskStep(TaskStepUpdateRequest request);
    
    /**
     * 更新任务流程状态
     * @param request 流程状态更新请求
     * @return 是否成功
     */
    boolean updateTaskFlowStatus(TaskFlowStatusRequest request);
    
    /**
     * 更新任务
     * @param taskId 任务ID
     * @param taskDTO 任务信息
     * @return 是否成功
     */
    boolean updateTask(String taskId, TaskDTO taskDTO);
    
    /**
     * 取消任务
     * @param taskId 任务ID
     * @param reason 取消原因
     * @return 是否成功
     */
    boolean cancelTask(String taskId, String reason);
    
    /**
     * 分配工程师
     * @param taskId 任务ID
     * @param engineerIds 工程师ID列表
     * @return 是否成功
     */
    boolean assignEngineers(String taskId, List<Long> engineerIds);
    
    /**
     * 获取任务统计信息
     * @return 统计信息
     */
    Map<String, Object> getTaskStatistics();
    
    /**
     * 更新任务上门决策
     * @param taskId 任务ID
     * @param stepIndex 步骤索引
     * @param requiresVisit 是否需要上门
     * @return 是否成功
     */
    boolean updateTaskSiteVisitDecision(String taskId, Integer stepIndex, Boolean requiresVisit);
    
    /**
     * 更新任务上门决策（包含约定上门时间）
     * @param taskId 任务ID
     * @param stepIndex 步骤索引
     * @param requiresVisit 是否需要上门
     * @param visitAppointmentTime 约定上门时间（可选）
     * @return 是否成功
     */
    boolean updateTaskSiteVisitDecision(String taskId, Integer stepIndex, Boolean requiresVisit, String visitAppointmentTime);

    /**
     * 重置任务的上门决策状态
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean resetTaskSiteVisitDecision(String taskId);
    
    /**
     * 更新任务报价确认状态
     * @param taskId 任务ID
     * @param confirmed 是否确认
     * @return 是否成功
     */
    boolean updateTaskPriceConfirmation(String taskId, Boolean confirmed);
    
    /**
     * 更新任务报价
     * @param taskId 任务ID
     * @param stepIndex 步骤索引
     * @param price 报价金额
     * @return 是否成功
     */
    boolean updateTaskPrice(String taskId, Integer stepIndex, Double price);
    
    /**
     * 获取任务报价确认状态
     * @param taskId 任务ID
     * @return 报价确认状态信息
     */
    Map<String, Object> getTaskPriceConfirmation(String taskId);
    
    /**
     * 转出任务
     * @param taskId 任务ID
     * @param engineerId 接收任务的工程师ID
     * @param note 转出备注
     * @param operatorId 操作人ID
     * @param isOperatorAdmin 操作人是否为管理员
     * @return 是否成功
     */
    boolean transferTask(String taskId, Long engineerId, String note, Long operatorId, Boolean isOperatorAdmin);
    
    /**
     * 通知客户报价
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean notifyCustomerOfPrice(String taskId);

    /**
     * 确认收款
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean confirmPayment(String taskId);

    /**
     * 更新任务状态
     * @param taskId 任务ID
     * @param status 新状态
     * @param note 状态变更说明
     * @return 是否成功
     */
    boolean updateTaskStatus(String taskId, String status, String note);

    /**
     * 工程师接受任务
     * @param taskId 任务ID
     */
    void acceptTask(Long taskId);

    /**
     * 工程师拒绝任务
     * @param taskId 任务ID
     * @param request 拒绝任务的请求体，包含原因和转派目标
     */
    void rejectTask(Long taskId, RejectTaskRequest request);

    /**
     * 添加步骤记录
     * @param taskId 任务ID
     * @param stepId 步骤ID
     * @param content 记录内容
     * @param duration 花费时间（分钟）
     * @param images 图片列表
     * @param attachments 附件列表
     * @param operatorId 操作员ID
     */
    void addStepRecord(String taskId, Long stepId, String content, Integer duration, List<MultipartFile> images, List<MultipartFile> attachments, Long operatorId);
} 