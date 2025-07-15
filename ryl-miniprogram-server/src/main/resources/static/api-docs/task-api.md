# 瑞屹林微信小程序 API 文档

## 任务相关接口

### 1. 获取任务列表

**请求路径**：`GET /task/list`

**请求参数**：无

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "title": "设备维修",
      "content": "设备需要维修",
      "taskType": "repair",
      "status": 0,
      "priority": 1,
      "startTime": "2023-06-01T10:00:00",
      "endTime": "2023-06-02T10:00:00",
      "customerId": 100,
      "createUserId": 1,
      "assigneeId": 2,
      "createTime": "2023-06-01T09:00:00",
      "updateTime": "2023-06-01T09:00:00",
      "remark": "紧急处理"
    }
  ]
}
```

### 2. 获取任务详情

**请求路径**：`GET /task/{id}`

**路径参数**：
- `id`：任务ID

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "title": "设备维修",
    "content": "设备需要维修",
    "taskType": "repair",
    "status": 0,
    "priority": 1,
    "startTime": "2023-06-01T10:00:00",
    "endTime": "2023-06-02T10:00:00",
    "customerId": 100,
    "createUserId": 1,
    "assigneeId": 2,
    "createTime": "2023-06-01T09:00:00",
    "updateTime": "2023-06-01T09:00:00",
    "remark": "紧急处理"
  }
}
```

### 3. 创建任务

**请求路径**：`POST /task/create`

**请求体**：
```json
{
  "title": "设备维修",
  "content": "设备需要维修",
  "taskType": "repair",
  "priority": 1,
  "startTime": "2023-06-01T10:00:00",
  "endTime": "2023-06-02T10:00:00",
  "remark": "紧急处理",
  "device": {
    "name": "分光光度计",
    "type": "UV-1800",
    "brand": "岛津",
    "model": "UV-1800",
    "faultDescription": "无法开机",
    "quantity": 1,
    "accessories": "电源线",
    "appointmentTime": "2023-06-01T14:00:00",
    "verificationType": "性能验证",
    "purpose": "实验室使用",
    "requirementDescription": "需要尽快修复"
  },
  "customer": {
    "id": 100,
    "customerName": "测试客户",
    "contactPerson": "张三",
    "contactPhone": "13800138000",
    "email": "test@example.com",
    "address": "北京市海淀区",
    "openid": "wx123456",
    "nickname": "小张",
    "avatarUrl": "https://example.com/avatar.jpg"
  },
  "description": "设备故障描述"
}
```

**响应示例**：
```json
{
  "code": 0,
  "message": "创建成功",
  "data": 1
}
```

### 4. 更新任务

**请求路径**：`POST /task/update`

**请求体**：
```json
{
  "id": 1,
  "title": "设备维修-更新",
  "content": "设备需要维修-更新",
  "taskType": "repair",
  "priority": 2,
  "startTime": "2023-06-01T10:00:00",
  "endTime": "2023-06-03T10:00:00",
  "remark": "紧急处理-更新"
}
```

**响应示例**：
```json
{
  "code": 0,
  "message": "更新成功",
  "data": null
}
```

### 5. 更新任务状态

**请求路径**：`POST /task/status/{id}/{status}`

**路径参数**：
- `id`：任务ID
- `status`：任务状态（0：未开始，1：进行中，2：已完成，3：已取消）

**响应示例**：
```json
{
  "code": 0,
  "message": "更新成功",
  "data": null
}
```

### 6. 获取任务步骤

**请求路径**：`GET /task/{id}/steps`

