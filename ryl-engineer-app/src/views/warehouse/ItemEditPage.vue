<template>
  <div class="item-edit-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>{{ pageTitle }}</h1>
      <div class="header-right"></div>
    </header>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>

    <template v-else>
      <form @submit.prevent="submitForm" class="edit-form">
        <!-- 基本信息卡片 -->
        <div class="form-card">
          <div class="card-title">基本信息</div>
          
          <!-- 物品编号 (添加模式可编辑，编辑模式只读) -->
          <div class="form-group">
            <label for="itemCode">物品编号</label>
            <input 
              type="text" 
              id="itemCode" 
              v-model="formData.itemCode" 
              class="form-control"
              :disabled="!isAddMode"
              :readonly="!isAddMode"
            />
            <div v-if="isAddMode" class="helper-text">保存后系统将自动生成正式编号</div>
          </div>

          <!-- 物品名称 -->
          <div class="form-group">
            <label for="name">物品名称</label>
            <input 
              type="text" 
              id="name" 
              v-model="formData.name" 
              class="form-control"
              :class="{ 'is-invalid': errors.name }"
              placeholder="输入物品名称"
            />
            <div v-if="errors.name" class="error-message">{{ errors.name }}</div>
          </div>

          <!-- 物品型号 -->
          <div class="form-group">
            <label for="model">规格型号</label>
            <input 
              type="text" 
              id="model" 
              v-model="formData.model" 
              class="form-control"
              :class="{ 'is-invalid': errors.model }"
              placeholder="输入规格型号"
            />
            <div v-if="errors.model" class="error-message">{{ errors.model }}</div>
          </div>

          <!-- 厂家/品牌 -->
          <div class="form-group">
            <label for="manufacturer">{{ isCategoryParts || isCategoryConsumables ? '品牌' : '厂家' }}</label>
            <input 
              type="text" 
              id="manufacturer" 
              v-model="formData.manufacturer" 
              class="form-control"
              :class="{ 'is-invalid': errors.manufacturer }"
              placeholder="输入厂家或品牌名称"
            />
            <div v-if="errors.manufacturer" class="error-message">{{ errors.manufacturer }}</div>
          </div>

          <!-- 物品类别 (添加模式可选，编辑模式不可编辑) -->
          <div class="form-group">
            <label for="categoryId">物品类别</label>
            <select 
              v-if="isAddMode"
              id="categoryId" 
              v-model="formData.categoryId" 
              class="form-control"
              @change="onCategoryChange"
            >
              <option value="1">仪器库</option>
              <option value="2">配件库</option>
              <option value="3">耗材库</option>
              <option value="4">固定资产库</option>
            </select>
            <input 
              v-else
              type="text" 
              id="categoryName" 
              v-model="categoryName" 
              class="form-control"
              disabled
              readonly
            />
          </div>

          <!-- 数量 (编辑模式不可编辑) -->
          <div class="form-group">
            <label for="quantity">{{ isAddMode ? '初始库存' : '当前库存' }}</label>
            <input 
              type="number" 
              id="quantity" 
              v-model="formData.quantity" 
              class="form-control"
              :disabled="!isAddMode"
              :readonly="!isAddMode"
              min="0"
            />
            <div class="helper-text">
              {{ isAddMode ? '设置物品初始库存数量' : '库存数量通过出入库操作修改' }}
            </div>
          </div>
          
          <!-- 成本 (添加模式可编辑，编辑模式只读) -->
          <div class="form-group">
            <label for="cost">成本价格</label>
            <input 
              v-if="isAddMode"
              type="number" 
              id="cost" 
              v-model="formData.cost" 
              class="form-control"
              min="0"
              step="0.01"
            />
            <input 
              v-else
              type="text" 
              id="cost" 
              v-model="formattedCost" 
              class="form-control"
              disabled
              readonly
            />
          </div>

          <!-- 到货日期 (添加模式可编辑，编辑模式只读) -->
          <div class="form-group">
            <label for="arrivalDate">到货日期</label>
            <input 
              type="date" 
              id="arrivalDate" 
              v-model="formData.arrivalDate" 
              class="form-control"
              :disabled="!isAddMode"
              :readonly="!isAddMode"
            />
          </div>

          <!-- 区域 -->
          <div class="form-group">
            <label for="area">区域</label>
            <input 
              type="text" 
              id="area" 
              v-model="formData.area" 
              class="form-control"
              :class="{ 'is-invalid': errors.area }"
              placeholder="输入存放区域"
            />
            <div v-if="errors.area" class="error-message">{{ errors.area }}</div>
          </div>

          <!-- 状态 -->
          <div class="form-group">
            <label>状态</label>
            <div class="radio-group">
              <label class="radio-label">
                <input type="radio" v-model="formData.status" :value="1" /> 
                <span>启用</span>
              </label>
              <label class="radio-label">
                <input type="radio" v-model="formData.status" :value="0" /> 
                <span>禁用</span>
              </label>
            </div>
          </div>
        </div>

        <!-- 仪器特有信息 -->
        <div v-if="isCategoryInstruments" class="form-card">
          <div class="card-title">仪器信息</div>
          
          <!-- 是否全新 -->
          <div class="form-group">
            <label>是否全新</label>
            <div class="radio-group">
              <label class="radio-label">
                <input 
                  type="radio" 
                  v-model="formData.details.isNew" 
                  :value="1" 
                /> 
                <span>是</span>
              </label>
              <label class="radio-label">
                <input 
                  type="radio" 
                  v-model="formData.details.isNew" 
                  :value="0" 
                /> 
                <span>否</span>
              </label>
            </div>
            <div v-if="errors.isNew" class="error-message">{{ errors.isNew }}</div>
          </div>
          
          <!-- 生产日期 -->
          <div class="form-group">
            <label for="productionDate">生产日期</label>
            <input 
              type="date" 
              id="productionDate" 
              v-model="formData.details.productionDate" 
              class="form-control"
              :class="{ 'is-invalid': errors.productionDate }"
            />
            <div v-if="errors.productionDate" class="error-message">{{ errors.productionDate }}</div>
          </div>
          
          <!-- 回收单位 (二手仪器) -->
          <div v-if="formData.details.isNew === 0" class="form-group">
            <label for="recoveryUnit">回收/购买单位</label>
            <input 
              type="text" 
              id="recoveryUnit" 
              v-model="formData.details.recoveryUnit" 
              class="form-control"
              placeholder="输入回收或购买单位名称"
            />
          </div>
        </div>

        <!-- 配件特有信息 -->
        <div v-if="isCategoryParts" class="form-card">
          <div class="card-title">配件信息</div>
          
          <!-- 货号 -->
          <div class="form-group">
            <label for="partNumber">货号</label>
            <input 
              type="text" 
              id="partNumber" 
              v-model="formData.details.partNumber" 
              class="form-control"
              :class="{ 'is-invalid': errors.partNumber }"
              placeholder="输入货号（选填）"
            />
            <div v-if="errors.partNumber" class="error-message">{{ errors.partNumber }}</div>
          </div>
        </div>

        <!-- 耗材特有信息 -->
        <div v-if="isCategoryConsumables" class="form-card">
          <div class="card-title">耗材信息</div>
          
          <!-- 货号 -->
          <div class="form-group">
            <label for="partNumber">货号</label>
            <input 
              type="text" 
              id="partNumber" 
              v-model="formData.details.partNumber" 
              class="form-control"
              :class="{ 'is-invalid': errors.partNumber }"
              placeholder="输入货号（选填）"
            />
            <div v-if="errors.partNumber" class="error-message">{{ errors.partNumber }}</div>
          </div>
        </div>

        <!-- 物品描述 -->
        <div class="form-card">
          <div class="card-title">其他信息</div>
          
          <div class="form-group">
            <label for="description">{{ isCategoryInstruments || isCategoryAssets ? '设备描述/备注' : '描述/备注' }}</label>
            <textarea 
              id="description" 
              v-model="formData.description" 
              class="form-control"
              placeholder="输入描述或备注信息"
              rows="4"
            ></textarea>
          </div>
        </div>

        <!-- 表单操作按钮 -->
        <div class="form-actions">
          <div class="button-group">
            <button type="button" class="btn-cancel" @click="goBack">取消</button>
            <button type="submit" class="btn-submit" :disabled="isSubmitting">
              {{ isSubmitting ? (isAddMode ? '添加中...' : '保存中...') : (isAddMode ? '添加' : '保存') }}
            </button>
          </div>
          <button v-if="!isAddMode" type="button" class="btn-delete" @click="openDeleteModal">删除物品</button>
        </div>
      </form>
      
      <!-- 删除确认对话框 - 仅编辑模式显示 -->
      <div v-if="!isAddMode && showDeleteModal" class="modal-overlay">
        <div class="modal-container">
          <div class="modal-header">
            <h3>确认删除</h3>
            <button class="close-button" @click="closeDeleteModal">&times;</button>
          </div>
          <div class="modal-body">
            <p>您确定要删除此物品吗？此操作不可撤销。</p>
            <div class="form-group">
              <label for="deletePassword">请输入删除密码以确认</label>
              <input 
                type="password" 
                id="deletePassword" 
                v-model="deletePassword" 
                class="form-control"
                placeholder="输入删除密码"
              />
              <div v-if="deleteError" class="error-message">{{ deleteError }}</div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="closeDeleteModal">取消</button>
            <button 
              class="btn-confirm-delete" 
              @click="confirmDelete"
              :disabled="isDeleting"
            >
              {{ isDeleting ? '删除中...' : '确认删除' }}
            </button>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { getItemDetail, updateItem, deleteItem, addItem } from '../../api/warehouse'

