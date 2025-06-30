package com.ryl.engineer.warehouse.dto.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 申请处理请求DTO
 */
@Data
public class RequestProcessRequest {

    /**
     * 申请ID
     */
    @NotNull(message = "申请ID不能为空")
    private Long id;

    /**
     * 处理结果（1-同意，2-拒绝）
     */
    @NotNull(message = "处理结果不能为空")
    @Min(value = 1, message = "处理结果只能为1-同意或2-拒绝")
    @Max(value = 2, message = "处理结果只能为1-同意或2-拒绝")
    private Integer status;

    /**
     * 处理意见
     */
    @Size(max = 255, message = "处理意见长度不能超过255个字符")
    private String comment;
} 