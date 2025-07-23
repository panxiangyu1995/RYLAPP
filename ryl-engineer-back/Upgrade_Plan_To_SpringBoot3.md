# 后端升级最终实施规划：`ryl-engineer-back` 至 Spring Boot 3.0

## 目标
将 `ryl-engineer-back` 项目从 Spring Boot 2.7.18 升级到 3.0.13，并确保所有功能，特别是认证和API文档，都能完美工作。

---

## 核心洞察 (基于详细研究)

*   **持久层**: MyBatis-Plus，无JPA注解。**升级时无需担心 `javax.persistence` 迁移。**
*   **安全层**: 自定义JWT拦截器，轻度使用Spring Security。**升级时主要工作是迁移拦截器中的 `javax.servlet` 到 `jakarta.servlet`，并更新 `SecurityConfig` 以符合新版API（`requestMatchers` 替代 `antMatchers`）。**
*   **Web配置层**: 使用 `WebMvcConfigurer` 注册拦截器和配置CORS。**升级时需要将整个类中的 `javax.servlet` 迁移到 `jakarta.servlet`。**
*   **API文档**: 纯 `springdoc-openapi` 配置，无旧注解。**升级时只需更新 `pom.xml` 中的依赖版本即可。**
*   **核心依赖**: `io.jsonwebtoken:jjwt:0.9.1` 版本过旧，强依赖 `javax.xml.bind`，在Java 17+ 和 Spring Boot 3+ 环境下极有可能出错。**升级时必须替换为现代的 `jjwt-api` 和 `jjwt-impl` 依赖。**

---

## 实施清单

### 步骤一：手动准备 `pom.xml`

*   **文件**: `ryl-engineer-back/pom.xml`
*   **动作**:
    a.  **升级 `jjwt` 依赖**：这是自动化工具无法完美处理的。删除旧的 `jjwt` 依赖，并替换为与 `Jakarta` 兼容的新版依赖：
        ```xml
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.11.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        ```
    b.  **添加 `OpenRewrite` 插件**：添加用于执行自动迁移的 `rewrite-maven-plugin`，并将其配置为使用 `UpgradeSpringBoot_3_0` 配方。

### 步骤二：执行自动化迁移

*   **动作**: 在 `ryl-engineer-back` 目录的终端中，运行 `mvn rewrite:run`。这个命令将自动完成绝大部分的迁移工作，包括：
    *   升级 `pom.xml` 中的 `spring-boot-starter-parent` 到 `3.0.13`。
    *   升级 `springdoc-openapi-ui` 到兼容版本 (如 `2.1.0`)。
    *   将项目中所有 `javax.servlet.*` 的 `import` 更改为 `jakarta.servlet.*`。
    *   将 `SecurityConfig.java` 中的 `antMatchers` 自动重构为 `requestMatchers`。

### 步骤三：手动验证与清理

*   **动作**:
    a.  **验证 `JwtUtil.java`**: 检查此类，确保 `io.jsonwebtoken` 的 `import` 语句已更新，并且没有编译错误。
    b.  **验证 `WebMvcConfig.java`**: 确认所有 `javax.servlet.*` 已变为 `jakarta.servlet.*`。
    c.  **验证 `SecurityConfig.java`**: 确认 `antMatchers` 已变为 `requestMatchers`。
    d.  **移除插件**: 确认一切正常后，从 `pom.xml` 中移除 `rewrite-maven-plugin`。 