# Mithras React 前端

`zswl-mithras-react` 是 Mithras 系统的前端工程，承载客户、项目、合同、付款、收款、风控、报表、流程、文件等业务页面和用户交互。

前端的核心语义是“用户界面与前端交互编排”。业务规则、审批状态、权限判断和数据一致性以后端接口为准；前端负责展示、输入校验、页面流转、轻量状态管理和操作反馈。

## 分层边界

前端目录不需要和后端 Maven 模块一比一对应。前端更贴近菜单、路由和用户工作流，但代码复用关系必须清楚。

- `src/pages`：路由入口和页面私有代码。页面目录内的组件、配置、`api.js` 默认只服务当前页面或当前业务域内部。
- `src/components`：跨业务域复用的通用 UI、表格、表单、金额、文件、流程图、操作按钮等组件。
- `src/api`：跨页面复用的接口封装。若某个 `pages/**/api.js` 被其他业务域引用，应迁入这里或对应稳定领域入口。
- `src/utils`：无页面语义、无业务归属的工具函数、格式化、hooks、校验逻辑。
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
- `AfterLease/CheckPlanCreateEntries.js`
- `AfterLease/CheckPlanDetailEntries.js`
- `AfterLease/CheckPlanExternalEntries.js`
- `AfterLease/CheckPlanPrepareEntries.js`
- `AfterLease/CheckPlanTemplateEntries.js`
- `BlackInfo/BlackInfoEntries.js`
- `BlackGray/BlackGrayEntries.js`
- `BpmnFlowChooseChart/BpmnFlowChooseChartEntries.js`
- `Budget/AccountsReceivableEntries.js`
- `Budget/ExchangeRateEntries.js`
- `Budget/PricingBaseDataEntries.js`
- `Budget/PricingBaseSetEntries.js`
- `Budget/PricingBusinessEntries.js`
- `Budget/PricingFtpInterestEntries.js`
- `Budget/ProfitDistributionEntries.js`
- `Budget/ProjectProfitEntries.js`
- `Budget/ProvisioningEntries.js`
- `BudgetManagement/BudgetManagementEntries.js`
- `Chart/BarChartEntries.js`
- `Chart/LineChartEntries.js`
- `Chart/TooltipEntries.js`
- `CheckBusiness/CheckBusinessEntries.js`
- `ClientFileTable/ClientFileTableEntries.js`
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
- `Customer/FinancialReportEntries.js`
- `Customer/HandoverEntries.js`
- `Customer/MaintainEntries.js`
- `Customer/SingleViewRiskEntries.js`
- `Dashboard/DashboardEntries.js`
- `Dashboard/MyAchievementEntries.js`
- `EvaluationAgency/EvaluationAgencyEntries.js`
- `FileDiff/FileDiffEntries.js`
- `Financial/DirectDetailEntries.js`
- `Financial/FinancingCarryInterestEntries.js`
- `Financial/FinancingUrlEntries.js`
- `Financial/FundDetailEntries.js`
- `Financial/FundListEntries.js`
- `Financial/FundProcessEntries.js`
- `Financial/PaymentBatchApprovalEntries.js`
- `Financial/PaymentDetailEntries.js`
- `Financial/PaymentListEntries.js`
- `FilingMaterials/AfterApplyEntries.js`
- `FilingMaterials/ApplyEntries.js`
- `FilingMaterials/FundApplyEntries.js`
- `FilingMaterials/OtherApplyEntries.js`
- `Kpi/BaseSetModalDetailEntries.js`
- `Kpi/KpiEstimationEntries.js`
- `Kpi/PmAssessEntries.js`
- `Kpi/ProjectAllotDetailEntries.js`
- `Kpi/ProjectAllotFormEntries.js`
- `Kpi/ProjectAllotListEntries.js`
- `Lease/ApprovalConfirmEntries.js`
- `Lease/MaintainEntries.js`
- `LifeCycle/LifeCycleEntries.js`
- `LoginIframe/LoginIframeEntries.js`
- `JumpClient/JumpClientEntries.js`
- `Overdue/OverdueEntries.js`
- `PaymentApplyColumns/PaymentApplyColumnsEntries.js`
- `PolicyColumns/PolicyColumnsEntries.js`
- `Policy/PolicyEntries.js`
- `Process/PrepareDetailEntries.js`
- `Process/ProcessEntries.js`
- `Project/ClientSelectEntries.js`
- `Project/EstablishmentDetailEntries.js`
- `Project/FinancialReportStatisticsEntries.js`
- `Project/FormListItemEntries.js`
- `Project/PriceDetailEntries.js`
- `Project/ReviewDetailEntries.js`
- `Project/ReviewMeetingEntries.js`
- `Project/ReviewProcessEntries.js`
- `Project/ReviewSnapshotEntries.js`
- `Report/FinancialReportApprovalEntries.js`
- `Report/FinancialReportListEntries.js`
- `Report/OperationEntries.js`
- `Risk/ConcentrationControlEntries.js`
- `Risk/MetricValueEntries.js`
- `Risk/OverdueEntries.js`
- `Risk/PublicMonitorColumnsEntries.js`
- `Risk/PublicMonitorDetailEntries.js`
- `Risk/PublicMonitorListEntries.js`
- `Risk/RelateMonitorEntries.js`
- `Risk/SourceCardEntries.js`
- `TrackEvent/TrackingEntries.js`
- `UpdateRatingInfoButton/UpdateRatingInfoButtonEntries.js`
- `WhiteList/WhiteListEntries.js`
- `ZhongDengButton/ZhongDengButtonEntries.js`

