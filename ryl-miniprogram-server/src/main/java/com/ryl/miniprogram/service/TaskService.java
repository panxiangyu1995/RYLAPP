package com.ryl.miniprogram.service;

import com.ryl.miniprogram.entity.Task;
import com.ryl.miniprogram.vo.PageVO;
import com.ryl.miniprogram.vo.TaskDetailVO;

import java.util.List;

/**
 * 任务服务接口
 */
public interface TaskService {
    
    /**
     * 根据ID查询任务
     */
    Task getById(Long id);
    
    /**
     * 获取任务详情，包括所有步骤和相关媒体文件
     * @param id 任务ID
     * @return 任务详情视图对象
     */
    TaskDetailVO getTaskDetailById(Long id);
    
    /**
     * 保存任务
     */
    boolean save(Task task);
    
    /**
     * 更新任务
     */
    boolean update(Task task);
    
    /**
     * 删除任务
     */
    boolean removeById(Long id);
    
    /**
     * 根据客户ID查询任务列表
     */
    List<Task> listByCustomerId(Long customerId);
    
    /**
     * 根据状态查询任务列表
     */
    List<Task> listByStatus(String status);
    
    /**
     * 分页查询任务列表
     */
    PageVO<Task> listByPage(int pageNum, int pageSize);
    
    /**
     * 更新任务状态
     */
    boolean updateStatus(Long id, String status);
    
    /**
     * 获取当前用户当天提交的订单数量
     */
    int countDailyTasks(Long userId);
} 