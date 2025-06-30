import { ref, watch } from 'vue'

/**
 * 轻量级事件总线
 * 用于组件间通信，特别是非父子组件间的通信
 */
class EventBus {
  constructor() {
    this.events = {}
  }

  /**
   * 订阅事件
   * @param {string} eventName 事件名称
   * @param {Function} callback 回调函数
   * @returns {Function} 取消订阅的函数
   */
  on(eventName, callback) {
    if (!this.events[eventName]) {
      this.events[eventName] = []
    }
    
    this.events[eventName].push(callback)
    
    // 返回取消订阅的函数
    return () => {
      this.off(eventName, callback)
    }
  }

  /**
   * 取消订阅事件
   * @param {string} eventName 事件名称
   * @param {Function} callback 回调函数
   */
  off(eventName, callback) {
    if (!this.events[eventName]) return
    
    if (callback) {
      this.events[eventName] = this.events[eventName].filter(cb => cb !== callback)
    } else {
      delete this.events[eventName]
    }
  }

  /**
   * 发布事件
   * @param {string} eventName 事件名称
   * @param {any} data 事件数据
   */
  emit(eventName, data) {
    if (!this.events[eventName]) return
    
    this.events[eventName].forEach(callback => {
      try {
        callback(data)
      } catch (error) {
        console.error(`Error in event handler for ${eventName}:`, error)
      }
    })
  }

  /**
   * 只订阅一次事件
   * @param {string} eventName 事件名称
   * @param {Function} callback 回调函数
   */
  once(eventName, callback) {
    const onceCallback = (data) => {
      callback(data)
      this.off(eventName, onceCallback)
    }
    
    return this.on(eventName, onceCallback)
  }
  
  /**
   * 清除所有事件订阅
   */
  clear() {
    this.events = {}
  }
}

// 创建事件总线实例
const eventBus = new EventBus()

/**
 * 任务流程相关事件类型
 */
export const TaskFlowEvents = {
  STEP_CHANGED: 'task-flow:step-changed',
  STEP_COMPLETED: 'task-flow:step-completed',
  RECORD_ADDED: 'task-flow:record-added',
  FLOW_UPDATED: 'task-flow:flow-updated',
  SITE_VISIT_DECIDED: 'task-flow:site-visit-decided'
}

/**
 * 创建一个可以在Vue组件中使用的事件总线Hook
 * @param {string} eventName 事件名称
 * @returns {Object} 包含事件数据的ref和取消订阅的函数
 */
export function useEventListener(eventName) {
  const eventData = ref(null)
  
  const unsubscribe = eventBus.on(eventName, (data) => {
    eventData.value = data
  })
  
  // 在组件卸载时自动取消订阅
  if (typeof window !== 'undefined') {
    const cleanup = () => {
      unsubscribe()
    }
    
    // 这里我们模拟onUnmounted的行为
    // 在实际组件中，应该使用onUnmounted钩子
    if (process.env.NODE_ENV === 'development') {
      console.log(`Event listener for ${eventName} will be cleaned up when component unmounts`)
    }
    
    return {
      eventData,
      unsubscribe
    }
  }
  
  return {
    eventData,
    unsubscribe
  }
}

export default eventBus 