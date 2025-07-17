<template>
  <view class="p-4">
    <view class="text-xl font-bold mb-4">用户协议</view>
    <view class="text-gray-700 leading-relaxed space-y-4">
      <!-- 服务条款 -->
      <view>
        <text class="font-bold block mb-2">1. 服务条款</text>
        <text class="block mb-1">1.1 瑞屹林小程序（以下简称“本平台”）为科研仪器提供以下服务：</text>
        <view class="ml-4">
          <text class="block">• 维修服务：仪器检测、故障维修、部件更换</text>
          <text class="block">• 保养服务：定期维护、耗材更换、性能校准</text>
          <text class="block">• 回收服务：仪器估值、残值回收、环保处理</text>
          <text class="block">• 租赁服务：短期租赁、长期租赁、租赁保险</text>
        </view>
        <text class="block mt-2">1.2 服务时间：工作日9:00-18:00（紧急服务需额外预约）</text>
      </view>

      <!-- 账户管理 -->
      <view>
        <text class="font-bold block mb-2">2. 账户管理</text>
        <text class="block mb-1">2.1 注册需提供真实有效的：</text>
        <view class="ml-4">
          <text class="block">• 个人姓名/企业名称</text>
          <text class="block">• 有效联系电话</text>
          <text class="block">• 仪器使用地址</text>
        </view>
        <text class="block mt-2 text-red-500">2.2 禁止行为：</text>
        <view class="ml-4">
          <text class="block">• 冒用他人身份注册</text>
          <text class="block">• 注册多个账号恶意占用资源</text>
        </view>
      </view>

      <!-- 订单规则 -->
      <view>
        <text class="font-bold block mb-2">3. 订单规则</text>
        <text class="block mb-1">3.1 订单提交需包含：</text>
        <view class="ml-4">
          <text class="block">• 仪器品牌型号</text>
          <text class="block">• 故障现象描述</text>
          <text class="block">• 期望服务时间</text>
        </view>
      </view>

      <!-- 责任条款 -->
      <view>
        <text class="font-bold block mb-2">4. 责任条款</text>
        <text class="block mb-1">4.1 用户需保证：</text>
        <view class="ml-4">
          <text class="block">• 提供准确的仪器信息</text>
          <text class="block">• 提前备份重要数据</text>
        </view>
        <text class="block mt-2">4.2 平台免责情形：</text>
        <view class="ml-4 text-red-500">
          <text class="block">• 因用户隐瞒仪器真实情况导致的损失</text>
          <text class="block">• 不可抗力（如自然灾害、政策调整）</text>
        </view>
      </view>

      <!-- 其他条款 -->
      <view>
        <text class="font-bold block mb-2">5. 其他条款</text>
        <text class="block">5.1 本协议最终解释权归瑞屹林所有</text>
        <text class="block">5.2 争议解决：协商不成可向泰州市人民法院提起诉讼</text>
      </view>
    </view>
  </view>
</template>

<script setup>
// 服务类型动态数据（可选）
const services = [
  "维修", "保养", "回收", "租赁", 
  "培训预约", "验证", "选型", "安装"
];
</script> 