<template>
  <view class="p-4">
    <view class="text-xl font-bold mb-4">用户协议</view>
    <scroll-view scroll-y class="text-gray-700 leading-relaxed agreement-content">
      <!-- 服务条款 -->
      <view class="mb-4">
        <text class="font-bold block mb-2">1. 服务条款</text>
        <text class="block mb-1">1.1 瑞屹林小程序（以下简称"本平台"）为科研仪器提供以下服务：</text>
        <view class="ml-4">
          <text class="block">• 维修服务：仪器检测、故障维修、部件更换</text>
          <text class="block">• 保养服务：定期维护、耗材更换、性能校准</text>
          <text class="block">• 回收服务：仪器估值、残值回收、环保处理</text>
          <text class="block">• 租赁服务：短期租赁、长期租赁、租赁保险</text>
        </view>
        <text class="block mt-2">1.2 服务时间：工作日8:30-17:30（紧急服务需额外预约）</text>
      </view>

      <!-- 账户管理 -->
      <view class="mb-4">
        <text class="font-bold block mb-2">2. 账户管理</text>
        <text class="block mb-1">2.1 用户注册时需提供真实、准确的信息，并妥善保管账号和密码。</text>
        <text class="block mb-1">2.2 用户需对账号下的所有行为负责，如发现账号被盗用，应立即联系客服。</text>
        <text class="block mb-1">2.3 本平台有权在以下情况暂停或终止用户账号：</text>
        <view class="ml-4">
          <text class="block">• 提供虚假信息</text>
          <text class="block">• 违反本协议条款</text>
          <text class="block">• 长期（超过12个月）不使用账号</text>
        </view>
      </view>
      
      <!-- 订单规则 -->
      <view class="mb-4">
        <text class="font-bold block mb-2">3. 订单规则</text>
        <text class="block mb-1">3.1 用户提交订单即表示确认订单中的服务内容和价格。</text>
        <text class="block mb-1">3.2 订单状态变更会通过微信消息或短信通知用户。</text>
        <text class="block mb-1">3.3 订单取消规则：</text>
        <view class="ml-4">
          <text class="block">• 未分配工程师前可免费取消</text>
          <text class="block">• 工程师已出发需支付上门费</text>
          <text class="block">• 服务已开始需支付已产生的费用</text>
        </view>
      </view>
      
      <!-- 责任条款 -->
      <view class="mb-4">
        <text class="font-bold block mb-2">4. 责任条款</text>
        <text class="block mb-1">4.1 本平台承诺：</text>
        <view class="ml-4">
          <text class="block">• 提供专业的仪器维修和保养服务</text>
          <text class="block">• 保护用户设备和数据安全</text>
          <text class="block">• 对服务质量负责，提供售后保障</text>
        </view>
        <text class="block mb-1 mt-1">4.2 用户承诺：</text>
        <view class="ml-4">
          <text class="block">• 提供真实的设备信息</text>
          <text class="block">• 配合工程师完成服务</text>
          <text class="block">• 及时确认服务完成情况</text>
        </view>
      </view>
      
      <!-- 费用与支付 -->
      <view class="mb-4">
        <text class="font-bold block mb-2">5. 费用与支付</text>
        <text class="block mb-1">5.1 服务费用包括：</text>
        <view class="ml-4">
          <text class="block">• 检测费：设备故障诊断费用</text>
          <text class="block">• 维修费：包括人工和材料费用</text>
          <text class="block">• 上门费：工程师上门服务费用</text>
        </view>
        <text class="block mt-2">5.2 支付方式：</text>
        <view class="ml-4">
          <text class="block">• 企业对公转账</text>
        </view>
      </view>
      
      <!-- 隐私保护 -->
      <view class="mb-4">
        <text class="font-bold block mb-2">6. 隐私保护</text>
        <text class="block mb-1">6.1 本平台重视用户隐私，收集的用户信息仅用于：</text>
        <view class="ml-4">
          <text class="block">• 提供和改进服务</text>
          <text class="block">• 订单处理和客户服务</text>
          <text class="block">• 法律法规要求的用途</text>
        </view>
        <text class="block mb-1 mt-1">6.2 详细隐私政策请参阅《隐私政策》</text>
      </view>
      
      <!-- 知识产权 -->
      <view class="mb-4">
        <text class="font-bold block mb-2">7. 知识产权</text>
        <text class="block mb-1">7.1 本平台内容（包括文字、图片、标识等）的知识产权归本公司所有。</text>
        <text class="block mb-1">7.2 未经许可，用户不得复制、修改、传播或用于商业目的。</text>
      </view>
      
      <!-- 协议变更 -->
      <view class="mb-4">
        <text class="font-bold block mb-2">8. 协议变更</text>
        <text class="block mb-1">8.1 本平台有权在必要时修改本协议条款。</text>
        <text class="block mb-1">8.2 协议变更后，会在平台显著位置公告。</text>
        <text class="block mb-1">8.3 继续使用本平台服务视为接受修改后的协议。</text>
      </view>
      
      <!-- 法律适用 -->
      <view class="mb-4">
        <text class="font-bold block mb-2">9. 法律适用</text>
        <text class="block mb-1">9.1 本协议适用中华人民共和国法律。</text>
        <text class="block mb-1">9.2 因本协议产生的争议，应友好协商解决；协商不成的，提交本公司所在地人民法院诉讼解决。</text>
      </view>
      
      <!-- 联系方式 -->
      <view class="mb-4">
        <text class="font-bold block mb-2">10. 联系方式</text>
        <text class="block mb-1">如有任何问题，请联系客服：</text>
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
// 服务类型动态数据（可选）
const services = [
  "维修", "保养", "回收", "租赁", 
  "培训预约", "验证", "选型", "安装"
];

const navigateBack = () => uni.navigateBack();
</script>

<style>
.agreement-content {
  height: calc(100vh - 180px);
}
</style> 