package com.ryl.engineer.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * App版本视图对象
 * @author: RYL
 */
@Data
public class AppVersionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer versionCode;
    private String versionName;
    private String updateLog;
    private String downloadUrl;
    private Boolean isForced;
} 