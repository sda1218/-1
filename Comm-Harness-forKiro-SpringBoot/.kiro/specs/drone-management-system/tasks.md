# 无人机信息管理系统实施任务清单

> **关联需求**：[requirements.md](./requirements.md)  
> **关联设计**：[design.md](./design.md)  
> **创建时间**：2024-01-15  
> **实施语言**：Java (Spring Boot 2.2.x)

---

## 概述

本任务清单基于无人机信息管理系统的需求和设计文档，采用 Spring Boot 2.2.x + MyBatis 3.5.x 技术栈，严格遵循四层架构约束（Controller → Service → Domain → Repository），实现无人机信息的增删改查功能，支持 SQLite 和 MySQL 数据库切换。

---

## 任务列表

- [ ] 1. 项目初始化和基础配置
  - 创建 Spring Boot 项目结构（基于 Maven）
  - 配置 pom.xml 依赖（Spring Boot 2.2.x、MyBatis 3.5.x、Shiro 1.7、Druid 1.2.x、SQLite JDBC、MySQL Connector）
  - 创建多环境配置文件（application.yml、application-dev.yml、application-prod.yml）
  - 配置日志框架（SLF4J + Logback，按日期和大小滚动）
  - 创建标准包结构（controller、service、domain、repository、config、common、exception、interceptor）
  - 为每个包创建 package-info.java 文件
  - _需求: 需求 5（数据库切换支持）、需求 7（架构分层约束）_

- [ ] 2. 数据库适配器实现
  - [ ] 2.1 创建数据库适配器接口和实现类
    - 创建 DatabaseAdapter 接口（定义 getDataSource() 和 getDatabaseType() 方法）
    - 实现 SQLiteAdapter（配置 SQLite 数据源）
    - 实现 MySQLAdapter（配置 Druid 连接池）
    - 创建 DatabaseConfig 配置类（根据 application.yml 中的 database.type 选择适配器）
    - _需求: 需求 5.1, 5.2, 5.3, 5.4_

  - [ ]* 2.2 编写数据库适配器单元测试
    - 测试 SQLite 数据源连接成功
    - 测试 MySQL 数据源连接成功
    - 测试配置切换时使用正确的适配器
    - _需求: 需求 5.3_

- [ ] 3. 领域模型和 DTO 实现
  - [ ] 3.1 创建领域实体和枚举
    - 创建 Drone 实体类（包含所有字段、Lombok 注解、Javadoc）
    - 创建 DroneStatus 枚举（AVAILABLE、UNDER_MAINTENANCE、SCRAPPED）
    - _需求: 需求 1（无人机属性模型）_

  - [ ] 3.2 创建 DTO 类
    - 创建 DroneDTO（响应 DTO，包含格式化的日期和状态描述）
    - 创建 CreateDroneRequest（包含 Hibernate Validation 注解）
    - 创建 UpdateDroneRequest（所有字段可选）
    - 创建 DroneQueryRequest（分页和查询条件）
    - 创建 ApiResponse 统一响应格式类
    - 创建 ErrorResponse 错误响应格式类
    - _需求: 需求 1.3, 需求 2.4, 需求 3.1_

- [ ] 4. 数据访问层实现
  - [ ] 4.1 创建数据库表结构
    - 编写 SQLite 建表 SQL（schema-sqlite.sql）
    - 编写 MySQL 建表 SQL（schema-mysql.sql）
    - 配置数据库初始化脚本自动执行
    - _需求: 需求 1.1_

  - [ ] 4.2 实现 MyBatis Repository
    - 创建 DroneRepository 接口（@Mapper 注解）
    - 定义 CRUD 方法（insert、selectById、selectBySerialNumber、selectByConditions、countByConditions、updateById、deleteById）
    - 编写 SQLite 版本 Mapper XML（mapper/sqlite/DroneMapper.xml）
    - 编写 MySQL 版本 Mapper XML（mapper/mysql/DroneMapper.xml）
    - 配置 MyBatis（类型别名、驼峰命名转换）
    - _需求: 需求 1.1, 需求 2.1, 需求 3.1, 需求 4.1_

  - [ ]* 4.3 编写 Repository 层集成测试
    - 测试 insert 方法（插入成功返回受影响行数）
    - 测试 selectById 方法（查询存在和不存在的记录）
    - 测试 selectBySerialNumber 方法（唯一性查询）
    - 测试 selectByConditions 方法（条件查询和分页）
    - 测试 updateById 方法（更新成功）
    - 测试 deleteById 方法（删除成功）
    - 使用 H2 内存数据库进行测试
    - _需求: 需求 1.1, 需求 2.1, 需求 3.1, 需求 4.1_

