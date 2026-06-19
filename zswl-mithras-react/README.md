# Mithras React 前端

`zswl-mithras-react` 是 Mithras 系统的前端工程，承载客户、项目、合同、付款、收款、风控、报表、流程、文件等业务页面和用户交互。

前端的核心语义是“用户界面与前端交互编排”。业务规则、审批状态、权限判断和数据一致性以后端接口为准；前端负责展示、输入校验、页面流转、轻量状态管理和操作反馈。

## 分层边界

前端目录不需要和后端 Maven 模块一比一对应。前端更贴近菜单、路由和用户工作流，但代码复用关系必须清楚。

- `src/pages`：路由入口和页面私有代码。页面目录内的组件、配置、`api.js` 默认只服务当前页面或当前业务域内部。
- `src/components`：跨业务域复用的通用 UI、表格、表单、金额、文件、流程图、操作按钮等组件。
- `src/api`：跨页面复用的接口封装。若某个 `pages/**/api.js` 被其他业务域引用，应迁入这里或对应稳定领域入口。
- `src/utils`：无页面语义、无业务归属的工具函数、格式化、hooks、校验逻辑。
- `src/utils/domains/<domain>`：构建器允许目录下的业务域工具稳定入口，用来承接历史 `utils/*` 中已有明确业务归属的小工具；它不是新的公共杂物区。
- `src/utils` 中直接依赖 `src/api/common` 的文件应只承载公共前端基础能力，例如文件下载、表格列配置、通用字典 hook；若出现业务域 API 调用，应迁入对应业务域目录。
- `src/components/Actions` 等通用动作组件若需要承接动态 URL 的删除、导入、导出，应使用 `src/api/common/actionApi`，不要在 UI 组件中直接调用请求库。
- `src/layout`：应用壳能力，包括菜单、消息、登录、布局和全局导航交互。

后续若新增 `src/features/<domain>`，它应承载某个业务域可复用的前端能力，例如领域组件、领域 hooks、领域配置和领域 API 包装；`pages` 只作为路由装配层使用。

## 领域入口

跨业务域复用组件时，调用方应优先依赖被调用领域暴露的稳定入口，而不是直接引用对方内部目录。

- 推荐：`@/components/Contract/ApplicationDetailEntries`
- 推荐：`@/components/Project/ReviewDetailEntries`
- 推荐：`@/components/Credit/ReviewEntries`
- 避免：`@/components/Contract/Detail/BaoJia`
- 避免：`@/components/Project/ReviewDetail/store`
- 避免：`@/components/Kpi/ProjectAllot/Column`

`*Entries.js` 的语义是“当前领域愿意暴露给外部复用的前端能力清单”。它只做 re-export，不承载业务逻辑；真正实现仍留在原领域目录内。

业务域内部实现文件不要从自己的 `*Entries.js` 反向导入；域内复用应使用相对路径。`*Entries.js` 只作为外部稳定入口使用。

当前已有领域入口：

