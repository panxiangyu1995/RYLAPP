<template>
  <view class="p-4 flex flex-col min-h-screen bg-ui-bg-white">
    <view class="flex-1 flex flex-col items-center justify-center">
      <view class="w-full max-w-sm">
        <view class="text-center mb-8">
          <view class="text-2xl font-bold text-ui-text-black">瑞屹林</view>
          <view class="text-gray-500 mt-2">科研仪器专业服务平台</view>
        </view>
        
        <view class="bg-white p-6 rounded-lg shadow-md">
          <!-- 登录方式切换 -->
          <view class="flex mb-6 border-b">
            <button 
              :class="['flex-1 py-2 text-center', activeTab === 'password' ? 'text-ui-text-black border-b-2 border-ui-blue-end font-bold' : 'text-gray-500']"
              @click="activeTab = 'password'"
            >
              账号密码登录
            </button>
            <block #ifdef MP-WEIXIN>
            <button 
              :class="['flex-1 py-2 text-center', activeTab === 'wechat' ? 'text-ui-text-black border-b-2 border-ui-blue-end font-bold' : 'text-gray-500']"
              @click="activeTab = 'wechat'"
            >
              手机账号快速登录
            </button>
            </block>
          </view>
          
          <!-- 用户协议 -->
          <view class="mb-4">
            <view class="flex items-center">
              <checkbox-group @change="agreementChecked = !!$event.detail.value.length">
                  <checkbox :checked="agreementChecked" />
              </checkbox-group>
              <view class="text-sm ml-2">
                我已阅读并同意
                <navigator url="/pages/agreement/agreement" class="text-ui-blue-start inline">《用户协议》</navigator>
                和
                <navigator url="/pages/privacy/privacy" class="text-ui-blue-start inline">《隐私政策》</navigator>
              </view>
            </view>
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
                class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-ui-blue-end"
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
                  class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-ui-blue-end"
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
              class="w-full flex items-center justify-center bg-ui-vibrant-gradient text-white font-bold py-3 rounded-lg shadow-md"
              :disabled="loading"
            >
              <text v-if="loading">登录中...</text>
              <text v-else>登 录</text>
            </button>
            
            <view class="mt-4 text-center flex justify-between">
              <button @click="goBack" class="text-ui-blue-start">返回首页</button>
              <button @click="goToRegister" class="text-ui-blue-start">注册新账号</button>
            </view>
          </view>
          
          <!-- 手机账号一键登录 -->
          <block #ifdef MP-WEIXIN>
          <view v-if="activeTab === 'wechat'">
            
            <button 
              open-type="getPhoneNumber"
              @getphonenumber="handlePhoneLogin"
              class="w-full flex items-center justify-center bg-ui-vibrant-gradient text-white font-bold py-3 rounded-lg shadow-md"
              :disabled="loading"
            >
              <text v-if="loading">登录中...</text>
              <text v-else>使用手机账号快速登录</text>
            </button>
            
            <view class="mt-4 text-center">
              <button @click="goBack" class="text-ui-blue-start">返回首页</button>
            </view>
          </view>
          </block>
        </view>
      </view>
    </view>
    
    <!-- <view class="mt-8 text-center text-xs text-gray-500">
      <view>登录即代表您已阅读并同意</view>
      <view>《用户协议》和《隐私政策》</view>
    </view> -->
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { useUserStore } from '@/stores/user';
import { onLoad } from '@dcloudio/uni-app';

const userStore = useUserStore();
const loading = ref(false);
const activeTab = ref('password');
const showPassword = ref(false);
const agreementChecked = ref(false);
let redirectUrl = '/pages/home/home';

const loginForm = ref({
  contact: '',
  password: ''
});

onLoad((options) => {
  if (options && options.redirect) {
    redirectUrl = decodeURIComponent(options.redirect);
  }
});

const handlePhoneLogin = async (e) => {
  if (!agreementChecked.value) {
    uni.showToast({ title: '请先阅读并同意用户协议和隐私政策', icon: 'none' });
    return;
  }
  if (!e.detail.code) {
    uni.showToast({ title: '您取消了授权', icon: 'none' });
    return;
  }
  
  loading.value = true;
  try {
    const phoneCode = e.detail.code;
    
    const loginRes = await uni.login();
    const loginCode = loginRes.code;
    
    // 调用 store 中的微信登录方法，昵称和头像传 null
    await userStore.wechatLogin(loginCode, null, null, phoneCode);
    
    uni.reLaunch({ url: redirectUrl });
    
  } catch (err) {
    console.error('微信登录流程失败:', err);
    uni.showToast({ title: '登录失败，请稍后重试', icon: 'none' });
  } finally {
    loading.value = false;
  }
};

// 处理账号密码登录
const handlePasswordLogin = async () => {
  if (!agreementChecked.value) {
    uni.showToast({ title: '请先阅读并同意用户协议和隐私政策', icon: 'none' });
    return;
  }
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
    await userStore.passwordLogin(loginForm.value.contact, loginForm.value.password);
    uni.reLaunch({ url: redirectUrl });
  } catch (error) {
    console.error('账号密码登录失败:', error);
    uni.showToast({ title: error.message || '登录失败', icon: 'none' });
  } finally {
    loading.value = false;
  }
};

const goToRegister = () => {
  uni.navigateTo({ url: '/pages/register/register' });
};

const goBack = () => {
  uni.reLaunch({ url: '/pages/home/home' });
};
</script>

<style scoped>
.bg-ui-vibrant-gradient {
  background-image: linear-gradient(to right, #007aff, #00c6ff);
}
.bg-ui-bg-white {
  background-color: #f8f8f8;
}
.text-ui-text-black {
  color: #1a1a1a;
}
.border-ui-blue-end {
  border-color: #00c6ff;
}
.text-ui-blue-start {
  color: #007aff;
}
</style>