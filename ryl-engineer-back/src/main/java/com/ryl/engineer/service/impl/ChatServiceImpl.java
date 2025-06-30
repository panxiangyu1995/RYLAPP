package com.ryl.engineer.service.impl;

import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.dto.chat.ConversationDTO;
import com.ryl.engineer.dto.chat.MessageDTO;
import com.ryl.engineer.entity.ChatConversation;
import com.ryl.engineer.entity.ChatConversationMember;
import com.ryl.engineer.entity.ChatMessage;
import com.ryl.engineer.entity.ChatMessageRead;
import com.ryl.engineer.entity.User;
import com.ryl.engineer.mapper.ChatConversationMapper;
import com.ryl.engineer.mapper.ChatConversationMemberMapper;
import com.ryl.engineer.mapper.ChatMessageMapper;
import com.ryl.engineer.mapper.ChatMessageReadMapper;
import com.ryl.engineer.mapper.UserMapper;
import com.ryl.engineer.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 聊天服务实现类
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatConversationMapper conversationMapper;
    
    @Autowired
    private ChatConversationMemberMapper conversationMemberMapper;
    
    @Autowired
    private ChatMessageMapper messageMapper;
    
    @Autowired
    private ChatMessageReadMapper messageReadMapper;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public PageResult<ConversationDTO> getConversationList(Long userId, String type, Boolean isTaskRelated,
                                                      String keyword, Boolean onlyUnread,
                                                      Integer page, Integer size) {
        // 获取用户的会话列表
        List<ChatConversation> conversations = conversationMapper.selectByUserId(userId, type, isTaskRelated, keyword);
        
        // 过滤只显示有未读消息的会话
        if (onlyUnread != null && onlyUnread) {
            conversations = conversations.stream()
                .filter(conv -> {
                    ChatConversationMember member = conversationMemberMapper.selectByUserIdAndConversationId(userId, conv.getConversationId());
                    return member != null && member.getUnreadCount() > 0;
                })
                .collect(Collectors.toList());
        }
        
        // 总记录数
        int total = conversations.size();
        
        // 分页处理
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        
        // 边界检查
        if (fromIndex >= total) {
            return new PageResult<>(0, new ArrayList<>(), page, size);
        }
        
        // 分页后的数据
        List<ChatConversation> pagedConversations = conversations.subList(fromIndex, toIndex);
        
        // 转换为DTO
        List<ConversationDTO> result = new ArrayList<>();
        for (ChatConversation conversation : pagedConversations) {
            ConversationDTO dto = convertToDTO(conversation, userId);
            result.add(dto);
        }
        
        return new PageResult<>(total, result, page, size);
    }

    @Override
    @Transactional
    public ConversationDTO createConversation(Long creatorId, String type, String name, List<Long> memberIds,
                                         Boolean isTaskRelated, String relatedTaskId, String avatar) {
        // 生成会话ID
        String conversationId = "conv_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        
        // 创建会话
        ChatConversation conversation = new ChatConversation();
        conversation.setConversationId(conversationId);
        conversation.setConversationType(type);
        conversation.setName(name);
        conversation.setAvatar(avatar);
        conversation.setCreatorId(creatorId);
        conversation.setIsTaskRelated(isTaskRelated != null && isTaskRelated ? 1 : 0);
        conversation.setRelatedTaskId(relatedTaskId);
        conversation.setMemberCount(memberIds != null ? memberIds.size() + 1 : 1);
        conversation.setCreateTime(new Date());
        conversation.setUpdateTime(new Date());
        conversation.setStatus("active");
        
        // 保存会话
        conversationMapper.insert(conversation);
        
        // 添加会话成员
        List<Long> allMemberIds = new ArrayList<>();
        if (memberIds != null) {
            allMemberIds.addAll(memberIds);
        }
        
        // 确保创建者也是成员
        if (!allMemberIds.contains(creatorId)) {
            allMemberIds.add(creatorId);
        }
        
        for (Long memberId : allMemberIds) {
            ChatConversationMember member = new ChatConversationMember();
            member.setConversationId(conversationId);
            member.setUserId(memberId);
            member.setRole(memberId.equals(creatorId) ? "owner" : "member");
            member.setUnreadCount(0);
            member.setIsMute(0);
            member.setIsSticky(0);
            member.setJoinTime(new Date());
            member.setCreateTime(new Date());
            member.setUpdateTime(new Date());
            
            conversationMemberMapper.insert(member);
        }
        
        // 返回创建的会话
        return convertToDTO(conversation, creatorId);
    }

    @Override
    public PageResult<MessageDTO> getMessageList(Long userId, String conversationId,
                                            Integer page, Integer size,
                                            String startTime, String endTime) {
        // 获取会话消息
        List<ChatMessage> messages = messageMapper.selectByConversationId(conversationId, startTime, endTime);
        
        // 总记录数
        int total = messages.size();
        
        // 分页处理
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        
        // 边界检查
        if (fromIndex >= total) {
            return new PageResult<>(0, new ArrayList<>(), page, size);
        }
        
        // 分页后的数据
        List<ChatMessage> pagedMessages = messages.subList(fromIndex, toIndex);
        
        // 转换为DTO
        List<MessageDTO> result = new ArrayList<>();
        for (ChatMessage message : pagedMessages) {
            MessageDTO dto = convertToDTO(message, userId);
            result.add(dto);
        }
        
        // 标记消息为已读
        if (!pagedMessages.isEmpty()) {
            String lastMessageId = pagedMessages.get(pagedMessages.size() - 1).getMessageId();
            markMessageRead(userId, conversationId, lastMessageId);
        }
        
        return new PageResult<>(total, result, page, size);
    }

    @Override
    @Transactional
    public Map<String, Object> sendMessage(Long senderId, String conversationId, String content,
                                     String messageType, String contentType, String replyTo) {
        // 生成消息ID
        String messageId = "msg_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        
        // 创建消息
        ChatMessage message = new ChatMessage();
        message.setMessageId(messageId);
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setMessageType(messageType != null ? messageType : "chat");
        message.setContentType(contentType != null ? contentType : "text");
        message.setReplyToMessageId(replyTo);
        message.setSendTime(new Date());
        message.setStatus("sent");
        message.setIsDeleted(0);
        message.setCreateTime(new Date());
        
        // 保存消息
        messageMapper.insert(message);
        
        // 更新会话最后一条消息
        ChatConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation != null) {
            conversation.setLastMessageContent(content);
            conversation.setLastMessageTime(new Date());
            conversation.setLastMessageSenderId(senderId);
            conversation.setUpdateTime(new Date());
            conversationMapper.update(conversation);
        }
        
        // 更新会话成员未读消息数
        List<ChatConversationMember> members = conversationMemberMapper.selectByConversationId(conversationId);
        for (ChatConversationMember member : members) {
            if (!member.getUserId().equals(senderId)) {
                member.setUnreadCount(member.getUnreadCount() + 1);
                member.setUpdateTime(new Date());
                conversationMemberMapper.update(member);
            }
        }
        
        // 获取发送者信息
        User sender = userMapper.selectById(senderId);
        
        // 返回消息信息
        Map<String, Object> result = new HashMap<>();
        result.put("messageId", messageId);
        result.put("conversationId", conversationId);
        result.put("senderId", senderId);
        result.put("senderName", sender != null ? sender.getName() : "未知用户");
        result.put("content", content);
        result.put("messageType", messageType);
        result.put("contentType", contentType);
        result.put("sendTime", new Date());
        result.put("status", "sent");
        
        return result;
    }

    @Override
    public Map<String, Object> uploadMessageImage(String imageUrl, String imageName,
                                           Long imageSize, Integer width, Integer height) {
        // 返回图片信息
        Map<String, Object> result = new HashMap<>();
        result.put("url", imageUrl);
        result.put("name", imageName);
        result.put("size", imageSize);
        result.put("width", width);
        result.put("height", height);
        result.put("uploadTime", new Date());
        
        return result;
    }

    @Override
    @Transactional
    public int markMessageRead(Long userId, String conversationId, String messageId) {
        // 标记消息已读
        ChatMessageRead read = new ChatMessageRead();
        read.setMessageId(messageId);
        read.setUserId(userId);
        read.setReadTime(new Date());
        messageReadMapper.insert(read);
        
        // 获取会话成员信息
        ChatConversationMember member = conversationMemberMapper.selectByUserIdAndConversationId(userId, conversationId);
        if (member != null && member.getUnreadCount() > 0) {
            // 更新未读消息数为0
            member.setUnreadCount(0);
            member.setUpdateTime(new Date());
            conversationMemberMapper.update(member);
            return 0; // 返回更新后的未读消息数
        }
        
        return member != null ? member.getUnreadCount() : 0;
    }

    @Override
    @Transactional
    public Map<String, Object> updateConversationSettings(Long userId, String conversationId,
                                                   Boolean isSticky, Boolean isMute) {
        // 获取会话成员信息
        ChatConversationMember member = conversationMemberMapper.selectByUserIdAndConversationId(userId, conversationId);
        
        if (member != null) {
            // 更新设置
            if (isSticky != null) {
                member.setIsSticky(isSticky ? 1 : 0);
            }
            if (isMute != null) {
                member.setIsMute(isMute ? 1 : 0);
            }
            member.setUpdateTime(new Date());
            
            // 保存更新
            conversationMemberMapper.update(member);
        }
        
        // 返回更新后的设置
        Map<String, Object> result = new HashMap<>();
        result.put("conversationId", conversationId);
        result.put("userId", userId);
        result.put("isSticky", member != null && member.getIsSticky() == 1);
        result.put("isMute", member != null && member.getIsMute() == 1);
        result.put("updateTime", new Date());
        
        return result;
    }

    @Override
    @Transactional
    public ConversationDTO updateConversation(Long userId, String conversationId,
                                        String name, String avatar) {
        // 获取会话
        ChatConversation conversation = conversationMapper.selectById(conversationId);
        
        if (conversation != null) {
            // 检查是否是群主
            ChatConversationMember member = conversationMemberMapper.selectByUserIdAndConversationId(userId, conversationId);
            if (member != null && "owner".equals(member.getRole())) {
                // 更新会话信息
                if (name != null) {
                    conversation.setName(name);
                }
                if (avatar != null) {
                    conversation.setAvatar(avatar);
                }
                conversation.setUpdateTime(new Date());
                
                // 保存更新
                conversationMapper.update(conversation);
            }
        }
        
        // 返回更新后的会话
        return convertToDTO(conversation, userId);
    }

    @Override
    @Transactional
    public ConversationDTO manageConversationMembers(Long userId, String conversationId,
                                               String action, List<Long> memberIds) {
        // 获取会话
        ChatConversation conversation = conversationMapper.selectById(conversationId);
        
        if (conversation != null && memberIds != null && !memberIds.isEmpty()) {
            // 检查是否是群主
            ChatConversationMember currentMember = conversationMemberMapper.selectByUserIdAndConversationId(userId, conversationId);
            
            if (currentMember != null && "owner".equals(currentMember.getRole())) {
                if ("add".equals(action)) {
                    // 添加成员
                    for (Long memberId : memberIds) {
                        // 检查成员是否已存在
                        ChatConversationMember existingMember = conversationMemberMapper.selectByUserIdAndConversationId(memberId, conversationId);
                        if (existingMember == null) {
                            ChatConversationMember newMember = new ChatConversationMember();
                            newMember.setConversationId(conversationId);
                            newMember.setUserId(memberId);
                            newMember.setRole("member");
                            newMember.setUnreadCount(0);
                            newMember.setIsMute(0);
                            newMember.setIsSticky(0);
                            newMember.setJoinTime(new Date());
                            newMember.setCreateTime(new Date());
                            newMember.setUpdateTime(new Date());
                            
                            conversationMemberMapper.insert(newMember);
                        }
                    }
                } else if ("remove".equals(action)) {
                    // 移除成员
                    for (Long memberId : memberIds) {
                        // 检查成员是否存在
                        ChatConversationMember existingMember = conversationMemberMapper.selectByUserIdAndConversationId(memberId, conversationId);
                        if (existingMember != null && !"owner".equals(existingMember.getRole())) {
                            conversationMemberMapper.deleteByUserIdAndConversationId(memberId, conversationId);
                        }
                    }
                }
                
                // 更新会话成员数
                List<ChatConversationMember> members = conversationMemberMapper.selectByConversationId(conversationId);
                conversation.setMemberCount(members.size());
                conversation.setUpdateTime(new Date());
                
                conversationMapper.update(conversation);
            }
        }
        
        // 返回更新后的会话
        return convertToDTO(conversation, userId);
    }

    @Override
    @Transactional
    public boolean exitConversation(Long userId, String conversationId) {
        // 获取会话成员信息
        ChatConversationMember member = conversationMemberMapper.selectByUserIdAndConversationId(userId, conversationId);
        
        if (member != null) {
            // 如果是群主，不能退出
            if ("owner".equals(member.getRole())) {
                return false;
            }
            
            // 删除成员
            conversationMemberMapper.deleteByUserIdAndConversationId(userId, conversationId);
            
            // 更新会话成员数
            ChatConversation conversation = conversationMapper.selectById(conversationId);
            if (conversation != null) {
                List<ChatConversationMember> members = conversationMemberMapper.selectByConversationId(conversationId);
                conversation.setMemberCount(members.size());
                conversation.setUpdateTime(new Date());
                
                conversationMapper.update(conversation);
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * 将ChatConversation实体转换为ConversationDTO
     */
    private ConversationDTO convertToDTO(ChatConversation conversation, Long currentUserId) {
        if (conversation == null) {
            return null;
        }
        
        ConversationDTO dto = new ConversationDTO();
        dto.setConversationId(conversation.getConversationId());
        dto.setType(conversation.getConversationType());
        dto.setName(conversation.getName());
        dto.setAvatar(conversation.getAvatar());
        dto.setIsTaskRelated(conversation.getIsTaskRelated() == 1);
        dto.setRelatedTaskId(conversation.getRelatedTaskId());
        dto.setCreateTime(conversation.getCreateTime());
        dto.setMemberCount(conversation.getMemberCount());
        
        // 获取会话成员列表
        List<ChatConversationMember> members = conversationMemberMapper.selectByConversationId(conversation.getConversationId());
        List<ConversationDTO.MemberInfo> memberInfos = new ArrayList<>();
        
        // 获取当前用户的会话设置
        ChatConversationMember currentMember = null;
        
        for (ChatConversationMember member : members) {
            // 构建成员信息
            User user = userMapper.selectById(member.getUserId());
            if (user != null) {
                ConversationDTO.MemberInfo memberInfo = new ConversationDTO.MemberInfo();
                memberInfo.setId(user.getId());
                memberInfo.setName(user.getName());
                memberInfo.setAvatar(user.getAvatar());
                memberInfo.setRole(member.getRole());
                memberInfos.add(memberInfo);
            }
            
            // 记录当前用户的会话成员信息
            if (member.getUserId().equals(currentUserId)) {
                currentMember = member;
            }
        }
        
        dto.setMembers(memberInfos);
        
        // 设置当前用户的未读消息数和会话设置
        if (currentMember != null) {
            dto.setUnreadCount(currentMember.getUnreadCount());
            dto.setIsSticky(currentMember.getIsSticky() == 1);
            dto.setIsMute(currentMember.getIsMute() == 1);
        } else {
            dto.setUnreadCount(0);
            dto.setIsSticky(false);
            dto.setIsMute(false);
        }
        
        // 设置最后一条消息
        if (conversation.getLastMessageTime() != null) {
            ConversationDTO.LastMessage lastMessage = new ConversationDTO.LastMessage();
            lastMessage.setContent(conversation.getLastMessageContent());
            lastMessage.setTime(conversation.getLastMessageTime());
            lastMessage.setSenderId(conversation.getLastMessageSenderId());
            
            // 获取发送者名称
            if (conversation.getLastMessageSenderId() != null) {
                User sender = userMapper.selectById(conversation.getLastMessageSenderId());
                lastMessage.setSenderName(sender != null ? sender.getName() : "未知用户");
            }
            
            // 获取最后一条消息的类型
            ChatMessage lastChatMessage = messageMapper.selectLastMessageByConversationId(conversation.getConversationId());
            if (lastChatMessage != null) {
                lastMessage.setType(lastChatMessage.getMessageType());
            } else {
                lastMessage.setType("chat");
            }
            
            dto.setLastMessage(lastMessage);
        }
        
        return dto;
    }
    
    /**
     * 将ChatMessage实体转换为MessageDTO
     */
    private MessageDTO convertToDTO(ChatMessage message, Long currentUserId) {
        if (message == null) {
            return null;
        }
        
        MessageDTO dto = new MessageDTO();
        dto.setMessageId(message.getMessageId());
        dto.setConversationId(message.getConversationId());
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setContentType(message.getContentType());
        dto.setSendTime(message.getSendTime());
        dto.setStatus(message.getStatus());
        
        // 设置发送者信息
        User sender = userMapper.selectById(message.getSenderId());
        if (sender != null) {
            MessageDTO.SenderInfo senderInfo = new MessageDTO.SenderInfo();
            senderInfo.setId(sender.getId());
            senderInfo.setName(sender.getName());
            senderInfo.setAvatar(sender.getAvatar());
            dto.setSender(senderInfo);
        }
        
        // 检查当前用户是否已读该消息
        ChatMessageRead read = messageReadMapper.selectByUserIdAndMessageId(currentUserId, message.getMessageId());
        dto.setIsRead(read != null);
        
        // 设置回复消息信息
        if (message.getReplyToMessageId() != null) {
            ChatMessage replyMessage = messageMapper.selectById(message.getReplyToMessageId());
            if (replyMessage != null) {
                MessageDTO.ReplyInfo replyInfo = new MessageDTO.ReplyInfo();
                replyInfo.setMessageId(replyMessage.getMessageId());
                replyInfo.setContent(replyMessage.getContent());
                replyInfo.setSenderId(replyMessage.getSenderId());
                
                // 获取回复消息的发送者名称
                User replySender = userMapper.selectById(replyMessage.getSenderId());
                replyInfo.setSenderName(replySender != null ? replySender.getName() : "未知用户");
                
                dto.setReplyTo(replyInfo);
            }
        }
        
        return dto;
    }
} 