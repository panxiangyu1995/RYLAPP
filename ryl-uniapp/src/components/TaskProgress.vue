<template>
  <view class="mb-6">
    <view class="text-lg font-medium mb-4 text-ui-blue-start">订单进度</view>
    
    <!-- 进度步骤 -->
    <view class="relative">
      <view class="absolute left-4 top-0 bottom-0 w-0.5 bg-ui-blue-start"></view>
      
      <view 
        v-for="(step, index) in steps" 
        :key="index"
        class="flex mb-6 last:mb-0"
      >
        <!-- 步骤圆点 -->
        <view 
          class="w-8 h-8 rounded-full flex items-center justify-center z-10 mr-4"
          :class="[
            index <= currentStep 
              ? 'bg-ui-vibrant-gradient text-white'
              : 'bg-neutral-light text-neutral-gray'
          ]"
        >
          {{ index + 1 }}
        </view>
        
        <!-- 步骤内容 -->
        <view class="flex-1">
          <view 
            class="font-medium mb-1"
            :class="[
              index <= currentStep 
                ? 'text-ui-blue-start'
                : 'text-neutral-gray'
            ]"
          >
            {{ step }}
          </view>
          
          <!-- 当前步骤的额外内容 -->
          <view v-if="index === currentStep" class="mt-2 p-3 bg-white border border-gray-200 rounded-lg">
            <!-- 步骤提示信息 -->
            <view class="mb-3 text-sm">
              <view>{{ (stepContent && stepContent.description) ? stepContent.description : getStepPrompt(index) }}</view>
            </view>
            
            <!-- 动态内容显示 -->
            <view v-if="stepContent" class="mt-2">
              <!-- 评估内容 -->
              <view v-if="hasAssessmentContent" class="mb-3">
                <view class="font-medium text-sm mb-1">{{ getAssessmentTitle() }}：</view>
                <view class="text-sm">{{ stepContent.diagnosis || stepContent.assessment || '' }}</view>
                
                <!-- 图片展示 -->
                <view v-if="stepContent.images && stepContent.images.length" class="grid grid-cols-3 gap-2 mt-2">
                  <view 
                    v-for="(image, imgIndex) in stepContent.images" 
                    :key="imgIndex"
                    class="w-full aspect-square rounded-lg overflow-hidden bg-white relative group"
                  >
                    <image 
                      :src="image.url" 
                      class="w-full h-full object-cover" 
                      :alt="image.description"
                      @click="previewImage(image.url, stepContent.images.map(img => img.url))"
                    />
                    <view class="absolute bottom-0 left-0 right-0 bg-black bg-opacity-50 p-1 transition-opacity flex justify-center">
                      <button @click.stop="downloadImage(image.url, image.id)" class="text-white text-xs mx-1">
                        <DownloadIcon :size="16" color="white" />
                      </button>
                    </view>
                  </view>
                </view>
              </view>
              
              <!-- 方案内容 -->
              <view v-if="hasSolutionContent" class="mb-3">
                <view class="font-medium text-sm mb-1">{{ getSolutionTitle() }}：</view>
                <view class="text-sm">{{ stepContent.solution || '' }}</view>
                
                <!-- 配件列表 -->
                <view v-if="stepContent.partsNeeded || stepContent.partsReplaced" class="mt-2">
                  <view class="font-medium text-xs mb-1">所需配件：</view>
                  <view class="bg-white rounded-lg p-2">
                    <view 
                      v-for="(part, partIndex) in (stepContent.partsNeeded || stepContent.partsReplaced)" 
                      :key="partIndex"
                      class="flex justify-between text-xs mb-1 last:mb-0"
                    >
                      <view>{{ part.name }}</view>
                      <view>× {{ part.quantity }}</view>
                    </view>
                  </view>
                </view>
                
                <!-- 报价信息 -->
                <view v-if="stepContent && (stepContent.estimatedCost || stepContent.cost || stepContent.text && stepContent.text.includes('报价'))" class="mt-2">
                  <view class="font-medium text-xs mb-1">报价：</view>
                  <view class="bg-white rounded-lg p-2">
                    <view class="flex justify-between items-center">
                      <view class="text-ui-text-black font-medium">{{ stepContent && (stepContent.estimatedCost || stepContent.cost) ? (stepContent.estimatedCost || stepContent.cost) + ' 元' : '' }}</view>
                      <button 
                        v-if="!props.priceConfirmed && index === 3"
                        @click="confirmPrice"
                        class="bg-ui-vibrant-gradient text-white text-xs py-1 px-3 rounded-full"
                        :disabled="confirmingPrice"
                      >
                        {{ confirmingPrice ? '确认中...' : '确认报价' }}
                      </button>
                      <view 
                        v-else-if="props.priceConfirmed === 1"
                        class="text-green-500 text-xs bg-green-100 py-1 px-3 rounded-full"
                      >
                        已确认
                      </view>
                    </view>
                  </view>
                </view>
              </view>
              
              <!-- 验证报告内容 -->
              <view v-if="index === 5 && stepContent.reportNumber" class="mb-3">
                <view class="font-medium text-sm mb-1">验证报告：</view>
                <view class="bg-white rounded-lg p-2 flex justify-between items-center">
                  <view class="text-sm">报告编号: {{ stepContent.reportNumber }}</view>
                  <button class="text-ui-blue-start text-sm">查看</button>
                </view>
              </view>
              
              <!-- 服务评价表单 -->
              <view v-if="index === 6" class="mb-3">
                <view class="font-medium text-sm mb-2">请对本次服务进行评价：</view>
                <!-- 评价表单内容在ServiceEvaluation组件中 -->
              </view>
            </view>
            
            <!-- 附件列表 -->
            <view v-if="stepContent && stepContent.files && stepContent.files.length > 0" class="mt-3">
              <view class="font-medium text-sm mb-1">附件：</view>
              <view 
                v-for="(file, fileIndex) in stepContent.files" 
                :key="fileIndex"
                class="flex items-center p-2 bg-white rounded-lg mb-1 last:mb-0"
              >
                <view class="text-ui-blue-start mr-2">{{ getFileIcon(file.fileName || file.name) }}</view>
                <view class="flex-1 truncate text-sm">{{ file.fileName || file.name }}</view>
                <view class="flex">
                  <navigator :url="file.fileUrl || file.url" target="miniProgram" class="text-ui-blue-start text-sm mr-2">查看</navigator>
                  <button @click="downloadFile(file.fileUrl || file.url, file.id, file.fileName || file.name)" class="text-ui-blue-start text-sm">下载</button>
                </view>
              </view>
            </view>
            
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useTaskStore } from '@/stores/task';
import DownloadIcon from '@/components/icons/DownloadIcon.vue';

