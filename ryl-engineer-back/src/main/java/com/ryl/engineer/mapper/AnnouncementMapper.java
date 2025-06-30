package com.ryl.engineer.mapper;

import com.ryl.engineer.entity.SystemAnnouncement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统公告Mapper接口
 */
@Mapper
public interface AnnouncementMapper {
    
    /**
     * 根据ID获取公告
     */
    SystemAnnouncement selectById(Long id);
    
    /**
     * 获取公告列表
     */
    List<SystemAnnouncement> selectList(@Param("keyword") String keyword);
    
    /**
     * 新增公告
     */
    int insert(SystemAnnouncement announcement);
    
    /**
     * 更新公告
     */
    int update(SystemAnnouncement announcement);
    
    /**
     * 删除公告
     */
    int deleteById(Long id);
    
    /**
     * 获取最近的公告列表
     */
    List<SystemAnnouncement> selectRecentList(@Param("limit") Integer limit);
} 