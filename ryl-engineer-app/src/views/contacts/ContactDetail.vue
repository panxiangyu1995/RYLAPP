<template>
  <div class="contact-detail">
    <header class="header">
      <div class="left">
        <div class="back-button" @click="goBack">
          <i class="material-icons">arrow_back</i>
        </div>
        <h1>联系人详情</h1>
      </div>
    </header>
    
    <div class="loading" v-if="loading">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>
    
    <div class="error-message" v-else-if="error">
      <i class="material-icons">error</i>
      <p>{{ error }}</p>
    </div>
    
    <template v-else>
      <div class="profile-header">
        <div class="avatar-container">
          <img :src="contact.avatar || '/default-avatar.png'" alt="头像" class="avatar" />
          <div class="status-indicator" :class="{ online: contact.status === 1 }"></div>
        </div>
        <h2 class="name">{{ contact.name }}</h2>
        <p class="work-id">工号：{{ contact.workId }}</p>
        <p class="department">{{ contact.department }}</p>
        <div class="status">
          {{ contact.status === 1 ? '在线' : '离线' }}
          <span v-if="contact.status === 0 && contact.lastActiveTime">
            - 最后活跃于 {{ formatDate(contact.lastActiveTime) }}
          </span>
        </div>
      </div>
      
      <div class="action-buttons">
        <div class="action" @click="callContact">
          <i class="material-icons">phone</i>
          <span>电话</span>
        </div>
        <div class="action" @click="messageContact">
          <i class="material-icons">message</i>
          <span>消息</span>
        </div>
        <div class="action" v-if="isEngineer" @click="requestAssistance">
          <i class="material-icons">help_outline</i>
          <span>协助</span>
        </div>
      </div>
      
      <div class="contact-info-section">
        <h3>联系信息</h3>
        <div class="info-item">
          <i class="material-icons">phone</i>
          <div class="info-content">
            <div class="label">手机号</div>
            <div class="value">{{ contact.mobile || '未填写' }}</div>
          </div>
        </div>
        <div class="info-item">
          <i class="material-icons">domain</i>
          <div class="info-content">
            <div class="label">部门</div>
            <div class="value">{{ contact.department || '未填写' }}</div>
          </div>
        </div>
        <div class="info-item">
          <i class="material-icons">assignment_ind</i>
          <div class="info-content">
            <div class="label">角色</div>
            <div class="value">{{ contact.role || '未填写' }}</div>
          </div>
        </div>
      </div>
      
      <div class="tasks-section" v-if="contact.currentTasks && contact.currentTasks.length > 0">
        <h3>当前任务</h3>
        <div 
          class="task-item" 
          v-for="task in contact.currentTasks" 
          :key="task.taskId"
          @click="viewTask(task.taskId)"
        >
          <div class="task-info">
            <div class="task-title">{{ task.title }}</div>
            <div class="task-id">{{ task.taskId }}</div>
          </div>
          <div class="task-status" :class="getStatusClass(task.status)">
            {{ getStatusText(task.status) }}
          </div>
          <i class="material-icons">chevron_right</i>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import { getContactDetail } from '@/api/contacts'

