package com.ryl.engineer.warehouse.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * 物品使用申请请求DTO
 */
@Data
public class ItemUseRequest {

    /**
     * 物品ID
     */
    @NotNull(message = "物品ID不能为空")
    private Long itemId;

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