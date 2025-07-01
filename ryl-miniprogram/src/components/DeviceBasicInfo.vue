<template>
  <div>
    <!-- 仪器名称 -->
    <div class="mb-4">
      <label for="deviceName" class="form-label">仪器名称 <span class="text-red-500">*</span></label>
      <input 
        id="deviceName" 
        :value="modelValue.name"
        @input="updateField('name', $event.target.value)"
        type="text" 
        class="form-input" 
        placeholder="请输入仪器名称"
        required
      />
    </div>
    
    <!-- 仪器类型 -->
    <div class="mb-4">
      <label for="deviceType" class="form-label">仪器类型 <span class="text-red-500">*</span></label>
      <select
        id="deviceType"
        :value="modelValue.type"
        @change="updateField('type', $event.target.value)"
        class="form-input"
        required
      >
        <option value="" disabled>请选择仪器类型</option>
        <option v-for="type in deviceTypes" :key="type" :value="type">{{ type }}</option>
      </select>
    </div>
    
    <!-- 品牌 -->
    <div class="mb-4">
      <label for="deviceBrand" class="form-label">品牌 <span class="text-red-500">*</span></label>
      <input 
        id="deviceBrand" 
        :value="modelValue.brand"
        @input="updateField('brand', $event.target.value)"
        type="text" 
        class="form-input" 
        placeholder="请输入品牌"
        required
      />
    </div>
    
    <!-- 型号 -->
    <div class="mb-4">
      <label for="deviceModel" class="form-label">型号 <span class="text-red-500">*</span></label>
      <input 
        id="deviceModel" 
        :value="modelValue.model"
        @input="updateField('model', $event.target.value)"
        type="text" 
        class="form-input" 
        placeholder="请输入型号"
        required
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  modelValue: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['update:modelValue']);

// 仪器类型列表
const deviceTypes = [
  '色谱、光谱类',
  '光谱类',
  '生命科学类',
  '三项一柜基础仪器类',
  '环境监测类',
  '主板类',
  '电气类',
  '其他类'
];

const updateField = (field, value) => {
  emit('update:modelValue', { ...props.modelValue, [field]: value });
};
</script> 