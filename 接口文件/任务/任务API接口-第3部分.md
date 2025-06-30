# 任务模块API文档（续）

## 统计和报表相关接口

### 11. 获取任务统计信息

- **接口说明**: 获取任务统计数据
- **请求方式**: GET
- **请求路径**: `/api/v1/task/statistics`
- **请求参数**:

| 参数名     | 类型    | 必填 | 描述                                         |
|-----------|--------|------|-------------------------------------------|
| startDate | string | 否   | 开始日期(格式：YYYY-MM-DD)，默认30天前           |
| endDate   | string | 否   | 结束日期(格式：YYYY-MM-DD)，默认当前日期          |
| type      | string | 否   | 统计类型(daily/weekly/monthly)，默认daily      |

- **响应参数**:

| 参数名                  | 类型    | 描述                                      |
|------------------------|--------|-----------------------------------------|
| overview               | object | 总览数据                                   |
| overview.total         | number | 总任务数                                   |
| overview.completed     | number | 已完成任务数                                |
| overview.inProgress    | number | 进行中任务数                                |
| overview.pending       | number | 待处理任务数                                |
| overview.cancelled     | number | 已取消任务数                                |
| overview.completionRate| number | 完成率                                    |
| overview.avgDuration   | number | 平均处理时长(小时)                           |
| typeDistribution       | array  | 任务类型分布                                |
| typeDistribution[].type| string | 任务类型                                   |
| typeDistribution[].count| number| 数量                                      |
| typeDistribution[].percent| number| 百分比                                   |
| trend                  | array  | 趋势数据                                   |
| trend[].date           | string | 日期                                      |
| trend[].new            | number | 新增任务数                                 |
| trend[].completed      | number | 完成任务数                                 |
| priorityDistribution   | array  | 优先级分布                                 |
| priorityDistribution[].priority| string | 优先级                             |
| priorityDistribution[].count| number | 数量                                 |

- **响应示例**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "overview": {
      "total": 150,
      "completed": 105,
      "inProgress": 30,
      "pending": 10,
      "cancelled": 5,
      "completionRate": 0.7,
      "avgDuration": 24.5
    },
    "typeDistribution": [
      {
        "type": "repair",
        "count": 60,
        "percent": 0.4
      },
      {
        "type": "maintenance",
        "count": 45,
        "percent": 0.3
      },
      {
        "type": "installation",
        "count": 20,
        "percent": 0.133
      },
      {
        "type": "training",
        "count": 15,
        "percent": 0.1
      },
      {
        "type": "verification",
        "count": 10,
        "percent": 0.067
      }
    ],
    "trend": [
      {
        "date": "2024-05-01",
        "new": 5,
        "completed": 4
      },
      {
        "date": "2024-05-02",
        "new": 7,
        "completed": 6
      },
      // 更多日期...
    ],
    "priorityDistribution": [
      {
        "priority": "high",
        "count": 45
      },
      {
        "priority": "normal",
        "count": 95
      },
      {
        "priority": "low",
        "count": 10
      }
    ]
  }
}
```

### 12. 获取工程师工作量统计

- **接口说明**: 获取工程师工作量统计数据
- **请求方式**: GET
- **请求路径**: `/api/v1/task/statistics/engineer`
- **请求参数**:

| 参数名     | 类型    | 必填 | 描述                                          |
|-----------|--------|------|----------------------------------------------|
| startDate | string | 否   | 开始日期(格式：YYYY-MM-DD)，默认30天前            |
| endDate   | string | 否   | 结束日期(格式：YYYY-MM-DD)，默认当前日期           |
| engineerId| number | 否   | 工程师ID，不传则返回所有工程师数据                  |

- **响应参数**:

| 参数名                       | 类型    | 描述                                      |
|-----------------------------|--------|-----------------------------------------|
| engineers                   | array  | 工程师数组                                 |
| engineers[].id              | number | 工程师ID                                  |
| engineers[].name            | string | 工程师姓名                                 |
| engineers[].avatar          | string | 工程师头像                                 |
| engineers[].department      | string | 部门                                      |
| engineers[].taskCount       | number | 任务总数                                   |
| engineers[].completedCount  | number | 已完成任务数                                |
| engineers[].pendingCount    | number | 待处理任务数                                |
| engineers[].inProgressCount | number | 进行中任务数                                |
| engineers[].avgDuration     | number | 平均处理时长(小时)                           |
| engineers[].satisfaction    | number | 客户满意度(1-5)                            |
| engineers[].taskTypes       | array  | 任务类型统计                                |
| engineers[].taskTypes[].type| string | 任务类型                                   |
| engineers[].taskTypes[].count| number| 数量                                      |

- **响应示例**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "engineers": [
      {
        "id": 2,
        "name": "张三",
        "avatar": "https://example.com/avatars/zhangsan.png",
        "department": "技术部",
        "taskCount": 35,
        "completedCount": 28,
        "pendingCount": 2,
        "inProgressCount": 5,
        "avgDuration": 22.5,
        "satisfaction": 4.8,
        "taskTypes": [
          {
            "type": "repair",
            "count": 20
          },
          {
            "type": "maintenance",
            "count": 10
          },
          {
            "type": "installation",
            "count": 5
          }
        ]
      },
      {
        "id": 5,
        "name": "王五",
        "avatar": "https://example.com/avatars/wangwu.png",
        "department": "技术部",
        "taskCount": 30,
        "completedCount": 25,
        "pendingCount": 1,
        "inProgressCount": 4,
        "avgDuration": 24.2,
        "satisfaction": 4.5,
        "taskTypes": [
          {
            "type": "repair",
            "count": 15
          },
          {
            "type": "maintenance",
            "count": 12
          },
          {
            "type": "verification",
            "count": 3
          }
        ]
      }
    ]
  }
}
```