- `AfterLease/AdjustEntries.js`
- `AfterLease/Level5ClassifyDetailEntries.js`
- `AfterLease/Level5ClassifyListEntries.js`
- `AfterLease/Level5ClassifyTipsEntries.js`
- `AfterLease/PolicyManageDetailEntries.js`
- `AfterLease/PolicyManageRemindEntries.js`
- `AfterLease/RentCollectionApiEntries.js`
- `AfterLease/RentCollectionDetailEntries.js`
- `AfterLease/RentCollectionListEntries.js`
- `AfterLease/RentCollectionProcessEntries.js`
- 租后回款选择卡片已归入 `src/components/AfterLease/RentCollection/CheckCard`，不再作为公共根组件使用。
- `AfterLease/CheckPlanCreateEntries.js`
- `AfterLease/CheckPlanDetailEntries.js`
- `AfterLease/CheckPlanExternalEntries.js`
- `AfterLease/CheckPlanPrepareEntries.js`
- `AfterLease/CheckPlanTemplateEntries.js`
- `Archives/ArchivesEntries.js`
- `BaseData/FileTemplateEntries.js`
- `BaseData/LeaseholdPropertyEntries.js`
- `BlackGray/BlackGrayEntries.js`
- `BlackGray/BlackGrayHitEntries.js`
- `Budget/AccountsReceivableEntries.js`
- `Budget/ExchangeRateEntries.js`
- `Budget/IncomeShareTableEntries.js`
- `Budget/PricingBaseDataEntries.js`
- `Budget/PricingBaseSetEntries.js`
- `Budget/PricingBusinessEntries.js`
- `Budget/PricingFtpInterestEntries.js`
- `Budget/ProfitDistributionEntries.js`
- `Budget/ProjectProfitEntries.js`
- `Budget/ProvisioningEntries.js`
- `BudgetManagement/BudgetManagementEntries.js`
- `BusinessInfoCheck/BusinessInfoCheckEntries.js`
- `Chart/BarChartEntries.js`
- `Chart/LineChartEntries.js`
- `Chart/TooltipEntries.js`
- `ClientMaterialTable/BusinessMaterialTableEntries.js`
- `ClientMaterialTable/ClientMaterialTableEntries.js`
- `Contract/ApplicationDetailEntries.js`
- `Contract/BaseInfoEntries.js`
- `Contract/ConfigEntries.js`
- `Contract/LeaseMaterialsEntries.js`
- `Contract/ListDetailEntries.js`
- `Contract/MaterialsEntries.js`
- `Contract/ProcessDetailEntries.js`
- `Cpm/CpmEntries.js`
- `Cpm/PaymentApplicationDetailEntries.js`
- `Cpm/PaymentApplicationMaterialsEntries.js`
- `Cpm/PaymentApplicationPublicCheckEntries.js`
- `Cpm/PaymentApplicationPublicInfoEntries.js`
- `Credit/CreditListEntries.js`
- `Credit/CreditReportSearchEntries.js`
- `Credit/EstablishEntries.js`
- `Credit/ReviewEntries.js`
- `Credit/SearchListEntries.js`
- `Credit/SearchModalEntries.js`
- `CreditManage/CreditManageEntries.js`
- `Customer/ApplyPermissionEntries.js`
- `Customer/CustomerRatingDetailEntries.js`
- `Customer/CustomerRatingListEntries.js`
- `Customer/CustomerRatingUploadEntries.js`
- `Customer/DebtRatingDetailEntries.js`
- `Customer/DebtRatingListEntries.js`
- `Customer/ExternalPublicInfoEntries.js`
- `Customer/HandoverEntries.js`
- `Customer/MaintainEntries.js`
- `Customer/MonitoringDetailEntries.js`
- `Customer/MonitoringEntries.js`
- `Customer/SingleViewRiskEntries.js`
- `Customer/UnifiedViewDetailEntries.js`
- `Customer/UnifiedViewEntries.js`
- `Dashboard/DashboardEntries.js`
- `Dashboard/MyAchievementEntries.js`
- `EvaluationAgency/AppraisalAgencyEntries.js`
- `EvaluationAgency/EvaluationAgencyEntries.js`
- `ChangeLogDiff/ChangeLogDiffEntries.js`
- `ExternalEmbed/ExternalEmbedEntries.js`
- `ExternalEmbed/RzyEntries.js`
- `Financial/ChangeLogEntries.js`
- `Financial/DirectDetailEntries.js`
- `Financial/FinancingCarryInterestEntries.js`
- `Financial/FinancingUrlEntries.js`
- `Financial/FundDetailEntries.js`
- `Financial/FundListEntries.js`
- `Financial/FundProcessEntries.js`
- `Financial/OrgEditModalEntries.js`
- `Financial/PaymentBatchApprovalEntries.js`
- `Financial/PaymentDetailEntries.js`
- `Financial/PaymentListEntries.js`
- `Financial/PropertyEntries.js`
- `Financial/SelectEntries.js`
- `Ocr/OcrEntries.js`
- `FilingMaterials/AfterApplyEntries.js`
- `FilingMaterials/ApplyEntries.js`
- `FilingMaterials/FundApplyEntries.js`
- `FilingMaterials/OtherApplyEntries.js`
- `Kpi/BaseSetModalDetailEntries.js`
- `Kpi/BusinessGoalEntries.js`
- `Kpi/KpiEstimationEntries.js`
- `Kpi/PmAssessEntries.js`
- `Kpi/ProjectAllotDetailEntries.js`
- `Kpi/ProjectAllotFormEntries.js`
- `Kpi/ProjectAllotListEntries.js`
- `Lease/ApprovalConfirmEntries.js`
- `Lease/MaintainEntries.js`
- `LifeCycle/LifeCycleEntries.js`
- `Message/NotificationEntries.js`
- `Overdue/OverdueEntries.js`
- `PaymentFtpColumns/FtpAssessmentColumnsEntries.js`
- `PaymentFtpColumns/PaymentFtpColumnsEntries.js`
- `Permission/BifrostEntries.js`
- `InsurancePolicy/InsurancePolicyColumnsEntries.js`
- `InsurancePolicy/InsurancePolicyEntries.js`
- `Process/ProcessInfoModalEntries.js`
- `Process/ProcessEntries.js`
- `Process/ProcessTaskFlowChartEntries.js`
- `Project/ClientSelectEntries.js`
- `Project/EstablishmentDetailEntries.js`
- `Project/FinancialReportStatisticsEntries.js`
- `Project/FormListItemEntries.js`
- `Project/PriceDetailEntries.js`
- `Project/ProjectSelectEntries.js`
- `Project/ProjectReviewMeetingModalEntries.js`
- `Project/ReviewDetailEntries.js`
- `Project/ReviewMeetingEntries.js`
- `Project/ReviewProcessEntries.js`
- `Project/ReviewSnapshotEntries.js`
- `Report/FinancialReportApprovalEntries.js`
- `Report/BiViewEntries.js`
- `Report/FinancialReportListEntries.js`
- `Report/OperationEntries.js`
- `Risk/ConcentrationControlEntries.js`
- `Risk/MetricValueEntries.js`
- `Risk/MonitorEarlyEntries.js`
- `Risk/OverdueEntries.js`
- `Risk/PublicMonitorColumnsEntries.js`
- `Risk/PublicMonitorDetailEntries.js`
- `Risk/PublicMonitorListEntries.js`
- `Risk/RelateMonitorEntries.js`
- `Risk/SourceCardEntries.js`
- `TrackEvent/TrackEventDetailEntries.js`
- `TrackEvent/TrackEventModalEntries.js`
- `TrackEvent/TrackEventListEntries.js`
- `TrackEvent/TrackEventTaskEntries.js`
- `TrackEvent/TrackEventEntries.js`
- `VisitorManage/VisitorManageEntries.js`
- `WhiteList/WhiteListEntries.js`

## 依赖规则

