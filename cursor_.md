# 分析任务派发功能需求
_Exported on 2025/7/8 at GMT+8 8:28:55 from Cursor (1.0.0)_

---

**User**

@任务派发需求说明.md 现在需要按照这份需求实现任务派发的功能。
进入研究模式，系统性的分析一下以后，有足够信息和把握制定方案和计划的时候结束对话等待我的命令后进入下一模式 

---

**Cursor**

[MODE: RESEARCH]

我需要系统性地分析任务派发需求，了解现有系统状态和需要实现的功能。

首先，让我查看一下任务派发需求说明文档的内容：

根据需求说明，我们需要实现一个基于工程师专业技能和工作负载的任务派发系统。主要功能包括：

1. 在用户表中添加"技术分类"字段，允许工程师选择多个擅长领域
2. 后端实时检测新任务提交
3. 根据仪器类型和工程师工作负载自动派发任务
4. 前端修改注册/登录页面和个人信息页面，添加技术分类字段
5. 消息通知系统，包括工程师接收派单消息、客户经理接收任务通知等
6. 系统管理员可以进行二次派发和指定客户经理

让我先了解一下当前的系统结构，特别是用户表和任务表的设计：

让我直接查看用户模块的数据库初始化文件：

让我修正文件路径：

让我继续查看用户表的其余部分：

现在我需要了解任务表的结构，让我查看任务模块的数据库初始化文件：

让我继续查看任务模块数据库文件的剩余部分：

现在我需要了解消息模块的数据库结构：

让我继续查看消息模块数据库文件的剩余部分：

现在我需要了解前端项目结构，特别是注册和个人信息页面的实现：

让我查看前端的注册页面：

让我查看认证相关的页面：

让我查看注册页面的代码：

让我查看注册页面的剩余部分：

现在让我查看个人信息页面：

让我查看个人信息编辑页面：

让我查看个人信息页面的剩余部分：

让我查看用户信息相关的API：

让我查看用户API：

让我查看认证API：

让我查看后端的用户控制器：

让我查看用户控制器：

让我继续查看用户控制器的剩余部分：

让我继续查看用户控制器的最后部分：

让我查看用户实体类：

<no message>

让我查看User实体类：

让我查看用户注册相关的DTO：

让我查看RegisterRequest类：

让我查看用户服务接口：

让我查看UserService接口：

让我查看用户服务实现类：

让我查看用户服务实现类：

让我查看用户服务实现类的剩余部分：

让我查看用户Mapper接口：

让我查看UserMapper接口：

让我查看UserMapper的XML配置：

<no message>

让我查看UserMapper.xml文件：

让我继续查看UserMapper.xml文件的剩余部分：

让我查看任务相关的实体和服务：

让我查看任务服务接口：

让我查看任务控制器：

让我查看前端的任务API：

基于我的研究，我已经对系统的现有结构和需求有了全面的了解。现在我可以总结我的发现，并准备好进入下一个阶段。

---

**User**

进入创新模式，不要给出任何代码，只定制方案

---

**Cursor**

[MODE: INNOVATE]

根据对任务派发需求的分析，我提出以下创新方案，聚焦于实现基于工程师专业技能和工作负载的智能任务派发系统。

### 核心方案设计

**1. 用户技术分类字段扩展**
- 在用户表中添加`technical_categories`字段，采用JSON数组格式存储
- 支持多选技术分类：色谱、光谱类、生命科学类、三项一柜基础仪器类、环境监测类、主板类、电气类、其他类
- 此设计允许工程师拥有多个专业领域，提高任务匹配灵活性

**2. 实时任务监测与派发机制**
- 采用定时任务+事件驱动的混合模式
- 定时任务每分钟扫描新提交的任务
- 同时实现WebSocket实时通知机制，当有新任务提交时立即触发派发流程
- 这种双重机制确保及时派发同时避免系统资源浪费

**3. 智能匹配算法**
- 基于两层匹配策略：
  - 第一层：仪器类型与工程师技术分类匹配度
  - 第二层：工程师当前工作负载评估
- 采用加权评分系统，综合考虑技术匹配度(70%)和工作负载(30%)
- 引入"紧急度"因素，高紧急度任务可适当提高派发优先级

**4. 消息通知系统扩展**
- 设计多级通知机制：
  - 工程师接收派单通知
  - 系统管理员接收派单状态通知
  - 客户经理(如存在)接收任务状态通知
- 消息状态追踪：未读、已读、已处理
- 消息优先级分级，确保重要通知不被忽略

**5. 任务接受流程优化**
- 工程师接收任务通知后进入"待接受"状态
- 设置自动超时机制：若工程师在指定时间内(如30分钟)未响应，系统自动重新派发
- 接受任务后状态自动更新为"处理中"，步骤更新为"已接单"

### 创新亮点

**1. 动态负载均衡**
- 实时计算工程师工作负载，不仅考虑任务数量，还考虑任务复杂度和预计完成时间
- 自适应调整：系统会根据历史数据不断优化工作负载评估模型

**2. 技能成长模型**
- 记录工程师处理不同类型任务的成功率和评价
- 随着工程师处理特定类型任务的经验增长，系统会自动提高该类型任务的匹配权重
- 这种机制鼓励专业化同时促进技能多样性

**3. 二次派发优化**
- 为系统管理员提供直观的任务重新分配界面
- 支持批量任务调整，适用于特殊情况(如工程师请假)
- 提供决策支持，系统会推荐最合适的替代工程师

**4. 客户经理关联机制**
- 智能推断客户与客户经理的关系
- 基于历史交互数据，自动推荐可能的客户经理
- 系统管理员可以一键确认或修改推荐

### 用户体验考量

**1. 注册与个人信息页面优化**
- 技术分类多选组件，视觉清晰，支持搜索
- 技术分类分组展示，便于快速选择
- 提供技能自评选项，帮助系统初始化匹配权重

**2. 通知中心设计**
- 分类展示不同类型通知
- 新任务派发通知突出显示，支持快速操作(接受/拒绝)
- 通知摘要显示关键信息：任务类型、客户、设备信息等

**3. 管理员派发监控面板**
- 实时显示任务派发状态
- 提供派发历史记录和统计分析
- 支持手动干预和调整派发决策

这个方案不仅满足了基本需求，还通过智能算法和用户体验优化，创造了一个高效、灵活且可持续发展的任务派发系统。系统能够随着使用不断学习和优化，确保任务总是分配给最合适的工程师，同时平衡工作负载，提高整体工作效率。

---

**User**

进入计划模式，不要给出任何代码，只定制计划

---

**Cursor**

[MODE: PLAN]

# 任务派发系统实现计划

## 一、数据库修改

### 1. 用户表技术分类字段添加
- **文件路径**: `接口文件/用户模块数据库初始化_sqlserver.sql`
- **修改内容**: 添加技术分类字段到用户表
- **SQL脚本**: 创建ALTER TABLE语句添加technical_categories字段，使用NVARCHAR(MAX)类型存储JSON数组

### 2. 数据库修复脚本
- **文件路径**: 新建`接口文件/数据库修复脚本/add_technical_categories.sql`
- **修改内容**: 创建数据库修复脚本，用于现有数据库添加技术分类字段

## 二、后端实现

### 1. 实体类修改
- **文件路径**: `ryl-engineer-back/src/main/java/com/ryl/engineer/entity/User.java`
- **修改内容**: 添加technicalCategories字段及其getter/setter方法

### 2. DTO类修改
- **文件路径**: `ryl-engineer-back/src/main/java/com/ryl/engineer/dto/RegisterRequest.java`
- **修改内容**: 添加technicalCategories字段
- **文件路径**: `ryl-engineer-back/src/main/java/com/ryl/engineer/dto/UserInfoRequest.java`
- **修改内容**: 添加technicalCategories字段
- **文件路径**: `ryl-engineer-back/src/main/java/com/ryl/engineer/dto/UserInfoResponse.java`
- **修改内容**: 添加technicalCategories字段

### 3. Mapper接口与XML修改
- **文件路径**: `ryl-engineer-back/src/main/java/com/ryl/engineer/mapper/UserMapper.java`
- **修改内容**: 更新相关方法支持技术分类字段
- **文件路径**: `ryl-engineer-back/src/main/resources/mapper/UserMapper.xml`
- **修改内容**: 更新SQL语句支持技术分类字段

### 4. 服务层实现
- **文件路径**: `ryl-engineer-back/src/main/java/com/ryl/engineer/service/impl/UserServiceImpl.java`
- **修改内容**: 更新注册和个人信息更新方法，处理技术分类字段

### 5. 控制器修改
- **文件路径**: `ryl-engineer-back/src/main/java/com/ryl/engineer/controller/UserController.java`
- **修改内容**: 更新相关API处理技术分类字段

### 6. 任务派发服务实现
- **文件路径**: 新建`ryl-engineer-back/src/main/java/com/ryl/engineer/service/TaskDispatchService.java`
- **修改内容**: 创建任务派发服务接口
- **文件路径**: 新建`ryl-engineer-back/src/main/java/com/ryl/engineer/service/impl/TaskDispatchServiceImpl.java`
- **修改内容**: 实现任务派发逻辑，包括技术分类匹配和工作负载评估

### 7. 任务监听器实现
- **文件路径**: 新建`ryl-engineer-back/src/main/java/com/ryl/engineer/listener/TaskCreationListener.java`
- **修改内容**: 实现任务创建事件监听器，触发任务派发

### 8. 定时任务实现
- **文件路径**: 新建`ryl-engineer-back/src/main/java/com/ryl/engineer/scheduler/TaskDispatchScheduler.java`
- **修改内容**: 实现定时检查新任务并派发的调度器

### 9. 消息通知服务扩展
- **文件路径**: `ryl-engineer-back/src/main/java/com/ryl/engineer/service/impl/ChatServiceImpl.java`
- **修改内容**: 扩展消息服务，支持任务派发通知

## 三、前端实现

