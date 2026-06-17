# zswl-mithras-dashboard

`zswl-mithras-dashboard` 是看板与管报聚合模块，负责运营、项目、付款、租后、资金、财务管报和管理报表相关聚合查询。

本模块的核心语义是“跨域数据的展示型聚合”。它天然会读取多个业务域数据，但不应成为核心业务规则的归宿。

资产五级分类在看板中仅作为展示口径消费，稳定分类编码维护在 dashboard 本地枚举中，避免看板为了展示标签直接依赖资产分类流程域。

流程模型 key、流程状态展示和退回类型在看板中也只作为读侧查询/展示口径消费，已沉淀为 `DashboardProcessModel`、`DashboardProcessBusinessStatus` 和 `DashboardFlowCommentType` 本地枚举，避免仅为了流程 key、状态码或展示名直接复用 workflow/flow-core 内部枚举和 `FlowUtil`。

当前约 300 个 Java 文件，其中 `dashboard` 根包承载看板聚合，`guanbao` 根包承载管理报表/观远相关读模型。POM 中业务依赖大多是读侧展示所需的真实依赖；`workflow` 直接 POM 依赖已经移除。

本轮复核确认 POM 中的业务依赖都有源码或资源层使用证据；`GuanYuanSsoUtil` 直接使用 `org.apache.commons.codec.binary.Base64`，因此显式声明 `commons-codec`，避免继续依赖其它库的传递引入。

dashboard 源码已经不直接 import workflow 模块类型。流程准备、流程扩展字段和退回备注等读侧需求，由 dashboard 声明自己的 port，再由 `application/orchestration/adapter/dashboard` 适配 workflow 持久化模型、Mapper 和服务。

其中待办/抄送列表转换需要的流程客户关系和流程扩展展示字段，已通过 `DashboardProcessExtraPort` 改为 dashboard 声明读侧需求，由 `DashboardProcessExtraPortAdapter` 适配 workflow 的 `BizProcessData` 与 `FlowQueryExtra`。租后检查待发起列表通过 `DashboardAfterLeasePreparePort` 读取流程准备快照，由 `DashboardAfterLeasePreparePortAdapter` 适配 `CommonProcessPrepareService`。退回备注通过 `DashboardBackRemarkPort` 读取，由 `DashboardBackRemarkPortAdapter` 适配 workflow 操作记录。运营待办将到达统计和管苑合同退回历史通过 `DashboardOperateRecordPort` 读取操作记录和节点退回记录快照，由 `DashboardOperateRecordPortAdapter` 适配 flow-core 的 `OperateRecordMapper` 与 `NodeBackRecordMapper`。

当前 dashboard 源码已不直接读取 flow-core 的操作记录 mapper/entity，也不再直接 import flow-core 流程状态和退回类型枚举；仍直接使用部分 flow-core API/DTO 做流程读侧查询和转换。后续若继续收敛，可优先把流程查询结果也转成 dashboard 本地快照。

资源层体现了典型看板 SQL：`ManageReportMapper.xml`、`DashboardProjectStageMapper.xml` 等直接 join `act_*`、`business_status`、`proj_*`、`contract_*`、`payment_*`、`collection_*`、`margin_*`、`client`、`corp_commerce_info`、`bifrost_*` 等多域事实表。这些属于读模型聚合，不代表 dashboard 应拥有这些业务域。

客户看板明细中，customer 侧 `pageClientBasicInfo` 已从跨域 join 瘦身为客户基础事实查询；资产分类和结清日期筛选由 `application` 编排 assetclassify/collection 后再调用 customer。customer 侧查询对象已改为 `ClientBasicPageQuery`/`ClientBasicInfoDTO`，不再暴露 dashboard 包名和看板类名。

`src/main/resources/sql/风控应用/industry_index_comparison.sql` 虽然目录名带“风控应用”，但实际只向 `dashboard_config` 写入“行业指标对比”卡片配置；表模型和配置生命周期归 dashboard。当前不按目录名迁移，后续若要治理，优先统一 SQL 目录命名，而不是改变业务归属。

反向依赖相对集中。只看 Java import，主要消费方是 `application` 约 47 处、`web` 约 3 处；如果把 POM、Mapper、SQL 粗略搜索也算入，主要消费方还包括 `finance` 和 `creditreport` 的少量历史引用。核心业务域不应继续新增对 dashboard 内部查询模型的依赖；确需复用看板口径时，应由 application 做转换。

结论：保持独立，不并入 `finance`、`metric` 或 `report`。后续整理重点是保持单向读取关系：dashboard 可以读业务域，业务域不应反向依赖 dashboard；同时逐步拆清 `dashboard` 与 `guanbao` 的内部边界，前者是看板/驾驶舱，后者更像管理报表或外部 BI 集成。
