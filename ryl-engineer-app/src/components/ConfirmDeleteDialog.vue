<template>
  <div class="confirm-delete-dialog" v-if="visible">
    <div class="dialog-overlay" @click="cancel"></div>
    <div class="dialog-content">
      <div class="dialog-header">
        <h2>{{ title }}</h2>
        <button class="close-btn" @click="cancel">×</button>
      </div>
      <div class="dialog-body">
        <p class="warning-text">{{ message }}</p>
        <div class="input-group">
          <label for="password">请输入密码确认删除：</label>
          <input 
            type="password" 
            id="password" 
            v-model="passwordInput"
            placeholder="请输入密码"
            @keyup.enter="confirm"
          >
        </div>
        <p class="error-message" v-if="errorMessage">{{ errorMessage }}</p>
      </div>
      <div class="dialog-footer">
        <button class="cancel-btn" @click="cancel">取消</button>
        <button 
          class="confirm-btn" 
          @click="confirm"
          :disabled="!passwordInput"
        >
          确认删除
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ConfirmDeleteDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '确认删除'
    },
    message: {
      type: String,
      default: '此操作将永久删除该数据，是否继续？'
    },
    errorMessage: {
      type: String,
      default: ''
    }
  },
  emits: ['confirm', 'cancel', 'update:visible'],
  data() {
    return {
      passwordInput: ''
    }
  },
  watch: {
    visible(val) {
      if (val) {
        document.body.style.overflow = 'hidden'
      } else {
        document.body.style.overflow = ''
        this.passwordInput = ''
      }
    }
  },
  methods: {
    confirm() {
      if (!this.passwordInput) return
      this.$emit('confirm', this.passwordInput)
    },
    cancel() {
      this.$emit('update:visible', false)
      this.$emit('cancel')
      this.passwordInput = ''
    }
  }
}
</script>

<style scoped>
.confirm-delete-dialog {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
}

.dialog-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
}

.dialog-content {
  width: 90%;
  max-width: 400px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1001;
  overflow: hidden;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.dialog-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #999;
  cursor: pointer;
}

.dialog-body {
  padding: 20px;
}

.warning-text {
  color: #f56c6c;
  font-size: 15px;
  margin-bottom: 20px;
}

.input-group {
  margin-bottom: 15px;
}

.input-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}

.input-group input {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  transition: all 0.3s;
}

.input-group input:focus {
  border-color: #409eff;
  outline: none;
}

.error-message {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  padding: 10px 20px 20px;
}

.cancel-btn, .confirm-btn {
  padding: 8px 20px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.cancel-btn {
  background-color: #f4f4f5;
  color: #909399;
  margin-right: 10px;
}

.cancel-btn:hover {
  background-color: #e9e9eb;
}

.confirm-btn {
  background-color: #f56c6c;
  color: white;
}

.confirm-btn:hover {
  background-color: #f78989;
}

.confirm-btn:disabled {
  background-color: #fab6b6;
  cursor: not-allowed;
}
</style> 