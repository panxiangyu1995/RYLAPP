package com.ryl.engineer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * APP版本信息实体类
 * @author: RYL
 */
@Data
@TableName("app_version")
public class AppVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 版本号
     */
    private Integer versionCode;

    /**
     * 版本名称
     */
    private String versionName;

    /**
     * 更新日志
     */
    private String updateLog;

    /**
     * 下载地址
     */
    private String downloadUrl;

    /**
     * 是否强制更新
     */
    private Boolean isForced;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 