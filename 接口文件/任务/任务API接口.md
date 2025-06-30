# 任务模块API文档

## 基础信息

- 基础路径: `/api/v1/task`
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

通用状态码:
- 200: 成功
- 400: 请求参数错误
- 401: 未授权
- 403: 权限不足
- 404: 资源不存在
- 500: 服务器内部错误

## 接口列表

### 1. 创建任务

- **接口说明**: 创建新任务
- **请求方式**: POST
- **请求路径**: `/api/v1/task/create`
- **请求参数**:

| 参数名                  | 类型    | 必填 | 描述                                        |
|------------------------|--------|------|-------------------------------------------|
| title                  | string | 是   | 任务标题                                     |
| taskType               | string | 是   | 任务类型(repair/maintenance/recycle/leasing/training/verification/selection/installation) |
| priority               | string | 否   | 优先级(low/normal/high)，默认normal           |
| startTime              | string | 是   | 开始时间(格式：YYYY-MM-DDTHH:mm:ss)            |
| endTime                | string | 是   | 结束时间(格式：YYYY-MM-DDTHH:mm:ss)            |
| isExternalTask         | boolean| 否   | 是否为系统外任务，默认false                      |
| customer               | object | 否   | 客户对象(系统外任务时可为空)                      |
| customer.id            | number | 否   | 客户ID(使用已有客户时必填)                       |
| customer.name          | string | 否   | 客户名称(新建客户时必填)                         |
| customer.level         | string | 否   | 客户等级(A/B/C)                              |
| customer.contact       | string | 否   | 联系人                                       |
| customer.phone         | string | 否   | 联系电话                                     |
| customer.address       | string | 否   | 地址                                        |
| sales                  | object | 否   | 销售人员对象                                  |
| sales.id               | number | 否   | 销售人员ID                                   |
| deviceName             | string | 条件  | 设备名称(除选型任务外必填)                       |
| deviceModel            | string | 否   | 设备型号                                     |
| deviceBrand            | string | 否   | 设备品牌                                     |
| description            | string | 否   | 任务描述                                     |
| faultDescription       | string | 条件  | 故障描述(维修和保养任务必填)                     |
| quantity               | number | 否   | 数量，默认1                                  |
| attachments            | string | 否   | 附件信息(回收和租赁任务)                        |
| verificationType       | string | 条件  | 验证类别(IQ/OQ/PQ)(验证任务必填)               |
| purpose                | string | 条件  | 用途(选型任务必填)                             |
| requirementDescription | string | 条件  | 需求描述(选型任务必填)                          |
| appointmentTime        | string | 条件  | 预约时间(培训任务必填，格式：YYYY-MM-DDTHH:mm:ss) |
| images                 | array  | 否   | 图片URL数组                                  |
| assignedEngineers      | array  | 是   | 指派的工程师ID数组                             |

- **响应参数**:

| 参数名  | 类型    | 描述                                   |
|--------|--------|----------------------------------------|
| taskId | string | 任务编号                                 |
| title  | string | 任务标题                                 |
| status | string | 任务状态(draft/pending/in-progress/completed/cancelled) |

- **请求示例**:

```json
{
  "title": "气相色谱仪维修",
  "taskType": "repair",
  "priority": "high",
  "startTime": "2024-05-20T09:00:00",
  "endTime": "2024-05-25T18:00:00",
  "isExternalTask": false,
  "customer": {
    "id": 1
  },
  "sales": {
    "id": 4
  },
  "deviceName": "气相色谱仪",
  "deviceModel": "GC-2000",
  "deviceBrand": "Agilent",
  "description": "客户反映设备无法正常启动",
  "faultDescription": "设备开机后报错代码E-231，无法进入系统",
  "quantity": 1,
  "images": [
    "https://example.com/images/gc-2000-1.jpg",
    "https://example.com/images/gc-2000-2.jpg"
  ],
  "assignedEngineers": [2]
}
```

- **响应示例**:

```json
{
  "code": 200,
  "message": "任务创建成功",
  "data": {
    "taskId": "RP-2024-005",
    "title": "气相色谱仪维修",
    "status": "pending"
  }
}
```

### 2. 获取任务列表

- **接口说明**: 获取当前用户可见的任务列表
- **请求方式**: GET
- **请求路径**: `/api/v1/task/list`
- **请求参数**:

