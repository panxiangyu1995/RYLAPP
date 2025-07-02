<template>
  <div>
    <!-- 页面头部 -->
    <Header />
    
    <div class="p-4">
      <!-- 用户信息卡片 -->
      <UserInfoCard />
      
      <!-- 服务类型导航 -->
      <div class="mb-6">
        <h2 class="text-xl font-medium mb-4 flex items-center">
          <LayoutGrid size="20" class="mr-2 text-primary-medium" />
          服务项目
        </h2>
        <div class="grid grid-cols-2 gap-3">
          <div 
            v-for="service in serviceTypes" 
            :key="service.id"
            @click="goToTaskCreate(service.id)"
            class="card p-3 flex flex-col items-center justify-center cursor-pointer transition-all duration-300 hover:scale-105 hover:shadow-sm border border-transparent hover:border-primary-light"
          >
            <div class="w-11 h-11 rounded-full bg-gradient-to-br from-primary-light to-primary-medium bg-opacity-20 flex items-center justify-center text-white mb-2">
              <component :is="service.icon" size="20" />
            </div>
            <span class="font-medium text-sm">{{ service.name }}</span>
          </div>
        </div>
      </div>
      
      <!-- 查看任务进展按钮 -->
      <div class="mb-6">
        <button 
          @click="goToTaskProgress" 
          class="btn btn-primary w-full py-3 text-lg flex items-center justify-center"
        >
          <ClipboardList size="20" class="mr-2" />
          查看任务进展
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { useTaskStore } from '@/stores/task';
import { useUserStore } from '@/stores/user';
import UserInfoCard from '@/components/UserInfoCard.vue';
import Header from '@/components/Header.vue';
import { 
  Wrench, Settings, Recycle, Package, BookOpen, CheckCircle, ListFilter, Hammer,
  ClipboardList, LayoutGrid
} from 'lucide-vue-next';

const router = useRouter();
const taskStore = useTaskStore();
const userStore = useUserStore();

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