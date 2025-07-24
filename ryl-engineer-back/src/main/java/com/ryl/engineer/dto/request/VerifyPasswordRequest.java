package com.ryl.engineer.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
 
@Data
public class VerifyPasswordRequest {
    @NotBlank(message = "密码不能为空")
    private String password;
} 