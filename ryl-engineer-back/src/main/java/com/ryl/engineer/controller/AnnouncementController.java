package com.ryl.engineer.controller;

import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.common.Result;
import com.ryl.engineer.dto.announcement.AnnouncementDTO;
import com.ryl.engineer.service.AnnouncementService;
import com.ryl.engineer.utils.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统公告控制器
 */
@RestController
@RequestMapping("/api/v1/announcement")
public class AnnouncementController {
    
    @Autowired
    private AnnouncementService announcementService;
    
    /**
     * 获取公告列表
     */
    @GetMapping("/list")
    public Result<PageResult<AnnouncementDTO>> getAnnouncementList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "false") Boolean onlyUnread,
            @RequestParam(required = false) String keyword) {
        
        Long userId = UserContextHolder.getUserId();
        PageResult<AnnouncementDTO> result = announcementService.getAnnouncementList(
            userId, page, size, onlyUnread, keyword);
        return Result.success(result);
    }
    
    /**
     * 获取公告详情
     */
    @GetMapping("/detail/{id}")
    public Result<AnnouncementDTO> getAnnouncementDetail(@PathVariable Long id) {
        Long userId = UserContextHolder.getUserId();
        AnnouncementDTO announcement = announcementService.getAnnouncementDetail(userId, id);
        return Result.success(announcement);
    }
    
    /**
     * 发布公告
     */
    @PostMapping("/publish")
    public Result<AnnouncementDTO> publishAnnouncement(@RequestBody Map<String, Object> params) {
        Long publisherId = UserContextHolder.getUserId();
        String title = (String) params.get("title");
        String content = (String) params.get("content");
        String type = (String) params.get("type");
        
        AnnouncementDTO announcement = announcementService.publishAnnouncement(
            title, content, publisherId, type);
        return Result.success(announcement);
    }
    
    /**
     * 更新公告
     */
    @PutMapping("/update")
    public Result<AnnouncementDTO> updateAnnouncement(@RequestBody Map<String, Object> params) {
        Long announcementId = Long.parseLong(params.get("id").toString());
        String title = (String) params.get("title");
        String content = (String) params.get("content");
        String type = (String) params.get("type");
        
        AnnouncementDTO announcement = announcementService.updateAnnouncement(
            announcementId, title, content, type);
        return Result.success(announcement);
    }
    
    /**
     * 删除公告
     */
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteAnnouncement(@PathVariable Long id) {
        boolean result = announcementService.deleteAnnouncement(id);
        return Result.success(result);
    }
    
    /**
     * 标记公告已读
     */
    @PostMapping("/read/{id}")
    public Result<Boolean> markAnnouncementRead(@PathVariable Long id) {
        Long userId = UserContextHolder.getUserId();
        boolean result = announcementService.markAnnouncementRead(userId, id);
        return Result.success(result);
    }
    
    /**
     * 标记所有公告已读
     */
    @PostMapping("/read/all")
    public Result<Map<String, Object>> markAllAnnouncementsRead() {
        Long userId = UserContextHolder.getUserId();
        int count = announcementService.markAllAnnouncementsRead(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", count);
        return Result.success(result);
    }
} 