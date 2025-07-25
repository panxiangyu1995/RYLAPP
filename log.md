# 研仪联工程师APP项目更新日志

## 版本 0.0.1 - 2025年07月01日

### 修改内容
1. 优化了TaskCreate.vue文件，删除了重复的仪器信息部分
2. 创建了DeviceBasicInfo.vue组件来封装常用的仪器基本信息字段
3. 将图片上传组件的最大数量从8张改为9张
4. 修改了UploadImage.vue组件，使用maxCount属性动态显示最大上传数量

### 修复问题
- 无

## 版本 0.0.2 - 2025年07月01日

### 修改内容
1. 修复了路由配置问题，将TaskProgress.vue的路由路径从components目录修改为views目录
2. 创建了TaskDetail.vue视图文件，用于显示任务详情
3. 创建了小程序API文档，为后端开发人员提供参考

### 修复问题
- 修复了路由配置错误导致的页面无法访问问题
- 经验：在设置路由时，需要确保组件路径正确，尤其是views和components目录的区分

## 版本 0.0.3 - 2025年07月01日

### 修改内容
1. 修复了Home.vue中图标导入错误问题，将不存在的Tool图标替换为Wrench图标

### 修复问题
- 修复了lucide-vue-next库中Tool图标不存在导致的运行时错误
- 经验：使用第三方图标库时，需要先查阅文档确认图标名称是否存在

## 版本 0.0.4 - 2025年07月01日

### 修改内容
1. 修改了DeviceBasicInfo.vue组件，将仪器类型从输入框改为下拉选择框
2. 添加了8种预设的仪器类型选项：色谱、光谱类，光谱类，生命科学类，三项一柜基础仪器类，环境监测类，主板类，电气类，其他类

### 修复问题
- 无

### 改进功能
- 通过下拉选择框限制用户输入，提高数据一致性
- 根据项目需求文档中的仪器类型分类实现预设选项

## 版本 0.0.5 - 2025年07月01日

### 修改内容
1. 创建了taskIdGenerator.js工具函数，实现任务ID的自动生成
2. 创建了pinyinUtil.js工具函数，用于获取中文字符的拼音首字母
3. 在TaskCreate.vue中集成了任务ID生成功能，提交任务时自动生成任务ID

### 改进功能
- 实现了任务ID的自动生成，格式为"RYL + 公司首字母缩写 + 年月日 + 4位随机数"
- 支持中英文混合的公司名称处理，自动提取首字母
- 自动过滤公司名称中的省市前缀和常见后缀，提取核心名称

### 技术亮点
- 使用简化版拼音首字母映射表，无需引入额外依赖
- 支持常见汉字的拼音首字母转换
- 实现了公司名称智能处理，提取更有意义的缩写

## 版本 0.0.6 - 2025年07月01日

### 修改内容
1. 修改了taskIdGenerator.js中的公司名称处理逻辑，不再移除省市前缀
2. 调整了首字母提取逻辑，只取公司名称的前5个字符的首字母
3. 增加了测试用例，确保任务ID生成的正确性

### 修复问题
- 修复了部分公司名称缩写不符合预期的问题
- 经验：在处理用户输入时，应尽量保留原始信息，避免过度处理导致信息丢失

### 改进功能
- 保留了公司名称中的省市前缀，使生成的任务ID更加准确
- 限制只取前5个字符的首字母，使缩写更加一致 

## 版本 0.0.7 - 2025年07月02日

### 修改内容
1. 数据库任务表添加了两个新字段：
   - appointment_time：约定上门时间
   - price_confirmed：客户是否已确认报价(0-未确认，1-已确认)
2. 后端API增强：
   - 修改TaskController，支持处理约定上门时间和报价确认
   - 在TaskService接口中添加了新方法
   - 实现TaskServiceImpl中的方法，支持约定上门时间和报价确认功能
3. APP前端功能增强：
   - 更新TaskFlowController.vue，添加约定上门时间选择器
   - 修改TaskStepRecordForm.vue，添加约定上门时间输入
   - 更新taskFlow.js store，支持约定上门时间和报价相关操作
4. 小程序前端功能增强：
   - 更新TaskProgress.vue，添加报价确认按钮
   - 更新TaskDetail.vue，添加处理报价确认的方法
   - 在task.js store中添加confirmTaskPrice方法
   - 在mockData.js中添加confirmMockTaskPrice函数

### 修复问题
- 无

### 改进功能
- 实现了APP判断不需要上门时直接跳转到服务评价步骤的功能
- 实现了约定上门时间的选择和存储功能
- 实现了小程序报价确认功能与APP报价填写功能
- 优化了任务流程的用户体验

### 技术亮点
- 前后端一体化设计，确保数据流转的一致性
- 使用Vue.js的响应式特性实现动态表单
- 实现了模拟数据的状态管理，便于小程序前端开发测试 