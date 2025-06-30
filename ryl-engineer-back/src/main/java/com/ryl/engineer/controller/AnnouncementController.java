package com.ryl.engineer.controller;

import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.common.Result;
import com.ryl.engineer.dto.announcement.AnnouncementDTO;
import com.ryl.engineer.service.AnnouncementService;
import com.ryl.engineer.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger logger = LoggerFactory.getLogger(AnnouncementController.class);
    
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
        
        try {
            Long userId = UserContextHolder.getUserId();
            if (userId == null) {
                logger.warn("获取公告列表失败：用户未登录");
                return Result.unauthorized();
            }
            
            logger.info("获取用户 {} 的公告列表", userId);
            PageResult<AnnouncementDTO> result = announcementService.getAnnouncementList(
                userId, page, size, onlyUnread, keyword);
            return Result.success(result);
        } catch (Exception e) {
            logger.error("获取公告列表失败", e);
            return Result.error("获取公告列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取公告详情
     */
    @GetMapping("/detail/{id}")
    public Result<AnnouncementDTO> getAnnouncementDetail(@PathVariable Long id) {
        try {
            Long userId = UserContextHolder.getUserId();
            if (userId == null) {
                logger.warn("获取公告详情失败：用户未登录");
                return Result.unauthorized();
            }
            
            logger.info("获取公告详情，ID: {}", id);
            AnnouncementDTO announcement = announcementService.getAnnouncementDetail(userId, id);
            if (announcement == null) {
                return Result.error(404, "公告不存在");
            }
            return Result.success(announcement);
        } catch (Exception e) {
            logger.error("获取公告详情失败", e);
            return Result.error("获取公告详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 发布公告
     */
    @PostMapping("/publish")
    public Result<AnnouncementDTO> publishAnnouncement(@RequestBody Map<String, Object> params) {
        try {
            Long publisherId = UserContextHolder.getUserId();
            if (publisherId == null) {
                logger.warn("发布公告失败：用户未登录");
                return Result.unauthorized();
            }
            
            logger.info("用户 {} 发布公告", publisherId);
            String title = (String) params.get("title");
            String content = (String) params.get("content");
            String type = (String) params.get("type");
            
            AnnouncementDTO announcement = announcementService.publishAnnouncement(
                title, content, publisherId, type);
            return Result.success(announcement);
        } catch (Exception e) {
            logger.error("发布公告失败", e);
            return Result.error("发布公告失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新公告
     */
    @PutMapping("/update")
    public Result<AnnouncementDTO> updateAnnouncement(@RequestBody Map<String, Object> params) {
        try {
            Long userId = UserContextHolder.getUserId();
            if (userId == null) {
                logger.warn("更新公告失败：用户未登录");
                return Result.unauthorized();
            }
            
            logger.info("更新公告，ID: {}", params.get("id"));
            Long announcementId = Long.parseLong(params.get("id").toString());
            String title = (String) params.get("title");
            String content = (String) params.get("content");
            String type = (String) params.get("type");
            
            AnnouncementDTO announcement = announcementService.updateAnnouncement(
                announcementId, title, content, type);
            return Result.success(announcement);
        } catch (Exception e) {
            logger.error("更新公告失败", e);
            return Result.error("更新公告失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除公告
     */
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteAnnouncement(@PathVariable Long id) {
        try {
            Long userId = UserContextHolder.getUserId();
            if (userId == null) {
                logger.warn("删除公告失败：用户未登录");
                return Result.unauthorized();
            }
            
            logger.info("删除公告，ID: {}", id);
            boolean result = announcementService.deleteAnnouncement(id);
            return Result.success(result);
        } catch (Exception e) {
            logger.error("删除公告失败", e);
            return Result.error("删除公告失败：" + e.getMessage());
        }
    }
    
    /**
     * 标记公告已读
     */
    @PostMapping("/read/{id}")
    public Result<Boolean> markAnnouncementRead(@PathVariable Long id) {
        try {
            Long userId = UserContextHolder.getUserId();
            if (userId == null) {
                logger.warn("标记公告已读失败：用户未登录");
                return Result.unauthorized();
            }
            
            logger.info("标记公告已读，ID: {}", id);
            boolean result = announcementService.markAnnouncementRead(userId, id);
            return Result.success(result);
        } catch (Exception e) {
            logger.error("标记公告已读失败", e);
            return Result.error("标记公告已读失败：" + e.getMessage());
        }
    }
    
    /**
     * 标记所有公告已读
     */
    @PostMapping("/read/all")
    public Result<Map<String, Object>> markAllAnnouncementsRead() {
        try {
            Long userId = UserContextHolder.getUserId();
            if (userId == null) {
                logger.warn("标记所有公告已读失败：用户未登录");
                return Result.unauthorized();
            }
            
            logger.info("标记所有公告已读");
            int count = announcementService.markAllAnnouncementsRead(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("count", count);
            return Result.success(result);
        } catch (Exception e) {
            logger.error("标记所有公告已读失败", e);
            return Result.error("标记所有公告已读失败：" + e.getMessage());
        }
    }
} 