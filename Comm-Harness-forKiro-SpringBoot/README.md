# 无人机信息管理系统

基于Spring Boot 2.2.x的无人机信息管理系统，支持无人机信息的增删改查功能。

## 技术栈

### 系统环境
- Java EE 8
- Servlet 3.0
- Apache Maven 3

### 主框架
- Spring Boot 2.2.x
- Spring Framework 5.2.x
- Apache Shiro 1.7
- Vue 3
- Vite

### 持久层
- Apache MyBatis 3.5.x
- Hibernate Validation 6.0.x
- Alibaba Druid 1.2.x

### 数据库
- MySQL（生产环境）
- SQLite（开发环境，可选）

## 功能特性

- ✅ 无人机信息录入
- ✅ 无人机信息查询（支持模糊查询和条件过滤）
- ✅ 无人机信息修改
- ✅ 无人机信息删除
- ✅ 分页显示
- ✅ 请求拦截和日志记录

## 快速开始

### 1. 环境要求

- JDK 8+
- Maven 3.6+
- Node.js 18+
- MySQL 5.7+ 或 8.0+

### 2. 数据库初始化

```bash
# 使用MySQL客户端执行初始化脚本
mysql -u root -p < init-database.sql
```

或者手动执行：
1. 创建数据库：`drone_management`
2. 执行`init-database.sql`中的建表和初始数据脚本

### 3. 配置数据库连接

编辑 `src/main/resources/application-prod.yml`：

```yaml
database:
  mysql:
    url: jdbc:mysql://localhost:3306/drone_management?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 4. 启动应用

**方式一：使用启动脚本（Windows）**
```bash
startup.bat
```

**方式二：使用Maven命令**
```bash
# 编译打包
mvn clean package -DskipTests

# 启动应用（MySQL）
java -jar target/drone-management-system-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod

# 启动前端
cd frontend
npm install
npm run dev
```

### 5. 访问应用

打开浏览器访问：http://localhost:5173/drones

后端 REST API 地址：http://localhost:8080/api/v1/drones

## 项目结构

```
src/main/java/com/example/drone/
├── config/              # 配置类（数据库、Shiro、Web）
├── controller/          # 控制器层
├── domain/              # 领域模型
│   ├── dto/            # 数据传输对象
│   ├── entity/         # 实体类
│   └── enums/          # 枚举类
├── exception/           # 异常处理
├── interceptor/         # 拦截器
├── repository/          # 数据访问层
│   ├── adapter/        # 数据库适配器
│   └── DroneRepository # Repository接口
├── security/            # 安全相关
└── service/             # 业务逻辑层
    └── impl/           # 业务实现

src/main/resources/
├── mapper/              # MyBatis映射文件
│   ├── mysql/          # MySQL映射
│   └── sqlite/         # SQLite映射
└── application-*.yml    # 配置文件

frontend/
├── src/                 # Vue前端源码
├── index.html           # Vite入口
└── package.json         # 前端依赖和脚本
```

## 数据库切换

系统支持MySQL和SQLite两种数据库，通过Spring Profile切换：

**使用MySQL（推荐）：**
```bash
java -jar target/drone-management-system-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
```

**使用SQLite（开发环境）：**
```bash
java -jar target/drone-management-system-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
```

## API文档

详见：`harness-collab/04-api-docs/drone-api.md`

## 开发规范

详见：`harness-collab/05-methodology/`

## 测试

```bash
# 运行所有测试
mvn test

# 运行指定测试类
mvn test -Dtest=DroneServiceImplTest
```

## 许可证

Copyright © 2024 无人机信息管理系统. All rights reserved.
