package com.ryl.engineer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.engineer.entity.TaskEngineer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 任务工程师关联Mapper接口
 */
@Mapper
public interface TaskEngineerMapper extends BaseMapper<TaskEngineer> {
    
    /**
     * 根据任务ID查询任务工程师关联列表
     * @param taskId 任务ID
     * @return 任务工程师关联列表
     */
    List<TaskEngineer> findByTaskId(@Param("taskId") String taskId);
    
    /**
     * 根据工程师ID查询任务工程师关联列表
     * @param engineerId 工程师ID
     * @return 任务工程师关联列表
     */
    List<TaskEngineer> findByEngineerId(@Param("engineerId") Long engineerId);
    
    /**
     * 批量插入任务工程师关联
     * @param taskEngineers 任务工程师关联列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<TaskEngineer> taskEngineers);
}