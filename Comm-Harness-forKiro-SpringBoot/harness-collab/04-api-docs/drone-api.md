# 无人机管理系统 API 文档

> **关联设计文档**：[.kiro/specs/drone-management-system/design.md](../../.kiro/specs/drone-management-system/design.md)  
> **关联需求文档**：[.kiro/specs/drone-management-system/requirements.md](../../.kiro/specs/drone-management-system/requirements.md)  
> **文档版本**：v1.0  
> **创建时间**：2024-01-15  
> **最后更新**：2024-01-15  
> **负责人**：开发团队

---

## 概述

无人机管理系统提供 RESTful API 接口，用于管理无人机信息的增删改查操作。系统支持分页查询、条件过滤、参数校验和统一的错误处理。

- **基础路径**：`/api/v1/drones`
- **API 版本**：v1
- **认证方式**：Apache Shiro（所有接口需要认证，创建/更新/删除需要管理员角色）
- **内容类型**：`application/json`
- **字符编码**：UTF-8

---

## 接口列表

| 方法 | 路径 | 描述 | 认证要求 | 引入版本 |
|------|------|------|---------|---------|
| POST | `/api/v1/drones` | 创建新无人机 | 管理员 | v1.0 |
| GET | `/api/v1/drones/{id}` | 根据 ID 查询无人机详情 | 普通用户/管理员 | v1.0 |
| GET | `/api/v1/drones` | 分页查询无人机列表 | 普通用户/管理员 | v1.0 |
| PUT | `/api/v1/drones/{id}` | 更新无人机信息 | 管理员 | v1.0 |
| DELETE | `/api/v1/drones/{id}` | 删除无人机 | 管理员 | v1.0 |

---

## 接口详情

### POST /api/v1/drones

**描述**：创建新无人机，将无人机信息录入系统。序列号必须唯一，所有必填字段必须提供。

**认证要求**：需要管理员角色

**请求头**：

| 参数名 | 必填 | 说明 |
|--------|------|------|
| Content-Type | 是 | `application/json` |

**请求体**：

```json
{
  "serialNumber": "DJI-2024-001",
  "modelName": "DJI Mavic 3",
  "manufacturer": "DJI",
  "purchaseDate": "2024-01-15",
  "status": "AVAILABLE",
  "maxFlightTime": 46,
  "maxFlightDistance": 30000,
  "weight": 895,
  "remarks": "企业版，配备 4/3 CMOS 哈苏相机"
}
```

**请求体字段说明**：

| 字段名 | 类型 | 必填 | 校验规则 | 说明 |
|--------|------|------|----------|------|
| serialNumber | String | 是 | 长度 ≤ 100，不能为空 | 无人机序列号，必须唯一 |
| modelName | String | 是 | 长度 ≤ 100，不能为空 | 无人机型号名称 |
| manufacturer | String | 是 | 长度 ≤ 100，不能为空 | 制造商名称 |
| purchaseDate | String | 是 | 格式：yyyy-MM-dd | 购买日期 |
| status | String | 是 | 枚举值：AVAILABLE / UNDER_MAINTENANCE / SCRAPPED | 无人机状态 |
| maxFlightTime | Integer | 是 | 必须 ≥ 1 | 最大飞行时间（分钟） |
| maxFlightDistance | Integer | 是 | 必须 ≥ 1 | 最大飞行距离（米） |
| weight | Integer | 是 | 必须 ≥ 1 | 无人机重量（克） |
| remarks | String | 否 | 长度 ≤ 500 | 备注信息 |

**cURL 示例**：

```bash
curl -X POST http://localhost:8080/api/v1/drones \
  -H "Content-Type: application/json" \
  -d '{
    "serialNumber": "DJI-2024-001",
    "modelName": "DJI Mavic 3",
    "manufacturer": "DJI",
    "purchaseDate": "2024-01-15",
    "status": "AVAILABLE",
    "maxFlightTime": 46,
    "maxFlightDistance": 30000,
    "weight": 895,
    "remarks": "企业版，配备 4/3 CMOS 哈苏相机"
  }'
```

**响应示例（201 Created）**：

