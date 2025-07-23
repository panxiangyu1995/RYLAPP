<template>
  <div class="create-task-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>创建新任务</h1>
      <div class="header-right">
        <button class="save-btn" @click="saveAsDraft">
          保存草稿
        </button>
      </div>
    </header>

    <!-- 表单内容 -->
    <div class="form-container">
      <form @submit.prevent="submitTask">
        <!-- 任务类型 -->
        <div class="form-section">
          <h3 class="section-title">任务类型</h3>
          <div class="task-type-grid">
            <div 
              v-for="(type, index) in taskTypes" 
              :key="index"
              :class="['task-type-item', { active: form.taskType === type.value }]"
              @click="handleTaskTypeChange(type.value)"
            >
              <i :class="['task-type-icon', type.icon]"></i>
              <span class="task-type-name">{{ type.name }}</span>
            </div>
          </div>
        </div>

        <!-- 客户信息 -->
        <div class="form-section">
          <h3 class="section-title">客户信息</h3>
          
          <div class="form-group">
            <label>选择客户</label>
            <div class="customer-selector">
              <input 
                type="text" 
                v-model="customerSearch" 
                placeholder="搜索客户..." 
                @focus="showCustomerDropdown = true"
                @blur="handleCustomerBlur"
              >
              <div class="customer-dropdown" v-if="showCustomerDropdown">
                <div 
                  v-for="(customer, index) in filteredCustomers" 
                  :key="index"
                  class="customer-item"
                  @click="selectCustomer(customer)"
                >
                  <div class="customer-info">
                    <p class="customer-name">{{ customer.name }}</p>
                    <p class="customer-address">{{ customer.address }}</p>
                  </div>
                  <span :class="['customer-level', `level-${customer.level}`]">{{ customer.level }}级</span>
                </div>
                <div class="create-new-customer" @click="createNewCustomer">
                  <i class="fas fa-plus"></i>
                  <span>创建新客户</span>
                </div>
              </div>
            </div>
          </div>

          <div class="form-group" v-if="form.customer">
            <div class="selected-customer">
              <div class="customer-info">
                <h4>{{ form.customer.name }}</h4>
                <p class="customer-contact">联系人: {{ form.customer.contact }}</p>
                <p class="customer-phone">电话: {{ form.customer.phone }}</p>
                <p class="customer-address">地址: {{ form.customer.address }}</p>
              </div>
              <span :class="['customer-level', `level-${form.customer.level}`]">{{ form.customer.level }}级</span>
            </div>
          </div>
        </div>

        <!-- 销售人员信息 -->
        <div class="form-section">
          <h3 class="section-title">对接销售</h3>
          
          <div class="form-group">
            <label>选择销售人员（可选）</label>
            <div class="sales-selector">
              <input 
                type="text" 
                v-model="salesSearch" 
                placeholder="搜索销售人员..." 
                @focus="showSalesDropdown = true"
                @blur="handleSalesBlur"
              >
              <div class="sales-dropdown" v-if="showSalesDropdown">
                <div 
                  v-for="(sales, index) in filteredSales" 
                  :key="index"
                  class="sales-item"
                  @click="selectSales(sales)"
                >
                  <div class="sales-info">
                    <p class="sales-name">{{ sales.name }}</p>
                    <p class="sales-department">{{ sales.department }}</p>
                  </div>
                </div>
                <div class="clear-sales" @click="clearSales">
                  <i class="icon-times"></i>
                  <span>不选择销售人员</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="form-group" v-if="form.sales">
            <div class="selected-sales">
              <div class="sales-info">
                <h4>{{ form.sales.name }}</h4>
                <p class="sales-department">{{ form.sales.department }}</p>
                <p class="sales-phone">电话: {{ form.sales.phone }}</p>
              </div>
              <button type="button" class="remove-sales" @click="clearSales">
                <i class="icon-times"></i>
              </button>
            </div>
          </div>
        </div>

        <!-- 任务信息 -->
        <div class="form-section">
          <h3 class="section-title">任务信息</h3>
          
          <div class="form-group">
            <label>任务标题</label>
            <input 
              type="text" 
              v-model="form.title" 
              placeholder="输入任务标题"
              required
            >
          </div>
          
          <div class="form-group">
            <label>紧急程度</label>
            <div class="priority-selector">
              <div 
                v-for="(priority, index) in priorities" 
                :key="index"
                :class="['priority-item', { active: form.priority === priority.value }]"
                @click="form.priority = priority.value"
              >
                <span class="priority-icon" :style="{ backgroundColor: priority.color }"></span>
                <span class="priority-name">{{ priority.name }}</span>
              </div>
            </div>
          </div>
          
          <div class="form-group">
            <label>预计开始时间</label>
            <input 
              type="datetime-local" 
              v-model="form.startTime"
              required
            >
          </div>
          
          <div class="form-group">
            <label>预计结束时间</label>
            <input 
              type="datetime-local" 
              v-model="form.endTime"
              required
            >
          </div>
        </div>

        <!-- 设备信息 -->
        <div class="form-section">
          <h3 class="section-title">{{ form.taskType === 'selection' ? '需求信息' : '设备信息' }}</h3>
          
          <!-- 仪器选型特定字段 -->
          <template v-if="form.taskType === 'selection'">
            <div class="form-group">
              <label>仪器用途</label>
              <input 
                type="text" 
                v-model="form.purpose" 
                placeholder="输入仪器用途或名称"
                required
              >
            </div>
            
            <div class="form-group">
              <label>需求描述</label>
              <textarea 
                v-model="form.description" 
                placeholder="描述具体需求..."
                rows="4"
                required
              ></textarea>
            </div>
            
            <div class="form-group">
              <label>需求数量</label>
              <input 
                type="number" 
                v-model="form.quantity" 
                min="1"
                required
              >
            </div>
          </template>
          
          <!-- 其他任务类型通用设备信息 -->
          <template v-else>
            <div class="form-group">
              <label>设备名称</label>
              <input 
                type="text" 
                v-model="form.deviceName" 
                placeholder="输入设备名称"
                required
              >
            </div>
            
            <div class="form-group">
              <label>设备型号</label>
              <input 
                type="text" 
                v-model="form.deviceModel" 
                placeholder="输入设备型号"
              >
            </div>
            
            <div class="form-group">
              <label>设备品牌</label>
              <input 
                type="text" 
                v-model="form.deviceBrand" 
                placeholder="输入设备品牌"
              >
            </div>
            
            <!-- 维修和保养特定字段 -->
            <div class="form-group" v-if="['repair', 'maintenance'].includes(form.taskType)">
              <label>故障描述</label>
              <textarea 
                v-model="form.description" 
                placeholder="描述故障情况..."
                rows="4"
                required
              ></textarea>
            </div>
            
            <!-- 培训特定字段 -->
            <div class="form-group" v-if="form.taskType === 'training'">
              <label>预约时间</label>
              <input 
                type="datetime-local" 
                v-model="form.appointmentTime"
                required
              >
            </div>
            
            <!-- 验证特定字段 -->
            <div class="form-group" v-if="form.taskType === 'verification'">
              <label>验证类别</label>
              <div class="verification-type-selector">
                <div 
                  v-for="(type, index) in verificationTypes" 
                  :key="index"
                  :class="['verification-type-item', { active: form.verificationType === type.value }]"
                  @click="form.verificationType = type.value"
                >
                  <span class="verification-type-name">{{ type.name }}</span>
                </div>
              </div>
            </div>
            
            <!-- 数量字段（除了培训和选型外的任务类型） -->
            <div class="form-group" v-if="form.taskType !== 'training'">
              <label>数量</label>
              <input 
                type="number" 
                v-model="form.quantity" 
                min="1"
                required
              >
            </div>
            
            <!-- 附件字段（回收和租赁） -->
            <div class="form-group" v-if="['recycle', 'leasing'].includes(form.taskType)">
              <label>附件</label>
              <textarea 
                v-model="form.attachments" 
                placeholder="描述附带的附件..."
                rows="3"
              ></textarea>
            </div>
          </template>
          
          <!-- 通用描述字段 -->
          <div class="form-group">
            <label>{{ form.taskType === 'selection' ? '其他说明' : '问题描述' }}</label>
            <textarea 
              v-model="form.description" 
              placeholder="其他补充说明..."
              rows="4"
            ></textarea>
          </div>
          
          <!-- 上传图片 -->
          <div class="form-group">
            <label>上传图片</label>
            <div class="image-uploader">
              <div 
                v-for="(image, index) in form.images" 
                :key="index"
                class="image-preview"
              >
                <img :src="image.preview" alt="预览图片">
                <button type="button" class="remove-image" @click="removeImage(index)">
                  <i class="icon-times"></i>
                </button>
              </div>
              <div class="add-image" @click="addImage">
                <i class="icon-plus"></i>
              </div>
            </div>
            <p class="help-text" v-if="form.images.length > 0">已选择 {{ form.images.length }} 张图片，将在任务创建后上传</p>
          </div>
        </div>

        <!-- 指派工程师 -->
        <div class="form-section">
          <h3 class="section-title">指派工程师</h3>
          
          <div class="form-group">
            <div class="engineer-selector">
              <div class="engineer-search">
                <input 
                  type="text" 
                  v-model="engineerSearch" 
                  placeholder="搜索工程师..." 
                  @focus="showEngineerDropdown = true"
                  @blur="handleEngineerBlur"
                >
                <div class="engineer-dropdown" v-if="showEngineerDropdown">
                  <div 
                    v-for="(engineer, index) in filteredEngineers" 
                    :key="index"
                    class="engineer-item"
                    @click="selectEngineer(engineer)"
                  >
                    <img :src="engineer.avatar" :alt="engineer.name" class="engineer-avatar">
                    <div class="engineer-info">
                      <p class="engineer-name">{{ engineer.name }}</p>
                      <p class="engineer-title">{{ engineer.title }}</p>
                    </div>
                    <span :class="['engineer-status', getStatusClass(engineer.status)]">{{ engineer.status }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="form-group" v-if="form.assignedEngineers.length > 0">
            <label>已指派工程师</label>
            <div class="assigned-engineers">
              <div 
                v-for="(engineer, index) in form.assignedEngineers" 
                :key="index"
                class="assigned-engineer"
              >
                <div class="engineer-info">
                  <img :src="engineer.avatar" :alt="engineer.name" class="engineer-avatar">
                  <div>
                    <p class="engineer-name">{{ engineer.name }}</p>
                    <p class="engineer-title">{{ engineer.title }}</p>
                  </div>
                </div>
                <button type="button" class="remove-engineer" @click="removeEngineer(index)">
                  <i class="icon-times"></i>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <button type="button" class="cancel-btn" @click="goBack">取消</button>
          <button type="submit" class="submit-btn">创建任务</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { onMounted, reactive, ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getCustomerList, getCustomerDetail } from '@/api/customer'
import { getSalesList } from '@/api/sales'
import { getEngineersList, createTask, uploadTaskImage } from '@/api/task'
import { convertLocalDateTimeToISO, formatToLocalDateTime } from '@/utils/dateUtils'
import { TASK_TYPE_FLOW_STEPS } from '@/constants/taskFlowTemplates'

export default {
  name: 'CreateTask',
  components: {},
  setup() {
    const router = useRouter()

    const goBack = () => {
      router.back()
    }

    return {
      goBack
    }
  },
  data() {
    return {
      form: {
        taskType: 'repair',
        title: '',
        priority: 'normal',
        startTime: '',
        endTime: '',
        deviceName: '',
        deviceModel: '',
        deviceBrand: '',
        description: '',
        images: [],
        selectedFiles: [],
        customer: null,
        assignedEngineers: [],
        faultDescription: '',
        quantity: 1,
        attachments: [],
        appointmentTime: '',
        verificationType: '',
        purpose: '',
        requirementDescription: '',
        sales: null,
      },
      customerSearch: '',
      engineerSearch: '',
      salesSearch: '',
      showCustomerDropdown: false,
      showEngineerDropdown: false,
      showSalesDropdown: false,
      timeoutIds: [],
      taskTypes: [
        { name: '维修', value: 'repair', icon: 'icon-wrench' },
        { name: '保养', value: 'maintenance', icon: 'icon-tools' },
        { name: '安装', value: 'installation', icon: 'icon-box-open' },
        { name: '培训', value: 'training', icon: 'icon-chalkboard-teacher' },
        { name: '验证', value: 'verification', icon: 'icon-clipboard-check' },
        { name: '选型', value: 'selection', icon: 'icon-search' },
        { name: '租赁', value: 'leasing', icon: 'icon-hand-holding-usd' },
        { name: '回收', value: 'recycle', icon: 'icon-recycle' }
      ],
      priorities: [
        { name: '低', value: 'low', color: '#60a5fa' },
        { name: '中', value: 'normal', color: '#fbbf24' },
        { name: '高', value: 'high', color: '#ef4444' }
      ],
      verificationTypes: [
        { name: 'IQ安装验证', value: 'IQ' },
        { name: 'OQ运行验证', value: 'OQ' },
        { name: 'PQ性能验证', value: 'PQ' }
      ],
      taskTypeFields: {
        repair: ['deviceName', 'deviceModel', 'deviceBrand', 'serialNumber', 'faultDescription', 'quantity'],
        maintenance: ['deviceName', 'deviceModel', 'deviceBrand', 'serialNumber', 'faultDescription', 'quantity'],
        recycle: ['deviceName', 'deviceModel', 'deviceBrand', 'serialNumber', 'quantity', 'attachments'],
        leasing: ['deviceName', 'deviceModel', 'deviceBrand', 'serialNumber', 'quantity', 'attachments'],
        training: ['deviceName', 'deviceModel', 'deviceBrand', 'serialNumber', 'appointmentTime'],
        verification: ['deviceName', 'deviceModel', 'deviceBrand', 'serialNumber', 'verificationType', 'quantity'],
        selection: ['purpose', 'requirementDescription', 'quantity'],
        installation: ['deviceName', 'deviceModel', 'deviceBrand', 'serialNumber', 'quantity']
      },
      customers: [],
      engineers: [],
      sales: [],
    }
  },
  computed: {
    filteredCustomers() {
      if (!this.customerSearch) return this.customers;
      
      const search = this.customerSearch.toLowerCase();
      return this.customers.filter(customer => 
        customer.name.toLowerCase().includes(search) || 
        customer.address.toLowerCase().includes(search)
      );
    },
    filteredEngineers() {
      if (!this.engineerSearch) return this.engineers;
      
      const search = this.engineerSearch.toLowerCase();
      return this.engineers.filter(engineer => 
        engineer.name.toLowerCase().includes(search) || 
        engineer.title.toLowerCase().includes(search)
      );
    },
    filteredSales() {
      if (!this.salesSearch) return this.sales;
      
      const search = this.salesSearch.toLowerCase();
      return this.sales.filter(sales => 
        sales.name.toLowerCase().includes(search) || 
        sales.department.toLowerCase().includes(search)
      );
    }
  },
  methods: {
    // 获取客户列表
    async fetchCustomers() {
      try {
        const response = await getCustomerList();
        if (response && response.code === 200 && response.data) {
          this.customers = response.data;
        } else {
          console.error('获取客户列表失败:', response);
        }
      } catch (error) {
        console.error('获取客户列表出错:', error);
      }
    },
    
    // 获取工程师列表
    async fetchEngineers() {
      try {
        const response = await getEngineersList();
        if (response && response.code === 200 && response.data) {
          this.engineers = response.data;
        } else {
          console.error('获取工程师列表失败:', response);
        }
      } catch (error) {
        console.error('获取工程师列表出错:', error);
      }
    },
    
    // 获取销售人员列表
    async fetchSales() {
      try {
        const response = await getSalesList();
        if (response && response.code === 200 && response.data) {
          this.sales = response.data;
        } else {
          console.error('获取销售人员列表失败:', response);
        }
      } catch (error) {
        console.error('获取销售人员列表出错:', error);
      }
    },
    
    selectCustomer(customer) {
      this.form.customer = customer;
      this.customerSearch = '';
      this.showCustomerDropdown = false;
    },
    createNewCustomer() {
      console.log('创建新客户');
      // 导航到客户创建页面
      this.$router.push('/customer/create');
    },
    selectSales(sales) {
      this.form.sales = sales;
      this.salesSearch = '';
      this.showSalesDropdown = false;
    },
    clearSales() {
      this.form.sales = null;
      this.salesSearch = '';
      this.showSalesDropdown = false;
    },
    selectEngineer(engineer) {
      // 检查是否已经添加过该工程师
      if (!this.form.assignedEngineers.some(e => e.id === engineer.id)) {
        this.form.assignedEngineers.push(engineer);
      }
      this.engineerSearch = '';
      this.showEngineerDropdown = false;
    },
    removeEngineer(index) {
      this.form.assignedEngineers.splice(index, 1);
    },
    addImage() {
      // 创建文件输入元素
      const fileInput = document.createElement('input');
      fileInput.type = 'file';
      fileInput.accept = 'image/*';
      fileInput.multiple = true; // 允许选择多个文件
      
      // 监听文件选择事件
      fileInput.addEventListener('change', async (event) => {
        const files = event.target.files;
        if (!files || files.length === 0) return;
        
        try {
          // 存储选择的文件，用于后续上传
          if (!this.form.selectedFiles) {
            this.form.selectedFiles = [];
          }
          
          // 处理选择的文件
          for (let i = 0; i < files.length; i++) {
            const file = files[i];
            
            // 将文件添加到选择的文件列表
            this.form.selectedFiles.push(file);
            
            // 创建本地预览
            const reader = new FileReader();
            reader.onload = (e) => {
              // 添加预览图片到表单
              this.form.images.push({
                preview: e.target.result,
                file: file,
                uploading: false,
                uploaded: false,
                url: null
              });
            };
            reader.readAsDataURL(file);
          }
        } catch (error) {
          console.error('处理图片出错:', error);
          alert('处理图片失败，请重试');
        }
      });
      
      // 触发文件选择对话框
      fileInput.click();
    },
    removeImage(index) {
      // 从selectedFiles中移除对应的文件
      if (this.form.selectedFiles && this.form.images[index].file) {
        const fileIndex = this.form.selectedFiles.findIndex(f => f === this.form.images[index].file);
        if (fileIndex !== -1) {
          this.form.selectedFiles.splice(fileIndex, 1);
        }
      }
      
      // 从images中移除图片
      this.form.images.splice(index, 1);
    },
    getStatusClass(status) {
      switch(status) {
        case '忙碌': return 'status-busy';
        case '空闲': return 'status-free';
        case '部分可协': return 'status-partial';
        default: return '';
      }
    },
    // 处理任务类型变更
    handleTaskTypeChange(newType) {
      this.form.taskType = newType;
      // 根据任务类型重置特定字段
      this.resetTaskSpecificFields();
    },
    // 重置任务特定字段
    resetTaskSpecificFields() {
      // 重置所有特定字段
      this.form.faultDescription = '';
      this.form.quantity = 1;
      this.form.attachments = [];
      this.form.appointmentTime = '';
      this.form.verificationType = '';
      this.form.purpose = '';
      this.form.requirementDescription = '';
    },
    // 验证表单
    validateForm() {
      // 基本验证
      if (!this.form.title.trim()) {
        alert('请输入任务标题');
        return false;
      }
      
      if (!this.form.startTime || !this.form.endTime) {
        alert('请设置任务开始和结束时间');
        return false;
      }
      
      // 任务类型特定验证
      const taskType = this.form.taskType;
      
      if (taskType === 'selection') {
        if (!this.form.purpose.trim()) {
          alert('请输入仪器用途');
          return false;
        }
        if (!this.form.description.trim()) {
          alert('请输入需求描述');
          return false;
        }
      } else {
        if (!this.form.deviceName.trim()) {
          alert('请输入设备名称');
          return false;
        }
        
        if (['repair', 'maintenance'].includes(taskType) && !this.form.description.trim()) {
          alert('请输入故障描述');
          return false;
        }
        
        if (taskType === 'training' && !this.form.appointmentTime) {
          alert('请设置预约时间');
          return false;
        }
        
        if (taskType === 'verification' && !this.form.verificationType) {
          alert('请选择验证类别');
          return false;
        }
      }
      
      if (this.form.assignedEngineers.length === 0) {
        alert('请至少指派一名工程师');
        return false;
      }
      
      return true;
    },
    saveAsDraft() {
      // 保存草稿不需要完整验证
      console.log('保存草稿', this.form);
      
      // 构建任务数据
      const taskData = this.buildTaskData();
      
      // 将任务数据保存到localStorage
      localStorage.setItem('taskDraft', JSON.stringify(taskData));
      
      // 显示保存成功提示
      alert('草稿已保存在本地');
    },
    submitTask() {
      // 验证表单
      if (!this.validateForm()) {
        return;
      }
      
      // 构建任务数据
      const taskData = this.buildTaskData();
      
      // 提交任务数据到服务器
      this.submitTaskToServer(taskData);
    },
    
    // 提交任务到服务器
    async submitTaskToServer(taskData) {
      try {
        // 1. 先上传图片（如果有）
        // if (this.form.selectedFiles && this.form.selectedFiles.length > 0) {
        //   const imageUrls = [];
          
        //   // 逐个上传图片
        //   for (const file of this.form.selectedFiles) {
        //     try {
        //       const formData = new FormData();
        //       formData.append('file', file);
              
        //       // 单个上传图片
        //       const uploadResponse = await uploadTaskImage(formData);
              
        //       if (uploadResponse && uploadResponse.code === 200 && uploadResponse.data) {
        //         imageUrls.push(uploadResponse.data);
        //       }
        //     } catch (error) {
        //       console.error('上传图片失败:', error);
        //       // 继续处理下一张图片
        //     }
        //   }
          
        //   // 将上传成功的图片URL添加到任务数据
        //   if (imageUrls.length > 0) {
        //     taskData.images = imageUrls;
        //   }
        // }
        
        // 2. 创建任务
        const response = await createTask(taskData);
        
        if (response && response.code === 200) {
          const taskId = response.data;
          
          // 清除本地草稿
          this.clearLocalDraft();
          
          alert('任务创建成功');
          this.$router.push('/home');
        } else {
          alert(`任务创建失败: ${response.message || '服务器错误'}`);
          console.error('任务创建失败:', response);
        }
      } catch (error) {
        alert('任务创建失败，请重试');
        console.error('任务创建出错:', error);
      }
    },
    // 构建任务数据
    buildTaskData() {
      const taskType = this.form.taskType;
      
      // 基本数据
      const taskData = {
        taskType,
        title: this.form.title,
        priority: this.form.priority,
        startTime: convertLocalDateTimeToISO(this.form.startTime),
        endTime: convertLocalDateTimeToISO(this.form.endTime),
        description: this.form.description,
        assignedEngineers: this.form.assignedEngineers.map(engineer => engineer.id)
      };
      
      // 添加客户信息
      if (this.form.customer) {
        taskData.customer = {
          id: this.form.customer.id,
          name: this.form.customer.name,
          level: this.form.customer.level,
          address: this.form.customer.address,
          contact: this.form.customer.contact,
          phone: this.form.customer.phone
        };
      }
      
      // 添加销售人员信息
      if (this.form.sales) {
        taskData.sales = {
          id: this.form.sales.id
        };
      }
      
      // 根据任务类型添加特定字段
      if (taskType === 'selection') {
        taskData.purpose = this.form.purpose;
        taskData.description = this.form.description;
        taskData.quantity = this.form.quantity;
      } else {
        // 直接将设备信息放在根级别，而不是嵌套在deviceInfo对象中
        taskData.deviceName = this.form.deviceName;
        taskData.deviceModel = this.form.deviceModel;
        taskData.deviceBrand = this.form.deviceBrand;
        taskData.serialNumber = this.form.serialNumber;
        
        if (['repair', 'maintenance'].includes(taskType)) {
          taskData.description = this.form.description;
          taskData.quantity = this.form.quantity;
        }
        
        if (taskType === 'training') {
          taskData.appointmentTime = convertLocalDateTimeToISO(this.form.appointmentTime);
        }
        
        if (taskType === 'verification') {
          taskData.verificationType = this.form.verificationType;
          taskData.quantity = this.form.quantity;
        }
        
        if (['recycle', 'leasing'].includes(taskType)) {
          taskData.quantity = this.form.quantity;
          taskData.attachments = this.form.attachments;
        }
        
        if (taskType === 'installation') {
          taskData.quantity = this.form.quantity;
        }
      }

      // 添加步骤定义
      const stepTemplate = TASK_TYPE_FLOW_STEPS[taskType];
      if (stepTemplate) {
        taskData.steps = stepTemplate.map((step, index) => ({
          stepIndex: index,
          title: step.title
        }));
      }
      
      // 打印任务数据，用于调试
      console.log('构建的任务数据:', JSON.stringify(taskData));
      
      return taskData;
    },
    // 根据ID加载客户信息
    async loadCustomerById(customerId) {
      try {
        // 查找本地客户列表中是否有匹配的客户
        const foundCustomer = this.customers.find(c => c.id == customerId);
        
        if (foundCustomer) {
          this.selectCustomer(foundCustomer);
          return;
        }
        
        // 如果本地没有找到，尝试从服务器获取
        const response = await getCustomerDetail(customerId);
        if (response && response.code === 200 && response.data) {
          this.selectCustomer(response.data);
        } else {
          console.error('未找到指定ID的客户:', customerId);
        }
      } catch (error) {
        console.error('根据ID加载客户信息失败:', error);
      }
    },
    // 处理客户搜索框的blur事件
    handleCustomerBlur() {
      const timeoutId = setTimeout(() => {
        this.showCustomerDropdown = false;
      }, 200);
      this.timeoutIds.push(timeoutId);
    },
    
    // 处理销售人员搜索框的blur事件
    handleSalesBlur() {
      const timeoutId = setTimeout(() => {
        this.showSalesDropdown = false;
      }, 200);
      this.timeoutIds.push(timeoutId);
    },
    
    // 处理工程师搜索框的blur事件
    handleEngineerBlur() {
      const timeoutId = setTimeout(() => {
        this.showEngineerDropdown = false;
      }, 200);
      this.timeoutIds.push(timeoutId);
    },
    
    // 从localStorage加载草稿数据
    loadDraftFromLocalStorage() {
      try {
        const draftData = localStorage.getItem('taskDraft');
        if (draftData) {
          const taskData = JSON.parse(draftData);
          
          // 填充基础表单数据
          this.form.taskType = taskData.taskType || 'repair';
          this.form.title = taskData.title || '';
          this.form.priority = taskData.priority || 'normal';
          this.form.description = taskData.description || '';
          
          // 处理日期时间
          if (taskData.startTime) {
            // 从ISO格式转换为本地datetime-local格式
            this.form.startTime = new Date(taskData.startTime).toISOString().slice(0, 16);
          }
          
          if (taskData.endTime) {
            this.form.endTime = new Date(taskData.endTime).toISOString().slice(0, 16);
          }
          
          // 处理设备信息
          this.form.deviceName = taskData.deviceName || '';
          this.form.deviceModel = taskData.deviceModel || '';
          this.form.deviceBrand = taskData.deviceBrand || '';
          this.form.serialNumber = taskData.serialNumber || '';
          this.form.description = taskData.description || '';
          this.form.quantity = taskData.quantity || 1;
          this.form.attachments = taskData.attachments || [];
          this.form.verificationType = taskData.verificationType || '';
          this.form.purpose = taskData.purpose || '';
          this.form.appointmentTime = taskData.appointmentTime || '';
          
          // 处理客户信息
          if (taskData.customer) {
            this.form.customer = taskData.customer;
          }
          
          // 处理销售信息
          if (taskData.sales) {
            // 需要加载完整的销售信息
            this.loadSalesById(taskData.sales.id);
          }
          
          // 处理工程师信息
          if (taskData.assignedEngineers && Array.isArray(taskData.assignedEngineers)) {
            // 需要加载完整的工程师信息
            this.loadEngineersById(taskData.assignedEngineers);
          }
          
          console.log('已从本地加载草稿数据');
          
          // 显示草稿已加载提示
          setTimeout(() => {
            if (confirm('检测到本地保存的草稿数据已加载。是否清除草稿？')) {
              this.clearLocalDraft();
            }
          }, 1000);
        }
      } catch (error) {
        console.error('加载草稿数据失败:', error);
      }
    },
    
    // 根据ID加载销售人员信息
    async loadSalesById(salesId) {
      try {
        // 等待销售列表加载完成
        if (this.sales.length === 0) {
          await this.fetchSales();
        }
        
        // 查找销售信息
        const foundSales = this.sales.find(s => s.id == salesId);
        if (foundSales) {
          this.form.sales = foundSales;
        }
      } catch (error) {
        console.error('加载销售信息失败:', error);
      }
    },
    
    // 根据ID列表加载工程师信息
    async loadEngineersById(engineerIds) {
      try {
        // 等待工程师列表加载完成
        if (this.engineers.length === 0) {
          await this.fetchEngineers();
        }
        
        // 找到对应的工程师并添加到已分配列表
        engineerIds.forEach(id => {
          const foundEngineer = this.engineers.find(e => e.id == id);
          if (foundEngineer && !this.form.assignedEngineers.some(e => e.id === foundEngineer.id)) {
            this.form.assignedEngineers.push(foundEngineer);
          }
        });
      } catch (error) {
        console.error('加载工程师信息失败:', error);
      }
    },
    
    // 在成功创建任务后清除本地草稿
    clearLocalDraft() {
      localStorage.removeItem('taskDraft');
      console.log('已清除本地草稿数据');
    },
  },
  watch: {
    // 监听任务类型变化
    'form.taskType': function(newType) {
      this.handleTaskTypeChange(newType);
    }
  },
  created() {
    // 初始化表单
    const now = new Date();
    const tomorrow = new Date();
    tomorrow.setDate(now.getDate() + 1);
    
    // 设置默认开始和结束时间
    this.form.startTime = now.toISOString().slice(0, 16);
    this.form.endTime = tomorrow.toISOString().slice(0, 16);
    
    // 获取客户列表
    this.fetchCustomers();
    
    // 获取工程师列表
    this.fetchEngineers();
    
    // 获取销售人员列表
    this.fetchSales();
    
    // 从URL参数获取客户ID
    const customerId = this.$route.query.customerId;
    if (customerId) {
      this.loadCustomerById(customerId);
    }
    
    // 尝试从localStorage加载草稿数据
    this.loadDraftFromLocalStorage();
  },
  beforeUnmount() {
    // 清除所有timeout
    this.timeoutIds.forEach(id => clearTimeout(id));
  }
}
</script>

<style scoped>
.create-task-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 32px;
}

