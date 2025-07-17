<template>
  <view class="p-4">
    <view class="text-xl font-bold mb-4">用户协议</view>
    <view class="text-gray-700 leading-relaxed">
      <view class="mb-4">
        <text class="font-bold">1. 服务内容</text>
        <text>瑞屹林工程师APP提供科研仪器的维修、保养、回收、租赁、培训预约、验证、选型及安装服务。所有服务需通过公司内部系统分配和管理。</text>
      </view>
      <view class="mb-4">
        <text class="font-bold">2. 用户义务</text>
        <text>工程师需在接到任务后24小时内响应，并严格按照流程操作。禁止泄露客户设备信息或擅自修改任务状态。</text>
      </view>
      <view class="mb-4">
        <text class="font-bold">3. 责任限制</text>
        <text>公司对维修结果提供90天有限保证，但因操作不当或未按流程导致的损失由用户自行承担。</text>
      </view>
      <view class="mb-4">
        <text class="font-bold">4. 协议变更</text>
        <text>本协议如有更新，将通过APP内部公告通知。继续使用服务视为接受变更。</text>
      </view>
    </view>
  </view>
</template>

<script setup>
const services = [
  "维修", "保养", "回收", "租赁", 
  "培训预约", "验证", "选型", "安装"
];
</script> 