package com.ryl.engineer.warehouse.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 添加仓库请求DTO
 */
@Data
public class WarehouseAddRequest {

    /**
     * 仓库编码
     */
    @NotBlank(message = "仓库编码不能为空")
    @Size(max = 20, message = "仓库编码长度不能超过20个字符")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "仓库编码只能包含字母和数字")
    private String code;

    /**
     * 仓库名称
     */
    @NotBlank(message = "仓库名称不能为空")
    @Size(max = 50, message = "仓库名称长度不能超过50个字符")
    private String name;

    /**
     * 仓库地点
     */
    @NotBlank(message = "仓库地点不能为空")
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
    private Integer status = 1;
} 