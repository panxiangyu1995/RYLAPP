package com.ryl.engineer.mapper;

import com.ryl.engineer.entity.ContactsGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 联系人分组Mapper接口
 */
@Mapper
public interface ContactsGroupMapper {
    
    /**
     * 获取用户的所有分组
     */
    List<ContactsGroup> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据ID获取分组
     */
    ContactsGroup selectById(Long id);
    
    /**
     * 添加分组
     */
    int insert(ContactsGroup group);
    
    /**
     * 更新分组
     */
    int update(ContactsGroup group);
    
    /**
     * 删除分组
     */
    int deleteById(Long id);
    
    /**
     * 更新分组排序
     */
    int updateSortOrder(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);
} 