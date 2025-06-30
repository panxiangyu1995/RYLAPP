# 研仪联工程师APP联系人模块前后端API数据交互分析

## 1. 概述

本文档系统性分析了研仪联工程师APP联系人模块的前后端API数据交互，包括API接口定义、数据库表结构、数据流转和UI展示等方面，旨在提供完整的联系人模块技术实现参考。

## 2. 数据库表结构

### 2.1 核心表

联系人模块主要涉及以下数据库表：

| 表名 | 描述 | 主要字段 |
|------|------|---------|
| user | 用户表 | id, work_id, name, mobile, password, department, location, avatar, status |
| user_status | 用户状态表 | id, user_id, status, last_active_time, current_task_id, current_task_name, current_location |
| user_role | 用户角色关联表 | id, user_id, role_id |
| role | 角色表 | id, code, name, description |
| customer | 客户表 | id, name, level, contact, phone, address |
| user_customer_relation | 用户客户关系表 | id, user_id, customer_id, relation_type |

### 2.2 重要关联关系

1. **用户与角色**：通过`user_role`表进行多对多关联
2. **用户与状态**：通过`user_status`表的`user_id`字段一对一关联
3. **用户与客户**：通过`user_customer_relation`表多对多关联，并通过`relation_type`字段标识关系类型

## 3. 前端API接口定义

### 3.1 API模块结构

前端联系人API定义在`ryl-engineer-app/src/api/contacts.js`文件中，主要提供以下功能：

1. 获取联系人列表（工程师、其他角色）
2. 获取工程师状态（按工作地点分组）
3. 获取联系人详情
4. 更新用户状态和位置信息
5. 客户管理相关功能

### 3.2 核心API函数

```javascript
// 获取联系人列表（通用方法）
export function getContactsList(params)

// 获取工程师联系人列表
export function getEngineerContacts(params)

// 获取工程师状态列表（按工作地点分组）
export function getEngineerStatusByLocation(params)

// 获取工程师详情
export function getEngineerDetail(userId)

// 更新工程师状态
export function updateEngineerStatus(status)

// 获取客户列表
export function getCustomersList(params)

// 获取其它联系人列表（非工程师角色）
export function getOtherContacts(params)

// 更新用户当前任务信息
export function updateUserCurrentTask(params)

// 更新用户当前位置
export function updateUserLocation(params)
```

### 3.3 API参数与返回格式

所有API遵循统一的请求和响应格式：

**请求参数示例**：
```javascript
{
  page: 1,           // 页码，从1开始
  size: 20,          // 每页记录数
  keyword: "搜索词"   // 搜索关键词
}
```

**响应格式示例**：
```javascript
{
  code: 200,         // 状态码，200表示成功
  message: "获取成功", // 响应消息
  data: {            // 响应数据
    total: 100,      // 总记录数
    pages: 5,        // 总页数
    current: 1,      // 当前页码
    size: 20,        // 每页记录数
    list: [...]      // 数据列表
  }
}
```

## 4. 后端API实现

### 4.1 控制器层

后端联系人API控制器定义在`ryl-engineer-back/src/main/java/com/ryl/engineer/controller/ContactsController.java`文件中：

```java
@RestController
@RequestMapping("/api/v1/contacts")
public class ContactsController {
    
    @Autowired
    private ContactsService contactsService;
    
    // 获取工程师联系人列表
    @GetMapping("/engineers")
    public Result<Map<String, Object>> getEngineerContacts(...)
    
    // 获取其他联系人列表
    @GetMapping("/others")
    public Result<Map<String, Object>> getOtherContacts(...)
    
    // 获取联系人详情
    @GetMapping("/detail")
    public Result<ContactUserDTO> getContactDetail(...)
    
    // 获取工程师状态列表（按工作地点分组）
    @GetMapping({"/engineer-status", "/engineers/status"})
    public Result<Map<String, List<ContactUserDTO>>> getEngineerStatusByLocation(...)
    
    // 更新用户在线状态
    @PutMapping("/status")
    public Result<Boolean> updateUserStatus(...)
    
    // 获取客户管理列表
    @GetMapping("/customers")
    public Result<Map<String, Object>> getCustomerManagement(...)
    
    // 更新用户当前任务信息
    @PutMapping("/current-task")
    public Result<Boolean> updateUserCurrentTask(...)
    
    // 更新用户当前位置
    @PutMapping("/location")
    public Result<Boolean> updateUserLocation(...)
}
```

