# 无人机信息管理系统需求文档

> **文档状态**：草稿  
> **创建时间**：2024-01-15  
> **最后更新**：2024-01-15  
> **负责人**：开发团队

---

## 功能背景

随着无人机技术的快速发展和应用场景的不断扩展，企业和组织需要对其拥有的无人机资产进行系统化管理。当前缺乏统一的信息管理平台，导致无人机信息分散、查询困难、维护记录不完整等问题。

**当前痛点**：
- 无人机信息分散在 Excel 表格或纸质记录中，查询效率低
- 缺乏统一的数据标准，不同部门记录的信息格式不一致
- 无法快速统计无人机资产状况和使用情况
- 信息更新不及时，导致管理决策缺乏准确数据支持

**预期收益**：
- 建立统一的无人机信息数据库，实现信息集中管理
- 提供便捷的 Web 界面，支持快速录入、查询、修改和删除操作
- 支持多种数据库后端（SQLite/MySQL），满足不同规模部署需求
- 为后续扩展功能（如飞行记录管理、维护计划等）奠定数据基础

---

## 术语表

- **Drone_Management_System**：无人机信息管理系统，本文档描述的核心系统
- **Database_Adapter**：数据库适配器，负责在 SQLite 和 MySQL 之间切换的组件
- **Request_Interceptor**：请求拦截器，负责拦截 HTTP 请求并记录日志的组件
- **Drone_Entity**：无人机实体，表示系统中管理的无人机对象
- **Configuration_Manager**：配置管理器，负责读取和管理系统配置的组件

---

## 需求

### 需求 1：无人机信息录入

**用户故事**：作为系统管理员，我希望能够录入新的无人机信息，以便将新采购或新接收的无人机纳入管理系统。

#### 验收标准

1. WHEN 管理员提交包含完整无人机信息的创建请求，THE Drone_Management_System SHALL 验证所有必填字段并将无人机信息保存到数据库
2. WHEN 管理员提交的无人机信息包含重复的序列号，THE Drone_Management_System SHALL 返回错误提示"序列号已存在"
3. WHEN 管理员提交的无人机信息缺少必填字段，THE Drone_Management_System SHALL 返回具体的字段验证错误信息
4. THE Drone_Management_System SHALL 为每个新录入的无人机自动生成唯一标识符
5. WHEN 无人机信息成功保存后，THE Drone_Management_System SHALL 返回包含无人机 ID 的成功响应

**无人机属性模型**：
- 序列号（必填，唯一）：无人机制造商提供的唯一序列号
- 型号名称（必填）：无人机型号，如 DJI Mavic 3、Phantom 4 Pro
- 制造商（必填）：制造商名称，如 DJI、Parrot
- 购买日期（必填）：无人机采购日期
- 状态（必填）：可用、维修中、已报废
- 最大飞行时间（必填）：单次充电最大飞行时长（分钟）
- 最大飞行距离（必填）：最大飞行距离（米）
- 重量（必填）：无人机重量（克）
- 备注（可选）：其他补充信息

### 需求 2：无人机信息查询

**用户故事**：作为系统用户，我希望能够查询无人机信息，以便快速了解无人机的详细情况和当前状态。

#### 验收标准

1. WHEN 用户请求查询指定 ID 的无人机信息，THE Drone_Management_System SHALL 返回该无人机的完整信息
2. WHEN 用户请求查询不存在的无人机 ID，THE Drone_Management_System SHALL 返回错误提示"无人机不存在"
3. WHEN 用户请求查询所有无人机列表，THE Drone_Management_System SHALL 返回所有无人机的基本信息列表
4. WHERE 用户指定分页参数，THE Drone_Management_System SHALL 返回指定页码和每页数量的无人机列表
5. WHERE 用户指定查询条件（型号、制造商、状态），THE Drone_Management_System SHALL 返回符合条件的无人机列表
6. THE Drone_Management_System SHALL 在 200 毫秒内完成单条记录查询（P95 性能要求）

