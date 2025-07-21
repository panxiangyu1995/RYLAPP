import { defineStore } from 'pinia'
import { ref } from 'vue'
import { 
  getUserProfile, 
  updateUserProfile, 
  updateUserAvatar, 
  getUserVehicle, 
  getUserVehicles,
  getUserVehicleById,
  updateUserVehicle, 
  createUserVehicle,
  deleteUserVehicle
} from '../api/user'

export const useUserStore = defineStore('user', () => {
  // 状态
  const profile = ref(null)
  const vehicle = ref(null)
  const vehicles = ref([])
  const loading = ref(false)
  const error = ref(null)

  // 获取用户个人信息
  async function fetchUserProfile() {
    loading.value = true
    error.value = null
    
    try {
      const response = await getUserProfile()
      
      if (response.code === 200) {
        profile.value = response.data
        return { success: true, profile: profile.value }
      } else {
        throw new Error(response.message || '获取用户信息失败')
      }
    } catch (err) {
      error.value = err.message || '获取用户信息过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 更新用户个人信息
  async function updateProfile(profileData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await updateUserProfile(profileData)
      
      if (response.code === 200 && response.data.updated) {
        // 更新成功后重新获取用户信息
        await fetchUserProfile()
        return { success: true }
      } else {
        throw new Error(response.message || '更新用户信息失败')
      }
    } catch (err) {
      error.value = err.message || '更新用户信息过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 更新用户头像
  async function updateAvatar(formData) {
    loading.value = true
    error.value = null
    
    // 检查是否为FormData类型
    if (!(formData instanceof FormData)) {
      error.value = '参数必须是FormData类型';
      return { success: false, error: error.value };
    }
    
    // 确保FormData中包含文件
    const file = formData.get('file');
    if (!file) {
      error.value = '上传数据中缺少文件';
      return { success: false, error: error.value };
    }
    
    try {
      console.log('Store - 准备调用头像更新API');
      const response = await updateUserAvatar(formData);
      
      // 处理响应
      if (response.code === 200 && response.data && response.data.avatarUrl) {
        // 更新本地头像URL
        const avatarUrl = response.data.avatarUrl;
        if (profile.value) {
          profile.value.avatar = avatarUrl;
        }
        
        // 记录成功日志
        console.log('头像上传成功，新URL:', avatarUrl);
        
        return { success: true, avatarUrl: avatarUrl };
      } else {
        // 处理服务器返回的错误
        console.error('服务器返回错误:', response);
        throw new Error(response.message || '服务器返回未知错误');
      }
    } catch (err) {
      // 详细记录错误
      console.error('头像上传错误:', err);
      
      error.value = err.message || '更新头像过程中发生错误';
      return { success: false, error: error.value };
    } finally {
      loading.value = false;
    }
  }

  // 获取用户车辆信息
  async function fetchUserVehicle() {
    loading.value = true
    error.value = null
    
    try {
      const response = await getUserVehicle()
      
      if (response.code === 200) {
        vehicle.value = response.data
        return { success: true, vehicle: vehicle.value }
      } else {
        throw new Error(response.message || '获取车辆信息失败')
      }
    } catch (err) {
      error.value = err.message || '获取车辆信息过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }
  
  // 获取用户所有车辆信息
  async function fetchUserVehicles() {
    loading.value = true
    error.value = null
    
    try {
      const response = await getUserVehicles()
      
      if (response.code === 200) {
        vehicles.value = response.data
        return { success: true, vehicles: vehicles.value }
      } else {
        throw new Error(response.message || '获取车辆列表失败')
      }
    } catch (err) {
      error.value = err.message || '获取车辆列表过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }
  
  // 获取指定ID的车辆信息
  async function fetchVehicleById(id) {
    loading.value = true
    error.value = null
    
    try {
      const response = await getUserVehicleById(id)
      
      if (response.code === 200) {
        return { success: true, vehicle: response.data }
      } else {
        throw new Error(response.message || '获取车辆详情失败')
      }
    } catch (err) {
      error.value = err.message || '获取车辆详情过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 更新用户车辆信息
  async function updateVehicle(vehicleData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await updateUserVehicle(vehicleData)
      
      if (response.code === 200 && response.data.updated) {
        // 更新本地车辆信息
        vehicle.value = {
          ...vehicle.value,
          ...vehicleData,
          vehicleId: response.data.vehicleId
        }
        return { success: true, vehicleId: response.data.vehicleId }
      } else {
        throw new Error(response.message || '更新车辆信息失败')
      }
    } catch (err) {
      error.value = err.message || '更新车辆信息过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }
  
  // 创建用户车辆信息
  async function createVehicle(vehicleData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await createUserVehicle(vehicleData)
      
      if (response.code === 200 && response.data.created) {
        return { success: true, vehicleId: response.data.vehicleId }
      } else {
        throw new Error(response.message || '创建车辆信息失败')
      }
    } catch (err) {
      error.value = err.message || '创建车辆信息过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }
  
  // 删除用户车辆信息
  async function deleteVehicle(id) {
    loading.value = true
    error.value = null
    
    try {
      const response = await deleteUserVehicle(id)
      
      if (response.code === 200 && response.data.deleted) {
        // 如果当前选中的车辆被删除，清空vehicle
        if (vehicle.value && vehicle.value.vehicleId === id) {
          vehicle.value = null
        }
        // 从列表中移除被删除的车辆
        vehicles.value = vehicles.value.filter(v => v.vehicleId !== id)
        return { success: true }
      } else {
        throw new Error(response.message || '删除车辆信息失败')
      }
    } catch (err) {
      error.value = err.message || '删除车辆信息过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  return {
    profile,
    vehicle,
    vehicles,
    loading,
    error,
    fetchUserProfile,
    updateProfile,
    updateAvatar,
    fetchUserVehicle,
    fetchUserVehicles,
    fetchVehicleById,
    updateVehicle,
    createVehicle,
    deleteVehicle
  }
}) 