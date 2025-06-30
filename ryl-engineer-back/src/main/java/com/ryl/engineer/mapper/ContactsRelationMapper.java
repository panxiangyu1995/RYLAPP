package com.ryl.engineer.mapper;

import com.ryl.engineer.entity.ContactsRelation;
import com.ryl.engineer.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 联系人关系Mapper接口
 */
@Mapper
public interface ContactsRelationMapper {
    
    /**
     * 获取用户的联系人列表
     */
    List<ContactsRelation> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据条件分页查询联系人
     */
    List<ContactsRelation> selectByCondition(
        @Param("userId") Long userId, 
        @Param("keyword") String keyword,
        @Param("department") String department,
        @Param("status") Integer status
    );
    
    /**
     * 获取非工程师角色的联系人列表
     */
    List<User> selectOtherContacts(
        @Param("userId") Long userId, 
        @Param("keyword") String keyword
    );
    
    /**
     * 获取用户的联系人数量
     */
    int countByCondition(
        @Param("userId") Long userId, 
        @Param("keyword") String keyword,
        @Param("department") String department,
        @Param("status") Integer status
    );
    
    /**
     * 检查是否已存在联系人关系
     */
    ContactsRelation selectByUserIdAndContactId(@Param("userId") Long userId, @Param("contactId") Long contactId);
    
    /**
     * 根据ID获取联系人关系
     */
    ContactsRelation selectById(@Param("id") Long id);
    
    /**
     * 新增联系人关系
     */
    int insert(ContactsRelation relation);
    
    /**
     * 更新联系人关系
     */
    int update(ContactsRelation relation);
    
    /**
     * 删除联系人关系
     */
    int deleteById(Long id);
    
    /**
     * 删除用户与联系人的关系
     */
    int deleteByUserIdAndContactId(@Param("userId") Long userId, @Param("contactId") Long contactId);
} 