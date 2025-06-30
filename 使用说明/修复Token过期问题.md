# 修复Token过期问题解决方案

## 问题描述

在APP个人中心页面底部显示"无效的token"提示，表明用户登录认证令牌可能已过期或无效，需要刷新或重新获取token。

## 问题原因分析

1. Token可能已经过期
2. Token可能格式不正确
3. 后端可能未正确返回或保存Token过期时间
4. 前端可能未正确存储或解析Token信息
5. 用户会话可能已失效

## 解决方案

### 1. 前端修复方案

#### 创建Token刷新服务

首先，添加token刷新API请求方法：

```javascript
// api/userApi.js
import http from '@/utils/http';

// 刷新用户token
export function refreshToken() {
  return http.post('/api/v1/user/token/refresh');
}
```

然后，创建token自动检查和刷新的服务：

```javascript
// utils/tokenService.js
import { refreshToken } from '@/api/userApi';
import { useUserStore } from '@/stores/user';
import { getToken, setToken, removeToken } from '@/utils/auth';
import router from '@/router';

// Token过期提前刷新阈值（毫秒）- 设置为10分钟
const REFRESH_THRESHOLD = 10 * 60 * 1000;

// 检查token是否快过期
export function isTokenExpiringSoon() {
  const userStore = useUserStore();
  if (!userStore.tokenExpire) return true;
  
  const expireTime = new Date(userStore.tokenExpire).getTime();
  const currentTime = new Date().getTime();
  
  // 如果token将在阈值时间内过期，则需要刷新
  return expireTime - currentTime < REFRESH_THRESHOLD;
}

// 检查token是否已过期
export function isTokenExpired() {
  const userStore = useUserStore();
  if (!userStore.tokenExpire) return true;
  
  const expireTime = new Date(userStore.tokenExpire).getTime();
  const currentTime = new Date().getTime();
  
  return currentTime >= expireTime;
}

// 自动刷新token
export async function autoRefreshToken() {
  try {
    // 如果没有token，不进行刷新
    const token = getToken();
    if (!token) return false;
    
    // 如果token已过期，跳转到登录页
    if (isTokenExpired()) {
      console.log('Token已过期，需要重新登录');
      await logout();
      return false;
    }
    
    // 如果token即将过期，刷新token
    if (isTokenExpiringSoon()) {
      console.log('Token即将过期，自动刷新');
      const response = await refreshToken();
      
      if (response.code === 200 && response.data) {
        const { token, tokenExpire } = response.data;
        
        // 更新token和过期时间
        setToken(token);
        const userStore = useUserStore();
        userStore.setTokenExpire(tokenExpire);
        
        console.log('Token刷新成功，新的过期时间:', tokenExpire);
        return true;
      } else {
        console.error('Token刷新失败:', response.message);
        return false;
      }
    }
    
    return true;
  } catch (error) {
    console.error('Token刷新异常:', error);
    return false;
  }
}

// 登出
export async function logout() {
  const userStore = useUserStore();
  userStore.resetUser();
  removeToken();
  router.push('/login');
}
```

修改个人中心页面以显示正确的token状态并提供刷新功能：

```javascript
// views/profile/index.vue
<template>
  <div class="profile-page">
    <!-- 原有个人信息内容 -->
    
    <!-- Token状态显示 -->
    <div class="token-status" :class="{ 'token-invalid': isTokenInvalid }">
      <template v-if="isTokenInvalid">
        <span class="token-error">无效的token</span>
        <button class="refresh-btn" @click="handleRefreshToken">刷新token</button>
      </template>
      <template v-else>
        <span class="token-valid">token有效</span>
        <span class="token-expire">过期时间: {{ formatExpireTime }}</span>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue';
import { useUserStore } from '@/stores/user';
import { autoRefreshToken, isTokenExpired } from '@/utils/tokenService';
import { refreshToken } from '@/api/userApi';

const userStore = useUserStore();

// 检查token是否无效
const isTokenInvalid = computed(() => {
  return !userStore.token || !userStore.tokenExpire || isTokenExpired();
});

// 格式化过期时间显示
const formatExpireTime = computed(() => {
  if (!userStore.tokenExpire) return '未知';
  return new Date(userStore.tokenExpire).toLocaleString();
});

// 处理手动刷新token
const handleRefreshToken = async () => {
  try {
    const response = await refreshToken();
    
    if (response.code === 200 && response.data) {
      const { token, tokenExpire } = response.data;
      
      // 更新token和过期时间
      userStore.setToken(token);
      userStore.setTokenExpire(tokenExpire);
      
      alert('Token刷新成功');
    } else {
      alert('Token刷新失败: ' + (response.message || '未知错误'));
    }
  } catch (error) {
    console.error('Token刷新异常:', error);
    alert('Token刷新异常: ' + (error.message || '未知错误'));
  }
};

// 页面加载时自动检查token
onMounted(async () => {
  await autoRefreshToken();
});
</script>

<style scoped>
.token-status {
  margin-top: 20px;
  padding: 10px;
  border-radius: 4px;
  text-align: center;
}

.token-valid {
  color: #67c23a;
}

.token-error {
  color: #f56c6c;
}

.token-invalid {
  background-color: #fef0f0;
}

.refresh-btn {
  margin-left: 10px;
  padding: 4px 10px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.token-expire {
  margin-left: 10px;
  font-size: 0.9em;
  color: #909399;
}
</style>
```

