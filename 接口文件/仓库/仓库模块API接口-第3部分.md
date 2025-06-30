### 3. 出入库API

#### 3.1 物品入库

- **接口说明**：物品入库操作
- **请求方式**：POST
- **请求路径**：`/api/v1/warehouse/stock/in`
- **权限要求**：系统管理员、仓库管理员
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| itemId     | number | 是   | 物品ID |
| quantity   | number | 是   | 入库数量 |
| operationTime | string | 是   | 操作时间（ISO 8601格式） |
| taskId     | string | 否   | 关联任务ID |
| description | string | 否   | 描述/备注 |

- **请求示例**：

```json
{
  "itemId": 1,
  "quantity": 2,
  "operationTime": "2024-05-20T10:30:00.000Z",
  "description": "新购入两台气相色谱仪"
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "入库成功",
  "data": {
    "id": 10,
    "recordCode": "IN-2024-010",
    "itemId": 1,
    "recordType": 1,
    "quantity": 2,
    "operatorId": 3,
    "operatorName": "李四",
    "operationTime": "2024-05-20T10:30:00.000Z",
    "taskId": null,
    "description": "新购入两台气相色谱仪",
    "createTime": "2024-05-20T10:30:00.000Z"
  }
}
```

#### 3.2 物品出库

- **接口说明**：物品出库操作
- **请求方式**：POST
- **请求路径**：`/api/v1/warehouse/stock/out`
- **权限要求**：系统管理员、仓库管理员
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| itemId     | number | 是   | 物品ID |
| quantity   | number | 是   | 出库数量 |
| operationTime | string | 是   | 操作时间（ISO 8601格式） |
| taskId     | string | 否   | 关联任务ID |
| description | string | 否   | 描述/备注 |

- **请求示例**：

```json
{
  "itemId": 4,
  "quantity": 2,
  "operationTime": "2024-05-20T14:30:00.000Z",
  "taskId": "RP-2024-001",
  "description": "用于气相色谱仪维修"
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "出库成功",
  "data": {
    "id": 11,
    "recordCode": "OUT-2024-005",
    "itemId": 4,
    "recordType": 2,
    "quantity": 2,
    "operatorId": 3,
    "operatorName": "李四",
    "operationTime": "2024-05-20T14:30:00.000Z",
    "taskId": "RP-2024-001",
    "description": "用于气相色谱仪维修",
    "createTime": "2024-05-20T14:30:00.000Z"
  }
}
```

#### 3.3 获取出入库记录

- **接口说明**：获取物品出入库记录
- **请求方式**：GET
- **请求路径**：`/api/v1/warehouse/stock/records`
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| itemId     | number | 否   | 物品ID，不传则查询所有物品 |
| recordType | number | 否   | 记录类型（1-入库，2-出库），不传则查询所有类型 |
| warehouseId | number | 否   | 仓库ID，不传则查询所有仓库 |
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
| records    | array  | 记录列表            |
| └ id       | number | 记录ID              |
| └ recordCode | string | 记录编号          |
| └ itemId   | number | 物品ID              |
| └ itemCode | string | 物品编号            |
| └ itemName | string | 物品名称            |
| └ warehouseId | number | 仓库ID           |
| └ warehouseName | string | 仓库名称       |
| └ recordType | number | 记录类型（1-入库，2-出库）|
| └ quantity | number | 数量                |
| └ operatorId | number | 操作人ID          |
| └ operatorName | string | 操作人姓名      |
| └ operationTime | string | 操作时间       |
| └ taskId   | string | 关联任务ID          |
| └ description | string | 描述/备注        |
| └ createTime | string | 创建时间          |