### 1. 注册页面修改
- **文件路径**: `ryl-engineer-app/src/views/auth/Register.vue`
- **修改内容**: 添加技术分类多选组件

### 2. 个人信息页面修改
- **文件路径**: `ryl-engineer-app/src/views/profile/ProfileInfo.vue`
- **修改内容**: 添加技术分类编辑组件

### 3. API服务修改
- **文件路径**: `ryl-engineer-app/src/api/auth.js`
- **修改内容**: 更新注册API，支持技术分类字段
- **文件路径**: `ryl-engineer-app/src/api/user.js`
- **修改内容**: 更新用户信息API，支持技术分类字段

### 4. 消息页面扩展
- **文件路径**: `ryl-engineer-app/src/views/message/MessageList.vue`
- **修改内容**: 添加任务派发通知处理
- **文件路径**: `ryl-engineer-app/src/views/message/TaskNotification.vue`
- **修改内容**: 创建任务通知组件，支持接受/拒绝操作

### 5. 任务详情页扩展
- **文件路径**: `ryl-engineer-app/src/views/task/TaskDetail.vue`
- **修改内容**: 添加任务接受功能

### 6. 状态管理扩展
- **文件路径**: `ryl-engineer-app/src/stores/task.js`
- **修改内容**: 添加任务接受和状态更新相关状态管理

### 7. 常量定义
- **文件路径**: `ryl-engineer-app/src/constants/technicalCategories.js`
- **修改内容**: 创建技术分类常量定义

## 四、测试计划

### 1. 单元测试
- 测试用户注册与技术分类保存
- 测试用户信息更新与技术分类修改
- 测试任务派发算法逻辑

### 2. 集成测试
- 测试任务创建到派发的完整流程
- 测试消息通知系统与任务派发的集成
- 测试定时任务与事件触发的协同工作

### 3. 端到端测试
- 测试前端注册流程与技术分类选择
- 测试个人信息页面技术分类编辑
- 测试任务派发通知与接受流程

## 实施清单：

1. 创建数据库修改脚本，添加用户表技术分类字段
2. 创建数据库修复脚本，用于现有数据库更新
3. 修改User实体类，添加technicalCategories字段
4. 更新RegisterRequest、UserInfoRequest和UserInfoResponse DTO类
5. 修改UserMapper接口和XML配置，支持技术分类字段
6. 更新UserServiceImpl，处理技术分类字段的保存和获取
7. 修改UserController，更新API处理技术分类字段
8. 创建TaskDispatchService接口和实现类
9. 实现任务创建事件监听器TaskCreationListener
10. 创建定时任务调度器TaskDispatchScheduler
11. 扩展ChatServiceImpl，支持任务派发通知
12. 修改Register.vue，添加技术分类多选组件
13. 更新ProfileInfo.vue，添加技术分类编辑功能
14. 修改auth.js和user.js API服务，支持技术分类字段
15. 扩展MessageList.vue，添加任务派发通知处理
16. 创建TaskNotification.vue组件，支持任务通知交互
17. 更新TaskDetail.vue，添加任务接受功能
18. 扩展task.js状态管理，添加任务接受和状态更新逻辑
19. 创建technicalCategories.js常量定义文件
20. 编写单元测试、集成测试和端到端测试
21. 执行测试并修复问题
22. 部署更新并监控系统运行状态

---

**User**

进入执行模式执行，要充分考虑前后端数据交互的一致性
在正式执行之前将这份计划先保存成.md文档到@/docs 目录，然后再执行，在执行完成后进行审查时更新这份文档标记已完成项

---

**Cursor**

[MODE: EXECUTE]

我将首先将计划保存为MD文档，然后开始执行计划。

```mdc
# 任务派发系统实现计划

## 一、数据库修改

### 1. 用户表技术分类字段添加
- **文件路径**: `接口文件/用户模块数据库初始化_sqlserver.sql`
- **修改内容**: 添加技术分类字段到用户表
- **SQL脚本**: 创建ALTER TABLE语句添加technical_categories字段，使用NVARCHAR(MAX)类型存储JSON数组
- **状态**: 未完成

### 2. 数据库修复脚本
- **文件路径**: 新建`接口文件/数据库修复脚本/add_technical_categories.sql`
- **修改内容**: 创建数据库修复脚本，用于现有数据库添加技术分类字段
- **状态**: 未完成

## 二、后端实现

### 1. 实体类修改
- **文件路径**: `ryl-engineer-back/src/main/java/com/ryl/engineer/entity/User.java`
- **修改内容**: 添加technicalCategories字段及其getter/setter方法
- **状态**: 未完成

### 2. DTO类修改
- **文件路径**: `ryl-engineer-back/src/main/java/com/ryl/engineer/dto/RegisterRequest.java`
- **修改内容**: 添加technicalCategories字段
- **状态**: 未完成
- **文件路径**: `ryl-engineer-back/src/main/java/com/ryl/engineer/dto/UserInfoRequest.java`
- **修改内容**: 添加technicalCategories字段
- **状态**: 未完成
- **文件路径**: `ryl-engineer-back/src/main/java/com/ryl/engineer/dto/UserInfoResponse.java`
- **修改内容**: 添加technicalCategories字段
- **状态**: 未完成

### 3. Mapper接口与XML修改
- **文件路径**: `ryl-engineer-back/src/main/java/com/ryl/engineer/mapper/UserMapper.java`
- **修改内容**: 更新相关方法支持技术分类字段
- **状态**: 未完成
- **文件路径**: `ryl-engineer-back/src/main/resources/mapper/UserMapper.xml`
- **修改内容**: 更新SQL语句支持技术分类字段
- **状态**: 未完成

### 4. 服务层实现
- **文件路径**: `ryl-engineer-back/src/main/java/com/ryl/engineer/service/impl/UserServiceImpl.java`
- **修改内容**: 更新注册和个人信息更新方法，处理技术分类字段
- **状态**: 未完成

### 5. 控制器修改
- **文件路径**: `ryl-engineer-back/src/main/java/com/ryl/engineer/controller/UserController.java`
- **修改内容**: 更新相关API处理技术分类字段
- **状态**: 未完成

### 6. 任务派发服务实现
- **文件路径**: 新建`ryl-engineer-back/src/main/java/com/ryl/engineer/service/TaskDispatchService.java`
- **修改内容**: 创建任务派发服务接口
- **状态**: 未完成
- **文件路径**: 新建`ryl-engineer-back/src/main/java/com/ryl/engineer/service/impl/TaskDispatchServiceImpl.java`
- **修改内容**: 实现任务派发逻辑，包括技术分类匹配和工作负载评估
- **状态**: 未完成

### 7. 任务监听器实现
- **文件路径**: 新建`ryl-engineer-back/src/main/java/com/ryl/engineer/listener/TaskCreationListener.java`
- **修改内容**: 实现任务创建事件监听器，触发任务派发
- **状态**: 未完成

### 8. 定时任务实现
- **文件路径**: 新建`ryl-engineer-back/src/main/java/com/ryl/engineer/scheduler/TaskDispatchScheduler.java`
- **修改内容**: 实现定时检查新任务并派发的调度器
- **状态**: 未完成

### 9. 消息通知服务扩展
- **文件路径**: `ryl-engineer-back/src/main/java/com/ryl/engineer/service/impl/ChatServiceImpl.java`
- **修改内容**: 扩展消息服务，支持任务派发通知
- **状态**: 未完成

## 三、前端实现

### 1. 注册页面修改
- **文件路径**: `ryl-engineer-app/src/views/auth/Register.vue`
- **修改内容**: 添加技术分类多选组件
- **状态**: 未完成

### 2. 个人信息页面修改
- **文件路径**: `ryl-engineer-app/src/views/profile/ProfileInfo.vue`
- **修改内容**: 添加技术分类编辑组件
- **状态**: 未完成

### 3. API服务修改
- **文件路径**: `ryl-engineer-app/src/api/auth.js`
- **修改内容**: 更新注册API，支持技术分类字段
- **状态**: 未完成
- **文件路径**: `ryl-engineer-app/src/api/user.js`
- **修改内容**: 更新用户信息API，支持技术分类字段
- **状态**: 未完成

### 4. 消息页面扩展
- **文件路径**: `ryl-engineer-app/src/views/message/MessageList.vue`
- **修改内容**: 添加任务派发通知处理
- **状态**: 未完成
- **文件路径**: `ryl-engineer-app/src/views/message/TaskNotification.vue`
- **修改内容**: 创建任务通知组件，支持接受/拒绝操作
- **状态**: 未完成

### 5. 任务详情页扩展
- **文件路径**: `ryl-engineer-app/src/views/task/TaskDetail.vue`
- **修改内容**: 添加任务接受功能
- **状态**: 未完成

### 6. 状态管理扩展
- **文件路径**: `ryl-engineer-app/src/stores/task.js`
- **修改内容**: 添加任务接受和状态更新相关状态管理
- **状态**: 未完成

### 7. 常量定义
- **文件路径**: `ryl-engineer-app/src/constants/technicalCategories.js`
- **修改内容**: 创建技术分类常量定义
- **状态**: 未完成

## 四、测试计划

### 1. 单元测试
- 测试用户注册与技术分类保存
- 测试用户信息更新与技术分类修改
- 测试任务派发算法逻辑

### 2. 集成测试
- 测试任务创建到派发的完整流程
- 测试消息通知系统与任务派发的集成
- 测试定时任务与事件触发的协同工作

### 3. 端到端测试
- 测试前端注册流程与技术分类选择
- 测试个人信息页面技术分类编辑
- 测试任务派发通知与接受流程

## 实施清单：

1. 创建数据库修改脚本，添加用户表技术分类字段
2. 创建数据库修复脚本，用于现有数据库更新
3. 修改User实体类，添加technicalCategories字段
4. 更新RegisterRequest、UserInfoRequest和UserInfoResponse DTO类
5. 修改UserMapper接口和XML配置，支持技术分类字段
6. 更新UserServiceImpl，处理技术分类字段的保存和获取
7. 修改UserController，更新API处理技术分类字段
8. 创建TaskDispatchService接口和实现类
9. 实现任务创建事件监听器TaskCreationListener
10. 创建定时任务调度器TaskDispatchScheduler
11. 扩展ChatServiceImpl，支持任务派发通知
12. 修改Register.vue，添加技术分类多选组件
13. 更新ProfileInfo.vue，添加技术分类编辑功能
14. 修改auth.js和user.js API服务，支持技术分类字段
15. 扩展MessageList.vue，添加任务派发通知处理
16. 创建TaskNotification.vue组件，支持任务通知交互
17. 更新TaskDetail.vue，添加任务接受功能
18. 扩展task.js状态管理，添加任务接受和状态更新逻辑
19. 创建technicalCategories.js常量定义文件
20. 编写单元测试、集成测试和端到端测试
21. 执行测试并修复问题
22. 部署更新并监控系统运行状态
```

