const config = {
  // 微信小程序订阅消息模板ID
  templateIds: {
    ORDER_STATUS_CHANGE: 'TJUibxXzlp2LwlK5Wwse0Gc5N8IwO-riR3aD-g89NBE', // 订单状态变更通知
    QUOTE_REMINDER: 'DaH51pHOJaxbqM1vf-NNKoFuRfweKbvEBRLF8OjYN1o',      // 订单报价提醒
    EVALUATION_REMINDER: 'YDVprZJqih5p3hGv_Odi-Oma5fLvW0vVmkcxfZZgi3M' // 报修评价提醒
  },
  
  // API基础路径
  apiBaseUrl: process.env.NODE_ENV === 'development'
    ? 'http://localhost:8085'
    : 'https://your-production-api.com',
    
  // 其他全局配置...
};

export default config;
 