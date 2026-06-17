# zswl-mithras-report

`zswl-mithras-report` 是征信报送处理域模块，负责征信报送 draft/formal/fullsnap/procsnap/base 快照、批次记录、修改快照、报送处理器、流程节点、审批处理和 Excel 导出。

本模块的核心语义是“面向监管口径的征信报送快照生成与报送处理”。它会读取合同、付款、收款、客户、项目、资产分类等事实数据，但不拥有这些交易事实。

当前判断是：保持独立，归 reporting/监管报送域治理，不并入 `creditreport`、`dashboard` 或交易域。`report` 拥有 draft/formal/fullsnap/procsnap/base 快照、批次、修改快照、审批处理、报送处理器和 BPMN 流程资源，已经不是薄读模型目录。

代码层复核显示，模块有 240 个 Java 文件，一级包分布约为 `mapper` 115 个、`service` 52 个、`enums` 25 个、`handler` 22 个、`controller` 11 个。Java import 主要集中在 `report` 自身约 806 处，同时直接读取多个事实源：`contract` 约 129 处、`payment` 约 64 处、`customer` 约 43 处、`collection` 约 37 处、`projectprocess` 约 12 处；还直接使用 `workflow/flow-core/flowable`、`system`、`basedata` 等横向/底座能力。POM 里的业务依赖大多是真实依赖，不能为了清爽直接删除。

资源层也证明它是监管报送快照域：`report/mapper` 主要围绕 `cr_*` 报送表、draft、full snapshot 和流程快照；SQL 中大量保留 `contract_id`、`payment_id`、`client_id`、`payment_apply_code`、`collection_amount` 等外域事实字段，这是报送快照字段，不是这些交易事实的所有权。

征信五级分类快照通过 `api/report/ReportAssetClassifyPort` 输入，由 application 适配 assetclassify；report 模块不直接依赖 assetclassify 的版本库模型。

征信客户报送资格通过 `api/report/ReportCreditClientPort` 输入，由 application 适配 creditreport；report 模块不直接依赖 application 或 creditreport。

征信报送流程中的数据变动通知通过 `api/report/ReportNotificationPort` 输出，由 application 适配 message；report 模块不直接依赖 message。

技术依赖方面，`ReportMysqlConfig` 和 mapper 使用 MyBatis/MyBatis-Spring 类型，但这些由 `mybatis-plus-boot-starter` 传递覆盖；直接 `org.mybatis:mybatis`、`org.mybatis:mybatis-spring` 声明已移除并通过编译验证。Druid、Flow core、Flowable、XXL Job、AspectJ、Guava、JetBrains annotations、Servlet、Spring JDBC 均有源码使用证据；`crypto-open-starter` 未发现源码引用，已从 POM 移除。

后续整理重点是继续把 workflow、system 等横向能力调用收敛到装配层，report 内部保留报送快照、批次、状态和规则；交易域事实读取可逐步收敛为报送输入快照，但不应通过合并交易模块来解决。