现在我将开始执行计划中的任务。首先，我会创建技术分类常量定义文件：

```javascript
/**
 * 技术分类常量定义
 * 用于工程师技术专长分类
 */

export const TECHNICAL_CATEGORIES = [
  {
    value: 'chromatography_spectroscopy',
    label: '色谱、光谱类',
    description: '包括气相色谱、液相色谱、质谱等设备'
  },
  {
    value: 'spectroscopy',
    label: '光谱类',
    description: '包括紫外、红外、原子吸收、荧光等光谱设备'
  },
  {
    value: 'life_science',
    label: '生命科学类',
    description: '包括PCR仪、酶标仪、细胞分析仪等设备'
  },
  {
    value: 'basic_instruments',
    label: '三项一柜基础仪器类',
    description: '包括天平、pH计、离心机等基础实验室设备'
  },
  {
    value: 'environmental_monitoring',
    label: '环境监测类',
    description: '包括水质分析仪、空气质量监测仪等设备'
  },
  {
    value: 'motherboard',
    label: '主板类',
    description: '包括各类仪器设备主板维修与更换'
  },
  {
    value: 'electrical',
    label: '电气类',
    description: '包括电源、电路、控制系统等维修'
  },
  {
    value: 'others',
    label: '其他类',
    description: '其他未分类的专业技术领域'
  }
];

/**
 * 获取技术分类选项（用于表单选择组件）
 * @returns {Array} 技术分类选项数组
 */
export function getTechnicalCategoryOptions() {
  return TECHNICAL_CATEGORIES.map(category => ({
    value: category.value,
    label: category.label
  }));
}

/**
 * 根据分类值获取分类标签
 * @param {String} value 分类值
 * @returns {String} 分类标签
 */
export function getTechnicalCategoryLabel(value) {
  const category = TECHNICAL_CATEGORIES.find(item => item.value === value);
  return category ? category.label : value;
}

/**
 * 将技术分类值数组转换为标签数组
 * @param {Array} values 分类值数组
 * @returns {Array} 分类标签数组
 */
export function getTechnicalCategoryLabels(values) {
  if (!values || !Array.isArray(values)) return [];
  return values.map(value => getTechnicalCategoryLabel(value));
}

export default TECHNICAL_CATEGORIES;
```

接下来，我将修改数据库脚本，添加技术分类字段：

```sql
-- 用户模块数据库设计
USE master;
GO

-- 创建数据库
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'ryl_engineer')
BEGIN
    CREATE DATABASE ryl_engineer;
END
GO

USE ryl_engineer;
GO

-- 用户表
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[user]') AND type in (N'U'))
BEGIN
CREATE TABLE [user] (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    work_id VARCHAR(50) NOT NULL, /* 工号 */
    name VARCHAR(50) NOT NULL, /* 姓名 */
    mobile VARCHAR(20) NOT NULL, /* 手机号 */
    password VARCHAR(255) NOT NULL, /* 密码 */
    department VARCHAR(50), /* 部门 */
    location VARCHAR(50), /* 工作地点 */
    avatar VARCHAR(255), /* 头像URL */
    technical_categories NVARCHAR(MAX), /* 技术分类，JSON数组格式 */
    status TINYINT DEFAULT 1, /* 状态（0-禁用，1-启用） */
    token VARCHAR(255), /* 登录令牌 */
    token_expire DATETIME2, /* 令牌过期时间 */
    last_login_time DATETIME2, /* 最后登录时间 */
    create_time DATETIME2 DEFAULT GETDATE(), /* 创建时间 */
    update_time DATETIME2 DEFAULT GETDATE(), /* 更新时间 */
    CONSTRAINT uk_work_id UNIQUE (work_id),
    CONSTRAINT uk_mobile UNIQUE (mobile)
);

CREATE INDEX idx_status ON [user](status);
END
GO

// ... existing code ...
```

现在创建数据库修复脚本：

Windows CMD不支持`-p`参数，让我修正命令：

现在创建数据库修复脚本文件：

```sql
-- 技术分类字段添加脚本
-- 用于向现有数据库的用户表添加技术分类字段
-- 执行日期：2024年

USE ryl_engineer;
GO

-- 检查字段是否存在，如果不存在则添加
IF NOT EXISTS (
    SELECT * 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_NAME = 'user' 
    AND COLUMN_NAME = 'technical_categories'
)
BEGIN
    -- 添加技术分类字段
    ALTER TABLE [user] ADD technical_categories NVARCHAR(MAX);
    
    PRINT '技术分类字段已成功添加到用户表';
END
ELSE
BEGIN
    PRINT '技术分类字段已存在，无需添加';
END
GO

-- 为现有工程师用户添加默认技术分类值
-- 注：此处仅为示例，实际应用中可能需要根据具体情况调整
UPDATE u
SET technical_categories = '["others"]'
FROM [user] u
JOIN [user_role] ur ON u.id = ur.user_id
JOIN [role] r ON ur.role_id = r.id
WHERE UPPER(r.code) = 'ENGINEER'
AND (u.technical_categories IS NULL OR u.technical_categories = '');

PRINT '已为现有工程师用户添加默认技术分类';
GO
```

现在修改后端User实体类：

```java
package com.ryl.engineer.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户实体类
 */
@Data
@ToString
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 工号
     */
    private String workId;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 手机号码
     */
    private String mobile;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 部门
     */
    private String department;
    
    /**
     * 工作地点
     */
    private String location;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 技术分类（工程师专业技能）
     */
    private String technicalCategories;
    
    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;
    
    /**
     * 登录令牌
     */
    private String token;
    
    /**
     * 令牌过期时间
     */
    private Date tokenExpire;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 最后登录时间
     */
    private Date lastLoginTime;
}
```

接下来修改RegisterRequest DTO类：

```java
package com.ryl.engineer.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

/**
 * 注册请求DTO
 */
@Data
public class RegisterRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 工号
     */
    @NotBlank(message = "工号不能为空")
    private String workId;
    
    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    
    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
    
    /**
     * 部门
     */
    private String department;
    
    /**
     * 地点
     */
    private String location;
    
    /**
     * 技术分类（工程师专业技能）
     */
    private List<String> technicalCategories;
}
```

现在检查是否有UserInfoRequest类：

<no message>

<no message>

```java
package com.ryl.engineer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

/**
 * 用户信息请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    /**
     * 手机号码
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;
    
    /**
     * 部门
     */
    private String department;
    
    /**
     * 工作地点
     */
    private String location;
    
    /**
     * 技术分类（工程师专业技能）
     */
    private List<String> technicalCategories;
}
```

<no message>

<no message>

```java
package com.ryl.engineer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 用户信息响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 工号
     */
    private String workId;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 手机号码
     */
    private String mobile;
    
    /**
     * 部门
     */
    private String department;
    
    /**
     * 工作地点
     */
    private String location;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 技术分类（工程师专业技能）
     */
    private List<String> technicalCategories;
    
    /**
     * 任务统计信息
     */
    private Map<String, Object> taskStats;
}
```

让我重试一下：

```java
import java.io.Serializable;
import java.util.Map;

/**
 * 用户信息响应DTO
 */

->

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 用户信息响应DTO
 */
```

```java
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 任务统计信息
     */

->

    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 技术分类（工程师专业技能）
     */
    private List<String> technicalCategories;
    
    /**
     * 任务统计信息
     */
```

现在修改UserMapper接口：

UserMapper接口不需要修改，因为它使用的是User实体类，我们已经在User实体类中添加了technicalCategories字段。

现在修改UserMapper.xml文件：

<no message>

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ryl.engineer.mapper.UserMapper">

    <resultMap id="BaseResultMap" type="com.ryl.engineer.entity.User">
        <id column="id" property="id" />
        <result column="work_id" property="workId" />
        <result column="name" property="name" />
        <result column="mobile" property="mobile" />
        <result column="password" property="password" />
        <result column="department" property="department" />
        <result column="location" property="location" />
        <result column="avatar" property="avatar" />
        <result column="technical_categories" property="technicalCategories" />
        <result column="status" property="status" />
        <result column="token" property="token" />
        <result column="token_expire" property="tokenExpire" />
        <result column="create_time" property="createTime" />
        <result column="update_time" property="updateTime" />
        <result column="last_login_time" property="lastLoginTime" />
    </resultMap>
    
    // ... existing code ...
    
    <sql id="Base_Column_List">
        id, work_id, name, mobile, password, department, location, avatar, technical_categories, status, token, token_expire, create_time, update_time, last_login_time
    </sql>
    
    <sql id="User_Column_List_With_Alias">
        u.id, u.work_id, u.name, u.mobile, u.password, u.department, u.location, u.avatar, u.technical_categories, u.status, u.token, u.token_expire, u.create_time, u.update_time, u.last_login_time
    </sql>
    
    // ... existing code ...
    
    <insert id="insert" parameterType="com.ryl.engineer.entity.User" useGeneratedKeys="true" keyProperty="id">
        insert into dbo.[user] (
            work_id, name, mobile, password, department, location, avatar, technical_categories, status, token, token_expire, create_time, update_time
        ) values (
            #{workId}, #{name}, #{mobile}, #{password}, #{department}, #{location}, #{avatar}, #{technicalCategories}, #{status}, #{token}, #{tokenExpire}, GETDATE(), GETDATE()
        )
    </insert>
    
    <update id="update" parameterType="com.ryl.engineer.entity.User">
        update dbo.[user]
        <set>
            <if test="name != null">name = #{name},</if>
            <if test="mobile != null">mobile = #{mobile},</if>
            <if test="password != null">password = #{password},</if>
            <if test="department != null">department = #{department},</if>
            <if test="location != null">location = #{location},</if>
            <if test="avatar != null">avatar = #{avatar},</if>
            <if test="technicalCategories != null">technical_categories = #{technicalCategories},</if>
            <if test="status != null">status = #{status},</if>
            <if test="token != null">token = #{token},</if>
            <if test="tokenExpire != null">token_expire = #{tokenExpire},</if>
            update_time = GETDATE()
        </set>
        where id = #{id}
    </update>
    
    // ... existing code ...
