package com.ryl.engineer.mapper;

import com.ryl.engineer.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 角色Mapper接口
 */
@Mapper
public interface RoleMapper {

    /**
     * 根据角色编码查询角色信息
     *
     * @param code 角色编码
     * @return 角色信息
     */
    Role selectByCode(@Param("code") String code);
} 