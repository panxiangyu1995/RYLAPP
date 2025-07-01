<template>
  <div class="p-4">
    <!-- 顶部导航 -->
    <div class="flex items-center mb-4">
      <button @click="goBack" class="mr-2">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m15 18-6-6 6-6"/></svg>
      </button>
      <h1 class="text-xl font-medium">任务详情</h1>
    </div>
    
    <!-- 加载中状态 -->
    <div v-if="loading" class="text-center py-8">
      <p>加载中...</p>
    </div>
    
    <!-- 错误状态 -->
    <div v-else-if="error" class="bg-red-50 border border-red-300 rounded-lg p-4 mb-6">
      <p class="text-red-700">{{ error }}</p>
      <button @click="fetchTaskDetail" class="btn bg-red-500 hover:bg-red-600 text-white mt-2">重试</button>
    </div>
    
    <!-- 任务详情 -->
    <div v-else-if="task">
      <!-- 基本信息 -->
      <div class="bg-white rounded-lg p-4 mb-6">
        <div class="flex items-center justify-between mb-3">
          <span class="bg-primary-light text-white px-2 py-1 rounded-lg text-sm">{{ taskTypeInfo.name }}</span>
          <span class="text-sm text-gray-500">{{ formatDate(task.create_time) }}</span>
        </div>
        
        <h3 class="font-medium mb-2">{{ task.device_name || '未指定设备' }}</h3>
        <p class="text-sm text-gray-600 mb-3">{{ task.description || '无任务描述' }}</p>
        
        <div class="text-sm">
          <p>负责人: {{ task.engineer_name || '未分配' }}</p>
          <p class="text-gray-500">联系电话: {{ task.engineer_phone || '未提供' }}</p>
        </div>
      </div>
      
      <!-- 任务进度 -->
      <TaskProgress 
        :task-type="task.task_type" 
        :current-step="task.current_step" 
        :step-content="currentStepContent" 
      />
      
      <!-- 服务评价表单 -->
      <div v-if="task.current_step === 6">
        <ServiceEvaluation 
          :task-id="task.task_id" 
          @submit-success="handleEvaluationSubmit" 
          @submit-error="handleEvaluationError" 
        />
      </div>
    </div>
    
    <!-- 任务不存在 -->
    <div v-else class="text-center py-8">
      <p class="text-gray-500">任务不存在或已被删除</p>
      <button @click="goToTaskList" class="btn btn-primary mt-4">返回任务列表</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useTaskStore } from '@/stores/task';
import TaskProgress from '@/components/TaskProgress.vue';
import ServiceEvaluation from '@/components/ServiceEvaluation.vue';

const router = useRouter();
const route = useRoute();
const taskStore = useTaskStore();

const taskId = route.params.id;
const task = ref(null);
const loading = ref(false);
const error = ref('');

// 获取任务类型信息
const taskTypeInfo = computed(() => {
  if (!task.value) return { name: '未知类型' };
  return taskStore.getTaskTypeInfo(task.value.task_type);
});

// 获取当前步骤内容
const currentStepContent = computed(() => {
  if (!task.value || !task.value.step_content) return null;
  return task.value.step_content;
});

// 获取任务详情
const fetchTaskDetail = async () => {
  loading.value = true;
  error.value = '';
  
  try {
    task.value = await taskStore.fetchTaskDetail(taskId);
  } catch (err) {
    error.value = err.message || '获取任务详情失败';
    console.error('获取任务详情失败:', err);
  } finally {
    loading.value = false;
  }
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

// 处理评价提交成功
const handleEvaluationSubmit = async (data) => {
  try {
    await taskStore.submitEvaluation(taskId, data);
    // 重新获取任务详情，刷新状态
    fetchTaskDetail();
  } catch (err) {
    console.error('提交评价失败:', err);
  }
};

// 处理评价提交失败
const handleEvaluationError = (err) => {
  console.error('评价提交错误:', err);
};

// 返回任务列表
const goToTaskList = () => {
  router.push({ name: 'TaskProgress' });
};

// 返回上一页
const goBack = () => {
  router.back();
};

// 页面加载时获取任务详情
onMounted(() => {
  fetchTaskDetail();
});
</script> 