- **响应示例**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 15,
    "pages": 1,
    "current": 1,
    "size": 20,
    "records": [
      {
        "id": 11,
        "recordCode": "OUT-2024-005",
        "itemId": 4,
        "itemCode": "PART-2024-001",
        "itemName": "GC色谱柱",
        "warehouseId": 1,
        "warehouseName": "泰州仓",
        "recordType": 2,
        "quantity": 2,
        "operatorId": 3,
        "operatorName": "李四",
        "operationTime": "2024-05-20T14:30:00.000Z",
        "taskId": "RP-2024-001",
        "description": "用于气相色谱仪维修",
        "createTime": "2024-05-20T14:30:00.000Z"
      },
      {
        "id": 10,
        "recordCode": "IN-2024-010",
        "itemId": 1,
        "itemCode": "INS-2024-001",
        "itemName": "气相色谱仪",
        "warehouseId": 1,
        "warehouseName": "泰州仓",
        "recordType": 1,
        "quantity": 2,
        "operatorId": 3,
        "operatorName": "李四",
        "operationTime": "2024-05-20T10:30:00.000Z",
        "taskId": null,
        "description": "新购入两台气相色谱仪",
        "createTime": "2024-05-20T10:30:00.000Z"
      }
      // 更多记录...
    ]
  }
}
```

### 4. 物品申请API

#### 4.1 提交物品使用申请

- **接口说明**：提交物品使用申请
- **请求方式**：POST
- **请求路径**：`/api/v1/warehouse/request/use`
- **权限要求**：工程师、客户经理
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| itemId     | number | 是   | 物品ID |
| quantity   | number | 是   | 申请数量 |
| taskId     | string | 否   | 关联任务ID |
| purpose    | string | 是   | 用途 |
| urgency    | number | 否   | 紧急程度（1-普通，2-紧急，3-特急），默认为1 |

- **请求示例**：

```json
{
  "itemId": 4,
  "quantity": 1,
  "taskId": "RP-2024-001",
  "purpose": "用于气相色谱仪维修",
  "urgency": 2
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "申请提交成功",
  "data": {
    "id": 5,
    "requestCode": "REQ-USE-2024-005",
    "requesterId": 2,
    "requesterName": "张三",
    "requestType": 1,
    "status": 0,
    "itemId": 4,
    "itemName": "GC色谱柱",
    "itemModel": "DB-5MS",
    "quantity": 1,
    "taskId": "RP-2024-001",
    "purpose": "用于气相色谱仪维修",
    "urgency": 2,
    "createTime": "2024-05-20T15:30:00.000Z"
  }
}
```

#### 4.2 提交物品采购申请

- **接口说明**：提交物品采购申请
- **请求方式**：POST
- **请求路径**：`/api/v1/warehouse/request/purchase`
- **权限要求**：工程师、客户经理、仓库管理员
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| itemName   | string | 是   | 物品名称 |
| itemModel  | string | 是   | 规格型号 |
| quantity   | number | 是   | 申请数量 |
| taskId     | string | 否   | 关联任务ID |
| purpose    | string | 是   | 用途 |
| urgency    | number | 否   | 紧急程度（1-普通，2-紧急，3-特急），默认为1 |

- **请求示例**：

```json
{
  "itemName": "质谱仪离子源",
  "itemModel": "ESI-5000 Plus",
  "quantity": 1,
  "taskId": "RP-2024-002",
  "purpose": "更换损坏的离子源",
  "urgency": 3
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "申请提交成功",
  "data": {
    "id": 6,
    "requestCode": "REQ-PUR-2024-006",
    "requesterId": 2,
    "requesterName": "张三",
    "requestType": 2,
    "status": 0,
    "itemId": null,
    "itemName": "质谱仪离子源",
    "itemModel": "ESI-5000 Plus",
    "quantity": 1,
    "taskId": "RP-2024-002",
    "purpose": "更换损坏的离子源",
    "urgency": 3,
    "createTime": "2024-05-20T16:00:00.000Z"
  }
}
```

#### 4.3 获取申请列表

- **接口说明**：获取物品申请列表
- **请求方式**：GET
- **请求路径**：`/api/v1/warehouse/requests`
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| requestType | number | 否   | 申请类型（1-使用申请，2-采购申请），不传则查询所有类型 |
| status     | number | 否   | 状态（0-待审批，1-已同意，2-已拒绝），不传则查询所有状态 |
| requesterId | number | 否   | 申请人ID，不传则根据角色查询（管理员查询所有，普通用户查询自己的） |
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
| requests   | array  | 申请列表            |
| └ id       | number | 申请ID              |
| └ requestCode | string | 申请编号         |
| └ requesterId | number | 申请人ID         |
| └ requesterName | string | 申请人姓名     |
| └ requestType | number | 申请类型（1-使用申请，2-采购申请）|
| └ status   | number | 状态（0-待审批，1-已同意，2-已拒绝）|
| └ itemId   | number | 物品ID（使用申请）  |
| └ itemName | string | 物品名称            |
| └ itemModel | string | 规格型号           |
| └ quantity | number | 数量                |
| └ taskId   | string | 关联任务ID          |
| └ purpose  | string | 用途                |
| └ urgency  | number | 紧急程度（1-普通，2-紧急，3-特急）|
| └ approverId | number | 审批人ID          |
| └ approverName | string | 审批人姓名      |
| └ approvalTime | string | 审批时间        |
| └ approvalComment | string | 审批意见     |
| └ createTime | string | 创建时间          |

- **响应示例**：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 10,
    "pages": 1,
    "current": 1,
    "size": 20,
    "requests": [
      {
        "id": 6,
        "requestCode": "REQ-PUR-2024-006",
        "requesterId": 2,
        "requesterName": "张三",
        "requestType": 2,
        "status": 0,
        "itemId": null,
        "itemName": "质谱仪离子源",
        "itemModel": "ESI-5000 Plus",
        "quantity": 1,
        "taskId": "RP-2024-002",
        "purpose": "更换损坏的离子源",
        "urgency": 3,
        "approverId": null,
        "approverName": null,
        "approvalTime": null,
        "approvalComment": null,
        "createTime": "2024-05-20T16:00:00.000Z"
      },
      {
        "id": 5,
        "requestCode": "REQ-USE-2024-005",
        "requesterId": 2,
        "requesterName": "张三",
        "requestType": 1,
        "status": 0,
        "itemId": 4,
        "itemName": "GC色谱柱",
        "itemModel": "DB-5MS",
        "quantity": 1,
        "taskId": "RP-2024-001",
        "purpose": "用于气相色谱仪维修",
        "urgency": 2,
        "approverId": null,
        "approverName": null,
        "approvalTime": null,
        "approvalComment": null,
        "createTime": "2024-05-20T15:30:00.000Z"
      }
      // 更多申请...
    ]
  }
}
```