| 参数名     | 类型    | 必填 | 描述                                      |
|-----------|--------|------|------------------------------------------|
| page      | number | 否   | 页码，默认1                                |
| size      | number | 否   | 每页记录数，默认10                          |
| status    | string | 否   | 任务状态(draft/pending/in-progress/completed/cancelled) |
| taskType  | string | 否   | 任务类型                                   |
| keyword   | string | 否   | 搜索关键词(任务标题/客户名称/设备名称)           |
| startDate | string | 否   | 开始日期(格式：YYYY-MM-DD)                  |
| endDate   | string | 否   | 结束日期(格式：YYYY-MM-DD)                  |
| priority  | string | 否   | 优先级(low/normal/high)                    |

- **响应参数**:

| 参数名       | 类型    | 描述                                   |
|-------------|--------|----------------------------------------|
| total       | number | 总记录数                                 |
| pages       | number | 总页数                                   |
| current     | number | 当前页码                                 |
| size        | number | 每页记录数                                |
| list        | array  | 任务列表                                 |
| list[].taskId | string | 任务编号                                 |
| list[].title | string | 任务标题                                 |
| list[].taskType | string | 任务类型                               |
| list[].customer | string | 客户名称                               |
| list[].location | string | 任务地点                               |
| list[].priority | string | 优先级                                 |
| list[].status | string | 任务状态                                |
| list[].deviceName | string | 设备名称                             |
| list[].createTime | string | 创建时间                             |
| list[].startDate | string | 开始日期                              |
| list[].deadline | string | 截止日期                               |
| list[].currentStep | number | 当前步骤                            |

- **响应示例**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 20,
    "pages": 2,
    "current": 1,
    "size": 10,
    "list": [
      {
        "taskId": "RP-2024-001",
        "title": "气相色谱仪维修",
        "taskType": "repair",
        "customer": "上海医疗器械有限公司",
        "location": "上海市浦东新区张江高科技园区",
        "priority": "high",
        "status": "in-progress",
        "deviceName": "气相色谱仪",
        "createTime": "2024-05-19T14:30:00",
        "startDate": "2024-05-20T09:00:00",
        "deadline": "2024-05-25T18:00:00",
        "currentStep": 2
      },
      // 更多任务...
    ]
  }
}
```

### 3. 获取任务详情

- **接口说明**: 获取任务详细信息
- **请求方式**: GET
- **请求路径**: `/api/v1/task/detail/{taskId}`
- **路径参数**:

| 参数名  | 类型    | 必填 | 描述                      |
|--------|--------|------|--------------------------|
| taskId | string | 是   | 任务编号                   |

- **响应参数**:

| 参数名                | 类型    | 描述                                      |
|----------------------|--------|-----------------------------------------|
| taskId               | string | 任务编号                                   |
| title                | string | 任务标题                                   |
| taskType             | string | 任务类型                                   |
| customer             | string | 客户名称                                   |
| customerId           | number | 客户ID                                    |
| customerLevel        | string | 客户级别                                   |
| location             | string | 地址                                      |
| contactPerson        | string | 联系人                                    |
| contactPhone         | string | 联系电话                                  |
| priority             | string | 优先级                                    |
| status               | string | 任务状态                                  |
| statusText           | string | 任务状态文本                               |
| isExternalTask       | boolean| 是否系统外任务                              |
| createTime           | string | 创建时间                                  |
| startDate            | string | 开始日期                                  |
| deadline             | string | 截止日期                                  |
| completedDate        | string | 完成时间                                  |
| deviceName           | string | 设备名称                                  |
| deviceModel          | string | 设备型号                                  |
| deviceBrand          | string | 设备品牌                                  |
| deviceSN             | string | 设备序列号                                 |
| description          | string | 描述                                     |
| faultDescription     | string | 故障描述                                  |
| quantity             | number | 数量                                     |
| attachments          | string | 附件信息                                  |
| verificationType     | string | 验证类别                                  |
| purpose              | string | 用途                                     |
| requirements         | string | 需求描述                                  |
| appointmentTime      | string | 预约时间                                  |
| currentStep          | number | 当前步骤                                  |
| images               | array  | 任务图片URL数组                            |
| engineers            | array  | 工程师列表                                 |
| engineers[].id       | number | 工程师ID                                  |
| engineers[].name     | string | 工程师姓名                                 |
| engineers[].avatar   | string | 工程师头像                                 |
| engineers[].department | string | 部门                                    |
| engineers[].mobile   | string | 手机号                                    |
| engineers[].isOwner  | boolean| 是否任务负责人                              |
| engineers[].online   | boolean| 是否在线                                  |

- **响应示例**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "taskId": "RP-2024-001",
    "title": "气相色谱仪维修",
    "taskType": "repair",
    "customer": "上海医疗器械有限公司",
    "customerId": 1,
    "customerLevel": "A",
    "location": "上海市浦东新区张江高科技园区",
    "contactPerson": "张经理",
    "contactPhone": "13800138001",
    "priority": "high",
    "status": "in-progress",
    "statusText": "进行中",
    "isExternalTask": false,
    "createTime": "2024-05-19T14:30:00",
    "startDate": "2024-05-20T09:00:00",
    "deadline": "2024-05-25T18:00:00",
    "deviceName": "气相色谱仪",
    "deviceModel": "GC-2000",
    "deviceBrand": "Agilent",
    "deviceSN": "GC2000-A12345",
    "description": "客户反映设备无法正常启动",
    "faultDescription": "设备开机后报错代码E-231，无法进入系统",
    "quantity": 1,
    "currentStep": 2,
    "images": [
      "https://example.com/images/RP-2024-001/img1.jpg",
      "https://example.com/images/RP-2024-001/img2.jpg"
    ],
    "engineers": [
      {
        "id": 2,
        "name": "张三",
        "avatar": "https://example.com/avatars/zhangsan.png",
        "department": "技术部",
        "mobile": "13900139001",
        "isOwner": true,
        "online": true
      }
    ]
  }
}
``` 
# 任务模块API文档（续）

