# zswl-mithras-projectprocess

`zswl-mithras-projectprocess` 是项目过程域模块，负责项目立项、项目评审、项目定价、现金流、会议纪要、项目状态、项目版本和审批材料。

本模块的核心语义是“租赁项目从机会到审批定价的业务过程”。客户是项目主体来源，合同、付款、风控、评级等通常是项目审批后的上下游。

当前判断是：保持独立，不并入 `contract`、`payment`、`riskcontrol` 或 `finance`。项目过程是合同、付款、风控、FTP、预算、报送等模块的上游事实源，物理合并会破坏交易主链路。

从代码证据看，模块 POM 依赖 `api`、`foundation`、`document`、`flow-core` 和框架能力；模块内 Java import 主要集中在 `projectprocess` 约 603 处、`foundation` 约 364 处、`dto` 约 298 处、`api` 约 64 处、`document` 约 11 处，并有 1 个流程结束监听接口直接使用 `flow-core`，没有直接 import contract/payment/customer/riskcontrol 等业务域。反向 Java 依赖很广，主要来自 `application` 约 872 处、`web` 约 101 处、`contract` 约 48 处、`riskcontrol` 约 30 处、`ftp` 约 23 处、`creditreport` 约 19 处、`metric` 约 15 处、`report` 约 14 处、`dashboard/afterlease` 各约 11 处。这说明 projectprocess 是核心交易事实源，不是薄模块。

POM 依赖复核后，`document`、`flow-core`、MapStruct、Hutool、FastJSON、MyBatis-Plus、Jackson annotations、Spring context/beans/web/tx/boot、validation、annotation、POI、XXL Job 都有源码使用依据；未发现 `pagehelper` 和 `commons-lang3` 的当前源码引用，已移除这两个直接依赖。MyBatis 注解由 MyBatis-Plus 依赖链覆盖。

模块已经通过若干 port 隔离外部事实和横向能力，例如 `PaymentCashFlowQueryPort`、`ProjectStatusSupportPort`、`ProjectRiskControlIndustryPort`、`ProjectProcessDictionaryPort`、`ProjectProcessSurvivingContractResolver`、`ProjReviewNoticeJobService`。这些接口由 `application` 侧 adapter 适配付款、合同、风控行业、字典、消息通知等能力，方向符合“项目过程定义需求，application 装配外部能力”。

资源层面，项目评审会议纪要相关表、权限点和接口脚本归本模块维护。原先放在 `payment` 资源目录中的 `评审会会议纪要_credit_date_check.sql` 已迁回本模块；付款页面调用该接口不改变接口的项目过程域归属。

需要注意的边界问题：

- POM 中直接依赖 `document`，主要服务项目材料、模板和版本处理。当前合理，但要避免 document 模型继续扩散到项目核心规则。
- POM 中直接依赖 `flow-core`，当前真实使用点是 `ILifecycleProcessor`：流程结束后查询流程信息，并把审批结果写为项目生命周期事件。这是项目过程与流程引擎的真实耦合点，短期不硬删；后续应考虑由 `application` 或 workflow adapter 把流程结束上下文转换为项目生命周期事件命令。
- `ProjEstablishBaseInfoMapper.xml` 的生命周期查询会 join `contract_base_info`，`ProjLifecycleEventMapper.xml` 会通过生命周期视图和合同、付款、收款等事实计算项目阶段/金额。这是项目生命周期读模型，不是 projectprocess 对合同/付款生命周期的所有权。
- `resources/sql/风控策略`、风险敞口字段、风控审批节点和 `ProjRiskControlIndustryTypeJob` 体现项目评审与风险审批的历史耦合。项目可以持有审批所需风险属性，但风险策略、风险报告和预警规则应归 riskcontrol 或 application 编排。
- `application/orchestration/projectprocess` 中仍有大量实现和跨域编排，后续需要识别哪些是项目单域规则、哪些是流程/材料/客户/合同/付款装配。

后续整理重点是保持项目过程主线清楚，将文档、流程和跨域取数从核心项目规则中分离。优先收敛外部模块直接引用 projectprocess mapper/model 的场景，为合同生成、风控评估、报送、FTP、预算等下游提供稳定查询服务、port 或 snapshot DTO。
