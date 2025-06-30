<template>
  <div class="sales-info-card">
    <h3 class="section-title">
      <i class="icon-handshake"></i>
      销售信息
    </h3>
    
    <div class="sales-content">
      <div class="sales-header">
        <div class="avatar-container">
          <img 
            v-if="salesAvatar" 
            :src="salesAvatar" 
            :alt="salesName" 
            class="sales-avatar"
          >
          <div v-else class="sales-avatar-placeholder">
            {{ getInitials(salesName) }}
          </div>
        </div>
        
        <div class="sales-name-container">
          <h4 class="sales-name">{{ salesName || '未知销售' }}</h4>
          <p v-if="salesDepartment" class="sales-department">{{ salesDepartment }}</p>
        </div>
        
        <div class="sales-actions">
          <a :href="`tel:${salesContact}`" class="action-button call" v-if="salesContact">
            <i class="icon-phone"></i>
          </a>
          <button class="action-button message" @click="sendMessage" v-if="salesId">
            <i class="icon-comment"></i>
          </button>
        </div>
      </div>
      
      <!-- 销售联系信息 -->
      <div class="sales-contact-info" v-if="salesContact">
        <div class="contact-item">
          <i class="icon-phone"></i>
          <div class="contact-details">
            <span class="contact-label">联系电话</span>
            <span class="contact-value">{{ salesContact }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TaskSalesInfoCard',
  props: {
    salesId: {
      type: [Number, String],
      required: true
    },
    salesName: {
      type: String,
      default: ''
    },
    salesContact: {
      type: String,
      default: ''
    },
    salesAvatar: {
      type: String,
      default: ''
    },
    salesDepartment: {
      type: String,
      default: ''
    }
  },
  setup(props) {
    // 获取名字首字母，用于默认头像
    const getInitials = (name) => {
      if (!name) return '?'
      return name.split(' ')
        .map(part => part[0])
        .join('')
        .toUpperCase()
        .substring(0, 2)
    }
    
    // 发送消息
    const sendMessage = () => {
      if (!props.salesId) return
      
      // 这里可以跳转到聊天页面或打开聊天窗口
      console.log('发送消息给销售:', props.salesName)
    }
    
    return {
      getInitials,
      sendMessage
    }
  }
}
</script>

<style scoped>
.sales-info-card {
  background-color: #fff;
  margin: 16px 12px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  animation: slideInUp 0.4s ease;
  animation-delay: 0.2s;
}

@keyframes slideInUp {
  from { 
    opacity: 0;
    transform: translateY(20px);
  }
  to { 
    opacity: 1;
    transform: translateY(0);
  }
}

.section-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin: 0;
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
}

.section-title i {
  font-size: 18px;
  margin-right: 8px;
  color: #f59e0b;
}

.sales-content {
  padding: 0;
}

.sales-header {
  display: flex;
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
}

.avatar-container {
  margin-right: 16px;
}

.sales-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e5e7eb;
}

.sales-avatar-placeholder {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #fef3c7;
  color: #92400e;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
  border: 2px solid #e5e7eb;
}

.sales-name-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.sales-name {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 4px 0;
  color: #111827;
}

.sales-department {
  font-size: 13px;
  color: #6b7280;
  margin: 0;
}

.sales-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-button {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  border: none;
  background-color: #f3f4f6;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #4b5563;
  text-decoration: none;
}

.action-button:hover {
  background-color: #e5e7eb;
}

.action-button.call {
  background-color: #fef3c7;
  color: #92400e;
}

.action-button.call:hover {
  background-color: #fde68a;
}

.action-button.message {
  background-color: #dbeafe;
  color: #1e40af;
}

.action-button.message:hover {
  background-color: #bfdbfe;
}

.sales-contact-info {
  padding: 16px;
}

.contact-item {
  display: flex;
  align-items: flex-start;
}

.contact-item i {
  width: 24px;
  height: 24px;
  margin-right: 12px;
  color: #6b7280;
  display: flex;
  align-items: center;
  justify-content: center;
}

.contact-details {
  display: flex;
  flex-direction: column;
}

.contact-label {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 2px;
}

.contact-value {
  font-size: 14px;
  color: #374151;
}
</style> 