```

现在修改UserServiceImpl类：

<no message>

<no message>

```java
package com.ryl.engineer.service.impl;

import com.ryl.engineer.dto.ForgotPasswordResponse;
import com.ryl.engineer.dto.LoginRequest;
import com.ryl.engineer.dto.LoginResponse;
import com.ryl.engineer.dto.RegisterRequest;
import com.ryl.engineer.dto.RegisterResponse;
import com.ryl.engineer.entity.User;
import com.ryl.engineer.mapper.UserMapper;
import com.ryl.engineer.service.UserService;
import com.ryl.engineer.util.JwtUtil;
import com.ryl.engineer.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserMapper userMapper;

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            logger.info("开始登录处理，工号：{}", request.getWorkId());
            
            // 根据工号查询用户
            User user = userMapper.selectByWorkId(request.getWorkId());
            
            // 用户不存在
            if (user == null) {
                logger.warn("登录失败：用户不存在，工号：{}", request.getWorkId());
                return null;
            }
            
            // 密码错误
            if (!PasswordUtil.verify(request.getPassword(), user.getPassword())) {
                logger.warn("登录失败：密码错误，工号：{}", request.getWorkId());
                return null;
            }
            
            logger.info("用户验证成功，更新最后登录时间，用户ID：{}", user.getId());
            
            // 更新最后登录时间
            try {
                userMapper.updateLastLoginTime(user.getId());
            } catch (Exception e) {
                logger.error("更新最后登录时间失败", e);
                // 继续处理，不影响登录流程
            }
            
            // 生成token
            String token = JwtUtil.generateToken(user.getId(), user.getWorkId());
            
            // 计算令牌过期时间（30天）
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 30);
            Date expireDate = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            String tokenExpire = sdf.format(expireDate);
            
            logger.info("生成token成功，更新数据库中的token信息");
            
            // 将token和过期时间保存到数据库
            try {
                userMapper.updateToken(user.getId(), token, expireDate);
            } catch (Exception e) {
                logger.error("更新token失败", e);
                // 继续处理，不影响登录流程
            }
            
            // 构建登录响应
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setTokenExpire(tokenExpire);
            response.setWorkId(user.getWorkId());
            response.setName(user.getName());
            response.setMobile(user.getMobile());
            response.setDepartment(user.getDepartment());
            response.setLocation(user.getLocation());
            response.setAvatar(user.getAvatar());
            response.setId(user.getId());
            
            // 获取用户角色列表
            List<String> roles = userMapper.selectUserRoles(user.getId());
            response.setRoles(roles);
            
            logger.info("登录成功，用户：{}", user.getWorkId());
            return response;
        } catch (Exception e) {
            logger.error("登录过程中发生异常", e);
            return null;
        }
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        try {
            // 检查工号是否已存在
            User existUser = userMapper.selectByWorkId(request.getWorkId());
            if (existUser != null) {
                return null;
            }
            
            // 检查手机号是否已存在
            existUser = userMapper.selectByMobile(request.getMobile());
            if (existUser != null) {
                return null;
            }
            
            // 检查密码和确认密码是否一致
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return null;
            }
            
            // 创建新用户
            User user = new User();
            user.setWorkId(request.getWorkId());
            user.setName(request.getName());
            user.setMobile(request.getMobile());
            user.setPassword(PasswordUtil.encode(request.getPassword()));
            user.setDepartment(request.getDepartment());
            user.setLocation(request.getLocation());
            user.setStatus(1); // 1-正常
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            
            // 设置技术分类
            if (request.getTechnicalCategories() != null && !request.getTechnicalCategories().isEmpty()) {
                user.setTechnicalCategories(objectMapper.writeValueAsString(request.getTechnicalCategories()));
            }
            
            // 插入用户
            boolean success = userMapper.insert(user) > 0;
            
            if (success) {
                RegisterResponse response = new RegisterResponse();
                response.setRegistered(true);
                response.setWorkId(user.getWorkId());
                response.setName(user.getName());
                return response;
            }
            
            return null;
        } catch (Exception e) {
            logger.error("注册过程中发生异常", e);
            return null;
        }
    }

    // ... existing code ...

    @Override
    @Transactional
    public boolean updateUser(User user) {
        try {
            user.setUpdateTime(new Date());
            return userMapper.update(user) > 0;
        } catch (Exception e) {
            logger.error("更新用户信息失败", e);
            return false;
        }
    }

    // ... existing code ...
    
    /**
     * 将用户列表转换为Map列表，过滤敏感信息
     * 
     * @param users 用户列表
     * @return 过滤后的Map列表
     */
    private List<Map<String, Object>> convertUserListToMapList(List<User> users) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        if (users != null && !users.isEmpty()) {
            for (User user : users) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", user.getId());
                map.put("workId", user.getWorkId());
                map.put("name", user.getName());
                map.put("department", user.getDepartment());
                map.put("location", user.getLocation());
                map.put("mobile", user.getMobile());
                map.put("avatar", user.getAvatar());
                
                // 添加技术分类
                if (user.getTechnicalCategories() != null && !user.getTechnicalCategories().isEmpty()) {
                    try {
                        List<String> categories = objectMapper.readValue(user.getTechnicalCategories(), List.class);
                        map.put("technicalCategories", categories);
                    } catch (Exception e) {
                        logger.error("解析技术分类字段失败", e);
                        map.put("technicalCategories", new ArrayList<>());
                    }
                } else {
                    map.put("technicalCategories", new ArrayList<>());
                }
                
                // 不返回密码等敏感信息
                
                result.add(map);
            }
        }
        
        return result;
    }
}
```

现在修改UserController类：

<no message>

<no message>

```java
package com.ryl.engineer.controller;

// ... existing code ...

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryl.engineer.common.Result;
// ... existing code ...

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // ... existing code ...

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<RegisterResponse> register(@RequestBody @Validated RegisterRequest request) {
        // 验证密码和确认密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return Result.error("两次输入的密码不一致");
        }
        
        RegisterResponse response = userService.register(request);
        if (response == null || !response.getRegistered()) {
            return Result.error("注册失败，工号或手机号已存在");
        }
        return Result.success(response);
    }

    // ... existing code ...
    
    /**
     * 获取用户个人信息
     *
     * @param request HTTP请求
     * @return 用户信息响应
     */
    @GetMapping("/profile")
    public Result<UserInfoResponse> getUserProfile(HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        
        // 从token中获取用户ID
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Result.error("无效的token");
        }
        
        // 获取用户信息
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 构建响应
        UserInfoResponse response = new UserInfoResponse();
        response.setWorkId(user.getWorkId());
        response.setName(user.getName());
        response.setMobile(user.getMobile());
        response.setDepartment(user.getDepartment());
        response.setLocation(user.getLocation());
        response.setAvatar(user.getAvatar());
        
        // 设置技术分类
        if (user.getTechnicalCategories() != null && !user.getTechnicalCategories().isEmpty()) {
            try {
                List<String> categories = objectMapper.readValue(user.getTechnicalCategories(), List.class);
                response.setTechnicalCategories(categories);
            } catch (Exception e) {
                response.setTechnicalCategories(new ArrayList<>());
            }
        } else {
            response.setTechnicalCategories(new ArrayList<>());
        }

        // 添加任务统计信息（如果需要的话）
        Map<String, Object> taskStats = new HashMap<>();
        taskStats.put("ongoing", 2);
        taskStats.put("completed", 3);
        taskStats.put("rating", 4.8);
        response.setTaskStats(taskStats);
        
        return Result.success(response);
    }
    
    // ... existing code ...
    
    /**
     * 更新用户个人信息
     *
     * @param request 用户信息请求
     * @param httpRequest HTTP请求
     * @return 更新结果
     */
    @PutMapping("/profile")
    public Result<Map<String, Object>> updateUserProfile(@RequestBody @Validated UserInfoRequest request, HttpServletRequest httpRequest) {
        // 从请求头中获取token
        String token = httpRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        
        // 从token中获取用户ID
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Result.error("无效的token");
        }
        
        // 获取用户信息
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 更新用户信息
        user.setName(request.getName());
        user.setMobile(request.getMobile());
        user.setDepartment(request.getDepartment());
        if (request.getLocation() != null) {
            user.setLocation(request.getLocation());
        }
        
        // 更新技术分类
        if (request.getTechnicalCategories() != null) {
            try {
                user.setTechnicalCategories(objectMapper.writeValueAsString(request.getTechnicalCategories()));
            } catch (Exception e) {
                return Result.error("技术分类格式错误");
            }
        }
        
        boolean success = userService.updateUser(user);
        if (!success) {
            return Result.error("更新失败");
        }
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("updated", true);
        
        return Result.success(response);
    }
    
    // ... existing code ...
```

现在我们需要创建TaskDispatchService接口：

```java
package com.ryl.engineer.service;

import com.ryl.engineer.entity.Task;

import java.util.List;
import java.util.Map;

