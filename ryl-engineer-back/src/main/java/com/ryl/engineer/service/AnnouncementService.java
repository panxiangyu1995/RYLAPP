package com.ryl.engineer.service;

import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.dto.announcement.AnnouncementDTO;

/**
 * 系统公告服务接口
 */
public interface AnnouncementService {
    
    /**
     * 获取公告列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @param onlyUnread 是否只显示未读
     * @param keyword 关键词搜索
     * @return 公告分页列表
     */
    PageResult<AnnouncementDTO> getAnnouncementList(Long userId, Integer page, Integer size, 
                                                   Boolean onlyUnread, String keyword);
    
    /**
     * 获取公告详情
     * @param userId 用户ID
     * @param announcementId 公告ID
     * @return 公告详情
     */
    AnnouncementDTO getAnnouncementDetail(Long userId, Long announcementId);
    
    /**
     * 发布公告
     * @param title 标题
     * @param content 内容
     * @param publisherId 发布者ID
     * @param type 公告类型
     * @return 发布的公告信息
     */
    AnnouncementDTO publishAnnouncement(String title, String content, Long publisherId, String type);
    
    /**
     * 更新公告
     * @param announcementId 公告ID
     * @param title 标题
     * @param content 内容
     * @param type 公告类型
     * @return 更新后的公告信息
     */
    AnnouncementDTO updateAnnouncement(Long announcementId, String title, String content, String type);
    
    /**
     * 删除公告
     * @param announcementId 公告ID
     * @return 是否删除成功
     */
    boolean deleteAnnouncement(Long announcementId);
    
    /**
     * 标记公告已读
     * @param userId 用户ID
     * @param announcementId 公告ID
     * @return 是否标记成功
     */
    boolean markAnnouncementRead(Long userId, Long announcementId);
    
    /**
     * 批量标记公告已读
     * @param userId 用户ID
     * @return 标记成功的公告数量
     */
    int markAllAnnouncementsRead(Long userId);
} 