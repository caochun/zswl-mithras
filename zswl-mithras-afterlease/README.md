# zswl-mithras-afterlease

`zswl-mithras-afterlease` 是租后管理域模块，负责租后检查计划、租后检查报告、租后外部信息查询、租后调整、租金催收、罚息减免和租后相关版本数据。

本模块的核心语义是“合同起租后的持续检查、跟踪、风险发现和处置”。它会读取客户、合同、项目、收款、风控、归档资料等事实，但不应直接承载这些外部业务域的主数据维护职责。

## 当前边界

- 租后检查：检查计划、检查客户、检查报告、报告内容/财务/摘要/模板、检查版本。
- 外部信息查询：按租后检查需要发起外部查询、记录查询客户及其角色、维护审批状态。
- 租后调整：展期、还款计划调整、调整审批、调整材料和版本。
- 租金催收：租金催收首页、催收明细、催收函邮件、催收模板和 HTML 记录。
- 罚息减免：罚息减免申请、明细记录、收款关联和流程状态。
- 租后支撑：检查初始化 job、超时提醒、流程协作 port、流程本地协议常量和文档模板。

资源归属上，租后检查报告模板表和模板数据归本模块维护。原先混在 `assetclassify` 初始化脚本里的资产分类非公开检查报告模板已迁回 `src/main/resources/sql/0.1.0/asset_classify_non_public_report_template.sql`。

## 依赖现状

当前 POM 只保留对 `document` 的业务域/横向能力依赖；`basedata`、`workflow`、`collection`、`customer`、`projectprocess`、`contract` 的直接 POM 依赖已随 Java import 清理移除。

从 Java import 分布看，当前已无 `basedata`、`workflow`、`collection`、`customer`、`projectprocess`、`contract` Java import，也已无 `flow-core` Java import。合同事实和合同版本库读取均通过租后本地 port 由 `application` 适配。

流程启动、流程查询、流程任务转换、流程结束处理和流程关联信息都已经通过 afterlease 本地 port/快照交给 `application` 适配；afterlease 模块源码不再暴露 flow-core 技术 DTO。

从反向依赖看，主要消费方是 `application`、`web` 和 `dashboard`：只看 Java import，`application` 约 312 处、`web` 约 24 处、`dashboard` 约 14 处；按 Java/POM/Mapper/SQL 全文粗略搜索，`application`、`web`、`dashboard` 仍是主要消费方，并有少量 `assetclassify`、`kpi`、`workflow`、`rating`、`margin` 引用或脚本关联。它不是无人使用的小模块，而是多个页面、看板和编排场景共同消费的租后事实源。

主要耦合形态：

