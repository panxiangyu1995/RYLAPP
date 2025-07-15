package com.ryl.miniprogram.service;


import com.ryl.miniprogram.entity.Task;
import com.ryl.miniprogram.mapper.TaskMapper;
import com.ryl.miniprogram.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TaskService单元测试
 */
public class TaskServiceTest {

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // 准备测试数据
        task = new Task();
        task.setId(1L);
        task.setTitle("测试任务");
        task.setDescription("测试任务内容");
        task.setStatus("pending");
        task.setCustomerId(1L);
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());
    }

    @Test
    public void testGetById() {
        when(taskMapper.selectById(1L)).thenReturn(task);
        
        Task result = taskService.getById(1L);
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试任务", result.getTitle());
        
        verify(taskMapper, times(1)).selectById(1L);
    }

    @Test
    public void testSave() {
        when(taskMapper.insert(any(Task.class))).thenReturn(1);
        
        Task newTask = new Task();
        newTask.setTitle("新任务");
        newTask.setDescription("新任务内容");
        newTask.setCustomerId(1L);
        
        boolean result = taskService.save(newTask);
        
        assertTrue(result);
        
        verify(taskMapper, times(1)).insert(any(Task.class));
    }

    @Test
    public void testUpdate() {
        when(taskMapper.updateById(any(Task.class))).thenReturn(1);
        
        Task updateTask = new Task();
        updateTask.setId(1L);
        updateTask.setTitle("更新任务");
        updateTask.setDescription("更新任务内容");
        
        boolean result = taskService.update(updateTask);
        
        assertTrue(result);
        
        verify(taskMapper, times(1)).updateById(any(Task.class));
    }

    @Test
    public void testRemoveById() {
        when(taskMapper.deleteById(1L)).thenReturn(1);
        
        boolean result = taskService.removeById(1L);
        
        assertTrue(result);
        
        verify(taskMapper, times(1)).deleteById(1L);
    }

    @Test
    public void testListByCustomerId() {
        List<Task> tasks = Arrays.asList(task);
        
        when(taskMapper.selectByCustomerId(1L)).thenReturn(tasks);
        
        List<Task> results = taskService.listByCustomerId(1L);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("测试任务", results.get(0).getTitle());
        
        verify(taskMapper, times(1)).selectByCustomerId(1L);
    }

    @Test
    public void testUpdateStatus() {
        // Mock getById 方法返回 task 对象
        when(taskMapper.selectById(1L)).thenReturn(task);
        when(taskMapper.updateById(any(Task.class))).thenReturn(1);
        
        boolean result = taskService.updateStatus(1L, "in-progress");
        
        assertTrue(result);
        
        verify(taskMapper, times(1)).selectById(1L);
        verify(taskMapper, times(1)).updateById(any(Task.class));
    }
} 