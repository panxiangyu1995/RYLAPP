### 2. 物品管理API

#### 2.1 获取物品列表

- **接口说明**：获取仓库中的物品列表，支持分页、筛选和排序
- **请求方式**：GET
- **请求路径**：`/api/v1/warehouse/{warehouseId}/items`
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| warehouseId | number | 是   | 仓库ID（路径参数） |
| categoryId | number | 否   | 分类ID，不传则查询所有分类 |
| keyword    | string | 否   | 搜索关键词，匹配物品名称、型号等 |
| sortBy     | string | 否   | 排序字段（name-名称，stock-库存，date-到货日期），默认为name |
| sortOrder  | string | 否   | 排序方式（asc-升序，desc-降序），默认为asc |
| page       | number | 否   | 页码，默认为1 |
| size       | number | 否   | 每页数量，默认为20 |

- **响应参数**：

| 参数名     | 类型   | 描述                |
| ---------- | ------ | ------------------- |
| total      | number | 总记录数            |
| pages      | number | 总页数              |
| current    | number | 当前页码            |
| size       | number | 每页数量            |
| items      | array  | 物品列表            |
| └ id       | number | 物品ID              |
| └ itemCode | string | 物品编号            |
| └ categoryId | number | 分类ID            |
| └ categoryName | string | 分类名称        |
| └ name     | string | 物品名称            |
| └ model    | string | 规格型号            |
| └ manufacturer | string | 厂家/品牌       |
| └ quantity | number | 数量                |
| └ cost     | number | 成本                |
| └ arrivalDate | string | 到货日期         |
| └ area     | string | 区域                |
| └ description | string | 描述/备注        |
| └ status   | number | 状态（0-禁用，1-启用）|
| └ stockStatus | string | 库存状态（库存充足/库存紧张/缺货）|

- **响应示例**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 120,
    "pages": 6,
    "current": 1,
    "size": 20,
    "items": [
      {
        "id": 1,
        "itemCode": "INS-2024-001",
        "categoryId": 1,
        "categoryName": "仪器库",
        "name": "气相色谱仪",
        "model": "GC-2000",
        "manufacturer": "Agilent",
        "quantity": 2,
        "cost": 150000.00,
        "arrivalDate": "2024-01-15",
        "area": "A区",
        "description": "高精度气相色谱仪",
        "status": 1,
        "stockStatus": "库存充足"
      },
      {
        "id": 4,
        "itemCode": "PART-2024-001",
        "categoryId": 2,
        "categoryName": "配件库",
        "name": "GC色谱柱",
        "model": "DB-5MS",
        "manufacturer": "Agilent",
        "quantity": 10,
        "cost": 3500.00,
        "arrivalDate": "2024-01-20",
        "area": "C区",
        "description": "30m x 0.25mm x 0.25μm",
        "status": 1,
        "stockStatus": "库存充足"
      }
      // 更多物品...
    ]
  }
}
```

#### 2.2 获取物品详情

- **接口说明**：获取物品详细信息
- **请求方式**：GET
- **请求路径**：`/api/v1/warehouse/item/{id}`
- **请求参数**：

| 参数名 | 类型   | 必填 | 描述   |
| ------ | ------ | ---- | ------ |
| id     | number | 是   | 物品ID |

- **响应参数**：

| 参数名     | 类型   | 描述                |
| ---------- | ------ | ------------------- |
| id         | number | 物品ID              |
| itemCode   | string | 物品编号            |
| categoryId | number | 分类ID              |
| categoryName | string | 分类名称          |
| warehouseId | number | 仓库ID            |
| warehouseName | string | 仓库名称        |
| name       | string | 物品名称            |
| model      | string | 规格型号            |
| manufacturer | string | 厂家/品牌         |
| quantity   | number | 数量                |
| cost       | number | 成本                |
| arrivalDate | string | 到货日期           |
| area       | string | 区域                |
| description | string | 描述/备注          |
| status     | number | 状态（0-禁用，1-启用）|
| createTime | string | 创建时间            |
| updateTime | string | 更新时间            |
| details    | object | 详细信息（根据分类不同而不同）|
| stockRecords | array | 最近的出入库记录    |
| └ id       | number | 记录ID              |
| └ recordCode | string | 记录编号          |
| └ recordType | number | 记录类型（1-入库，2-出库）|
| └ quantity | number | 数量                |
| └ operatorName | string | 操作人姓名      |
| └ operationTime | string | 操作时间       |

- **响应示例（仪器）**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "itemCode": "INS-2024-001",
    "categoryId": 1,
    "categoryName": "仪器库",
    "warehouseId": 1,
    "warehouseName": "泰州仓",
    "name": "气相色谱仪",
    "model": "GC-2000",
    "manufacturer": "Agilent",
    "quantity": 2,
    "cost": 150000.00,
    "arrivalDate": "2024-01-15",
    "area": "A区",
    "description": "高精度气相色谱仪",
    "status": 1,
    "createTime": "2024-05-10T10:30:00.000Z",
    "updateTime": "2024-05-10T10:30:00.000Z",
    "details": {
      "isNew": 1,
      "productionDate": "2023-12-01",
      "recoveryUnit": null
    },
    "stockRecords": [
      {
        "id": 1,
        "recordCode": "IN-2024-001",
        "recordType": 1,
        "quantity": 2,
        "operatorName": "李四",
        "operationTime": "2024-01-15T09:30:00.000Z"
      }
    ]
  }
}
```

