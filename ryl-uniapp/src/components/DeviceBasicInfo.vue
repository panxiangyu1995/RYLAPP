<template>
  <view>
    <!-- 仪器名称 -->
    <view class="mb-4">
      <view class="text-sm font-medium mb-1 text-ui-blue-start">仪器名称 <text class="text-red-500">*</text></view>
      <input 
        id="deviceName" 
        :value="modelValue.name"
        @input="(e) => updateField('name', e.detail.value)"
        type="text" 
        class="w-full p-2 bg-white border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-ui-blue-end focus:border-ui-blue-end focus:shadow-ui-glow" 
        placeholder="请输入仪器名称"
        required
      />
    </view>
    
    <!-- 仪器类型 -->
    <view class="mb-4">
      <view class="text-sm font-medium mb-1 text-ui-blue-start">仪器类型 <text class="text-red-500">*</text></view>
      <picker
        :value="deviceTypeIndex"
        :range="deviceTypes"
        @change="onDeviceTypeChange"
        class="w-full p-2 bg-white border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-ui-blue-end focus:border-ui-blue-end focus:shadow-ui-glow"
      >
        <view :class="{ 'text-gray-500': deviceTypeIndex === -1 }">
          {{ deviceTypeIndex === -1 ? '请选择仪器类型' : deviceTypes[deviceTypeIndex] }}
        </view>
      </picker>
    </view>
    
    <!-- 品牌 -->
    <view class="mb-4">
      <view class="text-sm font-medium mb-1 text-ui-blue-start">品牌 <text class="text-red-500">*</text></view>
      <input 
        id="deviceBrand" 
        :value="modelValue.brand"
        @input="(e) => updateField('brand', e.detail.value)"
        type="text" 
        class="w-full p-2 bg-white border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-ui-blue-end focus:border-ui-blue-end focus:shadow-ui-glow" 
        placeholder="请输入品牌"
        required
      />
    </view>
    
    <!-- 型号 -->
    <view class="mb-4">
      <view class="text-sm font-medium mb-1 text-ui-blue-start">型号 <text class="text-red-500">*</text></view>
      <input 
        id="deviceModel" 
        :value="modelValue.model"
        @input="(e) => updateField('model', e.detail.value)"
        type="text" 
        class="w-full p-2 bg-white border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-ui-blue-end focus:border-ui-blue-end focus:shadow-ui-glow" 
        placeholder="请输入型号"
        required
      />
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue';

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

const deviceTypeIndex = computed(() => {
  return deviceTypes.indexOf(props.modelValue.type);
});

const onDeviceTypeChange = (e) => {
  const index = e.detail.value;
  updateField('type', deviceTypes[index]);
};

const updateField = (field, value) => {
  emit('update:modelValue', { ...props.modelValue, [field]: value });
};
</script> 