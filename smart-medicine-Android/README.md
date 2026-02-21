# Smart Medicine Android

智慧医疗系统 Android 客户端

## 技术栈

- **Kotlin** 1.9.22
- **Jetpack Compose** - 现代化声明式UI
- **Hilt** - 依赖注入
- **Room** 2.6.1 - 本地数据库
- **Retrofit** 2.9.0 - 网络请求
- **Coil** - 图片加载
- **DataStore** - 数据持久化

## 系统要求

- **JDK** 17
- **Android Studio** Hedgehog (2023.1.1) 或更高版本
- **Min SDK** 24 (Android 7.0)
- **Target SDK** 34 (Android 14)

## 项目结构

```
smart-medicine-Android/
├── app/src/main/java/com/example/smart_medicine_android/
│   ├── data/                    # 数据层
│   │   ├── local/              # 本地数据源
│   │   │   ├── entity/         # 数据库实体
│   │   │   ├── dao/            # 数据访问对象
│   │   │   └── datastore/      # DataStore
│   │   ├── network/            # 网络层
│   │   │   ├── api/            # API接口定义
│   │   │   ├── model/          # 数据模型
│   │   │   └── ApiConfig.kt    # API配置
│   │   └── repository/         # 仓库层
│   ├── di/                     # 依赖注入
│   ├── ui/                     # UI层
│   │   ├── screen/             # 页面
│   │   ├── widget/             # 自定义组件
│   │   └── theme/              # 主题配置
│   └── util/                   # 工具类
├── app/build.gradle.kts        # 应用模块配置
├── build.gradle.kts            # 项目配置
└── settings.gradle.kts         # Gradle设置
```

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd smart-medicine/smart-medicine-Android
```

### 2. 配置后端地址

在 `app/src/main/java/com/example/smart_medicine_android/data/network/ApiConfig.kt` 中配置后端API地址：

```kotlin
fun getBaseUrl(): String {
    // 开发环境
    return "http://10.0.2.2:8080"  // 模拟器默认IP
    // 或使用 "http://localhost:8080"  // 真机
}
```

### 3. 构建项目

```bash
./gradlew assembleDebug
```

### 4. 运行应用

**方式一：使用 Android Studio**
1. 打开 Android Studio
2. 选择 `Run > Run app` 或按 Shift+F10

**方式二：使用命令行**
```bash
# 安装到连接的设备/模拟器
./gradlew installDebug

# 或直接运行
./gradlew assembleDebug && adb install app/build/outputs/apk/debug/app-debug.apk
```

## Gradle 脚本

| 脚本 | 说明 |
|------|------|
| `./gradlew assembleDebug` | 构建调试版本APK |
| `./gradlew assembleRelease` | 构建发布版本APK |
| `./gradlew installDebug` | 安装调试版本到设备 |
| `./gradlew clean` | 清理构建缓存 |
| `./gradlew test` | 运行单元测试 |
| `./gradlew connectedAndroidTest` | 运行连接测试 |

## 主要功能

### 首页
- 搜索疾病、药品、资讯
- 热门疾病推荐
- 常用药品推荐
- 健康资讯轮播

### 用户功能
- 注册/登录
- 个人资料管理
- 浏览历史记录

### 疾病
- 疾病搜索
- 疾病详情查看
- 相关药品推荐

### 药品
- 药品搜索
- 药品详情查看
- 按疾病查找药品

### 科普视频
- 视频列表
- 视频详情播放
- 分类浏览

### 健康资讯
- 资讯轮播
- 资讯列表
- Markdown内容渲染
- 资讯详情浏览

### AI 智能咨询
- 对话式健康咨询
- 流式响应显示
- 历史记录管理

### 管理后台
- 用户管理
- 药品管理
- 疾病管理
- 视频管理
- 资讯管理

## 开发规范

### 代码风格
- 遵循 Kotlin 官方编码规范
- 使用 4 空格缩进
- 函数不超过 50 行
- 文件不超过 800 行

### 命名规范
- **类名**: PascalCase (例: `HomeScreen`)
- **函数名**: camelCase (例: `loadNews`)
- **变量名**: camelCase (例: `userName`)
- **常量名**: UPPER_SNAKE_CASE (例: `MAX_ITEMS`)

### 注释规范
- 公共API必须有 KDoc 注释
- 复杂逻辑必须添加行内注释
- TODO 标记待完成功能

## 测试

### 运行测试

```bash
# 单元测试
./gradlew test

# 连接测试（需要设备/模拟器）
./gradlew connectedAndroidTest

# 生成测试报告
./gradlew testDebugUnitTest --continue
```

### 测试覆盖率目标
- 单元测试覆盖率: 80%+
- 关键业务流程必须有连接测试

## 构建变体

| 变体 | 说明 | 包名 |
|------|------|------|
| `debug` | 调试版本 | `com.example.smart_medicine_android.debug` |
| `release` | 发布版本 | `com.example.smart_medicine_android` |

## 故障排除

### 模拟器无法连接后端
- 使用 `10.0.2.2` 代替 `localhost`
- 确保后端服务正在运行
- 检查防火墙设置

### 构建失败
- 清理项目: `./gradlew clean`
- 重新下载依赖: `./gradlew build --refresh-dependencies`
- 检查 JDK 版本是否为 17

### 依赖下载缓慢
- 配置阿里云镜像（settings.gradle.kts）
- 使用代理

## 许可证

MIT License