const taskStore = useTaskStore();

const props = defineProps({
  taskType: {
    type: String,
    required: true
  },
  currentStep: {
    type: Number,
    default: 0
  },
  stepContent: {
    type: Object,
    default: () => null
  },
  taskId: {
    type: [String, Number],
    required: true
  },
  priceConfirmed: {
    type: Number,
    default: 0
  }
});

const emit = defineEmits(['confirm-price']);

const confirmingPrice = ref(false);

// 确认报价
const confirmPrice = async () => {
  if (confirmingPrice.value) return;
  
  confirmingPrice.value = true;
  try {
    // 发出确认报价事件，让父组件处理API调用
    emit('confirm-price', Number(props.taskId));
  } catch (error) {
    console.error('确认报价失败:', error);
  } finally {
    confirmingPrice.value = false;
  }
};

// 根据任务类型获取步骤
const steps = computed(() => {
  const stepMap = {
    'repair': ['已接单', '判断是否需要上门', '检修完成', '维修方案和报价', '维修中', '验证报告', '服务评价', '订单已完成'],
    'maintenance': ['已接单', '判断是否需要上门', '检修完成', '保养方案和报价', '保养中', '验证报告', '服务评价', '订单已完成'],
    'recycle': ['已接单', '判断是否需要上门', '检查完成', '回收方案和报价', '回收中', '验证报告', '服务评价', '订单已完成'],
    'leasing': ['已接单', '判断是否需要上门', '检查完成', '租赁方案和报价', '租赁中', '验证报告', '服务评价', '订单已完成'],
    'training': ['已接单', '判断是否需要上门', '培训准备完成', '培训方案和报价', '培训中', '验证报告', '服务评价', '订单已完成'],
    'verification': ['已接单', '判断是否需要上门', '验证准备完成', '验证方案和报价', '验证中', '验证报告', '服务评价', '订单已完成'],
    'selection': ['已接单', '判断是否需要上门', '选型分析完成', '选型方案和报价', '选型进行中', '验证报告', '服务评价', '订单已完成'],
    'installation': ['已接单', '判断是否需要上门', '安装准备完成', '安装方案和报价', '安装中', '验证报告', '服务评价', '订单已完成']
  };
  
  return stepMap[props.taskType] || stepMap['repair'];
});

// 获取文件图标
const getFileIcon = (fileName) => {
  const extension = fileName.split('.').pop().toLowerCase();
  
  if (extension === 'pdf') return '📄';
  if (extension === 'doc' || extension === 'docx') return '📝';
  if (extension === 'xls' || extension === 'xlsx') return '📊';
  
  return '📎';
};

// 获取步骤提示信息
const getStepPrompt = (stepIndex) => {
  const defaultPrompts = {
    0: '请保持电话畅通，我们稍后会联系您',
    1: props.stepContent && props.stepContent.appointmentTime 
       ? `等待上门：已约定上门时间为${props.stepContent.appointmentTime}，请保持电话畅通，工程师会按约定时间上门` 
       : '请保持电话畅通，工程师稍后会联系您',
    2: `${getCheckTitle()}：请稍等，正在为您出${getSolutionTitle()}和报价`,
    3: '', // 动态内容，在模板中显示
    4: `工程师${getServiceTypeText()}中`,
    5: '已完成验证，点击查看验证报告',
    6: '请对我们的服务进行评价',
    7: '祝您生活愉快！'
  };
  
  return defaultPrompts[stepIndex] || '';
};