- `AfterLeaseContractPortAdapter`、`AfterLeaseCollectionPortAdapter`、`AfterLeaseCorpCommercePortAdapter`、`RentCollectionEmail*PortAdapter` 等外域适配实现已经迁到 `application/orchestration/adapter/afterlease`，afterlease 模块内只保留 port 接口和租后业务规则。
- 主要跨域 port 已开始收窄为租后本地输入模型：`AfterLeaseContractPort` 的合同列表查询已改为返回 `AfterLeaseContractSnapshot`，`AfterLeaseClientPort.getById` 已改为返回 `AfterLeaseClientSnapshot`，`AfterLeaseContractRentActualPort` 已改为返回 `AfterLeaseContractRentSnapshot`，罚息减免审批所需单合同读取已改为 `AfterLeaseContractApprovalContext`，逾期催收标记更新已改为明确命令，租后检查批量客户数据接口也已不再接收 `Client` map。`AfterLeaseCollectionPort` 已用 `AfterLeaseCollectionEmailSnapshot`、`AfterLeaseReceiptCollectionSnapshot` 和 `AfterLeasePenaltyCollection` 替代公开签名里的 `CollectionBaseInfo` 与 MyBatis `Wrapper`。
- `RentCollectionDetailService` 已改为通过 `RentCollectionDetailDataPort` 获取租金催收明细、收款记录和逾期上下文，collection/contract 的 mapper、持久化 model 与 MyBatis 查询条件已迁到 `application/orchestration/adapter/afterlease/RentCollectionDetailDataPortAdapter`。`RentCollectionIndexServiceImpl` 中用于逾期状态、收款卡片和合同还款期数的 Java 层补充查询也已改走该 port。
- `RentCollectionEmailBankAccountPort` 和 `RentCollectionEmailPledgeAccountPort` 已改为返回 `RentCollectionEmailBankAccountSnapshot`，不再把 basedata 账户持久化模型或 DTO 暴露到 afterlease。
- `AfterLeaseMaterialsPort` 的查询侧已改为返回 `AfterLeaseMaterialSnapshot`，租后报告列表、检查报告下载和检查计划摘要报告不再直接消费 document 的 `MaterialsList` 持久化模型；document 材料查询由 `application/orchestration/adapter/afterlease/AfterLeaseMaterialsPortAdapter` 承接。
- `AfterLeaseClientPort` 已扩展为批量客户快照查询，`AfterLeaseCheckExternalQueryServiceImpl` 生成外部查询任务时不再直接注入 customer 的 `ClientMapper` 或消费 `Client` 持久化模型；客户名称、客户类型、主办和部门由 `application/orchestration/adapter/afterlease/AfterLeaseClientPortAdapter` 转换成 `AfterLeaseClientSnapshot`。
- `AfterLeaseContractPort.clientRoleNamesByContractId` 已承接合同参与方角色查询，`AfterLeaseCheckExternalQueryServiceImpl` 不再直接读取合同承租人/担保人 service、model 或合同侧角色枚举；合同参与方事实由 application adapter 转换成租后域能理解的角色名称集合。
- 租金催收相关展示逻辑已使用租后本地 `RentCollectionWriteOffStatus` 和本地字符串协议判断，去掉对 collection 核销状态枚举、contract 合同状态枚举和 customer 移动端日历枚举的 Java import；底层数据库状态值保持不变。
- 检查计划超时、逾期和审批提醒中的工作日历计算已改走 `AfterLeaseWorkdayCalendarPort`；特殊工作日配置仍由 application adapter 调用 basedata 提供，afterlease 不再直接依赖 basedata `DateUtil` 或 POM 依赖。
- afterlease POM 已移除 `basedata`、`workflow`、`collection`、`customer`、`projectprocess`、`contract` 直接依赖；这些协作都已由 application adapter 或租后本地 port 承接。剩余 POM 依赖中的 `document` 仍对应真实 Java import，暂不删除。
- 租后检查报告基本信息中的合同版本库读取已改为 `AfterLeaseContractVersionPort`，由 `application/orchestration/adapter/afterlease/AfterLeaseContractVersionPortAdapter` 适配合同版本服务；afterlease 只消费合同版本快照和租金版本快照。
- `AfterLeaseCheckExternalQueryRender` 已去掉对合同侧 `AbstractBasicRender` 的继承，只保留本地金额格式化和模板渲染逻辑，避免为了一个格式化方法引入 contract 模块。
- `AfterLeaseAdjustConvert` 和它的 `AfterLeaseTypeConversionWorker` 已迁到 `application/orchestration/afterlease/convert`。该 converter 只被 application 的租后调整实现使用，且承担 `ProjReviewBaseInfo` 到租后调整记录的跨域装配；afterlease 因此不再直接依赖 projectprocess。
- 罚息减免材料类型已收敛为 afterlease 本地 `AfterLeasePenaltyReductionMaterialsEnum`，`ReceiptCollectionServiceImpl` 不再直接 import document 的 `MaterialsEnum`；传给材料服务的业务模块和材料类型值保持不变。
- `genhtml/PaymentNoticeHtmlRender` 已改为只依赖 `PaymentNoticeRenderDataPort` 和租后邮件记录，收款、客户、合同版本、账户和用户信息由 `application/orchestration/adapter/afterlease/PaymentNoticeRenderDataPortAdapter` 组装。
- workflow 动态表单 handler、流程结束 handler 和流程准备 commit handle 已迁到 `application/orchestration/adapter/afterlease/workflow`。afterlease 已去掉对 workflow 流程枚举、材料节点常量和 `FlowEndEventProcessor` SPI 的直接依赖，流程模型 key、流程变量名和材料节点 id 改为租后域本地协议常量。
- 外部查询审批和罚息减免审批的流程启动、当前用户解析、`StartProcessReq` 组装、`BizProcessDataService` 记录、检查报告修改权限中的运行中流程节点判断，以及租后检查报告审批超时提醒的流程任务查询/转换，已迁到 `application/orchestration/adapter/afterlease/AfterLeaseWorkflowPortAdapter`；afterlease 只通过 `AfterLeaseWorkflowPort` 声明流程协作需求。
- 租后调整关联流程查询已改为返回 `AfterLeaseRelatedProcess` 本地快照，不再暴露 flow-core `ProcessResp`。
- `AfterLeaseCheckPlanProjectConvert` 需要 customer 的 `Client` 持久化模型，且调用方都在 application；该展示/装配转换器已迁到 `application/orchestration/afterlease/convert`，afterlease 模块不再因为它产生 customer 模型依赖。
- 材料版本化 handler `AfterLeaseCheckMaterialsListLibHandler` 仍直接使用 document 的 `MaterialsList`、`MaterialsListLib` 和版本 proxy；这是材料版本协议耦合，不同于普通查询侧材料列表，后续应单独评估是否由 document 提供更窄的版本化 port。
- Java 层已无 customer mapper/model 直接 import；SQL 层仍存在跨域读模型：`NewAfterLeaseCheckExternalQueryMapper.xml` join `client`，`NewAfterLeaseCheckPlanBaseMapper.xml` 读取 `asset_classify_client`、`bifrost_user`、`bifrost_org`，`RentCollectionIndexMapper.xml` join `payment_base_info`、`contract_base_info`、`collection_base_info`。其中 `RentCollectionIndexMapper.xml` 是催收首页主列表读模型，拆分风险高于 Java 层补充查询，应单独设计查询快照或 application 组装方案。
- 反向依赖主要来自 `application` 和 `dashboard`。`application` 负责编排租后流程、文档渲染、合同/客户/付款联动和 job；`dashboard` 读取租后检查计划用于看板展示。

