<template>
  <view class="p-4 pb-20 bg-ui-bg-white">
    <!-- 成功页面 -->
    <view v-if="isSuccess" class="bg-white rounded-lg shadow-md overflow-hidden">
      <view class="p-6">
        <view class="flex justify-center mb-4">
          <view class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center">
            <CheckIcon :size="40" color="green" />
          </view>
        </view>
        
        <view class="text-2xl font-bold text-center text-gray-800 mb-4 block">提交成功</view>
        <view class="text-center text-gray-600 mb-6 block">您的订单已成功创建</view>
        
        <view class="bg-gray-50 p-4 rounded-md mb-6">
          <view class="mb-2">
            <text class="font-medium text-gray-700">订单编号：</text>
            <text>{{ submittedTask.taskId }}</text>
          </view>
          <view class="mb-2">
            <text class="font-medium text-gray-700">订单标题：</text>
            <text>{{ submittedTask.title }}</text>
          </view>
          <view>
            <text class="font-medium text-gray-700">订单类型：</text>
            <text>{{ taskTypes.find(t => t.id === submittedTask.taskType)?.name }}</text>
          </view>
        </view>
        
        <view class="flex space-x-4">
          <button 
            @click="continueSubmit"
            class="flex-1 py-2 px-4 bg-white border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
          >
            继续提交
          </button>
          <button 
            @click="finishSubmit"
            class="flex-1 py-2 px-4 bg-ui-vibrant-gradient border border-transparent rounded-md text-white hover:opacity-90"
          >
            完成提交
          </button>
        </view>
      </view>
    </view>

    <!-- 订单表单 -->
    <view v-else>
      <!-- 顶部导航
      <view class="flex items-center mb-4">
        <button @click="goBack" class="mr-2">
          <ChevronLeftIcon :size="24" color="#0E4472" />
        </button>
        <view class="text-xl font-medium">提交订单</view>
      </view> -->
    
      <!-- 订单类型选择 -->
      <view class="mb-6 bg-white rounded-lg p-4">
        <view class="text-lg font-medium mb-3 text-ui-text-black">选择订单类型</view>
        <view class="grid grid-cols-3 gap-3">
          <view
            v-for="type in taskTypes"
            :key="type.id"
            @click="selectTaskType(type.id)"
            class="p-3 rounded-lg text-center text-sm cursor-pointer transition-colors"
            :class="[
              selectedType === type.id
                ? 'bg-ui-vibrant-gradient text-white font-semibold'
                : 'bg-neutral-light text-ui-text-black'
            ]"
          >
            {{ type.name }}
          </view>
        </view>
      </view>
      
      <!-- 表单区域 -->
      <form @submit="submitTask">
        <!-- 登录提示 -->
        <view v-if="!userStore.isLoggedIn" class="bg-yellow-50 border border-yellow-300 rounded-lg p-4 mb-6">
          <view class="text-yellow-700 mb-2">您需要登录后才能提交订单</view>
          <button 
            type="button"
            @click="goToLogin"
            class="px-4 py-2 bg-yellow-500 hover:bg-yellow-600 text-white rounded-md"
          >
            立即登录
          </button>
        </view>
        
        <!-- 客户信息 -->
        <view class="bg-white rounded-lg p-4 mb-6">
          <view class="text-lg font-medium mb-3 text-ui-blue-start">客户信息</view>
          
          <view class="mb-4">
            <view for="customerName" class="block text-sm font-medium text-ui-blue-start mb-1">客户姓名 <text class="text-red-500">*</text></view>
            <input 
              id="customerName" 
              v-model="formData.customer.contact"
              type="text" 
              class="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-ui-blue-end focus:border-ui-blue-end focus:shadow-ui-glow sm:text-sm" 
              placeholder="请输入姓名"
              required
            />
          </view>
          
          <view class="mb-4">
            <view for="customerCompany" class="block text-sm font-medium text-ui-blue-start mb-1">公司名称 <text class="text-red-500">*</text></view>
            <input 
              id="customerCompany" 
              v-model="formData.customer.name"
              type="text" 
              class="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-ui-blue-end focus:border-ui-blue-end focus:shadow-ui-glow sm:text-sm" 
              placeholder="请输入公司名称"
              required
            />
          </view>
          
          <view class="mb-4">
            <view for="customerPhone" class="block text-sm font-medium text-ui-blue-start mb-1">联系电话 <text class="text-red-500">*</text></view>
            <input 
              id="customerPhone" 
              v-model="formData.customer.phone"
              type="tel" 
              class="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-ui-blue-end focus:border-ui-blue-end focus:shadow-ui-glow sm:text-sm" 
              placeholder="请输入联系电话"
              required
            />
          </view>
          
          <view class="mb-4">
            <view for="customerAddress" class="block text-sm font-medium text-ui-blue-start mb-1">联系地址 <text class="text-red-500">*</text></view>
            <input 
              id="customerAddress" 
              v-model="formData.customer.address"
              type="text" 
              class="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-ui-blue-end focus:border-ui-blue-end focus:shadow-ui-glow sm:text-sm" 
              placeholder="请输入联系地址"
              required
            />
          </view>
          
          <view>
            <view for="customerEmail" class="block text-sm font-medium text-ui-blue-start mb-1">电子邮箱</view>
            <input 
              id="customerEmail" 
              v-model="formData.customer.email"
              type="email" 
              class="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-ui-blue-end focus:border-ui-blue-end focus:shadow-ui-glow sm:text-sm" 
              placeholder="请输入电子邮箱"
            />
          </view>
        </view>
        
        <!-- 仪器信息 -->
        <view class="bg-white rounded-lg p-4 mb-6">
          <view class="text-lg font-medium mb-3 text-ui-blue-start">仪器信息</view>
          
          <view v-if="['repair', 'maintenance', 'recycle', 'leasing', 'training', 'verification', 'installation'].includes(selectedType)">
            <device-basic-info v-model="formData.device" />
          </view>

          <!-- 仪器选型 -->
          <view v-else-if="selectedType === 'selection'">
            <view class="mb-4">
              <view for="devicePurpose" class="block text-sm font-medium text-ui-blue-start mb-1">仪器用途或名称 <text class="text-red-500">*</text></view>
              <input id="devicePurpose" v-model="formData.device.purpose" type="text" class="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-ui-blue-end focus:border-ui-blue-end focus:shadow-ui-glow sm:text-sm" placeholder="请输入仪器用途或名称" required />
            </view>
          </view>

          <!-- 通用数量字段 -->
           <view v-if="selectedType !== 'installation'" class="mb-4">
              <view for="deviceQuantity" class="block text-sm font-medium text-ui-blue-start mb-1">数量 <text class="text-red-500">*</text></view>
              <input id="deviceQuantity" v-model.number="formData.device.quantity" type="number" min="1" class="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-ui-blue-end focus:border-ui-blue-end focus:shadow-ui-glow sm:text-sm" required />
            </view>

          <!-- 培训预约时间 -->
          <view v-if="selectedType === 'training'" class="mb-4">
            <view for="appointmentTime" class="block text-sm font-medium text-ui-blue-start mb-1">预约时间 <text class="text-red-500">*</text></view>
            <picker mode="date" :value="formData.device.appointmentTime" @change="(e) => formData.device.appointmentTime = e.detail.value">
              <view class="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400">
                {{ formData.device.appointmentTime || '请选择预约时间' }}
              </view>
            </picker>
          </view>
          
          <!-- 仪器验证类型 -->
          <view v-if="selectedType === 'verification'" class="mb-4">
            <view class="block text-sm font-medium text-ui-blue-start mb-1">验证类别 <text class="text-red-500">*</text></view>
            <radio-group @change="(e) => formData.device.verificationType = e.detail.value" class="mt-2">
              <label class="inline-flex items-center mr-4">
                <radio value="IQ" :checked="formData.device.verificationType === 'IQ'" /><text class="ml-2">IQ安装验证</text>
              </label>
              <label class="inline-flex items-center mr-4">
                <radio value="OQ" :checked="formData.device.verificationType === 'OQ'" /><text class="ml-2">OQ运行验证</text>
              </label>
              <label class="inline-flex items-center">
                <radio value="PQ" :checked="formData.device.verificationType === 'PQ'" /><text class="ml-2">PQ性能验证</text>
              </label>
            </radio-group>
          </view>
        </view>
        
        <!-- 详细描述 -->
        <view class="bg-white rounded-lg p-4 mb-6">
          <view class="text-lg font-medium mb-3 text-ui-blue-start">订单描述</view>
          <view class="mb-4">
            <view for="description" class="block text-sm font-medium text-ui-blue-start mb-1">详细描述 <text class="text-red-500">*</text></view>
            <textarea 
              id="description" 
              v-model="formData.description"
              :rows="4" 
              class="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-ui-blue-end focus:border-ui-blue-end focus:shadow-ui-glow sm:text-sm" 
              placeholder="请详细描述您的需求"
              required
            ></textarea>
          </view>
        </view>
        
        <!-- 图片上传 -->
        <view class="bg-white rounded-lg p-4 mb-6">
          <view class="text-lg font-medium mb-3 text-ui-blue-start">图片上传</view>
          <view class="text-sm text-gray-500 mb-2">可上传最多8张图片，自动压缩</view>
          <upload-image v-model="formData.images" @upload-error="handleUploadError" />
        </view>
        
        <!-- 附件上传 -->
        <view class="bg-white rounded-lg p-4 mb-6">
          <view class="text-lg font-medium mb-3 text-ui-blue-start">附件上传</view>
          <view class="text-sm text-gray-500 mb-2">可上传最多5个文件，单个不超过10MB</view>
          <upload-file v-model="formData.files" @upload-error="handleUploadError" />
        </view>
        
        <!-- 提交按钮 -->
        <button 
          form-type="submit"
          class="w-full bg-ui-vibrant-gradient text-white font-bold py-3 px-4 rounded-lg shadow-md hover:opacity-90 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-ui-blue-end disabled:opacity-50"
          :disabled="isSubmitting || !userStore.isLoggedIn"
          :loading="isSubmitting"
        >
          {{ isSubmitting ? '提交中...' : '确认提交' }}
        </button>
      </form>
    </view>
    
    <!-- 全局提示消息 -->
    <global-toast />
  </view>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { useTaskStore } from '@/stores/task';