- 禁止一个业务页面长期直接复用另一个业务页面目录下的组件、配置或 API。
- `src/pages/<domainA>` 引用 `src/pages/<domainB>` 时，应先判断被引用内容是否应迁到 `src/components`、`src/api`、`src/utils` 或未来的 `src/features/<domain>`。
- 工作台、报表、流程这类聚合页面可以编排多个业务域页面，但聚合逻辑应显式，不能让聚合页面变成公共组件库。
- 生命周期和看板属于用户视角聚合页，可以显式装配租后、风控、客户等领域提供的稳定入口；这类依赖应保留在 `page aggregation` 报告中持续观察，不应为追求报表归零而把业务组件搬进聚合页。
- `process` 负责流程壳和审批交互；业务详情组件应尽量由业务域提供，再由流程页面进行装配。
- 通用组件应保持无业务名称、无菜单名称、无特定页面状态依赖。
- 若确实需要跨业务域复用能力，先在被调用领域新增或复用 `*Entries.js`，再由调用方引入。
- 禁止直接跨域引用组件内部的 `api`、`store`、`Column`、`Config`、`context` 等私有文件。
- 禁止在 `.less` 中通过 `@/components/<domain>/...` 引用业务域组件内部样式；路由兼容壳应只做 JS 转发，样式由真实组件自己维护。
- 已收敛到领域入口的共享业务组件禁止再通过组件根目录直连，例如黑灰名单管理应通过 `BlackGray/BlackGrayEntries.js` 引入，黑灰名单命中标识应通过 `BlackGray/BlackGrayHitEntries.js` 引入，保单列配置应通过 `InsurancePolicy/InsurancePolicyColumnsEntries.js` 引入，业务资料表应通过 `ClientMaterialTable/BusinessMaterialTableEntries.js` 引入，征信查询抽屉应通过 `Credit/CreditReportSearchEntries.js` 引入，评估机构关系表应通过 `EvaluationAgency/AppraisalAgencyEntries.js` 引入，FTP考核列配置应通过 `PaymentFtpColumns/FtpAssessmentColumnsEntries.js` 引入，跟踪事项弹窗应通过 `TrackEvent/TrackEventModalEntries.js` 引入，跟踪事项新增任务弹窗应通过 `TrackEvent/TrackEventTaskEntries.js` 引入，`BusinessInfoCheck`、`ClientMaterialTable`、`Dashboard`、`EvaluationAgency`、`ChangeLogDiff`、`InsurancePolicy`、`PaymentFtpColumns` 应通过对应 `*Entries.js` 引入。
- `npm run report:ui-domain-deps` 会把已拆出的窄入口按独立能力归类，例如 `AfterLeaseCheckPlanCreate`、`BlackGrayHit`、`BudgetPricingBusinessDetail`、`ContractApplicationDetail`、`ContractBaseInfo`、`ContractLeaseMaterials`、`CustomerDebtRatingList`、`CustomerExternalPublicInfo`、`CustomerSingleViewRisk`、`FinancialSelect`、`InsurancePolicyInfo`、`InsurancePolicyColumns`、`KpiBaseSetModalDetail`、`ProcessInfoModal`、`ProcessTaskFlowChart`、`ProjectReviewMeetingModal`、`ProjectReviewSnapshot`、`RentCollectionDetail`、`RiskPublicMonitorList`、`RiskSourceCardCalcModal`、`TrackEventDetail`、`TrackEventList`、`TrackEventModal`、`TrackEventTask`；这些报告项代表稳定共享能力，不等同于调用方依赖完整业务域。
- `npm run report:ui-domain-deps` 的输出会区分域实现、流程编排和普通页面聚合。`Stable shared business capabilities` 是已确认可复用的业务能力入口，`still need semantic review` 是仍需逐项判断的真实跨域嵌入，`workflow orchestration` 是流程壳对业务详情的装配，`page aggregation` 是工作台、看板、生命周期视图等普通聚合页面的跨域依赖。普通聚合页面也会继续分为稳定能力使用和待语义评审项；后续重构应优先处理待评审清单，而不是机械追求所有跨域项归零。
- `npm run report:ui-domain-deps` 默认忽略同业务域页面路由壳到本域组件入口的引用，例如 `pages/project` 到 `components/Project`；报告中的 workflow/page aggregation 部分应主要用于观察流程、工作台、跨域页面编排。
- 菜单路由和业务语义不一致时，依赖报告可以按真实语义归一。例如 `pages/lease/tracking` 是跟踪事项路由壳，`pages/customerView` 是客户视图，租后检查计划下的单一视图风险入口归入客户能力；这类归一只影响报告，不改变路由兼容。
- `pages/budgetManagement/provisionForecast` 是预算管理下的历史菜单路径，但页面和接口语义归入预算拨备预测；依赖报告按 `budget` 归一，路由保持兼容。
- `ClientMaterialTable`、`ChangeLogDiff`、`PaymentFtpColumns` 这类横向业务能力不再视为公共基础组件；依赖报告会保留它们的跨域使用关系，后续需要逐项判断是沉淀横向能力还是回收到具体业务域。
- `FileDiff/FileDiffEntries.js` 仅保留为历史兼容入口，新代码应使用 `ChangeLogDiff/ChangeLogDiffEntries.js`。
- `PaymentApplyColumns/PaymentApplyColumnsEntries.js`、`PaymentFtpColumns/PaymentFtpColumnsEntries.js` 仅保留为历史兼容入口，新代码应使用 `PaymentFtpColumns/FtpAssessmentColumnsEntries.js`。
- `Project/ReviewMeetingEntries.js` 仅保留为历史兼容入口，新代码应使用 `Project/ProjectReviewMeetingModalEntries.js`。
- `TrackEvent/TrackEventEntries.js` 仅保留为历史兼容入口，新代码应按场景使用 `TrackEvent/TrackEventListEntries.js`、`TrackEvent/TrackEventDetailEntries.js`、`TrackEvent/TrackEventModalEntries.js` 或 `TrackEvent/TrackEventTaskEntries.js`。
- `ClientFileTable/ClientFileTableEntries.js`、`ClientMaterialTable/ClientMaterialTableEntries.js` 仅保留为历史兼容入口，新代码应使用 `ClientMaterialTable/BusinessMaterialTableEntries.js`。
- `CheckBusiness/CheckBusinessEntries.js` 仅保留为历史兼容入口，新代码应使用 `BusinessInfoCheck/BusinessInfoCheckEntries.js`。
- `Credit/SearchModalEntries.js` 仅保留为历史兼容入口，新代码应使用 `Credit/CreditReportSearchEntries.js`。
- `EvaluationAgency/EvaluationAgencyEntries.js` 仅保留为历史兼容入口，新代码应使用 `EvaluationAgency/AppraisalAgencyEntries.js`。
- `Policy/PolicyEntries.js` 仅保留为历史兼容入口，新代码应使用 `InsurancePolicy/InsurancePolicyEntries.js`。
- `TrackEvent/TrackingEntries.js` 仅保留为历史兼容入口，新代码应使用 `TrackEvent/TrackEventEntries.js`。
- `npm run check:boundaries` 会阻止业务代码重新引用上述历史兼容入口；兼容文件可以保留，但新增和迁移代码必须使用语义入口或窄入口。
- 权限页 Bifrost iframe、用户管理页、字典/组织/角色 iframe 页、功能分组页和操作日志页已沉淀到 `src/components/Permission/BifrostEntries.js`，付款核销收款日面板、合同保证金退款文本展示已回收到各自页面或业务组件私有目录，不再作为公共根组件使用。
- 预算流水组织树选择器已回收到 `src/pages/budget/flowCenter/BankFlow/OrgTreeSelect`，不再作为公共根组件使用。
- 黑灰审批操作信息已回收到 `src/components/BlackGray/Actions/ApprovalOperation`，仍通过 `BlackGray/BlackGrayEntries.js` 对页面暴露；CPM 金额输入已回收到 `src/components/Cpm/AmountNumber`，跨层使用应通过 `Cpm/CpmEntries.js`。
- 项目多行文本展示已回收到 `src/components/Project/MultilineText`，项目页面跨层使用应通过 `Project/EstablishmentDetailEntries.js`。
- Dashboard 分段标签样式组件已回收到 `src/components/Dashboard/RadioTabs`，跨层使用应通过 `Dashboard/DashboardEntries.js`。
- 布局面包屑状态工具已回收到 `src/layout/components/BreadLine`，不再作为公共根组件使用。
- 禁止绕过公共组件稳定入口引用 `Actions/*`、`Form/*`、`Format/*`、`Table/*`、`Chart/tooltip`。
- 风险指标报送顶层 Tabs 页和子页均通过 `src/components/Risk/MetricValueEntries.js` 暴露，`src/pages/risk/metricValue/*` 只保留路由壳。
- 风险评分卡列表页、详情页和试算弹窗通过 `src/components/Risk/SourceCardEntries.js` 暴露，`src/pages/risk/sourceCard/*` 只保留路由壳和历史兼容入口。
- 租赁物维护列表页和详情页均通过 `src/components/Lease/MaintainEntries.js` 暴露，`src/pages/lease/maintain/*` 只保留路由壳。
- 应收账款列表页和详情页均通过 `src/components/Budget/AccountsReceivableEntries.js` 暴露，`src/pages/budget/accountsReceivable/*` 只保留路由壳。
- 收入分摊表列表页和详情页均通过 `src/components/Budget/IncomeShareTableEntries.js` 暴露，`src/pages/budget/incomeShareTable/*` 只保留路由壳。
- 预算拨备数据查询页通过 `src/components/Budget/ProvisioningEntries.js` 暴露，`src/pages/budget/provisioning/dataSearch/*` 只保留路由壳。
- 预算拨备减值列表页和详情页通过 `src/components/Budget/ProvisioningEntries.js` 暴露，`src/pages/budget/provisioning/impairment/*` 只保留路由壳。
- 预算拨备参数配置列表页和详情页通过 `src/components/Budget/ProvisioningEntries.js` 暴露，`src/pages/budget/provisioning/paramsConfig/*` 只保留路由壳。
- KPI 项目分配列表页、分配明细和表单片段通过 `src/components/Kpi/ProjectAllotListEntries.js`、`ProjectAllotDetailEntries.js`、`ProjectAllotFormEntries.js` 暴露，`src/pages/kpi/projectAllot/*` 保留路由壳和历史兼容入口。
- 预算考核列表页和详情页通过 `src/components/BudgetManagement/BudgetManagementEntries.js` 暴露，`src/pages/budgetManagement/assessment/*` 只保留路由壳。
- 预算管理业绩目标是历史菜单路径，实际归入 KPI 业绩目标组件域；列表页和详情页通过 `src/components/Kpi/BusinessGoalEntries.js` 暴露，`src/pages/budgetManagement/businessGoal/*` 只保留路由壳。
- 预算管理参数配置页通过 `src/components/BudgetManagement/BudgetManagementEntries.js` 暴露，`src/pages/budgetManagement/parameterConfig` 只保留路由壳。
- 预算管理参数配置（历史定价基础配置路径）页通过 `src/components/BudgetManagement/BudgetManagementEntries.js` 暴露，`src/pages/budgetManagement/parameterConfiguration` 只保留路由壳。
- 预算管理投放计划列表页、详情页和周报详情页通过 `src/components/BudgetManagement/BudgetManagementEntries.js` 暴露，`src/pages/budgetManagement/placementPlan/*` 只保留路由壳。
- 预算管理成本计划列表页和详情页通过 `src/components/BudgetManagement/BudgetManagementEntries.js` 暴露，`src/pages/budgetManagement/plan/cost/*` 只保留路由壳。
- 预算管理利润计划列表页、详情页和业务明细页通过 `src/components/BudgetManagement/BudgetManagementEntries.js` 暴露，`src/pages/budgetManagement/plan/profit/*` 只保留路由壳。
- 预算管理拨备预测列表页、详情页和配置详情页通过 `src/components/BudgetManagement/BudgetManagementEntries.js` 暴露，`src/pages/budgetManagement/provisionForecast/*` 只保留路由壳。
- 金融产权列表页通过 `src/components/Financial/PropertyEntries.js` 暴露，`src/pages/financial/property` 只保留路由壳。
- 逾期催收列表页、详情页和催收弹窗通过 `src/components/Overdue/OverdueEntries.js` 暴露，`src/pages/overdue/collection/*` 只保留路由壳。
- 禁止从 `@/components` 根目录导入表格族组件，例如 `FileTable`、`NoEnumFileTable`、`VersionTable`、`EditTable`、`EditDescription`、`Summary` 应从 `@/components/Table` 导入。
- 禁止从 `@/components` 根目录导入公共选择器，例如 `ClientSelect`、`FounderSelect`、`OrgSelect`、`ApiSelect`、`ProjectReviewSelect` 应从 `@/components/Select` 导入。
- 禁止从 `@/components` 根目录导入已具备独立入口的默认组件，例如 `PageListDown`、`CommonTips`、`CommonNoData`、`ReadOnly`、`DetailLayout`、`Collapse`、`RegionCascader` 应直接从对应 `@/components/<Component>` 导入。
- 禁止业务代码继续从 `@/components` 根目录导入组件；历史兼容根导出已删除，新增和迁移代码必须依赖具体稳定入口。
- 允许直接从 `@/components/<Component>` 引入的根组件必须是已确认的公共基础组件，例如 `Icon`、`DataUpload`、`RenderColumn`、`FormItemContent`、`FormUpload`、`Excel`、`Amount`、`CommonNoData`、`ReadOnly` 等；新增跨域根组件直连时，应先判断它是公共基础组件，还是应改成某个业务域的 `*Entries.js`。
- 禁止业务代码直接引用 `blackList`、`postRentalInspection`、`riskControl`、`liquidity`、`pricing`、`newFtp`、`financialReport`、`manageReport`、`fillingMaterials`、`workbench`、`header` 等历史 API 目录，应使用对应语义领域入口。
- 禁止页面和组件直接引用 `src/api/**/interface/**` 类型文件；接口类型应由对应的语义 API 包装文件承接，避免页面绑定接口实现层目录。
- 禁止 `src/api/<domain>` 内部跨业务域 import 或 re-export 其他 `@/api/<domain>` 文件；聚合页或流程页需要复用 endpoint 时，在自身语义 API 入口声明所需 endpoint，避免把整个业务域 API 暴露给另一个域。
- 禁止预算应收账款页面和组件直接引用 `financial/accountsReceivable` 历史 API 前缀，应使用 `budget/accountsReceivable` 语义入口。
- 禁止通用选择器直接引用 `groupCredit/common` 历史 API 前缀，应使用 `common/selectApi` 语义入口。
- 禁止在 `src/components/**/api.js` 中只做 `@/api/**` 的一行转发；组件内部应直接引用语义明确的 `src/api` 入口，避免制造假本地 API 边界。
- `npm run check:boundaries` 会扫描整个 `src` 的 JS/TS 源码和 `.less` 样式 import，禁止非 `Entries/entries` 的 `@/components/<domain>/<subpath>` 导入，禁止未登记的组件根目录直连，禁止已收敛共享业务组件的根目录直连，禁止组件域内部反向引用自身 `*Entries.js`，禁止通过 `@/pages/**` 复用页面私有代码，禁止直接引用历史 API 目录，禁止页面和组件直接引用 API interface 类型目录，禁止 `src/api` 内部跨业务域引用，并校验领域级入口已被代码使用且同步记录在 README。