```json
{
  "code": 201,
  "message": "创建成功",
  "data": {
    "id": 1,
    "serialNumber": "DJI-2024-001",
    "modelName": "DJI Mavic 3",
    "manufacturer": "DJI",
    "purchaseDate": "2024-01-15",
    "status": "可用",
    "maxFlightTime": 46,
    "maxFlightDistance": 30000,
    "weight": 895,
    "remarks": "企业版，配备 4/3 CMOS 哈苏相机",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

**响应字段说明**：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 无人机唯一标识（系统自动生成） |
| serialNumber | String | 序列号 |
| modelName | String | 型号名称 |
| manufacturer | String | 制造商 |
| purchaseDate | String | 购买日期（yyyy-MM-dd 格式） |
| status | String | 状态描述（可用/维修中/已报废） |
| maxFlightTime | Integer | 最大飞行时间（分钟） |
| maxFlightDistance | Integer | 最大飞行距离（米） |
| weight | Integer | 重量（克） |
| remarks | String | 备注，可为 null |
| createdAt | String | 创建时间（ISO 8601 格式） |
| updatedAt | String | 最后更新时间（ISO 8601 格式） |

**错误响应示例（400 Bad Request - 序列号重复）**：

```json
{
  "code": 400,
  "message": "序列号已存在",
  "data": null
}
```

**错误响应示例（400 Bad Request - 字段验证失败）**：

```json
{
  "code": 400,
  "message": "参数校验失败",
  "data": {
    "serialNumber": "序列号不能为空",
    "maxFlightTime": "最大飞行时间必须大于 0"
  }
}
```

**错误码**：

| HTTP 状态码 | 业务码 | 说明 |
|------------|--------|------|
| 201 | 201 | 创建成功 |
| 400 | 400 | 参数校验失败或序列号重复 |
| 401 | 401 | 未认证 |
| 403 | 403 | 无管理员权限 |
| 500 | 500 | 服务器内部错误 |

---

### GET /api/v1/drones/{id}

**描述**：根据 ID 查询无人机详细信息。

**认证要求**：需要认证（普通用户或管理员）

**请求参数**：

| 参数名 | 位置 | 类型 | 必填 | 描述 |
|--------|------|------|------|------|
| id | path | Long | 是 | 无人机唯一标识，必须为正整数 |

**cURL 示例**：

```bash
curl -X GET http://localhost:8080/api/v1/drones/1
```

**响应示例（200 OK）**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "serialNumber": "DJI-2024-001",
    "modelName": "DJI Mavic 3",
    "manufacturer": "DJI",
    "purchaseDate": "2024-01-15",
    "status": "可用",
    "maxFlightTime": 46,
    "maxFlightDistance": 30000,
    "weight": 895,
    "remarks": "企业版，配备 4/3 CMOS 哈苏相机",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

**错误响应示例（404 Not Found）**：

```json
{
  "code": 404,
  "message": "无人机不存在",
  "data": null
}
```

**错误码**：

| HTTP 状态码 | 业务码 | 说明 |
|------------|--------|------|
| 200 | 200 | 查询成功 |
| 401 | 401 | 未认证 |
| 404 | 404 | 无人机不存在 |
| 500 | 500 | 服务器内部错误 |

---

### GET /api/v1/drones

**描述**：分页查询无人机列表，支持按型号、制造商、状态进行过滤。

**认证要求**：需要认证（普通用户或管理员）

**请求参数**：

| 参数名 | 位置 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|------|--------|------|
| modelName | query | String | 否 | — | 型号名称（模糊查询） |
| manufacturer | query | String | 否 | — | 制造商（模糊查询） |
| status | query | String | 否 | — | 状态（精确匹配）：AVAILABLE / UNDER_MAINTENANCE / SCRAPPED |
| pageNum | query | Integer | 否 | 1 | 页码，从 1 开始 |
| pageSize | query | Integer | 否 | 20 | 每页数量 |

**cURL 示例**：

```bash
# 查询所有无人机（默认分页）
curl -X GET "http://localhost:8080/api/v1/drones"

# 按型号模糊查询
curl -X GET "http://localhost:8080/api/v1/drones?modelName=Mavic"

# 按制造商和状态查询
curl -X GET "http://localhost:8080/api/v1/drones?manufacturer=DJI&status=AVAILABLE"

# 分页查询第 2 页，每页 10 条
curl -X GET "http://localhost:8080/api/v1/drones?pageNum=2&pageSize=10"

