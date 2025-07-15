package com.ryl.miniprogram.service;


import com.ryl.miniprogram.entity.TaskEvaluation;
import com.ryl.miniprogram.mapper.TaskEvaluationMapper;
import com.ryl.miniprogram.service.impl.TaskEvaluationServiceImpl;
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
 * 任务评价服务测试
 */
public class TaskEvaluationServiceTest {
    
    @Mock
    private TaskEvaluationMapper taskEvaluationMapper;
    
    @Mock
    private TaskFlowService taskFlowService;
    
    @InjectMocks
    private TaskEvaluationServiceImpl taskEvaluationService;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    public void testAddEvaluation() {
        // 准备测试数据
        TaskEvaluation evaluation = new TaskEvaluation();
        evaluation.setId(1L);
        evaluation.setTaskId("100");
        evaluation.setCustomerId(200L);
        evaluation.setOverallRating(5);
        evaluation.setComment("服务很满意");
        evaluation.setServiceAttitude(5);
        evaluation.setServiceQuality(4);
        evaluation.setCreateTime(new Date());
        
        // 模拟 Mapper 行为
        when(taskEvaluationMapper.selectOne(any())).thenReturn(null); // 没有已存在的评价
        when(taskEvaluationMapper.insert(any(TaskEvaluation.class))).thenReturn(1);
        when(taskFlowService.updateTaskStep(anyString(), anyInt(), anyLong(), anyInt(), anyString(), anyString())).thenReturn(true);
        
        // 调用被测试方法
        boolean result = taskEvaluationService.addEvaluation(evaluation);
        
        // 验证结果
        assertTrue(result);
        
        // 验证 Mapper 方法调用
        verify(taskEvaluationMapper, times(1)).insert(any(TaskEvaluation.class));
        verify(taskFlowService, times(1)).updateTaskStep(
                eq("100"),
                eq(5),
                eq(200L),
                eq(1),
                eq("客户"),
                contains("客户评价：5分")
        );
    }
    
    @Test
    public void testAddEvaluationWithExistingEvaluation() {
        // 准备测试数据
        TaskEvaluation evaluation = new TaskEvaluation();
        evaluation.setTaskId("100");
        evaluation.setCustomerId(200L);
        evaluation.setOverallRating(5);
        
        TaskEvaluation existingEvaluation = new TaskEvaluation();
        existingEvaluation.setId(1L);
        existingEvaluation.setTaskId("100");
        
        // 模拟 Mapper 行为
        when(taskEvaluationMapper.selectOne(any())).thenReturn(existingEvaluation); // 已存在评价
        
        // 调用被测试方法
        boolean result = taskEvaluationService.addEvaluation(evaluation);
        
        // 验证结果
        assertFalse(result);
        
        // 验证 Mapper 方法调用
        verify(taskEvaluationMapper, never()).insert(any(TaskEvaluation.class));
        verify(taskFlowService, never()).updateTaskStep(anyString(), anyInt(), anyLong(), anyInt(), anyString(), anyString());
    }
    
    @Test
    public void testGetEvaluationByTaskId() {
        // 准备测试数据
        String taskId = "100";
        
        TaskEvaluation evaluation = new TaskEvaluation();
        evaluation.setId(1L);
        evaluation.setTaskId(taskId);
        evaluation.setOverallRating(5);
        
        // 模拟 Mapper 行为
        when(taskEvaluationMapper.selectOne(any())).thenReturn(evaluation);
        
        // 调用被测试方法
        TaskEvaluation result = taskEvaluationService.getEvaluationByTaskId(taskId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(taskId, result.getTaskId());
        assertEquals(5, result.getOverallRating());
        
        // 验证 Mapper 方法调用
        verify(taskEvaluationMapper, times(1)).selectOne(any());
    }
    
    @Test
    public void testGetEvaluationsByCustomerId() {
        // 准备测试数据
        Long customerId = 200L;
        
        TaskEvaluation evaluation1 = new TaskEvaluation();
        evaluation1.setId(1L);
        evaluation1.setTaskId("100");
        evaluation1.setCustomerId(customerId);
        evaluation1.setOverallRating(5);
        
        TaskEvaluation evaluation2 = new TaskEvaluation();
        evaluation2.setId(2L);
        evaluation2.setTaskId("101");
        evaluation2.setCustomerId(customerId);
        evaluation2.setOverallRating(4);
        
        List<TaskEvaluation> evaluations = Arrays.asList(evaluation1, evaluation2);
        
        // 模拟 Mapper 行为
        when(taskEvaluationMapper.selectList(any())).thenReturn(evaluations);
        
        // 调用被测试方法
        List<TaskEvaluation> result = taskEvaluationService.getEvaluationsByCustomerId(customerId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("100", result.get(0).getTaskId());
        assertEquals("101", result.get(1).getTaskId());
        
        // 验证 Mapper 方法调用
        verify(taskEvaluationMapper, times(1)).selectList(any());
    }
}