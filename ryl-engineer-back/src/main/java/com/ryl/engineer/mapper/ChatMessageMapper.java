package com.ryl.engineer.mapper;

import com.ryl.engineer.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 聊天消息Mapper接口
 */
@Mapper
public interface ChatMessageMapper {
    
    /**
     * 根据ID获取消息
     */
    ChatMessage selectById(String id);
    
    /**
     * 根据会话ID获取消息列表
     */
    List<ChatMessage> selectByConversationId(
        @Param("conversationId") String conversationId, 
        @Param("startTime") String startTime, 
        @Param("endTime") String endTime
    );
    
    /**
     * 获取会话的最后一条消息
     */
    ChatMessage selectLastMessageByConversationId(@Param("conversationId") String conversationId);
    
    /**
     * 新增消息
     */
    int insert(ChatMessage message);
    
    /**
     * 更新消息
     */
    int update(ChatMessage message);
    
    /**
     * 删除消息
     */
    int deleteById(String id);
    
    /**
     * 根据会话ID删除所有消息
     */
    int deleteByConversationId(@Param("conversationId") String conversationId);
    
    /**
     * 获取会话的消息数量
     */
    int countByConversationId(@Param("conversationId") String conversationId);
    
    /**
     * 获取会话在指定时间后的消息数量
     */
    int countByConversationIdAfterTime(
        @Param("conversationId") String conversationId, 
        @Param("time") String time
    );
} 