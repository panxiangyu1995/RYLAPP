<template>
  <div class="request-assistance-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>请求协助</h1>
      <div class="header-right"></div>
    </header>

    <!-- 加载状态 -->
    <div class="loading" v-if="loading">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 错误信息 -->
    <div class="error-message" v-else-if="error">
      <i class="icon-alert-circle"></i>
      <p>{{ error }}</p>
      <button @click="fetchEngineerInfo">重试</button>
    </div>

    <!-- 请求表单 -->
    <div class="form-container" v-else>
      <!-- 工程师信息 -->
      <div class="engineer-info">
        <h2>协助工程师</h2>
        <div class="engineer-card">
          <img :src="engineerAvatar" alt="工程师头像" class="engineer-avatar">
          <div class="engineer-details">
            <div class="engineer-name">{{ engineerName }}</div>
            <div class="engineer-task-count">当前任务数: {{ taskCount }}</div>
          </div>
        </div>
      </div>

      <!-- 请求信息 -->
      <div class="request-form">
        <div class="form-group">
          <label for="urgency">紧急程度</label>
          <select id="urgency" v-model="requestData.urgency" class="form-input">
            <option value="low">一般</option>
            <option value="medium">紧急</option>
            <option value="high">非常紧急</option>
          </select>
        </div>

        <div class="form-group">
          <label for="task-info">关联任务</label>
          <select id="task-info" v-model="requestData.taskId" class="form-input">
            <option value="">无关联任务</option>
            <option v-for="task in relatedTasks" :key="task.id" :value="task.id">
              {{ task.title }} ({{ task.taskId }})
            </option>
          </select>
        </div>

        <div class="form-group">
          <label for="description">问题描述</label>
          <textarea 
            id="description" 
            v-model="requestData.description" 
            class="form-input textarea"
            placeholder="简要描述需要协助的问题..."
            rows="4"
          ></textarea>
        </div>
      </div>
    </div>

    <!-- 底部提交按钮 -->
    <div class="bottom-actions">
      <button class="submit-btn" :disabled="!isFormValid || submitting" @click="submitRequest">
        {{ submitting ? '提交中...' : '提交请求' }}
      </button>
    </div>
  </div>
</template>

<script>
import { createAssistanceRequest } from '@/api/assistance'
import { getContactDetail } from '@/api/contacts'

export default {
  name: 'RequestAssistancePage',
  data() {
    return {
      engineerId: this.$route.params.engineerId,
      engineerName: '',
      engineerAvatar: '',
      taskCount: 0,
      loading: true,
      error: null,
      submitting: false,
      requestData: {
        urgency: 'medium',
        taskId: '',
        description: ''
      },
      relatedTasks: []
    }
  },
  computed: {
    isFormValid() {
      return this.requestData.description.trim().length > 0
    }
  },
  created() {
    this.fetchEngineerInfo()
    this.fetchRelatedTasks()
  },
  methods: {
    async fetchEngineerInfo() {
      this.loading = true
      this.error = null
      
      try {
        const response = await getContactDetail(this.engineerId)
        
        if (response.code === 200 && response.data) {
          const engineer = response.data
          this.engineerName = engineer.name
          this.engineerAvatar = engineer.avatar || '/default-avatar.png'
          
          // 如果有当前任务数据则设置任务数
          if (engineer.currentTasks) {
            this.taskCount = engineer.currentTasks.length
          }
        } else {
          this.error = response.message || '获取工程师信息失败'
        }
      } catch (error) {
        console.error('获取工程师信息出错', error)
        this.error = '获取工程师信息失败，请检查网络连接'
      } finally {
        this.loading = false
      }
    },
    
    async fetchRelatedTasks() {
      try {
        // 这里应该调用获取当前用户任务列表的API
        // 暂时使用模拟数据
        const response = await fetch('/api/v1/tasks?isRelated=true')
        const data = await response.json()
        
        if (data.code === 200) {
          this.relatedTasks = data.data.list || []
        } else {
          console.error('获取相关任务失败', data.message)
        }
      } catch (error) {
        console.error('获取相关任务出错', error)
      }
    },
    
    async submitRequest() {
      if (!this.isFormValid || this.submitting) return
      
      this.submitting = true
      
      try {
        const data = {
          title: `协助请求: ${this.requestData.urgency === 'high' ? '[紧急] ' : ''}${this.requestData.description.substring(0, 20)}...`,
          content: this.requestData.description,
          type: this.requestData.urgency,
          taskId: this.requestData.taskId || null,
          recipientIds: [parseInt(this.engineerId)]
        }
        
        const response = await createAssistanceRequest(data)
        
        if (response.code === 200) {
          alert('协助请求已发送')
          this.goBack()
        } else {
          alert(response.message || '提交请求失败')
        }
      } catch (error) {
        console.error('提交协助请求出错', error)
        alert('提交请求失败，请重试')
      } finally {
        this.submitting = false
      }
    },
    
    goBack() {
      this.$router.back()
    }
  }
}
</script>

<style scoped>
.request-assistance-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
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
  text-align: center;
}

.header-right {
  width: 24px;
}

.form-container {
  padding: 16px;
}

.engineer-info {
  margin-bottom: 24px;
}

.engineer-info h2 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
}

.engineer-card {
  background-color: #fff;
  padding: 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.engineer-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  margin-right: 16px;
  object-fit: cover;
}

.engineer-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 4px;
}

.engineer-task-count {
  font-size: 14px;
  color: #6b7280;
}

.request-form {
  background-color: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #374151;
}

.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 16px;
}

.textarea {
  resize: none;
}

.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  border-top: 1px solid #e5e7eb;
  padding: 16px;
  padding-bottom: calc(16px + 56px); /* 增加底部padding，防止被导航栏遮挡 */
}

.submit-btn {
  width: 100%;
  padding: 12px;
  background-color: #3b82f6;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.loading,
.error-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px;
  text-align: center;
}

.spinner {
  border: 3px solid rgba(0, 0, 0, 0.1);
  border-top: 3px solid #3498db;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message button {
  margin-top: 16px;
  padding: 8px 16px;
  border: none;
  background-color: #3498db;
  color: white;
  border-radius: 4px;
}
</style> 