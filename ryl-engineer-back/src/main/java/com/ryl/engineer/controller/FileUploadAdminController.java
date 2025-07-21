package com.ryl.engineer.controller;

import com.ryl.common.response.Result;
import com.ryl.engineer.dto.FileStorageInfo;
import com.ryl.engineer.service.FileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 后台管理 - 文件上传接口
 * @author RYL
 */
@RestController
@RequestMapping("/api/admin/files")
public class FileUploadAdminController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    public Result<FileStorageInfo> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileStorageInfo fileInfo = fileService.uploadFile(file, "apks");
            return Result.success(fileInfo);
        } catch (IOException e) {
            return Result.failed("文件上传失败: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return Result.failed(e.getMessage());
        }
    }
} 