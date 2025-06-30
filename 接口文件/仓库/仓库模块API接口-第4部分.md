### 5. 盘库API

#### 5.1 创建盘库任务

- **接口说明**：创建盘库任务
- **请求方式**：POST
- **请求路径**：`/api/v1/warehouse/inventory/check`
- **权限要求**：系统管理员、仓库管理员
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| warehouseId | number | 是   | 仓库ID |
| checkTime  | string | 是   | 盘库时间（ISO 8601格式） |
| description | string | 否   | 描述/备注 |

- **请求示例**：

```json
{
  "warehouseId": 1,
  "checkTime": "2024-05-25T09:00:00.000Z",
  "description": "泰州仓月度盘点"
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 3,
    "checkCode": "IC-2024-003",
    "warehouseId": 1,
    "warehouseName": "泰州仓",
    "checkerId": 3,
    "checkerName": "李四",
    "checkTime": "2024-05-25T09:00:00.000Z",
    "status": 0,
    "description": "泰州仓月度盘点",
    "createTime": "2024-05-20T10:30:00.000Z"
  }
}
```

#### 5.2 提交盘库结果

- **接口说明**：提交盘库结果
- **请求方式**：PUT
- **请求路径**：`/api/v1/warehouse/inventory/check/{id}/submit`
- **权限要求**：系统管理员、仓库管理员
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| id         | number | 是   | 盘库ID（路径参数） |
| details    | array  | 是   | 盘库明细 |
| └ itemId   | number | 是   | 物品ID |
| └ actualQuantity | number | 是   | 实际数量 |
| └ remark   | string | 否   | 备注 |

- **请求示例**：

```json
{
  "details": [
    {
      "itemId": 1,
      "actualQuantity": 2,
      "remark": "数量正确"
    },
    {
      "itemId": 4,
      "actualQuantity": 8,
      "remark": "少了2个，可能是出库未记录"
    },
    {
      "itemId": 7,
      "actualQuantity": 480,
      "remark": "少了20个，可能是损耗"
    }
  ]
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "提交成功",
  "data": {
    "id": 3,
    "checkCode": "IC-2024-003",
    "warehouseId": 1,
    "warehouseName": "泰州仓",
    "checkerId": 3,
    "checkerName": "李四",
    "checkTime": "2024-05-25T09:00:00.000Z",
    "status": 1,
    "description": "泰州仓月度盘点",
    "createTime": "2024-05-20T10:30:00.000Z",
    "updateTime": "2024-05-25T11:30:00.000Z",
    "details": [
      {
        "id": 1,
        "itemId": 1,
        "itemName": "气相色谱仪",
        "systemQuantity": 2,
        "actualQuantity": 2,
        "difference": 0,
        "remark": "数量正确"
      },
      {
        "id": 2,
        "itemId": 4,
        "itemName": "GC色谱柱",
        "systemQuantity": 10,
        "actualQuantity": 8,
        "difference": -2,
        "remark": "少了2个，可能是出库未记录"
      },
      {
        "id": 3,
        "itemId": 7,
        "itemName": "样品瓶",
        "systemQuantity": 500,
        "actualQuantity": 480,
        "difference": -20,
        "remark": "少了20个，可能是损耗"
      }
    ]
  }
}
```

#### 5.3 获取盘库记录

- **接口说明**：获取盘库记录列表
- **请求方式**：GET
- **请求路径**：`/api/v1/warehouse/inventory/checks`
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| warehouseId | number | 否   | 仓库ID，不传则查询所有仓库 |
| status     | number | 否   | 状态（0-进行中，1-已完成），不传则查询所有状态 |
| startTime  | string | 否   | 开始时间（ISO 8601格式） |
| endTime    | string | 否   | 结束时间（ISO 8601格式） |
| page       | number | 否   | 页码，默认为1 |
| size       | number | 否   | 每页数量，默认为20 |

- **响应参数**：

