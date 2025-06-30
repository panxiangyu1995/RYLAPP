package com.ryl.engineer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.engineer.entity.TaskTransferHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务转出历史记录Mapper接口
 */
@Mapper
public interface TaskTransferHistoryMapper extends BaseMapper<TaskTransferHistory> {
} 