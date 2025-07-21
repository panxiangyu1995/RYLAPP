package com.ryl.miniprogram.service;

import com.ryl.miniprogram.entity.RecordFile;
import com.ryl.miniprogram.entity.TaskAttachment;
import com.ryl.miniprogram.entity.TaskImage;
import com.ryl.miniprogram.dto.FileDownloadResource;
import com.ryl.miniprogram.dto.FileInfoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 文件服务接口
 */
public interface FileService {
    
    /**
     * 上传文件
     *
     * @param file          文件
     * @param recordId      记录ID
     * @param uploadUserId  上传人ID
     * @param uploadUserType 上传人类型
     * @return 文件信息
     */
    RecordFile uploadFile(MultipartFile file, Long recordId, Long uploadUserId, Integer uploadUserType) throws java.io.IOException;
    
    /**
     * 上传图片
     *
     * @param file      文件
     * @param taskId    任务ID
     * @param imageType 图片类型
     * @param sort      排序
     * @return 图片信息
     */
    FileInfoDTO uploadTaskImage(MultipartFile file, String taskId, Integer imageType, Integer sort) throws IOException;
    
    /**
     * 上传任务附件
     *
     * @param file      文件
     * @param taskId    任务ID
     * @param sort      排序
     * @return 附件信息
     */
    FileInfoDTO uploadTaskAttachment(MultipartFile file, String taskId, Integer sort) throws IOException;
    
    /**
     * 获取文件
     *
     * @param fileId 文件ID
     * @return 文件
     */
    FileDownloadResource getFile(Long fileId);

    
    /**
     * 获取任务附件文件
     *
     * @param attachmentId 附件ID
     * @return 文件
     */
    FileDownloadResource getTaskAttachmentFile(Long attachmentId);
    
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