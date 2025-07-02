/**
 * 模拟数据服务
 * 用于开发和调试时提供模拟数据
 */

// 模拟任务列表数据
export const mockTaskList = [
  {
    task_id: 'RYLBJK202507010001',
    task_type: 'repair',
    device_name: 'Realline 3000型质谱仪',
    description: '设备开机后无法正常初始化，显示系统错误代码E-201',
    create_time: '2025-07-01 09:30:00',
    engineer_name: '李工程师',
    engineer_phone: '13911112222',
    current_step: 4,
    status: 'processing'
  },
  {
    task_id: 'RYLBJK202507010002',
    task_type: 'maintenance',
    device_name: 'Realline 2500型液相色谱仪',
    description: '设备需要进行季度保养，检查泵压、更换密封圈等',
    create_time: '2025-07-01 10:15:00',
    engineer_name: '王工程师',
    engineer_phone: '13922223333',
    current_step: 4,
    status: 'processing'
  },
  {
    task_id: 'RYLBJK202507010003',
    task_type: 'verification',
    device_name: 'Realline 1800型原子吸收光谱仪',
    description: '设备需要进行年度验证，包括灵敏度、精密度、准确度等指标',
    create_time: '2025-07-01 11:20:00',
    engineer_name: '张工程师',
    engineer_phone: '13933334444',
    current_step: 5,
    status: 'processing'
  },
  {
    task_id: 'RYLBJK202506250001',
    task_type: 'installation',
    device_name: 'Realline 5000型气相色谱质谱联用仪',
    description: '新设备安装与调试',
    create_time: '2025-06-25 14:30:00',
    engineer_name: '刘工程师',
    engineer_phone: '13944445555',
    current_step: 7,
    status: 'completed'
  },
  {
    task_id: 'RYLBJK202506200001',
    task_type: 'training',
    device_name: 'Realline 4000型高效液相色谱仪',
    description: '新员工培训，包括基本操作、维护和故障排除',
    create_time: '2025-06-20 09:00:00',
    engineer_name: '赵工程师',
    engineer_phone: '13955556666',
    current_step: 6,
    status: 'waiting_evaluation'
  }
];