| 参数名     | 类型   | 描述                |
| ---------- | ------ | ------------------- |
| total      | number | 总记录数            |
| pages      | number | 总页数              |
| current    | number | 当前页码            |
| size       | number | 每页数量            |
| checks     | array  | 盘库记录列表        |
| └ id       | number | 盘库ID              |
| └ checkCode | string | 盘库编号           |
| └ warehouseId | number | 仓库ID           |
| └ warehouseName | string | 仓库名称       |
| └ checkerId | number | 盘库人ID           |
| └ checkerName | string | 盘库人姓名       |
| └ checkTime | string | 盘库时间           |
| └ status   | number | 状态（0-进行中，1-已完成）|
| └ description | string | 描述/备注        |
| └ createTime | string | 创建时间          |
| └ updateTime | string | 更新时间          |
| └ itemCount | number | 盘点物品数量       |
| └ differenceCount | number | 差异物品数量 |

- **响应示例**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 5,
    "pages": 1,
    "current": 1,
    "size": 20,
    "checks": [
      {
        "id": 3,
        "checkCode": "IC-2024-003",
        "warehouseId": 1,
        "warehouseName": "泰州仓",
        "checkerId": 3,
        "checkerName": "李四",
        "checkTime": "2024-05-25T09:00:00.000Z",
        "status": 1,
        "description": "泰州仓月度盘点",
        "createTime": "2024-05-20T10:30:00.000Z",
        "updateTime": "2024-05-25T11:30:00.000Z",
        "itemCount": 3,
        "differenceCount": 2
      },
      {
        "id": 2,
        "checkCode": "IC-2024-002",
        "warehouseId": 2,
        "warehouseName": "苏州仓",
        "checkerId": 3,
        "checkerName": "李四",
        "checkTime": "2024-04-28T09:00:00.000Z",
        "status": 1,
        "description": "苏州仓月度盘点",
        "createTime": "2024-04-25T10:30:00.000Z",
        "updateTime": "2024-04-28T11:30:00.000Z",
        "itemCount": 5,
        "differenceCount": 1
      }
      // 更多记录...
    ]
  }
}
```

#### 5.4 获取盘库详情

- **接口说明**：获取盘库详细信息
- **请求方式**：GET
- **请求路径**：`/api/v1/warehouse/inventory/check/{id}`
- **请求参数**：

| 参数名 | 类型   | 必填 | 描述   |
| ------ | ------ | ---- | ------ |
| id     | number | 是   | 盘库ID |

- **响应参数**：

| 参数名     | 类型   | 描述                |
| ---------- | ------ | ------------------- |
| id         | number | 盘库ID              |
| checkCode  | string | 盘库编号            |
| warehouseId | number | 仓库ID             |
| warehouseName | string | 仓库名称         |
| checkerId  | number | 盘库人ID            |
| checkerName | string | 盘库人姓名         |
| checkTime  | string | 盘库时间            |
| status     | number | 状态（0-进行中，1-已完成）|
| description | string | 描述/备注          |
| createTime | string | 创建时间            |
| updateTime | string | 更新时间            |
| details    | array  | 盘库明细            |
| └ id       | number | 明细ID              |
| └ itemId   | number | 物品ID              |
| └ itemCode | string | 物品编号            |
| └ itemName | string | 物品名称            |
| └ categoryName | string | 分类名称        |
| └ model    | string | 规格型号            |
| └ systemQuantity | number | 系统数量      |
| └ actualQuantity | number | 实际数量      |
| └ difference | number | 差异数量          |
| └ remark   | string | 备注                |

- **响应示例**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 3,
    "checkCode": "IC-2024-003",
    "warehouseId": 1,
    "warehouseName": "泰州仓",
    "checkerId": 3,
    "checkerName": "李四",
    "checkTime": "2024-05-25T09:00:00.000Z",
    "status": 1,
    "description": "泰州仓月度盘点",
    "createTime": "2024-05-20T10:30:00.000Z",
    "updateTime": "2024-05-25T11:30:00.000Z",
    "details": [
      {
        "id": 1,
        "itemId": 1,
        "itemCode": "INS-2024-001",
        "itemName": "气相色谱仪",
        "categoryName": "仪器库",
        "model": "GC-2000",
        "systemQuantity": 2,
        "actualQuantity": 2,
        "difference": 0,
        "remark": "数量正确"
      },
      {
        "id": 2,
        "itemId": 4,
        "itemCode": "PART-2024-001",
        "itemName": "GC色谱柱",
        "categoryName": "配件库",
        "model": "DB-5MS",
        "systemQuantity": 10,
        "actualQuantity": 8,
        "difference": -2,
        "remark": "少了2个，可能是出库未记录"
      },
      {
        "id": 3,
        "itemId": 7,
        "itemCode": "CON-2024-001",
        "itemName": "样品瓶",
        "categoryName": "耗材库",
        "model": "2mL",
        "systemQuantity": 500,
        "actualQuantity": 480,
        "difference": -20,
        "remark": "少了20个，可能是损耗"
      }
    ]
  }
}
```

