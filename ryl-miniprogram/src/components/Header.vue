<template>
  <header class="bg-white shadow-sm py-3 px-4 mb-4">
    <div class="flex justify-center relative">
      <!-- Logo和标题（居中） -->
      <div class="flex items-center">
        <img 
          src="@/assets/company-logo.png" 
          alt="Realline Scientific" 
          class="h-10 mr-2"
        />
        <div>
          <h1 class="text-lg font-semibold text-primary-dark">瑞屹林</h1>
          <p class="text-xs text-gray-500">科研仪器服务平台</p>
        </div>
      </div>
      
      <!-- 功能按钮 -->
      <div class="absolute top-0 right-0">
        <slot name="right-actions"></slot>
      </div>
    </div>
  </header>
</template>

<script setup>
// Header组件
</script>

<style scoped>
/* 确保logo显示正确 */
img {
  object-fit: contain;
}
</style> 