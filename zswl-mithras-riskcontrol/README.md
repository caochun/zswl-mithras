# zswl-mithras-riskcontrol

`zswl-mithras-riskcontrol` 是风险监控和风险运营域模块，负责风控策略、指标计算、预警/舆情、集中度、关联客户、评分卡、经济增加值报告和风险报告。

本模块的核心语义是“风险规则、风险识别、风险指标、风险报告和风险处置建议”。客户、项目、合同、付款、收款、资产分类等是风控判断所需事实，不应成为风控域自己维护的主数据。

## 当前边界

- `strategy` / `metric`：风险策略、指标定义、指标订阅计算器和指标触发事件。
- `warning` / `opinion`：预警和舆情监控、处置状态、监控列表和统计。
- `concentration` / `relation`：集中度、关联客户和关联交易报送基础数据。
- `report`：金控关联交易报送、经济增加值报表等风险报送能力。
- `scorecard`：风控评分卡模板、指标、区域和试算。
- `clientfile` / `job`：客户名单文件传输、定时同步和风险监控任务。
- `application/port`：风控域对外部事实和横向流程能力的输入接口；具体适配由 `application` 承接。

## 依赖现状

当前 POM 直接依赖 `customer`、`contract`、`projectprocess`、`payment`、`collection`。代码层面这些依赖均有实际 import，因此不是单纯的无效 POM 依赖。`workflow`、`basedata`、`assetclassify`、flow-core 和 flowable 的 Java/POM 直接依赖已清理。

本轮依赖复核确认，风险模块源码仅使用 MyBatis 注解，未直接使用 MyBatis 核心 API；直接 `org.mybatis:mybatis` 无独立必要性，已由 MyBatis-Plus starter 的传递依赖覆盖并移除。

本轮继续复核技术依赖，Guava EventBus 用于指标订阅计算器，JSch 用于客户名单 SFTP，MapStruct 用于风控对象转换，XXL Job 用于风控定时任务，FastJSON、Swagger、validation、javax.annotation、commons-lang3 均有源码使用点，暂不作为空依赖删除。原预警流程查询、流程变量写入和动态表单的 flow-core/flowable 适配已迁到 `application`。

代码层复核显示，模块有 202 个 Java 文件，一级包主体仍是 `metric`、`scorecard`、`report`、`common`、`concentration`、`strategy`、`controller`、`application`、`job`。代码层 import 粗分显示，除 `riskcontrol` 自身、`foundation`、`api`、`dto` 外，主要外域依赖集中在：

- `customer`：约 11 处。
- `projectprocess`：约 2 处。
- `contract`：约 6 处。
- `collection`：约 7 处。
- `payment`：约 6 处。

主要耦合形态：