### 需求 3：无人机信息修改

**用户故事**：作为系统管理员，我希望能够修改无人机信息，以便更新无人机的状态或纠正错误的信息。

#### 验收标准

1. WHEN 管理员提交包含更新字段的修改请求，THE Drone_Management_System SHALL 验证字段合法性并更新数据库中的无人机信息
2. WHEN 管理员尝试修改不存在的无人机 ID，THE Drone_Management_System SHALL 返回错误提示"无人机不存在"
3. WHEN 管理员尝试将序列号修改为已存在的序列号，THE Drone_Management_System SHALL 返回错误提示"序列号已存在"
4. THE Drone_Management_System SHALL 保留无人机的创建时间和唯一标识符不被修改
5. WHEN 无人机信息成功更新后，THE Drone_Management_System SHALL 返回更新后的完整无人机信息

### 需求 4：无人机信息删除

**用户故事**：作为系统管理员，我希望能够删除无人机信息，以便清理已报废或不再管理的无人机记录。

#### 验收标准

1. WHEN 管理员请求删除指定 ID 的无人机，THE Drone_Management_System SHALL 从数据库中永久删除该无人机记录
2. WHEN 管理员尝试删除不存在的无人机 ID，THE Drone_Management_System SHALL 返回错误提示"无人机不存在"
3. WHEN 无人机信息成功删除后，THE Drone_Management_System SHALL 返回删除成功的确认响应
4. WHEN 无人机被删除后，THE Drone_Management_System SHALL 确保后续查询该 ID 返回"无人机不存在"错误

### 需求 5：数据库切换支持

**用户故事**：作为系统部署人员，我希望能够在 SQLite 和 MySQL 之间自由切换数据库，以便根据部署规模和环境选择合适的数据库方案。

#### 验收标准

1. WHERE 配置文件指定使用 SQLite，THE Database_Adapter SHALL 使用 SQLite 数据库连接
2. WHERE 配置文件指定使用 MySQL，THE Database_Adapter SHALL 使用 MySQL 数据库连接
3. THE Database_Adapter SHALL 确保在不同数据库之间切换时，所有 CRUD 操作的行为保持一致
4. WHEN 系统启动时，THE Configuration_Manager SHALL 读取数据库配置并初始化对应的数据源
5. IF 数据库连接失败，THEN THE Drone_Management_System SHALL 记录详细错误日志并返回明确的错误提示

### 需求 6：请求拦截与日志记录

**用户故事**：作为系统运维人员，我希望系统能够自动记录所有 HTTP 请求的详细信息，以便进行问题排查和审计追踪。

#### 验收标准

1. WHEN 任何 HTTP 请求到达系统时，THE Request_Interceptor SHALL 拦截该请求
2. THE Request_Interceptor SHALL 记录请求的 HTTP 方法、请求路径、请求参数和请求时间
3. THE Request_Interceptor SHALL 记录请求的客户端 IP 地址和 User-Agent 信息
4. THE Request_Interceptor SHALL 在请求处理完成后记录响应状态码和处理耗时
5. THE Request_Interceptor SHALL 将日志信息输出到标准日志系统（支持按日期滚动）
6. THE Request_Interceptor SHALL 在 5 毫秒内完成日志记录操作，不影响请求处理性能

### 需求 7：架构分层约束

**用户故事**：作为系统架构师，我希望系统严格遵循四层架构设计，以便保证代码的可维护性和可扩展性。

#### 验收标准

1. THE Drone_Management_System SHALL 将所有 REST 控制器放置在 controller 包中
2. THE Drone_Management_System SHALL 将所有业务逻辑实现放置在 service 包中，并提供接口和实现分离
3. THE Drone_Management_System SHALL 将所有领域模型和 DTO 放置在 domain 包中
4. THE Drone_Management_System SHALL 将所有数据访问接口放置在 repository 包中，并提供接口和实现分离
5. THE Drone_Management_System SHALL 将所有拦截器放置在独立的 interceptor 包中
6. THE Drone_Management_System SHALL 确保 controller 层不直接依赖 repository 层
7. THE Drone_Management_System SHALL 确保 domain 层不包含任何 Spring 框架注解（除 JPA 和校验注解外）

