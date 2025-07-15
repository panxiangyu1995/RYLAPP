package com.ryl.miniprogram.converter;

import com.ryl.miniprogram.dto.TaskDTO;
import com.ryl.miniprogram.entity.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * 任务数据转换器
 */
@Component
public class TaskConverter {
    
    /**
     * 将 DTO 转换为实体
     *
     * @param taskDTO 任务 DTO
     * @return 任务实体
     */
    public Task toEntity(TaskDTO taskDTO) {
        if (taskDTO == null) {
            return null;
        }
        
        Task task = new Task();
        BeanUtils.copyProperties(taskDTO, task);
        
        // 设置客户信息
        if (taskDTO.getCustomer() != null && taskDTO.getCustomer().getId() != null) {
            task.setCustomerId(taskDTO.getCustomer().getId());
        }
        
        return task;
    }
    
    /**
     * 将实体转换为 DTO
     *
     * @param task 任务实体
     * @return 任务 DTO
     */
    public TaskDTO toDTO(Task task) {
        if (task == null) {
            return null;
        }
        
        TaskDTO taskDTO = new TaskDTO();
        BeanUtils.copyProperties(task, taskDTO);
        
        return taskDTO;
    }
    
    /**
     * 更新实体
     *
     * @param task    要更新的任务实体
     * @param taskDTO 包含更新数据的 DTO
     * @return 更新后的任务实体
     */
    public Task updateEntity(Task task, TaskDTO taskDTO) {
        if (task == null || taskDTO == null) {
            return task;
        }
        
        // 只更新非空字段
        if (taskDTO.getTitle() != null) {
            task.setTitle(taskDTO.getTitle());
        }
        
        if (taskDTO.getDescription() != null) {
            task.setDescription(taskDTO.getDescription());
        }
        
        if (taskDTO.getTaskType() != null) {
            task.setTaskType(taskDTO.getTaskType());
        }
        
        if (taskDTO.getStatus() != null) {
            task.setStatus(taskDTO.getStatus());
        }
        
        if (taskDTO.getPriority() != null) {
            task.setPriority(taskDTO.getPriority());
        }
        
        if (taskDTO.getStartDate() != null) {
            task.setStartDate(taskDTO.getStartDate());
        }
        
        if (taskDTO.getDeadline() != null) {
            task.setDeadline(taskDTO.getDeadline());
        }
        
        // 更新客户信息
        if (taskDTO.getCustomer() != null && taskDTO.getCustomer().getId() != null) {
            task.setCustomerId(taskDTO.getCustomer().getId());
        }
        
        return task;
    }
}