package com.ryl.miniprogram.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.miniprogram.entity.TaskStep;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务步骤Mapper接口
 */
@Mapper
public interface TaskStepMapper extends BaseMapper<TaskStep> {
}