// 获取服务类型文本
const getServiceTypeText = () => {
  const typeMap = {
    'repair': '维修',
    'maintenance': '保养',
    'recycle': '回收',
    'leasing': '租赁',
    'training': '培训',
    'verification': '验证',
    'selection': '选型',
    'installation': '安装'
  };
  
  return typeMap[props.taskType] || '服务';
};

// 获取检查/评估标题
const getCheckTitle = () => {
  const titleMap = {
    'repair': '维修评估',
    'maintenance': '保养评估',
    'recycle': '回收评估',
    'leasing': '租赁评估',
    'training': '培训评估',
    'verification': '验证评估',
    'selection': '选型评估',
    'installation': '安装评估'
  };
  
  return titleMap[props.taskType] || '评估';
};

// 获取方案标题
const getSolutionTitle = () => {
  const titleMap = {
    'repair': '维修方案',
    'maintenance': '保养方案',
    'recycle': '回收方案',
    'leasing': '租赁方案',
    'training': '培训方案',
    'verification': '验证方案',
    'selection': '选型方案',
    'installation': '安装方案'
  };
  
  return titleMap[props.taskType] || '方案';
};

// 获取评估标题
const getAssessmentTitle = () => {
  const titleMap = {
    'repair': '维修评估',
    'maintenance': '保养评估',
    'recycle': '回收评估',
    'leasing': '租赁评估',
    'training': '培训评估',
    'verification': '验证评估',
    'selection': '选型评估',
    'installation': '安装评估'
  };
  
  return titleMap[props.taskType] || '评估';
};

// 是否有评估内容
const hasAssessmentContent = computed(() => {
  return props.currentStep === 2 || props.currentStep === 3;
});

// 是否有方案内容
const hasSolutionContent = computed(() => {
  return props.currentStep === 3;
});

// 图片预览功能
const previewImage = (currentUrl, allUrls) => {
  uni.previewImage({
    current: currentUrl,
    urls: allUrls,
  });
};

// 图片下载功能
const downloadImage = async (url, imageId) => {
  try {
    // 获取下载链接
    const downloadUrl = await taskStore.downloadImage(Number(imageId));
    
    uni.showLoading({ title: '下载中...' });
    
    uni.downloadFile({
      url: downloadUrl,
      success: (res) => {
        if (res.statusCode === 200) {
          // 下载成功后保存到相册
          uni.saveFile({
            tempFilePath: res.tempFilePath,
            success: (saveRes) => {
              uni.hideLoading();
              uni.saveImageToPhotosAlbum({
                filePath: saveRes.savedFilePath,
                success: () => {
                  uni.showToast({ title: '图片已保存到相册', icon: 'success' });
                },
                fail: (err) => {
                  console.error('保存图片失败:', err);
                  uni.showToast({ title: '保存图片失败', icon: 'none' });
                }
              });
            },
            fail: () => {
              uni.hideLoading();
              uni.showToast({ title: '保存失败', icon: 'none' });
            }
          });
        } else {
          uni.hideLoading();
          uni.showToast({ title: '下载图片失败', icon: 'none' });
        }
      },
      fail: (err) => {
        console.error('下载图片失败:', err);
        uni.hideLoading();
        uni.showToast({ title: '下载图片失败', icon: 'none' });
      },
      complete: () => {
        uni.hideLoading();
      }
    });
  } catch (error) {
    console.error('下载图片失败:', error);
    if (typeof wx !== 'undefined') {
      uni.showToast({ title: '下载图片失败', icon: 'none' });
    }
  }
};

// 附件下载功能
const downloadFile = (fileUrl, fileId, fileName) => {
  // 如果有fileId，则使用API下载，否则直接使用fileUrl
  if (fileId) {
    uni.showLoading({ title: '下载中...' });
    // 使用store中的方法下载附件
    taskStore.downloadAttachment(fileId).then(() => {
      uni.hideLoading();
      uni.showToast({ title: '下载成功', icon: 'success' });
    }).catch(() => {
      uni.hideLoading();
      uni.showToast({ title: '下载失败', icon: 'none' });
    });
  } else {
    // 直接使用URL下载
    uni.showLoading({ title: '下载中...' });
    uni.downloadFile({
      url: fileUrl,
      success: (res) => {
        if (res.statusCode === 200) {
          uni.saveFile({
            tempFilePath: res.tempFilePath,
            success: (saveRes) => {
              uni.hideLoading();
              uni.showToast({
                title: '下载成功',
                icon: 'success',
                success: () => {
                  uni.openDocument({
                    filePath: saveRes.savedFilePath,
                    showMenu: true
                  });
                }
              });
            },
            fail: () => {
              uni.hideLoading();
              uni.showToast({ title: '保存失败', icon: 'none' });
            }
          });
        } else {
          uni.hideLoading();
          uni.showToast({ title: '下载失败', icon: 'none' });
        }
      },
      fail: () => {
        uni.hideLoading();
        uni.showToast({ title: '下载失败', icon: 'none' });
      }
    });
  }
};
</script> 