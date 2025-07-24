<template>
  <div class="quote-dialog-overlay">
    <div class="quote-dialog">
      <h2>填写报价</h2>
      <div class="form-group">
        <label for="price">报价金额 (元)</label>
        <input type="number" id="price" v-model.number="price" placeholder="请输入金额" />
      </div>
      <div class="dialog-actions">
        <button class="cancel-btn" @click="$emit('close')">取消</button>
        <button class="confirm-btn" @click="submitQuote">确定</button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';

export default {
  name: 'QuoteDialog',
  emits: ['close', 'submit'],
  setup(props, { emit }) {
    const price = ref(0);

    const submitQuote = () => {
      if (price.value > 0) {
        emit('submit', price.value);
      } else {
        alert('请输入有效的报价金额');
      }
    };

    return {
      price,
      submitQuote,
    };
  },
};
</script>

<style scoped>
.quote-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.quote-dialog {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 80%;
  max-width: 400px;
}

h2 {
  margin-top: 0;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.cancel-btn, .confirm-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn {
  background-color: #f0f0f0;
}

.confirm-btn {
  background-color: #007bff;
  color: white;
}
</style> 