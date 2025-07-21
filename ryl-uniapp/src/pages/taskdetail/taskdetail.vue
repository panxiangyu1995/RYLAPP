<template>
  <view class="p-4 bg-ui-bg-white">
    <!-- 顶部导航
    <view class="flex items-center mb-4">
      <button @click="goBack" class="mr-2">
        <ChevronLeftIcon :size="24" color="#0E4472" />
      </button>
      <view class="text-xl font-medium">订单详情</view>
    </view> -->
    
    <!-- 加载中状态 -->
    <view v-if="loading" class="text-center py-8">
      <view>加载中...</view>
    </view>
    
    <!-- 错误状态 -->
    <view v-else-if="error" class="bg-red-50 border border-red-300 rounded-lg p-4 mb-6">
      <view class="text-red-700">{{ error }}</view>
      <button @click="fetchTaskDetail" class="px-4 py-2 rounded-lg font-medium transition-colors bg-red-500 hover:bg-red-600 text-white mt-2">重试</button>
    </view>
    
    <!-- 订单详情 -->
    <view v-else-if="task">
      <!-- 基本信息 -->
      <view class="bg-white rounded-lg p-4 mb-6">
        <view class="flex items-center justify-between mb-3">
          <text class="bg-ui-vibrant-gradient text-white px-2 py-1 rounded-lg text-sm">{{ taskTypeInfo.name }}</text>
          <text class="text-sm text-gray-500">{{ formatDate(task.createTime) }}</text>
        </view>
        
        <view class="font-medium mb-2 block text-ui-text-black">{{ task.deviceName || '未指定设备' }}</view>
        <view class="text-sm text-gray-600 mb-3 block">{{ task.description || '无订单描述' }}</view>
        
        <view class="text-sm">
          <view class="text-ui-text-black">负责人: {{ task.engineerName || '未分配' }}</view>
          <view class="text-gray-500 block">联系电话: {{ task.engineerPhone || '未提供' }}</view>
        </view>
      </view>
      
      <!-- 订单进度 -->
      <TaskProgress 
        :task-type="task.taskType" 
        :current-step="task.currentStep" 
        :step-content="currentStepContent" 
        :task-id="task.id"
        :price-confirmed="task.priceConfirmed"
        @confirm-price="handleConfirmPrice"
      />
      
      <!-- 步骤记录时间线 -->
      <view v-if="stepsWithDetails.length > 0" class="mt-6">
        <view class="text-lg font-medium mb-3 text-ui-text-black">服务记录</view>
        <view v-for="step in stepsWithDetails" :key="step.id" class="bg-white rounded-lg shadow-md overflow-hidden p-4 mb-4">
          <view class="flex justify-between items-center mb-2">
            <text class="font-medium text-ui-text-black">{{ step.creatorName }}</text>
            <text class="text-xs text-gray-500">{{ formatDate(step.createTime) }}</text>
          </view>
          <view class="text-gray-700 block mb-3">{{ step.description }}</view>

          <!-- 图片展示 -->
          <view v-if="step.images && step.images.length > 0" class="grid grid-cols-4 gap-2 mb-3">
            <view v-for="(image, index) in step.images" :key="image.id" @click="previewImage(step.images, index)" class="w-full aspect-square rounded-lg overflow-hidden">
              <image :src="image.imageUrl" class="w-full h-full object-cover" mode="aspectFill" />
            </view>
          </view>

          <!-- 附件下载 -->
          <view v-if="step.files && step.files.length > 0">
            <view v-for="file in step.files" :key="file.id" class="flex items-center justify-between p-2 bg-neutral-light rounded-md mb-2">
              <view class="flex items-center overflow-hidden">
                <FileTextIcon :size="20" color="#111827" class="mr-2" />
                <text class="text-sm truncate text-ui-text-black">{{ file.fileName }}</text>
              </view>
              <button @click="downloadFile(file)" class="px-2 py-1 rounded-md font-medium transition-colors border border-ui-blue-start text-ui-blue-start hover:bg-ui-blue-start hover:text-white text-sm ml-2">下载</button>
            </view>
          </view>
        </view>
      </view>

      <!-- 服务评价表单 -->
      <view v-if="task.currentStep === 6 && task.priceConfirmed" class="mt-6">
        <ServiceEvaluation 
          :task-id="task.id" 
          @submit-success="handleEvaluationSubmit" 
          @submit-error="handleEvaluationError" 
        />
      </view>
    </view>
    
    <!-- 订单不存在 -->
    <view v-else class="text-center py-8">
      <view class="text-gray-500">订单不存在或已被删除</view>
      <button @click="goToTaskProgress" class="px-4 py-2 rounded-lg font-medium transition-colors bg-ui-vibrant-gradient text-white hover:opacity-90 mt-4">返回任务列表</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useTaskStore } from '@/stores/task';