### 6. 统计报表API

#### 6.1 获取库存统计

- **接口说明**：获取库存统计信息
- **请求方式**：GET
- **请求路径**：`/api/v1/warehouse/stats/inventory`
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| warehouseId | number | 否   | 仓库ID，不传则查询所有仓库 |
| categoryId | number | 否   | 分类ID，不传则查询所有分类 |

- **响应参数**：

| 参数名     | 类型   | 描述                |
| ---------- | ------ | ------------------- |
| totalItems | number | 物品总数            |
| totalValue | number | 总价值              |
| categoryStats | array | 分类统计          |
| └ categoryId | number | 分类ID            |
| └ categoryName | string | 分类名称        |
| └ itemCount | number | 物品数量           |
| └ totalValue | number | 总价值            |
| warehouseStats | array | 仓库统计         |
| └ warehouseId | number | 仓库ID           |
| └ warehouseName | string | 仓库名称       |
| └ itemCount | number | 物品数量           |
| └ totalValue | number | 总价值            |

- **响应示例**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "totalItems": 12,
    "totalValue": 782500.00,
    "categoryStats": [
      {
        "categoryId": 1,
        "categoryName": "仪器库",
        "itemCount": 3,
        "totalValue": 580000.00
      },
      {
        "categoryId": 2,
        "categoryName": "配件库",
        "itemCount": 3,
        "totalValue": 57000.00
      },
      {
        "categoryId": 3,
        "categoryName": "耗材库",
        "itemCount": 3,
        "totalValue": 15500.00
      },
      {
        "categoryId": 4,
        "categoryName": "固定资产库",
        "itemCount": 3,
        "totalValue": 130000.00
      }
    ],
    "warehouseStats": [
      {
        "warehouseId": 1,
        "warehouseName": "泰州仓",
        "itemCount": 4,
        "totalValue": 325500.00
      },
      {
        "warehouseId": 2,
        "warehouseName": "苏州仓",
        "itemCount": 4,
        "totalValue": 220200.00
      },
      {
        "warehouseId": 3,
        "warehouseName": "武汉仓",
        "itemCount": 4,
        "totalValue": 236800.00
      }
    ]
  }
}
```

#### 6.2 获取出入库统计

- **接口说明**：获取出入库统计信息
- **请求方式**：GET
- **请求路径**：`/api/v1/warehouse/stats/stock`
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| warehouseId | number | 否   | 仓库ID，不传则查询所有仓库 |
| categoryId | number | 否   | 分类ID，不传则查询所有分类 |
| startTime  | string | 是   | 开始时间（ISO 8601格式） |
| endTime    | string | 是   | 结束时间（ISO 8601格式） |
| groupBy    | string | 否   | 分组方式（day-按天，month-按月，year-按年），默认为month |

- **响应参数**：

| 参数名     | 类型   | 描述                |
| ---------- | ------ | ------------------- |
| inStats    | array  | 入库统计            |
| └ date     | string | 日期（根据groupBy格式不同）|
| └ count    | number | 入库次数            |
| └ quantity | number | 入库总数量          |
| └ value    | number | 入库总价值          |
| outStats   | array  | 出库统计            |
| └ date     | string | 日期（根据groupBy格式不同）|
| └ count    | number | 出库次数            |
| └ quantity | number | 出库总数量          |
| └ value    | number | 出库总价值          |
| categoryStats | array | 分类统计          |
| └ categoryId | number | 分类ID            |
| └ categoryName | string | 分类名称        |
| └ inCount  | number | 入库次数            |
| └ inQuantity | number | 入库总数量        |
| └ inValue  | number | 入库总价值          |
| └ outCount | number | 出库次数            |
| └ outQuantity | number | 出库总数量       |
| └ outValue | number | 出库总价值          |

- **响应示例**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "inStats": [
      {
        "date": "2024-01",
        "count": 5,
        "quantity": 520,
        "value": 183500.00
      },
      {
        "date": "2024-02",
        "count": 3,
        "quantity": 108,
        "value": 189200.00
      },
      {
        "date": "2024-03",
        "count": 4,
        "quantity": 1004,
        "value": 285500.00
      }
    ],
    "outStats": [
      {
        "date": "2024-01",
        "count": 2,
        "quantity": 5,
        "value": 17500.00
      },
      {
        "date": "2024-02",
        "count": 3,
        "quantity": 12,
        "value": 12600.00
      },
      {
        "date": "2024-03",
        "count": 5,
        "quantity": 23,
        "value": 18500.00
      }
    ],
    "categoryStats": [
      {
        "categoryId": 1,
        "categoryName": "仪器库",
        "inCount": 3,
        "inQuantity": 4,
        "inValue": 580000.00,
        "outCount": 0,
        "outQuantity": 0,
        "outValue": 0.00
      },
      {
        "categoryId": 2,
        "categoryName": "配件库",
        "inCount": 3,
        "inQuantity": 17,
        "inValue": 57000.00,
        "outCount": 5,
        "outQuantity": 10,
        "outValue": 35000.00
      },
      {
        "categoryId": 3,
        "categoryName": "耗材库",
        "inCount": 6,
        "inQuantity": 1600,
        "inValue": 15500.00,
        "outCount": 5,
        "outQuantity": 30,
        "outValue": 150.00
      }
    ]
  }
}
```

