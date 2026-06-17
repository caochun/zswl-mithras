# zswl-mithras-payment

`zswl-mithras-payment` 是付款域模块，负责付款申请、付款实际、付款流程、付款政策/问卷、公共付款信息、FTP 价格相关能力和付款核销事件。

本模块的核心语义是“从合同或项目触发的对外付款过程及其审批、执行和记录”。客户、项目、合同是付款上下文，不应由付款域维护其主数据。

## 依赖现状

当前 POM 直接依赖 `api`、`foundation`、`document`。`payment` 已去掉对 `workflow`、`cn.zswltech.flow:core`、`contract`、`customer` 的直接 POM 依赖；源码层也不再直接 import workflow/flow-core/contract/customer API。

模块源码约 135 个 Java 文件，拥有付款申请、计划付款明细、实际付款明细、付款保单、付款问卷、公开信息查询、FTP 考核信息、付款版本和材料版本等模型。当前 Java import 粗分约为 `api` 164、`payment` 154、`foundation` 136、`dto` 31、`document` 4；除 document 材料版本处理外，没有具体业务域源码依赖。

当前 POM 中的技术依赖均有源码证据，暂未发现可安全删除项：

- Gruul：权限校验和当前账号读取使用 `AccountUtil`、`UserDO`、`AccountVO`。
- `oss-toolkit`、EasyExcel、Guava：公开信息查询报告下载、材料复制和 Excel 导出使用 `OssClient`、`EasyExcelFactory`、`ByteStreams`。
- Spring context/beans/web/tx、Servlet、validation、Swagger：组件、Controller、事务、下载响应和接口校验使用。
- MyBatis-Plus/MyBatis 注解：mapper、model、service、分页和注解 SQL 使用。
- Hutool、Fastjson、commons-collections4、SLF4J：校验、转换、集合判断和日志使用。
- MapStruct：付款转换器使用。
- XXL Job：付款超期计算和公开信息复制重试任务使用。
- poi-tl：付款审批文档渲染使用 `XWPFTemplate`。

资源层面曾存在明显读侧耦合，本轮已迁出主要跨域读模型：

- `PaymentBaseInfoMapper.xml` 已删除。原本剩余的 `updateFinanceStatusByCode` 只是更新 `payment_base_info.financial_status` 的自有表操作，已改为 `PaymentBaseInfoMapper` 注解 SQL。
- 原 `PaymentBaseInfoMapper.myList` 已迁到 `application.orchestration.payment.mapper.PaymentProcessQueryMapper.listPayment`。它服务付款首页列表，需要按合同业务部门筛选/展示，归属为 application 的 payment + contract 读模型。
- 原 `PaymentBaseInfoMapper.paymentFlowList` 已迁到 `application.orchestration.collection.mapper.CollectionPaymentFlowCenterMapper`，对应 XML 位于 `zswl-mithras-application/src/main/resources/mapper/application/CollectionPaymentFlowCenterMapper.xml`。它仍直接读取 `payment_base_info`、`contract_base_info`、`margin_base_info`、`warranty_base_info`、`contract_rent_actual`、`margin_record_info`、`warranty_record_info`，但归属已从付款域内部 mapper 改为 application 跨域读模型。
- 原 `PaymentActualDetailMapper.listContractPayInfoBetween`、`listContractPayInfoBeforeTargetDate` 已迁出 payment。当前由 `budget.application.port.BudgetPaymentFactPort` 表达预算收益测算需要的合同付款事实快照，application 的 `BudgetPaymentFactMapper.xml` 承接合同、付款实际和收款计划的跨域 SQL；payment 不再维护 `ContractPayInfoDTO` 和对应 XML。
- 原 `PaymentBaseInfoMapper.queryListWithContractId` 已迁到 `application.orchestration.payment.mapper.PaymentProcessQueryMapper`。它服务合同侧查询“合同下审批通过的付款申请”，需要同时读取 payment 表和 Flowable 历史表，归属为 application 跨域读模型。
- `PaymentPolicyMapper.xml` 同时操作 `payment_policy_info` 和 `payment_base_info`，付款保单与正式保单生命周期存在同步关系。

按 mapper 方法进一步拆分，当前跨域 SQL 已从 payment 内部 mapper 迁到 application 读侧 mapper：

