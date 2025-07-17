<template>
  <view class="p-4 bg-ui-bg-white">
    <view class="flex items-center mb-4">
      <view @click="goBack" class="mr-2 p-2">
        <text class="text-2xl font-bold">‹</text>
      </view>
      <view class="text-xl font-medium text-ui-text-black">个人资料</view>
    </view>
    
    <form @submit="saveProfile" class="mt-4">
      <!-- 头像 -->
      <view class="flex flex-col items-center mb-6">
        <button 
          class="w-20 h-20 rounded-full overflow-hidden bg-neutral-light mb-2 p-0 border-none"
          @click="chooseAndUpdateAvatar"
        >
          <image 
            :src="formData.avatarUrl || '/static/images/avatar-placeholder.png'" 
            class="w-full h-full object-cover" 
            alt="头像"
          />
        </button>
        <view class="text-sm text-gray-500">点击头像可更换</view>
      </view>
      
      <!-- 姓名 -->
      <view class="mb-4">
        <view for="name" class="block text-sm font-medium text-ui-text-black mb-1">姓名 <text class="text-red-500">*</text></view>
        <input 
          id="name"
          v-model="formData.contact"
          type="text"
          class="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-ui-blue-end focus:border-ui-blue-end sm:text-sm"
          placeholder="请输入您的姓名"
          required
        />
        <view v-if="errors.name" class="text-red-500 text-xs mt-1">{{ errors.name }}</view>
      </view>
      
      <!-- 公司名称 -->
      <view class="mb-4">
        <view for="company" class="block text-sm font-medium text-ui-text-black mb-1">公司名称 <text class="text-red-500">*</text></view>
        <input 
          id="company"
          v-model="formData.name"
          type="text"
          class="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-ui-blue-end focus:border-ui-blue-end sm:text-sm"
          placeholder="请输入您的公司名称"
          required
        />
        <view v-if="errors.company" class="text-red-500 text-xs mt-1">{{ errors.company }}</view>
      </view>
      
      <!-- 联系电话 -->
      <view class="mb-4">
        <view for="phone" class="block text-sm font-medium text-ui-text-black mb-1">联系电话 <text class="text-red-500">*</text></view>
        <input 
          id="phone"
          v-model="formData.phone"
          type="tel"
          class="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-ui-blue-end focus:border-ui-blue-end sm:text-sm"
          placeholder="请输入您的联系电话"
          required
        />
        <view v-if="errors.phone" class="text-red-500 text-xs mt-1">{{ errors.phone }}</view>
      </view>
      
      <!-- 联系地址 -->
      <view class="mb-4">
        <view for="address" class="block text-sm font-medium text-ui-text-black mb-1">联系地址 <text class="text-red-500">*</text></view>
        <input 
          id="address"
          v-model="formData.address"
          type="text"
          class="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-ui-blue-end focus:border-ui-blue-end sm:text-sm"
          placeholder="请输入您的联系地址"
          required
        />
        <view v-if="errors.address" class="text-red-500 text-xs mt-1">{{ errors.address }}</view>
      </view>
      
      <!-- 部门 -->
      <view class="mb-4">
        <view for="department" class="block text-sm font-medium text-ui-text-black mb-1">部门</view>
        <input 
          id="department"
          v-model="formData.department"
          type="text"
          class="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-ui-blue-end focus:border-ui-blue-end sm:text-sm"
          placeholder="请输入您的部门"
        />
      </view>
      
      <!-- 职位 -->
      <view class="mb-4">
        <view for="position" class="block text-sm font-medium text-ui-text-black mb-1">职位</view>
        <input 
          id="position"
          v-model="formData.position"
          type="text"
          class="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-ui-blue-end focus:border-ui-blue-end sm:text-sm"
          placeholder="请输入您的职位"
        />
      </view>
      
      <!-- 电子邮箱 -->
      <view class="mb-6">
        <view for="email" class="block text-sm font-medium text-ui-text-black mb-1">电子邮箱</view>
        <input 
          id="email"
          v-model="formData.email"
          type="email"
          class="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-ui-blue-end focus:border-ui-blue-end sm:text-sm"
          placeholder="请输入您的电子邮箱"
        />
        <view v-if="errors.email" class="text-red-500 text-xs mt-1">{{ errors.email }}</view>
      </view>
      
      <button 
        form-type="submit" 
        class="w-full bg-ui-vibrant-gradient text-white font-bold py-3 px-4 rounded-lg shadow-md hover:opacity-90 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-ui-blue-end disabled:opacity-50"
        :disabled="loading"
        :loading="loading"
      >
        {{ loading ? '保存中...' : '保存' }}
      </button>
    </form>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useUserStore } from '@/stores/user';