# 组合查询
curl -X GET "http://localhost:8080/api/v1/drones?modelName=Mavic&manufacturer=DJI&status=AVAILABLE&pageNum=1&pageSize=20"
```

**响应示例（200 OK）**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 50,
    "pageNum": 1,
    "pageSize": 20,
    "pages": 3,
    "list": [
      {
        "id": 1,
        "serialNumber": "DJI-2024-001",
        "modelName": "DJI Mavic 3",
        "manufacturer": "DJI",
        "purchaseDate": "2024-01-15",
        "status": "可用",
        "maxFlightTime": 46,
        "maxFlightDistance": 30000,
        "weight": 895,
        "remarks": "企业版",
        "createdAt": "2024-01-15T10:30:00",
        "updatedAt": "2024-01-15T10:30:00"
      },
      {
        "id": 2,
        "serialNumber": "DJI-2024-002",
        "modelName": "DJI Phantom 4 Pro",
        "manufacturer": "DJI",
        "purchaseDate": "2024-01-10",
        "status": "维修中",
        "maxFlightTime": 30,
        "maxFlightDistance": 7000,
        "weight": 1375,
        "remarks": "例行维护",
        "createdAt": "2024-01-10T09:00:00",
        "updatedAt": "2024-01-14T15:20:00"
      }
    ]
  }
}
```

**响应字段说明（分页对象）**：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| total | Long | 总记录数 |
| pageNum | Integer | 当前页码 |
| pageSize | Integer | 每页数量 |
| pages | Integer | 总页数 |
| list | Array | 当前页的无人机列表 |

**错误码**：

| HTTP 状态码 | 业务码 | 说明 |
|------------|--------|------|
| 200 | 200 | 查询成功 |
| 400 | 400 | 请求参数错误 |
| 401 | 401 | 未认证 |
| 500 | 500 | 服务器内部错误 |

---

### PUT /api/v1/drones/{id}

**描述**：更新无人机信息，支持部分更新（只更新提供的字段）。如果更新序列号，新序列号必须唯一。

**认证要求**：需要管理员角色

**请求参数**：

| 参数名 | 位置 | 类型 | 必填 | 描述 |
|--------|------|------|------|------|
| id | path | Long | 是 | 无人机唯一标识 |

**请求头**：

| 参数名 | 必填 | 说明 |
|--------|------|------|
| Content-Type | 是 | `application/json` |

**请求体**：

```json
{
  "status": "UNDER_MAINTENANCE",
  "remarks": "进行例行维护检查"
}
```

**请求体字段说明**：

所有字段均为可选，只更新提供的字段。字段类型和校验规则与创建接口相同。

| 字段名 | 类型 | 必填 | 校验规则 | 说明 |
|--------|------|------|----------|------|
| serialNumber | String | 否 | 长度 ≤ 100 | 序列号（如更新，必须唯一） |
| modelName | String | 否 | 长度 ≤ 100 | 型号名称 |
| manufacturer | String | 否 | 长度 ≤ 100 | 制造商 |
| purchaseDate | String | 否 | 格式：yyyy-MM-dd | 购买日期 |
| status | String | 否 | 枚举值 | 状态 |
| maxFlightTime | Integer | 否 | 必须 ≥ 1 | 最大飞行时间（分钟） |
| maxFlightDistance | Integer | 否 | 必须 ≥ 1 | 最大飞行距离（米） |
| weight | Integer | 否 | 必须 ≥ 1 | 重量（克） |
| remarks | String | 否 | 长度 ≤ 500 | 备注 |

**cURL 示例**：

```bash
# 更新状态和备注
curl -X PUT http://localhost:8080/api/v1/drones/1 \
  -H "Content-Type: application/json" \
  -d '{
    "status": "UNDER_MAINTENANCE",
    "remarks": "进行例行维护检查"
  }'

# 更新多个字段
curl -X PUT http://localhost:8080/api/v1/drones/1 \
  -H "Content-Type: application/json" \
  -d '{
    "modelName": "DJI Mavic 3 Pro",
    "maxFlightTime": 50,
    "status": "AVAILABLE",
    "remarks": "升级到 Pro 版本"
  }'
```

**响应示例（200 OK）**：

