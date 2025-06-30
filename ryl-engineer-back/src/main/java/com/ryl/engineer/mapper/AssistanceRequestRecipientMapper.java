package com.ryl.engineer.mapper;

import com.ryl.engineer.entity.AssistanceRequestRecipient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 协助请求接收者关系Mapper接口
 */
@Mapper
public interface AssistanceRequestRecipientMapper {
    
    /**
     * 根据请求ID获取所有接收者
     */
    List<AssistanceRequestRecipient> selectByRequestId(@Param("requestId") Long requestId);
    
    /**
     * 根据接收者ID获取协助请求
     */
    List<AssistanceRequestRecipient> selectByRecipientId(@Param("recipientId") Long recipientId);
    
    /**
     * 根据请求ID和接收者ID获取关系
     */
    AssistanceRequestRecipient selectByRequestIdAndRecipientId(
        @Param("requestId") Long requestId, 
        @Param("recipientId") Long recipientId
    );
    
    /**
     * 新增接收者关系
     */
    int insert(AssistanceRequestRecipient recipient);
    
    /**
     * 批量新增接收者关系
     */
    int batchInsert(@Param("list") List<AssistanceRequestRecipient> recipients);
    
    /**
     * 更新接收者关系
     */
    int update(AssistanceRequestRecipient recipient);
    
    /**
     * 根据请求ID删除所有接收者关系
     */
    int deleteByRequestId(@Param("requestId") Long requestId);
    
    /**
     * 根据请求ID和接收者ID删除关系
     */
    int deleteByRequestIdAndRecipientId(
        @Param("requestId") Long requestId, 
        @Param("recipientId") Long recipientId
    );
    
    /**
     * 标记请求为已读
     */
    int markAsRead(
        @Param("requestId") Long requestId, 
        @Param("recipientId") Long recipientId
    );
    
    /**
     * 获取未读请求数量
     */
    int countUnreadByRecipientId(@Param("recipientId") Long recipientId);
} 