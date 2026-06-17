# zswl-mithras-assetclassify

`zswl-mithras-assetclassify` 是资产五级分类域模块，负责资产分类发起、复核、评审会/风委会/董事会流程、风险因子模板、分类结果和分类版本。

本模块的核心语义是“对存量资产进行风险分类和分类结果管理”。合同、付款、收款、客户等信息是分类依据，不是本模块维护的主数据。

当前模块直接依赖 `document`。客户关联关系、合同事实、收款核销金额和付款事实已通过 `AssetClassifyClientRelationPort`、`AssetClassifyContractFactPort`、`AssetClassifyCollectionWriteOffPort`、`AssetClassifyPaymentFactPort` 隔离，由 `application` 中的 adapter 装配到 `customer`、`contract`、`collection`、`payment`；流程任务自动处理已通过 `application.port` 下的 `AssetClassifyAutoPassExecutionPort` 隔离，不再直接依赖 workflow 或 flow-core。

## 边界判断

`assetclassify` 应保持独立，不建议并入 `riskcontrol` 或 `finance`。

- `assetclassify` 拥有资产五级分类发起、分类客户、检查内容、复核、会议节点、分类结果和版本归档。
- `riskcontrol` 是风险监控、预警、舆情、策略和风险报告平台，可以消费资产分类结果，但不应拥有资产分类流程。
- `finance` 会在利润测算中读取风险分类版本数据，但这是财务口径输入，不代表资产分类应并入 finance。
- `afterlease` 会读取最近资产分类结果用于租后策略展示，但这是租后检查输入，不代表资产分类应并入租后。

## 依赖现状

Java import 主要集中在 `assetclassify`、`foundation`、`dto`，少量直接引用 `document`。原先直接读取 contract mapper/model 的 `AssetClassifyClientWithdrawalRatioService`、`AssetClassifyClientRiskFactorService` 已改为通过 `AssetClassifyContractFactPort` 获取合同编号、业务类型、租赁类型、剩余租金期数和借据存在性，contract 持久化模型读取留在 `application` adapter。原先唯一直接读取 payment mapper/model 的 `AssetClassifyClientWithdrawalRatioService` 已改为通过 `AssetClassifyPaymentFactPort` 获取借据下付款事实和核销实付金额，payment 持久化模型读取留在 `application` adapter。

本轮复核确认资产分类源码只使用 MyBatis 注解，未直接使用 MyBatis 核心 API；直接 `org.mybatis:mybatis` 无独立必要性，已由 MyBatis-Plus starter 的传递依赖覆盖并移除。

资源层面主要围绕 `asset_classify_*` 表。原先放在 `fund` 资源目录中的 `asset_classify_client*` 计提比例字段脚本已迁回本模块；原先夹在 `sql/asset_classify/init.sql` 中的 `payment_base_info` 备注字段、项目索引、合同索引、基础字典字段、租后检查模板和 FTP 指导表脚本已分别迁回 `payment`、`projectprocess`、`contract`、`basedata`、`afterlease`、`ftp`。当前 `init.sql` 已回到资产分类表结构自身。

反向依赖方面，`application` 是主要编排者；`web` 是接口入口层；`metric`、`riskcontrol`、`finance` 会消费资产分类结果或版本数据；`customer`、`workflow`、`afterlease`、`creditreport` 等存在少量读侧或流程协作。按 Java/POM/Mapper/SQL 粗略搜索，`application` 约 120 处、`web` 约 33 处、`metric` 约 12 处、`riskcontrol` 约 11 处，其余模块引用较少。

模块内已有 `AssetClassifyMarginAmountPort`、`AssetClassifyCollectionWriteOffPort`、`AssetClassifyClientRelationPort`、`AssetClassifyContractFactPort`、`AssetClassifyPaymentFactPort` 等端口，说明客户关联、合同事实、保证金金额、收款核销金额、付款事实这类外域事实已经开始从直接依赖转为输入能力。

自动通过超时监听只表达“到期后尝试自动处理”的资产分类业务意图；流程是否运行中、当前节点任务查询和 flow-core 自动提交调用已收敛到 `AssetClassifyAutoPassExecutionPort` 的 `application` adapter 中，资产分类模块源码和 POM 都不再直接依赖 workflow 或 flow-core。XXL Job 入口依赖的初始化、复核自动通过和工作日提醒能力已收敛为 `AssetClassifyInitJobPort`、`AssetClassifyReviewAutoPassJobPort`、`AssetClassifyWeekdayRemindJobPort`，放在 `assetclassify.application.port` 下。

## 后续整理

- 不合并进 `riskcontrol`，保持资产质量/五级分类事实源定位。
- 继续把材料等外域输入收敛为资产分类输入 DTO 或 port，减少直接 mapper/model 依赖；合同事实、付款输入已完成第一轮 port 化。
- 将通知等剩余流程技术细节逐步迁到 `application` adapter，资产分类模块只保留分类业务状态和规则；自动通过监听器已经通过 port 隔离流程运行状态判断、任务查询和自动提交，并已移除 flow-core POM 依赖。
- 继续检查资源目录中的历史 SQL，避免后续新增外域 DDL；已迁出的外域脚本由对应模块维护。
