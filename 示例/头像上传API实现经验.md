# 头像上传API实现经验总结

## 一、概述

头像上传功能是用户个人中心的重要功能之一，涉及前端文件选择、预览、上传进度显示，以及后端文件处理、存储和URL生成等多个环节。本文档总结了在科研仪器维修APP项目中实现头像上传API的经验和最佳实践。

## 二、数据库设计

### 2.1 用户表设计

```sql
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    work_id VARCHAR(50) NOT NULL COMMENT '工号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    mobile VARCHAR(20) NOT NULL COMMENT '手机号',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    department VARCHAR(50) COMMENT '部门',
    location VARCHAR(50) COMMENT '工作地点',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    token VARCHAR(255) COMMENT '登录令牌',
    token_expire DATETIME COMMENT '令牌过期时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_time DATETIME COMMENT '最后登录时间',
    UNIQUE KEY uk_work_id (work_id),
    UNIQUE KEY uk_mobile (mobile)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

`avatar`字段使用VARCHAR(255)类型，用于存储头像图片的URL路径，足够存储常见的URL长度。

## 三、后端实现

### 3.1 配置文件设置

在`application.yml`中添加头像上传相关配置：

```yaml
# 应用配置
app:
  upload:
    # 是否使用绝对路径
    use-absolute-path: true
    # 上传文件基础路径，为空则使用系统工作目录下的uploads文件夹
    base-path: D:/uploads
    # 头像路径
    avatar-path: avatars
    # 头像访问URL前缀
    avatar-url-prefix: http://localhost:8089/api/v1/uploads/avatars
```

### 3.2 控制器实现

在`UserController.java`中实现头像上传API：

```java
@Autowired
private UserService userService;

// 从配置中获取上传路径
@Value("${app.upload.base-path:}")
private String baseUploadPath;

// 从配置中获取头像路径
@Value("${app.upload.avatar-path:avatars}")
private String avatarPath;

// 从配置中获取头像URL前缀
@Value("${app.upload.avatar-url-prefix:http://localhost:8089/api/v1/uploads/avatars}")
private String avatarUrlPrefix;

/**
 * 更新用户头像
 *
 * @param file 头像文件
 * @param request HTTP请求
 * @return 更新结果
 */
@PostMapping("/avatar")
public Result<Map<String, Object>> updateAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
    // 从请求头中获取token
    String token = request.getHeader("Authorization");
    if (token != null && token.startsWith("Bearer ")) {
        token = token.substring(7);
    }
    
    if (token == null || token.isEmpty()) {
        return Result.error("未登录");
    }
    
    // 从token中获取用户ID
    Long userId = JwtUtil.getUserId(token);
    if (userId == null) {
        return Result.error("无效的token");
    }
    
    // 获取用户信息
    User user = userService.getUserById(userId);
    if (user == null) {
        return Result.error("用户不存在");
    }
    
    // 检查文件是否为空
    if (file.isEmpty()) {
        return Result.error("文件不能为空");
    }
    
    try {
        // 验证文件类型和大小
        // 文件保存逻辑
        // 更新用户信息
        // 构建响应
    } catch (IOException e) {
        e.printStackTrace();
        return Result.error("文件上传失败: " + e.getMessage());
    }
}
```

### 3.3 文件验证与处理

```java
// 获取文件名
String fileName = file.getOriginalFilename();
// 检查文件类型
String contentType = file.getContentType();
if (contentType == null || !contentType.startsWith("image/")) {
    return Result.error("只支持上传图片文件");
}

// 获取文件后缀
String suffix = fileName.substring(fileName.lastIndexOf("."));
// 检查文件后缀
if (!".jpg".equalsIgnoreCase(suffix) && !".jpeg".equalsIgnoreCase(suffix) 
    && !".png".equalsIgnoreCase(suffix) && !".gif".equalsIgnoreCase(suffix)) {
    return Result.error("只支持JPG、JPEG、PNG和GIF格式的图片");
}

// 限制文件大小（5MB）
if (file.getSize() > 5 * 1024 * 1024) {
    return Result.error("文件大小不能超过5MB");
}
```

### 3.4 文件存储与URL生成

```java
// 使用类级别的配置变量
String effectiveBasePath = baseUploadPath;

