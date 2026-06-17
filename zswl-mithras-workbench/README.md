# zswl-mithras-workbench

`zswl-mithras-workbench` 是工作台域模块，负责快捷入口、公告、卡片指标、柱状/雷达/小图表、工作台初始化和指标刷新任务。

本模块的核心语义是“用户进入系统后的工作入口和轻量聚合展示”。它可以展示多业务域数据，但不应承载核心业务规则。

当前判断是：保持独立，归入 reporting 大域治理，不作为第一批物理合并对象。它虽然是展示聚合模块，但拥有公告、快捷入口、卡片配置、用户卡片关系、指标缓存和初始化任务等轻量写生命周期，不只是 `dashboard` 或 `application` 里的一个页面目录。

从代码证据看，模块内 Java import 主要集中在 `workbench` 约 84 处、`dto` 约 62 处、`foundation` 约 34 处、`api` 约 29 处，没有直接 import 其他业务域；模块也没有 `src/main/resources` 下的 mapper XML。反向依赖主要来自 `application` 约 31 处、`web` 约 1 处，说明 POM 的低耦合基本真实。

POM 依赖也基本都有源码证据：MyBatis-Plus 用于 mapper/model/service，Hutool 用于判空、集合、日期和 Excel 导出，MapStruct 用于 converter，Fastjson 用于快捷入口和指标 JSON 转换，XXL Job 用于初始化和指标刷新任务，Gruul starter 提供 `AccountUtil`、`Response` 等入口能力。

对外部业务事实的读取主要通过 `workbench.application.port` 和 `workbench.application.port.cardcal` 下的 `WorkbenchCardMetricPort`、`WorkbenchFundsLiquidityPort`、`WorkbenchCardCollectionPort`、`WorkbenchCardFundRepayPort`、`WorkbenchCardProjReviewPort`、`WorkbenchFinancialMetricFactorPort`、`WorkbenchRiskControlStrategyPort` 等接口表达，具体 adapter 放在 `application/orchestration/adapter/workbench`。这些 adapter 会读取项目、合同、客户、付款、收款、资金、风险、资产分类等外部事实，属于读侧聚合装配。

需要注意的是，`application/orchestration/facade/workbench` 里仍有较重的工作台图表 facade，直接注入 projectprocess、contract、collection、payment、margin、riskcontrol、assetclassify、system 等服务和模型。短期放在 `application` 是合理的，因为它是跨域页面聚合；后续若要继续瘦身，优先把纯展示口径沉到 `workbench` 的 port/service，把真实跨域查询留在 application adapter。

后续整理重点是保持工作台只做入口与展示聚合，核心业务域不要反向依赖 `workbench`；如果未来要合并，也应先证明它只剩展示壳，再考虑并入 reporting 装配模块，而不是并入任一核心业务域。

当前不满足物理合并条件：它有公告、快捷入口、用户卡片关系、指标缓存和初始化 job 这些独立写生命周期，不是单纯依附于 `dashboard` 的只读页面。
