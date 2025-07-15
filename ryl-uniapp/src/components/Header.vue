<template>
  <view class="bg-white shadow-sm py-3 px-4 mb-4">
    <view class="relative">
      <!-- 居中的垂直布局 -->
      <view class="flex flex-col items-center">
        <!-- Logo -->
        <image 
          src="/static/images/company-logo.png" 
          alt="Realline Scientific" 
          class="h-10"
          mode="aspectFit"
        />
        
        <!-- 标题文字 -->
        <view class="flex flex-col items-center mt-1">
          <view class="text-lg font-semibold text-primary-dark">瑞屹林</view>
          <view class="text-xs text-gray-500">科研仪器服务平台</view>
        </view>
      </view>
      
      <!-- 功能按钮 -->
      <view class="absolute top-0 right-0 h-full flex items-center">
        <slot name="right-actions"></slot>
      </view>
    </view>
  </view>
</template>

<script setup>
// Header组件 - 移植自 ryl-miniprogram
</script> 