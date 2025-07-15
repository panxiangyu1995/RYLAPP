package com.ryl.engineer.service.impl;

import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.entity.AssistanceRequest;
import com.ryl.engineer.entity.AssistanceRequestRecipient;
import com.ryl.engineer.entity.User;
import com.ryl.engineer.mapper.AssistanceRequestMapper;
import com.ryl.engineer.mapper.AssistanceRequestRecipientMapper;
import com.ryl.engineer.mapper.UserMapper;
import com.ryl.engineer.service.AssistanceService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 协助请求服务实现类
 */
@Service
public class AssistanceServiceImpl implements AssistanceService {

    @Autowired
    private AssistanceRequestMapper requestMapper;

    @Autowired
    private AssistanceRequestRecipientMapper recipientMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public PageResult<AssistanceRequest> getAssistanceRequestList(Long userId, Integer page, Integer size,
                                                              String status, String keyword) {
        if (userId == null) {
            System.out.println("警告: 用户ID为空，返回空列表");
            return new PageResult<AssistanceRequest>(Collections.emptyList(), 0L, size.longValue(), page.longValue());
        }

        // 1. 获取总记录数
        long total = requestMapper.countByUserId(userId, status, keyword);
        if (total == 0) {
            return new PageResult<AssistanceRequest>(Collections.emptyList(), 0L, size.longValue(), page.longValue());
        }

        // 2. 计算分页参数
        long offset = (long) (page - 1) * size;

        // 3. 获取当前页数据
        List<AssistanceRequest> list = requestMapper.selectByUserId(userId, status, keyword, offset, size);

        // 4. 组装并返回结果
        return new PageResult<AssistanceRequest>(list, total, size.longValue(), page.longValue());
    }

    @Override
    public AssistanceRequest getAssistanceRequestDetail(Long requestId) {
        return requestMapper.selectById(requestId);
    }

    @Override
    @Transactional
    public AssistanceRequest createAssistanceRequest(Long requesterId, String title, String content,
                                                  String type, String taskId, List<Long> recipientIds) {
        // 创建协助请求
        AssistanceRequest request = new AssistanceRequest();
        request.setTitle(title);
        request.setContent(content);
        request.setType(type);
        request.setTaskId(taskId);
        request.setRequesterId(requesterId);
        
        // 获取请求者姓名
        User requester = userMapper.selectById(requesterId);
        request.setRequesterName(requester != null ? requester.getName() : "未知用户");
        
        // 设置初始状态为待处理
        request.setStatus("pending");
        request.setCreateTime(new Date());
        request.setUpdateTime(new Date());
        
        // 保存请求
        requestMapper.insert(request);
        
        // 添加接收者
        if (recipientIds != null && !recipientIds.isEmpty()) {
            for (Long recipientId : recipientIds) {
                AssistanceRequestRecipient recipient = new AssistanceRequestRecipient();
                recipient.setRequestId(request.getId());
                recipient.setRecipientId(recipientId);
                recipient.setIsRead(0);
                recipient.setCreateTime(new Date());
                recipient.setUpdateTime(new Date());
                
                recipientMapper.insert(recipient);
            }
        }
        
        return request;
    }

    @Override
    @Transactional
    public AssistanceRequest replyAssistanceRequest(Long requestId, Long responderId, String content) {
        // 获取协助请求
        AssistanceRequest request = requestMapper.selectById(requestId);
        
        if (request != null) {
            // 获取回复者姓名
            User responder = userMapper.selectById(responderId);
            
            // 更新请求的回复信息
            request.setResponseContent(content);
            request.setResponderId(responderId);
            request.setResponderName(responder != null ? responder.getName() : "未知用户");
            request.setResponseTime(new Date());
            request.setStatus("replied");
            request.setUpdateTime(new Date());
            
            // 保存更新
            requestMapper.update(request);
        }
        
        return request;
    }

    @Override
    @Transactional
    public AssistanceRequest updateAssistanceRequestStatus(Long requestId, Long userId, String status, String reason) {
        // 获取协助请求
        AssistanceRequest request = requestMapper.selectById(requestId);
        
        if (request != null) {
            // 更新状态
            request.setStatus(status);
            
            // 如果是取消状态，记录原因
            if ("cancelled".equals(status) && StringUtils.hasText(reason)) {
                request.setResponseContent(reason);
            }
            
            // 如果是标记为已完成，记录处理人
            if ("completed".equals(status)) {
                User user = userMapper.selectById(userId);
                request.setResponderId(userId);
                request.setResponderName(user != null ? user.getName() : "未知用户");
                request.setResponseTime(new Date());
            }
            
            request.setUpdateTime(new Date());
            
            // 保存更新
            requestMapper.update(request);
        }
        
        return request;
    }

    @Override
    @Transactional
    public AssistanceRequest addRecipientsToAssistanceRequest(Long requestId, List<Long> recipientIds) {
        // 获取协助请求
        AssistanceRequest request = requestMapper.selectById(requestId);
        
        if (request != null && recipientIds != null && !recipientIds.isEmpty()) {
            // 获取已有的接收者
            List<AssistanceRequestRecipient> existingRecipients = recipientMapper.selectByRequestId(requestId);
            List<Long> existingRecipientIds = existingRecipients.stream()
                .map(AssistanceRequestRecipient::getRecipientId)
                .collect(Collectors.toList());
            
            // 添加新的接收者
            for (Long recipientId : recipientIds) {
                if (!existingRecipientIds.contains(recipientId)) {
                    AssistanceRequestRecipient recipient = new AssistanceRequestRecipient();
                    recipient.setRequestId(requestId);
                    recipient.setRecipientId(recipientId);
                    recipient.setIsRead(0);
                    recipient.setCreateTime(new Date());
                    recipient.setUpdateTime(new Date());
                    
                    recipientMapper.insert(recipient);
                }
            }
        }
        
        return request;
    }

    @Override
    @Transactional
    public boolean markAssistanceRequestRead(Long requestId, Long userId) {
        // 获取接收者关系
        AssistanceRequestRecipient recipient = recipientMapper.selectByRequestIdAndRecipientId(requestId, userId);
        
        if (recipient != null && recipient.getIsRead() == 0) {
            // 更新为已读
            recipient.setIsRead(1);
            recipient.setReadTime(new Date());
            recipient.setUpdateTime(new Date());
            
            recipientMapper.update(recipient);
            return true;
        }
        
        return false;
    }
} 