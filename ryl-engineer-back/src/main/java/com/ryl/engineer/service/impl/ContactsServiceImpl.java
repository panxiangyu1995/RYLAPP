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
import com.ryl.engineer.mapper.UserMapper;
import com.ryl.engineer.service.ContactsService;
import com.ryl.engineer.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 联系人服务实现类
 */
@Service
public class ContactsServiceImpl implements ContactsService {

    private static final Logger logger = LoggerFactory.getLogger(ContactsServiceImpl.class);

    @Autowired
    private ContactsRelationMapper contactsRelationMapper;
    
    @Autowired
    private ContactsGroupMapper contactsGroupMapper;
    
    @Autowired
    private ContactsGroupRelationMapper contactsGroupRelationMapper;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
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
        logger.info("获取其它联系人列表 - 页码: {}, 每页大小: {}, 关键词: {}", page, size, keyword);
        
        try {
        PageHelper.startPage(page, size);
            logger.debug("执行查询前 - 已设置分页参数");
            
            // 执行查询，不再传递userId参数
            List<User> userList = contactsRelationMapper.selectOtherContacts(keyword);
            
            // 直接输出查询结果
            logger.info("查询结果大小: {}", userList != null ? userList.size() : 0);
            if (userList != null && !userList.isEmpty()) {
                logger.info("第一条记录: id={}, name={}, workId={}", 
                    userList.get(0).getId(), 
                    userList.get(0).getName(),
                    userList.get(0).getWorkId());
            } else {
                logger.warn("查询结果为空");
            }
            
            if (userList instanceof Page) {
                Page<User> userPage = (Page<User>) userList;
                logger.info("查询成功 - 总记录数: {}, 当前页记录数: {}", userPage.getTotal(), userPage.size());
                
                // 转换为DTO
                List<ContactDTO> contactDTOList = userPage.getResult().stream()
                    .map(this::convertUserToContactDTO)
                    .collect(Collectors.toList());
                
                return new PageResult<>(userPage.getTotal(), contactDTOList);
            } else {
                // 处理非Page类型的结果（兼容性处理）
                logger.warn("查询结果不是Page类型，无法获取总数，使用列表大小作为总数");
                List<ContactDTO> contactDTOList = userList.stream()
                    .map(this::convertUserToContactDTO)
            .collect(Collectors.toList());
        
                return new PageResult<>((long) contactDTOList.size(), contactDTOList);
            }
        } catch (Exception e) {
            // 增强错误日志
            if (e.getCause() instanceof java.sql.SQLException) {
                java.sql.SQLException sqlException = (java.sql.SQLException) e.getCause();
                logger.error("SQL异常: 错误码={}, 状态={}, 消息={}", 
                    sqlException.getErrorCode(), 
                    sqlException.getSQLState(), 
                    sqlException.getMessage(), e);
            } else {
                logger.error("获取其它联系人列表异常", e);
            }
            // 返回空列表而不是抛出异常
            return new PageResult<>(0L, new ArrayList<>());
        }
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
    
    // 新增辅助方法：将User对象转换为ContactDTO
    private ContactDTO convertUserToContactDTO(User user) {
        if (user == null) {
            logger.warn("尝试转换null用户对象为ContactDTO");
            return null;
        }
        
        logger.debug("转换User对象为ContactDTO - 用户ID: {}, 姓名: {}", user.getId(), user.getName());
        
        ContactDTO dto = new ContactDTO();
        // 设置用户基本信息
        dto.setId(user.getId());
        dto.setWorkId(user.getWorkId());
        dto.setName(user.getName());
        dto.setAvatar(user.getAvatar());
        dto.setDepartment(user.getDepartment());
        dto.setMobile(user.getMobile());
        dto.setStatus(user.getStatus());
        
        // 使用最后登录时间作为最后活跃时间
        dto.setLastActiveTime(user.getLastLoginTime());
        
        // 简化角色处理，使用默认角色
        dto.setRole("其他联系人");
        
        return dto;
    }

    @Override
    public Map<String, List<Map<String, Object>>> getEngineerStatusByLocation(String status, String keyword) {
        logger.info("获取工程师状态列表 - 状态筛选: {}, 关键词: {}", status, keyword);
        
        try {
            // 使用JdbcTemplate直接执行SQL查询，获取工程师列表
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT u.id, u.work_id as workId, u.name, u.department, u.location, u.avatar, ");
            sql.append("(SELECT COUNT(*) FROM task_engineer te JOIN task t ON te.task_id = t.task_id ");
            sql.append("WHERE te.engineer_id = u.id AND t.status NOT IN ('completed', 'cancelled')) as taskCount, ");
            sql.append("(SELECT TOP 1 t.title FROM task_engineer te JOIN task t ON te.task_id = t.task_id ");
            sql.append("WHERE te.engineer_id = u.id AND t.status NOT IN ('completed', 'cancelled') ");
            sql.append("ORDER BY t.priority DESC, t.create_time DESC) as currentTask ");
            sql.append("FROM [user] u ");
            sql.append("JOIN user_role ur ON u.id = ur.user_id ");
            sql.append("JOIN role r ON ur.role_id = r.id ");
            sql.append("WHERE r.code = 'ENGINEER' AND u.status = 1 ");
            
            List<Object> params = new ArrayList<>();
            
            // 添加关键字搜索条件
            if (keyword != null && !keyword.isEmpty()) {
                sql.append("AND (u.name LIKE ? OR u.work_id LIKE ? OR u.department LIKE ?) ");
                String likePattern = "%" + keyword + "%";
                params.add(likePattern);
                params.add(likePattern);
                params.add(likePattern);
            }
            
            sql.append("ORDER BY u.location, u.name");
            
            List<Map<String, Object>> engineers;
            if (params.isEmpty()) {
                engineers = jdbcTemplate.queryForList(sql.toString());
            } else {
                engineers = jdbcTemplate.queryForList(sql.toString(), params.toArray());
            }
            
            logger.info("查询到{}个工程师", engineers.size());
            
            // 处理工程师状态
            for (Map<String, Object> engineer : engineers) {
                Integer taskCount = ((Number) engineer.getOrDefault("taskCount", 0)).intValue();
                
                // 根据任务数量判断状态
                if (taskCount == 0) {
                    engineer.put("status", "可协助");
                } else if (taskCount < 3) {
                    engineer.put("status", "部分可协");
                } else {
                    engineer.put("status", "忙碌");
                }
                
                // 如果没有当前任务，设置为"无任务"
                if (engineer.get("currentTask") == null) {
                    engineer.put("currentTask", "无任务");
                }
            }
            
            // 根据status参数过滤
            if (status != null && !status.isEmpty()) {
                if ("available".equals(status)) {
                    engineers = engineers.stream()
                        .filter(e -> "可协助".equals(e.get("status")) || "部分可协".equals(e.get("status")))
                        .collect(Collectors.toList());
                } else if ("busy".equals(status)) {
                    engineers = engineers.stream()
                        .filter(e -> "忙碌".equals(e.get("status")))
                        .collect(Collectors.toList());
                }
            }
            
            // 按工作地点分组
            Map<String, List<Map<String, Object>>> result = new HashMap<>();
            for (Map<String, Object> engineer : engineers) {
                String location = (String) engineer.getOrDefault("location", "未知");
                if (location == null || location.isEmpty()) {
                    location = "未知";
                }
                
                if (!result.containsKey(location)) {
                    result.put(location, new ArrayList<>());
                }
                result.get(location).add(engineer);
            }
            
            return result;
        } catch (Exception e) {
            logger.error("获取工程师状态列表异常", e);
            throw e;
        }
    }
} 