### 4.2 服务层

服务层接口定义在`ryl-engineer-back/src/main/java/com/ryl/engineer/service/ContactsService.java`，实现在`ContactsServiceImpl.java`：

```java
@Service
public class ContactsServiceImpl implements ContactsService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserStatusMapper userStatusMapper;
    
    @Autowired
    private UserCustomerRelationMapper userCustomerRelationMapper;
    
    // 获取工程师联系人列表
    @Override
    public PageInfo<ContactUserDTO> getEngineerContacts(Integer page, Integer size, String keyword) {
        PageHelper.startPage(page, size);
        List<ContactUserDTO> engineers = userMapper.selectContactsByRole("engineer", keyword);
        return new PageInfo<>(engineers);
    }
    
    // 获取工程师状态列表（按工作地点分组）
    @Override
    public Map<String, List<ContactUserDTO>> getEngineerStatusByLocation(Integer page, Integer size, String keyword) {
        try {
            PageHelper.startPage(page, size);
            List<ContactUserDTO> engineers = userMapper.selectEngineersByLocation(keyword);
            
            // 按工作地点分组
            Map<String, List<ContactUserDTO>> result = new HashMap<>();
            for (ContactUserDTO engineer : engineers) {
                String location = engineer.getPrimaryWorkLocation();
                if (location == null || location.isEmpty()) {
                    location = "未分配";
                }
                
                if (!result.containsKey(location)) {
                    result.put(location, new ArrayList<>());
                }
                result.get(location).add(engineer);
            }
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
```

### 4.3 数据访问层

数据访问层主要通过MyBatis映射文件实现，关键映射文件包括：

1. `UserMapper.xml` - 用户相关查询
2. `UserStatusMapper.xml` - 用户状态相关操作

核心SQL查询：

```xml
<!-- 查询工程师列表（按工作地点分组） -->
<select id="selectEngineersByLocation" resultMap="ContactUserResultMap">
    SELECT 
        u.id, u.work_id, u.name, u.avatar, u.department, u.mobile, 
        u.location as primary_work_location, u.location as current_location,
        r.code as role_code, r.name as role_name,
        us.status, us.last_active_time, us.current_task_id, us.current_task_name
    FROM 
        dbo.[user] u
    JOIN 
        dbo.[user_role] ur ON u.id = ur.user_id
    JOIN 
        dbo.[role] r ON ur.role_id = r.id
    LEFT JOIN 
        dbo.[user_status] us ON u.id = us.user_id
    WHERE 
        r.code = 'ENGINEER'
        AND u.status = 1
    <if test="keyword != null and keyword != ''">
        AND (u.name LIKE '%' + #{keyword} + '%' OR u.work_id LIKE '%' + #{keyword} + '%')
    </if>
    ORDER BY 
        u.location,
        CASE WHEN us.status = 'online' THEN 1
             WHEN us.status = 'busy' THEN 2
             ELSE 3
        END,
        u.name
</select>
```

## 5. 数据传输对象（DTO）

### 5.1 ContactUserDTO

`ContactUserDTO`是联系人模块的核心数据传输对象，定义如下：

```java
@Data
public class ContactUserDTO {
    private Long id;                  // 用户ID
    private String workId;            // 工号
    private String name;              // 姓名
    private String avatar;            // 头像
    private String department;        // 部门
    private String mobile;            // 手机号
    private String roleCode;          // 角色代码
    private String roleName;          // 角色名称
    private String status;            // 状态（online/busy/offline）
    private LocalDateTime lastActiveTime; // 最后活跃时间
    private String currentTaskId;     // 当前任务ID
    private String currentTaskName;   // 当前任务名称
    private String currentLocation;   // 当前位置
    private String primaryWorkLocation; // 主要工作地点
}
```

## 6. 前端UI组件与数据展示

### 6.1 联系人模块页面结构

联系人模块前端页面主要包括：

