<template>
  <view class="p-4">
    <view class="text-xl font-bold mb-4">隐私政策</view>
    <view class="text-gray-700 leading-relaxed">
      <!-- 数据收集 -->
      <view class="mb-4">
        <text class="font-bold">1. 我们收集的信息</text>
        <text>• <text class="font-semibold">必要信息</text>：姓名、电话、地址（用于订单处理）</text>
        <text>• <text class="font-semibold">设备信息</text>：型号、故障描述</text>
        <text>• <text class="font-semibold">微信数据</text>：OpenID（必选）、头像/昵称（可选）</text>
      </view>

      <!-- 数据用途 -->
      <view class="mb-4">
        <text class="font-bold">2. 数据用途</text>
        <text>• 订单分配和进度通知（短信/微信模板消息）</text>
        <text>• 服务改进（匿名化分析订单类型频率）</text>
      </view>

      <!-- 数据共享 -->
      <view class="mb-4 bg-yellow-50 p-2 rounded">
        <text class="font-bold">3. 数据共享</text>
        <text>• 您的信息仅限瑞屹林内部使用，不会共享给第三方。</text>
        <text>• 微信数据仅用于登录功能，不会获取好友列表等无关信息。</text>
      </view>

      <!-- 用户权利 -->
      <view class="mb-4">
        <text class="font-bold">4. 您的权利</text>
        <text>• 在“个人资料”页修改或删除非必要信息（如地址）</text>
        <text>• 注销账号需联系客服：support@ruiyilin.com</text>
      </view>

      <!-- 微信条款 -->
      <view class="mb-4">
        <text class="font-bold">5. 微信小程序特别说明</text>
        <text>本小程序遵守《微信小程序用户隐私保护指引》。</text>
      </view>
    </view>

    <!-- 返回按钮 -->
    <view class="mt-6">
      <button @click="navigateBack" class="bg-blue-500 text-white p-2 rounded">返回</button>
    </view>
  </view>
</template>

<script setup>
const navigateBack = () => uni.navigateBack();
</script> 