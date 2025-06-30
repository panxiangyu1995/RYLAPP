# 联系人模块API说明

## 基础信息

- 基础路径: `/api/v1/contacts`
- 响应格式: JSON
- 认证方式: Bearer Token
- 时间格式: ISO 8601 (YYYY-MM-DDTHH:mm:ss.sssZ)

## 通用响应格式

```json
{
  "code": 200,       // 状态码，200表示成功，非200表示失败
  "message": "操作成功", // 响应消息
  "data": {}         // 响应数据，根据接口不同而变化
}
```

通用状态码
- 200: 成功
- 400: 请求参数错误
- 401: 未授权
- 403: 权限不足
- 404: 资源不存在
- 500: 服务器内部错误

## 接口列表

### 1. 获取工程师状态列表

- **接口说明**：获取工程师状态列表，按工作地点分组
- **请求方式**：GET
- **请求路径**：`/api/v1/contacts/engineers/status`
- **请求参数**：无

- **响应参数**：

| 参数名      | 类型   | 描述           |
|------------|--------|---------------|
| locations  | array  | 工作地点分组列表 |

**locations数组项结构：**

| 参数名      | 类型   | 描述           |
|------------|--------|---------------|
| location   | string | 工作地点名称    |
| engineers  | array  | 该地点的工程师列表 |

**engineers数组项结构：**

| 参数名          | 类型   | 描述                |
|----------------|--------|-------------------|
| userId         | number | 用户ID             |
| workId         | string | 工号               |
| name           | string | 姓名               |
| avatar         | string | 头像URL            |
| status         | string | 状态(online/busy/offline) |
| currentTaskId  | string | 当前任务ID          |
| currentTaskName| string | 当前任务名称        |
| lastActiveTime | string | 最后活跃时间        |

- **响应示例**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "locations": [
      {
        "location": "上海市",
        "engineers": [
          {
            "userId": 2,
            "workId": "RYL001",
            "name": "张三",
            "avatar": "https://example.com/avatars/zhangsan.png",
            "status": "online",
            "currentTaskId": "RP-2024-001",
            "currentTaskName": "气相色谱仪维修",
            "lastActiveTime": "2024-06-10T10:30:00.000Z"
          },
          {
            "userId": 5,
            "workId": "RYL004",
            "name": "李四",
            "avatar": "https://example.com/avatars/lisi.png",
            "status": "busy",
            "currentTaskId": "RP-2024-002",
            "currentTaskName": "液相色谱仪保养",
            "lastActiveTime": "2024-06-10T09:45:00.000Z"
          }
        ]
      },
      {
        "location": "北京市",
        "engineers": [
          {
            "userId": 3,
            "workId": "RYL002",
            "name": "王五",
            "avatar": "https://example.com/avatars/wangwu.png",
            "status": "offline",
            "currentTaskId": null,
            "currentTaskName": null,
            "lastActiveTime": "2024-06-09T18:20:00.000Z"
          }
        ]
      }
    ]
  }
}
```

### 2. 获取工程师详情

- **接口说明**：获取工程师详情，包括个人信息和任务信息
- **请求方式**：GET
- **请求路径**：`/api/v1/contacts/engineers/{userId}`
- **请求参数**：

| 参数名   | 类型   | 位置 | 必填 | 描述    |
|---------|--------|-----|-----|---------|
| userId  | number | path | 是  | 用户ID  |

- **响应参数**：

| 参数名          | 类型   | 描述                |
|----------------|--------|-------------------|
| userId         | number | 用户ID             |
| workId         | string | 工号               |
| name           | string | 姓名               |
| avatar         | string | 头像URL            |
| department     | string | 部门               |
| location       | string | 工作地点           |
| status         | string | 状态(online/busy/offline) |
| mobile         | string | 手机号             |
| currentTasks   | array  | 进行中的任务列表    |
| completedTasks | array  | 已完成的任务列表    |

**任务数组项结构：**

| 参数名      | 类型   | 描述           |
|------------|--------|---------------|
| taskId     | string | 任务ID        |
| taskName   | string | 任务名称       |
| type       | string | 任务类型       |
| status     | string | 任务状态       |
| startTime  | string | 开始时间       |
| endTime    | string | 结束时间       |

- **响应示例**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "userId": 2,
    "workId": "RYL001",
    "name": "张三",
    "avatar": "https://example.com/avatars/zhangsan.png",
    "department": "技术部",
    "location": "上海市",
    "status": "online",
    "mobile": "13900139001",
    "currentTasks": [
      {
        "taskId": "RP-2024-001",
        "taskName": "气相色谱仪维修",
        "type": "repair",
        "status": "in_progress",
        "startTime": "2024-06-08T09:00:00.000Z",
        "endTime": null
      }
    ],
    "completedTasks": [
      {
        "taskId": "RP-2024-000",
        "taskName": "质谱仪维修",
        "type": "repair",
        "status": "completed",
        "startTime": "2024-06-01T09:00:00.000Z",
        "endTime": "2024-06-02T15:30:00.000Z"
      }
    ]
  }
}
```

