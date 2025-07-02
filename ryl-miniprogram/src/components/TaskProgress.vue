<template>
  <div class="mb-6">
    <h4 class="text-lg font-medium mb-4">任务进度</h4>
    
    <!-- 进度步骤 -->
    <div class="relative">
      <div class="absolute left-4 top-0 bottom-0 w-0.5 bg-neutral-gray"></div>
      
      <div 
        v-for="(step, index) in steps" 
        :key="index"
        class="flex mb-6 last:mb-0"
      >
        <!-- 步骤圆点 -->
        <div 
          class="w-8 h-8 rounded-full flex items-center justify-center z-10 mr-4"
          :class="[
            index <= currentStep 
              ? 'bg-primary-medium text-white'
              : 'bg-neutral-light text-neutral-gray'
          ]"
        >
          {{ index + 1 }}
        </div>
        
        <!-- 步骤内容 -->
        <div class="flex-1">
          <h5 
            class="font-medium mb-1"
            :class="[
              index <= currentStep 
                ? 'text-primary-medium'
                : 'text-neutral-gray'
            ]"
          >
            {{ step }}
          </h5>
          
          <!-- 当前步骤的额外内容 -->
          <div v-if="index === currentStep" class="mt-2 p-3 bg-neutral-light bg-opacity-50 rounded-lg">
            <!-- 步骤提示信息 -->
            <div class="mb-3 text-sm">
              <p>{{ getStepPrompt(index) }}</p>
            </div>
            
            <!-- 动态内容显示 -->
            <div v-if="stepContent" class="mt-2">
              <!-- 评估内容 -->
              <div v-if="hasAssessmentContent" class="mb-3">
                <h6 class="font-medium text-sm mb-1">{{ getAssessmentTitle() }}：</h6>
                <p class="text-sm">{{ stepContent.diagnosis || stepContent.assessment || '' }}</p>
                
                <!-- 图片展示 -->
                <div v-if="stepContent.images && stepContent.images.length" class="grid grid-cols-3 gap-2 mt-2">
                  <div 
                    v-for="(image, imgIndex) in stepContent.images" 
                    :key="imgIndex"
                    class="w-full aspect-square rounded-lg overflow-hidden bg-white relative group"
                  >
                    <img 
                      :src="image.url" 
                      class="w-full h-full object-cover" 
                      :alt="image.description"
                      @click="previewImage(image.url, stepContent.images.map(img => img.url))"
                    />
                    <div class="absolute bottom-0 left-0 right-0 bg-black bg-opacity-50 p-1 opacity-0 group-hover:opacity-100 transition-opacity flex justify-center">
                      <button @click.stop="downloadImage(image.url, image.id)" class="text-white text-xs mx-1">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 方案内容 -->
              <div v-if="hasSolutionContent" class="mb-3">
                <h6 class="font-medium text-sm mb-1">{{ getSolutionTitle() }}：</h6>
                <p class="text-sm">{{ stepContent.solution || '' }}</p>
                
                <!-- 配件列表 -->
                <div v-if="stepContent.parts_needed || stepContent.parts_replaced" class="mt-2">
                  <h6 class="font-medium text-xs mb-1">所需配件：</h6>
                  <div class="bg-white rounded-lg p-2">
                    <div 
                      v-for="(part, partIndex) in (stepContent.parts_needed || stepContent.parts_replaced)" 
                      :key="partIndex"
                      class="flex justify-between text-xs mb-1 last:mb-0"
                    >
                      <span>{{ part.name }}</span>
                      <span>× {{ part.quantity }}</span>
                    </div>
                  </div>
                </div>
                
                <!-- 报价信息 -->
                <div v-if="stepContent.estimated_cost || stepContent.cost" class="mt-2">
                  <h6 class="font-medium text-xs mb-1">报价：</h6>
                  <div class="bg-white rounded-lg p-2">
                    <div class="flex justify-between items-center">
                      <span class="text-primary-dark font-medium">{{ stepContent.estimated_cost || stepContent.cost }} 元</span>
                      <button 
                        v-if="!stepContent.price_confirmed && index === 3"
                        @click="confirmPrice"
                        class="bg-primary-medium text-white text-xs py-1 px-3 rounded-full"
                        :disabled="confirmingPrice"
                      >
                        {{ confirmingPrice ? '确认中...' : '确认报价' }}
                      </button>
                      <span 
                        v-else-if="stepContent.price_confirmed"
                        class="text-success-medium text-xs bg-success-light py-1 px-3 rounded-full"
                      >
                        已确认
                      </span>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 验证报告内容 -->
              <div v-if="index === 5 && stepContent.report_number" class="mb-3">
                <h6 class="font-medium text-sm mb-1">验证报告：</h6>
                <div class="bg-white rounded-lg p-2 flex justify-between items-center">
                  <span class="text-sm">报告编号: {{ stepContent.report_number }}</span>
                  <button class="text-primary-medium text-sm">查看</button>
                </div>
              </div>
              
              <!-- 服务评价表单 -->
              <div v-if="index === 6" class="mb-3">
                <h6 class="font-medium text-sm mb-2">请对本次服务进行评价：</h6>
                <!-- 评价表单内容在ServiceEvaluation组件中 -->
              </div>
            </div>
            
            <!-- 附件列表 -->
            <div v-if="stepContent && stepContent.files && stepContent.files.length">
              <h6 class="font-medium text-sm mb-1">附件：</h6>
              <div 
                v-for="(file, fileIndex) in stepContent.files" 
                :key="fileIndex"
                class="flex items-center p-2 bg-white rounded-lg mb-1 last:mb-0"
              >
                <span class="text-primary-medium mr-2">{{ getFileIcon(file.name) }}</span>
                <span class="flex-1 truncate text-sm">{{ file.name }}</span>
                <div class="flex">
                  <a :href="file.url" target="_blank" class="text-primary-medium text-sm mr-2">查看</a>
                  <button @click="downloadFile(file.url, file.id, file.name)" class="text-primary-medium text-sm">下载</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useTaskStore } from '@/stores/task';

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
    type: String,
    required: true
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
    emit('confirm-price', props.taskId);
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
    1: props.stepContent && props.stepContent.appointment_time 
       ? `等待上门：已约定上门时间为${props.stepContent.appointment_time}，请保持电话畅通，工程师会按约定时间上门` 
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
const previewImage = (current, urls) => {
  if (typeof wx !== 'undefined') {
    wx.previewImage({
      current, // 当前显示图片的http链接
      urls, // 需要预览的图片http链接列表
      success: () => {
        console.log('图片预览成功');
      },
      fail: (err) => {
        console.error('图片预览失败:', err);
        wx.showToast({ title: '图片预览失败', icon: 'none' });
      }
    });
  } else {
    // 非微信环境，使用普通的图片预览方式
    window.open(current, '_blank');
  }
};