---

## 非功能性需求

### 性能要求

- **响应时间**：
  - 单条记录查询：P95 ≤ 200ms，P99 ≤ 500ms
  - 列表查询（分页 20 条）：P95 ≤ 500ms，P99 ≤ 1000ms
  - 创建/更新/删除操作：P95 ≤ 300ms，P99 ≤ 800ms
- **并发量**：支持 50 并发用户同时操作
- **数据量**：支持单表 10 万条记录的查询性能

### 安全要求

- **认证**：所有 API 接口需要通过 Apache Shiro 进行身份认证
- **授权**：创建、修改、删除操作仅限管理员角色访问，查询操作允许普通用户访问
- **输入验证**：所有用户输入必须进行参数校验，使用 Hibernate Validation 注解
- **SQL 注入防护**：使用 MyBatis 参数化查询，禁止拼接 SQL 字符串
- **XSS 防护**：所有输出到前端的数据必须进行 HTML 转义

### 兼容性要求

- **浏览器兼容性**：支持 Chrome 90+、Firefox 88+、Edge 90+
- **数据库兼容**：支持 SQLite 3.30+ 和 MySQL 8.0+
- **Java 版本**：Java 8（Java EE 8）
- **Spring Boot 版本**：Spring Boot 2.2.x

### 可维护性要求

- **代码覆盖率**：单元测试覆盖率 ≥ 80%
- **代码规范**：通过 Checkstyle 严格模式检查
- **文档完整性**：所有公共方法必须包含 Javadoc 注释
- **日志规范**：使用 SLF4J + Logback，日志级别分为 DEBUG、INFO、WARN、ERROR

### 可用性要求

- **错误提示**：所有错误响应必须包含明确的错误代码和中文错误描述
- **数据校验反馈**：字段验证失败时，返回具体的字段名称和验证规则说明
- **操作确认**：删除操作前端需要二次确认

---

## 排除范围

本次迭代**不包含**以下内容：

- 用户管理功能（用户注册、登录、权限管理）
- 飞行记录管理功能
- 维护计划和维护记录管理
- 无人机实时位置追踪
- 移动端应用（仅支持 Web 端）
- 数据导入导出功能（Excel/CSV）
- 多租户支持
- 国际化支持（仅支持中文）

**后续迭代计划**：
- 第二迭代：增加飞行记录管理功能
- 第三迭代：增加维护计划和提醒功能
- 第四迭代：增加数据统计和报表功能

---

## 依赖与前置条件

| 依赖项 | 类型 | 说明 | 状态 |
|--------|------|------|------|
| Spring Boot 2.2.x | 基础框架 | 核心应用框架 | 已就绪 |
| Apache Shiro 1.7 | 安全框架 | 身份认证和授权 | 已就绪 |
| MyBatis 3.5.x | 持久层框架 | 数据库访问 | 已就绪 |
| Druid 1.2.x | 数据源 | 数据库连接池 | 已就绪 |
| Thymeleaf 3.0.x | 视图层 | 前端模板引擎 | 已就绪 |
| Bootstrap 3.3.7 | UI 框架 | 前端样式框架 | 已就绪 |
| SQLite JDBC Driver | 数据库驱动 | SQLite 数据库连接 | 需配置 |
| MySQL Connector | 数据库驱动 | MySQL 数据库连接 | 需配置 |

---

## 变更记录

| 版本 | 日期 | 变更内容 | 变更人 |
|------|------|----------|--------|
| v1.0 | 2024-01-15 | 初始版本 | 开发团队 |
