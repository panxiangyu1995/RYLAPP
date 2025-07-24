package com.ryl.engineer.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
 
@Data
public class SetSecurityPasswordRequest {
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
} 