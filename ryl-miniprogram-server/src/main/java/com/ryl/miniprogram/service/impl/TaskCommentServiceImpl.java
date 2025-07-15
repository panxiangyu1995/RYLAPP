package com.ryl.miniprogram.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryl.miniprogram.entity.TaskComment;
import com.ryl.miniprogram.mapper.TaskCommentMapper;
import com.ryl.miniprogram.service.TaskCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 任务评论服务实现类
 */
@Slf4j
@Service
public class TaskCommentServiceImpl extends ServiceImpl<TaskCommentMapper, TaskComment> implements TaskCommentService {
    
    @Autowired
    private TaskCommentMapper taskCommentMapper;
    
    @Override
    public TaskComment getById(Long id) {
        return taskCommentMapper.selectById(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(TaskComment comment) {
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        return taskCommentMapper.insert(comment) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(TaskComment comment) {
        comment.setUpdateTime(new Date());
        return taskCommentMapper.updateById(comment) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Long id) {
        return taskCommentMapper.deleteById(id) > 0;
    }
    
    @Override
    public List<TaskComment> listByTaskId(String taskId) {
        return taskCommentMapper.selectByTaskId(taskId);
    }
} 