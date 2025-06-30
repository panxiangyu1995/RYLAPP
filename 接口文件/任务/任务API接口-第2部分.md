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