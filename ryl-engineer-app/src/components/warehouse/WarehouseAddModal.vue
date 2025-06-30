<template>
  <div class="modal-overlay">
    <div class="modal-container">
      <div class="modal-header">
        <h3>新增仓库</h3>
        <button class="close-button" @click="closeModal">&times;</button>
      </div>
      <div class="modal-body">
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label for="name">仓库名称 <span class="required">*</span></label>
            <input 
              type="text" 
              id="name" 
              v-model="formData.name" 
              class="form-control"
              :class="{ 'is-invalid': errors.name }"
              placeholder="输入仓库名称"
              required
            />
            <div v-if="errors.name" class="error-message">{{ errors.name }}</div>
          </div>

          <div class="form-group">
            <label for="description">仓库描述</label>
            <textarea
              id="description" 
              v-model="formData.description" 
              class="form-control"
              placeholder="输入仓库描述信息（可选）"
              rows="3"
            ></textarea>
          </div>

          <div class="form-group">
            <label>仓库状态</label>
            <div class="radio-group">
              <label class="radio-label">
                <input type="radio" v-model="formData.status" :value="1" /> 
                <span>启用</span>
              </label>
              <label class="radio-label">
                <input type="radio" v-model="formData.status" :value="0" /> 
                <span>禁用</span>
              </label>
            </div>
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button class="btn-cancel" @click="closeModal">取消</button>
        <button 
          class="btn-submit" 
          @click="handleSubmit"
          :disabled="isSubmitting"
        >
          {{ isSubmitting ? '提交中...' : '提交' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { addWarehouse } from '../../api/warehouse'

export default {
  name: 'WarehouseAddModal',
  emits: ['close', 'success'],
  setup(props, { emit }) {
    // 表单数据
    const formData = reactive({
      name: '',
      description: '',
      status: 1 // 默认启用
    })
    
    // 错误信息
    const errors = reactive({
      name: ''
    })
    
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
      errors.name = ''
      
      // 验证仓库名称
      if (!formData.name.trim()) {
        errors.name = '请输入仓库名称'
        isValid = false
      } else if (formData.name.length > 50) {
        errors.name = '仓库名称不能超过50个字符'
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
        // 构造请求数据
        const warehouseData = {
          name: formData.name.trim(),
          description: formData.description.trim(),
          status: formData.status
        }
        
        // 调用API添加仓库
        const response = await addWarehouse(warehouseData)
        
        if (response.code === 200) {
          // 添加成功
          emit('success', response.data)
        } else {
          alert(`添加仓库失败: ${response.message || '未知错误'}`)
        }
      } catch (error) {
        console.error('添加仓库错误:', error)
        alert(`添加仓库失败: ${error.message || '未知错误'}`)
      } finally {
        isSubmitting.value = false
      }
    }
    
    return {
      formData,
      errors,
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

.radio-group {
  display: flex;
  gap: 16px;
}

.radio-label {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.radio-label input {
  margin-right: 6px;
}

.btn-cancel {
  background-color: #f3f4f6;
  color: #4b5563;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.btn-submit {
  background-color: #3b82f6;
  color: #fff;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.btn-submit:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}
</style> 