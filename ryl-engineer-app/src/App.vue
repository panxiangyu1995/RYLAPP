<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
// import { StatusBar } from '@capacitor/status-bar' // 移除
import BottomNavigation from './components/BottomNavigation.vue'
import StagewiseToolbarLoader from './components/StagewiseToolbarLoader.vue'
import UpdateDialog from './components/UpdateDialog.vue'
import { useAppStore } from './stores/app'

const route = useRoute()
const appStore = useAppStore()

// 判断当前路由是否需要显示底部导航
const showBottomNav = computed(() => {
  // 这些路由不显示底部导航
  const noNavRoutes = ['Login', 'Register', 'ForgotPassword']
  return !noNavRoutes.includes(route.name)
})

onMounted(() => { // 移除 async
  // 应用启动时自动检查更新
  appStore.checkUpdate();

  // 移除获取状态栏高度的逻辑
});
</script>

<template>
  <div class="app-container">
    <router-view />
    <BottomNavigation v-if="showBottomNav" />
    <StagewiseToolbarLoader />
    <UpdateDialog />
  </div>
</template>

<style>
/* 全局样式已移至style.css */
.app-container {
  /* 使用 env() 函数和备用值 */
  padding-top: env(safe-area-inset-top, 25px); 
  padding-bottom: 56px; /* 为底部导航留出空间 */
}

/* 登录页面不需要底部padding */
.login-container {
  padding-bottom: 0 !important;
}

/* 为需要适配状态栏的标题栏定义一个全局类 */
.main-header {
  padding-top: env(safe-area-inset-top, 25px);
  background-color: #fff; /* 根据你的设计添加背景色 */
  position: sticky; /* 或者 fixed, 取决于你的布局需求 */
  top: 0;
  z-index: 100;
}
</style>