### 3. 获取客户列表

- **接口说明**：获取当前用户有权限查看的客户列表
- **请求方式**：GET
- **请求路径**：`/api/v1/contacts/customers`
- **请求参数**：

| 参数名   | 类型   | 位置  | 必填 | 描述           |
|---------|--------|------|-----|---------------|
| page    | number | query | 否  | 页码，默认1     |
| size    | number | query | 否  | 每页数量，默认10 |
| keyword | string | query | 否  | 搜索关键词      |

- **响应参数**：

| 参数名  | 类型   | 描述           |
|--------|--------|---------------|
| total  | number | 总记录数       |
| pages  | number | 总页数         |
| current| number | 当前页码       |
| size   | number | 每页记录数      |
| list   | array  | 客户列表        |

**客户列表项结构：**

| 参数名        | 类型   | 描述                       |
|--------------|--------|---------------------------|
| customerId   | number | 客户ID                     |
| name         | string | 客户名称                    |
| level        | string | 客户级别(A/B/C)             |
| contact      | string | 联系人                      |
| phone        | string | 联系电话                    |
| address      | string | 地址                        |
| relationType | string | 关系类型(engineer/sales)    |
| isCreator    | boolean| 是否为创建者                 |
| salesId      | number | 销售ID(可能为null)          |
| salesName    | string | 销售姓名(可能为null)         |
| engineerCount| number | 负责工程师数量               |

