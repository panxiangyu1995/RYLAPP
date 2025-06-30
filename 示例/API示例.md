# 个人信息相关API说明

## 基础信息

- 基础路径: `/api/v1/user`
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

### 1. 获取个人信息

- **接口说明**：获取当前登录用户的个人信息
- **请求方式**：GET
- **请求路径**：`/api/v1/user/info`
- **请求参数**：无（使用 token 认证）

- **响应参数**：

| 参数名     | 类型   | 描述                |
| ---------- | ------ | ------------------- |
| workId     | string | 工号                |
| name       | string | 姓名                |
| mobile     | string | 手机号              |
| department | string | 部门                |
| location   | string | 工作地点            |
| avatar     | string | 头像URL             |
| taskCount  | number | 任务数              |
| completeRate | string | 完成率（带%符号）   |
| score      | number | 评分                |
| tokenExpire | string | token过期时间       |

- **响应示例**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "workId": "RYL001",
    "name": "张三",
    "mobile": "13900139001",
    "department": "技术部",
    "location": "上海",
    "avatar": "https://example.com/avatars/zhangsan.png",
    "taskCount": 5,
    "completeRate": "80%",
    "score": 4.8,
    "tokenExpire": "2024-08-15T10:30:00.000Z"
  }
}
```

### 2. 修改个人信息

- **接口说明**：修改当前登录用户的个人信息
- **请求方式**：PUT
- **请求路径**：`/api/v1/user/info`
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述        |
| ---------- | ------ | ---- | ----------- |
| name       | string | 否   | 姓名        |
| mobile     | string | 否   | 手机号      |
| location   | string | 否   | 工作地点    |
| avatar     | string | 否   | 头像URL     |

- **请求示例**：

```json
{
  "name": "张三",
  "mobile": "13900139001",
  "location": "北京",
  "avatar": "https://example.com/avatars/new-avatar.png"
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "修改成功",
  "data": {
    "workId": "RYL001",
    "name": "张三",
    "mobile": "13900139001",
    "department": "技术部",
    "location": "北京",
    "avatar": "https://example.com/avatars/new-avatar.png"
  }
}
```

### 3. 上传头像

- **接口说明**：上传用户头像
- **请求方式**：POST
- **请求路径**：`/api/v1/user/avatar`
- **请求参数**：
  - Content-Type: multipart/form-data

| 参数名 | 类型 | 必填 | 描述       |
| ------ | ---- | ---- | ---------- |
| file   | file | 是   | 头像文件   |

- **响应参数**：

| 参数名 | 类型   | 描述      |
| ------ | ------ | --------- |
| url    | string | 头像URL   |

- **响应示例**：

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "https://example.com/avatars/user_123456.jpg"
  }
}
```

### 4. 修改密码

- **接口说明**：修改当前登录用户的密码
- **请求方式**：PUT
- **请求路径**：`/api/v1/user/password`
- **请求参数**：

| 参数名      | 类型   | 必填 | 描述   |
| ----------- | ------ | ---- | ------ |
| oldPassword | string | 是   | 旧密码 |
| newPassword | string | 是   | 新密码 |

- **请求示例**：

```json
{
  "oldPassword": "123456",
  "newPassword": "abcdef"
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null
}
```

### 5. 刷新Token

- **接口说明**：刷新用户登录Token
- **请求方式**：POST
- **请求路径**：`/api/v1/user/token/refresh`
- **请求参数**：无（使用当前token认证）

- **响应参数**：

| 参数名       | 类型   | 描述                |
| ------------ | ------ | ------------------- |
| token        | string | 新的JWT令牌          |
| tokenExpire  | string | 令牌过期时间         |

- **响应示例**：

```json
{
  "code": 200,
  "message": "Token刷新成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenExpire": "2024-08-15T10:30:00.000Z"
  }
}
``` 