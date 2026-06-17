# zswl-mithras-contract

`zswl-mithras-contract` 是合同域模块，负责合同创建、起租、变更、展期、提前还款/结清、LPR 调整、还款计划、租赁物、担保、抵质押和合同文本签署。

本模块的核心语义是“租赁交易在合同阶段形成的权利义务、履约计划、合同资产和合同变更”。客户与项目是合同形成的前置事实，付款、收款、租后、报表和风控监控是合同生效后的下游消费场景。

## 当前边界判断

`contract` 应继续作为独立核心交易域存在，不适合作为短期物理合并目标。它不是薄模块，也不是某个下游域的附属能力，而是大量业务域共同消费的合同事实源。

从代码层看，模块内部引用主要集中在 `contract` 自身、`foundation`、`dto` 和 `api`，但仍直接引用 `projectprocess`、`customer`、`workflow`、`document`、`third`、`basedata`。反向引用非常重，主要来自 `application`、`web`、`report`、`metric`、`collection`、`afterlease`、`finance`、`payment`、`dashboard`、`riskcontrol`、`creditreport`、`assetclassify`、`leaseholdproperty`、`budget`。这说明合同是交易链路中枢，不说明它应该被并入 finance、risk、reporting 或其他下游模块。

资源层也能看到合同的事实源特征：Mapper SQL 以 `contract_base_info`、`contract_receipt`、`contract_income_sharing`、`contract_guarantor`、`contract_tenantry` 等合同表为主，同时为了列表、逾期和材料场景读取 `client`、`collection_base_info`、`fund_*`、`bifrost_user` 等外域表。BPMN 中存在风控、财务、法务等审批节点，这些属于流程参与角色，不代表合同域拥有风控或财务规则。

## 后续整理方向

- 保持独立 Maven 模块，作为合同事实源和履约生命周期中心。
- 不把 `payment`、`collection`、`finance`、`leaseholdproperty` 等模块粗暴并入 `contract`。
- 优先做依赖瘦身：流程启动、文档生成、第三方数据和跨域列表查询要逐步通过 application 装配、port 或稳定查询快照隔离。
- 优先整理内部包结构：合同核心、逾期催收、合同文本/材料、租赁物关系、版本、流程适配、任务适配需要更清楚地分层。
- 对外提供稳定的合同查询服务或 snapshot DTO，减少下游模块直接引用合同 mapper/model。

## 本轮整理记录

- `ContractBaseInfoConverter` 不再声明 `ProjPricingBaseInfo -> ContractBaseInfo` 映射，合同模块少认识一个项目定价持久化模型。
- 合同创建时从项目定价生成合同初始信息的映射留在 `application` 合同编排服务中；这是“项目过程事实进入合同创建”的跨域装配，不属于合同域内部通用转换器。
- 合同持久化模型中的项目过程枚举 Javadoc 链接已改为普通“编码”说明，避免 `ContractBaseInfo`、`ContractLeasePrice`、`ContractAccount`、`ContractAocPrice`、`ContractFactoringPrice` 仅因注释 import `projectprocess.enums`。
- `BusinessDataRepository` 的通配符 import 已展开为显式 import，确认它是真实的合同文档渲染数据聚合点：直接读取 customer 版本模型、third 外部数据、basedata 字典和合同表。短期先显式化依赖，后续应优先评估是否迁到 application 装配或改为合同渲染输入 port。
- `ContractBaseInfoMapper.myList` 的分页参数泛型已从 `Page<ProjReviewBaseInfo>` 改为 `Page<ContractBaseInfo>`，删除一个错误的项目评审模型 import；XML resultType 和调用方原本就是合同实体分页。
- `ContractConstitutionFileService` 已不再暴露 `projectprocess.application.model.ContractConstitutionFileBO`，改为 contract 自有 `ContractConstitutionFileCommand`；application 合同编排调用方已同步替换，projectprocess 中旧 BO 已删除。
- 合同交易结构同步已改用 contract 自有 `ContractTradeStructureRoleEnum`，不再借用 `projectprocess.enums.TradeStructureRoleEnum`；持久化编码仍保持 `LESSEE/GUARANTOR/MORTGAGE/PLEDGE/CREDITOR/DEBTOR` 不变。
- 本轮复核确认合同源码仅使用 MyBatis 注解，直接 `org.mybatis:mybatis` 无独立必要性，已由 MyBatis-Plus starter 的传递依赖覆盖并移除。
- `contract -> projectprocess` 仍是当前真实依赖，主要集中在现金流 Excel 模型、还款/利率枚举、项目评审信息等位置，暂不能删除 POM 依赖。
