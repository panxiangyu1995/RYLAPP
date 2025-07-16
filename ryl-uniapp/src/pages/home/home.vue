<template>
  <view>
    <!-- 页面头部 -->
    <Header />
    
    <view class="p-4">
      <!-- 用户信息卡片 -->
      <UserInfoCard />
      
      <!-- 服务类型导航 -->
      <view class="mb-6">
        <view class="text-xl font-medium mb-4 flex items-center">
          <LayoutGridIcon :size="20" color="#286B9C" class="mr-2" />
          服务项目
        </view>
        <view class="grid grid-cols-2 gap-3">
          <view 
            v-for="service in taskStore.taskTypes" 
            :key="service.id"
            @click="goToTaskCreate(service.id)"
            class="bg-white rounded-lg shadow-md overflow-hidden p-3 flex flex-col items-center justify-center cursor-pointer transition-all duration-300 hover:scale-105 hover:shadow-sm border border-transparent hover:border-primary-light"
          >
            <view class="w-11 h-11 rounded-full bg-gradient-to-br from-primary-light to-primary-medium bg-opacity-20 flex items-center justify-center text-white mb-2">
              <ToolIcon v-if="service.icon === 'tool'" :size="20" color="white" />
              <HammerIcon v-else-if="service.icon === 'hammer'" :size="20" color="white" />
              <RecycleIcon v-else-if="service.icon === 'recycle'" :size="20" color="white" />
              <PackageIcon v-else-if="service.icon === 'package'" :size="20" color="white" />
              <BookOpenIcon v-else-if="service.icon === 'book-open'" :size="20" color="white" />
              <CheckCircleIcon v-else-if="service.icon === 'check-circle'" :size="20" color="white" />
              <ListFilterIcon v-else-if="service.icon === 'list-filter'" :size="20" color="white" />
              <SettingsIcon v-else-if="service.icon === 'settings'" :size="20" color="white" />
            </view>
            <view class="font-medium text-sm">{{ service.name }}</view>
          </view>
        </view>
      </view>
      
      <!-- 查看任务进展按钮 -->
      <view class="mb-6">
        <button 
          @click="goToTaskProgress" 
          class="px-4 py-2 rounded-lg font-medium transition-colors bg-primary-medium text-white hover:bg-primary-dark w-full py-3 text-lg flex items-center justify-center"
        >
          <ClipboardListIcon :size="20" color="white" class="mr-2" />
          查看订单进展
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue';
import { useTaskStore } from '@/stores/task';
import UserInfoCard from '@/components/UserInfoCard.vue';
import Header from '@/components/Header.vue';
import LayoutGridIcon from '@/components/icons/LayoutGridIcon.vue';
import ClipboardListIcon from '@/components/icons/ClipboardListIcon.vue';
import ToolIcon from '@/components/icons/ToolIcon.vue';
import HammerIcon from '@/components/icons/HammerIcon.vue';
import RecycleIcon from '@/components/icons/RecycleIcon.vue';
import PackageIcon from '@/components/icons/PackageIcon.vue';
import BookOpenIcon from '@/components/icons/BookOpenIcon.vue';
import CheckCircleIcon from '@/components/icons/CheckCircleIcon.vue';
import ListFilterIcon from '@/components/icons/ListFilterIcon.vue';
import SettingsIcon from '@/components/icons/SettingsIcon.vue';


const taskStore = useTaskStore();

// 前往任务创建页
const goToTaskCreate = (type) => {
  uni.navigateTo({
    url: `/pages/taskcreate/taskcreate?type=${type}`
  });
};

// 前往任务进展页
const goToTaskProgress = () => {
  uni.navigateTo({ url: '/pages/taskprogress/taskprogress' });
};
</script> 