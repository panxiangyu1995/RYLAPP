<template>
  <div class="edit-task-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>编辑任务</h1>
      <div class="header-right">
        <button class="save-btn" @click="saveAsDraft">
          保存草稿
        </button>
      </div>
    </header>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>
    
    <!-- 错误提示 -->
    <div v-else-if="error" class="error-container">
      <i class="icon-error"></i>
      <p>{{ error }}</p>
      <div class="error-actions">
        <button class="retry-btn" @click="loadTaskDetail">重试</button>
        <button class="back-btn" @click="goBack">返回</button>
      </div>
    </div>

    <!-- 表单内容 -->
    <div v-else class="form-container">
      <p>编辑任务功能正在开发中...</p>
    </div>
  </div>
</template>

<script>
import { useRouter, useRoute } from 'vue-router'
import { ref, onMounted } from 'vue'

export default {
  name: 'EditTaskPage',
  props: {
    id: {
      type: String,
      required: false
    }
  },
  setup(props) {
    const router = useRouter()
    const route = useRoute()
    const loading = ref(true)
    const error = ref(null)

    // 导航返回
    const goBack = () => {
      router.back()
    }

    // 保存为草稿
    const saveAsDraft = () => {
      alert('保存草稿功能正在开发中')
    }

    // 加载任务详情
    const loadTaskDetail = async () => {
      loading.value = true
      error.value = null
      
      try {
        // 模拟加载
        await new Promise(resolve => setTimeout(resolve, 1000))
        loading.value = false
      } catch (err) {
        console.error('加载任务详情失败:', err)
        error.value = err.message || '加载任务详情失败'
        loading.value = false
      }
    }

    onMounted(() => {
      loadTaskDetail()
    })

    return {
      loading,
      error,
      goBack,
      saveAsDraft,
      loadTaskDetail
    }
  }
}
</script>

<style scoped>
.edit-task-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 32px;
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

.save-btn {
  color: #3b82f6;
  font-size: 14px;
  background: none;
  border: none;
}

.form-container {
  padding: 16px;
  text-align: center;
  margin-top: 20px;
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
</style> 