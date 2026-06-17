# zswl-mithras-filingmaterials

`zswl-mithras-filingmaterials` 是归档资料域模块，负责归档资料目录、资金端/项目端资料台账、资料下载记录和归档策略。

本模块的核心语义是“资料从业务过程进入归档准备阶段的清单、校验和台账”。它不同于 document 的文件能力，也不同于 archives 的档案生命周期管理。

当前模块 POM 直接依赖 document，Java 层主要使用 document 的模板/文件能力，并通过 `FilingMaterialsContractInfoPort` 从 application 获取合同信息；没有直接 import 项目、资金、租后等业务域模块。

依赖必要性复核后，`document`、`poi-tl`、`poi`、`mapstruct`、`xxl-job`、MyBatis-Plus、Spring Web/Context、validation、hutool 都能在源码中找到明确使用点。原 POM 中的 `oss-toolkit` 未发现源码引用，已移除。

需要注意的是，资源层存在明显读模型表级耦合：`FilingMaterialsMapper.xml` 会直接读取 `proj_review_base_info`、`fund_direct_financing_base_info`、`fund_financing_base_info`、`fund_financing_credit_ref`、`contract_base_info`、`act_hi_actinst`、`act_hi_taskinst` 等表，用于项目端/资金端台账和流程时效展示。这些 SQL 说明 filingmaterials 不是纯低耦合模块，而是归档准备域里的跨业务场景读模型。

当前判断是：保持独立，不并入 `archives` 或 `document`。后续整理重点是明确归档资料与档案、文档三者边界：document 负责文件/模板能力，filingmaterials 负责归档准备和资料台账，archives 负责正式档案管理；业务域只提供资料事实和归档要求。若继续收敛，应优先把项目/资金/合同/流程台账读取改成 application 侧组装或归档资料快照，而不是扩大 mapper 直连外域表。

## 合并准备判断

`filingmaterials` 被放入合并候选池，是因为它和 `archives`、`document` 都落在 records 上位语义下，且模块本体 Java 依赖较轻。但代码级复核后，当前不适合并入 `archives` 或 `document`。

- 与 `document` 的关系：`document` 是文件、材料清单、模板和版本能力；`filingmaterials` 是围绕归档准备流程组织资料目录、资料校验、资料下载和台账。合并会让 document 认识具体归档流程。
- 与 `archives` 的关系：`archives` 管正式档案、借阅、下载权限和档案审批；`filingmaterials` 管业务资料进入正式档案前的准备、补充、提交和台账。二者生命周期前后相邻但不重合。
- 模块本体 Java import 主要是 `dto` 45、`filingmaterials` 31、`foundation` 22、`api` 21、`document` 4，说明 POM/Java 层低耦合基本真实。
- 但是 application 侧归档资料实现很重：归档资料相关目录和流程监听/文件 provider 共约 31 个 Java 文件，import 分布约为 `filingmaterials` 86、`application` 66、`dto` 62、`foundation` 58、`workflow` 32、`document` 31、`fund` 15、`system` 14、`payment` 14、`contract` 14、`customer` 11、`projectprocess` 10、`message` 10、`afterlease` 10、`credit` 5，另有少量 `basedata`、`leaseholdproperty`、`third`。
- 这说明真正复杂的是跨域归档资料编排，而不是 filingmaterials 模块本体。把 filingmaterials 并入 archives/document 不能消除这些依赖，只会把跨项目、合同、付款、授信、租后、流程、消息、文档的编排污染 records 模块。

因此当前动作应定义为“保持独立 + application 编排瘦身”，不是物理合并。

## application 拆分标注

后续如果要动代码，建议先按下面的职责拆分 application 侧实现：

| 类/目录 | 当前职责 | 判断 |
| --- | --- | --- |
| `AbstractFilingMaterialsService` | 归档资料通用关闭、文件复制/删除、目录字典、审批历史、审批人回填、模板渲染辅助 | 混合了 filingmaterials 自身规则、document 文件能力、workflow 历史和 system 配置。可拆出纯目录/状态规则回模块，文件和流程继续由 application 适配。 |
| `FilingMaterialsService` | 项目资料归档主流程，生成基础资料、同步项目/合同/付款/授信/租赁物/客户资料、下载和邮件 | 典型跨域编排，直接触达项目、合同、付款、客户、授信、租赁物、workflow、document、message。整体应留在 application。 |
| `FundFilingMaterialsService` | 资金端归档流程、资料检查、流程启动/结束、目录字典 | fund 与 filingmaterials/workflow/document 的编排。整体先留在 application。 |
| `AfterFilingMaterialsService` | 租后资料归档流程、租后检查资料复制、流程启动/结束 | afterlease 与 filingmaterials/workflow/document 的编排。整体先留在 application。 |
| `OtherFilingMaterialsService` | 其他归档流程、项目选择、资料描述维护、导出 | 依赖项目评审、用户权限和流程，先留在 application；资料描述这类单域字段维护可作为回迁候选。 |
| `facade/filingmaterials` | controller application 接口实现、下载、导入、模板下载、查询入口 | 面向 API 的装配层，继续留在 application。 |
| `job/filingmaterials`、`email/filingmaterials` | 归档资料初始化、逾期/跟进邮件提醒 | job/email 属横向能力装配，继续留在 application。 |
| `adapter/filingmaterials` | 合同信息和流程准备 port adapter | 方向正确，继续留在 application。 |

下一步优先级不是合并，而是把 `FilingMaterialsService` 中只操作 `FilingMaterials`、目录配置、资料状态和下载记录的规则识别出来，逐步回迁；凡是同时读取项目、合同、付款、授信、租后、流程、消息或 document 文件能力的逻辑继续留在 application。
