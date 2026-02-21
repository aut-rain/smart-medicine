# 智慧医疗前端应用

基于 React + TypeScript + Vite + Ant Design 开发的智慧医疗前端应用。

## 🚀 快速开始

### 安装依赖
```bash
npm install
```

### 启动开发服务器
```bash
npm run dev
```

访问 http://localhost:5173

### 构建生产版本
```bash
npm run build
```

## ⚙️ 环境配置

创建 `.env` 文件（参考 `.env.example`）：

```env
# API 基础地址（可选，默认 http://localhost:8080）
VITE_API_BASE=http://localhost:8080
```

## 📦 功能模块

- ✅ 用户认证（登录/注册/登出）
- ✅ 首页（热门疾病、常用药品）
- ✅ 疾病搜索与详情
- ✅ 药品搜索与详情
- ✅ AI 智能问诊（流式对话）
- ✅ 个人中心（资料修改、密码修改、浏览历史）
- ✅ 意见反馈

## 🛠 技术栈

- React 18
- TypeScript
- Vite
- Ant Design 5
- React Router 6
- Axios
- React Markdown
- Day.js

## 📝 注意事项

1. 确保后端服务已启动（默认 http://localhost:8080）
2. 首次使用需要先注册账号
3. AI 问诊需要登录后使用
