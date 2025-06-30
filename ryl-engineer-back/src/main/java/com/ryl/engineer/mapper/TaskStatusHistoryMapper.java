package com.ryl.engineer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.engineer.entity.TaskStatusHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务状态历史记录 Mapper 接口
 */
@Mapper
public interface TaskStatusHistoryMapper extends BaseMapper<TaskStatusHistory> {
} 