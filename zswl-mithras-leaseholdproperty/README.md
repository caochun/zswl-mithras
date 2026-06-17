# zswl-mithras-leaseholdproperty

`zswl-mithras-leaseholdproperty` 是租赁物域模块，负责租赁物、租赁物台账、评估、车辆登记、发票、OCR、评估机构白名单和租赁物相关流程。

本模块的核心语义是“租赁标的物的识别、评估、登记和台账管理”。合同是租赁物的重要上下文，但租赁物自身应保持独立生命周期。

租赁物域本地维护租赁物创建、变更和评估机构白名单相关流程模型 key，避免为了借用流程枚举而直接依赖 `workflow` 模块；评估机构白名单的流程启动/运行中查询已通过 `AppraisalCompanyWhitelistWorkflowPort` 交给 `application` adapter 适配 flow-core。

## 当前边界

- 租赁物主数据：`LeaseItemInfo`、`LeaseItemListRowData`、租赁物 Excel 导入和台账导出。
- 评估：评估公司查询、租赁物评估关系、天眼查评估公司基础信息。
- 车辆与发票：车辆登记证、车牌变更记录、VAT 发票和发票产品。
- 评估公司白名单：白名单、白名单版本、材料归档和定时提醒。
- 流程入口：租赁物创建/变更、评估公司白名单审批的领域入口；workflow 事件适配主要在 `application`。

## 依赖现状

当前 POM 依赖 `api`、`foundation`、`document`、`basedata`、`third`。评估机构白名单流程已不再让本模块直接依赖 flow-core；租赁物发起/评估需要的合同上下文已通过 `LeaseholdContractContextPort` 由 `application` adapter 适配到合同域。代码层实际 import 也集中在这些模块：

- `document`：白名单材料版本处理和材料文件类型协作。
- `third`：通过天眼查查询/刷新评估公司基础信息。
- `basedata`：读取通用字典，用于租赁物展示和查询。

POM 依赖复核后，`document` 用于白名单材料版本处理，`basedata` 用于通用字典/日期工具，`third` 用于天眼查评估公司信息；`poi`、Hutool、Spring context/web/tx、servlet、validation、MyBatis-Plus、Swagger、Jackson、XXL Job 都有源码使用。直接 `org.mybatis:mybatis` 只为 `@Param` 注解提供显式依赖，已由 MyBatis-Plus 传递依赖覆盖并移除。

代码层 import 粗分显示，除 `dto`、`foundation`、`leaseholdproperty` 自身和 `api` 外，主要外域依赖集中在 `third`、`document`、`basedata`。当前未发现对 `contract`、`workflow` 或 flow-core 的 Java import，说明合同上下文、流程模型枚举和白名单流程技术调用都已从租赁物域收敛出去。

SQL 层也有一处明确跨域读取：`LeaseItemListRowDataMapper.xml` 查询 `contract_lease_item`，用于找出指定合同范围之外仍可用的租赁物行数据。

反向依赖主要来自 `application`、`api`、`web`、`afterlease` 和 `contract`：合同生效、租后检查、付款、材料检查、归档、工作台、KPI、流程结束事件和合同文档生成都需要租赁物事实。这说明 `leaseholdproperty` 更像被应用层编排、并被合同/租后消费的独立业务域，而不是横向底座。

## 模块判断

`leaseholdproperty` 不建议并入 `contract`。合同拥有合同条款和合同生命周期，租赁物拥有标的物、台账、评估、车辆登记、发票、OCR、评估机构白名单等独立生命周期；二者强相关但语义不同。

后续整理重点：

- 合同只通过窄接口获取租赁物摘要、可绑定行数据和合同租赁物关系，避免直接侵入租赁物持久化模型；`ContractLeaseItemService` 已回到合同模块，租赁物模块不再声明合同租赁物服务接口。
- 白名单材料、文件校验、流程启动/查询、流程结束等适配继续放在 `application`，租赁物域只暴露业务方法。
- 如果未来合并，应作为 contract 大域下的物理子模块合并，而不是把租赁物代码混入合同核心包。

## 合并准备判断

`leaseholdproperty` 被放入合并候选池，是因为它和 `contract` 同属 contract 大域，合同是租赁物最重要的业务上下文。但代码级复核后，当前不适合物理并入 `contract`。

- 模块不是薄壳：它拥有租赁物台账、租赁物行数据、评估关系、VAT 发票、车辆登记证、车辆变更、OCR、评估公司白名单、白名单版本、天眼查评估公司基础信息等模型和流程。
- 模块内部 Java import 分布约为 `dto` 175、`leaseholdproperty` 99、`foundation` 98、`api` 55、`third` 6、`document` 3、`basedata` 1；当前源码已无 contract Java import，合同上下文通过租赁物自有 port 由 application adapter 装配。
- 真实 Java 反向依赖主要来自 `application` 约 110 处，`web` 仅少量；`contract` 模块对“lease item”的大量引用主要是合同自己的 `contract_lease_item`、`ContractLeaseItem` 和合同文本渲染，不等同于直接依赖 `leaseholdproperty` 模块内部模型。
- SQL/资源层反向引用显示 `contract`、`report`、`dashboard` 等会读取租赁物/合同租赁物相关表，说明租赁物是读侧事实，但不是合并依据。

因此，当前动作应定义为“保持独立 + 收窄合同侧交互”，不是物理合并。只有在租赁物台账、评估公司白名单、车辆登记、发票、OCR 等生命周期被证明都只是合同内部明细时，才重新评估合并。

当前不满足物理合并条件：它依赖合同，但不是合同核心的薄目录；车辆登记、VAT 发票、OCR、评估公司白名单、第三方评估信息和白名单版本都说明它有独立子域生命周期。

## application/contract 交互标注

后续如果要动代码，建议先按交互类型收窄：

| 场景 | 当前关系 | 判断 |
| --- | --- | --- |
| 合同发起租赁物变更 | `LeaseVersionFacade` 从 `ContractBaseInfoService` 获取合同上下文，校验回租和岗位后创建 `LeaseItemInfo` 并发起租赁物流程 | 属于合同驱动租赁物生命周期入口，可保留在 leaseholdproperty，但合同输入应收窄为合同上下文快照。 |
| 合同选择租赁物 | application 的 `ContractLeaseItemServiceImpl` 直接使用 `LeaseItemInfo`、`LeaseItemListRowData`、`LeaseItemCommonService` | 属于 contract/leaseholdproperty 编排，适合留在 application；后续让 contract 只拿可绑定租赁物摘要和绑定结果。 |
| 合同文本渲染 | 合同文档渲染读取合同自己的 `ContractLeaseItem`，并少量使用 `LeaseItemCommonService` 处理表头 | 文档渲染是 application 装配，租赁物域不应承担合同文本生成。 |
| 付款/材料/归档/租后读取租赁物 | application 中付款、材料检查、归档资料、租后流程和工作台会读取租赁物 service/model/enum | 属跨域用例，继续放在 application；租赁物应提供稳定查询 DTO。 |
| 白名单流程结束和材料检查 | workflow end handler、document file check handler 读取白名单和材料枚举 | 继续留在 application adapter，租赁物域保留状态流转和材料类型语义。 |

下一步优先级不是合并，而是定义租赁物对合同/付款/文档/归档/租后的稳定输出：租赁物摘要、可绑定行数据、材料类型、白名单状态、评估公司摘要，减少外部直接使用租赁物持久化模型。
