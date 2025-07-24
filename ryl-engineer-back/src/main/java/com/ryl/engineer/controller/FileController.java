package com.ryl.engineer.controller;

import com.ryl.engineer.common.Result;
import com.ryl.engineer.entity.TaskAttachment;
import com.ryl.engineer.entity.TaskStepAttachment;
import com.ryl.engineer.mapper.TaskAttachmentMapper;
import com.ryl.engineer.mapper.TaskStepAttachmentMapper;
import com.ryl.engineer.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class FileController {

    private final FileService fileService;
    private final TaskStepAttachmentMapper attachmentMapper;
    private final TaskAttachmentMapper taskAttachmentMapper;


    public FileController(FileService fileService, TaskStepAttachmentMapper attachmentMapper, TaskAttachmentMapper taskAttachmentMapper) {
        this.fileService = fileService;
        this.attachmentMapper = attachmentMapper;
        this.taskAttachmentMapper = taskAttachmentMapper;
    }

    @GetMapping("/step-attachments/{attachmentId}/download")
    public ResponseEntity<Resource> downloadTaskStepAttachment(@PathVariable Long attachmentId, HttpServletRequest request) {
        // 1. 从数据库查找附件信息
        TaskStepAttachment attachment = attachmentMapper.selectById(attachmentId);
        if (attachment == null) {
            log.warn("尝试下载不存在的步骤附件, ID: {}", attachmentId);
            return ResponseEntity.notFound().build();
        }

        // 2. 加载文件资源
        Resource resource = fileService.loadFileAsResource(attachment.getFileUrl());

        // 3. 确定文件MIME类型
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("无法确定文件类型, resource: {}", resource.getFilename());
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // 4. 构建响应
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(attachment.getFileName(), StandardCharsets.UTF_8) + "\"")
                .body(resource);
    }

    @GetMapping("/task-attachments/{attachmentId}/download")
    public ResponseEntity<Resource> downloadTaskAttachment(@PathVariable Long attachmentId, HttpServletRequest request) {
        // 1. 从数据库查找附件信息
        TaskAttachment attachment = taskAttachmentMapper.selectById(attachmentId);
        if (attachment == null) {
            log.warn("尝试下载不存在的任务附件, ID: {}", attachmentId);
            return ResponseEntity.notFound().build();
        }

        // 2. 加载文件资源
        Resource resource = fileService.loadFileAsResource(attachment.getFileUrl());

        // 3. 确定文件MIME类型
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("无法确定文件类型, resource: {}", resource.getFilename());
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // 4. 构建响应
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(attachment.getFileName(), StandardCharsets.UTF_8) + "\"")
                .body(resource);
    }
} 