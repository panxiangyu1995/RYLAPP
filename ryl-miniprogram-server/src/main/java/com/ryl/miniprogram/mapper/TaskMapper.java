package com.ryl.miniprogram.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.miniprogram.entity.Task;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 任务Mapper接口
 */
@Repository
public interface TaskMapper extends BaseMapper<Task> {
    
    /**
     * 根据任务ID查询任务
     */
    Task selectByTaskId(@Param("taskId") String taskId);

    /**
     * 根据客户ID查询任务列表
     */
    List<Task> selectByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 根据状态查询任务列表
     */
    List<Task> selectByStatus(@Param("status") String status);

    Task selectDetailById(@Param("id") Long id);

    /**
     * 检查是否存在重复任务
     * @param title 任务标题
     * @param description 任务描述
     * @param taskType 任务类型
     * @param customerId 客户ID
     * @return 重复任务数量
     */
    Integer countDuplicateTask(@Param("title") String title, 
                              @Param("description") String description, 
                              @Param("taskType") String taskType,
                              @Param("customerId") Long customerId);
} 