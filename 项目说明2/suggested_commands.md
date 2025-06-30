# 研仪联工程师APP开发常用命令

## Windows系统常用命令

### 基础文件操作
```bash
# 列出目录内容
dir [目录路径]

# 切换目录
cd [目录路径]

# 创建目录
mkdir [目录名称]

# 删除文件
del [文件名]

# 删除目录
rmdir /s /q [目录名]

# 复制文件
copy [源文件] [目标文件]

# 移动文件
move [源文件] [目标文件]

# 查找文本内容
findstr "搜索文本" [文件名或路径]
```

### Git命令
```bash
# 克隆仓库
git clone [仓库URL]

# 查看状态
git status

# 添加文件到暂存区
git add [文件名]
git add .  # 添加所有文件

# 提交更改
git commit -m "提交信息"

# 拉取更新
git pull

# 推送更改
git push

# 创建并切换到新分支
git checkout -b [分支名]

# 切换分支
git checkout [分支名]

# 合并分支
git merge [分支名]
```

## 前端项目命令（ryl-engineer-app）

### 项目运行
```bash
# 进入前端项目目录
cd D:/Android/rylappfront/ryl-engineer-app

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览构建结果
npm run preview
```

### 依赖管理
```bash
# 安装依赖
npm install [包名]

# 安装开发依赖
npm install [包名] --save-dev

# 更新依赖
npm update [包名]
```

## 后端项目命令（ryl-engineer-back）

### Maven命令
```bash
# 进入后端项目目录
cd D:/Android/rylappfront/ryl-engineer-back

# 清理并编译项目
mvn clean compile

# 打包项目
mvn package

# 运行测试
mvn test

# 安装到本地仓库
mvn install

# 跳过测试打包
mvn package -DskipTests
```

### Spring Boot运行
```bash
# 使用Maven运行Spring Boot应用
mvn spring-boot:run

# 指定配置文件运行
mvn spring-boot:run -Dspring.profiles.active=dev
```

## 数据库操作

### SQL Server命令
```bash
# 连接到SQL Server（使用sqlcmd，需要SQL Server客户端工具）
sqlcmd -S [服务器名称] -d [数据库名称] -U [用户名] -P [密码]

# 执行SQL脚本文件
sqlcmd -S [服务器名称] -d [数据库名称] -U [用户名] -P [密码] -i [脚本文件路径]
```

## 项目检查与测试

### 前端测试与检查
```bash
# 代码格式化（如果配置了）
npm run format

# 代码检查（如果配置了）
npm run lint

# 运行单元测试（如果配置了）
npm run test
```

### 后端测试与检查
```bash
# 运行单元测试
mvn test

# 运行特定测试
mvn test -Dtest=[测试类名]

# 代码质量检查（如果配置了）
mvn verify
```

## 项目部署

### 前端部署
```bash
# 构建生产版本
npm run build

# 生成的文件在dist目录，可以部署到任何静态文件服务器
```

### 后端部署
```bash
# 打包成可执行Jar
mvn package -DskipTests

# 运行Jar文件
java -jar target/ryl-engineer-back-0.0.1-SNAPSHOT.jar
```