import { useUserStore } from '@/stores/user';
import { onLoad, onShow } from '@dcloudio/uni-app';

import DeviceBasicInfo from '@/components/DeviceBasicInfo.vue';
import UploadImage from '@/components/UploadImage.vue';
import UploadFile from '@/components/UploadFile.vue';
import GlobalToast from '@/components/GlobalToast.vue';
import CheckIcon from '@/components/icons/CheckIcon.vue';
import ChevronLeftIcon from '@/components/icons/ChevronLeftIcon.vue';

const taskStore = useTaskStore();
const userStore = useUserStore();

// 响应式状态
const selectedType = ref('repair');
const isSubmitting = ref(false);
const isSuccess = ref(false);
const submittedTask = ref(null);
const taskTypes = computed(() => taskStore.taskTypes);

// 表单数据
const getInitialFormData = () => ({
  customer: {
    name: '',
    contact: '',
    phone: '',
    address: '',
    email: '',
  },
  device: {
    name: '',
    type: '',
    brand: '',
    model: '',
    quantity: 1,
    accessories: '',
    appointmentTime: '',
    verificationType: '',
    purpose: '',
    requirementDescription: ''
  },
  description: '',
  images: [],
  files: [],
});

const formData = reactive(getInitialFormData());

// 生命周期钩子
onLoad((options) => {
  if (options.type) {
    selectTaskType(options.type);
  }
});

