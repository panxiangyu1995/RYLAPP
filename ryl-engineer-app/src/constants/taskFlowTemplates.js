// ryl-engineer-app/src/constants/taskFlowTemplates.js

/**
 * 任务类型对应的流程步骤定义
 * 这是整个前端任务流程的“唯一真理来源”。
 * 新任务创建、未初始化任务的步骤生成，都依赖此模板。
 */
export const TASK_TYPE_FLOW_STEPS = {
  // 仪器维修流程
  'repair': [
    { id: 'accepted', title: '已接单' },
    { id: 'site-visit-decision', title: '判断是否需要上门' },
    { id: 'inspection-complete', title: '检修完成' },
    { id: 'repair-plan', title: '维修方案和报价' },
    { id: 'repairing', title: '维修中' },
    { id: 'verification-report', title: '验证报告' },
    { id: 'service-evaluation', title: '服务评价' },
    { id: 'completed', title: '订单已完成' }
  ],
  
  // 仪器保养流程
  'maintenance': [
    { id: 'accepted', title: '已接单' },
    { id: 'site-visit-decision', title: '判断是否需要上门' },
    { id: 'inspection-complete', title: '检修完成' },
    { id: 'maintenance-plan', title: '保养方案和报价' },
    { id: 'maintaining', title: '保养中' },
    { id: 'verification-report', title: '验证报告' },
    { id: 'service-evaluation', title: '服务评价' },
    { id: 'completed', title: '订单已完成' }
  ],
  
  // 仪器回收流程
  'recycle': [
    { id: 'accepted', title: '已接单' },
    { id: 'site-visit-decision', title: '判断是否需要上门' },
    { id: 'inspection-complete', title: '检查完成' },
    { id: 'recycle-plan', title: '回收方案和报价' },
    { id: 'recycling', title: '回收中' },
    { id: 'verification-report', title: '验证报告' },
    { id: 'service-evaluation', title: '服务评价' },
    { id: 'completed', title: '订单已完成' }
  ],
  
  // 仪器租赁流程
  'rental': [
    { id: 'accepted', title: '已接单' },
    { id: 'site-visit-decision', title: '判断是否需要上门' },
    { id: 'inspection-complete', title: '检查完成' },
    { id: 'rental-plan', title: '租赁方案和报价' },
    { id: 'renting', title: '租赁中' },
    { id: 'verification-report', title: '验证报告' },
    { id: 'service-evaluation', title: '服务评价' },
    { id: 'completed', title: '订单已完成' }
  ],
  
  // 培训预约流程
  'training': [
    { id: 'accepted', title: '已接单' },
    { id: 'site-visit-decision', title: '判断是否需要上门' },
    { id: 'preparation-complete', title: '培训准备完成' },
    { id: 'training-plan', title: '培训方案和报价' },
    { id: 'training-in-progress', title: '培训中' },
    { id: 'verification-report', title: '验证报告' },
    { id: 'service-evaluation', title: '服务评价' },
    { id: 'completed', title: '订单已完成' }
  ],
  
  // 仪器验证流程
  'verification': [
    { id: 'accepted', title: '已接单' },
    { id: 'site-visit-decision', title: '判断是否需要上门' },
    { id: 'preparation-complete', title: '验证准备完成' },
    { id: 'verification-plan', title: '验证方案和报价' },
    { id: 'verifying', title: '验证中' },
    { id: 'verification-report', title: '验证报告' },
    { id: 'service-evaluation', title: '服务评价' },
    { id: 'completed', title: '订单已完成' }
  ],
  
  // 仪器选型流程
  'selection': [
    { id: 'accepted', title: '已接单' },
    { id: 'site-visit-decision', title: '判断是否需要上门' },
    { id: 'analysis-complete', title: '选型分析完成' },
    { id: 'selection-plan', title: '选型方案和报价' },
    { id: 'selecting', title: '选型进行中' },
    { id: 'verification-report', title: '验证报告' },
    { id: 'service-evaluation', title: '服务评价' },
    { id: 'completed', title: '订单已完成' }
  ],
  
  // 仪器安装流程
  'installation': [
    { id: 'accepted', title: '已接单' },
    { id: 'site-visit-decision', title: '判断是否需要上门' },
    { id: 'preparation-complete', title: '安装准备完成' },
    { id: 'installation-plan', title: '安装方案和报价' },
    { id: 'installing', title: '安装中' },
    { id: 'verification-report', title: '验证报告' },
    { id: 'service-evaluation', title: '服务评价' },
    { id: 'completed', title: '订单已完成' }
  ]
}; 