<template>
  <div class="item-request-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>物品申请</h1>
      <div class="header-right"></div>
    </header>

    <!-- 申请类型选择 -->
    <div class="request-type-tabs">
      <button 
        :class="['type-tab', { active: requestType === 'use' }]"
        @click="requestType = 'use'"
      >
        使用申请
      </button>
      <button 
        :class="['type-tab', { active: requestType === 'purchase' }]"
        @click="requestType = 'purchase'"
      >
        采购申请
      </button>
    </div>

    <!-- 使用申请表单 -->
    <div v-if="requestType === 'use'" class="form-container">
      <div class="form-group">
        <label class="form-label">物品类别</label>
        <div class="category-selector">
          <button 
            v-for="(category, index) in categories"
            :key="index"
            :class="['category-btn', { active: selectedCategory === category.value }]"
            @click="selectedCategory = category.value"
          >
            <i :class="['category-icon', category.icon]"></i>
            <span>{{ category.label }}</span>
          </button>
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">物品信息</label>
        <div class="item-selector">
          <div class="selector-header" @click="toggleItemSelector">
            <span v-if="selectedItem">{{ selectedItem.name }} ({{ selectedItem.model }})</span>
            <span v-else>选择物品</span>
            <i class="icon-chevron-down"></i>
          </div>
          <div v-if="showItemSelector" class="selector-dropdown">
            <div class="search-box">
              <i class="icon-search"></i>
              <input 
                type="text" 
                v-model="itemSearchQuery" 
                placeholder="搜索物品名称或型号"
                class="search-input"
              >
            </div>
            <div class="item-list">
              <div 
                v-for="item in filteredItems" 
                :key="item.id"
                class="item-option"
                @click="selectItem(item)"
              >
                <div class="item-info">
                  <h3 class="item-name">{{ item.name }}</h3>
                  <p class="item-model">{{ item.model }}</p>
                </div>
                <div class="item-stock">
                  <span :class="{ 'stock-warning': item.quantity <= 5, 'stock-danger': item.quantity <= 0 }">
                    库存: {{ item.quantity }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">申请数量</label>
        <div class="quantity-input">
          <button class="quantity-btn" @click="decreaseQuantity">-</button>
          <input 
            type="number" 
            v-model="quantity" 
            class="quantity-field"
            min="1"
            :max="selectedItem ? selectedItem.quantity : 999"
          >
          <button class="quantity-btn" @click="increaseQuantity">+</button>
        </div>
        <div v-if="selectedItem && quantity > selectedItem.quantity" class="error-message">
          申请数量不能超过库存数量
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">关联任务</label>
        <div class="task-selector">
          <div class="selector-header" @click="toggleTaskSelector">
            <span v-if="selectedTask">{{ selectedTask.title }}</span>
            <span v-else>选择关联任务（选填）</span>
            <i class="icon-chevron-down"></i>
          </div>
          <div v-if="showTaskSelector" class="selector-dropdown">
            <div class="search-box">
              <i class="icon-search"></i>
              <input 
                type="text" 
                v-model="taskSearchQuery" 
                placeholder="搜索任务名称或编号"
                class="search-input"
              >
            </div>
            <div class="task-list">
              <div 
                v-for="task in filteredTasks" 
                :key="task.id"
                class="task-option"
                @click="selectTask(task)"
              >
                <div class="task-info">
                  <h3 class="task-title">{{ task.title }}</h3>
                  <p class="task-code">{{ task.code }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">紧急程度</label>
        <div class="urgency-selector">
          <button 
            v-for="(level, index) in urgencyLevels"
            :key="index"
            :class="['urgency-btn', { active: urgency === level.value }]"
            @click="urgency = level.value"
          >
            {{ level.label }}
          </button>
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">申请用途</label>
        <textarea 
          v-model="purpose" 
          placeholder="请输入申请用途"
          class="form-textarea"
        ></textarea>
      </div>
    </div>

    <!-- 采购申请表单 -->
    <div v-else class="form-container">
      <div class="form-group">
        <label class="form-label">物品类别</label>
        <div class="category-selector">
          <button 
            v-for="(category, index) in categories"
            :key="index"
            :class="['category-btn', { active: selectedCategory === category.value }]"
            @click="selectedCategory = category.value"
          >
            <i :class="['category-icon', category.icon]"></i>
            <span>{{ category.label }}</span>
          </button>
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">物品名称</label>
        <input 
          type="text" 
          v-model="itemName" 
          placeholder="请输入物品名称"
          class="form-input"
        >
      </div>

      <div class="form-group">
        <label class="form-label">规格型号</label>
        <input 
          type="text" 
          v-model="itemModel" 
          placeholder="请输入规格型号"
          class="form-input"
        >
      </div>

      <div class="form-group">
        <label class="form-label">申请数量</label>
        <div class="quantity-input">
          <button class="quantity-btn" @click="decreaseQuantity">-</button>
          <input 
            type="number" 
            v-model="quantity" 
            class="quantity-field"
            min="1"
          >
          <button class="quantity-btn" @click="increaseQuantity">+</button>
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">关联任务</label>
        <div class="task-selector">
          <div class="selector-header" @click="toggleTaskSelector">
            <span v-if="selectedTask">{{ selectedTask.title }}</span>
            <span v-else>选择关联任务（选填）</span>
            <i class="icon-chevron-down"></i>
          </div>
          <div v-if="showTaskSelector" class="selector-dropdown">
            <div class="search-box">
              <i class="icon-search"></i>
              <input 
                type="text" 
                v-model="taskSearchQuery" 
                placeholder="搜索任务名称或编号"
                class="search-input"
              >
            </div>
            <div class="task-list">
              <div 
                v-for="task in filteredTasks" 
                :key="task.id"
                class="task-option"
                @click="selectTask(task)"
              >
                <div class="task-info">
                  <h3 class="task-title">{{ task.title }}</h3>
                  <p class="task-code">{{ task.code }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">紧急程度</label>
        <div class="urgency-selector">
          <button 
            v-for="(level, index) in urgencyLevels"
            :key="index"
            :class="['urgency-btn', { active: urgency === level.value }]"
            @click="urgency = level.value"
          >
            {{ level.label }}
          </button>
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">申请用途</label>
        <textarea 
          v-model="purpose" 
          placeholder="请输入申请用途"
          class="form-textarea"
        ></textarea>
      </div>
    </div>

    <!-- 底部按钮 -->
    <div class="bottom-actions">
      <button class="btn-cancel" @click="goBack">取消</button>
      <button class="btn-confirm" @click="submitRequest">提交申请</button>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useWarehouseStore } from '../../stores/warehouse'
import { useAuthStore } from '../../stores/auth'

export default {
  name: 'ItemRequestPage',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const warehouseStore = useWarehouseStore()
    const authStore = useAuthStore()
    
    const warehouseId = computed(() => route.params.warehouseId)
    const requestType = ref('use') // 'use' 或 'purchase'
    const selectedCategory = ref('parts')
    const selectedItem = ref(null)
    const showItemSelector = ref(false)
    const itemSearchQuery = ref('')
    const quantity = ref(1)
    const itemName = ref('')
    const itemModel = ref('')
    const selectedTask = ref(null)
    const showTaskSelector = ref(false)
    const taskSearchQuery = ref('')
    const urgency = ref(1) // 1-普通，2-紧急，3-特急
    const purpose = ref('')
    
    // 物品类别
    const categories = computed(() => warehouseStore.itemCategories)
    
    // 紧急程度选项
    const urgencyLevels = [
      { value: 1, label: '普通' },
      { value: 2, label: '紧急' },
      { value: 3, label: '特急' }
    ]
    
    // 获取当前仓库的物品列表
    const loadWarehouseItems = async () => {
      if (!warehouseId.value) return
      
      try {
        const params = {
          categoryId: warehouseStore.getCategoryIdByValue(selectedCategory.value)
        }
        await warehouseStore.fetchWarehouseItems(warehouseId.value, params)
      } catch (error) {
        console.error('加载物品列表失败:', error)
      }
    }
    
    // 获取任务列表
    const tasks = ref([])
    const loadTasks = async () => {
      // 实际项目中应从任务API获取任务列表
      // 这里使用模拟数据
      tasks.value = [
        {
          id: '1',
          code: 'RP-2024-001',
          title: '气相色谱仪维修'
        },
        {
          id: '2',
          code: 'MT-2024-001',
          title: '液相色谱仪保养'
        },
        {
          id: '3',
          code: 'RP-2024-002',
          title: '质谱仪维修'
        }
      ]
    }
    
    // 筛选物品
    const filteredItems = computed(() => {
      let result = warehouseStore.warehouseItems
      
      if (itemSearchQuery.value) {
        const query = itemSearchQuery.value.toLowerCase()
        result = result.filter(item => 
          item.name.toLowerCase().includes(query) || 
          item.model.toLowerCase().includes(query)
        )
      }
      
      return result
    })
    
    // 筛选任务
    const filteredTasks = computed(() => {
      let result = tasks.value
      
      if (taskSearchQuery.value) {
        const query = taskSearchQuery.value.toLowerCase()
        result = result.filter(task => 
          task.title.toLowerCase().includes(query) || 
          task.code.toLowerCase().includes(query)
        )
      }
      
      return result
    })
    
    // 显示/隐藏物品选择器
    const toggleItemSelector = () => {
      showItemSelector.value = !showItemSelector.value
      if (showItemSelector.value) {
        showTaskSelector.value = false
      }
    }
    
    // 显示/隐藏任务选择器
    const toggleTaskSelector = () => {
      showTaskSelector.value = !showTaskSelector.value
      if (showTaskSelector.value) {
        showItemSelector.value = false
      }
    }
    
    // 选择物品
    const selectItem = (item) => {
      selectedItem.value = item
      showItemSelector.value = false
      if (quantity.value > item.quantity) {
        quantity.value = item.quantity
      }
    }
    
    // 选择任务
    const selectTask = (task) => {
      selectedTask.value = task
      showTaskSelector.value = false
    }
    
    // 增加数量
    const increaseQuantity = () => {
      if (requestType.value === 'use' && selectedItem.value && quantity.value >= selectedItem.value.quantity) {
        return
      }
      quantity.value++
    }
    
    // 减少数量
    const decreaseQuantity = () => {
      if (quantity.value > 1) {
        quantity.value--
      }
    }
    
    // 返回上一页
    const goBack = () => {
      router.back()
    }
    
    // 提交申请
    const submitRequest = async () => {
      if (requestType.value === 'use') {
        // 使用申请验证
        if (!selectedItem.value) {
          alert('请选择物品')
          return
        }
        
        if (quantity.value <= 0) {
          alert('请输入有效的申请数量')
          return
        }
        
        if (quantity.value > selectedItem.value.quantity) {
          alert('申请数量不能超过库存数量')
          return
        }
        
        if (!purpose.value) {
          alert('请输入申请用途')
          return
        }
        
        try {
          const requestData = {
            itemId: selectedItem.value.id,
            quantity: quantity.value,
            taskId: selectedTask.value ? selectedTask.value.id : undefined,
            purpose: purpose.value,
            urgency: urgency.value
          }
          
          const result = await warehouseStore.submitItemUseRequest(requestData)
          
          if (result.success) {
            alert('申请提交成功')
            router.back()
          } else {
            alert(`申请提交失败: ${result.error}`)
          }
        } catch (error) {
          console.error('提交使用申请错误:', error)
          alert('申请提交过程中发生错误，请稍后重试')
        }
      } else {
        // 采购申请验证
        if (!itemName.value) {
          alert('请输入物品名称')
          return
        }
        
        if (!itemModel.value) {
          alert('请输入规格型号')
          return
        }
        
        if (quantity.value <= 0) {
          alert('请输入有效的申请数量')
          return
        }
        
        if (!purpose.value) {
          alert('请输入申请用途')
          return
        }
        
        try {
          const requestData = {
            itemName: itemName.value,
            itemModel: itemModel.value,
            quantity: quantity.value,
            taskId: selectedTask.value ? selectedTask.value.id : undefined,
            purpose: purpose.value,
            urgency: urgency.value
          }
          
          const result = await warehouseStore.submitItemPurchaseRequest(requestData)
          
          if (result.success) {
            alert('申请提交成功')
            router.back()
          } else {
            alert(`申请提交失败: ${result.error}`)
          }
        } catch (error) {
          console.error('提交采购申请错误:', error)
          alert('申请提交过程中发生错误，请稍后重试')
        }
      }
    }
    
    // 当选择的分类变化时重新加载物品列表
    watch(selectedCategory, () => {
      loadWarehouseItems()
      selectedItem.value = null
    })
    
    onMounted(() => {
      loadWarehouseItems()
      loadTasks()
    })
    
    return {
      requestType,
      selectedCategory,
      categories,
      selectedItem,
      showItemSelector,
      itemSearchQuery,
      filteredItems,
      quantity,
      itemName,
      itemModel,
      selectedTask,
      showTaskSelector,
      taskSearchQuery,
      filteredTasks,
      urgency,
      urgencyLevels,
      purpose,
      toggleItemSelector,
      toggleTaskSelector,
      selectItem,
      selectTask,
      increaseQuantity,
      decreaseQuantity,
      goBack,
      submitRequest
    }
  }
}
</script>

<style scoped>
.item-request-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
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
}