#### 6.3 获取物品使用统计

- **接口说明**：获取物品使用统计信息
- **请求方式**：GET
- **请求路径**：`/api/v1/warehouse/stats/usage`
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| itemId     | number | 否   | 物品ID，不传则查询所有物品 |
| startTime  | string | 是   | 开始时间（ISO 8601格式） |
| endTime    | string | 是   | 结束时间（ISO 8601格式） |
| groupBy    | string | 否   | 分组方式（task-按任务，user-按用户），默认为task |

- **响应参数**：

| 参数名     | 类型   | 描述                |
| ---------- | ------ | ------------------- |
| topItems   | array  | 使用量最多的物品    |
| └ itemId   | number | 物品ID              |
| └ itemName | string | 物品名称            |
| └ quantity | number | 使用数量            |
| └ percentage | number | 使用占比（百分比）|
| taskStats  | array  | 按任务统计（groupBy=task时）|
| └ taskId   | string | 任务ID              |
| └ taskName | string | 任务名称            |
| └ quantity | number | 使用数量            |
| └ value    | number | 使用价值            |
| userStats  | array  | 按用户统计（groupBy=user时）|
| └ userId   | number | 用户ID              |
| └ userName | string | 用户姓名            |
| └ quantity | number | 使用数量            |
| └ value    | number | 使用价值            |

- **响应示例**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "topItems": [
      {
        "itemId": 7,
        "itemName": "样品瓶",
        "quantity": 20,
        "percentage": 40.0
      },
      {
        "itemId": 4,
        "itemName": "GC色谱柱",
        "quantity": 10,
        "percentage": 20.0
      },
      {
        "itemId": 8,
        "itemName": "进样针",
        "quantity": 5,
        "percentage": 10.0
      }
    ],
    "taskStats": [
      {
        "taskId": "RP-2024-001",
        "taskName": "气相色谱仪维修",
        "quantity": 15,
        "value": 38500.00
      },
      {
        "taskId": "MT-2024-001",
        "taskName": "液相色谱仪保养",
        "quantity": 8,
        "value": 4350.00
      },
      {
        "taskId": "RP-2024-002",
        "taskName": "质谱仪维修",
        "quantity": 12,
        "value": 15250.00
      }
    ]
  }
}
```

### 7. 二维码API

#### 7.1 解析二维码

- **接口说明**：解析二维码内容，获取对应的物品信息
- **请求方式**：POST
- **请求路径**：`/api/v1/warehouse/qrcode/parse`
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| content    | string | 是   | 二维码内容 |

- **请求示例**：

```json
{
  "content": "A1INS-2024-001"
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "解析成功",
  "data": {
    "warehouseCode": "A",
    "warehouseName": "泰州仓",
    "categoryCode": "1",
    "categoryName": "仪器库",
    "itemCode": "INS-2024-001",
    "item": {
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
      "status": 1
    }
  }
}
``` 