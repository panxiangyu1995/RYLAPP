<template>
  <div class="p-4">
    <div class="flex items-center mb-4">
      <button @click="goBack" class="mr-2">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m15 18-6-6 6-6"/></svg>
      </button>
      <h1 class="text-xl font-medium">个人资料</h1>
    </div>
    
    <form @submit.prevent="saveProfile" class="mt-4">
      <!-- 头像 -->
      <div class="flex flex-col items-center mb-6">
        <div class="w-20 h-20 rounded-full overflow-hidden bg-neutral-light mb-2">
          <img 
            :src="formData.avatarUrl || '/avatar-placeholder.png'" 
            class="w-full h-full object-cover" 
            alt="头像"
          />
        </div>
        <p class="text-sm text-gray-500">头像已同步微信账号</p>
      </div>
      
      <!-- 姓名 -->
      <div class="mb-4">
        <label for="name" class="form-label">姓名 <span class="text-red-500">*</span></label>
        <input 
          id="name"
          v-model="formData.name"
          type="text"
          class="form-input"
          placeholder="请输入您的姓名"
          required
        />
        <p v-if="errors.name" class="text-red-500 text-xs mt-1">{{ errors.name }}</p>
      </div>
      
      <!-- 公司名称 -->
      <div class="mb-4">
        <label for="company" class="form-label">公司名称 <span class="text-red-500">*</span></label>
        <input 
          id="company"
          v-model="formData.company"
          type="text"
          class="form-input"
          placeholder="请输入您的公司名称"
          required
        />
        <p v-if="errors.company" class="text-red-500 text-xs mt-1">{{ errors.company }}</p>
      </div>
      
      <!-- 联系电话 -->
      <div class="mb-4">
        <label for="phone" class="form-label">联系电话 <span class="text-red-500">*</span></label>
        <input 
          id="phone"
          v-model="formData.phone"
          type="tel"
          class="form-input"
          placeholder="请输入您的联系电话"
          required
        />
        <p v-if="errors.phone" class="text-red-500 text-xs mt-1">{{ errors.phone }}</p>
      </div>
      
      <!-- 联系地址 -->
      <div class="mb-4">
        <label for="address" class="form-label">联系地址 <span class="text-red-500">*</span></label>
        <input 
          id="address"
          v-model="formData.address"
          type="text"
          class="form-input"
          placeholder="请输入您的联系地址"
          required
        />
        <p v-if="errors.address" class="text-red-500 text-xs mt-1">{{ errors.address }}</p>
      </div>
      
      <!-- 电子邮箱 -->
      <div class="mb-6">
        <label for="email" class="form-label">电子邮箱</label>
        <input 
          id="email"
          v-model="formData.email"
          type="email"
          class="form-input"
          placeholder="请输入您的电子邮箱"
        />
        <p v-if="errors.email" class="text-red-500 text-xs mt-1">{{ errors.email }}</p>
      </div>
      
      <button 
        type="submit" 
        class="btn btn-primary w-full py-3"
        :disabled="loading"
      >
        {{ loading ? '保存中...' : '保存' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const userStore = useUserStore();
const loading = ref(false);

// 表单数据
const formData = reactive({
  name: '',
  company: '',
  phone: '',
  address: '',
  email: '',
  avatarUrl: ''
});

// 表单错误信息
const errors = reactive({
  name: '',
  company: '',
  phone: '',
  address: '',
  email: ''
});

// 加载用户信息
onMounted(async () => {
  if (userStore.hasUserInfo) {
    // 从store中获取用户信息
    const userInfo = userStore.userInfo;
    formData.name = userInfo.contact || '';
    formData.company = userInfo.name || '';
    formData.phone = userInfo.phone || '';
    formData.address = userInfo.address || '';
    formData.email = userInfo.email || '';
    formData.avatarUrl = userInfo.wechat_avatar_url || '';
  } else {
    // 从服务器获取用户信息
    try {
      await userStore.getUserInfo();
      const userInfo = userStore.userInfo;
      formData.name = userInfo.contact || '';
      formData.company = userInfo.name || '';
      formData.phone = userInfo.phone || '';
      formData.address = userInfo.address || '';
      formData.email = userInfo.email || '';
      formData.avatarUrl = userInfo.wechat_avatar_url || '';
    } catch (error) {
      console.error('获取用户信息失败:', error);
    }
  }
});

// 验证表单
const validateForm = () => {
  let isValid = true;
  
  // 重置所有错误
  Object.keys(errors).forEach(key => {
    errors[key] = '';
  });
  
  // 验证姓名
  if (!formData.name.trim()) {
    errors.name = '姓名不能为空';
    isValid = false;
  }
  
  // 验证公司名称
  if (!formData.company.trim()) {
    errors.company = '公司名称不能为空';
    isValid = false;
  }
  
  // 验证联系电话
  if (!formData.phone) {
    errors.phone = '联系电话不能为空';
    isValid = false;
  } else if (!/^1[3-9]\d{9}$/.test(formData.phone)) {
    errors.phone = '请输入正确的手机号码';
    isValid = false;
  }
  
  // 验证联系地址
  if (!formData.address.trim()) {
    errors.address = '联系地址不能为空';
    isValid = false;
  }
  
  // 验证电子邮箱（可选）
  if (formData.email && !/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(formData.email)) {
    errors.email = '请输入正确的电子邮箱';
    isValid = false;
  }
  
  return isValid;
};

// 保存个人资料
const saveProfile = async () => {
  if (!validateForm()) {
    return;
  }
  
  loading.value = true;
  
  try {
    // 准备更新的数据
    const updateData = {
      contact: formData.name,
      name: formData.company,
      phone: formData.phone,
      address: formData.address,
      email: formData.email
    };
    
    // 调用API更新用户资料
    await userStore.updateUserInfo(updateData);
    
    // 返回上一页
    router.back();
  } catch (error) {
    console.error('更新用户资料失败:', error);
  } finally {
    loading.value = false;
  }
};

// 返回上一页
const goBack = () => {
  router.back();
};
</script> 