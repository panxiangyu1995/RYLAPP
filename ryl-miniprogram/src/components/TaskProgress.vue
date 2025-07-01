<template>
  <div class="mb-6">
    <h4 class="text-lg font-medium mb-4">任务进度</h4>
    
    <!-- 进度步骤 -->
    <div class="relative">
      <div class="absolute left-4 top-0 bottom-0 w-0.5 bg-neutral-gray"></div>
      
      <div 
        v-for="(step, index) in steps" 
        :key="index"
        class="flex mb-6 last:mb-0"
      >
        <!-- 步骤圆点 -->
        <div 
          class="w-8 h-8 rounded-full flex items-center justify-center z-10 mr-4"
          :class="[
            index <= currentStep 
              ? 'bg-primary-medium text-white'
              : 'bg-neutral-light text-neutral-gray'
          ]"
        >
          {{ index + 1 }}
        </div>
        
        <!-- 步骤内容 -->
        <div class="flex-1">
          <h5 
            class="font-medium mb-1"
            :class="[
              index <= currentStep 
                ? 'text-primary-medium'
                : 'text-neutral-gray'
            ]"
          >
            {{ step }}
          </h5>
          
          <!-- 当前步骤的额外内容 -->
          <div v-if="index === currentStep && stepContent" class="mt-2 p-3 bg-neutral-light bg-opacity-50 rounded-lg">
            <div v-if="stepContent.description" class="mb-2">{{ stepContent.description }}</div>
            
            <!-- 图片展示 -->
            <div v-if="stepContent.images && stepContent.images.length" class="grid grid-cols-3 gap-2 mb-2">
              <div 
                v-for="(image, imgIndex) in stepContent.images" 
                :key="imgIndex"
                class="w-full aspect-square rounded-lg overflow-hidden bg-white"
              >
                <img :src="image" class="w-full h-full object-cover" />
              </div>
            </div>
            
            <!-- 附件列表 -->
            <div v-if="stepContent.files && stepContent.files.length">
              <div 
                v-for="(file, fileIndex) in stepContent.files" 
                :key="fileIndex"
                class="flex items-center p-2 bg-white rounded-lg mb-1 last:mb-0"
              >
                <span class="text-primary-medium mr-2">{{ getFileIcon(file.name) }}</span>
                <span class="flex-1 truncate text-sm">{{ file.name }}</span>
                <a :href="file.url" target="_blank" class="text-primary-medium text-sm">查看</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  taskType: {
    type: String,
    required: true
  },
  currentStep: {
    type: Number,
    default: 0
  },
  stepContent: {
    type: Object,
    default: () => null
  }
});

// 根据任务类型获取步骤
const steps = computed(() => {
  const stepMap = {
    'repair': ['已接单', '判断是否需要上门', '检修完成', '维修方案和报价', '维修中', '验证报告', '服务评价', '订单已完成'],
    'maintenance': ['已接单', '判断是否需要上门', '检修完成', '保养方案和报价', '保养中', '验证报告', '服务评价', '订单已完成'],
    'recycle': ['已接单', '判断是否需要上门', '检查完成', '回收方案和报价', '回收中', '验证报告', '服务评价', '订单已完成'],
    'leasing': ['已接单', '判断是否需要上门', '检查完成', '租赁方案和报价', '租赁中', '验证报告', '服务评价', '订单已完成'],
    'training': ['已接单', '判断是否需要上门', '培训准备完成', '培训方案和报价', '培训中', '验证报告', '服务评价', '订单已完成'],
    'verification': ['已接单', '判断是否需要上门', '验证准备完成', '验证方案和报价', '验证中', '验证报告', '服务评价', '订单已完成'],
    'selection': ['已接单', '判断是否需要上门', '选型分析完成', '选型方案和报价', '选型进行中', '验证报告', '服务评价', '订单已完成'],
    'installation': ['已接单', '判断是否需要上门', '安装准备完成', '安装方案和报价', '安装中', '验证报告', '服务评价', '订单已完成']
  };
  
  return stepMap[props.taskType] || stepMap['repair'];
});

// 获取文件图标
const getFileIcon = (fileName) => {
  const extension = fileName.split('.').pop().toLowerCase();
  
  if (extension === 'pdf') return '📄';
  if (extension === 'doc' || extension === 'docx') return '📝';
  if (extension === 'xls' || extension === 'xlsx') return '📊';
  
  return '📎';
};
</script> 