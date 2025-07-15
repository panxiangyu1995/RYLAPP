package com.ryl.miniprogram.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryl.miniprogram.entity.RecordFile;
import com.ryl.miniprogram.entity.TaskImage;
import com.ryl.miniprogram.service.FileService;
import com.ryl.miniprogram.vo.ResultVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 文件控制器测试
 */
public class FileControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private FileService fileService;
    
    @InjectMocks
    private FileController fileController;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();
    }
    
    @Test
    public void testUploadFile() throws Exception {
        // 准备测试数据
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "test-file.txt",
                MediaType.TEXT_PLAIN_VALUE, 
                "test file content".getBytes());
        
        RecordFile recordFile = new RecordFile();
        recordFile.setId(1L);
        recordFile.setFileName("test-file.txt");
        recordFile.setFilePath("2023/01/01/test-file.txt");
        recordFile.setFileSize(10L);
        recordFile.setFileType("txt");
        recordFile.setCreateTime(new Date());
        
        // 模拟服务方法
        when(fileService.uploadFile(any(MultipartFile.class), anyLong(), anyInt(), anyLong(), anyInt()))
                .thenReturn(recordFile);
        
        // 执行请求并验证结果
        mockMvc.perform(multipart("/files/upload")
                .file(file)
                .param("relationId", "100")
                .param("relationType", "1")
                .param("uploadUserId", "200")
                .param("uploadUserType", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.fileName").value("test-file.txt"));
    }
    
    @Test
    public void testUploadTaskImage() throws Exception {
        // 准备测试数据
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE, 
                "test image content".getBytes());
        
        TaskImage taskImage = new TaskImage();
        taskImage.setId(1L);
        taskImage.setTaskId("100");
        taskImage.setImageType(1);
        taskImage.setImageUrl("/files/images/2023/01/01/test-image.jpg");
        taskImage.setSort(0);
        taskImage.setCreateTime(new Date());
        taskImage.setUpdateTime(new Date());
        
        // 模拟服务方法
        when(fileService.uploadTaskImage(any(MultipartFile.class), anyString(), anyInt(), anyInt()))
                .thenReturn(taskImage);
        
        // 执行请求并验证结果
        mockMvc.perform(multipart("/files/task/image")
                .file(file)
                .param("taskId", "100")
                .param("imageType", "1")
                .param("sort", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.imageUrl").value("/files/images/2023/01/01/test-image.jpg"));
    }
    
    @Test
    public void testGetFile() throws Exception {
        // 准备测试数据
        Long fileId = 1L;
        
        RecordFile recordFile = new RecordFile();
        recordFile.setId(fileId);
        recordFile.setFileName("test-file.txt");
        recordFile.setFilePath("test-file.txt");
        
        // 创建临时测试文件
        File tempFile = File.createTempFile("test", ".txt");
        Files.write(tempFile.toPath(), "test file content".getBytes());
        
        // 模拟服务方法
        when(fileService.getFile(fileId)).thenReturn(recordFile);
        
        // 执行请求并验证结果
        mockMvc.perform(get("/files/{fileId}", fileId))
                .andExpect(status().isOk());
                
        tempFile.delete();
    }
    
    @Test
    public void testGetTaskImages() throws Exception {
        // 准备测试数据
        String taskId = "100";
        Integer imageType = 1;
        
        TaskImage image1 = new TaskImage();
        image1.setId(1L);
        image1.setTaskId(taskId);
        image1.setImageType(imageType);
        image1.setImageUrl("/files/images/image1.jpg");
        
        TaskImage image2 = new TaskImage();
        image2.setId(2L);
        image2.setTaskId(taskId);
        image2.setImageType(imageType);
        image2.setImageUrl("/files/images/image2.jpg");
        
        List<TaskImage> images = Arrays.asList(image1, image2);
        
        // 模拟服务方法
        when(fileService.getTaskImages(taskId, imageType)).thenReturn(images);
        
        // 执行请求并验证结果
        mockMvc.perform(get("/files/task/{taskId}/images", taskId)
                .param("imageType", String.valueOf(imageType)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].imageUrl").value("/files/images/image1.jpg"))
                .andExpect(jsonPath("$.data[1].imageUrl").value("/files/images/image2.jpg"));
    }
    
    @Test
    public void testDeleteFile() throws Exception {
        // 准备测试数据
        Long fileId = 1L;
        
        // 模拟服务方法
        when(fileService.deleteFile(fileId)).thenReturn(true);
        
        // 执行请求并验证结果
        mockMvc.perform(delete("/files/{fileId}", fileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
    
    @Test
    public void testDeleteTaskImage() throws Exception {
        // 准备测试数据
        Long imageId = 1L;
        
        // 模拟服务方法
        when(fileService.deleteTaskImage(imageId)).thenReturn(true);
        
        // 执行请求并验证结果
        mockMvc.perform(delete("/files/task/image/{imageId}", imageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
}