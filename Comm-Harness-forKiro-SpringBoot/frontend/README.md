# 无人机管理前端

独立 Vue 3 + Vite 前端应用，通过后端 REST API 访问无人机管理能力。

## 开发启动

```bash
npm install
npm run dev
```

默认访问地址：

```text
http://localhost:5173
```

开发环境通过 Vite proxy 将 `/api` 转发到：

```text
http://localhost:8080
```

## 构建

```bash
npm run build
```

构建产物输出到 `dist/`。

## 页面路由

```text
/drones
/drones/new
/drones/:id
/drones/:id/edit
```
