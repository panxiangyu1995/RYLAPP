package com.ryl.engineer.mapper;

import com.ryl.engineer.entity.ChatMessageRead;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 聊天消息已读状态Mapper接口
 */
@Mapper
public interface ChatMessageReadMapper {
    
    /**
     * 根据用户ID和消息ID获取已读状态
     */
    ChatMessageRead selectByUserIdAndMessageId(
        @Param("userId") Long userId, 
        @Param("messageId") String messageId
    );
    
    /**
     * 根据会话ID获取用户的已读状态列表
     */
    List<ChatMessageRead> selectByUserIdAndConversationId(
        @Param("userId") Long userId, 
        @Param("conversationId") String conversationId
    );
    
    /**
     * 获取消息的所有已读状态
     */
    List<ChatMessageRead> selectByMessageId(@Param("messageId") String messageId);
    
    /**
     * 新增已读状态
     */
    int insert(ChatMessageRead messageRead);
    
    /**
     * 批量新增已读状态
     */
    int batchInsert(@Param("list") List<ChatMessageRead> messageReads);
    
    /**
     * 更新已读状态
     */
    int update(ChatMessageRead messageRead);
    
    /**
     * 删除已读状态
     */
    int deleteByUserIdAndMessageId(
        @Param("userId") Long userId, 
        @Param("messageId") String messageId
    );
    
    /**
     * 根据会话ID删除用户的所有已读状态
     */
    int deleteByUserIdAndConversationId(
        @Param("userId") Long userId, 
        @Param("conversationId") String conversationId
    );
    
    /**
     * 根据消息ID删除所有已读状态
     */
    int deleteByMessageId(@Param("messageId") String messageId);
    
    /**
     * 获取用户在会话中的未读消息数量
     */
    int countUnreadByUserIdAndConversationId(
        @Param("userId") Long userId, 
        @Param("conversationId") String conversationId
    );
} 