// 模拟任务详情数据
export const mockTaskDetails = {
  'RYLBJK202507010001': {
    task_id: 'RYLBJK202507010001',
    task_type: 'repair',
    device_name: 'Realline 3000型质谱仪',
    device_model: 'RL-MS-3000',
    serial_number: 'MS30002505001',
    description: '设备开机后无法正常初始化，显示系统错误代码E-201',
    create_time: '2025-07-01 09:30:00',
    engineer_name: '李工程师',
    engineer_phone: '13911112222',
    current_step: 3,
    status: 'processing',
    customer_name: '北京科研院',
    customer_contact: '张经理',
    customer_phone: '13866667777',
    address: '北京市海淀区科学园路88号',
         step_content: {
      diagnosis: '初步诊断为电源模块故障，需要更换电源控制板',
      solution: '更换电源控制板，重新校准系统参数',
      parts_needed: [
        { name: '电源控制板', model: 'PCB-MS3000-P01', quantity: 1 },
        { name: '电源线束', model: 'CBL-MS3000-P01', quantity: 1 }
      ],
      estimated_cost: 3800,
      estimated_time: '2天',
      appointment_time: '2025-07-02 14:00:00'
    },
    images: [
      { url: '/mock-images/error-display.jpg', description: '错误显示屏幕' },
      { url: '/mock-images/power-module.jpg', description: '电源模块状态' }
    ],
    history: [
      { time: '2025-07-01 09:30:00', step: 0, content: '客户提交维修申请' },
      { time: '2025-07-01 10:15:00', step: 1, content: '工程师接单，计划当天下午上门' },
      { time: '2025-07-01 14:30:00', step: 2, content: '工程师到达现场，初步诊断为电源模块故障' }
    ]
  },
  'RYLBJK202507010002': {
    task_id: 'RYLBJK202507010002',
    task_type: 'maintenance',
    device_name: 'Realline 2500型液相色谱仪',
    device_model: 'RL-HPLC-2500',
    serial_number: 'HPLC25002504023',
    description: '设备需要进行季度保养，检查泵压、更换密封圈等',
    create_time: '2025-07-01 10:15:00',
    engineer_name: '王工程师',
    engineer_phone: '13922223333',
    current_step: 3,
    status: 'processing',
    customer_name: '上海医药研究所',
    customer_contact: '李研究员',
    customer_phone: '13977778888',
    address: '上海市浦东新区张江高科技园区创新路101号',
         step_content: {
      maintenance_items: [
        '更换泵头密封圈',
        '清洗进样针',
        '更换在线过滤器',
        '检查并校准泵压',
        '检查并校准检测器灵敏度'
      ],
      parts_replaced: [
        { name: '泵头密封圈套装', model: 'SEAL-HPLC2500-K01', quantity: 1 },
        { name: '在线过滤器', model: 'FLT-HPLC2500-01', quantity: 2 }
      ],
      cost: 1200,
      next_maintenance: '2025-10-01',
      appointment_time: '2025-07-02 09:00:00',
      assessment: '设备需要进行季度保养，检查发现泵头密封圈磨损，需要更换'
    },
    images: [
      { url: '/mock-images/pump-maintenance.jpg', description: '泵头维护' },
      { url: '/mock-images/detector-calibration.jpg', description: '检测器校准' }
    ],
    history: [
      { time: '2025-07-01 10:15:00', step: 0, content: '客户提交保养申请' },
      { time: '2025-07-01 11:00:00', step: 1, content: '工程师接单，计划次日上门' },
      { time: '2025-07-02 09:30:00', step: 2, content: '工程师到达现场，开始保养工作' },
      { time: '2025-07-02 10:45:00', step: 3, content: '完成初步检查，确定需要更换的部件' },
      { time: '2025-07-02 13:30:00', step: 4, content: '正在进行保养工作' }
    ]
  },
  'RYLBJK202507010003': {
    task_id: 'RYLBJK202507010003',
    task_type: 'verification',
    device_name: 'Realline 1800型原子吸收光谱仪',
    device_model: 'RL-AAS-1800',
    serial_number: 'AAS18002503015',
    description: '设备需要进行年度验证，包括灵敏度、精密度、准确度等指标',
    create_time: '2025-07-01 11:20:00',
    engineer_name: '张工程师',
    engineer_phone: '13933334444',
    current_step: 5,
    status: 'processing',
    customer_name: '广州环境监测中心',
    customer_contact: '陈主任',
    customer_phone: '13988889999',
    address: '广州市天河区科学城科学大道136号',
         step_content: {
      verification_items: [
        '波长准确度验证',
        '灵敏度验证',
        '精密度验证',
        '准确度验证',
        '检出限验证'
      ],
      standard_materials: [
        { name: '铜标准溶液', specification: '1000mg/L', batch: 'GSB04-1714-2004' },
        { name: '铅标准溶液', specification: '1000mg/L', batch: 'GSB04-1726-2004' },
        { name: '锌标准溶液', specification: '1000mg/L', batch: 'GSB04-1738-2004' }
      ],
      verification_result: {
        wavelength_accuracy: 'Pass',
        sensitivity: 'Pass',
        precision: 'Pass',
        accuracy: 'Pass',
        detection_limit: 'Pass'
      },
      report_number: 'VR-AAS-2025-0701',
      appointment_time: '2025-07-02 10:00:00',
      assessment: '设备需要进行年度验证，包括波长准确度、灵敏度、精密度、准确度和检出限验证',
      solution: '按照标准方法进行验证，使用标准溶液进行测试',
      cost: 2500
    },
    images: [
      { url: '/mock-images/verification-setup.jpg', description: '验证设置' },
      { url: '/mock-images/standard-measurement.jpg', description: '标准品测量' }
    ],
    history: [
      { time: '2025-07-01 11:20:00', step: 0, content: '客户提交验证申请' },
      { time: '2025-07-01 13:45:00', step: 1, content: '工程师接单，计划次日上门' },
      { time: '2025-07-02 10:00:00', step: 2, content: '工程师到达现场，开始验证准备工作' },
      { time: '2025-07-02 11:30:00', step: 3, content: '完成验证方案制定' },
      { time: '2025-07-02 14:00:00', step: 4, content: '开始执行验证方案' },
      { time: '2025-07-03 10:30:00', step: 5, content: '完成验证，正在编写验证报告' }
    ]
  },
  'RYLBJK202506250001': {
    task_id: 'RYLBJK202506250001',
    task_type: 'installation',
    device_name: 'Realline 5000型气相色谱质谱联用仪',
    device_model: 'RL-GCMS-5000',
    serial_number: 'GCMS50002506002',
    description: '新设备安装与调试',
    create_time: '2025-06-25 14:30:00',
    engineer_name: '刘工程师',
    engineer_phone: '13944445555',
    current_step: 7,
    status: 'completed',
    customer_name: '深圳分析测试中心',
    customer_contact: '王主任',
    customer_phone: '13999990000',
    address: '深圳市南山区高新科技园创新路88号',
    step_content: {
      installation_items: [
        '设备拆箱检查',
        '硬件安装',
        '软件安装',
        '系统连接测试',
        '性能验证',
        '用户培训'
      ],
      accessories_installed: [
        { name: '自动进样器', model: 'RL-AS-5000', serial: 'AS50002506002' },
        { name: '数据工作站', model: 'RL-WS-2025', serial: 'WS20252506002' }
      ],
      performance_test: {
        sensitivity: 'Pass',
        resolution: 'Pass',
        mass_accuracy: 'Pass',
        scan_speed: 'Pass'
      },
      completion_date: '2025-06-30'
    },
    images: [
      { url: '/mock-images/installation-site.jpg', description: '安装现场' },
      { url: '/mock-images/performance-test.jpg', description: '性能测试' }
    ],
    history: [
      { time: '2025-06-25 14:30:00', step: 0, content: '客户提交安装申请' },
      { time: '2025-06-26 09:00:00', step: 1, content: '工程师接单，计划次日上门' },
      { time: '2025-06-27 09:30:00', step: 2, content: '工程师到达现场，开始安装准备工作' },
      { time: '2025-06-27 11:00:00', step: 3, content: '完成安装方案确认' },
      { time: '2025-06-27 13:30:00', step: 4, content: '开始执行安装' },
      { time: '2025-06-28 16:00:00', step: 5, content: '完成安装，开始进行验证测试' },
      { time: '2025-06-29 10:30:00', step: 6, content: '完成验证测试，客户进行评价' },
      { time: '2025-06-30 09:45:00', step: 7, content: '安装任务完成' }
    ],
    evaluation: {
      service_score: 5,
      timeliness_score: 5,
      quality_score: 5,
      comments: '工程师专业素质高，安装过程规范，培训详细，非常满意'
    }
  },
  'RYLBJK202506200001': {
    task_id: 'RYLBJK202506200001',
    task_type: 'training',
    device_name: 'Realline 4000型高效液相色谱仪',
    device_model: 'RL-HPLC-4000',
    serial_number: 'HPLC40002503008',
    description: '新员工培训，包括基本操作、维护和故障排除',
    create_time: '2025-06-20 09:00:00',
    engineer_name: '赵工程师',
    engineer_phone: '13955556666',
    current_step: 6,
    status: 'waiting_evaluation',
    customer_name: '北京医科大学实验室',
    customer_contact: '张教授',
    customer_phone: '13900001111',
    address: '北京市海淀区学院路38号',
    step_content: {
      training_topics: [
        '仪器原理介绍',
        '软件操作培训',
        '日常维护方法',
        '常见故障排除',
        '应用方法开发'
      ],
      training_materials: [
        { name: '操作手册', type: 'PDF', url: '/mock-files/operation-manual.pdf' },
        { name: '维护指南', type: 'PDF', url: '/mock-files/maintenance-guide.pdf' },
        { name: '应用案例', type: 'PDF', url: '/mock-files/application-notes.pdf' }
      ],
      trainees: [
        { name: '李研究员', department: '分析实验室' },
        { name: '王技术员', department: '分析实验室' },
        { name: '张助教', department: '教学实验室' }
      ],
      training_hours: 16,
      completion_date: '2025-06-22'
    },
    images: [
      { url: '/mock-images/training-session.jpg', description: '培训现场' },
      { url: '/mock-images/hands-on-practice.jpg', description: '实操练习' }
    ],
    history: [
      { time: '2025-06-20 09:00:00', step: 0, content: '客户提交培训申请' },
      { time: '2025-06-20 10:30:00', step: 1, content: '工程师接单，计划次日上门' },
      { time: '2025-06-21 09:00:00', step: 2, content: '工程师到达现场，开始培训准备工作' },
      { time: '2025-06-21 09:30:00', step: 3, content: '完成培训方案确认' },
      { time: '2025-06-21 10:00:00', step: 4, content: '开始执行培训' },
      { time: '2025-06-22 16:00:00', step: 5, content: '完成培训，提交培训报告' },
      { time: '2025-06-23 09:00:00', step: 6, content: '培训完成，等待客户评价' }
    ]
  }
};

// 获取模拟任务列表
export const getMockTaskList = () => {
  return Promise.resolve([...mockTaskList]);
};

// 获取模拟任务详情
export const getMockTaskDetail = (taskId) => {
  const task = mockTaskDetails[taskId];
  if (!task) {
    return Promise.reject(new Error('任务不存在'));
  }
  return Promise.resolve({...task});
};

// 提交模拟评价
export const submitMockEvaluation = (taskId, evaluationData) => {
  const task = mockTaskDetails[taskId];
  if (!task) {
    return Promise.reject(new Error('任务不存在'));
  }
  
  // 更新任务状态
  task.current_step = 7;
  task.status = 'completed';
  task.evaluation = evaluationData;
  
  return Promise.resolve({success: true, message: '评价提交成功'});
};

// 确认模拟报价
export const confirmMockTaskPrice = (taskId) => {
  const task = mockTaskDetails[taskId];
  if (!task) {
    return Promise.reject(new Error('任务不存在'));
  }
  
  // 确保step_content存在
  if (!task.step_content) {
    task.step_content = {};
  }
  
  // 更新报价确认状态
  task.step_content.price_confirmed = true;
  
  return Promise.resolve({success: true, message: '报价确认成功'});
}; 