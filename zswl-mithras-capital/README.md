# zswl-mithras-capital

`zswl-mithras-capital` 是资金流水与核销域模块，负责银行流水处理、业务流水、财务流水、自动核销、手工核销释放和流水补偿任务。

本模块的核心语义是“资金流水进入系统后的识别、匹配、核销与释放”。它不同于 fund 的融资业务，也不同于 finance 的财务报表和回写。

当前模块本体主要包含 controller、application 接口、job 接口、资金流水/核销枚举、Excel 导出模型，以及 `FinanceFlowWriteOffDetail` 持久化；真正的大块资金流水处理仍在 `application/orchestration/capital`。这些实现同时依赖 third 财资流水、fund 融资、contract 合同、payment 付款、collection 收款、margin 保证金、riskcontrol 指标触发等多个域。

## 依赖现状

`capital` 模块本体 POM 只依赖 `api`、`foundation` 和框架 provided 依赖。Java import 分布约为：`dto` 52 处、`foundation` 21 处、`capital` 15 处、`api` 14 处，没有直接 import 其他业务域。

模块源码约 50 个 Java 文件，主要是：

- 4 个 controller 和对应 application 接口。
- 资金流水/核销相关枚举。
- 4 个 job 入口和 job service 接口。
- 业务流水导出模型和 exporter。
- `FinanceFlowWriteOffDetail` mapper/model/service。

资源层面也很薄，Mapper XML 只围绕 `finance_flow_write_off_detail` 核销明细表。原先放在 `fund` 资源目录、用于给 `finance_flow_write_off_detail` 增加逻辑删除字段的苍穹删除接口脚本已迁回本模块。

反向依赖方面，按 Java/POM/Mapper/SQL 粗略搜索，主要消费方仍集中在 `application`；`fund`、`payment`、`collection`、`third`、`margin` 等模块主要消费资金流水枚举、核销状态、现金流项目编码、job 接口和核销明细服务，`web` 负责 mapper scan/启动装配。

`application/orchestration/capital` 当前有 13 个核心实现/模型类。对这些代码做 import 粗分，外部依赖主要是 `third` 66 处、`foundation` 64 处、`capital` 63 处、`application` 59 处、`fund` 56 处、`dto` 30 处、`contract` 22 处、`payment` 20 处、`margin` 16 处、`collection` 16 处，另有少量 `system`、`customer`、`riskcontrol`、`finance`、`basedata`。这说明 application 里的 capital 代码不是单域实现，而是资金流水处理和跨域核销编排混在一起。

## 边界判断

`capital` 不是马上并入 `finance` 的低风险候选，也不适合把 application 里的实现原样搬回模块。

- `capital` 的语义是资金流水进入系统后的识别、匹配、核销与释放，和 `fund` 的融资事实、`finance` 的财务经营聚合、`payment/collection` 的应付应收事实不同。
- 当前模块本体过薄，直接物理合并只会减少一个 Maven 壳，但不能解决 application 中的复杂跨域编排。
- application 中同时操作合同、收款、付款、融资、保证金、第三方财资流水和风险指标的逻辑，应继续作为编排层存在。

## application/capital 拆分标注

对 `application/orchestration/capital` 做代码级复核后，当前更适合先按类拆分职责，而不是马上迁移源码。

| 类/目录 | 当前职责 | 判断 |
| --- | --- | --- |
| `FinanceFlowRecordService` | 财资平台流水保存、去重、全量同步、流水状态迁移、反核销流水处理 | 最接近 capital 自身事实，但当前直接使用 `third` 的 `FinanceFlowRecord` 持久化模型、财资平台 API、宝融同步、基础账户和系统用户。可作为回迁候选，但必须先把流水模型/同步 port 定义清楚。 |
| `BankFlowProcessingCenterService` | 银行流水处理中心列表、项目侧收付明细、无需处理、恢复/删除、轧差退款、确认收入、回款编码 | 典型跨域编排，直接触达客户、合同、收款、付款、保证金、融资、财务经营、第三方财资和风险指标。应继续留在 application，只能抽出纯流水状态和核销明细写入的小块。 |
| `BankFlowProcessingCenterFinanceService` | 融资侧机构、融资信息、还本付息/保证金/费用现金流、融资流水子表和可付款现金流 | 本质是 capital 与 fund 的读侧组装，直接读取间融、直融、还本付息、费用、保证金等 fund 模型。应留在 application 或后续改成 fund/capital 之间的窄输入，不应直接迁入 capital。 |
| `BusinessFlowService` | 资金端业务流水、资金端收付明细保存、手工确认推送苍穹、资金端 Excel 导出 | 以 fund 还本付息和 third 财资推送为核心，属于 fund/capital/third 的编排服务。应留在 application。 |
| `FinanceFlowAutoWriteOffService` | 项目侧和融资侧自动/手工核销，写收款、付款、保证金、融资还本付息、第三方推送和流水状态 | 最复杂的跨域核销编排，直接触达 collection、payment、margin、fund、contract、third、system。不能整体回迁；后续只适合抽出 capital 自有的金额分配、流水状态、核销明细落库规则。 |
| `write_off/CommonWriteOffService`、`WriteOffCommonService` | 自动核销公共接口和租户、合同、监管户、客户等辅助查询 | 依赖 third 流水匹配模型和 contract/customer/fund 查询，当前属于编排辅助。 |
| `write_off/impl/ProjectCollectWriteOffServiceImpl` | 项目收款自动匹配、匹配结果、核销落库和第三方收款请求构造 | 直接依赖 collection、contract、fund、third，属于项目收款核销编排。留在 application。 |
| `write_off/strategy/*` | 手工核销策略、批次、匹配结果、银行流水/业务流水增删改和重匹配 | 当前依赖 third 的 tab/match 持久化模型和 collection 输入。先留在 application，后续可在 capital 定义自己的匹配批次/结果模型后再评估回迁。 |