### 13. 获取客户相关任务统计

- **接口说明**: 获取客户相关任务统计数据
- **请求方式**: GET
- **请求路径**: `/api/v1/task/statistics/customer`
- **请求参数**:

| 参数名     | 类型    | 必填 | 描述                                          |
|-----------|--------|------|----------------------------------------------|
| startDate | string | 否   | 开始日期(格式：YYYY-MM-DD)，默认30天前            |
| endDate   | string | 否   | 结束日期(格式：YYYY-MM-DD)，默认当前日期           |
| customerId| number | 否   | 客户ID，不传则返回所有客户数据                      |

- **响应参数**:

| 参数名                        | 类型    | 描述                                      |
|------------------------------|--------|-----------------------------------------|
| customers                    | array  | 客户数组                                   |
| customers[].id               | number | 客户ID                                    |
| customers[].name             | string | 客户名称                                   |
| customers[].level            | string | 客户级别                                   |
| customers[].taskCount        | number | 任务总数                                   |
| customers[].completedCount   | number | 已完成任务数                                |
| customers[].pendingCount     | number | 待处理任务数                                |
| customers[].inProgressCount  | number | 进行中任务数                                |
| customers[].cancelledCount   | number | 已取消任务数                                |
| customers[].avgResponseTime  | number | 平均响应时间(小时)                           |
| customers[].avgCompletionTime| number | 平均完成时间(小时)                           |
| customers[].taskTypes        | array  | 任务类型统计                                |
| customers[].taskTypes[].type | string | 任务类型                                   |
| customers[].taskTypes[].count| number | 数量                                      |

