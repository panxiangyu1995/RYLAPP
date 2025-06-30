package com.ryl.engineer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.engineer.entity.TaskActivity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务活动记录Mapper接口
 */
@Mapper
public interface TaskActivityMapper extends BaseMapper<TaskActivity> {
} 