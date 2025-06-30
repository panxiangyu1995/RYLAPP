package com.ryl.engineer.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 出入库记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("stock_record")
public class StockRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 记录编号
     */
    private String recordCode;

    /**
     * 物品ID
     */
    private Long itemId;

    /**
     * 记录类型（1-入库，2-出库）
     */
    private Integer recordType;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作时间
     */
    private LocalDateTime operationTime;

    /**
     * 关联任务ID
     */
    private String taskId;

    /**
     * 描述/备注
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 