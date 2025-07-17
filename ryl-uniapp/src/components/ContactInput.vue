<template>
  <div>
    <input 
      type="text" 
      :value="inputValue" 
      @input="handleInput" 
      class="w-full px-3 py-2 bg-white border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-ui-blue-end focus:border-ui-blue-end focus:shadow-ui-glow"
      placeholder="请输入登录用户名"
    />
    <view v-if="inputValue && inputValue.length < minLength" class="text-red-500 text-xs mt-1">
      登录用户名至少需要{{ minLength }}个字符
    </view>
  </div>
</template>

<script>
import { ref, watch } from 'vue';

export default {
  name: 'ContactInput',
  props: {
    value: {
      type: String,
      default: ''
    },
    minLength: {
      type: Number,
      default: 4
    }
  },
  emits: ['update'],
  setup(props, { emit }) {
    const inputValue = ref(props.value);

    // 监听 props.value 变化
    watch(() => props.value, (newValue) => {
      inputValue.value = newValue;
    });

    // 处理输入
    const handleInput = (event) => {
      const value = event.detail.value;
      inputValue.value = value;
      emit('update', value);
    };

    return {
      inputValue,
      handleInput
    };
  }
};
</script>

<style scoped>
/* 可根据需要添加样式 */
</style> 