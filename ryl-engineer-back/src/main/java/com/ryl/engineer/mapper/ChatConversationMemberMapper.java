package com.ryl.engineer.mapper;

import com.ryl.engineer.entity.ChatConversationMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 聊天会话成员Mapper接口
 */
@Mapper
public interface ChatConversationMemberMapper {
    
    /**
     * 根据会话ID获取所有成员
     */
    List<ChatConversationMember> selectByConversationId(@Param("conversationId") String conversationId);
    
    /**
     * 根据用户ID和会话ID获取成员信息
     */
    ChatConversationMember selectByUserIdAndConversationId(
        @Param("userId") Long userId, 
        @Param("conversationId") String conversationId
    );
    
    /**
     * 获取用户的所有会话ID列表
     */
    List<String> selectConversationIdsByUserId(@Param("userId") Long userId);
    
    /**
     * 新增会话成员
     */
    int insert(ChatConversationMember member);
    
    /**
     * 批量新增会话成员
     */
    int batchInsert(@Param("list") List<ChatConversationMember> members);
    
    /**
     * 更新会话成员设置
     */
    int update(ChatConversationMember member);
    
    /**
     * 删除会话成员
     */
    int deleteByUserIdAndConversationId(
        @Param("userId") Long userId, 
        @Param("conversationId") String conversationId
    );
    
    /**
     * 批量删除会话成员
     */
    int batchDeleteByUserIdsAndConversationId(
        @Param("userIds") List<Long> userIds, 
        @Param("conversationId") String conversationId
    );
    
    /**
     * 删除会话的所有成员
     */
    int deleteByConversationId(@Param("conversationId") String conversationId);
    
    /**
     * 获取会话成员数量
     */
    int countByConversationId(@Param("conversationId") String conversationId);
} 