## 当前边界收敛

- 表格、文件表、描述表、审批详情等统一从 `src/components/Table` 稳定入口导入。
- 表单金额、只读表单、银行账号、日期范围等统一从 `src/components/Form` 稳定入口导入。
- 文件导出、模板下载、审批操作等统一从 `src/components/Actions` 稳定入口导入。
- 基于 `TableStore` 当前筛选条件或选中行的导出按钮统一从 `src/components/Actions.StoreExportAction` 使用；黑灰名单动作实现位于 `src/components/BlackGray/Actions`。
- 格式化列、可编辑列、超时展示等统一从 `src/components/Format` 稳定入口导入。
- 财务、预算等外部页面不再从 `dashboard/workbench/components` 取通用表格合计和文件导出能力。
- dashboard 锚点滚动导航已归入 `src/components/Dashboard/AnchorScrollNav`，dashboard 页面通过 `src/components/Dashboard/DashboardEntries.js` 使用。
- `dashboard/workbench/components` 暂时保留工作台内部私有组件；后续只处理确实跨业务域复用的部分。
- 我的流程页签、我的审批页签、流程查询、流程设计、准备列表页与准备详情页已收敛到 `src/components/Process`，路由页仅保留入口装配。
- 白名单列表、详情、列定义已收敛到 `src/components/WhiteList`，路由页仅保留入口装配。
- 组件域之间的跨域能力复用已收敛到领域入口，避免调用方绑定对方内部实现路径。
- 融资机构、融资银行、直融认购选择器实现已收敛到 `src/components/Financial/Select.js`；外部调用统一走 `src/components/Financial/SelectEntries.js`，公共 `src/components/Select` 不再转发财务域选择器。