- **响应示例（配件）**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 4,
    "itemCode": "PART-2024-001",
    "categoryId": 2,
    "categoryName": "配件库",
    "warehouseId": 1,
    "warehouseName": "泰州仓",
    "name": "GC色谱柱",
    "model": "DB-5MS",
    "manufacturer": "Agilent",
    "quantity": 10,
    "cost": 3500.00,
    "arrivalDate": "2024-01-20",
    "area": "C区",
    "description": "30m x 0.25mm x 0.25μm",
    "status": 1,
    "createTime": "2024-05-10T10:30:00.000Z",
    "updateTime": "2024-05-10T10:30:00.000Z",
    "details": {
      "partNumber": "AG-DB5-30"
    },
    "stockRecords": [
      {
        "id": 4,
        "recordCode": "IN-2024-004",
        "recordType": 1,
        "quantity": 10,
        "operatorName": "李四",
        "operationTime": "2024-01-20T14:30:00.000Z"
      }
    ]
  }
}
```

#### 2.3 添加物品

- **接口说明**：添加新物品
- **请求方式**：POST
- **请求路径**：`/api/v1/warehouse/item`
- **权限要求**：系统管理员、仓库管理员
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| categoryId | number | 是   | 分类ID |
| warehouseId | number | 是   | 仓库ID |
| name       | string | 是   | 物品名称 |
| model      | string | 是   | 规格型号 |
| manufacturer | string | 是   | 厂家/品牌 |
| quantity   | number | 是   | 数量 |
| cost       | number | 否   | 成本 |
| arrivalDate | string | 是   | 到货日期（YYYY-MM-DD） |
| area       | string | 是   | 区域 |
| description | string | 否   | 描述/备注 |
| details    | object | 否   | 详细信息（根据分类不同而不同） |

- **请求示例（仪器）**：

```json
{
  "categoryId": 1,
  "warehouseId": 1,
  "name": "液质联用仪",
  "model": "LCMS-2100",
  "manufacturer": "Shimadzu",
  "quantity": 1,
  "cost": 300000.00,
  "arrivalDate": "2024-05-20",
  "area": "A区",
  "description": "高灵敏度液质联用仪",
  "details": {
    "isNew": 1,
    "productionDate": "2024-03-15"
  }
}
```

- **请求示例（配件）**：

```json
{
  "categoryId": 2,
  "warehouseId": 1,
  "name": "MS离子源",
  "model": "ESI-Pro",
  "manufacturer": "Waters",
  "quantity": 2,
  "cost": 25000.00,
  "arrivalDate": "2024-05-20",
  "area": "B区",
  "description": "高灵敏度电喷雾离子源",
  "details": {
    "partNumber": "WA-ESI-PRO"
  }
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "id": 13,
    "itemCode": "INS-2024-004",
    "categoryId": 1,
    "warehouseId": 1,
    "name": "液质联用仪",
    "model": "LCMS-2100",
    "manufacturer": "Shimadzu",
    "quantity": 1,
    "cost": 300000.00,
    "arrivalDate": "2024-05-20",
    "area": "A区",
    "description": "高灵敏度液质联用仪",
    "status": 1,
    "createTime": "2024-05-20T08:30:00.000Z",
    "updateTime": "2024-05-20T08:30:00.000Z"
  }
}
```

#### 2.4 修改物品

- **接口说明**：修改物品信息
- **请求方式**：PUT
- **请求路径**：`/api/v1/warehouse/item/{id}`
- **权限要求**：系统管理员、仓库管理员
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| id         | number | 是   | 物品ID（路径参数） |
| name       | string | 否   | 物品名称 |
| model      | string | 否   | 规格型号 |
| manufacturer | string | 否   | 厂家/品牌 |
| area       | string | 否   | 区域 |
| description | string | 否   | 描述/备注 |
| status     | number | 否   | 状态（0-禁用，1-启用） |
| details    | object | 否   | 详细信息（根据分类不同而不同） |

- **请求示例**：

```json
{
  "name": "高效液质联用仪",
  "description": "高效高灵敏度液质联用仪",
  "details": {
    "productionDate": "2024-04-01"
  }
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "修改成功",
  "data": {
    "id": 13,
    "itemCode": "INS-2024-004",
    "categoryId": 1,
    "warehouseId": 1,
    "name": "高效液质联用仪",
    "model": "LCMS-2100",
    "manufacturer": "Shimadzu",
    "quantity": 1,
    "cost": 300000.00,
    "arrivalDate": "2024-05-20",
    "area": "A区",
    "description": "高效高灵敏度液质联用仪",
    "status": 1,
    "createTime": "2024-05-20T08:30:00.000Z",
    "updateTime": "2024-05-20T09:15:00.000Z"
  }
}
```

#### 2.5 删除物品

- **接口说明**：删除物品
- **请求方式**：DELETE
- **请求路径**：`/api/v1/warehouse/item/{id}`
- **权限要求**：系统管理员、仓库管理员
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| id         | number | 是   | 物品ID（路径参数） |
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