onShow(() => {
  if (userStore.isLoggedIn) {
    const userInfo = userStore.userInfo;
    formData.customer = {
      name: userInfo.name || '',
      contact: userInfo.contact || '',
      phone: userInfo.phone || '',
      address: userInfo.address || '',
      email: userInfo.email || '',
    };
  }
});


// 方法
const selectTaskType = (type) => {
  selectedType.value = type;
  
  const newDevice = { ...getInitialFormData().device };
  
  if (type === 'selection') {
    newDevice.purpose = '';
  } else if (type === 'installation') {
    newDevice.quantity = 1;
  }
  
  formData.device = newDevice;
};

// 前往登录页
const goToLogin = () => {
  uni.navigateTo({
    url: `/pages/login/login?redirect=/pages/taskcreate/taskcreate?type=${selectedType.value}`
  });
};

const goBack = () => {
  uni.navigateBack();
};

const finishSubmit = () => {
  uni.reLaunch({ url: '/pages/home/home' });
};

const continueSubmit = () => {
  Object.assign(formData, getInitialFormData());
  isSuccess.value = false;
  submittedTask.value = null;
  // 重新加载用户信息
  if (userStore.isLoggedIn) {
      const userInfo = userStore.userInfo;
        formData.customer = {
        name: userInfo.name || '',
        contact: userInfo.contact || '',
        phone: userInfo.phone || '',
        address: userInfo.address || '',
        email: userInfo.email || '',
        };
    }
};

