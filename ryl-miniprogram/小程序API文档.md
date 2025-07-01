# 研仪联小程序 API 文档

## 基础说明

### API 基本路径
所有 API 请求都使用以下基本路径：
```
/api/v1
```

### 请求与响应格式
- GET 请求：参数通过 URL 查询字符串传递
- POST/PUT/PATCH 请求：参数通过请求体传递，格式为 JSON
- 文件上传：使用 multipart/form-data 格式

### 统一响应格式
```json
{
  "code": 200,       // 状态码，200表示成功，非200表示失败
  "message": "操作成功", // 响应消息
  "data": {}         // 响应数据，根据接口不同而变化
}
```

### 认证方式
- 使用 Bearer Token 认证
- 请求头格式：`Authorization: Bearer {token}`

## API 接口列表

### 1. 用户认证

#### 1.1 微信一键登录
- **URL**: `/wechat/login`
- **方法**: POST
- **是否需要认证**: 否
- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|-------|------|------|------|
| code | string | 是 | 微信登录临时凭证 |

- **响应参数**:

| 参数名 | 类型 | 描述 |
|-------|------|------|
| token | string | 认证令牌 |
| userInfo | object | 用户信息对象 |
| userInfo.contact | string | 客户姓名 |
| userInfo.name | string | 公司名称 |
| userInfo.phone | string | 联系电话 |
| userInfo.address | string | 联系地址 |
| userInfo.email | string | 电子邮箱 |
| userInfo.wechat_avatar_url | string | 微信头像URL |

- **示例**:
```json
// 请求
{
  "code": "wx_login_code_123456"
}

// 响应
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "contact": "张三",
      "name": "ABC科技有限公司",
      "phone": "13800138000",
      "address": "北京市海淀区中关村",
      "email": "zhangsan@example.com",
      "wechat_avatar_url": "https://thirdwx.qlogo.cn/..."
    }
  }
}
```

#### 1.2 获取用户信息
- **URL**: `/user/profile`
- **方法**: GET
- **是否需要认证**: 是
- **请求参数**: 无
- **响应参数**:

| 参数名 | 类型 | 描述 |
|-------|------|------|
| contact | string | 客户姓名 |
| name | string | 公司名称 |
| phone | string | 联系电话 |
| address | string | 联系地址 |
| email | string | 电子邮箱 |
| wechat_avatar_url | string | 微信头像URL |

- **示例**:
```json
// 响应
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "contact": "张三",
    "name": "ABC科技有限公司",
    "phone": "13800138000",
    "address": "北京市海淀区中关村",
    "email": "zhangsan@example.com",
    "wechat_avatar_url": "https://thirdwx.qlogo.cn/..."
  }
}
```

#### 1.3 更新用户信息
- **URL**: `/user/profile`
- **方法**: PUT
- **是否需要认证**: 是
- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|-------|------|------|------|
| contact | string | 是 | 客户姓名 |
| name | string | 是 | 公司名称 |
| phone | string | 是 | 联系电话 |
| address | string | 是 | 联系地址 |
| email | string | 否 | 电子邮箱 |

- **响应参数**:

| 参数名 | 类型 | 描述 |
|-------|------|------|
| contact | string | 客户姓名 |
| name | string | 公司名称 |
| phone | string | 联系电话 |
| address | string | 联系地址 |
| email | string | 电子邮箱 |
| wechat_avatar_url | string | 微信头像URL |

- **示例**:
```json
// 请求
{
  "contact": "张三",
  "name": "ABC科技有限公司",
  "phone": "13800138000",
  "address": "北京市海淀区中关村",
  "email": "zhangsan@example.com"
}

// 响应
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "contact": "张三",
    "name": "ABC科技有限公司",
    "phone": "13800138000",
    "address": "北京市海淀区中关村",
    "email": "zhangsan@example.com",
    "wechat_avatar_url": "https://thirdwx.qlogo.cn/..."
  }
}
```

### 2. 任务管理

#### 2.1 创建任务
- **URL**: `/tasks`
- **方法**: POST
- **是否需要认证**: 是
- **请求参数**: multipart/form-data 格式

| 参数名 | 类型 | 必填 | 描述 |
|-------|------|------|------|
| type | string | 是 | 任务类型（repair/maintenance/recycle/leasing/training/verification/selection/installation） |
| customer.name | string | 是 | 客户姓名 |
| customer.company | string | 是 | 公司名称 |
| customer.phone | string | 是 | 联系电话 |
| customer.address | string | 是 | 联系地址 |
| customer.email | string | 否 | 电子邮箱 |
| device.name | string | 条件必填 | 仪器名称（除选型外必填） |
| device.type | string | 条件必填 | 仪器类型（除选型外必填） |
| device.brand | string | 条件必填 | 品牌（除选型外必填） |
| device.model | string | 条件必填 | 型号（除选型外必填） |
| device.fault_description | string | 条件必填 | 故障描述（维修/保养必填） |
| device.quantity | number | 是 | 数量 |
| device.accessories | string | 否 | 附件描述（回收/租赁可选） |
| device.appointment_time | string | 条件必填 | 预约时间（培训必填） |
| device.verification_type | string | 条件必填 | 验证类别（验证必填，值为IQ/OQ/PQ） |
| device.purpose | string | 条件必填 | 仪器用途或名称（选型必填） |
| device.requirement_description | string | 条件必填 | 需求描述（选型必填） |
| description | string | 是 | 任务详细描述 |
| images | file[] | 否 | 图片文件，最多9张，每张不超过5MB |
| files | file[] | 否 | 附件文件，支持PDF、Word、Excel格式，每个不超过10MB |

- **响应参数**:

| 参数名 | 类型 | 描述 |
|-------|------|------|
| task_id | string | 任务ID |
| message | string | 提交结果消息 |

