package com.ryl.engineer.warehouse.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * 添加物品请求DTO
 */
@Data
public class WarehouseItemAddRequest {

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * 仓库ID
     */
    @NotNull(message = "仓库ID不能为空")
    private Long warehouseId;

    /**
     * 物品名称
     */
    @NotBlank(message = "物品名称不能为空")
    @Size(max = 100, message = "物品名称长度不能超过100个字符")
    private String name;

    /**
     * 规格型号
     */
    @NotBlank(message = "规格型号不能为空")
    @Size(max = 100, message = "规格型号长度不能超过100个字符")
    private String model;

    /**
     * 厂家/品牌
     */
    @NotBlank(message = "厂家/品牌不能为空")
    @Size(max = 100, message = "厂家/品牌长度不能超过100个字符")
    private String manufacturer;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @Positive(message = "数量必须大于0")
    private Integer quantity;

    /**
     * 成本
     */
    private BigDecimal cost;

    /**
     * 到货日期
     */
    @NotNull(message = "到货日期不能为空")
    private LocalDate arrivalDate;

    /**
     * 区域
     */
    @NotBlank(message = "区域不能为空")
    @Size(max = 50, message = "区域长度不能超过50个字符")
    private String area;

    /**
     * 描述/备注
     */
    @Size(max = 255, message = "描述/备注长度不能超过255个字符")
    private String description;

    /**
     * 详细信息（根据分类不同而不同）
     */
    private Map<String, Object> details;
} 