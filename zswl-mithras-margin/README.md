# zswl-mithras-margin

`zswl-mithras-margin` 是保证金域模块，负责保证金基础信息、保证金抵退、核销、通知和与收款场景的联动。

本模块的核心语义是“保证金的收取、占用、释放和核销”。合同、付款、收款是保证金发生和使用的上下文。

当前 POM 和 Java import 层保持低耦合，只直接依赖 `api`、`foundation` 以及框架能力；合同、付款、收款、权限等外部事实主要通过 `Margin*Port` 接入，adapter 放在 `application`。

代码层面复核后，这个低耦合判断基本成立：模块内 Java import 主要集中在 `margin`、`foundation`、`dto`、`api`，没有直接 import 合同、付款、收款、工作流等业务域。POM 中的框架依赖也能在 controller、Excel 导出、MyBatis-Plus model/mapper、XXL job、Spring transaction、servlet/validation 等源码里找到对应使用；其中 `xxl-job-core` 被 `DepositWriteOffJob` 的 `@XxlJob` 直接使用，不是冗余依赖。直接 `org.mybatis:mybatis` 仅为 `@Param` 提供显式来源，已由 MyBatis-Plus starter 传递覆盖并移除。

模块源码约 40 个 Java 文件，包含 application 接口、7 个 port、8 个 port model、controller、转换器、保证金/担保金 mapper/model/service、job 入口和 Excel 导出。虽然规模不大，但它拥有 `margin_base_info`、`margin_record_info`、`margin_write_off_record`、`warranty_base_info`、`warranty_record_info` 等独立写模型。

已有 port 包括 `DepositWriteOffJobPort`、`MarginContractInfoPort`、`MarginCollectionPort`、`MarginPaymentReceiptPort`、`MarginRecordSupportPort`、`MarginViewAuthPort`、`MarginBaseInfoListQueryPort`。这些 port 已经使用 margin 自有输入/输出模型，例如 `MarginContractInfo`、`MarginCollectionRecordInfo`、`MarginRefundPaymentInfo`、`MarginPlannedReceivableCommand`、`WarrantyPlannedReceivableCommand`、`MarginCollectionSnapshot`，没有直接暴露合同/收款/付款持久化模型，方向比较干净。

边界上需要注意两类隐性耦合：

- 原 `MarginBaseInfoMapper.xml` 的 `pageList` 为了列表权限和合同过滤直接关联 `contract_base_info`，本轮已迁出到 application 读侧 `MarginBaseInfoListQueryMapper`，margin 只保留 `MarginBaseInfoListQueryPort` 作为查询需求。
- 原 application 收款事件监听器直接 select/update `margin_base_info`、`warranty_base_info` 来维护保证金/质保金计划应收，本轮已回收到 `MarginBaseInfoService.savePlannedReceivable` 和 `WarrantyBaseInfoService.savePlannedReceivable`。application 仍负责收款事件、收款汇总和付款上下文编排，但不再直接操作这两个 margin 写模型。
- `CollectionRecordInfoService`、`ProfitCalculateSupportPortAdapter` 原本为读取保证金已收金额而拿到 `MarginBaseInfo` 持久化模型，本轮已改为调用 `MarginBaseInfoService.getLatestCollectionAmountByContractId`，减少外部模块对 margin model 的暴露。
- `ContractCollectionMarginPortAdapter` 原本为实现 collection port 直接查询 `MarginBaseInfo` 并使用 MyBatis wrapper，本轮已改为消费 margin 自有 `MarginCollectionSnapshot` 查询方法。application adapter 仍负责把 margin snapshot 转成 collection port 模型，但不再触碰 margin 持久化模型和查询条件。
- `payment`、`dashboard` 等外部模块存在直接读取 `margin_*` / `warranty_*` 表的场景，属于外部读侧对保证金事实的表级依赖。
- `application` 中存在不少直接使用 `MarginBaseInfoService`、`MarginRecordService`、mapper 和 model 的跨域编排，主要集中在 collection、payment、contract、capital、third financial、workflow end handler 和若干 adapter。放在 application 不一定错，但后续要区分跨域编排和单域逻辑外溢。
- 本轮还删除了 `MarginRecordService` 和 `MarginWriteOffRecordService` 中已经没有调用方的历史记录/核销记录辅助代码，减少保证金域内部的陈旧入口。

按 Java/POM/Mapper/SQL 粗略反向搜索，外部引用主要是 `application` 约 97 处、`web` 约 37 处、`payment` 约 32 处、`collection` 约 4 处、`dashboard` 约 2 处。`payment` 的引用主要反映付款/退款读侧需要保证金事实，不代表保证金应并入付款域。

结论：`margin` 是财务大域下语义清楚的保证金子域，暂不建议并入 `finance`、`payment` 或 `collection`。它不是薄读模型，也不是单纯依附于付款/收款的页面能力，而是拥有独立写生命周期和比较干净的端口边界。后续优先治理外部表级读取，把跨域展示收敛到明确读模型或 application 编排层；特别是 application 内直接使用 `MarginBaseInfoMapper`、`MarginBaseInfo`、`MarginRecordInfo` 的地方，可以逐步改成 margin 自有查询/命令接口或面向调用方的 snapshot。
