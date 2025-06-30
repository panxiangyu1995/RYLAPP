<template>
  <div class="customer-info-card">
    <h3 class="section-title">
      <i class="icon-user-tie"></i>
      客户信息
    </h3>
    
    <div v-if="loading" class="loading-spinner">
      <div class="spinner-small"></div>
      <span>加载中...</span>
    </div>
    
    <div v-else-if="error" class="error-message">
      <i class="icon-exclamation-triangle"></i>
      <span>{{ error }}</span>
      <button @click="loadCustomerInfo" class="reload-btn">重试</button>
    </div>
    
    <div v-else class="customer-content">
      <!-- 客户简略信息 -->
      <div class="customer-brief-info">
        <div class="customer-header">
          <div class="customer-name">{{ task.customer || task.customerName || '未知客户' }}</div>
          <div v-if="customer.level || task.customerLevel" class="customer-level" :class="`level-${customer.level || task.customerLevel}`">
            {{ customer.level || task.customerLevel }}级
          </div>
        </div>
        <div class="customer-company" v-if="task.customerCompany || customer.company">
          {{ task.customerCompany || customer.company }}
        </div>
      </div>
      
      <!-- 客户基本信息 -->
      <div class="customer-basic-info">
        <div class="info-row" v-if="customer.phone || task.customerPhone">
          <div class="info-label">电话</div>
          <div class="info-value">
            <a :href="`tel:${customer.phone || task.customerPhone}`" class="phone-link">
              {{ customer.phone || task.customerPhone }}
            </a>
          </div>
        </div>
        
        <div class="info-row" v-if="customer.address || task.customerAddress">
          <div class="info-label">地址</div>
          <div class="info-value">{{ customer.address || task.customerAddress }}</div>
        </div>
        
        <div class="info-row" v-if="customer.email || task.customerEmail">
          <div class="info-label">邮箱</div>
          <div class="info-value">{{ customer.email || task.customerEmail }}</div>
        </div>
      </div>
      
      <div class="customer-actions">
        <button 
          v-if="canViewCustomer" 
          class="view-customer-btn"
          @click="viewCustomerDetail"
        >
          查看完整客户资料
          <i class="icon-arrow-right"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import http from '../../../api/http'

export default {
  name: 'TaskCustomerInfoCard',
  props: {
    task: {
      type: Object,
      required: true
    },
    canViewCustomer: {
      type: Boolean,
      default: true
    }
  },
  setup(props) {
    const router = useRouter()
    const customer = ref({})
    const loading = ref(true)
    const error = ref(null)
    
    // 加载客户信息
    const loadCustomerInfo = async () => {
      if (!props.task || !props.task.customerId) {
        loading.value = false
        return
      }
      
      loading.value = true
      error.value = null
      
      try {
        // 从任务中获取客户信息
        if (props.task.customer) {
          customer.value = props.task.customer
          loading.value = false
          return
        }
        
        // 如果任务中没有客户信息，则从API获取
        const customerId = props.task.customerId
        const response = await http.get(`/api/v1/customers/${customerId}`)
        
        if (response && response.code === 200) {
          customer.value = response.data
        } else {
          throw new Error('获取客户信息失败')
        }
      } catch (err) {
        console.error('加载客户信息失败:', err)
        error.value = '获取客户信息失败，请稍后再试'
        
        // 如果API调用失败，尝试使用任务中的客户基本信息
        if (props.task.customerName || props.task.customerContact) {
          customer.value = {
            name: props.task.customerName,
            contact: props.task.customerContact,
            phone: props.task.customerPhone,
            email: props.task.customerEmail,
            address: props.task.customerAddress,
            company: props.task.customerCompany,
            department: props.task.customerDepartment,
            position: props.task.customerPosition,
            level: props.task.customerLevel
          }
        }
      } finally {
        loading.value = false
      }
    }
    
    // 查看客户详情
    const viewCustomerDetail = () => {
      if (!props.canViewCustomer) return
      
      const customerId = props.task.customerId || customer.value.id
      if (customerId) {
        router.push(`/customer/${customerId}`)
      }
    }
    
    // 组件挂载时加载客户信息
    onMounted(() => {
      loadCustomerInfo()
    })
    
    return {
      customer,
      loading,
      error,
      loadCustomerInfo,
      viewCustomerDetail
    }
  }
}
</script>

<style scoped>
.customer-info-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 16px;
  margin: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #f0f0f0;
  border-left: 4px solid #3b82f6; /* 蓝色边框 */
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
}

.section-title i {
  margin-right: 8px;
  color: #3b82f6;
}

.loading-spinner, .error-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px 0;
}

.spinner-small {
  width: 24px;
  height: 24px;
  border: 2px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: #3b82f6;
  animation: spin 1s linear infinite;
  margin-bottom: 8px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  color: #ef4444;
}

.error-message i {
  font-size: 24px;
  margin-bottom: 8px;
}

.reload-btn {
  margin-top: 8px;
  padding: 6px 12px;
  background-color: #f3f4f6;
  color: #4b5563;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.customer-content {
  display: flex;
  flex-direction: column;
}

.customer-brief-info {
  margin-bottom: 16px;
}

.customer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 4px;
}

.customer-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.customer-level {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.level-A {
  background-color: #fee2e2;
  color: #dc2626;
}

.level-B {
  background-color: #fef3c7;
  color: #d97706;
}

.level-C {
  background-color: #dbeafe;
  color: #2563eb;
}

.customer-company {
  font-size: 14px;
  color: #6b7280;
}

.customer-basic-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-row {
  display: flex;
  align-items: flex-start;
}

.info-label {
  width: 60px;
  font-size: 14px;
  color: #6b7280;
  flex-shrink: 0;
}

.info-value {
  flex: 1;
  font-size: 14px;
  color: #111827;
  word-break: break-word;
}

.phone-link {
  color: #2563eb;
  text-decoration: none;
}

.phone-link:hover {
  text-decoration: underline;
}

.customer-actions {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}

.view-customer-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px 16px;
  background-color: #f3f4f6;
  color: #4b5563;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.view-customer-btn:hover {
  background-color: #e5e7eb;
}

.view-customer-btn i {
  margin-left: 6px;
  font-size: 12px;
}
</style> 