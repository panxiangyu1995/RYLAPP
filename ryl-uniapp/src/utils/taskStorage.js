/**
 * 任务存储工具类
 * 用于在前端本地存储任务记录，并检测重复提交
 */

// 存储键名
const STORAGE_KEY = 'submitted_tasks';

// 检测运行环境
const isWechatMiniProgram = () => {
  return typeof wx !== 'undefined' && wx.getStorageSync && typeof wx.getStorageSync === 'function';
};

/**
 * 存储适配器 - 提供统一的存储接口
 */
const storageAdapter = {
  // 获取存储数据
  getItem(key) {
    try {
      if (isWechatMiniProgram()) {
        return wx.getStorageSync(key);
      } else {
        const value = uni.getStorageSync(key);
        if (value) {
          return JSON.parse(value);
        }
        return null;
      }
    } catch (error) {
      console.error('获取存储数据失败:', error);
      return null;
    }
  },

  // 设置存储数据
  setItem(key, value) {
    try {
      const stringValue = typeof value === 'string' ? value : JSON.stringify(value);
      if (isWechatMiniProgram()) {
        wx.setStorageSync(key, stringValue);
      } else {
        uni.setStorageSync(key, stringValue);
      }
      return true;
    } catch (error) {
      console.error('设置存储数据失败:', error);
      return false;
    }
  },

  // 移除存储数据
  removeItem(key) {
    try {
      if (isWechatMiniProgram()) {
        wx.removeStorageSync(key);
      } else {
        uni.removeStorageSync(key);
      }
      return true;
    } catch (error) {
      console.error('移除存储数据失败:', error);
      return false;
    }
  }
};

/**
 * 任务存储类
 */
class TaskStorage {
  /**
   * 获取已提交的任务列表
   * @returns {Array} 任务列表
   */
  getSubmittedTasks() {
    try {
      const tasksString = storageAdapter.getItem(STORAGE_KEY);
      if (!tasksString) {
        return [];
      }
      
      const tasks = JSON.parse(tasksString);
      if (!Array.isArray(tasks)) {
        return [];
      }
      
      return tasks;
    } catch (error) {
      console.error('解析任务列表失败:', error);
      return [];
    }
  }

  /**
   * 保存任务记录
   * @param {Object} task 任务数据
   * @returns {Boolean} 是否保存成功
   */
  saveTask(task) {
    try {
      // 获取现有任务列表
      const tasks = this.getSubmittedTasks();
      
      // 清理过期数据
      const cleanedTasks = this.cleanExpiredTasks(tasks);
      
      // 添加新任务，包含时间戳
      const taskWithTimestamp = {
        ...task,
        timestamp: new Date().getTime(),
        customerId: task.customer?.id || 'unknown'
      };
      
      cleanedTasks.push(taskWithTimestamp);
      
      // 保存回存储
      return storageAdapter.setItem(STORAGE_KEY, JSON.stringify(cleanedTasks));
    } catch (error) {
      console.error('保存任务记录失败:', error);
      return false;
    }
  }

  /**
   * 清理过期任务数据（15天前的记录）
   * @param {Array} tasks 任务列表
   * @returns {Array} 清理后的任务列表
   */
  cleanExpiredTasks(tasks) {
    if (!Array.isArray(tasks)) {
      return [];
    }
    
    const now = new Date().getTime();
    const fifteenDaysInMs = 15 * 24 * 60 * 60 * 1000;
    const cutoffTime = now - fifteenDaysInMs;
    
    return tasks.filter(task => {
      return task.timestamp && task.timestamp > cutoffTime;
    });
  }

  /**
   * 检测是否存在重复任务（10分钟内提交的相同任务）
   * @param {Object} task 待检测的任务
   * @returns {Object|null} 重复的任务或null
   */
  checkDuplicateTask(task) {
    try {
      if (!task) {
        return null;
      }
      
      // 获取任务列表
      const tasks = this.getSubmittedTasks();
      if (!tasks.length) {
        return null;
      }
      
      // 当前时间
      const now = new Date().getTime();
      // 10分钟（毫秒）
      const tenMinutesInMs = 10 * 60 * 1000;
      // 10分钟前的时间戳
      const tenMinutesAgo = now - tenMinutesInMs;
      
      // 获取当前用户ID
      const customerId = task.customer?.id || 'unknown';
      
      // 筛选10分钟内该用户提交的任务
      const recentTasks = tasks.filter(t => {
        return t.timestamp > tenMinutesAgo && 
               t.customerId === customerId;
      });
      
      if (!recentTasks.length) {
        return null;
      }
      
      // 查找重复任务
      const duplicateTask = recentTasks.find(t => {
        // 比较基本属性
        const basicMatch = t.taskType === task.taskType && 
                          t.title === task.title && 
                          t.description === task.description;
        
        if (!basicMatch) {
          return false;
        }
        
        // 比较图片名称（如果有）
        const imagesMatch = this.compareArrays(
          this.extractFileNames(t.images), 
          this.extractFileNames(task.images)
        );
        
        // 比较附件名称（如果有）
        const filesMatch = this.compareArrays(
          this.extractFileNames(t.files), 
          this.extractFileNames(task.files)
        );
        
        return imagesMatch && filesMatch;
      });
      
      return duplicateTask || null;
    } catch (error) {
      console.error('检测重复任务失败:', error);
      return null;
    }
  }

  /**
   * 提取文件名数组
   * @param {Array} files 文件数组
   * @returns {Array} 文件名数组
   */
  extractFileNames(files) {
    if (!files || !Array.isArray(files)) {
      return [];
    }
    
    return files.map(file => {
      // 处理不同的文件对象格式
      if (typeof file === 'string') {
        // 如果是字符串（URL或路径），提取文件名
        const parts = file.split('/');
        return parts[parts.length - 1];
      } else if (file && file.name) {
        // 如果是File对象，直接使用name属性
        return file.name;
      } else if (file && file.url) {
        // 如果有url属性，从url提取文件名
        const parts = file.url.split('/');
        return parts[parts.length - 1];
      }
      return '';
    }).filter(name => name); // 过滤空值
  }

  /**
   * 比较两个数组是否相同（不考虑顺序）
   * @param {Array} arr1 数组1
   * @param {Array} arr2 数组2
   * @returns {Boolean} 是否相同
   */
  compareArrays(arr1, arr2) {
    // 如果两个数组都为空，视为相同
    if ((!arr1 || !arr1.length) && (!arr2 || !arr2.length)) {
      return true;
    }
    
    // 如果只有一个数组为空，视为不同
    if ((!arr1 || !arr1.length) || (!arr2 || !arr2.length)) {
      return false;
    }
    
    // 如果长度不同，视为不同
    if (arr1.length !== arr2.length) {
      return false;
    }
    
    // 排序后比较每个元素
    const sorted1 = [...arr1].sort();
    const sorted2 = [...arr2].sort();
    
    for (let i = 0; i < sorted1.length; i++) {
      if (sorted1[i] !== sorted2[i]) {
        return false;
      }
    }
    
    return true;
  }
}

// 导出单例
export default new TaskStorage(); 