package com.ryl.engineer.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
     * 获取用户相关的协助请求总数
     *
     * @param userId  用户ID
     * @param status  状态过滤
     * @param keyword 关键词搜索
     * @return 记录总数
     */
    long countByUserId(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("keyword") String keyword
    );

    /**
     * 获取用户相关的协助请求列表（手动分页）
     *
     * @param userId   用户ID
     * @param status   状态过滤
     * @param keyword  关键词搜索
     * @param offset   查询偏移量
     * @param pageSize 每页大小
     * @return 协助请求列表
     */
    List<AssistanceRequest> selectByUserId(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("keyword") String keyword,
            @Param("offset") long offset,
            @Param("pageSize") long pageSize
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