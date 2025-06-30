<template>
  <div class="stock-out-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>物品出库</h1>
      <div class="header-right">
        <button class="scan-btn" @click="openScanner">
          <i class="icon-qrcode"></i>
          扫码出库
        </button>
      </div>
    </header>

    <!-- 出库表单 -->
    <div class="form-container">
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
        <label class="form-label">出库数量</label>
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
          出库数量不能超过库存数量
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">出库用途</label>
        <select v-model="outPurpose" class="form-select">
          <option value="">请选择出库用途</option>
          <option value="使用出库">使用出库</option>
          <option value="调拨出库">调拨出库</option>
          <option value="报废出库">报废出库</option>
          <option value="其他出库">其他出库</option>
        </select>
      </div>

      <div class="form-group">
        <label class="form-label">关联任务</label>
        <div class="task-selector">
          <div class="selector-header" @click="toggleTaskSelector">
            <span v-if="selectedTask">{{ selectedTask.title }} ({{ selectedTask.code }})</span>
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
        <label class="form-label">备注</label>
        <textarea 
          v-model="description" 
          placeholder="请输入备注信息（选填）"
          class="form-textarea"
        ></textarea>
      </div>
    </div>

    <!-- 底部按钮 -->
    <div class="bottom-actions">
      <button class="btn-cancel" @click="goBack">取消</button>
      <button class="btn-confirm" @click="submitStockOut">确认出库</button>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { useWarehouseStore } from '../../stores/warehouse'

