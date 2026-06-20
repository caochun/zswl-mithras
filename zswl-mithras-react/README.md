# Mithras React 前端

`zswl-mithras-react` 是 Mithras 系统的前端工程，承载客户、项目、合同、付款、收款、风控、报表、流程、文件等业务页面和用户交互。

前端的核心语义是“用户界面与前端交互编排”。业务规则、审批状态、权限判断和数据一致性以后端接口为准；前端负责展示、输入校验、页面流转、轻量状态管理和操作反馈。

## 分层边界

前端目录不需要和后端 Maven 模块一比一对应。前端更贴近菜单、路由和用户工作流，但代码复用关系必须清楚。

- `src/pages`：路由入口。当前路由 JS/TS 文件应只做 `@/components/<Domain>/*Entries` 转发，不承载真实实现；样式、资源和页面私有实现跟随真实组件目录维护，不能继续放在 `src/pages` 下。
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

- 推荐：`@/components/Contract/ApplicationDetailPageEntries`
- 推荐：`@/components/Project/ReviewDetailPageEntries`
- 推荐：`@/components/Credit/ReviewDetailEntries`
- 避免：`@/components/Contract/Detail/BaoJia`
- 避免：`@/components/Project/ReviewDetail/store`
- 避免：`@/components/Kpi/ProjectAllot/Column`

`*Entries.js` 的语义是“当前领域愿意暴露给外部复用的前端能力清单”。它只做 re-export，不承载业务逻辑；真正实现仍留在原领域目录内。领域入口文件只允许 `export { ... } from '...'` 形式的相对路径转发，不引入状态、样式、业务逻辑或 `@/components/**` 绝对转发。

`*Entries.js` 默认应按页面、弹窗、表格或明确复用场景保持窄入口；但同一页面的表单片段、同一流程详情的互斥展示片段、同一共享配置组可以保留为一个内聚入口，不需要为了减少 export 数量机械拆分。

禁止无语义地区复制出内容完全相同的 `*Entries.js`；只有页面入口和流程装配入口等消费者语义明确不同的场景，才可以保留显式登记的重复入口。

业务域内部实现文件不要从自己的 `*Entries.js` 反向导入；域内复用应使用相对路径。`*Entries.js` 只作为外部稳定入口使用。

当前已有领域入口：

