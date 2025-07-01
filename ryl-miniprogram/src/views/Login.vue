<template>
  <div class="p-4 flex flex-col min-h-screen">
    <div class="flex-1 flex flex-col items-center justify-center">
      <div class="w-full max-w-sm">
        <div class="text-center mb-8">
          <h1 class="text-2xl font-bold text-primary-dark">瑞屹林</h1>
          <p class="text-gray-500 mt-2">科研仪器专业服务平台</p>
        </div>
        
        <div class="bg-white p-6 rounded-lg shadow-md">
          <p class="text-center mb-6">使用微信账号快速登录</p>
          
          <button 
            @click="handleWechatLogin" 
            class="btn w-full flex items-center justify-center bg-green-500 hover:bg-green-600 text-white py-3"
            :disabled="loading"
          >
            <span v-if="loading">登录中...</span>
            <span v-else>微信一键登录</span>
          </button>
          
          <div class="mt-4 text-center">
            <button @click="goBack" class="text-primary-medium">返回首页</button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="mt-8 text-center text-xs text-gray-500">
      <p>登录即代表您已阅读并同意</p>
      <p>《用户协议》和《隐私政策》</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const loading = ref(false);

// 处理微信登录
const handleWechatLogin = async () => {
  loading.value = true;
  
  try {
    // 模拟获取微信登录code的过程
    // 在实际微信小程序中，应使用 wx.login() 获取code
    const mockCode = 'mock_wechat_code_' + Date.now();
    
    // 调用登录API
    await userStore.wechatLogin(mockCode);
    
    // 登录成功后的跳转
    const redirect = route.query.redirect || '/';
    router.replace(redirect);
  } catch (error) {
    console.error('微信登录失败:', error);
    // 处理登录失败
  } finally {
    loading.value = false;
  }
};

// 返回首页
const goBack = () => {
  router.push('/');
};
</script> 