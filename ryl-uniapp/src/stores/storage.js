// src/stores/storage.js

/**
 * 适配 pinia-plugin-persistedstate 的自定义存储引擎
 *
 * @description 将数据存储在 uni-app 的 Storage 中
 */
export const uniStorage = {
  setItem: (key, value) => {
    try {
      uni.setStorageSync(key, JSON.parse(value));
    } catch (error) {
      uni.setStorageSync(key, value);
    }
  },
  getItem: (key) => {
    const value = uni.getStorageSync(key);
    return value ? JSON.stringify(value) : null;
  },
}; 