.request-type-tabs {
  display: flex;
  background-color: #fff;
  border-bottom: 1px solid #f3f4f6;
  margin-bottom: 12px;
}

.type-tab {
  flex: 1;
  padding: 12px 0;
  text-align: center;
  font-size: 15px;
  font-weight: 500;
  color: #6b7280;
  background: none;
  border: none;
  position: relative;
}

.type-tab.active {
  color: #3b82f6;
}

.type-tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 25%;
  width: 50%;
  height: 2px;
  background-color: #3b82f6;
}

.form-container {
  background-color: #fff;
  border-radius: 8px;
  margin: 12px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 16px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #374151;
}

.category-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.category-btn {
  flex: 1;
  min-width: calc(25% - 8px);
  background-color: #f3f4f6;
  border: none;
  border-radius: 6px;
  padding: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  color: #6b7280;
  font-size: 12px;
}

.category-btn.active {
  background-color: #e0f2fe;
  color: #3b82f6;
}

.category-icon {
  font-size: 16px;
}

.icon-tools::before {
  content: '\f7d9';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-microscope::before {
  content: '\f610';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-vial::before {
  content: '\f492';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-desktop::before {
  content: '\f108';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.item-selector, .task-selector {
  position: relative;
}

.selector-header {
  border: 1px solid #d1d5db;
  border-radius: 6px;
  padding: 10px 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  cursor: pointer;
}

.icon-chevron-down::before {
  content: '\f078';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #9ca3af;
}

.selector-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background-color: #fff;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  margin-top: 4px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  z-index: 10;
  max-height: 250px;
  overflow-y: auto;
}

.search-box {
  padding: 8px 12px;
  border-bottom: 1px solid #f3f4f6;
  display: flex;
  align-items: center;
}

.icon-search::before {
  content: '\f002';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #9ca3af;
}

.search-input {
  flex: 1;
  border: none;
  padding: 8px;
  font-size: 14px;
  outline: none;
}

.item-list, .task-list {
  padding: 8px;
}

.item-option, .task-option {
  padding: 10px;
  border-radius: 6px;
  cursor: pointer;
}

.item-option:hover, .task-option:hover {
  background-color: #f3f4f6;
}

.item-info, .task-info {
  margin-bottom: 4px;
}

.item-name, .task-title {
  margin: 0;
  font-size: 14px;
  font-weight: 500;
}

.item-model, .task-code {
  margin: 4px 0 0 0;
  font-size: 12px;
  color: #6b7280;
}

.item-stock {
  font-size: 12px;
  color: #6b7280;
}

.stock-warning {
  color: #d97706;
}

.stock-danger {
  color: #dc2626;
}

.quantity-input {
  display: flex;
  align-items: center;
  max-width: 200px;
}

.quantity-btn {
  width: 36px;
  height: 36px;
  border: 1px solid #d1d5db;
  background-color: #f9fafb;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  cursor: pointer;
}

.quantity-btn:first-child {
  border-radius: 6px 0 0 6px;
}

.quantity-btn:last-child {
  border-radius: 0 6px 6px 0;
}

.quantity-field {
  flex: 1;
  height: 36px;
  border: 1px solid #d1d5db;
  border-left: none;
  border-right: none;
  text-align: center;
  font-size: 14px;
}

.error-message {
  color: #dc2626;
  font-size: 12px;
  margin-top: 4px;
}

.urgency-selector {
  display: flex;
}

.urgency-btn {
  flex: 1;
  padding: 10px 0;
  background-color: #f3f4f6;
  border: none;
  font-size: 14px;
  color: #6b7280;
  position: relative;
}

.urgency-btn:first-child {
  border-radius: 6px 0 0 6px;
}

.urgency-btn:last-child {
  border-radius: 0 6px 6px 0;
}

.urgency-btn.active {
  background-color: #3b82f6;
  color: #fff;
}

.form-input {
  width: 100%;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  padding: 10px 12px;
  font-size: 14px;
}

.form-textarea {
  width: 100%;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  padding: 10px 12px;
  font-size: 14px;
  min-height: 80px;
  resize: vertical;
}

.bottom-actions {
  position: fixed;
  bottom: 56px; /* 底部导航高度 */
  left: 0;
  right: 0;
  padding: 12px 16px;
  background-color: #fff;
  box-shadow: 0 -1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 16px;
  z-index: 10;
}

.btn-cancel {
  flex: 1;
  padding: 12px 0;
  border: 1px solid #d1d5db;
  background-color: #fff;
  border-radius: 8px;
  font-weight: 500;
  color: #4b5563;
}

.btn-confirm {
  flex: 2;
  padding: 12px 0;
  border: none;
  background-color: #3b82f6;
  border-radius: 8px;
  font-weight: 500;
  color: #fff;
}
</style> 