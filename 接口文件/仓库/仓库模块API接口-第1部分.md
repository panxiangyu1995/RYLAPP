# 仓库模块API接口文档

## 基础信息

- 基础路径: `/api/v1/warehouse`
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

### 1. 仓库管理API

#### 1.1 获取所有仓库

- **接口说明**：获取所有仓库信息
- **请求方式**：GET
- **请求路径**：`/api/v1/warehouse`
- **请求参数**：无

- **响应参数**：

| 参数名     | 类型   | 描述                |
| ---------- | ------ | ------------------- |
| warehouses | array  | 仓库列表            |
| └ id       | number | 仓库ID              |
| └ code     | string | 仓库编码            |
| └ name     | string | 仓库名称            |
| └ location | string | 仓库地点            |
| └ description | string | 仓库描述          |
| └ status   | number | 状态（0-禁用，1-启用）|
| └ createTime | string | 创建时间           |
| └ updateTime | string | 更新时间           |

- **响应示例**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "warehouses": [
      {
        "id": 1,
        "code": "TZ",
        "name": "泰州仓",
        "location": "泰州",
        "description": "泰州公司仓库",
        "status": 1,
        "createTime": "2024-05-10T10:30:00.000Z",
        "updateTime": "2024-05-10T10:30:00.000Z"
      },
      {
        "id": 2,
        "code": "SZ",
        "name": "苏州仓",
        "location": "苏州",
        "description": "苏州公司仓库",
        "status": 1,
        "createTime": "2024-05-10T10:30:00.000Z",
        "updateTime": "2024-05-10T10:30:00.000Z"
      }
    ]
  }
}
```

#### 1.2 获取仓库详情

- **接口说明**：获取单个仓库的详细信息
- **请求方式**：GET
- **请求路径**：`/api/v1/warehouse/{id}`
- **请求参数**：

| 参数名 | 类型   | 必填 | 描述   |
| ------ | ------ | ---- | ------ |
| id     | number | 是   | 仓库ID |

- **响应参数**：

| 参数名     | 类型   | 描述                |
| ---------- | ------ | ------------------- |
| id         | number | 仓库ID              |
| code       | string | 仓库编码            |
| name       | string | 仓库名称            |
| location   | string | 仓库地点            |
| description | string | 仓库描述           |
| status     | number | 状态（0-禁用，1-启用）|
| createTime | string | 创建时间            |
| updateTime | string | 更新时间            |
| statistics | object | 仓库统计信息        |
| └ totalItems | number | 物品总数          |
| └ instrumentCount | number | 仪器数量     |
| └ partCount | number | 配件数量           |
| └ consumableCount | number | 耗材数量     |
| └ assetCount | number | 固定资产数量      |

- **响应示例**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "code": "TZ",
    "name": "泰州仓",
    "location": "泰州",
    "description": "泰州公司仓库",
    "status": 1,
    "createTime": "2024-05-10T10:30:00.000Z",
    "updateTime": "2024-05-10T10:30:00.000Z",
    "statistics": {
      "totalItems": 120,
      "instrumentCount": 15,
      "partCount": 45,
      "consumableCount": 50,
      "assetCount": 10
    }
  }
}
```

#### 1.3 添加仓库

- **接口说明**：添加新仓库
- **请求方式**：POST
- **请求路径**：`/api/v1/warehouse`
- **权限要求**：系统管理员
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| code       | string | 是   | 仓库编码 |
| name       | string | 是   | 仓库名称 |
| location   | string | 是   | 仓库地点 |
| description | string | 否   | 仓库描述 |
| status     | number | 否   | 状态（0-禁用，1-启用），默认为1 |

- **请求示例**：

```json
{
  "code": "BJ",
  "name": "北京仓",
  "location": "北京",
  "description": "北京公司仓库",
  "status": 1
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "id": 5,
    "code": "BJ",
    "name": "北京仓",
    "location": "北京",
    "description": "北京公司仓库",
    "status": 1,
    "createTime": "2024-05-15T08:30:00.000Z",
    "updateTime": "2024-05-15T08:30:00.000Z"
  }
}
```

#### 1.4 修改仓库

- **接口说明**：修改仓库信息
- **请求方式**：PUT
- **请求路径**：`/api/v1/warehouse/{id}`
- **权限要求**：系统管理员
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| id         | number | 是   | 仓库ID（路径参数） |
| name       | string | 否   | 仓库名称 |
| location   | string | 否   | 仓库地点 |
| description | string | 否   | 仓库描述 |
| status     | number | 否   | 状态（0-禁用，1-启用） |

- **请求示例**：

```json
{
  "name": "北京中央仓",
  "description": "北京总公司中央仓库",
  "status": 1
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "修改成功",
  "data": {
    "id": 5,
    "code": "BJ",
    "name": "北京中央仓",
    "location": "北京",
    "description": "北京总公司中央仓库",
    "status": 1,
    "createTime": "2024-05-15T08:30:00.000Z",
    "updateTime": "2024-05-15T09:15:00.000Z"
  }
}
```

#### 1.5 删除仓库

- **接口说明**：删除仓库
- **请求方式**：DELETE
- **请求路径**：`/api/v1/warehouse/{id}`
- **权限要求**：系统管理员
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| id         | number | 是   | 仓库ID（路径参数） |
| password   | string | 是   | 删除密码 |

- **请求示例**：

```json
{
  "password": "admin123"
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