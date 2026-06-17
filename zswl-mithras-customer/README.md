# zswl-mithras-customer

`zswl-mithras-customer` 是客户域模块，负责法人/自然人客户、工商信息、股东、联系人、银行账户、关联企业、客户移交、生命周期和客户版本。

本模块的核心语义是“客户主数据与客户生命周期”。它是项目、合同、征信、风控、评级等模块的重要事实来源。

当前判断是：保持独立，不并入 projectprocess、contract、risk-credit 或 finance。客户是多个业务域共同消费的上游事实源，物理合并只会让下游场景污染客户主数据边界。

从代码证据看，`customer` 自身 POM 只依赖 `api`、`foundation` 和框架能力；模块内 Java import 主要集中在 `customer` 约 684 处、`dto` 约 361 处、`foundation` 约 294 处、`api` 约 109 处，没有直接 import 其他业务域。反向 Java 依赖很广，主要来自 `application` 约 849 处、`web` 约 147 处、`riskcontrol` 约 86 处、`report` 约 46 处、`creditreport` 约 22 处、`rating` 约 20 处、`contract` 约 18 处、`metric` 约 14 处、`afterlease` 约 12 处。这说明 customer 是事实源，不是薄模块。本轮复核确认 POM 中直接 `org.mybatis:mybatis` 依赖应保留：源码除 `@Param` 外还直接使用 `org.apache.ibatis.type.*` 自定义 TypeHandler，以及 `MapperBuilderAssistant` 等 MyBatis 核心类型，不应只依赖 MyBatis-Plus 的传递依赖。

模块内已有若干 port 用于隔离外部事实或横向能力，例如 `ClientUserDeptPort`、`ClientAuthorityDataPort`、`ClientDataSaveCheckPort`、`CustomerDictionaryPort`、`CorpCommerceInfoSupportPort`，由 `application` 侧 adapter 适配用户部门、权限、字典、第三方/外部支持等能力。客户提醒、客户逾期、客户释放、风控监控同步、消息通知等 job 也通过接口表达执行需求，方向基本符合“客户定义需求，application 装配外部能力”。

需要注意的边界问题：

- `ClientMapper.xml` 中 `pageClientBasicInfo` 原先直接 join `asset_classify_client`、`asset_classify`、`collection_base_info`，这是客户看板读模型混入 customer mapper。当前已将资产分类和最新租金日期筛选移到 `application` 编排层，由 application 分别向 assetclassify/collection 查询候选客户，再调用 customer 基础客户查询。customer mapper 现在只保留客户与工商基础事实。
- 原 `customer/dashboard/query` 下的看板查询对象已改为 customer-neutral 的 `ClientBasicPageQuery`，原 `DashboardClientBasicDTO` 已改为 `ClientBasicInfoDTO`。customer 对外暴露“客户基础分页查询”，DTO 只保留客户与工商基础事实；资产分类、结清日期、评级等看板/外域展示字段留在 application/dashboard 编排侧。
- `application/monitor`、`ClientViewByRiskControlJobService`、`ClientFocusOpinionSyncService` 以及 `sql/风控应用`、`sql/风控策略` 体现客户与风控监控的历史耦合。客户可以保存风险行业分类等客户属性，但风控预警、舆情、监控页面规则应归 riskcontrol 或 application 编排。当前已将客户监控列表中对 `risk_control_opinion_monitor`、`risk_control_warn_monitor` 的表级读取移出 `ClientMapper.xml`，由 application 先向 riskcontrol 查询风险客户和舆情数量，再传入 customer 查询客户列表。
- `sql/风控策略/customer.sql` 给 `corp_commerce_info`/`corp_commerce_info_lib` 增加风控行业分类字段，字段落在客户工商信息主表，短期仍归 customer 维护；但字段含义来自风险分类口径，后续应由 riskcontrol/application 提供分类计算或同步输入。
- `sql/风控应用/customer_monitoring.sql` 注册 `/clientMonitor/*` 菜单和权限点，实际 controller/service 在 application，列表数据来自 customer，预警/舆情详情来自 riskcontrol。这不是纯 customer 能力，后续更适合作为 application 编排入口或 riskcontrol 监控应用入口治理，不应继续扩大 customer 内的监控页面规则。
- `application/project/ProjectService` 只查询 `tmp_client_project` 判断客户是否有关联项目，仍是客户侧临时关系表，不代表 customer 应依赖 projectprocess；但命名容易误导，后续可改成客户关联项目状态查询。

后续整理重点是让其他业务域通过明确契约读取客户事实，避免客户域反向承担具体业务场景规则。优先治理 dashboard/risk/report 等读侧场景对 customer mapper/model 的直接使用，把高频客户事实沉淀为稳定查询服务、resolver 或 snapshot DTO；同时约束外部模块不要直接写客户表。