- **响应示例**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 3,
    "pages": 1,
    "current": 1,
    "size": 10,
    "list": [
      {
        "customerId": 1,
        "name": "上海医疗器械有限公司",
        "level": "A",
        "contact": "张经理",
        "phone": "13800138001",
        "address": "上海市浦东新区张江高科技园区",
        "relationType": "engineer",
        "isCreator": false,
        "salesId": 6,
        "salesName": "赵六",
        "engineerCount": 1
      },
      {
        "customerId": 3,
        "name": "广州医学检验中心",
        "level": "A",
        "contact": "王主任",
        "phone": "13800138003",
        "address": "广州市黄埔区科学城",
        "relationType": "admin",
        "isCreator": true,
        "salesId": null,
        "salesName": null,
        "engineerCount": 0
      }
    ]
  }
}
```

### 4. 获取客户详情

- **接口说明**：获取客户详情信息
- **请求方式**：GET
- **请求路径**：`/api/v1/contacts/customers/{customerId}`
- **请求参数**：

| 参数名     | 类型   | 位置 | 必填 | 描述    |
|-----------|--------|-----|-----|---------|
| customerId| number | path | 是  | 客户ID  |
| includeDevices | boolean | query | 否 | 是否包含设备列表，默认true |
| includeTasks | boolean | query | 否 | 是否包含任务列表，默认true |

- **响应参数**：

| 参数名      | 类型   | 描述                    |
|------------|--------|------------------------|
| customerId | number | 客户ID                  |
| name       | string | 客户名称                 |
| level      | string | 客户级别(A/B/C)          |
| contact    | string | 联系人                   |
| phone      | string | 联系电话                 |
| address    | string | 地址                     |
| email      | string | 邮箱                     |
| department | string | 部门                     |
| position   | string | 职位                     |
| relationType | string | 关系类型(engineer/sales/admin) |
| isCreator  | boolean| 是否为创建者              |
| sales      | object | 销售信息(可能为null)      |
| engineers  | array  | 工程师列表                |
| devices    | array  | 设备列表                  |
| tasks      | array  | 任务列表                  |

**销售信息结构：**

| 参数名   | 类型   | 描述      |
|---------|--------|----------|
| userId  | number | 用户ID    |
| workId  | string | 工号      |
| name    | string | 姓名      |
| mobile  | string | 手机号    |
| avatar  | string | 头像URL   |

**工程师列表项结构：**

| 参数名   | 类型   | 描述      |
|---------|--------|----------|
| userId  | number | 用户ID    |
| workId  | string | 工号      |
| name    | string | 姓名      |
| mobile  | string | 手机号    |
| avatar  | string | 头像URL   |
| isCreator | boolean | 是否为创建者 |

**设备列表项结构：**

| 参数名      | 类型   | 描述           |
|------------|--------|---------------|
| deviceId   | number | 设备ID        |
| deviceName | string | 设备名称       |
| deviceType | string | 设备类型       |
| brand      | string | 品牌          |
| model      | string | 型号          |
| serialNumber | string | 序列号      |

**任务列表项结构：**

| 参数名      | 类型   | 描述                |
|------------|--------|-------------------|
| taskId     | string | 任务ID             |
| title      | string | 任务标题            |
| taskType   | string | 任务类型            |
| status     | string | 任务状态            |
| priority   | string | 优先级              |
| deviceName | string | 设备名称            |
| startDate  | string | 开始日期            |
| deadline   | string | 截止日期            |
| engineerName | string | 负责工程师         |

- **响应示例**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "customerId": 1,
    "name": "上海医疗器械有限公司",
    "level": "A",
    "contact": "张经理",
    "phone": "13800138001",
    "address": "上海市浦东新区张江高科技园区",
    "email": "zhang@shylqx.com",
    "department": "设备部",
    "position": "经理",
    "relationType": "engineer",
    "isCreator": false,
    "sales": {
      "userId": 6,
      "workId": "RYL005",
      "name": "赵六",
      "mobile": "13900139006",
      "avatar": "https://example.com/avatars/zhaoliu.png"
    },
    "engineers": [
      {
        "userId": 2,
        "workId": "RYL001",
        "name": "张三",
        "mobile": "13900139001",
        "avatar": "https://example.com/avatars/zhangsan.png",
        "isCreator": false
      }
    ],
    "devices": [
      {
        "deviceId": 1,
        "deviceName": "气相色谱仪",
        "deviceType": "分析仪器",
        "brand": "Agilent",
        "model": "GC-2000",
        "serialNumber": "SN20240001"
      }
    ],
    "tasks": [
      {
        "taskId": "RP-2024-001",
        "title": "气相色谱仪维修",
        "taskType": "repair",
        "status": "in_progress",
        "priority": "high",
        "deviceName": "气相色谱仪",
        "startDate": "2024-06-08T09:00:00.000Z",
        "deadline": "2024-06-15T18:00:00.000Z",
        "engineerName": "张三"
      },
      {
        "taskId": "MT-2024-002",
        "title": "液相色谱仪保养",
        "taskType": "maintenance",
        "status": "pending",
        "priority": "normal",
        "deviceName": "液相色谱仪",
        "startDate": null,
        "deadline": "2024-07-10T18:00:00.000Z",
        "engineerName": "李四"
      }
    ]
  }
}
```