- `PaymentProcessQueryMapper.listPayment` 是付款申请列表查询，直接读取 `contract_base_info.biz_dept_id` 用于部门过滤和展示。它仍是跨域读模型，后续如果要继续收敛，应把合同部门口径改成付款申请创建时的冗余字段，或拆成 payment 列表快照 + contract 部门筛选输入。
- `CollectionPaymentFlowCenterMapper.paymentFlowList` 是收付中心付款业务流水列表，调用方是 `application.orchestration.collection.CollectionFlowCenterService.paymentList`。它同时拼接付款、保证金、质保金、合同、租金计划和退回记录，语义上是 collection/capital 视角的跨域现金流读模型，当前已不再作为 payment 内部 mapper 能力。
- `PaymentProcessQueryMapper.listApprovedPaymentCreateByContractId` 查询合同下审批通过的付款申请，依赖 Flowable 历史表判断流程 key 和审批状态。它服务合同侧校验/展示，属于付款事实 + 流程上下文的读模型，当前已从 payment XML 中迁出。
- `BudgetPaymentFactPort.listContractPayInfoBetween`、`listContractPayInfoBeforeTargetDate` 现在由预算域定义语义，application 适配层拼接合同、付款实际和收款计划。后续若继续收敛，应再把 application 读侧 SQL 拆成 payment/collection/contract 的稳定快照或由各事实源提供更窄查询。
- `PaymentPolicyInfoMapper.countPolicyCode`、`listPolicyCode`、`occupyPolicy`、`cancelOccupyPolicy` 只围绕 `payment_policy_info` 和 `payment_base_info`，当前仍属于付款保单占用/释放视角；需要继续和正式 `policy` 域同步边界分开，但不是优先迁出的跨域 SQL。

反向依赖非常广，按 Java/POM/Mapper/SQL 粗略搜索，主要消费方包括 `application` 约 456 处、`web` 约 271 处、`report` 约 269 处、`dashboard` 约 54 处、`kpi` 约 51 处、`creditreport` 约 36 处、`contract` 约 36 处、`metric` 约 35 处、`policy` 约 31 处、`collection` 约 19 处、`finance` 约 17 处、`riskcontrol` 约 16 处。付款是核心交易事实源，不是页面或报表附属模块。

## 边界判断

`payment` 应保持独立，不建议并入 `finance`、`collection`、`contract` 或 `policy`。

- `payment` 拥有付款申请、审批状态、计划付款、实际付款、付款核销、付款版本和付款材料关系。
- `collection` 是收款/应收方向，生命周期与付款相反；二者可以共享现金流/核销公共语言，但不应共享持久化模型。
- `contract` 是付款发生的业务上下文，不应拥有付款审批和执行事实。
- `policy` 是保单生命周期，付款保单只是付款中的保单关联/占用视角。

当前 `payment` 已经移除对 `third`、`contract`、`customer` 的直接依赖。公开信息查询展示外部 providence 查询结果时，付款域通过 `PaymentPublicInfoSupportPort#getLatestOuterQuerySnapshot` 获取查询快照，不直接依赖 third 模块的持久化模型。

当前 `payment` 也已移除对 `workflow` / `flow-core` 的直接依赖。付款域通过 `PaymentWorkflowPort` 表达自身需要的流程查询语义，例如查询付款申请在办流程、判断付款核销明细是否在审批中、获取最新通过的项目评审流程号；具体的 `FlowTaskApiService`、`ProcessService`、`ProcessModelTypeEnum`、`ProcessBusinessStatusEnum` 适配放在 `application` 的 `PaymentWorkflowPortAdapter`。

公开信息材料读取已收窄：`PaymentPublicInfoSupportPort` 不再暴露 document 的 `MaterialsList` 持久化模型。页面展示使用稳定的 `FileListRSP`，复制公开信息材料时使用付款域自有 `PaymentPublicInfoMaterialSnapshot`；document 持久化模型只留在 `application` adapter 内部。

公开信息合同/客户上下文读取也已收窄：`PaymentPublicInfoSupportPort` 不再暴露 contract 的 `ContractBaseInfo` 持久化模型。复制公开信息时只通过付款域自有 `PaymentPublicInfoContractSnapshot` 获取所需合同 id；客户列表、合同主办校验、同项目评审合同查询所需的合同 id、客户类型、合同主办、项目评审 id 等信息通过 `PaymentPublicInfoContractContextSnapshot` 获取。contract/customer 持久化模型只留在 `application` adapter 内部。