.header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 10;
}

.header-left {
  width: 24px;
}

.icon-arrow-left::before {
  content: '\f060';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
}

.header h1 {
  flex: 1;
  font-size: 18px;
  font-weight: 600;
  margin-left: 16px;
}

.save-btn {
  color: #3b82f6;
  font-size: 14px;
  background: none;
  border: none;
}

.form-container {
  padding: 16px;
}

.form-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
}

.task-type-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.task-type-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
}

.task-type-item.active {
  border-color: #3b82f6;
  background-color: #eff6ff;
}

.task-type-icon {
  font-size: 24px;
  color: #6b7280;
  margin-bottom: 8px;
}

.task-type-item.active .task-type-icon {
  color: #3b82f6;
}

.task-type-name {
  font-size: 14px;
}

/* 系统外任务选项样式 */
.external-task-option {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  margin-right: 8px;
}

.hint-text {
  display: block;
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
  margin-left: 24px;
}

/* 验证类型选择器样式 */
.verification-type-selector {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.verification-type-item {
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  cursor: pointer;
}

.verification-type-item.active {
  border-color: #3b82f6;
  background-color: #eff6ff;
  color: #3b82f6;
}

.verification-type-name {
  font-size: 14px;
}

.icon-wrench::before {
  content: '\f0ad';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-tools::before {
  content: '\f7d9';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-box-open::before {
  content: '\f49e';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-chalkboard-teacher::before {
  content: '\f51c';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-clipboard-check::before {
  content: '\f46c';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-search::before {
  content: '\f002';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-hand-holding-usd::before {
  content: '\f4c0';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-recycle::before {
  content: '\f1b8';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.form-group {
  margin-bottom: 16px;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  font-size: 14px;
  color: #374151;
  margin-bottom: 8px;
}

input[type="text"],
input[type="datetime-local"],
input[type="number"],
textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
}

textarea {
  resize: vertical;
}

.customer-selector {
  position: relative;
}

.customer-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background-color: #fff;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 10;
  max-height: 250px;
  overflow-y: auto;
}

.customer-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-bottom: 1px solid #f3f4f6;
  cursor: pointer;
}

.customer-item:hover {
  background-color: #f9fafb;
}

.customer-info {
  flex: 1;
}

.customer-name {
  font-weight: 500;
}

.customer-address {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

.customer-level {
  padding: 2px 8px;
  border-radius: 9999px;
  font-size: 12px;
  font-weight: 500;
}

.level-A {
  background-color: #fee2e2;
  color: #dc2626;
}

.level-B {
  background-color: #fef3c7;
  color: #d97706;
}

.level-C {
  background-color: #dbeafe;
  color: #2563eb;
}

.create-new-customer {
  display: flex;
  align-items: center;
  padding: 12px;
  border-top: 1px solid #f3f4f6;
  cursor: pointer;
}

.create-new-customer:hover {
  background-color: #f9fafb;
}

.create-new-customer i {
  margin-right: 8px;
  color: #3b82f6;
}

.selected-customer {
  display: flex;
  justify-content: space-between;
  padding: 12px;
  background-color: #f9fafb;
  border-radius: 6px;
}

.selected-customer h4 {
  font-weight: 500;
  margin-bottom: 4px;
}

.customer-contact,
.customer-phone,
.customer-address {
  font-size: 14px;
  color: #6b7280;
  margin-top: 4px;
}

.priority-selector {
  display: flex;
  gap: 16px;
}

.priority-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  cursor: pointer;
}

.priority-item.active {
  border-color: #3b82f6;
  background-color: #eff6ff;
}

.priority-icon {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 8px;
}

.image-uploader {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.image-preview {
  position: relative;
  width: 100px;
  height: 100px;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-image {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.8);
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-image {
  width: 100px;
  height: 100px;
  border: 1px dashed #ddd;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.add-image:hover {
  border-color: #409eff;
}

.help-text {
  font-size: 12px;
  color: #606266;
  margin-top: 5px;
}

.engineer-selector {
  position: relative;
}

.engineer-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background-color: #fff;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 10;
  max-height: 250px;
  overflow-y: auto;
}

.engineer-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-bottom: 1px solid #f3f4f6;
  cursor: pointer;
}

.engineer-item:hover {
  background-color: #f9fafb;
}

.engineer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 12px;
}

.engineer-info {
  flex: 1;
}

.engineer-name {
  font-weight: 500;
}

.engineer-title {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

.engineer-status {
  padding: 2px 8px;
  border-radius: 9999px;
  font-size: 12px;
  white-space: nowrap;
}

.status-busy {
  background-color: #fee2e2;
  color: #dc2626;
}

.status-free {
  background-color: #d1fae5;
  color: #059669;
}

.status-partial {
  background-color: #fef3c7;
  color: #d97706;
}

.assigned-engineers {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.assigned-engineer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background-color: #f9fafb;
  border-radius: 6px;
}

.assigned-engineer .engineer-info {
  display: flex;
  align-items: center;
}

.remove-engineer {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: #f3f4f6;
  color: #6b7280;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}

.form-actions {
  display: flex;
  gap: 16px;
  margin-top: 24px;
}

.cancel-btn,
.submit-btn {
  flex: 1;
  padding: 12px;
  border-radius: 8px;
  font-size: 16px;
  text-align: center;
  border: none;
}

.cancel-btn {
  background-color: #f3f4f6;
  color: #6b7280;
}

.submit-btn {
  background-color: #3b82f6;
  color: #fff;
}

.sales-selector {
  position: relative;
}

.sales-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background-color: #fff;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 10;
  max-height: 250px;
  overflow-y: auto;
}

.sales-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-bottom: 1px solid #f3f4f6;
  cursor: pointer;
}

.sales-item:hover {
  background-color: #f9fafb;
}

.sales-info {
  flex: 1;
}

.sales-name {
  font-weight: 500;
}

.sales-department {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

.sales-phone {
  font-size: 14px;
  color: #6b7280;
  margin-top: 4px;
}

.clear-sales {
  display: flex;
  align-items: center;
  padding: 12px;
  color: #6b7280;
  cursor: pointer;
  border-top: 1px solid #f3f4f6;
}

.clear-sales:hover {
  background-color: #f9fafb;
}

.selected-sales {
  display: flex;
  justify-content: space-between;
  padding: 12px;
  background-color: #f9fafb;
  border-radius: 6px;
}

.selected-sales h4 {
  font-weight: 500;
  margin-bottom: 4px;
}

.remove-sales {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: #f3f4f6;
  color: #6b7280;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}
</style> 