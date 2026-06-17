# zswl-mithras-basedata

`zswl-mithras-basedata` 是基础数据域模块，负责字典、特殊日期、LPR、汇率、银行账户基础数据和基础数据同步/提醒任务。

本模块的核心语义是“业务运行所需的通用基础数据”。它不是 system 配置，也不是具体交易域数据。

后续整理重点是保持低耦合：基础数据可以被多个业务域读取，但不应反向依赖具体业务域规则。

当前代码层复核显示，本模块 POM 只依赖 `api`、`foundation` 和必要技术依赖；源码 import 主要集中在 `basedata` 自身、`dto`、`foundation`、`api`，没有直接 import 合同、付款、客户、项目、流程、报送等业务域。

现有技术依赖都有源码依据：MyBatis-Plus 用于 mapper/model/service，Hutool 用于日期、集合和 Excel 解析，`oss-toolkit` 用于 LPR 模板预览，XXL Job 用于汇率、特殊日期和 LPR 提醒任务。

模块内已有若干 port 用于外接系统用户、项目校验、同步、消息和待办能力，例如 `BaseDataBankAccountUserPort`、`BaseDataBankAccountProjectPort`、`BaseDataBankAccountSyncPort`、`BaseDataJobMessagePort`、`BaseDataJobTodoPort`、`BaseDataJobUserPort`。这些 port 让 basedata 表达基础数据自己的需求，具体外部能力由 application 或其他装配层适配。

资源归属上，通用字典表和岗位字典值归本模块维护。原先混在 `assetclassify` 初始化脚本里的 `general_dictionary.code` 字段扩展和资金/定价委员会岗位字典已迁回 `src/main/resources/sql/0.0.1/job_dictionary_extension.sql`。
