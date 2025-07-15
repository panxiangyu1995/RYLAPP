package com.ryl.engineer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.engineer.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 任务Mapper接口
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    
    /**
     * 根据工程师ID查询任务列表
     * @param page 分页参数
     * @param engineerId 工程师ID
     * @return 任务列表
     */
    List<Task> findByEngineerId(Page<Task> page, @Param("engineerId") Long engineerId);
    
    /**
     * 根据客户ID查询任务列表
     * @param customerId 客户ID
     * @return 任务列表
     */
    List<Task> findByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 根据销售ID查询任务列表
     * @param salesId 销售ID
     * @return 任务列表
     */
    List<Task> findBySalesId(@Param("salesId") Long salesId);
    
    /**
     * 获取任务状态历史
     * @param taskId 任务ID
     * @return 状态历史列表
     */
    List<Map<String, Object>> getTaskStatusHistory(@Param("taskId") String taskId);

    /**
     * 查找所有待处理且未分配工程师的任务
     * @return 任务列表
     */
    List<Task> findPendingTasks();

    /**
     * 统计指定工程师未完成的任务数量
     * @param userId 工程师ID
     * @return 未完成任务数
     */
    int countIncompleteTasksByUserId(@Param("userId") Long userId);

    /**
     * 将任务分配给工程师并更新状态
     * @param taskId 任务的物理ID (id)
     * @param engineerId 工程师ID
     * @return 影响行数
     */
    int assignTaskToEngineer(@Param("taskId") Long taskId, @Param("engineerId") Long engineerId);
} 