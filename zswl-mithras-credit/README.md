# zswl-mithras-credit

`zswl-mithras-credit` 是集团授信域模块，负责集团授信立项、授信评审、集团授信版本、授信审批流程和授信材料快照。

本模块的核心语义是“集团客户或关联主体的授信额度与授信审批”。它与客户、项目、评级、征信关系密切，但不应承担这些外域的主数据维护。

当前 POM 直接依赖 `api`、`foundation`、`document`。Java 层没有直接 import 客户、项目、评级、征信、风控、资金等业务域；`document` 依赖主要用于授信材料快照和材料版本比对。本轮复核确认源码仅使用 MyBatis 注解，直接 `org.mybatis:mybatis` 无独立必要性，已由 MyBatis-Plus starter 的传递依赖覆盖并移除；同时移除了无源码引用的 `commons-collections4`。

从 Java import 分布看，模块内引用约为：`credit` 112 处、`dto` 94 处、`foundation` 75 处、`api` 23 处、`document` 6 处、`validation` 1 处。POM 依赖与实际代码引用基本一致，没有明显无效业务依赖。

本轮继续复核 resources：`GroupCreditEstablishBaseInfoMapper.xml`、`GroupCreditReviewBaseInfoMapper.xml` 只围绕集团授信立项/评审自有表查询，未发现外域业务表 join；BPMN 资源属于授信立项/授信评审审批流程。历史迁移后遗留的空 `adapter`、`flow` 目录已清理，避免形成假边界信号。

模块内有两条核心主线：

- 集团授信立项/评审：`group_credit_establish_*`、`group_credit_review_*` 及其流程状态、版本、材料快照。
- 授信额度管理：`credit_limit`、`credit_limit_detail`、`credit_business_ref`、`credit_limit_change_record`，负责额度创建、占用、释放、失效和查询。

边界上需要注意：

- `GroupCreditEstablishBaseInfoMapper.xml`、`GroupCreditReviewBaseInfoMapper.xml` 基本只查询本域表，SQL 层没有明显跨域 join。
- `application`、`fund`、`projectprocess`、`creditreport` 等模块会消费授信事实；其中资金域占用/释放额度是合理跨域协作，但应稳定在额度服务和 BO 边界，避免外部直接理解授信持久化细节。
- 按 Java/POM/Mapper/SQL 粗略反向搜索，主要消费方是 `application` 约 159 处、`web` 约 43 处、`blackgray` 约 12 处、`creditreport` 约 10 处、`dashboard` 约 9 处、`fund` 约 7 处、`projectprocess` 约 4 处。
- 模块内已有 `GroupCreditEstablishProcessPort`、`GroupCreditEstablishContractPort`、`FundCreditEffectiveStatusService` 等较窄接口。流程启动/流程查询、合同风险敞口、融资授信过期处理应由外部实现或调用，credit 保留授信状态和额度规则。
- `application` 中仍有较多 groupcredit facade、流程、材料、项目联动代码，后续应区分真正跨域编排和可以回归 `credit` 的单域授信逻辑。

结论：`credit` 是 risk-credit 大域中的核心授信事实源，暂不建议并入 `creditreport`、`rating`、`riskcontrol` 或 `assetclassify`。后续整理重点是收敛外部模块对授信 mapper/model 的直接读取，并继续用 application 装配流程、材料和项目事实。

它也不应并入 `application`。`application` 可以持有授信与项目、流程、材料、合同风险敞口之间的编排，但授信立项、授信评审和额度占用/释放规则属于 `credit` 自己的业务事实。