- 风险指标计算器直接读取客户、项目评审、合同、付款、收款、资产分类等外域 mapper/model。
- 风险报告服务直接装配客户、合同、项目评审、付款、收款事实。
- 预警/舆情 Mapper XML 直接 join `client`、`bifrost_org`，SQL 层也存在外域读模型耦合。
- 指标计算器原先少量引用 `basedata.util.DateUtil` 只是为了计算快照日期当天结束时间，本轮已改为 JDK `LocalDate.atTime(23, 59, 59)`。
- 评分卡客户注册地址解析原先放在 riskcontrol 内部 adapter，直接读取 customer 地址和 basedata 地址字典；本轮已迁到 application 的 `ScoreCardClientAddressResolverAdapter`，riskcontrol 只保留 `ScoreCardClientAddressResolver` port 和评分卡业务逻辑。至此 `riskcontrol` 源码和 POM 已无 `basedata` 直接依赖。
- 区域风险限额指标原先只为使用 `ProjRegionalClassify` 枚举而依赖 projectprocess 枚举；本轮补齐 riskcontrol 自有 `RegionalProjectClassify.ZHEJIANG`，并把 `RegionalRiskQuotaComputer` 与三个区域指标切到风控本地枚举，传给 mapper 的稳定 `name()` 值不变。
- 预警数量变化统计原先只为两个流程模型 key 引入 workflow 的 `ProcessModelTypeEnum`；本轮改为风险域本地稳定 key 常量。
- 预警流程查询和处置变量写入原先由 `RiskControlWarnMonitorService` 直接调用 flow-core/Flowable；本轮改为 `RiskControlWarnWorkflowPort` 和 `RiskControlWarnWorkflowInstance`，由 application 的 `RiskControlWarnWorkflowPortAdapter` 调用流程引擎。至此 `riskcontrol` 源码/resources/POM 已无 flow-core、Flowable 和 workflow 直接依赖。
- `RiskOpinionFileHandler` 原先通过 workflow 的 `ProcessModelTypeEnum.businessModuleName` 判断读取舆情表或预警表；本轮改为 riskcontrol 本地的舆情/预警流程 key 集合，去掉该枚举依赖，并避免预警流程也映射到 `RISK_OPINION` 时误读舆情表。
- 风控相关动态表单处理类原先放在 riskcontrol，直接实现 workflow 的 `DynamicFormHandler`；本轮已迁到 application 的 `orchestration/workflow/flow/dynamicform/riskcontrol`，风控域只保留风险处置业务能力。
- 资产分类结果原先由风险集中度和两个风险指标计算器直接读取 `assetclassify` service/mapper/model；本轮已改为 `RiskControlAssetClassifyPort` 输入，application adapter 负责适配当前分类批次、后三类客户集合和客户最新分类结果，`riskcontrol` Java/POM 层已无 `assetclassify` 依赖。
- 金控关联交易报送原先在 `RiskControlGljyReportService` 内部监听 `PaymentWriteOffEvent`，同时直接读取 payment、contract、projectprocess 事实。本轮已将该付款核销监听器迁到 application 的 `RiskControlGljyPaymentWriteOffListener`，风控域保留关联交易报送记录能力，跨域事件监听和事实拼装由 application 承接。
- 金控关联交易关联方名单同步原先在 `RiskControlGljyReportService` 中直接用 `ClientMapper` 按统一社会信用代码解析客户 ID；本轮已扩展 `RiskControlGljyReportExternalPort`，由 application adapter 查询 customer 并返回客户 ID 映射。
- 指标 `A10000396_MD001` 原先直接读取 `PaymentActualDetailMapper` 和 `CollectionBaseInfoMapper` 的现金流汇总方法；本轮改为 `RiskControlCashFlowFactPort` 输入，由 application adapter 适配付款总额、首租金实收和租金本金实收三个汇总事实。
- 指标 `JC049/JC050` 原先直接读取 customer 的相关方客户库；本轮改为 `RiskControlClientFactPort.relatedClientIds()` 输入，由 application adapter 查询客户相关方事实，riskcontrol 只消费客户 ID 集合。
- 指标 `ZL002/ZL003/ZL004/ZL005`、`MD002/MD003`、`JC47548/JC47558` 和 `JC030/JC031` 原先直接读取 customer 的客户地域、风控行业分类和所属集团事实；本轮改为 `RiskControlClientFactPort` 输入，由 application adapter 查询 customer 并返回客户集合或客户到集团映射，riskcontrol 只保留剩余本金指标计算公式。
- 风控行业分类系列指标、`FJC47608` 和区域风险限额指标原先直接读取 customer 的工商版本表来筛选风控行业分类客户；本轮改为 `RiskControlClientFactPort` 输入，riskcontrol 只消费客户 ID 集合或客户 ID 到行业码映射。
- 风控策略详情和项目/付款拦截原先直接读取 customer 的二级行业字典和客户风控行业分类；本轮改为 `RiskControlClientFactPort` 输入，策略服务不再直接依赖 customer mapper/model。
- 金控关联方名录导入原先直接读取 customer 生效客户来按统一社会信用代码匹配客户 ID；本轮改为 `RiskControlClientFactPort.activeClientIdsByCreditCodes` 输入，关联方服务不再直接依赖 customer。
- 风险集中度客户维度原先由 `RiskControlConcentrationClientService` 直接读取 customer 工商、地址和客户主表；本轮改为 `RiskControlClientFactPort.concentrationClientFacts` 输入，application adapter 负责装配 `RiskControlConcentrationClientFact`，集中度服务不再直接依赖 customer mapper/model。
- 关联方交易列表原先由 `RiskControlRelatedClientService` 直接分页读取 payment/collection 的已核销付款和收款记录；本轮改为 `RiskControlRelatedTransactionPort` 输入，application adapter 保留原查询条件、排序和字段映射，关联方服务只负责风险关联方信息和响应组装。
- 指标 `A10000396_ZL044` 原先直接读取合同版本库，统计快照日前“仅一份合同客户”的最大授信金额；本轮改为 `RiskControlContractFactPort.maxSingleContractClientApplyCreditAmountBefore` 输入，application adapter 保留原分组和金额计算口径。
- 区域风险限额指标原先直接读取项目评审版本和合同版本库来找区域分类下的最新合同；本轮扩展 `RiskControlProjectReviewFactPort.newestContractIdsByClientIdsAndRegionalClassifies`，application adapter 保留原项目评审/合同版本过滤口径，风控域只消费合同 ID 集合并保留剩余本金/保证金算法。
- 风控策略拦截原先在 `RiskControlStrategyService` 里直接读取项目立项、项目价格和付款申请持久化模型；本轮改为 `RiskControlInterceptFactPort` 输入，只向风控域提供客户 ID 和本次申请金额。
- 客户监控相关接口当前放在 application：列表和统计读取 customer，预警/舆情详情读取 riskcontrol。`customer` 资源目录中有 `customer_monitoring.sql` 注册 `/clientMonitor/*` 菜单权限点，说明客户监控是客户事实和风险监控的跨域页面，不应继续沉到 customer 单域。客户监控列表所需的风险客户集合、红黄灯数量和舆情数量已由 riskcontrol mapper/service 提供，customer mapper 不再直接读取风险监控表。
- `sql/风控应用/dml.sql` 中包含黑灰名单菜单和功能数据，说明历史上风控应用和黑灰名单 UI/菜单存在重叠，但这不是把 `blackgray` 合进 `riskcontrol` 的理由。
- 反向依赖主要来自 `application`、`dashboard`、`web`、`api`，另有少量 `customer`、`projectprocess`、`credit`、`contract`、`payment`、`budget`、`metric` 等：`application` 编排风控流程、job、看板和客户监控，`dashboard`/`web` 消费风险监控和看板入口。`metric -> riskcontrol.strategy` 的直接读取已通过 `RiskStrategyCurrentValuePort` 和 application adapter 完成第一轮隔离。

