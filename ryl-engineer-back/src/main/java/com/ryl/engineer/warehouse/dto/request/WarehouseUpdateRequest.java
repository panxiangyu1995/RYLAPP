package com.ryl.engineer.warehouse.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 更新仓库请求DTO
 */
@Data
public class WarehouseUpdateRequest {

    /**
     * 仓库ID
     */
    @NotNull(message = "仓库ID不能为空")
    private Long id;

    /**
     * 仓库名称
     */
    @Size(max = 50, message = "仓库名称长度不能超过50个字符")
    private String name;

    /**
     * 仓库地点
     */
    @Size(max = 100, message = "仓库地点长度不能超过100个字符")
    private String location;

    /**
     * 仓库描述
     */
    @Size(max = 255, message = "仓库描述长度不能超过255个字符")
    private String description;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;
} 