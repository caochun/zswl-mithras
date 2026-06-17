# zswl-mithras-metric

`zswl-mithras-metric` 是指标域模块，负责金融云/风控指标、指标因子、指标报表、指标刷新和报表文件存储接口。

本模块的核心语义是“跨域指标口径、指标计算和指标输出”。它天然需要读取项目、合同、资金、付款、收款、风控、资产分类、绩效、基础数据和第三方报表数据。

金融云指标对流动性账户余额表的读取已通过 `FinancialCloudAccountBalancePort` 隔离，由 application 适配具体数据源。风险策略当前值、客户/工商/行业分类也已经分别通过 `RiskStrategyCurrentValuePort`、`MetricCustomerInfoPort` 输入。后续整理重点是避免指标模块反向污染交易域，并将外域事实读取和指标口径清晰分离。

## 边界判断

`metric` 应保持独立，不建议并入 `riskcontrol`、`dashboard`、`associationreport` 或 `finance`。

- `metric` 拥有指标元数据、指标因子、指标值、定时存数、金融云指标、计算器和指标上报。
- `riskcontrol` 是风险运营、预警、舆情、策略和风险报告域，可以消费指标或提供风险策略输入，但不应拥有全部指标计算。
- `dashboard`、`associationreport`、`finance` 是指标结果的展示、报送或财务口径消费者，不应承载指标计算生命周期。

## 依赖现状

代码层面，`metric` 直接读取 `fund`、`contract`、`payment`、`collection`、`projectprocess`、`assetclassify`、`kpi`、`basedata`、`third` 等模块的事实，用于计算金融云、风险指标和报表指标。POM 中这些业务依赖大多是真实依赖，不适合通过删依赖掩盖耦合。

本轮复核统计到 409 个 Java 文件，一级包分布约为 `financialcloudmetric` 263 个、`aggregator` 51 个、`enums` 25 个、`emit` 24 个、`mapper` 19 个、`service` 17 个、`controller` 4 个、`application` 3 个、`job` 3 个。Java import 分布约为 `metric` 717 处、`fund` 64 处、`foundation` 52 处、`contract` 45 处、`payment` 34 处、`dto` 28 处、`projectprocess` 15 处、`api` 15 处、`collection` 14 处、`assetclassify` 11 处、`kpi` 10 处、`third` 3 处、`basedata` 2 处。原先 import 分布里只有少量 `riskcontrol` 直接引用，集中在 `RiskMetricValueService` 读取 `RiskControlStrategyMapper`。这一处已收敛为 `RiskStrategyCurrentValuePort` 和 `RiskStrategyCurrentValueSnapshot`，由 application adapter 读取 riskcontrol 策略当前值后输入 metric；`metric` 源码和 POM 不再直接依赖 `riskcontrol`。

`metric -> customer` 的隐藏源码依赖已清理：月度指标存数和金融云行业维度计算所需客户、工商、行业分类数据改由 `MetricCustomerInfoPort` 输入，metric 侧只消费 `MetricCustomerSnapshot`、`MetricCorpCommerceSnapshot`、`MetricIndustryTypeSnapshot`，customer mapper/model 读取留在 application adapter。

`metric -> riskcontrol` 已收敛一层：原先放在 `metric/adapter/riskcontrol` 下、用于实现 `riskcontrol` port 的 3 个 adapter 已迁到 `application/orchestration/adapter/riskcontrol`，由 application 负责把 riskcontrol 的外部需求接到 metric。

`RiskMetricValueService` 指标计算时仍保留“风险策略当前值优先”的业务语义，但不再直接读取 `riskcontrol.strategy` 持久化模型；策略当前值通过 metric 自有 port 输入。

资源层面主要是 `risk_metric*`、`financial_cloud_metric_value` 等指标表。`RiskMetricTimedMapper.xml` 直接读取 `corp_address_info`，说明指标定时存数仍有客户地址读模型耦合。

反向依赖相对少，按 Java/POM/Mapper/SQL 粗略搜索，主要消费方集中在 `application`、`finance`、`web`。这说明 metric 更像共享指标计算输出域，而不是被核心交易域广泛反向依赖的事实源。

模块已经开始使用少量 port：`FinancialCloudAccountBalancePort` 隔离金融云指标所需账户余额输入，`MetricCustomerInfoPort` 隔离客户/工商/行业分类输入，`RiskStrategyCurrentValuePort` 隔离风险策略当前值输入，`JinKongMonthlyReportPort` 暴露金控月报同步能力。但大量 calculator 仍直接注入 `fund`、`contract`、`payment`、`collection`、`assetclassify`、`kpi` 的 mapper/model，是后续瘦身重点。

本轮删除了 `metric.application.third.yunhu.YunHuMonthlyReportService`。全仓静态搜索显示该类没有外部引用，且它与 `finance.service.third.jk.JinKongMonthlyReportService` 的金控/月报同步逻辑重复但更旧：只写 `RiskMetricFactor`，不处理组织编码、合并报表 `RiskMetricFactorMerge`、财务原始余额落库、辅助核算表和系统配置。删除它不会解决 `finance -> metric` 的剩余依赖，真正的耦合仍在 finance 金控同步服务直接写 metric 因子模型和服务。

本轮也复核了 `metric/pom.xml` 的主要技术依赖，没有发现适合直接删除的项：Gruul 用于系统配置、组织和用户信息；MyBatis-Plus 用于 mapper/model/service；Hutool 用于日期、集合、JSON、HTTP 和工具函数；POI 用于指标因子 Excel 导入；`crypto-open-starter` 用于 emit 请求签名/加密；XXL Job 用于指标定时任务；JUEL 用于指标表达式计算；MapStruct 用于金融云指标 converter。因此当前 POM 瘦身重点不在技术依赖，而在逐步减少业务模块持久化模型输入。

## 后续整理

- 保持独立 Maven 模块，作为 reporting/risk/finance 共享的指标计算域。
- 短期不拆 `financialcloudmetric` 子域；它虽然体量很大，但与 `risk_metric` 同属指标口径和指标输出，拆分前应先收敛外域输入模型。
- 继续把 calculator 中直接注入外域 mapper/model 的位置收敛为指标输入快照或查询 port，优先治理 fund、contract、payment、collection 这些交易事实输入。
- 已处理 `metric -> riskcontrol.strategy`：风险策略当前值通过 `RiskStrategyCurrentValuePort` 输入，application 适配 riskcontrol mapper。后续重点转向 fund、contract、payment、collection 等交易事实输入。
- 已处理 `metric -> customer` 隐藏依赖：客户、工商、行业分类输入通过 `MetricCustomerInfoPort` 由 application 适配。
- 把报表文件、外部云湖/金控上报细节与指标口径分层，避免指标核心被外部接口模型污染。
- 继续治理 `finance -> metric`：不要用旧 `YunHuMonthlyReportService` 替换 finance 现有同步逻辑；更合适的方向是由 metric 提供稳定的因子写入 port/快照输入，finance 负责外部报表拉取和财务原始数据处理。
- 不建议通过删除 `metric` 当前业务依赖来制造低耦合假象；源码证据表明 fund、contract、payment、collection、projectprocess、assetclassify、kpi、basedata、third 都仍是指标计算真实输入。
