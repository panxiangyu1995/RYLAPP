package com.ryl.miniprogram.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.miniprogram.entity.TaskComment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 任务评论Mapper接口
 */
@Repository
public interface TaskCommentMapper extends BaseMapper<TaskComment> {
    
    /**
     * 根据任务ID查询评论列表
     */
    List<TaskComment> selectByTaskId(@Param("taskId") String taskId);
} 