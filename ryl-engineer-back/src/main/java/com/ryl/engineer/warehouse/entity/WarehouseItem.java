package com.ryl.engineer.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 物品实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("warehouse_item")
public class WarehouseItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 物品编号
     */
    private String itemCode;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 物品名称
     */
    private String name;

    /**
     * 规格型号
     */
    private String model;

    /**
     * 厂家/品牌
     */
    private String manufacturer;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 成本
     */
    private BigDecimal cost;

    /**
     * 到货日期
     */
    private LocalDate arrivalDate;

    /**
     * 区域
     */
    private String area;

    /**
     * 描述/备注
     */
    private String description;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 