### 5. 新增客户

- **接口说明**：新增客户信息
- **请求方式**：POST
- **请求路径**：`/api/v1/contacts/customers`
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述           |
|-----------|--------|-----|---------------|
| name      | string | 是  | 客户名称       |
| level     | string | 是  | 客户级别(A/B/C) |
| contact   | string | 是  | 联系人         |
| phone     | string | 是  | 联系电话       |
| address   | string | 是  | 地址           |
| email     | string | 否  | 邮箱           |
| department| string | 否  | 部门           |
| position  | string | 否  | 职位           |
| salesId   | number | 否  | 销售ID（当前用户为工程师时必填） |
| engineerIds | array | 否  | 工程师ID列表（当前用户为销售时可选） |

- **请求示例**：

```json
{
  "name": "深圳科技有限公司",
  "level": "B",
  "contact": "赵总",
  "phone": "13900139004",
  "address": "深圳市南山区科技园",
  "email": "zhao@sztec.com",
  "department": "研发部",
  "position": "总监",
  "salesId": 6,
  "engineerIds": [2, 3]
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "customerId": 4,
    "name": "深圳科技有限公司"
  }
}
```

### 6. 修改客户信息

- **接口说明**：修改客户信息
- **请求方式**：PUT
- **请求路径**：`/api/v1/contacts/customers/{customerId}`
- **请求参数**：

| 参数名     | 类型   | 位置 | 必填 | 描述           |
|-----------|--------|-----|-----|---------------|
| customerId| number | path | 是  | 客户ID        |
| name      | string | body | 否  | 客户名称       |
| level     | string | body | 否  | 客户级别(A/B/C) |
| contact   | string | body | 否  | 联系人         |
| phone     | string | body | 否  | 联系电话       |
| address   | string | body | 否  | 地址           |
| email     | string | body | 否  | 邮箱           |
| department| string | body | 否  | 部门           |
| position  | string | body | 否  | 职位           |

- **请求示例**：

```json
{
  "level": "A",
  "contact": "赵总监",
  "phone": "13900139005"
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "修改成功",
  "data": null
}
```

### 7. 删除客户

- **接口说明**：删除客户信息
- **请求方式**：DELETE
- **请求路径**：`/api/v1/contacts/customers/{customerId}`
- **请求参数**：

| 参数名      | 类型   | 位置 | 必填 | 描述      |
|------------|--------|-----|-----|-----------|
| customerId | number | path | 是  | 客户ID    |
| password   | string | body | 是  | 确认密码  |

- **请求示例**：

