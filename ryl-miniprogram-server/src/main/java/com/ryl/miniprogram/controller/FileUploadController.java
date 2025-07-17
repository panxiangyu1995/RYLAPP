package com.ryl.miniprogram.controller;

import com.ryl.miniprogram.service.FileStorageService;
import com.ryl.miniprogram.service.CustomerService;
import com.ryl.miniprogram.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private CustomerService customerService;
    
    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public ResultVO<?> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("用户上传头像，userId: {}, 文件大小: {}", userId, file.getSize());
        
        try {
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResultVO.failed("只能上传图片文件");
            }
            
            // 检查文件大小
            if (file.getSize() > 5 * 1024 * 1024) { // 5MB
                return ResultVO.failed("图片大小不能超过5MB");
            }
            
            // 上传文件
            String fileUrl = fileStorageService.storeAvatar(file, userId);
            
            // 更新用户头像并获取最新用户信息
            Object updatedCustomer = customerService.updateAvatar(userId, fileUrl);
            
            // 返回更新后的用户信息
            return ResultVO.success(updatedCustomer);
        } catch (Exception e) {
            log.error("上传头像失败", e);
            return ResultVO.failed("上传头像失败: " + e.getMessage());
        }
    }
} 