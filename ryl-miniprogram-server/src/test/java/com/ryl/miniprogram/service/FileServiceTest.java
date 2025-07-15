package com.ryl.miniprogram.service;

import com.ryl.miniprogram.entity.RecordFile;
import com.ryl.miniprogram.entity.TaskImage;
import com.ryl.miniprogram.mapper.RecordFileMapper;
import com.ryl.miniprogram.mapper.TaskImageMapper;
import com.ryl.miniprogram.service.impl.FileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 文件服务测试
 */
public class FileServiceTest {

    @TempDir
    Path tempDir;

    @Mock
    private RecordFileMapper recordFileMapper;
    
    @Mock
    private TaskImageMapper taskImageMapper;
    
    @Spy
    @InjectMocks
    private FileServiceImpl fileService;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(fileService, "uploadPath", tempDir.toString());
        ReflectionTestUtils.setField(fileService, "urlPrefix", "/files");
    }
    
    @Test
    public void testUploadFile() throws IOException {
        // 准备测试数据
        String content = "test file content";
        MultipartFile file = new MockMultipartFile(
                "test-file.txt", 
                "test-file.txt",
                "text/plain", 
                content.getBytes());
        
        Long relationId = 100L;
        Integer relationType = 1;
        Long uploadUserId = 200L;
        Integer uploadUserType = 1;
        
        // 模拟数据库操作
        doReturn(true).when(fileService).save(any(RecordFile.class));
        
        // 调用被测试方法
        RecordFile result = fileService.uploadFile(file, relationId, relationType, uploadUserId, uploadUserType);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(relationId, result.getRelationId());
        assertEquals(relationType, result.getRelationType());
        assertEquals("test-file.txt", result.getFileName());
        assertEquals("txt", result.getFileType());
        assertEquals(uploadUserId, result.getUploadUserId());
        assertEquals(uploadUserType, result.getUploadUserType());
        
        // 验证文件是否存在
        String filePath = result.getFilePath();
        assertNotNull(filePath);
        assertTrue(Files.exists(tempDir.resolve(filePath)));
    }
    
    @Test
    public void testUploadTaskImage() throws IOException {
        // 准备测试数据
        String content = "test image content";
        MultipartFile file = new MockMultipartFile(
                "test-image.jpg", 
                "test-image.jpg",
                "image/jpeg", 
                content.getBytes());
        
        String taskId = "100";
        Integer imageType = 1;
        Integer sort = 0;
        
        // 模拟 Mapper 方法
        doReturn(1).when(taskImageMapper).insert(any(TaskImage.class));
        
        // 调用被测试方法
        TaskImage result = fileService.uploadTaskImage(file, taskId, imageType, sort);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(taskId, result.getTaskId());
        assertEquals(imageType, result.getImageType());
        assertEquals(sort, result.getSort());
        assertTrue(result.getImageUrl().contains("/files/images/"));
        assertTrue(result.getImageUrl().endsWith(".jpg"));
    }
    
    @Test
    public void testGetFile() {
        // 准备测试数据
        Long fileId = 1L;
        RecordFile recordFile = new RecordFile();
        recordFile.setId(fileId);
        recordFile.setFileName("test-file.txt");
        recordFile.setFilePath("test/path/test-file.txt");
        
        // 模拟 Mapper 方法
        when(recordFileMapper.selectById(fileId)).thenReturn(recordFile);
        
        // 调用被测试方法
        RecordFile result = fileService.getFile(fileId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(fileId, result.getId());
        assertEquals("test-file.txt", result.getFileName());
        assertEquals("test/path/test-file.txt", result.getFilePath());
        
        // 验证 Mapper 方法调用
        verify(recordFileMapper, times(1)).selectById(fileId);
    }
    
    @Test
    public void testGetTaskImages() {
        // 准备测试数据
        String taskId = "100";
        Integer imageType = 1;
        
        TaskImage image1 = new TaskImage();
        image1.setId(1L);
        image1.setTaskId(taskId);
        image1.setImageType(imageType);
        
        TaskImage image2 = new TaskImage();
        image2.setId(2L);
        image2.setTaskId(taskId);
        image2.setImageType(imageType);
        
        List<TaskImage> images = Arrays.asList(image1, image2);
        
        // 模拟 Mapper 方法
        when(taskImageMapper.selectList(any())).thenReturn(images);
        
        // 调用被测试方法
        List<TaskImage> result = fileService.getTaskImages(taskId, imageType);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        
        // 验证 Mapper 方法调用
        verify(taskImageMapper, times(1)).selectList(any());
    }
    
    @Test
    public void testDeleteFile() throws IOException {
        // 准备测试数据
        Long fileId = 1L;
        
        // 创建临时文件
        Path tempFile = tempDir.resolve("test-file.txt");
        Files.write(tempFile, "test content".getBytes());
        
        RecordFile recordFile = new RecordFile();
        recordFile.setId(fileId);
        recordFile.setFileName("test-file.txt");
        recordFile.setFilePath("test-file.txt");
        
        // 模拟 Mapper 方法
        when(recordFileMapper.selectById(fileId)).thenReturn(recordFile);
        when(recordFileMapper.deleteById(fileId)).thenReturn(1);
        
        // 调用被测试方法
        boolean result = fileService.deleteFile(fileId);
        
        // 验证结果
        assertTrue(result);
        
        // 验证 Mapper 方法调用
        verify(recordFileMapper, times(1)).selectById(fileId);
        verify(recordFileMapper, times(1)).deleteById(fileId);
    }
    
    @Test
    public void testDeleteTaskImage() throws IOException {
        // 准备测试数据
        Long imageId = 1L;
        
        // 创建临时文件
        Path tempFile = tempDir.resolve("images/test-image.jpg");
        Files.createDirectories(tempFile.getParent());
        Files.write(tempFile, "test content".getBytes());
        
        TaskImage taskImage = new TaskImage();
        taskImage.setId(imageId);
        taskImage.setImageUrl("/files/images/test-image.jpg");
        
        // 模拟 Mapper 方法
        when(taskImageMapper.selectById(imageId)).thenReturn(taskImage);
        when(taskImageMapper.deleteById(imageId)).thenReturn(1);
        
        // 调用被测试方法
        boolean result = fileService.deleteTaskImage(imageId);
        
        // 验证结果
        assertTrue(result);
        
        // 验证 Mapper 方法调用
        verify(taskImageMapper, times(1)).selectById(imageId);
        verify(taskImageMapper, times(1)).deleteById(imageId);
    }
}