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
          open-type="chooseAvatar" 
          @chooseavatar="onChooseAvatar"
          class="w-20 h-20 rounded-full overflow-hidden bg-neutral-light mb-2 p-0 border-none"
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
        <view class="mt-1 flex justify-between items-center w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm">
          <text>{{ formData.contact }}</text>
          <button 
            type="primary" 
            size="mini" 
            @click.prevent="openNicknameModal"
            class="bg-blue-500 text-white"
          >
            修改
          </button>
        </view>
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
    
    <!-- 昵称修改模态框 -->
    <view v-if="showNicknameModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <view class="bg-white rounded-lg p-6 w-11/12 max-w-sm">
        <h3 class="text-lg font-bold text-center mb-4">修改昵称</h3>
        <input 
          type="nickname" 
          v-model="tempNickname"
          class="w-full px-3 py-2 border border-gray-300 rounded-md mb-4"
          placeholder="请输入新昵称"
        />
        <view class="flex justify-end">
          <button @click="closeNicknameModal" class="mr-2">取消</button>
          <button @click="confirmNicknameChange" type="primary" class="bg-blue-500 text-white">确定</button>
        </view>
      </view>
    </view>

  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useUserStore } from '@/stores/user';
import request from '@/utils/request';

const userStore = useUserStore();
const loading = ref(false);

const showNicknameModal = ref(false);
const tempNickname = ref('');

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

// 将用户信息同步到表单数据
const syncFormData = (userInfo) => {
  if (userInfo) {
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
  }
};

// 加载用户信息
onMounted(async () => {
  if (userStore.hasUserInfo) {
    syncFormData(userStore.userInfo);
  } else {
    try {
      await userStore.getUserInfo();
      syncFormData(userStore.userInfo);
    } catch (error) {
      console.error('获取用户信息失败:', error);
    }
  }
});

const onChooseAvatar = (e) => {
  const { avatarUrl } = e.detail;
  uploadAvatar(avatarUrl);
};

// 统一的头像选择和上传方法
const uploadAvatar = async (tempFilePath) => {
  uni.showLoading({ title: '上传中...' });
  try {
    const result = await request.upload('/upload/avatar', tempFilePath);
    // 使用后端返回的最新用户信息更新Store
    userStore.setUserInfo(result.data);
    // 将更新后的信息同步到本地表单
    syncFormData(result.data);
    uni.showToast({ title: '头像上传成功', icon: 'success' });
  } catch (error) {
    console.error('上传头像失败:', error);
    uni.showToast({ title: '上传头像失败', icon: 'none' });
  } finally {
    uni.hideLoading();
  }
};

const openNicknameModal = () => {
  tempNickname.value = formData.contact;
  showNicknameModal.value = true;
};

const closeNicknameModal = () => {
  showNicknameModal.value = false;
};

const confirmNicknameChange = () => {
  formData.contact = tempNickname.value;
  closeNicknameModal();
};

const validateForm = () => {
  let isValid = true;
  errors.name = '';
  errors.company = '';
  errors.phone = '';
  errors.address = '';
  
  if (!formData.contact) {
    errors.name = '姓名不能为空';
    isValid = false;
  }
  if (!formData.name) {
    errors.company = '公司名称不能为空';
    isValid = false;
  }
  if (!formData.phone) {
    errors.phone = '联系电话不能为空';
    isValid = false;
  }
  if (!formData.address) {
    errors.address = '联系地址不能为空';
    isValid = false;
  }
  return isValid;
};

// 保存个人资料
const saveProfile = async () => {
  if (!validateForm()) {
    uni.showToast({ title: '请填写所有必填项', icon: 'none' });
    return;
  }
  
  loading.value = true;
  try {
    const response = await request.put('/user/profile', formData);
    if (response.code === 200) {
      userStore.setUserInfo(response.data);
      uni.showToast({ title: '保存成功', icon: 'success' });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    } else {
      uni.showToast({ title: response.message || '保存失败', icon: 'none' });
    }
  } catch (error) {
    console.error('保存个人资料失败:', error);
    uni.showToast({ title: '保存失败，请稍后再试', icon: 'none' });
  } finally {
    loading.value = false;
  }
};

const goBack = () => {
  uni.navigateBack();
};
</script> 