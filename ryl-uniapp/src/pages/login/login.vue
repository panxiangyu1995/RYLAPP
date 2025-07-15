<template>
  <view class="p-4 flex flex-col min-h-screen">
    <view class="flex-1 flex flex-col items-center justify-center">
      <view class="w-full max-w-sm">
        <view class="text-center mb-8">
          <view class="text-2xl font-bold text-primary-dark">瑞屹林</view>
          <view class="text-gray-500 mt-2">科研仪器专业服务平台</view>
        </view>
        
        <view class="bg-white p-6 rounded-lg shadow-md">
          <!-- 登录方式切换 -->
          <view class="flex mb-6 border-b">
            <button 
              :class="['flex-1 py-2 text-center', activeTab === 'password' ? 'text-primary-dark border-b-2 border-primary-dark font-bold' : 'text-gray-500']"
              @click="activeTab = 'password'"
            >
              账号密码登录
            </button>
            <block #ifdef MP-WEIXIN>
            <button 
              :class="['flex-1 py-2 text-center', activeTab === 'wechat' ? 'text-primary-dark border-b-2 border-primary-dark font-bold' : 'text-gray-500']"
              @click="activeTab = 'wechat'"
            >
              微信一键登录
            </button>
            </block>
          </view>
          
          <!-- 账号密码登录表单 -->
          <view v-if="activeTab === 'password'">
            <view class="mb-4">
              <view class="block text-gray-700 text-sm font-bold mb-2" for="contact">
                登录用户名
              </view>
              <input 
                id="contact" 
                v-model="loginForm.contact" 
                type="text" 
                class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-light"
                placeholder="请输入登录用户名"
              />
            </view>
            
            <view class="mb-6">
              <view class="block text-gray-700 text-sm font-bold mb-2" for="password">
                密码
              </view>
              <view class="relative">
                <input 
                  id="password" 
                  v-model="loginForm.password" 
                  :type="showPassword ? 'text' : 'password'" 
                  class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-light"
                  placeholder="请输入密码"
                />
                <button 
                  type="button"
                  @click="showPassword = !showPassword"
                  class="absolute right-3 top-2.5 text-gray-500"
                >
                  {{ showPassword ? '隐藏' : '显示' }}
                </button>
              </view>
            </view>
            
            <button 
              @click="handlePasswordLogin" 
              class="w-full flex items-center justify-center bg-primary-medium hover:bg-primary-dark text-white font-bold py-3 rounded-lg shadow-md"
              :disabled="loading"
            >
              <text v-if="loading">登录中...</text>
              <text v-else>登 录</text>
            </button>
            
            <view class="mt-4 text-center flex justify-between">
              <button @click="goBack" class="text-primary-medium">返回首页</button>
              <button @click="goToRegister" class="text-primary-medium">注册新账号</button>
            </view>
          </view>
          
          <!-- 微信一键登录 -->
          <block #ifdef MP-WEIXIN>
          <view v-if="activeTab === 'wechat'">
            <view class="text-center mb-6">使用微信账号快速登录</view>
            
            <button 
              @click="handleWechatLogin" 
              class="w-full flex items-center justify-center bg-green-500 hover:bg-green-600 text-white font-bold py-3 rounded-lg shadow-md"
              :disabled="loading"
            >
              <text v-if="loading">登录中...</text>
              <text v-else>微信一键登录</text>
            </button>
            
            <view class="mt-4 text-center">
              <button @click="goBack" class="text-primary-medium">返回首页</button>
            </view>
          </view>
          </block>
        </view>
      </view>
    </view>
    
    <view class="mt-8 text-center text-xs text-gray-500">
      <view>登录即代表您已阅读并同意</view>
      <view>《用户协议》和《隐私政策》</view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { useUserStore } from '@/stores/user';
import { onShow, onLoad } from '@dcloudio/uni-app';

const userStore = useUserStore();
const loading = ref(false);
const activeTab = ref('password'); // 默认显示账号密码登录
const showPassword = ref(false);
let redirectUrl = '/pages/home/home';

// 登录表单
const loginForm = ref({
  contact: '',
  password: ''
});

onLoad((options) => {
  if (options && options.redirect) {
    redirectUrl = decodeURIComponent(options.redirect);
  }
});


// 处理账号密码登录
const handlePasswordLogin = async () => {
  if (!loginForm.value.contact) {
    uni.showToast({ title: '请输入用户名', icon: 'none' });
    return;
  }
  
  if (!loginForm.value.password) {
    uni.showToast({ title: '请输入密码', icon: 'none' });
    return;
  }
  
  loading.value = true;
  
  try {
    // 调用登录API
    await userStore.passwordLogin(loginForm.value.contact, loginForm.value.password);
    
    // 登录成功后的跳转
    uni.reLaunch({ url: redirectUrl });
  } catch (error) {
    console.error('账号密码登录失败:', error);
    uni.showToast({ title: error.message || '登录失败', icon: 'none' });
  } finally {
    loading.value = false;
  }
};

// 处理微信登录
const handleWechatLogin = async () => {
  loading.value = true;
  
  uni.login({
    provider: 'weixin',
    success: async (res) => {
      try {
        await userStore.wechatLogin(res.code);
        uni.reLaunch({ url: redirectUrl });
      } catch (error) {
        console.error('微信登录失败:', error);
        uni.showToast({ title: error.message || '微信登录失败', icon: 'none' });
      } finally {
        loading.value = false;
      }
    },
    fail: (err) => {
      console.error('微信登录失败:', err);
      uni.showToast({ title: '微信登录授权失败', icon: 'none' });
      loading.value = false;
    }
  });
};

// 前往注册页面
const goToRegister = () => {
  uni.navigateTo({ url: '/pages/register/register' });
};

// 返回首页
const goBack = () => {
  uni.reLaunch({ url: '/pages/home/home' });
};
</script> 