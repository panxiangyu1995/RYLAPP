package com.ryl.miniprogram.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.miniprogram.entity.RecordImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RecordImageMapper extends BaseMapper<RecordImage> {

    @Select("SELECT * FROM record_image WHERE relation_id = #{relationId} AND relation_type = #{relationType}")
    List<RecordImage> selectByRelationIdAndType(@Param("relationId") Long relationId, @Param("relationType") int relationType);
} 