/**
 * 日期工具类
 */

/**
 * 将日期时间本地格式 (yyyy-MM-ddTHH:mm) 转换为 ISO 格式字符串
 * @param {string} localDateTimeString 本地时间字符串格式 (如 2025-06-25T10:30)
 * @returns {string} ISO格式字符串 (如 2025-06-25T10:30:00.000Z)
 */
export function convertLocalDateTimeToISO(localDateTimeString) {
  if (!localDateTimeString) return null;
  
  try {
    // 创建日期对象
    const date = new Date(localDateTimeString);
    
    // 检查是否有效
    if (isNaN(date.getTime())) {
      console.error('无效的日期时间格式:', localDateTimeString);
      return null;
    }
    
    // 返回ISO格式
    return date.toISOString();
  } catch (error) {
    console.error('日期转换错误:', error);
    return null;
  }
}

/**
 * 将时间戳或ISO字符串转换为本地日期时间格式 (yyyy-MM-ddTHH:mm)
 * @param {string|number} dateTimeValue 时间戳或ISO字符串
 * @returns {string} 本地日期时间格式 (如 2025-06-25T10:30)
 */
export function formatToLocalDateTime(dateTimeValue) {
  if (!dateTimeValue) return '';
  
  try {
    // 创建日期对象
    const date = new Date(dateTimeValue);
    
    // 检查是否有效
    if (isNaN(date.getTime())) {
      console.error('无效的日期时间值:', dateTimeValue);
      return '';
    }
    
    // 格式化为 yyyy-MM-ddTHH:mm
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  } catch (error) {
    console.error('日期格式化错误:', error);
    return '';
  }
} 