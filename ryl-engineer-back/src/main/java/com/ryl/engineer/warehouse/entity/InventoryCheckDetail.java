package com.ryl.engineer.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 盘库明细实体类
 */
@Data
@TableName("inventory_check_detail")
public class InventoryCheckDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 盘库ID
     */
    private Long checkId;

    /**
     * 物品ID
     */
    private Long itemId;

    /**
     * 系统数量
     */
    private Integer systemQuantity;

    /**
     * 实际数量
     */
    private Integer actualQuantity;

    /**
     * 差异数量
     */
    private Integer difference;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 