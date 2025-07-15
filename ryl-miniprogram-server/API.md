# 瑞屹林微信小程序后端API文档

## 目录

1. [概述](#概述)
2. [接口规范](#接口规范)
3. [认证机制](#认证机制)
4. [错误码说明](#错误码说明)
5. [接口列表](#接口列表)
   - [微信登录相关](#微信登录相关)
   - [用户信息相关](#用户信息相关)
   - [任务相关](#任务相关)
   - [任务评论相关](#任务评论相关)
   - [文件上传下载相关](#文件上传下载相关)

## 概述

本文档描述了瑞屹林微信小程序后端服务API接口规范和使用方法。所有接口均采用RESTful风格设计，返回JSON格式数据。

### 基本信息

- 接口基础路径：`/`
- 服务端口：`8085`
- 开发环境：`http://localhost:8085`
- 生产环境：`https://api.example.com`

## 接口规范

### 请求规范

- GET请求：参数通过URL Query Parameters传递
- POST/PUT/DELETE请求：参数通过Request Body以JSON格式传递
- 文件上传：使用multipart/form-data格式

### 响应规范

所有接口返回统一的JSON格式：

```json
{
  "code": 200,       // 状态码，200表示成功，非200表示失败
  "message": "操作成功", // 状态描述
  "data": {}         // 返回的数据，可能是对象、数组或null
}
```

## 认证机制

除了登录接口外，其他接口都需要在请求头中携带JWT Token进行认证：

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Token通过微信登录接口获取，有效期为24小时。

## 错误码说明

| 错误码 | 说明 |
| ----- | ---- |
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权或授权失败 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 接口列表

### 微信登录相关

#### 微信小程序登录

**接口描述**：通过微信小程序获取的code进行登录认证，获取JWT Token

**请求方法**：POST

**请求路径**：`/api/wechat/login`

**请求参数**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| code | String | 是 | 微信小程序登录时获取的code |

**请求示例**：

```json
{
  "code": "071UJoll2PoqL94TGfol2gIJll2UJolO"
}
```

**响应参数**：

| 参数名 | 类型 | 描述 |
| ----- | ---- | ---- |
| token | String | JWT Token，用于后续接口认证 |
| openid | String | 用户的openid |

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "openid": "oVDID5Bav-7R_5uqJn8O9XcbkTAA"
  }
}
```

### 用户信息相关

#### 获取用户信息

**接口描述**：获取当前登录用户的信息

**请求方法**：GET

**请求路径**：`/api/user/info`

**请求头**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| Authorization | String | 是 | Bearer + JWT Token |

**响应参数**：

| 参数名 | 类型 | 描述 |
| ----- | ---- | ---- |
| id | Long | 用户ID |
| openid | String | 用户openid |
| nickName | String | 用户昵称 |
| avatarUrl | String | 用户头像URL |
| gender | Integer | 性别（0：未知，1：男，2：女） |
| country | String | 国家 |
| province | String | 省份 |
| city | String | 城市 |
| language | String | 语言 |
| phone | String | 手机号码 |

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "openid": "oVDID5Bav-7R_5uqJn8O9XcbkTAA",
    "nickName": "张三",
    "avatarUrl": "https://thirdwx.qlogo.cn/mmopen/vi_32/...",
    "gender": 1,
    "country": "中国",
    "province": "广东",
    "city": "深圳",
    "language": "zh_CN",
    "phone": "13800138000"
  }
}
```

#### 更新用户信息

**接口描述**：更新当前登录用户的信息

**请求方法**：PUT

**请求路径**：`/api/user/info`

**请求头**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| Authorization | String | 是 | Bearer + JWT Token |

**请求参数**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| nickName | String | 否 | 用户昵称 |
| avatarUrl | String | 否 | 用户头像URL |
| gender | Integer | 否 | 性别（0：未知，1：男，2：女） |
| country | String | 否 | 国家 |
| province | String | 否 | 省份 |
| city | String | 否 | 城市 |
| language | String | 否 | 语言 |
| phone | String | 否 | 手机号码 |

**请求示例**：

```json
{
  "nickName": "李四",
  "phone": "13900139000"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "openid": "oVDID5Bav-7R_5uqJn8O9XcbkTAA",
    "nickName": "李四",
    "avatarUrl": "https://thirdwx.qlogo.cn/mmopen/vi_32/...",
    "gender": 1,
    "country": "中国",
    "province": "广东",
    "city": "深圳",
    "language": "zh_CN",
    "phone": "13900139000"
  }
}
```

### 任务相关

#### 创建任务

**接口描述**：创建一个新的任务

**请求方法**：POST

**请求路径**：`/api/tasks`

**请求头**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| Authorization | String | 是 | Bearer + JWT Token |

**请求参数**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| title | String | 是 | 任务标题 |
| content | String | 是 | 任务内容 |
| status | Integer | 否 | 任务状态（0：未完成，1：已完成），默认为0 |

**请求示例**：

```json
{
  "title": "测试任务",
  "content": "这是一个测试任务的内容描述"
}
```

**响应参数**：

| 参数名 | 类型 | 描述 |
| ----- | ---- | ---- |
| id | Long | 任务ID |
| title | String | 任务标题 |
| content | String | 任务内容 |
| status | Integer | 任务状态（0：未完成，1：已完成） |
| customerId | Long | 创建者ID |
| createTime | String | 创建时间 |
| updateTime | String | 更新时间 |

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "测试任务",
    "content": "这是一个测试任务的内容描述",
    "status": 0,
    "customerId": 1,
    "createTime": "2023-06-01 12:00:00",
    "updateTime": "2023-06-01 12:00:00"
  }
}
```

#### 获取任务列表

**接口描述**：分页获取当前用户的任务列表

**请求方法**：GET

**请求路径**：`/api/tasks`

**请求头**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| Authorization | String | 是 | Bearer + JWT Token |

**请求参数**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| pageNum | Integer | 否 | 页码，默认为1 |
| pageSize | Integer | 否 | 每页记录数，默认为10 |
| status | Integer | 否 | 任务状态（0：未完成，1：已完成），不传则查询全部 |

**响应参数**：

| 参数名 | 类型 | 描述 |
| ----- | ---- | ---- |
| total | Long | 总记录数 |
| list | Array | 任务列表 |
| pageNum | Integer | 当前页码 |
| pageSize | Integer | 每页记录数 |
| pages | Integer | 总页数 |

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 2,
    "list": [
      {
        "id": 1,
        "title": "测试任务1",
        "content": "这是测试任务1的内容",
        "status": 0,
        "customerId": 1,
        "createTime": "2023-06-01 12:00:00",
        "updateTime": "2023-06-01 12:00:00"
      },
      {
        "id": 2,
        "title": "测试任务2",
        "content": "这是测试任务2的内容",
        "status": 0,
        "customerId": 1,
        "createTime": "2023-06-01 13:00:00",
        "updateTime": "2023-06-01 13:00:00"
      }
    ],
    "pageNum": 1,
    "pageSize": 10,
    "pages": 1
  }
}
```

#### 获取任务详情

**接口描述**：获取指定任务的详细信息

**请求方法**：GET

**请求路径**：`/api/tasks/{id}`

**请求头**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| Authorization | String | 是 | Bearer + JWT Token |

**请求参数**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| id | Long | 是 | 任务ID，路径参数 |

**响应参数**：

| 参数名 | 类型 | 描述 |
| ----- | ---- | ---- |
| id | Long | 任务ID |
| title | String | 任务标题 |
| content | String | 任务内容 |
| status | Integer | 任务状态（0：未完成，1：已完成） |
| customerId | Long | 创建者ID |
| createTime | String | 创建时间 |
| updateTime | String | 更新时间 |

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "测试任务",
    "content": "这是一个测试任务的内容描述",
    "status": 0,
    "customerId": 1,
    "createTime": "2023-06-01 12:00:00",
    "updateTime": "2023-06-01 12:00:00"
  }
}
```

#### 更新任务

**接口描述**：更新指定任务的信息

**请求方法**：PUT

**请求路径**：`/api/tasks/{id}`

**请求头**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| Authorization | String | 是 | Bearer + JWT Token |

**请求参数**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| id | Long | 是 | 任务ID，路径参数 |
| title | String | 否 | 任务标题 |
| content | String | 否 | 任务内容 |
| status | Integer | 否 | 任务状态（0：未完成，1：已完成） |

**请求示例**：

```json
{
  "title": "更新后的任务标题",
  "content": "更新后的任务内容"
}
```

**响应参数**：

| 参数名 | 类型 | 描述 |
| ----- | ---- | ---- |
| id | Long | 任务ID |
| title | String | 任务标题 |
| content | String | 任务内容 |
| status | Integer | 任务状态（0：未完成，1：已完成） |
| customerId | Long | 创建者ID |
| createTime | String | 创建时间 |
| updateTime | String | 更新时间 |

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "更新后的任务标题",
    "content": "更新后的任务内容",
    "status": 0,
    "customerId": 1,
    "createTime": "2023-06-01 12:00:00",
    "updateTime": "2023-06-01 14:00:00"
  }
}
```

#### 删除任务

**接口描述**：删除指定任务

**请求方法**：DELETE

**请求路径**：`/api/tasks/{id}`

**请求头**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| Authorization | String | 是 | Bearer + JWT Token |

**请求参数**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| id | Long | 是 | 任务ID，路径参数 |

**响应参数**：

| 参数名 | 类型 | 描述 |
| ----- | ---- | ---- |
| - | Boolean | 操作结果，true表示成功，false表示失败 |

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

#### 更新任务状态

**接口描述**：更新指定任务的状态

**请求方法**：PUT

**请求路径**：`/api/tasks/{id}/status/{status}`

**请求头**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| Authorization | String | 是 | Bearer + JWT Token |

**请求参数**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| id | Long | 是 | 任务ID，路径参数 |
| status | Integer | 是 | 任务状态（0：未完成，1：已完成），路径参数 |

**响应参数**：

| 参数名 | 类型 | 描述 |
| ----- | ---- | ---- |
| id | Long | 任务ID |
| title | String | 任务标题 |
| content | String | 任务内容 |
| status | Integer | 任务状态（0：未完成，1：已完成） |
| customerId | Long | 创建者ID |
| createTime | String | 创建时间 |
| updateTime | String | 更新时间 |

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "测试任务",
    "content": "这是一个测试任务的内容描述",
    "status": 1,
    "customerId": 1,
    "createTime": "2023-06-01 12:00:00",
    "updateTime": "2023-06-01 15:00:00"
  }
}
```

### 任务评论相关

#### 添加任务评论

**接口描述**：为指定任务添加评论

**请求方法**：POST

**请求路径**：`/api/task-comments`

**请求头**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| Authorization | String | 是 | Bearer + JWT Token |

**请求参数**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| taskId | Long | 是 | 任务ID |
| content | String | 是 | 评论内容 |

**请求示例**：

```json
{
  "taskId": 1,
  "content": "这是一条测试评论"
}
```

**响应参数**：

| 参数名 | 类型 | 描述 |
| ----- | ---- | ---- |
| id | Long | 评论ID |
| taskId | Long | 任务ID |
| customerId | Long | 评论者ID |
| content | String | 评论内容 |
| createTime | String | 创建时间 |
| updateTime | String | 更新时间 |

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "taskId": 1,
    "customerId": 1,
    "content": "这是一条测试评论",
    "createTime": "2023-06-01 16:00:00",
    "updateTime": "2023-06-01 16:00:00"
  }
}
```

#### 获取任务评论列表

**接口描述**：获取指定任务的评论列表

**请求方法**：GET

**请求路径**：`/api/task-comments/task/{taskId}`

**请求头**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| Authorization | String | 是 | Bearer + JWT Token |

**请求参数**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| taskId | Long | 是 | 任务ID，路径参数 |

**响应参数**：

| 参数名 | 类型 | 描述 |
| ----- | ---- | ---- |
| - | Array | 评论列表 |

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "taskId": 1,
      "customerId": 1,
      "content": "这是第一条评论",
      "createTime": "2023-06-01 16:00:00",
      "updateTime": "2023-06-01 16:00:00"
    },
    {
      "id": 2,
      "taskId": 1,
      "customerId": 2,
      "content": "这是第二条评论",
      "createTime": "2023-06-01 16:30:00",
      "updateTime": "2023-06-01 16:30:00"
    }
  ]
}
```

#### 删除任务评论

**接口描述**：删除指定的任务评论

**请求方法**：DELETE

**请求路径**：`/api/task-comments/{id}`

**请求头**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| Authorization | String | 是 | Bearer + JWT Token |

**请求参数**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| id | Long | 是 | 评论ID，路径参数 |

**响应参数**：

| 参数名 | 类型 | 描述 |
| ----- | ---- | ---- |
| - | Boolean | 操作结果，true表示成功，false表示失败 |

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

### 文件上传下载相关

#### 上传单个文件

**接口描述**：上传单个文件

**请求方法**：POST

**请求路径**：`/api/files/upload`

**请求头**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| Authorization | String | 是 | Bearer + JWT Token |
| Content-Type | String | 是 | multipart/form-data |

**请求参数**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| file | File | 是 | 要上传的文件 |

**响应参数**：

| 参数名 | 类型 | 描述 |
| ----- | ---- | ---- |
| fileName | String | 服务器存储的文件名 |
| originalFileName | String | 原始文件名 |
| fileSize | Long | 文件大小（字节） |
| fileType | String | 文件类型 |
| fileUrl | String | 文件访问URL |

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "fileName": "a1b2c3d4-e5f6-g7h8-i9j0.jpg",
    "originalFileName": "photo.jpg",
    "fileSize": 1024000,
    "fileType": "image/jpeg",
    "fileUrl": "http://localhost:8085/api/files/download/a1b2c3d4-e5f6-g7h8-i9j0.jpg"
  }
}
```

