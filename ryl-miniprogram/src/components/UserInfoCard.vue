<template>
  <div class="card p-4 mb-6 border border-neutral-light">
    <!-- 未登录状态 -->
    <div v-if="!userStore.isLoggedIn" class="flex items-center justify-between">
      <div>
        <h3 class="font-medium text-lg text-primary-dark">欢迎使用</h3>
        <p class="text-sm text-gray-600">登录后可查看任务进展及提交任务</p>
      </div>
      <button @click="goToLogin" class="btn btn-primary flex items-center">
        <LogIn size="18" class="mr-1" />
        登录
      </button>
    </div>
    
    <!-- 已登录状态 -->
    <div v-else class="flex items-center">
      <div class="relative">
        <img 
          :src="userStore.userInfo.wechat_avatar_url || '/avatar-placeholder.png'" 
          class="w-16 h-16 rounded-full mr-4 object-cover border-2 border-primary-light"
          alt="用户头像"
        >
        <div class="absolute -bottom-1 -right-1 w-6 h-6 bg-green-500 rounded-full flex items-center justify-center text-white text-xs border-2 border-white">
          <Check size="14" />
        </div>
      </div>
      <div class="flex-1">
        <h3 class="font-medium text-lg">{{ userStore.userInfo.contact || '未设置姓名' }}</h3>
        <p class="text-sm text-gray-600 flex items-center">
          <Building2 size="14" class="mr-1" />
          {{ userStore.userInfo.name || '未设置公司' }}
        </p>
        <p class="text-xs text-gray-500 flex items-center mt-1">
          <Phone size="12" class="mr-1" />
          {{ userStore.userInfo.phone || '未设置手机' }}
        </p>
      </div>
      <button @click="goToProfile" class="btn btn-outline text-sm flex items-center">
        <Edit size="14" class="mr-1" />
        编辑
      </button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { LogIn, Check, Building2, Phone, Edit } from 'lucide-vue-next';

const router = useRouter();
const userStore = useUserStore();

const goToLogin = () => {
  router.push('/login');
};

const goToProfile = () => {
  router.push('/profile');
};
</script> 