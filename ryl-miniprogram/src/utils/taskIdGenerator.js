/**
 * 任务ID生成器
 * 
 * 生成规则：
 * RYL（瑞屹林，公司名的缩写）+ 
 * 客户公司名首字母缩写（如果前面有省会和城市名就省略，只要客户公司的字号名称的首字母大写的缩写）+ 
 * 年月日 + 
 * 随机4位数字（0000-9999）
 * 
 * 示例：RYLABJ20250701XXXX（瑞屹林+ABC公司+2025年7月1日+随机数）
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
 * 获取当前日期的年月日字符串（格式：YYYYMMDD）
 * 
 * @returns {string} 格式化的日期字符串
 */
function getCurrentDateString() {
  const now = new Date();
  const year = now.getFullYear();
  // 月份需要+1，因为getMonth()返回的是0-11
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  
  return `${year}${month}${day}`;
}

/**
 * 生成4位随机数字字符串
 * 
 * @returns {string} 4位随机数字
 */
function generateRandomDigits() {
  // 生成0-9999之间的随机数
  const randomNum = Math.floor(Math.random() * 10000);
  // 转换为字符串并补齐前导零
  return String(randomNum).padStart(4, '0');
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
  const dateString = getCurrentDateString();
  const randomDigits = generateRandomDigits();
  
  return `${prefix}${companyInitials}${dateString}${randomDigits}`;
}

export default generateTaskId; 