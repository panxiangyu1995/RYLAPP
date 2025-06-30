package com.ryl.engineer.mapper;

import com.ryl.engineer.entity.ChatConversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 聊天会话Mapper接口
 */
@Mapper
public interface ChatConversationMapper {
    
    /**
     * 根据ID获取会话
     */
    ChatConversation selectById(String id);
    
    /**
     * 获取用户的会话列表
     */
    List<ChatConversation> selectByUserId(
        @Param("userId") Long userId,
        @Param("type") String type,
        @Param("isTaskRelated") Boolean isTaskRelated,
        @Param("keyword") String keyword
    );
    
    /**
     * 新增会话
     */
    int insert(ChatConversation conversation);
    
    /**
     * 更新会话信息
     */
    int update(ChatConversation conversation);
    
    /**
     * 删除会话
     */
    int deleteById(String id);
    
    /**
     * 根据关联任务ID查找会话
     */
    ChatConversation selectByTaskId(@Param("taskId") String taskId);
} 