const handleUploadError = (errorMessage) => {
    uni.showToast({
        title: `上传错误: ${errorMessage}`,
        icon: 'none'
    });
};

const generateTaskId = (type) => {
    const typePrefix = {
        repair: 'R',
        maintenance: 'M',
        recycle: 'H',
        leasing: 'L',
        training: 'T',
        verification: 'V',
        selection: 'S',
        installation: 'I',
    };
    const prefix = typePrefix[type] || 'U';
    const date = new Date();
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const random = String(Math.floor(Math.random() * 10000)).padStart(4, '0');
    return `${prefix}${year}${month}${day}-${random}`;
};

const checkDuplicateTask = (taskData) => {
  const tenMinutesAgo = Date.now() - 10 * 60 * 1000;
  const recentTasks = uni.getStorageSync('submittedTasks') || [];
  
  const duplicate = recentTasks.find(savedTask => 
    savedTask.timestamp > tenMinutesAgo &&
    savedTask.taskType === taskData.taskType &&
    savedTask.device.name === taskData.device.name &&
    savedTask.description === taskData.description
  );
  
  return duplicate;
};

const saveTaskToLocal = (taskData, result) => {
    const recentTasks = uni.getStorageSync('submittedTasks') || [];
    const newTask = {
        ...taskData,
        id: result.id, 
        taskId: result.taskId,
        timestamp: Date.now()
    };
    const updatedTasks = [...recentTasks, newTask].slice(-10); // 只保留最近10条
    uni.setStorageSync('submittedTasks', updatedTasks);
};

// 提交任务
const submitTask = async () => {
  if (isSubmitting.value) return;

  isSubmitting.value = true;
  uni.showLoading({ title: '正在提交...' });

  try {
    const taskData = buildTaskData();
    
    const duplicate = checkDuplicateTask(taskData);
    if (duplicate) {
        uni.hideLoading();
        const res = await uni.showModal({
            title: '重复提交确认',
            content: `检测到您在10分钟内提交过相似订单 (编号: ${duplicate.taskId})，是否仍要继续提交？`
        });

        if (!res.confirm) {
          isSubmitting.value = false;
            const resView = await uni.showModal({
                title: '操作确认',
                content: '是否前往查看已提交的订单？'
            });
            if(resView.confirm) {
                uni.navigateTo({ url: `/pages/TaskDetail/index?id=${duplicate.id}`});
            }
          return;
        }
        uni.showLoading({ title: '正在提交...' });
    }

    // 提交最终任务数据
    const result = await taskStore.createTask(taskData);
        
    saveTaskToLocal(taskData, result);

    uni.hideLoading();
        isSuccess.value = true;
        submittedTask.value = {
      taskId: result.taskId,
      title: taskData.title,
      taskType: taskData.taskType
    };

    // 在任务创建成功后请求订阅消息
    try {
      await taskStore.requestSubscription();
    } catch (subError) {
      console.error('订阅消息请求失败:', subError);
      // 即使用户拒绝或请求失败，也不应中断主流程
    }

  } catch (error) {
    uni.hideLoading();
    console.error('提交订单失败:', error);
    uni.showToast({
      title: error.message || '提交失败，请稍后重试',
      icon: 'none',
      duration: 3000
    });
  } finally {
    isSubmitting.value = false;
  }
};

const buildTaskData = () => {
    const taskId = generateTaskId(selectedType.value);
    const companyName = formData.customer.name || formData.customer.contact || '未知客户';
    const taskTypeName = taskTypes.value.find(t => t.id === selectedType.value)?.name || '未知任务';
    let deviceName = selectedType.value === 'selection' ? formData.device.purpose : formData.device.name;
    const taskTitle = `${companyName}-${deviceName || '未知设备'}-${taskTypeName}`;

    return {
      taskId: taskId,
      title: taskTitle,
      taskType: selectedType.value,
      description: formData.description,
      priority: 0,
      device: { ...formData.device },
      customer: { ...formData.customer },
      images: formData.images,
      files: formData.files
    };
};

// 查看复制的任务详情
const viewDuplicateDetail = (duplicate) => {
  uni.navigateTo({ url: `/pages/taskdetail/taskdetail?id=${duplicate.id}`});
};
</script> 