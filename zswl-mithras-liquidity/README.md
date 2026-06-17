# zswl-mithras-liquidity

`zswl-mithras-liquidity` 是流动性风险和资金指标读侧模块，负责账户余额、资金日报、还款展示、资金调拨、流动性指标、流动性看板、错配分析、现金流入/流出、短期借款和相关导出。

本模块的核心语义是“基于资金与交易事实评估流动性风险”。它会消费合同、收款、融资、授信、账户、工作日历等外部事实，但不应拥有这些外域主数据。

## 当前边界

`liquidity` 目前保持独立 Maven 模块。代码侧已经完成一轮输入模型收敛：授信额度、合同基础信息、合同租金现金流、收款计划、实际回款、还本付息现金流、直融主体、直融实际还款、间融主体、融资机构、间融还款账户、质押监管、基础账户和特殊日期都已转换为 liquidity 自有 snapshot 或本地协议对象。

当前 POM 只保留 `api`、`foundation` 和技术依赖；源码中已无 `cn.zswltech.mithras.basedata`、`cn.zswltech.mithras.fund`、`cn.zswltech.mithras.collection` 直接 import。`FundTransferService` 对基础账户和工作日差的需求已经通过 `FundTransferBaseDataPort` 表达，具体 basedata mapper/service 调用放在 `application.orchestration.adapter.liquidity.FundTransferBaseDataPortAdapter`。对外 API/DTO 仍沿用历史 `liquiditymanage` 契约包名，本轮不做兼容性风险较高的契约重命名。

`LiquidityIndicatorHolder`、`LiquidityIndicatorIndexHolder`、`LiquidityIndicatorBoardHolder`、`LiquidityIndicatorMismatchHolder` 不再持有 fund、basedata、collection 的持久化模型。计算器通过 `LiquidityBankAccountSnapshot`、`LiquiditySpecialDateSnapshot`、`LiquidityWorkdayCalendar`、`LiquidityBankAccountType` 以及各类 liquidity snapshot 消费输入。

本轮复核确认：`liquidity` Java 源码没有直接 import 其他业务域模块包，resources 中也没有外域包名或 fund/contract/collection/payment/system 等跨域表 join 残留。POM 中保留的技术依赖都有源码使用证据：MyBatis-Plus 用于 mapper/model/service，Spring Context/Web 用于组件和 controller，`javax.annotation` 用于注入，`validation-api` 用于接口校验，Hutool 用于集合、反射、Excel 和日期工具，POI 用于导出 workbook，Fastjson 用于参数配置 JSON，Swagger annotations 用于 DTO/BO 字段说明。

本轮追加清理了 `zswl-mithras-web` 测试代码中指向旧 `application.orchestration.liquiditymanage` 包的残留 import，改为当前 `application.orchestration.liquidity` 包；随后执行 `mvn -pl zswl-mithras-liquidity -am -DskipTests compile` 通过，reactor 为 root、api、foundation、liquidity。

但 application 侧仍是明确的跨域事实装载层：`application/orchestration/liquidity` 及其 adapter/facade 约 18 个 Java 文件，直接读取 fund、contract、basedata、collection、projectprocess、payment、workflow、system、capital 等事实，再转换成 liquidity 自有 snapshot 或配置输入。因此当前不能把 application 侧 liquidity 代码整体回迁到本模块，只能继续拆分“纯 liquidity 配置/计算规则”和“跨域事实装载”。

## 已处理的读侧耦合

`FundFinancingAccountSettingMapper.xml` 原先直接 join `fund_financing_base_info`、`fund_financing_plan`、`fund_direct_financing_base_info`、`fund_financing_credit_ref`、`fund_organization` 和 `bifrost_user`，用于融资账户设置列表查询。这不是 Java/POM 依赖，但意味着 liquidity 的部分列表查询理解 fund/system 表结构。

本轮代码级复核确认，这个 `queryList` 只被 `application/orchestration/liquidity/FundFinancingAccountSettingService.accountSettingList` 调用。该 service 本身已经直接依赖 fund、basedata 和 liquidity 的模型/service，负责初始化和维护 `fund_financing_account_setting`，所以它更像 application 层的跨域账户设置编排，而不是 liquidity 计算器内部能力。

本轮已把这条跨域列表查询迁到 `application.orchestration.adapter.liquidity.mapper.AccountSettingListQueryMapper` 和 `mapper/application/AccountSettingListQueryMapper.xml`。`liquidity` 侧 `FundFinancingAccountSettingMapper` 只保留 `fund_financing_account_setting` 自表 CRUD，不再维护 fund/system join SQL。

这条 SQL 的迁移也是当前不适合物理并入 `finance` 的反向证据：问题并不是 liquidity 模块壳过细，而是读侧列表需要跨 fund/system 事实组装。正确方向是继续把这类跨域读模型放在 application，或进一步拆成 liquidity 自有配置表读取 + fund/system 提供融资摘要、机构摘要、资金经理姓名等快照。

另有少量跨模块契约 DTO，例如 `ContractPriceDetailRSP` 来自 `api`，属于稳定契约依赖，不等同于直接依赖 contract 模块。

## 合并判断

`liquidity` 属于 finance 大域里的流动性分析能力，但当前不建议并入 `fund`、`capital` 或 `finance` 主交易模块。

- 它不是空壳，模块内有完整计算器体系和自己的配置/输出表。
- 它拥有 `AccountBalanceBaseInfo`、`FundParameterConfig`、`FundFinancingAccountSetting`、`BaseAmountSetting`、`FinancingDeliverDetailSetting` 等写入或配置模型。
- 它的核心问题不是模块过细，而是读侧输入曾经直接绑定外域模型；这部分已经改为 snapshot/port。
- `application/orchestration/liquidity/LiquidityDataService` 的定位是跨域装载器，负责把外域事实转换成 liquidity 输入。

因此当前动作定义为“保持独立 + 依赖瘦身 + 合并准备观察”，不是物理合并。只有当后续证明 liquidity 只剩 finance 大域内一个无独立生命周期的分析包，且合并不会污染交易域边界时，才重新评估物理合并。

## 后续重点

1. 继续把跨域账户设置列表从 application SQL 读模型演进为显式快照组装：liquidity 提供配置记录，fund/system 提供融资摘要、机构摘要和资金经理姓名快照。
2. 梳理 `LiquidityDataService` 静态 holder 装载方式，长期应收敛为显式计算上下文，避免全局状态扩大。
3. 保持 liquidity 对外域事实“只读输入、快照消费”的规则，不在本模块维护合同、收款、融资、授信或基础账户主数据。
