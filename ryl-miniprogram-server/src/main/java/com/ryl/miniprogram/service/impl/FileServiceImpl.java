package com.ryl.miniprogram.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryl.miniprogram.entity.RecordFile;
import com.ryl.miniprogram.entity.TaskAttachment;
import com.ryl.miniprogram.entity.TaskImage;
import com.ryl.miniprogram.mapper.RecordFileMapper;
import com.ryl.miniprogram.mapper.TaskAttachmentMapper;
import com.ryl.miniprogram.mapper.TaskImageMapper;
import com.ryl.miniprogram.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 文件服务实现类
 */
@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<RecordFileMapper, RecordFile> implements FileService {
    
    @Autowired
    private TaskImageMapper taskImageMapper;
    
    @Autowired
    private TaskAttachmentMapper taskAttachmentMapper;
    
    @Value("${file.upload.path:/upload}")
    private String uploadPath;
    
    @Value("${file.upload.url-prefix:/files}")
    private String urlPrefix;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecordFile uploadFile(MultipartFile file, Long relationId, Integer relationType, Long uploadUserId, Integer uploadUserType) {
        // 检查参数
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        try {
            // 创建上传目录
            String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            String dirPath = uploadPath + "/" + datePath;
            Path directory = Paths.get(dirPath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String fileType = getFileType(originalFilename);
            String newFilename = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileType;
            String filePath = dirPath + "/" + newFilename;
            
            // 保存文件
            File destFile = new File(filePath);
            file.transferTo(destFile);
            
            // 保存文件记录
            RecordFile recordFile = new RecordFile();
            recordFile.setRelationId(relationId);
            recordFile.setRelationType(relationType);
            recordFile.setFileName(originalFilename);
            recordFile.setFilePath(datePath + "/" + newFilename);
            recordFile.setFileSize(file.getSize() / 1024); // 转换为KB
            recordFile.setFileType(fileType);
            recordFile.setUploadUserId(uploadUserId);
            recordFile.setUploadUserType(uploadUserType);
            recordFile.setCreateTime(new Date());
            
            this.save(recordFile);
            
            return recordFile;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskImage uploadTaskImage(MultipartFile file, String taskId, Integer imageType, Integer sort) {
        log.info("开始上传任务图片，taskId: {}, imageType: {}, sort: {}", taskId, imageType, sort);
        
        // 检查参数
        if (file == null || file.isEmpty()) {
            log.warn("上传任务图片失败：文件为空");
            throw new IllegalArgumentException("图片不能为空");
        }
        
        if (taskId == null || taskId.trim().isEmpty()) {
            log.warn("上传任务图片失败：任务ID为空");
            throw new IllegalArgumentException("任务ID不能为空");
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            log.warn("上传任务图片失败：不支持的文件类型 {}", contentType);
            throw new IllegalArgumentException("只能上传图片文件");
        }
        
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        log.info("上传任务图片文件名: {}, 类型: {}, 大小: {}", originalFilename, contentType, file.getSize());
        
        String fileType = getFileType(originalFilename);
        if (!isImageFile(fileType)) {
            log.warn("上传任务图片失败：不支持的图片格式 {}", fileType);
            throw new IllegalArgumentException("不支持的图片格式: " + fileType);
        }
        
        try {
            // 创建上传目录
            String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            String dirPath = uploadPath + "/images/" + datePath;
            Path directory = Paths.get(dirPath);
            if (!Files.exists(directory)) {
                log.info("创建上传目录: {}", dirPath);
                Files.createDirectories(directory);
            }
            
            // 生成文件名
            String newFilename = "task_" + taskId + "_" + UUID.randomUUID().toString().replaceAll("-", "") + "." + fileType;
            Path targetPath = directory.resolve(newFilename);
            
            // 保存文件 - 使用Files.copy替代file.transferTo以获得更好的错误处理
            log.info("保存文件到: {}", targetPath);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // 构建URL
            String imageUrl;
            if (urlPrefix.endsWith("/")) {
                imageUrl = urlPrefix + "images/" + datePath + "/" + newFilename;
            } else {
                imageUrl = urlPrefix + "/images/" + datePath + "/" + newFilename;
            }
            
            // 保存图片记录
            TaskImage taskImage = new TaskImage();
            taskImage.setTaskId(taskId);
            taskImage.setImageUrl(imageUrl);
            taskImage.setImageType(imageType);
            taskImage.setSort(sort);
            Date now = new Date();
            taskImage.setCreateTime(now);
            taskImage.setUpdateTime(now);
            
            log.info("保存任务图片记录到数据库");
            taskImageMapper.insert(taskImage);
            log.info("任务图片上传成功，ID: {}, URL: {}", taskImage.getId(), imageUrl);
            
            return taskImage;
        } catch (IOException e) {
            log.error("任务图片上传失败", e);
            throw new RuntimeException("图片上传失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskAttachment uploadTaskAttachment(MultipartFile file, String taskId, Integer sort) {
        log.info("开始上传任务附件，taskId: {}, sort: {}", taskId, sort);
        
        // 检查参数
        if (file == null || file.isEmpty()) {
            log.warn("上传任务附件失败：文件为空");
            throw new IllegalArgumentException("附件不能为空");
        }
        
        if (taskId == null || taskId.trim().isEmpty()) {
            log.warn("上传任务附件失败：任务ID为空");
            throw new IllegalArgumentException("任务ID不能为空");
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileType = getFileType(originalFilename);
        
        log.info("上传任务附件文件名: {}, 类型: {}, 大小: {}", originalFilename, contentType, file.getSize());
        
        // 验证文件类型
        if (!isAllowedFileType(fileType)) {
            log.warn("上传任务附件失败：不支持的文件格式 {}", fileType);
            throw new IllegalArgumentException("不支持的文件格式: " + fileType);
        }
        
        try {
            // 创建上传目录
            String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            String dirPath = uploadPath + "/attachments/" + datePath;
            Path directory = Paths.get(dirPath);
            if (!Files.exists(directory)) {
                log.info("创建上传目录: {}", dirPath);
                Files.createDirectories(directory);
            }
            
            // 生成文件名
            String newFilename = "task_" + taskId + "_" + UUID.randomUUID().toString().replaceAll("-", "") + "." + fileType;
            Path targetPath = directory.resolve(newFilename);
            
            // 保存文件
            log.info("保存文件到: {}", targetPath);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // 构建URL
            String fileUrl;
            if (urlPrefix.endsWith("/")) {
                fileUrl = urlPrefix + "attachments/" + datePath + "/" + newFilename;
            } else {
                fileUrl = urlPrefix + "/attachments/" + datePath + "/" + newFilename;
            }
            
            // 保存附件记录
            TaskAttachment taskAttachment = new TaskAttachment();
            taskAttachment.setTaskId(taskId);
            taskAttachment.setFileName(originalFilename);
            taskAttachment.setFileUrl(fileUrl);
            taskAttachment.setFileType(fileType);
            taskAttachment.setFileSize(file.getSize());
            taskAttachment.setSort(sort);
            Date now = new Date();
            taskAttachment.setCreateTime(now);
            taskAttachment.setUpdateTime(now);
            
            log.info("保存任务附件记录到数据库");
            taskAttachmentMapper.insert(taskAttachment);
            log.info("任务附件上传成功，ID: {}, URL: {}", taskAttachment.getId(), fileUrl);
            
            return taskAttachment;
        } catch (IOException e) {
            log.error("任务附件上传失败", e);
            throw new RuntimeException("附件上传失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public RecordFile getFile(Long fileId) {
        return this.getById(fileId);
    }
    
    @Override
    public List<TaskImage> getTaskImages(String taskId, Integer imageType) {
        LambdaQueryWrapper<TaskImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskImage::getTaskId, taskId);
        if (imageType != null) {
            queryWrapper.eq(TaskImage::getImageType, imageType);
        }
        queryWrapper.orderByAsc(TaskImage::getSort);
        
        return taskImageMapper.selectList(queryWrapper);
    }

    @Override
    public List<TaskAttachment> getTaskAttachments(String taskId) {
        LambdaQueryWrapper<TaskAttachment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskAttachment::getTaskId, taskId);
        queryWrapper.orderByAsc(TaskAttachment::getSort);
        
        return taskAttachmentMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFile(Long fileId) {
        RecordFile recordFile = this.getById(fileId);
        if (recordFile == null) {
            return false;
        }
        
        // 删除物理文件
        String filePath = uploadPath + "/" + recordFile.getFilePath();
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        
        // 删除记录
        return this.removeById(fileId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTaskImage(Long imageId) {
        TaskImage taskImage = taskImageMapper.selectById(imageId);
        if (taskImage == null) {
            return false;
        }
        
        // 从URL中提取文件路径
        String imagePath = taskImage.getImageUrl().replace(urlPrefix, uploadPath);
        File file = new File(imagePath);
        if (file.exists()) {
            file.delete();
        }
        
        // 删除记录
        return taskImageMapper.deleteById(imageId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTaskAttachment(Long attachmentId) {
        TaskAttachment taskAttachment = taskAttachmentMapper.selectById(attachmentId);
        if (taskAttachment == null) {
            return false;
        }
        
        // 从URL中提取文件路径
        String filePath = taskAttachment.getFileUrl().replace(urlPrefix, uploadPath);
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        
        // 删除记录
        return taskAttachmentMapper.deleteById(attachmentId) > 0;
    }
    
    /**
     * 获取文件类型
     *
     * @param filename 文件名
     * @return 文件类型
     */
    private String getFileType(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
    
    /**
     * 判断是否为图片文件
     *
     * @param fileType 文件类型
     * @return 是否为图片
     */
    private boolean isImageFile(String fileType) {
        return "jpg".equals(fileType) || "jpeg".equals(fileType) || "png".equals(fileType) || "gif".equals(fileType) || "bmp".equals(fileType);
    }

    /**
     * 判断是否为允许的文件类型
     *
     * @param fileType 文件类型
     * @return 是否为允许的文件类型
     */
    private boolean isAllowedFileType(String fileType) {
        // 允许的图片类型
        if (isImageFile(fileType)) {
            return true;
        }
        // 允许的文档类型
        return "doc".equals(fileType) || "docx".equals(fileType) || "pdf".equals(fileType) || "txt".equals(fileType);
    }
}