## 目录语义

前端目录同时受到业务域、菜单路由、历史兼容路径影响，不能简单按后端模块一比一拆分。

当前较清晰的业务域目录：

- `afterLease` / `AfterLease`：租后检查、租后调整、租后回款等。
- `budget` / `Budget`：预算、定价、应收账款、印花税、计提等。
- `budgetManagement` / `BudgetManagement`：预算管理、计划、目标、参数配置等；依赖报告按预算域 `budget` / `Budget` 归一，路由和兼容入口保持不变。
- `contract` / `Contract`：合同详情、起租、提前结清、合同材料等。
- `cpm` / `Cpm`：付款申请、付款核销、保证金、合同付款管理等。
- `credit`、`creditManage` / `Credit`、`CreditManage`：授信审批和授信查询/台账能力；依赖报告按征信/授信域 `credit` / `Credit` 归一，路由和兼容入口保持不变。
- `customer` / `Customer`：客户维护、客户评级、债项评级、客户财报等。
- `financial` / `Financial`：融资、资金、流动性、金融机构、应付利息等。
- `kpi` / `Kpi`：绩效分配、绩效参数、绩效测算等。
- `lease` / `Lease`：租赁物维护和租赁物跟踪。
- `baseData` / `BaseData`：基础维护配置，包括文件模板维护、租赁物类型导入等；路由目录保留历史菜单路径，页面实现已收敛到 `src/components/BaseData/*Entries.js`。
- `project` / `Project`：项目立项、项目定价、项目评审。
- `risk` / `Risk`：风控指标、风险策略、公开监控、评分卡等。
- `report` / `Report`：管理报表、运营报表、内部历史报表等。
- `process` / `Process`：流程中心、流程详情、审批记录、流程准备详情装配。

当前带有聚合或展示面语义的目录：

- `dashboard` / `Dashboard`：工作台、总览、看板、SSO 入口，偏展示与聚合。
- `customerView`：客户单一视图/客户画像聚合页，首页与详情实现已收敛到 `src/components/Customer`，路由页仅保留入口装配。
- `customerMonitoring`：客户监控历史路由壳，当前实现已收敛到 `src/components/Customer/MonitoringEntries.js` 和 `src/components/Customer/MonitoringDetailEntries.js`。
- `lifeCycle` / `LifeCycle`：项目或客户生命周期聚合展示。
- `monitorEarly`：预警监控历史路由壳，当前实现已收敛到 `src/components/Risk/MonitorEarlyEntries.js`。
- `login`：登录和初次登录改密页面，前端路由名保留 `login`，API 语义归属权限认证。
- `msgNotification`：消息中心历史路由壳，当前实现已收敛到 `src/components/Message/NotificationEntries.js`。
- `preview`：PDF、报表预览。
- `visitorManage`：拜访管理历史路由壳，当前实现已收敛到 `src/components/VisitorManage/VisitorManageEntries.js`。
- `financialReport`：财务报表待办/审批/完成列表历史路由壳，当前列表实现已收敛到 `src/components/Report/FinancialReportListEntries.js`，审批详情入口使用 `src/components/Report/FinancialReportApprovalEntries.js`。
- `implant`：外部系统嵌入历史路由壳，当前 iframe 桥接实现已收敛到 `src/components/ExternalEmbed/ExternalEmbedEntries.js`。
- `rzy`：厂商管理外部系统历史路由壳，当前 iframe 展示实现已收敛到 `src/components/ExternalEmbed/RzyEntries.js`，菜单和链接配置保留在 `src/utils/domains/rzy/RzyConfig.js`。

当前历史或兼容壳目录：

