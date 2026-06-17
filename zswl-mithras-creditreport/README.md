# zswl-mithras-creditreport

`zswl-mithras-creditreport` 是征信域模块，负责征信查询、征信报告解析与入库、查询额度、还款责任、征信摘要、征信文件和异步查询结果任务。

本模块的核心语义是“征信数据的申请、获取、解析、存储和业务使用”。客户、项目、授信、合同、付款是征信使用场景，不是本模块维护的主数据；付款场景通过 `CreditReportPaymentPort` 由 application 适配到 payment，征信申请状态由本模块的 `CreditApplyStatusEnum` 表达。

流程结束事件由 application 的征信流程 handler 适配 workflow，再委托到本模块的业务方法；本模块不直接实现 workflow SPI。

当前 POM 直接依赖 `api`、`foundation`、`third`、`document`。客户、项目、授信、合同、付款和 workflow 等外部事实已经通过 `CreditReportClientSupportPort`、`CreditReportProjectDataPort`、`CreditReportContractPort`、`CreditReportPaymentPort`、`CreditReportMaterialPort` 以及 application adapter 收窄，Java 层不再直接 import customer、projectprocess、credit、contract、payment、workflow 等业务域包。剩余真实外域依赖主要是 third 的征信/外部接口能力和 document 的材料/版本能力。

边界上需要注意：

- `CreditReportBaseInfoService`、`CreditReportApiXJServiceImpl`、`CreditReportMaterialsListLibHandler` 仍直接使用 document 的材料查询、材料版本和材料模型，材料协作还没有完全收窄。
- `CreditReportBaseInfoMapper.xml` 直接读取 `client`、`client_authority`，`CreditReportMapper.xml` 直接读取 `contract_base_info_lib`、`contract_tenantry_lib`、`payment_base_info`，属于 SQL 层读模型耦合。
- 反向依赖主要来自 `report`、`web`、`application` 和 `api`。其中 `report` 消费征信事实做征信报送快照，`application` 负责 workflow 结束事件、付款/材料/客户等 port adapter，`web` 是接口装配和测试入口。
- 模块已存在 `CreditReportPaymentPort`、`CreditReportMaterialPort`、`CreditReportClientSupportPort`，说明付款、材料、客户业务历史等部分协作已经开始收敛为端口。

结论：`creditreport` 是 risk-credit 大域中的外部征信查询/报送子域，暂不建议并入 `credit`。后续整理重点是继续收窄 document 材料/版本协作，并把 Mapper XML 中直接读取客户、合同、付款表的读模型迁到 application 或明确查询 port。

依赖整理记录：模块内 `CreditReportMysqlConfig` 使用 `org.mybatis.spring.annotation.MapperScan`、MyBatis core 的 `Interceptor`、`SqlSessionFactory`、`JdbcType` 等类型，以及 MyBatis-Plus 的 `MybatisSqlSessionFactoryBean`；这些已由 `mybatis-plus-boot-starter` 依赖链覆盖，POM 中不再保留单独的 `mybatis-spring` 或 `org.mybatis:mybatis` 直接声明。
