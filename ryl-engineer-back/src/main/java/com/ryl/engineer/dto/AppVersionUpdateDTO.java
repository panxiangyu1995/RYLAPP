package com.ryl.engineer.dto;

import lombok.Data;

/**
 * 更新App版本的DTO
 * @author: RYL
 */
@Data
public class AppVersionUpdateDTO {

    private Integer versionCode;

    private String versionName;

    private String updateLog;

    private String downloadUrl;

    private Boolean isForced;
} 