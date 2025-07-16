<template>
  <view class="p-4 bg-white rounded-lg shadow-md mb-4" @click="goToDetail">
    <view class="mb-3">
      <view class="font-medium">{{ task.deviceName || '未指定设备' }}</view>
      <view class="text-sm text-gray-600 truncate">{{ task.description || '无订单描述' }}</view>
    </view>

    <view class="flex items-center justify-between">
      <view>
        <text>负责人: {{ task.engineerName || '未分配' }}</text>
        <text class="text-gray-500">联系电话: {{ task.engineerPhone || '未提供' }}</text>
      </view>

      <view class="text-right">
        <text>当前阶段</text>
        <text class="text-primary-medium">{{ getCurrentStepName() }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue';
import { useTaskStore } from '@/stores/task';

const props = defineProps({
  task: {
    type: Object,
    required: true
  }
});

const taskStore = useTaskStore();

const taskTypeInfo = computed(() => {
  return taskStore.getTaskTypeInfo(props.task.taskType);
});

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

// 获取当前步骤名称
const getCurrentStepName = () => {
  const stepIndex = props.task.currentStep || 0;
  const stepNames = {
    'repair': ['已接单', '是否上门', '检修完成', '维修方案和报价', '维修中', '验证报告', '服务评价', '订单已完成'],
    'maintenance': ['已接单', '是否上门', '检修完成', '保养方案和报价', '保养中', '验证报告', '服务评价', '订单已完成'],
    'recycle': ['已接单', '是否上门', '检查完成', '回收方案和报价', '回收中', '验证报告', '服务评价', '订单已完成'],
    'leasing': ['已接单', '是否上门', '检查完成', '租赁方案和报价', '租赁中', '验证报告', '服务评价', '订单已完成'],
    'training': ['已接单', '是否上门', '培训准备完成', '培训方案和报价', '培训中', '验证报告', '服务评价', '订单已完成'],
    'verification': ['已接单', '是否上门', '验证准备完成', '验证方案和报价', '验证中', '验证报告', '服务评价', '订单已完成'],
    'selection': ['已接单', '是否上门', '选型分析完成', '选型方案和报价', '选型进行中', '验证报告', '服务评价', '订单已完成'],
    'installation': ['已接单', '是否上门', '安装准备完成', '安装方案和报价', '安装中', '验证报告', '服务评价', '订单已完成']
  };

  const taskType = props.task.taskType || 'repair';
  return stepNames[taskType][stepIndex] || '未知阶段';
};

// 前往订单详情
const goToDetail = () => {
  uni.navigateTo({
    url: `/pages/taskdetail/taskdetail?id=${props.task.id}`
  });
};
</script>

<style scoped>
/* 组件样式 */
</style> 