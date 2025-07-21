package com.ryl.engineer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.engineer.entity.TaskStepRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务步骤记录 Mapper 接口
 */
@Mapper
public interface TaskStepRecordMapper extends BaseMapper<TaskStepRecord> {
} 