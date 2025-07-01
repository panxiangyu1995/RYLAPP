<template>
  <div class="card p-4 mb-3 cursor-pointer" @click="goToTaskDetail">
    <div class="flex items-center justify-between mb-3">
      <span class="bg-primary-light text-white px-2 py-1 rounded-lg text-sm">{{ taskTypeInfo.name }}</span>
      <span class="text-sm text-gray-500">{{ formatDate(task.create_time) }}</span>
    </div>
    
    <div class="mb-3">
      <h4 class="font-medium">{{ task.device_name || '未指定设备' }}</h4>
      <p class="text-sm text-gray-600 truncate">{{ task.description || '无任务描述' }}</p>
    </div>
    
    <div class="flex justify-between text-sm">
      <div>
        <p>负责人: {{ task.engineer_name || '未分配' }}</p>
        <p class="text-gray-500">联系电话: {{ task.engineer_phone || '未提供' }}</p>
      </div>
      
      <div class="text-right">
        <p>当前阶段</p>
        <p class="text-primary-medium">{{ getCurrentStepName() }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useTaskStore } from '@/stores/task';

const props = defineProps({
  task: {
    type: Object,
    required: true
  }
});

const router = useRouter();
const taskStore = useTaskStore();

const taskTypeInfo = computed(() => {
  return taskStore.getTaskTypeInfo(props.task.task_type);
});

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

// 获取当前步骤名称
const getCurrentStepName = () => {
  const stepIndex = props.task.current_step || 0;
  const stepNames = {
    'repair': ['已接单', '是否上门', '检修完成', '维修方案和报价', '维修中', '验证报告', '服务评价', '订单已完成'],
    'maintenance': ['已接单', '是否上门', '检修完成', '保养方案和报价', '保养中', '验证报告', '服务评价', '订单已完成'],
    'recycle': ['已接单', '是否上门', '检查完成', '回收方案和报价', '回收中', '验证报告', '服务评价', '订单已完成'],
    'leasing': ['已接单', '是否上门', '检查完成', '租赁方案和报价', '租赁中', '验证报告', '服务评价', '订单已完成'],
    'training': ['已接单', '是否上门', '培训准备完成', '培训方案和报价', '培训中', '验证报告', '服务评价', '订单已完成'],
    'verification': ['已接单', '是否上门', '验证准备完成', '验证方案和报价', '验证中', '验证报告', '服务评价', '订单已完成'],
    'selection': ['已接单', '是否上门', '选型分析完成', '选型方案和报价', '选型进行中', '验证报告', '服务评价', '订单已完成'],
    'installation': ['已接单', '是否上门', '安装准备完成', '安装方案和报价', '安装中', '验证报告', '服务评价', '订单已完成'],
  };

  const taskType = props.task.task_type || 'repair';
  return stepNames[taskType][stepIndex] || '未知阶段';
};

// 前往任务详情
const goToTaskDetail = () => {
  router.push({
    name: 'TaskDetail',
    params: { id: props.task.task_id }
  });
};
</script> 