export default {
  name: 'ItemEditPage',
  props: {
    isAddMode: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const router = useRouter()
    const route = useRoute()
    const authStore = useAuthStore()
    
    const warehouseId = computed(() => route.params.warehouseId)
    const itemId = computed(() => route.params.itemId)
    const loading = ref(true)
    const isSubmitting = ref(false)
    
    // 删除功能相关状态
    const showDeleteModal = ref(false)
    const deletePassword = ref('')
    const isDeleting = ref(false)
    const deleteError = ref('')
    
    // 页面标题
    const pageTitle = computed(() => props.isAddMode ? '新增物品' : '编辑物品')
    
    // 表单数据
    const formData = reactive({
      name: '',              // 物品名称
      model: '',             // 规格型号
      manufacturer: '',      // 厂家/品牌
      categoryId: 2,         // 分类ID (默认为配件库)
      categoryName: '',      // 分类名称
      itemCode: '',          // 物品编号
      quantity: 0,           // 数量
      cost: 0,               // 成本
      arrivalDate: '',       // 到货日期
      area: '',              // 区域
      description: '',       // 描述/备注
      status: 1,             // 状态（0-禁用，1-启用）
      warehouseId: parseInt(warehouseId.value),  // 仓库ID
      details: {             // 详细信息（根据分类不同而不同）
        isNew: 1
      }
    })
    
    // 错误信息
    const errors = reactive({
      name: '',
      model: '',
      manufacturer: '',
      categoryId: '',
      area: '',
      isNew: '',
      productionDate: '',
      partNumber: ''
    })
    
    // 是否有编辑权限（系统管理员或仓库管理员）
    const hasEditPermission = computed(() => 
      authStore.hasRole('ROLE_ADMIN') || authStore.hasRole('ROLE_WAREHOUSE')
    )
    
    // 分类特定的计算属性
    const isCategoryInstruments = computed(() => parseInt(formData.categoryId) === 1)
    const isCategoryParts = computed(() => parseInt(formData.categoryId) === 2)
    const isCategoryConsumables = computed(() => parseInt(formData.categoryId) === 3)
    const isCategoryAssets = computed(() => parseInt(formData.categoryId) === 4)
    
    // 分类名称
    const categoryName = computed(() => {
      const categoryMap = {
        1: '仪器库',
        2: '配件库',
        3: '耗材库',
        4: '固定资产库'
      }
      return categoryMap[formData.categoryId] || '未知分类'
    })
    
    // 格式化成本显示
    const formattedCost = computed(() => {
      if (formData.cost) {
        return `¥${formData.cost.toFixed(2)}`
      }
      return ''
    })
    
    // 返回上一页
    const goBack = () => {
      router.back()
    }
    
    // 类别变更处理
    const onCategoryChange = () => {
      // 根据不同类别初始化详细信息
      if (isCategoryInstruments.value) {
        formData.details = { 
          isNew: 1, 
          productionDate: new Date().toISOString().split('T')[0] 
        }
      } else if (isCategoryParts.value || isCategoryConsumables.value) {
        formData.details = { partNumber: '' }
      } else {
        formData.details = {}
      }
    }
    
    // 验证表单 - 简化验证
    const validateForm = () => {
      let isValid = true
      
      // 重置错误信息
      Object.keys(errors).forEach(key => {
        errors[key] = ''
      })
      
      // 验证名称
      if (!formData.name.trim()) {
        errors.name = '请输入物品名称'
        isValid = false
      }
      
      // 验证日期格式等基本格式
      if (formData.details.productionDate && !/^\d{4}-\d{2}-\d{2}$/.test(formData.details.productionDate)) {
        errors.productionDate = '生产日期格式不正确，应为YYYY-MM-DD'
        isValid = false
      }
      
      return isValid
    }
    
    // 提交表单
    const submitForm = async () => {
      if (!validateForm()) {
        return
      }
      
      isSubmitting.value = true
      
      try {
        if (props.isAddMode) {
          // 添加物品模式
          const addData = {
            name: formData.name,
            model: formData.model || '',
            manufacturer: formData.manufacturer || '',
            categoryId: parseInt(formData.categoryId),
            quantity: parseInt(formData.quantity),
            cost: parseFloat(formData.cost || 0),
            area: formData.area || '',
            description: formData.description || '',
            status: formData.status,
            warehouseId: parseInt(warehouseId.value),
            details: formData.details,
            arrivalDate: formData.arrivalDate || new Date().toISOString().split('T')[0]
          }
          
          // 调用添加物品API
          const response = await addItem(addData)
          
          if (response.code === 200) {
            // 添加成功，返回仓库详情页
            alert('添加成功')
            router.replace(`/warehouse/${warehouseId.value}`)
          } else {
            alert(`添加失败: ${response.message || '未知错误'}`)
          }
        } else {
          // 编辑物品模式
          const updateData = {
            name: formData.name,
            model: formData.model || '',
            manufacturer: formData.manufacturer || '',
            area: formData.area || '',
            description: formData.description || '',
            status: formData.status,
            details: formData.details
          }
          
          const response = await updateItem(itemId.value, updateData)
          
          if (response.code === 200) {
            // 更新成功，返回物品详情页
            router.replace(`/warehouse/${warehouseId.value}/item/${itemId.value}`)
          } else {
            alert(`更新失败: ${response.message || '未知错误'}`)
          }
        }
      } catch (error) {
        console.error(props.isAddMode ? '添加物品错误:' : '更新物品错误:', error)
        alert(`${props.isAddMode ? '添加' : '更新'}失败: ${error.message || '未知错误'}`)
      } finally {
        isSubmitting.value = false
      }
    }
    
    // 删除相关方法
    const openDeleteModal = () => {
      if (props.isAddMode) return // 添加模式不显示删除功能
      
      showDeleteModal.value = true
      deletePassword.value = ''
      deleteError.value = ''
    }

    const closeDeleteModal = () => {
      showDeleteModal.value = false
    }

    const confirmDelete = async () => {
      if (!deletePassword.value.trim()) {
        deleteError.value = '请输入删除密码'
        return
      }
      
      isDeleting.value = true
      
      try {
        const response = await deleteItem(itemId.value, deletePassword.value)
        
        if (response.code === 200) {
          // 删除成功，返回物品列表页
          alert('删除成功')
          router.replace(`/warehouse/${warehouseId.value}`)
        } else {
          deleteError.value = response.message || '删除失败'
        }
      } catch (error) {
        console.error('删除物品错误:', error)
        deleteError.value = error.message || '删除失败'
      } finally {
        isDeleting.value = false
      }
    }
    
    // 初始化空表单
    const initEmptyForm = () => {
      formData.name = ''
      formData.model = ''
      formData.manufacturer = ''
      formData.categoryId = 2 // 默认选择配件库
      formData.itemCode = '待生成' // 临时编码
      formData.quantity = 0
      formData.cost = 0
      formData.area = ''
      formData.description = ''
      formData.status = 1
      formData.arrivalDate = new Date().toISOString().split('T')[0] // 当前日期
      
      // 初始化类别特定的详情
      onCategoryChange()
    }
    
    // 加载物品详情
    const loadItemDetail = async () => {
      loading.value = true
      try {
        // 检查权限
        if (!hasEditPermission.value) {
          alert('您没有编辑物品的权限')
          router.back()
          return
        }
        
        // 直接调用API加载物品详情
        const response = await getItemDetail(itemId.value)
        
        if (response.code === 200 && response.data) {
          const item = response.data
          
          // 填充表单数据
          formData.name = item.name
          formData.model = item.model || ''
          formData.manufacturer = item.manufacturer || ''
          formData.categoryId = item.categoryId
          formData.categoryName = item.categoryName
          formData.itemCode = item.itemCode
          formData.quantity = item.quantity
          formData.cost = item.cost
          formData.arrivalDate = item.arrivalDate
          formData.area = item.area || ''
          formData.description = item.description || ''
          formData.status = item.status
          formData.warehouseId = item.warehouseId
          
          // 填充分类特定的详情数据
          if (item.details) {
            formData.details = { ...item.details }
          }
        } else {
          alert(`加载物品详情失败: ${response.message || '未知错误'}`)
          router.back()
        }
      } catch (error) {
        console.error('加载物品详情错误:', error)
        alert(`加载物品详情失败: ${error.message || '未知错误'}`)
        router.back()
      } finally {
        loading.value = false
      }
    }
    
    onMounted(() => {
      // 检查权限
      if (!hasEditPermission.value) {
        alert('您没有操作物品的权限')
        router.back()
        return
      }
      
      if (props.isAddMode) {
        // 添加模式，初始化空表单
        initEmptyForm()
        loading.value = false
      } else {
        // 编辑模式，加载物品详情
        loadItemDetail()
      }
    })
    
    return {
      loading,
      isSubmitting,
      formData,
      errors,
      hasEditPermission,
      categoryName,
      formattedCost,
      isCategoryInstruments,
      isCategoryParts,
      isCategoryConsumables,
      isCategoryAssets,
      pageTitle,
      isAddMode: props.isAddMode,
      goBack,
      submitForm,
      onCategoryChange,
      
      // 删除相关
      showDeleteModal,
      deletePassword,
      isDeleting,
      deleteError,
      openDeleteModal,
      closeDeleteModal,
      confirmDelete
    }
  }
}
</script>

