# 智慧医疗系统

<div align="center">
<b>一个现代化的全栈智慧医疗健康管理平台</b>

![LICENSE](https://img.shields.io/badge/License-MIT-yellow.svg)

</div>

---


## 📖 项目简介

智慧医疗系统是一个全栈医疗健康服务平台，整合了疾病查询、药品查询、AI 智能问诊等功能。(毕设别骂)

### 核心功能

- 🔍 **疾病查询** - 按分类、症状搜索疾病信息
- 💊 **药品查询** - 查询药品功效、用法、禁忌
- 🤖 **AI 智能问诊** - 基于阿里云通义千问的智能医疗咨询
- 📹 **健康科普** - 科普视频观看
- 👤 **个人中心** - 用户资料、浏览历史管理
- 🔐 **管理后台** - 疾病、药品、视频数据管理


## 🏗️ 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.6 | 基础框架 |
| Spring Security | - | 安全认证 |
| Spring AI | 1.0.0.2 | AI 集成 |
| MyBatis Plus | 3.5.12 | ORM 框架 |
| MySQL | 8.0+ | 关系数据库 |
| Redis | 6.0+ | 缓存数据库 |
| Knife4j | 4.6.0 | API 文档 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| React | 18.3.1 | UI 框架 |
| TypeScript | 5.9.3 | 类型系统 |
| Vite | 5.4.8 | 构建工具 |
| Ant Design | 5.18.4 | UI 组件库 |
| Axios | 1.7.7 | HTTP 客户端 |

### Android 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Kotlin | 1.9.22 | 开发语言 |
| Jetpack Compose | - | UI 框架 |
| Hilt | - | 依赖注入 |
| Room | 2.6.1 | 本地数据库 |
| Retrofit | 2.9.0 | 网络请求 |


## 🚀 快速开始

### 环境要求

| 组件 | 要求 |
|------|------|
| JDK | 17+ |
| Node.js | 18+ |
| MySQL | 8.0+ |
| Redis | 6.0+ |
| Android Studio | Hedgehog (2023.1.1)+ |

### 后端启动

```bash
cd src
mvn spring-boot:run
```

**服务地址**: http://localhost:8080
**API文档**: http://localhost:8080/doc.html

### 前端启动

```bash
cd smart-medicine-web
npm install
npm run dev
```

**开发服务器**: http://localhost:5173

### Android 构建

```bash
cd smart-medicine-Android
./gradlew assembleDebug
./gradlew installDebug
```

详细说明请参考：[smart-medicine-Android/README.md](smart-medicine-Android/README.md)


## 📝 常用命令参考

### 后端 (Maven)

| 命令 | 说明 |
|------|------|
| `mvn spring-boot:run` | 启动开发服务器 |
| `mvn clean package` | 清理并构建 JAR |
| `mvn test` | 运行单元测试 |
| `mvn clean` | 清理构建缓存 |

### 前端 (NPM)

| 命令 | 说明 |
|------|------|
| `npm run dev` | 启动开发服务器 |
| `npm run build` | 构建生产版本 |
| `npm run preview` | 预览生产构建 |

### Android (Gradle)

| 命令 | 说明 |
|------|------|
| `./gradlew assembleDebug` | 构建调试版本APK |
| `./gradlew assembleRelease` | 构建发布版本APK |
| `./gradlew installDebug` | 安装到设备 |
| `./gradlew clean` | 清理构建缓存 |
| `./gradlew test` | 运行单元测试 |


## 📁 项目结构

```
smart-medicine/
├── src/                    # Spring Boot 后端
├── smart-medicine-web/     # React 前端
├── smart-medicine-Android/ # Android 应用
└── docs/                   # 项目文档
```


## ⚙️ 配置说明

### 后端配置

配置文件: `src/main/resources/application.yml`

**需要配置的关键参数：**
- 数据库连接信息
- Redis 连接信息
- 阿里云通义千问 API Key
- JWT 密钥

### 前端配置

环境变量: `smart-medicine-web/.env`
```env
VITE_API_BASE=http://localhost:8080
```


## 🔗 API 文档

启动后端服务后访问：
- **Knife4j UI**: http://localhost:8080/doc.html

主要端点：

| 功能 | 端点 | 方法 |
|------|------|------|
| 用户登录 | `/api/v1/auth/login` | POST |
| 疾病搜索 | `/api/v1/illnesses` | GET |
| 药品搜索 | `/api/v1/medicines` | GET |
| AI 问诊 | `/api/v1/ai-chat/stream` | POST |


## 🧪 测试

### 后端测试

```bash
cd src
mvn test
```

### 前端测试

```bash
cd smart-medicine-web
npm run build
```


## 📦 部署

### 后端部署

```bash
cd src
mvn clean package
java -jar target/smart-medicine-1.0.0.jar
```

### 前端部署

```bash
cd smart-medicine-web
npm run build
```


## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

### 提交规范

- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `refactor`: 代码重构

详细指南请参考：[贡献指南.md](docs/贡献指南.md)


## 📄 许可证

本项目采用 [MIT 许可证](LICENSE)


## 👥 作者

zzy


## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot)
- [React](https://react.dev/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Ant Design](https://ant.design/)
- [阿里云通义千问](https://tongyi.aliyun.com/)
- **学长: zxz**
- **老师: lx**


<div align="center">
<b>⭐ 如果觉得这个项目有帮助，请点个 Star ⭐</b>

</div>
