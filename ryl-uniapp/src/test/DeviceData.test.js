/**
 * 设备数据一致性测试文件
 * 
 * 这个文件测试不同任务类型的设备数据一致性
 */

import { reactive } from 'vue';
import axios from 'axios';

// 模拟Axios响应
const mockAxios = {
  post: (url, data) => {
    console.log(`[MockAxios] POST ${url}`, data);
    
    if (url === '/task/create') {
      // 验证设备数据格式
      if (data.device) {
        return Promise.resolve({
          data: 999 // 返回创建的任务ID
        });
      } else {
        return Promise.reject(new Error('请求中缺少设备信息'));
      }
    }
    
    return Promise.reject(new Error('Mock URL not found'));
  }
};

// 替换原始axios
axios.post = mockAxios.post;

// 设备数据模板（按任务类型）
const deviceTemplates = {
  // 维修任务
  repair: {
    name: '显微镜',
    type: '光学设备',
    brand: '蔡司',
    model: 'Primo Star',
    faultDescription: '对焦系统失灵',
    quantity: 1
  },
  
  // 保养任务
  maintenance: {
    name: '离心机',
    type: '实验设备',
    brand: 'Thermo Fisher',
    model: 'CL10',
    faultDescription: '需要定期保养',
    quantity: 2
  },
  
  // 回收任务
  recycle: {
    name: '旧光谱仪',
    type: '分析设备',
    brand: 'Agilent',
    model: '8453',
    quantity: 1,
    accessories: '包含所有原装配件'
  },
  
  // 租赁任务
  leasing: {
    name: '液相色谱仪',
    type: '分析设备',
    brand: 'Waters',
    model: 'Alliance',
    quantity: 1,
    accessories: '需要自动进样器'
  },
  
  // 培训预约
  training: {
    name: '质谱仪',
    type: '分析设备',
    brand: 'Thermo',
    model: 'Orbitrap',
    appointmentTime: '2023-12-15 14:00'
  },
  
  // 仪器验证
  verification: {
    name: 'HPLC',
    type: '分析设备',
    brand: 'Agilent',
    model: '1260',
    verificationType: 'OQ运行验证',
    quantity: 1
  },
  
  // 仪器选型
  selection: {
    name: '液氮罐',
    purpose: '样品长期存储',
    requirementDescription: '需要能存储500个样品的液氮罐',
    quantity: 2
  },
  
  // 仪器安装
  installation: {
    name: '原子吸收光谱仪',
    type: '分析设备',
    brand: 'PerkinElmer',
    model: 'PinAAcle 900F',
    quantity: 1
  }
};

// 测试函数
async function testDeviceDataConsistency() {
  console.log('测试不同任务类型的设备数据一致性...');
  const results = {};
  
  // 测试每种任务类型
  for (const [taskType, deviceData] of Object.entries(deviceTemplates)) {
    console.log(`测试 ${taskType} 类型任务的设备数据...`);
    
    const taskData = {
      title: `测试${taskType}任务`,
      description: `测试${taskType}描述`,
      taskType: taskType,
      device: deviceData
    };
    
    try {
      const response = await mockAxios.post('/task/create', taskData);
      results[taskType] = { success: true, taskId: response.data };
      console.log(`✅ ${taskType} 任务设备数据格式验证通过`);
    } catch (error) {
      results[taskType] = { success: false, error: error.message };
      console.error(`❌ ${taskType} 任务设备数据格式验证失败: ${error.message}`);
    }
  }
  
  return results;
}

// 测试设备数据字段验证
function testDeviceDataValidation() {
  console.log('测试设备数据字段验证...');
  
  // 验证规则
  const validationRules = {
    repair: ['name', 'faultDescription'],
    maintenance: ['name'],
    recycle: ['name', 'quantity'],
    leasing: ['name', 'quantity'],
    training: ['name', 'appointmentTime'],
    verification: ['name', 'verificationType'],
    selection: ['purpose', 'requirementDescription'],
    installation: ['name']
  };
  
  const results = {};
  
  // 验证每种任务类型的必填字段
  for (const [taskType, requiredFields] of Object.entries(validationRules)) {
    console.log(`验证 ${taskType} 类型任务的必填字段: ${requiredFields.join(', ')}`);
    
    const template = {...deviceTemplates[taskType]};
    let valid = true;
    let missingFields = [];
    
    // 检查是否有必填字段
    for (const field of requiredFields) {
      if (!template[field]) {
        valid = false;
        missingFields.push(field);
      }
    }
    
    results[taskType] = { 
      valid, 
      requiredFields,
      missingFields,
      template 
    };
    
    if (valid) {
      console.log(`✅ ${taskType} 设备数据验证通过`);
    } else {
      console.error(`❌ ${taskType} 设备数据缺少必填字段: ${missingFields.join(', ')}`);
    }
  }
  
  return results;
}

// 测试设备数据转换
function testDeviceDataConversion() {
  console.log('测试设备数据转换...');
  
  const results = {};
  
  // 前端数据格式（扁平化）
  const frontendData = {
    title: '显微镜维修',
    description: '设备无法正常对焦',
    taskType: 'repair',
    deviceName: '显微镜',
    deviceType: '光学设备',
    deviceBrand: '蔡司',
    deviceModel: 'Primo Star',
    faultDescription: '对焦系统失灵',
    quantity: 1
  };
  
  // 转换为后端期望的嵌套格式
  const backendData = {
    title: frontendData.title,
    description: frontendData.description,
    taskType: frontendData.taskType,
    device: {
      name: frontendData.deviceName,
      type: frontendData.deviceType,
      brand: frontendData.deviceBrand,
      model: frontendData.deviceModel,
      faultDescription: frontendData.faultDescription,
      quantity: frontendData.quantity
    }
  };
  
  console.log('前端扁平数据格式:', frontendData);
  console.log('转换后的后端嵌套格式:', backendData);
  
  // 验证转换后的数据
  const isValid = backendData.device && 
                 backendData.device.name === frontendData.deviceName &&
                 backendData.device.brand === frontendData.deviceBrand;
  
  results.conversion = {
    valid: isValid,
    frontendData,
    backendData
  };
  
  if (isValid) {
    console.log('✅ 设备数据格式转换验证通过');
  } else {
    console.error('❌ 设备数据格式转换验证失败');
  }
  
  return results;
}

// 运行所有测试
async function runAllTests() {
  console.log('开始运行设备数据一致性测试...');
  
  const consistencyResults = await testDeviceDataConsistency();
  const validationResults = testDeviceDataValidation();
  const conversionResults = testDeviceDataConversion();
  
  return {
    consistency: consistencyResults,
    validation: validationResults,
    conversion: conversionResults
  };
}

// 导出测试函数
export default {
  runAllTests,
  testDeviceDataConsistency,
  testDeviceDataValidation,
  testDeviceDataConversion
};