- `ProfitDistribution`：项目分润路由壳，业务语义更接近 `budget/projProfit` 或 KPI/预算分润。
- `overdueListSearch`：逾期列表查询历史路由壳，当前实现已收敛到 `src/components/Risk/OverdueEntries.js`。
- `workbench`：工作台兼容入口，实际能力应优先落在 `dashboard/workbench`。
- `blackListManage`：页面目录仍沿用黑名单管理命名，组件目录已是 `BlackGray`；API 调用优先使用 `src/api/blackGray` 兼容入口，后续可考虑菜单路径稳定的前提下收敛命名。
- `postRentalInspection`：租后检查 API 的历史命名目录；租后业务代码优先使用 `src/api/afterLease` 下的兼容入口。
- `riskControl`：风控接口历史命名目录；风险域页面和组件优先使用 `src/api/risk` 下的兼容入口。
- `risk/publicMonitor`：公开监控/舆情监控接口属于风险域；生命周期页只展示风险摘要时使用 `src/api/lifeCycle/riskWarningApi` 聚合入口，流程详情展示或处理舆情监控时使用 `src/api/process/detail/publicMonitorApi` 聚合入口；这些入口只声明自身需要的 endpoint，不再转发风险域 API 文件。
- `liquidity`：金融流动性接口历史命名目录；金融域页面优先使用 `src/api/financial/liquidity` 下的兼容入口。
- `financial/accountsReceivable`：应收账款接口历史落在财务目录；预算应收账款页面和组件优先使用 `src/api/budget/accountsReceivable` 下的语义入口。
- `cpm/payment/contractPaymentFtp`：合同付款 FTP 接口历史落在付款目录；合同付款申请组件优先使用 `src/api/contract/payment/contractPaymentFtp` 语义入口。
- `contract/payment/contractPaymentFtp.modifyContractPayment`：付款核销详情内修改付款 FTP 信息时使用 `src/api/cpm/payment/contractPaymentFtpApi` 付款语义入口，不直接绑定合同付款 API 文件。
- `budget/flowCenter`：银行流水/付款流水中心接口历史落在预算目录；付款核销组件使用 `src/api/cpm/payment/writeOffFlowCenterApi` 语义入口，不再转发预算流水中心 API 文件。
- `afterLease/policyLedgerApi.getPaymentDetail`：付款详情接口历史挂在保单台账 API 中；付款核销详情组件优先使用 `src/api/cpm/payment/paymentDetailApi` 语义入口。
- `lease/trackingApi`：跟踪事项接口历史落在租赁物目录；跟踪事项组件优先使用 `src/api/trackEvent/trackingApi` 语义入口。
- `trackEvent/trackingApi.getTrackEventClose`：关闭跟踪事项是横向动作；项目评审会议纪要内触发时优先使用 `src/api/project/projReviewMeetingMinute` 下的项目语义方法，组件不直接穿透到跟踪事项域。
- `lease/evaluationAgencyApi`：评估机构接口历史落在租赁物目录；评估机构组件、租赁物维护和白名单页面使用各自语义入口。
- `evaluationAgency/evaluationAgencyApi`：租赁物详情维护评估机构时使用 `src/api/lease/evaluationAgencyMaintainApi` 租赁物语义入口，不再转发评估机构 API 文件；评估机构共享组件内部可继续使用评估机构域 API。
- `evaluationAgency/evaluationAgencyApi.postAppraisalQueryCompany`：白名单新增页查询评估机构候选时使用 `src/api/whiteList/appraisalCompanyApi` 语义入口，不再转发评估机构 API 文件；评估机构组件和租赁物维护可继续使用评估机构域 API。
- `whiteList/assessmentWhitelistApi`：评估机构白名单本身保留在白名单域；评估机构组件内选择白名单机构时使用 `src/api/evaluationAgency/assessmentWhitelistApi` 语义入口，不再转发白名单 API 文件。
- `ocr/ocrInvoiceApi`：发票识别接口历史落在 OCR 目录，其中租赁物发票金额校验使用 `src/api/lease/vatInvoiceApi` 语义入口，不再转发 OCR 发票 API 文件。
- `afterLease/assessmentWhitelistApi`：评估机构白名单接口历史落在租后目录；白名单页面和评估机构组件优先使用 `src/api/whiteList/assessmentWhitelistApi` 语义入口。
- `baseData/pricing/baseSet/ftpBaseSet`：FTP 参数设定接口历史落在基础数据目录；预算定价基础设置页面和组件优先使用 `src/api/budget/pricing/baseSet/ftpBaseSet` 语义入口。
- `baseData/ftpMaterialsFile`、`baseData/ftpQuarterlyGuidance`：FTP 定价资料和季度指导接口历史落在基础数据目录；预算定价组件优先使用 `src/api/budget/pricing` 下的语义入口。
- `baseData/bankAccountApi`：我方银行账户接口历史落在基础数据目录；预算银行账户页面优先使用 `src/api/budget/bankAccountApi` 语义入口。
- `budget/pricing/ftpInterestChangeApi`：FTP 计息变更属于预算定价域；流程详情展示 FTP 计息变更时使用 `src/api/process/detail/ftpInterestChangeApi` 聚合入口，不再转发预算定价 API 文件。
- `pricing`：预算定价接口历史命名目录；预算定价页面优先使用 `src/api/budget/pricing` 下的兼容入口。
- `newFtp`：新版 FTP 定价接口历史命名目录；预算定价页面和组件优先使用 `src/api/budget/pricing/ftp` 下的兼容入口。
- `financialReport`、`manageReport`：报表接口历史命名目录；报表页面和组件优先使用 `src/api/report` 下的兼容入口。
- `fillingMaterialsDetail`、`fillingMaterials`：归档资料接口和路由的历史拼写目录；资料归集页面和组件优先使用 `src/api/filingMaterials`、`src/components/FilingMaterials` 下的兼容入口。
- `filingMaterials/otherFilingMaterialsDetail`：其他资料归集接口保留在资料归集域；流程提交前做资料说明校验时使用 `src/api/process/detail/filingMaterialsApi` 聚合入口，不再转发资料归集 API 文件。
- `common/customerOverview`：历史公共实现已清理；dashboard 客户总览页面使用 `src/api/dashboard/customerOverview`，客户视图页面使用 `src/api/customerView/customerOverviewApi` 聚合入口。
- `common/flowList`：历史公共目录已清理；流程任务列表、流程详情、退回节点和随机退回等 `/flow/**` 接口使用 `src/api/process/flowTaskApi`。
- `process/flowTaskApi.getProcessDetail`：预算管理和租赁物组件只读取流程详情状态时，分别使用 `src/api/budgetManagement/processDetailApi`、`src/api/lease/processDetailApi` 本域语义入口，不直接绑定流程任务 API 文件。
- `common/editableCompare`：历史上混合了 FTP、项目、付款和文件比对接口，当前已清理；文件表组件使用 `src/api/common/fileCompareApi`。
- `common/dataList`：历史命名已清理；资料清单上传、下载、预览、项目资料和授信资料列表使用 `src/api/common/materialsApi`。
- `customerView`：客户全景页是聚合展示面；从黑灰、风险预警、区域经济、客户总览读取数据时通过 `src/api/customerView` 下的语义入口，不直接穿透到各业务域生成 API；这些入口已收窄为 endpoint 壳，不再转发其他业务域 API 文件。
- `workbench`：工作台/看板相关接口历史生成目录；真实承载在 `src/api/dashboard/workbench`，dashboard 页面优先使用 `src/api/dashboard` 下的语义入口，历史 `workbench` 入口仅保留兼容转发。
- `utils/dashboard*`：dashboard 专用工具历史落在全局 utils；dashboard 页面和组件优先使用 `src/utils/domains/dashboard/DashboardUtils*`，旧路径仅保留兼容转发。
- `utils/processFlow`：流程详情上下文和动态表单配置历史落在全局 utils；流程页面和流程详情复用组件优先使用 `src/utils/domains/process/ProcessFlowContext`，旧路径仅保留兼容转发。
- `utils/afterLease`、`utils/risk`、`utils/report`、`utils/kpi`、`utils/customer`、`utils/budgetManagement`：业务域小工具历史落在全局 utils；对应业务域页面和组件优先使用 `src/<domain>/*Utils` 语义入口，旧路径仅保留兼容转发。预算拨备预测刷新入口使用 `src/utils/domains/budget/ProvisionForecastUtils`，历史 `src/utils/domains/budgetManagement/BudgetManagementUtils` 仅兼容转发。
- `utils/rzyConfig`：RZY 厂商管理外部系统菜单和链接配置历史落在全局 utils；布局菜单和 RZY 页面优先使用 `src/utils/domains/rzy/RzyConfig`，旧路径仅保留兼容转发。
- `utils/options/financialReport`、`utils/options/ftp`：历史业务选项文件已清理；后续若需要报表或 FTP 定价选项，应放入对应 `src/utils/domains/<domain>` 语义目录。
- `utils/hooks/useGetStatus`：黑灰名单审批状态筛选和按钮可用性历史落在全局 hooks；黑灰名单页面和组件优先使用 `src/utils/domains/blackGray/BlackGrayStatusUtils`，旧路径仅保留兼容转发。
- `components/BlackGray/Actions`：黑灰名单审批动作集合；黑灰名单页面应通过 `src/components/BlackGray/BlackGrayEntries.js` 使用，其他业务域需要通用导出时使用 `src/components/Actions.StoreExportAction` 或其他公共 Actions。
- `components/BlackGray/RiskIframe`：黑灰名单外部查询页面 iframe 适配；黑灰名单页面应通过 `src/components/BlackGray/BlackGrayEntries.js` 使用。
- `components/BlackGray/Info`：黑灰名单命中标识组件；业务页面和组件应通过 `src/components/BlackGray/BlackGrayHitEntries.js` 使用。
- `components/Process/BpmnFlowChart`、`components/Process/TaskFlowChart`：流程图组件；流程详情、流程弹窗和审批记录通过 `src/components/Process/ProcessEntries.js` 使用。
- `components/Financial/ChangeLogLayout`：财务版本变更日志布局；财务付款/融资日志页面通过 `src/components/Financial/ChangeLogEntries.js` 使用。
- `process/flowExecution`：流程执行接口是流程中心通用能力；业务组件提交自身审批时使用本业务域的语义入口，例如客户评级使用 `src/api/customer/customerRat/customerRatApprovalApi`，不再转发流程 API 文件。
- `customer/customerRat/customerRatApi`：客户评级页面和客户组件保留客户域 API；项目立项/评审更新评级信息使用 `src/api/project/ratingApi`，项目接口实现不再挂在客户评级 API 内。
- `customer/customerRat/customerRatApi`、`customer/customerRat/debtRatApi`：流程详情展示评级摘要时使用 `src/api/process/detail/customerRatingApi` 和 `src/api/process/detail/debtRatingApi` 聚合入口，不再转发客户评级 API 文件。
- `customer/customerRat/customerRatApi`：流程操作中执行评级推翻等审批动作时使用 `src/api/process/operation/customerRatingOperationApi`，不再转发客户评级 API 文件。
- `customer/maintainApi`：客户维护页和客户组件保留客户域 API；流程申请列表占用客户后跳转详情时使用 `src/api/process/application/customerMaintainApi`，不再转发客户维护 API 文件。
- `utils/customerRat`：客户评级工具历史落在全局 utils；客户评级和流程操作优先使用 `src/utils/domains/customer/CustomerRatUtils`，旧路径仅保留兼容转发。
- `customer/clientBasic`：客户维护基础信息 API 保留在客户域；行业、区域等 `/select` 字典优先使用 `src/api/common/selectApi`。
- `credit/creditReportApi.postCompareBusiness`：征信查询下的工商信息比对接口可由共享 `BusinessInfoCheck` 组件本地 `api.js` 聚合，调用方不应因此直接绑定征信域 API。
- `utils/paymentApplication`：付款申请校验工具历史落在全局 utils；付款组件和流程操作优先使用 `src/utils/domains/cpm/PaymentApplicationUtils`，旧路径仅保留兼容转发。
- `cpm/payment/paymentApplicationDetail`：付款申请详情接口保留在付款域；流程详情展示付款资料时使用 `src/api/process/detail/paymentApplicationDetailApi` 聚合入口，不再转发付款申请 API 文件。
- `cpm/payment/paymentApplicationDetail`、`cpm/payment/publicInfoApi`：流程操作中执行付款申请前置校验或公开信息提交校验时使用 `src/api/process/operation` 下的聚合入口，不直接绑定付款域 API 文件。
- `approval/processModifyRemarkApi`：流程变更/复议说明是审批横向能力；共享审批组件使用 `src/api/common/approvalRemarkApi`，业务详情组件使用本业务域的 `approvalRemarkApi` 固定权限码入口，不再转发公共审批备注 API 文件。
- `contract/baseInfo`：合同基础信息接口本身保留在合同域；预算域取合同候选信息时使用 `src/api/budget/contractInfoApi`，跟踪事项选择项目/合同候选信息时使用 `src/api/trackEvent/contractInfoApi`，不再转发合同 API 文件。
- `contract/contractDetail`：合同详情接口保留在合同域；流程详情展示合同相关资料时使用 `src/api/process/detail/contractDetailApi` 聚合入口，不再转发合同详情 API 文件。
- `overdue/collectionManagementApi`、`overdue/sealForDocumentsApi`：逾期催收和用印资料接口保留在逾期域；流程详情展示逾期催收或诉讼用印资料时使用 `src/api/process/detail` 下的聚合入口，不再转发逾期域 API 文件。
- `project/projReviewDetail`、`project/projReviewMeetingMinute`：项目评审详情和会议纪要接口保留在项目域；流程详情展示项目评审资料或会议纪要时使用 `src/api/process/detail` 下的聚合入口，不再转发项目域 API 文件。
- `project/projReviewFinancialReport`：项目评审财务报表完整性接口保留在项目域；流程操作提交前校验时使用 `src/api/process/operation/projectReviewFinancialReportApi`，不再转发项目域 API 文件。
- `financial/fundApi.getLprLast`：最新 LPR 接口历史挂在融资 API；合同报价利率组件使用 `src/api/contract/priceApi` 语义入口。
- `header/projProfitTool`：全局入口触发的利润测算工具接口历史目录；真实承载在 `src/api/layout/projProfitToolApi`，历史 `header` 与 `kpi/projProfit/profitCalculateTool` 入口仅保留兼容转发。
- `common/irrGenerationApi`：IRR/现金流生成工具是全局 layout 工具弹窗能力；工具弹窗使用 `src/api/layout/irrGenerationApi` 语义入口，不直接绑定 common API 文件。
- `kpi/projProfit`、`kpi/baseSet/parameterConfig`：项目分润接口历史落在 KPI 目录；预算分润页面优先使用 `src/api/budget/projectProfit*` 语义入口。
- `message/messageNotification`：消息中心页面保留消息域 API；全局 layout 消息弹窗使用 `src/api/layout/messageApi`，dashboard 工作台消息列表使用 `src/api/dashboard/workbenchMessageApi`，不再转发消息中心 API 文件。
- `permission/login`：登录页保留权限域登录 API；全局 layout 快速切换登录使用 `src/api/layout/fastLoginApi`，不再转发登录 API 文件。
- `common/workbenchApi`：历史上混合了用户自定义配置和费控 SSO 授权，当前已清理；dashboard 工作台保存/查询用户配置使用 `src/api/dashboard/userCustomConfigApi`，通用表格列配置工具保留 `src/api/common/userCustomConfigApi`，费控 SSO 授权使用 `src/api/dashboard/feikongSsoApi`。
- `groupCredit/common`：历史上承载了创建人、组织、客户等通用选择接口；通用选择器优先使用 `src/api/common/selectApi` 语义入口。
- `groupCredit/projectApproval*`：集团授信立项接口历史生成目录；授信域页面和组件优先使用 `src/api/credit/groupCreditEstablish*` 语义入口。
- `ocr/list`、`ocr/recognition`：OCR 发票/车证列表、比对和上传识别实现已收敛到 `src/components/Ocr`，路由页只引用稳定组件入口；发票/车证 API 仍保留在 OCR 生成入口，后续若拆分租赁物语义 API 需另行评估。
- `cvicse`、`student`：当前代码树中已不存在；后续若恢复这些外部系统或实验路由，需先确认菜单、权限和外部链接来源。
- `demo`：本地实验/脚手架目录，含注释示例和硬编码调试地址；不作为业务域边界判断输入。