- **响应示例**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "customers": [
      {
        "id": 1,
        "name": "上海医疗器械有限公司",
        "level": "A",
        "taskCount": 25,
        "completedCount": 20,
        "pendingCount": 1,
        "inProgressCount": 4,
        "cancelledCount": 0,
        "avgResponseTime": 2.5,
        "avgCompletionTime": 26.8,
        "taskTypes": [
          {
            "type": "repair",
            "count": 12
          },
          {
            "type": "maintenance",
            "count": 8
          },
          {
            "type": "verification",
            "count": 5
          }
        ]
      },
      {
        "id": 2,
        "name": "北京科研设备有限公司",
        "level": "B",
        "taskCount": 18,
        "completedCount": 15,
        "pendingCount": 0,
        "inProgressCount": 2,
        "cancelledCount": 1,
        "avgResponseTime": 3.2,
        "avgCompletionTime": 28.5,
        "taskTypes": [
          {
            "type": "repair",
            "count": 8
          },
          {
            "type": "maintenance",
            "count": 6
          },
          {
            "type": "installation",
            "count": 4
          }
        ]
      }
    ]
  }
}
```

### 14. 获取设备故障统计

- **接口说明**: 获取设备故障统计数据
- **请求方式**: GET
- **请求路径**: `/api/v1/task/statistics/fault`
- **请求参数**:

| 参数名     | 类型    | 必填 | 描述                                          |
|-----------|--------|------|----------------------------------------------|
| startDate | string | 否   | 开始日期(格式：YYYY-MM-DD)，默认30天前            |
| endDate   | string | 否   | 结束日期(格式：YYYY-MM-DD)，默认当前日期           |
| deviceType| string | 否   | 设备类型，不传则返回所有设备类型                    |

- **响应参数**:

| 参数名                           | 类型    | 描述                                      |
|--------------------------------|--------|-----------------------------------------|
| totalFaults                    | number | 故障总数                                   |
| deviceTypes                    | array  | 设备类型故障统计                             |
| deviceTypes[].type             | string | 设备类型                                   |
| deviceTypes[].count            | number | 故障数量                                   |
| deviceTypes[].percent          | number | 百分比                                     |
| brandDistribution              | array  | 品牌故障分布                                |
| brandDistribution[].brand      | string | 设备品牌                                   |
| brandDistribution[].count      | number | 故障数量                                   |
| faultTypes                     | array  | 故障类型统计                                |
| faultTypes[].type              | string | 故障类型                                   |
| faultTypes[].count             | number | 数量                                      |
| faultTypes[].percent           | number | 百分比                                     |
| avgRepairTime                  | object | 平均维修时间统计                             |
| avgRepairTime.overall          | number | 总体平均维修时间(小时)                        |
| avgRepairTime.byDeviceType     | array  | 按设备类型统计的平均维修时间                    |
| avgRepairTime.byDeviceType[].type | string | 设备类型                                 |
| avgRepairTime.byDeviceType[].time | number | 平均维修时间(小时)                         |

- **响应示例**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "totalFaults": 85,
    "deviceTypes": [
      {
        "type": "气相色谱仪",
        "count": 22,
        "percent": 0.259
      },
      {
        "type": "液相色谱仪",
        "count": 18,
        "percent": 0.212
      },
      {
        "type": "质谱仪",
        "count": 15,
        "percent": 0.176
      },
      {
        "type": "光谱仪",
        "count": 12,
        "percent": 0.141
      },
      {
        "type": "其他",
        "count": 18,
        "percent": 0.212
      }
    ],
    "brandDistribution": [
      {
        "brand": "Agilent",
        "count": 28
      },
      {
        "brand": "Thermo Fisher",
        "count": 22
      },
      {
        "brand": "Shimadzu",
        "count": 15
      },
      {
        "brand": "Waters",
        "count": 12
      },
      {
        "brand": "其他",
        "count": 8
      }
    ],
    "faultTypes": [
      {
        "type": "电源故障",
        "count": 25,
        "percent": 0.294
      },
      {
        "type": "软件问题",
        "count": 18,
        "percent": 0.212
      },
      {
        "type": "机械部件损坏",
        "count": 16,
        "percent": 0.188
      },
      {
        "type": "校准偏差",
        "count": 14,
        "percent": 0.165
      },
      {
        "type": "其他故障",
        "count": 12,
        "percent": 0.141
      }
    ],
    "avgRepairTime": {
      "overall": 18.5,
      "byDeviceType": [
        {
          "type": "气相色谱仪",
          "time": 22.3
        },
        {
          "type": "液相色谱仪",
          "time": 20.1
        },
        {
          "type": "质谱仪",
          "time": 24.5
        },
        {
          "type": "光谱仪",
          "time": 16.8
        }
      ]
    }
  }
}
```

### 15. 导出任务报表

- **接口说明**: 导出任务数据报表
- **请求方式**: GET
- **请求路径**: `/api/v1/task/export`
- **请求参数**:

| 参数名     | 类型    | 必填 | 描述                                          |
|-----------|--------|------|----------------------------------------------|
| startDate | string | 否   | 开始日期(格式：YYYY-MM-DD)，默认30天前            |
| endDate   | string | 否   | 结束日期(格式：YYYY-MM-DD)，默认当前日期           |
| taskType  | string | 否   | 任务类型，不传则包含所有类型                       |
| status    | string | 否   | 任务状态，不传则包含所有状态                       |
| format    | string | 否   | 导出格式(excel/csv/pdf)，默认excel               |

- **响应参数**:

| 参数名      | 类型    | 描述                                |
|------------|--------|------------------------------------|
| url        | string | 报表下载URL                          |
| expires    | string | URL过期时间                          |
| format     | string | 报表格式                             |
| recordCount| number | 记录数量                             |

- **响应示例**:

```json
{
  "code": 200,
  "message": "报表生成成功",
  "data": {
    "url": "https://example.com/reports/task-report-20240601-123456.xlsx",
    "expires": "2024-06-01T14:30:00",
    "format": "excel",
    "recordCount": 150
  }
}
``` 