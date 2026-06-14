# AGENTS.md — AI 协作协议入口

本文件是 Spring Boot Harness 工作区的 AI 协作协议入口。完整协议定义在 [harness-collab/AGENTS.md](harness-collab/AGENTS.md)，所有在本工作区内工作的 AI 助手（包括 Kiro）必须遵守该协议。

---

## Harness 监管机制

Harness 通过两种原生机制对工作区内的所有子项目实施自动监管：

**Steering 文件自动加载**

`.kiro/steering/` 目录下的所有 steering 文件均包含 `inclusion: auto` 元数据，Kiro 在每次对话启动时自动加载这些文件。无论开发者在工作区的哪个子目录下工作，以下约束始终生效：

- `java-engineering-standards.md` — 四层架构约束、编码标准
- `testing-quality-standards.md` — 测试分层策略、覆盖率要求
- `api-doc-sync-protocol.md` — API 文档同步规则
- `ai-collaboration-protocol.md` — Kiro 行为规范、交付标准
- `project-lifecycle.md` — Dev_Lifecycle 六阶段流程及门禁

**Hooks 自动触发**

`.kiro/hooks/` 目录下的 Hook 在特定 IDE 事件发生时自动触发，无需手动调用：

- Controller 文件变更 → 提示更新 API 文档
- 新建 Java 文件 → 检查包路径分层合法性
- 业务代码变更 → 提示同步更新测试文件
- 新建 pom.xml → 提示继承 Harness Maven Profile

---

## Kiro 核心行为约束（快速参考）

在本工作区内，Kiro 必须遵守以下五条核心规则：

**规则 1：文档先于代码**
生成任何业务代码之前，必须确认 `harness-collab/01-product-specs/` 下存在需求文档，且 `harness-collab/02-design-docs/` 下存在设计文档。两者缺一不可，否则停止代码生成并引导开发者先完成文档。

**规则 2：严格遵循四层架构**
生成的 Java 代码必须放置在正确的包层级（`controller / service / domain / repository / config / common / exception`），禁止跨层直接依赖（如 controller 直接注入 repository）。

**规则 3：代码与测试同步生成**
每次生成业务代码时，必须同步生成对应的测试类（service 层用 Mockito，controller 层用 `@WebMvcTest`，repository 层用 `@DataJpaTest`），测试类与被测类保持相同包路径。

**规则 4：代码变更后同步文档**
完成代码交付后，必须提示开发者同步更新 `harness-collab/04-api-docs/` 中的 API 文档和 `harness-collab/func.md` 中的功能状态。

**规则 5：不得跳过 Lifecycle_Gate**
Dev_Lifecycle 六阶段中的每个准出检查点（GATE-01 至 GATE-08）不得跳过。若当前阶段产出物不满足准出标准，必须输出具体的缺失项清单和修正指引，并在后续对话中持续追踪直到问题解决。

---

## 完整协议

完整的研发流程规范、交付标准、门禁规则和文档维护规范，参见：

**[harness-collab/AGENTS.md](harness-collab/AGENTS.md)**