## 任务流程和操作相关接口

### 4. 获取任务流程详情

- **接口说明**: 获取任务的流程和进度详情
- **请求方式**: GET
- **请求路径**: `/api/v1/task/flow/{taskId}`
- **路径参数**:

| 参数名  | 类型    | 必填 | 描述                      |
|--------|--------|------|--------------------------|
| taskId | string | 是   | 任务编号                   |

- **响应参数**:

| 参数名            | 类型    | 描述                                         |
|------------------|--------|--------------------------------------------|
| taskId           | string | 任务编号                                      |
| title            | string | 任务标题                                      |
| taskType         | string | 任务类型                                      |
| status           | string | 任务状态                                      |
| currentStep      | number | 当前步骤                                      |
| totalSteps       | number | 总步骤数                                      |
| steps            | array  | 步骤数组                                      |
| steps[].index    | number | 步骤索引                                      |
| steps[].name     | string | 步骤名称                                      |
| steps[].status   | string | 步骤状态(waiting/processing/completed/skipped)|
| steps[].startTime| string | 步骤开始时间                                   |
| steps[].endTime  | string | 步骤完成时间                                   |
| steps[].operator | object | 操作人信息                                    |
| steps[].operator.id | number | 操作人ID                                  |
| steps[].operator.name | string | 操作人姓名                                |
| steps[].operator.avatar | string | 操作人头像                              |
| steps[].description | string | 步骤描述                                   |
| steps[].result   | string | 步骤结果                                      |
| steps[].images   | array  | 步骤相关图片URL数组                             |
| steps[].formData | object | 步骤表单数据                                   |
| steps[].actions  | array  | 可执行操作                                    |
| timeline         | array  | 任务时间线                                    |
| timeline[].time  | string | 时间                                         |
| timeline[].action| string | 操作                                         |
| timeline[].operator | string | 操作人                                     |
| timeline[].content | string | 内容                                        |

