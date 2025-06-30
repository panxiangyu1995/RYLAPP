# 用户认证模块API接口文档

## 基础信息

- 基础路径: `/api/user`
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
200: 成功
400: 请求参数错误
401: 未授权
403: 权限不足
404: 资源不存在
500: 服务器内部错误

## 接口列表

### 1. 用户登录

- **接口说明**：用户登录接口
- **请求方式**：POST
- **请求路径**：`/api/user/login`
- **请求参数**：

| 参数名   | 类型   | 必填 | 描述   |
| -------- | ------ | ---- | ------ |
| workId   | string | 是   | 工号   |
| password | string | 是   | 密码   |

- **响应参数**：

| 参数名     | 类型   | 描述                |
| ---------- | ------ | ------------------- |
| token      | string | JWT令牌             |
| workId     | string | 工号                |
| name       | string | 姓名                |
| mobile     | string | 手机号              |
| department | string | 部门                |
| location   | string | 工作地点            |
| avatar     | string | 头像URL             |

- **请求示例**：

```json
{
  "workId": "RYL001",
  "password": "password123"
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "workId": "RYL001",
    "name": "张三",
    "mobile": "13800138000",
    "department": "技术部",
    "location": "上海",
    "avatar": "https://example.com/avatars/default.png"
  }
}
```

### 2. 用户注册

- **接口说明**：用户注册接口
- **请求方式**：POST
- **请求路径**：`/api/user/register`
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述     |
| ---------- | ------ | ---- | -------- |
| workId     | string | 是   | 工号     |
| name       | string | 是   | 姓名     |
| mobile     | string | 是   | 手机号   |
| password   | string | 是   | 密码     |
| department | string | 否   | 部门     |
| location   | string | 否   | 工作地点 |

- **响应参数**：

| 参数名     | 类型    | 描述           |
| ---------- | ------- | -------------- |
| registered | boolean | 是否注册成功   |
| workId     | string  | 工号           |

- **请求示例**：

```json
{
  "workId": "RYL002",
  "name": "李四",
  "mobile": "13900139000",
  "password": "password123",
  "department": "市场部",
  "location": "北京"
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "registered": true,
    "workId": "RYL002"
  }
}
```

### 3. 忘记密码验证

- **接口说明**：忘记密码身份验证
- **请求方式**：GET
- **请求路径**：`/api/user/forgot-password`
- **请求参数**：

| 参数名 | 类型   | 必填 | 描述   |
| ------ | ------ | ---- | ------ |
| workId | string | 是   | 工号   |
| mobile | string | 是   | 手机号 |

- **响应参数**：

| 参数名 | 类型    | 描述           |
| ------ | ------- | -------------- |
| reset  | boolean | 是否验证成功   |
| workId | string  | 工号           |

- **响应示例**：

```json
{
  "code": 200,
  "message": "验证成功",
  "data": {
    "reset": true,
    "workId": "RYL001"
  }
}
```

### 4. 重置密码

- **接口说明**：重置用户密码
- **请求方式**：POST
- **请求路径**：`/api/user/reset-password`
- **请求参数**：

| 参数名      | 类型   | 必填 | 描述     |
| ----------- | ------ | ---- | -------- |
| workId      | string | 是   | 工号     |
| newPassword | string | 是   | 新密码   |

- **响应参数**：无特定返回数据，通过状态码和消息判断是否成功

- **请求示例**：

```json
{
  "workId": "RYL001",
  "newPassword": "newpassword123"
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "密码重置成功",
  "data": null
}
``` 