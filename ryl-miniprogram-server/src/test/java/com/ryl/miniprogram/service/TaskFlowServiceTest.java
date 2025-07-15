package com.ryl.miniprogram.service;

import com.ryl.miniprogram.entity.TaskFlow;
import com.ryl.miniprogram.entity.TaskStep;
import com.ryl.miniprogram.mapper.TaskFlowMapper;
import com.ryl.miniprogram.mapper.TaskStepMapper;
import com.ryl.miniprogram.service.impl.TaskFlowServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 任务流程服务测试
 */
public class TaskFlowServiceTest {
    
    @Mock
    private TaskFlowMapper taskFlowMapper;
    
    @Mock
    private TaskStepMapper taskStepMapper;
    
    @InjectMocks
    private TaskFlowServiceImpl taskFlowService;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    public void testCreateTaskFlow() {
        // 准备测试数据
        String taskId = "1";
        String taskType = "repair";
        
        // 模拟 Mapper 行为
        when(taskFlowMapper.insert(any(TaskFlow.class))).thenReturn(1);
        when(taskStepMapper.insert(any(TaskStep.class))).thenReturn(1);
        
        // 调用被测试方法
        TaskFlow result = taskFlowService.createTaskFlow(taskId, taskType);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(taskId, result.getTaskId());
        assertEquals(0, result.getCurrentStep());
        assertEquals(taskType, result.getTaskType());
        assertEquals(0, result.getStatus());
        
        // 验证 Mapper 方法调用
        verify(taskFlowMapper, times(1)).insert(any(TaskFlow.class));
        verify(taskStepMapper, times(1)).insert(any(TaskStep.class));
    }
    
    @Test
    public void testUpdateTaskStep() {
        // 准备测试数据
        String taskId = "1";
        Integer stepIndex = 1;
        Long operatorId = 100L;
        Integer operatorType = 1;
        String operatorName = "张三";
        String remark = "测试备注";
        
        TaskFlow taskFlow = new TaskFlow();
        taskFlow.setId(1L);
        taskFlow.setTaskId(taskId);
        taskFlow.setCurrentStep(0);
        taskFlow.setTotalSteps(5);
        taskFlow.setStatus(0);
        
        // 模拟 Mapper 行为
        when(taskFlowMapper.selectOne(any())).thenReturn(taskFlow);
        when(taskFlowMapper.updateById(any(TaskFlow.class))).thenReturn(1);
        when(taskStepMapper.insert(any(TaskStep.class))).thenReturn(1);
        
        // 调用被测试方法
        boolean result = taskFlowService.updateTaskStep(taskId, stepIndex, operatorId, operatorType, operatorName, remark);
        
        // 验证结果
        assertTrue(result);
        
        // 验证 Mapper 方法调用
        verify(taskFlowMapper, times(1)).selectOne(any());
        verify(taskFlowMapper, times(1)).updateById(any(TaskFlow.class));
        verify(taskStepMapper, times(1)).insert(any(TaskStep.class));
    }
    
    @Test
    public void testGetTaskFlowHistory() {
        // 准备测试数据
        String taskId = "1";
        
        TaskStep step1 = new TaskStep();
        step1.setId(1L);
        step1.setTaskId(taskId);
        step1.setStepIndex(0);
        step1.setDescription("创建任务");
        
        TaskStep step2 = new TaskStep();
        step2.setId(2L);
        step2.setTaskId(taskId);
        step2.setStepIndex(1);
        step2.setDescription("接收任务");
        
        List<TaskStep> steps = Arrays.asList(step1, step2);
        
        // 模拟 Mapper 行为
        when(taskStepMapper.selectList(any())).thenReturn(steps);
        
        // 调用被测试方法
        List<TaskStep> result = taskFlowService.getTaskFlowHistory(taskId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(0, result.get(0).getStepIndex());
        assertEquals(1, result.get(1).getStepIndex());
        
        // 验证 Mapper 方法调用
        verify(taskStepMapper, times(1)).selectList(any());
    }
}