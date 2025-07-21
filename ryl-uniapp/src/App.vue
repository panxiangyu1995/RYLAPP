<template>
  <view id="app">
    <!-- uni-app 在 pages.json 中定义页面，不需要 router-view -->
  </view>
</template>
 
<script setup>
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app';
import { setupInterceptors } from '@/utils/interceptor';

onLaunch(() => {
  console.log('App Launch');
  
  // 设置路由拦截器
  setupInterceptors();
  
  // 监听需要登录重定向的全局事件
  // uni.$on('needs-login-redirect', () => {
  //   uni.reLaunch({
  //     url: '/pages/login/login'
  //   });
  // });
});

onShow(() => {
  console.log('App Show');
});

onHide(() => {
  console.log('App Hide');
});
</script>

<style>
	/*每个页面公共css */
	@import '@/styles/tailwind.css';
</style>