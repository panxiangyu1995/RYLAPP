package com.ryl.miniprogram.entity;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 任务实体类测试
 */
public class TaskTest {
    
    @Test
    public void testTaskFields() {
        // 创建任务对象
        Task task = new Task();
        task.setId(1L);
        task.setTaskId("1");
        task.setTitle("设备维修");
        task.setDescription("设备需要维修");
        task.setTaskType("repair");
        task.setStatus("pending");
        task.setPriority("high");
        
        Date startDate = new Date();
        Date deadline = new Date(startDate.getTime() + 86400000); // 一天后
        task.setStartDate(startDate);
        task.setDeadline(deadline);
        
        task.setCustomerId(100L);
        task.setCreateUserId(1L);
        task.setAssigneeId(2L);
        
        Date createTime = new Date();
        Date updateTime = new Date();
        task.setCreateTime(createTime);
        task.setUpdateTime(updateTime);
        
        
        // 验证字段值
        assertEquals(1L, task.getId());
        assertEquals("1", task.getTaskId());
        assertEquals("设备维修", task.getTitle());
        assertEquals("设备需要维修", task.getDescription());
        assertEquals("repair", task.getTaskType());
        assertEquals("pending", task.getStatus());
        assertEquals("high", task.getPriority());
        assertEquals(startDate, task.getStartDate());
        assertEquals(deadline, task.getDeadline());
        assertEquals(100L, task.getCustomerId());
        assertEquals(1L, task.getCreateUserId());
        assertEquals(2L, task.getAssigneeId());
        assertEquals(createTime, task.getCreateTime());
        assertEquals(updateTime, task.getUpdateTime());

    }
}