package com.ryl.miniprogram.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.miniprogram.entity.RecordImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RecordImageMapper extends BaseMapper<RecordImage> {

    @Select("SELECT * FROM record_image WHERE record_id = #{relationId}")
    List<RecordImage> selectByRelationIdAndType(@Param("relationId") Long relationId);
} 