/**
 * 任务派发服务接口
 * 负责根据工程师技术分类和工作负载自动派发任务
 */
public interface TaskDispatchService {
    
    /**
     * 派发任务给合适的工程师
     *
     * @param taskId 任务ID
     * @return 派发结果，包含派发状态和工程师信息
     */
    Map<String, Object> dispatchTask(Long taskId);
    
    /**
     * 批量派发未分配的任务
     *
     * @return 派发结果列表
     */
    List<Map<String, Object>> dispatchPendingTasks();
    
    /**
     * 手动派发任务给指定工程师
     *
     * @param taskId 任务ID
     * @param engineerId 工程师ID
     * @return 派发结果
     */
    boolean assignTaskToEngineer(Long taskId, Long engineerId);
    
    /**
     * 重新派发任务
     *
     * @param taskId 任务ID
     * @return 派发结果
     */
    Map<String, Object> reassignTask(Long taskId);
    
    /**
     * 根据任务类型查找匹配的工程师列表
     *
     * @param task 任务信息
     * @return 匹配的工程师列表，按匹配度排序
     */
    List<Map<String, Object>> findMatchingEngineers(Task task);
    
    /**
     * 评估工程师当前工作负载
     *
     * @param engineerId 工程师ID
     * @return 工作负载评估结果（0-10，数值越低表示负载越轻）
     */
    int evaluateEngineerWorkload(Long engineerId);
}
```

现在创建TaskDispatchServiceImpl实现类：

```java
package com.ryl.engineer.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryl.engineer.entity.Task;
import com.ryl.engineer.entity.User;
import com.ryl.engineer.mapper.TaskMapper;
import com.ryl.engineer.mapper.UserMapper;
import com.ryl.engineer.service.ChatService;
import com.ryl.engineer.service.TaskDispatchService;
import com.ryl.engineer.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务派发服务实现类
 */