// 如果配置为空，使用默认路径
if (effectiveBasePath == null || effectiveBasePath.isEmpty()) {
    effectiveBasePath = System.getProperty("user.dir") + "/uploads";
}

// 创建文件存储目录
String avatarDir = effectiveBasePath + "/" + avatarPath + "/";
File dir = new File(avatarDir);
if (!dir.exists()) {
    boolean created = dir.mkdirs();
    if (!created) {
        return Result.error("创建上传目录失败");
    }
}

// 生成新的文件名
String newFileName = "avatar_" + userId + "_" + System.currentTimeMillis() + suffix;
String filePath = avatarDir + newFileName;

// 保存文件
File dest = new File(filePath);
file.transferTo(dest);

// 构建头像URL
String avatarUrl = avatarUrlPrefix + "/" + newFileName;

// 更新用户头像URL
user.setAvatar(avatarUrl);
boolean success = userService.updateUser(user);

if (!success) {
    return Result.error("更新头像失败");
}

// 构建响应
Map<String, Object> response = new HashMap<>();
response.put("updated", true);
response.put("avatarUrl", avatarUrl);

return Result.success(response);
```

### 3.5 静态资源访问配置

在`WebMvcConfig.java`中配置静态资源访问：

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ServletContext servletContext;
    
    @Value("${app.upload.use-absolute-path:true}")
    private boolean useAbsolutePath;
    
    @Value("${app.upload.base-path:}")
    private String configuredUploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取上传文件的基础路径
        String baseUploadPath;
        if (useAbsolutePath) {
            // 如果配置了自定义路径，则使用配置的路径
            if (configuredUploadPath != null && !configuredUploadPath.isEmpty()) {
                baseUploadPath = configuredUploadPath;
            } else {
                // 否则使用当前工作目录
                baseUploadPath = System.getProperty("user.dir") + File.separator + "uploads";
            }
        } else {
            // 使用相对于Web应用的路径
            baseUploadPath = servletContext.getRealPath("/uploads");
        }
        
        // 确保路径以分隔符结尾
        if (!baseUploadPath.endsWith(File.separator)) {
            baseUploadPath += File.separator;
        }
        
        // 配置上传的头像文件访问路径
        registry.addResourceHandler("/api/v1/uploads/**")
                .addResourceLocations("file:" + baseUploadPath);
                
        // 添加其他静态资源路径
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
```

## 四、前端实现

### 4.1 头像上传组件

在`ProfileInfo.vue`中实现头像上传功能：

```html
<!-- 头像和基本信息 -->
<div class="avatar-card">
  <div class="avatar-container">
    <img :src="userInfo.avatar || '/img/default-avatar.png'" class="avatar" alt="工程师头像">
    <div class="avatar-edit-btn" @click="changeAvatar">
      <i class="fas fa-camera"></i>
      <span>更换头像</span>
    </div>
    <!-- 添加上传进度条 -->
    <div v-if="isUploading" class="upload-progress-container">
      <div class="upload-progress-bar" :style="{ width: uploadProgress + '%' }"></div>
      <span class="upload-progress-text">{{ uploadProgress }}%</span>
    </div>
  </div>
  <input
    type="file"
    ref="avatarInput"
    style="display: none"
    accept="image/jpeg, image/png, image/gif"
    @change="onAvatarSelected"
  />
</div>
```

### 4.2 文件选择与预览

```javascript
// 点击更换头像
function changeAvatar() {
  avatarInput.value.click()
}

// 头像文件选择后
async function onAvatarSelected(event) {
  const file = event.target.files[0]
  if (!file) return
  
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    alert('请选择图片文件')
    return
  }
  
  // 验证文件后缀
  const validExtensions = ['.jpg', '.jpeg', '.png', '.gif']
  const fileExtension = file.name.substring(file.name.lastIndexOf('.')).toLowerCase()
  if (!validExtensions.includes(fileExtension)) {
    alert('只支持JPG、JPEG、PNG和GIF格式的图片')
    return
  }
  
  // 验证文件大小（5MB限制）
  const maxSize = 5 * 1024 * 1024 // 5MB
  if (file.size > maxSize) {
    alert('文件大小不能超过5MB')
    return
  }
  
  // 创建预览
  selectedFile.value = file
  previewImageUrl.value = URL.createObjectURL(file)
  showPreview.value = true
}
```

### 4.3 文件上传与进度显示

