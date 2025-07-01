<template>
  <div class="p-4">
    <!-- 顶部导航 -->
    <div class="flex items-center mb-4">
      <button @click="goBack" class="mr-2">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m15 18-6-6 6-6"/></svg>
      </button>
      <h1 class="text-xl font-medium">任务进展</h1>
    </div>
    
    <!-- 登录提示 -->
    <div v-if="!userStore.isLoggedIn" class="bg-yellow-50 border border-yellow-300 rounded-lg p-4 mb-6">
      <p class="text-yellow-700 mb-2">您需要登录后才能查看任务进展</p>
      <button 
        type="button"
        @click="goToLogin"
        class="btn bg-yellow-500 hover:bg-yellow-600 text-white"
      >
        立即登录
      </button>
    </div>
    
    <!-- 任务列表 -->
    <div v-else>
      <!-- 加载中状态 -->
      <div v-if="loading" class="text-center py-8">
        <p>加载中...</p>
      </div>
      
      <!-- 无任务状态 -->
      <div v-else-if="tasks.length === 0" class="text-center py-8">
        <p class="text-gray-500 mb-4">您还没有提交任何任务</p>
        <button @click="goToHome" class="btn btn-primary">创建新任务</button>
      </div>
      
      <!-- 任务列表 -->
      <div v-else>
        <TaskCard 
          v-for="task in tasks" 
          :key="task.task_id" 
          :task="task" 
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { useTaskStore } from '@/stores/task';
import TaskCard from '@/components/TaskCard.vue';

const router = useRouter();
const userStore = useUserStore();
const taskStore = useTaskStore();

const tasks = ref([]);
const loading = ref(false);
const error = ref('');

// 获取任务列表
const fetchTasks = async () => {
  if (!userStore.isLoggedIn) return;
  
  loading.value = true;
  
  try {
    tasks.value = await taskStore.fetchTaskList();
  } catch (err) {
    error.value = err.message || '获取任务列表失败';
    console.error('获取任务列表失败:', err);
  } finally {
    loading.value = false;
  }
};

// 页面加载时获取任务列表
onMounted(() => {
  if (userStore.isLoggedIn) {
    fetchTasks();
  }
});

// 前往登录页
const goToLogin = () => {
  router.push({
    name: 'Login',
    query: { redirect: router.currentRoute.value.fullPath }
  });
};

// 返回首页
const goToHome = () => {
  router.push('/');
};

// 返回上一页
const goBack = () => {
  router.back();
};
</script> 