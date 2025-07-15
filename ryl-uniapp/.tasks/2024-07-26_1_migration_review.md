# 迁移后审查待办事项 (2024-07-26)

这是一个在对 `uni-app` 迁移项目进行第四阶段（UI/组件迁移）后的全面审查时，发现的待办事项列表。

---

### 1. `ryl-uniapp/src/components/ServiceEvaluation.vue`

- **类型**: 代码优化
- **问题**: 表单提交方式不符合 `uni-app` 最佳实践。
- **具体描述**: 组件当前同时使用了 `@submit.prevent`、`type="submit"` 和 `@click` 来触发表单提交。这在功能上可以工作，但不是标准方式。
- **修复方案**:
  1.  修改 `<form @submit.prevent="submitEvaluation">` 为 `<form @submit="submitEvaluation">`。
  2.  修改 `<button type="submit" ... @click="submitEvaluation">` 为 `<button form-type="submit" ...>`，移除 `type` 和 `@click` 属性。

### 2. `ryl-uniapp/src/components/TaskProgress.vue`

- **类型**: UI/UX Bug
- **问题**: 图片上的“下载”按钮在手机端不可见。
- **具体描述**: 用于显示下载按钮的 `group-hover` 效果在移动端无效，导致用户无法看到和使用下载功能。
- **修复方案**:
  1.  找到图片渲染区域的 `<view class="absolute bottom-0 ...">`。
  2.  移除 `opacity-0` 和 `group-hover:opacity-100` 这两个 TailwindCSS 类，让下载按钮区域默认可见。

### 3. `ryl-uniapp/src/components/TaskProgress.vue`

- **类型**: 逻辑 Bug
- **问题**: 在特定步骤下，图片列表会重复渲染两次。
- **具体描述**: 当 `currentStep` 为 2 或 3 时，组件内两个不同的 `v-if` 条件都会满足，导致 `stepContent.images` 被渲染两次。
- **修复方案**:
  1.  审查模板代码，定位到组件末尾多余的图片渲染块。
  2.  删除该重复的图片列表渲染代码块。

### 4. `ryl-uniapp/src/views/Login.vue`

- **类型**: 逻辑 Bug
- **问题**: “微信一键登录”功能在非小程序环境下会显示并报错。
- **具体描述**: `uni.login({ provider: 'weixin' })` 仅在微信小程序中可用，但在 H5 等其他环境中，用户仍然可以看到并点击该登录按钮，导致功能不可用和控制台错误。
- **修复方案**:
  1.  使用 `uni-app` 的条件编译指令。
  2.  将“微信一键登录”的 `<button>`（切换 Tab 的按钮）和对应的 `<view>`（登录表单区域）都包裹在 `<#ifdef MP-WEIXIN> ... </#ifdef>` 块中。
  3.  这样，微信登录相关的 UI 将只在编译为微信小程序时出现。

### 5. `ryl-uniapp/src/views/Register.vue`

- **类型**: 代码优化
- **问题**: 表单提交方式不符合 `uni-app` 最佳实践。
- **具体描述**: 与 `ServiceEvaluation.vue` 中的问题类似，该组件也混合使用了 `@submit.prevent`, `type="submit"` 和 `@click` 来触发表单提交。
- **修复方案**:
  1.  修改 `<form @submit.prevent="handleRegister">` 为 `<form @submit="handleRegister">`。
  2.  修改 `<button type="submit" ... @click="handleRegister">` 为 `<button form-type="submit" ...>`，并移除 `type` 和 `@click` 属性。

### 6. `ryl-uniapp/src/views/TaskCreate.vue` (需要大规模重构)

- **类型**: 严重 Bug / 迁移不完整
- **问题**: 该组件的核心逻辑完全没有适配 `uni-app`，仍在使用大量 Web-only API。
- **具体待办事项**:
  1.  **修复导航**:
      -   将所有 `router.push`, `router.back` 等 `vue-router` 调用替换为 `uni.navigateTo`, `uni.navigateBack`, `uni.reLaunch`。
  2.  **修复用户提示**:
      -   移除所有直接操作 DOM 的 `createToast`, `removeToast` 函数。
      -   将所有提示替换为 `uni.showToast` 或 `uni.showLoading`。
      -   将所有 `confirm()` 对话框 (`showDuplicateConfirmDialog`) 替换为 `uni.showModal`。
      -   移除 `handleUploadError` 中操作 DOM 的逻辑。
  3.  **修复本地存储**:
      -   移除所有对 `localStorage` 和未定义的 `taskStorage` 的引用。
      -   使用 `uni.setStorageSync` 和 `uni.getStorageSync` 重写检查重复订单的逻辑。
  4.  **修复数据流**:
      -   为 `<upload-image>` 和 `<upload-file>` 组件添加 `v-model` 绑定 (`v-model="formData.images"` 和 `v-model="formData.files"`)，确保文件数据能传递到父组件。
  5.  **修复表单提交**:
      -   与之前的文件类似，将表单的提交方式优化为 `uni-app` 的标准实践（使用 `form-type="submit"`）。
  6.  **生命周期适配**:
      -   将 `onMounted` 中加载客户信息的逻辑迁移到 `onLoad` 或 `onShow`，确保每次进入页面都能正确加载。 