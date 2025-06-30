package com.ryl.engineer.mapper;

import com.ryl.engineer.entity.AssistanceRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 协助请求Mapper接口
 */
@Mapper
public interface AssistanceRequestMapper {
    
    /**
     * 根据ID获取请求
     */
    AssistanceRequest selectById(Long id);
    
    /**
     * 获取用户相关的协助请求列表
     */
    List<AssistanceRequest> selectByUserId(
        @Param("userId") Long userId, 
        @Param("status") String status, 
        @Param("keyword") String keyword
    );
    
    /**
     * 新增协助请求
     */
    int insert(AssistanceRequest request);
    
    /**
     * 更新协助请求
     */
    int update(AssistanceRequest request);
    
    /**
     * 更新协助请求状态
     */
    int updateStatus(
        @Param("id") Long id,
        @Param("status") String status,
        @Param("reason") String reason,
        @Param("updateTime") String updateTime
    );
    
    /**
     * 删除协助请求
     */
    int deleteById(Long id);
    
    /**
     * 根据任务ID查找相关协助请求
     */
    List<AssistanceRequest> selectByTaskId(@Param("taskId") String taskId);
} 