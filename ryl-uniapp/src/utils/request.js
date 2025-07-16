 // 基于 uni.request 的封装
 const baseURL = 'http://localhost:8085/api'; // 后端 API 地址

 // 基础请求函数
 const baseRequest = (options) => {
	 return new Promise((resolve, reject) => {
		 const url = baseURL + options.url;
 
		 let header = options.header || {};
		 const token = uni.getStorageSync('token');
		 if (token) {
			 header['Authorization'] = `Bearer ${token}`;
		 }
		 
		 uni.request({
			 ...options,
			 url: url,
			 header: header,
			 timeout: options.timeout || 10000, // 默认10秒超时
			 success: (res) => {
				 const { statusCode, data } = res;
				 
				 if (statusCode === 200) {
					 if (data.code === 200) {
						 resolve(data);
					 } else {
						// Check for the suppress option
						if (!options.suppressErrorToast) {
							uni.showToast({
								title: data.msg || '业务处理失败',
								icon: 'none'
							});
						}
						reject(data);
					 }
				 } else if (statusCode === 401) {
					 uni.showModal({
						 title: '提示',
						 content: '登录状态已过期，请重新登录',
						 showCancel: false,
						 success: (modalRes) => {
							 if (modalRes.confirm) {
								 uni.reLaunch({
									 url: '/pages/login/login'
								 });
							 }
						 }
					 });
					 reject(new Error('Unauthorized'));
				 } else {
					 uni.showToast({
						 title: `请求失败，状态码：${statusCode}`,
						 icon: 'none'
					 });
					 reject(new Error(`HTTP error! status: ${statusCode}`));
				 }
			 },
			 fail: (err) => {
				 uni.showToast({
					 title: '网络请求异常，请检查您的网络连接',
					 icon: 'none'
				 });
				 reject(err);
			 }
		 });
	 });
 };
 
 // 导出 request 对象，它本身是 baseRequest 函数，并附加了便捷方法
 const request = baseRequest;
 
 request.get = (url, options = {}) => {
   return baseRequest({ url, method: 'GET', ...options });
 }
 
 request.post = (url, data, options = {}) => {
   return baseRequest({ url, data, method: 'POST', ...options });
 }
 
 request.put = (url, data, options = {}) => {
   return baseRequest({ url, data, method: 'PUT', ...options });
 }
 
 request.delete = (url, options = {}) => {
   return baseRequest({ url, method: 'DELETE', ...options });
 }
 
 export default request;
 