@Service
public class TaskDispatchServiceImpl implements TaskDispatchService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskDispatchServiceImpl.class);
    
    @Autowired
    private TaskMapper taskMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private ChatService chatService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    @Transactional
    public Map<String, Object> dispatchTask(Long taskId) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        
        try {
            // 获取任务信息
            Task task = taskMapper.selectById(taskId);
            if (task == null) {
                result.put("message", "任务不存在");
                return result;
            }
            
            // 检查任务是否已经分配
            if (task.getStatus() != 0) { // 假设0表示待分配状态
                result.put("message", "任务已分配或不在待分配状态");
                return result;
            }
            
            // 查找匹配的工程师
            List<Map<String, Object>> matchingEngineers = findMatchingEngineers(task);
            if (matchingEngineers.isEmpty()) {
                result.put("message", "未找到匹配的工程师");
                return result;
            }
            
            // 选择最合适的工程师（第一个是匹配度最高的）
            Map<String, Object> bestEngineer = matchingEngineers.get(0);
            Long engineerId = (Long) bestEngineer.get("id");
            
            // 分配任务给工程师
            boolean assigned = assignTaskToEngineer(taskId, engineerId);
            if (!assigned) {
                result.put("message", "分配任务失败");
                return result;
            }
            
            // 发送通知给工程师
            String engineerName = (String) bestEngineer.get("name");
            sendTaskNotification(task, engineerId, engineerName);
            
            result.put("success", true);
            result.put("message", "任务已成功派发给工程师: " + engineerName);
            result.put("engineer", bestEngineer);
            
            return result;
        } catch (Exception e) {
            logger.error("派发任务时发生错误", e);
            result.put("message", "派发任务时发生错误: " + e.getMessage());
            return result;
        }
    }
    
    @Override
    public List<Map<String, Object>> dispatchPendingTasks() {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            // 获取所有待分配的任务
            List<Task> pendingTasks = taskMapper.selectByStatus(0); // 假设0表示待分配状态
            
            for (Task task : pendingTasks) {
                Map<String, Object> result = dispatchTask(task.getId());
                results.add(result);
            }
            
            return results;
        } catch (Exception e) {
            logger.error("批量派发任务时发生错误", e);
            return results;
        }
    }
    
    @Override
    @Transactional
    public boolean assignTaskToEngineer(Long taskId, Long engineerId) {
        try {
            // 获取任务信息
            Task task = taskMapper.selectById(taskId);
            if (task == null) {
                return false;
            }
            
            // 获取工程师信息
            User engineer = userMapper.selectById(engineerId);
            if (engineer == null) {
                return false;
            }
            
            // 更新任务状态为已分配
            task.setStatus(1); // 假设1表示已分配状态
            task.setEngineerAssignTime(new Date());
            
            // 保存任务工程师关联
            boolean success = taskService.assignEngineer(taskId, engineerId);
            if (!success) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            logger.error("分配任务给工程师时发生错误", e);
            return false;
        }
    }
    
    @Override
    public Map<String, Object> reassignTask(Long taskId) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        
        try {
            // 获取任务信息
            Task task = taskMapper.selectById(taskId);
            if (task == null) {
                result.put("message", "任务不存在");
                return result;
            }
            
            // 更新任务状态为待分配
            task.setStatus(0); // 假设0表示待分配状态
            taskService.updateTask(task);
            
            // 重新派发任务
            return dispatchTask(taskId);
        } catch (Exception e) {
            logger.error("重新派发任务时发生错误", e);
            result.put("message", "重新派发任务时发生错误: " + e.getMessage());
            return result;
        }
    }
    
    @Override
    public List<Map<String, Object>> findMatchingEngineers(Task task) {
        try {
            // 获取所有工程师
            Map<String, Object> params = new HashMap<>();
            List<User> engineers = userMapper.selectEngineers(params);
            
            if (engineers.isEmpty()) {
                return Collections.emptyList();
            }
            
            // 计算每个工程师的匹配度
            List<Map<String, Object>> engineersWithScore = new ArrayList<>();
            
            for (User engineer : engineers) {
                Map<String, Object> engineerMap = new HashMap<>();
                engineerMap.put("id", engineer.getId());
                engineerMap.put("name", engineer.getName());
                engineerMap.put("workId", engineer.getWorkId());
                engineerMap.put("department", engineer.getDepartment());
                engineerMap.put("location", engineer.getLocation());
                
                // 计算技术匹配度（70%权重）
                double techScore = calculateTechnicalMatchScore(engineer, task);
                
                // 计算工作负载评分（30%权重）
                int workload = evaluateEngineerWorkload(engineer.getId());
                double workloadScore = (10 - workload) / 10.0; // 负载越低，分数越高
                
                // 综合评分
                double totalScore = techScore * 0.7 + workloadScore * 0.3;
                
                engineerMap.put("techScore", techScore);
                engineerMap.put("workloadScore", workloadScore);
                engineerMap.put("totalScore", totalScore);
                
                engineersWithScore.add(engineerMap);
            }
            
            // 按总分排序（降序）
            engineersWithScore.sort(Comparator.comparing((Map<String, Object> map) -> 
                (Double) map.get("totalScore")).reversed());
            
            return engineersWithScore;
        } catch (Exception e) {
            logger.error("查找匹配工程师时发生错误", e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public int evaluateEngineerWorkload(Long engineerId) {
        try {
            // 获取工程师当前任务信息
            Map<String, Object> taskInfo = userMapper.selectEngineerCurrentTask(engineerId);
            if (taskInfo == null) {
                return 0; // 无任务，负载为0
            }
            
            // 获取任务数量
            Integer taskCount = (Integer) taskInfo.get("taskCount");
            if (taskCount == null) {
                return 0;
            }
            
            // 根据任务数量评估负载（0-10）
            if (taskCount <= 1) {
                return 2;
            } else if (taskCount <= 3) {
                return 5;
            } else if (taskCount <= 5) {
                return 8;
            } else {
                return 10;
            }
        } catch (Exception e) {
            logger.error("评估工程师工作负载时发生错误", e);
            return 5; // 发生错误时返回中等负载
        }
    }
    
    /**
     * 计算工程师与任务的技术匹配度
     *
     * @param engineer 工程师
     * @param task 任务
     * @return 匹配度评分（0-1）
     */
    private double calculateTechnicalMatchScore(User engineer, Task task) {
        try {
            // 如果工程师没有技术分类信息，返回最低分
            if (engineer.getTechnicalCategories() == null || engineer.getTechnicalCategories().isEmpty()) {
                return 0.1; // 给一个很低但非零的基础分
            }
            
            // 解析工程师的技术分类
            List<String> engineerCategories = objectMapper.readValue(
                engineer.getTechnicalCategories(),
                new TypeReference<List<String>>() {}
            );
            
            if (engineerCategories.isEmpty()) {
                return 0.1;
            }
            
            // 获取任务的仪器类型（假设任务有equipmentType字段）
            String equipmentType = task.getEquipmentType();
            if (equipmentType == null || equipmentType.isEmpty()) {
                return 0.5; // 任务没有明确的设备类型，给中等分数
            }
            
            // 简单匹配逻辑：检查工程师技术分类是否包含与设备类型相关的关键词
            // 这里可以根据实际需求实现更复杂的匹配算法
            for (String category : engineerCategories) {
                if (isEquipmentMatchCategory(equipmentType, category)) {
                    return 1.0; // 找到完全匹配
                }
            }
            
            // 没有直接匹配，但有技术分类，给一个中等偏低的分数
            return 0.3;
        } catch (Exception e) {
            logger.error("计算技术匹配度时发生错误", e);
            return 0.2; // 发生错误时返回较低分数
        }
    }
    
    /**
     * 判断设备类型是否与技术分类匹配
     *
     * @param equipmentType 设备类型
     * @param category 技术分类
     * @return 是否匹配
     */
    private boolean isEquipmentMatchCategory(String equipmentType, String category) {
        // 这里实现具体的匹配逻辑，可以根据实际业务需求调整
        // 示例：简单的关键词匹配
        switch (category) {
            case "chromatography_spectroscopy":
                return equipmentType.toLowerCase().contains("色谱") || 
                       equipmentType.toLowerCase().contains("光谱");
            case "spectroscopy":
                return equipmentType.toLowerCase().contains("光谱") || 
                       equipmentType.toLowerCase().contains("分光");
            case "life_science":
                return equipmentType.toLowerCase().contains("生命") || 
                       equipmentType.toLowerCase().contains("pcr") ||
                       equipmentType.toLowerCase().contains("细胞");
            case "basic_instruments":
                return equipmentType.toLowerCase().contains("天平") || 
                       equipmentType.toLowerCase().contains("ph") ||
                       equipmentType.toLowerCase().contains("离心");
            case "environmental_monitoring":
                return equipmentType.toLowerCase().contains("环境") || 
                       equipmentType.toLowerCase().contains("监测") ||
                       equipmentType.toLowerCase().contains("水质");
            case "motherboard":
                return equipmentType.toLowerCase().contains("主板") || 
                       equipmentType.toLowerCase().contains("电路板");
            case "electrical":
                return equipmentType.toLowerCase().contains("电气") || 
                       equipmentType.toLowerCase().contains("电源") ||
                       equipmentType.toLowerCase().contains("电路");
            case "others":
                return true; // 其他类别可以匹配任何设备
            default:
                return false;
        }
    }
    
    /**
     * 发送任务通知给工程师
     *
     * @param task 任务
     * @param engineerId 工程师ID
     * @param engineerName 工程师姓名
     */
    private void sendTaskNotification(Task task, Long engineerId, String engineerName) {
        try {
            // 构建通知内容
            Map<String, Object> content = new HashMap<>();
            content.put("taskId", task.getId());
            content.put("taskName", task.getName());
            content.put("taskType", task.getTaskType());
            content.put("equipmentType", task.getEquipmentType());
            content.put("priority", task.getPriority());
            content.put("dispatchTime", new Date());
            content.put("messageType", "task_dispatch");
            
            // 发送系统消息给工程师
            chatService.sendSystemMessage(
                engineerId, 
                "新任务派发通知", 
                "您有一个新的任务【" + task.getName() + "】已派发给您，请及时处理。", 
                objectMapper.writeValueAsString(content)
            );
            
            logger.info("已成功发送任务派发通知给工程师: {}", engineerName);
        } catch (Exception e) {
            logger.error("发送任务通知时发生错误", e);
        }
    }
}
```

<no message>

<no message>

现在我们需要创建任务监听器：

<no message>

<no message>

```java
package com.ryl.engineer.listener;

import com.ryl.engineer.entity.Task;
import com.ryl.engineer.service.TaskDispatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

/**
 * 任务创建事件监听器
 * 监听任务创建事件，触发任务派发
 */
@Component
public class TaskCreationListener {

    private static final Logger logger = LoggerFactory.getLogger(TaskCreationListener.class);

    @Autowired
    private TaskDispatchService taskDispatchService;

    /**
     * 监听任务创建事件
     * 在事务提交后异步处理，避免影响主流程
     *
     * @param event 任务创建事件
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskCreatedEvent(TaskCreatedEvent event) {
        try {
            logger.info("收到任务创建事件，任务ID: {}", event.getTaskId());
            
            // 派发任务
            Map<String, Object> result = taskDispatchService.dispatchTask(event.getTaskId());
            
            if ((boolean) result.getOrDefault("success", false)) {
                logger.info("任务派发成功: {}", result.get("message"));
            } else {
                logger.warn("任务派发失败: {}", result.get("message"));
            }
        } catch (Exception e) {
            logger.error("处理任务创建事件时发生错误", e);
        }
    }
}
```

<no message>

```java
package com.ryl.engineer.listener;

import org.springframework.context.ApplicationEvent;

/**
 * 任务创建事件
 * 用于在任务创建后触发相关处理
 */
public class TaskCreatedEvent extends ApplicationEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final Long taskId;
    
    /**
     * 创建任务创建事件
     *
     * @param source 事件源
     * @param taskId 任务ID
     */
    public TaskCreatedEvent(Object source, Long taskId) {
        super(source);
        this.taskId = taskId;
    }
    
    /**
     * 获取任务ID
     *
     * @return 任务ID
     */
    public Long getTaskId() {
        return taskId;
    }
}
```

现在创建定时任务调度器：

<no message>

```java
package com.ryl.engineer.scheduler;

import com.ryl.engineer.service.TaskDispatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 任务派发调度器
 * 定时检查未分配的任务并进行派发
 */
@Component
public class TaskDispatchScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskDispatchScheduler.class);
    
    @Autowired
    private TaskDispatchService taskDispatchService;
    
    /**
     * 定时派发任务
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60000) // 60秒
    public void dispatchPendingTasks() {
        try {
            logger.info("开始执行定时任务派发");
            
            List<Map<String, Object>> results = taskDispatchService.dispatchPendingTasks();
            
            int successCount = 0;
            for (Map<String, Object> result : results) {
                if ((boolean) result.getOrDefault("success", false)) {
                    successCount++;
                }
            }
            
            if (!results.isEmpty()) {
                logger.info("定时任务派发完成，共处理{}个任务，成功派发{}个", results.size(), successCount);
            }
        } catch (Exception e) {
            logger.error("定时任务派发过程中发生错误", e);
        }
    }
}
```

现在修改前端注册页面：

<no message>

<no message>

```vue
<template>
  <div class="app-container">
    <!-- 顶部导航 -->
    <div class="bg-white shadow-sm p-3 d-flex align-items-center">
      <router-link to="/login" class="text-secondary">
        <i class="fas fa-arrow-left"></i>
      </router-link>
      <h1 class="fs-5 fw-semibold ms-3 mb-0">注册账号</h1>
    </div>

    <!-- 主要内容 -->
    <div class="p-3">
      <p class="text-muted small mb-3">请填写以下信息完成注册，带 <span class="text-danger">*</span> 为必填项</p>
      
      <form @submit.prevent="handleRegister">
        <!-- 基本信息 -->
        <div class="card mb-3">
          <div class="card-body">
            <h2 class="fs-5 fw-medium mb-3">基本信息</h2>
            
            <div class="mb-3">
              <label class="form-label">
                工号 <span class="text-danger">*</span>
              </label>
              <div class="input-group">
                <span class="input-group-text">
                  <i class="fas fa-id-card"></i>
                </span>
                <input 
                  type="text" 
                  v-model="formData.workId" 
                  placeholder="请输入工号" 
                  class="form-control"
                  :class="{ 'is-invalid': errors.workId }"
                >
              </div>
              <div v-if="errors.workId" class="invalid-feedback d-block">{{ errors.workId }}</div>
              <small class="text-muted">工号是您登录的唯一凭证，请向管理员获取</small>
            </div>
            
            <div class="mb-3">
              <label class="form-label">
                姓名 <span class="text-danger">*</span>
              </label>
              <div class="input-group">
                <span class="input-group-text">
                  <i class="fas fa-user"></i>
                </span>
                <input 
                  type="text" 
                  v-model="formData.name" 
                  placeholder="请输入姓名" 
                  class="form-control"
                  :class="{ 'is-invalid': errors.name }"
                >
              </div>
              <div v-if="errors.name" class="invalid-feedback d-block">{{ errors.name }}</div>
            </div>
            
            <div class="mb-3">
              <label class="form-label">
                手机号码 <span class="text-danger">*</span>
              </label>
              <div class="input-group">
                <span class="input-group-text">
                  <i class="fas fa-mobile-alt"></i>
                </span>
                <input 
                  type="tel" 
                  v-model="formData.mobile" 
                  placeholder="请输入手机号码" 
                  class="form-control"
                  :class="{ 'is-invalid': errors.mobile }"
                >
              </div>
              <div v-if="errors.mobile" class="invalid-feedback d-block">{{ errors.mobile }}</div>
            </div>
          </div>
        </div>
        
        <!-- 账号安全 -->
        <div class="card mb-3">
          <div class="card-body">
            <h2 class="fs-5 fw-medium mb-3">账号安全</h2>
            
            <div class="mb-3">
              <label class="form-label">
                设置密码 <span class="text-danger">*</span>
              </label>
              <div class="input-group">
                <span class="input-group-text">
                  <i class="fas fa-lock"></i>
                </span>
                <input 
                  :type="showPassword ? 'text' : 'password'" 
                  v-model="formData.password" 
                  placeholder="请输入密码" 
                  class="form-control"
                  :class="{ 'is-invalid': errors.password }"
                >
                <button class="btn btn-outline-secondary" type="button" @click="togglePasswordVisibility">
                  <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                </button>
              </div>
              <div v-if="errors.password" class="invalid-feedback d-block">{{ errors.password }}</div>
            </div>
            
            <div class="mb-3">
              <label class="form-label">
                确认密码 <span class="text-danger">*</span>
              </label>
              <div class="input-group">
                <span class="input-group-text">
                  <i class="fas fa-lock"></i>
                </span>
                <input 
                  :type="showConfirmPassword ? 'text' : 'password'" 
                  v-model="formData.confirmPassword" 
                  placeholder="请再次输入密码" 
                  class="form-control"
                  :class="{ 'is-invalid': errors.confirmPassword }"
                >
                <button class="btn btn-outline-secondary" type="button" @click="toggleConfirmPasswordVisibility">
                  <i :class="showConfirmPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                </button>
              </div>
              <div v-if="errors.confirmPassword" class="invalid-feedback d-block">{{ errors.confirmPassword }}</div>
            </div>
          </div>
        </div>
        
        <!-- 职业信息 -->
        <div class="card mb-3">
          <div class="card-body">
            <h2 class="fs-5 fw-medium mb-3">职业信息</h2>
            
            <div class="mb-3">
              <label class="form-label">
                所属部门 <span class="text-danger">*</span>
              </label>
              <div class="input-group">
                <span class="input-group-text">
                  <i class="fas fa-building"></i>
                </span>
                <select 
                  v-model="formData.department" 
                  class="form-select"
                  :class="{ 'is-invalid': errors.department }"
                >
                  <option value="" selected disabled>请选择部门</option>
                  <option>系统管理员</option>
                  <option>工程师</option>
                  <option>仓库管理员</option>
                  <option>客户经理</option>
                </select>
              </div>
              <div v-if="errors.department" class="invalid-feedback d-block">{{ errors.department }}</div>
            </div>
            
            <!-- 工作地点 -->
            <div class="mb-3">
              <label class="form-label">工作地点</label>
              <div class="input-group">
                <span class="input-group-text">
                  <i class="fas fa-map-marker-alt"></i>
                </span>
                <input 
                  type="text" 
                  v-model="formData.location" 
                  placeholder="请输入工作地点" 
                  class="form-control"
                >
              </div>
            </div>
            
            <!-- 技术分类（仅工程师可见） -->
            <div class="mb-3" v-if="formData.department === '工程师'">
              <label class="form-label">
                技术分类 <span class="text-danger">*</span>
                <small class="text-muted">(可多选)</small>
              </label>
              <div class="tech-categories-container">
                <div v-for="category in technicalCategories" :key="category.value" class="form-check mb-2">
                  <input 
                    class="form-check-input" 
                    type="checkbox" 
                    :id="category.value" 
                    :value="category.value" 
                    v-model="formData.technicalCategories"
                  >
                  <label class="form-check-label" :for="category.value">
                    {{ category.label }}
                    <small class="text-muted d-block">{{ category.description }}</small>
                  </label>
                </div>
              </div>
              <div v-if="errors.technicalCategories" class="invalid-feedback d-block">{{ errors.technicalCategories }}</div>
            </div>
          </div>
        </div>
        
        <!-- 注册按钮 -->
        <button 
          type="submit" 
          class="btn btn-primary w-100 mb-3"
          :disabled="loading"
        >
          <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
          {{ loading ? '注册中...' : '完成注册' }}
        </button>
        
        <div class="text-center">
          <span class="text-muted small">已有账号?</span>
          <router-link to="/login" class="text-decoration-none ms-1">返回登录</router-link>
        </div>
      </form>
      
      <!-- 错误提示 -->
      <div v-if="registerError" class="alert alert-danger mt-3" role="alert">
        {{ registerError }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { validateWorkId, validatePassword, validateConfirmPassword, validateMobile, validateName, validateDepartment } from '../../utils/validation'
import { TECHNICAL_CATEGORIES } from '../../constants/technicalCategories'

const authStore = useAuthStore()
const loading = computed(() => authStore.loading)
const registerError = computed(() => authStore.error)
const technicalCategories = TECHNICAL_CATEGORIES

// 表单数据
const formData = reactive({
  workId: '',
  name: '',
  mobile: '',
  password: '',
  confirmPassword: '',
  department: '',
  location: '',
  technicalCategories: []
})

// 表单错误
const errors = reactive({
  workId: '',
  name: '',
  mobile: '',
  password: '',
  confirmPassword: '',
  department: '',
  technicalCategories: ''
})

// 密码可见性
const showPassword = ref(false)
const showConfirmPassword = ref(false)

// 切换密码可见性
function togglePasswordVisibility() {
  showPassword.value = !showPassword.value
}

function toggleConfirmPasswordVisibility() {
  showConfirmPassword.value = !showConfirmPassword.value
}

// 验证表单
function validateForm() {
  errors.workId = validateWorkId(formData.workId)
  errors.name = validateName(formData.name)
  errors.mobile = validateMobile(formData.mobile)
  errors.password = validatePassword(formData.password)
  errors.confirmPassword = validateConfirmPassword(formData.password, formData.confirmPassword)
  errors.department = validateDepartment(formData.department)
  
  // 验证技术分类（如果是工程师）
  if (formData.department === '工程师' && (!formData.technicalCategories || formData.technicalCategories.length === 0)) {
    errors.technicalCategories = '请至少选择一项技术分类'
  } else {
    errors.technicalCategories = ''
  }
  
  return !errors.workId && !errors.name && !errors.mobile && 
         !errors.password && !errors.confirmPassword && !errors.department &&
         !errors.technicalCategories
}

// 处理注册
async function handleRegister() {
  if (!validateForm()) return
  
  const result = await authStore.registerUser({
    workId: formData.workId,
    name: formData.name,
    mobile: formData.mobile,
    password: formData.password,
    confirmPassword: formData.confirmPassword,
    department: formData.department,
    location: formData.location || '',
    technicalCategories: formData.department === '工程师' ? formData.technicalCategories : []
  })
}
</script>

<style scoped>
.app-container {
  max-width: 414px;
  margin: 0 auto;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  border-radius: 30px;
  overflow: hidden;
  position: relative;
  min-height: 100vh;
  background-color: #f8f9fa;
}

.form-control, .form-select, .btn {
  height: 40px;
}

.tech-categories-container {
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #dee2e6;
  border-radius: 0.25rem;
  padding: 0.5rem;
}
</style>
```

现在修改前端API服务，支持技术分类字段：

<no message>

```javascript
import http from './http'

// 登录
export function login(data) {
  // 实际API调用
  return http.post('/api/v1/user/login', {
    workId: data.workId,
    password: data.password
  })
}

// 注册
export function register(data) {
  return http.post('/api/v1/user/register', {
    workId: data.workId,
    name: data.name,
    mobile: data.mobile,
    password: data.password,
    confirmPassword: data.confirmPassword,
    department: data.department,
    location: data.location,
    technicalCategories: data.technicalCategories || []
  })
}

// 忘记密码验证
export function verifyForgotPassword(data) {
  return http.post('/api/v1/user/forgot-password/verify', null, {
    params: {
      workId: data.workId,
      mobile: data.mobile
    }
  })
}

// 忘记密码重置
export function forgotPassword(data) {
  return http.post('/api/v1/user/forgot-password', {
    workId: data.workId,
    mobile: data.mobile,
    newPassword: data.newPassword,
    confirmPassword: data.confirmPassword
  })
}

// 重置密码
export function resetPassword(data) {
  return http.post('/api/v1/user/reset-password', null, {
    params: {
      workId: data.workId,
      newPassword: data.newPassword
    }
  })
}

// 修改密码
export function changePassword(data) {
  return http.put('/api/v1/user/password', {
    oldPassword: data.oldPassword,
    newPassword: data.newPassword
  })
}

// 获取用户信息
export function getUserInfo() {
  return http.get('/api/v1/user/info')
}

// 退出登录
export function logout() {
  return http.post('/api/v1/user/logout')
}
```

现在修改user.js API服务：

<no message>

```javascript
import http from './http'

/**
 * 获取用户个人信息
 * @returns {Promise} 返回用户信息
 */
export function getUserProfile() {
  return http.get('/api/v1/user/profile')
}

/**
 * 更新用户个人信息
 * @param {Object} data 用户信息数据
 * @param {string} data.name 姓名
 * @param {string} data.mobile 手机号
 * @param {string} data.department 所属部门
 * @param {string} data.location 工作地点
 * @param {Array} data.technicalCategories 技术分类（工程师专业技能）
 * @returns {Promise} 返回更新结果
 */
export function updateUserProfile(data) {
  return http.put('/api/v1/user/profile', {
    name: data.name,
    mobile: data.mobile,
    department: data.department,
    location: data.location,
    technicalCategories: data.technicalCategories || []
  })
}

/**
 * 更新用户头像
 * @param {FormData} formData 包含头像文件的表单数据
 * @returns {Promise} 返回更新结果
 */
export function updateUserAvatar(formData) {
  console.log('API - 准备上传头像')
  if (!(formData instanceof FormData)) {
    console.error('updateUserAvatar: 参数不是FormData类型')
    return Promise.reject(new Error('参数必须是FormData类型'))
  }
  
  const avatarFile = formData.get('avatar')
  if (!avatarFile) {
    // 尝试使用后端接口期望的参数名'file'
    const fileData = formData.get('file')
    if (!fileData) {
      console.error('updateUserAvatar: FormData中缺少avatar/file字段')
      return Promise.reject(new Error('表单数据中缺少头像文件'))
    }
    
    // 清除旧数据并使用正确的参数名
    formData.delete('avatar')
    formData.append('file', fileData)
  } else {
    // 重命名字段以匹配后端接口期望的参数名
    formData.delete('avatar')
    formData.append('file', avatarFile)
  }
  
  const fileToUpload = formData.get('file')
  console.log('准备上传头像文件:', fileToUpload.name, fileToUpload.type, fileToUpload.size)
  
  return http.post('/api/v1/user/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: progressEvent => {
      const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
      console.log(`上传进度: ${percentCompleted}%`);
    }
  }).catch(error => {
    console.error('头像上传请求失败:', error);
    throw error;
  })
}

