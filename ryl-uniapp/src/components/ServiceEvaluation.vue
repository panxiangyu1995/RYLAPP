<template>
  <view class="bg-white p-4 rounded-lg">
    <view class="text-lg font-medium mb-4">服务评价</view>

    <form @submit="submitEvaluation">
      <!-- 服务态度评分 -->
      <view class="mb-4">
        <view class="text-sm font-medium mb-1 text-ui-blue-start">服务态度 <text class="text-red-500">*</text></view>
        <view class="flex items-center">
          <StarIcon
            v-for="i in 5"
            :key="i"
            @click="evaluation.serviceAttitude = i"
            :size="24"
            :color="i <= evaluation.serviceAttitude ? '#FFC107' : '#D1D5DB'"
            class="cursor-pointer p-1"
          />
        </view>
        <view v-if="errors.serviceAttitude" class="text-red-500 text-xs mt-1">{{ errors.serviceAttitude }}</view> 
      </view>

      <!-- 服务质量评分 -->
      <view class="mb-4">
        <view class="text-sm font-medium mb-1 text-ui-blue-start">服务质量 <text class="text-red-500">*</text></view>
        <view class="flex items-center">
          <StarIcon
            v-for="i in 5"
            :key="i"
            @click="evaluation.serviceQuality = i"
            :size="24"
            :color="i <= evaluation.serviceQuality ? '#FFC107' : '#D1D5DB'"
            class="cursor-pointer p-1"
          />
        </view>
        <view v-if="errors.serviceQuality" class="text-red-500 text-xs mt-1">{{ errors.serviceQuality }}</view>   
      </view>

      <!-- 总体评价评分 -->
      <view class="mb-4">
        <view class="text-sm font-medium mb-1 text-ui-blue-start">总体评价 <text class="text-red-500">*</text></view>
        <view class="flex items-center">
          <StarIcon
            v-for="i in 5"
            :key="i"
            @click="evaluation.overallRating = i"
            :size="24"
            :color="i <= evaluation.overallRating ? '#FFC107' : '#D1D5DB'"
            class="cursor-pointer p-1"
          />
        </view>
        <view v-if="errors.overallRating" class="text-red-500 text-xs mt-1">{{ errors.overallRating }}</view>     
      </view>

      <!-- 评价内容 -->
      <view class="mb-4">
        <view for="comment" class="text-sm font-medium mb-1 text-ui-blue-start">评价内容</view>
        <textarea
          id="comment"
          v-model="evaluation.comment"
          :rows="3"
          class="w-full p-2 border border-neutral-gray rounded-lg focus:outline-none focus:ring-2 focus:ring-ui-blue-end focus:border-ui-blue-end focus:shadow-ui-glow"
          placeholder="请输入您的评价内容（选填）"
        ></textarea>
      </view>

      <button
        form-type="submit"
        class="px-4 py-2 rounded-lg font-medium transition-colors bg-ui-vibrant-gradient text-white hover:opacity-90 w-full"
        :disabled="loading"
      >
        {{ loading ? '提交中...' : '提交评价' }}
      </button>
    </form>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue';
import StarIcon from '@/components/icons/StarIcon.vue';

const props = defineProps({
  taskId: {
    type: [String, Number],
    required: true
  }
});

const emit = defineEmits(['submit-success', 'submit-error']);

const loading = ref(false);
const evaluation = reactive({
  serviceAttitude: 0,
  serviceQuality: 0,
  overallRating: 0,
  comment: ''
});
const errors = reactive({
  serviceAttitude: '',
  serviceQuality: '',
  overallRating: ''
});

// 验证表单
const validateForm = () => {
  let isValid = true;

  // 重置错误
  errors.serviceAttitude = '';
  errors.serviceQuality = '';
  errors.overallRating = '';

  // 验证服务态度评分
  if (!evaluation.serviceAttitude) {
    errors.serviceAttitude = '请为服务态度评分';
    isValid = false;
  }

  // 验证服务质量评分
  if (!evaluation.serviceQuality) {
    errors.serviceQuality = '请为服务质量评分';
    isValid = false;
  }

  // 验证总体评价评分
  if (!evaluation.overallRating) {
    errors.overallRating = '请为总体评价评分';
    isValid = false;
  }

  return isValid;
};

// 提交评价
const submitEvaluation = async () => {
  if (!validateForm()) return;

  loading.value = true;

  try {
    // 构造提交数据
    const data = {
      taskId: Number(props.taskId),
      serviceAttitude: evaluation.serviceAttitude,
      serviceQuality: evaluation.serviceQuality,
      overallRating: evaluation.overallRating,
      comment: evaluation.comment
    };

    emit('submit-success', data);
  } catch (error) {
    console.error('提交评价失败:', error);
    emit('submit-error', error);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
/* 保留组件样式 */
</style> 