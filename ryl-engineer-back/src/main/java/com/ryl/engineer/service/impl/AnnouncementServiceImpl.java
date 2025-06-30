package com.ryl.engineer.service.impl;

import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.dto.announcement.AnnouncementDTO;
import com.ryl.engineer.entity.SystemAnnouncement;
import com.ryl.engineer.entity.AnnouncementRead;
import com.ryl.engineer.entity.User;
import com.ryl.engineer.mapper.AnnouncementMapper;
import com.ryl.engineer.mapper.AnnouncementReadMapper;
import com.ryl.engineer.mapper.UserMapper;
import com.ryl.engineer.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统公告服务实现类
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;
    
    @Autowired
    private AnnouncementReadMapper announcementReadMapper;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public PageResult<AnnouncementDTO> getAnnouncementList(Long userId, Integer page, Integer size, 
                                                        Boolean onlyUnread, String keyword) {
        // 获取公告列表
        List<SystemAnnouncement> announcements = announcementMapper.selectList(keyword);
        
        // 获取用户已读公告ID列表
        List<Long> readAnnouncementIds = announcementReadMapper.selectReadAnnouncementIdsByUserId(userId);
        
        // 如果只查询未读，则过滤掉已读公告
        if (onlyUnread != null && onlyUnread) {
            announcements = announcements.stream()
                .filter(announcement -> !readAnnouncementIds.contains(announcement.getId()))
                .collect(Collectors.toList());
        }
        
        // 总记录数
        int total = announcements.size();
        
        // 分页处理
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        
        // 边界检查
        if (fromIndex >= total) {
            return new PageResult<>(0, new ArrayList<>(), page, size);
        }
        
        // 分页后的数据
        List<SystemAnnouncement> pagedAnnouncements = announcements.subList(fromIndex, toIndex);
        
        // 转换为DTO
        List<AnnouncementDTO> result = new ArrayList<>();
        for (SystemAnnouncement announcement : pagedAnnouncements) {
            AnnouncementDTO dto = convertToDTO(announcement, readAnnouncementIds, userId);
            result.add(dto);
        }
        
        return new PageResult<>(total, result, page, size);
    }

    @Override
    public AnnouncementDTO getAnnouncementDetail(Long userId, Long announcementId) {
        // 获取公告
        SystemAnnouncement announcement = announcementMapper.selectById(announcementId);
        
        if (announcement != null) {
            // 查询公告已读状态
            boolean isRead = announcementReadMapper.isAnnouncementRead(userId, announcementId);
            
            // 获取阅读时间
            AnnouncementRead announcementRead = announcementReadMapper.selectByUserIdAndAnnouncementId(userId, announcementId);
            
            // 转换为DTO
            AnnouncementDTO dto = new AnnouncementDTO();
            dto.setId(announcement.getId());
            dto.setTitle(announcement.getTitle());
            dto.setContent(announcement.getContent());
            
            // 设置发布者信息
            AnnouncementDTO.SenderInfo senderInfo = new AnnouncementDTO.SenderInfo();
            senderInfo.setId(announcement.getSenderId());
            senderInfo.setName(announcement.getSenderName());
            dto.setSender(senderInfo);
            
            // 设置重要程度
            dto.setImportance(announcement.getImportance());
            
            // 设置状态
            dto.setStatus(announcement.getStatus());
            
            // 设置时间
            dto.setStartTime(announcement.getStartTime());
            dto.setEndTime(announcement.getEndTime());
            dto.setIsPopup(announcement.getIsPopup() == 1);
            dto.setCreateTime(announcement.getCreateTime());
            dto.setUpdateTime(announcement.getUpdateTime());
            
            // 设置已读状态
            dto.setIsRead(isRead);
            if (announcementRead != null) {
                dto.setReadTime(announcementRead.getReadTime());
            }
            
            return dto;
        }
        
        return null;
    }

    @Override
    @Transactional
    public AnnouncementDTO publishAnnouncement(String title, String content, Long publisherId, String type) {
        // 创建公告
        SystemAnnouncement announcement = new SystemAnnouncement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setImportance(type);
        announcement.setSenderId(publisherId);
        
        // 获取发布者姓名
        User publisher = userMapper.selectById(publisherId);
        announcement.setSenderName(publisher != null ? publisher.getName() : "未知用户");
        
        // 设置发布时间
        announcement.setStartTime(new Date());
        announcement.setStatus("active");
        announcement.setIsPopup(0);
        announcement.setCreateTime(new Date());
        announcement.setUpdateTime(new Date());
        
        // 保存公告
        announcementMapper.insert(announcement);
        
        // 转换为DTO
        return convertToDTO(announcement, new ArrayList<>(), publisherId);
    }

    @Override
    @Transactional
    public AnnouncementDTO updateAnnouncement(Long announcementId, String title, String content, String type) {
        // 获取公告
        SystemAnnouncement announcement = announcementMapper.selectById(announcementId);
        
        if (announcement != null) {
            // 更新公告内容
            announcement.setTitle(title);
            announcement.setContent(content);
            announcement.setImportance(type);
            announcement.setUpdateTime(new Date());
            
            // 保存更新
            announcementMapper.update(announcement);
            
            // 转换为DTO
            return convertToDTO(announcement, new ArrayList<>(), announcement.getSenderId());
        }
        
        return null;
    }

    @Override
    @Transactional
    public boolean deleteAnnouncement(Long announcementId) {
        // 删除公告
        int result = announcementMapper.deleteById(announcementId);
        
        if (result > 0) {
            // 删除公告的所有已读记录
            announcementReadMapper.deleteByAnnouncementId(announcementId);
            return true;
        }
        
        return false;
    }

    @Override
    @Transactional
    public boolean markAnnouncementRead(Long userId, Long announcementId) {
        // 检查公告是否已读
        boolean isRead = announcementReadMapper.isAnnouncementRead(userId, announcementId);
        
        if (!isRead) {
            // 创建已读记录
            AnnouncementRead read = new AnnouncementRead();
            read.setAnnouncementId(announcementId);
            read.setUserId(userId);
            read.setReadTime(new Date());
            
            // 保存已读记录
            int result = announcementReadMapper.insert(read);
            
            return result > 0;
        }
        
        return true; // 已经是已读状态
    }

    @Override
    @Transactional
    public int markAllAnnouncementsRead(Long userId) {
        // 获取所有公告
        List<SystemAnnouncement> announcements = announcementMapper.selectList(null);
        
        if (announcements == null || announcements.isEmpty()) {
            return 0;
        }
        
        // 获取用户已读公告ID列表
        List<Long> readAnnouncementIds = announcementReadMapper.selectReadAnnouncementIdsByUserId(userId);
        
        // 过滤出未读公告
        List<SystemAnnouncement> unreadAnnouncements = announcements.stream()
            .filter(announcement -> !readAnnouncementIds.contains(announcement.getId()))
            .collect(Collectors.toList());
        
        if (unreadAnnouncements.isEmpty()) {
            return 0;
        }
        
        // 批量创建已读记录
        List<AnnouncementRead> reads = new ArrayList<>();
        Date now = new Date();
        
        for (SystemAnnouncement announcement : unreadAnnouncements) {
            AnnouncementRead read = new AnnouncementRead();
            read.setAnnouncementId(announcement.getId());
            read.setUserId(userId);
            read.setReadTime(now);
            
            reads.add(read);
        }
        
        // 批量保存已读记录
        announcementReadMapper.batchInsert(reads);
        
        return unreadAnnouncements.size();
    }
    
    /**
     * 将SystemAnnouncement实体转换为AnnouncementDTO
     *
     * @param announcement 公告实体
     * @param readAnnouncementIds 已读公告ID列表
     * @param userId 当前用户ID
     * @return 公告DTO
     */
    private AnnouncementDTO convertToDTO(SystemAnnouncement announcement, List<Long> readAnnouncementIds, Long userId) {
        if (announcement == null) {
            return null;
        }
        
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setContent(announcement.getContent());
        
        // 设置发布者信息
        AnnouncementDTO.SenderInfo senderInfo = new AnnouncementDTO.SenderInfo();
        senderInfo.setId(announcement.getSenderId());
        senderInfo.setName(announcement.getSenderName());
        dto.setSender(senderInfo);
        
        // 设置重要程度
        dto.setImportance(announcement.getImportance());
        
        // 设置状态
        dto.setStatus(announcement.getStatus());
        
        // 设置时间
        dto.setStartTime(announcement.getStartTime());
        dto.setEndTime(announcement.getEndTime());
        dto.setIsPopup(announcement.getIsPopup() == 1);
        dto.setCreateTime(announcement.getCreateTime());
        dto.setUpdateTime(announcement.getUpdateTime());
        
        // 设置已读状态
        boolean isRead = readAnnouncementIds.contains(announcement.getId());
        dto.setIsRead(isRead);
        
        if (isRead) {
            // 获取阅读时间
            AnnouncementRead announcementRead = announcementReadMapper.selectByUserIdAndAnnouncementId(userId, announcement.getId());
            if (announcementRead != null) {
                dto.setReadTime(announcementRead.getReadTime());
            }
        }
        
        return dto;
    }
} 