```json
{
  "password": "123456"
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 8. 分配客户销售

- **接口说明**：分配客户对应的销售（仅系统管理员和工程师可操作）
- **请求方式**：POST
- **请求路径**：`/api/v1/contacts/customers/{customerId}/assign-sales`
- **请求参数**：

| 参数名      | 类型   | 位置 | 必填 | 描述      |
|------------|--------|-----|-----|-----------|
| customerId | number | path | 是  | 客户ID    |
| salesId    | number | body | 是  | 销售ID    |

- **请求示例**：

```json
{
  "salesId": 6
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "分配成功",
  "data": null
}
```

### 9. 分配客户工程师

- **接口说明**：分配客户对应的工程师（仅系统管理员和销售可操作）
- **请求方式**：POST
- **请求路径**：`/api/v1/contacts/customers/{customerId}/assign-engineers`
- **请求参数**：

| 参数名      | 类型   | 位置 | 必填 | 描述         |
|------------|--------|-----|-----|--------------|
| customerId | number | path | 是  | 客户ID       |
| engineerIds| array  | body | 是  | 工程师ID列表  |

- **请求示例**：

```json
{
  "engineerIds": [2, 3]
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "分配成功",
  "data": null
}
```

### 10. 移除客户工程师

- **接口说明**：移除客户对应的工程师（仅系统管理员和销售可操作）
- **请求方式**：POST
- **请求路径**：`/api/v1/contacts/customers/{customerId}/remove-engineer/{engineerId}`
- **请求参数**：

| 参数名      | 类型   | 位置 | 必填 | 描述      |
|------------|--------|-----|-----|-----------|
| customerId | number | path | 是  | 客户ID    |
| engineerId | number | path | 是  | 工程师ID  |

- **响应示例**：

```json
{
  "code": 200,
  "message": "移除成功",
  "data": null
}
```

### 11. 获取其他联系人列表

- **接口说明**：获取其他联系人列表（除工程师外的所有角色）
- **请求方式**：GET
- **请求路径**：`/api/v1/contacts/other`
- **请求参数**：

| 参数名   | 类型   | 位置  | 必填 | 描述           |
|---------|--------|------|-----|---------------|
| keyword | string | query | 否  | 搜索关键词      |

- **响应参数**：

| 参数名  | 类型   | 描述           |
|--------|--------|---------------|
| list   | array  | 联系人列表      |

**联系人列表项结构：**

| 参数名      | 类型   | 描述              |
|------------|--------|------------------|
| userId     | number | 用户ID            |
| workId     | string | 工号              |
| name       | string | 姓名              |
| avatar     | string | 头像URL           |
| department | string | 部门              |
| role       | string | 角色              |
| mobile     | string | 手机号            |

- **响应示例**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [
      {
        "userId": 1,
        "workId": "admin",
        "name": "系统管理员",
        "avatar": "https://example.com/avatars/admin.png",
        "department": "系统部",
        "role": "admin",
        "mobile": "13800138000"
      },
      {
        "userId": 6,
        "workId": "RYL005",
        "name": "赵六",
        "avatar": "https://example.com/avatars/zhaoliu.png",
        "department": "销售部",
        "role": "sales",
        "mobile": "13900139006"
      }
    ]
  }
}
```

### 12. 更新工程师状态

- **接口说明**：更新当前用户的状态
- **请求方式**：PUT
- **请求路径**：`/api/v1/contacts/engineers/status`
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述                |
|-----------|--------|-----|-------------------|
| status    | string | 是  | 状态(online/busy/offline) |

- **请求示例**：

```json
{
  "status": "busy"
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": null
}
```

### 11. 获取客户相关任务列表

- **接口说明**：获取客户相关的任务列表
- **请求方式**：GET
- **请求路径**：`/api/v1/contacts/customers/{customerId}/tasks`
- **请求参数**：

| 参数名      | 类型   | 位置  | 必填 | 描述           |
|------------|--------|------|-----|---------------|
| customerId | number | path | 是  | 客户ID         |
| page       | number | query | 否  | 页码，默认1     |
| size       | number | query | 否  | 每页数量，默认10 |
| status     | string | query | 否  | 任务状态        |
| taskType   | string | query | 否  | 任务类型        |

- **响应参数**：

| 参数名  | 类型   | 描述           |
|--------|--------|---------------|
| total  | number | 总记录数       |
| pages  | number | 总页数         |
| current| number | 当前页码       |
| size   | number | 每页记录数      |
| list   | array  | 任务列表        |

**任务列表项结构：**

| 参数名      | 类型   | 描述                |
|------------|--------|-------------------|
| taskId     | string | 任务ID             |
| title      | string | 任务标题            |
| taskType   | string | 任务类型            |
| status     | string | 任务状态            |
| priority   | string | 优先级              |
| deviceName | string | 设备名称            |
| deviceModel| string | 设备型号            |
| startDate  | string | 开始日期            |
| deadline   | string | 截止日期            |
| completedDate | string | 完成日期         |
| engineers  | array  | 负责工程师列表       |

**工程师列表项结构：**

