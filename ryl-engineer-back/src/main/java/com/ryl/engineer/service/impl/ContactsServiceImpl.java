package com.ryl.engineer.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.ryl.engineer.util.FileUrlConverter;
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
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FileUrlConverter fileUrlConverter;
    
    @Override
    public PageResult<ContactDTO> getContactsList(Long userId, Integer pageNum, Integer pageSize,
                                                 String keyword, String department, Integer status) {
        // 使用MyBatis-Plus的分页对象
        Page<ContactsRelation> page = new Page<>(pageNum, pageSize);
        IPage<ContactsRelation> relationPage = contactsRelationMapper.selectByCondition(
            page, userId, keyword, department, status);

        // 使用stream转换DTO
        IPage<ContactDTO> dtoPage = relationPage.convert(this::convertToContactDTO);

        return PageResult.fromPage(dtoPage);
    }
    
    @Override
    public PageResult<ContactDTO> getOtherContactsList(Long userId, Integer pageNum, Integer pageSize, String keyword) {
        logger.info("获取其它联系人列表 - 页码: {}, 每页大小: {}, 关键词: {}", pageNum, pageSize, keyword);
        
        try {
            // 使用MyBatis-Plus的分页对象
            Page<User> page = new Page<>(pageNum, pageSize);
            IPage<User> userPage = contactsRelationMapper.selectOtherContacts(page, keyword);
            
            logger.info("查询成功 - 总记录数: {}, 当前页记录数: {}", userPage.getTotal(), userPage.getRecords().size());

            // 转换为DTO
            IPage<ContactDTO> contactDTOPage = userPage.convert(this::convertUserToContactDTO);

            return PageResult.fromPage(contactDTOPage);
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
            PageResult<ContactDTO> errorResult = new PageResult<>();
            errorResult.setTotal(0L);
            errorResult.setList(new ArrayList<>());
            errorResult.setCurrent(pageNum);
            errorResult.setSize(pageSize);
            errorResult.setPages(0L);
            return errorResult;
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
            dto.setAvatar(fileUrlConverter.toFullUrl((String) getAvatar.invoke(relation)));
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
                        dto.setAvatar(fileUrlConverter.toFullUrl(user.getAvatar()));
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
        dto.setAvatar(fileUrlConverter.toFullUrl(user.getAvatar()));
        dto.setDepartment(user.getDepartment());
        dto.setMobile(user.getMobile());
        dto.setStatus(user.getStatus());
        
        // 使用最后登录时间作为最后活跃时间
        dto.setLastActiveTime(user.getLastLoginTime());
        
        // 简化角色处理，使用默认角色
        dto.setRole("其他联系人");
        
        return dto;
    }

    // 新增：获取工程师详情及任务列表
    @Override
    public Map<String, Object> getEngineerDetailWithTasks(Long engineerId) {
        logger.info("获取工程师详情及任务列表 - 工程师ID: {}", engineerId);
        
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 1. 获取工程师基本信息
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT u.id, u.work_id as workId, u.name, u.department, u.location, u.avatar, ");
            sql.append("(SELECT COUNT(*) FROM task_engineer te JOIN task t ON te.task_id = t.task_id ");
            sql.append("WHERE te.engineer_id = u.id AND t.status NOT IN ('completed', 'cancelled')) as taskCount, ");
            sql.append("CASE ");
            sql.append("  WHEN (SELECT COUNT(*) FROM task_engineer te JOIN task t ON te.task_id = t.task_id ");
            sql.append("        WHERE te.engineer_id = u.id AND t.status NOT IN ('completed', 'cancelled')) > 2 THEN '忙碌' ");
            sql.append("  ELSE '可协助' ");
            sql.append("END as status ");
            sql.append("FROM [user] u ");
            sql.append("JOIN user_role ur ON u.id = ur.user_id ");
            sql.append("JOIN role r ON ur.role_id = r.id ");
            sql.append("WHERE u.id = ? AND r.code = 'ROLE_ENGINEER'");
            
            List<Map<String, Object>> engineerInfo = jdbcTemplate.queryForList(sql.toString(), engineerId);
            
            if (engineerInfo.isEmpty()) {
                throw new ServiceException("未找到指定工程师信息");
            }
            
            // 手动转换头像URL
            Map<String, Object> engineerDetails = engineerInfo.get(0);
            if (engineerDetails.get("avatar") instanceof String) {
                engineerDetails.put("avatar", fileUrlConverter.toFullUrl((String) engineerDetails.get("avatar")));
            }

            result.put("engineer", engineerDetails);
            
            // 2. 获取工程师的任务列表（进行中的任务）
            StringBuilder taskSql = new StringBuilder();
            taskSql.append("SELECT t.task_id as taskId, t.title, t.status, ");
            taskSql.append("(SELECT TOP 1 sr.step_index FROM step_record sr WHERE sr.task_id = t.task_id ORDER BY sr.create_time DESC) as currentStep, ");
            taskSql.append("(SELECT TOP 1 sr.description FROM step_record sr WHERE sr.task_id = t.task_id ORDER BY sr.create_time DESC) as stepDescription, ");
            taskSql.append("(SELECT TOP 1 sr.create_time FROM step_record sr WHERE sr.task_id = t.task_id ORDER BY sr.create_time DESC) as lastUpdateTime, ");
            taskSql.append("(SELECT COUNT(*) FROM step_record sr WHERE sr.task_id = t.task_id) as stepCount ");
            taskSql.append("FROM task t ");
            taskSql.append("JOIN task_engineer te ON t.task_id = te.task_id ");
            taskSql.append("WHERE te.engineer_id = ? AND t.status = 'in-progress' ");
            taskSql.append("ORDER BY t.priority DESC, t.create_time DESC");
            
            List<Map<String, Object>> inProgressTasks = jdbcTemplate.queryForList(taskSql.toString(), engineerId);
            
            // 3. 获取工程师的已完成任务
            StringBuilder completedTaskSql = new StringBuilder();
            completedTaskSql.append("SELECT t.task_id as taskId, t.title, t.status, ");
            completedTaskSql.append("(SELECT TOP 1 sr.step_index FROM step_record sr WHERE sr.task_id = t.task_id ORDER BY sr.create_time DESC) as currentStep, ");
            completedTaskSql.append("(SELECT TOP 1 sr.description FROM step_record sr WHERE sr.task_id = t.task_id ORDER BY sr.create_time DESC) as stepDescription, ");
            completedTaskSql.append("(SELECT TOP 1 sr.create_time FROM step_record sr WHERE sr.task_id = t.task_id ORDER BY sr.create_time DESC) as lastUpdateTime, ");
            completedTaskSql.append("(SELECT COUNT(*) FROM step_record sr WHERE sr.task_id = t.task_id) as stepCount ");
            completedTaskSql.append("FROM task t ");
            completedTaskSql.append("JOIN task_engineer te ON t.task_id = te.task_id ");
            completedTaskSql.append("WHERE te.engineer_id = ? AND t.status = 'completed' ");
            completedTaskSql.append("ORDER BY t.completed_date DESC");
            
            List<Map<String, Object>> completedTasks = jdbcTemplate.queryForList(completedTaskSql.toString(), engineerId);
            
            // 4. 为每个任务获取步骤记录图片
            inProgressTasks = processTaskImages(inProgressTasks);
            completedTasks = processTaskImages(completedTasks);
            
            // 5. 添加持续时间信息
            inProgressTasks = addTaskDurations(inProgressTasks);
            completedTasks = addTaskDurations(completedTasks);
            
            result.put("inProgressTasks", inProgressTasks);
            result.put("completedTasks", completedTasks);
            
            return result;
        } catch (ServiceException se) {
            // 直接抛出业务异常
            throw se;
        } catch (Exception e) {
            logger.error("获取工程师详情异常", e);
            throw new ServiceException("获取工程师详情失败: " + e.getMessage());
        }
    }
    
    // 为任务列表添加图片
    private List<Map<String, Object>> processTaskImages(List<Map<String, Object>> tasks) {
        for (Map<String, Object> task : tasks) {
            String taskId = (String) task.get("taskId");
            
            // 获取任务的最新记录ID
            Long latestRecordId = null;
            try {
                latestRecordId = jdbcTemplate.queryForObject(
                    "SELECT TOP 1 id FROM step_record WHERE task_id = ? ORDER BY create_time DESC", 
                    Long.class, taskId);
            } catch (Exception e) {
                logger.warn("获取任务记录ID失败，任务ID: {}", taskId);
                continue;
            }
            
            if (latestRecordId != null) {
                // 获取记录对应的图片URL列表
                List<String> imageUrls = jdbcTemplate.queryForList(
                    "SELECT image_url FROM record_image WHERE record_id = ?", 
                    String.class, latestRecordId);
                
                task.put("images", imageUrls);
            } else {
                task.put("images", new ArrayList<String>());
            }
        }
        return tasks;
    }
    
    // 为任务添加持续时间
    private List<Map<String, Object>> addTaskDurations(List<Map<String, Object>> tasks) {
        for (Map<String, Object> task : tasks) {
            String taskId = (String) task.get("taskId");
            
            // 获取任务的创建时间和最后更新时间
            Map<String, Object> timeData = null;
            try {
                timeData = jdbcTemplate.queryForMap(
                    "SELECT create_time, " +
                    "(SELECT TOP 1 create_time FROM step_record WHERE task_id = ? ORDER BY create_time DESC) as last_time " +
                    "FROM task WHERE task_id = ?", 
                    taskId, taskId);
            } catch (Exception e) {
                logger.warn("获取任务时间数据失败，任务ID: {}", taskId);
                task.put("duration", "未知");
                continue;
            }
            
            if (timeData != null) {
                Date createTime = (Date) timeData.get("create_time");
                Date lastTime = (Date) timeData.get("last_time");
                
                if (createTime != null && lastTime != null) {
                    // 计算持续时间（毫秒）
                    long durationMillis = lastTime.getTime() - createTime.getTime();
                    
                    // 转换为小时和分钟
                    long hours = durationMillis / (60 * 60 * 1000);
                    long minutes = (durationMillis % (60 * 60 * 1000)) / (60 * 1000);
                    
                    String durationText = hours + "小时" + minutes + "分钟";
                    task.put("duration", durationText);
                } else {
                    task.put("duration", "未知");
                }
            } else {
                task.put("duration", "未知");
            }
        }
        return tasks;
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
            sql.append("WHERE r.code = 'ROLE_ENGINEER' AND u.status = 1 ");
            
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

                // 转换头像URL
                if (engineer.get("avatar") instanceof String) {
                    engineer.put("avatar", fileUrlConverter.toFullUrl((String) engineer.get("avatar")));
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