目录整理原则：

- 路由目录改名必须先确认菜单、权限、后端路由配置和外部链接，不做纯前端局部改名。
- 小型兼容壳目录可以先保持路径，内部通过稳定入口引用真实业务能力。
- 聚合页不要沉淀公共业务能力；一旦被其他业务域复用，应迁入业务域组件或领域入口。
- 聚合页的 `src/api` 入口只保留自身需要的 endpoint 壳，不 re-export 其他业务域完整 API。
- 优先整理新增代码和高频复用能力，历史路由壳只在确认无外部依赖后逐步收敛。

## 整理优先级

1. 先统计真实 import 关系，优先处理被跨域引用次数高的 `pages/**` 代码。
2. 先迁移语义清楚、行为稳定的公共组件和工具，避免大规模搬目录。
3. API 入口逐步从 `pages/**/api.js` 收敛到 `src/api/<domain>`。
4. 对 `process`、`dashboard`、`report` 这类聚合页面，先明确它们是“编排/展示面”，再拆出被复用的业务能力。
5. 每轮整理后进行构建验证，并避免无关格式化和大面积重排。

## 项目命令

- `npm run start`：启动本地开发服务。
- `npm run build`：构建当前环境。
- `npm run buildAll`：执行多环境打包。
- `npm run api`：根据 `admin.config.js` 中的 YApi 配置生成接口。
- `npm run page`：生成页面脚手架。
- `npm run check:boundaries`：检查是否存在跨域深层组件路径、组件私有文件引用、公共组件子路径引用、组件域自引用 `*Entries.js`、`@/pages/**` 页面私有代码复用、`src/api` 内部跨业务域引用、未使用或未记录的领域级入口。
- `node scripts/report-component-entry-deps.js`：输出 `src` 内页面、组件、工具等对组件领域稳定入口形成的依赖关系，并对已确认的历史路由壳目录做领域归一化，用于判断后续边界整理优先级。
- `node scripts/report-api-domain-deps.js`：输出页面、组件、工具等业务使用方对跨域 API 的依赖关系；`src/api/**` 内的语义兼容入口作为实现细节跳过，用于识别需要收敛到领域组件、领域入口或 `src/api/<domain>` 的候选点。

## 项目约定

1. 金额展示使用千分位并保留 2 位小数；表格列中的金额右对齐。
2. 按钮权限根据后端返回的接口标识判断，没有权限时隐藏或禁用。
3. 客户名称、项目主办、项目协办、业务部门、风控经理等公共下拉优先使用 `src/components/Select`。
4. 描述组件、文件表、合计表和审批详情优先从 `src/components/Table` 导入。
5. 文件分组列表使用 `src/components/Table` 导出的 `FileTable`；普通文件列表使用 `NoEnumFileTable`。
6. 表单编辑能力优先从 `src/components/Format` 导入。
7. 详情页锚点布局优先使用 `src/components/DetailLayout`。

## 外部信息

- 代码地址：http://gitlab.zswl.cn:8888/frontend/mithras-react/
- 公司前端规范：http://wiki.zswltech.cn:8888/pages/viewpage.action?pageId=2818303
- Jenkins 发布地址：http://172.16.200.57:8099/view/%E7%A7%9F%E8%B5%81/job/mithras-frontend/