#### 上传多个文件

**接口描述**：批量上传多个文件

**请求方法**：POST

**请求路径**：`/api/files/upload-multiple`

**请求头**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| Authorization | String | 是 | Bearer + JWT Token |
| Content-Type | String | 是 | multipart/form-data |

**请求参数**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| files | File[] | 是 | 要上传的文件列表 |

**响应参数**：

| 参数名 | 类型 | 描述 |
| ----- | ---- | ---- |
| - | Array | 文件信息列表 |

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "fileName": "a1b2c3d4-e5f6-g7h8-i9j0.jpg",
      "originalFileName": "photo1.jpg",
      "fileSize": 1024000,
      "fileType": "image/jpeg",
      "fileUrl": "http://localhost:8085/api/files/download/a1b2c3d4-e5f6-g7h8-i9j0.jpg"
    },
    {
      "fileName": "b2c3d4e5-f6g7-h8i9-j0k1.pdf",
      "originalFileName": "document.pdf",
      "fileSize": 2048000,
      "fileType": "application/pdf",
      "fileUrl": "http://localhost:8085/api/files/download/b2c3d4e5-f6g7-h8i9-j0k1.pdf"
    }
  ]
}
```

#### 下载文件

**接口描述**：下载指定文件

**请求方法**：GET

**请求路径**：`/api/files/download/{fileName}`

**请求头**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| Authorization | String | 是 | Bearer + JWT Token |

**请求参数**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| fileName | String | 是 | 文件名，路径参数 |

**响应**：

文件二进制流，Content-Type根据文件类型设置，Content-Disposition为attachment。

#### 删除文件

**接口描述**：删除指定文件

**请求方法**：DELETE

**请求路径**：`/api/files/{fileName}`

**请求头**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| Authorization | String | 是 | Bearer + JWT Token |

**请求参数**：

| 参数名 | 类型 | 是否必须 | 描述 |
| ----- | ---- | ------- | ---- |
| fileName | String | 是 | 文件名，路径参数 |

**响应参数**：

| 参数名 | 类型 | 描述 |
| ----- | ---- | ---- |
| - | Boolean | 操作结果，true表示成功，false表示失败 |

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
``` 