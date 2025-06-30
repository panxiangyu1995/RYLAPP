<template>
  <div class="create-customer-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>创建新客户</h1>
      <div class="header-right">
        <button class="save-btn" @click="saveCustomer">
          保存
        </button>
      </div>
    </header>

    <!-- 表单内容 -->
    <div class="form-container">
      <form @submit.prevent="submitCustomer">
        <!-- 客户基本信息 -->
        <div class="form-section">
          <h3 class="section-title">基本信息</h3>
          
          <div class="form-group">
            <label>公司名称</label>
            <input 
              type="text" 
              v-model="form.companyName" 
              placeholder="输入公司名称"
              required
            >
          </div>
          
          <div class="form-group">
            <label>客户等级</label>
            <div class="customer-level-selector">
              <div 
                v-for="(level, index) in customerLevels" 
                :key="index"
                :class="['level-item', { active: form.level === level.value }]"
                @click="form.level = level.value"
              >
                <span class="level-name" :class="`level-${level.value}`">{{ level.name }}</span>
                <span class="level-description">{{ level.description }}</span>
              </div>
            </div>
          </div>
          
          <div class="form-group">
            <label>联系地址</label>
            <input 
              type="text" 
              v-model="form.address" 
              placeholder="输入详细地址"
              required
            >
          </div>
        </div>

        <!-- 联系人信息 -->
        <div class="form-section">
          <h3 class="section-title">联系人信息</h3>
          
          <div class="form-group">
            <label>姓名</label>
            <input 
              type="text" 
              v-model="form.contactName" 
              placeholder="输入联系人姓名"
              required
            >
          </div>
          
          <div class="form-group">
            <label>部门</label>
            <input 
              type="text" 
              v-model="form.department" 
              placeholder="输入所在部门"
            >
          </div>
          
          <div class="form-group">
            <label>职位</label>
            <input 
              type="text" 
              v-model="form.position" 
              placeholder="输入职位"
            >
          </div>
          
          <div class="form-group">
            <label>联系电话</label>
            <input 
              type="tel" 
              v-model="form.phone" 
              placeholder="输入联系电话"
              required
            >
          </div>
          
          <div class="form-group">
            <label>电子邮箱</label>
            <input 
              type="email" 
              v-model="form.email" 
              placeholder="输入电子邮箱"
            >
          </div>
        </div>

        <!-- 备注信息 -->
        <div class="form-section">
          <h3 class="section-title">备注信息</h3>
          
          <div class="form-group">
            <textarea 
              v-model="form.notes" 
              placeholder="输入客户备注信息..."
              rows="4"
            ></textarea>
          </div>
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <button type="button" class="cancel-btn" @click="goBack">取消</button>
          <button type="submit" class="submit-btn">创建客户</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'CreateCustomerPage',
  setup() {
    const router = useRouter()
    
    const form = ref({
      companyName: '',
      level: 'B', // 默认B级客户
      address: '',
      contactName: '',
      department: '',
      position: '',
      phone: '',
      email: '',
      notes: ''
    })
    
    const customerLevels = [
      { 
        name: 'A级', 
        value: 'A',
        description: '重要客户，优先响应' 
      },
      { 
        name: 'B级', 
        value: 'B',
        description: '常规客户，标准响应' 
      },
      { 
        name: 'C级', 
        value: 'C',
        description: '一般客户，按序响应' 
      }
    ]
    
    // 返回上一页
    const goBack = () => {
      router.back()
    }
    
    // 保存客户信息（不关闭页面）
    const saveCustomer = async () => {
      if (!validateForm()) return
      
      try {
        // 这里应该调用API保存客户信息
        console.log('保存客户信息:', form.value)
        alert('客户信息已保存')
      } catch (error) {
        console.error('保存客户信息失败:', error)
        alert('保存客户信息失败: ' + error.message)
      }
    }
    
    // 提交客户信息（保存并关闭页面）
    const submitCustomer = async () => {
      if (!validateForm()) return
      
      try {
        // 这里应该调用API保存客户信息
        console.log('提交客户信息:', form.value)
        
        // 创建成功后返回上一页
        alert('客户创建成功')
        router.back()
      } catch (error) {
        console.error('提交客户信息失败:', error)
        alert('提交客户信息失败: ' + error.message)
      }
    }
    
    // 表单验证
    const validateForm = () => {
      if (!form.value.companyName) {
        alert('请输入公司名称')
        return false
      }
      
      if (!form.value.address) {
        alert('请输入联系地址')
        return false
      }
      
      if (!form.value.contactName) {
        alert('请输入联系人姓名')
        return false
      }
      
      if (!form.value.phone) {
        alert('请输入联系电话')
        return false
      }
      
      // 电子邮箱验证
      if (form.value.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.value.email)) {
        alert('请输入有效的电子邮箱')
        return false
      }
      
      return true
    }
    
    return {
      form,
      customerLevels,
      goBack,
      saveCustomer,
      submitCustomer
    }
  }
}
</script>

<style scoped>
.create-customer-page {
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
  cursor: pointer;
}

.icon-arrow-left::before {
  content: '\f060';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
}

.header h1 {
  font-size: 18px;
  font-weight: 600;
  margin-left: 16px;
}

.header-right {
  margin-left: auto;
}

.save-btn {
  padding: 6px 12px;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
}

.form-container {
  padding: 16px;
}

.form-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #333;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #4b5563;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 16px;
}

.form-group textarea {
  resize: vertical;
}

.customer-level-selector {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.level-item {
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

.level-item.active {
  border-color: #3b82f6;
  background-color: #eff6ff;
}

.level-name {
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 4px;
}

.level-A {
  background-color: #fee2e2;
  color: #ef4444;
}

.level-B {
  background-color: #fef3c7;
  color: #d97706;
}

.level-C {
  background-color: #dbeafe;
  color: #2563eb;
}

.level-description {
  font-size: 14px;
  color: #6b7280;
}

.form-actions {
  display: flex;
  gap: 16px;
  margin-top: 24px;
}

.cancel-btn,
.submit-btn {
  flex: 1;
  padding: 12px;
  border-radius: 8px;
  font-size: 16px;
  text-align: center;
  border: none;
}

.cancel-btn {
  background-color: #f3f4f6;
  color: #6b7280;
}

.submit-btn {
  background-color: #3b82f6;
  color: #fff;
}
</style> 