1. `Contacts.vue` - 联系人主页面，包含导航标签
2. `EngineerStatus.vue` - 工程师状态页面（按工作地点分组显示）
3. `CustomerManagement.vue` - 客户管理页面
4. `OtherContacts.vue` - 其他联系人页面
5. `ContactDetail.vue` - 联系人详情页面

### 6.2 工程师状态页面实现

`EngineerStatus.vue`是联系人模块的核心页面，实现了按工作地点分组显示工程师状态的功能：

```vue
<template>
  <div class="engineer-status-page">
    <!-- 内容过滤器 -->
    <div class="filter-bar">
      <div class="filter-buttons">
        <button 
          v-for="(filter, index) in filters" 
          :key="index"
          :class="['filter-btn', { active: activeFilter === filter.value }]"
          @click="activeFilter = filter.value"
        >
          {{ filter.label }}
        </button>
      </div>
    </div>

    <!-- 工程师状态内容 -->
    <div class="engineers-container">
      <!-- 按工作地点分组显示 -->
      <div 
        v-for="(engineers, location) in filteredEngineers" 
        :key="location"
        class="location-group"
      >
        <!-- 位置标题 -->
        <div class="location-header">
          <h3 class="location-name">
            {{ location }}
            <span class="engineer-count">({{ engineers.length }}人)</span>
          </h3>
        </div>
        
        <!-- 工程师列表 -->
        <div class="location-content">
          <table class="engineers-table">
            <thead>
              <tr>
                <th>工程师</th>
                <th>当前任务</th>
                <th>状态</th>
                <th>当前位置</th>
              </tr>
            </thead>
            <tbody>
              <!-- 工程师行 -->
              <tr v-for="engineer in engineers" :key="engineer.id">
                <!-- 工程师信息 -->
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
```

### 6.3 数据加载与处理

前端组件通过API获取数据并进行处理：

```javascript
// 加载工程师数据
const loadEngineers = () => {
  loading.value = true;
  error.value = null;
  
  // 调用API获取工程师列表
  getEngineerStatusByLocation({})
    .then(response => {
      if (response && response.code === 200) {
        // 处理API返回的数据结构
        const locationData = response.data || {};
        const formattedData = {};
        
        // 将API返回的数据转换为组件需要的格式
        Object.entries(locationData).forEach(([location, engineers]) => {
          formattedData[location] = engineers.map(engineer => ({
            id: engineer.id,
            name: engineer.name,
            avatar: engineer.avatar,
            status: engineer.status || 'offline',
            currentTaskName: engineer.currentTaskName || '无任务',
            currentLocation: engineer.currentLocation || engineer.primaryWorkLocation || '未知'
          }));
        });
        
        engineersByLocation.value = formattedData;
      }
    })
    .finally(() => {
      loading.value = false;
    });
};
```

## 7. 数据流转流程

### 7.1 获取工程师状态列表流程

1. **前端发起请求**：
   ```javascript
   getEngineerStatusByLocation({})
   ```

2. **后端控制器处理**：
   ```java
   @GetMapping("/engineer-status")
   public Result<Map<String, List<ContactUserDTO>>> getEngineerStatusByLocation(...)
   ```

3. **服务层处理**：
   ```java
   Map<String, List<ContactUserDTO>> result = contactsService.getEngineerStatusByLocation(page, size, keyword);
   ```

4. **数据访问层查询**：
   ```java
   List<ContactUserDTO> engineers = userMapper.selectEngineersByLocation(keyword);
   ```

5. **MyBatis执行SQL**：
   ```xml
   <select id="selectEngineersByLocation" resultMap="ContactUserResultMap">
     <!-- SQL查询语句 -->
   </select>
   ```

6. **返回数据到前端**：
   ```javascript
   {
     code: 200,
     message: "获取成功",
     data: {
       "北京": [
         {id: 1, name: "张工", status: "online", ...},
         {id: 2, name: "李工", status: "busy", ...}
       ],
       "上海": [
         {id: 3, name: "王工", status: "offline", ...}
       ]
     }
   }
   ```

7. **前端处理数据**：
   ```javascript
   Object.entries(locationData).forEach(([location, engineers]) => {
     formattedData[location] = engineers.map(engineer => ({...}));
   });
   ```

