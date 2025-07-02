<template>
  <div class="min-h-screen bg-gray-50">
    <router-view />
    
    <!-- 开发环境下显示调试登录工具 -->
    <DebugLogin v-if="isDev" />
  </div>
</template>
 
<script setup>
import DebugLogin from '@/components/DebugLogin.vue';

// 判断是否为开发环境
const isDev = import.meta.env.DEV;
</script>

<style>
/* 全局样式增强 */
:root {
  --primary-dark: #0E4472;
  --primary-medium: #286B9C;
  --primary-light: #6C9DBB;
}

/* 平滑滚动 */
html {
  scroll-behavior: smooth;
}

/* 改进的阴影效果 */
.card {
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

/* 按钮过渡效果 */
.btn {
  transition: all 0.3s ease;
}
</style>