- `AfterLease/AdjustCreateEntries.js`
- `AfterLease/AdjustDetailEntries.js`
- `AfterLease/AdjustListEntries.js`
- `AfterLease/CheckPlanCreateEntries.js`
- `AfterLease/CheckPlanCheckListEntries.js`
- `AfterLease/CheckPlanDetailEntries.js`
- `AfterLease/CheckPlanExternalEntries.js`
- `AfterLease/CheckPlanListPageEntries.js`
- `AfterLease/CheckPlanOpenListEntries.js`
- `AfterLease/CheckPlanPrepareEntries.js`
- `AfterLease/CheckPlanStrategyColumnEntries.js`
- `AfterLease/CheckPlanStrategyCreateEntries.js`
- `AfterLease/CheckPlanStrategyLogEntries.js`
- `AfterLease/CheckPlanStrategyPageEntries.js`
- `AfterLease/CheckPlanTemplateEntries.js`
- `AfterLease/Level5ClassifyDetailEntries.js`
- `AfterLease/Level5ClassifyListEntries.js`
- `AfterLease/ManageLedgerEntries.js`
- `AfterLease/PolicyManageBasePageEntries.js`
- `AfterLease/PolicyManageBasePolicyEntries.js`
- `AfterLease/PolicyManageBasePolicyToolEntries.js`
- `AfterLease/PolicyManageDetailEntries.js`
- `AfterLease/PolicyManageListEntries.js`
- `AfterLease/PolicyManageRemindEntries.js`
- `AfterLease/RentCollectionDetailEntries.js`
- `AfterLease/RentCollectionInterestModalEntries.js`
- `AfterLease/RentCollectionListPageEntries.js`
- `AfterLease/RentCollectionListRenderEntries.js`
- `AfterLease/RentCollectionProcessEntries.js`
- `App/RootRedirectEntries.js`
- `Archives/ManageEntries.js`
- `Archives/ManagementEntries.js`
- `Archives/OtherFilingMaterialsEntries.js`
- `Archives/TaskEntries.js`
- `BaseData/FileTemplateEntries.js`
- `BaseData/LeaseholdPropertyEntries.js`
- `BlackGray/BreakThroughApplicationEntries.js`
- `BlackGray/BreakThroughApprovalEntries.js`
- `BlackGray/AllQueryEnterpriseEntries.js`
- `BlackGray/AllQueryGroupEntries.js`
- `BlackGray/AllQueryRecordEntries.js`
- `BlackGray/BlackGrayHitEntries.js`
- `BlackGray/EnterDatabaseApplicationEntries.js`
- `BlackGray/EnterDatabaseExternalEntries.js`
- `BlackGray/EnterDatabaseHistoryEntries.js`
- `BlackGray/EnterDatabaseUploadEntries.js`
- `BlackGray/OutboundApplicationEntries.js`
- `BlackGray/OutboundApprovalEntries.js`
- `BlackGray/OutboundSearchEntries.js`
- `BlackGray/ParameterEntries.js`
- `BlackGray/QueryIframeEntries.js`
- `BlackGray/WarehouseApprovalDetailEntries.js`
- `BlackGray/WarehouseApprovalListEntries.js`
- `BlackGray/WarehouseApprovalOutDetailEntries.js`
- `BlackGray/WarehouseMainTaskDetailEntries.js`
- `BlackGray/WarehouseMainTaskListEntries.js`
- `BlackGray/WarehouseMainTaskOutDetailEntries.js`
- `BlackGray/WarehouseSearchEntries.js`
- `BlackGray/WarehouseSubTaskEntries.js`
- `Budget/AccountsReceivableEntries.js`
- `Budget/BankAccountEntries.js`
- `Budget/BusinessAgingTableEntries.js`
- `Budget/ExchangeRateEntries.js`
- `Budget/FinancialMonthlyManagementEntries.js`
- `Budget/FlowCenterEntries.js`
- `Budget/IncomeShareTableEntries.js`
- `Budget/LprEntries.js`
- `Budget/PricingBaseDataEntries.js`
- `Budget/PricingBaseSetListEntries.js`
- `Budget/PricingBaseSetModalDetailEntries.js`
- `Budget/PricingBaseSetModalEditTableEntries.js`
- `Budget/PricingBusinessDetailEntries.js`
- `Budget/PricingBusinessListEntries.js`
- `Budget/PricingBusinessLogEntries.js`
- `Budget/PricingFtpInterestDetailEntries.js`
- `Budget/PricingFtpInterestListEntries.js`
- `Budget/PricingFtpInterestPriceChangeEntries.js`
- `Budget/PricingFtpInterestPriceDetailEntries.js`
- `Budget/PricingFtpYieldEntries.js`
- `Budget/ProfitDistributionEntries.js`
- `Budget/ProjectProfitDetailEntries.js`
- `Budget/ProjectProfitListEntries.js`
- `Budget/ProvisioningDataEntries.js`
- `Budget/ProvisioningImpairmentEntries.js`
- `Budget/ProvisioningParamsConfigEntries.js`
- `Budget/ProvisioningSharedEntries.js`
- `Budget/StampDutyEntries.js`
- `BudgetManagement/AssessmentEntries.js`
- `BudgetManagement/ParameterEntries.js`
- `BudgetManagement/PlacementPlanDetailEntries.js`
- `BudgetManagement/PlacementPlanListEntries.js`
- `BudgetManagement/PlacementPlanWeekDetailEntries.js`
- `BudgetManagement/PlanCostEntries.js`
- `BudgetManagement/PlanProfitBusinessDetailEntries.js`
- `BudgetManagement/PlanProfitDetailEntries.js`
- `BudgetManagement/PlanProfitListEntries.js`
- `BudgetManagement/ProvisionForecastConfigDetailEntries.js`
- `BudgetManagement/ProvisionForecastDetailEntries.js`
- `BudgetManagement/ProvisionForecastListEntries.js`
- `BusinessInfoCheck/BusinessInfoCheckEntries.js`
- `ChangeLogDiff/ChangeLogDiffEntries.js`
- `Chart/BarChartEntries.js`
- `Chart/LineChartEntries.js`
- `Chart/TooltipEntries.js`
- `ClientMaterialTable/BusinessMaterialTableEntries.js`
- `Contract/ChangeDetailEntries.js`
- `Contract/ApplicationLeaseLogEntries.js`
- `Contract/ApplicationLogDiffEntries.js`
- `Contract/ApplicationLogEntries.js`
- `Contract/ApplicationDetailPageEntries.js`
- `Contract/BaseInfoEntries.js`
- `Contract/ConfigEntries.js`
- `Contract/ContractMaterialListEntries.js`
- `Contract/ContractProtocolEntries.js`
- `Contract/ContractStartRentMaterialEntries.js`
- `Contract/ContractTextEntries.js`
- `Contract/CreateReceiptDetailEntries.js`
- `Contract/LeaseMaterialsEntries.js`
- `Contract/ListEntries.js`
- `Contract/MarginRefundDetailEntries.js`
- `Contract/ProcessPrepareDetailEntries.js`
- `Contract/SettlementDetailEntries.js`
- `Contract/SignEntries.js`
- `Contract/StartRentDetailEntries.js`
- `Contract/StartRentCheckEntries.js`
- `Cpm/BillManageEntries.js`
- `Cpm/CollectionWriteOffEntries.js`
- `Cpm/ContractCpmCashFlowEntries.js`
- `Cpm/ContractCpmDetailEntries.js`
- `Cpm/ContractCpmDownPaymentEntries.js`
- `Cpm/ContractCpmListEntries.js`
- `Cpm/MarginManagementPageEntries.js`
- `Cpm/MarginManagementPaymentEntries.js`
- `Cpm/MarginManagementRefundEntries.js`
- `Cpm/MarginManagementVerificationEntries.js`
- `Cpm/PaymentApplicationDetailEntries.js`
- `Cpm/PaymentApplicationListEntries.js`
- `Cpm/PaymentApplicationMaterialsEntries.js`
- `Cpm/PaymentApplicationPublicCheckEntries.js`
- `Cpm/PaymentApplicationPublicInfoEntries.js`
- `Cpm/PaymentWriteOffEntries.js`
- `Credit/CreditReportSearchEntries.js`
- `Credit/EstablishDetailEntries.js`
- `Credit/EstablishLogEntries.js`
- `Credit/EstablishPageEntries.js`
- `Credit/ReviewDetailEntries.js`
- `Credit/ReviewLogEntries.js`
- `Credit/ReviewPageEntries.js`
- `Credit/SearchListEntries.js`
- `CreditManage/CreditManageEntries.js`
- `Customer/ApplyPermissionEntries.js`
- `Customer/CustomerRatingDetailEntries.js`
- `Customer/CustomerRatingListEntries.js`
- `Customer/CustomerRatingUploadEntries.js`
- `Customer/DebtRatingDetailEntries.js`
- `Customer/DebtRatingListEntries.js`
- `Customer/ExternalPublicInfoEntries.js`
- `Customer/HandoverEntries.js`
- `Customer/MaintainDetailEntries.js`
- `Customer/MaintainListEntries.js`
- `Customer/MaintainLogEntries.js`
- `Customer/MonitoringDetailEntries.js`
- `Customer/MonitoringEntries.js`
- `Customer/QccSingleViewEntries.js`
- `Customer/SingleViewRiskEntries.js`
- `Customer/UnifiedViewDetailEntries.js`
- `Customer/UnifiedViewEntries.js`
- `Dashboard/OverviewEntries.js`
- `Dashboard/SsoEntries.js`
- `Dashboard/WorkbenchEntries.js`
- `EvaluationAgency/AppraisalAgencyEntries.js`
- `ExternalEmbed/ExternalEmbedEntries.js`
- `ExternalEmbed/RzyEntries.js`
- `FilingMaterials/AfterApplyEntries.js`
- `FilingMaterials/ApplyEntries.js`
- `FilingMaterials/FundApplyEntries.js`
- `FilingMaterials/OtherApplyEntries.js`
- `Financial/CreditEntries.js`
- `Financial/DirectDetailEntries.js`
- `Financial/DirectListEntries.js`
- `Financial/FinancingCarryInterestEntries.js`
- `Financial/FinancingUrlEntries.js`
- `Financial/FundDetailAccountEntries.js`
- `Financial/FundDetailAssetEntries.js`
- `Financial/FundDetailActualTableEntries.js`
- `Financial/FundDetailEstimateTableEntries.js`
- `Financial/FundDetailLogEntries.js`
- `Financial/FundDetailPageEntries.js`
- `Financial/FundDetailSchemeEntries.js`
- `Financial/FundEffectEntries.js`
- `Financial/FundActualTableEntries.js`
- `Financial/FundGuaranteeEntries.js`
- `Financial/FundListChangeModalEntries.js`
- `Financial/FundListCreateModalEntries.js`
- `Financial/FundListMainEntries.js`
- `Financial/FundOrgEntries.js`
- `Financial/FundProcessEntries.js`
- `Financial/FundYearRateEntries.js`
- `Financial/GuaranteeEntries.js`
- `Financial/LiquidityAccountBalanceEntries.js`
- `Financial/LiquidityFundDailyReportEntries.js`
- `Financial/LiquidityManagementEntries.js`
- `Financial/LiquidityPredictionParametersEntries.js`
- `Financial/LiquidityRiskEntries.js`
- `Financial/LiquiditySupervisionAccountEntries.js`
- `Financial/OrgEntries.js`
- `Financial/PayableInterestEntries.js`
- `Financial/PaymentBatchApprovalEntries.js`
- `Financial/PaymentDetailPageEntries.js`
- `Financial/PaymentListPageEntries.js`
- `Financial/PaymentLogEntries.js`
- `Financial/PropertyEntries.js`
- `Financial/SelectEntries.js`
- `InsurancePolicy/InsurancePolicyColumnsEntries.js`
- `InsurancePolicy/InsurancePolicyEntries.js`
- `Kpi/BaseSetBaBeiJiTiEntries.js`
- `Kpi/BaseSetBasePrizeRateEntries.js`
- `Kpi/BaseSetCopyEntries.js`
- `Kpi/BaseSetDepartmentTheoryEntries.js`
- `Kpi/BaseSetExpenseAccrualEntries.js`
- `Kpi/BaseSetFormulaTheoryEntries.js`
- `Kpi/BaseSetJinRongShiChangEntries.js`
- `Kpi/BaseSetListEntries.js`
- `Kpi/BaseSetModalEntries.js`
- `Kpi/BaseSetParameterModalEntries.js`
- `Kpi/BaseSetProjectScaleFactorEntries.js`
- `Kpi/BaseSetProjectExtractEntries.js`
- `Kpi/BaseSetProjectTypeFactorEntries.js`
- `Kpi/BaseSetPutPrizeFactorEntries.js`
- `Kpi/BaseSetSuiLvWeiHuEntries.js`
- `Kpi/BaseSetTableEntries.js`
- `Kpi/FormulaValueTipEntries.js`
- `Kpi/BaseSetYeWuDeptEntries.js`
- `Kpi/BaseSetZhiDengXiShuEntries.js`
- `Kpi/BaseSetZhongHouTaiDeptEntries.js`
- `Kpi/BusinessGoalEntries.js`
- `Kpi/EstimationContractEntries.js`
- `Kpi/EstimationDepartmentalPoolEntries.js`
- `Kpi/EstimationEntries.js`
- `Kpi/EstimationProjectManagerPrizeEntries.js`
- `Kpi/EstimationProjectManagerProfitEntries.js`
- `Kpi/PmAssessDetailContentEntries.js`
- `Kpi/PmAssessEditModalEntries.js`
- `Kpi/PmAssessListEntries.js`
- `Kpi/ProjectAllotDetailEntries.js`
- `Kpi/ProjectAllotFormEntries.js`
- `Kpi/ProjectAllotHistoryEntries.js`
- `Kpi/ProjectAllotPageEntries.js`
- `Kpi/ProjectAllotProjectAllocateListEntries.js`
- `Lease/ApprovalConfirmEntries.js`
- `Lease/MaintainDetailEntries.js`
- `Lease/MaintainListEntries.js`
- `LifeCycle/CustomerEntries.js`
- `LifeCycle/ProjectEntries.js`
- `Message/NotificationEntries.js`
- `Ocr/ListEntries.js`
- `Ocr/RecognitionEntries.js`
- `Overdue/CollectionEntries.js`
- `Overdue/CollectionModalEntries.js`
- `Overdue/LitigationDocEntries.js`
- `Overdue/LitigationRegistrationEntries.js`
- `PaymentFtpColumns/FtpAssessmentColumnsEntries.js`
- `Permission/AuthEntries.js`
- `Permission/BifrostPageEntries.js`
- `Permission/GroupEntries.js`
- `Permission/LogEntries.js`
- `Permission/UserEntries.js`
- `Preview/PreviewEntries.js`
- `Process/ApprovalHistoryEntries.js`
- `Process/ApplicationEntries.js`
- `Process/BlankBlockEntries.js`
- `Process/DesignEntries.js`
- `Process/DetailEntries.js`
- `Process/ProcessInfoModalEntries.js`
- `Process/ProcessSnapshotEntries.js`
- `Process/ProcessTaskFlowChartEntries.js`
- `Process/ProcessTypeTreeEntries.js`
- `Process/QueryEntries.js`
- `Process/ReceiveEntries.js`
- `Project/DebtEvaluationEntries.js`
- `Project/EstablishmentDetailLogEntries.js`
- `Project/EstablishmentDetailPageEntries.js`
- `Project/EstablishmentDetailQuotationEntries.js`
- `Project/EstablishmentEntries.js`
- `Project/FinancialReportStatisticsEntries.js`
- `Project/FormListItemEntries.js`
- `Project/PriceDetailLogEntries.js`
- `Project/PriceDetailPageEntries.js`
- `Project/PriceDetailQuotationEntries.js`
- `Project/PriceEntries.js`
- `Project/ProjectReviewMeetingModalEntries.js`
- `Project/ReviewDetailBaseEntries.js`
- `Project/ReviewDetailCashFlowEntries.js`
- `Project/ReviewDetailLogEntries.js`
- `Project/ReviewDetailMaterialEntries.js`
- `Project/ReviewDetailPageEntries.js`
- `Project/ReviewDetailQuotationEntries.js`
- `Project/ReviewEntries.js`
- `Project/ReviewMaterialTableEntries.js`
- `Project/ReviewProcessDetailEntries.js`
- `Project/ReviewProcessMeetingEntries.js`
- `Project/ReviewSnapshotEntries.js`
- `Report/FinancialReportApprovalEntries.js`
- `Report/FinancialReportFinishedEntries.js`
- `Report/FinancialReportWaitEntries.js`
- `Report/ManagementEntries.js`
- `Report/OperationAnalysisEntries.js`
- `Report/OperationContractMonitorEntries.js`
- `Report/OperationPendingEntries.js`
- `Risk/CloudMetricValueEntries.js`
- `Risk/FinanceSheetEntries.js`
- `Risk/FinanceSheetFileEntries.js`
- `Risk/MetricTimedEntries.js`
- `Risk/MetricValueControlEntries.js`
- `Risk/MetricValueJinKonEntries.js`
- `Risk/MetricValuePageEntries.js`
- `Risk/MetricValueTargetEntries.js`
- `Risk/MonitorEarlyEntries.js`
- `Risk/OverdueEntries.js`
- `Risk/PublicMonitorColumnsEntries.js`
- `Risk/PublicMonitorDetailEntries.js`
- `Risk/PublicMonitorListEntries.js`
- `Risk/PublicMonitorOpinionDetailEntries.js`
- `Risk/RiskStrategyConcentrationEntries.js`
- `Risk/RiskStrategyIndicatorEntries.js`
- `Risk/RiskStrategyPageEntries.js`
- `Risk/RiskStrategyRelateMonitorEntries.js`
- `Risk/SourceCardCalcModalEntries.js`
- `Risk/SourceCardDetailEntries.js`
- `Risk/SourceCardListEntries.js`
- `TrackEvent/TrackEventDetailEntries.js`
- `TrackEvent/TrackEventListEntries.js`
- `TrackEvent/TrackEventModalEntries.js`
- `TrackEvent/TrackEventTaskEntries.js`
- `VisitorManage/VisitorManageEntries.js`
- `WhiteList/DetailEntries.js`
- `WhiteList/ListEntries.js`

