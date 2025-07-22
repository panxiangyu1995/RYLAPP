### **实施规划：使用Capacitor打包 `ryl-engineer-app`**

#### **目标**
将基于Vite和Vue 3的 `ryl-engineer-app` Web项目，打包成一个可以安装在Android设备上的原生APK文件，并确保应用可以正常访问后端API。

---

#### **第一阶段：环境准备与项目初始化**

此阶段的目标是在您现有的前端项目中集成Capacitor环境。

**实施清单：**

1.  **安装必备软件**
    *   确保您的开发环境中已安装 [Node.js](https://nodejs.org/) (推荐LTS版本)。
    *   下载并安装 [Android Studio](https://developer.android.com/studio)。安装过程中，请确保勾选并安装了 "Android SDK" 和 "Android Virtual Device" (用于模拟器)。

2.  **安装Capacitor依赖**
    *   在 `ryl-engineer-app` 项目的根目录下，打开终端，执行以下命令来安装Capacitor的核心库、命令行工具(CLI)以及Android平台支持包：
        ```bash
        cd D:\Android\app\RYLAPP\ryl-engineer-app
        npm install @capacitor/core @capacitor/cli @capacitor/android
        ```

3.  **初始化Capacitor配置**
    *   继续在终端中执行初始化命令。这条命令会创建 `capacitor.config.ts` 配置文件，它是Capacitor的核心。
        ```bash
        npx cap init "瑞屹林工程师" "com.ryl.engineer.app" --web-dir="dist"
        ```
        *   **参数说明**:
            *   `"瑞屹林工程师"`: 这是应用的显示名称 (App Name)。
            *   `"com.ryl.engineer.app"`: 这是应用的包名 (Package ID)，是Android系统中应用的唯一标识，通常采用反向域名格式。
            *   `--web-dir="dist"`: 这是至关重要的一步。它告诉Capacitor，您的Web项目构建后的静态资源将存放在 `dist` 目录中。

4.  **添加Android原生平台**
    *   执行以下命令，Capacitor将在 `ryl-engineer-app` 根目录下创建一个名为 `android` 的文件夹。这个文件夹本身就是一个完整的、可以独立运行的Android Studio项目。
        ```bash
        npx cap add android
        ```
    *   **注意**: 此时 `android` 目录应该被添加到 `.gitignore` 文件中，以避免将庞大的原生项目文件提交到Git仓库。Capacitor的设计理念是原生项目可以根据配置文件随时重新生成。

---

#### **第二阶段：配置与构建**

此阶段的目标是解决Web应用在原生环境中的适配问题，并完成首次构建同步。

**实施清单：**

5.  **处理API网络请求**
    *   **问题**: 在原生App环境中，Web应用无法通过 `http://localhost:8080` 这样的地址访问本地开发服务器的API。
    *   **解决方案**: 您需要在前端代码中配置一个生产环境的后端API地址。请检查 `src/api` 或相关网络请求的封装文件（如 `utils/request.js`），确保axios的基础URL (`baseURL`) 指向您部署好的后端服务器公网IP或域名。如果需要在开发阶段连接本地后端，可以使用您电脑在局域网中的IP地址（例如 `http://192.168.1.10:8080`），并确保手机与电脑在同一Wi-Fi网络下。

6.  **构建Web项目**
    *   执行标准的Vue项目构建命令，将所有代码和资源打包到 `dist` 目录。
        ```bash
        npm run build
        ```

7.  **同步Web资源到Android项目**
    *   执行Capacitor的同步命令。这个命令会完成两件事：
        1.  将 `dist` 目录下的所有文件复制到 `android/app/src/main/assets/public` 目录。
        2.  读取已安装的Capacitor插件，并自动配置Android原生项目。
        ```bash
        npx cap sync android
        ```

---

#### **第三阶段：原生打包与发布**

此阶段我们将在Android Studio中完成最后的编译、调试和打包工作。

**实施清单：**

8.  **在Android Studio中打开项目**
    *   执行以下命令，Capacitor会自动启动Android Studio并加载 `android` 目录下的原生项目。
        ```bash
        npx cap open android
        ```
    *   Android Studio首次打开项目时，会使用Gradle进行项目同步和构建，这可能需要一些时间，请耐心等待。

9.  **在模拟器或真机上运行**
    *   **模拟器**: 在Android Studio顶部的设备选择下拉菜单中，选择一个已创建的模拟器（或通过 "AVD Manager" 新建一个），然后点击绿色的 "Run 'app'" 播放按钮。
    *   **真机**: 用USB线连接您的Android手机，并确保手机已开启 "开发者模式" 和 "USB调试"。连接后，设备会出现在下拉菜单中，选中它并点击运行按钮。

10. **生成用于测试的Debug APK**
    *   在Android Studio的菜单栏中，选择 `Build` -> `Build Bundle(s) / APK(s)` -> `Build APK(s)`。
    *   构建完成后，Android Studio右下角会弹出提示，点击 "locate" 链接即可找到生成的 `app-debug.apk` 文件。您可以将此文件直接发给测试人员进行安装。

11. **生成用于发布的Release APK**
    *   正式发布需要对APK进行数字签名。在菜单栏中选择 `Build` -> `Generate Signed Bundle / APK...`。
    *   选择 `APK` 并点击 "Next"。
    *   在下一个界面中，点击 "Create new..." 创建一个新的签名密钥库（Keystore）。请务必**妥善保管好这个文件和您设置的密码**，未来所有应用更新都必须使用同一个签名。
    *   创建并选择好密钥后，选择 "release" 构建类型，然后点击 "Finish"。Android Studio将生成一个已签名的、用于正式发布的 `app-release.apk` 文件。 