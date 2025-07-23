<template>
  <view class="p-4 bg-ui-bg-white">
    <!-- 顶部导航
    <view class="flex items-center mb-4">
      <button @click="goBack" class="mr-2">
        <ChevronLeftIcon :size="24" color="#0E4472" />
      </button>
      <view class="text-xl font-medium">订单进展</view>
    </view> -->
    
    <!-- 调试模式提示 (REMOVED) -->
    
    <!-- 任务列表 -->
    <view>
      <!-- 加载中状态 -->
      <view v-if="loading" class="text-center py-8">
        <view>加载中...</view>
      </view>
      
      <!-- 无任务状态 -->
      <view v-else-if="tasks.length === 0" class="text-center py-8">
        <view class="text-gray-500 mb-4">{{ userStore.isLoggedIn ? '您还没有提交任何订单' : '请登录后查看订单' }}</view>
        <button 
          @click="userStore.isLoggedIn ? goToHome() : goToLogin()" 
          class="bg-ui-vibrant-gradient text-white font-bold py-2 px-4 rounded-lg shadow-md hover:opacity-90"
        >
          {{ userStore.isLoggedIn ? '创建新订单' : '立即登录' }}
        </button>
      </view>
      
      <!-- 任务列表 -->
      <view v-else>
        <TaskCard 
          v-for="task in tasks" 
          :key="task.id" 
          :task="task" 
        />
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { useUserStore } from '@/stores/user';
import { useTaskStore } from '@/stores/task';
import TaskCard from '@/components/TaskCard.vue';
import ChevronLeftIcon from '@/components/icons/ChevronLeftIcon.vue';
import { onShow } from '@dcloudio/uni-app';

const userStore = useUserStore();
const taskStore = useTaskStore();

const tasks = ref([]);
const loading = ref(false);
const error = ref('');

// 获取任务列表
const fetchTasks = async () => {
  if (!userStore.isLoggedIn) {
    tasks.value = [];
    return;
  }
  loading.value = true;
  
  try {
    // 确保在调用前 Pinia store 已准备好
    tasks.value = await taskStore.fetchTaskList();
  } catch (err) {
    error.value = err.message || '获取订单列表失败';
    console.error('获取订单列表失败:', err);
  } finally {
    loading.value = false;
  }
};

// 页面加载时获取任务列表
onShow(() => {
  fetchTasks();
});

// 前往登录页
const goToLogin = () => {
  uni.navigateTo({
    url: '/pages/login/login?redirect=/pages/taskprogress/taskprogress'
  });
};

// 返回首页
const goToHome = () => {
  uni.reLaunch({ url: '/pages/home/home' });
};

// 返回上一页
const goBack = () => {
  uni.navigateBack();
};
</script> 