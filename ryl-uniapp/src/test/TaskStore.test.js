/**
 * 任务 Store 测试文件
 * 
 * 注意：这是一个模拟测试文件，用于在开发环境中进行手动测试
 * 在实际项目中，应当使用像 Jest、Vitest 或 Cypress 等测试框架
 */

import { useTaskStore } from '../stores/task.js';
import { createPinia } from 'pinia';
import axios from 'axios';
import { createApp } from 'vue';

// 创建一个Vue应用实例和Pinia Store
const app = createApp({});
const pinia = createPinia();
app.use(pinia);

// 创建一个Mock axios实例来测试API调用
const mockAxios = {
  get: (url) => {
    console.log(`[MockAxios] GET ${url}`);
    
    if (url === '/task/list') {
      return Promise.resolve({
        data: [
          { id: 1, title: '测试任务1', taskType: 'repair' },
          { id: 2, title: '测试任务2', taskType: 'maintenance' }
        ]
      });
    }
    
    if (url.startsWith('/task/1')) {
      return Promise.resolve({
        data: {
          id: 1, 
          title: '测试任务1', 
          taskType: 'repair',
          device: {
            name: '显微镜',
            brand: '蔡司',
            model: 'Primo Star'
          }
        }
      });
    }
    
    if (url === '/task/1/steps') {
      return Promise.resolve({
        data: [
          { id: 1, taskId: 1, stepIndex: 0, stepName: '创建任务', createTime: '2023-06-01 12:00:00' },
          { id: 2, taskId: 1, stepIndex: 1, stepName: '接收任务', createTime: '2023-06-01 13:00:00' }
        ]
      });
    }
    
    return Promise.reject(new Error('Mock URL not found'));
  },
  
  post: (url, data) => {
    console.log(`[MockAxios] POST ${url}`, data);
    
    if (url === '/task/create') {
      return Promise.resolve({
        data: 999 // 返回创建的任务ID
      });
    }
    
    if (url === '/files/task/image') {
      return Promise.resolve({
        data: {
          id: 1,
          taskId: data.get('taskId'),
          imageUrl: '/files/images/2023/01/01/test.jpg',
          imageType: data.get('imageType'),
          sort: data.get('sort') || 0
        }
      });
    }
    
    if (url.includes('/evaluation')) {
      return Promise.resolve({
        data: true
      });
    }
    
    if (url.includes('/step/')) {
      return Promise.resolve({
        data: true
      });
    }
    
    return Promise.reject(new Error('Mock URL not found'));
  }
};

// 替换原始axios
axios.get = mockAxios.get;
axios.post = mockAxios.post;

// 创建TaskStore实例
const taskStore = useTaskStore();

// 测试用例

async function testFetchTaskList() {
  console.log('测试获取任务列表...');
  try {
    const tasks = await taskStore.fetchTaskList();
    console.log('获取任务列表成功:', tasks);
    console.assert(tasks.length === 2, '任务列表长度应为2');
    console.assert(tasks[0].title === '测试任务1', '第一个任务标题应为"测试任务1"');
  } catch (error) {
    console.error('获取任务列表失败:', error);
  }
}

async function testFetchTaskDetail() {
  console.log('测试获取任务详情...');
  try {
    const task = await taskStore.fetchTaskDetail(1);
    console.log('获取任务详情成功:', task);
    console.assert(task.title === '测试任务1', '任务标题应为"测试任务1"');
    console.assert(task.device.name === '显微镜', '设备名称应为"显微镜"');
  } catch (error) {
    console.error('获取任务详情失败:', error);
  }
}

async function testFetchTaskSteps() {
  console.log('测试获取任务步骤...');
  try {
    const steps = await taskStore.fetchTaskSteps(1);
    console.log('获取任务步骤成功:', steps);
    console.assert(steps.length === 2, '步骤数量应为2');
    console.assert(steps[0].stepName === '创建任务', '第一个步骤名称应为"创建任务"');
  } catch (error) {
    console.error('获取任务步骤失败:', error);
  }
}

async function testCreateTask() {
  console.log('测试创建任务...');
  
  // 创建一个测试任务对象
  const taskData = {
    title: '测试维修任务',
    content: '设备故障描述',
    taskType: 'repair',
    description: '测试描述',
    priority: 1,
    deviceName: '显微镜',
    deviceType: '光学设备',
    deviceBrand: '蔡司',
    deviceModel: 'Primo Star',
    faultDescription: '无法对焦',
    quantity: 1,
    images: [
      new File(['image content'], 'test-image.jpg', { type: 'image/jpeg' })
    ]
  };
  
  try {
    const taskId = await taskStore.createTask(taskData);
    console.log('创建任务成功，返回ID:', taskId);
    console.assert(taskId === 999, '创建任务应返回ID 999');
  } catch (error) {
    console.error('创建任务失败:', error);
  }
}

async function testUploadTaskImage() {
  console.log('测试上传任务图片...');
  const imageData = {
    file: new File(['image content'], 'test-image.jpg', { type: 'image/jpeg' }),
    sort: 1
  };
  
  try {
    const result = await taskStore.uploadTaskImage(imageData, 999, 1);
    console.log('上传任务图片成功:', result);
    console.assert(result.imageUrl === '/files/images/2023/01/01/test.jpg', '图片URL不正确');
    console.assert(result.taskId === "999", '任务ID不正确');
    console.assert(result.imageType === "1", '图片类型不正确');
    console.assert(result.sort === "1", '排序不正确');
  } catch (error) {
    console.error('上传任务图片失败:', error);
  }
}

async function testSubmitEvaluation() {
  console.log('测试提交服务评价...');
  const evaluationData = {
    score: 5,
    content: '服务非常好',
    serviceAttitudeScore: 5,
    professionalScore: 4,
    responseSpeedScore: 5
  };
  
  try {
    const result = await taskStore.submitEvaluation(999, evaluationData);
    console.log('提交服务评价成功:', result);
    console.assert(result === true, '提交评价应返回true');
  } catch (error) {
    console.error('提交服务评价失败:', error);
  }
}

async function testUpdateTaskStep() {
  console.log('测试更新任务步骤...');
  try {
    const result = await taskStore.updateTaskStep(999, 2, '测试步骤更新');
    console.log('更新任务步骤成功:', result);
    console.assert(result === true, '更新步骤应返回true');
  } catch (error) {
    console.error('更新任务步骤失败:', error);
  }
}

// 运行测试
async function runAllTests() {
  console.log('开始运行所有测试...');
  
  await testFetchTaskList();
  await testFetchTaskDetail();
  await testFetchTaskSteps();
  await testCreateTask();
  await testUploadTaskImage();
  await testSubmitEvaluation();
  await testUpdateTaskStep();
  
  console.log('所有测试完成!');
}

// 导出测试函数
export default runAllTests;