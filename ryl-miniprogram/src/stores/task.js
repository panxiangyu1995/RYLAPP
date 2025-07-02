import { defineStore } from 'pinia';
import request from '@/utils/request';
import { getMockTaskList, getMockTaskDetail, submitMockEvaluation, confirmMockTaskPrice } from '@/utils/mockData';

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
    error: null
  }),

  getters: {
    getTaskTypeInfo: (state) => (typeId) => {
      return state.taskTypes.find(type => type.id === typeId) || { id: '', name: '未知类型', icon: 'help-circle' };
    }
  },

  actions: {
    // 获取任务列表
    async fetchTaskList() {
      this.loading = true;
      try {
        // 使用模拟数据
        const data = await getMockTaskList();
        this.taskList = data;
        return this.taskList;
        
        // 实际API调用（目前注释掉）
        // const response = await request.get('/tasks');
        // this.taskList = response.data.list || [];
        // return this.taskList;
      } catch (error) {
        this.error = error.message || '获取任务列表失败';
        console.error('获取任务列表失败:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // 获取任务详情
    async fetchTaskDetail(taskId) {
      this.loading = true;
      try {
        // 使用模拟数据
        const data = await getMockTaskDetail(taskId);
        this.currentTask = data;
        return this.currentTask;
        
        // 实际API调用（目前注释掉）
        // const response = await request.get(`/tasks/${taskId}`);
        // this.currentTask = response.data;
        // return this.currentTask;
      } catch (error) {
        this.error = error.message || '获取任务详情失败';
        console.error('获取任务详情失败:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // 提交任务
    async createTask(taskData) {
      this.loading = true;
      try {
        const formData = new FormData();
        
        // 添加基本任务信息
        Object.keys(taskData).forEach(key => {
          if (key !== 'images' && key !== 'files') {
            formData.append(key, taskData[key]);
          }
        });
        
        // 添加图片
        if (taskData.images && taskData.images.length) {
          taskData.images.forEach(image => {
            formData.append('images', image);
          });
        }
        
        // 添加附件
        if (taskData.files && taskData.files.length) {
          taskData.files.forEach(file => {
            formData.append('files', file);
          });
        }
        
        const response = await request.post('/tasks', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        
        return response.data;
      } catch (error) {
        this.error = error.message || '提交任务失败';
        console.error('提交任务失败:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    // 提交服务评价
    async submitEvaluation(taskId, evaluationData) {
      this.loading = true;
      try {
        // 使用模拟数据
        const result = await submitMockEvaluation(taskId, evaluationData);
        return result;
        
        // 实际API调用（目前注释掉）
        // const response = await request.post(`/tasks/${taskId}/evaluation`, evaluationData);
        // return response.data;
      } catch (error) {
        this.error = error.message || '提交评价失败';
        console.error('提交评价失败:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    // 确认任务报价
    async confirmTaskPrice(taskId) {
      this.loading = true;
      try {
        // 使用模拟数据
        const result = await confirmMockTaskPrice(taskId);
        return result;
        
        // 实际API调用（目前注释掉）
        // const response = await request.post(`/tasks/${taskId}/confirm-price`, { confirmed: true });
        // return response.data;
      } catch (error) {
        this.error = error.message || '确认报价失败';
        console.error('确认报价失败:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    // 获取高清图片预览
    async getImagePreview(imageId, options = {}) {
      try {
        // 构建查询参数
        const params = new URLSearchParams();
        if (options.width) params.append('width', options.width);
        if (options.height) params.append('height', options.height);
        
        const url = `/images/${imageId}/preview${params.toString() ? `?${params.toString()}` : ''}`;
        const response = await request.get(url);
        return response.data;
      } catch (error) {
        this.error = error.message || '获取图片预览失败';
        console.error('获取图片预览失败:', error);
        throw error;
      }
    },
    
    // 下载图片
    async downloadImage(imageId) {
      try {
        // 在微信小程序环境中，这个API通常会直接在组件中调用wx.downloadFile
        // 此处返回下载链接，以便在非微信环境中使用
        return `/api/v1/images/${imageId}/download`;
      } catch (error) {
        this.error = error.message || '下载图片失败';
        console.error('下载图片失败:', error);
        throw error;
      }
    },
    
    // 下载附件
    async downloadAttachment(taskId, fileId) {
      try {
        // 在微信小程序环境中，这个API通常会直接在组件中调用wx.downloadFile
        // 此处返回下载链接，以便在非微信环境中使用
        return `/api/v1/tasks/${taskId}/attachments/${fileId}/download`;
      } catch (error) {
        this.error = error.message || '下载附件失败';
        console.error('下载附件失败:', error);
        throw error;
      }
    }
  }
}); 