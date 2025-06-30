package com.ryl.engineer.service;

import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.entity.AssistanceRequest;

import java.util.List;

/**
 * 协助请求服务接口
 */
public interface AssistanceService {
    
    /**
     * 获取协助请求列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @param status 状态过滤
     * @param keyword 关键词搜索
     * @return 协助请求分页列表
     */
    PageResult<AssistanceRequest> getAssistanceRequestList(Long userId, Integer page, Integer size, 
                                                             String status, String keyword);
    
    /**
     * 获取协助请求详情
     * @param requestId 请求ID
     * @return 协助请求详情
     */
    AssistanceRequest getAssistanceRequestDetail(Long requestId);
    
    /**
     * 创建协助请求
     * @param requesterId 请求者ID
     * @param title 标题
     * @param content 内容
     * @param type 类型
     * @param taskId 关联任务ID
     * @param recipientIds 接收者ID列表
     * @return 创建的协助请求信息
     */
    AssistanceRequest createAssistanceRequest(Long requesterId, String title, String content,
                                                String type, String taskId, List<Long> recipientIds);
    
    /**
     * 回复协助请求
     * @param requestId 请求ID
     * @param responderId 回复者ID
     * @param content 回复内容
     * @return 更新后的协助请求信息
     */
    AssistanceRequest replyAssistanceRequest(Long requestId, Long responderId, String content);
    
    /**
     * 更新协助请求状态
     * @param requestId 请求ID
     * @param userId 操作用户ID
     * @param status 状态
     * @param reason 原因（可选）
     * @return 更新后的协助请求信息
     */
    AssistanceRequest updateAssistanceRequestStatus(Long requestId, Long userId, 
                                                      String status, String reason);
    
    /**
     * 添加接收者到协助请求
     * @param requestId 请求ID
     * @param recipientIds 接收者ID列表
     * @return 更新后的协助请求信息
     */
    AssistanceRequest addRecipientsToAssistanceRequest(Long requestId, List<Long> recipientIds);
    
    /**
     * 标记协助请求为已读
     * @param requestId 请求ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAssistanceRequestRead(Long requestId, Long userId);
} 