<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { StatusBar } from '@capacitor/status-bar'
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

onMounted(async () => {
  // 应用启动时自动检查更新
  appStore.checkUpdate();

  // 获取状态栏高度并设置为CSS变量
  try {
    const info = await StatusBar.getInfo();
    if (info.statusBarHeight > 0) {
      document.documentElement.style.setProperty('--status-bar-height', `${info.statusBarHeight}px`);
    }
  } catch (err) {
    console.error('获取状态栏信息失败:', err);
  }
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
  /* 使用 var() 函数和备用值，以防CSS变量未设置 */
  padding-top: var(--status-bar-height, 25px); 
  padding-bottom: 56px; /* 为底部导航留出空间 */
}

/* 登录页面不需要底部padding */
.login-container {
  padding-bottom: 0 !important;
}
</style>
