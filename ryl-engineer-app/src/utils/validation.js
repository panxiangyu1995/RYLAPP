// 验证工号
export function validateWorkId(workId) {
  // 测试用例：允许工号admin
  if (workId === 'admin') return ''
  
  if (!workId) return '工号不能为空'
  // 工号可以是任何非空字符串
  if (workId.length < 3) return '工号长度不能少于3个字符'
  return ''
}

// 验证密码
export function validatePassword(password) {
  // 明文密码，只验证不为空
  if (!password) return '密码不能为空'
  return ''
}

// 验证确认密码
export function validateConfirmPassword(password, confirmPassword) {
  if (!confirmPassword) return '确认密码不能为空'
  if (password !== confirmPassword) return '两次输入的密码不一致'
  return ''
}

// 验证手机号
export function validateMobile(mobile) {
  if (!mobile) return '手机号不能为空'
  if (!/^1[3-9]\d{9}$/.test(mobile)) return '请输入正确的手机号码'
  return ''
}

// 验证姓名
export function validateName(name) {
  if (!name) return '姓名不能为空'
  if (name.length < 2) return '姓名长度不能少于2个字符'
  return ''
}

// 验证部门
export function validateDepartment(department) {
  if (!department || department === '请选择部门') return '请选择部门'
  return ''
}

// 验证工作地点
export function validateLocation(location) {
  if (!location || location === '请选择工作地点') return '请选择工作地点'
  return ''
}