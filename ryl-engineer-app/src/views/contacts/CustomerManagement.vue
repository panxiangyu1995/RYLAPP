<template>
  <div class="customer-management-page">
    <!-- 搜索栏 -->
    <div class="search-container">
      <div class="search-bar">
        <i class="icon-search"></i>
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="搜索客户名称、联系人..."
          @input="searchCustomers"
        >
        <i v-if="searchQuery" class="icon-times" @click="clearSearch"></i>
      </div>
      <div class="filter-buttons">
        <button 
          v-for="(filter, index) in filters" 
          :key="index"
          :class="['filter-btn', { active: activeFilter === filter.value }]"
          @click="setFilter(filter.value)"
        >
          {{ filter.name }}
        </button>
      </div>
    </div>

    <!-- 客户可视范围提示 (仅对非管理员显示) -->
    <div v-if="!isAdmin && customerScopeMessage" class="scope-message">
      <i class="icon-info-circle"></i>
      <span>{{ customerScopeMessage }}</span>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 客户列表 -->
    <div v-else-if="filteredCustomers.length > 0" class="customer-list">
      <div 
        v-for="customer in filteredCustomers" 
        :key="customer.id"
        class="customer-card"
        @click="viewCustomerDetail(customer.id)"
      >
        <div class="customer-info">
          <h3 class="customer-name">{{ customer.companyName }}</h3>
          <div class="contact-info">
            <p class="contact-name">联系人：{{ customer.contactName }}</p>
            <p class="contact-phone">电话：{{ customer.phone }}</p>
          </div>
          <p class="address">{{ customer.address }}</p>
        </div>
        <div class="customer-right">
          <span :class="['customer-level', `level-${customer.level}`]">{{ customer.level }}级</span>
          <div class="action-buttons">
            <button class="action-btn edit-btn" @click.stop="editCustomer(customer.id)">
              <i class="icon-edit"></i>
            </button>
            <button class="action-btn task-btn" @click.stop="createTask(customer.id)">
              <i class="icon-file-alt"></i>
            </button>
            <button v-if="isAdmin || isSales" class="action-btn delete-btn" @click.stop="confirmDelete(customer)">
              <i class="icon-trash"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 无数据状态 -->
    <div v-else class="no-data-container">
      <i class="icon-info-circle"></i>
      <p>{{ searchQuery ? '没有找到匹配的客户' : '暂无客户数据' }}</p>
    </div>

    <!-- 底部添加按钮 (仅对管理员和客户经理显示) -->
    <div v-if="isAdmin || isSales" class="fab-container">
      <router-link to="/customer/create" class="fab-button">
        <i class="icon-plus"></i>
      </router-link>
    </div>

    <!-- 删除确认对话框 -->
    <confirm-delete-dialog
      v-model:visible="deleteDialogVisible"
      :title="`确认删除客户`"
      :message="`确定要删除客户 ${customerToDelete?.companyName || ''} 吗？此操作不可恢复。`"
      :error-message="deleteError"
      @confirm="handleDeleteConfirm"
      @cancel="cancelDelete"
    />
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { getCustomerList, deleteCustomerWithConfirm } from '../../api/customer'
import ConfirmDeleteDialog from '../../components/ConfirmDeleteDialog.vue'

