<template>
  <view 
    v-if="toast.show" 
    :class="toastClasses" 
    class="fixed top-5 left-1/2 -translate-x-1/2 max-w-sm w-full p-4 rounded-lg shadow-lg z-50 transition-opacity duration-300"
  >
    <view class="flex items-center">
      <view class="flex-shrink-0">
        <!-- Icons can be added here based on toast.type -->
        <text v-if="toast.type === 'success'" class="text-xl">✅</text>
        <text v-if="toast.type === 'error'" class="text-xl">❌</text>
        <text v-if="toast.type === 'warning'" class="text-xl">⚠️</text>
        <text v-if="toast.type === 'info'" class="text-xl">ℹ️</text>
      </view>
      <view class="ml-3">
        <text class="text-sm font-medium">{{ toast.message }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue';
import { useTaskStore } from '@/stores/task';
import { storeToRefs } from 'pinia';

const taskStore = useTaskStore();
const { toast } = storeToRefs(taskStore);

const toastClasses = computed(() => {
  const baseClasses = 'border';
  switch (toast.value.type) {
    case 'success':
      return `${baseClasses} bg-green-100 border-green-400 text-green-700`;
    case 'error':
      return `${baseClasses} bg-red-100 border-red-400 text-red-700`;
    case 'warning':
      return `${baseClasses} bg-yellow-100 border-yellow-400 text-yellow-700`;
    case 'info':
    default:
      return `${baseClasses} bg-ui-blue-end/10 border-ui-blue-end/40 text-ui-blue-start`;
  }
});
</script>

<style scoped>
/* Utility to center the toast */
.-translate-x-1\/2 {
  transform: translateX(-50%);
}
</style> 