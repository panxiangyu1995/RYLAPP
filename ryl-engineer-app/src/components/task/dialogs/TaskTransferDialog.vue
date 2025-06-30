<template>
  <div class="dialog-overlay" v-if="show" @click.self="handleClose">
    <div class="dialog-container">
      <div class="dialog-header">
        <h3 class="dialog-title">任务转出</h3>
        <button class="close-btn" @click="handleClose">
          <i class="icon-times"></i>
        </button>
      </div>
      
      <div class="dialog-body">
        <div class="form-group">
          <label for="engineer-select">转出给工程师:</label>
          <div class="engineer-selector">
            <select 
              id="engineer-select"
              v-model="selectedEngineerId"
              :disabled="loading"
              class="form-control"
            >
              <option value="" disabled selected>-- 请选择工程师 --</option>
              <option 
                v-for="engineer in availableEngineers" 
                :key="engineer.id"
                :value="engineer.id"
              >
                {{ engineer.name }}
              </option>
            </select>
            <div class="selector-icon">
              <i class="icon-chevron-down"></i>
            </div>
          </div>
        </div>
        
        <div class="form-group">
          <label for="transfer-note">转出说明:</label>
          <textarea 
            id="transfer-note"
            v-model="transferNote"
            :disabled="loading"
            placeholder="请说明转出原因（选填）"
            class="form-control"
            rows="4"
          ></textarea>
        </div>
        
        <p class="info-text">
          <i class="icon-info-circle"></i>
          转出后，该任务的所有权将转移给接收工程师，您将不再是任务的主要负责人。
        </p>
        
        <div v-if="error" class="error-message">
          <i class="icon-exclamation-circle"></i>
          {{ error }}
        </div>
      </div>
      
      <div class="dialog-footer">
        <button 
          class="cancel-btn" 
          @click="handleClose"
          :disabled="loading"
        >
          取消
        </button>
        <button 
          class="confirm-btn" 
          @click="handleConfirm"
          :disabled="!isValid || loading"
        >
          <span v-if="loading" class="loading-spinner"></span>
          <span v-else>确认转出</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { getEngineersList } from '@/api/task'

export default {
  name: 'TaskTransferDialog',
  props: {
    show: Boolean,
    task: Object
  },
  emits: ['close', 'transfer'],
  
  setup(props, { emit }) {
    const selectedEngineerId = ref('')
    const transferNote = ref('')
    const availableEngineers = ref([])
    const loading = ref(false)
    const error = ref('')
    
    // 是否表单有效
    const isValid = computed(() => {
      return selectedEngineerId.value && selectedEngineerId.value !== props.task?.ownerId
    })
    
    // 加载工程师列表
    const loadEngineers = async () => {
      try {
        loading.value = true
        error.value = ''
        const response = await getEngineersList({})
        // 筛选掉当前任务拥有者
        availableEngineers.value = response.data.filter(engineer => 
          engineer.id !== props.task?.ownerId
        )
      } catch (err) {
        console.error('加载工程师列表失败:', err)
        error.value = '加载工程师列表失败，请稍后再试'
      } finally {
        loading.value = false
      }
    }
    
    // 确认转出
    const handleConfirm = () => {
      if (!isValid.value || loading.value) return
      
      const data = {
        engineerId: selectedEngineerId.value,
        note: transferNote.value
      }
      
      emit('transfer', data)
    }
    
    // 关闭对话框
    const handleClose = () => {
      if (loading.value) return
      emit('close')
      
      // 重置表单
      selectedEngineerId.value = ''
      transferNote.value = ''
      error.value = ''
    }
    
    onMounted(() => {
      if (props.show) {
        loadEngineers()
      }
    })
    
    return {
      selectedEngineerId,
      transferNote,
      availableEngineers,
      loading,
      error,
      isValid,
      handleConfirm,
      handleClose
    }
  }
}
</script>

<style scoped>
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog-container {
  width: 90%;
  max-width: 500px;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 25px rgba(0, 0, 0, 0.1);
  animation: scaleIn 0.3s ease;
}

@keyframes scaleIn {
  from { transform: scale(0.9); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
}

.dialog-title {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.close-btn {
  background: transparent;
  border: none;
  color: #6b7280;
  font-size: 20px;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dialog-body {
  padding: 20px 16px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
}

.form-control {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  color: #1f2937;
  background: #fff;
  transition: border-color 0.2s ease;
}

.form-control:focus {
  border-color: #2563eb;
  outline: none;
}

.form-control:disabled {
  background-color: #f3f4f6;
  cursor: not-allowed;
}

.engineer-selector {
  position: relative;
}

.engineer-selector select {
  appearance: none;
  padding-right: 30px;
}

.selector-icon {
  position: absolute;
  top: 50%;
  right: 12px;
  transform: translateY(-50%);
  pointer-events: none;
  color: #6b7280;
}

.info-text {
  display: flex;
  align-items: flex-start;
  font-size: 13px;
  color: #6b7280;
  line-height: 1.5;
  background-color: #f9fafb;
  padding: 12px;
  border-radius: 6px;
}

.info-text i {
  margin-right: 8px;
  font-size: 14px;
  color: #3b82f6;
  margin-top: 2px;
}

.error-message {
  display: flex;
  align-items: center;
  color: #b91c1c;
  font-size: 14px;
  padding: 10px;
  background-color: #fee2e2;
  border-radius: 6px;
  margin-top: 16px;
}

.error-message i {
  margin-right: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 12px 16px;
  border-top: 1px solid #f3f4f6;
  background-color: #f9fafb;
}

.cancel-btn, .confirm-btn {
  padding: 10px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cancel-btn {
  background-color: #f3f4f6;
  color: #4b5563;
  border: 1px solid #e5e7eb;
}

.cancel-btn:hover:not(:disabled) {
  background-color: #e5e7eb;
}

.confirm-btn {
  background-color: #ef4444;
  color: #fff;
  border: none;
}

.confirm-btn:hover:not(:disabled) {
  background-color: #dc2626;
}

.confirm-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 1s linear infinite;
  margin-right: 8px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style> 