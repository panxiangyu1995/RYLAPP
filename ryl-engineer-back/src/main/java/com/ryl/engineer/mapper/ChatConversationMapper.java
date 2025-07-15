package com.ryl.engineer.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    IPage<ChatConversation> selectByUserId(
        Page<ChatConversation> page,
        @Param("userId") Long userId,
        @Param("type") String type,
        @Param("isTaskRelated") Boolean isTaskRelated,
        @Param("keyword") String keyword,
        @Param("onlyUnread") Boolean onlyUnread
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

    /**
     * 根据两个成员ID查找他们之间的私聊会话
     */
    ChatConversation findPrivateConversationByMemberIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
} 