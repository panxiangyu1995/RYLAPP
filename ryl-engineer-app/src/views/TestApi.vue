<template>
  <div class="test-api-page">
    <header class="header">
      <h1>API测试页面</h1>
      <div class="header-actions">
        <button @click="goBack" class="back-btn">返回</button>
      </div>
    </header>

    <div class="content">
      <div class="card">
        <h2>API连接测试</h2>
        <div class="actions">
          <button @click="testTaskList" class="test-btn">测试任务列表API</button>
        </div>
      </div>

      <div class="card result-card" v-if="loading">
        <div class="spinner"></div>
        <p>请求中...</p>
      </div>

      <div class="card result-card" v-if="error">
        <h3>错误信息</h3>
        <pre class="error-message">{{ error }}</pre>
      </div>

      <div class="card result-card" v-if="result">
        <h3>API响应结果</h3>
        <pre class="result-json">{{ JSON.stringify(result, null, 2) }}</pre>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '../api/http'
import { getTaskList } from '../api/task'

export default {
  name: 'TestApiPage',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const error = ref(null)
    const result = ref(null)

    const goBack = () => {
      router.back()
    }

    const testTaskList = async () => {
      loading.value = true
      error.value = null
      result.value = null

      try {
        const response = await getTaskList({ page: 1, size: 10 })
        console.log('任务列表API响应:', response)
        result.value = response
      } catch (err) {
        console.error('API测试失败:', err)
        error.value = err.message || JSON.stringify(err)
      } finally {
        loading.value = false
      }
    }

    return {
      loading,
      error,
      result,
      goBack,
      testTaskList
    }
  }
}
</script>

<style scoped>
.test-api-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.header h1 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.back-btn {
  padding: 6px 12px;
  background-color: #f3f4f6;
  border: none;
  border-radius: 4px;
  font-size: 14px;
}

.content {
  padding: 16px;
}

.card {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.card h2 {
  font-size: 16px;
  margin-top: 0;
  margin-bottom: 16px;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.test-btn {
  padding: 8px 16px;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
}

.result-card {
  max-height: 400px;
  overflow-y: auto;
}

.result-json, .error-message {
  background-color: #f3f4f6;
  padding: 12px;
  border-radius: 4px;
  font-family: monospace;
  font-size: 12px;
  white-space: pre-wrap;
  word-break: break-word;
}

.error-message {
  color: #ef4444;
}

.spinner {
  width: 30px;
  height: 30px;
  border: 3px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: #3b82f6;
  animation: spin 1s ease-in-out infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style> 