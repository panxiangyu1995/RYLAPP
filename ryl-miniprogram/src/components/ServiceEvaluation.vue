<template>
  <div class="bg-white p-4 rounded-lg">
    <h4 class="text-lg font-medium mb-4">服务评价</h4>
    
    <form @submit.prevent="submitEvaluation">
      <!-- 服务态度评分 -->
      <div class="mb-4">
        <label class="form-label">服务态度 <span class="text-red-500">*</span></label>
        <div class="flex items-center">
          <div 
            v-for="i in 5" 
            :key="i"
            @click="evaluation.serviceAttitude = i"
            class="cursor-pointer p-1"
          >
            <svg 
              xmlns="http://www.w3.org/2000/svg" 
              viewBox="0 0 24 24" 
              width="24" 
              height="24" 
              :fill="i <= evaluation.serviceAttitude ? '#FFC107' : '#D1D5DB'"
            >
              <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
            </svg>
          </div>
        </div>
        <p v-if="errors.serviceAttitude" class="text-red-500 text-xs mt-1">{{ errors.serviceAttitude }}</p>
      </div>
      
      <!-- 服务质量评分 -->
      <div class="mb-4">
        <label class="form-label">服务质量 <span class="text-red-500">*</span></label>
        <div class="flex items-center">
          <div 
            v-for="i in 5" 
            :key="i"
            @click="evaluation.serviceQuality = i"
            class="cursor-pointer p-1"
          >
            <svg 
              xmlns="http://www.w3.org/2000/svg" 
              viewBox="0 0 24 24" 
              width="24" 
              height="24" 
              :fill="i <= evaluation.serviceQuality ? '#FFC107' : '#D1D5DB'"
            >
              <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
            </svg>
          </div>
        </div>
        <p v-if="errors.serviceQuality" class="text-red-500 text-xs mt-1">{{ errors.serviceQuality }}</p>
      </div>
      
      <!-- 总体评价评分 -->
      <div class="mb-4">
        <label class="form-label">总体评价 <span class="text-red-500">*</span></label>
        <div class="flex items-center">
          <div 
            v-for="i in 5" 
            :key="i"
            @click="evaluation.overallRating = i"
            class="cursor-pointer p-1"
          >
            <svg 
              xmlns="http://www.w3.org/2000/svg" 
              viewBox="0 0 24 24" 
              width="24" 
              height="24" 
              :fill="i <= evaluation.overallRating ? '#FFC107' : '#D1D5DB'"
            >
              <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
            </svg>
          </div>
        </div>
        <p v-if="errors.overallRating" class="text-red-500 text-xs mt-1">{{ errors.overallRating }}</p>
      </div>
      
      <!-- 评价内容 -->
      <div class="mb-4">
        <label for="comment" class="form-label">评价内容</label>
        <textarea 
          id="comment"
          v-model="evaluation.comment"
          rows="3"
          class="form-input"
          placeholder="请输入您的评价内容（选填）"
        ></textarea>
      </div>
      
      <button 
        type="submit" 
        class="btn btn-primary w-full" 
        :disabled="loading"
      >
        {{ loading ? '提交中...' : '提交评价' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';

const props = defineProps({
  taskId: {
    type: String,
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
      taskId: props.taskId,
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