export default {
  name: 'CustomerManagementPage',
  components: {
    ConfirmDeleteDialog
  },
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const customers = ref([])
    const loading = ref(true)
    const searchQuery = ref('')
    const activeFilter = ref('all')
    const deleteDialogVisible = ref(false)
    const customerToDelete = ref(null)
    const deleteError = ref('')
    
    const filters = [
      { name: '全部', value: 'all' },
      { name: 'A级', value: 'A' },
      { name: 'B级', value: 'B' },
      { name: 'C级', value: 'C' }
    ]

    // 用户角色判断
    const isAdmin = computed(() => authStore.hasRole('ROLE_ADMIN'))
    const isEngineer = computed(() => authStore.hasRole('ROLE_ENGINEER'))
    const isSales = computed(() => authStore.hasRole('ROLE_SALES'))
    
    // 客户可视范围提示信息
    const customerScopeMessage = computed(() => {
      if (isEngineer.value) {
        return '您当前只能看到与您任务相关的客户'
      } else if (isSales.value) {
        return '您当前只能看到您负责的客户'
      }
      return ''
    })
    
    // 设置筛选器
    const setFilter = (filter) => {
      activeFilter.value = filter
    }
    
    // 清空搜索
    const clearSearch = () => {
      searchQuery.value = ''
      searchCustomers()
    }
    
    // 搜索客户
    const searchCustomers = () => {
      // 实际项目中应该调用API进行搜索
      console.log('搜索客户:', searchQuery.value)
    }
    
    // 查看客户详情
    const viewCustomerDetail = (customerId) => {
      router.push(`/customer/${customerId}`)
    }
    
    // 编辑客户
    const editCustomer = (customerId) => {
      router.push(`/customer/${customerId}/edit`)
    }
    
    // 为客户创建任务
    const createTask = (customerId) => {
      router.push(`/task/create?customerId=${customerId}`)
    }
    
    // 确认删除客户
    const confirmDelete = (customer) => {
      customerToDelete.value = customer
      deleteError.value = ''
      deleteDialogVisible.value = true
    }
    
    // 取消删除
    const cancelDelete = () => {
      customerToDelete.value = null
      deleteError.value = ''
    }
    
    // 处理删除确认
    const handleDeleteConfirm = async (password) => {
      if (!customerToDelete.value) return
      
      try {
        const response = await deleteCustomerWithConfirm(customerToDelete.value.id, password)
        
        if (response.code === 200) {
          // 删除成功，从列表中移除该客户
          customers.value = customers.value.filter(c => c.id !== customerToDelete.value.id)
          deleteDialogVisible.value = false
          customerToDelete.value = null
        } else {
          deleteError.value = response.message || '删除失败，请稍后重试'
        }
      } catch (error) {
        console.error('删除客户失败:', error)
        deleteError.value = error.response?.data?.message || '删除失败，请稍后重试'
      }
    }
    
    // 过滤后的客户列表
    const filteredCustomers = computed(() => {
      let result = customers.value
      
      // 按级别筛选
      if (activeFilter.value !== 'all') {
        result = result.filter(customer => customer.level === activeFilter.value)
      }
      
      // 按搜索关键词筛选
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        result = result.filter(customer => 
          customer.companyName.toLowerCase().includes(query) ||
          (customer.contactName && customer.contactName.toLowerCase().includes(query)) ||
          (customer.phone && customer.phone.includes(query)) ||
          (customer.address && customer.address.toLowerCase().includes(query))
        )
      }
      
      return result
    })
    
    // 根据用户角色加载不同的客户数据
    const loadCustomers = async () => {
      loading.value = true
      
      try {
        let params = {}
        
        if (searchQuery.value) {
          params.keyword = searchQuery.value
        }
        
        // 根据用户角色设置不同的API路径
        let apiPath = '/list'
        if (isSales.value && !isAdmin.value) {
          apiPath = '/list/sales'
        } else if (isEngineer.value && !isAdmin.value) {
          apiPath = '/list/engineer'
        }
        
        // 调用API获取客户列表
        const response = await getCustomerList({
          ...params,
          path: apiPath
        })
        
        if (response.code === 200) {
          customers.value = response.data.list || []
        } else {
          console.error('获取客户列表失败:', response.message)
        }
      } catch (error) {
        console.error('加载客户数据失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    onMounted(() => {
      loadCustomers()
    })
    
    return {
      customers,
      loading,
      searchQuery,
      activeFilter,
      filters,
      filteredCustomers,
      isAdmin,
      isEngineer,
      isSales,
      customerScopeMessage,
      deleteDialogVisible,
      customerToDelete,
      deleteError,
      setFilter,
      clearSearch,
      searchCustomers,
      viewCustomerDetail,
      editCustomer,
      createTask,
      confirmDelete,
      cancelDelete,
      handleDeleteConfirm
    }
  }
}
</script>

<style scoped>
.customer-management-page {
  padding-bottom: 80px;
}

.search-container {
  padding: 16px;
  background-color: #fff;
}

.search-bar {
  display: flex;
  align-items: center;
  background-color: #f3f4f6;
  border-radius: 8px;
  padding: 0 12px;
  margin-bottom: 12px;
}

.icon-search::before {
  content: '\f002';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #9ca3af;
  margin-right: 8px;
}

.search-bar input {
  flex: 1;
  border: none;
  background: transparent;
  height: 40px;
  font-size: 14px;
  outline: none;
}

.icon-times::before {
  content: '\f00d';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #9ca3af;
  cursor: pointer;
  margin-left: 8px;
}

.filter-buttons {
  display: flex;
  gap: 24px;
  overflow-x: auto;
}

.filter-btn {
  color: #6b7280;
  background: none;
  border: none;
  padding: 0;
  padding-bottom: 4px;
  white-space: nowrap;
}

.filter-btn.active {
  color: #3b82f6;
  font-weight: 500;
  border-bottom: 2px solid #3b82f6;
}

.scope-message {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  background-color: #e0f2fe;
  color: #0369a1;
  font-size: 13px;
}

.icon-info-circle::before {
  font-family: 'Font Awesome 6 Free';
  content: '\f05a';
  font-weight: 900;
  margin-right: 8px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px;
}

.spinner {
  border: 3px solid #f3f4f6;
  border-top: 3px solid #3b82f6;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  animation: spin 1s linear infinite;
  margin-bottom: 8px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.customer-list {
  padding: 0 16px;
}

.customer-card {
  display: flex;
  justify-content: space-between;
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.customer-info {
  flex: 1;
}

.customer-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
}

.contact-info {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #4b5563;
}

.address {
  font-size: 14px;
  color: #6b7280;
}

.customer-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;
}

.customer-level {
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 12px;
  font-weight: 500;
}

.level-A {
  background-color: #e0f2fe;
  color: #0369a1;
}

.level-B {
  background-color: #f0fdf4;
  color: #166534;
}

.level-C {
  background-color: #fef9c3;
  color: #854d0e;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  cursor: pointer;
}

.edit-btn {
  background-color: #f3f4f6;
  color: #4b5563;
}

.task-btn {
  background-color: #e0f2fe;
  color: #0369a1;
}

.delete-btn {
  background-color: #fee2e2;
  color: #b91c1c;
}

.icon-edit::before {
  content: '\f304';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-file-alt::before {
  content: '\f15c';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-trash::before {
  content: '\f1f8';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.no-data-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 16px;
  text-align: center;
  color: #6b7280;
}

.fab-container {
  position: fixed;
  right: 16px;
  bottom: 72px;
  z-index: 20;
}

.fab-button {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #3b82f6;
  color: #fff;
  border-radius: 50%;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.icon-plus::before {
  content: '\f067';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}
</style> 