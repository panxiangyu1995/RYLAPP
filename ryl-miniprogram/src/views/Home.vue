<template>
  <div class="p-4">
    <!-- 用户信息卡片 -->
    <UserInfoCard />
    
    <!-- 服务类型导航 -->
    <div class="mb-6">
      <h2 class="text-xl font-medium mb-4">服务项目</h2>
      <div class="grid grid-cols-2 gap-4">
        <div 
          v-for="service in serviceTypes" 
          :key="service.id"
          @click="goToTaskCreate(service.id)"
          class="card p-4 flex flex-col items-center justify-center cursor-pointer transition-transform transform hover:scale-105"
        >
          <div class="w-12 h-12 rounded-full bg-primary-light bg-opacity-20 flex items-center justify-center text-primary-medium mb-2">
            <component :is="service.icon" size="24" />
          </div>
          <span>{{ service.name }}</span>
        </div>
      </div>
    </div>
    
    <!-- 查看任务进展按钮 -->
    <div class="mb-6">
      <button 
        @click="goToTaskProgress" 
        class="btn btn-primary w-full py-3 text-lg"
      >
        查看任务进展
      </button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { useTaskStore } from '@/stores/task';
import UserInfoCard from '@/components/UserInfoCard.vue';
import { 
  Wrench, Settings, Recycle, Package, BookOpen, CheckCircle, ListFilter, Hammer
} from 'lucide-vue-next';

const router = useRouter();
const taskStore = useTaskStore();

// 服务类型
const serviceTypes = [
  { id: 'repair', name: '仪器维修', icon: Wrench },
  { id: 'maintenance', name: '仪器保养', icon: Settings },
  { id: 'recycle', name: '仪器回收', icon: Recycle },
  { id: 'leasing', name: '仪器租赁', icon: Package },
  { id: 'training', name: '培训预约', icon: BookOpen },
  { id: 'verification', name: '仪器验证', icon: CheckCircle },
  { id: 'selection', name: '仪器选型', icon: ListFilter },
  { id: 'installation', name: '仪器安装', icon: Hammer }
];

// 前往任务创建页
const goToTaskCreate = (type) => {
  router.push({
    name: 'TaskCreate',
    query: { type }
  });
};

// 前往任务进展页
const goToTaskProgress = () => {
  router.push({ name: 'TaskProgress' });
};
</script> 