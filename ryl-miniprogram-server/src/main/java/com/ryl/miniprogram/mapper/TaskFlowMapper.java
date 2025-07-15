package com.ryl.miniprogram.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.miniprogram.entity.TaskFlow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务流程Mapper接口
 */
@Mapper
public interface TaskFlowMapper extends BaseMapper<TaskFlow> {
}