- **响应示例**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "taskId": "RP-2024-001",
    "title": "气相色谱仪维修",
    "taskType": "repair",
    "status": "in-progress",
    "currentStep": 2,
    "totalSteps": 5,
    "steps": [
      {
        "index": 1,
        "name": "接单确认",
        "status": "completed",
        "startTime": "2024-05-20T09:10:00",
        "endTime": "2024-05-20T09:15:00",
        "operator": {
          "id": 2,
          "name": "张三",
          "avatar": "https://example.com/avatars/zhangsan.png"
        },
        "description": "已接单并确认任务",
        "result": "已到达客户现场",
        "images": [],
        "formData": {},
        "actions": []
      },
      {
        "index": 2,
        "name": "故障诊断",
        "status": "processing",
        "startTime": "2024-05-20T10:00:00",
        "endTime": null,
        "operator": {
          "id": 2,
          "name": "张三",
          "avatar": "https://example.com/avatars/zhangsan.png"
        },
        "description": "正在进行故障诊断",
        "result": "",
        "images": [],
        "formData": {},
        "actions": ["complete", "upload"]
      },
      {
        "index": 3,
        "name": "维修处理",
        "status": "waiting",
        "startTime": null,
        "endTime": null,
        "operator": null,
        "description": "",
        "result": "",
        "images": [],
        "formData": {},
        "actions": []
      },
      {
        "index": 4,
        "name": "设备验证",
        "status": "waiting",
        "startTime": null,
        "endTime": null,
        "operator": null,
        "description": "",
        "result": "",
        "images": [],
        "formData": {},
        "actions": []
      },
      {
        "index": 5,
        "name": "任务结束",
        "status": "waiting",
        "startTime": null,
        "endTime": null,
        "operator": null,
        "description": "",
        "result": "",
        "images": [],
        "formData": {},
        "actions": []
      }
    ],
    "timeline": [
      {
        "time": "2024-05-19T14:30:00",
        "action": "创建任务",
        "operator": "李四",
        "content": "创建了任务"
      },
      {
        "time": "2024-05-19T14:35:00",
        "action": "指派工程师",
        "operator": "李四",
        "content": "指派给张三"
      },
      {
        "time": "2024-05-20T09:10:00",
        "action": "开始任务",
        "operator": "张三",
        "content": "接受任务并开始"
      },
      {
        "time": "2024-05-20T09:15:00",
        "action": "完成步骤",
        "operator": "张三",
        "content": "完成步骤：接单确认"
      },
      {
        "time": "2024-05-20T10:00:00",
        "action": "开始步骤",
        "operator": "张三",
        "content": "开始步骤：故障诊断"
      }
    ]
  }
}
```

### 5. 更新任务步骤

- **接口说明**: 更新任务步骤状态和信息
- **请求方式**: POST
- **请求路径**: `/api/v1/task/step/update`
- **请求参数**:

| 参数名         | 类型    | 必填 | 描述                                         |
|---------------|--------|------|-------------------------------------------|
| taskId        | string | 是   | 任务编号                                      |
| stepIndex     | number | 是   | 步骤索引                                      |
| action        | string | 是   | 操作类型(start/complete/cancel/skip)          |
| description   | string | 否   | 步骤描述                                      |
| result        | string | 否   | 步骤结果                                      |
| images        | array  | 否   | 步骤相关图片URL数组                             |
| formData      | object | 否   | 步骤表单数据                                   |

- **响应参数**:

| 参数名            | 类型    | 描述                                         |
|------------------|--------|--------------------------------------------|
| taskId           | string | 任务编号                                      |
| currentStep      | number | 当前步骤                                      |
| status           | string | 任务状态                                      |
| stepStatus       | string | 步骤状态                                      |

- **请求示例**:

```json
{
  "taskId": "RP-2024-001",
  "stepIndex": 2,
  "action": "complete",
  "description": "完成故障诊断",
  "result": "发现主板电源供应不稳定，需要更换电源模块",
  "images": [
    "https://example.com/images/RP-2024-001/step2-1.jpg",
    "https://example.com/images/RP-2024-001/step2-2.jpg"
  ],
  "formData": {
    "faultComponents": ["电源模块", "电源接口"],
    "diagnosticMethod": "电压测量",
    "voltage": "不稳定",
    "requiredParts": ["GC-2000电源模块"]
  }
}
```

- **响应示例**:

```json
{
  "code": 200,
  "message": "步骤更新成功",
  "data": {
    "taskId": "RP-2024-001",
    "currentStep": 3,
    "status": "in-progress",
    "stepStatus": "completed"
  }
}
```

### 6. 上传任务相关图片

- **接口说明**: 上传任务相关图片
- **请求方式**: POST
- **请求路径**: `/api/v1/task/upload/image`
- **请求参数**: 

| 参数名     | 类型    | 必填 | 描述                                |
|-----------|--------|------|------------------------------------|
| taskId    | string | 是   | 任务编号                             |
| stepIndex | number | 否   | 步骤索引，不传则关联到整个任务          |
| file      | file   | 是   | 图片文件(支持jpg, jpeg, png, gif)    |

- **响应参数**:

| 参数名  | 类型    | 描述                                |
|--------|--------|------------------------------------|
| url    | string | 图片URL                             |
| name   | string | 图片名称                             |
| size   | number | 图片大小(字节)                       |

- **响应示例**:

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "https://example.com/images/RP-2024-001/step2-3.jpg",
    "name": "故障图片.jpg",
    "size": 1024000
  }
}
```

### 7. 分配/重新分配任务

- **接口说明**: 分配或重新分配任务给工程师
- **请求方式**: POST
- **请求路径**: `/api/v1/task/assign`
- **请求参数**:

| 参数名           | 类型    | 必填 | 描述                                |
|-----------------|--------|------|------------------------------------|
| taskId          | string | 是   | 任务编号                             |
| engineerIds     | array  | 是   | 工程师ID数组                         |
| mainEngineerId  | number | 是   | 主负责工程师ID                       |
| reason          | string | 否   | 分配/重新分配原因                     |

- **响应参数**:

| 参数名       | 类型    | 描述                                |
|-------------|--------|------------------------------------|
| taskId      | string | 任务编号                             |
| status      | string | 任务状态                             |
| engineers   | array  | 工程师列表                           |