#### 4.4 处理申请

- **接口说明**：处理物品申请（同意或拒绝）
- **请求方式**：PUT
- **请求路径**：`/api/v1/warehouse/request/{id}/process`
- **权限要求**：系统管理员、仓库管理员
- **请求参数**：

| 参数名     | 类型   | 必填 | 描述   |
| ---------- | ------ | ---- | ------ |
| id         | number | 是   | 申请ID（路径参数） |
| status     | number | 是   | 处理结果（1-同意，2-拒绝） |
| comment    | string | 否   | 处理意见 |

- **请求示例**：

```json
{
  "status": 1,
  "comment": "已批准，请到仓库领取"
}
```

- **响应示例**：

```json
{
  "code": 200,
  "message": "处理成功",
  "data": {
    "id": 5,
    "requestCode": "REQ-USE-2024-005",
    "requesterId": 2,
    "requesterName": "张三",
    "requestType": 1,
    "status": 1,
    "itemId": 4,
    "itemName": "GC色谱柱",
    "itemModel": "DB-5MS",
    "quantity": 1,
    "taskId": "RP-2024-001",
    "purpose": "用于气相色谱仪维修",
    "urgency": 2,
    "approverId": 3,
    "approverName": "李四",
    "approvalTime": "2024-05-20T16:30:00.000Z",
    "approvalComment": "已批准，请到仓库领取",
    "createTime": "2024-05-20T15:30:00.000Z"
  }
}
``` 