package com.ryl.engineer.mapper;

import com.ryl.engineer.entity.AnnouncementRead;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公告已读状态Mapper接口
 */
@Mapper
public interface AnnouncementReadMapper {
    
    /**
     * 根据用户ID和公告ID获取已读状态
     */
    AnnouncementRead selectByUserIdAndAnnouncementId(
        @Param("userId") Long userId, 
        @Param("announcementId") Long announcementId
    );
    
    /**
     * 获取用户已读的所有公告ID
     */
    List<Long> selectReadAnnouncementIdsByUserId(@Param("userId") Long userId);
    
    /**
     * 获取公告的所有已读用户ID
     */
    List<Long> selectReadUserIdsByAnnouncementId(@Param("announcementId") Long announcementId);
    
    /**
     * 新增已读记录
     */
    int insert(AnnouncementRead announcementRead);
    
    /**
     * 批量新增已读记录
     */
    int batchInsert(@Param("list") List<AnnouncementRead> announcementReads);
    
    /**
     * 删除已读记录
     */
    int deleteByUserIdAndAnnouncementId(
        @Param("userId") Long userId, 
        @Param("announcementId") Long announcementId
    );
    
    /**
     * 删除用户的所有已读记录
     */
    int deleteByUserId(@Param("userId") Long userId);
    
    /**
     * 删除公告的所有已读记录
     */
    int deleteByAnnouncementId(@Param("announcementId") Long announcementId);
    
    /**
     * 检查公告是否已读
     */
    boolean isAnnouncementRead(
        @Param("userId") Long userId, 
        @Param("announcementId") Long announcementId
    );
    
    /**
     * 获取用户未读公告数量
     */
    int countUnreadByUserId(@Param("userId") Long userId);
} 