- [ ] 5. 业务逻辑层实现
  - [ ] 5.1 创建业务异常类
    - 创建 DroneBusinessException 基类
    - 创建 DroneNotFoundException（404 错误）
    - 创建 DuplicateSerialNumberException（400 错误）
    - 创建 InvalidParameterException（400 错误）
    - 创建 DatabaseConnectionException（500 错误）
    - _需求: 需求 1.2, 需求 2.2, 需求 3.2, 需求 4.2, 需求 5.5_

  - [ ] 5.2 实现 DroneService 接口和实现类
    - 创建 DroneService 接口（定义 5 个业务方法）
    - 实现 DroneServiceImpl（使用构造器注入 DroneRepository）
    - 实现 createDrone 方法（校验序列号唯一性、保存数据、返回 DTO）
    - 实现 getDroneById 方法（查询数据、转换为 DTO）
    - 实现 listDrones 方法（条件查询、分页、转换为 DTO 列表）
    - 实现 updateDrone 方法（校验存在性、校验序列号唯一性、更新数据）
    - 实现 deleteDrone 方法（校验存在性、删除数据）
    - 实现实体与 DTO 转换方法
    - 添加 @Transactional 注解（写操作使用默认传播行为，读操作使用 readOnly=true）
    - _需求: 需求 1.1, 需求 1.2, 需求 2.1, 需求 3.1, 需求 4.1_

  - [ ]* 5.3 编写 Service 层单元测试
    - 测试 createDrone 成功场景（返回包含 ID 的 DTO）
    - 测试 createDrone 序列号重复场景（抛出 DuplicateSerialNumberException）
    - 测试 getDroneById 成功场景（返回正确的 DTO）
    - 测试 getDroneById 不存在场景（抛出 DroneNotFoundException）
    - 测试 updateDrone 成功场景（返回更新后的 DTO）
    - 测试 updateDrone 不存在场景（抛出 DroneNotFoundException）
    - 测试 updateDrone 序列号重复场景（抛出 DuplicateSerialNumberException）
    - 测试 deleteDrone 成功场景（无异常）
    - 测试 deleteDrone 不存在场景（抛出 DroneNotFoundException）
    - 测试 listDrones 带条件查询场景（返回符合条件的分页结果）
    - 测试 listDrones 空结果场景（返回空列表）
    - 使用 Mockito 模拟 DroneRepository
    - _需求: 需求 1.1, 需求 1.2, 需求 2.1, 需求 2.2, 需求 3.1, 需求 3.2, 需求 4.1, 需求 4.2_

- [ ] 6. Checkpoint - 确保核心业务逻辑测试通过
  - 确保所有测试通过，询问用户是否有问题

- [ ] 7. 控制器层实现
  - [ ] 7.1 创建全局异常处理器
    - 创建 GlobalExceptionHandler（@RestControllerAdvice 注解）
    - 处理 DroneNotFoundException（返回 404 和错误信息）
    - 处理 DuplicateSerialNumberException（返回 400 和错误信息）
    - 处理 MethodArgumentNotValidException（返回 400 和字段验证错误）
    - 处理 UnauthorizedException（返回 401）
    - 处理 ForbiddenException（返回 403）
    - 处理 Exception（返回 500 和通用错误信息，记录详细日志）
    - _需求: 需求 1.2, 需求 1.3, 需求 2.2, 需求 3.2, 需求 4.2_

  - [ ] 7.2 实现 DroneController
    - 创建 DroneController（@RestController 注解，基础路径 /api/v1/drones）
    - 使用构造器注入 DroneService
    - 实现 POST /api/v1/drones（创建无人机，@Valid 校验，返回 201）
    - 实现 GET /api/v1/drones/{id}（查询单个无人机，返回 200）
    - 实现 GET /api/v1/drones（分页查询列表，返回 200）
    - 实现 PUT /api/v1/drones/{id}（更新无人机，@Valid 校验，返回 200）
    - 实现 DELETE /api/v1/drones/{id}（删除无人机，返回 200）
    - 所有方法包含完整 Javadoc 注释
    - _需求: 需求 1.1, 需求 1.5, 需求 2.1, 需求 3.1, 需求 4.1_

  - [ ]* 7.3 编写 Controller 层切片测试
    - 使用 @WebMvcTest 和 MockMvc
    - 测试 POST /api/v1/drones 有效请求（返回 201 和正确响应体）
    - 测试 POST /api/v1/drones 参数校验失败（返回 400 和字段错误）
    - 测试 POST /api/v1/drones 缺少必填字段（返回 400）
    - 测试 GET /api/v1/drones/{id} 成功场景（返回 200 和正确响应体）
    - 测试 GET /api/v1/drones/{id} 不存在场景（返回 404）
    - 测试 GET /api/v1/drones 分页查询（返回 200 和分页数据）
    - 测试 PUT /api/v1/drones/{id} 成功场景（返回 200）
    - 测试 DELETE /api/v1/drones/{id} 成功场景（返回 200）
    - 模拟 DroneService 行为
    - _需求: 需求 1.1, 需求 1.2, 需求 1.3, 需求 2.1, 需求 2.2, 需求 3.1, 需求 4.1_

