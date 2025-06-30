package com.ryl.engineer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 任务转出历史记录实体类
 */
@Data
@TableName("task_transfer_history")
public class TaskTransferHistory {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 转出工程师ID
     */
    private Long fromEngineerId;
    
    /**
     * 接收工程师ID
     */
    private Long toEngineerId;
    
    /**
     * 转出时间
     */
    private Date transferTime;
    
    /**
     * 转出备注
     */
    private String note;
    
    /**
     * 创建时间
     */
    private Date createTime;
} 