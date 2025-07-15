package com.ryl.miniprogram.service;

import com.ryl.miniprogram.entity.RecordFile;
import com.ryl.miniprogram.entity.TaskAttachment;
import com.ryl.miniprogram.entity.TaskImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件服务接口
 */
public interface FileService {
    
    /**
     * 上传文件
     *
     * @param file          文件
     * @param relationId    关联ID
     * @param relationType  关联类型
     * @param uploadUserId  上传人ID
     * @param uploadUserType 上传人类型
     * @return 文件信息
     */
    RecordFile uploadFile(MultipartFile file, Long relationId, Integer relationType, Long uploadUserId, Integer uploadUserType);
    
    /**
     * 上传图片
     *
     * @param file      文件
     * @param taskId    任务ID
     * @param imageType 图片类型
     * @param sort      排序
     * @return 图片信息
     */
    TaskImage uploadTaskImage(MultipartFile file, String taskId, Integer imageType, Integer sort);
    
    /**
     * 上传任务附件
     *
     * @param file      文件
     * @param taskId    任务ID
     * @param sort      排序
     * @return 附件信息
     */
    TaskAttachment uploadTaskAttachment(MultipartFile file, String taskId, Integer sort);
    
    /**
     * 获取文件
     *
     * @param fileId 文件ID
     * @return 文件信息
     */
    RecordFile getFile(Long fileId);
    
    /**
     * 获取任务图片列表
     *
     * @param taskId    任务ID
     * @param imageType 图片类型
     * @return 图片列表
     */
    List<TaskImage> getTaskImages(String taskId, Integer imageType);
    
    /**
     * 获取任务附件列表
     *
     * @param taskId    任务ID
     * @return 附件列表
     */
    List<TaskAttachment> getTaskAttachments(String taskId);
    
    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 是否删除成功
     */
    boolean deleteFile(Long fileId);
    
    /**
     * 删除任务图片
     *
     * @param imageId 图片ID
     * @return 是否删除成功
     */
    boolean deleteTaskImage(Long imageId);
    
    /**
     * 删除任务附件
     *
     * @param attachmentId 附件ID
     * @return 是否删除成功
     */
    boolean deleteTaskAttachment(Long attachmentId);
}