- [ ] 8. 请求拦截器实现
  - [ ] 8.1 创建请求拦截器
    - 创建 RequestInterceptor（实现 HandlerInterceptor 接口）
    - 实现 preHandle 方法（记录请求方法、路径、参数、IP、User-Agent、时间戳）
    - 实现 afterCompletion 方法（记录响应状态码、处理耗时）
    - 添加慢请求警告（耗时 > 1000ms）
    - 实现 getClientIp 方法（支持 X-Forwarded-For 和 X-Real-IP）
    - _需求: 需求 6.1, 6.2, 6.3, 6.4, 6.5, 6.6_

  - [ ] 8.2 配置拦截器注册
    - 创建 WebMvcConfig（实现 WebMvcConfigurer 接口）
    - 注册 RequestInterceptor（拦截所有路径，排除静态资源和错误页面）
    - _需求: 需求 6.1_

  - [ ]* 8.3 编写拦截器单元测试
    - 测试 preHandle 方法记录请求信息
    - 测试 afterCompletion 方法记录响应信息和耗时
    - 测试慢请求警告触发
    - 测试 getClientIp 方法（X-Forwarded-For、X-Real-IP、RemoteAddr）
    - _需求: 需求 6.2, 6.3, 6.4, 6.5_

- [ ] 9. 安全框架配置
  - [ ] 9.1 配置 Apache Shiro
    - 创建 ShiroConfig 配置类
    - 配置 SecurityManager（使用自定义 Realm）
    - 配置 ShiroFilterFactoryBean（定义过滤器链）
    - 设置 /api/v1/drones/** 需要认证（authc）
    - 设置其他路径匿名访问（anon）
    - _需求: 需求 2（查询需要认证）、需求 1/3/4（创建/更新/删除需要管理员权限）_

  - [ ] 9.2 实现自定义 Realm
    - 创建 DroneRealm（继承 AuthorizingRealm）
    - 实现 doGetAuthenticationInfo 方法（认证逻辑，暂时使用硬编码用户）
    - 实现 doGetAuthorizationInfo 方法（授权逻辑，区分普通用户和管理员角色）
    - _需求: 需求 2（查询需要认证）、需求 1/3/4（创建/更新/删除需要管理员权限）_

- [ ] 10. 前端页面实现
  - [ ] 10.1 创建 Thymeleaf 模板
    - 创建 layout.html（公共布局，引入 Bootstrap 3.3.7 CDN）
    - 创建 list.html（无人机列表页，包含分页、查询条件、操作按钮）
    - 创建 detail.html（无人机详情页）
    - 创建 form.html（创建/编辑表单，包含前端验证）
    - _需求: 需求 1, 2, 3, 4_

  - [ ] 10.2 实现前端表单验证
    - 使用 Bootstrap Validator 或原生 HTML5 验证
    - 验证必填字段、字段长度、日期格式、数值范围
    - 删除操作添加二次确认弹窗
    - _需求: 需求 1.3_

  - [ ] 10.3 创建前端控制器
    - 创建 DroneViewController（@Controller 注解）
    - 实现 GET /drones（返回列表页面）
    - 实现 GET /drones/{id}（返回详情页面）
    - 实现 GET /drones/new（返回创建表单）
    - 实现 GET /drones/{id}/edit（返回编辑表单）
    - _需求: 需求 1, 2, 3, 4_

- [ ] 11. Checkpoint - 确保所有功能集成测试通过
  - 确保所有测试通过，询问用户是否有问题

- [ ] 12. 集成测试和端到端测试
  - [ ]* 12.1 编写端到端 API 测试
    - 使用 @SpringBootTest 和 TestRestTemplate
    - 测试创建后查询流程（创建的数据可以被查询到）
    - 测试更新流程（更新后数据正确变更）
    - 测试删除流程（删除后无法查询到）
    - 测试分页查询流程（分页数据正确）
    - 测试条件查询流程（返回符合条件的结果）
    - 测试序列号唯一性约束（并发创建重复序列号）
    - 使用 H2 内存数据库
    - _需求: 需求 1, 2, 3, 4_

  - [ ]* 12.2 编写数据库切换集成测试
    - 测试 SQLite 环境下的完整 CRUD 流程
    - 测试 MySQL 环境下的完整 CRUD 流程
    - 验证两种数据库行为一致性
    - _需求: 需求 5.3_

- [ ] 13. 文档和部署准备
  - [ ] 13.1 编写 API 文档
    - 在 harness-collab/04-api-docs/ 目录下创建 drone-api.md
    - 记录所有 REST API 接口（路径、方法、参数、响应格式、错误码）
    - 提供 cURL 示例和响应示例
    - _需求: 所有功能需求_

  - [ ] 13.2 更新功能状态文档
    - 更新 harness-collab/func.md
    - 标记无人机信息管理功能为"已完成"
    - 记录实现的功能点和测试覆盖率
    - _需求: 所有功能需求_

  - [ ] 13.3 编写部署文档
    - 创建 DEPLOYMENT.md
    - 说明 SQLite 和 MySQL 两种部署方式
    - 提供配置文件示例和环境变量说明
    - 说明数据库初始化步骤
    - 提供启动命令和健康检查方法
    - _需求: 需求 5_

  - [ ] 13.4 更新 README.md
    - 添加项目简介和功能特性
    - 添加快速开始指南
    - 添加技术栈说明
    - 添加目录结构说明
    - 添加开发和测试命令
    - _需求: 所有功能需求_

- [ ] 14. 最终验证和质量检查
  - [ ] 14.1 运行完整测试套件
    - 执行 mvn clean test（确保所有单元测试通过）
    - 执行 mvn clean verify -Pharness-new（确保覆盖率 ≥ 80%）
    - 检查 JaCoCo 覆盖率报告（target/site/jacoco/index.html）
    - _需求: 可维护性要求（代码覆盖率 ≥ 80%）_

  - [ ] 14.2 运行静态代码检查
    - 执行 mvn checkstyle:check -Pharness-new（确保无违规）
    - 执行 mvn spotbugs:check -Pharness-new（确保无高危 Bug）
    - 修复所有 Checkstyle 和 SpotBugs 报告的问题
    - _需求: 可维护性要求（通过 Checkstyle 严格模式检查）_

  - [ ] 14.3 性能验证
    - 使用 JMeter 或 Gatling 进行性能测试
    - 验证单条记录查询 P95 ≤ 200ms
    - 验证列表查询（20 条）P95 ≤ 500ms
    - 验证创建/更新/删除操作 P95 ≤ 300ms
    - 验证 50 并发用户场景
    - _需求: 性能要求_

  - [ ] 14.4 安全验证
    - 验证未认证用户无法访问 API
    - 验证普通用户无法执行创建/更新/删除操作
    - 验证管理员可以执行所有操作
    - 验证 SQL 注入防护（尝试注入攻击）
    - 验证 XSS 防护（输入 HTML 标签）
    - _需求: 安全要求_

- [ ] 15. Final Checkpoint - 发布前检查
  - 确保所有测试通过、文档完整、代码质量达标，询问用户是否准备发布

---

## 注意事项

1. **任务执行顺序**：按照任务编号顺序执行，确保依赖关系正确
2. **测试驱动**：每个实现任务完成后立即执行对应的测试任务
3. **增量验证**：在 Checkpoint 任务处暂停，确保当前阶段所有功能正常
4. **文档同步**：代码实现完成后必须同步更新 API 文档和功能状态文档
5. **质量门禁**：最终验证阶段的所有检查项必须通过才能发布

---

## 测试覆盖率目标

| 层级 | 覆盖率目标 | 说明 |
|------|-----------|------|
| Service 层 | ≥ 90% | 核心业务逻辑必须充分测试 |
| Controller 层 | ≥ 85% | 覆盖所有 API 端点和参数校验 |
| Repository 层 | ≥ 80% | 覆盖所有数据访问方法 |
| 整体覆盖率 | ≥ 80% | 符合 Harness 可维护性要求 |

---

## 完成标准

本任务清单完成后，系统应满足以下标准：

- ✅ 所有功能需求已实现（需求 1-7）
- ✅ 所有非功能性需求已满足（性能、安全、兼容性、可维护性）
- ✅ 单元测试覆盖率 ≥ 80%
- ✅ 通过 Checkstyle 严格模式检查
- ✅ 通过 SpotBugs 检查（无高危 Bug）
- ✅ API 文档完整且与代码同步
- ✅ 部署文档完整且可操作
- ✅ 所有公共方法包含 Javadoc 注释
- ✅ 代码符合四层架构约束
