package com.ryl.engineer.dto;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 创建App版本的DTO
 * @author: RYL
 */
@Data
public class AppVersionCreateDTO {

    @NotNull(message = "版本号不能为空")
    private Integer versionCode;

    @NotEmpty(message = "版本名不能为空")
    private String versionName;

    private String updateLog;

    @NotEmpty(message = "下载链接不能为空")
    private String downloadUrl;

    @NotNull(message = "是否强制更新不能为空")
    private Boolean isForced;
} 