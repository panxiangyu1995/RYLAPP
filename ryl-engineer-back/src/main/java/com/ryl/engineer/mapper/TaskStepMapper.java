package com.ryl.engineer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.engineer.entity.TaskStep;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 任务步骤Mapper接口
 */
@Mapper
public interface TaskStepMapper extends BaseMapper<TaskStep> {
    
    /**
     * 根据任务ID查询任务步骤列表
     * @param taskId 任务ID
     * @return 任务步骤列表
     */
    List<TaskStep> findByTaskId(@Param("taskId") String taskId);
    
    /**
     * 根据任务ID和步骤索引查询任务步骤
     * @param taskId 任务ID
     * @param stepIndex 步骤索引
     * @return 任务步骤
     */
    TaskStep findByTaskIdAndStepIndex(@Param("taskId") String taskId, @Param("stepIndex") Integer stepIndex);
} 