- **请求示例**:

```json
{
  "taskId": "RP-2024-001",
  "engineerIds": [2, 5],
  "mainEngineerId": 2,
  "reason": "添加一名辅助工程师协助处理"
}
```

- **响应示例**:

```json
{
  "code": 200,
  "message": "任务分配成功",
  "data": {
    "taskId": "RP-2024-001",
    "status": "in-progress",
    "engineers": [
      {
        "id": 2,
        "name": "张三",
        "isOwner": true
      },
      {
        "id": 5,
        "name": "王五",
        "isOwner": false
      }
    ]
  }
}
```

### 8. 取消任务

- **接口说明**: 取消正在进行的任务
- **请求方式**: POST
- **请求路径**: `/api/v1/task/cancel`
- **请求参数**:

| 参数名     | 类型    | 必填 | 描述                                |
|-----------|--------|------|------------------------------------|
| taskId    | string | 是   | 任务编号                             |
| reason    | string | 是   | 取消原因                             |
| remark    | string | 否   | 备注                                |

- **响应参数**:

| 参数名    | 类型    | 描述                                |
|----------|--------|------------------------------------|
| taskId   | string | 任务编号                             |
| status   | string | 任务状态                             |

- **请求示例**:

```json
{
  "taskId": "RP-2024-001",
  "reason": "客户要求取消",
  "remark": "客户决定更换新设备，不再维修"
}
```

- **响应示例**:

```json
{
  "code": 200,
  "message": "任务已取消",
  "data": {
    "taskId": "RP-2024-001",
    "status": "cancelled"
  }
}
```

### 9. 添加任务日志

- **接口说明**: 为任务添加日志记录
- **请求方式**: POST
- **请求路径**: `/api/v1/task/log/add`
- **请求参数**:

| 参数名     | 类型    | 必填 | 描述                                |
|-----------|--------|------|------------------------------------|
| taskId    | string | 是   | 任务编号                             |
| content   | string | 是   | 日志内容                             |
| type      | string | 否   | 日志类型(note/warning/issue)，默认note |
| images    | array  | 否   | 相关图片URL数组                       |

- **响应参数**:

| 参数名    | 类型    | 描述                                |
|----------|--------|------------------------------------|
| id       | number | 日志ID                              |
| time     | string | 记录时间                             |

- **请求示例**:

```json
{
  "taskId": "RP-2024-001",
  "content": "拆卸设备后发现内部有灰尘积累，已清理",
  "type": "note",
  "images": [
    "https://example.com/images/RP-2024-001/log1.jpg"
  ]
}
```

- **响应示例**:

```json
{
  "code": 200,
  "message": "日志添加成功",
  "data": {
    "id": 12,
    "time": "2024-05-20T14:30:00"
  }
}
```

### 10. 获取任务日志列表

- **接口说明**: 获取任务相关的日志记录
- **请求方式**: GET
- **请求路径**: `/api/v1/task/log/list/{taskId}`
- **路径参数**:

| 参数名  | 类型    | 必填 | 描述                      |
|--------|--------|------|--------------------------|
| taskId | string | 是   | 任务编号                   |

- **请求参数**:

| 参数名  | 类型    | 必填 | 描述                            |
|--------|--------|------|--------------------------------|
| page   | number | 否   | 页码，默认1                      |
| size   | number | 否   | 每页记录数，默认20                |
| type   | string | 否   | 日志类型(note/warning/issue)     |

- **响应参数**:

| 参数名             | 类型    | 描述                                |
|-------------------|--------|------------------------------------|
| total             | number | 总记录数                             |
| pages             | number | 总页数                               |
| current           | number | 当前页码                             |
| size              | number | 每页记录数                           |
| list              | array  | 日志列表                             |
| list[].id         | number | 日志ID                              |
| list[].time       | string | 记录时间                             |
| list[].content    | string | 日志内容                             |
| list[].type       | string | 日志类型                             |
| list[].images     | array  | 相关图片URL数组                       |
| list[].user       | object | 记录用户                             |
| list[].user.id    | number | 用户ID                              |
| list[].user.name  | string | 用户姓名                             |
| list[].user.avatar| string | 用户头像                             |

