package com.ryl.miniprogram.entity;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 任务评论实体类测试
 */
public class TaskCommentTest {
    
    @Test
    public void testTaskCommentFields() {
        // 创建任务评论对象
        TaskComment comment = new TaskComment();
        comment.setId(1L);
        comment.setTaskId("100");
        comment.setContent("这是一条测试评论");
        comment.setUserId(200L);
        comment.setUserType(1);
        
        Date createTime = new Date();
        Date updateTime = new Date();
        comment.setCreateTime(createTime);
        comment.setUpdateTime(updateTime);
        
        // 验证字段值
        assertEquals(1L, comment.getId());
        assertEquals("100", comment.getTaskId());
        assertEquals("这是一条测试评论", comment.getContent());
        assertEquals(200L, comment.getUserId());
        assertEquals(1, comment.getUserType());
        assertEquals(createTime, comment.getCreateTime());
        assertEquals(updateTime, comment.getUpdateTime());
    }
}