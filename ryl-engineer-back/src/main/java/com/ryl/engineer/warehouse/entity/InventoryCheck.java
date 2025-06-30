package com.ryl.engineer.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 盘库记录实体类
 */
@Data
@TableName("inventory_check")
public class InventoryCheck implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 盘库编号
     */
    private String checkCode;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 盘库人ID
     */
    private Long checkerId;

    /**
     * 盘库人姓名
     */
    private String checkerName;

    /**
     * 盘库时间
     */
    private LocalDateTime checkTime;

    /**
     * 状态（0-进行中，1-已完成）
     */
    private Integer status;

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