package com.ryl.engineer.controller;

import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.common.Result;
import com.ryl.engineer.dto.chat.ConversationDTO;
import com.ryl.engineer.dto.chat.MessageDTO;
import com.ryl.engineer.service.ChatService;
import com.ryl.engineer.utils.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天控制器
 */
@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    /**
     * 获取会话列表
     */
    @GetMapping("/conversation/list")
    public Result<PageResult<ConversationDTO>> getConversationList(
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "false") Boolean isTaskRelated,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "false") Boolean onlyUnread,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        Long userId = UserContextHolder.getUserId();
        PageResult<ConversationDTO> result = chatService.getConversationList(
            userId, type, isTaskRelated, keyword, onlyUnread, page, size);
        return Result.success(result);
    }
    
    /**
     * 创建聊天会话
     */
    @PostMapping("/conversation/create")
    public Result<ConversationDTO> createConversation(@RequestBody Map<String, Object> params) {
        Long userId = UserContextHolder.getUserId();
        String type = (String) params.get("type");
        String name = (String) params.get("name");
        @SuppressWarnings("unchecked")
        List<Long> memberIds = (List<Long>) params.get("memberIds");
        Boolean isTaskRelated = params.get("isTaskRelated") != null ? 
            (Boolean) params.get("isTaskRelated") : false;
        String relatedTaskId = (String) params.get("relatedTaskId");
        String avatar = (String) params.get("avatar");
        
        ConversationDTO conversationDTO = chatService.createConversation(
            userId, type, name, memberIds, isTaskRelated, relatedTaskId, avatar);
        return Result.success(conversationDTO);
    }
    
    /**
     * 获取聊天消息列表
     */
    @GetMapping("/message/list/{conversationId}")
    public Result<PageResult<MessageDTO>> getMessageList(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        Long userId = UserContextHolder.getUserId();
        PageResult<MessageDTO> result = chatService.getMessageList(
            userId, conversationId, page, size, startTime, endTime);
        return Result.success(result);
    }
    
    /**
     * 发送消息
     */
    @PostMapping("/message/send")
    public Result<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> params) {
        Long userId = UserContextHolder.getUserId();
        String conversationId = (String) params.get("conversationId");
        String content = (String) params.get("content");
        String messageType = params.get("messageType") != null ? 
            (String) params.get("messageType") : "chat";
        String contentType = params.get("contentType") != null ? 
            (String) params.get("contentType") : "text";
        String replyTo = (String) params.get("replyTo");
        
        Map<String, Object> result = chatService.sendMessage(
            userId, conversationId, content, messageType, contentType, replyTo);
        return Result.success(result);
    }
    
    /**
     * 上传消息图片
     */
    @PostMapping("/message/upload/image")
    public Result<Map<String, Object>> uploadMessageImage(@RequestParam("file") MultipartFile file) {
        // 这里应该有文件上传的处理逻辑，为简化示例，假设已保存并返回URL
        String imageUrl = "https://example.com/images/" + file.getOriginalFilename();
        String imageName = file.getOriginalFilename();
        Long imageSize = file.getSize();
        // 实际应用中应获取真实的图片宽高
        Integer width = 800; 
        Integer height = 600;
        
        Map<String, Object> result = chatService.uploadMessageImage(
            imageUrl, imageName, imageSize, width, height);
        return Result.success(result);
    }
    
    /**
     * 标记消息已读
     */
    @PostMapping("/message/read")
    public Result<Map<String, Object>> markMessageRead(@RequestBody Map<String, Object> params) {
        Long userId = UserContextHolder.getUserId();
        String conversationId = (String) params.get("conversationId");
        String messageId = (String) params.get("messageId");
        
        int unreadCount = chatService.markMessageRead(userId, conversationId, messageId);
        Map<String, Object> result = new HashMap<>();
        result.put("unreadCount", unreadCount);
        return Result.success(result);
    }
    
    /**
     * 更新会话设置
     */
    @PutMapping("/conversation/settings/update")
    public Result<Map<String, Object>> updateConversationSettings(@RequestBody Map<String, Object> params) {
        Long userId = UserContextHolder.getUserId();
        String conversationId = (String) params.get("conversationId");
        Boolean isSticky = (Boolean) params.get("isSticky");
        Boolean isMute = (Boolean) params.get("isMute");
        
        Map<String, Object> result = chatService.updateConversationSettings(
            userId, conversationId, isSticky, isMute);
        return Result.success(result);
    }
    
    /**
     * 修改群聊信息
     */
    @PutMapping("/conversation/update")
    public Result<ConversationDTO> updateConversation(@RequestBody Map<String, Object> params) {
        Long userId = UserContextHolder.getUserId();
        String conversationId = (String) params.get("conversationId");
        String name = (String) params.get("name");
        String avatar = (String) params.get("avatar");
        
        ConversationDTO conversationDTO = chatService.updateConversation(
            userId, conversationId, name, avatar);
        return Result.success(conversationDTO);
    }
    
    /**
     * 管理会话成员
     */
    @PostMapping("/conversation/members/manage")
    public Result<ConversationDTO> manageConversationMembers(@RequestBody Map<String, Object> params) {
        Long userId = UserContextHolder.getUserId();
        String conversationId = (String) params.get("conversationId");
        String action = (String) params.get("action");
        @SuppressWarnings("unchecked")
        List<Long> memberIds = (List<Long>) params.get("memberIds");
        
        ConversationDTO conversationDTO = chatService.manageConversationMembers(
            userId, conversationId, action, memberIds);
        return Result.success(conversationDTO);
    }
    
    /**
     * 退出会话
     */
    @PostMapping("/conversation/exit")
    public Result<Map<String, Object>> exitConversation(@RequestBody Map<String, Object> params) {
        Long userId = UserContextHolder.getUserId();
        String conversationId = (String) params.get("conversationId");
        
        boolean success = chatService.exitConversation(userId, conversationId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("exitTime", System.currentTimeMillis());
        return Result.success(result);
    }
} 