import request from '@/utils/request';

const userStore = useUserStore();
const loading = ref(false);

// 表单数据
const formData = reactive({
  contact: '',
  name: '',
  phone: '',
  address: '',
  email: '',
  avatarUrl: '',
  department: '',
  position: ''
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
    const userInfo = userStore.userInfo;
    Object.assign(formData, {
      contact: userInfo.contact || '',
      name: userInfo.name || '',
      phone: userInfo.phone || '',
      address: userInfo.address || '',
      email: userInfo.email || '',
      department: userInfo.department || '',
      position: userInfo.position || '',
      avatarUrl: userInfo.avatarUrl || userInfo.wechat_avatar_url || ''
    });
  } else {
    try {
      await userStore.getUserInfo();
      const userInfo = userStore.userInfo;
      Object.assign(formData, {
        contact: userInfo.contact || '',
        name: userInfo.name || '',
        phone: userInfo.phone || '',
        address: userInfo.address || '',
        email: userInfo.email || '',
        department: userInfo.department || '',
        position: userInfo.position || '',
        avatarUrl: userInfo.avatarUrl || userInfo.wechat_avatar_url || ''
      });
    } catch (error) {
      console.error('获取用户信息失败:', error);
    }
  }
});

// 统一的头像选择和上传方法
const chooseAndUpdateAvatar = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      const tempFilePath = res.tempFilePaths[0];
      
      // 显示本地预览
      formData.avatarUrl = tempFilePath;

      uni.showLoading({
        title: '上传中...'
      });

      // 上传头像到服务器
      uni.uploadFile({
        url: request.defaults.baseURL + '/upload/avatar', // 使用axios实例的基本URL
        filePath: tempFilePath,
        name: 'file',
        header: {
          'Authorization': `Bearer ${uni.getStorageSync('token')}`
        },
        success(uploadRes) {
          const result = JSON.parse(uploadRes.data);
          if (result.code === 200) {
            formData.avatarUrl = result.data.url; // 更新为服务器返回的最终URL
            uni.showToast({ title: '头像上传成功', icon: 'success' });
          } else {
            uni.showToast({ title: result.message || '上传失败', icon: 'none' });
          }
        },
        fail(err) {
          console.error('上传头像失败:', err);
          uni.showToast({ title: '上传头像失败', icon: 'none' });
        },
        complete() {
          uni.hideLoading();
        }
      });
    }
  });
};

// 验证表单
const validateForm = () => {
  let isValid = true;
  
  // 重置所有错误
  Object.keys(errors).forEach(key => {
    errors[key] = '';
  });
  
  // 验证姓名
  if (!formData.contact.trim()) {
    errors.name = '姓名不能为空';
    isValid = false;
  }
  
  // 验证公司名称
  if (!formData.name.trim()) {
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
      contact: formData.contact,
      name: formData.name,
      phone: formData.phone,
      address: formData.address,
      email: formData.email,
      avatarUrl: formData.avatarUrl,
      department: formData.department,
      position: formData.position,
    };
    
    console.log('提交更新用户资料:', updateData);
    
    // 调用API更新用户资料
    await userStore.updateUserInfo(updateData);
    
    uni.showToast({
      title: '个人资料更新成功！',
      icon: 'success'
    });
    
    // 返回上一页
    setTimeout(() => {
      uni.navigateBack();
    }, 1500);

  } catch (error) {
    console.error('更新用户资料失败:', error);
    uni.showToast({
      title: error.message || '更新资料失败',
      icon: 'none'
    });
  } finally {
    loading.value = false;
  }
};

// 返回上一页
const goBack = () => {
  uni.navigateBack();
};
</script> 