## 依赖规则

- 禁止一个业务页面长期直接复用另一个业务页面目录下的组件、配置或 API。
- `src/pages/<domainA>` 引用 `src/pages/<domainB>` 时，应先判断被引用内容是否应迁到 `src/components`、`src/api`、`src/utils` 或未来的 `src/features/<domain>`。
- 工作台、报表、流程这类聚合页面可以编排多个业务域页面，但聚合逻辑应显式，不能让聚合页面变成公共组件库。
- `process` 负责流程壳和审批交互；业务详情组件应尽量由业务域提供，再由流程页面进行装配。
- 通用组件应保持无业务名称、无菜单名称、无特定页面状态依赖。
- 若确实需要跨业务域复用能力，先在被调用领域新增或复用 `*Entries.js`，再由调用方引入。
- 禁止直接跨域引用组件内部的 `api`、`store`、`Column`、`Config`、`context` 等私有文件。
- 已收敛到领域入口的共享业务组件禁止再通过组件根目录直连，例如 `BlackInfo`、`ClientFileTable`、`EvaluationAgency`、`FileDiff`、`JumpClient`、`PaymentApplyColumns`、`Policy`、`PolicyColumns`、`UpdateRatingInfoButton`、`ZhongDengButton` 应通过对应 `*Entries.js` 引入。
- 禁止绕过公共组件稳定入口引用 `Actions/*`、`Form/*`、`Format/*`、`Table/*`、`BreadLine/config`、`Chart/tooltip`。
- 禁止业务代码直接引用 `blackList`、`postRentalInspection`、`riskControl`、`liquidity`、`pricing`、`newFtp`、`financialReport`、`manageReport`、`fillingMaterials`、`workbench`、`header` 等历史 API 目录，应使用对应语义领域入口。
- 禁止预算应收账款页面和组件直接引用 `financial/accountsReceivable` 历史 API 前缀，应使用 `budget/accountsReceivable` 语义入口。
- `npm run check:boundaries` 会扫描整个 `src`，禁止非 `Entries/entries` 的 `@/components/<domain>/<subpath>` 导入，禁止已收敛共享业务组件的根目录直连，禁止组件域内部反向引用自身 `*Entries.js`，禁止通过 `@/pages/**` 复用页面私有代码，禁止直接引用历史 API 目录，并校验领域级入口已被代码使用且同步记录在 README。

## 当前边界收敛

- 表格、文件表、描述表、审批详情等统一从 `src/components/Table` 稳定入口导入。
- 表单金额、只读表单、银行账号、日期范围等统一从 `src/components/Form` 稳定入口导入。
- 文件导出、模板下载、审批操作等统一从 `src/components/Actions` 稳定入口导入。
- 格式化列、可编辑列、超时展示等统一从 `src/components/Format` 稳定入口导入。
- 财务、预算等外部页面不再从 `dashboard/workbench/components` 取通用表格合计和文件导出能力。
- `dashboard/workbench/components` 暂时保留工作台内部私有组件；后续只处理确实跨业务域复用的部分。
- 流程准备详情页通过 `src/components/Process/PrepareDetailEntries.js` 装配业务域详情组件。
- 组件域之间的跨域能力复用已收敛到领域入口，避免调用方绑定对方内部实现路径。

## 目录语义

前端目录同时受到业务域、菜单路由、历史兼容路径影响，不能简单按后端模块一比一拆分。

当前较清晰的业务域目录：

- `afterLease` / `AfterLease`：租后检查、租后调整、租后回款等。
- `budget` / `Budget`：预算、定价、应收账款、印花税、计提等。
- `budgetManagement` / `BudgetManagement`：预算管理、计划、目标、参数配置等。
- `contract` / `Contract`：合同详情、起租、提前结清、合同材料等。
- `cpm` / `Cpm`：付款申请、付款核销、保证金、合同付款管理等。
- `credit`、`creditManage` / `Credit`、`CreditManage`：授信审批和授信查询/台账能力。
- `customer` / `Customer`：客户维护、客户评级、债项评级、客户财报等。
- `financial` / `Financial`：融资、资金、流动性、金融机构、应付利息等。
- `kpi` / `Kpi`：绩效分配、绩效参数、绩效测算等。
- `lease` / `Lease`：租赁物维护和租赁物跟踪。
- `project` / `Project`：项目立项、项目定价、项目评审。
- `risk` / `Risk`：风控指标、风险策略、公开监控、评分卡等。
- `report` / `Report`：管理报表、运营报表、内部历史报表等。
- `process` / `Process`：流程中心、流程详情、审批记录、流程准备详情装配。