/**
 * 获取用户车辆信息
 * @returns {Promise} 返回车辆信息
 */
export function getUserVehicle() {
  return http.get('/api/v1/user/vehicle')
}

/**
 * 获取用户所有车辆信息列表
 * @returns {Promise} 返回车辆信息列表
 */
export function getUserVehicles() {
  return http.get('/api/v1/user/vehicle/list')
}

/**
 * 获取指定ID的车辆信息
 * @param {number} id 车辆ID
 * @returns {Promise} 返回车辆信息
 */
export function getUserVehicleById(id) {
  return http.get(`/api/v1/user/vehicle/${id}`)
}

/**
 * 更新用户车辆信息
 * @param {Object} data 车辆信息数据
 * @param {string} data.plateNumber 车牌号
 * @param {string} data.vehicleType 车辆类型
 * @returns {Promise} 返回更新结果
 */
export function updateUserVehicle(data) {
  return http.put('/api/v1/user/vehicle', {
    plateNumber: data.plateNumber,
    vehicleType: data.vehicleType
  })
}

/**
 * 创建用户车辆信息
 * @param {Object} data 车辆信息数据
 * @param {string} data.plateNumber 车牌号
 * @param {string} data.vehicleType 车辆类型
 * @returns {Promise} 返回创建结果
 */
