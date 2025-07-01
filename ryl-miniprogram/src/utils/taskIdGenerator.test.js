/**
 * 任务ID生成器测试文件
 */
import { generateTaskId } from './taskIdGenerator';

// 模拟测试函数
function testGenerateTaskId() {
  console.log('===== 任务ID生成器测试 =====');
  
  // 测试不同类型的公司名称
  const testCases = [
    '北京科技有限公司',
    '上海ABC科技有限公司',
    '广州瑞屹林信息技术有限公司',
    'Microsoft China',
    '深圳市腾讯计算机系统有限公司',
    '阿里巴巴集团',
    '华为技术有限公司',
    '中国移动通信集团公司',
    '北京大学',
    '北京市海淀区实验中学',
    '上海交通大学附属医院',
    '广东省广州市天河区某某公司',
    '123测试公司',
    '一二三四五六七八九十有限公司'
  ];
  
  // 测试每个案例
  testCases.forEach(companyName => {
    const taskId = generateTaskId(companyName);
    console.log(`公司名称: ${companyName}`);
    console.log(`生成的任务ID: ${taskId}`);
    console.log('----------------------------');
  });
  
  // 测试多次生成，验证随机性
  console.log('测试随机性:');
  const company = '瑞屹林科技';
  for (let i = 0; i < 5; i++) {
    console.log(`第${i+1}次: ${generateTaskId(company)}`);
  }
}

// 导出测试函数
export default testGenerateTaskId; 