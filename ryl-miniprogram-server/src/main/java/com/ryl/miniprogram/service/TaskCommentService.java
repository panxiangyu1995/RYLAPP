package com.ryl.miniprogram.service;

import com.ryl.miniprogram.entity.TaskComment;

import java.util.List;

/**
 * 任务评论服务接口
 */
public interface TaskCommentService {
    
    /**
     * 根据ID查询评论
     */
    TaskComment getById(Long id);
    
    /**
     * 保存评论
     */
    boolean save(TaskComment comment);
    
    /**
     * 更新评论
     */
    boolean update(TaskComment comment);
    
    /**
     * 删除评论
     */
    boolean removeById(Long id);
    
    /**
     * 根据任务ID查询评论列表
     */
    List<TaskComment> listByTaskId(String taskId);
} 