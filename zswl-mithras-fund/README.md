# zswl-mithras-fund

`zswl-mithras-fund` 是资金融资域模块，负责资金机构、资金授信、担保信息、间接融资、直接融资、融资计划、融资还本付息、质押资产、融资费用、版本归档和财务系统提交。

本模块的核心语义是“公司资金来源、融资安排和融资偿付”。它描述公司从资金方获得融资、形成融资计划、管理融资关联资产、跟踪融资现金流和还款事实。它和合同、付款、收款、财务、流动性存在业务协作，但应保持融资域事实独立。

## 边界判断

`fund` 应继续作为独立 Maven 模块存在，不建议并入 `finance`、`capital` 或 `liquidity`。

- `fund` 管融资业务事实：资金方、授信、融资、直融、还本付息、融资计划、质押和融资相关现金流。
- `capital` 管银行流水和核销，不应承载融资合同、融资计划和还款生命周期。
- `finance` 管财务经营、利润、税费、账龄和月度管理等财务口径聚合，不应成为所有资金类模块的物理总包。
- `liquidity` 管流动性风险指标和资金缺口分析，应消费融资快照或查询结果，不应拥有融资事实。
- `ftp` 管内部资金转移定价和收益分解，可以消费融资信息，但不应和融资事实源合并。

合并判断：`fund` 属于 finance 大域，但不是当前物理合并对象。它有独立写生命周期、独立 BPMN、独立融资/还本付息/直融表族和较大的代码规模；合并进 `finance` 会把融资事实、财务经营聚合、资金流水核销和流动性分析揉成新的大模块。

## 依赖现状

代码层面，`fund` 的 Java import 只指向 `fund`、`foundation`、`api`、`dto`，没有直接 import `contract`、`payment`、`collection`、`finance`、`capital`、`liquidity`、`workflow`、`third`、`credit`、`document` 等业务模块。POM 也只声明 `api`、`foundation` 和基础框架依赖，说明模块主体边界相对干净。

从 Java import 分布看，模块内引用约为：`fund` 504 处、`foundation` 383 处、`dto` 217 处、`api` 121 处。模块源码约 402 个 Java 文件，规模和生命周期都足以支撑独立边界。

资源层面仍有表级耦合：

- `PropertyMapper.xml` 从 `payment_actual_detail_unconfirmed`、`payment_base_info`、`payment_actual_detail`、`contract_base_info`、`client` 取直融资产信息。
- `FundFinancingBaseInfoMapper.xml` 返回 `dto.capital` 下的资金流水列表 DTO，存在 fund 查询服务复用 capital DTO 的痕迹。
- Java import 层还可见少量 `dto.dashboard`、`dto.liquiditymanage`、`dto.capital` 这类 API DTO 复用。它们不形成 Maven 业务依赖，但会让融资事实源对读侧/资金流水视图的语义边界变模糊，后续应逐步替换成 fund 自有查询 DTO 或由 application 适配。
- 原 `sql/融资-直融.sql` 中修改 `collection_overdue_history` 的客户冗余字段脚本已迁回 `collection`；原 `sql/fund_financing_init.sql` 中修改 `asset_classify_client*` 的计提比例脚本已迁回 `assetclassify`；原先修改 `finance_flow_write_off_detail` 的苍穹删除接口脚本已迁回 `capital`。fund 资源目录不再承载这三类外域表 DDL。

POM 依赖逐项复核后，除直接 `org.mybatis:mybatis` 外，当前没有发现可以安全删除的依赖：

- `mybatis-plus-boot-starter`：mapper/service/model 使用；`@Mapper`、`@Param` 注解由其传递的 MyBatis 依赖覆盖，直接 `org.mybatis:mybatis` 已移除。
- `hutool-all`、`commons-lang3`、`commons-collections4`、`fastjson`：应用服务、转换器、版本和导出逻辑使用。
- `poi`、`javax.servlet-api`：Excel 导入导出和下载接口使用。
- `spring-context`、`spring-beans`、`spring-web`、`spring-tx`、`javax.annotation-api`：Spring 组件、注入、Web Controller 和事务使用。
- `swagger-annotations`、`validation-api`：接口模型和参数校验使用。
- `mapstruct`：模块内 converter 使用。
- `xxl-job-core`：`FundReceiptRepayStateJob`、`FundFinancingJob`、`FundOrganizationJob` 使用。

反向依赖方面，`application` 是主要消费者和编排者；`metric`、`liquidity`、`dashboard`、`finance` 等模块读取融资事实或融资枚举，主要是指标、流动性、看板和财务口径消费。

如果只看 Java import 和 POM，主要反向依赖约为：`application` 984 处、`metric` 65 处、`web` 42 处、`liquidity` 41 处、`dashboard` 5 处、`finance` 4 处。如果把 Mapper/SQL 中的 fund 表名也计入，读侧还包括 `web`、`dashboard`、`ftp`、`liquidity`、`filingmaterials`、`budget`、`rating`、`finance`、`contract`、`projectprocess`、`creditreport` 等。后者说明 fund 是重要事实源，但不代表这些模块都应直接依赖 fund 代码。

模块内已有 `FinancialSystemDataPort`、`FundProcessPrepareMaterialPort`、`FundReceiptRepayStateUpdatePort`、`FundReceiptFlowDetailAmountPort`、`FundOrganization*Port` 等接口，用来表达财务系统推送、流程材料、还本付息状态、流水金额和机构信息等外部协作需求。

## 后续整理

- 保持 `fund` 独立，不作为当前合并对象。
- 继续复核资源层跨域痕迹，避免 fund 资源目录重新承载 collection、assetclassify、capital 等外域 DDL。
- 回看 `application/orchestration/fund`：纯融资单域规则可以逐步回迁到 `fund`，涉及 credit 额度、合同材料、流程、财务系统、流动性和 FTP 的装配继续留在 `application`。
- 对 `liquidity`、`dashboard`、`metric` 读取 fund 持久化模型或 fund 表的场景，后续优先改成稳定查询服务、快照 DTO 或明确 port。
- 对 `FundFinancingBaseInfoMapper.xml` 返回 `dto.capital` 的查询，后续应改为 fund 自有查询 DTO，由 application 或 capital 适配成资金流水视图。