## 模块判断

`afterlease` 不建议并入 `contract`、`collection` 或 `riskcontrol`。它消费合同起租后的合同、客户、收款和风险事实，但自身拥有独立的检查、调整、外部查询、催收、减免和版本生命周期。

它也不适合作为 `application` 内部包存在。`application` 应负责把合同、收款、客户、流程、文档、消息等外部能力接到租后用例上，而不是拥有租后检查计划、检查报告、租后调整、罚息减免这些业务事实。

后续整理重点：

- 继续保持 adapter 在 `application/orchestration/adapter/afterlease`，afterlease 只保留 port 接口和业务规则。
- 收窄 port 入参和返回值，用租后输入快照/查询 DTO 替代外域 model、mapper、Wrapper。
- workflow 动态表单、流程结束 handler 和流程准备 commit handle 已迁到 `application`；workflow 枚举、材料节点常量、流程结束 SPI、流程启动、运行中流程节点判断、审批提醒流程任务查询/转换、关联流程返回值和流程业务数据记录已从 afterlease 中剥离。后续重点转向收窄外域 model、mapper、Wrapper 和跨表 SQL。
- SQL 层对 `client`、`asset_classify_client`、`bifrost_user` 的读取后续应收敛到租后查询模型或应用层装配。

建议执行顺序：

