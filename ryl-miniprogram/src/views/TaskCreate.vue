<template>
  <div class="p-4 pb-20">
    <!-- 顶部导航 -->
    <div class="flex items-center mb-4">
      <button @click="goBack" class="mr-2">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m15 18-6-6 6-6"/></svg>
      </button>
      <h1 class="text-xl font-medium">提交任务</h1>
    </div>
    
    <!-- 任务类型选择 -->
    <div class="mb-6 bg-white rounded-lg p-4">
      <h2 class="text-lg font-medium mb-3">选择任务类型</h2>
      <div class="flex flex-nowrap overflow-x-auto pb-2 -mx-1">
        <button 
          v-for="type in taskTypes" 
          :key="type.id"
          @click="selectTaskType(type.id)"
          class="flex-shrink-0 px-4 py-2 mr-2 rounded-lg text-sm whitespace-nowrap"
          :class="[
            selectedType === type.id
              ? 'bg-primary-medium text-white'
              : 'bg-neutral-light text-primary-dark'
          ]"
        >
          {{ type.name }}
        </button>
      </div>
    </div>
    
    <!-- 表单区域 -->
    <form @submit.prevent="submitTask">
      <!-- 登录提示 -->
      <div v-if="!userStore.isLoggedIn" class="bg-yellow-50 border border-yellow-300 rounded-lg p-4 mb-6">
        <p class="text-yellow-700 mb-2">您需要登录后才能提交任务</p>
        <button 
          type="button"
          @click="goToLogin"
          class="btn bg-yellow-500 hover:bg-yellow-600 text-white"
        >
          立即登录
        </button>
      </div>
      
      <!-- 客户信息 -->
      <div class="bg-white rounded-lg p-4 mb-6">
        <h2 class="text-lg font-medium mb-3">客户信息</h2>
        
        <!-- 姓名 -->
        <div class="mb-4">
          <label for="customerName" class="form-label">客户姓名 <span class="text-red-500">*</span></label>
          <input 
            id="customerName" 
            v-model="formData.customer.name"
            type="text" 
            class="form-input" 
            placeholder="请输入姓名"
            required
          />
        </div>
        
        <!-- 公司 -->
        <div class="mb-4">
          <label for="customerCompany" class="form-label">公司名称 <span class="text-red-500">*</span></label>
          <input 
            id="customerCompany" 
            v-model="formData.customer.company"
            type="text" 
            class="form-input" 
            placeholder="请输入公司名称"
            required
          />
        </div>
        
        <!-- 联系电话 -->
        <div class="mb-4">
          <label for="customerPhone" class="form-label">联系电话 <span class="text-red-500">*</span></label>
          <input 
            id="customerPhone" 
            v-model="formData.customer.phone"
            type="tel" 
            class="form-input" 
            placeholder="请输入联系电话"
            required
          />
        </div>
        
        <!-- 联系地址 -->
        <div class="mb-4">
          <label for="customerAddress" class="form-label">联系地址 <span class="text-red-500">*</span></label>
          <input 
            id="customerAddress" 
            v-model="formData.customer.address"
            type="text" 
            class="form-input" 
            placeholder="请输入联系地址"
            required
          />
        </div>
        
        <!-- 电子邮箱 -->
        <div>
          <label for="customerEmail" class="form-label">电子邮箱</label>
          <input 
            id="customerEmail" 
            v-model="formData.customer.email"
            type="email" 
            class="form-input" 
            placeholder="请输入电子邮箱"
          />
        </div>
      </div>
      
      <!-- 仪器信息 -->
      <div class="bg-white rounded-lg p-4 mb-6">
        <h2 class="text-lg font-medium mb-3">仪器信息</h2>
        
        <!-- 根据不同任务类型显示不同表单 -->
        <!-- 仪器维修/保养 -->
        <div v-if="['repair', 'maintenance'].includes(selectedType)">
          <!-- 仪器基本信息 -->
          <device-basic-info v-model="formData.device" />
          
          <!-- 故障描述 -->
          <div class="mb-4">
            <label for="faultDescription" class="form-label">故障描述 <span class="text-red-500">*</span></label>
            <textarea 
              id="faultDescription" 
              v-model="formData.device.fault_description"
              rows="3" 
              class="form-input" 
              placeholder="请详细描述仪器故障情况"
              required
            ></textarea>
          </div>
          
          <!-- 数量 -->
          <div class="mb-4">
            <label for="deviceQuantity" class="form-label">数量 <span class="text-red-500">*</span></label>
            <input 
              id="deviceQuantity" 
              v-model="formData.device.quantity"
              type="number" 
              min="1" 
              class="form-input" 
              required
            />
          </div>
        </div>
        
        <!-- 仪器回收/租赁 -->
        <div v-else-if="['recycle', 'leasing'].includes(selectedType)">
          <!-- 仪器基本信息 -->
          <device-basic-info v-model="formData.device" />
          
          <!-- 数量 -->
          <div class="mb-4">
            <label for="deviceQuantity" class="form-label">数量 <span class="text-red-500">*</span></label>
            <input 
              id="deviceQuantity" 
              v-model="formData.device.quantity"
              type="number" 
              min="1" 
              class="form-input" 
              required
            />
          </div>
          
          <!-- 附件 -->
          <div class="mb-4">
            <label for="deviceAccessories" class="form-label">附件</label>
            <textarea 
              id="deviceAccessories" 
              v-model="formData.device.accessories"
              rows="3" 
              class="form-input" 
              placeholder="请描述仪器附带的附件"
            ></textarea>
          </div>
        </div>
        
        <!-- 培训预约 -->
        <div v-else-if="selectedType === 'training'">
          <!-- 仪器基本信息 -->
          <device-basic-info v-model="formData.device" />
          
          <!-- 预约时间 -->
          <div class="mb-4">
            <label for="appointmentTime" class="form-label">预约时间 <span class="text-red-500">*</span></label>
            <input 
              id="appointmentTime" 
              v-model="formData.device.appointment_time"
              type="datetime-local" 
              class="form-input" 
              required
            />
          </div>
        </div>
        
        <!-- 仪器验证 -->
        <div v-else-if="selectedType === 'verification'">
          <!-- 仪器基本信息 -->
          <device-basic-info v-model="formData.device" />
          
          <!-- 验证类别 -->
          <div class="mb-4">
            <label class="form-label">验证类别 <span class="text-red-500">*</span></label>
            <div class="mt-2">
              <label class="inline-flex items-center mr-4">
                <input type="radio" v-model="formData.device.verification_type" value="IQ" required>
                <span class="ml-2">IQ安装验证</span>
              </label>
              <label class="inline-flex items-center mr-4">
                <input type="radio" v-model="formData.device.verification_type" value="OQ" required>
                <span class="ml-2">OQ运行验证</span>
              </label>
              <label class="inline-flex items-center">
                <input type="radio" v-model="formData.device.verification_type" value="PQ" required>
                <span class="ml-2">PQ性能验证</span>
              </label>
            </div>
          </div>
          
          <!-- 数量 -->
          <div class="mb-4">
            <label for="deviceQuantity" class="form-label">数量 <span class="text-red-500">*</span></label>
            <input 
              id="deviceQuantity" 
              v-model="formData.device.quantity"
              type="number" 
              min="1" 
              class="form-input" 
              required
            />
          </div>
        </div>
        
        <!-- 仪器选型 -->
        <div v-else-if="selectedType === 'selection'">
          <!-- 仪器用途或名称 -->
          <div class="mb-4">
            <label for="devicePurpose" class="form-label">仪器用途或名称 <span class="text-red-500">*</span></label>
            <input 
              id="devicePurpose" 
              v-model="formData.device.purpose"
              type="text" 
              class="form-input" 
              placeholder="请输入仪器用途或名称"
              required
            />
          </div>
          
          <!-- 需求描述 -->
          <div class="mb-4">
            <label for="requirementDescription" class="form-label">需求描述 <span class="text-red-500">*</span></label>
            <textarea 
              id="requirementDescription" 
              v-model="formData.device.requirement_description"
              rows="3" 
              class="form-input" 
              placeholder="请详细描述您的需求"
              required
            ></textarea>
          </div>
          
          <!-- 数量 -->
          <div class="mb-4">
            <label for="deviceQuantity" class="form-label">需求数量 <span class="text-red-500">*</span></label>
            <input 
              id="deviceQuantity" 
              v-model="formData.device.quantity"
              type="number" 
              min="1" 
              class="form-input" 
              required
            />
          </div>
        </div>
        
        <!-- 仪器安装 -->
        <div v-else-if="selectedType === 'installation'">
          <!-- 仪器基本信息 -->
          <device-basic-info v-model="formData.device" />
          
          <!-- 数量 -->
          <div class="mb-4">
            <label for="deviceQuantity" class="form-label">数量 <span class="text-red-500">*</span></label>
            <input 
              id="deviceQuantity" 
              v-model="formData.device.quantity"
              type="number" 
              min="1" 
              class="form-input" 
              required
            />
          </div>
        </div>
      </div>
      
      <!-- 详细描述 -->
      <div class="bg-white rounded-lg p-4 mb-6">
        <h2 class="text-lg font-medium mb-3">任务描述</h2>
        <div class="mb-4">
          <label for="description" class="form-label">详细描述 <span class="text-red-500">*</span></label>
          <textarea 
            id="description" 
            v-model="formData.description"
            rows="4" 
            class="form-input" 
            placeholder="请详细描述您的需求"
            required
          ></textarea>
        </div>
      </div>
      
      <!-- 图片上传 -->
      <div class="bg-white rounded-lg p-4 mb-6">
        <h2 class="text-lg font-medium mb-3">图片上传</h2>
        <p class="text-sm text-gray-500 mb-2">可上传最多9张图片，自动压缩</p>
        <UploadImage v-model="formData.images" :max-count="9" :auto-compress="true" />
      </div>
      
      <!-- 附件上传 -->
      <div class="bg-white rounded-lg p-4 mb-6">
        <h2 class="text-lg font-medium mb-3">附件上传</h2>
        <p class="text-sm text-gray-500 mb-2">支持PDF、Word、Excel格式，单个文件最大10MB</p>
        <UploadFile v-model="formData.files" :max-size="10" :allowed-types="['pdf', 'doc', 'docx', 'xls', 'xlsx']" />
      </div>
      
      <!-- 提交按钮 -->
      <div class="fixed bottom-0 left-0 right-0 bg-white p-4 border-t border-gray-200">
        <button 
          type="submit" 
          class="w-full py-3 bg-primary-medium hover:bg-primary-dark text-white rounded-lg font-medium"
          :disabled="!userStore.isLoggedIn || isSubmitting"
        >
          {{ isSubmitting ? '提交中...' : '提交任务' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { useTaskStore } from '@/stores/task';
import UploadImage from '@/components/UploadImage.vue';
import UploadFile from '@/components/UploadFile.vue';
import DeviceBasicInfo from '@/components/DeviceBasicInfo.vue';
import { generateTaskId } from '@/utils/taskIdGenerator';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const taskStore = useTaskStore();

// 任务类型
const taskTypes = [
  { id: 'repair', name: '仪器维修' },
  { id: 'maintenance', name: '仪器保养' },
  { id: 'recycle', name: '仪器回收' },
  { id: 'leasing', name: '仪器租赁' },
  { id: 'training', name: '培训预约' },
  { id: 'verification', name: '仪器验证' },
  { id: 'selection', name: '仪器选型' },
  { id: 'installation', name: '仪器安装' }
];

// 任务类型配置
const taskTypeConfig = {
  repair: {
    fields: ['name', 'type', 'brand', 'model', 'fault_description', 'quantity'],
    title: '仪器维修'
  },
  maintenance: {
    fields: ['name', 'type', 'brand', 'model', 'fault_description', 'quantity'],
    title: '仪器保养'
  },
  recycle: {
    fields: ['name', 'type', 'brand', 'model', 'quantity', 'accessories'],
    title: '仪器回收'
  },
  leasing: {
    fields: ['name', 'type', 'brand', 'model', 'quantity', 'accessories'],
    title: '仪器租赁'
  },
  training: {
    fields: ['name', 'type', 'brand', 'model', 'appointment_time'],
    title: '培训预约'
  },
  verification: {
    fields: ['name', 'type', 'brand', 'model', 'verification_type', 'quantity'],
    title: '仪器验证'
  },
  selection: {
    fields: ['purpose', 'requirement_description', 'quantity'],
    title: '仪器选型'
  },
  installation: {
    fields: ['name', 'type', 'brand', 'model', 'quantity'],
    title: '仪器安装'
  }
};

// 选中的任务类型
const selectedType = ref(route.query.type || 'repair');

// 提交状态
const isSubmitting = ref(false);

// 提交结果
const submitResult = ref({
  success: false,
  message: ''
});

// 表单数据
const formData = reactive({
  customer: {
    name: '',
    company: '',
    phone: '',
    address: '',
    email: ''
  },
  device: {
    name: '',
    type: '',
    brand: '',
    model: '',
    fault_description: '',
    quantity: 1,
    accessories: '',
    appointment_time: '',
    verification_type: '',
    purpose: '',
    requirement_description: ''
  },
  description: '',
  images: [],
  files: []
});

// 当前任务类型配置
const currentTypeConfig = computed(() => {
  return taskTypeConfig[selectedType.value] || taskTypeConfig.repair;
});

// 加载用户信息
onMounted(() => {
  // 从URL参数获取任务类型
  const typeFromQuery = route.query.type;
  if (typeFromQuery && taskTypes.some(t => t.id === typeFromQuery)) {
    selectedType.value = typeFromQuery;
  }
  
  // 如果用户已登录，自动填充客户信息
  if (userStore.isLoggedIn) {
    const userInfo = userStore.userInfo;
    formData.customer.name = userInfo.contact || '';
    formData.customer.company = userInfo.name || '';
    formData.customer.phone = userInfo.phone || '';
    formData.customer.address = userInfo.address || '';
    formData.customer.email = userInfo.email || '';
  }
});

// 切换任务类型
const selectTaskType = (type) => {
  selectedType.value = type;
  resetDeviceFields(type);
};

// 重置设备字段
const resetDeviceFields = (type) => {
  // 创建新的设备对象，只保留当前任务类型需要的字段
  const newDevice = {
    name: '',
    type: '',
    brand: '',
    model: '',
    quantity: 1
  };
  
  // 根据任务类型添加特定字段
  if (['repair', 'maintenance'].includes(type)) {
    newDevice.fault_description = '';
  } else if (['recycle', 'leasing'].includes(type)) {
    newDevice.accessories = '';
  } else if (type === 'training') {
    newDevice.appointment_time = '';
  } else if (type === 'verification') {
    newDevice.verification_type = '';
  } else if (type === 'selection') {
    newDevice.purpose = '';
    newDevice.requirement_description = '';
  }
  
  // 更新formData.device
  formData.device = newDevice;
};

// 前往登录页
const goToLogin = () => {
  router.push({
    name: 'Login',
    query: { redirect: router.currentRoute.value.fullPath }
  });
};

// 返回上一页
const goBack = () => {
  router.back();
};

// 提交任务
const submitTask = async () => {
  if (!userStore.isLoggedIn) {
    goToLogin();
    return;
  }
  
  try {
    isSubmitting.value = true;
    
    // 生成任务ID
    const taskId = generateTaskId(formData.customer.company);
    
    // 构建提交数据
    const taskData = {
      task_id: taskId,
      type: selectedType.value,
      customer: { ...formData.customer },
      device: { ...formData.device },
      description: formData.description,
      // 图片和附件会由上传组件自动处理并返回URL
      images: formData.images,
      files: formData.files
    };
    
    // 调用API提交任务
    const result = await taskStore.createTask(taskData);
    
    submitResult.value = {
      success: true,
      message: '任务提交成功'
    };
    
    // 成功后跳转到任务列表页面
    router.push({
      name: 'TaskProgress',
      params: { id: result.id }
    });
  } catch (error) {
    console.error('提交任务失败:', error);
    submitResult.value = {
      success: false,
      message: error.message || '提交失败，请稍后重试'
    };
  } finally {
    isSubmitting.value = false;
  }
};
</script> 