const config = {
  // 微信小程序订阅消息模板ID
  templateIds: {
    ENGINEER_ASSIGNED: 'YOUR_TEMPLATE_ID_HERE',
    QUOTE_GENERATED: 'YOUR_TEMPLATE_ID_HERE',
    PRICE_CONFIRMED: 'YOUR_TEMPLATE_ID_HERE',
    SERVICE_COMPLETED: 'YOUR_TEMPLATE_ID_HERE',
    EVALUATION_RECEIVED: 'YOUR_TEMPLATE_ID_HERE'
  },
  
  // API基础路径
  apiBaseUrl: process.env.NODE_ENV === 'development'
    ? 'http://localhost:8085'
    : 'https://your-production-api.com',
    
  // 其他全局配置...
};

export default config;
 