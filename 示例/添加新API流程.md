# 添加新API的标准流程

本文档描述了在工程师APP项目中添加新API的标准流程和规范。遵循这些步骤可以确保前后端一致性和高质量的API设计。

## 一、API规划设计阶段

### 1. 需求分析
- 明确API的业务需求和功能目标
- 确定API的输入参数和输出结果
- 分析API可能的异常情况和处理方式

### 2. API设计
- 确定API路径，遵循RESTful设计原则
  - 基础路径格式: `/api/v1/{模块}/{功能}`
  - 例如: `/api/v1/user/info`, `/api/v1/task/list`
- 确定HTTP请求方法
  - GET: 获取资源
  - POST: 创建资源
  - PUT: 更新资源
  - DELETE: 删除资源
- 设计请求参数和响应结构
  - 参数命名采用驼峰命名法
  - 响应格式遵循统一标准

### 3. 编写API文档
- 创建或更新对应模块的API说明文档
- 文档应包含：
  - 接口说明、请求方式、请求路径
  - 请求参数详细说明（参数名、类型、是否必填、描述）
  - 响应参数详细说明（参数名、类型、描述）
  - 请求示例和响应示例

## 二、后端开发阶段

### 1. 数据库设计
- 确定API涉及的数据表和字段
- 如需新建表或添加字段，编写SQL脚本
- 更新数据库初始化或迁移脚本

### 2. 后端代码实现
- 创建Entity实体类
  ```java
  @Data
  @TableName("表名")
  public class XxxEntity {
      @TableId
      private Long id;
      private String fieldName;
      // 其他字段...
  }
  ```

- 创建DTO数据传输对象
  ```java
  @Data
  public class XxxDTO {
      private String fieldName;
      // 其他字段...
  }
  ```

- 创建Mapper接口
  ```java
  @Mapper
  public interface XxxMapper extends BaseMapper<XxxEntity> {
      // 自定义查询方法...
  }
  ```

- 创建Service接口和实现类
  ```java
  public interface XxxService {
      XxxDTO getXxx(Long id);
      // 其他方法...
  }
  
  @Service
  public class XxxServiceImpl implements XxxService {
      @Autowired
      private XxxMapper xxxMapper;
      
      @Override
      public XxxDTO getXxx(Long id) {
          // 实现逻辑...
      }
      // 其他方法实现...
  }
  ```

- 创建Controller
  ```java
  @RestController
  @RequestMapping("/api/v1/xxx")
  public class XxxController {
      @Autowired
      private XxxService xxxService;
      
      @GetMapping("/yyy")
      public Result<XxxDTO> getXxx(@RequestParam Long id) {
          XxxDTO data = xxxService.getXxx(id);
          return Result.success(data);
      }
      // 其他接口方法...
  }
  ```
- 创建实现类
- 创建Mapper.xml文件



### 3. 单元测试
- 编写Service层和Controller层的单元测试
- 使用Mockito模拟依赖
- 测试正常情况和异常情况

## 三、前端开发阶段

### 1. 创建API请求方法
- 在前端API服务模块中添加新的API请求方法
  ```javascript
  // api/xxxApi.js
  import http from '@/utils/http';

  export function getXxx(params) {
    return http.get('/api/v1/xxx/yyy', { params });
  }

  export function createXxx(data) {
    return http.post('/api/v1/xxx/yyy', data);
  }
  
  // 其他API方法...
  ```

### 2. 创建或更新界面组件
- 在相关页面或组件中引入API方法
- 实现数据获取、提交和展示逻辑
- 处理加载状态和错误提示

### 3. 前端测试
- 测试API调用是否正常
- 测试界面交互是否符合预期
- 测试异常情况下的提示是否友好

## 四、联调测试阶段

### 1. 前后端联调
- 确保前后端请求和响应格式一致
- 解决可能的跨域问题
- 调整参数格式和类型问题

### 2. 功能测试
- 测试API在各种场景下的表现
- 验证业务逻辑是否正确
- 检查数据一致性

### 3. 性能和安全测试
- 检查API响应时间
- 检查API的安全性（如参数验证、权限控制）
- 验证token认证是否正常

## 五、部署上线阶段

### 1. 更新API文档
- 确保文档与实际实现一致
- 更新可能的变更点

### 2. 代码合并
- 通过代码审查
- 合并到开发分支或主分支

### 3. 发布部署
- 部署后端服务
- 部署前端应用
- 监控首次上线情况

## 注意事项

1. **命名规范**
   - API路径使用小写字母和连字符
   - 参数和响应字段使用驼峰命名法
   - 保持前后端字段名称一致

2. **响应格式统一**
   - 所有API响应格式必须符合统一标准
   ```json
   {
     "code": 200,
     "message": "操作成功",
     "data": {}
   }
   ```

3. **错误处理**
   - 所有API必须处理可能的异常情况
   - 返回合适的错误码和错误消息

4. **版本管理**
   - API路径中包含版本号，便于后续版本迭代
   - 重大改动时考虑保留旧版本API

5. **安全考虑**
   - 敏感API必须进行身份验证和权限校验
   - 防止SQL注入和XSS攻击 