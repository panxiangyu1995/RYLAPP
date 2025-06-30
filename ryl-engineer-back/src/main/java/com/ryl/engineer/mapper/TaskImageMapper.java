package com.ryl.engineer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.engineer.entity.TaskImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 任务图片Mapper接口
 */
@Mapper
public interface TaskImageMapper extends BaseMapper<TaskImage> {
    
    /**
     * 根据任务ID查询任务图片列表
     * @param taskId 任务ID
     * @return 任务图片列表
     */
    List<TaskImage> findByTaskId(@Param("taskId") String taskId);
    
    /**
     * 批量插入任务图片
     * @param taskImages 任务图片列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<TaskImage> taskImages);
} 