// 图片下载功能
const downloadImage = async (url, imageId) => {
  try {
    // 获取下载链接
    const downloadUrl = await taskStore.downloadImage(imageId);
    
    if (typeof wx !== 'undefined') {
      // 微信小程序环境
      wx.showLoading({ title: '下载中...' });
      
      wx.downloadFile({
        url: downloadUrl,
        success: (res) => {
          if (res.statusCode === 200) {
            // 下载成功后保存到相册
            wx.saveImageToPhotosAlbum({
              filePath: res.tempFilePath,
              success: () => {
                wx.showToast({ title: '图片已保存到相册', icon: 'success' });
              },
              fail: (err) => {
                console.error('保存图片失败:', err);
                wx.showToast({ title: '保存图片失败', icon: 'none' });
              }
            });
          } else {
            wx.showToast({ title: '下载图片失败', icon: 'none' });
          }
        },
        fail: (err) => {
          console.error('下载图片失败:', err);
          wx.showToast({ title: '下载图片失败', icon: 'none' });
        },
        complete: () => {
          wx.hideLoading();
        }
      });
    } else {
      // 浏览器环境
      const a = document.createElement('a');
      a.href = downloadUrl;
      a.download = `image-${imageId}`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
    }
  } catch (error) {
    console.error('下载图片失败:', error);
    if (typeof wx !== 'undefined') {
      wx.showToast({ title: '下载图片失败', icon: 'none' });
    }
  }
};

// 附件下载功能
const downloadFile = async (url, fileId, fileName) => {
  try {
    // 获取下载链接
    const downloadUrl = await taskStore.downloadAttachment(props.taskId, fileId);
    
    if (typeof wx !== 'undefined') {
      // 微信小程序环境
      wx.showLoading({ title: '下载中...' });
      
      wx.downloadFile({
        url: downloadUrl,
        success: (res) => {
          if (res.statusCode === 200) {
            // 下载成功后打开文件
            wx.openDocument({
              filePath: res.tempFilePath,
              showMenu: true,
              success: () => {
                wx.showToast({ title: '文件已打开', icon: 'success' });
              },
              fail: (err) => {
                console.error('打开文件失败:', err);
                wx.showToast({ title: '打开文件失败', icon: 'none' });
              }
            });
          } else {
            wx.showToast({ title: '下载文件失败', icon: 'none' });
          }
        },
        fail: (err) => {
          console.error('下载文件失败:', err);
          wx.showToast({ title: '下载文件失败', icon: 'none' });
        },
        complete: () => {
          wx.hideLoading();
        }
      });
    } else {
      // 浏览器环境
      const a = document.createElement('a');
      a.href = downloadUrl;
      a.download = fileName || `file-${fileId}`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
    }
  } catch (error) {
    console.error('下载文件失败:', error);
    if (typeof wx !== 'undefined') {
      wx.showToast({ title: '下载文件失败', icon: 'none' });
    }
  }
};
</script> 