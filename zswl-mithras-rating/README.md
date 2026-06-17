# zswl-mithras-rating

`zswl-mithras-rating` 是客户评级域模块，负责评级客户、评级额度、区域/城市模型指标、评级决策引擎集成和评级流程处理。

本模块的核心语义是“客户或主体的评级结果、额度和评级过程”。客户、项目、风控是评级依据和使用场景。

当前 POM 直接依赖 `api`、`foundation`、`basedata`、`customer`、`workflow`。代码层 import 粗分显示，除 `dto`、`rating` 自身、`foundation` 和 `api` 外，主要外域依赖集中在 `customer`、`workflow` 和 `basedata`。其中 `basedata` 用于地区/地址字典等评级输入事实，`customer` 用于主体信息，`workflow` 用于评级流程状态和流程结束处理。最新静态统计中，`rating -> customer` 已收敛到约 11 个 Java import、1 个文件，剩余集中在 `RatingClientService`。

项目评审上下文不再通过 `projectprocess` mapper/model 直接读取。债项评级需要的项目名称、项目编号、主承租人、评估主体、地区分类、行业分类、资金用途等事实收敛为 `RatingProjectReviewContextPort` 和 `RatingProjectReviewSnapshot`，由 `application/orchestration/adapter/rating/RatingProjectReviewContextPortAdapter` 负责适配 projectprocess。

模块内有两条核心主线：

- 客户评级：`rating_client`、`rating_client_lib`、评级报告、评级快照、客户评级材料和版本。
- 债项/额度评级：`rating_amount`、`rating_amount_lib`、项目评审上下文、评级额度和债项评级报告。

边界上需要注意：

- `RatingClientService` 仍直接读取客户、基础数据和 workflow mapper/model，单类承担了较多客户评级申请和跨域事实装配。
- `RatingAmountService` 的项目评审上下文和债项评级所需客户事实已分别收敛为 `RatingProjectReviewContextPort`、`RatingAmountClientFactPort` 等端口，具体客户 mapper/model 读取由 `application/orchestration/adapter/rating` 适配。
- 模块已存在 `RatingClientSupportPort`、`RatingNotificationPort`、`RatingAreaIndicatorDataPort`、`RatingProjectReviewContextPort`、`RatingAmountClientFactPort`，说明外部事实和通知需求已经开始收敛为端口。
- mapper XML 目前主要围绕评级区域指标表 `rzy_dm_calculate_indicator`，没有明显跨业务域 join；真正耦合主要在 Java service 层。
- 未发现 `rating -> riskcontrol`、`rating -> creditreport` 的直接 Java/POM 依赖；风险相邻模块对评级结果的消费不应倒推评级并入风控或征信。
- 反向依赖主要来自 `web`、`application`、`kpi`、`budget`、`customer`、`projectprocess` 等，说明评级结果被项目、预算/ECL、客户视图和装配层消费。

结论：`rating` 是 risk-credit 大域中的独立评级子域，暂不建议并入 `credit`、`riskcontrol` 或 `creditreport`。后续整理重点是继续把客户详情、流程结束、通知等装配逻辑逐步移到 `application` adapter，评级模块内部保留评级模型、评级调用、评级结果、快照和版本生命周期。

依赖整理记录：模块内 `FactoryMysqlConfig` 使用 `org.mybatis.spring.annotation.MapperScan`、MyBatis core 的 `Interceptor`、`SqlSessionFactory`、`JdbcType` 等类型，以及 MyBatis-Plus 的 `MybatisSqlSessionFactoryBean`；这些已由 MyBatis-Plus/tk-mybatis 依赖链覆盖，POM 中不再保留单独的 `mybatis-spring` 或 `org.mybatis:mybatis` 直接声明。
