# zswl-mithras-kpi

`zswl-mithras-kpi` 是 KPI 与绩效域模块，负责 KPI 参数、绩效管理、项目预测、ECL 业务配置、指标导出和绩效计算。

本模块的核心语义是“经营绩效和指标口径管理”。它可以被预算、看板、报表等模块读取，但不应承载这些模块的展示或报送逻辑。

当前判断是：保持独立，定位为经营绩效、项目分配、项目测算和 ECL/拨备口径事实源，不并入 `dashboard`、`metric`、`budget` 或 `finance`。这些模块可以消费 KPI 事实或参数，但不应拥有 KPI 的写生命周期。

当前 POM 和 Java import 层低耦合，只直接依赖 `api`、`foundation` 以及框架能力。代码层面复核显示，模块内 Java import 主要集中在 `kpi` 约 263 处、`foundation` 约 193 处、`dto` 约 125 处、`api` 约 71 处，没有直接 import 其他业务域。模块内仅保存 BPMN 定义，不直接调用 flow-core；原 POM 中无源码引用的 `cn.zswltech.flow:core` 依赖已移除，流程调用留在 application 装配层。直接 `org.mybatis:mybatis` 已移除，源码中 `@Mapper`、`@Param` 由 MyBatis-Plus starter 的传递依赖满足。本轮 `mvn -pl zswl-mithras-kpi -am -DskipTests compile` 通过，reactor 仅 root、api、foundation、kpi。

本轮复核到 206 个 Java 文件，一级包分布约为 `application` 38 个、`mapper` 27 个、`model` 27 个、`excel` 26 个、`enums` 23 个、`controller` 17 个、`dto` 17 个、`bo` 13 个、`distribution` 10 个、`job` 4 个、`convert` 2 个、`datacompare` 1 个、`constant` 1 个。源码层仍没有其他业务域 import，说明 KPI 的 Maven 边界不是虚假的低耦合。删除历史流程迁移后留下的空目录 `application/process/prepare/handle`、`application/process/prepare`、`application/process`。

本轮也复核了 `kpi/pom.xml` 的技术依赖，未发现适合直接删除的项：Gruul 用于用户、组织和当前账号；POI 与 EasyExcel 用于导入导出；MyBatis-Plus 用于 mapper/model/service；Spring context/web/tx 用于 Bean、Controller 和事务；Servlet 用于导出响应流；validation/swagger 用于接口契约；Hutool/Fastjson 用于工具和配置 JSON；QLExpress 用于 KPI 参数公式计算；XXL Job 用于绩效任务。当前 POM 瘦身重点不在技术依赖，而在资源层跨域 SQL 和外部模块直接消费 KPI 持久化模型。

客户、合同、部门、用户等外部名称和权限事实主要通过 foundation resolver 或模块内 port 获取。比如项目分配记录通过 `KpiProjectDistributionContractPort` 查询有效合同编码到合同 ID 的映射，由 `application/orchestration/adapter/kpi/KpiProjectDistributionContractPortAdapter` 适配合同模块，方向是对的。

边界上需要注意两类耦合：

- `KpiProjectDistributionMapper.xml` 直接关联 `contract_base_info`、`proj_review_base_info`，用于项目分配列表的合同/项目读侧展示。
- `KpiProjGuessBaseInfoMapper.xml` 关联 `contract_base_info` 做项目测算列表和主办人过滤；`sql/绩效管理.sql` 也通过 `contract_base_info` 回填部门字段。这些属于 mapper/历史 SQL 层的合同/项目读模型耦合。
- `PerformanceMainInfoMapper.xml` 读取 `gruul_user_org_job`，属于组织/用户岗位读模型耦合。
- `sql/kpi/v1_ddl.sql` 会修改 `materials_list`、`materials_list_lib` 的文件名字段长度，这是历史脚本层对 document/materials 表的耦合，不应继续扩大。
- ECL 和绩效相关 SQL 会写入 `bifrost_menu`、`bifrost_custom_tree` 等平台菜单表，这是平台初始化耦合，不是 KPI 业务依赖，但需要记录。
- `dashboard`、`finance`、`metric`、`budget`、`application` 等模块存在直接引用 KPI mapper、model、service、enum 的场景，说明 KPI 事实被广泛消费，但对外暴露面偏粗。只看 Java import，反向依赖主要是 `application` 约 214 处、`web` 约 15 处、`dashboard` 约 14 处、`finance` 约 12 处、`metric` 约 10 处、`budget` 约 9 处。

后续优先把外部模块对 KPI 持久化模型和 mapper 的直接依赖收敛为稳定查询服务、快照 DTO 或明确 port。`application/orchestration/kpi` 中既有跨域装配，也有可能外溢的 KPI 单域逻辑，后续可以逐步识别并回迁；但预算、财务、看板、指标模块消费 KPI 参数或结果的关系本身是合理的，不应因此物理合并。