这轮标注后的实际判断是：`capital` 的合并准备第一步不是合并 Maven 模块，而是把 application 中的资金流水实现拆成三类：

- `capital` 回迁候选：流水保存/去重、流水状态、核销明细、金额分配等不需要理解外域生命周期的规则。
- application 编排保留：收款、付款、保证金、融资、合同、第三方财资平台之间的核销和同步。
- 外域事实输入：fund/collection/payment/margin/contract/customer/basedata/system/third 需要先变成 capital 自有快照或 port，不能把持久化模型原样带进 `capital`。

当前已开始做最小粒度的补实：

- `FinanceFlowRecordService.saveFlowRecord` 中“按已存在流水 id 判断哪些远端流水需要新增、以及新增流水默认展示状态”的纯保存规则已下沉为 `CapitalBankFlowSaveRuleService`，并使用 `CapitalBankFlowSaveDecision` 表达结果。application 仍负责查询已有 third 流水、复制 third 持久化对象、批量保存和事务提交后的无需处理判定。
- `FinanceFlowRecordService.move2NoHandle` 中“哪些银行流水应自动转为无需处理”的纯规则已下沉为 `CapitalBankFlowNoHandleRuleService`，并使用 `CapitalBankFlowSnapshot` 表达 capital 自己需要的流水字段。系统用户姓名、我方银行账号等外部事实仍由 application 查询后传入，避免把 `system`、`basedata` 或 `third` 持久化模型带回 `capital`。
- `FinanceFlowRecordService.withdrawBankFlow` 中“反核销后流水核销状态和处理中心状态如何变化”的纯规则已下沉为 `CapitalBankFlowWriteOffRuleService`，并使用 `CapitalBankFlowWriteOffState` 表达结果。application 仍负责删除核销明细、读取 third 流水持久化模型和执行更新。
- `FinanceFlowRecordService.fullSync` 中“已保存流水与远端临时流水按 `billno` 判断新增/删除”的纯同步差异规则已下沉为 `CapitalBankFlowSyncRuleService`，并使用 `CapitalBankFlowSyncSnapshot`、`CapitalBankFlowSyncDiff` 表达 capital 自己需要的同步事实。application 仍负责平台 API 查询、临时表读取、持久化更新和宝融同步。

## 后续整理

后续整理重点是先把资金流水自身事实、核销明细、流水状态规则沉淀为 capital 自身能力；涉及合同、收款、付款、融资、保证金、财资平台同步和指标触发的逻辑继续留在 application 编排，或通过 capital 定义的事实接口逐步隔离。

建议执行顺序：

1. 先按类标注 `application/orchestration/capital`：纯资金流水/核销明细/流水状态规则作为回迁候选，跨域核销和第三方同步继续留在 application。
2. 设计 capital 自有的银行流水、业务流水、核销明细和核销结果 DTO，避免回迁时把外域 model 带入 capital。
3. 收敛外部模块对 `capital.enums` 的直接使用，稳定编码可以在消费方本地表达，真正属于资金流水公共语言的枚举再保留。`FinanceCashFlowItemEnum`、`FinanceFlowDetailTableEnum` 这类枚举目前已经被 fund、payment、collection、liquidity、finance 和 application 多处消费，后续要判断它们是 capital 公共语言，还是应该上移为 finance 大域的稳定协议。
4. 当 capital 自身事实和 application 编排拆清后，再判断是否仍需要独立 Maven 模块，或并入 finance 大域的物理模块。
