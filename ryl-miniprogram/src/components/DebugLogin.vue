<template>
  <div class="fixed bottom-0 left-0 right-0 bg-gray-100 border-t border-gray-300 p-3 z-50">
    <div class="flex justify-between items-center">
      <div>
        <span class="text-sm font-medium">调试模式:</span>
        <span class="text-xs ml-2">{{ userStore.isLoggedIn ? '已登录' : '未登录' }}</span>
      </div>
      
      <div class="flex space-x-2">
        <button 
          @click="mockLogin" 
          class="px-3 py-1 bg-blue-500 text-white text-sm rounded"
          :disabled="userStore.isLoggedIn"
        >
          模拟登录
        </button>
        <button 
          @click="logout" 
          class="px-3 py-1 bg-gray-500 text-white text-sm rounded"
          :disabled="!userStore.isLoggedIn"
        >
          退出登录
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();

// 模拟登录
const mockLogin = () => {
  // 模拟用户数据
  const mockUserData = {
    id: 1001,
    name: '瑞屹林科技有限公司',
    contact: '张工程师',
    phone: '13800138000',
    wechat_avatar_url: 'https://thirdwx.qlogo.cn/mmopen/vi_32/sample/132',
    role: 'ROLE_USER',
    created_at: '2025-07-01 10:00:00'
  };
  
  // 设置模拟token和用户信息
  userStore.setToken('mock-debug-token-' + Date.now());
  userStore.setUserInfo(mockUserData);
};

// 退出登录
const logout = () => {
  userStore.logout();
};
</script> 