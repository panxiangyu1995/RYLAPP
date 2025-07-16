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

    @Select("SELECT * FROM record_file WHERE record_id = #{relationId}")
    List<RecordFile> selectByRelationIdAndType(@Param("relationId") Long relationId);
}