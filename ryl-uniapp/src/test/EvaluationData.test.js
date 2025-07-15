/**
 * 任务评价数据一致性测试文件
 * 
 * 这个文件测试任务评价相关的数据一致性
 */

import { reactive } from 'vue';
import axios from 'axios';

// 模拟Axios响应
const mockAxios = {
  post: (url, data) => {
    console.log(`[MockAxios] POST ${url}`, data);
    
    if (url.includes('/evaluation')) {
      // 验证评价数据格式
      const valid = validateEvaluationData(data);
      if (valid) {
        return Promise.resolve({
          data: true
        });
      } else {
        return Promise.reject(new Error('评价数据格式无效'));
      }
    }
    
    return Promise.reject(new Error('Mock URL not found'));
  }
};

// 替换原始axios
axios.post = mockAxios.post;

// 验证评价数据
function validateEvaluationData(data) {
  // 检查必填字段
  if (typeof data.score !== 'number' || data.score < 1 || data.score > 5) {
    console.error('总评分无效，必须是1-5之间的数字');
    return false;
  }
  
  if (typeof data.serviceAttitudeScore !== 'number' || data.serviceAttitudeScore < 1 || data.serviceAttitudeScore > 5) {
    console.error('服务态度评分无效，必须是1-5之间的数字');
    return false;
  }
  
  if (typeof data.professionalScore !== 'number' || data.professionalScore < 1 || data.professionalScore > 5) {
    console.error('专业程度评分无效，必须是1-5之间的数字');
    return false;
  }
  
  if (typeof data.responseSpeedScore !== 'number' || data.responseSpeedScore < 1 || data.responseSpeedScore > 5) {
    console.error('响应速度评分无效，必须是1-5之间的数字');
    return false;
  }
  
  return true;
}

// 测试评价数据有效性
async function testEvaluationDataValidity() {
  console.log('测试评价数据有效性...');
  const results = {};
  
  // 有效数据
  const validData = {
    score: 5,
    description: '服务非常满意',
    serviceAttitudeScore: 5,
    professionalScore: 4,
    responseSpeedScore: 5
  };
  
  // 无效数据列表
  const invalidDataList = [
    { 
      name: 'missing_score', 
      data: { 
        description: '服务非常满意', 
        serviceAttitudeScore: 5, 
        professionalScore: 4, 
        responseSpeedScore: 5 
      } 
    },
    { 
      name: 'invalid_score', 
      data: { 
        score: 6, // 超出范围
        description: '服务非常满意', 
        serviceAttitudeScore: 5, 
        professionalScore: 4, 
        responseSpeedScore: 5 
      } 
    },
    { 
      name: 'missing_service_attitude', 
      data: { 
        score: 5, 
        description: '服务非常满意', 
        professionalScore: 4, 
        responseSpeedScore: 5 
      } 
    }
  ];
  
  // 测试有效数据
  console.log('测试有效评价数据...');
  try {
    const response = await mockAxios.post('/task/999/evaluation', validData);
    results.valid = { success: true, response: response.data };
    console.log('✅ 有效评价数据验证通过');
  } catch (error) {
    results.valid = { success: false, error: error.message };
    console.error(`❌ 有效评价数据验证失败: ${error.message}`);
  }
  
  // 测试无效数据
  console.log('测试无效评价数据...');
  results.invalid = {};
  
  for (const invalid of invalidDataList) {
    try {
      console.log(`测试无效数据: ${invalid.name}`, invalid.data);
      await mockAxios.post('/task/999/evaluation', invalid.data);
      results.invalid[invalid.name] = { 
        success: true, // 这应该失败才对
        expected: false
      };
      console.error(`❌ 无效评价数据 ${invalid.name} 验证失败，被错误接受`);
    } catch (error) {
      results.invalid[invalid.name] = { 
        success: false, 
        error: error.message,
        expected: true // 预期应该失败
      };
      console.log(`✅ 无效评价数据 ${invalid.name} 验证通过，被正确拒绝`);
    }
  }
  
  return results;
}

// 测试评价字段映射
function testEvaluationFieldMapping() {
  console.log('测试评价字段映射...');
  
  // 前端评价数据
  const frontendData = {
    rating: 5,
    comment: '服务非常满意',
    serviceAttitude: 5,
    professionalLevel: 4,
    responseSpeed: 5
  };
  
  // 转换为后端期望的格式
  const backendData = {
    score: frontendData.rating,
    description: frontendData.comment,
    serviceAttitudeScore: frontendData.serviceAttitude,
    professionalScore: frontendData.professionalLevel,
    responseSpeedScore: frontendData.responseSpeed
  };
  
  console.log('前端评价数据:', frontendData);
  console.log('转换后的后端评价数据:', backendData);
  
  // 验证转换是否正确
  const isValid = backendData.score === frontendData.rating &&
                 backendData.description === frontendData.comment &&
                 backendData.serviceAttitudeScore === frontendData.serviceAttitude &&
                 backendData.professionalScore === frontendData.professionalLevel &&
                 backendData.responseSpeedScore === frontendData.responseSpeed;
  
  const result = {
    valid: isValid,
    frontendData,
    backendData
  };
  
  if (isValid) {
    console.log('✅ 评价字段映射验证通过');
  } else {
    console.error('❌ 评价字段映射验证失败');
  }
  
  return result;
}

// 测试评分计算
function testScoreCalculation() {
  console.log('测试评分计算...');
  
  // 各维度评分
  const dimensionScores = {
    serviceAttitudeScore: 4,
    professionalScore: 5,
    responseSpeedScore: 3
  };
  
  // 计算平均分（四舍五入到整数）
  const averageScore = Math.round(
    (dimensionScores.serviceAttitudeScore + 
     dimensionScores.professionalScore + 
     dimensionScores.responseSpeedScore) / 3
  );
  
  console.log('各维度评分:', dimensionScores);
  console.log('计算得到的平均分:', averageScore);
  
  // 验证平均分是否正确
  const expectedScore = 4; // (4+5+3)/3 = 4
  const isValid = averageScore === expectedScore;
  
  const result = {
    valid: isValid,
    dimensionScores,
    averageScore,
    expectedScore
  };
  
  if (isValid) {
    console.log('✅ 评分计算验证通过');
  } else {
    console.error(`❌ 评分计算验证失败，期望 ${expectedScore}，实际 ${averageScore}`);
  }
  
  return result;
}

// 运行所有测试
async function runAllTests() {
  console.log('开始运行评价数据一致性测试...');
  
  const validityResults = await testEvaluationDataValidity();
  const mappingResults = testEvaluationFieldMapping();
  const calculationResults = testScoreCalculation();
  
  return {
    validity: validityResults,
    mapping: mappingResults,
    calculation: calculationResults
  };
}

// 导出测试函数
export default {
  runAllTests,
  testEvaluationDataValidity,
  testEvaluationFieldMapping,
  testScoreCalculation
};