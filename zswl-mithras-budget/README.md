# zswl-mithras-budget

`zswl-mithras-budget` 是预算与 ECL 相关业务模块，负责预算计划、预算执行、收益测算、付息/付款明细、ECL 预测配置与执行记录。

本模块的核心语义是“经营预算、预测与预算执行管理”。项目、KPI、财务、合同、付款和收款是它计算所需的外部事实输入；其中合同归属事实通过 `BudgetContractFactPort` 由 application 适配到 contract，收款事实通过 `BudgetCollectionFactPort` 由 application 适配到 collection，合同付款事实和付款实缴事实通过 `BudgetPaymentFactPort` 由 application 适配到合同、付款实际和收款计划的跨域读模型，财务风险/利润事实通过 `BudgetFinanceFactPort` 由 application 适配到 finance，年度 KPI 绩效目标通过 `BudgetKpiFactPort` 由 application 适配到 kpi，预算考核审批流通过 `BudgetExamineWorkflowPort` 由 application 适配到 flow-core，预算自身持有预算审批状态。

本轮复核统计到约 155 个 Java 文件，包结构集中在 `cn.zswltech.mithras.budget` 单根包下。当前 POM 中只保留 `api`、`foundation` 和必要框架依赖；`projectprocess`、`finance`、`contract`、`payment`、`collection`、`workflow`、flow-core、`kpi` 直接依赖已移除。静态搜索确认 `zswl-mithras-budget/src/main/java`、`src/main/resources` 和 `pom.xml` 中已无 `cn.zswltech.mithras.projectprocess`、`zswl-mithras-projectprocess`、`cn.zswltech.mithras.finance`、`zswl-mithras-finance`、`FinanceProjectProfitDetail`、`FinanceSubjectBalanceAssist`、`FinanceBcmBalanceMf`、`cn.zswltech.mithras.contract`、`zswl-mithras-contract`、`ContractBaseInfo`、`ContractBaseInfoService`、`cn.zswltech.mithras.payment`、`zswl-mithras-payment`、`PaymentActualDetail`、`PaymentActualDetailMapper`、旧 `ContractPayInfoDTO`、`cn.zswltech.mithras.kpi` 或 `zswl-mithras-kpi` 残留；预算考核执行服务中也已无 `projectprocess` mapper/model 直接引用，项目立项数量和项目评审 FTP 分类通过 `BudgetProjectFactPort` 输入，年度 KPI 绩效目标通过 `BudgetKpiFactPort` 输入。

预算域已经自持 FTP 行业分类口径：`BudgetFtpIndustryCategory` 保留与历史编码一致的枚举值，参数配置服务接收预算分类编码或预算本域枚举，不再为了风险准备金/FTP 参数读取复用 `projectprocess` 的 FTP 行业分类枚举。付款预算现金流导入也已改用预算自有 `BudgetCashFlowExcelImporter` 和 `BudgetCashFlowExcelModel`，不再复用项目过程域的 Excel importer/model。

POM 框架依赖也有源码证据：`mybatis-plus-boot-starter` 用于 BaseMapper、Wrappers、ServiceImpl 和分页；`hutool-all` 用于 BeanUtil、CollectionUtil、StrUtil、JSONUtil、SpringUtil 等工具；`spring-web` 用于 controller；`xxl-job-core` 用于 `BudgetPlanPayWeeklyJob` 的 `@XxlJob`。校验、注解和 Swagger 类型目前通过既有依赖链可编译，后续如要继续显式化 direct dependency，可统一按模块规范处理。

资源层主要围绕 `budget_*`、ECL 预测与预算考核表，Mapper XML 大多只读写预算自有表。历史 SQL 中夹带 `finance_bcm_balance_mf` 建表和 `performance_base_info` 字段变更，属于脚本归属不纯，不代表 budget 应拥有 finance 或 kpi 表；`ecl` SQL 中写 `bifrost_menu`、`bifrost_function`、`bifrost_custom_tree_menu_ref`，属于平台菜单初始化耦合。ECL 预测配置校验使用 budget 自有 `BudgetEclConfigEnum` 和 `BudgetEcl*BO` JSON 结构；KPI 全局 ECL 配置仍由 application 装配复制到预测配置，不再让 budget 直接依赖 KPI 包。

反向依赖较少，按 Java/POM/Mapper/SQL 粗略搜索，主要消费方是 `web` 约 181 处、`application` 约 119 处、`rating` 约 15 处。这说明 budget 更像自足业务域，而不是底座能力或交易事实源。

结论：保持独立，不并入 `finance` 或 `kpi`。预算使用财务项目利润、KPI 指标、项目投放、付款核销、合同归属和收款剩余本金作为计算输入，但它拥有独立的预算计划、预算执行、预算考核、周报和 ECL 预测生命周期；外部输入现在通过预算自有 port 或 application 装配表达，不再直接依赖外部业务模块。

后续整理重点是识别预算自身规则与外域数据读取，减少对项目、KPI、财务、合同、付款、收款等模块的直接业务耦合。其中预算收益测算原先直接使用 payment 的 `ContractPayInfoDTO` 和 `PaymentActualDetailMapper.listContractPayInfo*`，预算考核执行原先直接读取 `PaymentActualDetailMapper`、contract 的 `ContractBaseInfoService`/`ContractBaseInfo`、finance 的项目利润/风险辅助 mapper/service、projectprocess 的项目立项/定价 mapper/model 以及 KPI 的年度绩效目标 service/model，现在都已收敛为预算自有 `BudgetContractPaymentFactSnapshot`、`BudgetPaymentActualSnapshot`、`BudgetPaymentFactPort`、`BudgetContractFactSnapshot`、`BudgetContractFactPort`、`BudgetFinanceProjectProfitSnapshot`、`BudgetFinanceFactPort`、`BudgetProjectFactPort`、`BudgetPerformanceTargetSnapshot` 与 `BudgetKpiFactPort`。application 侧承接合同、付款实际、收款计划、财务利润/风险事实、项目事实、KPI 年度目标和 KPI 全局 ECL 配置的跨域读取与转换。
