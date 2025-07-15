package com.ryl.miniprogram.controller;

import com.ryl.miniprogram.vo.ResultVO;
import com.ryl.miniprogram.dto.FileInfoDTO;
import com.ryl.miniprogram.entity.RecordFile;
import com.ryl.miniprogram.entity.TaskImage;
import com.ryl.miniprogram.entity.TaskAttachment;
import com.ryl.miniprogram.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * 文件控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/files")
public class FileController {
    
    @Autowired
    private FileService fileService;
    
    @Value("${file.upload.path:/upload}")
    private String uploadPath;
    
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
    @PostMapping("/upload")
    public ResultVO<FileInfoDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("relationId") Long relationId,
            @RequestParam("relationType") Integer relationType,
            @RequestParam("uploadUserId") Long uploadUserId,
            @RequestParam("uploadUserType") Integer uploadUserType) {
        try {
            RecordFile recordFile = fileService.uploadFile(file, relationId, relationType, uploadUserId, uploadUserType);
            FileInfoDTO fileInfoDTO = new FileInfoDTO();
            BeanUtils.copyProperties(recordFile, fileInfoDTO);
            return ResultVO.success(fileInfoDTO);
        } catch (Exception e) {
            return ResultVO.failed("文件上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 上传任务图片
     *
     * @param file      文件
     * @param taskId    任务ID
     * @param imageType 图片类型
     * @param sort      排序
     * @return 图片信息
     */
    @PostMapping("/task/image")
    public ResultVO<TaskImage> uploadTaskImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("taskId") String taskId,
            @RequestParam("imageType") Integer imageType,
            @RequestParam(value = "sort", required = false, defaultValue = "0") Integer sort) {
        
        log.info("上传任务图片，taskId: {}, imageType: {}, sort: {}, 文件大小: {}", taskId, imageType, sort, file.getSize());
        
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                log.warn("上传任务图片失败：文件为空");
                return ResultVO.failed("文件不能为空");
            }
            
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                log.warn("上传任务图片失败：不支持的文件类型 {}", contentType);
                return ResultVO.failed("只能上传图片文件");
            }
            
            // 检查文件大小
            if (file.getSize() > 5 * 1024 * 1024) { // 5MB
                log.warn("上传任务图片失败：文件过大 {}", file.getSize());
                return ResultVO.failed("图片大小不能超过5MB");
            }
            
            // 上传图片
            TaskImage taskImage = fileService.uploadTaskImage(file, taskId, imageType, sort);
            log.info("任务图片上传成功，taskId: {}, imageId: {}", taskId, taskImage.getId());
            return ResultVO.success(taskImage);
        } catch (Exception e) {
            log.error("上传任务图片失败", e);
            return ResultVO.failed("图片上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 上传任务附件
     *
     * @param file      文件
     * @param taskId    任务ID
     * @param sort      排序
     * @return 附件信息
     */
    @PostMapping("/task/attachment")
    public ResultVO<TaskAttachment> uploadTaskAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("taskId") String taskId,
            @RequestParam(value = "sort", required = false, defaultValue = "0") Integer sort) {
        
        log.info("上传任务附件，taskId: {}, sort: {}, 文件大小: {}", taskId, sort, file.getSize());
        
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                log.warn("上传任务附件失败：文件为空");
                return ResultVO.failed("文件不能为空");
            }
            
            // 检查文件类型
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase() : "";
            
            // 允许的文件类型
            String[] allowedExtensions = {"pdf", "doc", "docx", "xls", "xlsx"};
            boolean isAllowedExtension = false;
            for (String ext : allowedExtensions) {
                if (ext.equals(fileExtension)) {
                    isAllowedExtension = true;
                    break;
                }
            }
            
            if (!isAllowedExtension) {
                log.warn("上传任务附件失败：不支持的文件类型 {}", fileExtension);
                return ResultVO.failed("只支持PDF、Word和Excel格式的文件");
            }
            
            // 检查文件大小
            if (file.getSize() > 10 * 1024 * 1024) { // 10MB
                log.warn("上传任务附件失败：文件过大 {}", file.getSize());
                return ResultVO.failed("附件大小不能超过10MB");
            }
            
            // 上传附件
            TaskAttachment taskAttachment = fileService.uploadTaskAttachment(file, taskId, sort);
            log.info("任务附件上传成功，taskId: {}, attachmentId: {}", taskId, taskAttachment.getId());
            return ResultVO.success(taskAttachment);
        } catch (Exception e) {
            log.error("上传任务附件失败", e);
            return ResultVO.failed("附件上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取任务附件列表
     *
     * @param taskId 任务ID
     * @return 附件列表
     */
    @GetMapping("/task/{taskId}/attachments")
    public ResultVO<List<TaskAttachment>> getTaskAttachments(@PathVariable String taskId) {
        List<TaskAttachment> attachments = fileService.getTaskAttachments(taskId);
        return ResultVO.success(attachments);
    }
    
    /**
     * 获取文件
     *
     * @param fileId 文件ID
     * @return 文件
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> getFile(@PathVariable Long fileId) {
        RecordFile recordFile = fileService.getFile(fileId);
        if (recordFile == null) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            Path filePath = Paths.get(uploadPath).resolve(recordFile.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            
            String contentType = "application/octet-stream";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recordFile.getFileName() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 获取任务图片列表
     *
     * @param taskId    任务ID
     * @param imageType 图片类型
     * @return 图片列表
     */
    @GetMapping("/task/{taskId}/images")
    public ResultVO<List<TaskImage>> getTaskImages(
            @PathVariable String taskId,
            @RequestParam(value = "imageType", required = false) Integer imageType) {
        List<TaskImage> images = fileService.getTaskImages(taskId, imageType);
        return ResultVO.success(images);
    }
    
    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{fileId}")
    public ResultVO<Boolean> deleteFile(@PathVariable Long fileId) {
        boolean result = fileService.deleteFile(fileId);
        return ResultVO.success(result);
    }
    
    /**
     * 删除任务图片
     *
     * @param imageId 图片ID
     * @return 是否删除成功
     */
    @DeleteMapping("/task/image/{imageId}")
    public ResultVO<Boolean> deleteTaskImage(@PathVariable Long imageId) {
        boolean result = fileService.deleteTaskImage(imageId);
        return ResultVO.success(result);
    }
}