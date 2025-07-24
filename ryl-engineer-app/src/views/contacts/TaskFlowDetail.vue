<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import http from '@/api/http';
import { format } from 'date-fns';

const route = useRoute();
const router = useRouter();

// State
const taskDetail = ref(null);
const taskFlow = ref(null);
const loading = ref(true);
const error = ref(null);

// 任务类型名称映射
const taskTypeNames = {
  repair: '仪器维修',
  maintenance: '仪器保养',
  recycle: '仪器回收',
  leasing: '仪器租赁',
  training: '培训预约',
  verification: '仪器验证',
  selection: '仪器选型',
  installation: '仪器安装'
};

// --- Methods ---
const goBack = () => {
  if (window.history.length > 1) {
    router.back();
  } else {
    // Fallback if no history
    router.push({ name: 'Home' }); 
  }
};

const fetchTaskData = async () => {
  loading.value = true;
  error.value = null;
  const taskId = route.params.id;

  if (!taskId) {
    error.value = '未提供任务ID';
    loading.value = false;
    return;
  }

  try {
    const [detailRes, flowRes] = await Promise.all([
      http.get(`/api/v1/tasks/${taskId}`),
      http.get(`/api/v1/tasks/${taskId}/flow`)
    ]);

    if (detailRes.code === 200 && detailRes.data) {
      taskDetail.value = detailRes.data;
    } else {
      throw new Error(detailRes.message || '获取任务详情失败');
    }

    if (flowRes.code === 200 && flowRes.data) {
      taskFlow.value = flowRes.data;
    } else {
      throw new Error(flowRes.message || '获取任务流程失败');
    }
  } catch (err) {
    console.error('加载任务数据出错:', err);
    error.value = err.message || '加载任务数据时发生未知错误';
  } finally {
    loading.value = false;
  }
};

const getStatusClass = (status) => {
  switch (status) {
    case 'in-progress': return 'status-in-progress';
    case 'completed': return 'status-completed';
    case 'pending': return 'status-pending';
    default: return '';
  }
};

const previewImage = (image) => {
  console.log('预览图片:', image);
  // Implement image preview logic, e.g., using a modal
};

// --- Computed Properties ---
const processedSteps = computed(() => {
  if (!taskFlow.value || !taskFlow.value.steps) {
    return { completed: [], current: null, pending: [] };
  }

  const steps = {
    completed: [],
    current: null,
    pending: []
  };

  taskFlow.value.steps.forEach(step => {
    switch (step.status) {
      case 'completed':
      case 'skipped':
        steps.completed.push(step);
        break;
      case 'in-progress':
        steps.current = step;
        break;
      case 'pending':
        steps.pending.push(step);
        break;
    }
  });

  return steps;
});

// --- Lifecycle Hooks ---
onMounted(() => {
  fetchTaskData();
});
</script>

