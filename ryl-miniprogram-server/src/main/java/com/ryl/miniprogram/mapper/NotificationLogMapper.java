package com.ryl.miniprogram.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.miniprogram.entity.NotificationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NotificationLogMapper extends BaseMapper<NotificationLog> {
    NotificationLog selectByTaskIdAndTemplateId(@Param("taskId") String taskId, @Param("templateId") String templateId);
} 