# zswl-mithras-collection

`zswl-mithras-collection` 是收款域模块，负责收款台账、账单、核销、逾期记录、罚息、对账函、租金催收函和收款相关任务。

本模块的核心语义是“应收、实收、核销、逾期与催收”。合同、付款、客户是收款事实的重要来源，但不应由收款域维护其主数据。

代码层复核显示，`collection` 不是低耦合小模块：Java import 主要集中在 `collection`、`foundation`、`dto`，同时有较多 `contract` 引用和少量 `payment` 引用；源码和 resources 已无 `customer` 模块类型引用，POM 中的 `customer` 历史依赖已移除。`CollectionBaseInfoService` 既管理收款计划和核销状态，也实现合同侧剩余本金、租金实收状态等 resolver；`ContractCollectionPaymentService` 同时读取合同、付款和收款事实，属于合同收付台账视角。

本轮复核统计到约 87 个 Java 文件，POM 中 `contract`、`payment` 依赖目前都有直接代码使用，不是可直接删除的空依赖；但这些依赖暴露的是持久化模型、mapper、枚举和版本处理器，后续仍有瘦身空间。最新 Java import 统计显示，`collection -> contract` 已收敛到约 15 处、3 个文件，`collection -> payment` 约 6 处、2 个文件。

本轮继续复核技术依赖，确认 collection 源码只使用 MyBatis 注解，直接 `org.mybatis:mybatis` 无独立必要性，已由 MyBatis-Plus starter 的传递依赖覆盖并移除。当前 `commons-lang3` 仍由 `ContractCollectionPaymentService` 的 `StringUtils` 使用；`CollectionBaseInfoService` 中一处历史 `org.apache.commons.lang.ObjectUtils` 已改为 JDK `Objects.equals`，避免继续依赖老 commons-lang 传递包。

资源层也有明显事实耦合：`CollectionBaseInfoMapper.xml` 直接 join `contract_base_info`、`contract_tenantry`、`contract_guarantor`、`client`，并读取 `contract_rent_actual` 剩余本金；这是收款台账、逾期展示、合同维度汇总所需的读模型耦合。

反向依赖很广，按 Java/POM/Mapper/SQL 粗略搜索，主要消费方包括 `application` 约 298 处、`web` 约 139 处、`dashboard` 约 94 处、`contract` 约 57 处、`report` 约 56 处、`afterlease` 约 49 处、`finance` 约 48 处，另有 `riskcontrol`、`payment`、`metric`、`fund`、`margin`、`liquidity` 等。这个现象说明 `collection` 是核心事实源，而不是可轻易并入其他模块的附属包。

模块内已有少量 port：`CollectionNotificationPort` 隔离通知发送，`ContractCollectionMarginPort` 隔离保证金输入，`CollectionPenaltyInterestJobSupportPort` 承接罚息任务支撑能力。`CollectionMailJobSupportPort` 已收窄为 `CollectionMailContractInfo`、`CollectionMailClientInfo`、`CollectionMailContactInfo` 等收款自有邮件快照，不再在租金催收/到期提醒 job 中暴露 `ContractBaseInfo`、`Client`、`CorpContactInfo` 等外域持久化模型。`CollectionBaseInfoMsgJobServiceImpl` 的租金到期站内信已改为通过 `CollectionRentDueContractInfoPort` 获取收款自有合同提醒快照；`CollectionBaseInfoService.detail` 已改为通过 `CollectionDetailContractInfoPort` 获取详情页合同摘要。合同版本实体、合同状态翻译和版本处理器查询留在 application 适配层。催收函、征信告知书、履行连带责任保证通知书的渲染接口已收敛为 collection 自有 `CollectionOverdueLetterRenderService` 与 `CollectionLesseeLetterData` / `CollectionGuarantorLetterData`，contract 所需的 `CollectionLetterRenderer` 由 `application/orchestration/adapter/collection/ContractCollectionLetterRendererAdapter` 做 DTO 适配。逾期历史汇总查询也已收敛为 collection 自有 `CollectionOverdueHistoryQueryService` 与 `CollectionOverdueHistorySnapshot`，contract 侧 `CollectionOverdueHistoryResolver` 由 `application/orchestration/adapter/collection/ContractCollectionOverdueHistoryResolverAdapter` 做模型适配，避免 collection 的 mapper/XML 继续返回 contract 的 `OverdueCollection` 持久化模型。原放在 collection 包下、但只被合同押金 facade 使用的 `ContractDeductRentInfoConverter` 已迁到 application 合同编排层，collection 不再为了 contract 押金/退租模型转换器额外引用 contract 模型。`ContractCollectionPaymentService` 仍直接注入合同/付款 mapper 或合同版本处理器，这是后续解耦的主要切入点；其中收付台账页面自有的现金流筛选和记录来源枚举已从 `contract.enums.contractcp` 收回到 collection 枚举包。

结论：保持独立，不并入 `payment` 或 `finance`。后续整理重点是拆清合同/付款/客户事实读取与收款自身计算规则，减少与交易链路其他核心域的直接耦合；外部模块读取收款事实时，应优先通过明确查询服务、port 或快照，而不是直接使用 collection mapper/model 或跨模块 SQL 直读收款表。
