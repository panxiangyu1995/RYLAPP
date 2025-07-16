package com.ryl.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 记录图片实体类
 */
@Data
@TableName("record_image")
public class RecordImage {
    /**
     * 图片ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 记录ID
     */
    @TableField("record_id")
    private Long recordId;

    /**
     * 图片URL
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 图片描述
     */
    @TableField("description")
    private String description;

    /**
     * 上传人ID
     */
    @TableField("upload_user_id")
    private Long uploadUserId;

    /**
     * 上传人类型（0：系统用户，1：客户）
     */
    @TableField("upload_user_type")
    private Integer uploadUserType;

    /**
     * 创建时间
     */
    private Date createTime;
} 