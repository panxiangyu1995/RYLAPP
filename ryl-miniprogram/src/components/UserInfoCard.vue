<template>
  <div class="card p-4 mb-4">
    <!-- 未登录状态 -->
    <div v-if="!userStore.isLoggedIn" class="flex items-center justify-between">
      <div>
        <h3 class="font-medium text-lg">欢迎使用</h3>
        <p class="text-sm text-gray-600">登录后可查看任务进展及提交任务</p>
      </div>
      <button @click="goToLogin" class="btn btn-primary">登录</button>
    </div>
    
    <!-- 已登录状态 -->
    <div v-else class="flex items-center">
      <img 
        :src="userStore.userInfo.wechat_avatar_url || '/avatar-placeholder.png'" 
        class="w-14 h-14 rounded-full mr-4 object-cover"
        alt="用户头像"
      >
      <div class="flex-1">
        <h3 class="font-medium">{{ userStore.userInfo.contact || '未设置姓名' }}</h3>
        <p class="text-sm text-gray-600">{{ userStore.userInfo.name || '未设置公司' }}</p>
        <p class="text-xs text-gray-500">{{ userStore.userInfo.phone || '未设置手机' }}</p>
      </div>
      <button @click="goToProfile" class="btn btn-outline text-sm">编辑</button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const userStore = useUserStore();

const goToLogin = () => {
  router.push('/login');
};

const goToProfile = () => {
  router.push('/profile');
};
</script> 