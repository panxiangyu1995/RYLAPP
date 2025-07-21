package com.ryl.engineer.service;

import com.ryl.engineer.dto.FileStorageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 统一文件服务接口
 */
public interface FileService {

    /**
     * 上传单个文件到指定的业务子目录
     *
     * @param file   待上传的文件
     * @param subDir 业务子目录 (例如: "avatars", "task-attachments")
     * @return 包含文件访问URL和路径的存储信息对象
     * @throws IOException 如果文件保存时发生IO错误
     */
    FileStorageInfo uploadFile(MultipartFile file, String subDir) throws IOException;

    /**
     * 根据相对路径加载文件资源
     *
     * @param relativePath 存储在数据库中的文件相对路径
     * @return Spring的Resource对象，用于文件下载
     */
    org.springframework.core.io.Resource loadFileAsResource(String relativePath);
} 