- **示例**:
```json
// 响应
{
  "code": 200,
  "message": "任务提交成功",
  "data": {
    "task_id": "T202507010001",
    "message": "任务已提交，我们会尽快处理"
  }
}
```

#### 2.2 获取任务列表
- **URL**: `/tasks`
- **方法**: GET
- **是否需要认证**: 是
- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|-------|------|------|------|
| page | number | 否 | 页码，从1开始，默认1 |
| size | number | 否 | 每页记录数，默认10 |

- **响应参数**:

| 参数名 | 类型 | 描述 |
|-------|------|------|
| total | number | 总记录数 |
| pages | number | 总页数 |
| current | number | 当前页码 |
| size | number | 每页记录数 |
| list | array | 任务列表 |
| list[].task_id | string | 任务ID |
| list[].task_type | string | 任务类型 |
| list[].device_name | string | 仪器名称 |
| list[].description | string | 任务描述 |
| list[].engineer_name | string | 负责工程师姓名 |
| list[].engineer_phone | string | 负责工程师电话 |
| list[].current_step | number | 当前步骤索引（0-7） |
| list[].create_time | string | 创建时间 |
| list[].update_time | string | 更新时间 |

- **示例**:
```json
// 响应
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 15,
    "pages": 2,
    "current": 1,
    "size": 10,
    "list": [
      {
        "task_id": "T202507010001",
        "task_type": "repair",
        "device_name": "高效液相色谱仪",
        "description": "仪器无法正常启动",
        "engineer_name": "李工",
        "engineer_phone": "13900139000",
        "current_step": 2,
        "create_time": "2025-07-01 10:00:00",
        "update_time": "2025-07-02 14:30:00"
      },
      // ... 其他任务
    ]
  }
}
```

#### 2.3 获取任务详情
- **URL**: `/tasks/{id}`
- **方法**: GET
- **是否需要认证**: 是
- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|-------|------|------|------|
| id | string | 是 | 任务ID（路径参数） |

- **响应参数**:

| 参数名 | 类型 | 描述 |
|-------|------|------|
| task_id | string | 任务ID |
| task_type | string | 任务类型 |
| device_name | string | 仪器名称 |
| device_type | string | 仪器类型 |
| device_brand | string | 品牌 |
| device_model | string | 型号 |
| description | string | 任务描述 |
| engineer_name | string | 负责工程师姓名 |
| engineer_phone | string | 负责工程师电话 |
| current_step | number | 当前步骤索引（0-7） |
| create_time | string | 创建时间 |
| update_time | string | 更新时间 |
| step_content | object | 当前步骤的内容 |
| step_content.description | string | 步骤描述文本 |
| step_content.images | array | 步骤相关图片URL数组 |
| step_content.files | array | 步骤相关文件 |
| step_content.files[].name | string | 文件名称 |
| step_content.files[].url | string | 文件URL |

- **示例**:
```json
// 响应
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "task_id": "T202507010001",
    "task_type": "repair",
    "device_name": "高效液相色谱仪",
    "device_type": "色谱仪",
    "device_brand": "安捷伦",
    "device_model": "1260",
    "description": "仪器无法正常启动",
    "engineer_name": "李工",
    "engineer_phone": "13900139000",
    "current_step": 2,
    "create_time": "2025-07-01 10:00:00",
    "update_time": "2025-07-02 14:30:00",
    "step_content": {
      "description": "经过检修，发现是电源模块故障，需要更换电源模块。",
      "images": [
        "https://example.com/images/task/T202507010001/img1.jpg",
        "https://example.com/images/task/T202507010001/img2.jpg"
      ],
      "files": [
        {
          "name": "检修报告.pdf",
          "url": "https://example.com/files/task/T202507010001/report.pdf"
        }
      ]
    }
  }
}
```

#### 2.4 提交服务评价
- **URL**: `/tasks/{id}/evaluation`
- **方法**: POST
- **是否需要认证**: 是
- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|-------|------|------|------|
| id | string | 是 | 任务ID（路径参数） |
| serviceAttitude | number | 是 | 服务态度评分（1-5） |
| serviceQuality | number | 是 | 服务质量评分（1-5） |
| overallRating | number | 是 | 总体评价评分（1-5） |
| comment | string | 否 | 评价内容 |

- **响应参数**:

| 参数名 | 类型 | 描述 |
|-------|------|------|
| message | string | 评价结果消息 |

- **示例**:
```json
// 请求
{
  "serviceAttitude": 5,
  "serviceQuality": 4,
  "overallRating": 5,
  "comment": "服务很专业，解决问题迅速"
}

// 响应
{
  "code": 200,
  "message": "评价提交成功",
  "data": {
    "message": "感谢您的评价"
  }
}
```

## 错误码说明

| 错误码 | 描述 |
|-------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权（未登录或token无效） |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 数据库修改说明

为了支持小程序用户的微信一键登录和信息存储，需要对 `customer` 表进行以下扩展：

```sql
-- 添加微信小程序用户的唯一标识OpenID
ALTER TABLE [dbo].[customer] ADD [wechat_openid] NVARCHAR(128) NULL;

-- 添加微信开放平台下的唯一应用标识UnionID
ALTER TABLE [dbo].[customer] ADD [wechat_unionid] NVARCHAR(128) NULL;

-- 添加用户的微信头像URL
ALTER TABLE [dbo].[customer] ADD [wechat_avatar_url] NVARCHAR(512) NULL;

-- 为openid添加唯一索引以防止重复用户
CREATE UNIQUE INDEX UQ_customer_wechat_openid ON [dbo].[customer]([wechat_openid]) WHERE [wechat_openid] IS NOT NULL;
``` 