1. 先迁出 `afterlease.adapter` 中 7 个外域 adapter，由 `application` 实现这些 port，租后模块保留接口。（已完成）
2. 再处理 `afterlease/application/workflow` 下动态表单和流程结束 handler，把 workflow SPI 迁到 `application`。（已完成）
3. 已拆掉流程枚举、流程变量名、材料节点常量和流程结束 SPI 对 workflow 的直接依赖。（已完成）
4. 已把外部查询审批、罚息减免审批的流程启动和 `BizProcessDataService` 记录迁到 `application` 的 `AfterLeaseWorkflowPortAdapter`。（已完成）
5. 已处理 `AfterLeaseAdjustInfoService` 的 `ProcessResp` 返回值，改为租后本地 `AfterLeaseRelatedProcess` 快照。（已完成）
6. 已收窄 `AfterLeaseClientPort.getById` 和租后检查批量客户数据接口，去掉公开签名中的 `Client` 模型。（已完成）
7. 已收窄 `AfterLeaseContractRentActualPort`，用 `AfterLeaseContractRentSnapshot` 替代 `ContractRentActual`。（已完成）
8. 已收窄罚息减免审批所需的合同读取和逾期催收标记更新，用 `AfterLeaseContractApprovalContext` 和 `markOverdueCollectionNotified` 替代完整 `ContractBaseInfo` 读写。（已完成）
9. 已收窄 `AfterLeaseContractPort` 的合同列表查询，用 `AfterLeaseContractSnapshot` 替代 `ContractBaseInfo`。（已完成）
10. 已收窄 `RentCollectionDetailService` 对 collection/contract 的读取，改为 afterlease 本地快照和 `application` 适配。（已完成）
11. 已收窄 `RentCollectionIndexServiceImpl` Java 层对 collection/contract 的补充读取，改为 afterlease 本地快照和 `application` 适配。（已完成）
12. 已收窄租金催收邮件账户 port，用 `RentCollectionEmailBankAccountSnapshot` 替代 basedata 账户模型/DTO。（已完成）
13. 已收窄 `AfterLeaseMaterialsPort` 查询侧，用 `AfterLeaseMaterialSnapshot` 替代 document 的 `MaterialsList` 持久化模型。（已完成）
14. 已将需要 customer `Client` 模型的 `AfterLeaseCheckPlanProjectConvert` 迁到 application 编排层，避免 afterlease 因展示转换器依赖客户持久化模型。（已完成）
15. 已收窄外部查询任务生成中的客户读取，用 `AfterLeaseClientSnapshot` 替代 customer `ClientMapper`/`Client` 直接依赖。（已完成）
16. 已收窄外部查询任务生成中的合同参与方角色读取，用 `AfterLeaseContractPort.clientRoleNamesByContractId` 替代 afterlease 直接读取合同承租人/担保人 service、model 和合同侧角色枚举。（已完成）
17. 已将租金催收展示中的 collection/contract/customer 枚举 import 收敛为租后本地状态/字符串协议，降低低价值枚举依赖。（已完成）
18. 已将工作日历能力改为 `AfterLeaseWorkdayCalendarPort`，由 application 适配 basedata 特殊工作日配置，并移除 afterlease 对 basedata 的直接 POM 依赖。（已完成）
19. 已移除 afterlease POM 中无源码引用的 `workflow`、`collection`、`customer` 直接依赖。（已完成）
20. 已将只被 application 使用且依赖 projectprocess `ProjReviewBaseInfo` 的 `AfterLeaseAdjustConvert` 迁到 application，移除 afterlease 对 projectprocess 的直接 POM 依赖。（已完成）
21. 已将罚息减免材料类型改为 afterlease 本地 `AfterLeasePenaltyReductionMaterialsEnum`，去掉 `ReceiptCollectionServiceImpl` 对 document `MaterialsEnum` 的直接依赖。（已完成）
22. 已通过 `AfterLeaseContractVersionPort` 收窄租后报告对合同版本库的读取，并移除 afterlease 对 contract 的直接 POM/Java 依赖。（已完成）
23. 最后继续收窄材料版本化协议和跨表 SQL；这一步涉及接口语义变化，风险高于迁包，应放在 workflow 适配迁出之后。