<template>
  <div class="task-flow-detail-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="fas fa-arrow-left"></i>
      </div>
      <h1>任务流程详情</h1>
      <div class="header-right">
        <!-- Share button removed -->
      </div>
    </header>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>
    
    <!-- 错误提示 -->
    <div v-else-if="error" class="error-container">
      <i class="fas fa-exclamation-circle"></i>
      <p>{{ error }}</p>
      <div class="error-actions">
        <button class="retry-btn" @click="fetchTaskData">重试</button>
        <button class="back-btn" @click="goBack">返回</button>
      </div>
    </div>
    
    <!-- 任务基本信息 -->
    <div v-else-if="taskDetail" class="task-info">
      <div class="task-header">
        <div>
          <h2 class="task-title">{{ taskDetail.title }}</h2>
          <p class="task-id">任务编号: {{ taskDetail.taskId }}</p>
          <div class="task-meta">
            <span class="task-type">{{ taskTypeNames[taskDetail.taskType] || '未知类型' }}</span>
            <span class="task-assignee" v-if="taskDetail.engineers && taskDetail.engineers.length > 0">
              负责人: {{ taskDetail.engineers.find(e => e.isOwner)?.name || '未指定' }}
            </span>
            <span :class="['task-status', getStatusClass(taskDetail.status)]">{{ taskDetail.statusText }}</span>
          </div>
        </div>
        <div class="task-time" v-if="taskDetail.createTime">
          <p class="time-label">创建时间</p>
          <p class="time-value">{{ format(new Date(taskDetail.createTime), 'yyyy-MM-dd') }}</p>
        </div>
      </div>

      <!-- 设备信息 -->
      <div class="device-info" v-if="taskDetail.deviceName">
        <h3 class="section-subtitle">设备信息</h3>
        <div class="device-details">
          <div class="device-item">
            <span class="device-label">名称:</span>
            <span class="device-value">{{ taskDetail.deviceName }}</span>
          </div>
          <div class="device-item">
            <span class="device-label">型号:</span>
            <span class="device-value">{{ taskDetail.deviceModel }}</span>
          </div>
          <div class="device-item">
            <span class="device-label">品牌:</span>
            <span class="device-value">{{ taskDetail.deviceBrand }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 任务流程时间线 -->
    <div v-if="!loading && !error && taskFlow" class="flow-timeline">
      <h3 class="section-title">{{ taskTypeNames[taskFlow.taskType] || '未知类型' }}流程</h3>
      
      <!-- 已完成步骤 -->
      <div 
        v-for="step in processedSteps.completed" 
        :key="step.id"
        class="flow-step completed"
      >
        <div class="step-line"></div>
        <div class="step-content">
          <div class="step-icon">
            <i class="fas fa-check"></i>
          </div>
          <div class="step-details">
            <div class="step-header">
              <h4 class="step-title">{{ step.name }}</h4>
              <span class="step-time">{{ step.endTime ? format(new Date(step.endTime), 'HH:mm') : '' }}</span>
            </div>
            <div class="step-card">
              <p class="step-description">{{ step.recordContent || '无记录内容' }}</p>
              <div class="step-images" v-if="step.images && step.images.length > 0">
                <img 
                  v-for="(image, i) in step.images" 
                  :key="i"
                  :src="image"
                  alt="步骤图片"
                  class="step-image"
                  @click="previewImage(image)"
                >
              </div>
              <div class="step-meta">
                <span class="step-date">{{ step.endTime ? format(new Date(step.endTime), 'yyyy-MM-dd') : '' }}</span>
                <!-- duration需要后端计算，暂时留空 -->
                <!-- <span class="step-duration">用时: {{ step.duration }}</span> -->
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 当前步骤 -->
      <div v-if="processedSteps.current" class="flow-step current">
        <div class="step-content">
          <div class="step-icon">
            <i class="fas fa-pen"></i>
          </div>
          <div class="step-details">
            <div class="step-header">
              <h4 class="step-title">{{ processedSteps.current.name }}</h4>
              <span class="step-time">{{ processedSteps.current.startTime ? format(new Date(processedSteps.current.startTime), 'HH:mm') : '' }}</span>
            </div>
            <div class="step-card current">
              <p class="step-description">{{ processedSteps.current.recordContent || '暂无记录' }}</p>
              <div class="step-images" v-if="processedSteps.current.images && processedSteps.current.images.length > 0">
                <img 
                  v-for="(image, i) in processedSteps.current.images" 
                  :key="i"
                  :src="image"
                  alt="步骤图片"
                  class="step-image"
                  @click="previewImage(image)"
                >
              </div>
              <div class="step-meta">
                <span class="step-date">{{ processedSteps.current.startTime ? format(new Date(processedSteps.current.startTime), 'yyyy-MM-dd') : '' }}</span>
                <!-- <span class="step-duration">用时: {{ processedSteps.current.duration }}</span> -->
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 未完成步骤 -->
      <div class="pending-steps" v-if="processedSteps.pending.length > 0">
        <div class="pending-label">待完成步骤</div>
        <ol class="pending-list">
          <li v-for="step in processedSteps.pending" :key="step.id">
            {{ step.name }}
          </li>
        </ol>
      </div>
    </div>
    
    <!-- 底部操作栏 -->
    <div class="bottom-actions">
      <button class="bottom-btn primary" @click="goBack">
        <i class="fas fa-arrow-left"></i>返回
      </button>
    </div>
  </div>
</template>

<style scoped>
.task-flow-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
}

.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: #3b82f6;
  animation: spin 1s ease-in-out infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.icon-error {
  font-size: 48px;
  color: #ef4444;
  margin-bottom: 16px;
}

.error-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
}

.retry-btn,
.back-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.retry-btn {
  background-color: #3b82f6;
  color: white;
}

.back-btn {
  background-color: #6b7280;
  color: white;
}