更新用户状态管理：

```javascript
// stores/user.js
import { defineStore } from 'pinia';
import { getToken, setToken, removeToken } from '@/utils/auth';

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    tokenExpire: localStorage.getItem('tokenExpire') || null,
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
    // 其他用户相关状态...
  }),
  
  actions: {
    setToken(token) {
      this.token = token;
      setToken(token);
    },
    
    setTokenExpire(expireTime) {
      this.tokenExpire = expireTime;
      localStorage.setItem('tokenExpire', expireTime);
    },
    
    setUserInfo(info) {
      this.userInfo = info;
      localStorage.setItem('userInfo', JSON.stringify(info));
    },
    
    resetUser() {
      this.token = null;
      this.tokenExpire = null;
      this.userInfo = {};
      removeToken();
      localStorage.removeItem('tokenExpire');
      localStorage.removeItem('userInfo');
    }
  }
});
```

### 2. 后端修复方案

创建或更新Token刷新API：

```java
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // ... 其他接口方法 ...
    
    /**
     * 刷新用户Token
     */
    @PostMapping("/token/refresh")
    public Result<TokenDTO> refreshToken(HttpServletRequest request) {
        // 从请求头获取当前token
        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token) || !token.startsWith("Bearer ")) {
            return Result.error(ResultCode.UNAUTHORIZED, "无效的token");
        }
        
        token = token.substring(7);
        
        try {
            // 调用service层刷新token
            TokenDTO tokenDTO = userService.refreshToken(token);
            return Result.success(tokenDTO);
        } catch (Exception e) {
            log.error("刷新token失败", e);
            return Result.error(ResultCode.UNAUTHORIZED, "刷新token失败");
        }
    }
}
```

实现刷新Token的服务：

```java
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private TokenUtils tokenUtils;
    
    // ... 其他服务方法 ...
    
    /**
     * 刷新用户Token
     */
    @Override
    public TokenDTO refreshToken(String oldToken) {
        // 验证旧token
        Long userId = tokenUtils.validateToken(oldToken);
        if (userId == null) {
            throw new BusinessException("token已失效");
        }
        
        // 获取用户信息
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 生成新token
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + tokenUtils.getExpiration());
        String newToken = tokenUtils.generateToken(user.getId(), user.getWorkId());
        
        // 更新用户token信息
        user.setToken(newToken);
        user.setTokenExpire(expireTime);
        userMapper.updateById(user);
        
        // 返回新token信息
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setToken(newToken);
        tokenDTO.setTokenExpire(expireTime);
        
        return tokenDTO;
    }
}
```

## 实施步骤

1. 后端实现：
   - 添加Token刷新API
   - 实现Token刷新服务
   - 单元测试Token刷新功能

2. 前端实现：
   - 添加Token服务工具类
   - 更新用户状态管理
   - 修改个人中心页面

3. 联调测试：
   - 测试Token自动刷新功能
   - 测试Token过期后的登录跳转
   - 测试手动刷新Token功能

4. 上线部署：
   - 部署后端服务
   - 部署前端应用
   - 监控Token相关异常情况

## 预期效果

1. 个人中心页面将不再显示"无效的token"提示
2. 当Token即将过期时，系统会自动刷新Token
3. 当Token已过期时，系统会引导用户重新登录
4. 用户可以手动刷新Token 