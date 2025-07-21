import { defineStore } from 'pinia';
import request from '@/utils/request';
import { API_PATHS } from '@/constants/api';
import generateTaskId from '@/utils/taskIdGenerator';
import config from '@/config';

export const useTaskStore = defineStore('task', {
  state: () => ({
    taskList: [],
    currentTask: null,
    taskTypes: [
      { id: 'repair', name: '仪器维修', icon: 'tool' },
      { id: 'maintenance', name: '仪器保养', icon: 'settings' },
      { id: 'recycle', name: '仪器回收', icon: 'recycle' },
      { id: 'leasing', name: '仪器租赁', icon: 'package' },
      { id: 'training', name: '培训预约', icon: 'book-open' },
      { id: 'verification', name: '仪器验证', icon: 'check-circle' },
      { id: 'selection', name: '仪器选型', icon: 'list-filter' },
      { id: 'installation', name: '仪器安装', icon: 'hammer' }
    ],
    loading: false,
    error: null,
    toast: {
      show: false,
      message: '',
      type: 'info', // 'info', 'success', 'warning', 'error'
      duration: 3000
    }
  }),

  getters: {
    getTaskTypeInfo: (state) => (typeId) => {
      return state.taskTypes.find(type => type.id === typeId) || { id: '', name: '未知类型', icon: 'help-circle' };
    }
  },

  actions: {
    // 内部方法，用于更新Toast状态
    _updateToast(payload) {
      // 合并新的 payload 到当前 toast 状态
      Object.assign(this.toast, { ...payload, show: true });

      // 如果设置了自动隐藏，则在 duration 后隐藏
      if (payload.show === false || (payload.duration || this.toast.duration)) {
        setTimeout(() => {
          this.toast.show = false;
        }, payload.duration || this.toast.duration);
      }
    },
    
    // 统一错误处理函数
    handleError(error, defaultMessage) {
      this.error = error.message || defaultMessage;
      console.error(`${defaultMessage}:`, error);
      throw error;
    },

    // 获取任务列表
    async fetchTaskList() {
      this.loading = true;
      try {
        const response = await request.get(API_PATHS.TASK_LIST);
        this.taskList = response.data || [];
        return this.taskList;
      } catch (error) {
        return this.handleError(error, '获取订单列表失败');
      } finally {
        this.loading = false;
      }
    },

    // 获取任务详情
    async fetchTaskDetail(taskId) {
      this.loading = true;
      try {
        const response = await request.get(API_PATHS.TASK_DETAIL(taskId));
        this.currentTask = response.data;
        return this.currentTask;
      } catch (error) {
        return this.handleError(error, '获取订单详情失败');
      } finally {
        this.loading = false;
      }
    },
    
    // 获取带详情的任务步骤
    async fetchTaskStepsWithDetails(taskId) {
      this.loading = true;
      try {
        const response = await request.get(API_PATHS.TASK_STEPS(taskId));
        return response.data || [];
      } catch (error) {
        return this.handleError(error, '获取订单步骤失败');
      } finally {
        this.loading = false;
      }
    },

    // 检查任务ID是否重复
    async checkTaskIdExists(taskId) {
      try {
        // 通过尝试获取任务详情来检查ID是否存在
        // 如果存在会返回任务详情，如果不存在会抛出404错误
        await request.get(API_PATHS.TASK_DETAIL(taskId));
        return true; // 任务ID存在
      } catch (error) {
        if (error.message && error.message.includes('404')) {
          return false; // 任务ID不存在
        }
        // 其他错误，可能是网络或服务器问题，假设ID不存在
        console.warn('检查任务ID时发生错误:', error);
        return false;
      }
    },

    // 生成唯一任务ID
    async generateUniqueTaskId(companyName, maxRetries = 3) {
      let retryCount = 0;
      let taskId = generateTaskId(companyName);
      
      // 在本地检查是否重复
      const localIds = uni.getStorageSync('submittedTaskIds') 
        ? JSON.parse(uni.getStorageSync('submittedTaskIds')) 
        : [];
      
      // 如果本地存在，重新生成
      while (localIds.includes(taskId) && retryCount < maxRetries) {
        console.log(`本地已存在任务ID ${taskId}，重新生成`);
        taskId = generateTaskId(companyName);
        retryCount++;
      }
      
      return taskId;
    },

    // 请求订阅消息
    async requestSubscription() {
      const tmplIds = Object.values(config.templateIds);
      if (tmplIds.length === 0) {
        console.warn('没有配置任何订阅消息模板ID');
        return;
      }

      // 使用Set确保模板ID的唯一性，避免重复请求
      const uniqueTmplIds = [...new Set(tmplIds)];
      console.log('请求订阅的模板ID:', uniqueTmplIds);

      try {
        const res = await wx.requestSubscribeMessage({
          tmplIds: uniqueTmplIds,
        });

        if (res.errMsg === 'requestSubscribeMessage:ok') {
          console.log('用户授权订阅成功', res);
          // 可以根据res的具体内容判断哪些模板被接受了
          for (const key in res) {
            if (key !== 'errMsg' && res[key] === 'accept') {
              console.log(`用户同意了模板: ${key}`);
            }
          }
        } else {
          console.error('请求订阅消息失败', res);
        }
      } catch (error) {
        console.error('调用订阅消息接口时发生异常', error);
      }
    },

    // 提交任务
    async createTask(taskData) {
      this.loading = true;
      try {
        // 验证必填字段
        if (!taskData.title) {
          throw new Error('订单标题不能为空');
        }
        
        if (!taskData.taskType) {
          throw new Error('订单类型不能为空');
        }
        
        // 确保任务ID存在且唯一
        if (!taskData.taskId) {
          const companyName = taskData.customer?.customerName || '未知公司';
          taskData.taskId = await this.generateUniqueTaskId(companyName);
          console.log(`生成任务ID: ${taskData.taskId}`);
        }
        
        // 准备任务数据
        const taskPayload = {
          taskId: taskData.taskId,
          title: taskData.title,
          description: taskData.description,
          taskType: taskData.taskType,
          priority: taskData.priority || 'normal',
          device: taskData.device || {
            name: '',
            type: '',
            brand: '',
            model: '',
            description: '',
            quantity: 1
          },
          customer: taskData.customer || {}
        };
        
        // 发送任务创建请求
        const response = await request.post(API_PATHS.TASK_CREATE, taskPayload);
        const taskId = response.data;
        
        // 记录已提交的任务ID
        const submittedIds = uni.getStorageSync('submittedTaskIds') 
          ? JSON.parse(uni.getStorageSync('submittedTaskIds')) 
          : [];
        submittedIds.push(taskData.taskId);
        if (submittedIds.length > 100) {
          submittedIds.shift(); // 只保留最近100个
        }
        uni.setStorageSync('submittedTaskIds', JSON.stringify(submittedIds));
        
        // 如果创建成功且有图片，上传图片
    if (taskId && taskData.images && taskData.images.length > 0) {
      console.log(`准备上传 ${taskData.images.length} 张图片`);
      
      // 跟踪上传成功和失败的图片
      const uploadResults = {
        success: 0,
        failed: 0,
        total: taskData.images.length
      };

      this._updateToast({ 
        message: `任务创建成功，准备上传图片...`, 
        type: 'info',
        duration: 20000 // 保持显示直到上传结束
      });
      
      // 逐个上传图片，避免并发上传可能导致的问题
      for (let i = 0; i < taskData.images.length; i++) {
        try {
          // 更新进度提示
          this._updateToast({
            message: `正在上传图片 (${i + 1}/${uploadResults.total})...`,
            type: 'info',
            duration: 20000
          });
          
          const image = taskData.images[i];
          
          // 确保图片是File或Blob对象
          if (!(image instanceof File || image instanceof Blob)) {
            console.error(`第 ${i+1} 张图片不是有效的文件对象:`, typeof image);
            uploadResults.failed++;
            continue;
          }
          
          console.log(`开始上传第 ${i+1}/${uploadResults.total} 张图片，文件类型: ${image.type}, 大小: ${image.size} 字节`);
          
          // 直接传递文件对象，不再包装成对象
          await this.uploadTaskImage(image, taskId, 0, i);
          
          console.log(`第 ${i+1}/${uploadResults.total} 张图片上传成功`);
          uploadResults.success++;
        } catch (uploadError) {
          console.error(`第 ${i+1}/${uploadResults.total} 张图片上传失败:`, uploadError);
          uploadResults.failed++;
          
          // 记录详细错误信息
          if (uploadError.response) {
            console.error('服务器响应:', uploadError.response.status, uploadError.response.data);
          }
          
          // 继续上传下一张图片，不中断整个流程
        }
        
        // 更新最终进度
        // 不再直接更新UI，由createTask中的循环控制
      }
      
      // 输出上传结果统计
      console.log(`图片上传完成: 总计 ${uploadResults.total} 张, 成功 ${uploadResults.success} 张, 失败 ${uploadResults.failed} 张`);
      
      // 更新最终状态提示
      if (uploadResults.failed > 0) {
        if (uploadResults.success > 0) {
          // 部分成功，部分失败
          this._updateToast({
            message: `任务提交成功，但有 ${uploadResults.failed}/${uploadResults.total} 张图片上传失败`,
            type: 'warning',
            duration: 5000
          });
        } else {
          // 全部失败
          this._updateToast({
            message: `任务提交成功，但所有图片 (${uploadResults.total} 张) 上传失败`,
            type: 'error',
            duration: 5000
          });
        }
      } else if (uploadResults.success > 0) {
        // 全部成功
        this._updateToast({
          message: `任务提交成功，${uploadResults.success} 张图片已上传`,
          type: 'success',
          duration: 3000
        });
      }
      
      // 如果有上传失败的图片，添加到任务的错误信息中
      if (uploadResults.failed > 0) {
        const warningMessage = `警告: ${uploadResults.failed}/${uploadResults.total} 张图片上传失败，但任务创建成功。`;
        console.warn(warningMessage);
        
        // 如果所有图片都上传失败，则返回提示信息
        if (uploadResults.success === 0 && uploadResults.total > 0) {
          this.error = warningMessage;
        }
      }
    }
    
    // 如果创建成功且有附件，上传附件
    if (taskId && taskData.files && taskData.files.length > 0) {
      console.log(`准备上传 ${taskData.files.length} 个附件`);
      
      // 跟踪上传成功和失败的附件
      const fileUploadResults = {
        success: 0,
        failed: 0,
        total: taskData.files.length
      };

      this._updateToast({ 
        message: `准备上传附件...`, 
        type: 'info',
        duration: 20000
      });
      
      // 逐个上传附件，避免并发上传可能导致的问题
      for (let i = 0; i < taskData.files.length; i++) {
        try {
          // 更新进度提示
           this._updateToast({
            message: `正在上传附件 (${i + 1}/${fileUploadResults.total})...`,
            type: 'info',
            duration: 20000
          });
          
          const file = taskData.files[i];
          
          // 确保文件是File或Blob对象
          if (!(file instanceof File || file instanceof Blob)) {
            console.error(`第 ${i+1} 个附件不是有效的文件对象:`, typeof file);
            fileUploadResults.failed++;
            continue;
          }
          
          console.log(`开始上传第 ${i+1}/${fileUploadResults.total} 个附件，文件类型: ${file.type}, 大小: ${file.size} 字节`);
          
          // 直接传递文件对象
          await this.uploadTaskAttachment(file, taskId, i);
          
          console.log(`第 ${i+1}/${fileUploadResults.total} 个附件上传成功`);
          fileUploadResults.success++;
        } catch (uploadError) {
          console.error(`第 ${i+1}/${fileUploadResults.total} 个附件上传失败:`, uploadError);
          fileUploadResults.failed++;
          
          // 记录详细错误信息
          if (uploadError.response) {
            console.error('服务器响应:', uploadError.response.status, uploadError.response.data);
          }
          
          // 继续上传下一个附件，不中断整个流程
        }
        
        // 更新最终进度
        // 不再直接更新UI，由createTask中的循环控制
      }
      
      // 输出上传结果统计
      console.log(`附件上传完成: 总计 ${fileUploadResults.total} 个, 成功 ${fileUploadResults.success} 个, 失败 ${fileUploadResults.failed} 个`);
      
      // 更新最终状态提示
      if (fileUploadResults.failed > 0) {
        if (fileUploadResults.success > 0) {
          // 部分成功，部分失败
          this._updateToast({
            message: `任务提交成功，但有 ${fileUploadResults.failed}/${fileUploadResults.total} 个附件上传失败`,
            type: 'warning',
            duration: 5000
          });
        } else {
          // 全部失败
          this._updateToast({
            message: `任务提交成功，但所有附件 (${fileUploadResults.total} 个) 上传失败`,
            type: 'error',
            duration: 5000
          });
        }
      } else if (fileUploadResults.success > 0) {
        // 全部成功
        this._updateToast({
          message: `任务提交成功，${fileUploadResults.success} 个附件已上传`,
          type: 'success',
          duration: 3000
        });
      }
      
      // 如果有上传失败的附件，添加到任务的错误信息中
      if (fileUploadResults.failed > 0) {
        const warningMessage = `警告: ${fileUploadResults.failed}/${fileUploadResults.total} 个附件上传失败，但任务创建成功。`;
        console.warn(warningMessage);
        
        // 如果所有附件都上传失败，则返回提示信息
        if (fileUploadResults.success === 0 && fileUploadResults.total > 0) {
          this.error = warningMessage;
        }
      }
    }
        
        return response.data;
      } catch (error) {
        // 如果是重复键错误，尝试生成新的任务ID并重试
        if (error.message && (
            error.message.includes('重复键') || 
            error.message.includes('违反了') || 
            error.message.includes('duplicate') ||
            error.message.includes('已存在')
          )) {
          console.warn('任务ID重复，尝试重新生成:', error);
          
          // 重新生成任务ID
          const companyName = taskData.customer?.customerName || '未知公司';
          taskData.taskId = await this.generateUniqueTaskId(companyName);
          console.log(`重新生成任务ID: ${taskData.taskId}`);
          
          // 递归调用自身重试一次
          return this.createTask(taskData);
        }
        
        return this.handleError(error, '提交任务失败');
      } finally {
        this.loading = false;
      }
    },
    
    // 上传任务图片
  async uploadTaskImage(file, taskId, imageType = 0, sort = 0) {
    try {
      // 验证参数
      if (!file) {
        console.error('图片文件为空');
        throw new Error('图片文件为空');
      }
      
      // 确保文件是File或Blob对象
      if (!(file instanceof File || file instanceof Blob)) {
        console.error('图片文件类型无效:', typeof file, file);
        throw new Error('图片文件类型无效');
      }
      
      // 验证文件类型
      const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/jpg'];
      if (!validTypes.includes(file.type)) {
        console.error('上传图片失败：不支持的图片类型', file.type);
        throw new Error(`不支持的图片类型: ${file.type}`);
      }
      
      // 验证文件大小
      const maxSize = 5 * 1024 * 1024; // 5MB
      if (file.size > maxSize) {
        console.error('上传图片失败：图片过大', file.size);
        throw new Error(`图片大小不能超过5MB, 当前大小: ${(file.size / 1024 / 1024).toFixed(2)}MB`);
      }
      
      console.log(`准备上传图片: 任务ID=${taskId}, 类型=${imageType}, 排序=${sort}, 大小=${file.size}字节`);
      
        const formData = new FormData();
      formData.append('file', file);
        formData.append('taskId', taskId);
        formData.append('imageType', imageType);
      formData.append('sort', sort);
      
      // 添加详细日志
      console.log('上传图片FormData内容:');
      for (let [key, value] of formData.entries()) {
        if (key === 'file') {
          console.log(`${key}: ${value.name || 'unnamed'}, 类型: ${value.type}, 大小: ${value.size} 字节`);
        } else {
          console.log(`${key}: ${value}`);
        }
      }
      
      // 使用完整的API URL路径
      const url = API_PATHS.TASK_IMAGE_UPLOAD;
      console.log(`上传图片URL: ${url}`);
      
      // 发送请求
      const response = await request.post(url, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
        },
        timeout: 30000, // 增加超时时间到30秒
        onUploadProgress: progressEvent => {
          const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          console.log(`图片 ${sort+1} 上传进度: ${percentCompleted}%`);
          // 不再直接更新UI，由createTask中的循环控制
        }
      });
      
      // 检查响应
      if (response.code !== 200) {
        throw new Error(response.message || '服务器返回错误');
      }
      
      console.log('图片上传成功:', response.data);
        return response.data;
      } catch (error) {
      console.error('图片上传详细错误:', error);
      
      // 提供更详细的错误信息
      let errorMessage = '上传图片失败';
      if (error.response) {
        const status = error.response.status;
        const responseData = error.response.data;
        console.error('上传失败响应数据:', responseData);
        errorMessage += `: 服务器返回 ${status} 错误`;
        if (responseData && responseData.message) {
          errorMessage += ` - ${responseData.message}`;
        }
      } else if (error.request) {
        errorMessage += ': 服务器无响应';
      } else {
        errorMessage += `: ${error.message}`;
      }
      
      throw new Error(errorMessage);
      }
    },
    
    // 上传任务附件
  async uploadTaskAttachment(file, taskId, sort = 0) {
    try {
      // 验证参数
      if (!file) {
        console.error('附件文件为空');
        throw new Error('附件文件为空');
      }
      
      // 确保文件是File或Blob对象
      if (!(file instanceof File || file instanceof Blob)) {
        console.error('附件文件类型无效:', typeof file, file);
        throw new Error('附件文件类型无效');
      }
      
      // 验证文件类型
      const originalFilename = file.name || '';
      const fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
      const validTypes = ['pdf', 'doc', 'docx', 'xls', 'xlsx'];
      
      if (!validTypes.includes(fileExtension)) {
        console.error('上传附件失败：不支持的文件类型', fileExtension);
        throw new Error(`不支持的文件类型: ${fileExtension}`);
      }
      
      // 验证文件大小
      const maxSize = 10 * 1024 * 1024; // 10MB
      if (file.size > maxSize) {
        console.error('上传附件失败：文件过大', file.size);
        throw new Error(`附件大小不能超过10MB, 当前大小: ${(file.size / 1024 / 1024).toFixed(2)}MB`);
      }
      
      console.log(`准备上传附件: 任务ID=${taskId}, 排序=${sort}, 大小=${file.size}字节`);
      
      const formData = new FormData();
      formData.append('file', file);
      formData.append('taskId', taskId);
      formData.append('sort', sort);
      
      // 添加详细日志
      console.log('上传附件FormData内容:');
      for (let [key, value] of formData.entries()) {
        if (key === 'file') {
          console.log(`${key}: ${value.name || 'unnamed'}, 类型: ${value.type}, 大小: ${value.size} 字节`);
        } else {
          console.log(`${key}: ${value}`);
        }
      }
      
      // 使用完整的API URL路径
      const url = API_PATHS.TASK_ATTACHMENT_UPLOAD;
      console.log(`上传附件URL: ${url}`);
      
      // 发送请求
      const response = await request.post(url, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        timeout: 30000, // 增加超时时间到30秒
        onUploadProgress: progressEvent => {
          const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          console.log(`附件 ${sort+1} 上传进度: ${percentCompleted}%`);
          // 不再直接更新UI，由createTask中的循环控制
        }
      });
      
      // 检查响应
      if (response.code !== 200) {
        throw new Error(response.message || '服务器返回错误');
      }
      
      console.log('附件上传成功:', response.data);
      return response.data;
    } catch (error) {
      console.error('附件上传详细错误:', error);
      
      // 提供更详细的错误信息
      let errorMessage = '上传附件失败';
      if (error.response) {
        const status = error.response.status;
        const responseData = error.response.data;
        console.error('上传失败响应数据:', responseData);
        errorMessage += `: 服务器返回 ${status} 错误`;
        if (responseData && responseData.message) {
          errorMessage += ` - ${responseData.message}`;
        }
      } else if (error.request) {
        errorMessage += ': 服务器无响应';
      } else {
        errorMessage += `: ${error.message}`;
      }
      
      throw new Error(errorMessage);
    }
  },

    // 提交服务评价
    async submitEvaluation(taskId, evaluationData) {
      this.loading = true;
      try {
        // 验证评价数据
        if (!evaluationData.serviceAttitude || !evaluationData.serviceQuality || !evaluationData.overallRating) {
          throw new Error('评价数据不完整');
        }
        
        // 调整字段名称以匹配后端实体类
        const evaluationPayload = {
          serviceAttitude: evaluationData.serviceAttitude,
          serviceQuality: evaluationData.serviceQuality,
          overallRating: evaluationData.overallRating,
          comment: evaluationData.comment || ''
        };
        
        const response = await request.post(API_PATHS.TASK_EVALUATION(taskId), evaluationPayload);
        return response.data;
      } catch (error) {
        return this.handleError(error, '提交评价失败');
      } finally {
        this.loading = false;
      }
    },
    
    // 更新任务步骤
    async updateTaskStep(taskId, stepIndex, remark) {
      this.loading = true;
      try {
        const response = await request.post(API_PATHS.TASK_STEP_UPDATE(taskId, stepIndex), { remark });
        return response.data;
      } catch (error) {
        return this.handleError(error, '更新任务步骤失败');
      } finally {
        this.loading = false;
      }
    },
    
    // 确认任务报价
    async confirmTaskPrice(taskId) {
      this.loading = true;
      try {
        await request.post(API_PATHS.TASK_CONFIRM_PRICE(taskId));
        // 成功后更新本地任务状态
        if (this.currentTask && this.currentTask.taskId === taskId) {
          this.currentTask.priceConfirmed = 1;
        }
        this._updateToast({ message: '报价确认成功', type: 'success' });
      } catch (error) {
        return this.handleError(error, '确认报价失败');
      } finally {
        this.loading = false;
      }
    },
    
    // 获取高清图片预览
    async getImagePreview(imageId, options = {}) {
      try {
        const response = await request.get(API_PATHS.IMAGE_PREVIEW(imageId), {
          responseType: 'arraybuffer'
        });
        return response.data;
      } catch (error) {
        return this.handleError(error, '获取图片预览失败');
      }
    },
    
    // 下载图片
    async downloadImage(imageId) {
      try {
        // 确保imageId是数字类型
        const id = typeof imageId === 'string' ? Number(imageId) : imageId;
        // 在微信小程序环境中，这个API通常会直接在组件中调用wx.downloadFile
        // 此处返回下载链接，以便在非微信环境中使用
        return API_PATHS.IMAGE_DOWNLOAD(id);
      } catch (error) {
        return this.handleError(error, '下载图片失败');
      }
    },
    
    // 下载附件
    async downloadAttachment(fileId) {
      try {
        const response = await request.get(API_PATHS.ATTACHMENT_DOWNLOAD(fileId), {
          responseType: 'blob'
        });
        return response.data;
      } catch (error) {
        return this.handleError(error, '下载附件失败');
      }
    }
  }
}); 