package com.ryl.miniprogram.controller;

import com.ryl.miniprogram.dto.TaskCommentDTO;
import com.ryl.miniprogram.entity.Task;
import com.ryl.miniprogram.entity.TaskComment;
import com.ryl.miniprogram.service.TaskCommentService;
import com.ryl.miniprogram.service.TaskService;
import com.ryl.miniprogram.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 任务评论相关接口控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/task/comment")
public class TaskCommentController {
    
    @Autowired
    private TaskCommentService taskCommentService;
    
    @Autowired
    private TaskService taskService;
    
    /**
     * 获取任务评论列表
     */
    @GetMapping("/list/{taskId}")
    public ResultVO<?> getCommentList(@PathVariable String taskId) {
        List<TaskComment> list = taskCommentService.listByTaskId(taskId);
        return ResultVO.success(list);
    }
    
    /**
     * 添加评论
     */
    @PostMapping("/add")
    public ResultVO<?> addComment(@RequestBody @Validated TaskCommentDTO commentDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        // 验证任务是否存在
        Task task = taskService.getById(Long.valueOf(commentDTO.getTaskId()));
        if (task == null) {
            return ResultVO.failed("任务不存在");
        }
        
        TaskComment comment = new TaskComment();
        BeanUtils.copyProperties(commentDTO, comment);
        comment.setUserId(userId);
        comment.setUserType(1); // 1表示客户
        
        boolean result = taskCommentService.save(comment);
        return result ? ResultVO.success(comment.getId(), "评论成功") : ResultVO.failed("评论失败");
    }
    
    /**
     * 删除评论
     */
    @PostMapping("/delete/{id}")
    public ResultVO<?> deleteComment(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        TaskComment comment = taskCommentService.getById(id);
        
        if (comment == null) {
            return ResultVO.failed("评论不存在");
        }
        
        // 只能删除自己的评论
        if (!comment.getUserId().equals(userId) || comment.getUserType() != 1) {
            return ResultVO.forbidden("无权删除他人评论");
        }
        
        boolean result = taskCommentService.removeById(id);
        return result ? ResultVO.success(null, "删除成功") : ResultVO.failed("删除失败");
    }
} 