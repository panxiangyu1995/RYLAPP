package com.ryl.engineer.warehouse.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 删除请求DTO
 */
@Data
public class DeleteRequest {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 删除密码
     */
    @NotBlank(message = "删除密码不能为空")
    private String password;
} 