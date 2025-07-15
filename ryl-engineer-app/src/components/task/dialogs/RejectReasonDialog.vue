<template>
  <div class="dialog-overlay" @click.self="$emit('close')">
    <div class="dialog-container">
      <div class="dialog-header">
        <h3 class="dialog-title">拒绝任务</h3>
        <button class="close-btn" @click="$emit('close')">&times;</button>
      </div>

      <div class="dialog-content">
        <div class="form-group">
          <label for="rejection-reason" class="form-label">拒绝理由 (必填)</label>
          <textarea
            id="rejection-reason"
            v-model.trim="reason"
            class="form-textarea"
            rows="4"
            placeholder="请输入详细的拒绝原因，例如：当前任务繁忙，无法接收新任务..."
          ></textarea>
        </div>

        <div class="form-group">
          <label class="form-label">转派目标 (必选)</label>
          <div class="radio-group">
            <label class="radio-label">
              <input type="radio" v-model="transferType" value="admin" name="transfer-type" />
              <span class="radio-text">转派给系统管理员</span>
            </label>
            <label class="radio-label">
              <input type="radio" v-model="transferType" value="engineer" name="transfer-type" />
              <span class="radio-text">选择其他工程师</span>
            </label>
          </div>
        </div>
      </div>

      <div class="dialog-actions">
        <button class="btn-secondary" @click="$emit('close')">取消</button>
        <button 
          class="btn-primary" 
          :disabled="!isValid" 
          @click="handleSubmit"
        >
          确认
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const props = defineProps({
  taskId: {
    type: [String, Number],
    required: true
  }
});

const emit = defineEmits(['close', 'submit']);

const reason = ref('');
const transferType = ref(''); // 'admin' or 'engineer'

const isValid = computed(() => {
  return reason.value.length > 0 && transferType.value !== '';
});

const handleSubmit = () => {
  if (isValid.value) {
    emit('submit', {
      taskId: props.taskId,
      reason: reason.value,
      transferType: transferType.value
    });
  }
};
</script>

<style scoped>
/* Scoped styles based on TaskAssignDialog and general project styles */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog-container {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 400px;
  overflow: hidden;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e5e7eb;
}

.dialog-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #111827;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #6b7280;
  cursor: pointer;
}

.dialog-content {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 0.9rem;
  font-weight: 500;
  margin-bottom: 8px;
  color: #374151;
}

.form-textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 1rem;
  resize: vertical;
}

.radio-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.radio-label {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.radio-text {
  margin-left: 8px;
  font-size: 1rem;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  background-color: #f9fafb;
  border-top: 1px solid #e5e7eb;
}

.btn-secondary, .btn-primary {
  padding: 8px 16px;
  border-radius: 6px;
  border: 1px solid transparent;
  font-weight: 500;
  cursor: pointer;
}

.btn-secondary {
  background-color: white;
  border-color: #d1d5db;
  color: #374151;
}

.btn-primary {
  background-color: #4f46e5;
  color: white;
}

.btn-primary:disabled {
  background-color: #a5b4fc;
  cursor: not-allowed;
}
</style> 