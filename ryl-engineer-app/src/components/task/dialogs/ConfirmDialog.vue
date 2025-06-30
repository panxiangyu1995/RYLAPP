<template>
  <div class="confirm-dialog-overlay" @click="closeDialog"></div>
  <div class="confirm-dialog">
    <div class="dialog-header">
      <h3 class="dialog-title">{{ title }}</h3>
    </div>
    <div class="dialog-content">
      <p class="dialog-message" v-if="!customContent">{{ message }}</p>
      <div class="custom-content" v-else>
        <component :is="customContent" v-if="typeof customContent === 'function'" />
        <div v-else v-html="message"></div>
      </div>
    </div>
    <div class="dialog-actions">
      <button 
        class="cancel-btn" 
        @click="closeDialog"
        v-if="showCancel"
      >
        {{ cancelText }}
      </button>
      <button 
        class="confirm-btn" 
        :class="confirmButtonType" 
        @click="$emit('confirm')"
      >
        {{ confirmText }}
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ConfirmDialog',
  props: {
    title: {
      type: String,
      default: '确认'
    },
    message: {
      type: String,
      required: true
    },
    confirmText: {
      type: String,
      default: '确认'
    },
    cancelText: {
      type: String,
      default: '取消'
    },
    type: {
      type: String,
      default: 'primary',
      validator: (value) => ['primary', 'success', 'danger', 'warning'].includes(value)
    },
    showCancel: {
      type: Boolean,
      default: true
    },
    customContent: {
      type: [Function, Boolean, null],
      default: null
    }
  },
  emits: ['confirm', 'cancel', 'close'],
  computed: {
    confirmButtonType() {
      return {
        'btn-primary': this.type === 'primary',
        'btn-success': this.type === 'success',
        'btn-danger': this.type === 'danger',
        'btn-warning': this.type === 'warning'
      }
    }
  },
  methods: {
    closeDialog() {
      this.$emit('close');
      this.$emit('cancel');
    }
  }
}
</script>

<style scoped>
.confirm-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 100;
  animation: fadeIn 0.2s ease;
}

.confirm-dialog {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 90%;
  max-width: 400px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  z-index: 101;
  overflow: hidden;
  animation: slideIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideIn {
  from { opacity: 0; transform: translate(-50%, -45%); }
  to { opacity: 1; transform: translate(-50%, -50%); }
}

.dialog-header {
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.dialog-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.dialog-content {
  padding: 16px;
}

.dialog-message {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  color: #4b5563;
}

.dialog-actions {
  padding: 16px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn {
  padding: 10px 16px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background-color: #fff;
  color: #4b5563;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.cancel-btn:hover {
  background-color: #f9fafb;
  border-color: #9ca3af;
}

.confirm-btn {
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-primary {
  background-color: #2563eb;
  color: #fff;
}

.btn-primary:hover {
  background-color: #1d4ed8;
}

.btn-success {
  background-color: #10b981;
  color: #fff;
}

.btn-success:hover {
  background-color: #059669;
}

.btn-danger {
  background-color: #ef4444;
  color: #fff;
}

.btn-danger:hover {
  background-color: #dc2626;
}

.btn-warning {
  background-color: #f59e0b;
  color: #fff;
}

.btn-warning:hover {
  background-color: #d97706;
}
</style>