import { useUserStore } from '@/stores/user';
import { withAuth } from '@/utils/authGuard';
import TaskProgress from '@/components/TaskProgress.vue';
import ServiceEvaluation from '@/components/ServiceEvaluation.vue';
import ChevronLeftIcon from '@/components/icons/ChevronLeftIcon.vue';
import FileTextIcon from '@/components/icons/FileTextIcon.vue';
import { onLoad } from '@dcloudio/uni-app';

const taskStore = useTaskStore();
const userStore = useUserStore();

const taskId = ref(null);
const task = ref(null);
const stepsWithDetails = ref([]);
const loading = ref(false);
const error = ref('');

// 获取任务类型信息
const taskTypeInfo = computed(() => {
  if (!task.value) return { name: '未知类型' };
  return taskStore.getTaskTypeInfo(task.value.taskType);
});

// 获取当前步骤内容
const currentStepContent = computed(() => {
  if (!task.value || !stepsWithDetails.value || stepsWithDetails.value.length === 0) {
    return null;
  }
  // 步骤已经按时间排序，最新的在最前面
  return stepsWithDetails.value[0] || null;
});

// 获取任务详情
const fetchTaskDetail = async () => {
  if (!userStore.isLoggedIn) {
    error.value = '请登录后查看订单详情。';
    loading.value = false;
    return;
  }
  if (!taskId.value) return;
  loading.value = true;
  error.value = '';
  
  try {
    // 1. 获取主任务详情
    const taskData = await taskStore.fetchTaskDetail(taskId.value);
    
    if (taskData) {
      task.value = taskData;
      // 2. 获取包含详情的步骤列表
      const stepsData = await taskStore.fetchTaskStepsWithDetails(taskId.value);
      // 按创建时间降序排序
      stepsWithDetails.value = stepsData.sort((a, b) => new Date(b.createTime) - new Date(a.createTime));
    } else {
      task.value = null;
      stepsWithDetails.value = [];
    }
  } catch (err) {
    error.value = err.message || '获取订单详情失败';
    console.error('获取订单详情失败:', err);
  } finally {
    loading.value = false;
  }
};

const previewImage = (images, currentIndex) => {
    const urls = images.map(i => i.imageUrl);
    uni.previewImage({
        urls: urls,
        current: currentIndex
    });
};

const downloadFile = (file) => {
    uni.showLoading({ title: '正在打开...' });
    uni.downloadFile({
        url: file.fileUrl,
        success: (res) => {
            if (res.statusCode === 200) {
                uni.openDocument({
                    filePath: res.tempFilePath,
                    showMenu: true, // H5/App端会显示右上角菜单
                    fail: (openErr) => {
                        console.error('打开文件失败:', openErr);
                        uni.showToast({ title: '打开文件失败', icon: 'none' });
                    }
                });
            } else {
                uni.showToast({ title: '下载文件失败', icon: 'none' });
            }
        },
        fail: (downloadErr) => {
            console.error('下载文件失败:', downloadErr);
            uni.showToast({ title: '下载文件失败', icon: 'none' });
        },
        complete: () => {
            uni.hideLoading();
        }
    });
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

// 处理评价提交成功
const handleEvaluationSubmit = withAuth(async (data) => {
  try {
    await taskStore.submitEvaluation(taskId.value, data);
    // 重新获取任务详情，刷新状态
    fetchTaskDetail();
  } catch (err) {
    console.error('提交评价失败:', err);
  }
});

// 处理评价提交失败
const handleEvaluationError = (err) => {
  console.error('评价提交错误:', err);
};

// 返回任务列表
const goToTaskProgress = () => {
  uni.navigateTo({ url: '/pages/taskprogress/taskprogress' });
};

// 返回上一页
const goBack = () => {
  uni.navigateBack();
};

// 处理报价确认
const handleConfirmPrice = withAuth(async () => {
  try {
    await taskStore.confirmTaskPrice(taskId.value);
    uni.showToast({ title: '报价确认成功！', icon: 'success' });
    // 在重新获取任务详情之前，直接更新当前task的priceConfirmed属性
    if (task.value) {
      task.value.priceConfirmed = 1;
    }
    // 重新获取任务详情，刷新状态
    fetchTaskDetail();
  } catch (err) {
    console.error('确认报价失败:', err);
    uni.showToast({ title: '确认报价失败，请重试', icon: 'none' });
  }
});

// 页面加载时获取任务详情
onLoad((options) => {
  if (options.id) {
    taskId.value = Number(options.id);
    fetchTaskDetail();
  } else {
    error.value = '无效的订单ID';
  }
});
</script> 

<style scoped>
/* 样式 */
</style>