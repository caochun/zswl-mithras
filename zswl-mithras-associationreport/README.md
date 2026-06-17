# zswl-mithras-associationreport

`zswl-mithras-associationreport` 是金融局/协会报送域模块，负责监管或协会报表报送、报送模板、报送流程、报送任务和报送数据组装。

本模块的核心语义是“面向外部监管/协会口径的数据报送”。它天然会读取客户、合同、付款、收款、资金、指标等多个业务域数据，但报送域只拥有报送生命周期和报送口径，不拥有外部业务事实。

边界上，报送域可以消费外域事实，但不应直接继承外域持久化模型或内部枚举。当前收款/核销事实通过 `AssociationReportCollectionFactPort` 进入本模块，由 application 负责适配 collection；付款客户统计通过 `AssociationReportPaymentFactPort` 进入本模块，由 application 负责适配 payment；主要业务清单所需的合同、客户、付款事实通过 `AssociationReportMainBusinessFactPort` 进入本模块，由 application 负责适配 contract/customer/payment；客户集中度报送所需的有效法人客户、剩余本金和风险敞口通过 `AssociationReportClientSupportPort` 进入本模块，由 application 负责适配 customer 和客户视图服务；借据底表事实通过 `AssociationReportContractReceiptBottomPort` 进入本模块，由 application 负责适配 rating；资产分类结果通过 `AssociationReportAssetClassifyPort` 进入本模块，由 application 负责适配 assetclassify；管苑投放收益率/项目情况读模型通过 `AssociationReportGuanYuanDataPort` 进入本模块，由 application 负责适配 dashboard；指标口径事实通过 `AssociationReportMetricPort` 进入本模块，由 application 负责适配 metric；对外融资事实通过 `AssociationReportExternalFinancingPort` 进入本模块，由 application 负责适配 fund；资金流水相关的 `REPAY`、`PRINCIPAL` 等稳定报送编码保留为本模块本地常量，避免直接依赖 capital 枚举。

当前 POM 已不再直接依赖 customer、contract、payment、collection、rating、assetclassify、dashboard、metric、capital、fund；直接业务依赖只剩 basedata。后续整理重点是避免被核心业务域反向依赖，并继续判断 basedata 字典/基础数据读取是否需要进一步稳定化。