## 依赖规则

- 禁止一个业务页面长期直接复用另一个业务页面目录下的组件、配置或 API。
- `src/pages/<domainA>` 引用 `src/pages/<domainB>` 时，应先判断被引用内容是否应迁到 `src/components`、`src/api`、`src/utils` 或未来的 `src/features/<domain>`。
- 工作台、报表、流程这类聚合页面可以编排多个业务域页面，但聚合逻辑应显式，不能让聚合页面变成公共组件库。
- 生命周期和看板属于用户视角聚合页，可以显式装配租后、风控、客户等领域提供的稳定入口；这类依赖应保留在 `page aggregation` 报告中持续观察，不应为追求报表归零而把业务组件搬进聚合页。
- `process` 负责流程壳和审批交互；业务详情组件应尽量由业务域提供，再由流程页面进行装配。
- 通用组件应保持无业务名称、无菜单名称、无特定页面状态依赖。
- 若确实需要跨业务域复用能力，先在被调用领域新增或复用 `*Entries.js`，再由调用方引入。
- 禁止在 `src/pages` 下保留样式、图片、JSON 或其它非路由 JS/TS 文件；页面层只保留路由壳。
- `src/pages` 顶层路由目录必须能对应到真实 `src/components/<Domain>`，历史菜单路径或兼容路径需要在 `scripts/domain-report-config.js` 显式登记业务语义归属。
- 禁止直接跨域引用组件内部的 `api`、`store`、`Column`、`Config`、`context` 等私有文件。
- 禁止在 `.less` 中通过 `@/components/<domain>/...` 引用业务域组件内部样式；路由兼容壳应只做 JS 转发，样式由真实组件自己维护。
- 禁止保留 `0` 字节空样式文件；如果样式已经为空，应删除样式文件和对应的空 `styles` import / `className`。
- 禁止在业务组件目录中保留只转发 `@/components/**` 的组件壳；调用方应直接引用目标公共组件或领域入口。
- 禁止在 `src/components` 和 `src/pages` 下保留空目录；删除迁移残留文件时应同步清理空壳目录。
- 已收敛到领域入口的共享业务组件禁止再通过组件根目录直连，例如黑灰名单管理应通过 `BlackGray/AllQueryEnterpriseEntries.js`、`AllQueryGroupEntries.js`、`AllQueryRecordEntries.js`、`QueryIframeEntries.js`、`EnterDatabaseApplicationEntries.js`、`EnterDatabaseExternalEntries.js`、`EnterDatabaseHistoryEntries.js`、`EnterDatabaseUploadEntries.js`、`BreakThroughApplicationEntries.js`、`BreakThroughApprovalEntries.js`、`OutboundApplicationEntries.js`、`OutboundApprovalEntries.js`、`OutboundSearchEntries.js`、`WarehouseApprovalListEntries.js`、`WarehouseApprovalDetailEntries.js`、`WarehouseApprovalOutDetailEntries.js`、`WarehouseMainTaskListEntries.js`、`WarehouseMainTaskDetailEntries.js`、`WarehouseMainTaskOutDetailEntries.js`、`WarehouseSearchEntries.js`、`WarehouseSubTaskEntries.js`、`ParameterEntries.js` 等窄入口引入，黑灰名单命中标识应通过 `BlackGray/BlackGrayHitEntries.js` 引入，保单列配置应通过 `InsurancePolicy/InsurancePolicyColumnsEntries.js` 引入，业务资料表应通过 `ClientMaterialTable/BusinessMaterialTableEntries.js` 引入，征信查询抽屉应通过 `Credit/CreditReportSearchEntries.js` 引入，评估机构关系表应通过 `EvaluationAgency/AppraisalAgencyEntries.js` 引入，FTP考核列配置应通过 `PaymentFtpColumns/FtpAssessmentColumnsEntries.js` 引入，跟踪事项弹窗应通过 `TrackEvent/TrackEventModalEntries.js` 引入，跟踪事项新增任务弹窗应通过 `TrackEvent/TrackEventTaskEntries.js` 引入，Dashboard 应通过 `Dashboard/OverviewEntries.js`、`Dashboard/SsoEntries.js`、`Dashboard/WorkbenchEntries.js` 等窄入口引入，`BusinessInfoCheck`、`ClientMaterialTable`、`EvaluationAgency`、`ChangeLogDiff`、`InsurancePolicy`、`PaymentFtpColumns` 应通过对应 `*Entries.js` 引入。
- `npm run report:unused-component-candidates` 只输出静态无入边候选，不代表可直接删除；删除前必须人工复核动态约定、配置导出和类型声明等情况。
- `npm run report:unused-component-candidates -- --include-index --include-entries` 应保持清零；`npm run check:boundaries` 已把这组候选纳入门禁，用于阻止无引用 `index`、样式文件和 `*Entries.js` 残留回潮。
- `npm run report:ui-domain-deps` 会把已拆出的窄入口按独立能力归类，例如 `AfterLeaseCheckPlanCreate`、`BlackGrayHit`、`BudgetPricingBusinessDetail`、`ContractApplicationDetail`、`ContractBaseInfo`、`ContractLeaseMaterials`、`CustomerDebtRatingList`、`CustomerExternalPublicInfo`、`CustomerSingleViewRisk`、`FinancialSelect`、`InsurancePolicyInfo`、`InsurancePolicyColumns`、`KpiBaseSetModalDetail`、`ProcessInfoModal`、`ProcessTaskFlowChart`、`ProjectReviewMeetingModal`、`ProjectReviewSnapshot`、`RentCollectionDetail`、`RiskPublicMonitorList`、`RiskSourceCardCalcModal`、`TrackEventDetail`、`TrackEventList`、`TrackEventModal`、`TrackEventTask`；这些报告项代表稳定共享能力，不等同于调用方依赖完整业务域。
- `npm run report:ui-domain-deps` 的输出会区分域实现、流程编排和普通页面聚合。`Stable shared business capabilities` 是已确认可复用的业务能力入口，`still need semantic review` 是仍需逐项判断的真实跨域嵌入，`workflow orchestration` 是流程壳对业务详情的装配，`page aggregation` 是工作台、看板、生命周期视图等普通聚合页面的跨域依赖。普通聚合页面也会继续分为稳定能力使用和待语义评审项；后续重构应优先处理待评审清单，而不是机械追求所有跨域项归零。
- `npm run check:boundaries` 会阻止 `report:ui-domain-deps` 中新增的 `still need semantic review` 项；新增跨域复用必须先明确是稳定共享能力、流程编排、页面聚合，或回收到单一业务域。
- `npm run report:ui-domain-deps` 默认忽略同业务域页面路由壳到本域组件入口的引用，例如 `pages/project` 到 `components/Project`；报告中的 workflow/page aggregation 部分应主要用于观察流程、工作台、跨域页面编排。
- `pages/index` 到 `App/RootRedirectEntries.js`、`pages/workbench` 到 `Dashboard/WorkbenchEntries.js` 属于稳定页面聚合入口；它们是路由重定向/工作台入口，不代表业务域实现互相嵌入。
- `npm run check:boundaries` 会强制 `src/pages/**/*.js|jsx|ts|tsx` 保持为从 `@/components/<Domain>/*Entries` 转发的轻薄路由壳，真实页面实现、样式和局部组件应放在对应 `src/components/<Domain>` 下。
- 菜单路由和业务语义不一致时，依赖报告可以按真实语义归一。例如 `pages/lease/tracking` 是跟踪事项路由壳，`pages/customerView` 是客户视图，租后检查计划下的单一视图风险入口归入客户能力；这类归一只影响报告，不改变路由兼容。
- `pages/budgetManagement/provisionForecast` 是预算管理下的历史菜单路径，但页面和接口语义归入预算拨备预测；依赖报告按 `budget` 归一，路由保持兼容。
- 项目定价列表页和发起定价弹窗通过 `src/components/Project/PriceEntries.js` 暴露，`src/pages/project/price/*` 只保留路由壳、历史兼容壳和详情子路由壳。
- 项目评审列表页和发起评审弹窗通过 `src/components/Project/ReviewEntries.js` 暴露，`src/pages/project/review/*` 只保留路由壳、历史兼容壳和详情子路由壳。
- `ClientMaterialTable`、`ChangeLogDiff`、`PaymentFtpColumns` 这类横向业务能力不再视为公共基础组件；依赖报告会保留它们的跨域使用关系，后续需要逐项判断是沉淀横向能力还是回收到具体业务域。
- 已无入边的历史兼容入口已移除，例如旧的 `FileDiff`、`PaymentApplyColumns`、`Project/ReviewMeetingEntries.js`、`ClientFileTable`、`CheckBusiness`、`Credit/SearchModalEntries.js`、`EvaluationAgency/EvaluationAgencyEntries.js`、`Policy/PolicyEntries.js`、`TrackEvent/TrackEventEntries.js` 和 `TrackEvent/TrackingEntries.js`；新代码必须使用对应语义入口或更窄的场景入口。
- `npm run check:boundaries` 会阻止业务代码重新引用上述已移除兼容入口；新增和迁移代码必须使用语义入口或窄入口。
- 权限页字典/组织/角色 iframe、用户管理页、功能分组页和操作日志页分别通过 `src/components/Permission/BifrostPageEntries.js`、`UserEntries.js`、`GroupEntries.js`、`LogEntries.js` 暴露，付款核销收款日面板、合同保证金退款文本展示已回收到各自页面或业务组件私有目录，不再作为公共根组件使用。
- 预算流水组织树选择器已回收到 `src/components/Budget/FlowCenter/BankFlow/OrgTreeSelect`，不再作为公共根组件使用。
- 黑灰名单页面入口已拆分为查询、入库、突破、出库、仓库、参数等窄 `*Entries.js`，不再使用宽泛 `BlackGray/BlackGrayEntries.js`；CPM 页面入口已拆分为票据、付款申请、付款核销、合同付款、收款核销、保证金管理等窄 `*Entries.js`，不再使用宽泛 `Cpm/CpmEntries.js`。
- 项目多行文本展示已回收到 `src/components/Project/MultilineText`，当前仅作为项目立项详情域内私有组件使用。
- Dashboard 分段标签样式组件已回收到 `src/components/Dashboard/RadioTabs`，跨层使用应优先通过对应 Dashboard 窄入口或域内相对路径。
- 布局面包屑状态工具已回收到 `src/layout/components/BreadLine`，不再作为公共根组件使用。
- 禁止绕过公共组件稳定入口引用 `Actions/*`、`Form/*`、`Format/*`、`Table/*`、`Chart/tooltip`。
- 风险指标报送顶层 Tabs 页、控制指标、金控指标和目标指标分别通过 `src/components/Risk/MetricValuePageEntries.js`、`MetricValueControlEntries.js`、`MetricValueJinKonEntries.js`、`MetricValueTargetEntries.js` 暴露，`src/pages/risk/metricValue/*` 只保留路由壳。
- 风险评分卡列表页、详情页和试算弹窗分别通过 `src/components/Risk/SourceCardListEntries.js`、`SourceCardDetailEntries.js`、`SourceCardCalcModalEntries.js` 暴露，`src/pages/risk/sourceCard/*` 只保留路由壳和历史兼容入口。
- 风控策略顶层页、指标管理、集中度管理和关联交易监测分别通过 `src/components/Risk/RiskStrategyPageEntries.js`、`RiskStrategyIndicatorEntries.js`、`RiskStrategyConcentrationEntries.js`、`RiskStrategyRelateMonitorEntries.js` 暴露，`src/pages/risk/riskStrategy/*` 只保留路由壳。
- 租赁物维护列表页和详情页分别通过 `src/components/Lease/MaintainListEntries.js`、`MaintainDetailEntries.js` 暴露，流程详情复用详情窄入口，`src/pages/lease/maintain/*` 只保留路由壳。
- 应收账款列表页和详情页均通过 `src/components/Budget/AccountsReceivableEntries.js` 暴露，`src/pages/budget/accountsReceivable/*` 只保留路由壳。
- 收入分摊表列表页和详情页均通过 `src/components/Budget/IncomeShareTableEntries.js` 暴露，`src/pages/budget/incomeShareTable/*` 只保留路由壳。
- 预算银行账户维护页通过 `src/components/Budget/BankAccountEntries.js` 暴露，`src/pages/budget/bankAccount/*` 只保留路由壳。
- 预算业务账龄表列表页和详情页通过 `src/components/Budget/BusinessAgingTableEntries.js` 暴露，`src/pages/budget/businessAgingTable/*` 只保留路由壳。
- 预算历史路径下的财务报表导入实际归入风险指标财报域，通过 `src/components/Risk/FinanceSheetFileEntries.js` 暴露，`src/pages/budget/financeSheet/*` 只保留路由壳。
- 预算财务月结管理页通过 `src/components/Budget/FinancialMonthlyManagementEntries.js` 暴露，`src/pages/budget/financialMonthlyManagement/*` 只保留路由壳。
- 预算拨备数据查询页通过 `src/components/Budget/ProvisioningDataEntries.js` 暴露，`src/pages/budget/provisioning/dataSearch/*` 只保留路由壳。
- 预算拨备减值列表页和详情页通过 `src/components/Budget/ProvisioningImpairmentEntries.js` 暴露，`src/pages/budget/provisioning/impairment/*` 只保留路由壳。
- 预算拨备参数配置列表页和详情页通过 `src/components/Budget/ProvisioningParamsConfigEntries.js` 暴露，`src/pages/budget/provisioning/paramsConfig/*` 只保留路由壳；预算管理拨备预测复用的拨备弹窗和列配置通过 `src/components/Budget/ProvisioningSharedEntries.js` 暴露。
- 预算定价基础数据维护页通过 `src/components/Budget/PricingBaseDataEntries.js` 暴露，`src/pages/budget/pricing/baseData/*` 只保留路由壳。
- 预算定价基础参数设置列表页、详情弹窗和编辑表格分别通过 `src/components/Budget/PricingBaseSetListEntries.js`、`PricingBaseSetModalDetailEntries.js`、`PricingBaseSetModalEditTableEntries.js` 暴露，`src/pages/budget/pricing/baseSet/*` 只保留路由壳。
- 预算定价业务通过 `src/components/Budget/PricingBusinessListEntries.js`、`src/components/Budget/PricingBusinessDetailEntries.js` 和 `src/components/Budget/PricingBusinessLogEntries.js` 分别暴露列表、详情和日志能力，`src/pages/budget/pricing/business/*` 只保留路由壳。
- 预算定价 FTP 计息列表页、详情页、价格明细页和价格变更弹窗分别通过 `src/components/Budget/PricingFtpInterestListEntries.js`、`PricingFtpInterestDetailEntries.js`、`PricingFtpInterestPriceDetailEntries.js` 和 `PricingFtpInterestPriceChangeEntries.js` 暴露，`src/pages/budget/pricing/ftpInterest/*` 只保留路由壳。
- 预算定价 FTP 收益率列表页和详情页通过 `src/components/Budget/PricingFtpYieldEntries.js` 暴露，`src/pages/budget/pricing/ftpYield/*` 只保留路由壳。
- 预算 LPR 维护页通过 `src/components/Budget/LprEntries.js` 暴露，`src/pages/budget/lpr/*` 只保留路由壳。
- 预算项目利润列表页和详情页分别通过 `src/components/Budget/ProjectProfitListEntries.js`、`ProjectProfitDetailEntries.js` 暴露，列配置保留在预算项目利润组件内部，`src/pages/budget/projProfit/*` 只保留路由壳。
- 预算印花税维护页通过 `src/components/Budget/StampDutyEntries.js` 暴露，`src/pages/budget/stampDuty/*` 只保留路由壳。
- KPI 项目分配列表页、历史页、项目分配列表子组件、分配明细和表单片段分别通过 `src/components/Kpi/ProjectAllotPageEntries.js`、`ProjectAllotHistoryEntries.js`、`ProjectAllotProjectAllocateListEntries.js`、`ProjectAllotDetailEntries.js`、`ProjectAllotFormEntries.js` 暴露，`src/pages/kpi/projectAllot/*` 保留路由壳和历史兼容入口。
- 预算考核列表页和详情页通过 `src/components/BudgetManagement/AssessmentEntries.js` 暴露，`src/pages/budgetManagement/assessment/*` 只保留路由壳。
- 预算管理业绩目标是历史菜单路径，实际归入 KPI 业绩目标组件域；列表页和详情页通过 `src/components/Kpi/BusinessGoalEntries.js` 暴露，`src/pages/budgetManagement/businessGoal/*` 只保留路由壳。
- 预算管理参数配置页通过 `src/components/BudgetManagement/ParameterEntries.js` 暴露，`src/pages/budgetManagement/parameterConfig` 只保留路由壳。
- 预算管理参数配置（历史定价基础配置路径）页通过 `src/components/BudgetManagement/ParameterEntries.js` 暴露，`src/pages/budgetManagement/parameterConfiguration` 只保留路由壳。
- 预算管理投放计划列表页、详情页和周报详情页分别通过 `src/components/BudgetManagement/PlacementPlanListEntries.js`、`PlacementPlanDetailEntries.js`、`PlacementPlanWeekDetailEntries.js` 暴露，`src/pages/budgetManagement/placementPlan/*` 只保留路由壳。
- 预算管理成本计划列表页和详情页通过 `src/components/BudgetManagement/PlanCostEntries.js` 暴露，`src/pages/budgetManagement/plan/cost/*` 只保留路由壳。
- 预算管理利润计划列表页、详情页和业务明细页分别通过 `src/components/BudgetManagement/PlanProfitListEntries.js`、`PlanProfitDetailEntries.js`、`PlanProfitBusinessDetailEntries.js` 暴露，`src/pages/budgetManagement/plan/profit/*` 只保留路由壳。
- 预算管理拨备预测列表页、详情页和配置详情页分别通过 `src/components/BudgetManagement/ProvisionForecastListEntries.js`、`ProvisionForecastDetailEntries.js`、`ProvisionForecastConfigDetailEntries.js` 暴露，`src/pages/budgetManagement/provisionForecast/*` 只保留路由壳。
- 金融产权列表页通过 `src/components/Financial/PropertyEntries.js` 暴露，`src/pages/financial/property` 只保留路由壳。
- 直融产品列表页通过 `src/components/Financial/DirectListEntries.js` 暴露，`src/pages/financial/direct/index.js` 只保留路由壳；直融详情继续通过 `src/components/Financial/DirectDetailEntries.js` 暴露。
- 金融机构管理页通过 `src/components/Financial/OrgEntries.js` 暴露，`src/pages/financial/org/*` 只保留路由壳。
- 财务应付利息列表页和详情页通过 `src/components/Financial/PayableInterestEntries.js` 暴露，`src/pages/financial/payableInterest/*` 只保留路由壳。
- 财务还本付息列表页、变更日志和差异详情分别通过 `src/components/Financial/PaymentListPageEntries.js` 和 `src/components/Financial/PaymentLogEntries.js` 暴露，`src/pages/financial/payment/*` 中列表和日志子路由只保留路由壳。
- 融资管理列表页、创建弹窗、变更弹窗、融资生效、变更日志、差异详情和历史组件子路由通过 `src/components/Financial/FundListMainEntries.js`、`FundListCreateModalEntries.js`、`FundListChangeModalEntries.js`、`FundActualTableEntries.js`、`FundGuaranteeEntries.js`、`FundOrgEntries.js`、`FundYearRateEntries.js`、`FundEffectEntries.js`、`FundDetailPageEntries.js`、`FundDetailActualTableEntries.js`、`FundDetailEstimateTableEntries.js`、`FundDetailSchemeEntries.js`、`FundDetailAccountEntries.js`、`FundDetailAssetEntries.js`、`FundDetailLogEntries.js` 暴露，`src/pages/financial/fund/*` 中对应路由只保留路由壳。
- 金融授信额度列表页和详情页通过 `src/components/Financial/CreditEntries.js` 暴露，`src/pages/financial/credit/*` 只保留路由壳。
- 金融担保额度列表页和详情页通过 `src/components/Financial/GuaranteeEntries.js` 暴露，`src/pages/financial/guarantee/*` 只保留路由壳。
- 财务流动性管理、资金日报、监管户待转资金、账户余额明细和预测参数配置分别通过 `src/components/Financial/LiquidityManagementEntries.js`、`LiquidityFundDailyReportEntries.js`、`LiquiditySupervisionAccountEntries.js`、`LiquidityAccountBalanceEntries.js`、`LiquidityPredictionParametersEntries.js` 暴露，`src/pages/financial/liquidity/*` 只保留路由壳。
- 金融流动性风险统计页通过 `src/components/Financial/LiquidityRiskEntries.js` 暴露，`src/pages/financial/liquidityRisk/*` 只保留路由壳。
- Dashboard 工作台、经营总览和 SSO 跳转页分别通过 `src/components/Dashboard/WorkbenchEntries.js`、`OverviewEntries.js`、`SsoEntries.js` 暴露，`src/pages/dashboard/workbench`、`src/pages/dashboard/overView`、`src/pages/dashboard/sso*` 只保留路由壳。
- 逾期催收列表页、详情页通过 `src/components/Overdue/CollectionEntries.js` 暴露，流程催收弹窗通过 `src/components/Overdue/CollectionModalEntries.js` 暴露，`src/pages/overdue/collection/*` 只保留路由壳。
- 逾期诉讼用印列表页和用印弹窗通过 `src/components/Overdue/LitigationDocEntries.js` 暴露，`src/pages/overdue/litigationDoc/*` 只保留路由壳。
- 逾期诉讼登记列表页和详情页通过 `src/components/Overdue/LitigationRegistrationEntries.js` 暴露，`src/pages/overdue/litigationRegistration/*` 只保留路由壳。
- 项目立项列表页和创建弹窗通过 `src/components/Project/EstablishmentEntries.js` 暴露，`src/pages/project/establishment/*` 只保留路由壳和详情子路由壳。
- 禁止从 `@/components` 根目录导入表格族组件，例如 `FileTable`、`NoEnumFileTable`、`VersionTable`、`EditTable`、`EditDescription`、`Summary` 应从 `@/components/Table` 导入。
- 禁止从 `@/components` 根目录导入公共选择器，例如 `ClientSelect`、`FounderSelect`、`OrgSelect`、`ApiSelect`、`ProjectReviewSelect` 应从 `@/components/Select` 导入。
- 禁止从 `@/components` 根目录导入已具备独立入口的默认组件，例如 `PageListDown`、`CommonTips`、`CommonNoData`、`ReadOnly`、`DetailLayout`、`Collapse`、`RegionCascader` 应直接从对应 `@/components/<Component>` 导入。
- 禁止业务代码继续从 `@/components` 根目录导入组件；历史兼容根导出已删除，新增和迁移代码必须依赖具体稳定入口。
- 允许直接从 `@/components/<Component>` 引入的根组件必须是已确认的公共基础组件，例如 `Icon`、`DataUpload`、`RenderColumn`、`FormItemContent`、`FormUpload`、`Excel`、`Amount`、`CommonNoData`、`ReadOnly` 等；新增跨域根组件直连时，应先判断它是公共基础组件，还是应改成某个业务域的 `*Entries.js`。
- 全局样式不放在 `src/components` 下伪装成组件域；当前全局动画样式已内聚到 `src/app.less`。
- 禁止业务代码直接引用 `blackList`、`postRentalInspection`、`riskControl`、`liquidity`、`pricing`、`newFtp`、`financialReport`、`manageReport`、`fillingMaterials`、`workbench`、`header` 等历史 API 目录，应使用对应语义领域入口。
- 禁止页面和组件直接引用 `src/api/**/interface/**` 类型文件；接口类型应由对应的语义 API 包装文件承接，避免页面绑定接口实现层目录。
- 禁止 `src/api/<domain>` 内部跨业务域 import 或 re-export 其他 `@/api/<domain>` 文件；聚合页或流程页需要复用 endpoint 时，在自身语义 API 入口声明所需 endpoint，避免把整个业务域 API 暴露给另一个域。
- 禁止预算应收账款页面和组件直接引用 `financial/accountsReceivable` 历史 API 前缀，应使用 `budget/accountsReceivable` 语义入口。
- 禁止通用选择器直接引用 `groupCredit/common` 历史 API 前缀，应使用 `common/selectApi` 语义入口。
- 禁止在 `src/components/**/api.js` 中只做 `@/api/**` 的一行转发；组件内部应直接引用语义明确的 `src/api` 入口，避免制造假本地 API 边界。
- `npm run check:boundaries` 会扫描整个 `src` 的 JS/TS 源码和 `.less` 样式 import，禁止非 `Entries/entries` 的 `@/components/<domain>/<subpath>` 导入，禁止未登记的组件根目录直连，禁止已收敛共享业务组件的根目录直连，禁止组件域内部反向引用自身 `*Entries.js`，禁止领域入口文件承载非 re-export 内容或使用 `@/components/**` 绝对转发，禁止业务组件目录中的组件转发壳，禁止通过 `@/pages/**` 复用页面私有代码，禁止 `src/pages` 下出现非路由 JS/TS 文件，禁止直接引用历史 API 目录，禁止页面和组件直接引用 API interface 类型目录，禁止 `src/api` 内部跨业务域引用，并校验领域级入口已被代码使用且同步记录在 README，同时校验无引用组件候选清零、无引用 API 实现清零、空样式文件清零、空目录清零、重复领域入口受控、跨域组件入口依赖基线受控、UI 跨域依赖无待语义评审项。
- 跨业务域复用 `@/components/<Domain>/*Entries.js` 时，新增依赖边必须先确认语义，再更新 `scripts/component-entry-deps-baseline.json`；清理掉跨域复用后也要同步删除过期基线边。不要为了通过检查直接把内部实现路径或宽入口暴露给调用方。
- `npm run check:boundaries` 会阻止 `src` 源码中的 `console.log` 和 `debugger` 调试残留，包括运行时代码和注释掉的旧调试语句；需要用户可见反馈时使用页面/组件层的提示能力，需要排错时应在具体业务域临时处理并随调试结束移除。

