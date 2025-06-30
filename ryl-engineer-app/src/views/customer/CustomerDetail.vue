<template>
  <div class="customer-info-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>客户信息</h1>
      <div class="header-right" v-if="isAdmin || isSales">
        <div class="header-dropdown" @click="toggleDropdown">
          <i class="icon-more"></i>
          <div class="dropdown-menu" v-if="showDropdown">
            <div class="dropdown-item" @click="editCustomer">
              <i class="icon-edit"></i>编辑客户
            </div>
            <div class="dropdown-item danger" @click="confirmDelete">
              <i class="icon-trash"></i>删除客户
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- 客户详情卡片 -->
    <div class="customer-card">
      <div class="customer-info">
        <img :src="customer.image" alt="客户图片" class="customer-image">
        <div class="customer-details">
          <div class="customer-header">
            <h2 class="customer-name">{{ customer.name }}</h2>
            <span :class="['customer-level', `level-${customer.level}`]">{{ customer.level }}级</span>
          </div>
          <p class="customer-type">{{ customer.type }}</p>
          <div class="customer-actions">
            <button class="action-btn primary" @click="contactCustomer">
              <i class="icon-phone"></i>联系客户
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 客户等级说明 -->
    <div class="info-section">
      <h3 class="section-title">客户等级：{{ customer.level }}级</h3>
      <div class="level-indicator">
        <div :class="['level-dot', `level-${customer.level}`]"></div>
        <span class="level-description">{{ customer.levelDescription }}</span>
      </div>
      <p class="level-requirements">
        需求响应时间：{{ customer.responseTime }}<br>
        上门服务时间：{{ customer.visitTime }}<br>
        维修完成时限：{{ customer.completionTime }}
      </p>
      <div class="level-note">
        {{ customer.levelNote }}
      </div>
    </div>

    <!-- 客户基本信息 -->
    <div class="info-section">
      <h3 class="section-title">基本信息</h3>
      <div class="info-list">
        <div class="info-item" v-for="(item, index) in customer.basicInfo" :key="index">
          <span class="info-label">{{ item.label }}</span>
          <span class="info-value">{{ item.value }}</span>
        </div>
      </div>
    </div>

    <!-- 设备列表 -->
    <div class="info-section">
      <h3 class="section-title">已有设备</h3>
      <div class="device-list">
        <div 
          v-for="(device, index) in customer.devices" 
          :key="index"
          class="device-item"
        >
          <i :class="['device-icon', device.icon]"></i>
          <div class="device-details">
            <div class="device-header">
              <p class="device-name">{{ device.name }}</p>
              <span :class="['device-status', device.active ? 'active' : 'inactive']">
                {{ device.active ? '活跃' : '闲置' }}
              </span>
            </div>
            <p class="device-serial">序列号: {{ device.serialNumber }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 服务历史摘要 -->
    <div class="info-section">
      <div class="section-header">
        <h3 class="section-title">服务历史</h3>
        <button class="view-all-btn" @click="viewAllHistory">查看全部</button>
      </div>
      <div class="history-list">
        <div 
          v-for="(history, index) in customer.serviceHistory" 
          :key="index"
          class="history-item"
        >
          <div class="history-dot"></div>
          <div class="history-details">
            <p class="history-title">{{ history.title }}</p>
            <p class="history-info">{{ history.description }} | {{ history.date }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="bottom-actions">
      <button class="bottom-btn primary" @click="createNewTask">
        <i class="icon-plus"></i>新建任务
      </button>
    </div>

    <!-- 删除确认对话框 -->
    <confirm-delete-dialog
      v-model:visible="deleteDialogVisible"
      :title="`确认删除客户`"
      :message="`确定要删除客户 ${customer.name || ''} 吗？此操作不可恢复。`"
      :error-message="deleteError"
      @confirm="handleDeleteConfirm"
      @cancel="cancelDelete"
    />
  </div>
</template>

<script>
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { getCustomerDetail, deleteCustomerWithConfirm } from '../../api/customer'
import ConfirmDeleteDialog from '../../components/ConfirmDeleteDialog.vue'
import { computed } from 'vue'

export default {
  name: 'CustomerInfoPage',
  components: {
    ConfirmDeleteDialog
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const authStore = useAuthStore()

    const goBack = () => {
      router.back()
    }

    // 用户角色判断
    const isAdmin = computed(() => authStore.hasRole('ROLE_ADMIN'))
    const isSales = computed(() => authStore.hasRole('ROLE_SALES'))

    return {
      goBack,
      isAdmin,
      isSales
    }
  },
  data() {
    return {
      customerId: null,
      showDropdown: false,
      deleteDialogVisible: false,
      deleteError: '',
      customer: {
        id: 1,
        name: '上海某研究所',
        image: 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?ixlib=rb-1.2.1&auto=format&fit=crop&w=80&h=80&q=80',
        level: 'A',
        type: '科研仪器类',
        levelDescription: '战略合作伙伴，最高优先级',
        responseTime: '2小时内',
        visitTime: '当天',
        completionTime: '3个工作日内',
        levelNote: 'A级客户为公司重要战略合作伙伴，请务必确保最高质量的服务和最快的响应速度。任何延误需向主管汇报。',
        basicInfo: [
          { label: '联系人', value: '张教授' },
          { label: '联系电话', value: '13800138000' },
          { label: '邮箱', value: 'zhang@research.edu.cn' },
          { label: '地址', value: '上海市浦东新区张江高科技园区科苑路88号' },
          { label: '合作开始', value: '2018-05-12' }
        ],
        devices: [
          {
            name: 'Agilent 7890B 气相色谱仪',
            serialNumber: 'CN12345678',
            active: true,
            icon: 'icon-microscope'
          },
          {
            name: 'Waters Alliance HPLC系统',
            serialNumber: 'WAT987654321',
            active: true,
            icon: 'icon-vial'
          },
          {
            name: 'Thermo Q Exactive 质谱仪',
            serialNumber: 'TH20220156',
            active: false,
            icon: 'icon-atom'
          }
        ],
        serviceHistory: [
          {
            title: '气相色谱仪维修',
            description: '电源模块故障',
            date: '2024-03-15'
          },
          {
            title: 'HPLC系统季度保养',
            description: '常规保养',
            date: '2024-01-10'
          },
          {
            title: '质谱仪进样系统检修',
            description: '进样口堵塞',
            date: '2023-11-25'
          }
        ]
      }
    }
  },
  created() {
    // 从路由参数获取客户ID
    this.customerId = this.$route.params.id;
    console.log('加载客户ID:', this.customerId);
    // 这里可以添加根据ID获取客户信息的API调用
    this.loadCustomerData(this.customerId);
  },
  methods: {
    loadCustomerData(id) {
      // 模拟API调用，获取客户数据
      console.log('加载客户数据，ID:', id);
      // 实际项目中，这里应该调用API获取客户数据
      // getCustomerDetail(id).then(response => {
      //   if (response.code === 200) {
      //     this.customer = response.data;
      //   }
      // }).catch(error => {
      //   console.error('获取客户详情失败:', error);
      // });
    },
    viewAllHistory() {
      console.log('查看全部服务历史')
      // 后续实现查看全部服务历史功能
    },
    createNewTask() {
      console.log('创建新任务')
      // 跳转到创建任务页面
      this.$router.push(`/task/create?customerId=${this.customerId}`)
    },
    contactCustomer() {
      if (this.customer.basicInfo && this.customer.basicInfo.length > 0) {
        const phone = this.customer.basicInfo.find(item => item.label === '联系电话')?.value;
        if (phone) {
          window.location.href = `tel:${phone}`;
        }
      }
    },
    toggleDropdown() {
      this.showDropdown = !this.showDropdown;
    },
    editCustomer() {
      this.showDropdown = false;
      this.$router.push(`/customer/${this.customerId}/edit`);
    },
    confirmDelete() {
      this.showDropdown = false;
      this.deleteError = '';
      this.deleteDialogVisible = true;
    },
    cancelDelete() {
      this.deleteError = '';
    },
    handleDeleteConfirm(password) {
      deleteCustomerWithConfirm(this.customerId, password)
        .then(response => {
          if (response.code === 200) {
            // 删除成功，返回客户列表页面
            this.$router.push('/contacts/customers');
          } else {
            this.deleteError = response.message || '删除失败，请稍后重试';
          }
        })
        .catch(error => {
          console.error('删除客户失败:', error);
          this.deleteError = error.response?.data?.message || '删除失败，请稍后重试';
        });
    }
  }
}
</script>

<style scoped>
.customer-info-page {
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
}

.header-right {
  width: 24px;
  position: relative;
}

.header-dropdown {
  position: relative;
}

.icon-more::before {
  content: '\f142';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
  cursor: pointer;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  background-color: #fff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  width: 150px;
  z-index: 100;
}

.dropdown-item {
  padding: 12px 16px;
  display: flex;
  align-items: center;
  font-size: 14px;
  cursor: pointer;
}

.dropdown-item:hover {
  background-color: #f9fafb;
}

.dropdown-item.danger {
  color: #ef4444;
}

.icon-edit::before {
  content: '\f304';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 8px;
}

.icon-trash::before {
  content: '\f1f8';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 8px;
}

.customer-card {
  background-color: #fff;
  margin-top: 8px;
  padding: 16px;
}

.customer-info {
  display: flex;
}

.customer-image {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  object-fit: cover;
}

.customer-details {
  flex: 1;
  margin-left: 16px;
}

.customer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.customer-name {
  font-size: 20px;
  font-weight: 700;
}

.customer-level {
  padding: 4px 12px;
  border-radius: 9999px;
  font-size: 14px;
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

.customer-type {
  color: #6b7280;
  margin-top: 4px;
}

.customer-actions {
  display: flex;
  margin-top: 8px;
  gap: 8px;
}

.action-btn {
  display: flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 9999px;
  font-size: 14px;
  border: none;
  cursor: pointer;
}

.action-btn.primary {
  background-color: #dbeafe;
  color: #3b82f6;
}

.action-btn.secondary {
  background-color: #f3f4f6;
  color: #6b7280;
}

.icon-phone::before {
  content: '\f095';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 4px;
}

.icon-history::before {
  content: '\f1da';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 4px;
}

.info-section {
  background-color: #fff;
  margin-top: 8px;
  padding: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.view-all-btn {
  color: #3b82f6;
  font-size: 14px;
  background: none;
  border: none;
}

.level-indicator {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.level-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  margin-right: 8px;
}

.level-dot.level-A {
  background-color: #dc2626;
}

.level-dot.level-B {
  background-color: #d97706;
}

.level-dot.level-C {
  background-color: #2563eb;
}

.level-description {
  font-size: 14px;
  color: #374151;
}

.level-requirements {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
  margin-bottom: 12px;
}

.level-note {
  background-color: #f9fafb;
  padding: 12px;
  border-radius: 8px;
  font-size: 14px;
  color: #6b7280;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
}

.info-label {
  width: 96px;
  color: #6b7280;
  font-size: 14px;
}

.info-value {
  flex: 1;
  color: #374151;
  font-size: 14px;
}

.device-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.device-item {
  display: flex;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px solid #f3f4f6;
}

.device-item:last-child {
  padding-bottom: 0;
  border-bottom: none;
}

.device-icon {
  width: 32px;
  font-size: 20px;
  color: #9ca3af;
  text-align: center;
}

.icon-microscope::before {
  content: '\f610';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-vial::before {
  content: '\f492';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-atom::before {
  content: '\f5d2';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.device-details {
  flex: 1;
  margin-left: 12px;
}

.device-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.device-name {
  font-weight: 500;
}

.device-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}

.device-status.active {
  background-color: #dbeafe;
  color: #3b82f6;
}

.device-status.inactive {
  background-color: #f3f4f6;
  color: #6b7280;
}

.device-serial {
  font-size: 14px;
  color: #6b7280;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.history-item {
  display: flex;
}

.history-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #3b82f6;
  margin-top: 8px;
  flex-shrink: 0;
}

.history-details {
  margin-left: 12px;
}

.history-title {
  color: #374151;
}

.history-info {
  font-size: 14px;
  color: #6b7280;
  margin-top: 4px;
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
  display: flex;
  gap: 16px;
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

.bottom-btn.secondary {
  background-color: #f3f4f6;
  color: #6b7280;
}

.icon-plus::before {
  content: '\f067';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 8px;
}
</style> 