export default {
  name: 'StockOutPage',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const authStore = useAuthStore()
    const warehouseStore = useWarehouseStore()
    
    const warehouseId = computed(() => route.params.warehouseId)
    const itemIdFromUrl = computed(() => route.query.itemId)
    const selectedCategory = ref('parts')
    const selectedItem = ref(null)
    const showItemSelector = ref(false)
    const itemSearchQuery = ref('')
    const quantity = ref(1)
    const outPurpose = ref('')
    const selectedTask = ref(null)
    const showTaskSelector = ref(false)
    const taskSearchQuery = ref('')
    const description = ref('')
    const loading = ref(false)
    
    // 检查权限
    const isWarehouseAdmin = computed(() => authStore.hasRole('ROLE_WAREHOUSE'))
    
    // 物品类别
    const categories = computed(() => warehouseStore.itemCategories)
    
    // 获取仓库物品
    const loadWarehouseItems = async () => {
      if (!warehouseId.value) return
      
      loading.value = true
      try {
        const params = {
          categoryId: warehouseStore.getCategoryIdByValue(selectedCategory.value)
        }
        await warehouseStore.fetchWarehouseItems(warehouseId.value, params)
      } catch (error) {
        console.error('加载物品列表失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 模拟任务数据
    const tasks = ref([
      {
        id: 1,
        code: 'TASK-2023-001',
        title: '气相色谱仪安装调试',
        status: '进行中'
      },
      {
        id: 2,
        code: 'TASK-2023-002',
        title: '液相色谱仪维修',
        status: '待处理'
      },
      {
        id: 3,
        code: 'TASK-2023-003',
        title: '质谱仪年度校准',
        status: '进行中'
      }
    ])
    
    // 根据选择的类别和搜索关键词过滤物品
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
    
    // 根据URL中的物品ID自动选择物品
    const autoSelectItemFromUrl = async () => {
      if (itemIdFromUrl.value) {
        try {
          const itemDetail = await warehouseStore.fetchItemDetail(itemIdFromUrl.value)
          if (itemDetail.success && itemDetail.data) {
            selectedItem.value = itemDetail.data
            // 更新类别选择
            const categoryId = itemDetail.data.categoryId
            const categoryValue = warehouseStore.getCategoryValueById(categoryId)
            if (categoryValue) {
              selectedCategory.value = categoryValue
            }
            // 设置出库数量，确保不超过库存
            if (itemDetail.data.quantity < quantity.value) {
              quantity.value = itemDetail.data.quantity > 0 ? itemDetail.data.quantity : 1
            }
          }
        } catch (error) {
          console.error('自动选择物品失败:', error)
        }
      }
    }
    
    // 根据搜索关键词过滤任务
    const filteredTasks = computed(() => {
      if (!taskSearchQuery.value) {
        return tasks.value
      }
      
      const query = taskSearchQuery.value.toLowerCase()
      return tasks.value.filter(task => 
        task.title.toLowerCase().includes(query) || 
        task.code.toLowerCase().includes(query)
      )
    })
    
    // 返回上一页
    const goBack = () => {
      router.back()
    }
    
    // 显示/隐藏物品选择器
    const toggleItemSelector = () => {
      showItemSelector.value = !showItemSelector.value
    }
    
    // 选择物品
    const selectItem = (item) => {
      selectedItem.value = item
      showItemSelector.value = false
      
      // 如果选择的物品库存小于当前设置的数量，则调整数量
      if (item.quantity < quantity.value) {
        quantity.value = item.quantity > 0 ? item.quantity : 1
      }
    }
    
    // 显示/隐藏任务选择器
    const toggleTaskSelector = () => {
      showTaskSelector.value = !showTaskSelector.value
    }
    
    // 选择任务
    const selectTask = (task) => {
      selectedTask.value = task
      showTaskSelector.value = false
    }
    
    // 增加数量
    const increaseQuantity = () => {
      if (selectedItem.value && quantity.value < selectedItem.value.quantity) {
        quantity.value++
      } else if (!selectedItem.value) {
        quantity.value++
      }
    }
    
    // 减少数量
    const decreaseQuantity = () => {
      if (quantity.value > 1) {
        quantity.value--
      }
    }
    
    // 扫码功能
    const openScanner = () => {
      alert('扫码出库功能即将开放，敬请期待！')
      // 这里未来将实现调用原生扫码功能的逻辑
    }
    
    // 提交出库
    const submitStockOut = async () => {
      // 检查权限
      if (!isWarehouseAdmin.value) {
        alert('只有仓库管理员才能执行出库操作')
        return
      }
      
      // 表单验证
      if (!selectedItem.value) {
        alert('请选择出库物品')
        return
      }
      
      if (!quantity.value || quantity.value < 1) {
        alert('请输入有效的出库数量')
        return
      }
      
      if (selectedItem.value.quantity < quantity.value) {
        alert('出库数量不能超过库存数量')
        return
      }
      
      if (!outPurpose.value) {
        alert('请选择出库用途')
        return
      }
      
      // 构建出库数据
      const stockOutData = {
        itemId: selectedItem.value.id,
        quantity: quantity.value,
        operationTime: new Date().toISOString(),
        taskId: selectedTask.value ? selectedTask.value.code : null,
        description: outPurpose.value + (description.value ? ` - ${description.value}` : '')
      }
      
      loading.value = true
      
      try {
        const result = await warehouseStore.performStockOut(stockOutData)
        
        if (result.success) {
          alert('出库成功!')
          router.back()
        } else {
          alert(`出库失败: ${result.error}`)
        }
      } catch (error) {
        console.error('出库操作错误:', error)
        alert('出库过程中发生错误，请稍后重试')
      } finally {
        loading.value = false
      }
    }
    
    // 当选择的分类变化时重新加载物品列表
    watch(selectedCategory, () => {
      loadWarehouseItems()
      selectedItem.value = null
    })
    
    // 页面加载时检查权限并加载数据
    onMounted(() => {
      if (!isWarehouseAdmin.value) {
        alert('您没有权限执行出库操作')
        router.back()
      } else {
        loadWarehouseItems()
        autoSelectItemFromUrl()
      }
    })
    
    return {
      warehouseId,
      itemIdFromUrl,
      categories,
      selectedCategory,
      selectedItem,
      showItemSelector,
      itemSearchQuery,
      filteredItems,
      quantity,
      outPurpose,
      selectedTask,
      showTaskSelector,
      taskSearchQuery,
      filteredTasks,
      description,
      loading,
      goBack,
      toggleItemSelector,
      selectItem,
      toggleTaskSelector,
      selectTask,
      increaseQuantity,
      decreaseQuantity,
      openScanner,
      submitStockOut
    }
  }
}
</script>

<style scoped>
.stock-out-page {
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
  width: auto;
}

.scan-btn {
  display: flex;
  align-items: center;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 12px;
  cursor: pointer;
}

.icon-qrcode::before {
  content: '\f029';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 4px;
}

.form-container {
  padding: 16px;
}

.form-group {
  margin-bottom: 16px;
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.category-selector {
  display: flex;
  gap: 8px;
}

.category-btn {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 4px;
  background-color: #f3f4f6;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  color: #6b7280;
  font-size: 12px;
  transition: all 0.2s;
}

.category-btn.active {
  background-color: #e0f2fe;
  border-color: #93c5fd;
  color: #0369a1;
}

.category-icon {
  font-size: 18px;
  margin-bottom: 4px;
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background-color: #f9fafb;
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
  margin-top: 4px;
  background-color: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 10;
  max-height: 300px;
  overflow-y: auto;
}

.search-box {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-bottom: 1px solid #e5e7eb;
}

.icon-search::before {
  content: '\f002';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #9ca3af;
  margin-right: 8px;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 14px;
}

.item-list, .task-list {
  padding: 8px 0;
}

.item-option, .task-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  cursor: pointer;
}

.item-option:hover, .task-option:hover {
  background-color: #f3f4f6;
}

.item-info, .task-info {
  flex: 1;
}

.item-name, .task-title {
  font-size: 14px;
  font-weight: 500;
  margin: 0;
}

.item-model, .task-code {
  font-size: 12px;
  color: #6b7280;
  margin: 4px 0 0 0;
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
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f3f4f6;
  border: 1px solid #e5e7eb;
  font-size: 18px;
  font-weight: 500;
}

.quantity-btn:first-child {
  border-radius: 8px 0 0 8px;
}

.quantity-btn:last-child {
  border-radius: 0 8px 8px 0;
}

.quantity-field {
  flex: 1;
  height: 40px;
  border-top: 1px solid #e5e7eb;
  border-bottom: 1px solid #e5e7eb;
  border-left: none;
  border-right: none;
  text-align: center;
  font-size: 14px;
}

.error-message {
  color: #dc2626;
  font-size: 12px;
  margin-top: 8px;
}

.form-select,
.form-input,
.form-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background-color: #f9fafb;
  font-size: 14px;
  outline: none;
}

.form-textarea {
  min-height: 100px;
  resize: vertical;
}

.bottom-actions {
  position: fixed;
  bottom: 60px;
  left: 0;
  right: 0;
  padding: 12px 16px;
  background-color: #fff;
  box-shadow: 0 -1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 16px;
  z-index: 10;
}

.bottom-actions button {
  flex: 1;
  padding: 12px 0;
  border: none;
  border-radius: 8px;
  font-weight: 500;
}

.btn-cancel {
  background-color: #f3f4f6;
  color: #6b7280;
}

.btn-confirm {
  background-color: #3b82f6;
  color: #fff;
}
</style> 