<style scoped>
.item-edit-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 10;
}

.header-left {
  width: 24px;
}

.icon-arrow-left::before {
  content: '\f060';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
}

.header h1 {
  flex: 1;
  text-align: center;
  font-size: 18px;
  font-weight: 600;
}

.header-right {
  width: 24px;
  text-align: right;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 0;
}

.spinner {
  border: 3px solid #f3f4f6;
  border-top: 3px solid #3b82f6;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  animation: spin 1s linear infinite;
  margin-bottom: 8px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.form-card {
  background-color: #fff;
  margin: 16px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #1f2937;
  padding-bottom: 8px;
  border-bottom: 1px solid #e5e7eb;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #374151;
}

.required {
  color: #dc2626;
  margin-left: 4px;
}

.form-control {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-control:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.form-control:disabled {
  background-color: #f3f4f6;
  color: #6b7280;
  cursor: not-allowed;
}

.form-control.is-invalid {
  border-color: #dc2626;
}

.error-message {
  margin-top: 4px;
  color: #dc2626;
  font-size: 12px;
}

.helper-text {
  margin-top: 4px;
  color: #6b7280;
  font-size: 12px;
}

.radio-group {
  display: flex;
  gap: 16px;
}

.radio-label {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.radio-label input {
  margin-right: 6px;
}

.form-actions {
  display: flex;
  flex-direction: column;
  margin: 24px 16px;
  gap: 16px;
}

.button-group {
  display: flex;
  justify-content: space-between;
}

.form-actions button {
  padding: 12px 0;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
}

.button-group button {
  flex: 1;
}

.btn-cancel {
  background-color: #f3f4f6;
  color: #4b5563;
  margin-right: 8px;
}

.btn-submit {
  background-color: #3b82f6;
  color: #fff;
  margin-left: 8px;
}

.btn-submit:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}

.btn-delete {
  background-color: #ef4444;
  color: #fff;
  width: 100%;
}

.btn-delete:hover {
  background-color: #dc2626;
}

/* 确认对话框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 100;
}

.modal-container {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.close-button {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #6b7280;
}

.modal-body {
  padding: 16px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 16px;
  border-top: 1px solid #e5e7eb;
}

.btn-confirm-delete {
  background-color: #ef4444;
  color: #fff;
  padding: 8px 16px;
  border-radius: 6px;
  border: none;
}

.btn-confirm-delete:disabled {
  background-color: #fca5a5;
  cursor: not-allowed;
}
</style> 