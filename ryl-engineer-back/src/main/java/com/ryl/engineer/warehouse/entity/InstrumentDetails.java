package com.ryl.engineer.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 仪器详情实体类
 */
@Data
@TableName("instrument_details")
public class InstrumentDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 物品ID
     */
    private Long itemId;

    /**
     * 是否全新（1-是，0-否）
     */
    private Integer isNew;

    /**
     * 生产日期
     */
    private LocalDate productionDate;

    /**
     * 回收/购买单位（二手仪器）
     */
    private String recoveryUnit;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 