.header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 10;
}

.header-left {
  width: 24px;
}

.icon-arrow-left::before {
  content: '\f060';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
}

.header h1 {
  flex: 1;
  font-size: 18px;
  font-weight: 600;
  margin-left: 16px;
}

.share-btn {
  display: flex;
  align-items: center;
  padding: 6px 12px;
  background-color: #3b82f6;
  color: #fff;
  border-radius: 9999px;
  font-size: 14px;
  border: none;
}

.icon-share::before {
  content: '\f064';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 4px;
}

.task-info {
  background-color: #fff;
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.task-title {
  font-size: 18px;
  font-weight: 600;
}

.task-id {
  font-size: 14px;
  color: #6b7280;
  margin-top: 4px;
}

.task-meta {
  display: flex;
  align-items: center;
  margin-top: 8px;
}

.task-type {
  padding: 4px 8px;
  border-radius: 9999px;
  font-size: 12px;
  background-color: #fef3c7;
  color: #d97706;
  margin-right: 16px;
}

.task-assignee {
  font-size: 14px;
  color: #6b7280;
  margin-right: 16px;
}

.task-status {
  padding: 4px 8px;
  border-radius: 9999px;
  font-size: 12px;
}

.status-in-progress {
  background-color: #fef3c7;
  color: #d97706;
}

.status-completed {
  background-color: #d1fae5;
  color: #059669;
}

.status-pending {
  background-color: #dbeafe;
  color: #3b82f6;
}

.task-time {
  text-align: right;
}

.time-label {
  font-size: 12px;
  color: #6b7280;
}

.time-value {
  font-size: 14px;
  font-weight: 500;
  margin-top: 4px;
}

.device-info {
  margin-top: 16px;
}

.section-subtitle {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
}

.device-details {
  display: flex;
  flex-wrap: wrap;
}

.device-item {
  width: 50%;
  margin-bottom: 8px;
}

.device-label {
  font-size: 14px;
  color: #6b7280;
}

.device-value {
  font-size: 14px;
  font-weight: 500;
  margin-left: 8px;
}

.flow-timeline {
  background-color: #fff;
  padding: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 16px;
}

.flow-step {
  position: relative;
  padding-bottom: 32px;
}

.flow-step.completed .step-line {
  position: absolute;
  left: 16px;
  top: 32px;
  bottom: 0;
  width: 2px;
  background-color: #e5e7eb;
}

.step-content {
  display: flex;
}

.step-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #10b981;
  flex-shrink: 0;
  z-index: 1;
}

.flow-step.current .step-icon {
  background-color: #3b82f6;
}

.icon-check::before {
  content: '\f00c';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #fff;
  font-size: 12px;
}

.icon-pen::before {
  content: '\f304';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #fff;
  font-size: 12px;
}

.step-details {
  margin-left: 16px;
  flex: 1;
}

.step-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.step-title {
  font-size: 14px;
  font-weight: 500;
}

.step-time {
  font-size: 12px;
  color: #6b7280;
}

.step-card {
  background-color: #f3f4f6;
  padding: 12px;
  border-radius: 8px;
}

.step-card.current {
  background-color: #eff6ff;
  border-left: 4px solid #3b82f6;
}

.step-description {
  font-size: 14px;
}

.step-images {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding: 8px 0;
  margin-top: 8px;
}

.step-image {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  object-fit: cover;
  cursor: pointer;
}

.step-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #6b7280;
  margin-top: 8px;
}

.pending-steps {
  margin-left: 48px;
  margin-top: 16px;
}

.pending-label {
  font-size: 14px;
  color: #9ca3af;
  margin-bottom: 8px;
}

.pending-list {
  list-style: decimal;
  padding-left: 20px;
  font-size: 14px;
  color: #6b7280;
}

.pending-list li {
  margin-bottom: 8px;
}

/* 底部操作栏样式 */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  border-top: 1px solid #e5e7eb;
  padding: 16px;
  padding-bottom: calc(16px + 56px); /* 增加底部padding，防止被导航栏遮挡 */
  display: flex;
  gap: 8px;
}

.bottom-btn {
  flex: 1;
  padding: 12px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  border: none;
}

.bottom-btn.primary {
  background-color: #3b82f6;
  color: #fff;
}

.icon-arrow-left::before {
  content: '\f060';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 8px;
}
</style> 