公开信息合同参与方读取已收窄：`PublicInfoQueryService` 不再直接注入合同参与方 service，也不直接消费 `ContractTenantry`、`ContractGuarantor`、`ContractMortgage`、`ContractPledge` 等合同持久化模型。承租人、担保人、抵押人、质押人客户集合通过 `PaymentPublicInfoContractParticipantSnapshot` 输入付款域，具体合同读取留在 `application` adapter。

资源层面，原先放在本模块的 `评审会会议纪要_credit_date_check.sql` 已迁回 `projectprocess`。该脚本注册的是 `/proj/review/meet/minute/credit/date/check` 项目评审会议纪要接口权限点，虽然付款页面会调用它，但接口和业务能力归属是项目过程域。

`src/main/resources/sql/风控应用/public_info_config.sql` 虽然目录名带“风控应用”，但它更新的是 `public_info_config` 公开信息配置表；该表、mapper、service 和公开信息查询记录当前都在 payment。公开信息依赖 third 的外部公开查询结果，但 payment 已通过 `PaymentPublicInfoSupportPort` 获取快照，脚本短期仍归 payment。后续要治理的是公开信息查询与合同/客户/流程/third 的装配边界，而不是简单按目录名迁移脚本。

## 发现的问题

- `PublicInfoQueryService` 已不再直接注入合同 mapper、客户 mapper 或合同参与方 service；公开信息的合同/客户上下文和合同参与方客户集合已收敛到 `PaymentPublicInfoSupportPort`。
- 付款审批文档渲染 `PaymentApprovalBLRender`、`PaymentApprovalZLRender` 已不再继承 contract 的 `AbstractBasicRender`，也不直接消费合同/客户版本模型；合同、客户、机构、人员等外部输入已通过 `PaymentApprovalRenderSupportPort` 和 `PaymentApprovalRenderSnapshot` 收口，具体适配留在 `application`。
- `PaymentConvert` 已不再承担 `ContractBaseInfoLib -> PaymentContractListRsp` 的外域模型转换；合同库到付款列表响应的映射留在 `application` 的付款编排服务中。
- `PaymentActualDetailOperationAuthChecker`、`PaymentActualDetailRemoveAuthChecker` 已经不直接使用 workflow `ProcessService` 和流程模型枚举，改由 `PaymentWorkflowPort` 判断付款核销/合同自动起租相关流程是否在办。
- `PaymentBaseInfoMapper.xml` 已删除；payment 模块自身 Mapper XML 已不再维护 `payment_base_info` 主表列表类跨域读模型。收付中心、付款首页列表、预算收益测算和合同侧审批通过付款申请等跨域读取都已迁到 application 读侧 mapper。后续还需要把这些 application SQL 继续拆成明确快照输入或 application 组装。

## 后续整理

后续整理重点是拆清付款自身状态机、审批与执行逻辑，并将合同/项目/客户/流程/材料事实读取接口化。

建议执行顺序：

1. 继续拆 `CollectionPaymentFlowCenterMapper.paymentFlowList` 的内部 SQL：payment 只提供付款申请/实付快照，margin 和 warranty 提供保证金/质保金快照，contract 提供部门和项目名称快照，由 application 组装收付中心读模型。
2. 继续细化 `BudgetPaymentFactPort` 的实现边界：当前 payment 已不再读取 collection 和 contract 表，但 application 的 `BudgetPaymentFactMapper.xml` 仍是一个跨域 SQL，可在下一轮拆成 payment/collection/contract 快照输入。
3. 继续细化 `PaymentProcessQueryMapper.listPayment` 的实现边界：当前 payment 已不再读取 contract 表，但 application 仍直接 join `contract_base_info.biz_dept_id`；优先评估付款申请是否应冗余业务部门，或由 application 提供合同部门筛选输入。
4. 继续区分付款保单与正式保单的同步关系，避免 payment 与 policy 互相写内部细节。
5. 当前 `document` 依赖仍来自付款材料和版本处理，短期视为有意保留；后续重点确认材料版本是否应继续直接复用 document 持久化模型，还是改成付款自有材料快照。
