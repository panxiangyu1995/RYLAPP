<template>
  <view class="bg-ui-vibrant-gradient rounded-lg shadow-md overflow-hidden p-4 mb-6">
    <!-- 未登录状态 -->
    <view v-if="!userStore.isLoggedIn" class="flex items-center justify-between">
      <view>
        <view class="font-medium text-lg text-white">欢迎使用</view>
        <view class="text-sm text-gray-200">登录后可提交订单及查看订单进展</view>
      </view>
      <button @click="goToLogin" class="px-4 py-2 rounded-lg font-bold transition-colors bg-ui-accent-lime text-ui-text-black hover:opacity-90 flex items-center">
        <LogInIcon :size="18" color="#111827" class="mr-1" />
        登录
      </button>
    </view>
    
    <!-- 已登录状态 -->
    <view v-else class="flex items-center">
      <view class="relative">
        <image 
          :src="getUserAvatar()" 
          class="w-16 h-16 rounded-full mr-4 object-cover border-2 border-white/50 bg-white"
          alt="用户头像"
        />
        <view class="absolute -bottom-1 -right-1 w-6 h-6 bg-green-500 rounded-full flex items-center justify-center text-white text-xs border-2 border-white">
          <CheckIcon :size="14" color="white" />
        </view>
      </view>
      <view class="flex-1">
        <view class="font-medium text-lg text-white">{{ userStore.userInfo.contact || '未设置姓名' }}</view>
        <view class="text-sm text-gray-200 flex items-center">
          <Building2Icon :size="14" class="mr-1" />
          {{ userStore.userInfo.name || '未设置公司' }}
        </view>
        <view class="text-sm text-gray-200 flex items-center">
          <PhoneIcon :size="14" class="mr-1" />
          {{ userStore.userInfo.phone || '未设置电话' }}
        </view>
      </view>
      <view class="flex flex-col space-y-2 justify-center">
        <button @click="goToProfile" class="px-4 py-2 rounded-lg font-bold transition-colors bg-ui-accent-lime text-ui-text-black hover:opacity-90 text-sm flex items-center w-20 justify-center">
          <EditIcon :size="14" class="mr-1" />
          编辑
        </button>
        <button @click="logout" class="px-4 py-2 rounded-lg font-medium transition-colors bg-red-600 text-white hover:bg-red-700 text-sm flex items-center w-20 justify-center">
          <LogOutIcon :size="14" class="mr-1" />
          退出
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { useUserStore } from '@/stores/user';
import LogInIcon from '@/components/icons/LogInIcon.vue';
import CheckIcon from '@/components/icons/CheckIcon.vue';
import Building2Icon from '@/components/icons/Building2Icon.vue';
import PhoneIcon from '@/components/icons/PhoneIcon.vue';
import EditIcon from '@/components/icons/EditIcon.vue';
import LogOutIcon from '@/components/icons/LogOutIcon.vue';

const userStore = useUserStore();

// 获取用户头像URL，优先使用avatarUrl，然后是wechat_avatar_url，最后是默认头像
const getUserAvatar = () => {
  const userInfo = userStore.userInfo;
  if (!userInfo) return '/static/images/avatar-placeholder.png';

  let avatarUrl = userInfo.avatarUrl || userInfo.wechat_avatar_url;
  if (!avatarUrl || avatarUrl.startsWith('blob:')) {
    return '/static/images/avatar-placeholder.png';
  }
  
  // 检查URL是否为相对路径（不包含http或https），如果是则确保路径正确
  if (!avatarUrl.startsWith('http')) {
    return avatarUrl.startsWith('/') ? avatarUrl : `/${avatarUrl}`;
  }
  
  return avatarUrl;
};

const goToLogin = () => {
  if (userStore.isLoggedIn) {
    // 如果已登录，则跳转到个人中心
    uni.navigateTo({ url: '/pages/profile/profile' });
  } else {
    // 如果未登录，则跳转到登录页
    uni.navigateTo({ url: '/pages/login/login' });
  }
};

const goToProfile = () => {
  uni.navigateTo({ url: '/pages/profile/profile' });
};

// 退出登录
const logout = () => {
  userStore.logout();
  uni.reLaunch({ url: '/pages/login/login' });
};
</script> 