- **响应示例**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 5,
    "pages": 1,
    "current": 1,
    "size": 20,
    "list": [
      {
        "id": 12,
        "time": "2024-05-20T14:30:00",
        "content": "拆卸设备后发现内部有灰尘积累，已清理",
        "type": "note",
        "images": [
          "https://example.com/images/RP-2024-001/log1.jpg"
        ],
        "user": {
          "id": 2,
          "name": "张三",
          "avatar": "https://example.com/avatars/zhangsan.png"
        }
      },
      {
        "id": 11,
        "time": "2024-05-20T13:15:00",
        "content": "设备主板电源接口松动，已重新固定",
        "type": "issue",
        "images": [
          "https://example.com/images/RP-2024-001/log2.jpg"
        ],
        "user": {
          "id": 2,
          "name": "张三",
          "avatar": "https://example.com/avatars/zhangsan.png"
        }
      },
      // 更多日志...
    ]
  }
}
``` 
# 任务模块API文档（续）

## 统计和报表相关接口

### 11. 获取任务统计信息

- **接口说明**: 获取任务统计数据
- **请求方式**: GET
- **请求路径**: `/api/v1/task/statistics`
- **请求参数**:

| 参数名     | 类型    | 必填 | 描述                                         |
|-----------|--------|------|-------------------------------------------|
| startDate | string | 否   | 开始日期(格式：YYYY-MM-DD)，默认30天前           |
| endDate   | string | 否   | 结束日期(格式：YYYY-MM-DD)，默认当前日期          |
| type      | string | 否   | 统计类型(daily/weekly/monthly)，默认daily      |

- **响应参数**:

| 参数名                  | 类型    | 描述                                      |
|------------------------|--------|-----------------------------------------|
| overview               | object | 总览数据                                   |
| overview.total         | number | 总任务数                                   |
| overview.completed     | number | 已完成任务数                                |
| overview.inProgress    | number | 进行中任务数                                |
| overview.pending       | number | 待处理任务数                                |
| overview.cancelled     | number | 已取消任务数                                |
| overview.completionRate| number | 完成率                                    |
| overview.avgDuration   | number | 平均处理时长(小时)                           |
| typeDistribution       | array  | 任务类型分布                                |
| typeDistribution[].type| string | 任务类型                                   |
| typeDistribution[].count| number| 数量                                      |
| typeDistribution[].percent| number| 百分比                                   |
| trend                  | array  | 趋势数据                                   |
| trend[].date           | string | 日期                                      |
| trend[].new            | number | 新增任务数                                 |
| trend[].completed      | number | 完成任务数                                 |
| priorityDistribution   | array  | 优先级分布                                 |
| priorityDistribution[].priority| string | 优先级                             |
| priorityDistribution[].count| number | 数量                                 |

- **响应示例**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "overview": {
      "total": 150,
      "completed": 105,
      "inProgress": 30,
      "pending": 10,
      "cancelled": 5,
      "completionRate": 0.7,
      "avgDuration": 24.5
    },
    "typeDistribution": [
      {
        "type": "repair",
        "count": 60,
        "percent": 0.4
      },
      {
        "type": "maintenance",
        "count": 45,
        "percent": 0.3
      },
      {
        "type": "installation",
        "count": 20,
        "percent": 0.133
      },
      {
        "type": "training",
        "count": 15,
        "percent": 0.1
      },
      {
        "type": "verification",
        "count": 10,
        "percent": 0.067
      }
    ],
    "trend": [
      {
        "date": "2024-05-01",
        "new": 5,
        "completed": 4
      },
      {
        "date": "2024-05-02",
        "new": 7,
        "completed": 6
      },
      // 更多日期...
    ],
    "priorityDistribution": [
      {
        "priority": "high",
        "count": 45
      },
      {
        "priority": "normal",
        "count": 95
      },
      {
        "priority": "low",
        "count": 10
      }
    ]
  }
}
```

### 12. 获取工程师工作量统计

- **接口说明**: 获取工程师工作量统计数据
- **请求方式**: GET
- **请求路径**: `/api/v1/task/statistics/engineer`
- **请求参数**:

| 参数名     | 类型    | 必填 | 描述                                          |
|-----------|--------|------|----------------------------------------------|
| startDate | string | 否   | 开始日期(格式：YYYY-MM-DD)，默认30天前            |
| endDate   | string | 否   | 结束日期(格式：YYYY-MM-DD)，默认当前日期           |
| engineerId| number | 否   | 工程师ID，不传则返回所有工程师数据                  |

- **响应参数**:

| 参数名                       | 类型    | 描述                                      |
|-----------------------------|--------|-----------------------------------------|
| engineers                   | array  | 工程师数组                                 |
| engineers[].id              | number | 工程师ID                                  |
| engineers[].name            | string | 工程师姓名                                 |
| engineers[].avatar          | string | 工程师头像                                 |
| engineers[].department      | string | 部门                                      |
| engineers[].taskCount       | number | 任务总数                                   |
| engineers[].completedCount  | number | 已完成任务数                                |
| engineers[].pendingCount    | number | 待处理任务数                                |
| engineers[].inProgressCount | number | 进行中任务数                                |
| engineers[].avgDuration     | number | 平均处理时长(小时)                           |
| engineers[].satisfaction    | number | 客户满意度(1-5)                            |
| engineers[].taskTypes       | array  | 任务类型统计                                |
| engineers[].taskTypes[].type| string | 任务类型                                   |
| engineers[].taskTypes[].count| number| 数量                                      |

