package com.ryl.engineer.mapper;

import com.ryl.engineer.entity.ContactsGroupRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 联系人分组关系Mapper接口
 */
@Mapper
public interface ContactsGroupRelationMapper {
    
    /**
     * 获取分组中的所有联系人关系
     */
    List<ContactsGroupRelation> selectByGroupId(@Param("groupId") Long groupId);
    
    /**
     * 获取指定联系人所在的分组关系
     */
    List<ContactsGroupRelation> selectByContactId(@Param("contactId") Long contactId);
    
    /**
     * 检查联系人是否在分组中
     */
    ContactsGroupRelation selectByGroupIdAndContactId(@Param("groupId") Long groupId, @Param("contactId") Long contactId);
    
    /**
     * 添加联系人到分组
     */
    int insert(ContactsGroupRelation relation);
    
    /**
     * 批量添加联系人到分组
     */
    int batchInsert(@Param("list") List<ContactsGroupRelation> relationList);
    
    /**
     * 从分组中移除联系人
     */
    int deleteByGroupIdAndContactId(@Param("groupId") Long groupId, @Param("contactId") Long contactId);
    
    /**
     * 批量从分组中移除联系人
     */
    int batchDeleteByGroupIdAndContactIds(@Param("groupId") Long groupId, @Param("contactIds") List<Long> contactIds);
    
    /**
     * 删除分组的所有关联
     */
    int deleteByGroupId(@Param("groupId") Long groupId);
} 