```javascript
// 确认上传
async function confirmUpload() {
  if (!selectedFile.value) return
  
  try {
    isUploading.value = true
    uploadProgress.value = 0
    globalError.value = null
    
    // 创建FormData对象
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    
    // 监听上传进度
    const originalPost = axios.post
    axios.post = (url, data, config) => {
      config = config || {}
      config.onUploadProgress = (progressEvent) => {
        const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        uploadProgress.value = percentCompleted
      }
      return originalPost(url, data, config)
    }
    
    // 上传头像
    const result = await userStore.updateAvatar(formData)
    
    // 恢复原始方法
    axios.post = originalPost
    
    if (result.success) {
      userInfo.avatar = result.avatarUrl
      showPreview.value = false
      // 释放创建的URL对象
      if (previewImageUrl.value) {
        URL.revokeObjectURL(previewImageUrl.value)
        previewImageUrl.value = ''
      }
      selectedFile.value = null
      alert('头像更新成功')
    } else {
      throw new Error(result.error || '更新头像失败')
    }
  } catch (error) {
    console.error('更新头像失败:', error)
    globalError.value = '更新头像失败，请稍后再试'
  } finally {
    isUploading.value = false
    // 清空文件输入框，允许重新选择相同文件
    avatarInput.value.value = ''
  }
}
```

### 4.4 状态管理

在`user.js`存储中实现更新头像方法：

```javascript
// 更新用户头像
async function updateAvatar(formData) {
  try {
    const response = await axios.post('/api/v1/user/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    
    const data = response.data
    if (data.code === 200) {
      // 更新本地存储中的头像URL
      try {
        const userJson = localStorage.getItem('user')
        if (userJson) {
          const userData = JSON.parse(userJson)
          userData.avatar = data.data.avatarUrl
          localStorage.setItem('user', JSON.stringify(userData))
        }
      } catch (e) {
        console.error('更新本地用户头像失败:', e)
      }
      
      return {
        success: true,
        avatarUrl: data.data.avatarUrl
      }
    } else {
      return {
        success: false,
        error: data.message || '更新头像失败'
      }
    }
  } catch (error) {
    console.error('更新头像请求失败:', error)
    return {
      success: false,
      error: error.response?.data?.message || '网络错误，请稍后再试'
    }
  }
}
```

## 五、常见问题与解决方案

### 5.1 CORS跨域问题

确保后端正确配置了CORS，允许文件上传请求：

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
}
```

### 5.2 文件上传大小限制

在`application.yml`中配置文件上传大小限制：

```yaml
server:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 100MB
```

### 5.3 头像预览和URL管理

1. 使用`URL.createObjectURL()`创建本地预览
2. 预览结束后使用`URL.revokeObjectURL()`释放资源
3. 使用合适的图片格式和压缩级别，平衡图片质量和文件大小

### 5.4 Vue组件中常见的语法错误

在Vue 3的`<script setup>`语法中，不应该使用独立的`return`语句暴露变量，而应该使用`defineExpose()`方法：

```javascript
// 错误写法
return {
  userInfo,
  loading,
  // ...
}

// 正确写法
defineExpose({
  userInfo,
  loading,
  // ...
})
```

## 六、最佳实践

1. **配置化文件存储**：通过配置文件管理存储路径和URL前缀，便于环境切换
2. **严格的文件验证**：检查文件类型、大小和扩展名，确保安全性
3. **用户友好的体验**：
   - 实现图片预览功能
   - 显示上传进度条
   - 添加成功/失败反馈
4. **安全的文件命名**：使用用户ID和时间戳生成唯一文件名，避免文件覆盖
5. **资源释放**：使用完毕的资源及时释放，如预览URL和文件选择器
6. **错误处理**：全面的错误捕获和用户友好的错误提示
7. **本地状态更新**：上传成功后同步更新本地存储和状态管理中的用户信息

## 七、未来改进方向

1. **图片压缩**：在前端上传前压缩图片，减少传输数据量
2. **图片裁剪**：添加图片裁剪功能，让用户可以调整头像显示区域
3. **对象存储服务**：使用云存储服务代替本地文件存储，提高可靠性和可扩展性
4. **图片CDN加速**：使用CDN分发头像图片，提高加载速度
5. **断点续传**：支持大文件的断点续传功能
6. **图片缓存策略**：实现合理的缓存策略，减少重复请求 