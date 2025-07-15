# 瑞屹林后端服务 Windows Server 部署指南

## 1. 概述 (Overview)

本文档旨在提供一份详尽的操作手册，指导开发或运维人员如何将 `ryl-miniprogram-server`（一个基于 Spring Boot 的 Java 应用）打包，并作为一项稳定、可靠的后台服务部署到 Windows Server 操作系统上。

## 2. 环境准备 (Prerequisites)

在开始部署之前，请确保您的 Windows Server 服务器满足以下条件：

*   **Java 运行环境**: 服务器上必须安装 Java 运行环境 (JRE) 或 Java 开发工具包 (JDK)，版本要求为 **Java 8 或更高版本**。
    *   打开命令提示符 (CMD) 或 PowerShell，输入以下命令进行验证：
        ```shell
        java -version
        ```
    *   如果命令能够成功显示 Java 版本号，则表示环境已准备就绪。

## 3. 打包应用程序 (Packaging)

首先，我们需要将后端项目的源代码打包成一个可执行的 JAR 文件。

1.  **环境要求**: 确保您的开发机器上已安装 [Apache Maven](https://maven.apache.org/download.cgi)。
2.  **执行打包**:
    *   在您的开发机器上，打开终端，进入后端项目 `ryl-miniprogram-server` 的根目录（即 `pom.xml` 文件所在的目录）。
    *   运行标准的 Maven 打包命令：
        ```shell
        mvn clean package
        ```
3.  **获取产物**: 命令成功执行后，您会在 `ryl-miniprogram-server\target\` 目录下找到最终的产物：`ryl-miniprogram-server-0.0.1-SNAPSHOT.jar`。这个文件就是我们将要部署到服务器上的目标文件。

## 4. 核心部署流程 (Deployment with WinSW)

我们将使用业界流行的 `WinSW` 工具，将 JAR 文件包装成一个真正的 Windows 服务，实现开机自启和稳定运行。

#### 步骤 4.1: 在服务器上创建服务目录

在您的 Windows Server 上，创建一个专门用于存放服务文件的目录。建议使用一个路径不含中文和空格的简洁路径。

例如：`C:\Services\RuiYiLin`

#### 步骤 4.2: 下载并配置 WinSW

1.  **下载 WinSW**: 从 [WinSW 的官方 GitHub Releases 页面](https://github.com/winsw/winsw/releases) 下载最新的 `WinSW-x64.exe`（或根据您的系统选择其他版本）。
2.  **重命名**: 将下载的 `.exe` 文件放入上一步创建的 `C:\Services\RuiYiLin` 目录中，并将其重命名为一个与您的服务相关的名称，例如：`ryl-service.exe`。
3.  **创建配置文件**: 在**相同目录**下，创建一个 XML 文件，并将其命名为与 `.exe` 文件**完全相同**的名称，只是扩展名不同。例如：`ryl-service.xml`。
4.  **编辑配置文件**: 使用记事本或任何文本编辑器打开 `ryl-service.xml`，并粘贴以下内容。这是一个为本项目预设好的模板：

    ```xml
    <service>
      <!-- 服务的唯一ID，建议使用纯英文 -->
      <id>ryl-miniprogram-server</id>
      
      <!-- 在服务管理器中显示的名称 -->
      <name>RuiYiLin Miniprogram Service</name>
      
      <!-- 服务的详细描述 -->
      <description>这是瑞屹林微信小程序的后端服务。</description>
      
      <!-- 要执行的命令。对于 Spring Boot fat JAR，就是 java -->
      <executable>java</executable>
      
      <!-- 传递给可执行文件的参数。-jar 参数告诉 java 运行一个 JAR 文件 -->
      <arguments>-jar ryl-miniprogram-server-0.0.1-SNAPSHOT.jar</arguments>
      
      <!-- 日志模式。'rotate' 会自动管理日志文件，防止其无限增大 -->
      <logmode>rotate</logmode>
    </service>
    ```

#### 步骤 4.3: 放置所有文件

将之前打包好的 `ryl-miniprogram-server-0.0.1-SNAPSHOT.jar` 文件，也拷贝到 `C:\Services\RuiYiLin` 目录中。

此时，您的目录中应包含**三个文件**：
1.  `ryl-service.exe`
2.  `ryl-service.xml`
3.  `ryl-miniprogram-server-0.0.1-SNAPSHOT.jar`

#### 步骤 4.4: 安装与管理服务

1.  以 **管理员身份** 打开命令提示符 (CMD) 或 PowerShell。
2.  使用 `cd` 命令切换到您的服务目录：
    ```shell
    cd C:\Services\RuiYiLin
    ```
3.  执行以下命令来安装、启动、停止或卸载服务：

    ```shell
    # 安装服务
    .\ryl-service.exe install

    # 启动服务
    .\ryl-service.exe start

    # 停止服务
    .\ryl-service.exe stop

    # 重启服务
    .\ryl-service.exe restart

    # 查看服务状态
    .\ryl-service.exe status

    # 卸载服务
    .\ryl-service.exe uninstall
    ```
4.  您也可以通过 Windows 的图形化工具 `services.msc` 来查看和管理刚刚安装的 `RuiYiLin Miniprogram Service`。

## 5. 网络配置 (Firewall Configuration)

为了让外部用户能够访问您的服务，您需要在 Windows Server 的防火墙中开放服务所使用的端口（默认为 `8085`）。

1.  打开 "高级安全 Windows Defender 防火墙"。
2.  在左侧面板中，点击 "入站规则"。
3.  在右侧面板中，点击 "新建规则..."。
4.  **规则类型**: 选择 "端口"，点击 "下一步"。
5.  **协议和端口**: 选择 "TCP"，在 "特定本地端口" 中输入 `8085`，点击 "下一步"。
6.  **操作**: 选择 "允许连接"，点击 "下一步"。
7.  **配置文件**: 根据您的网络环境勾选（通常全选即可），点击 "下一步"。
8.  **名称**: 为规则命名，例如 `RuiYiLin Service Port (8085)`，然后点击 "完成"。

## 6. 生产环境加固 (Production Hardening)

#### 6.1. 外部化配置 (External Configuration)

在生产环境中，数据库密码等敏感信息不应打包在 JAR 文件内。Spring Boot 允许您将配置文件外置。

*   只需在 `C:\Services\RuiYiLin` 目录下（与 `.jar` 文件同级）创建一个名为 `application.properties` 的文件。
*   启动时，外部的配置文件会**自动覆盖** JAR 包内部的配置。
*   **生产环境 `application.properties` 示例**:
    ```properties
    # 生产环境服务器端口
    server.port=8085

    # 生产环境数据库连接
    spring.datasource.url=jdbc:sqlserver://your-prod-db-host:1433;databaseName=your-prod-db
    spring.datasource.username=prod_user
    spring.datasource.password=your_secure_password

    # 其他生产环境特定的配置...
    ```

#### 6.2. 使用 IIS 作为反向代理 (推荐)

直接将 `8085` 端口暴露于公网是不安全的。强烈建议使用 Windows Server 自带的 IIS 作为反向代理服务器。

*   **好处**: 可以使用标准的 `80 (http)` 和 `443 (https)` 端口对外提供服务，方便地配置 SSL 证书来启用 HTTPS，提高安全性。
*   **方法**:
    1.  在 IIS 中安装 **Application Request Routing (ARR)** 和 **URL Rewrite** 这两个官方扩展模块。
    2.  在 IIS 中创建一个新的网站，绑定您的域名（如 `api.example.com`）。
    3.  使用 URL Rewrite 规则，将所有传入该网站的请求，全部转发到在本地运行的服务 `http://localhost:8085`。

---
**文档结束**
--- 