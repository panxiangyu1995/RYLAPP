import { defineStore } from 'pinia';
import request from '@/utils/request';

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
        const response = await request.get('/tasks');
        this.taskList = response.data.list || [];
        return this.taskList;
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
        const response = await request.get(`/tasks/${taskId}`);
        this.currentTask = response.data;
        return this.currentTask;
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
        const response = await request.post(`/tasks/${taskId}/evaluation`, evaluationData);
        return response.data;
      } catch (error) {
        this.error = error.message || '提交评价失败';
        console.error('提交评价失败:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
}); 