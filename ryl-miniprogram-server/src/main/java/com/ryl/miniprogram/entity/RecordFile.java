package com.ryl.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 记录文件实体类
 */
@Data
@TableName("record_file")
public class RecordFile {
    /**
     * 文件ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 记录ID
     */
    @TableField("record_id")
    private Long recordId;
    
    /**
     * 文件名
     */
    @TableField("file_name")
    private String fileName;
    
    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;
    
    /**
     * 文件大小（KB）
     */
    @TableField("file_size")
    private Long fileSize;
    
    /**
     * 文件类型
     */
    @TableField("file_type")
    private String fileType;
    
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