## 当前边界收敛

- 表格、文件表、描述表、审批详情等统一从 `src/components/Table` 稳定入口导入。
- 表单金额、只读表单、银行账号、日期范围等统一从 `src/components/Form` 稳定入口导入。
- 文件导出、模板下载、审批操作等统一从 `src/components/Actions` 稳定入口导入。
- 根路由重定向通过 `src/components/App/RootRedirectEntries.js` 暴露，真实实现命名为 `src/components/App/RootRedirect/RootRedirect.js`。
- 基础数据租赁物类型导入页通过 `src/components/BaseData/LeaseholdPropertyEntries.js` 暴露，真实实现命名为 `src/components/BaseData/LeaseholdProperty/BaseDataLeaseholdProperty.js`。
- 基于 `TableStore` 当前筛选条件或选中行的导出按钮统一从 `src/components/Actions.StoreExportAction` 使用；黑灰名单动作实现位于 `src/components/BlackGray/Actions`。
- 格式化列、可编辑列、超时展示等统一从 `src/components/Format` 稳定入口导入。
- 工商信息校验能力通过 `src/components/BusinessInfoCheck/BusinessInfoCheckEntries.js` 暴露，真实实现命名为 `src/components/BusinessInfoCheck/BusinessInfoCheck.js`。
- 业务资料表能力通过 `src/components/ClientMaterialTable/BusinessMaterialTableEntries.js` 暴露，真实实现命名为 `src/components/ClientMaterialTable/BusinessMaterialTable.js`。
- 变更日志文件比对能力通过 `src/components/ChangeLogDiff/ChangeLogDiffEntries.js` 暴露，真实实现命名为 `src/components/ChangeLogDiff/ChangeLogDiff.js`。
- 评估机构关系表能力通过 `src/components/EvaluationAgency/AppraisalAgencyEntries.js` 暴露，真实实现命名为 `src/components/EvaluationAgency/AppraisalAgency.js`。
- 保单信息能力通过 `src/components/InsurancePolicy/InsurancePolicyEntries.js` 暴露，真实实现命名为 `src/components/InsurancePolicy/InsurancePolicy.js`。
- 租赁物审核确认动作通过 `src/components/Lease/ApprovalConfirmEntries.js` 暴露，真实实现命名为 `src/components/Lease/ApprovalConfirm/LeaseApprovalConfirmAction.js`。
- 消息通知页通过 `src/components/Message/NotificationEntries.js` 暴露，真实实现命名为 `src/components/Message/Notification/MessageNotification.js`。
- OCR 识别页通过 `src/components/Ocr/RecognitionEntries.js` 暴露，真实实现命名为 `src/components/Ocr/Recognition/OcrRecognition.js`。
- FTP 考核列配置通过 `src/components/PaymentFtpColumns/FtpAssessmentColumnsEntries.js` 暴露，真实实现命名为 `src/components/PaymentFtpColumns/FtpAssessmentColumns.js`。
- PDF 预览页通过 `src/components/Preview/PreviewEntries.js` 暴露，真实实现命名为 `src/components/Preview/PdfPreview/PdfPreview.js`。
- 流程空白块和流程类型树分别通过 `src/components/Process/BlankBlockEntries.js`、`ProcessTypeTreeEntries.js` 暴露，真实实现命名为 `src/components/Process/BlankBlock/ProcessBlankBlock.js` 和 `src/components/Process/ProcessTypeTree/ProcessTypeTree.js`。
- 白名单列表和详情分别通过 `src/components/WhiteList/ListEntries.js`、`DetailEntries.js` 暴露，真实实现命名为 `src/components/WhiteList/List/WhiteListList.js` 和 `src/components/WhiteList/Detail/WhiteListDetail.js`，列定义保留为白名单域内私有配置。
- 财务、预算等外部页面不再从 `dashboard/workbench/components` 取通用表格合计和文件导出能力。
- dashboard 锚点滚动导航已归入 `src/components/Dashboard/AnchorScrollNav`，dashboard 域内页面使用相对路径复用。
- `dashboard/workbench/components` 暂时保留工作台内部私有组件；后续只处理确实跨业务域复用的部分。
- 我的流程页签、我的审批页签、流程查询、流程设计、流程详情路由和历史组件路由已收敛到 `src/components/Process` 的窄 `*Entries.js`，路由页仅保留入口装配。
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
- `customerView`：客户单一视图/客户画像聚合页，首页与详情实现已收敛到 `src/components/Customer`，路由页仅保留入口装配；企查查单一视图新入口使用 `customerView/singleView`，历史拼写 `customerView/singeView` 仅保留兼容路由壳。
- `customerMonitoring`：客户监控历史路由壳，当前实现已收敛到 `src/components/Customer/MonitoringEntries.js` 和 `src/components/Customer/MonitoringDetailEntries.js`。
- `lifeCycle` / `LifeCycle`：项目或客户生命周期聚合展示。
- `monitorEarly`：预警监控历史路由壳，当前实现已收敛到 `src/components/Risk/MonitorEarlyEntries.js`。
- `login`：登录和初次登录改密历史路由壳，页面实现已收敛到 `src/components/Permission/AuthEntries.js`，API 语义归属权限认证。
- `customer/customerRat`：客户评级列表历史路由壳，列表实现和列配置已收敛到 `src/components/Customer/CustomerRatingListEntries.js`。
- `customer/maintain`：客户维护列表历史路由壳，列表、详情和日志分别通过 `src/components/Customer/MaintainListEntries.js`、`src/components/Customer/MaintainDetailEntries.js` 和 `src/components/Customer/MaintainLogEntries.js` 暴露。
- `msgNotification`：消息中心历史路由壳，当前实现已收敛到 `src/components/Message/NotificationEntries.js`。
- `preview`：PDF、报表预览。
- `visitorManage`：拜访管理历史路由壳，当前入口收敛到 `src/components/VisitorManage/VisitorManageEntries.js`，页面实现命名为 `src/components/VisitorManage/VisitorManagePage.js`。
- `financialReport`：财务报表待办/审批/完成列表历史路由壳，当前待办和完成列表分别通过 `src/components/Report/FinancialReportWaitEntries.js`、`src/components/Report/FinancialReportFinishedEntries.js` 暴露，审批详情入口使用 `src/components/Report/FinancialReportApprovalEntries.js`。
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
- `budget/flowCenter`：银行流水/付款流水中心接口历史落在预算目录；预算流水中心页面通过 `src/components/Budget/FlowCenterEntries.js` 暴露，`src/pages/budget/flowCenter/*` 只保留路由壳；付款核销组件使用 `src/api/cpm/payment/writeOffFlowCenterApi` 语义入口，不再转发预算流水中心 API 文件。
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
- `common/customerOverview`：历史公共实现已清理，边界检查会阻止恢复；dashboard 客户总览页面使用 `src/api/dashboard/customerOverview`，客户视图页面使用 `src/api/customerView/customerOverviewApi` 聚合入口。
- `common/flowList`：历史公共目录已清理，边界检查会阻止恢复；流程任务列表、流程详情、退回节点和随机退回等 `/flow/**` 接口使用 `src/api/process/flowTaskApi`。
- `process/flowTaskApi.getProcessDetail`：预算管理和租赁物组件只读取流程详情状态时，分别使用 `src/api/budgetManagement/processDetailApi`、`src/api/lease/processDetailApi` 本域语义入口，不直接绑定流程任务 API 文件。
- `common/editableCompare`：历史上混合了 FTP、项目、付款和文件比对接口，当前已清理，边界检查会阻止恢复；文件表组件使用 `src/api/common/fileCompareApi`。
- `common/dataList`：历史命名已清理，边界检查会阻止恢复；资料清单上传、下载、预览、项目资料和授信资料列表使用 `src/api/common/materialsApi`。
- `customerView`：客户全景页是聚合展示面；从黑灰、风险预警、区域经济、客户总览读取数据时通过 `src/api/customerView` 下的语义入口，不直接穿透到各业务域生成 API；这些入口已收窄为 endpoint 壳，不再转发其他业务域 API 文件。
- `risk/customerUnifiedViewController`：客户全景区域经济和客户详情接口历史落在风险域，当前已清理并由边界检查阻止恢复；客户全景页使用 `src/api/customerView/riskAreaApi` 和 `customerDetailApi`。
- `workbench`：工作台/看板相关接口历史生成目录已清理，边界检查会阻止恢复；dashboard 页面优先使用 `src/api/dashboard` 下的语义入口。
- `dashboard/workbench`：旧工作台快捷功能和图表指标生成 API 当前无业务引用，已清理并由边界检查阻止恢复；工作台用户配置、消息和看板能力使用 `src/api/dashboard/userCustomConfigApi`、`workbenchMessageApi` 或其他 dashboard 语义 API。
- `utils/dashboard*`：dashboard 专用工具历史落在全局 utils；dashboard 页面和组件必须使用 `src/utils/domains/dashboard/DashboardUtils*`，历史 `src/utils/dashboard*.js` 文件已清理，边界检查会阻止恢复。
- `utils/processFlow`：流程详情上下文和动态表单配置历史落在全局 utils；流程页面和流程详情复用组件必须使用 `src/utils/domains/process/ProcessFlowContext`，历史 `src/utils/processFlow.js` 文件已清理，边界检查会阻止恢复。
- `utils/afterLease`、`utils/risk`、`utils/report`、`utils/kpi`、`utils/customer`、`utils/customerRat`、`utils/paymentApplication`、`utils/budgetManagement`：业务域小工具历史落在全局 utils；对应业务域页面和组件必须使用 `src/utils/domains/<domain>/*Utils` 语义入口，历史全局文件已清理，边界检查会阻止恢复。预算拨备预测刷新入口使用 `src/utils/domains/budget/ProvisionForecastUtils`，历史 `src/utils/domains/budgetManagement/BudgetManagementUtils` 兼容转发已清理。
- `utils/hooks/useGetColumns`、`utils/hooks/useLayoutEffect`：历史通用 hook 当前无业务引用且未从全局 utils 导出，已清理并由边界检查阻止恢复；后续如需类似能力，应放在实际业务域组件本地或明确的领域工具目录。
- `utils/rzyConfig`：RZY 厂商管理外部系统菜单和链接配置历史落在全局 utils；布局菜单和 RZY 页面必须使用 `src/utils/domains/rzy/RzyConfig`，历史 `src/utils/rzyConfig.js` 文件已清理，边界检查会阻止恢复。
- `utils/options/financialReport`、`utils/options/ftp`：历史业务选项文件已清理；后续若需要报表或 FTP 定价选项，应放入对应 `src/utils/domains/<domain>` 语义目录。
- `utils/hooks/useGetStatus`：黑灰名单审批状态筛选和按钮可用性历史落在全局 hooks；黑灰名单页面和组件必须使用 `src/utils/domains/blackGray/BlackGrayStatusUtils`，历史 `src/utils/hooks/useGetStatus.js` 文件已清理，边界检查会阻止恢复。
- `components/BlackGray/Actions`：黑灰名单审批动作集合；黑灰名单页面应通过对应黑灰名单窄入口或域内相对路径使用，其他业务域需要通用导出时使用 `src/components/Actions.StoreExportAction` 或其他公共 Actions。
- `components/BlackGray/RiskIframe`：黑灰名单外部查询页面 iframe 适配；黑灰名单查询页面应通过 `src/components/BlackGray/QueryIframeEntries.js` 使用。
- `components/BlackGray/Info`：黑灰名单命中标识组件；业务页面和组件应通过 `src/components/BlackGray/BlackGrayHitEntries.js` 使用。
- `components/Customer/FinancialReport/DeteleIcon`：客户财报域内删除图标组件历史拼写错误，当前已重命名为 `DeleteIcon`，边界检查会阻止旧文件恢复。
- `components/Process/BpmnFlowChart`、`components/Process/TaskFlowChart`：流程图组件；流程详情域内使用相对路径，外部审批记录通过 `src/components/Process/ProcessTaskFlowChartEntries.js` 使用。
- `components/Financial/ChangeLogLayout`：财务版本变更日志布局；财务付款/融资日志页面通过 `src/components/Financial/ChangeLogEntries.js` 使用。
- `process/flowExecution`：流程执行接口是流程中心通用能力；业务组件提交自身审批时使用本业务域的语义入口，例如客户评级使用 `src/api/customer/customerRat/customerRatApprovalApi`，不再转发流程 API 文件。
- `customer/customerRat/customerRatApi`：客户评级页面和客户组件保留客户域 API；项目立项/评审更新评级信息使用 `src/api/project/ratingApi`，项目接口实现不再挂在客户评级 API 内。
- `customer/customerRat/customerRatApi`、`customer/customerRat/debtRatApi`：流程详情展示评级摘要时使用 `src/api/process/detail/customerRatingApi` 和 `src/api/process/detail/debtRatingApi` 聚合入口，不再转发客户评级 API 文件。
- `customer/customerRat/customerRatApi`：流程操作中执行评级推翻等审批动作时使用 `src/api/process/operation/customerRatingOperationApi`，不再转发客户评级 API 文件。
- `customer/maintainApi`：客户维护页和客户组件保留客户域 API；流程申请列表占用客户后跳转详情时使用 `src/api/process/application/customerMaintainApi`，不再转发客户维护 API 文件。
- `utils/customerRat`：客户评级工具历史落在全局 utils；客户评级和流程操作必须使用 `src/utils/domains/customer/CustomerRatUtils`，历史 `src/utils/customerRat.js` 文件已清理，边界检查会阻止恢复。
- `customer/clientBasic`：客户维护基础信息 API 保留在客户域；行业、区域等 `/select` 字典优先使用 `src/api/common/selectApi`。
- `credit/creditReportApi.postCompareBusiness`：征信查询下的工商信息比对接口可由共享 `BusinessInfoCheck` 组件本地 `api.js` 聚合，调用方不应因此直接绑定征信域 API。
- `utils/paymentApplication`：付款申请校验工具历史落在全局 utils；付款组件和流程操作必须使用 `src/utils/domains/cpm/PaymentApplicationUtils`，历史 `src/utils/paymentApplication.js` 文件已清理，边界检查会阻止恢复。
- `cpm/payment/paymentApplicationDetail`：付款申请详情接口保留在付款域；流程详情展示付款资料时使用 `src/api/process/detail/paymentApplicationDetailApi` 聚合入口，不再转发付款申请 API 文件。
- `cpm/payment/paymentApplicationDetail`、`cpm/payment/publicInfoApi`：流程操作中执行付款申请前置校验或公开信息提交校验时使用 `src/api/process/operation` 下的聚合入口，不直接绑定付款域 API 文件。
- `approval/processModifyRemarkApi`：流程变更/复议说明是审批横向能力；共享审批组件使用 `src/api/common/approvalRemarkApi`，业务详情组件使用本业务域的 `approvalRemarkApi` 固定权限码入口，不再转发公共审批备注 API 文件。
- `contract/baseInfo`：合同基础信息接口本身保留在合同域；预算域取合同候选信息时使用 `src/api/budget/contractInfoApi`，跟踪事项选择项目/合同候选信息时使用 `src/api/trackEvent/contractInfoApi`，不再转发合同 API 文件。
- `contract/contractDetail`：合同详情接口保留在合同域；流程详情展示合同相关资料时使用 `src/api/process/detail/contractDetailApi` 聚合入口，不再转发合同详情 API 文件。
- `overdue/collectionManagementApi`、`overdue/sealForDocumentsApi`：逾期催收和用印资料接口保留在逾期域；流程详情展示逾期催收或诉讼用印资料时使用 `src/api/process/detail` 下的聚合入口，不再转发逾期域 API 文件。
- `project/projReviewDetail`、`project/projReviewMeetingMinute`：项目评审详情和会议纪要接口保留在项目域；流程详情展示项目评审资料或会议纪要时使用 `src/api/process/detail` 下的聚合入口，不再转发项目域 API 文件。
- `project/projReviewFinancialReport`：项目评审财务报表完整性接口保留在项目域；流程操作提交前校验时使用 `src/api/process/operation/projectReviewFinancialReportApi`，不再转发项目域 API 文件。
- `financial/fundApi.getLprLast`：最新 LPR 接口历史挂在融资 API；合同报价利率组件使用 `src/api/contract/priceApi` 语义入口。
- `header/projProfitTool`：全局入口触发的利润测算工具接口历史目录已清理，边界检查会阻止恢复；真实承载在 `src/api/layout/projProfitToolApi`。
- `common/irrGenerationApi`：IRR/现金流生成工具是全局 layout 工具弹窗能力，历史 common 入口和 interface 文件已清理，边界检查会阻止恢复；工具弹窗使用 `src/api/layout/irrGenerationApi` 语义入口，不直接绑定 common API 文件。
- `kpi/projProfit`、`kpi/baseSet/parameterConfig`：项目分润接口历史落在 KPI 目录；`src/api/kpi/projProfit` 和 `src/api/kpi/baseSet/parameterConfig` 历史路径已清理，边界检查会阻止恢复，预算分润页面优先使用 `src/api/budget/projectProfit*` 语义入口；KPI 自域仍保留 `src/api/kpi/baseSet/parameterConfigApi`。
- `message/messageNotification`：消息中心页面保留消息域 API；全局 layout 消息弹窗使用 `src/api/layout/messageApi`，dashboard 工作台消息列表使用 `src/api/dashboard/workbenchMessageApi`，不再转发消息中心 API 文件。
- `permission/login`：登录页保留权限域登录 API；全局 layout 快速切换登录使用 `src/api/layout/fastLoginApi`，不再转发登录 API 文件。
- `common/workbenchApi`：历史上混合了用户自定义配置和费控 SSO 授权，当前已清理，边界检查会阻止恢复；dashboard 工作台保存/查询用户配置使用 `src/api/dashboard/userCustomConfigApi`，通用表格列配置工具保留 `src/api/common/userCustomConfigApi`，费控 SSO 授权使用 `src/api/dashboard/feikongSsoApi`。
- `groupCredit/common`：历史上承载了创建人、组织、客户等通用选择接口；通用选择器优先使用 `src/api/common/selectApi` 语义入口。
- `groupCredit/projectApproval*`：集团授信立项接口历史生成目录；授信域页面和组件优先使用 `src/api/credit/groupCreditEstablish*` 语义入口。
- `ocr/list`、`ocr/recognition`：OCR 发票/车证列表、比对和上传识别实现已收敛到 `src/components/Ocr`，列表和识别页分别通过 `src/components/Ocr/ListEntries.js`、`RecognitionEntries.js` 暴露，上传弹窗保留为 OCR 内部组件；发票/车证 API 仍保留在 OCR 生成入口，后续若拆分租赁物语义 API 需另行评估。
- `cvicse`、`student`：当前代码树中已不存在；后续若恢复这些外部系统或实验路由，需先确认菜单、权限和外部链接来源。
- `mock`、`demo`、`example`：源码目录下不保留本地实验或样例文件；需要示例时应进入 README 或正式文档，不作为业务域实现文件存在。

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
- `npm run check:boundaries`：检查是否存在跨域深层组件路径、组件私有文件引用、公共组件子路径引用、组件域自引用 `*Entries.js`、`@/pages/**` 页面私有代码复用、未登记真实业务语义的顶层页面路由目录、`src/api` 内部跨业务域引用、未使用或未记录的领域级入口、未登记的跨域组件入口依赖边。
- `npm run report:component-entry-deps`：输出 `src` 内页面、组件、工具等对组件领域稳定入口形成的依赖关系，并对已确认的历史路由壳目录做领域归一化，用于判断后续边界整理优先级；`--write-baseline` 可在人工确认后刷新跨域入口依赖基线，`--fail-on-unlisted` 会同时检查新增未登记边和已过期基线边。
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
