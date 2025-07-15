/**
 * 任务ID生成器
 * 
 * 生成规则：
 * RYL（瑞屹林，公司名的缩写）+ 
 * 客户公司名首字母缩写（如果前面有省会和城市名就省略，只要客户公司的字号名称的首字母大写的缩写）+ 
 * 年月日 + 
 * 随机4位字母和数字组合
 * 
 * 示例：RYLABJ20250701A1B2
 */

import { getFirstLetter } from './pinyinUtil';

/**
 * 提取公司名称的首字母缩写
 * 
 * @param {string} companyName - 公司全称
 * @returns {string} 公司名首字母缩写
 */
function extractCompanyInitials(companyName) {
  if (!companyName) return '';
  
  // 去除可能的空格
  const trimmedName = companyName.trim();
  
  // 不再移除省市名称前缀，保留完整公司名称
  let processedName = trimmedName;
  
  // 移除常见的公司后缀
  const commonSuffixes = [
    '有限公司', '股份有限公司', '有限责任公司', '集团', '集团公司', 
    '科技', '科技有限公司', '技术有限公司', '股份公司', '公司'
  ];
  
  for (const suffix of commonSuffixes) {
    if (processedName.endsWith(suffix)) {
      processedName = processedName.substring(0, processedName.length - suffix.length);
      break;
    }
  }
  
  // 提取前5个字符的首字母（支持中英文混合）
  const initials = Array.from(processedName)
    // 只保留汉字和字母
    .filter(char => /[\u4e00-\u9fa5a-zA-Z]/.test(char))
    // 只取前5个字符
    .slice(0, 5)
    // 使用拼音工具获取首字母
    .map(char => getFirstLetter(char))
    .join('');
  
  // 如果首字母缩写为空，则返回默认值"XX"
  return initials || "XX";
}

/**
 * 获取当前日期时间的字符串（格式：YYYYMMDD）
 * 
 * @returns {string} 格式化的日期时间字符串
 */
function getCurrentDateTimeString() {
  const now = new Date();
  const year = now.getFullYear();
  // 月份需要+1，因为getMonth()返回的是0-11
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  
  return `${year}${month}${day}`;
}

/**
 * 生成4位随机字母和数字组合
 * 
 * @returns {string} 4位随机字符串
 */
function generateRandomString() {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
  let result = '';
  for (let i = 0; i < 4; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}

/**
 * 生成任务ID
 * 
 * @param {string} companyName - 客户公司名称
 * @returns {string} 生成的任务ID
 */
export function generateTaskId(companyName) {
  const prefix = 'RYL'; // 瑞屹林的缩写
  const companyInitials = extractCompanyInitials(companyName);
  const dateTimeString = getCurrentDateTimeString();
  const randomString = generateRandomString();
  
  // 创建一个缓存键，用于检查本地是否已存在相同ID
  const taskId = `${prefix}${companyInitials}${dateTimeString}${randomString}`;
  
  // 添加一个简单的重复检查机制
  // 将生成的taskId存储在本地，下次生成时先检查是否已存在
  const existingIds = uni.getStorageSync('generatedTaskIds') 
    ? JSON.parse(uni.getStorageSync('generatedTaskIds')) 
    : {};
  
  // 如果ID已存在，递归重新生成
  if (existingIds[taskId]) {
    console.warn(`任务ID ${taskId} 重复，重新生成`);
    do {
      const newTaskId = `${prefix}${companyInitials}${dateTimeString}${generateRandomString()}`;
      existingIds[newTaskId] = true;
      uni.setStorageSync('generatedTaskIds', JSON.stringify(existingIds));
    } while (existingIds[taskId]);
  }
  
  // 存储生成的ID，限制存储最近50个
  existingIds[taskId] = true;
  if (Object.keys(existingIds).length > 50) {
    // 移除最老的ID
    const oldestKey = Object.keys(existingIds)[0];
    delete existingIds[oldestKey];
  }
  uni.setStorageSync('generatedTaskIds', JSON.stringify(existingIds));
  
  return taskId;
}

export default generateTaskId; 