package com.ryl.miniprogram.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.miniprogram.entity.RecordFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 记录文件Mapper接口
 */
@Mapper
public interface RecordFileMapper extends BaseMapper<RecordFile> {

    @Select("SELECT * FROM record_file WHERE relation_id = #{relationId} AND relation_type = #{relationType}")
    List<RecordFile> selectByRelationIdAndType(@Param("relationId") Long relationId, @Param("relationType") int relationType);
}