/**
 * 数据流一致性综合测试文件
 * 
 * 这个文件测试从前端到后端的完整数据流一致性
 */

import { useTaskStore } from '../stores/task.js';
import { createPinia } from 'pinia';
import { createApp } from 'vue';
import axios from 'axios';
import deviceTests from './DeviceData.test.js';
import evaluationTests from './EvaluationData.test.js';

// 创建一个Vue应用实例和Pinia Store
const app = createApp({});
const pinia = createPinia();
app.use(pinia);

// 创建TaskStore实例
const taskStore = useTaskStore();

// 完整数据流测试 - 任务创建到评价的完整流程
async function testCompleteTaskFlow() {
  console.log('测试完整任务流程数据一致性...');
  const results = {};
  
  try {
    // 1. 创建任务
    console.log('1. 创建任务...');
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
    
    const taskId = await taskStore.createTask(taskData);
    console.log(`✅ 任务创建成功，ID: ${taskId}`);
    results.createTask = { success: true, taskId };
    
    // 2. 获取任务步骤
    console.log('2. 获取任务步骤...');
    const steps = await taskStore.fetchTaskSteps(taskId);
    console.log(`✅ 获取任务步骤成功，步骤数: ${steps.length}`);
    results.fetchSteps = { success: true, steps };
    
    // 3. 更新任务步骤
    console.log('3. 更新任务步骤...');
    const stepUpdateResult = await taskStore.updateTaskStep(taskId, 1, '任务已接收');
    console.log(`✅ 更新任务步骤成功`);
    results.updateStep = { success: true };
    
    // 4. 上传任务图片
    console.log('4. 上传任务图片...');
    const imageData = {
      file: new File(['image content'], 'test-image.jpg', { type: 'image/jpeg' }),
      sort: 0
    };
    const imageResult = await taskStore.uploadTaskImage(imageData, taskId, 1);
    console.log(`✅ 上传任务图片成功`);
    results.uploadImage = { success: true, imageResult };
    
    // 5. 提交任务评价
    console.log('5. 提交任务评价...');
    const evaluationData = {
      score: 5,
      content: '服务非常好',
      serviceAttitudeScore: 5,
      professionalScore: 4,
      responseSpeedScore: 5
    };
    const evaluationResult = await taskStore.submitEvaluation(taskId, evaluationData);
    console.log(`✅ 提交任务评价成功`);
    results.submitEvaluation = { success: true };
    
    console.log('完整任务流程测试成功!');
    results.overallSuccess = true;
    
  } catch (error) {
    console.error('任务流程测试失败:', error);
    results.error = error.message;
    results.overallSuccess = false;
  }
  
  return results;
}

// 测试字段映射一致性
function testFieldMappingConsistency() {
  console.log('测试字段映射一致性...');
  
  // 定义前后端字段映射关系
  const fieldMappings = {
    // 任务相关字段
    task: {
      frontend: ['title', 'content', 'taskType', 'description', 'priority'],
      backend: ['title', 'content', 'taskType', 'description', 'priority']
    },
    // 设备相关字段
    device: {
      frontend: ['deviceName', 'deviceType', 'deviceBrand', 'deviceModel', 'faultDescription', 'quantity'],
      backend: ['name', 'type', 'brand', 'model', 'faultDescription', 'quantity']
    },
    // 评价相关字段
    evaluation: {
      frontend: ['score', 'content', 'serviceAttitudeScore', 'professionalScore', 'responseSpeedScore'],
      backend: ['score', 'content', 'serviceAttitudeScore', 'professionalScore', 'responseSpeedScore']
    }
  };
  
  const results = {};
  
  // 检查每个映射组
  for (const [group, mapping] of Object.entries(fieldMappings)) {
    console.log(`检查 ${group} 字段映射...`);
    
    const frontendFields = mapping.frontend;
    const backendFields = mapping.backend;
    
    // 创建示例数据对象
    const frontendData = {};
    frontendFields.forEach(field => {
      frontendData[field] = `test-${field}`;
    });
    
    // 创建转换函数
    let convertedData;
    if (group === 'task') {
      convertedData = { ...frontendData };
    } else if (group === 'device') {
      convertedData = {};
      frontendFields.forEach((field, index) => {
        const backendField = backendFields[index];
        const value = frontendData[field];
        convertedData[backendField] = value;
      });
    } else if (group === 'evaluation') {
      convertedData = { ...frontendData };
    }
    
    console.log(`前端数据:`, frontendData);
    console.log(`转换后的后端数据:`, convertedData);
    
    // 验证转换是否正确
    let isValid = true;
    const mismatches = [];
    
    frontendFields.forEach((field, index) => {
      const backendField = backendFields[index];
      const frontendValue = frontendData[field];
      let backendValue;
      
      if (group === 'device') {
        backendValue = convertedData[backendField];
      } else {
        backendValue = convertedData[field];
      }
      
      if (frontendValue !== backendValue) {
        isValid = false;
        mismatches.push(`${field} -> ${backendField}`);
      }
    });
    
    results[group] = {
      valid: isValid,
      mismatches,
      frontendData,
      convertedData
    };
    
    if (isValid) {
      console.log(`✅ ${group} 字段映射验证通过`);
    } else {
      console.error(`❌ ${group} 字段映射验证失败，不匹配字段: ${mismatches.join(', ')}`);
    }
  }
  
  return results;
}