export default {
  name: 'ContactDetail',
  data() {
    return {
      contact: {},
      loading: true,
      error: null
    }
  },
  computed: {
    contactId() {
      return parseInt(this.$route.params.id)
    },
    isEngineer() {
      return this.contact.role === '工程师'
    }
  },
  created() {
    this.fetchContactDetail()
  },
  methods: {
    async fetchContactDetail() {
      this.loading = true
      this.error = null
      
      try {
        console.log(`请求联系人详情，ID: ${this.contactId}`)
        const response = await getContactDetail(this.contactId)
        
        if (response.code === 200 && response.data) {
          this.contact = response.data
          console.log('获取联系人详情成功:', this.contact)
          
          // 如果没有role字段，根据类型设置
          const type = this.$route.query.type
          if (!this.contact.role) {
            if (type === 'engineer') {
              this.contact.role = '工程师'
            } else if (type === 'customer') {
              this.contact.role = '客户'
            } else {
              this.contact.role = '其它联系人'
            }
          }
          
          // 确保lastActiveTime字段格式正确
          if (this.contact.lastActiveTime && typeof this.contact.lastActiveTime === 'string') {
            this.contact.lastActiveTime = new Date(this.contact.lastActiveTime)
          }
          
          console.log('处理后的联系人详情数据:', this.contact)
        } else {
          this.error = response.message || '获取联系人详情失败'
          console.error('获取联系人详情失败:', response)
        }
      } catch (error) {
        console.error('获取联系人详情请求异常:', error)
        
        // 详细错误信息记录
        if (error.status) {
          console.error(`HTTP错误: ${error.status}`, error.message)
          this.error = `获取联系人详情失败: ${error.message}`
        } else if (error.isNetworkError) {
          console.error('网络连接错误')
          this.error = '网络连接错误，请检查网络'
        } else if (error.isTimeout) {
          console.error('请求超时')
          this.error = '请求超时，服务器响应时间过长'
        } else {
          this.error = '获取联系人详情失败，请稍后重试'
        }
      } finally {
        this.loading = false
      }
    },
    formatDate(dateString) {
      if (!dateString) return ''
      
      const date = new Date(dateString)
      return date.toLocaleString()
    },
    callContact() {
      if (this.contact.mobile) {
        window.location.href = `tel:${this.contact.mobile}`
      } else {
        alert('没有该联系人的电话号码')
      }
    },
    messageContact() {
      this.$router.push(`/chat/new?contactId=${this.contact.id}`)
    },
    requestAssistance() {
      if (this.isEngineer) {
        this.$router.push(`/request-assistance/${this.contact.id}`)
      }
    },
    getStatusClass(status) {
      switch (status) {
        case 'pending':
          return 'status-pending'
        case 'in_progress':
          return 'status-in-progress'
        case 'completed':
          return 'status-completed'
        default:
          return ''
      }
    },
    getStatusText(status) {
      switch (status) {
        case 'pending':
          return '待处理'
        case 'in_progress':
          return '进行中'
        case 'completed':
          return '已完成'
        default:
          return status
      }
    },
    viewTask(taskId) {
      this.$router.push(`/task/${taskId}`)
    },
    goBack() {
      // 获取来源类型（其他联系人/工程师等）
      const type = this.$route.query.type
      
      if (type === 'other') {
        this.$router.push('/contacts/others')
      } else if (type === 'engineer') {
        this.$router.push('/contacts/engineers')
      } else if (type === 'customer') {
        this.$router.push('/contacts/customers')
      } else {
        this.$router.go(-1) // 默认返回上一页
      }
    }
  }
}
</script>

<style scoped>
.contact-detail {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background-color: #2196f3;
  color: white;
  padding: 10px 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 56px;
}

.left {
  display: flex;
  align-items: center;
}

.left i {
  margin-right: 16px;
}

.back-button {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.loading,
.error-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px;
  text-align: center;
}

.loading .spinner {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #2196f3;
  animation: spin 1.5s linear infinite;
  margin-bottom: 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message i {
  font-size: 48px;
  color: #9e9e9e;
  margin-bottom: 10px;
}

.error-message p {
  color: #f44336;
}

.profile-header {
  background-color: white;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 10px;
}

.avatar-container {
  position: relative;
  margin-bottom: 15px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.status-indicator {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background-color: #9e9e9e;
  border: 2px solid white;
}

.status-indicator.online {
  background-color: #4caf50;
}

.name {
  margin: 0 0 5px;
  font-size: 20px;
}

.work-id {
  margin: 0 0 5px;
  font-size: 14px;
  color: #757575;
}

.department, .status {
  margin: 0;
  font-size: 14px;
  color: #757575;
}

.action-buttons {
  display: flex;
  background-color: white;
  padding: 15px;
  margin-bottom: 10px;
}

.action {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #2196f3;
}

.action i {
  font-size: 24px;
  margin-bottom: 5px;
}

.contact-info-section {
  background-color: white;
  padding: 15px;
  margin-bottom: 10px;
}

.contact-info-section h3 {
  margin: 0 0 15px;
  font-size: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-item i {
  color: #757575;
  margin-right: 15px;
}

.info-content {
  flex: 1;
}

.label {
  font-size: 12px;
  color: #757575;
  margin-bottom: 2px;
}

.value {
  font-size: 16px;
}

.tasks-section {
  background-color: white;
  padding: 15px;
}

.tasks-section h3 {
  margin: 0 0 15px;
  font-size: 16px;
}

.task-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.task-item:last-child {
  border-bottom: none;
}

.task-info {
  flex: 1;
}

.task-title {
  font-size: 16px;
  margin-bottom: 2px;
}

.task-id {
  font-size: 12px;
  color: #757575;
}

.task-status {
  padding: 4px 8px;
  border-radius: 10px;
  font-size: 12px;
  margin: 0 10px;
  text-align: center;
  min-width: 60px;
}

.status-pending {
  background-color: #ffecb3;
  color: #ff6f00;
}

.status-in-progress {
  background-color: #e3f2fd;
  color: #1976d2;
}

.status-completed {
  background-color: #e8f5e9;
  color: #2e7d32;
}
</style> 