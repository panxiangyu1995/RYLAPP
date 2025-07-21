package com.ryl.engineer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryl.engineer.entity.AppVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * AppVersion Mapper
 * @author: RYL
 */
@Mapper
public interface AppVersionMapper extends BaseMapper<AppVersion> {

    /**
     * 查询最新版本信息
     * @return AppVersion
     */
    @Select("SELECT TOP 1 * FROM app_version ORDER BY version_code DESC")
    AppVersion findLatest();
} 