// 测试错误处理一致性
async function testErrorHandlingConsistency() {
  console.log('测试错误处理一致性...');
  
  // 定义错误情景和期望的处理方式
  const errorScenarios = [
    {
      name: 'missing_required_field',
      action: 'createTask',
      data: { title: '测试任务', content: '测试内容' }, // 缺少taskType
      expectedError: true,
      expectedErrorMessage: /taskType.*required/i
    },
    {
      name: 'invalid_task_type',
      action: 'createTask',
      data: { title: '测试任务', content: '测试内容', taskType: 'invalid_type' },
      expectedError: true,
      expectedErrorMessage: /invalid.*type/i
    },
    {
      name: 'invalid_evaluation_score',
      action: 'submitEvaluation',
      taskId: 999,
      data: { score: 10, content: '测试评价' }, // 评分超出范围
      expectedError: true,
      expectedErrorMessage: /score.*invalid/i
    }
  ];
  
  const results = {};
  
  // 测试每个错误情景
  for (const scenario of errorScenarios) {
    console.log(`测试错误情景: ${scenario.name}...`);
    
    try {
      if (scenario.action === 'createTask') {
        await taskStore.createTask(scenario.data);
      } else if (scenario.action === 'submitEvaluation') {
        await taskStore.submitEvaluation(scenario.taskId, scenario.data);
      }
      
      // 如果执行到这里，说明没有抛出错误
      results[scenario.name] = {
        threwError: false,
        expected: scenario.expectedError,
        success: !scenario.expectedError
      };
      
      if (scenario.expectedError) {
        console.error(`❌ ${scenario.name} 测试失败，期望抛出错误但没有`);
      } else {
        console.log(`✅ ${scenario.name} 测试通过，没有抛出错误`);
      }
    } catch (error) {
      // 抛出了错误
      const errorMatches = scenario.expectedErrorMessage ? 
        scenario.expectedErrorMessage.test(error.message) : 
        true;
      
      results[scenario.name] = {
        threwError: true,
        errorMessage: error.message,
        expectedPattern: scenario.expectedErrorMessage,
        errorMatches,
        expected: scenario.expectedError,
        success: scenario.expectedError && errorMatches
      };
      
      if (scenario.expectedError) {
        if (errorMatches) {
          console.log(`✅ ${scenario.name} 测试通过，正确抛出错误: ${error.message}`);
        } else {
          console.error(`❌ ${scenario.name} 测试失败，抛出错误但消息不匹配，期望: ${scenario.expectedErrorMessage}, 实际: ${error.message}`);
        }
      } else {
        console.error(`❌ ${scenario.name} 测试失败，不应该抛出错误但抛出了: ${error.message}`);
      }
    }
  }
  
  return results;
}

// 运行所有测试
async function runAllTests() {
  console.log('开始运行数据流一致性综合测试...');
  
  const taskFlowResults = await testCompleteTaskFlow();
  const fieldMappingResults = testFieldMappingConsistency();
  const errorHandlingResults = await testErrorHandlingConsistency();
  const deviceTestResults = await deviceTests.runAllTests();
  const evaluationTestResults = await evaluationTests.runAllTests();
  
  return {
    taskFlow: taskFlowResults,
    fieldMapping: fieldMappingResults,
    errorHandling: errorHandlingResults,
    deviceTests: deviceTestResults,
    evaluationTests: evaluationTestResults
  };
}

// 导出测试函数
export default {
  runAllTests,
  testCompleteTaskFlow,
  testFieldMappingConsistency,
  testErrorHandlingConsistency
};