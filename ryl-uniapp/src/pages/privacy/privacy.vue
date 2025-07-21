<template>
  <view class="p-4">
    <view class="text-xl font-bold mb-4">隐私政策</view>
    <scroll-view scroll-y class="text-gray-700 leading-relaxed privacy-content">
      <!-- 数据收集 -->
      <view class="mb-4">
        <text class="font-bold block">1. 我们收集的信息</text>
        <text class="block">• <text class="font-semibold">必要信息</text>：姓名、电话、地址（用于订单处理）</text>
        <text class="block">• <text class="font-semibold">设备信息</text>：型号、故障描述</text>
        <text class="block">• <text class="font-semibold">微信数据</text>：OpenID（必选）、手机号（必选）</text>
      </view>

      <!-- 数据用途 -->
      <view class="mb-4">
        <text class="font-bold block">2. 数据用途</text>
        <text class="block">• 订单分配和进度通知（微信模板消息）</text>
        <text class="block">• 服务改进（匿名化分析订单类型频率）</text>
      </view>

      <!-- 数据共享 -->
      <view class="mb-4">
        <text class="font-bold block">3. 数据共享</text>
        <text class="block">• 我们不会将您的个人信息出售给第三方</text>
        <text class="block">• 仅在必要情况下与服务提供商共享（如物流公司）</text>
      </view>
      
      <!-- 数据存储 -->
      <view class="mb-4">
        <text class="font-bold block">4. 数据存储</text>
        <text class="block">• 数据存储在中国大陆的服务器上</text>
        <text class="block">• 采用加密技术保护您的个人信息</text>
        <text class="block">• 存储时间：订单完成后1年</text>
      </view>
      
      <!-- 用户权利 -->
      <view class="mb-4">
        <text class="font-bold block">5. 您的权利</text>
        <text class="block">• 访问、更正您的个人信息</text>
        <text class="block">• 删除账号（请联系客服）</text>
      </view>
      
      <!-- 安全保护 -->
      <view class="mb-4">
        <text class="font-bold block">6. 安全保护</text>
        <text class="block">• 采用业界标准的安全技术和程序</text>
        <text class="block">• 定期安全评估和员工培训</text>
      </view>
      
      <!-- 政策更新 -->
      <view class="mb-4">
        <text class="font-bold block">7. 政策更新</text>
        <text class="block">• 我们可能会更新本隐私政策</text>
        <text class="block">• 重大变更会通过小程序通知您</text>
      </view>
      
      <!-- 联系我们 -->
      <view class="mb-4">
        <text class="block">• 客服电话：13670469261</text>
        <text class="block">• 电子邮箱：Ryl@realline-scientific.com</text>
        <text class="block">• 公司地址：泰州市医药高新技术产业开发区秀水路22号2幢五层</text>
      </view>
    </scroll-view>
    
    <!-- 返回按钮 -->
    <button class="mt-4 py-2 bg-blue-500 text-white rounded-lg" @click="navigateBack">返回</button>
  </view>
</template>

<script setup>
import { ref } from 'vue';

const navigateBack = () => uni.navigateBack();
</script>

<style>
.privacy-content {
  height: calc(100vh - 180px);
}
</style> 