- **响应示例**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "engineers": [
      {
        "id": 2,
        "name": "张三",
        "avatar": "https://example.com/avatars/zhangsan.png",
        "department": "技术部",
        "taskCount": 35,
        "completedCount": 28,
        "pendingCount": 2,
        "inProgressCount": 5,
        "avgDuration": 22.5,
        "satisfaction": 4.8,
        "taskTypes": [
          {
            "type": "repair",
            "count": 20
          },
          {
            "type": "maintenance",
            "count": 10
          },
          {
            "type": "installation",
            "count": 5
          }
        ]
      },
      {
        "id": 5,
        "name": "王五",
        "avatar": "https://example.com/avatars/wangwu.png",
        "department": "技术部",
        "taskCount": 30,
        "completedCount": 25,
        "pendingCount": 1,
        "inProgressCount": 4,
        "avgDuration": 24.2,
        "satisfaction": 4.5,
        "taskTypes": [
          {
            "type": "repair",
            "count": 15
          },
          {
            "type": "maintenance",
            "count": 12
          },
          {
            "type": "verification",
            "count": 3
          }
        ]
      }
    ]
  }
}
```

### 13. 获取客户相关任务统计

- **接口说明**: 获取客户相关任务统计数据
- **请求方式**: GET
- **请求路径**: `/api/v1/task/statistics/customer`
- **请求参数**:

| 参数名     | 类型    | 必填 | 描述                                          |
|-----------|--------|------|----------------------------------------------|
| startDate | string | 否   | 开始日期(格式：YYYY-MM-DD)，默认30天前            |
| endDate   | string | 否   | 结束日期(格式：YYYY-MM-DD)，默认当前日期           |
| customerId| number | 否   | 客户ID，不传则返回所有客户数据                      |

- **响应参数**:

| 参数名                        | 类型    | 描述                                      |
|------------------------------|--------|-----------------------------------------|
| customers                    | array  | 客户数组                                   |
| customers[].id               | number | 客户ID                                    |
| customers[].name             | string | 客户名称                                   |
| customers[].level            | string | 客户级别                                   |
| customers[].taskCount        | number | 任务总数                                   |
| customers[].completedCount   | number | 已完成任务数                                |
| customers[].pendingCount     | number | 待处理任务数                                |
| customers[].inProgressCount  | number | 进行中任务数                                |
| customers[].cancelledCount   | number | 已取消任务数                                |
| customers[].avgResponseTime  | number | 平均响应时间(小时)                           |
| customers[].avgCompletionTime| number | 平均完成时间(小时)                           |
| customers[].taskTypes        | array  | 任务类型统计                                |
| customers[].taskTypes[].type | string | 任务类型                                   |
| customers[].taskTypes[].count| number | 数量                                      |

- **响应示例**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "customers": [
      {
        "id": 1,
        "name": "上海医疗器械有限公司",
        "level": "A",
        "taskCount": 25,
        "completedCount": 20,
        "pendingCount": 1,
        "inProgressCount": 4,
        "cancelledCount": 0,
        "avgResponseTime": 2.5,
        "avgCompletionTime": 26.8,
        "taskTypes": [
          {
            "type": "repair",
            "count": 12
          },
          {
            "type": "maintenance",
            "count": 8
          },
          {
            "type": "verification",
            "count": 5
          }
        ]
      },
      {
        "id": 2,
        "name": "北京科研设备有限公司",
        "level": "B",
        "taskCount": 18,
        "completedCount": 15,
        "pendingCount": 0,
        "inProgressCount": 2,
        "cancelledCount": 1,
        "avgResponseTime": 3.2,
        "avgCompletionTime": 28.5,
        "taskTypes": [
          {
            "type": "repair",
            "count": 8
          },
          {
            "type": "maintenance",
            "count": 6
          },
          {
            "type": "installation",
            "count": 4
          }
        ]
      }
    ]
  }
}
```

### 14. 获取设备故障统计

- **接口说明**: 获取设备故障统计数据
- **请求方式**: GET
- **请求路径**: `/api/v1/task/statistics/fault`
- **请求参数**:

