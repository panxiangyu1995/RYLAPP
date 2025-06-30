<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import BottomNavigation from './components/BottomNavigation.vue'
import StagewiseToolbarLoader from './components/StagewiseToolbarLoader.vue'

const route = useRoute()

// 判断当前路由是否需要显示底部导航
const showBottomNav = computed(() => {
  // 这些路由不显示底部导航
  const noNavRoutes = ['Login', 'Register', 'ForgotPassword']
  return !noNavRoutes.includes(route.name)
})
</script>

<template>
  <div class="app-container">
    <router-view />
    <BottomNavigation v-if="showBottomNav" />
    <StagewiseToolbarLoader />
  </div>
</template>

<style>
/* 全局样式已移至style.css */
.app-container {
  padding-bottom: 56px; /* 为底部导航留出空间 */
}

/* 登录页面不需要底部padding */
.login-container {
  padding-bottom: 0 !important;
}
</style>
