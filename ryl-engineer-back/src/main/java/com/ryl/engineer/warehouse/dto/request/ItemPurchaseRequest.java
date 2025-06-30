package com.ryl.engineer.warehouse.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * 物品采购申请请求DTO
 */
@Data
public class ItemPurchaseRequest {

    /**
     * 物品名称
     */
    @NotBlank(message = "物品名称不能为空")
    @Size(max = 100, message = "物品名称长度不能超过100个字符")
    private String itemName;

    /**
     * 规格型号
     */
    @NotBlank(message = "规格型号不能为空")
    @Size(max = 100, message = "规格型号长度不能超过100个字符")
    private String itemModel;

    /**
     * 申请数量
     */
    @NotNull(message = "申请数量不能为空")
    @Positive(message = "申请数量必须大于0")
    private Integer quantity;

    /**
     * 关联任务ID
     */
    private String taskId;

    /**
     * 用途
     */
    @NotNull(message = "用途不能为空")
    @Size(max = 255, message = "用途长度不能超过255个字符")
    private String purpose;

    /**
     * 紧急程度（1-普通，2-紧急，3-特急）
     */
    private Integer urgency = 1;
} 