| 参数名     | 类型    | 必填 | 描述                                          |
|-----------|--------|------|----------------------------------------------|
| startDate | string | 否   | 开始日期(格式：YYYY-MM-DD)，默认30天前            |
| endDate   | string | 否   | 结束日期(格式：YYYY-MM-DD)，默认当前日期           |
| deviceType| string | 否   | 设备类型，不传则返回所有设备类型                    |

- **响应参数**:

| 参数名                           | 类型    | 描述                                      |
|--------------------------------|--------|-----------------------------------------|
| totalFaults                    | number | 故障总数                                   |
| deviceTypes                    | array  | 设备类型故障统计                             |
| deviceTypes[].type             | string | 设备类型                                   |
| deviceTypes[].count            | number | 故障数量                                   |
| deviceTypes[].percent          | number | 百分比                                     |
| brandDistribution              | array  | 品牌故障分布                                |
| brandDistribution[].brand      | string | 设备品牌                                   |
| brandDistribution[].count      | number | 故障数量                                   |
| faultTypes                     | array  | 故障类型统计                                |
| faultTypes[].type              | string | 故障类型                                   |
| faultTypes[].count             | number | 数量                                      |
| faultTypes[].percent           | number | 百分比                                     |
| avgRepairTime                  | object | 平均维修时间统计                             |
| avgRepairTime.overall          | number | 总体平均维修时间(小时)                        |
| avgRepairTime.byDeviceType     | array  | 按设备类型统计的平均维修时间                    |
| avgRepairTime.byDeviceType[].type | string | 设备类型                                 |
| avgRepairTime.byDeviceType[].time | number | 平均维修时间(小时)                         |

- **响应示例**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "totalFaults": 85,
    "deviceTypes": [
      {
        "type": "气相色谱仪",
        "count": 22,
        "percent": 0.259
      },
      {
        "type": "液相色谱仪",
        "count": 18,
        "percent": 0.212
      },
      {
        "type": "质谱仪",
        "count": 15,
        "percent": 0.176
      },
      {
        "type": "光谱仪",
        "count": 12,
        "percent": 0.141
      },
      {
        "type": "其他",
        "count": 18,
        "percent": 0.212
      }
    ],
    "brandDistribution": [
      {
        "brand": "Agilent",
        "count": 28
      },
      {
        "brand": "Thermo Fisher",
        "count": 22
      },
      {
        "brand": "Shimadzu",
        "count": 15
      },
      {
        "brand": "Waters",
        "count": 12
      },
      {
        "brand": "其他",
        "count": 8
      }
    ],
    "faultTypes": [
      {
        "type": "电源故障",
        "count": 25,
        "percent": 0.294
      },
      {
        "type": "软件问题",
        "count": 18,
        "percent": 0.212
      },
      {
        "type": "机械部件损坏",
        "count": 16,
        "percent": 0.188
      },
      {
        "type": "校准偏差",
        "count": 14,
        "percent": 0.165
      },
      {
        "type": "其他故障",
        "count": 12,
        "percent": 0.141
      }
    ],
    "avgRepairTime": {
      "overall": 18.5,
      "byDeviceType": [
        {
          "type": "气相色谱仪",
          "time": 22.3
        },
        {
          "type": "液相色谱仪",
          "time": 20.1
        },
        {
          "type": "质谱仪",
          "time": 24.5
        },
        {
          "type": "光谱仪",
          "time": 16.8
        }
      ]
    }
  }
}
```

### 15. 导出任务报表

- **接口说明**: 导出任务数据报表
- **请求方式**: GET
- **请求路径**: `/api/v1/task/export`
- **请求参数**:

| 参数名     | 类型    | 必填 | 描述                                          |
|-----------|--------|------|----------------------------------------------|
| startDate | string | 否   | 开始日期(格式：YYYY-MM-DD)，默认30天前            |
| endDate   | string | 否   | 结束日期(格式：YYYY-MM-DD)，默认当前日期           |
| taskType  | string | 否   | 任务类型，不传则包含所有类型                       |
| status    | string | 否   | 任务状态，不传则包含所有状态                       |
| format    | string | 否   | 导出格式(excel/csv/pdf)，默认excel               |

- **响应参数**:

| 参数名      | 类型    | 描述                                |
|------------|--------|------------------------------------|
| url        | string | 报表下载URL                          |
| expires    | string | URL过期时间                          |
| format     | string | 报表格式                             |
| recordCount| number | 记录数量                             |

- **响应示例**:

```json
{
  "code": 200,
  "message": "报表生成成功",
  "data": {
    "url": "https://example.com/reports/task-report-20240601-123456.xlsx",
    "expires": "2024-06-01T14:30:00",
    "format": "excel",
    "recordCount": 150
  }
}
``` 