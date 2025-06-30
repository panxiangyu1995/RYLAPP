package com.ryl.engineer.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ryl.engineer.common.PageResult;
import com.ryl.engineer.dto.contact.ContactDTO;
import com.ryl.engineer.dto.contact.ContactGroupDTO;
import com.ryl.engineer.entity.ContactsGroup;
import com.ryl.engineer.entity.ContactsGroupRelation;
import com.ryl.engineer.entity.ContactsRelation;
import com.ryl.engineer.entity.User;
import com.ryl.engineer.exception.ServiceException;
import com.ryl.engineer.mapper.ContactsGroupMapper;
import com.ryl.engineer.mapper.ContactsGroupRelationMapper;
import com.ryl.engineer.mapper.ContactsRelationMapper;
import com.ryl.engineer.service.ContactsService;
import com.ryl.engineer.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 联系人服务实现类
 */
@Service
public class ContactsServiceImpl implements ContactsService {

    @Autowired
    private ContactsRelationMapper contactsRelationMapper;
    
    @Autowired
    private ContactsGroupMapper contactsGroupMapper;
    
    @Autowired
    private ContactsGroupRelationMapper contactsGroupRelationMapper;
    
    @Autowired
    private UserService userService;
    
    @Override
    public PageResult<ContactDTO> getContactsList(Long userId, Integer page, Integer size, 
                                                 String keyword, String department, Integer status) {
        PageHelper.startPage(page, size);
        Page<ContactsRelation> relationPage = (Page<ContactsRelation>) contactsRelationMapper.selectByCondition(
            userId, keyword, department, status);
        
        List<ContactDTO> contactDTOList = relationPage.getResult().stream()
            .map(this::convertToContactDTO)
            .collect(Collectors.toList());
        
        return new PageResult<>(relationPage.getTotal(), contactDTOList);
    }
    
    @Override
    public PageResult<ContactDTO> getOtherContactsList(Long userId, Integer page, Integer size, String keyword) {
        PageHelper.startPage(page, size);
        Page<ContactsRelation> relationPage = (Page<ContactsRelation>) contactsRelationMapper.selectOtherContacts(
            userId, keyword);
        
        List<ContactDTO> contactDTOList = relationPage.getResult().stream()
            .map(this::convertToContactDTO)
            .collect(Collectors.toList());
        
        return new PageResult<>(relationPage.getTotal(), contactDTOList);
    }
    
    @Override
    public ContactDTO getContactDetail(Long currentUserId, Long contactUserId) {
        // 获取联系人关系
        ContactsRelation relation = contactsRelationMapper.selectByUserIdAndContactId(currentUserId, contactUserId);
        if (relation == null) {
            throw new ServiceException("联系人不存在");
        }
        
        ContactDTO contactDTO = convertToContactDTO(relation);
        // 这里可以添加更多的详情信息，如任务列表等
        
        return contactDTO;
    }
    