8. **UI渲染**：
   ```vue
   <div v-for="(engineers, location) in filteredEngineers" :key="location">
     <!-- 渲染每个位置的工程师列表 -->
   </div>
   ```

### 7.2 更新用户状态流程

1. **前端发起请求**：
   ```javascript
   updateEngineerStatus('online')
   ```

2. **后端控制器处理**：
   ```java
   @PutMapping("/status")
   public Result<Boolean> updateUserStatus(...)
   ```

3. **服务层处理**：
   ```java
   boolean success = contactsService.updateUserStatus(userId, status);
   ```

4. **数据访问层更新**：
   ```java
   userStatusMapper.updateUserStatus(userId, status)
   ```

5. **MyBatis执行SQL**：
   ```xml
   <update id="updateUserStatus">
     UPDATE user_status
     SET status = #{status},
         last_active_time = GETDATE(),
         update_time = GETDATE()
     WHERE user_id = #{userId}
   </update>
   ```

6. **返回结果到前端**：
   ```javascript
   {
     code: 200,
     message: "更新成功",
     data: true
   }
   ```

## 8. 关键字段映射与转换

### 8.1 数据库字段与DTO字段映射

| 数据库字段 | DTO字段 | 说明 |
|-----------|---------|------|
| user.id | ContactUserDTO.id | 用户ID |
| user.work_id | ContactUserDTO.workId | 工号 |
| user.name | ContactUserDTO.name | 姓名 |
| user.avatar | ContactUserDTO.avatar | 头像 |
| user.department | ContactUserDTO.department | 部门 |
| user.mobile | ContactUserDTO.mobile | 手机号 |
| user.location | ContactUserDTO.primaryWorkLocation | 主要工作地点 |
| role.code | ContactUserDTO.roleCode | 角色代码 |
| role.name | ContactUserDTO.roleName | 角色名称 |
| user_status.status | ContactUserDTO.status | 状态 |
| user_status.last_active_time | ContactUserDTO.lastActiveTime | 最后活跃时间 |
| user_status.current_task_id | ContactUserDTO.currentTaskId | 当前任务ID |
| user_status.current_task_name | ContactUserDTO.currentTaskName | 当前任务名称 |
| user_status.current_location | ContactUserDTO.currentLocation | 当前位置 |

### 8.2 状态值转换

| 数据库值 | 前端显示 | 样式类 |
|---------|---------|--------|
| online | 在线 | status-available |
| busy | 忙碌 | status-busy |
| offline | 离线 | status-offline |

## 9. 权限控制

联系人模块的权限控制主要体现在：

1. **客户管理标签的显示**：
   ```javascript
   const hasCustomerAccess = computed(() => {
     return authStore.hasRole('ROLE_ENGINEER') || 
            authStore.hasRole('ROLE_SALES') || 
            authStore.hasRole('ROLE_ADMIN')
   })
   ```

2. **客户数据的访问**：
   后端根据用户角色和ID过滤客户数据：
   ```java
   List<Map<String, Object>> customers = userMapper.selectCustomerManagement(userId, keyword, level);
   ```

## 10. 性能优化与注意事项

1. **分页查询**：使用PageHelper进行分页，避免一次性加载过多数据
   ```java
   PageHelper.startPage(page, size);
   ```

2. **按需加载**：工程师状态页面按工作地点分组，并支持展开/折叠
   ```javascript
   const expandedLocations = ref([]);
   ```

3. **错误处理**：前端和后端都有完善的错误处理机制
   ```javascript
   .catch(err => {
     console.error('获取工程师列表异常:', err);
     error.value = '无法连接服务器，请检查网络连接';
   })
   ```

4. **状态缓存**：用户状态信息存储在localStorage中，减少不必要的请求
   ```javascript
   const user = JSON.parse(localStorage.getItem('user') || '{}');
   ```

## 11. 总结

研仪联工程师APP联系人模块实现了工程师状态展示、客户管理和联系人管理等功能，前后端数据交互清晰，UI展示友好。通过本文档的分析，可以全面了解联系人模块的技术实现细节，为后续开发和维护提供参考。

主要特点：
1. 按工作地点分组显示工程师状态
2. 实时更新和显示工程师在线状态
3. 基于角色的权限控制
4. 统一的API请求和响应格式
5. 完善的错误处理机制