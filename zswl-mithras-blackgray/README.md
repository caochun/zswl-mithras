# zswl-mithras-blackgray

`zswl-mithras-blackgray` 是黑灰名单域模块，负责黑灰名单库、规则配置、入库任务、失信名单、人工出库和黑灰名单相关审核。

本模块的核心语义是“对象是否命中黑灰名单及其名单生命周期”。它可以服务客户、风控、项目准入等场景，但不应承载这些业务域的完整规则。

当前模块 POM 和 Java import 都保持低耦合，直接依赖只有 api、foundation 和框架 provided 依赖。代码层 import 粗分约为 `blackgray` 336 处、`foundation` 47 处、`api` 36 处、`dto` 12 处，没有直接引用其他业务域包。客户详情、审批流程启动已经分别通过 `blackgray.application.port` 下的 `BlackGrayCustomerPort`、`BlackGrayApprovalProcessPort` 由 application 适配，方向是对的。

POM 依赖复核显示，`tk.mybatis mapper`、MyBatis-Plus、Spring、Redis、PageHelper、EasyExcel、POI、Fastjson、Jackson、validation、Swagger、Servlet、JPA、commons-lang3、commons-collections4、commons-io、commons-codec、Netty 和 Gruul starter 都能在源码中找到明确使用点。源码确实使用 MyBatis core 类型，例如 `BlackMysqlConfig` 的 `Slf4jImpl`、`StdOutImpl`、`Interceptor`、`SqlSessionFactory`、`JdbcType` 和 mapper `@Param`，但这些已由 MyBatis-Plus/tk-mybatis 依赖链提供，本轮移除无独立必要性的直接 `org.mybatis:mybatis` 依赖。模块的低耦合不是因为依赖遗漏，而是因为业务协作基本通过 port/application 或平台运行期表完成。

需要注意的是，模块资源层仍存在隐含平台/读侧表依赖：`BlackGrayBreakBusinessMapper.xml`、`BlackGrayWarehouseRecordMapper.xml`、`BlackGrayWarehouseTaskMapper.xml`、`BlackGrayManualOutboundMapper.xml` 查询 `audit_task`；`BlackGrayLibraryMapper.xml` 查询 `concentration_report` 并 join `bifrost_org`；SQL 初始化脚本也写入 `bifrost_menu`、`bifrost_custom_tree` 等平台菜单表。这些不构成业务域间 Java 依赖，但属于运行期表级耦合，后续如果继续收敛，可以优先把审批任务列表和集中度行业读模型封装成更窄的查询接口。

因此当前判断是：保持独立，不合并进 `riskcontrol`、`credit` 或 `assetclassify`。它可以归入 risk-credit 大域治理，但物理模块上应作为名单/准入能力保留，通过接口向其他域提供名单判断和名单事实。
