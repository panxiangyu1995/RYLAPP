import http from './http'

/**
 * 获取用户个人信息
 * @returns {Promise} 返回用户信息
 */
export function getUserProfile() {
  return http.get('/api/v1/user/profile')
}

/**
 * 更新用户个人信息
 * @param {Object} data 用户信息数据
 * @param {string} data.name 姓名
 * @param {string} data.mobile 手机号
 * @param {string} data.department 所属部门
 * @returns {Promise} 返回更新结果
 */
export function updateUserProfile(data) {
  return http.put('/api/v1/user/profile', {
    name: data.name,
    mobile: data.mobile,
    department: data.department
  })
}

/**
 * 更新用户头像
 * @param {FormData} formData 包含头像文件的表单数据
 * @returns {Promise} 返回更新结果
 */
export function updateUserAvatar(formData) {
  console.log('API - 准备上传头像')
  if (!(formData instanceof FormData)) {
    console.error('updateUserAvatar: 参数不是FormData类型')
    return Promise.reject(new Error('参数必须是FormData类型'))
  }
  
  const avatarFile = formData.get('avatar')
  if (!avatarFile) {
    // 尝试使用后端接口期望的参数名'file'
    const fileData = formData.get('file')
    if (!fileData) {
      console.error('updateUserAvatar: FormData中缺少avatar/file字段')
      return Promise.reject(new Error('表单数据中缺少头像文件'))
    }
    
    // 清除旧数据并使用正确的参数名
    formData.delete('avatar')
    formData.append('file', fileData)
  } else {
    // 重命名字段以匹配后端接口期望的参数名
    formData.delete('avatar')
    formData.append('file', avatarFile)
  }
  
  const fileToUpload = formData.get('file')
  console.log('准备上传头像文件:', fileToUpload.name, fileToUpload.type, fileToUpload.size)
  
  return http.post('/api/v1/user/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: progressEvent => {
      const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
      console.log(`上传进度: ${percentCompleted}%`);
    }
  }).catch(error => {
    console.error('头像上传请求失败:', error);
    throw error;
  })
}

/**
 * 获取用户车辆信息
 * @returns {Promise} 返回车辆信息
 */
export function getUserVehicle() {
  return http.get('/api/v1/user/vehicle')
}

/**
 * 获取用户所有车辆信息列表
 * @returns {Promise} 返回车辆信息列表
 */
export function getUserVehicles() {
  return http.get('/api/v1/user/vehicle/list')
}

/**
 * 获取指定ID的车辆信息
 * @param {number} id 车辆ID
 * @returns {Promise} 返回车辆信息
 */
export function getUserVehicleById(id) {
  return http.get(`/api/v1/user/vehicle/${id}`)
}

/**
 * 更新用户车辆信息
 * @param {Object} data 车辆信息数据
 * @param {string} data.plateNumber 车牌号
 * @param {string} data.vehicleType 车辆类型
 * @returns {Promise} 返回更新结果
 */
export function updateUserVehicle(data) {
  return http.put('/api/v1/user/vehicle', {
    plateNumber: data.plateNumber,
    vehicleType: data.vehicleType
  })
}

/**
 * 创建用户车辆信息
 * @param {Object} data 车辆信息数据
 * @param {string} data.plateNumber 车牌号
 * @param {string} data.vehicleType 车辆类型
 * @returns {Promise} 返回创建结果
 */
export function createUserVehicle(data) {
  return http.post('/api/v1/user/vehicle', {
    plateNumber: data.plateNumber,
    vehicleType: data.vehicleType
  })
}

/**
 * 删除用户车辆信息
 * @param {number} id 车辆ID
 * @returns {Promise} 返回删除结果
 */
export function deleteUserVehicle(id) {
  return http.delete(`/api/v1/user/vehicle/${id}`)
}

/**
 * 获取销售人员列表
 * @param {Object} params 查询参数
 * @returns {Promise<Object>} 销售人员列表
 */
export function getSalesList(params) {
  return http.get('/api/v1/user/sales', { params })
} 