## 模块判断

`riskcontrol` 不适合继续合并 `credit`、`rating`、`assetclassify`、`blackgray` 等风险相邻模块。它已经是一个风险运营平台，如果再吸收相邻模块，会进一步变成大而全的风险桶。

更合理的方向是保持独立，但收敛外域依赖：

- 客户、项目、合同、付款、收款事实逐步改为风险输入模型或查询 port；资产分类事实已完成第一轮 port 隔离。
- payment 事件监听和合同/项目上下文拼装应放在 `application`；当前金控关联交易报送的付款核销监听已迁出 riskcontrol。
- 简单现金流汇总事实优先通过 `RiskControlCashFlowFactPort` 输入；当前 `MD001` 指标已不再直接注入 payment/collection mapper。
- 项目立项、付款申请等拦截入口只应给风控域传入“客户 + 金额”事实；当前 `RiskControlStrategyService` 已不再直接依赖 payment/projectprocess mapper/model。
- workflow 动态表单、流程查询、流程变量和流程结束适配应留在 `application`，风控模块只暴露风险处置业务方法；动态表单、预警流程查询和处置变量写入已完成第一轮迁出。
- 指标域和风控域不应互相吞并；当前 `metric -> riskcontrol.strategy` 已通过 `RiskStrategyCurrentValuePort` 输入和 application adapter 隔离，后续重点转向 riskcontrol 自身对客户、项目、合同、付款、收款、资产分类事实的直接读取。
- `strategy/metric`、`warning/opinion`、`concentration/relation`、`report`、`scorecard` 在模块内部继续保持清晰分区；若后续拆分，优先从这些边界切入。