**路径参数**：
- `id`：任务ID

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "taskId": 1,
      "flowId": 1,
      "stepIndex": 0,
      "stepName": "创建任务",
      "stepDescription": "任务已创建，等待处理",
      "operatorId": 100,
      "operatorType": 1,
      "operatorName": "张三",
      "remark": "客户提交任务",
      "createTime": "2023-06-01T09:00:00"
    },
    {
      "id": 2,
      "taskId": 1,
      "flowId": 1,
      "stepIndex": 1,
      "stepName": "接收任务",
      "stepDescription": "任务已接收，正在处理",
      "operatorId": 1,
      "operatorType": 0,
      "operatorName": "客服",
      "remark": "已安排工程师",
      "createTime": "2023-06-01T10:00:00"
    }
  ]
}
```

### 7. 更新任务步骤

**请求路径**：`POST /task/{id}/step/{stepIndex}`

**路径参数**：
- `id`：任务ID
- `stepIndex`：步骤索引

**请求体**：
```json
{
  "remark": "处理中，预计明天完成"
}
```

**响应示例**：
```json
{
  "code": 0,
  "message": "更新成功",
  "data": null
}
```

### 8. 提交任务评价

**请求路径**：`POST /task/{id}/evaluation`

**路径参数**：
- `id`：任务ID

**请求体**：
```json
{
  "score": 5,
  "content": "服务很好，工程师专业",
  "serviceAttitudeScore": 5,
  "professionalScore": 5,
  "responseSpeedScore": 4
}
```

**响应示例**：
```json
{
  "code": 0,
  "message": "评价成功",
  "data": null
}
```

### 9. 获取任务评价

**请求路径**：`GET /task/{id}/evaluation`

**路径参数**：
- `id`：任务ID

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "taskId": 1,
    "customerId": 100,
    "score": 5,
    "content": "服务很好，工程师专业",
    "serviceAttitudeScore": 5,
    "professionalScore": 5,
    "responseSpeedScore": 4,
    "createTime": "2023-06-03T15:00:00"
  }
}
```

## 文件上传相关接口

### 1. 上传文件

**请求路径**：`POST /files/upload`

**请求参数**：
- `file`：文件（multipart/form-data）
- `relationId`：关联ID
- `relationType`：关联类型（0：任务，1：步骤，2：评价）
- `uploadUserId`：上传人ID
- `uploadUserType`：上传人类型（0：系统用户，1：客户）

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "relationId": 1,
    "relationType": 0,
    "fileName": "test.jpg",
    "filePath": "2023/06/01/abc123.jpg",
    "fileSize": 1024,
    "fileType": "jpg",
    "uploadUserId": 100,
    "uploadUserType": 1,
    "createTime": "2023-06-01T09:00:00"
  }
}
```

### 2. 上传任务图片

**请求路径**：`POST /files/task/image`

**请求参数**：
- `file`：图片文件（multipart/form-data）
- `taskId`：任务ID
- `imageType`：图片类型（0：任务图片，1：设备图片，2：结果图片）
- `sort`：排序（可选）

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "taskId": 1,
    "imageUrl": "/files/images/2023/06/01/abc123.jpg",
    "imageType": 0,
    "sort": 0,
    "createTime": "2023-06-01T09:00:00",
    "updateTime": "2023-06-01T09:00:00"
  }
}
```

### 3. 获取文件

**请求路径**：`GET /files/{fileId}`

**路径参数**：
- `fileId`：文件ID

**响应**：文件内容（二进制流）

### 4. 获取任务图片列表

**请求路径**：`GET /files/task/{taskId}/images`

**路径参数**：
- `taskId`：任务ID

**请求参数**：
- `imageType`：图片类型（可选）

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "taskId": 1,
      "imageUrl": "/files/images/2023/06/01/abc123.jpg",
      "imageType": 0,
      "sort": 0,
      "createTime": "2023-06-01T09:00:00",
      "updateTime": "2023-06-01T09:00:00"
    },
    {
      "id": 2,
      "taskId": 1,
      "imageUrl": "/files/images/2023/06/01/def456.jpg",
      "imageType": 0,
      "sort": 1,
      "createTime": "2023-06-01T09:01:00",
      "updateTime": "2023-06-01T09:01:00"
    }
  ]
}
```

### 5. 删除文件

**请求路径**：`DELETE /files/{fileId}`

**路径参数**：
- `fileId`：文件ID

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 6. 删除任务图片

**请求路径**：`DELETE /files/task/image/{imageId}`

**路径参数**：
- `imageId`：图片ID

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```