package com.ryl.engineer.controller;

import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.common.Result;
import com.ryl.engineer.entity.AssistanceRequest;
import com.ryl.engineer.service.AssistanceService;
import com.ryl.engineer.utils.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 协助请求控制器
 */
@RestController
@RequestMapping("/api/v1/assistance")
public class AssistanceController {
    
    @Autowired
    private AssistanceService assistanceService;
    
    /**
     * 获取协助请求列表
     */
    @GetMapping("/list")
    public Result<PageResult<AssistanceRequest>> getAssistanceRequestList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        
        Long userId = UserContextHolder.getUserId();
        PageResult<AssistanceRequest> result = assistanceService.getAssistanceRequestList(
            userId, page, size, status, keyword);
        return Result.success(result);
    }
    
    /**
     * 获取协助请求详情
     */
    @GetMapping("/detail/{id}")
    public Result<AssistanceRequest> getAssistanceRequestDetail(@PathVariable Long id) {
        AssistanceRequest request = assistanceService.getAssistanceRequestDetail(id);
        return Result.success(request);
    }
    
    /**
     * 创建协助请求
     */
    @PostMapping("/create")
    public Result<AssistanceRequest> createAssistanceRequest(@RequestBody Map<String, Object> params) {
        Long requesterId = UserContextHolder.getUserId();
        String title = (String) params.get("title");
        String content = (String) params.get("content");
        String type = (String) params.get("type");
        String taskId = (String) params.get("taskId");
        @SuppressWarnings("unchecked")
        List<Long> recipientIds = (List<Long>) params.get("recipientIds");
        
        AssistanceRequest request = assistanceService.createAssistanceRequest(
            requesterId, title, content, type, taskId, recipientIds);
        return Result.success(request);
    }
    
    /**
     * 回复协助请求
     */
    @PostMapping("/reply")
    public Result<AssistanceRequest> replyAssistanceRequest(@RequestBody Map<String, Object> params) {
        Long responderId = UserContextHolder.getUserId();
        Long requestId = Long.parseLong(params.get("requestId").toString());
        String content = (String) params.get("content");
        
        AssistanceRequest request = assistanceService.replyAssistanceRequest(
            requestId, responderId, content);
        return Result.success(request);
    }
    
    /**
     * 更新协助请求状态
     */
    @PutMapping("/status/update")
    public Result<AssistanceRequest> updateAssistanceRequestStatus(@RequestBody Map<String, Object> params) {
        Long userId = UserContextHolder.getUserId();
        Long requestId = Long.parseLong(params.get("requestId").toString());
        String status = (String) params.get("status");
        String reason = (String) params.get("reason");
        
        AssistanceRequest request = assistanceService.updateAssistanceRequestStatus(
            requestId, userId, status, reason);
        return Result.success(request);
    }
    
    /**
     * 添加接收者到协助请求
     */
    @PostMapping("/recipients/add")
    public Result<AssistanceRequest> addRecipientsToAssistanceRequest(@RequestBody Map<String, Object> params) {
        Long requestId = Long.parseLong(params.get("requestId").toString());
        @SuppressWarnings("unchecked")
        List<Long> recipientIds = (List<Long>) params.get("recipientIds");
        
        AssistanceRequest request = assistanceService.addRecipientsToAssistanceRequest(
            requestId, recipientIds);
        return Result.success(request);
    }
    
    /**
     * 标记协助请求为已读
     */
    @PostMapping("/read/{id}")
    public Result<Boolean> markAssistanceRequestRead(@PathVariable Long id) {
        Long userId = UserContextHolder.getUserId();
        boolean result = assistanceService.markAssistanceRequestRead(id, userId);
        return Result.success(result);
    }
} 