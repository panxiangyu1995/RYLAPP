package com.ryl.engineer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.engineer.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 任务Mapper接口
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    
    /**
     * 根据工程师ID查询任务列表
     * @param engineerId 工程师ID
     * @return 任务列表
     */
    List<Task> findByEngineerId(@Param("engineerId") Long engineerId);
    
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
} 