当前带有聚合或展示面语义的目录：

- `dashboard` / `Dashboard`：工作台、总览、看板、SSO 入口，偏展示与聚合。
- `customerView`：客户单一视图/客户画像聚合页，复用客户、财报、区域数据等能力。
- `customerMonitoring`：客户监控大屏/可视化页，偏展示面。
- `lifeCycle` / `LifeCycle`：项目或客户生命周期聚合展示。
- `monitorEarly`：预警监控展示。
- `preview`：PDF、报表预览。
- `financialReport`：财务报表待办/审批/完成列表，偏报表流程视图。

当前历史或兼容壳目录：

- `ProfitDistribution`：项目分润路由壳，业务语义更接近 `budget/projProfit` 或 KPI/预算分润。
- `overdueListSearch`：逾期列表查询历史路由壳，当前实现已收敛到 `src/components/Risk/OverdueEntries.js`。
- `workbench`：工作台兼容入口，实际能力应优先落在 `dashboard/workbench`。
- `blackListManage`：页面目录仍沿用黑名单管理命名，组件目录已是 `BlackGray`；API 调用优先使用 `src/api/blackGray` 兼容入口，后续可考虑菜单路径稳定的前提下收敛命名。
- `postRentalInspection`：租后检查 API 的历史命名目录；租后业务代码优先使用 `src/api/afterLease` 下的兼容入口。
- `riskControl`：风控接口历史命名目录；风险域页面和组件优先使用 `src/api/risk` 下的兼容入口。
- `liquidity`：金融流动性接口历史命名目录；金融域页面优先使用 `src/api/financial/liquidity` 下的兼容入口。
- `financial/accountsReceivable`：应收账款接口历史落在财务目录；预算应收账款页面和组件优先使用 `src/api/budget/accountsReceivable` 下的语义入口。
- `cpm/payment/contractPaymentFtp`：合同付款 FTP 接口历史落在付款目录；合同付款申请组件优先使用 `src/api/contract/payment/contractPaymentFtp` 语义入口。
- `pricing`：预算定价接口历史命名目录；预算定价页面优先使用 `src/api/budget/pricing` 下的兼容入口。
- `newFtp`：新版 FTP 定价接口历史命名目录；预算定价页面和组件优先使用 `src/api/budget/pricing/ftp` 下的兼容入口。
- `financialReport`、`manageReport`：报表接口历史命名目录；报表页面和组件优先使用 `src/api/report` 下的兼容入口。
- `fillingMaterialsDetail`、`fillingMaterials`：归档资料接口和路由的历史拼写目录；资料归集页面和组件优先使用 `src/api/filingMaterials`、`src/components/FilingMaterials` 下的兼容入口。
- `common/customerOverview`：客户总览接口的历史公共目录；dashboard 客户总览页面优先使用 `src/api/dashboard/customerOverview`，客户视图页面优先使用 `src/api/customer/customerOverview` 兼容入口。
- `workbench`：工作台/看板相关接口历史生成目录；dashboard 页面优先使用 `src/api/dashboard` 下的语义入口。
- `header/projProfitTool`：全局入口触发的利润测算工具接口历史目录；业务语义优先使用 `src/api/kpi/projProfit/profitCalculateTool` 兼容入口。
- `common/workbenchApi`：历史上混合了用户自定义配置和费控 SSO 授权；保存/查询用户配置优先使用 `src/api/common/userCustomConfigApi` 兼容入口。
- `rzy`、`implant`、`cvicse`、`student`、`visitorManage`：外部系统、嵌入页或历史实验目录，重构前需先确认路由和菜单来源。

目录整理原则：

- 路由目录改名必须先确认菜单、权限、后端路由配置和外部链接，不做纯前端局部改名。
- 小型兼容壳目录可以先保持路径，内部通过稳定入口引用真实业务能力。
- 聚合页不要沉淀公共业务能力；一旦被其他业务域复用，应迁入业务域组件或领域入口。
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
- `npm run check:boundaries`：检查是否存在跨域深层组件路径、组件私有文件引用、公共组件子路径引用、组件域自引用 `*Entries.js`、`@/pages/**` 页面私有代码复用、未使用或未记录的领域级入口。
- `node scripts/report-component-entry-deps.js`：输出 `src` 内页面、组件、工具等对组件领域稳定入口形成的依赖关系，并对已确认的历史路由壳目录做领域归一化，用于判断后续边界整理优先级。
- `node scripts/report-api-domain-deps.js`：输出 `src` 内页面、组件、工具等对跨域 API 的依赖关系，用于识别需要收敛到领域组件、领域入口或 `src/api/<domain>` 的候选点。

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
