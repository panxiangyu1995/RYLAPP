<template>
  <div class="modal-overlay">
    <div class="modal-container">
      <div class="modal-header">
        <h3>删除仓库</h3>
        <button class="close-button" @click="closeModal">&times;</button>
      </div>
      <div class="modal-body">
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label for="warehouseId">选择仓库 <span class="required">*</span></label>
            <select 
              id="warehouseId" 
              v-model="formData.id" 
              class="form-control"
              :class="{ 'is-invalid': errors.id }"
              required
            >
              <option value="">请选择要删除的仓库</option>
              <option 
                v-for="warehouse in warehouses" 
                :key="warehouse.id" 
                :value="warehouse.id"
              >
                {{ warehouse.name }}
              </option>
            </select>
            <div v-if="errors.id" class="error-message">{{ errors.id }}</div>
          </div>

          <div class="form-group">
            <label for="password">删除密码 <span class="required">*</span></label>
            <input 
              type="password" 
              id="password" 
              v-model="formData.password" 
              class="form-control"
              :class="{ 'is-invalid': errors.password }"
              placeholder="输入删除密码"
              required
            />
            <div v-if="errors.password" class="error-message">{{ errors.password }}</div>
          </div>
          
          <div class="warning-message">
            <i class="icon-warning"></i>
            <p>警告：删除仓库操作不可恢复，请确认仓库内没有物品后再删除。</p>
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button class="btn-cancel" @click="closeModal">取消</button>
        <button 
          class="btn-delete" 
          @click="handleSubmit"
          :disabled="isSubmitting"
        >
          {{ isSubmitting ? '删除中...' : '确认删除' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { useWarehouseStore } from '../../stores/warehouse'

export default {
  name: 'WarehouseDeleteModal',
  emits: ['close', 'success'],
  setup(props, { emit }) {
    const warehouseStore = useWarehouseStore()
    
    // 表单数据
    const formData = reactive({
      id: '',
      password: ''
    })
    
    // 错误信息
    const errors = reactive({
      id: '',
      password: ''
    })
    
    // 仓库列表
    const warehouses = computed(() => warehouseStore.warehouses)
    
    // 提交状态
    const isSubmitting = ref(false)
    
    // 关闭弹窗
    const closeModal = () => {
      emit('close')
    }
    
    // 表单验证
    const validateForm = () => {
      let isValid = true
      
      // 重置错误信息
      errors.id = ''
      errors.password = ''
      
      // 验证仓库ID
      if (!formData.id) {
        errors.id = '请选择要删除的仓库'
        isValid = false
      }
      
      // 验证删除密码
      if (!formData.password.trim()) {
        errors.password = '请输入删除密码'
        isValid = false
      }
      
      return isValid
    }
    
    // 提交表单
    const handleSubmit = async () => {
      if (!validateForm()) {
        return
      }
      
      isSubmitting.value = true
      
      try {
        const result = await warehouseStore.deleteWarehouse(formData.id, formData.password)
        
        if (result.success) {
          // 删除成功
          emit('success')
        } else {
          // 显示错误信息
          if (result.error.includes('密码')) {
            errors.password = result.error
          } else if (result.error.includes('存在物品')) {
            errors.id = result.error
          } else {
            alert(result.error || '删除仓库失败')
          }
        }
      } catch (error) {
        console.error('删除仓库错误:', error)
        alert(`删除仓库失败: ${error.message || '未知错误'}`)
      } finally {
        isSubmitting.value = false
      }
    }
    
    return {
      formData,
      errors,
      warehouses,
      isSubmitting,
      closeModal,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 100;
}

.modal-container {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.close-button {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #6b7280;
}

.modal-body {
  padding: 16px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 16px;
  border-top: 1px solid #e5e7eb;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #374151;
}

.required {
  color: #dc2626;
  margin-left: 4px;
}

.form-control {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-control:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.form-control.is-invalid {
  border-color: #dc2626;
}

.error-message {
  margin-top: 4px;
  color: #dc2626;
  font-size: 12px;
}

.warning-message {
  display: flex;
  align-items: flex-start;
  background-color: #fff7ed;
  border: 1px solid #ffedd5;
  border-radius: 6px;
  padding: 12px;
  margin-top: 8px;
}

.icon-warning::before {
  content: '\f071';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #f59e0b;
  margin-right: 8px;
  font-size: 16px;
}

.warning-message p {
  margin: 0;
  color: #9a3412;
  font-size: 14px;
}

.btn-cancel {
  padding: 8px 16px;
  background-color: #f3f4f6;
  color: #4b5563;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.btn-delete {
  padding: 8px 16px;
  background-color: #ef4444;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.btn-delete:disabled {
  background-color: #fca5a5;
  cursor: not-allowed;
}
</style> 