    @Override
    public List<ContactGroupDTO> getContactGroups(Long userId) {
        List<ContactsGroup> groups = contactsGroupMapper.selectByUserId(userId);
        return groups.stream()
            .map(this::convertToContactGroupDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public ContactGroupDTO createContactGroup(Long userId, String name) {
        if (!StringUtils.hasText(name)) {
            throw new ServiceException("分组名称不能为空");
        }
        
        ContactsGroup group = new ContactsGroup();
        group.setUserId(userId);
        group.setName(name);
        group.setSortOrder(0); // 默认排序
        group.setCreateTime(new Date());
        group.setUpdateTime(new Date());
        
        contactsGroupMapper.insert(group);
        
        return convertToContactGroupDTO(group);
    }
    
    @Override
    @Transactional
    public ContactGroupDTO updateContactGroup(Long groupId, Long userId, String name) {
        if (!StringUtils.hasText(name)) {
            throw new ServiceException("分组名称不能为空");
        }
        
        ContactsGroup group = contactsGroupMapper.selectById(groupId);
        if (group == null) {
            throw new ServiceException("分组不存在");
        }
        
        // 验证权限
        if (!group.getUserId().equals(userId)) {
            throw new ServiceException("无权操作此分组");
        }
        
        group.setName(name);
        group.setUpdateTime(new Date());
        
        contactsGroupMapper.update(group);
        
        return convertToContactGroupDTO(group);
    }
    
    @Override
    @Transactional
    public boolean deleteContactGroup(Long groupId, Long userId) {
        ContactsGroup group = contactsGroupMapper.selectById(groupId);
        if (group == null) {
            throw new ServiceException("分组不存在");
        }
        
        // 验证权限
        if (!group.getUserId().equals(userId)) {
            throw new ServiceException("无权操作此分组");
        }
        
        // 删除分组关联
        contactsGroupRelationMapper.deleteByGroupId(groupId);
        
        // 删除分组
        return contactsGroupMapper.deleteById(groupId) > 0;
    }
    
    @Override
    @Transactional
    public ContactGroupDTO addContactsToGroup(Long groupId, Long userId, List<Long> contactIds) {
        if (contactIds == null || contactIds.isEmpty()) {
            throw new ServiceException("联系人ID列表不能为空");
        }
        
        // 验证分组
        ContactsGroup group = contactsGroupMapper.selectById(groupId);
        if (group == null) {
            throw new ServiceException("分组不存在");
        }
        
        // 验证权限
        if (!group.getUserId().equals(userId)) {
            throw new ServiceException("无权操作此分组");
        }
        
        // 批量添加关联
        List<ContactsGroupRelation> relationList = new ArrayList<>();
        Date now = new Date();
        
        for (Long contactId : contactIds) {
            // 检查联系人关系是否存在
            ContactsRelation relation = contactsRelationMapper.selectByUserIdAndContactId(userId, contactId);
            if (relation == null) {
                continue;
            }
            
            // 检查是否已在分组中
            if (contactsGroupRelationMapper.selectByGroupIdAndContactId(groupId, relation.getId()) != null) {
                continue;
            }
            
            ContactsGroupRelation groupRelation = new ContactsGroupRelation();
            groupRelation.setGroupId(groupId);
            groupRelation.setContactId(relation.getId());
            groupRelation.setCreateTime(now);
            relationList.add(groupRelation);
        }
        
        if (!relationList.isEmpty()) {
            contactsGroupRelationMapper.batchInsert(relationList);
        }
        
        // 返回更新后的分组信息
        return getContactGroupWithMembers(groupId);
    }
    
    @Override
    @Transactional
    public ContactGroupDTO removeContactsFromGroup(Long groupId, Long userId, List<Long> contactIds) {
        if (contactIds == null || contactIds.isEmpty()) {
            throw new ServiceException("联系人ID列表不能为空");
        }
        
        // 验证分组
        ContactsGroup group = contactsGroupMapper.selectById(groupId);
        if (group == null) {
            throw new ServiceException("分组不存在");
        }
        
        // 验证权限
        if (!group.getUserId().equals(userId)) {
            throw new ServiceException("无权操作此分组");
        }
        
        // 获取联系人关系ID列表
        List<Long> relationIds = new ArrayList<>();
        for (Long contactId : contactIds) {
            ContactsRelation relation = contactsRelationMapper.selectByUserIdAndContactId(userId, contactId);
            if (relation != null) {
                relationIds.add(relation.getId());
            }
        }
        
        if (!relationIds.isEmpty()) {
            contactsGroupRelationMapper.batchDeleteByGroupIdAndContactIds(groupId, relationIds);
        }
        
        // 返回更新后的分组信息
        return getContactGroupWithMembers(groupId);
    }
    
    // 辅助方法：将联系人关系转换为DTO
    private ContactDTO convertToContactDTO(ContactsRelation relation) {
        ContactDTO dto = new ContactDTO();
        // 设置联系人关系信息
        dto.setId(relation.getContactId());
        dto.setRelationType(relation.getRelationType());
        dto.setRemark(relation.getRemark());
        
        // 设置从User表关联的信息（通过JOIN查询出来的）
        try {
            // 使用反射获取额外的用户字段
            java.lang.reflect.Method getWorkId = relation.getClass().getMethod("getWorkId");
            java.lang.reflect.Method getName = relation.getClass().getMethod("getName");
            java.lang.reflect.Method getAvatar = relation.getClass().getMethod("getAvatar");
            java.lang.reflect.Method getDepartment = relation.getClass().getMethod("getDepartment");
            java.lang.reflect.Method getMobile = relation.getClass().getMethod("getMobile");
            java.lang.reflect.Method getStatus = relation.getClass().getMethod("getStatus");
            java.lang.reflect.Method getLastActiveTime = relation.getClass().getMethod("getLastActiveTime");
            
            // 设置用户信息
            dto.setWorkId((String) getWorkId.invoke(relation));
            dto.setName((String) getName.invoke(relation));
            dto.setAvatar((String) getAvatar.invoke(relation));
            dto.setDepartment((String) getDepartment.invoke(relation));
            dto.setMobile((String) getMobile.invoke(relation));
            dto.setStatus((Integer) getStatus.invoke(relation));
            dto.setLastActiveTime((Date) getLastActiveTime.invoke(relation));
        } catch (Exception e) {
            // 如果无法获取额外字段（例如从selectById获取的对象），尝试使用UserService获取完整用户信息
            if (relation.getContactId() != null) {
                try {
                    User user = userService.getUserById(relation.getContactId());
                    if (user != null) {
                        dto.setWorkId(user.getWorkId());
                        dto.setName(user.getName());
                        dto.setAvatar(user.getAvatar());
                        dto.setDepartment(user.getDepartment());
                        dto.setMobile(user.getMobile());
                        dto.setStatus(user.getStatus());
                        // 使用更新时间作为最后活跃时间（简化处理）
                        dto.setLastActiveTime(user.getUpdateTime());
                    }
                } catch (Exception ex) {
                    // 忽略异常，确保转换过程不会中断
                }
            }
        }
        
        return dto;
    }
    
    // 辅助方法：将分组信息转换为DTO
    private ContactGroupDTO convertToContactGroupDTO(ContactsGroup group) {
        ContactGroupDTO dto = new ContactGroupDTO();
        BeanUtils.copyProperties(group, dto);
        
        // 获取分组成员信息
        List<ContactsGroupRelation> relations = contactsGroupRelationMapper.selectByGroupId(group.getId());
        List<ContactDTO> contacts = new ArrayList<>();
        
        for (ContactsGroupRelation relation : relations) {
            ContactsRelation contactRelation = contactsRelationMapper.selectById(relation.getContactId());
            if (contactRelation != null) {
                contacts.add(convertToContactDTO(contactRelation));
            }
        }
        
        dto.setContacts(contacts);
        return dto;
    }
    
    // 辅助方法：获取带成员的分组信息
    private ContactGroupDTO getContactGroupWithMembers(Long groupId) {
        ContactsGroup group = contactsGroupMapper.selectById(groupId);
        if (group == null) {
            throw new ServiceException("分组不存在");
        }
        return convertToContactGroupDTO(group);
    }
} 