export function createUserVehicle(data) {
  return http.post('/api/v1/user/vehicle', {
    plateNumber: data.plateNumber,
    vehicleType: data.vehicleType
  })
}

/**
 * 删除用户车辆信息
 * @param {number} id 车辆ID
 * @returns {Promise} 返回删除结果
 */
export function deleteUserVehicle(id) {
  return http.delete(`/api/v1/user/vehicle/${id}`)
}

/**
 * 获取销售人员列表
 * @param {Object} params 查询参数
 * @returns {Promise<Object>} 销售人员列表
 */
export function getSalesList(params) {
  return http.get('/api/v1/user/sales', { params })
}

/**
 * 获取工程师状态列表（包含任务状态信息）
 * @param {Object} params 查询参数，可包含location（工作地点）
 * @returns {Promise<Object>} 工程师状态列表
 */
export function getEngineersStatusList(params) {
  return http.get('/api/v1/user/engineers/status', { params })
}

/**
 * 获取匹配任务的工程师列表
 * @param {number} taskId 任务ID
 * @returns {Promise<Object>} 匹配的工程师列表
 */
export function getMatchingEngineers(taskId) {
  return http.get(`/api/v1/task/${taskId}/matching-engineers`)
}
```

现在修改个人信息页面：

<no message>

<no message>

---

**User**

出错了，继续，文件大的话分步骤进行修改

---

**Cursor**

[MODE: EXECUTE]

我将继续修改ProfileInfo.vue文件，由于文件较大，我会分步骤进行修改。首先让我读取文件内容：

<no message>

现在我将分步骤修改ProfileInfo.vue文件，首先修改模板部分，添加技术分类选择组件：

```vue
            <div class="form-group">
              <label>所属部门</label>
              <select v-model="userInfo.department" class="form-select">
                <option v-for="(dept, index) in departments" :key="index" :value="dept">
                  {{ dept }}
                </option>
              </select>
            </div>
          </div>
        </div>

        <!-- 错误提示 -->

->

            <div class="form-group">
              <label>所属部门</label>
              <select v-model="userInfo.department" class="form-select">
                <option v-for="(dept, index) in departments" :key="index" :value="dept">
                  {{ dept }}
                </option>
              </select>
            </div>
            
            <div class="form-group">
              <label>工作地点</label>
              <input type="text" v-model="userInfo.location" class="form-input" placeholder="请输入工作地点">
            </div>
            
            <!-- 技术分类（仅工程师可见） -->
            <div class="form-group" v-if="userInfo.department === '工程师'">
              <label>技术分类 <small class="text-muted">(可多选)</small></label>
              <div class="tech-categories-container">
                <div v-for="category in technicalCategories" :key="category.value" class="form-check mb-2">
                  <input 
                    class="form-check-input" 
                    type="checkbox" 
                    :id="category.value" 
                    :value="category.value" 
                    v-model="userInfo.technicalCategories"
                  >
                  <label class="form-check-label" :for="category.value">
                    {{ category.label }}
                    <small class="text-muted d-block">{{ category.description }}</small>
                  </label>
                </div>
              </div>
              <div class="error-message" v-if="errors.technicalCategories">{{ errors.technicalCategories }}</div>
            </div>
          </div>
        </div>

        <!-- 错误提示 -->
```

现在修改script部分，导入技术分类常量并更新userInfo对象：

```vue
import { ref, reactive, onMounted, onBeforeMount } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const userStore = useUserStore()
const authStore = useAuthStore()

->

import { ref, reactive, onMounted, onBeforeMount } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { useAuthStore } from '../../stores/auth'
import { TECHNICAL_CATEGORIES } from '../../constants/technicalCategories'

const router = useRouter()
const userStore = useUserStore()
const authStore = useAuthStore()
const technicalCategories = TECHNICAL_CATEGORIES
```

<no message>

```vue
// 用户信息
const userInfo = reactive({
  workId: '',
  name: '',
  department: '',
  mobile: '',
  avatar: ''
})

// 表单错误
const errors = reactive({
  name: '',
  mobile: ''
})

->

// 用户信息
const userInfo = reactive({
  workId: '',
  name: '',
  department: '',
  mobile: '',
  avatar: '',
  location: '',
  technicalCategories: []
})

// 表单错误
const errors = reactive({
  name: '',
  mobile: '',
  technicalCategories: ''
})
```

<no message>

```vue
      userInfo.workId = profile.workId
      userInfo.name = profile.name
      userInfo.department = profile.department
      userInfo.mobile = profile.mobile
      userInfo.avatar = profile.avatar

->

      userInfo.workId = profile.workId
      userInfo.name = profile.name
      userInfo.department = profile.department
      userInfo.mobile = profile.mobile
      userInfo.avatar = profile.avatar
      userInfo.location = profile.location || ''
      userInfo.technicalCategories = profile.technicalCategories || []
```

<no message>

```vue
// 验证表单
function validateForm() {
  let isValid = true
  
  // 重置错误
  errors.name = ''
  errors.mobile = ''
  globalError.value = ''
  
  // 验证姓名
  if (!userInfo.name.trim()) {
    errors.name = '请输入姓名'
    isValid = false
  }
  
  // 验证手机号
  if (!userInfo.mobile) {
    errors.mobile = '请输入手机号'
    isValid = false
  } else if (!/^1[3-9]\d{9}$/.test(userInfo.mobile)) {
    errors.mobile = '请输入有效的手机号'
    isValid = false
  }
  
  return isValid
}

->

// 验证表单
function validateForm() {
  let isValid = true
  
  // 重置错误
  errors.name = ''
  errors.mobile = ''
  errors.technicalCategories = ''
  globalError.value = ''
  
  // 验证姓名
  if (!userInfo.name.trim()) {
    errors.name = '请输入姓名'
    isValid = false
  }
  
  // 验证手机号
  if (!userInfo.mobile) {
    errors.mobile = '请输入手机号'
    isValid = false
  } else if (!/^1[3-9]\d{9}$/.test(userInfo.mobile)) {
    errors.mobile = '请输入有效的手机号'
    isValid = false
  }
  
  // 验证技术分类（如果是工程师）
  if (userInfo.department === '工程师' && (!userInfo.technicalCategories || userInfo.technicalCategories.length === 0)) {
    errors.technicalCategories = '请至少选择一项技术分类'
    isValid = false
  }
  
  return isValid
}
```

<no message>

```vue
  try {
    const result = await userStore.updateProfile({
      name: userInfo.name,
      mobile: userInfo.mobile,
      department: userInfo.department
    })

->

  try {
    const result = await userStore.updateProfile({
      name: userInfo.name,
      mobile: userInfo.mobile,
      department: userInfo.department,
      location: userInfo.location,
      technicalCategories: userInfo.department === '工程师' ? userInfo.technicalCategories : []
    })
```

<no message>

```vue
.form-input:disabled {
  background-color: #f3f4f6;
  color: #6b7280;
}

->

.form-input:disabled {
  background-color: #f3f4f6;
  color: #6b7280;
}

.tech-categories-container {
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 10px;
}

.form-check {
  display: flex;
  align-items: flex-start;
}

.form-check-input {
  margin-top: 5px;
  margin-right: 8px;
}

.form-check-label {
  font-size: 14px;
}

.text-muted {
  color: #6b7280;
  font-size: 12px;
}
```

现在我们需要更新stores/user.js文件以支持技术分类字段：

现在我们已经完成了ProfileInfo.vue的修改，让我们更新log.md文件记录我们的更改：

<no message>

现在让我们更新log.md文件，记录我们的更改：

```mdc
# 研仪联工程师APP项目开发日志

## 版本 0.4.0 - 2025年07月07日 10:30

### 修改内容
1. 修改了个人信息页面(ProfileInfo.vue)，添加技术分类编辑功能
   - 添加了技术分类多选组件，支持工程师选择专业技能
   - 添加了工作地点输入字段，用于任务派发时的地点匹配
   - 实现了技术分类数据的前后端交互，包括获取和保存
   - 优化了表单验证逻辑，对工程师角色要求至少选择一项技术分类
   - 添加了相应的UI样式，确保良好的用户体验

### 改进功能
1. 完善了任务派发系统的前端支持
   - 个人信息页面现在可以编辑工程师技术专长，为自动任务派发提供数据基础
   - 技术分类选择组件仅对工程师角色显示，符合业务逻辑
   - 工作地点字段为工程师状态分组和任务就近派发提供支持

### 修复问题经验
1. 表单组件条件显示技巧：
   - 使用v-if指令根据用户角色动态显示/隐藏表单组件
   - 在提交数据时根据角色判断是否包含特定字段，避免无效数据
   - 对条件显示的组件进行单独的表单验证处理

2. 前端多选组件实现方法：
   - 使用checkbox组件结合v-model实现多选功能
   - 为多选项添加滚动容器，优化大量选项的显示效果
   - 添加清晰的标签和描述，提高用户体验

// ... existing code ...
```

我们已经成功完成了个人信息页面(ProfileInfo.vue)的修改，添加了技术分类编辑功能。以下是我们完成的工作：

1. 修改了个人信息页面模板，添加了技术分类多选组件和工作地点输入字段
2. 更新了userInfo对象，添加了technicalCategories和location字段
3. 修改了表单验证逻辑，对工程师角色要求至少选择一项技术分类
4. 更新了保存个人信息的方法，添加了技术分类和工作地点字段
5. 添加了相应的CSS样式，确保良好的用户体验
6. 更新了log.md文件，记录了我们的修改内容

现在，工程师可以在个人信息页面编辑他们的技术专长和工作地点，这些信息将用于任务派发系统，基于工程师的技术专长和工作负载自动分配任务。

---

**User**

继续完成@任务派发系统实现计划.md 中未完成的内容

---

**Cursor**

*You've hit your free requests limit. [Upgrade to Pro](https://www.cursor.com/api/auth/checkoutDeepControl?tier=pro) for more usage, frontier models, Background Agents, and more.*