```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "serialNumber": "DJI-2024-001",
    "modelName": "DJI Mavic 3",
    "manufacturer": "DJI",
    "purchaseDate": "2024-01-15",
    "status": "维修中",
    "maxFlightTime": 46,
    "maxFlightDistance": 30000,
    "weight": 895,
    "remarks": "进行例行维护检查",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T14:20:00"
  }
}
```

**错误响应示例（404 Not Found）**：

```json
{
  "code": 404,
  "message": "无人机不存在",
  "data": null
}
```

**错误响应示例（400 Bad Request - 序列号重复）**：

```json
{
  "code": 400,
  "message": "序列号已存在",
  "data": null
}
```

**错误码**：

| HTTP 状态码 | 业务码 | 说明 |
|------------|--------|------|
| 200 | 200 | 更新成功 |
| 400 | 400 | 参数校验失败或序列号重复 |
| 401 | 401 | 未认证 |
| 403 | 403 | 无管理员权限 |
| 404 | 404 | 无人机不存在 |
| 500 | 500 | 服务器内部错误 |

---

### DELETE /api/v1/drones/{id}

**描述**：删除无人机记录（物理删除）。删除操作不可恢复，建议前端进行二次确认。

**认证要求**：需要管理员角色

**请求参数**：

| 参数名 | 位置 | 类型 | 必填 | 描述 |
|--------|------|------|------|------|
| id | path | Long | 是 | 无人机唯一标识 |

**cURL 示例**：

```bash
curl -X DELETE http://localhost:8080/api/v1/drones/1
```

**响应示例（200 OK）**：

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

**错误响应示例（404 Not Found）**：

```json
{
  "code": 404,
  "message": "无人机不存在",
  "data": null
}
```

**错误码**：

| HTTP 状态码 | 业务码 | 说明 |
|------------|--------|------|
| 200 | 200 | 删除成功 |
| 401 | 401 | 未认证 |
| 403 | 403 | 无管理员权限 |
| 404 | 404 | 无人机不存在 |
| 500 | 500 | 服务器内部错误 |

---

## 错误码汇总

### 通用错误码

| HTTP 状态码 | 业务码 | 说明 | 处理建议 |
|------------|--------|------|----------|
| 200 | 200 | 请求成功 | — |
| 201 | 201 | 创建成功 | — |
| 400 | 400 | 请求参数错误 | 检查请求参数格式、必填项和校验规则 |
| 401 | 401 | 未认证 | 需要登录或 Token 已过期 |
| 403 | 403 | 无权限 | 当前用户无管理员权限 |
| 404 | 404 | 资源不存在 | 确认无人机 ID 是否正确 |
| 500 | 500 | 服务器内部错误 | 联系开发团队排查 |

### 业务错误码

| 业务码 | 说明 | 触发场景 | 处理建议 |
|--------|------|----------|----------|
| 400 | 序列号已存在 | 创建或更新时序列号与现有记录重复 | 使用不同的序列号 |
| 400 | 参数校验失败 | 请求参数不符合校验规则 | 检查响应中的具体字段错误信息 |
| 404 | 无人机不存在 | 查询、更新或删除不存在的无人机 ID | 确认 ID 是否正确或无人机是否已被删除 |
| 500 | 数据库连接失败 | 数据库不可用 | 检查数据库配置和连接状态 |

---

## 数据字典

### 无人机状态枚举

| 枚举值 | 中文描述 | 说明 |
|--------|---------|------|
| AVAILABLE | 可用 | 无人机处于正常工作状态，可以执行飞行任务 |
| UNDER_MAINTENANCE | 维修中 | 无人机正在进行维护或修理，暂时不可用 |
| SCRAPPED | 已报废 | 无人机已达到使用寿命或损坏严重，不再使用 |

### 日期时间格式

| 字段类型 | 格式 | 示例 | 说明 |
|---------|------|------|------|
| 购买日期 | yyyy-MM-dd | 2024-01-15 | 日期格式 |
| 创建时间 | ISO 8601 | 2024-01-15T10:30:00 | 日期时间格式（本地时间） |
| 更新时间 | ISO 8601 | 2024-01-15T14:20:00 | 日期时间格式（本地时间） |

---

## 使用示例

### 完整业务流程示例

**场景**：录入新无人机 → 查询详情 → 更新状态 → 删除记录

