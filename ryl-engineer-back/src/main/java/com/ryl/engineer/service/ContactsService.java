package com.ryl.engineer.service;

import com.ryl.engineer.dto.contact.ContactDTO;
import com.ryl.engineer.dto.contact.ContactGroupDTO;
import com.ryl.engineer.common.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 联系人服务接口
 */
public interface ContactsService {
    
    /**
     * 获取联系人列表
     * @param userId 当前用户ID
     * @param page 页码
     * @param size 页大小
     * @param keyword 搜索关键词
     * @param department 部门
     * @param status 状态
     * @return 联系人分页列表
     */
    PageResult<ContactDTO> getContactsList(Long userId, Integer page, Integer size, 
                                          String keyword, String department, Integer status);
    
    /**
     * 获取非工程师联系人列表
     * @param userId 当前用户ID
     * @param page 页码
     * @param size 页大小
     * @param keyword 搜索关键词
     * @return 非工程师角色的联系人分页列表
     */
    PageResult<ContactDTO> getOtherContactsList(Long userId, Integer page, Integer size, String keyword);
    
    /**
     * 获取工程师状态列表（按工作地点分组）
     * @param status 协助状态筛选（available-可协助，busy-忙碌）
     * @param keyword 搜索关键词
     * @return 按工作地点分组的工程师状态列表
     */
    Map<String, List<Map<String, Object>>> getEngineerStatusByLocation(String status, String keyword);
    
    /**
     * 获取工程师详情及任务列表
     * @param engineerId 工程师ID
     * @return 工程师详情和任务列表数据
     */
    Map<String, Object> getEngineerDetailWithTasks(Long engineerId);
    
    /**
     * 获取联系人详情
     * @param currentUserId 当前用户ID
     * @param contactUserId 联系人用户ID
     * @return 联系人详情
     */
    ContactDTO getContactDetail(Long currentUserId, Long contactUserId);
    
    /**
     * 获取联系人分组列表
     * @param userId 用户ID
     * @return 分组列表
     */
    List<ContactGroupDTO> getContactGroups(Long userId);
    
    /**
     * 创建联系人分组
     * @param userId 用户ID
     * @param name 分组名称
     * @return 创建的分组信息
     */
    ContactGroupDTO createContactGroup(Long userId, String name);
    
    /**
     * 更新联系人分组
     * @param groupId 分组ID
     * @param userId 当前用户ID
     * @param name 分组名称
     * @return 更新后的分组信息
     */
    ContactGroupDTO updateContactGroup(Long groupId, Long userId, String name);
    
    /**
     * 删除联系人分组
     * @param groupId 分组ID
     * @param userId 当前用户ID
     * @return 是否删除成功
     */
    boolean deleteContactGroup(Long groupId, Long userId);
    
    /**
     * 向分组添加联系人
     * @param groupId 分组ID
     * @param userId 当前用户ID
     * @param contactIds 联系人ID列表
     * @return 添加后的分组信息
     */
    ContactGroupDTO addContactsToGroup(Long groupId, Long userId, List<Long> contactIds);
    
    /**
     * 从分组移除联系人
     * @param groupId 分组ID
     * @param userId 当前用户ID
     * @param contactIds 联系人ID列表
     * @return 移除后的分组信息
     */
    ContactGroupDTO removeContactsFromGroup(Long groupId, Long userId, List<Long> contactIds);
} 