# zswl-mithras-finance

`zswl-mithras-finance` 是财务域模块，负责月度管理、印花税、账龄、逾期报送、项目利润分配、财务报表指标和财务系统回写处理。

本模块的核心语义是“财务核算、财务报表、财务回写和财务口径管理”。它需要读取合同、收款、付款、资金、客户等交易事实，因此当前依赖较高。

## 边界判断

`finance` 当前不是所有财务资金模块的物理总包，而是财务经营和核算口径聚合域。它不应吸收 `payment`、`collection`、`fund`、`capital`、`budget`、`margin`、`liquidity`、`ftp` 等模块。

- `payment` 和 `collection` 是付款、收款交易事实源。
- `fund` 是融资资金事实源。
- `capital` 是银行流水和核销域。
- `ftp` 是内部资金转移定价和收益分解域。
- `finance` 消费这些事实，形成账龄、利润、税费、月度管理、逾期报送、金控报表等财务口径。

## 依赖现状

代码层面，`finance` 直接依赖 `contract`、`collection`、`payment`、`fund`、`ftp`、`kpi`、`metric`、`third`、`basedata`、`workflow`、`customer` 等模块，说明它是高耦合聚合域，不适合作为其他交易事实模块的合并目标。

本轮复核统计到 220 个 Java 文件，当前源码根包已统一到 `cn.zswltech.mithras.finance`，内部一层包包括 `monthly`、`projectdistribution`、`view`、`service`、`mapper`、`adapter` 等。import 分布约为 `finance` 308 处、`foundation` 224 处、`dto` 131 处、`api` 66 处、`contract` 30 处、`third` 21 处、`collection` 14 处、`workflow` 13 处、`kpi` 12 处、`metric` 8 处，另有 `payment`、`ftp`、`customer`、`fund`、`basedata`、`assetclassify`。POM 中声明的业务模块依赖基本都有代码级使用，当前没有明显可直接删除的空依赖。

利润测算还使用 `assetclassify` 的风险分类版本数据，`finance/pom.xml` 已显式声明 `zswl-mithras-assetclassify`，避免通过传递依赖隐藏真实业务输入。

包结构层面，模块内曾同时存在 `finance`、`monthly`、`financeprojectdistribution` 三个根包，说明历史上月度管理和财务项目分配作为相对独立能力并入了同一个 Maven 模块。目前实现包已收敛为 `finance.monthly` 和 `finance.projectdistribution`；`api.monthly`、`dto.monthly` 作为外部契约包保持不变。

当前仍有两个语义残留需要单独治理：

- `finance.view` 与 `DashboardFv*` 表达的是资金/财务看板快照，代码仍复用 `dto.dashboard`。这不等同于 `finance -> dashboard` 模块依赖，但会让 finance 和 reporting 口径混在一起。
- 原 `finance/adapter/metric` 中由 finance 实现 metric 侧 port 的接线类已迁到 `application/orchestration/adapter/metric`。但 `JinKongMonthlyReportService` 仍直接读写 metric 因子模型和服务，所以 `finance -> metric` 依赖目前仍是真实依赖，后续应继续把金控月报输出指标的写入协议改成更稳定的 metric port 或快照输入。

本轮复核了 `metric` 中历史遗留的 `YunHuMonthlyReportService`，确认它全仓无外部引用，且行为比 `finance` 现有 `JinKongMonthlyReportService` 少组织编码、合并报表、财务原始余额、辅助核算和系统配置处理，不能作为替换实现。该旧类已删除；`finance -> metric` 的真实问题仍是 finance 同步服务直接操作 metric 因子模型和服务。

资源层主要围绕 `finance_*`、`monthly_*`、利润测算、账龄和逾期报送表，但会保存或读取 `contract_id`、`collection_id`、`client_id` 等外域事实标识；`ProfitCalculateResultMapper.xml`、`FinanceProjectProfitDetailMapper.xml` 等 SQL 直接 join `contract_base_info`、`client`、`corp_commerce_info_lib`、`bifrost_*` 等读模型。本轮已修正 `ProfitCalculateResultMapper.xml` 中利润测算列表的客户关联条件，由 `a.contract_id = c.id` 改为 `b.client_id = c.id`，避免用合同 id 错连客户 id。

反向依赖相对集中，按 Java/POM/Mapper/SQL 粗略搜索，主要消费方包括 `application` 约 145 处、`web` 约 130 处、`api` 约 38 处、`ftp` 约 28 处、`third` 约 20 处、`assetclassify` 约 16 处、`fund` 约 14 处、`budget` 约 11 处。它不像 `payment`、`collection`、`fund` 那样是大量业务模块的交易事实源，更像财务口径的聚合和输出模块。

## 后续整理

- 不把其他财务资金子域并入 `finance`，避免形成大而全财务模块。
- 本轮验证 `mvn -pl zswl-mithras-finance -am -DskipTests compile` 通过，reactor 拉起 18 个模块：root、api、foundation、third、document、basedata、customer、workflow、payment、projectprocess、contract、collection、fund、kpi、assetclassify、metric、ftp、finance。这进一步说明 `finance` 是高耦合财务口径聚合域，不适合作为 payment/collection/fund/capital/liquidity 等事实模块的物理合并容器。
- 已完成内部根包收敛：`financeprojectdistribution` 纳入 `finance.projectdistribution`，`monthly` 实现纳入 `finance.monthly`；后续不再新增与 `finance` 并列的财务实现根包。
- 对利润测算、账龄、逾期报送中直接读取外域 mapper/model 的位置，逐步改成财务口径需要的查询 DTO、快照或 port。现有 `ProfitCalculateSupportPort`、`FinanceAccountAgeSupportPort` 仍暴露 `BillManagement`、`PaymentBaseInfo`、`CollectionBaseInfo`、`FtpInterestBaseInfo` 等外域持久化模型，后续应替换为 finance 自己的输入模型。
- 第三方财务系统、金控报表推送可以继续保留在 finance，但外部接口细节和重试能力应尽量沉到 `third` 或 application adapter。
- `finance.view` / `DashboardFv*` 后续应判断是继续作为 finance 自有财务看板快照，还是迁到 reporting/dashboard 大域；迁移前不能只按类名硬搬，因为它依赖 fund/finance 的资金财务口径。
- 原 `finance/adapter/metric` 两个 metric port adapter 已迁到 application，避免由 finance 直接实现 metric 侧接口；下一步重点转为收敛 `JinKongMonthlyReportService` 对 metric 因子服务和模型的直接写入。
- 收敛 `JinKongMonthlyReportService` 时不要直接搬到 metric 或复用已删除的旧服务；更稳妥的切片是先定义 metric 因子写入快照/port，让 finance 输出报表因子数据，由 metric 负责持久化指标因子和文件状态。