| 参数名      | 类型   | 描述           |
|------------|--------|---------------|
| userId     | number | 用户ID        |
| name       | string | 姓名          |
| isOwner    | boolean| 是否为负责人   |

- **响应示例**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 5,
    "pages": 1,
    "current": 1,
    "size": 10,
    "list": [
      {
        "taskId": "RP-2024-001",
        "title": "气相色谱仪维修",
        "taskType": "repair",
        "status": "in_progress",
        "priority": "high",
        "deviceName": "气相色谱仪",
        "deviceModel": "GC-2000",
        "startDate": "2024-06-08T09:00:00.000Z",
        "deadline": "2024-06-15T18:00:00.000Z",
        "completedDate": null,
        "engineers": [
          {
            "userId": 2,
            "name": "张三",
            "isOwner": true
          },
          {
            "userId": 3,
            "name": "李四",
            "isOwner": false
          }
        ]
      },
      {
        "taskId": "MT-2024-002",
        "title": "液相色谱仪保养",
        "taskType": "maintenance",
        "status": "pending",
        "priority": "normal",
        "deviceName": "液相色谱仪",
        "deviceModel": "LC-3000",
        "startDate": null,
        "deadline": "2024-07-10T18:00:00.000Z",
        "completedDate": null,
        "engineers": [
          {
            "userId": 4,
            "name": "王五",
            "isOwner": true
          }
        ]
      }
    ]
  }
}
```

### 12. 获取客户相关设备列表

- **接口说明**：获取客户相关的设备列表
- **请求方式**：GET
- **请求路径**：`/api/v1/contacts/customers/{customerId}/devices`
- **请求参数**：

| 参数名      | 类型   | 位置  | 必填 | 描述           |
|------------|--------|------|-----|---------------|
| customerId | number | path | 是  | 客户ID         |
| page       | number | query | 否  | 页码，默认1     |
| size       | number | query | 否  | 每页数量，默认10 |
| keyword    | string | query | 否  | 搜索关键词      |

- **响应参数**：

| 参数名  | 类型   | 描述           |
|--------|--------|---------------|
| total  | number | 总记录数       |
| pages  | number | 总页数         |
| current| number | 当前页码       |
| size   | number | 每页记录数      |
| list   | array  | 设备列表        |

**设备列表项结构：**

| 参数名        | 类型   | 描述           |
|--------------|--------|---------------|
| deviceId     | number | 设备ID        |
| deviceName   | string | 设备名称       |
| deviceType   | string | 设备类型       |
| brand        | string | 品牌          |
| model        | string | 型号          |
| serialNumber | string | 序列号        |
| quantity     | number | 数量          |
| recentTasks  | array  | 最近任务列表    |

**最近任务项结构：**

| 参数名    | 类型   | 描述         |
|----------|--------|-------------|
| taskId   | string | 任务ID       |
| title    | string | 任务标题      |
| taskType | string | 任务类型      |
| status   | string | 任务状态      |

- **响应示例**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 3,
    "pages": 1,
    "current": 1,
    "size": 10,
    "list": [
      {
        "deviceId": 1,
        "deviceName": "气相色谱仪",
        "deviceType": "分析仪器",
        "brand": "Agilent",
        "model": "GC-2000",
        "serialNumber": "SN20240001",
        "quantity": 1,
        "recentTasks": [
          {
            "taskId": "RP-2024-001",
            "title": "气相色谱仪维修",
            "taskType": "repair",
            "status": "in_progress"
          }
        ]
      },
      {
        "deviceId": 2,
        "deviceName": "液相色谱仪",
        "deviceType": "分析仪器",
        "brand": "Waters",
        "model": "LC-3000",
        "serialNumber": "SN20240002",
        "quantity": 1,
        "recentTasks": [
          {
            "taskId": "MT-2024-002",
            "title": "液相色谱仪保养",
            "taskType": "maintenance",
            "status": "pending"
          }
        ]
      }
    ]
  }
}
``` 