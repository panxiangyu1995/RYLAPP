<template>
  <div class="transfer-panel">
    <h3 class="section-title">
      <i class="icon-exchange-alt"></i>
      任务转出
    </h3>
    
    <div class="transfer-content">
      <p class="transfer-description">
        如果您无法继续负责此任务，可以申请将其转交给其他有资质的工程师处理。
      </p>
      
      <button @click="transfer" class="transfer-btn">
        <i class="icon-paper-plane"></i>
        申请转出任务
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TaskTransferPanel',
  emits: ['transfer'],
  setup(props, { emit }) {
    // 转出任务
    const transfer = () => {
      emit('transfer')
    }
    
    return {
      transfer
    }
  }
}
</script>

<style scoped>
.transfer-panel {
  background-color: #fff;
  margin: 16px 12px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  animation: slideInUp 0.4s ease;
  animation-delay: 0.7s;
}

@keyframes slideInUp {
  from { 
    opacity: 0;
    transform: translateY(20px);
  }
  to { 
    opacity: 1;
    transform: translateY(0);
  }
}

.section-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin: 0;
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
}

.section-title i {
  font-size: 18px;
  margin-right: 8px;
  color: #f59e0b;
}

.transfer-content {
  padding: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.transfer-description {
  font-size: 14px;
  color: #4b5563;
  margin: 0 0 16px 0;
  line-height: 1.5;
  text-align: center;
}

.transfer-btn {
  background-color: #fff7ed;
  color: #9a3412;
  border: 1px solid #fed7aa;
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  max-width: 300px;
}

.transfer-btn:hover {
  background-color: #ffedd5;
  border-color: #fb923c;
}

.transfer-btn:active {
  transform: scale(0.98);
}

.transfer-btn i {
  margin-right: 8px;
}
</style> 