```bash
# 1. 创建新无人机
curl -X POST http://localhost:8080/api/v1/drones \
  -H "Content-Type: application/json" \
  -d '{
    "serialNumber": "DJI-2024-003",
    "modelName": "DJI Mini 3 Pro",
    "manufacturer": "DJI",
    "purchaseDate": "2024-01-20",
    "status": "AVAILABLE",
    "maxFlightTime": 34,
    "maxFlightDistance": 25000,
    "weight": 249,
    "remarks": "轻量级无人机"
  }'

# 响应：{"code":201,"message":"创建成功","data":{"id":3,...}}

# 2. 查询刚创建的无人机详情
curl -X GET http://localhost:8080/api/v1/drones/3

# 3. 更新无人机状态为维修中
curl -X PUT http://localhost:8080/api/v1/drones/3 \
  -H "Content-Type: application/json" \
  -d '{
    "status": "UNDER_MAINTENANCE",
    "remarks": "云台维修"
  }'

# 4. 查询所有维修中的无人机
curl -X GET "http://localhost:8080/api/v1/drones?status=UNDER_MAINTENANCE"

# 5. 删除无人机记录
curl -X DELETE http://localhost:8080/api/v1/drones/3
```

### 分页查询示例

```bash
# 查询第 1 页，每页 10 条
curl -X GET "http://localhost:8080/api/v1/drones?pageNum=1&pageSize=10"

# 查询 DJI 制造商的所有可用无人机
curl -X GET "http://localhost:8080/api/v1/drones?manufacturer=DJI&status=AVAILABLE"

# 查询型号包含 "Mavic" 的无人机，第 2 页
curl -X GET "http://localhost:8080/api/v1/drones?modelName=Mavic&pageNum=2&pageSize=5"
```

### 错误处理示例

```bash
# 尝试创建重复序列号的无人机
curl -X POST http://localhost:8080/api/v1/drones \
  -H "Content-Type: application/json" \
  -d '{
    "serialNumber": "DJI-2024-001",
    "modelName": "Test Model",
    "manufacturer": "Test",
    "purchaseDate": "2024-01-20",
    "status": "AVAILABLE",
    "maxFlightTime": 30,
    "maxFlightDistance": 5000,
    "weight": 500
  }'

# 响应：{"code":400,"message":"序列号已存在","data":null}

# 尝试查询不存在的无人机
curl -X GET http://localhost:8080/api/v1/drones/9999

# 响应：{"code":404,"message":"无人机不存在","data":null}

# 参数校验失败示例
curl -X POST http://localhost:8080/api/v1/drones \
  -H "Content-Type: application/json" \
  -d '{
    "serialNumber": "",
    "modelName": "Test Model",
    "maxFlightTime": -1
  }'

# 响应：
# {
#   "code": 400,
#   "message": "参数校验失败",
#   "data": {
#     "serialNumber": "序列号不能为空",
#     "manufacturer": "制造商不能为空",
#     "purchaseDate": "购买日期不能为空",
#     "status": "状态不能为空",
#     "maxFlightTime": "最大飞行时间必须大于 0",
#     "maxFlightDistance": "最大飞行距离不能为空",
#     "weight": "重量不能为空"
#   }
# }
```

---

## 性能指标

根据需求文档中的性能要求，各接口的性能指标如下：

| 接口 | P95 响应时间 | P99 响应时间 | 说明 |
|------|-------------|-------------|------|
| GET /api/v1/drones/{id} | ≤ 200ms | ≤ 500ms | 单条记录查询 |
| GET /api/v1/drones | ≤ 500ms | ≤ 1000ms | 列表查询（分页 20 条） |
| POST /api/v1/drones | ≤ 300ms | ≤ 800ms | 创建操作 |
| PUT /api/v1/drones/{id} | ≤ 300ms | ≤ 800ms | 更新操作 |
| DELETE /api/v1/drones/{id} | ≤ 300ms | ≤ 800ms | 删除操作 |

**并发支持**：系统支持 50 并发用户同时操作  
**数据规模**：支持单表 10 万条记录的查询性能

---

## 变更记录

| 版本 | 日期 | 变更内容 | 变更人 |
|------|------|----------|--------|
| v1.0 | 2024-01-15 | 初始版本，包含无人机 CRUD 接口 | 开发团队 |
