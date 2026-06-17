# 模块收敛方案

本文记录当前 Maven 模块是否划分过细、哪些模块适合收敛、哪些模块具备合并准备价值、以及后续真正合并模块时的执行顺序。

结论先行：当前模块划分确实偏细，目标中包含“合并可合并的模块”。但不建议立刻大规模物理合并。更稳妥的路径是先完成业务语义收敛和依赖瘦身，再分批做 Maven 模块合并。否则容易把依赖关系从 POM 层搬到包层，表面模块变少，实际边界更乱。

## 判断标准

一个模块是否值得独立存在，主要看下面几件事：

| 标准 | 适合独立模块 | 更适合合并为包 |
| --- | --- | --- |
| 业务语义 | 有清晰业务对象、生命周期和规则 | 只是另一个业务域的子场景或读模型 |
| 数据所有权 | 拥有自己的主表和写模型 | 只读其他域数据或做组装 |
| 依赖方向 | 主要依赖底座层和少量横向能力 | 依赖大量业务域，或被某个大域强绑定 |
| 发布价值 | 可以单独编译、测试、演进 | 必须跟另一个模块一起改、一起验证 |
| 代码规模 | 足够承载独立边界 | 代码很少，但维护一个 Maven 边界 |
| 团队认知 | 名称能直接表达业务边界 | 名称表达的是技术/页面/历史拆分 |

不能只看直接 POM 依赖。判断耦合度时，还要看实际代码引用、传递依赖、表/流程/资源归属，以及改一个业务时通常要同时修改哪些模块。

## 收敛动作分级

后续每轮推进不只做分析，也要明确模块最后落在哪一种动作上：

| 动作 | 含义 | 进入条件 |
| --- | --- | --- |
| 保持独立 | 模块语义、数据所有权、生命周期清楚 | 业务事实独立，且合并会污染边界或形成大而全模块 |
| 依赖瘦身 | 模块仍独立，但先去掉错误方向和过粗依赖 | 语义独立，但直接 import、POM、Mapper XML 或 application 外溢暴露出边界问题 |
| 合并准备 | 暂不立刻合并，但已经能看出目标归属 | 生命周期可能依附于上位域，且需要先迁移代码、收窄接口或清理包名 |
| 物理合并 | 删除独立 Maven 边界，作为目标模块内的 package 存在 | 没有独立写生命周期，反向依赖少，依赖瘦身已完成，合并后不会制造新的大而全模块 |

因此，当前目标不是“只整理不合并”，而是：先用代码级证据判断模块是否该独立；对不该长期独立的模块做合并准备；条件成熟时再小步物理合并。

## 当前现象

当前后端有 39 个 Maven 子模块。部分模块语义清楚、依赖轻，适合继续独立；也有一批模块更像“大业务域里的子能力”，独立 Maven 边界带来的收益不明显。

典型信号：

- `application` 依赖几乎所有业务域，说明很多跨域适配仍集中在装配层。
- `finance`、`metric`、`associationreport`、`dashboard` 等聚合模块依赖很多业务域，属于天然读侧聚合，但不应被核心交易域反向依赖。其中 dashboard 已去掉对 workflow 模块的 Java/POM 直接依赖，流程读侧由 application 适配。
- `capital`、`margin`、`archives`、`basedata` 等模块代码量较小且 POM 低耦合，是否独立要看业务所有权，而不是只看代码量。
- `payment`、`collection`、`fund`、`finance`、`capital`、`liquidity`、`ftp`、`budget` 之间业务语义相邻，存在收敛成财务资金大域的空间。
- `credit`、`creditreport`、`rating`、`riskcontrol`、`assetclassify`、`blackgray` 之间业务语义相邻，存在收敛成风险授信大域的空间。
- `document`、`filingmaterials`、`archives` 都与材料文件相关，但生命周期不同，是否合并要非常谨慎。

当前按 POM 直接依赖和 Java import 双重校验，低耦合模块大致分为两类：

- 底座/横向能力低耦合：`basedata`、`customer`、`document`、`message`、`system`、`third`、`workflow` 当前没有业务模块直接依赖或源码业务 import，说明它们的底层边界基本成立。
- 业务子域低耦合：`archives`、`blackgray`、`capital`、`fund`、`kpi`、`margin`、`workbench` 当前也没有源码业务 import。这里不能简单等同于“都应合并”：`fund`、`kpi`、`margin` 有明确事实和生命周期，`capital` 则更像“干净模块壳 + application 中存在大量资金流水编排”，后续应先补实边界再判断是否并入 finance 大域。

最新依赖瘦身结果：

- `afterlease` 已去掉对 `contract` 的直接依赖，合同版本事实通过 `AfterLeaseContractVersionPort` 和 application adapter 输入。
- `ftp` 已去掉对 `projectprocess` 的直接依赖，FTP 定价维度收敛到 `ftp.common.enums`，项目过程分类按稳定字符串协议传入 FTP。
- `budget` 已去掉对 `projectprocess`、finance、contract、payment、collection、workflow/flow-core 和 `kpi` 的直接依赖；KPI 年度绩效目标通过 `BudgetKpiFactPort` 输入，ECL 预测配置校验使用 budget 自有配置结构。
- `rating` 已去掉对 `projectprocess` 的直接依赖，债项评级需要的项目评审上下文通过 `RatingProjectReviewContextPort` 和 application adapter 输入；本轮继续收敛债项评级所需客户事实，新增 `RatingAmountClientFactPort`/`RatingAmountClientSnapshot`，`RatingAmountService` 不再直接注入 customer mapper/model。
- `collection` 的催收函渲染能力已收敛为 collection 自有 `CollectionOverdueLetterRenderService` 和 letter data，contract 侧 `CollectionLetterRenderer` 由 application adapter 适配，collection 不再为渲染实现 contract 的接口或使用 contract 的催收函 DTO；逾期历史汇总查询也已收敛为 collection 自有 `CollectionOverdueHistoryQueryService` 和 `CollectionOverdueHistorySnapshot`，contract 侧 `CollectionOverdueHistoryResolver` 由 application adapter 适配。
- `riskcontrol` 已去掉对 `assetclassify` 的 Java/POM 直接依赖；资产分类事实通过 `RiskControlAssetClassifyPort` 和 application adapter 输入。本轮继续把金控关联交易报送的付款核销监听从 riskcontrol 迁到 application，`RiskControlGljyReportService` 不再直接监听 `PaymentWriteOffEvent` 或在该监听路径读取 contract/projectprocess，跨域事件装配由 `RiskControlGljyPaymentWriteOffListener` 承接；随后把 GLJY 关联方客户 ID 解析和指标 `JC049/JC050` 相关客户集合查询收敛到 application adapter，riskcontrol 通过 `RiskControlGljyReportExternalPort` / `RiskControlClientFactPort` 消费稳定事实；本轮继续把 `ZL002/ZL003/ZL004/ZL005`、`MD002/MD003`、`JC47548/JC47558`、`JC030/JC031`、风控行业分类系列指标、`FJC47608`、区域风险限额指标里的客户行业分类事实、策略详情/拦截所需客户风控行业分类事实、关联方名录导入所需生效客户 ID 解析，以及风险集中度客户维度事实收敛到 `RiskControlClientFactPort`，riskcontrol 的 customer Java import 从 74 降到 11；关联方交易列表所需 payment/collection 已核销交易事实改为 `RiskControlRelatedTransactionPort` 输入，riskcontrol 对 payment/collection Java import 分别降到 6/7；指标 `A10000396_ZL044` 所需合同版本授信金额事实改为 `RiskControlContractFactPort` 输入；区域风险限额指标所需项目评审版本与合同版本查询改为 `RiskControlProjectReviewFactPort` 输入，riskcontrol 对 projectprocess/contract Java import 分别降到 2/6。

当前 POM 声明但 Java 层没有对应业务 import 的明显项较少，主要是 `web -> report`；本轮发现 `web` 生产源码直接引用 `system`，但 POM 只通过 `application/report` 间接获得 system，已补为显式 `web -> system` 依赖，使启动装配模块的声明依赖与源码事实一致。此前 `metric -> customer` 属于隐藏源码依赖，已通过 metric 自有 port 和 application adapter 收敛，不再作为 POM 依赖显式化。

直接 MyBatis 依赖已经基本清到只保留必要项。当前全局扫描显示，除 `application` 的 dependencyManagement 版本约束外，只有 `foundation` 和 `customer` 仍直接声明 `org.mybatis:mybatis`；`foundation` 使用 MyBatis 插件、`MappedStatement`、`SqlSource`、`DefaultSqlSession` 等核心 API，`customer` 使用自定义 TypeHandler 和 `MapperBuilderAssistant`，都不是同类可删的历史依赖。其它业务模块里的 MyBatis 注解/配置类型均已由 MyBatis-Plus/tk-mybatis 依赖链覆盖。

### 实际依赖闭包视角

直接 POM 依赖只能说明“当前模块声明了谁”，不能说明“它通过依赖的依赖最终拉进多少业务模块”。本轮用当前 POM 模块图计算传递闭包，按排除 `api`、`foundation` 后的实际业务/横向模块数量分层如下：

| 实际依赖层级 | 模块 | 判断 |
| --- | --- | --- |
| 0 个实际业务/横向模块 | `archives`, `basedata`, `blackgray`, `capital`, `customer`, `document`, `fund`, `kpi`, `liquidity`, `margin`, `message`, `system`, `third`, `workbench`, `workflow` | 这些模块的 Maven 依赖闭包仍只到 `api/foundation`，是后续低风险复核、边界样板或合并候选观察的优先对象。但仍需结合 resources/SQL/application 外溢判断，不能只按 POM 认定干净。 |
| 1 个实际模块 | `afterlease`, `assetclassify`, `associationreport`, `credit`, `filingmaterials`, `ftp`, `payment`, `policy`, `projectprocess` | 多数只拉入 `document` 或 `basedata`，属于相对低耦合业务域；下一步重点是看资源层和 application adapter，而不是优先合并。 |
| 2-3 个实际模块 | `creditreport`, `leaseholdproperty`, `rating` | 有明确横向能力或上游事实输入，仍可独立，但需要持续防止依赖继续外扩。 |
| 6-11 个实际模块 | `contract`, `collection`, `report`, `riskcontrol`, `dashboard` | 已是核心交易或读侧聚合模块，不能按低耦合模块处理；治理重点是拆读模型、收敛 port、避免反向依赖。 |
| 12 个以上实际模块 | `metric`, `finance`, `budget`, `application`, `web` | 典型高耦合/装配/聚合模块。`application/web` 是装配层，依赖多符合定位；`metric/finance/budget` 需要持续把外域事实输入快照化，不能作为物理合并目标。 |

采样 Maven `dependency:tree` 时发现一个工具限制：直接执行 `mvn -pl <module> dependency:tree` 或加 `-am` 时，当前 maven-dependency-plugin 2.8 仍会尝试从本地/远端仓库解析兄弟 SNAPSHOT artifact，遇到未 install 的 `zswl-mithras-*` 模块会失败。该失败不是模块编译失败；实际可执行验证仍以 `mvn -pl <module> -am -DskipTests compile` 和 POM 图闭包为准。

## 全模块覆盖后的阶段性结论

截至当前文档记录，39 个后端 Maven 子模块都已经形成模块级判断。这个阶段可以认为“全局语义画像”已完成，但“模块边界收敛”还没有完成，因为高耦合模块仍有大量跨域事实读取和 application 外溢需要继续治理。

当前阶段的主要结论：

| 类型 | 模块 | 判断 |
| --- | --- | --- |
| 底座/横向能力已基本成立 | `api`, `foundation`, `system`, `basedata`, `document`, `message`, `workflow`, `third` | 保持独立。重点不是合并，而是避免继续下沉业务规则；workflow/message/document/third 之间也应尽量互不依赖，由 application 装配。 |
| 低耦合业务域已基本成立 | `archives`, `blackgray`, `capital`, `customer`, `fund`, `kpi`, `liquidity`, `margin`, `workbench` | Maven 依赖闭包轻，但仍要看资源层和 application 外溢。当前没有适合直接物理合并的模块。 |
| 轻依赖业务模块 | `afterlease`, `assetclassify`, `associationreport`, `credit`, `filingmaterials`, `ftp`, `payment`, `policy`, `projectprocess`, `creditreport`, `leaseholdproperty`, `rating` | 多数依赖 document/basedata/third/customer/workflow 等少量模块。治理重点是继续防止重新直接依赖交易域或读侧聚合。 |
| 核心交易或读侧聚合高耦合模块 | `contract`, `collection`, `report`, `riskcontrol`, `dashboard`, `metric`, `finance`, `budget` | 不适合按“低耦合小模块”处理。治理重点是把跨域 SQL、外域 model/mapper 直接引用和流程/消息/文档装配压回 application 或明确 port。 |
| 装配层 | `application`, `web` | 依赖多符合定位。`application` 应保留跨域 facade、adapter、流程/消息/文档/第三方装配；只操作单域模型和 mapper 的实现应逐步回迁。`web` 只负责启动、运行时配置和迁移资源。 |

因此，当前不应以“立刻减少 Maven 模块数量”为阶段目标。更合理的推进方式是先让模块成为语义干净的域：业务域之间尽量没有直接依赖；跨域读写通过 application、port 或快照输入表达；横向能力不反向适配业务域。等这些前置条件满足后，再选择极少数生命周期确实重合的模块做物理合并试点。

### 当前收敛队列

后续继续推进时，建议按下面队列走，而不是反复整理已经干净的模块：

| 优先级 | 队列 | 目标 |
| --- | --- | --- |
| P0 | `application` 中明显属于单一业务域的实现 | 回迁单域逻辑，保留跨域编排。尤其是 capital、filingmaterials、finance/monthly、liquidity、credit/groupcredit、projectprocess 等目录，要逐段判断是否真跨域；本轮已把 application 中原顶层 `orchestration/monthly` 和 `orchestration/job/monthly` 归入 `orchestration/finance/monthly`、`orchestration/job/finance/monthly`，把历史 `liquiditymanage` 应用编排包收敛为 `orchestration/liquidity`、`orchestration/adapter/liquidity`，并把历史顶层 `groupcredit` 编排/facade/adapter 收敛到 `credit/groupcredit`；本轮继续清掉顶层 `overdue` 和 `email` 伪域，把催收函材料存储归到 `adapter/contract/overdue`，把 collection/customer/filingmaterials 邮件 handler 归到对应业务域 adapter/email；随后把单文件顶层 `aop` 中的合同变更切面归到 `orchestration/contract/aop`，把历史顶层 `liquidityrisk` 和 `facade/liquidityrisk` 收敛为 `liquidity/risk`、`facade/liquidity/risk`；继续把单文件顶层 `consumer` 中的 EP 舆情消息消费归到 `adapter/third/ep/consumer`；随后把顶层 `facade/projestablish`、`facade/projpricing`、`facade/projreview` 收敛为 `facade/projectprocess/projestablish`、`facade/projectprocess/projpricing`、`facade/projectprocess/projreview`，并把顶层 `adapter/projlifecycle` 收敛为 `adapter/projectprocess/projlifecycle`，先修正语义归属，暂不迁入对应业务模块。 |
| P1 | 高耦合读侧 SQL | 把 payment、policy、liquidity、margin 已经迁到 application 的读侧 SQL 继续拆成 owner domain 快照输入，避免 application 变成新的 SQL 聚合垃圾桶。 |
| P1 | `contract`、`collection`、`finance`、`metric` | 这些模块直接或传递依赖重，优先清理错误方向依赖、外域持久化模型引用和流程/看板口径泄漏。budget 已完成对 payment、contract、collection、workflow/flow-core、finance、projectprocess、KPI 年度绩效目标和 ECL 配置结构的第一轮直接依赖隔离，当前进入低耦合样板观察。 |
| P2 | `workflow`/`message`/`document` 业务语义污染 | 流程模型 key、消息类型、文件模板和材料枚举里有大量业务名称。短期可接受，长期应由业务域/application 注册或配置化。 |
| P2 | records 大域 | `document`、`filingmaterials`、`archives` 暂不合并；继续明确“文件能力、归档准备、正式档案”的边界。 |
| P3 | 物理合并试点 | 暂无成熟对象。只有当某模块没有独立写生命周期、反向引用少、跨域依赖已瘦身、合并后不会制造大而全模块时，才进入试点。 |

### 何时认为这一轮目标阶段性完成

这一轮目标的“大成”不是把所有模块物理合并完，而是满足下面条件：

1. 全部模块都有业务语义、实际依赖、资源耦合和是否合并的明确结论。
2. 底座层、横向能力层、业务域、读侧聚合层、application/web 装配层的依赖方向清楚。
3. 每个大域都有下一步治理队列，而不是只停留在“模块过细”的泛泛判断。
4. 已完成一批低风险清理，并通过对应模块的批量编译验证。
5. 剩余工作能按队列继续推进，不需要重新做全局架构摸底。

按这个标准，当前已经完成了“全局摸底和模块级结论”，但还没有完成“边界收敛”。下一步应转入队列化治理：优先处理 application 外溢和高耦合模块的错误方向依赖。

## 目标业务域视图

建议先把现有模块映射到更少的目标业务域，而不是马上改 Maven 结构。

| 目标域 | 当前模块 | 判断 |
| --- | --- | --- |
| platform | `api`, `foundation`, `system`, `basedata` | 底座层，继续独立，但要保持低耦合。 |
| horizontal-capability | `workflow`, `message`, `document`, `third` | 横向能力层，应该尽量互不依赖。 |
| customer | `customer` | 客户主数据域，保持独立。 |
| project | `projectprocess` | 项目过程域，保持独立。 |
| contract | `contract`, `leaseholdproperty`, `policy` | 合同、租赁物和保单在业务上相邻，但租赁物和保单都有独立生命周期，先语义归组，不急合并。 |
| finance | `payment`, `collection`, `fund`, `capital`, `finance`, `ftp`, `liquidity`, `margin`, `budget` | 最明显的大域候选，建议先按子域治理，再评估物理合并。 |
| risk-credit | `credit`, `creditreport`, `rating`, `riskcontrol`, `assetclassify`, `blackgray` | 风险授信大域候选。`blackgray` 当前低耦合，可先保持独立能力。 |
| afterlease | `afterlease` | 租后域，读取合同、收款、风控事实，暂时保持独立。 |
| records | `document`, `filingmaterials`, `archives` | 文档、归档资料、档案的上位语义相近，但职责不同。 |
| reporting | `report`, `associationreport`, `dashboard`, `metric`, `kpi`, `workbench` | 读侧聚合和报送展示域。应避免核心业务反向依赖。 |
| assembly | `application`, `web` | 装配启动层。`application` 只放跨域编排和 adapter。 |

## 收敛候选

### 第一批：优先做语义收敛，不急物理合并

这些模块之间语义相邻，但直接物理合并风险较大。先统一包结构、命名和依赖方向。

| 大域 | 候选模块 | 当前建议 |
| --- | --- | --- |
| finance | `payment`, `collection`, `fund`, `capital`, `finance`, `ftp`, `liquidity`, `margin`, `budget` | 先建立 finance 大域地图。短期保留模块，消除双向/链式依赖；`fund` 是融资事实源，`capital` 是资金流水/核销边界，二者相邻但不能互相吞并。 |
| risk-credit | `credit`, `creditreport`, `rating`, `riskcontrol`, `assetclassify`, `blackgray` | 先梳理风险授信主链路，保持低耦合模块独立。 |
| reporting | `report`, `associationreport`, `dashboard`, `metric`, `kpi`, `workbench` | 先明确读模型、指标口径、页面聚合、外部报送的边界。 |
| records | `document`, `filingmaterials`, `archives` | 先明确文件能力、归档准备、档案生命周期。 |

### 合并候选池：先准备，证据成熟后再动 Maven

这些不是“马上合并”的清单，而是后续优先验证是否可以合并或降级为 package 的候选。进入物理合并前，必须先证明它没有独立生命周期，且合并不会把目标模块变成新的大而全。

| 候选 | 可能归属 | 当前判断 |
| --- | --- | --- |
| `capital` | finance 大域内的资金流水/核销能力 | 当前模块壳很干净，但核心实现仍在 application，先补实边界和区分单域/跨域逻辑；不急并入 `finance`。 |
| `liquidity` | finance 大域内的流动性分析能力 | 有独立计算器、配置表和输出，不是薄模块；当前先做输入模型瘦身，把 fund/basedata 输入改成 liquidity snapshot，再判断是否继续独立。 |
| `filingmaterials` | records 大域内的归档准备能力 | 与 `archives` 上位语义相近，但生命周期不同；先清楚“归档前准备”和“正式档案管理”的边界。 |
| `leaseholdproperty` | contract 大域内的租赁物/评估子域 | 合同强相关，但拥有租赁物台账和评估白名单生命周期；先收窄合同侧调用。 |
| `policy` | contract/finance 支撑下的保单管理 | 付款保单和正式保单关系紧密，但正式保单有独立台账、续保和到期提醒；先梳理同步关系。 |
| `workbench` | reporting 大域内的个人工作台/入口能力 | 代码低耦合且有公告、卡片配置生命周期；只有在确认只是展示壳时才考虑并入更大的 reporting 装配。 |

### 物理合并成熟度矩阵

语义上归入同一个大域，不等于立刻删除 Maven 模块。当前更合适的判断方式是把候选分成三类：可观察、合并准备、可试点。按已经完成的 POM、源码 import、resources、反向引用和编译证据看，目前还没有模块进入“可试点物理合并”。

| 候选模块 | 目标大域 | 当前成熟度 | 主要证据 | 下一步动作 |
| --- | --- | --- | --- | --- |
| `capital` | finance | 合并准备，但不物理合并 | 模块本体 Java/POM/resources 低耦合，复杂度主要仍在 `application/orchestration/capital`，且当前仍围绕 third 流水持久化模型做跨域核销编排。 | 继续补实 capital 自有流水/核销事实模型，把纯资金流水规则从 application 回迁；等 application 只剩跨域装配后再评估是否并入 finance。 |
| `liquidity` | finance | 可观察，不合并 | 已有独立指标计算器、配置和输出模型；外域输入已快照化，说明它更像资金/风险读侧分析域，而不是 finance 的薄目录。 | 继续把 application 中的 liquidity 跨域 SQL 拆成 fund/system/contract/payment 等快照输入，保持 liquidity 只做指标计算。 |
| `filingmaterials` | records | 可观察，不合并 | 归档准备、目录配置、材料状态和下载记录有独立生命周期；复杂依赖集中在 application 的跨项目/合同/付款/授信/租后编排。 | 区分单域归档资料规则与跨域资料生成编排，先回迁单域规则，不并入 archives/document。 |
| `leaseholdproperty` | contract | 可观察，不合并 | 虽与合同强相关，但包含租赁物台账、评估公司白名单、车辆登记、VAT 发票、OCR 和版本等子生命周期。 | 收窄 contract/application 对租赁物持久化模型的直接读取；只有证明这些都只是合同内部明细时才重新评估合并。 |
| `policy` | contract/finance | 可观察，不合并 | 正式保单、暂存保单、续保提醒、到期提醒、台账和材料版本有独立生命周期；资源层仍有付款/合同/项目表级读模型。 | 继续把 `PolicyInfoMapper.xml` 中剩余跨域列表/提醒 SQL 迁到 application 读侧或通过快照输入。 |
| `workbench` | reporting | 低优先观察，不合并 | 代码低耦合，但拥有公告、快捷入口、用户卡片关系、指标缓存和初始化 job；不是纯展示壳。 | 保持独立，防止核心业务域反向依赖 workbench；只有退化成纯 UI 入口时才考虑并入 reporting 装配。 |

当前真正的“合并准备”只有 `capital` 最接近，但它的问题不是 Maven 边界太多，而是业务事实还没有完全从 application 和 third 流水模型中剥离出来。其它候选更适合先做大域治理、命名统一和依赖瘦身。

#### capital 下一步切片画像

本轮继续复核 `application/orchestration/capital`，确认这里不是一整块可以搬回 `capital` 的单域实现，而是三类代码混在一起：

| 切片 | 当前性质 | 判断 |
| --- | --- | --- |
| `FinanceFlowRecordService` | 财资流水同步、去重、流水状态和反核销状态计算 | 最接近 capital 自有事实，但当前继承 third 的 `FinanceFlowRecordMapper/FinanceFlowRecord`，还调用财资平台接口、临时流水、宝融同步、基础账户和用户姓名。下一步应先抽 capital 自有流水快照/服务边界，不能直接迁 Maven 包。 |
| `FinanceFlowAutoWriteOffService` | 自动核销总编排 | 同时触达 third、fund、payment、collection、margin、contract、customer、system，属于典型 application 跨域编排，暂不回迁。已清理该类中误导性的 `liquibase.pro.packaged.B/S` 无效 import，避免形成假依赖信号。 |
| `BankFlowProcessingCenterService` | 项目端银行流水处理中心 | 直接读取合同、付款、收款、保证金、资金、客户和风控事实，是跨域列表/操作编排，暂不回迁。 |
| `BankFlowProcessingCenterFinanceService` | 资金端银行流水处理中心 | 主要围绕 fund 还本付息、直融/间融、费用、保证金等事实做展示与核销准备，属于 fund + capital 的跨域读侧编排，暂不回迁。 |
| `BusinessFlowService` | 业务流水/融资还款流水生成与查询 | fund 依赖最重，夹杂 third 流水和少量合同/收款/付款事实，当前更适合留在 application，后续可拆成 fund 事实快照 + capital 核销指令。 |
| `write_off/*` | 手工/自动核销策略和匹配模型 | 其中匹配算法可逐步沉淀为 capital 规则；但现有实现直接使用 third 的 tab/match/record 持久化模型，并读取合同、收款、资金监管信息，短期只做小规则提取，不整体迁移。 |

因此，`capital` 的下一步不是物理合并，也不是把 application/capital 整包搬回模块，而是沿着“third 财资流水持久化模型 -> capital 自有流水/核销事实模型 -> application 只传外部事实快照”的顺序继续补实边界。

验证：清理 `FinanceFlowAutoWriteOffService` 无效 Liquibase import 后，`mvn -pl zswl-mithras-application -am -DskipTests compile` 通过，38 个 reactor 模块全部 SUCCESS。随后继续清理 `MigrateFileFacade`、`PurposeOfFundsRender` 中同类误导性的 `liquibase.pro.packaged.S` import，并用 `rg -n 'liquibase\.pro\.packaged' zswl-mithras-*/src/main/java` 确认源码已无 Liquibase 内部包误导入；再次执行 `mvn -pl zswl-mithras-application -am -DskipTests compile` 通过，38 个 reactor 模块全部 SUCCESS。

### 第二批：优先做依赖瘦身和逻辑回迁

逐模块分析后，目前没有发现适合马上物理合并的低风险后端业务模块。更值得先做的是把错误依赖方向和 application 中的单域逻辑清掉。

| 候选 | 收敛方向 | 前置条件 |
| --- | --- | --- |
| `report` | 已去掉 `report -> application/creditreport/message` 依赖 | 后续继续收敛 workflow/system 等横向能力调用。 |
| `rating` | 已去掉 `rating -> riskcontrol/projectprocess` 直接依赖，并收窄债项评级对 customer 的直接读取 | 风险经理/行业分类已通过 foundation 枚举和 resolver 表达；项目评审上下文通过 `RatingProjectReviewContextPort` 由 application 适配；债项评级客户事实通过 `RatingAmountClientFactPort` 由 application 适配。 |
| `ftp` | 已去掉 `ftp -> projectprocess` 直接依赖 | FTP 自持定价维度枚举，外部项目分类按稳定字符串协议传入。 |
| `finance` | 已去掉 `finance -> dashboard` 读侧反向依赖 | 绩效接口和资金看板查询对象迁入 api，finance 自持资金看板卡片枚举。 |
| `payment` | 已完成 Java/POM 层大幅瘦身；继续收敛 application 读侧跨域 SQL | 付款域源码当前只直接依赖 document 材料版本处理；原 `paymentFlowList` 收付中心/现金流聚合已迁到 application 读侧 mapper；原 `PaymentActualDetailMapper.listContractPayInfo*` 已改为 budget 自有 `BudgetPaymentFactPort`，由 application 读侧 mapper 承接跨域 SQL；原 `PaymentBaseInfoMapper.queryListWithContractId` 已迁到 application 的 `PaymentProcessQueryMapper`，由 application 承接付款事实 + 流程历史表读模型；原 `PaymentBaseInfoMapper.myList` 已迁到 `PaymentProcessQueryMapper.listPayment`，由 application 承接付款列表 + 合同部门读模型。`PaymentBaseInfoMapper.xml` 已删除，payment 自身 Mapper XML 已不再维护 `payment_base_info` 主表列表类跨域读模型。 |
| `margin` | 已完成 Java/POM 层复核，并收敛列表 SQL、部分写模型外溢和 collection 侧保证金查询模型 | `margin` 直接依赖仍保持在 api/foundation/framework 层；原 `MarginBaseInfoMapper.pageList` 直接 join `contract_base_info` 并复用合同/项目数据权限 SQL，本轮改为 margin 自有 `MarginBaseInfoListQueryPort`，由 application 的 `MarginBaseInfoListQueryMapper` 承接保证金 + 合同数据权限读模型。收款事件中原来直接维护 `margin_base_info` / `warranty_base_info` 的写模型逻辑已回收到 `MarginBaseInfoService.savePlannedReceivable` 和 `WarrantyBaseInfoService.savePlannedReceivable`，application 只传入计划应收 command。部分只需要保证金已收金额的外部调用已改为 `getLatestCollectionAmountByContractId`；`ContractCollectionMarginPortAdapter` 也已改为消费 margin 自有 `MarginCollectionSnapshot`，不再直接查询 `MarginBaseInfo` 或构造 MyBatis wrapper。下一步继续治理 application 中直接使用 margin mapper/model 的跨域读取。 |
| `capital` | 从 application 回迁单域资金流水逻辑 | 先区分单域规则与合同/收款/付款/保证金跨域编排。 |
| `collection` | 已收敛催收函渲染和逾期历史汇总查询边界；继续收窄 contract/payment 直接读取 | 下一步优先看 `CollectionBaseInfoService`、`CollectionOverdueRecordInfoService` 与 `ContractCollectionPaymentService`，避免 collection 直接暴露 contract/payment 持久化模型或 mapper。 |
| `riskcontrol` | 已完成 assetclassify 依赖隔离，并把金控关联交易报送的 payment 事件监听迁到 application | 下一步继续收敛剩余 customer、projectprocess、contract、payment、collection 事实读取；优先选择只读汇总事实或跨域事件装配切片，避免一次性改动剩余本金/JZD 报告等复杂计算。 |
| `liquidity` | 隔离外域输入模型 | 把合同、收款、融资、授信等外域 model 转成 liquidity snapshot。 |
| `application` | 回迁单域实现，保留跨域装配 | 每次只回迁一个明确业务域，批量编译验证。本轮确认月结/日成本实现主要使用 finance.monthly 模型与服务，但同时编排 fund、contract、customer、third financialshare、capital、kpi 等上下文，因此仍留在 application；包名从顶层 monthly 收敛到 finance/monthly，避免把 monthly 表达成独立顶层业务域。 |

### 第三批：暂不建议合并

这些模块虽然相关，但边界价值仍然明显。

| 模块 | 不急合并原因 |
| --- | --- |
| `customer` | 客户主数据是多个域的事实来源，独立价值高。 |
| `projectprocess` | 项目过程是核心交易主链路，独立价值高。 |
| `contract` | 合同生命周期复杂，是交易中枢，不应轻易并入其他域。 |
| `workflow` | 横向流程能力，应独立且不理解业务域。 |
| `message` | 横向消息能力，应独立且不适配具体业务域。 |
| `document` | 文件/模板/OCR 是横向能力，应独立。 |
| `third` | 第三方集成是防腐层，应独立。 |
| `basedata` | 基础业务数据低耦合，独立可减少业务域污染。 |
| `blackgray` | 当前 POM 低耦合，可作为名单能力独立供风控/准入使用。 |
| `kpi` | 有经营绩效/ECL/项目分配写模型，不只是报表展示。 |
| `workbench` | 有公告、卡片配置、用户工作台关系等轻量写生命周期。 |
| `margin` | 保证金/担保金生命周期清楚，且当前边界较干净。 |
| `archives` 与 `filingmaterials` | 同属 records，但归档前准备和归档后管理生命周期不同。 |

## 推荐执行顺序

### 阶段 1：建立目标边界，不动 Maven

1. 在 README 和 docs 中明确目标大域。
2. 标注每个模块未来归属的大域。
3. 找出跨大域依赖，例如 finance 直接依赖 risk 或 reporting 反向依赖交易域。
4. 对每个大域列出“域内子能力”和“外部事实读取点”。

验收标准：每个模块都能回答三个问题：我拥有哪个业务事实；我读取哪些外部事实；谁应该通过接口调用我。

### 阶段 2：先瘦身依赖，再考虑合并

1. 清理无效 import 和不必要 POM 依赖。
2. 把“只为读取外域数据”的直接依赖改为 port 或 application adapter。
3. 把业务域之间的通知、流程、文档调用尽量收敛到 application 编排。
4. 横向能力模块之间避免相互依赖。

验收标准：业务域模块尽量只依赖 `api`、`foundation`、`system/basedata` 和必要横向能力；复杂跨域协作由 `application` 负责。

### 阶段 3：小步物理合并

只有当依赖瘦身完成、生命周期确实重合、且合并不会把一个模块变成新的大而全时，才进入物理合并。

目前不建议马上启动物理合并。后续如果要试点，优先从“已经去掉反向依赖、没有独立写生命周期”的小模块开始，且每次只合并一个明确候选。

真正合并时要做：

1. 移动源码和 resources。
2. 调整 package 和 mapper XML namespace。
3. 更新 POM 依赖和父 POM modules。
4. 全局替换 import。
5. 批量编译相关链路。
6. 启动验证。

### 阶段 4：收敛 application

物理合并或依赖瘦身完成后，`application` 应只剩：

- 跨域 facade。
- port adapter。
- workflow/message/document/third 的装配逻辑。
- 流程结束后的跨域事件处理。
- 不能归入单一业务域的业务用例编排。

如果某段代码只操作一个业务域的模型、mapper、service，就应该迁回对应业务域。

## 试点分析记录

### api

结论：`api` 是对外契约和跨模块 API 壳，必须保持无业务实现、无领域依赖。

依据：

- `zswl-mithras-api` 没有依赖其他业务模块，是最底层契约模块。
- 多数业务模块通过 `api` 暴露 controller/application 接口或 DTO 契约，说明它承担编译期契约共享角色。
- 当前源码分布约为 2600 个 `dto` 文件、490 个 `api` 文件，另有 3 个校验相关文件和 1 个注解文件；没有 resources 资源文件。
- 源码 import 只涉及 `api/dto/validation/annotation` 内部包和 Swagger、Validation、Spring Web、Jackson、Hutool、MyBatis-Plus 等契约表达依赖，没有直接 import 任何业务模块。

发现的问题：

- 如果把业务枚举、业务规则、持久化模型持续塞进 `api`，它会变成新的业务垃圾桶。
- `api` 中已有不少业务 DTO/API，后续需要区分“跨模块稳定契约”和“单模块内部 DTO”。
- 少量 DTO 使用 `com.baomidou.mybatisplus.annotation.TableField`、`TableId` 和 `Page`，说明契约层仍混入持久化/分页实现味道，后续应逐步清理。
- 个别 DTO 曾包含日志副作用，已在本轮清掉 `DashboardFundFinanceBaseREQ` 的 `@Slf4j` 和异常日志输出。

处理建议：

- 保持独立模块。
- 只放稳定 API、DTO、少量通用注解和跨模块契约。
- 单一业务域内部 DTO 不要继续上移到 `api`。
- `api` 不承担日志、持久化、流程编排和业务规则；跨域临时结构优先放 application 或具体业务域，不要默认上移到 `api`。

### foundation

结论：`foundation` 是技术底座和跨域基础能力，不应承载具体业务语义。

依据：

- `zswl-mithras-foundation` 只依赖 `api`，承担公共异常、基础枚举、版本、异步任务、数据比较、缓存、通用 mapper/model、基础 port 等能力。
- 前面已完成一轮 package 整理，当前包名已经比最初更接近 foundation 边界。
- 本轮代码级复核确认 foundation 源码没有 import 具体业务域模块；POM 中 MyBatis、Spring、Servlet、AspectJ、Redisson、POI/EasyExcel、HTTPClient、FreeMarker、diff-match-patch、Guava、MapStruct 等依赖都有源码使用证据。
- 资源层只有 `AsyncTaskRecordMapper.xml`、`CommonMapper.xml` 和基础 SQL，但 `CommonMapper.xml` 的权限 SQL 已经暴露项目字段口径。

发现的问题：

- `Constant` 中包含航运评级模型、租赁物查重字段、五级分类等具体业务规则，语义上更接近 rating、leaseholdproperty 或风险/资产分类相关域。
- `FileExportEnum` 主要是 dashboard/workbench/finance 导出项，不是技术底座枚举。
- `CacheEnum` 包含合同起租、立项、集团授信、租金催收、流动性、KPI 等业务锁 key。
- `JobEnum` 是岗位编码共享词表，使用面很广，更接近 system 主数据契约；短期留在 foundation 是兼容选择，长期应评估收敛到 system 契约或配置。
- `RiskControlIndustryClassify` 是风控行业分类，被 riskcontrol、afterlease、contract/application 等多处共享；短期不能直接迁移，长期应明确为风险口径词表或客户/行业分类契约。
- `CommonMapper.xml` 的权限 SQL 直接使用 `proj_sponsor_user_id`、`proj_cosponsor_user_ids` 等项目字段，属于项目口径权限片段。
- `ClientNameResolver`、`ClientInfoResolver`、`ContractInfoResolver` 等 port 承载客户/合同事实解析，短期作为跨域兼容抽象存在，后续应由 owning domain 自有 port 或 application adapter 收窄。

处理建议：

- 保持独立模块。
- 继续执行“无具体业务流程、无业务主表所有权、无业务规则”的边界。
- 新增公共能力前先判断是否属于 `system`、`basedata` 或具体业务域。
- 不做一次性大搬迁；按引用面从小到大迁出业务语义：先迁单域常量/导出项，再治理业务锁 key，再治理岗位和风控行业分类这类大范围共享词表。

### system

结论：`system` 是组织、用户、权限、系统配置等系统主数据域，不是通用工具包。

依据：

- `zswl-mithras-system` 只依赖 `api`、`foundation`。
- 前面已把 system 内部包结构简化，保留系统配置、组织用户、权限类能力。
- 大量业务模块需要用户/部门/岗位/系统配置解析，但这应通过 system 或 foundation resolver 提供稳定接口。
- 本轮代码级复核确认 `system` 源码内部 import 只涉及 `api`、`foundation` 和 `system`，没有直接 import 具体业务域模块；resources 主要是 Gruul/系统表、系统功能组和项目编号存储 SQL。
- POM 技术依赖均有源码使用证据：Gruul、MyBatis-Plus、Hutool、Fastjson、Jackson、Servlet/Spring Web、Redis、Druid/JDBC、AspectJ、XXL Job、QLExpress、OSS toolkit 等都对应系统运行能力。

发现的问题：

- system 很容易被误用成“任何全局配置/全局服务”的收纳处。
- 与 foundation 的边界要保持清楚：foundation 是技术基础，system 是系统业务主数据。
- `ProjCodeStoreService` / `proj_code_store` 带有项目编号语义，但当前只在 system 内部出现，更像全局编号存储/唯一性服务；暂不迁移，后续如果证明只服务项目主链路，再评估下沉到 `projectprocess`。

处理建议：

- 保持独立模块。
- 只放用户、组织、角色、岗位、权限、系统配置等系统域事实。
- 系统域可以提供全局编号、配置、登录态和审计等运行支撑，但不能承载项目、合同、付款等业务规则。

### payment

结论：`payment` 是付款域，应保持独立；Java/POM 层已经完成一轮低风险依赖瘦身，当前主要问题转为 Mapper XML 层的跨域读模型。

依据：

- 付款域拥有付款申请、付款实际、付款核销、付款公开信息、付款材料和付款版本等核心事实。
- 付款流程状态只是付款审批/权限/公开信息展示的外部上下文，不应让付款域直接依赖 `FlowTaskApiService`、`ProcessService` 或 workflow 的流程模型枚举。
- 当前已新增 `PaymentWorkflowPort` 和 `PaymentWorkflowProcessSnapshot`，付款域只表达自身需要的流程语义：付款申请在办流程、付款核销明细是否在流程中、合同自动起租/新增收款流程是否在办、项目评审最新通过流程号。
- 具体 flow-core / workflow 适配已经下沉到 `application.orchestration.adapter.payment.PaymentWorkflowPortAdapter`。
- 原 `PaymentBaseInfoMapper.paymentFlowList` 已迁到 `application.orchestration.collection.mapper.CollectionPaymentFlowCenterMapper`，调用方仍是 `application.orchestration.collection.CollectionFlowCenterService.paymentList`。它同时拼接付款、保证金、质保金、合同、租金计划和退回记录，归属已从 payment 内部 mapper 改为 application 跨域读模型；后续还需要继续拆成多域快照输入或 application 组装。
- 原 `PaymentActualDetailMapper.listContractPayInfoBetween`、`listContractPayInfoBeforeTargetDate` 已迁出 payment。现在由 `budget.application.port.BudgetPaymentFactPort` 定义预算收益测算需要的合同付款事实快照，application 的 `BudgetPaymentFactMapper.xml` 承接合同、付款实际和收款计划的跨域 SQL。payment 已删除对应 `ContractPayInfoDTO` 和空 XML。
- 原 `PaymentBaseInfoMapper.queryListWithContractId` 已迁到 `application.orchestration.payment.mapper.PaymentProcessQueryMapper.listApprovedPaymentCreateByContractId`。它依赖 Flowable 历史表判断付款申请审批通过状态，服务合同侧最低 IRR 计算场景，归属改为 application 的付款事实 + 流程历史读模型；payment XML 不再读取 Flowable 历史表。
- 原 `PaymentBaseInfoMapper.myList` 已迁到 `application.orchestration.payment.mapper.PaymentProcessQueryMapper.listPayment`。它读取合同业务部门字段用于付款首页列表筛选/展示，归属改为 application 的付款事实 + 合同部门读模型；payment XML 不再读取 `contract_base_info`。
- `PaymentPolicyInfoMapper` 当前只围绕付款保单占用/释放视角，仍属于 payment 内部能力；风险点是与正式 `policy` 域的同步边界，而不是优先迁出的跨域 SQL。

### margin

结论：`margin` 是财务大域下语义清楚的保证金子域，当前不建议并入 `finance`、`payment` 或 `collection`。

依据：

- `zswl-mithras-margin` 源码规模不大，但拥有 `margin_base_info`、`margin_record_info`、`margin_write_off_record`、`warranty_base_info`、`warranty_record_info` 等独立写模型，表达保证金/质保金的收取、占用、释放、退款和核销生命周期。
- Java import 层只出现 `margin`、`foundation`、`dto`、`api`，没有直接 import 合同、付款、收款、工作流等业务域。POM 也只声明 `api`、`foundation` 和框架依赖。
- 现有 `MarginContractInfoPort`、`MarginCollectionPort`、`MarginPaymentReceiptPort`、`MarginRecordSupportPort`、`MarginViewAuthPort` 已经用 margin 自有输入/输出模型隔离合同、收款、付款、权限等外部事实，方向是干净的。
- POM 中的框架依赖能在源码中找到对应使用：controller/servlet/validation、MyBatis-Plus mapper/service、Excel/POI 导出、Spring transaction、XXL job、Hutool 和 commons-lang3。直接 `org.mybatis:mybatis` 仅为 `@Param` 提供显式来源，已由 MyBatis-Plus starter 传递覆盖并移除。

边界风险：

- 原 `MarginBaseInfoMapper.xml` 的 `pageList` 为列表查询直接 join `contract_base_info` 并使用通用权限 SQL，属于 SQL 层合同读模型耦合；本轮已迁出到 application 读侧 `MarginBaseInfoListQueryMapper`，margin 仅保留 `MarginBaseInfoListQueryPort`。
- 外部模块主要通过 application 读取 margin 事实，但 payment、dashboard 等读侧仍存在表级/模型级引用场景；后续优先治理外部表级读取，而不是合并 margin。

收敛建议：

- 保持独立 Maven 模块，作为 finance 大域内的保证金事实源。
- 不做物理合并；优先治理外部直接读取 `margin_*` / `warranty_*` 表和模型的场景。
- 如果后续做 finance 大域物理收敛，`margin` 应作为候选观察模块，但前提是证明保证金不再需要独立写生命周期。目前证据不支持这一点。

### capital

结论：`capital` 是资金流水与核销域的候选边界，但当前不适合物理合并，也不适合把 `application/orchestration/capital` 原样搬回模块。

依据：

- `zswl-mithras-capital` 本体 POM 只依赖 `api`、`foundation` 和框架 provided 依赖；源码 import 只有 `dto`、`foundation`、`api`、`capital`，没有直接 import 其他业务域。
- 模块本体约 45 个 Java 文件，主要是 controller/application 接口、资金流水/核销枚举、job 入口和 job service 接口、业务流水 Excel 模型/导出器，以及 `FinanceFlowWriteOffDetail` mapper/model/service。
- resources 层也很薄，Mapper XML 只围绕 `finance_flow_write_off_detail` 核销明细表，说明 capital 本体目前更像“资金流水接口壳 + 少量核销明细事实”。
- 真正复杂的资金流水处理在 `application/orchestration/capital`。源码 import 粗分显示该目录同时依赖 `third`、`fund`、`contract`、`payment`、`margin`、`collection`、`system`、`customer`、`riskcontrol`、`finance`、`basedata` 等多个域。

边界判断：

- `capital` 的业务语义和 `finance`、`fund`、`payment`、`collection`、`margin` 相邻，但不等价。它表达的是资金流水进入系统后的识别、匹配、核销、释放和核销明细，不是融资事实、付款事实、收款事实或财务报表。
- 如果现在直接合并 `capital`，只能减少一个 Maven 壳，不能降低实际复杂度；复杂度仍然留在 application 的跨域核销编排里。
- 如果现在把 application/capital 整体回迁，反而会把 third/fund/contract/payment/collection/margin 等业务域依赖带回 capital，污染边界。

下一步可执行切口：

- 先把 `application/orchestration/capital` 拆成三类标注：纯资金流水/核销明细/流水状态规则、跨域核销编排、外域事实读取。
- 优先从 `FinanceFlowRecordService` 中识别可回迁的小块：流水保存/去重、流水状态迁移、核销明细落库等不需要理解外域生命周期的逻辑。
- 在 capital 内定义自有流水快照、核销输入、核销结果模型，避免回迁时把 `third` 的财资平台持久化模型或 `fund/payment/collection/margin` 的模型带入 capital。
- `BankFlowProcessingCenterService`、`BankFlowProcessingCenterFinanceService`、`BusinessFlowService`、`FinanceFlowAutoWriteOffService`、`ProjectCollectWriteOffServiceImpl` 当前都属于跨域编排或跨域读侧组装，应继续留在 application，直到其依赖被切成明确 port/快照。

已完成：

- 删除 `zswl-mithras-payment` 对 `zswl-mithras-workflow` 和 `cn.zswltech.flow:core` 的 POM 直接依赖。
- `PublicInfoQueryService`、`PaymentActualDetailRemoveAuthChecker`、`PaymentActualDetailOperationAuthChecker`、`PaymentApprovalBLRender`、`PaymentApprovalZLRender` 不再直接 import workflow/flow-core。
- `PaymentPublicInfoSupportPort` 不再暴露 document 的 `MaterialsList` 持久化模型；页面文件展示通过 `FileListRSP`，公开信息复制通过付款域自有 `PaymentPublicInfoMaterialSnapshot`。
- `PaymentPublicInfoSupportPort` 不再暴露 contract 的 `ContractBaseInfo` 持久化模型；同项目评审合同列表查询通过付款域自有 `PaymentPublicInfoContractSnapshot` 只返回所需合同 id。
- `PublicInfoQueryService` 不再直接注入 `ContractBaseInfoMapper`、`ClientMapper`、`ContractBaseInfo`、`Client`。公开信息客户列表、合同主办校验、同项目评审合同查询所需上下文已通过 `PaymentPublicInfoContractContextSnapshot` 由 `application` adapter 适配。
- `PublicInfoQueryService` 不再直接注入合同参与方 service，也不直接消费 `ContractTenantry`、`ContractGuarantor`、`ContractMortgage`、`ContractPledge` 等合同持久化模型。承租人、担保人、抵押人、质押人客户集合已通过 `PaymentPublicInfoContractParticipantSnapshot` 由 `application` adapter 适配。
- `PaymentApprovalBLRender`、`PaymentApprovalZLRender` 不再继承 contract 的 `AbstractBasicRender`，也不直接消费合同/客户版本模型；合同、客户、机构、人员等外部输入已通过 `PaymentApprovalRenderSupportPort` 和 `PaymentApprovalRenderSnapshot` 收口，具体适配留在 `application.orchestration.adapter.payment.PaymentApprovalRenderSupportPortAdapter`。
- `PaymentConvert` 已不再承担 `ContractBaseInfoLib -> PaymentContractListRsp` 的外域模型转换；合同库到付款列表响应的映射留在 `application` 的付款编排服务中。
- 已移除 `zswl-mithras-payment` 对 `workflow`、`flow-core`、`contract`、`customer` 的直接 POM 依赖。当前 `payment` POM 直接业务/底座依赖收敛为 `api`、`foundation`、`document`。
- 静态搜索确认 `zswl-mithras-payment/src/main/java` 和 `zswl-mithras-payment/pom.xml` 中无 workflow/flow-core/contract/customer 直接引用。
- 编译验证通过：`mvn -pl zswl-mithras-payment -am -DskipTests compile`，`mvn -pl zswl-mithras-application -am -DskipTests compile`。其中 `payment -am` reactor 已缩小到 root、api、foundation、document、payment 5 个模块。

仍需后续处理：

- `PaymentPublicInfoSupportPort` 已不再暴露 `ContractBaseInfo` 外域模型，且公开信息合同/客户上下文已收敛为付款自有快照。
- `PublicInfoQueryService` 已不再直接读取合同参与方 service/model；公开信息查询当前更接近“payment 记录与规则 + application 输入合同/客户/参与方/材料/third/workflow 快照”的边界。
- 付款审批文档渲染已通过付款自有快照隔离合同/客户版本模型；后续重点转向 mapper XML 表级读模型和 document 材料版本依赖必要性验证。
- 业务域自己的配置不要放进 system，除非它确实是平台级系统配置。

### basedata

结论：`basedata` 是基础业务数据域，适合独立存在。

依据：

- `zswl-mithras-basedata` 只依赖 `api`、`foundation` 和必要技术依赖。
- 代码层复核显示，源码 import 主要集中在 `basedata` 自身、`dto`、`foundation`、`api`，没有直接 import 合同、付款、客户、项目、流程、报送等业务域。
- MyBatis-Plus、Hutool、`oss-toolkit`、XXL Job 都有源码依据：分别用于 mapper/model/service、日期/集合/Excel、LPR 模板预览、汇率/特殊日期/LPR 提醒任务。
- 前面已整理过 dictionary、exchange rate、bank account、job 等包结构，并把基础数据 job 的消息/todo/用户，以及银行账户项目校验/同步等通过 port 外接。
- 它被业务域读取，但不应理解具体业务流程。

发现的问题：

- basedata 与 system 都会被大量模块读取，容易被混用。系统组织用户属于 system，汇率、银行账户、业务字典等属于 basedata。

处理建议：

- 保持独立模块。
- 继续保持低耦合，不向业务域反向依赖。
- 业务域需要基础数据时优先通过稳定 service/resolver，而不是复制基础数据枚举或 SQL。

### afterlease

结论：`afterlease` 是租后管理域，建议保持独立，但继续做依赖瘦身和跨域读模型隔离。

依据：

- 租后检查、租后调整、外部查询、租金催收、罚息减免都有独立生命周期，不是 `contract`、`collection` 或 `riskcontrol` 的简单子包。
- afterlease 会读取合同、客户、收款、项目、流程、文档等外部事实，但这些读取不应让 afterlease 直接持有外域 mapper/model。
- 当前已把 workflow 适配、部分合同/客户/收款读取、催收邮件和付款通知渲染数据组装迁到 `application/orchestration/adapter/afterlease`。

已推进：

- `AfterLeaseWorkflowPort` 承接流程启动、流程查询、流程业务数据记录和任务转换需求，afterlease 不再直接依赖 workflow/flow-core 技术 DTO。
- `AfterLeaseContractVersionPort` 承接租后检查报告对合同版本库和租金版本库的读取需求，afterlease 不再直接依赖 contract 版本 service/model。
- `AfterLeaseCollectionPort` 已用租后本地快照替代公开签名中的 `CollectionBaseInfo` 和 MyBatis `Wrapper`。
- `PaymentNoticeHtmlRender` 已改为通过 `PaymentNoticeRenderDataPort` 获取跨域渲染数据。
- `RentCollectionDetailService` 已改为通过 `RentCollectionDetailDataPort` 获取催收明细、收款记录和逾期上下文，collection/contract 的 mapper 与持久化模型查询迁到 application adapter。
- `RentCollectionIndexServiceImpl` 的 Java 层补充查询已改为通过 `RentCollectionDetailDataPort` 获取逾期收款、收款卡片和合同还款期数快照；催收首页主列表 SQL 仍保留在 afterlease mapper 中，后续单独治理。
- 租金催收邮件账户 port 已改为返回 `RentCollectionEmailBankAccountSnapshot`，不再向 afterlease 暴露 basedata 账户持久化模型或账户列表 DTO。

后续重点：

- `RentCollectionIndexMapper.xml` 仍是租后催收首页的跨域读模型集中点，后续应优先收敛为租后查询快照或 application 装配。
- `NewAfterLeaseCheckExternalQueryMapper.xml`、`NewAfterLeaseCheckPlanBaseMapper.xml` 中的客户、资产分类、用户组织读取仍需继续评估。
- 保持 afterlease 独立，不做物理合并；优先把它变成“拥有租后规则，读取外部事实通过 port/快照”的干净业务域。

### workflow

结论：`workflow` 是横向流程能力，应独立且不适配具体业务域。

依据：

- `zswl-mithras-workflow` POM 只依赖 `api`、`foundation` 和 Flowable/flow-core/MyBatis/Spring/XXL Job/POI 等技术依赖，没有直接依赖业务域模块。
- 当前约 110 个 Java 文件，源码 import 搜索只发现 `api`、`foundation` 这两个内部底座依赖，未发现 message/document/contract/payment/customer/projectprocess/riskcontrol 等业务域包直接 import。
- 它负责流程模型、流程实例、待办、流程准备、节点、流程查询、流程关注、流程变更说明、跟踪事项等横向流程能力。

发现的问题：

- POM/Java import 边界干净，但语义注册表不干净：`ProcessModelTypeEnum` 集中登记了客户、项目立项/评审/定价、授信、合同、付款、租后、资产五级分类、FTP、资金、KPI、保单、租赁物、黑灰名单、预算、征信、金融局报送、资料归档、保证金等大量业务流程模型。
- `resources/sql/0.0.1/init_flow.sql` 保存了客户、项目评审、合同、付款等业务流程初始化数据；`resources/sql/20230608/审批流变更说明.sql` 直接写入项目评审、合同管理、授信立项、授信评审等业务菜单权限；`resources/sql/评审会会议纪要_track_event.sql` 给跟踪事项增加项目评审会议纪要字段；BPMN 示例文件仍带“租赁”测试语义。
- `ProcessVarEnum`、`FlowConstants`、`TrackTaskTypeEnum`、`TrackEventExcelExporter` 等类中仍有合同、付款、项目评审、授信、资产、租后、客户等业务词汇，说明 workflow 仍承担了一部分业务流程协议/展示字段注册职责。
- `RentPaymentNotifyFlowHandle`、`FinancingRepayPlanConfirmFlowHandle`、`FinancingRepayWriteOffConfirmFlowHandle` 把租金支付通知、融资还款计划确认、融资还款核销确认等业务场景专用流程准备和启动校验放在 workflow 内部，短期可运行，但长期更像业务域/application 提供的流程准备适配。
- 反向使用面很广：application 中大量 workflow adapter、动态表单和流程结束 handler 是合理装配点；但 finance、contract、creditreport 等业务域仍有直接使用 `ProcessModelTypeEnum`、`ProcessService`、`CommonProcessPrepareMapper`、flow-core API 的痕迹，会把横向流程细节带入业务域。
- workflow 不应直接依赖 message/document 或具体业务域；业务流程结束后的通知、材料、业务状态更新应由 application 编排。当前 workflow 自身没有这类源码依赖，方向基本守住。

处理建议：

- 保持独立模块。
- 坚持 `workflow` 不依赖 `message`，`message` 不依赖 `workflow`。
- 需要把流程通知接到消息时，由 `application` 实现 adapter 或编排服务。
- 后续不要把业务模块并入 workflow；真正要做的是把业务流程模型 key、业务流程准备数据、业务菜单 SQL、流程结束后的业务动作逐步外移到业务域或 application 的注册/adapter 层。workflow 只保留流程引擎、任务、通用流程查询、通用流程准备框架和稳定扩展点。
- 业务域直接依赖 workflow/flow-core 的场景，应优先收敛为业务域自有 port 或 application adapter；其中 finance、contract、creditreport 是后续较明显的治理候选。

### message

结论：`message` 是横向消息/通知能力，应独立且不适配业务域。

依据：

- `zswl-mithras-message` 只依赖 `api`、`foundation`，当前 POM 边界干净。
- 前面已处理过 message 与 workflow 的耦合方向，目标是业务域或 application 适配 message，而不是 message 认识业务域。
- 本轮代码层复核显示，message 模块约 58 个 Java 文件，源码和 resources 没有直接 import `workflow`、`contract`、`payment`、`collection`、`customer`、`projectprocess`、`riskcontrol` 等业务域模块；resources 只包含 `zhfk_notice`、`message_base_info`、`exception_info` 等消息自有表和 Mapper。
- POM 技术依赖有源码证据：notice starter、MyBatis-Plus、PageHelper、Hutool、Jackson、Fastjson、commons-lang3/collections4、XXL Job、Swagger、Servlet、Spring context/web/tx、slf4j 均在 message 源码中使用，当前没有明显可删的空依赖。
- 反向使用主要集中在 `application`：workflow、margin、afterlease、riskcontrol、rating、basedata、collection、archives、report、payment、customer、fund、filingmaterials 等通过 application adapter 或编排服务调用 `MessageService`、`MessageConver`、`EmailUtil`、`AbstractSendEmailHandler` 等消息能力，方向总体正确。

发现的问题：

- 任何 `message/adapter/<业务域>` 都要谨慎：如果 message 为具体业务域实现 adapter，就会反向依赖业务语义。
- 消息模板、收件人、跳转链接常常需要业务上下文，这部分应由 application 或业务域提供上下文，message 只负责发送能力。
- `NoticeSourceENUM` 和 `MessageTypeEnum` 仍枚举了大量具体业务通知类型，例如资产分类、绩效、付款/收款、征信报送、租后、评级、项目进展、还本付息、评估机构白名单等。这不是 Maven/Java import 依赖，但属于横向能力模块承载业务通知注册表的语义耦合。
- `TodoMessageBody`、`NoticeMessageBody` 中仍使用 `workflowname`、`flowid`、`nodename` 等流程字段名，表示消息协议仍沿用了 OA/流程语义。短期可接受，但 message 不应据此理解 workflow 业务规则。

处理建议：

- 保持独立模块。
- 不依赖 workflow/document/业务域。
- 跨域通知用 application 接线：业务域提出通知需求，application 取上下文并调用 message 发送。
- 后续治理重点不是拆 message 模块，而是把业务通知类型从 message 的硬编码枚举中逐步外移：可以先由各业务域或 application 提供通知类型/来源字符串或注册配置，message 只校验通道和消息体结构。
- `MessageTypeEnum`、`NoticeSourceENUM` 迁移风险较高，因为 application 中大量 adapter 和编排类直接引用它们；应分批做兼容层或先新增字符串协议入口，再逐步替换调用方。

### document

结论：`document` 是文档、材料、模板、OCR/文件能力，应作为横向能力独立。

依据：

- `zswl-mithras-document` 只依赖 `api`、`foundation`。
- 多个业务域依赖 document 的材料清单、版本材料、模板渲染或文件 provider，说明它是横向文件能力。
- 本轮代码层复核显示，document 模块约 59 个 Java 文件，源码和 resources 没有直接 import `contract`、`payment`、`collection`、`customer`、`workflow`、`message` 等业务域或横向能力模块；内部主要围绕 file、materialsfile、file template、OnlyOffice、OCR、OSS 文件和材料版本处理。
- 资源层主要是 `materials_list`、`materials_list_lib`、`file_template`、`file_authentication_config` 等 document 自有表；POM 技术依赖如 oss-toolkit、MyBatis/MyBatis-Plus、Hutool、pinyin4j、Fastjson、Spring、Servlet、MapStruct、Swagger、validation、XXL Job 等都有源码或插件使用场景。
- 反向使用很广：projectprocess、contract、payment、credit、creditreport、assetclassify、afterlease、policy、leaseholdproperty、filingmaterials、application 等模块都直接消费材料清单、材料版本、模板服务或 document API。这说明 document 是横向能力，不能并入某个单一业务域。

发现的问题：

- document 的材料模型被多个业务域直接 import，用起来方便，但也容易让业务域和文件持久化结构绑定。
- document 不应理解业务流程和业务状态，只应管理文件、材料、模板、渲染和访问能力。
- `FileTemplateEnum`、`FileTemplateKeyEnum`、`FileDownloadZipPathEnum`、`MaterialsEnum`、`MaterialsType`、`NormalMaterialsType` 等枚举中承载了大量具体业务模板和材料语义，例如合同、付款、收款、授信、资产分类、租后、融资、FTP、ECL、印花税、归档等。这不是 Maven 依赖，但属于横向模块承载业务配置的语义耦合。
- `MaterialsListMapper.queryNeedSignFile` 直接写死 `business_type = 'CONTRACT'`、`MAIN_CONTRACT`、`LEASE_ITEM`、`COLLECTION_CONFIRM` 等合同面签材料查询条件；`MaterialsListController` 也暴露 `contractMaterialList`、`contractLeaseMaterialList`、`paymentMaterialList` 等业务命名接口。它们是 document 中最明显的业务读模型残留。
- `FileTemplateKeyInitJobServiceImpl` 内硬编码“合同模板展示清单”和一批合同/租赁/质押/保理模板名称，说明历史模板初始化职责带有合同域语义。

处理建议：

- 保持独立模块。
- 业务域可定义材料类型和材料需求，但文件存储/模板/材料清单能力由 document 承担。
- 复杂的业务材料校验和流程装配优先放 application。
- 短期不要为了“纯净”搬走所有模板枚举，否则会影响大量业务域和 application 中的模板调用；先把它们标记为业务模板注册表。
- 后续治理方向是把业务模板/材料类型的注册职责逐步迁到业务域或 application 配置：document 提供模板存储、查询、渲染和材料版本能力；业务域提供模板 key、材料类型、业务筛选条件。
- 优先切口可以是 `queryNeedSignFile` 和 `contract/payment` 命名接口：新增更通用的材料查询能力，让 contract/payment/application 传入业务类型和材料类型集合，逐步减少 document 对合同/付款语义的硬编码。

### third

结论：`third` 是第三方集成防腐层，应独立。

依据：

- `zswl-mithras-third` POM 只依赖 `api`、`foundation` 两个内部模块，其他都是外部系统 SDK、HTTP/Feign、文档/PDF/OCR、Redis、MyBatis、Spring、XXL Job 等技术依赖。
- 当前约 407 个 Java 文件，源码 import 搜索只发现 `api`、`foundation` 两个内部底座依赖，未发现 finance/capital/contract/customer/riskcontrol 等业务域包直接 import。
- 模块内部按外部系统/外部能力分组明显：`financialshare` 约 105 个 Java 文件，`tianyancha` 约 70 个，`datashare` 约 33 个，`overduereport` 约 30 个，`externaldata` 约 30 个，另有 `baorong`、`qiyuesuo`、`providence`、`aliyun/ocr`、`qcc`、`xinsight`、`yunhu`、`jinkong` 等。
- 天眼查、企查查、OCR、中登、契约锁、财务共享、宝融、金控/云湖、Providence、XInsight、外部数据、异常重试等外部接口 DTO、调用日志、请求重试、临时同步表应隔离在 third，避免业务域直接绑定外部接口细节。
- 资源 SQL 里虽然存在 `风控应用`、`租前息` 等历史目录名，但表和 Java 模型主要对应 third 自身的外部数据能力：`data_share_*` 对应数据共享；`exception_request_info`、`external_exception_info` 对应第三方异常重试；`sync_cq_record`、`cq_related_mithras` 对应苍穹/财务共享请求记录；`bill_overdue`、`public_outer_info_record`、`peer_comparison_*` 对应 providence 外部数据和公共信息查询。

发现的问题：

- 如果业务域直接依赖 third client，会把外部 API 结构带进业务规则。
- third 不应拥有业务主数据，只负责外部调用、转换和防腐。
- `financialshare` 是 third 内最大子能力，包含财务共享 API、苍穹同步记录、财务流水临时表、匹配结果、异常请求等；它和 capital/application 中资金流水、核销编排语义很近，但 ownership 不同：third 持有外部系统协议和同步记录，capital/application 持有业务流水和核销规则。
- `BRFlowRecordMapper.xml` 会 update `finance_flow_record`，`xinsight/mapper/XinsightWarnMonitorMapper.xml` 读取 `risk_control_warn_monitor`，说明 third resources 中仍有少量跨表耦合。短期可以视为外部同步/查询适配层的历史实现，长期应通过业务域 port 或 application adapter 输入/输出业务快照。
- `util/WatermarkUtil`、`aliyun/ocr`、`providence` PDF 解析等文档/PDF/OCR工具能力放在 third 有些混合：它们不是某个业务域，但也不是纯第三方接口。短期不拆；如果未来 document 和 third 都继续膨胀，可以评估独立 `integration-document` 或把纯文件处理能力迁回 document/foundation 的技术能力包。
- 模块体量已经较大，长期可以按外部系统拆成 `third-financialshare`、`third-enterprise-data`、`third-signature`、`third-risk-data` 等子模块，但这属于横向能力内部拆分，不是并入业务域。
- `XinsightMySqlConfig` 使用 MyBatis-Spring 的 `@MapperScan`、MyBatis core 的 `Interceptor`、`SqlSessionFactory`、`JdbcType` 等类型，以及 MyBatis-Plus 的 `MybatisSqlSessionFactoryBean`；这些已由 `mybatis-plus-boot-starter` 依赖链覆盖，POM 中不再保留单独的 `mybatis-spring` 或 `org.mybatis:mybatis` 直接声明。

处理建议：

- 保持独立模块。
- 不并入 finance/capital/riskcontrol/customer。合并会降低 Maven 模块数量，但会把外部系统协议、SDK、请求重试、外部临时表带进业务域。
- 业务域需要外部数据时，可以通过 application adapter 或业务域 port 调用 third。
- third 返回的数据应尽量转换成业务域需要的输入模型。
- `风控应用` 目录下的 third SQL 暂不迁移；目录名不能作为业务归属证据，优先看模型、service 和调用生命周期。
- 长期应让 `capital` 定义自己的流水快照/流水事实模型，`third` 继续保留财务共享 API、临时流水、匹配结果、宝融同步和异常重试等外部系统防腐能力。
- 后续如果要治理 third，优先清理资源层跨表写读和对外暴露的持久化模型；再评估是否按外部系统拆 Maven 子模块。

### application

结论：`application` 是装配和跨域编排层，不是业务实现长期居住地。

依据：

- `zswl-mithras-application` 依赖几乎所有业务域和横向能力，符合“启动装配层/跨域 adapter”的位置。
- 前面已清理过大量假 application 包名，把一些业务逻辑迁回对应模块，但仍有许多跨域 facade、adapter、workflow end handler、document provider、job impl。
- 本轮复核确认 application 源码存在少量 MyBatis 注解和异常类型引用，主要集中在 application 读侧 mapper 和 `TrackEventService` 的 `MyBatisSystemException` 处理；直接 `org.mybatis:mybatis` / `org.mybatis:mybatis-spring` 依赖无独立必要性，已删除，相关类型由 MyBatis-Plus/tk-mybatis 等依赖链提供。`dependencyManagement` 中的 MyBatis/MyBatis-Spring 版本管理暂保留。

发现的问题：

- 很多单域业务实现仍在 `application/orchestration/<domain>`，例如资金、保单、授信、项目、合同等历史实现。
- application 过厚会掩盖真实业务域边界：看似模块低耦合，实际核心逻辑在 application 里互相调用。
- 本轮继续复核剩余顶层小目录后，暂不把所有顶层目录视为可迁移对象：
  - `externalinfo` 原先混放 third/providence/天眼查/dataminer 适配和客户监控聚合。本轮已把 `DmImportService` 归到 `adapter/third/dataminer`，把 `TycExecutionService` 归到 `adapter/third/tianyancha`，把 Providence 公开信息查询和统一视图代理归到 `adapter/third/providence`；剩余 `ClientMonitorController/Service` 同时读取 customer、riskcontrol、system，属于客户监控读侧聚合，本轮已归到 `client/monitor`，短期仍留在 application。
  - `adapter/share` 和 `adapter/third` 根目录下的数据共享适配器语义上都属于 third/data-share 装配。本轮已把 `DataShareMaterialPortAdapter`、`DataShareClientCodeSyncPortAdapter` 统一归到 `adapter/third/datashare`；third 仍持有数据共享外部协议和记录，application 只负责把材料、客户编码等业务输入接到 third port。
  - `adapter/third` 根目录剩余的 `TycIndustryTypePortAdapter` 和 `ExceptionRequestRetryPortAdapter` 也不是第三方通用根能力，分别属于天眼查行业映射和财务共享异常重试。本轮已归到 `adapter/third/tianyancha`、`adapter/third/financialshare`，让 third 装配按外部系统子能力分组。
  - `app` 是移动端/客户端入口聚合，直接读取 customer、contract、collection、payment、projectprocess、afterlease、document、third 等多域事实，短期属于页面/端侧聚合，不应硬迁到单一业务域。本轮已把顶层 `app/AppService` 归到 `client/app`，与已有 `facade/client/app` 对齐，保留 application 装配定位。
  - `enums/BusinessModuleEnum` 实际承担数据权限业务模块注册表和流程模型到业务模块映射，不是普通枚举集合。本轮已归到 `auth/BusinessModuleEnum`，与 `BusinessModuleEnumResolver` 同包，清掉无语义的顶层 `enums` 目录。
  - `maintenance` 是跨域历史数据修复/清理脚本集合，直接操作 contract、customer、rating、kpi、finance、ftp、fund、payment、policy、projectprocess 等模型，短期作为维护装配代码保留，长期应按一次性脚本或运维工具隔离。
  - `metadata/enumscan` 是全局下拉枚举和材料类型扫描，用来扫描 application/report/rating/blackgray 等枚举并服务 system select/document material 展示；短期保留在 application，长期应改成业务域/application 注册式元数据。
  - `export/excel/ExcelExporterFactory` 是 projectprocess 与 contract Excel 导出器选择器，当前由项目现金流和合同租金导出共同使用，短期不拆；长期应将选择逻辑下沉到 owner domain 或变成各域自己的 factory。
  - `util/FinancialUtil` 是跨 projectprocess/contract/budget/fund/finance/monthly/ftp 的历史金融计算工具，牵涉现金流、IRR、日折现率、FTP 和税率计算；短期不搬，后续应拆为稳定计算内核和各业务域自己的适配模型，避免继续把 projectprocess BO 当作全局公共模型。
  - `job` 目录大多已经按 afterlease/capital/client/contract/filingmaterials/finance/ftp/kpi/policy/riskcontrol/workflow 等业务语义分组；本轮已把融资还款确认任务 `FinancingRepayInfoJob` 归到 `job/fund`。顶层剩余 `SystemJob`、`NextMonthRentNotify` 仍是历史跨域定时入口，短期留在 application，后续逐个拆成 owner domain job service + application adapter。
  - `listener` 目录保留跨域事件接线定位。本轮已把顶层合同/项目评审相关监听器 `ContractPriceChangeEventListener`、`ContractUpdateProjReviewInfoListener` 收敛到 `listener/contract`；client/collection/timeout 子包暂留。
  - `facade/materialsdger` 只是归档资料台账 facade 的错拼历史目录，对应服务已在 `filingmaterials/materialsledger` 下。本轮把 `FundSideMaterialsManagementLedgerFacade`、`ProjSideMaterialsManagementLedgerFacade` 归到 `facade/filingmaterials/materialsledger`，不改变对外 service 接口。

处理建议：

- 保持独立装配模块。
- 长期只保留跨域 facade、port adapter、workflow/message/document/third 装配、流程结束后的跨域编排。
- 只操作一个业务域 mapper/model/service 的代码，应逐步迁回对应业务域。

### web

结论：`web` 是启动入口和运行时组装模块，不应放业务逻辑。

依据：

- `zswl-mithras-web` 当前主要依赖 `application`、`system` 和 `report`，作为 Spring Boot 启动入口；其中 `system` 来自全局异常处理和系统开关/审计日志/用户名称解析的直接生产源码引用。
- mapper scan、启动配置、测试入口等集中在 web。
- 本轮复核生产源码只有少量启动/Web advice/调试入口类，resources 集中在 profile 配置、Flyway 迁移、logback、静态 OnlyOffice 调试页和监控配置。
- 已删除未启用、未引用的废弃 `ResponseBodyHandler`，避免 web 保留合同提醒逻辑和 contract service 的假依赖信号。

发现的问题：

- `web -> report` 是历史特殊依赖，因为 report 没经 application 完全装配。长期看 web 最好只依赖 application。
- `web -> system` 是启动层全局 Web advice 对系统配置、审计和用户名称解析的直接依赖，短期显式声明比依赖 transitive 更诚实；长期若全局 advice 下沉到 application 或 system 提供 Web starter，再重新评估。
- web 测试里直接引用大量业务模块，可以作为集成测试存在，但不应形成生产代码依赖模式。
- `src/main/java/cn/zswltech/flow/core/api/FlowExecutionApiService.java` 位于 web 模块但包名属于 flow-core，且被 workflow/application 使用，属于启动模块中的历史覆写/补丁类。长期应回到 workflow/flow-core 扩展治理线，而不是继续留在 web。
- `TestController` 和 `ApprovalTestInterceptor` 是本地调试/审批测试入口，应避免成为生产业务触发路径。

处理建议：

- 保持独立启动模块。
- 长期目标是 `web -> application`，特殊的 `web -> report` 后续随 report/application 边界治理再处理。
- 从“能否合并”看，web 可以作为 application 的启动子包存在；但当前独立 Maven 模块仍有最终打包、环境配置、迁移资源和测试工具隔离价值，短期不建议物理合并。

### react

结论：`react` 是前端工程，不纳入后端 Maven 业务域合并判断。

依据：

- `zswl-mithras-react` 是 React/Admin 前端项目，包含 `package.json`、构建脚本、静态资源和 `dist`。
- 它不属于后端 Maven 模块，也不拥有后端业务事实。

处理建议：

- 后端模块合并不处理 `react`。
- 若后续整理前后端边界，应单独从页面路由、API 调用和前端模块组织角度分析。

### workbench

结论：`workbench` 暂不适合作为第一批物理合并对象。

依据：

- `zswl-mithras-workbench` 自身 POM 只依赖 `api`、`foundation` 和框架 provided 依赖，模块内没有直接 import 其他业务域。
- 代码层面复核显示，模块内 Java import 主要集中在 `workbench` 约 84 处、`dto` 约 62 处、`foundation` 约 34 处、`api` 约 29 处，没有直接 import 其他业务域；模块也没有 `src/main/resources` 下的 mapper XML。
- POM 依赖有源码证据：MyBatis-Plus 用于 mapper/model/service，Hutool 用于判空、集合、日期和 Excel 导出，MapStruct 用于 converter，Fastjson 用于快捷入口和指标 JSON 转换，XXL Job 用于初始化和指标刷新任务，Gruul starter 提供 `AccountUtil`、`Response` 等入口能力。
- 模块拥有自己的工作台写模型和缓存表，例如公告、快捷入口、卡片配置、用户卡片关系、指标缓存等。
- 对外部业务事实的读取主要通过 port 完成，adapter 放在 `application/orchestration/adapter/workbench`，符合“业务域不直接吃一串外域依赖”的目标。
- 这些 port 包括 `WorkbenchCardMetricPort`、`WorkbenchFundsLiquidityPort`、`WorkbenchCardCollectionPort`、`WorkbenchCardFundRepayPort`、`WorkbenchCardProjReviewPort`、`WorkbenchFinancialMetricFactorPort`、`WorkbenchRiskControlStrategyPort` 等；application adapter 读取项目、合同、客户、付款、收款、资金、风险、资产分类等事实后转换成 workbench 自己的展示输入。
- 反向依赖主要来自 `application` 约 31 处、`web` 约 1 处。`web` 是入口调用，`application` 主要是跨域 adapter 和 facade，不说明核心业务域依赖 workbench。
- 语义上它属于 reporting 展示入口，但不只是 dashboard 的一个目录；它还有用户工作台配置、初始化 job 和公告等独立生命周期。

发现的问题：

- `application/orchestration/facade/workbench` 仍有较重的页面级聚合，例如 `WorkbenchChartMetricFacade` 直接注入 projectprocess、contract、collection、payment、margin、riskcontrol、assetclassify、system 等服务和模型。短期放在 application 是合理的，因为它是跨域页面聚合；长期不应继续扩散成 workbench 或核心业务域里的业务规则。
- 工作台指标口径里有“存量项目、逾期项目、不良项目、剩余本金、资金流动性”等业务统计。它们可以作为展示指标存在，但事实口径应来自对应业务域或 application adapter，workbench 不应成为这些口径的事实源。

处理建议：

- 短期保持独立 Maven 模块。
- 在目标域地图中归入 reporting 大域治理。
- 后续重点不是合并，而是确保核心业务域不反向依赖 workbench/dashboard/metric 这类读侧聚合模块。
- 如果未来要合并，先证明它只剩展示壳，再考虑并入 reporting 装配模块；不要并入 projectprocess、contract、payment、riskcontrol 等核心业务域。
- 当前不满足物理合并条件：它有公告、快捷入口、用户卡片关系、指标缓存和初始化 job 这些独立写生命周期，不是单纯依附于 `dashboard` 的只读页面。

### report

结论：`report` 是征信报送处理域，不是普通 dashboard/reporting 展示模块；短期应保持独立。`report -> application` 的直接依赖已经移除，后续重点是继续降低横向能力和事实源表级直读的耦合。

依据：

- 模块包含征信报送 draft/formal/fullsnap/procsnap/base 多套报送表、批次记录、修改快照、报送处理器、流程节点、审批处理和 Excel 导出。
- `CrFacade` 负责征信报送批量处理、流程中数据保护、全量/增量快照、流程结束后的数据复制与版本状态恢复，说明它拥有独立报送生命周期。
- POM 直接依赖 `contract`、`workflow`、`payment`、`customer`、`collection`、`system` 等实际读取或协作的模块，属于强读侧/报送侧聚合。
- 代码层复核显示，模块内 Java import 主要集中在 `report` 自身约 806 处，同时直接读取多个事实源：`contract` 约 129 处、`payment` 约 64 处、`customer` 约 43 处、`collection` 约 37 处、`projectprocess` 约 12 处；还直接使用 `workflow/flow-core/flowable`、`system`、`basedata` 等横向/底座能力。POM 里的业务依赖大多是真实依赖，不能为了清爽直接删除。
- 资源层主要围绕 `cr_*` 报送表、draft、full snapshot 和流程快照；SQL 中大量保留 `contract_id`、`payment_id`、`client_id`、`payment_apply_code`、`collection_amount` 等外域事实字段，这是报送快照字段，不是这些交易事实的所有权。
- 已移除 `report -> third`：原先只剩一个未使用 import 和 POM 依赖，没有代码级业务语义。
- 已移除 `report -> creditreport`：客户表报送需要判断客户是否仍需征信报送，现通过 `api/report/ReportCreditClientPort` 表达报送输入需求，由 application adapter 复用 creditreport mapper 实现。
- 已移除 `report -> message`：征信报送流程中的数据变动通知通过 `api/report/ReportNotificationPort` 表达通知需求，由 application adapter 复用 message 发送能力实现。
- 已移除 `report -> assetclassify`：五级分类报送需要读取指定报送日期对应季度的已完成客户分类快照，现通过 `api/report/ReportAssetClassifyPort` 表达报送输入需求，由 application adapter 复用 assetclassify 版本库服务并转换为 report snapshot。
- 外部依赖它的主要是 `web` 和 `application`，核心业务域没有大面积反向依赖 `report`，这一点是好的。

发现的问题：

- `report -> application` 原本是不合理方向，已移除。当前更真实地暴露为 report 对多个事实源和横向能力的读侧聚合依赖。
- `CrFacade` 仍直接使用 workflow、system、contract gendoc repository 等横向/业务能力，说明报送处理和跨域装配仍有继续收敛空间。
- 由于征信报送需要稳定快照，直接读取交易域数据短期可理解，但应明确它是报送快照生成，不是交易域事实所有者。
- `ReportMysqlConfig` 和 mapper 使用 MyBatis/MyBatis-Spring 类型，但这些由 `mybatis-plus-boot-starter` 传递覆盖；直接 `org.mybatis:mybatis`、`org.mybatis:mybatis-spring` 声明已移除并通过编译验证。

处理建议：

- 保持独立 Maven 模块，归入 reporting/监管报送域，而不是合并到 `creditreport` 或 dashboard。
- 已完成 `report -> application` 依赖治理：report 中的 application service 引用改为真实业务域 mapper 或 foundation port，POM 改为显式依赖实际使用模块。
- workflow/system 的调用可逐步收敛到 application 装配层，report 内部保留报送快照、批次、报送状态和报送规则。

### associationreport

结论：`associationreport` 是金融局/协会报送域，属于 reporting/监管报送，不应并入 dashboard 或 metric。

依据：

- 模块拥有报送主表、报送申请、排序、数据权限、各类报表明细、版本表、Excel storedata、文件处理、定时任务和报送流程。
- `AssociationReportService` 管理报表创建、周期、批次、导入、模板文件、流程、版本和报送状态，具有独立监管报送生命周期。
- 它当前直接依赖 `basedata`，本质是多域监管报表汇总；对 customer、contract、payment、collection、rating、assetclassify、dashboard、metric、fund 等外域事实已经通过报送输入 port 由 application 适配。
- 模块已经通过 `AssociationReportTemplateFilePort`、`AssociationReportWorkflowPort` 表达模板文件和流程需求，方向比直接吃 document/workflow 要好。
- 已移除 `associationreport -> collection`：主业报送需要读取收款/核销事实，但现在通过 `AssociationReportCollectionFactPort` 表达报送输入需求，由 application 适配 collection，不再让报送域直接依赖 collection 持久化模型。
- 已移除 `associationreport -> rating`：业务情况表需要读取借据底表事实，现通过 `AssociationReportContractReceiptBottomPort` 表达报送输入需求，由 application adapter 复用 rating mapper 并转换为报送 snapshot。
- 已移除 `associationreport -> assetclassify`：主业报送需要判断客户最新资产分类结果是否为后三类，现通过 `AssociationReportAssetClassifyPort` 表达报送输入需求，由 application adapter 适配 assetclassify 查询和枚举。
- 已移除 `associationreport -> dashboard`：监管报送需要复用管苑投放收益率和项目情况读模型，现通过 `AssociationReportGuanYuanDataPort` 表达报送输入需求，由 application adapter 适配 dashboard 的 `GuanYuanOperationService` 并转换为报送 snapshot。
- 已移除 `associationreport -> metric`：监管报送需要复用资本、利润、国资快报、科目余额等指标口径，现通过 `AssociationReportMetricPort` 表达报送输入需求，由 application adapter 适配 metric 服务并转换为报送需要的稳定数据结构。
- 已移除 `associationreport -> payment`：服务实体经济情况表只需要统计期间内实缴客户数，现通过 `AssociationReportPaymentFactPort` 表达报送输入需求，由 application adapter 适配 payment mapper。
- 已移除 `associationreport -> contract/payment`：主要业务清单需要读取合同、租户、担保、付款和实付事实，现通过 `AssociationReportMainBusinessFactPort` 表达报送输入需求，由 application adapter 适配 contract/customer/payment 并转换为报送 snapshot。
- 已收敛最大十家客户集中度表的客户读取：有效法人客户列表、剩余本金和风险敞口统一通过 `AssociationReportClientSupportPort` 表达，customer mapper/model 读取留在 application adapter。
- 已移除 `associationreport -> customer`：关联方股东/集团关系读取和客户集中度客户列表均通过 `AssociationReportClientSupportPort` 表达，customer mapper/model 读取留在 application adapter；服务实体经济表所需“小型/微型”判断使用报送输入快照中的显示值和本地报送口径常量。
- 已移除 `associationreport -> fund`：对外融资清单需要读取间接/直接融资事实，现通过 `AssociationReportExternalFinancingPort` 表达报送输入需求，由 application adapter 适配 fund mapper/service 并转换为报送 snapshot。
- 已移除 associationreport 中对 `capital.enums` 的隐藏代码引用：报送 storedata 仅需要稳定的现金流/核销项目编码，现沉淀为报送本地常量，避免为了枚举引入资金流水域耦合。

发现的问题：

- 该模块依赖大量业务域，如果把它并入某个业务域，会污染核心域边界。
- 报送数据权限、模板文件、流程启动和数据生成混在同一服务层，后续内部包结构还能继续拆清。

处理建议：

- 保持独立 Maven 模块，定位为监管报送域。
- 不并入 `dashboard`、`metric` 或 `finance`；它消费这些事实/指标，但不拥有它们。
- 后续重点是继续判断对 `basedata` 字典/基础数据的读取是否需要稳定化，并避免核心业务域反向依赖报送域。

### dashboard

结论：`dashboard` 是管理驾驶舱/看板聚合域，天然多依赖，不应被核心业务域依赖。

依据：

- 模块包含 boss 看板、管报/观远数据、项目阶段、付款、还款、租后、风控、资产分类、KPI 等大量查询模型和 Excel 导出。
- POM 依赖 `basedata`、`customer`、`afterlease`、`contract`、`payment`、`fund`、`kpi`、`projectprocess` 等，符合看板读侧聚合特征；`workflow` 直接 POM 依赖已经移除。
- 代码层面复核显示，模块约 300 个 Java 文件，其中 `dashboard` 根包承载看板聚合，`guanbao` 根包承载管理报表/观远相关读模型；POM 中业务依赖大多是读侧展示所需的真实依赖。
- 已移除 `dashboard -> assetclassify`：看板仅需要资产五级分类的稳定展示编码，现沉淀为 dashboard 本地展示枚举，避免为了展示口径依赖资产分类流程域。
- 已移除 dashboard 对 workflow `ProcessModelTypeEnum` 和 `FlowUtil` 的轻量复用：看板只需要稳定流程模型 key、展示名和主模块名时，使用本地读侧枚举 `DashboardProcessModel`。
- 已移除 dashboard 对 flow-core `ProcessBusinessStatusEnum`、`CommentTypeEnum` 的轻量复用：看板只需要稳定流程状态码/展示名和退回类型名时，使用本地读侧枚举 `DashboardProcessBusinessStatus`、`DashboardFlowCommentType`。
- 已移除 dashboard 待办转换对 workflow `BizProcessData`、`FlowQueryExtra`、`BizProcessDataService`、`FlowQueryExtraMapper` 的直接依赖：dashboard 通过 `DashboardProcessExtraPort` 声明流程客户关系和流程扩展展示字段需求，由 application adapter 适配 workflow 持久化模型。
- 已移除 dashboard 租后待发起列表对 workflow `CommonProcessPrepareService`、`CommonProcessPrepare` 和 `ProcessState` 的直接依赖：dashboard 通过 `DashboardAfterLeasePreparePort` 声明待发起流程准备快照需求，由 application adapter 适配 workflow 查询和状态映射。
- 已移除 dashboard 管报退回备注对 workflow `ToDoOperateRecordMapper`、`OperateRecord` 和 flow-core `CommentTypeEnum` 的直接依赖：dashboard 通过 `DashboardBackRemarkPort` 声明退回备注读侧需求，由 application adapter 适配 workflow 操作记录。
- 已移除 `DashboardOperateTodoService` 和 `GuanYuanOperationService` 对 flow-core `OperateRecordMapper`、`NodeBackRecordMapper`、`OperateRecord`、`NodeBackRecord` 的直接依赖：dashboard 通过 `DashboardOperateRecordPort` 声明操作记录和节点退回记录快照需求，由 application adapter 适配 flow-core mapper。
- `DashboardAfterLeaseService` 直接读取租后、客户和权限信息，用于生成工作台/看板卡片，并不拥有租后检查生命周期。
- 资源层体现典型看板 SQL：`ManageReportMapper.xml`、`DashboardProjectStageMapper.xml` 等直接 join `act_*`、`business_status`、`proj_*`、`contract_*`、`payment_*`、`collection_*`、`margin_*`、`client`、`corp_commerce_info`、`bifrost_*` 等多域事实表。这些属于读模型聚合，不代表 dashboard 应拥有这些业务域。
- `sql/风控应用/industry_index_comparison.sql` 虽然目录名带“风控应用”，但只写 `dashboard_config` 的“行业指标对比”看板配置，归属 dashboard。当前不迁移，后续可统一 SQL 目录命名。
- 外部依赖它相对集中。只看 Java import，主要消费方是 `application` 约 47 处、`web` 约 3 处；如果把 POM、Mapper、SQL 粗略搜索也算入，主要消费方还包括 `finance` 和 `creditreport` 的少量历史引用。dashboard 的管苑读模型如需被监管报送或其他聚合复用，应通过 application adapter 转换为目标模块输入。

发现的问题：

- 读侧聚合之间可以复用口径，但监管报送不应依赖 dashboard 内部查询模型；`associationreport -> dashboard` 已通过报送输入 port 改成 application 适配。
- dashboard 内部包含 `guanbao` 包名，说明历史上把管理报表/观远集成也塞进了看板模块，语义有些混。
- dashboard 对 workflow 的 Java/POM 直接依赖已经拆除；对 flow-core 的底层操作记录 mapper/entity 直接读取、流程状态枚举和退回类型枚举也已收敛为 dashboard 本地 port/枚举 + application adapter。当前源码仍直接使用部分 flow-core API/DTO 做流程读侧查询和转换，资源 SQL 中仍可能读取 Flowable `act_*` 等流程读模型表，短期作为看板读模型依赖记录，后续如需进一步收敛，可把流程查询聚合迁到 application 或专门 read adapter。
- 直接 SQL join 多域事实表让看板对外域表结构敏感；短期可接受为读模型便利，长期应避免被业务域反向复用这些 SQL 结构。

处理建议：

- 保持独立 Maven 模块，定位为 reporting 大域中的看板展示/管理驾驶舱。
- 不把 dashboard 合并到 finance 或 metric；它更像消费多域数据的 UI read model。
- `finance -> dashboard`、`associationreport -> dashboard` 已完成第一轮治理；后续重点是继续拆清 `dashboard` 和 `guanbao` 的内部边界。
- 后续内部可以拆清 `dashboard` 与 `guanbao`，前者是看板，后者更像管理报表/外部 BI 集成。

### metric

结论：`metric` 是指标口径/指标计算域，和 dashboard、associationreport 相邻，但不应简单并入展示或报送模块。

依据：

- 模块包含风险指标、指标值、定时任务、指标因子、指标文件、指标字典、金融云指标、指标计算器、emit 上报和大量 calculator。
- POM 依赖 `third`、`basedata`、`projectprocess`、`contract`、`fund`、`kpi`、`payment`、`collection`、`assetclassify`，说明它是跨域指标计算中心。原先 `metric -> riskcontrol` 直接依赖仅用于读取风险策略当前值，本轮已通过 port 和 application adapter 收敛。
- 代码层面复核显示，模块有 410 个 Java 文件，其中 `financialcloudmetric` 约 263 个、`aggregator` 约 51 个、`enums` 约 25 个、`emit` 约 24 个、`mapper` 约 19 个、`service` 约 17 个。Java import 分布约为 `metric` 722、`fund` 64、`foundation` 55、`contract` 45、`payment` 34、`dto` 28、`projectprocess` 15、`api` 15、`collection` 14、`third` 13、`assetclassify` 11、`kpi` 10、`basedata` 4，POM 中的业务依赖大多是真实依赖。此前 import 分布中少量 `riskcontrol` 直接引用已清理，`customer` 隐藏源码依赖也已通过 `MetricCustomerInfoPort` 收敛。
- 本轮删除旧 `YunHuMonthlyReportService` 后，metric 当前为 409 个 Java 文件，旧 `application/third/yunhu` 空目录已清理。POM 技术依赖复核未发现可直接删除项：Gruul 用于系统配置/组织/用户，MyBatis-Plus 用于持久化，Hutool 用于工具/JSON/HTTP，POI 用于因子 Excel 导入，crypto 用于 emit 加密签名，XXL Job 用于定时任务，JUEL 用于指标表达式，MapStruct 用于 converter。
- `RiskMetricService` 管理指标元数据和指标值关系；`FinancialCloudMetricService` 管理金融云指标创建和计算任务，具有独立指标生命周期。
- 反向依赖相对少，按 Java/POM/Mapper/SQL 粗略搜索，主要消费方集中在 `application`、`finance`、`web`；说明它的指标结果被多个报表/经营场景消费，但并不是核心交易事实源。
- 已迁出 `metric/adapter/riskcontrol`：3 个用于实现 `riskcontrol` port 的 adapter 已移动到 `application/orchestration/adapter/riskcontrol`，由 application 把 riskcontrol 的指标因子查询、集中度报送、关联交易报送需求接到 metric。
- 资源层面主要是 `risk_metric*`、`financial_cloud_metric_value` 等指标表；`RiskMetricTimedMapper.xml` 直接读取 `corp_address_info`，说明指标定时存数仍有客户地址读模型耦合。
- 已有少量 port 隔离外域输入或输出：`FinancialCloudAccountBalancePort` 隔离金融云账户余额输入，`MetricCustomerInfoPort` 隔离客户/工商/行业分类输入，`RiskStrategyCurrentValuePort` 隔离风险策略当前值输入，`JinKongMonthlyReportPort` 暴露金控月报同步能力。

发现的问题：

- `metric` 依赖过多业务域，尤其是直接读取交易域模型做计算，指标口径容易绑定外域表结构。
- `metric -> riskcontrol` 的 Java/POM 直接依赖已清理；riskcontrol 是风险运营/预警，metric 是指标口径和计算，二者通过 application adapter 连接策略当前值输入。
- 大量 financial cloud calculator 仍直接注入 `fund`、`contract`、`payment`、`collection`、`assetclassify` 的 mapper/model；这证明 metric 当前不是低耦合模块，而是典型读侧计算聚合。
- POM 里的 fund、contract、payment、collection、projectprocess、assetclassify、kpi、basedata、third 当前都有源码引用证据，不应通过删依赖制造低耦合假象；后续应先抽指标输入快照/port，再移除对应模块依赖。
- 大量 calculator 按编号组织，可维护性依赖命名和配置，不适合再承载展示/报送流程。
- 已移除 `metric -> liquidity`：金融云 FCM_129/FCM_130 所需账户余额数据改由 `FinancialCloudAccountBalancePort` 提供，application 适配 liquidity 账户余额表。
- `metric -> basedata` 当前保留：代码级引用集中在汇率换算和科目余额标准科目字典，属于指标计算消费基础数据事实，方向合理；后续只需避免直接写入 basedata。
- `RiskMetricValueService` 的“风险策略当前值优先”语义已通过 `RiskStrategyCurrentValuePort` 输入，application 适配 `RiskControlStrategyMapper`，metric 不再直接读取 riskcontrol 持久化表。
- 月度指标存数和金融云行业维度计算所需客户、工商、行业分类数据已通过 `MetricCustomerInfoPort` 输入，application 适配 customer mapper/model；静态检查确认 `metric` 源码/resources 已无 `cn.zswltech.mithras.customer` 引用。

处理建议：

- 保持独立 Maven 模块，定位为 reporting/risk/finance 共享的指标计算域。
- 不并入 dashboard、associationreport、finance 或 riskcontrol；它们应消费指标结果或提供部分输入，而不是拥有指标计算生命周期。
- 短期不拆 `financialcloudmetric` 子域；它虽然体量很大，但与 `risk_metric` 同属指标口径和指标输出，拆分前应先收敛外域输入模型。
- 后续重点是把外域事实读取转成指标输入快照或查询接口，减少 calculator 直接依赖外域 mapper/model。
- 继续治理 fund、contract、payment、collection、assetclassify 等交易事实输入，避免 calculator 直接依赖外域 mapper/model。

### kpi

结论：`kpi` 是经营绩效/ECL/项目分配与测算域，不只是报表展示模块；短期应保持独立。

依据：

- `zswl-mithras-kpi` POM 只依赖 `api`、`foundation`，模块内没有直接 import 其他业务域，Java 层边界很干净。
- 代码层面复核显示，模块内 Java import 主要集中在 `kpi` 约 263 处、`foundation` 约 193 处、`dto` 约 125 处、`api` 约 71 处，没有直接 import 其他业务域。
- 模块内仅保存 BPMN 定义，不直接调用 flow-core；原 POM 中无源码引用的 `cn.zswltech.flow:core` 依赖已移除，流程调用留在 `application/orchestration/kpi`。
- 模块拥有绩效、项目测算、项目分配、分配权重、KPI 参数配置、拨备/ECL 配置和执行记录等写模型。
- `KpiProjGuessBaseInfoService` 通过 foundation resolver 获取客户、合同、部门、用户名称，而不是直接依赖客户/合同模块，方向较好。
- 项目分配记录通过 `KpiProjectDistributionContractPort` 查询有效合同编码到合同 ID 的映射，由 `application/orchestration/adapter/kpi/KpiProjectDistributionContractPortAdapter` 适配合同模块，说明部分合同输入已经按“kpi 定义需求、application 适配外部事实”的方向收敛。
- 外部依赖它的模块较多。只看 Java import，反向依赖主要是 `application` 约 214 处、`web` 约 15 处、`dashboard` 约 14 处、`finance` 约 12 处、`metric` 约 10 处、`budget` 约 9 处。这说明 KPI 是被多场景消费的经营指标事实源，而不是普通展示模块。

发现的问题：

- `KpiProjectDistributionMapper.xml` 直接关联 `contract_base_info`、`proj_review_base_info`，用于项目分配列表展示，属于 mapper SQL 层的合同/项目读模型耦合。
- `KpiProjGuessBaseInfoMapper.xml` 也会关联 `contract_base_info` 做项目测算列表和主办人过滤，说明 KPI 虽然 Java 层低耦合，但 SQL 读侧仍绑定合同表。
- `PerformanceMainInfoMapper.xml` 读取 `gruul_user_org_job`，属于绩效列表对组织/用户岗位读模型的耦合。
- `sql/kpi/v1_ddl.sql` 仍修改 `materials_list`、`materials_list_lib` 的文件名字段长度，属于历史脚本层对资料/文件表的耦合，不应继续扩大。
- ECL 和绩效相关 SQL 会写入 `bifrost_menu`、`bifrost_custom_tree` 等平台菜单表，这是平台初始化耦合，不是 KPI 业务依赖。
- 虽然 POM 低耦合，但外部模块大量依赖 kpi，说明 kpi 的 API/模型是跨域公共语言，需要保持稳定。
- `dashboard/metric/finance/budget -> kpi` 语义上大多合理，但当前存在直接引用 KPI mapper、model、service、enum 的场景，对外暴露面偏粗。
- `kpi` 内部同时包含绩效、项目测算、项目分配、ECL/拨备，语义略宽；但这些都属于经营指标/绩效测算上位域，暂不急拆。

处理建议：

- 保持独立 Maven 模块，定位为经营绩效与指标测算事实源。
- 不并入 dashboard/metric/budget/finance。dashboard 展示 KPI，metric 可能复用 KPI 口径，budget/finance 消费 KPI 参数和 ECL 口径，但 KPI 自己有写生命周期。
- 后续优先把外部模块对 KPI mapper/model 的直接依赖收敛为稳定查询服务、快照 DTO 或明确 port；如果只是读结果/调用应用服务，可以保留。
- `application/orchestration/kpi` 中既有跨域装配，也有可能外溢的 KPI 单域逻辑，后续可以逐步识别并回迁；但不应把预算、财务、看板、指标模块消费 KPI 参数或结果误判为合并理由。

### payment

结论：`payment` 是付款申请/实际付款/付款材料/付款保单/付款版本域，是财务大域的核心交易子域，应保持独立。

依据：

- 模块拥有 `payment_base_info`、计划付款明细、实际付款明细、付款保单、付款问卷、付款核销历史、FTP 价格/考核信息等模型。
- POM 依赖 `document`、`contract`、`customer`，说明付款申请仍需要读取合同、客户和材料信息；`payment -> third`、`payment -> workflow/flow-core` 已经移除，公开信息查询的 third 查询结果和流程查询都通过 application adapter 转成付款所需快照。
- 模块已通过 `PaymentWorkflowPort` 隔离 workflow/flow-core，通过 `PaymentPublicInfoMaterialSnapshot` 隔离 document 的 `MaterialsList` 持久化模型。
- 模块源码约 128 个 Java 文件，拥有付款申请、计划付款明细、实际付款明细、付款保单、付款问卷、公开信息查询、FTP 考核信息、付款版本和材料版本等模型。
- 已移除 `payment -> projectprocess`：原先只为判断项目类型引入项目枚举，现改为基于合同快照中的稳定项目类型编码判断，避免付款域仅因常量比较依赖项目过程域。
- 已将 `payment` 资源目录中的项目评审会议纪要授信到期日判断权限脚本迁回 `projectprocess`。该接口虽然被付款页面调用，但 URL、API 和 controller 都属于项目评审会议纪要能力。
- `PaymentPlanedDetailService` 只围绕付款申请金额、明细累计校验和付款明细持久化，属于单域规则。
- 外部大量依赖 `payment`：报送、指标、风控、看板、预算、资产分类、征信报送等都需要付款事实，这说明 payment 是事实源而不是展示模块。按 Java/POM/Mapper/SQL 粗略搜索，主要消费方包括 `application` 约 456 处、`web` 约 271 处、`report` 约 269 处、`dashboard` 约 54 处、`kpi` 约 51 处、`creditreport` 约 36 处、`contract` 约 36 处、`metric` 约 35 处、`policy` 约 31 处、`collection` 约 19 处、`finance` 约 17 处、`riskcontrol` 约 16 处。

发现的问题：

- `payment` mapper XML 直接读 `margin_*`、`warranty_*`、`collection_base_info`、`contract_receipt` 等外域表，存在读模型便利带来的表所有权泄漏。
- 付款保单与 `policy` 的正式保单生命周期交织，`PaymentPolicyInfoService` 中同步正式保单的逻辑应明确为跨域同步，而不是两个域互相写细节。
- workflow/flow-core 已不再直接进入 payment 模块；document 的普通材料查询已通过 `PaymentPublicInfoMaterialSnapshot` / `FileListRSP` 收窄，但材料版本化、付款材料转换等历史耦合仍需后续单独评估。
- 已移除 `payment -> third`：公开信息查询页面需要展示 providence 外部查询落库结果，现在由 `PaymentPublicInfoSupportPort#getLatestOuterQuerySnapshot` 暴露支付域所需的时间和行结果快照，third 的 `OuterInfoRecord` 查询留在 application adapter。
- `sql/风控应用/public_info_config.sql` 虽然目录名带“风控应用”，但更新的是 payment 拥有的 `public_info_config` 公开信息配置表；表模型、mapper、service 和公开信息查询记录当前都在 payment，短期不迁移。
- `PublicInfoQueryService` 已不再直接注入合同 mapper、客户 mapper 或合同参与方 service；公开信息合同/客户上下文与合同参与方集合已通过付款自有快照收口。
- 付款审批文档渲染 `PaymentApprovalBLRender`、`PaymentApprovalZLRender` 已通过 `PaymentApprovalRenderSupportPort` / `PaymentApprovalRenderSnapshot` 隔离合同、客户、机构、人员读取；流程查询继续通过 `PaymentWorkflowPort` 收口。
- `PaymentActualDetailOperationAuthChecker`、`PaymentActualDetailRemoveAuthChecker` 已不再直接使用 workflow `ProcessService` 和流程模型枚举。

处理建议：

- 保持独立 Maven 模块，不先并入 `finance`。
- 不并入 `collection`、`contract` 或 `policy`：收款方向、合同生命周期、保单生命周期都与付款不同，付款只是消费或关联这些事实。
- 优先治理 payment 对 margin/collection/contract/policy 的直接表读取和模型写入，区分“付款事实”与“付款列表展示/跨域同步”。
- 公开信息查询的普通合同/客户/参与方/材料/third/workflow 读取，以及付款审批文档渲染的合同/客户输入，均已完成一轮快照化；下一步不要继续在这里兜圈子，优先处理 mapper XML 表级读模型和 POM 依赖必要性。
- 付款作为 finance 大域的核心子域，可以被报送、指标、预算、风控读取，但外部不应直接写 payment mapper/model。

### collection

结论：`collection` 是应收/收款计划/收款核销/逾期域，是财务大域核心交易子域，应保持独立。

依据：

- 模块拥有 `collection_base_info`、实际收款记录、收款核销记录、逾期记录、账单管理和催收/邮件 job 等模型。
- `CollectionBaseInfoService` 管理租金/手续费/名义价/保证金等现金流项目的收款编号、逾期判断、合同维度收款计划和剩余本金解析。
- 它实现 `ContractRemainingPrincipalResolver`、`CollectionRentActualReceiptStatusResolver`，说明合同域会把收款事实作为外部解析能力使用。
- POM 当前只保留 `payment`、`contract` 两个业务域依赖；`customer` 历史依赖已清理，客户/联系人输入通过收款自有快照或读模型 SQL 承接。
- 代码层复核显示，模块约 87 个 Java 文件，Java import 主要集中在 `collection`、`foundation`、`dto`，同时仍有 `contract` 引用和少量 `payment` 引用；当前源码 import 分布约为 `collection` 122 处、`contract` 15 处、`payment` 6 处。源码和 resources 已无 `customer` 模块类型引用，POM 中的 `customer` 历史依赖已移除；POM 对 `contract`、`payment` 的直接依赖与实际代码一致，不是传递依赖假象。
- `ContractCollectionPaymentService` 同时读取合同、付款和收款事实，说明模块内包含合同收付台账视角；这加强了 collection 与 contract/payment 的业务耦合，但仍属于收款域围绕应收事实的视图。
- 已移除 `collection -> workflow`：收款域事件和苍穹同步接口只暴露流程模型编码，`ProcessModelTypeEnum` 的解析与流程判断留在 application 编排层。
- Mapper XML 层面，`CollectionBaseInfoMapper.xml` 直接 join `contract_base_info`、`contract_tenantry`、`contract_guarantor`、`client`，并读取 `contract_rent_actual` 剩余本金；这是收款台账、逾期展示和合同维度汇总保留的表级耦合。
- 反向依赖很广，按 Java/POM/Mapper/SQL 粗略搜索，主要消费方包括 `application` 约 298 处、`web` 约 139 处、`dashboard` 约 94 处、`contract` 约 57 处、`report` 约 56 处、`afterlease` 约 49 处、`finance` 约 48 处，另有 `riskcontrol`、`payment`、`metric`、`fund`、`margin`、`liquidity` 等，说明 collection 是核心事实源。
- 模块内已有少量 port：`CollectionNotificationPort` 隔离通知发送，`ContractCollectionMarginPort` 隔离保证金输入，`CollectionPenaltyInterestJobSupportPort` 承接罚息任务支撑能力；`CollectionMailJobSupportPort` 已收窄为 `CollectionMailContractInfo`、`CollectionMailClientInfo`、`CollectionMailContactInfo` 等收款自有邮件快照，不再在租金催收/到期提醒 job 中暴露 `ContractBaseInfo`、`Client`、`CorpContactInfo` 等外域持久化模型；`CollectionBaseInfoMsgJobServiceImpl` 的租金到期站内信已改为通过 `CollectionRentDueContractInfoPort` 获取收款自有合同提醒快照；`CollectionBaseInfoService.detail` 已改为通过 `CollectionDetailContractInfoPort` 获取详情页合同摘要。合同版本实体、合同状态翻译和版本处理器查询迁到 application adapter。逾期历史汇总查询改为 collection 自有 `CollectionOverdueHistoryQueryService` / `CollectionOverdueHistorySnapshot`，contract 所需的 `CollectionOverdueHistoryResolver` 由 application adapter 转换，mapper XML 不再返回 contract 的 `OverdueCollection`。
- `ContractCollectionPaymentService` 当前仍是合同、付款、收款、保证金的收付台账读模型聚合；本轮复核确认它直接使用 `ContractBaseInfoMapper`、`ContractBaseInfoLibHandler`、`PaymentBaseInfoMapper`、`ContractStatus`、`PaymentStatusEnum` 和 `PaymentWriteOffStatus`，这是 collection 与 contract/payment 的真实业务耦合，不是可直接删除的 POM 噪声；此前已把只属于收付台账页面的 `CashSelectTypeEnum`、`RecordSourceEnum` 从 `contract.enums.contractcp` 本地化到 collection，避免 collection 为页面筛选/来源展示协议依赖 contract 包。
- 原 `collection.convert.contract.ContractDeductRentInfoConverter` 只被 application 的合同押金 facade 使用，且转换的是 contract 押金/退租模型；已迁到 `application/orchestration/facade/contract`，collection 不再因为这个编排层转换器额外引用 contract 模型。
- `CollectionRecordInfo` 中仅用于 Javadoc 的 `payment.enums.WriteOffTypeEnum` import 已移除，避免形成虚假的 Java 依赖信号；字段仍保存既有字符串协议值。
- 本轮继续清理一处历史老包引用：`CollectionBaseInfoService` 中 `org.apache.commons.lang.ObjectUtils` 已替换为 JDK `Objects.equals`，避免 collection 继续依赖老 commons-lang 传递包；`commons-lang3` 仍由 `ContractCollectionPaymentService` 的 `StringUtils` 使用，POM 保留。`mvn -pl zswl-mithras-collection -am -DskipTests compile` 通过，reactor 12 个模块 SUCCESS。
- `collection/adapter` 目录当前没有实际 Java 文件，说明本模块主要问题不是 adapter 命名，而是 application service 和 Mapper SQL 中的跨域事实读取。

发现的问题：

- `collection` 对 contract 的依赖较深，直接使用合同租金表、合同版本、合同回款状态解析等模型；这部分语义上相关，但会让合同/收款边界变厚。
- `CollectionBaseInfoService` 仍通过合同侧 resolver 接口暴露剩余本金和租金实收状态，也仍在首期利息/已收本金查询中读取合同借据和付款事实；`CollectionOverdueRecordInfoService` 通过 `ContractBaseInfoMapper` 和 `ContractLeasePriceLibMapper` 获取业务类型与最新罚息利率；`ContractCollectionPaymentService` 仍直接注入合同/付款 mapper 或合同版本处理器。后续应逐步收敛成 collection 自己需要的合同快照、付款快照或查询接口。
- `collection -> customer` 的 Java/POM 直接依赖已清理；租金催收和到期提醒所需客户/联系人信息通过 `CollectionMailJobSupportPort` 返回 `CollectionMailClientInfo`、`CollectionMailContactInfo` 等收款自有快照，客户表 join 仅残留在收款台账 Mapper XML 的读模型里。
- 部分未启用或注释掉的服务类仍存在，例如 `CollectionWriteOffRecordService` 目前主体被注释，后续可以清理或恢复。
- 报送、风险、看板、流动性、预算、租后、财务等模块大量读取 collection，属于事实源被消费，但需要避免外部直接写收款表或直接依赖 collection 持久化模型。

处理建议：

- 保持独立 Maven 模块，不并入 `payment` 或 `contract`。付款、收款和合同履约生命周期不同，虽然收付台账需要聚合三者事实，但直接物理合并容易形成财务交易大泥球。
- 可在 finance 大域内治理 payment/collection 的共同语言，例如现金流项目、核销状态、收付状态，但不要共享持久化模型。
- 后续优先梳理 collection 与 contract/payment 的接口：合同状态、合同版本、租金计划、罚息利率、付款记录可以分阶段改为 collection 自有快照或查询 port；在此之前不要删除 `collection -> contract/payment` POM 依赖。
- 对外部读 collection 事实的场景，应逐步收敛为明确查询服务、port 或快照输入；report/dashboard 等读侧聚合可以短期保留表级读取，但要标注为读模型依赖，不应反向推动 collection 合并进 reporting 或 finance。

### fund

结论：`fund` 是融资资金域，包含资金机构、授信、融资、还本付息、直接融资和财务系统推送，代码规模和生命周期都足以独立。

依据：

- `zswl-mithras-fund` 自身 POM 只依赖 `api`、`foundation`，代码量约 402 个 Java 文件，说明它通过 application 装配外部依赖，模块内边界相对干净。
- 代码层面复核显示，Java import 只指向 `fund`、`foundation`、`api`、`dto`，没有直接 import `contract`、`payment`、`collection`、`finance`、`capital`、`liquidity`、`workflow`、`third`、`credit`、`document` 等业务模块；POM 的业务依赖保持低耦合。import 分布约为 `fund` 504 处、`foundation` 383 处、`dto` 217 处、`api` 121 处。
- POM 依赖逐项复核后，除直接 `org.mybatis:mybatis` 外，当前没有发现可以安全删除的依赖：MyBatis-Plus 用于 mapper/service/model，`@Mapper`/`@Param` 注解由 MyBatis-Plus 传递依赖覆盖；Hutool/Commons/Fastjson 用于服务、转换和版本逻辑，POI/Servlet 用于导入导出和下载，Spring/Annotation/Validation/Swagger 用于组件、事务、Web 和接口模型，MapStruct 用于 converter，XXL Job 用于 `FundReceiptRepayStateJob`、`FundFinancingJob`、`FundOrganizationJob`。直接 `org.mybatis:mybatis` 已移除。
- 模块拥有资金机构、资金授信、担保机构、间接融资、直接融资、还本付息、融资计划、实际还款、质押、费用、版本和材料等大量模型。
- `FinancialSystemContractService` 通过 `FinancialSystemDataPort` 获取待推送融资数据、机构、计划、实际还款等，体现了“fund 定义需求，外部适配数据”的方向。
- 外部依赖 fund 的主要是 `application`、`metric`、`liquidity`、`dashboard`、`finance`，多为融资事实读取、指标、流动性、看板和财务口径消费。只看 Java import/POM，主要反向依赖约为 `application` 984 处、`metric` 65 处、`web` 42 处、`liquidity` 41 处、`dashboard` 5 处、`finance` 4 处。
- 如果把 Mapper/SQL 中的 fund 表名也计入，读侧还包括 `web`、`dashboard`、`ftp`、`liquidity`、`filingmaterials`、`budget`、`rating`、`finance`、`contract`、`projectprocess`、`creditreport` 等；这说明 fund 是重要事实源，但不能把表名读侧引用等同于代码级依赖。
- Mapper XML 层面存在少量跨域读模型：`PropertyMapper.xml` 读取 `payment_actual_detail_unconfirmed`、`payment_base_info`、`payment_actual_detail`、`contract_base_info`、`client` 以组装直融资产信息；`FundFinancingBaseInfoMapper.xml` 返回 `dto.capital` 下的资金流水列表 DTO。
- Java import 层还可见少量 `dto.dashboard`、`dto.liquiditymanage`、`dto.capital` 等 API DTO 复用。它们不形成 Maven 业务依赖，但说明 fund 的查询契约仍被读侧/资金流水视图口径影响。
- 资源 SQL 中曾夹带外域表变更，例如 `collection_*`、`asset_classify_*`、`finance_flow_write_off_detail`，这是历史迁移脚本放置位置不纯，不代表 fund 应拥有这些表。当前已将 `collection_overdue_history.client_id` 脚本迁回 `collection`，将 `asset_classify_client*.award_ratio` 脚本迁回 `assetclassify`，将 `finance_flow_write_off_detail.deleted` 脚本迁回 `capital`。
- 模块内已有 `FinancialSystemDataPort`、`FundProcessPrepareMaterialPort`、`FundReceiptRepayStateUpdatePort`、`FundReceiptFlowDetailAmountPort`、`FundOrganization*Port` 等接口，用来表达财务系统推送、流程材料、还本付息状态、流水金额和机构信息等外部协作需求。

发现的问题：

- fund 内部语义很宽，间融、直融、还本付息、资金授信、财务共享推送都在一个模块；但它们仍属于融资资金上位域。
- `application/orchestration/fund` 中仍有大量核心实现，尤其是资金授信与 credit 额度占用、融资流程、材料、财务系统推送的跨域编排。
- fund 与 credit 的额度关系很关键：fund 可以消费 `CreditLimitManagerService`，但不应直接理解 credit 持久化表。
- `liquidity`、`dashboard`、`metric` 等读侧模块直接使用 fund 持久化模型或 fund 表，短期可接受为读模型依赖，长期应收敛为融资查询服务、快照 DTO 或 port。

处理建议：

- 保持独立 Maven 模块，作为 finance 大域中的融资资金事实源。
- 合并判断：`fund` 属于 finance 大域，但不是当前物理合并对象。它有独立写生命周期、独立 BPMN、独立融资/还本付息/直融表族和较大的代码规模；合并进 `finance` 会把融资事实、财务经营聚合、资金流水核销和流动性分析揉成新的大模块。
- 不并入 `finance` 聚合模块；`finance` 当前更像经营财务/利润/税费/账龄聚合。
- 不并入 `capital`、`liquidity` 或 `ftp`：capital 管流水核销，liquidity 管流动性风险指标，ftp 管内部资金转移定价，三者都应消费 fund 事实而不是拥有融资生命周期。
- 后续优先回看 `application/orchestration/fund`，把纯 fund 单域逻辑回迁，把 credit/合同/流程/材料/财务系统装配留在 application。
- 清理 fund 资源目录里的外域 DDL，把外域表变更迁回对应模块；同时把 `dto.capital` 这类跨域 DTO 返回值改为 fund 自有查询 DTO。
- 对 `liquidity`、`dashboard`、`metric` 等读侧模块，后续优先改成稳定查询服务、快照 DTO 或明确 port，避免它们继续直接理解 fund 持久化模型或表结构。

### finance

结论：`finance` 当前不是“所有财务模块的总模块”，更像财务经营核算/利润/税费/账龄/月度管理/财务项目分配聚合域。

依据：

- 模块包含账龄、项目利润、利润测算、印花税、财务第三方、月度管理、资金日报成本、财务项目分配等多块能力。
- POM 依赖 `collection`、`contract`、`customer`、`third`、`metric`、`basedata`、`workflow`、`fund`、`kpi`、`ftp`、`payment`、`assetclassify`，实际是财务经营聚合中心。
- 代码层面复核显示，模块有 220 个 Java 文件，当前源码根包已统一到 `cn.zswltech.mithras.finance`，内部一层包包括 `monthly`、`projectdistribution`、`view`、`service`、`mapper`、`adapter` 等。Java import 除 `finance` 自身外，还直接引用 `contract`、`collection`、`workflow`、`kpi`、`metric`、`third`、`payment`、`ftp`、`customer`、`fund`、`basedata`、`assetclassify` 等模块；POM 里的业务依赖基本都是真实依赖。import 分布约为 `finance` 308 处、`foundation` 224 处、`dto` 131 处、`api` 66 处、`contract` 30 处、`third` 21 处、`collection` 14 处、`workflow` 13 处、`kpi` 12 处、`metric` 8 处。
- 已补齐 `finance -> assetclassify` 的显式 POM 依赖：`ProfitCalculateResultService` 实际使用 `AssetClassifyClientAuxiliaryLibService` 和 `AssetClassifyClientAuxiliaryLib` 读取风险分类版本数据，不能继续依赖 `metric -> assetclassify` 的传递依赖。
- 代码中曾同时出现 `finance`、`monthly`、`financeprojectdistribution` 多个根包，说明历史上多个财务经营能力被放进同一个 Maven 模块；当前已把 `financeprojectdistribution` 实现包收敛为 `finance.projectdistribution`，并把 `monthly` 实现包收敛为 `finance.monthly`。`api.monthly`、`dto.monthly` 作为对外契约包保持不变。
- 外部依赖 finance 相对集中，按 Java/POM/Mapper/SQL 粗略搜索，主要消费方包括 `application` 约 145 处、`web` 约 130 处、`api` 约 38 处、`ftp` 约 28 处、`third` 约 20 处、`assetclassify` 约 16 处、`fund` 约 14 处、`budget` 约 11 处；不像 payment/collection/fund 那样被大量交易/报送模块直接消费。
- Mapper XML 和 SQL 主要围绕 `finance_*`、`monthly_*`、利润测算、账龄、逾期报送表，但会 join `contract_base_info`、`client`、`corp_commerce_info_lib` 等外域读模型。
- 现有 `ProfitCalculateSupportPort`、`FinanceAccountAgeSupportPort`、`FinanceProjectDistributionSupportPort` 表明部分跨域输入已经开始以 port 表达；但前两个 port 仍暴露 `BillManagement`、`PaymentBaseInfo`、`CollectionBaseInfo`、`FtpInterestBaseInfo` 等外域持久化模型。
- 原 `finance/adapter/metric` 中 `JinKongMonthlyReportPortAdapter`、`RiskMetricFactorRefreshClientAdapter` 由 finance 实现 metric 侧 port，并反向调用 metric job service，方向上更像 application 编排。本轮已迁到 `application/orchestration/adapter/metric`，finance 不再直接承载这两个 metric 接线类。

发现的问题：

- `finance -> dashboard` 已移除；原先依赖主要来自看板绩效接口和资金看板查询对象复用。
- `finance` 内部根包已完成一轮统一，`monthly` 和 `financeprojectdistribution` 两个历史并列实现根包已分别收敛到 `finance.monthly`、`finance.projectdistribution`。
- `finance.view`、`DashboardFv*` 和 `dto.dashboard` 复用说明 finance 仍承载资金/财务看板快照语义；这不是 dashboard 模块直接依赖，但属于 finance/reporting 口径混合的后续治理点。
- 由于 finance 依赖 payment/collection/fund/ftp/kpi/metric，它不适合作为这些模块的物理合并目标；否则会成为新的大而全财务模块。
- 利润测算直接使用合同、付款、收款、FTP、KPI、风险分类版本模型，当前是财务口径算法的真实输入，但会让 finance 对交易事实和风险分类事实耦合较深。
- `finance/adapter/metric` 暴露出的横向聚合模块互相适配问题已先迁出到 application。剩余问题是 `JinKongMonthlyReportService` 仍直接读写 metric 因子服务和模型，后续应改成更单向的 metric port 或快照输入。
- `metric.application.third.yunhu.YunHuMonthlyReportService` 全仓无外部引用，且与 finance 的 `JinKongMonthlyReportService` 重复但功能更旧：不处理组织编码、合并报表 `RiskMetricFactorMerge`、财务原始余额、辅助核算表和系统配置。本轮已删除该旧实现；这不等同于清掉 `finance -> metric`，因为 finance 现有服务仍直接写 metric 因子模型和服务。

处理建议：

- 保持独立 Maven 模块，但定位为财务经营/核算聚合子域，不要把整个 finance 大域都塞进去。
- 不把 payment、collection、fund、ftp、budget、capital、margin、liquidity 直接并入 `finance`。
- 后续不再新增与 `finance` 并列的财务实现根包；利润测算、账龄、逾期报送和资金财务看板快照等高耦合服务优先抽出财务口径查询 DTO、快照或 port，避免长期直接依赖外域 mapper/model。
- 对利润测算、账龄、逾期报送等高耦合服务，后续优先抽出财务口径查询 DTO、快照或 port，避免长期直接依赖外域 mapper/model。
- 对金控/月报同步，后续不要用已删除的旧 `YunHuMonthlyReportService` 替换 finance 实现；正确方向是让 metric 提供稳定的因子写入快照/port，finance 只输出从云湖/金控报表转换出的指标因子数据。

### ftp

结论：`ftp` 是内部资金转移定价/收益分解域，old/new 两套短期应并存，不应并入 `finance` 或 `budget`。

依据：

- 模块有 `oldftp` 和 `newftp` 两套模型、mapper、service、controller、版本、状态机、FMS 计算和 job。
- POM 现在只保留对 `basedata` 的直接业务/基础数据依赖；客户主体分类、关联方等客户事实已由 `application` adapter 读取客户域后传入 FTP。`basedata` 依赖有真实源码证据：LPR mapper/service/model 与日期工具在新版 FTP 配置、草稿和版本处理中被使用。
- 代码层面复核显示，模块当前一层包主要是 `common`、`convert`、`oldftp`、`newftp`。FTP 定价使用的 FTP 行业分类、项目管理层级、项目分类和地区分类已经收敛到 `ftp.common.enums`，项目过程传入的分类值按稳定字符串协议映射为 FTP 定价维度。
- 资源层面主要是自身 `ftp_income_*` 表、FTP BPMN、模板和 `oldftp_guidance_tables.sql`；`FTP计息.sql` 中有 `bifrost_menu`/`bifrost_function` 菜单初始化，属于系统菜单资源耦合，不是业务域合并信号。
- 依赖必要性复核显示，OSS 模板预览、QLExpress 公式计算、EasyExcel/POI 导入导出、Hutool、XXL Job、Gruul、Spring、validation、servlet、Swagger、commons、fastjson 等都有源码使用点；直接 `org.mybatis:mybatis` 未发现独立必要性，已由 MyBatis-Plus starter 覆盖并移除。
- `NewFtpBaseInfoController` 管理新 FTP 主表、月度指导价、季度基准定价、描述文本、版本差异和状态机，具备独立审批/版本生命周期。
- 外部依赖它的主要是 `application`、`finance`、`budget`，说明 FTP 结果被财务经营和预算消费。

发现的问题：

- old/new 并存导致模块内包和模型数量较多，但这是业务迁移期的明确事实，不适合为了“干净”强删 oldftp。
- README 已按当前代码校准：`interest`、`income`、`common`、`flow` 目前还不是 `ftp` 模块内真实一层包；流程动态表单和流程结束 handler 已在 `application/orchestration/workflow/.../ftp`，计息/收益仍在 `oldftp` 包内。
- 已移除 `ftp -> customer`：FTP 内部使用自有 `EnterpriseTypeEnum` 表达客户主体定价分类，不再因为复用客户枚举或行业模型依赖 customer 模块。
- 已移除 `ftp -> projectprocess`：原 projectprocess 下的 FTP 定价维度枚举已在 FTP 内本地化，两个老版版本对比/handler 中无效的 projectprocess import 也已删除。
- `finance` 对 ftp 的依赖应是读取定价结果/利息拆分，不应反向写 FTP 定价生命周期。
- 已移除 `budget -> ftp`：预算参数只需要期限区间编码，调用方在编排层把 FTP 期限枚举转换为编码传入预算域。
- FTP 模块内部分 controller 承担了较多应用逻辑，后续可逐步沉到 service/application 层。
- `FTP计息.sql` 仍包含对 `payment_base_info`、`payment_base_info_lib`、`client`、`fund_direct_financing_base_info`、`fund_financing_plan`、`fund_financing_plan_lib`、`fund_direct_financing_product_detail` 的字段变更，这是历史资源脚本层耦合。它说明 FTP 结果会落到付款/客户/资金事实表或被这些域使用，但不构成把 FTP 合并进 payment/customer/fund 的理由。

处理建议：

- 保持独立 Maven 模块，继续保留 `oldftp` 和 `newftp`。
- 合并判断：`ftp` 属于 finance 大域，但不建议物理并入 `finance`、`budget`、`fund` 或 `capital`。FTP 拥有独立定价规则、指导价审批、版本库、取价服务、计息/收益结果和模板资源；其他模块消费 FTP 价格/收益结果，不应拥有 FTP 定价生命周期。
- 不并入 budget；budget 使用 FTP 作为预算测算输入，但不拥有 FTP 定价规则。
- 后续重点是明确 old/new 的使用入口和迁移策略，并避免外部模块直接操作 FTP draft/lib 细节。
- 客户、合同、付款、资金等外部事实继续通过 port/application adapter 输入，不再把外域模型或枚举直接暴露给 FTP 定价核心。

### budget

结论：`budget` 是预算计划/预算考核/ECL 预测域，和 finance/kpi/ftp 相邻但有独立计划与考核生命周期。

依据：

- 模块包含预算计划、付款预算、成本预算、利润预算、周报、预算考核、效益考核、预算执行、ECL 预测配置和执行记录。
- 模块有约 155 个 Java 文件，包结构集中在 `cn.zswltech.mithras.budget` 单根包下，没有 finance 那种多个业务根包并列的问题。
- POM 直接业务依赖已收敛为 0，当前只依赖 `api`、`foundation` 和框架能力；合同归属事实、付款事实、收款事实、财务风险/利润事实、项目立项/定价事实和 KPI 年度绩效目标不再通过直接依赖 contract/payment/collection/finance/projectprocess mapper/model 或 KPI service/model 获取，而是分别通过预算自有 `BudgetContractFactPort`、`BudgetPaymentFactPort`、`BudgetCollectionFactPort`、`BudgetFinanceFactPort`、`BudgetProjectFactPort`、`BudgetKpiFactPort` 由 application adapter 装配。
- 静态搜索确认 `zswl-mithras-budget/src/main/java`、`src/main/resources` 和 `pom.xml` 中已无 `cn.zswltech.mithras.contract`、`zswl-mithras-contract`、`ContractBaseInfo`、`ContractBaseInfoService`、`cn.zswltech.mithras.payment`、`zswl-mithras-payment`、`PaymentActualDetail`、`PaymentActualDetailMapper` 或旧 `ContractPayInfoDTO` 残留。
- 本轮按源码 import 复核，budget 已无 `cn.zswltech.mithras.kpi` 或 `zswl-mithras-kpi` 残留；预算付款现金流已改为预算自有导入模型，预算执行所需 KPI 年度绩效目标已改为 `BudgetKpiFactPort` 输入，预算预测配置校验使用 `BudgetEclConfigEnum` 和 `BudgetEcl*BO` 自有 JSON 结构，预算考核所需项目立项/定价事实已改为 `BudgetProjectFactPort` 输入，预算效益考核所需财务项目利润和风险辅助数据已改为 `BudgetFinanceFactPort` 输入。
- 预算域已经自持 FTP 行业分类口径：`BudgetFtpIndustryCategory` 保留历史编码，参数配置服务接收预算分类编码或预算本域枚举；风险准备金/FTP 参数读取不再复用 `projectprocess` 的 FTP 行业分类枚举。
- POM 框架依赖也有源码证据：MyBatis-Plus 用于 BaseMapper、Wrappers、ServiceImpl 和分页，Hutool 用于工具类，Spring Web 用于 controller，XXL Job 用于 `BudgetPlanPayWeeklyJob`。
- `BudgetExamineBenefitService` 会读取预算自有财务事实 port、预算付款事实、组织/部门信息和财务风险辅助数据，用于预算效益考核。
- `BudgetExamineBudgetExecuteService` 会读取预算自有 KPI 年度目标 port、预算自有项目事实 port、预算付款事实和收款剩余本金，体现预算作为经营考核聚合域的输入需求。
- 预算考核流程启动、流程结束类型判断和通过后抄送人力负责人已通过 `BudgetExamineWorkflowPort` 收敛到 application adapter，budget 源码和 POM 不再直接依赖 workflow/flow-core。
- 静态搜索确认 `budget` 源码/POM 不再直接引用 workflow/flow-core；`collection` 也不再作为 POM/source 依赖，收款剩余本金和合同上迁判断已经通过 `BudgetCollectionFactPort` 表达；`payment` 也不再作为 POM/source 依赖，预算收益测算和预算考核执行所需付款事实已经通过 `BudgetPaymentFactPort` 表达；`contract` 也不再作为 POM/source 依赖，预算需要的合同业务部门和项目评审归属已经通过 `BudgetContractFactPort` 表达。
- 资源层主要围绕 `budget_*`、ECL 预测与预算考核表，Mapper XML 大多只读写预算自有表；历史 SQL 中夹带 `finance_bcm_balance_mf` 建表，属于脚本归属不纯，不代表 budget 应拥有 finance 表。
- 外部依赖 budget 的模块很少，按 Java/POM/Mapper/SQL 粗略搜索，主要消费方是 `web` 约 181 处、`application` 约 119 处、`rating` 约 15 处，说明它更像自足的业务域，而不是基础公共能力或交易事实源。

发现的问题：

- budget 已不再直接依赖 finance；预算考核仍消费财务项目利润和风险辅助事实，但这些事实已通过 `BudgetFinanceFactPort` 和 application adapter 输入，避免预算计算理解 finance 持久化细节。
- budget 不再直接引用 `ContractBaseInfoService`、`ContractBaseInfo`；合同事实已按 `BudgetCollectionFactPort` 的模式收敛为 `BudgetContractFactPort`。
- budget 不再直接引用 `FinanceProjectProfitDetailMapper`、`FinanceSubjectBalanceAssistService`、`FinanceBcmBalanceMfService` 等 finance 外域服务/mapper，也不再在预算考核执行服务中直接引用 `ProjEstablishBaseInfoMapper`、`ProjPricingBaseInfoMapper`、`ProjPricingBaseInfo` 等 projectprocess mapper/model；预算执行年度绩效目标也已不再直接引用 `KpiPerformanceBaseInfoService`、`PerformanceBaseInfo` 或 KPI 枚举，ECL 预测配置也不再直接引用 KPI 的 ECL BO/枚举。
- 模块同时包含预算计划和 ECL 预测，语义略宽；但两者都属于经营计划/风险准备测算，短期可以同域治理。
- budget 曾直接依赖 workflow/flow-core 处理流程模型、流程状态、启动流程和流程结束事件；现在预算域持有自己的 `BudgetApprovalStatusEnum`，流程模型、流程启动、结束类型判断和抄送都由 application adapter 承接，避免把流程技术细节扩散到预算域。
- budget 曾直接依赖 collection 读取合同上迁判断和部门剩余本金；现在由 `BudgetCollectionFactPort` 声明预算所需收款事实，application adapter 复用 collection 现有服务/Mapper 实现。
- budget 曾直接依赖 payment 读取合同付款计划和付款实际明细；现在由 `BudgetPaymentFactPort` 声明预算所需合同付款事实和付款实缴事实，application adapter 复用 payment/contract/collection 相关表形成跨域读模型。
- budget 曾直接依赖 contract 读取合同业务部门和项目评审归属；现在由 `BudgetContractFactPort` 声明预算所需合同事实，application adapter 复用 contract 服务实现。
- budget 曾复用 `projectprocess` 的 FTP 行业分类枚举表达预算参数配置口径；现在已收敛为 `BudgetFtpIndustryCategory`。预算考核中的项目立项数量和项目定价 FTP 分类也已收敛为 `BudgetProjectFactPort`，付款预算现金流导入也已改用预算自有 `BudgetCashFlowExcelImporter` 和 `BudgetCashFlowExcelModel`，因此 budget 源码/resources/POM 已无 projectprocess 直接引用。

处理建议：

- 保持独立 Maven 模块，不并入 finance。
- 预算对 projectprocess/kpi/finance/contract/payment/collection 的事实需求应被视为计划测算输入；其中 finance、contract、payment、collection、workflow、projectprocess 项目事实、项目现金流导入格式、KPI 年度目标和 KPI 全局 ECL 配置都已通过 port/application adapter、预算自有导入模型或预算自有预测配置结构隔离。
- 预算审批流继续通过 `BudgetExamineWorkflowPort` 接入 application 的 flow-core adapter；预算表里的审批状态保持为预算域状态，不直接引用 workflow/flow-core 枚举或 DTO。
- 后续优先检查 budget 是否直接写外域表；如果只是读事实并写预算结果，可以保留现状。
- 下一步重点不再是删 POM，而是检查资源脚本归属和 application 侧预算 adapter 是否还能继续简化；不要为了追求更纯，把 KPI 全局 ECL 写模型硬并入 budget。

### customer

结论：`customer` 是客户主数据域，是最不应该合并的核心事实源之一。

依据：

- `zswl-mithras-customer` POM 只依赖 `api`、`foundation`，模块自身没有业务域 import，边界非常干净。
- 代码层面复核显示，模块内 Java import 主要集中在 `customer` 约 684 处、`dto` 约 361 处、`foundation` 约 294 处、`api` 约 109 处，没有直接 import 其他业务域。
- 模块拥有客户主表、法人/自然人信息、工商信息、股东、联系人、地址、银行账户、客户权限、外部客户、沙盘记录、客户移动端拜访、版本和材料访问等完整生命周期。
- `ClientChangeCheckService` 只在客户模型、客户版本和变更字段之间判断是否需要审批，属于客户单域规则。
- 模块内已有若干 port 用于隔离外部事实或横向能力，例如 `ClientUserDeptPort`、`ClientAuthorityDataPort`、`ClientDataSaveCheckPort`、`CustomerDictionaryPort`、`CorpCommerceInfoSupportPort`，由 application 侧 adapter 适配用户部门、权限、字典、第三方/外部支持等能力。
- 几乎所有交易、风险、报送、看板模块都会读取客户事实。当前粗略反向搜索显示，主要来自 `application`、`web`、`riskcontrol`、`report`、`contract`、`rating`、`dashboard`、`finance` 等模块。这说明 customer 是上游事实源。

发现的问题：

- customer 模块内部语义较宽，包含外部客户、沙盘记录、移动拜访等子能力，但这些仍围绕客户生命周期展开。
- `ClientMapper.xml` 中 `pageClientBasicInfo` 原先直接 join `asset_classify_client`、`asset_classify`、`collection_base_info`，这是客户看板读模型混入 customer mapper。当前已将资产分类和最新租金日期筛选移到 application 编排层，由 application 分别向 assetclassify/collection 查询候选客户，再调用 customer 基础客户查询；customer mapper 现在只保留客户与工商基础事实。
- 原 `customer/dashboard/query` 下的看板查询对象已改为 customer-neutral 的 `ClientBasicPageQuery`，原 `DashboardClientBasicDTO` 已改为 `ClientBasicInfoDTO`。customer 对外暴露“客户基础分页查询”，DTO 只保留客户与工商基础事实；资产分类、结清日期、评级等看板/外域展示字段留在 application/dashboard 编排侧。
- `application/monitor`、`ClientViewByRiskControlJobService`、`ClientFocusOpinionSyncService` 以及 `sql/风控应用`、`sql/风控策略` 体现客户与风控监控的历史耦合。客户可以保存风险行业分类等客户属性，但风控预警、舆情、监控页面规则应归 riskcontrol 或 application 编排。
- 客户模块 resources 中仍有客户修改/客户移交流程 BPMN，以及风控应用/风控策略相关 SQL。它们目前围绕客户生命周期和客户属性演进，短期归 customer 可接受；长期应避免 customer 承担风险监控页面和流程编排规则。
- `sql/风控策略/customer.sql` 修改的是客户工商信息主表和版本表，短期归 customer 维护；但字段口径来自风险分类，后续应由 riskcontrol/application 输入分类结果。
- `sql/风控应用/customer_monitoring.sql` 注册 `/clientMonitor/*` 菜单权限点，实际实现跨 customer 和 riskcontrol：application 负责 controller/service 编排，customer 提供客户列表/统计基础数据，riskcontrol 提供预警和舆情详情。它是后续应用层编排或风险监控入口治理候选，不应继续扩大 customer 单域职责。
- 已移除 customer mapper 对风险监控表的直接读取：`ClientMapper.xml#listMonitorClient` 不再 join `risk_control_opinion_monitor` 或子查询 `risk_control_warn_monitor`，客户监控列表由 application 先从 riskcontrol 获取风险客户和舆情数量，再调用 customer 查询授权客户列表并组装结果。
- `application/project/ProjectService` 只查询 `tmp_client_project` 判断客户是否有关联项目，仍是客户侧临时关系表，不代表 customer 应依赖 projectprocess；但命名容易误导，后续可改成客户关联项目状态查询。
- 外部模块大量直接引用 customer mapper/model，短期是事实读取，长期可按高频场景沉淀更窄的客户查询接口。

处理建议：

- 保持独立 Maven 模块。
- 不并入 project、contract 或 risk/finance。
- 后续重点不是合并，而是约束外部模块直接写客户表，并为高频读取提供稳定应用服务、resolver 或 snapshot DTO。
- 优先治理 dashboard/risk/report 等读侧场景对 customer mapper/model 的直接使用，避免客户域继续承载看板、风控监控和报送的展示规则。

### projectprocess

结论：`projectprocess` 是项目立项/评审/定价/生命周期域，应保持独立。

依据：

- 模块 POM 依赖 `api`、`foundation`、`document` 和 `flow-core`。其中 `document` 用于材料/版本处理，`flow-core` 当前由项目生命周期流程结束处理器直接使用，不是空依赖。
- POM 技术依赖复核后，`document`、`flow-core`、MapStruct、Hutool、FastJSON、MyBatis-Plus、Jackson annotations、Spring context/beans/web/tx/boot、validation、annotation、POI、XXL Job 都有源码使用依据；未发现 `pagehelper` 和 `commons-lang3` 的当前源码引用，已移除这两个直接依赖，并通过 `mvn -pl zswl-mithras-projectprocess -am -DskipTests compile` 验证。
- 代码层面复核显示，模块内 Java import 主要集中在 `projectprocess` 约 603 处、`foundation` 约 364 处、`dto` 约 298 处、`api` 约 64 处、`document` 约 11 处，并有 1 个流程结束监听接口直接 import `flow-core`；没有直接 import contract/payment/customer/riskcontrol 等业务域。
- 模块拥有项目立项、项目评审、项目定价、现金流测算、项目生命周期、版本、材料、审批状态和 job。
- 模块已经通过若干 port 隔离外部事实和横向能力，例如 `PaymentCashFlowQueryPort`、`ProjectStatusSupportPort`、`ProjectRiskControlIndustryPort`、`ProjectProcessDictionaryPort`、`ProjectProcessSurvivingContractResolver`、`ProjReviewNoticeJobService`。这些接口由 application 侧 adapter 适配付款、合同、风控行业、字典、消息通知等能力。
- 立项/评审/定价服务大多围绕自身 mapper/model 修改和版本记录，项目过程是合同、付款、风险、报送、FTP、预算等模块的上游事实。
- 外部依赖它的模块很多。只看 Java import，反向依赖主要来自 `application` 约 872 处、`web` 约 101 处、`contract` 约 48 处、`riskcontrol` 约 30 处、`ftp` 约 23 处、`creditreport` 约 19 处、`metric` 约 15 处、`report` 约 14 处、`dashboard/afterlease` 各约 11 处。这说明它是核心交易主链路，不说明它应被合并到某个下游模块。

发现的问题：

- `projectprocess` 直接依赖 document，材料和版本处理合理，但要避免 document 模型进一步扩散。
- `ILifecycleProcessor` 直接使用 `FlowTaskApiService`、`ProcessResp`、`ProcessBusinessStatusEnum`、`ProcessEndContext` 和 `ApplicationContextUtil`，用于流程结束后写入项目生命周期事件。这是项目域与流程引擎的真实耦合点，短期不硬删；后续可考虑把流程结束上下文转换放到 application/workflow adapter，projectprocess 只接收项目生命周期事件命令。
- `ProjEstablishBaseInfoMapper.xml` 的生命周期查询会 join `contract_base_info`，`ProjLifecycleEventMapper.xml` 会通过生命周期视图和合同、付款、收款等事实计算项目阶段/金额。这是项目生命周期读模型，不是 projectprocess 对合同/付款生命周期的所有权。
- `resources/sql/风控策略`、风险敞口字段、风控审批节点和 `ProjRiskControlIndustryTypeJob` 体现项目评审与风险审批的历史耦合。项目可以持有审批所需风险属性，但风险策略、风险报告和预警规则应归 riskcontrol 或 application 编排。
- `application/orchestration/projectprocess` 中仍有很多实现和跨域编排，后续需要区分项目单域逻辑与真正跨域逻辑。

处理建议：

- 保持独立 Maven 模块。
- 不并入 contract。合同从项目评审转化而来，但合同生效后的生命周期已经不同。
- 后续重点是从 application 中识别单域项目过程逻辑并回迁，跨域流程/材料/通知仍由 application 装配。
- 流程引擎依赖先作为真实耦合记录，不为了清 POM 强删；若后续治理，应通过项目生命周期事件 port/adapter 隔离 flow-core DTO 和查询 API。
- 为合同生成、风控评估、报送、FTP、预算等下游提供稳定查询服务、port 或 snapshot DTO，减少外部模块直接引用 projectprocess mapper/model。

### contract

结论：`contract` 是合同履约中枢和交易事实源，规模大、依赖多、反向消费重，应保持独立并做依赖瘦身，不能作为简单合并目标。

依据：

- 模块包含合同基本信息、租金/价格、承租人/担保人/账户/租赁物关系、逾期、版本、材料、合同文本生成、超时任务、合同变更和生效检查。
- `ContractBaseInfoService` 暴露合同状态、合同变更、风险敞口、剩余额度、资产余额、合同列表/详情等核心能力，被很多模块消费。
- POM 依赖 `document`、`customer`、`basedata`、`workflow`、`projectprocess`、`third`，符合合同从项目和客户生成、并接入流程/文档/第三方的主链路位置。
- 代码层 import 以自身和底座为主，但仍直接引用外域：`contract` 约 949 处、`foundation` 约 514 处、`dto` 约 284 处、`api` 约 82 处、`projectprocess` 约 48 处、`customer` 约 18 处、`workflow` 约 8 处、`document` 约 8 处、`third` 约 6 处、`basedata` 约 4 处。
- 外部依赖 contract 的模块最多。只看 Java import，反向依赖主要来自 `application` 约 1680 处、`web` 约 273 处、`report` 约 130 处、`metric` 约 45 处、`collection` 约 41 处、`afterlease` 约 38 处、`finance` 约 30 处、`payment` 约 26 处、`dashboard` 约 14 处、`riskcontrol` 约 13 处、`creditreport` 约 8 处、`budget` 约 4 处。此前 `assetclassify/leaseholdproperty` 对 contract 的少量源码依赖已通过各自 port/application adapter 收敛。这说明 contract 是交易域中枢，不应被并入 finance/risk/reporting。
- 资源层 SQL 以 `contract_base_info`、`contract_receipt`、`contract_income_sharing`、`contract_guarantor`、`contract_tenantry` 等合同表为主，同时为了列表、逾期和材料场景读取 `client`、`collection_base_info`、`fund_*`、`bifrost_user` 等外域表。BPMN 中存在风控、财务、法务等审批节点，这是流程参与角色，不代表合同域拥有风控或财务规则。

发现的问题：

- contract 模块本身已经很大，包含逾期子域、合同核心、文本生成、流程、租赁物相关模型等，内部边界需要持续整理。
- `contract` 直接依赖 workflow/document/third，长期看流程/文档/第三方装配可进一步外移或端口化。
- `ContractBaseInfoMapper.xml` 中逾期相关查询会读取 `collection_base_info`，合同列表查询也会排除 `fund_*` 融资质押数据；这些更像跨域读模型或筛选条件，后续要用稳定查询接口或 application 编排隔离。
- `leaseholdproperty` 与 contract 强相关，但租赁物台账和评估白名单是否归 contract，需要看生命周期，不应仅凭依赖合并。

处理建议：

- 保持独立 Maven 模块，作为核心交易中枢。
- 不把 finance、collection、payment、leaseholdproperty 粗暴并入 contract。
- 后续优先整理 contract 内部子域：合同核心、逾期催收、文本/材料、租赁物关系、版本、流程适配、任务适配分清楚。
- 对外提供稳定的合同查询服务或 snapshot DTO，减少下游模块直接引用合同 mapper/model。

### leaseholdproperty

结论：`leaseholdproperty` 是租赁物/评估公司白名单/租赁物材料域，与 contract 强相关但应保持独立；它可以归入 contract 大域治理，但不应直接混入合同核心模块。

依据：

- 模块拥有租赁物、租赁物信息、VAT 发票、车辆登记证、评估关系、评估公司白名单、天眼查评估公司基础信息和版本。
- `AppraisalCompanyWhitelistService` 管理评估公司白名单的新增、修改、出库流程、材料检查、天眼查信息和租赁物评估关系，具备独立审批/版本生命周期。
- POM 依赖 `document`、`basedata`、`third`，说明它围绕租赁物材料、基础字典和第三方评估扩展；需要的合同上下文已通过 `LeaseholdContractContextPort` 由 application adapter 适配到合同域。
- POM 依赖复核后，`document` 用于白名单材料版本处理，`basedata` 用于通用字典/日期工具，`third` 用于天眼查评估公司信息；`poi`、Hutool、Spring context/web/tx、servlet、validation、MyBatis-Plus、Swagger、Jackson、XXL Job 都有源码使用。直接 `org.mybatis:mybatis` 只为 `@Param` 注解提供显式依赖，已由 MyBatis-Plus 传递依赖覆盖并移除。
- 代码层实际 import 较集中：约 `third` 6 处、`document` 3 处、`basedata` 1 处，没有直接依赖合同、项目、付款、收款、风控等交易域。
- 当前未发现 `leaseholdproperty -> workflow/flow-core` 的 Java import；流程模型枚举本地化后，评估机构白名单流程启动/运行中查询也已通过 `AppraisalCompanyWhitelistWorkflowPort` 交给 application adapter 适配。
- 反向依赖较广：粗略搜索显示 `application` 约 336 处、`api` 约 284 处、`web` 约 241 处、`afterlease` 约 161 处、`contract` 约 78 处。租赁物是合同、租后、材料、归档、文档生成和看板等场景的重要事实源。
- 进一步按 Java import 复核，真实编译期反向依赖主要来自 `application` 约 110 处，`web` 仅少量；`contract` 模块对“lease item”的大量引用主要是合同自己的 `contract_lease_item`、`ContractLeaseItem`、`ContractLeaseItemLib` 和合同文本渲染，不等同于直接依赖 `leaseholdproperty` 模块内部模型。
- `LeaseVersionFacade` 从合同上下文发起租赁物创建/变更审批，会通过 `LeaseholdContractContextPort` 读取合同快照、校验回租业务和岗位，并创建租赁物审批草稿；这是合同驱动租赁物生命周期的入口，不代表租赁物应并入合同核心。
- `LeaseAppraisalServiceImpl` 调用天眼查查询/刷新评估公司基础信息，同时通过 `LeaseholdContractContextPort` 按合同 ID 查询项目上下文，说明评估公司管理是租赁物域自己的业务能力。
- Mapper XML 中 `LeaseItemListRowDataMapper.xml` 会查询 `contract_lease_item` 以排除已绑定合同的租赁物明细，这是目前 SQL 层最明确的合同耦合点。
- 外部依赖它的主要是 `application`，核心模块并没有大面积依赖它，说明它不是公共基础模块。

发现的问题：

- 直接依赖 contract 是合理的，但如果合同只需要租赁物摘要，应通过更窄的租赁物查询接口，避免合同和租赁物模型互相侵入。
- 白名单流程不再借用 `workflow` 模块的流程模型枚举；租赁物域本地维护 `LeaseholdPropertyProcessModel`，并通过 `AppraisalCompanyWhitelistWorkflowPort` 声明流程启动/运行中查询需求，flow-core API 调用已外移到 application adapter。
- `application` 中合同、付款、材料、归档、流程监听和文档生成大量读取租赁物 service/model/enum，这属于应用层编排，但也说明租赁物对外暴露面偏粗。
- `LeaseVersionFacade` 已不再直接读取 `ContractBaseInfo`，合同上下文已收敛为租赁物域自有快照，避免租赁物域为了发起流程理解过多合同持久化字段。
- application 中 `ContractLeaseItemServiceImpl` 直接使用 `LeaseItemInfo`、`LeaseItemListRowData`、`LeaseItemCommonService` 完成合同选择租赁物，这是 contract/leaseholdproperty 编排，适合留在 application；其服务接口 `ContractLeaseItemService` 已迁回 contract 模块，避免由 leaseholdproperty 声明合同持久化模型服务。

处理建议：

- 暂时保持独立 Maven 模块，不急着并入 contract。
- 在目标域上归入 contract 大域治理，作为租赁物/评估子域。
- 后续重点是明确 contract 与 leaseholdproperty 的交互边界：合同拥有合同条款，leaseholdproperty 拥有租赁物台账和评估白名单。
- 优先收窄合同侧调用：合同、付款、文档、归档只应读取租赁物摘要、材料类型、绑定关系等稳定语义，不应直接依赖租赁物内部 mapper/model。
- 白名单材料、文件校验、流程启动/查询和流程结束事件继续留在 `application` 适配，租赁物域只保留业务规则和状态流转。
- 当前动作定义为“保持独立 + 收窄合同侧交互”，不是物理合并。只有在租赁物台账、评估公司白名单、车辆登记、发票、OCR 等生命周期被证明都只是合同内部明细时，才重新评估合并。
- 当前不满足物理合并条件：它依赖合同，但不是合同核心的薄目录；车辆登记、VAT 发票、OCR、评估公司白名单、第三方评估信息和白名单版本都说明它有独立子域生命周期。

### afterlease

结论：`afterlease` 是租后检查/租后调整/外部查询/罚息减免/催收管理域，应保持独立；当前不是模块过细，而是租后域内部混入了外域 adapter 和 workflow 适配细节。

依据：

- 模块包含租后调整、租后检查计划、检查客户、检查报告、外部查询、罚息减免、催收邮件、租后检查模板、版本和流程。
- POM 当前只保留 `contract`、`document` 两个业务域依赖；`basedata`、`workflow`、`collection`、`customer`、`projectprocess` 直接依赖已清理。
- afterlease 源码已无 `basedata`、`workflow`、`collection`、`customer`、`projectprocess`、`contract` 和 `flow-core` Java import；流程启动、流程查询、任务转换、权限节点判断和关联流程返回值已经通过租后流程 port 或本地快照迁到 application adapter。
- 只看 Java import，反向依赖主要来自 `application` 约 312 处、`web` 约 24 处、`dashboard` 约 14 处；按 Java/POM/Mapper/SQL 全文粗略搜索，`application`、`web`、`dashboard` 仍是主要消费方，并有少量 `assetclassify`、`kpi`、`workflow`、`rating`、`margin` 引用或脚本关联。
- `AfterLeaseAdjustInfoService`、`AfterLeaseCheckExternalQueryService` 等接口体现租后自己的审批、查询、流程结束处理和版本生命周期。
- 模块内已存在一批 port：`AfterLeaseContractPort`、`AfterLeaseCollectionPort`、`AfterLeaseClientPort`、`AfterLeaseMaterialsPort`、`AfterLeaseNotificationPort`、`AfterLeaseCheckReportRenderPort`、`RentCollectionEmail*Port` 等，说明外域协作方向已经部分抽象出来。
- 这些 port 的 7 个外域 adapter 已从 `zswl-mithras-afterlease/src/main/java/.../adapter` 迁到 `application/orchestration/adapter/afterlease`，例如合同、收款、客户和基础数据适配实现由 application 承接。afterlease 模块内只保留 port 接口和租后业务规则。
- 主要跨域 port 已开始收窄为租后本地输入模型：`AfterLeaseContractPort` 的合同列表查询已改为返回租后本地 `AfterLeaseContractSnapshot`，`AfterLeaseClientPort.getById` 已改为返回租后本地 `AfterLeaseClientSnapshot`，`AfterLeaseContractRentActualPort` 已改为返回租后本地 `AfterLeaseContractRentSnapshot`，罚息减免审批所需单合同读取已改为 `AfterLeaseContractApprovalContext`，逾期催收标记更新已改为明确命令，租后检查批量客户数据接口也已不再接收 `Client` map。`AfterLeaseCollectionPort` 已用 `AfterLeaseCollectionEmailSnapshot`、`AfterLeaseReceiptCollectionSnapshot` 和 `AfterLeasePenaltyCollection` 替代公开签名里的 `CollectionBaseInfo` 与 MyBatis `Wrapper`。
- `AfterLeaseMaterialsPort` 查询侧已改为返回租后本地 `AfterLeaseMaterialSnapshot`，租后报告列表、检查报告下载和检查计划摘要报告不再直接消费 document 的 `MaterialsList` 持久化模型；普通材料查询由 application adapter 调用 document 材料服务并转换。
- `AfterLeaseClientPort` 已扩展为批量客户快照查询，外部查询任务生成不再直接注入 customer `ClientMapper` 或消费 `Client` 持久化模型；客户名称、类型、主办和部门通过 `AfterLeaseClientSnapshot` 输入租后域。
- `AfterLeaseContractPort.clientRoleNamesByContractId` 已承接合同参与方角色查询，外部查询任务生成不再直接读取合同承租人/担保人 service、model 或合同侧角色枚举；合同参与方事实由 application adapter 转换成租后域角色名称集合。
- 租金催收展示逻辑已使用租后本地 `RentCollectionWriteOffStatus` 和本地字符串协议判断，去掉对 collection 核销状态枚举、contract 合同状态枚举和 customer 移动端日历枚举的 Java import；底层数据库状态值保持不变。
- 检查计划超时、逾期和审批提醒中的工作日历计算已改走 `AfterLeaseWorkdayCalendarPort`；特殊工作日配置仍由 application adapter 调用 basedata 提供，afterlease 不再直接依赖 basedata `DateUtil` 或 POM 依赖。
- afterlease POM 已移除 `basedata`、`workflow`、`collection`、`customer`、`projectprocess`、`contract` 直接依赖；这些协作都已由 application adapter 或租后本地 port 承接。剩余 POM 依赖中的 `document` 仍对应真实 Java import，暂不删除。
- 租后检查报告基本信息读取合同版本库和租金版本库已通过 `AfterLeaseContractVersionPort` 声明，application adapter 负责调用 contract 版本服务并转换为租后本地快照。
- 外部查询报告渲染已去掉对 contract `AbstractBasicRender` 的继承，避免为了金额格式化方法保留 contract 模块依赖。
- `AfterLeaseAdjustConvert` 和它的 `AfterLeaseTypeConversionWorker` 已迁到 `application/orchestration/afterlease/convert`。该 converter 只被 application 的租后调整实现使用，且承担 `ProjReviewBaseInfo` 到租后调整记录的跨域装配；afterlease 因此不再直接依赖 projectprocess。
- 罚息减免材料类型已收敛为 afterlease 本地 `AfterLeasePenaltyReductionMaterialsEnum`，`ReceiptCollectionServiceImpl` 不再直接 import document 的 `MaterialsEnum`；传给材料服务的业务模块和材料类型值保持不变。
- `genhtml/PaymentNoticeHtmlRender` 已改为只依赖 `PaymentNoticeRenderDataPort` 和租后邮件记录，收款、客户、合同版本、账户和用户信息由 `application/orchestration/adapter/afterlease/PaymentNoticeRenderDataPortAdapter` 组装。
- 需要 customer `Client` 持久化模型的 `AfterLeaseCheckPlanProjectConvert` 仅由 application 调用，已迁到 `application/orchestration/afterlease/convert`；afterlease 模块不再因展示转换器直接依赖客户模型。
- SQL 层也存在隐性耦合：`NewAfterLeaseCheckExternalQueryMapper.xml` join `client`，`NewAfterLeaseCheckPlanBaseMapper.xml` 读取 `asset_classify_client`、`bifrost_user`、`bifrost_org`，`RentCollectionIndexMapper.xml` join `payment_base_info`、`contract_base_info`、`collection_base_info`。
- dashboard 等模块读取 afterlease 用于看板展示，但 afterlease 自己拥有检查和调整结果。

发现的问题：

- afterlease 对 collection/customer/projectprocess 的 Java/POM 直接依赖已经清理；其中 `AfterLeaseCollectionPort` 公开签名已不再暴露 collection 持久化模型，租金通知书渲染也已通过 `PaymentNoticeRenderDataPort` 隔离跨域读取。剩余主要是 SQL 层跨域读模型，例如租金催收首页、外部查询列表和检查计划列表。
- customer Java 直接依赖已清理：展示转换器已迁到 application，`AfterLeaseCheckExternalQueryServiceImpl` 也已改走 `AfterLeaseClientPort`/`AfterLeaseClientSnapshot`。SQL 层 `NewAfterLeaseCheckExternalQueryMapper.xml` 仍 join `client`，属于读模型耦合，后续单独设计。
- 普通材料查询侧已改为 `AfterLeaseMaterialSnapshot`，但材料版本化 handler `AfterLeaseCheckMaterialsListLibHandler` 仍直接依赖 document 的 `MaterialsList`、`MaterialsListLib` 和版本 proxy；这是材料版本协议耦合，后续应单独评估 document 是否提供更窄的版本化 port。
- workflow 动态表单 handler、流程结束 handler 和流程准备 commit handle 已迁到 `application/orchestration/adapter/afterlease/workflow`。afterlease 已去掉 workflow 流程枚举、流程变量名、材料节点常量和 `FlowEndEventProcessor` SPI 依赖，相关协议值改为租后域本地常量。
- 外部查询审批和罚息减免审批的流程启动、当前用户解析、`StartProcessReq` 组装、`BizProcessDataService` 记录、检查报告修改权限中的运行中流程节点判断，以及租后检查报告审批超时提醒的流程任务查询/转换，已迁到 `application/orchestration/adapter/afterlease/AfterLeaseWorkflowPortAdapter`；afterlease 只通过 `AfterLeaseWorkflowPort` 声明流程协作需求。
- afterlease 模块内已不再暴露 flow-core DTO；`AfterLeaseAdjustInfoService` 的关联流程查询已改为返回 `AfterLeaseRelatedProcess` 本地快照。
- 租后检查资料进入归档准备流程的场景应通过 application/adapter 装配，避免 afterlease 直接依赖 filingmaterials。
- 原 `adapter` 包在模块内部会让“afterlease 声明协作需求”退化为“afterlease 自己适配外域实现”；这一步已经先迁出 7 个外域 adapter，但 port 类型和 workflow 适配仍待继续收窄。

处理建议：

- 保持独立 Maven 模块，作为合同生效后的租后管理域。
- 不并入 contract 或 collection；租后读取合同/收款事实，但拥有独立检查、调整、查询和减免流程。
- 也不并入 `application`；`application` 只做跨域编排和 adapter，不能成为租后事实的所有者。
- 后续治理重点是把外域事实读取变成租后输入快照或查询接口，减少直接 mapper/model 依赖。
- 已迁出 afterlease 内部 `adapter` 包中 7 个外域 adapter 到 `application/orchestration/adapter/afterlease`，afterlease 只保留 port 接口和租后业务规则。
- 已迁出 workflow 动态表单、流程结束 handler 和流程准备 commit handle；afterlease 暴露状态变更和业务处理方法，由 application 接 workflow。
- 已拆掉流程模型枚举、流程变量名、材料节点常量和流程结束 SPI 对 workflow 的直接依赖。
- 已把外部查询审批、罚息减免审批的流程启动、检查报告修改权限的运行中流程节点判断、租后检查报告审批超时提醒的流程任务查询/转换和 `BizProcessDataService` 记录迁到 application adapter。
- 已处理 `AfterLeaseAdjustInfoService` 的 `ProcessResp` 返回值，用租后本地 `AfterLeaseRelatedProcess` 快照替代 flow-core DTO。
- 已收窄 `AfterLeaseClientPort.getById` 和租后检查批量客户数据接口，去掉公开签名中的 `Client` 模型；也已收窄 `AfterLeaseContractRentActualPort`，用租后本地 `AfterLeaseContractRentSnapshot` 替代 `ContractRentActual`；罚息减免审批所需的单合同读取和逾期催收标记更新已用 `AfterLeaseContractApprovalContext` 和 `markOverdueCollectionNotified` 替代完整 `ContractBaseInfo` 读写；合同列表查询已用 `AfterLeaseContractSnapshot` 替代 `ContractBaseInfo`；`AfterLeaseCollectionPort` 的催收邮件、逾期列表、罚息减免分配、逾期等级和剩余罚息汇总已分别用 `AfterLeaseCollectionEmailSnapshot`、`AfterLeaseReceiptCollectionSnapshot`、`AfterLeasePenaltyCollection` 和明确查询方法替代完整 `CollectionBaseInfo` 与 MyBatis `Wrapper`。
- 已收窄 `AfterLeaseMaterialsPort` 普通查询侧，用 `AfterLeaseMaterialSnapshot` 替代 document 的 `MaterialsList`；材料版本化 handler 暂留，后续单独设计版本材料 port。
- 已将仅由 application 使用、且需要 customer `Client` 模型的 `AfterLeaseCheckPlanProjectConvert` 迁到 application 编排层。
- 已收窄外部查询任务生成中的客户读取，用 `AfterLeaseClientSnapshot` 替代 customer `ClientMapper`/`Client` 直接依赖。
- 已收窄外部查询任务生成中的合同参与方角色读取，用 `AfterLeaseContractPort.clientRoleNamesByContractId` 替代 afterlease 直接读取合同承租人/担保人 service、model 和合同侧角色枚举。
- 已将租金催收展示中的 collection/contract/customer 枚举 import 收敛为租后本地状态/字符串协议，降低低价值枚举依赖。
- 已将工作日历能力改为 `AfterLeaseWorkdayCalendarPort`，由 application 适配 basedata 特殊工作日配置，并移除 afterlease 对 basedata 的直接 POM 依赖。
- 已移除 afterlease POM 中无源码引用的 `workflow`、`collection`、`customer` 直接依赖。
- 已将只被 application 使用且依赖 projectprocess `ProjReviewBaseInfo` 的 `AfterLeaseAdjustConvert` 迁到 application，移除 afterlease 对 projectprocess 的直接 POM 依赖。
- 已将罚息减免材料类型改为 afterlease 本地 `AfterLeasePenaltyReductionMaterialsEnum`，去掉 `ReceiptCollectionServiceImpl` 对 document `MaterialsEnum` 的直接依赖。
- 租金支付通知书 HTML/DOCX 渲染已增加 `PaymentNoticeRenderData` 与 `PaymentNoticeRenderDataPort`，由 application adapter 汇集收款、客户、合同、账户和用户信息，afterlease renderer 只负责模板填充。
- 当前已通过 `mvn -pl zswl-mithras-afterlease,zswl-mithras-application -am -DskipTests compile` 验证；afterlease 低风险瘦身阶段可以视为完成。剩余 `document` 属于材料/模板协议耦合，不能为了清零依赖硬拆，后续应单独设计 document 材料版本化 port。
- SQL 层跨表读取短期保留，后续按检查计划列表、外部查询列表、资产分类展示等读模型逐步收敛。

### archives

结论：`archives` 是正式档案管理域，应保持独立；详细对比见“archives 与 filingmaterials”。

依据：

- `zswl-mithras-archives` 只依赖 `api`、`foundation`，模块内没有直接 import 其他业务域。
- 模块拥有档案模板、档案类型、档案管理、审批、借阅/下载权限等正式档案生命周期。
- 通过 `ArchivesWorkflowPort`、`ArchivesNotificationPort`、`ArchivesSupportPort` 接入流程、通知和外部支撑信息，方向合理。
- 代码级复核显示 Java import 只指向自身、api、foundation；POM 低耦合不是假象。

发现的问题：

- `ArchiveTemplateMapper.xml` 直接读取 `materials_list where business_type = "ARCHIVES"`，说明档案文件展示复用 document/materials 的文件清单表。
- `ArchivesManagementMapper.xml` 直接 join `proj_establish_base_info`，说明档案列表读模型仍直接读取项目主数据。短期可接受，但属于表级耦合，后续可考虑收敛为 application 侧组装或档案列表快照。

处理建议：

- 保持独立 Maven 模块。
- 不并入 `document` 或 `filingmaterials`；document 是文件能力，filingmaterials 是归档准备流程，archives 是正式档案管理。

### filingmaterials

结论：`filingmaterials` 是归档准备/归档资料流程域，应保持独立；详细对比见“archives 与 filingmaterials”。

依据：

- `zswl-mithras-filingmaterials` 依赖 `document`，模块内只有少量 document import，主要使用文档能力生成/管理归档材料。
- 依赖必要性复核后，`document`、`poi-tl`、`poi`、`mapstruct`、`xxl-job`、MyBatis-Plus、Spring Web/Context、validation、hutool 都能在源码中找到明确使用点；原 POM 中未发现源码引用的 `oss-toolkit` 已移除，直接 `org.mybatis:mybatis` 未发现独立必要性，已由 MyBatis-Plus starter 覆盖并移除。
- 模块拥有资料目录、归档配置、项目/资金/租后/其他资料归档、归档台账、批量下载和归档材料生成。
- 它处理正式归档前的资料准备和流程，不等同于正式档案管理。
- 代码级复核显示 Java 层没有直接 import 项目、资金、租后等业务域；合同信息已通过 `FilingMaterialsContractInfoPort` 由 application adapter 提供。
- 模块本体 Java import 主要是 `dto` 45、`filingmaterials` 31、`foundation` 22、`api` 21、`document` 4，说明 POM/Java 层低耦合基本真实。
- application 侧归档资料实现很重：归档资料相关目录和流程监听/文件 provider 共约 31 个 Java 文件，import 分布约为 `filingmaterials` 86、`application` 66、`dto` 62、`foundation` 58、`workflow` 32、`document` 31、`fund` 15、`system` 14、`payment` 14、`contract` 14、`customer` 11、`projectprocess` 10、`message` 10、`afterlease` 10、`credit` 5，另有少量 `basedata`、`leaseholdproperty`、`third`。
- 这说明真正复杂的是跨域归档资料编排，而不是 filingmaterials 模块本体。把 filingmaterials 并入 archives/document 不能消除这些依赖，只会把跨项目、合同、付款、授信、租后、流程、消息、文档的编排污染 records 模块。

发现的问题：

- `FilingMaterialsMapper.xml` 直接读取 `proj_review_base_info`、`fund_direct_financing_base_info`、`fund_financing_base_info`、`fund_financing_credit_ref`、`contract_base_info`、`act_hi_actinst`、`act_hi_taskinst`，用于项目端/资金端归档台账和流程时效展示。
- 这说明 filingmaterials 的 Java/POM 边界较轻，但 mapper SQL 是明显的跨域读模型；后续如果继续治理，应优先把这些台账查询收敛为 application 侧组装或归档资料快照。
- `FilingMaterialsService` 是 application 侧最大耦合点，直接触达项目、合同、付款、客户、授信、租赁物、workflow、document、message 等；它整体是跨域编排，不应为了“回迁”直接搬入 filingmaterials。
- `AbstractFilingMaterialsService` 混合了目录/状态规则、document 文件复制、workflow 历史、system 配置和模板渲染辅助。后续可从这里优先拆出纯 filingmaterials 规则。

处理建议：

- 保持独立 Maven 模块。
- 不并入 `archives`；二者同属 records 上位域，但生命周期不同。
- 当前动作定义为“保持独立 + application 编排瘦身”，不是物理合并。
- 后续可继续从 application 中区分单域资料归档逻辑和跨域流程编排：
  1. `FilingMaterials`、目录配置、资料状态、下载记录等单域规则作为回迁候选。
  2. 项目、合同、付款、授信、租后、流程、消息、document 文件能力相关逻辑继续留在 application。
  3. `FilingMaterialsMapper.xml` 的项目/资金/合同/流程台账查询，后续改为 application 组装或归档资料快照。

### margin

结论：`margin` 是财务大域里的保证金子域，但暂不建议第一批物理并入 `finance`。

依据：

- `zswl-mithras-margin` 自身 POM 只依赖 `api`、`foundation` 和框架 provided 依赖，模块内没有直接 import 合同、收款、付款、工作流等业务域。
- 代码层复核显示，模块内 Java import 主要集中在 `margin`、`foundation`、`dto`、`api`，没有直接 import 合同、付款、收款、工作流等业务域；POM 中的 controller、Excel、MyBatis-Plus、XXL job、Spring transaction、servlet/validation 等 provided 依赖也都能在源码中找到对应使用，其中 `xxl-job-core` 被 `DepositWriteOffJob` 的 `@XxlJob` 直接使用。直接 `org.mybatis:mybatis` 仅为 `@Param` 提供显式来源，已由 MyBatis-Plus starter 覆盖并移除。Java import 分布约为 `margin` 54 处、`foundation` 33 处、`dto` 31 处、`api` 5 处。
- 模块源码约 36 个 Java 文件，包含 application 接口、5 个 port、5 个 port model、controller、转换器、保证金/担保金 mapper/model/service、job 入口和 Excel 导出。
- 模块拥有明确主表和写模型：`margin_base_info`、`margin_record_info`、`margin_write_off_record`、`warranty_base_info`、`warranty_record_info`。
- 核心语义是保证金/担保金的收取、占用、退款、抵扣和核销，具有独立生命周期。
- 合同、收款、付款、权限、消息、流程等外部协作已经通过 `MarginContractInfoPort`、`MarginCollectionPort`、`MarginPaymentReceiptPort`、`MarginRecordSupportPort`、`MarginViewAuthPort` 接入，adapter 主要放在 `application`。这些 port 已经使用 margin 自有输入/输出模型，例如 `MarginContractInfo`、`MarginCollectionRecordInfo`、`MarginRefundPaymentInfo`、`MarginCollectionSnapshot`，没有直接暴露合同/收款/付款持久化模型。
- `DepositWriteOffJobService` 的接口在 `margin`，实现放在 `application` 是合理的：该实现同时触达合同、收款、流程、租后、归档状态，强行迁回 `margin` 会污染边界。
- 按 Java/POM/Mapper/SQL 粗略反向搜索，外部引用主要是 `application` 约 97 处、`web` 约 37 处、`payment` 约 32 处、`collection` 约 4 处、`dashboard` 约 2 处。

发现的问题：

- 原 `MarginBaseInfoMapper.xml` 的 `pageList` 直接关联 `contract_base_info`，这是 `margin` 内部为了列表查询和数据权限引入的合同读模型耦合；本轮已迁出到 application 读侧 `MarginBaseInfoListQueryMapper`，margin 只保留查询 port。
- `payment` 的 mapper XML 直接查询 `margin_base_info`、`margin_record_info`、`warranty_base_info`、`warranty_record_info`，用于付款/退款读侧列表。这属于表所有权泄漏。
- `dashboard` 的 mapper XML 直接 join `margin_base_info`，属于读侧聚合便利，短期可接受，但应记录为 reporting 对 margin 的读事实依赖。
- `application` 中部分跨域编排直接注入 `MarginBaseInfoService`、`MarginRecordService`、mapper 和 model，主要集中在 payment、contract、capital、third financial、workflow end handler 和若干 adapter。放在 `application` 不一定错，但后续应区分“跨域编排”与“单域逻辑外溢”。collection 侧 `ContractCollectionMarginPortAdapter` 已先收敛为消费 `MarginCollectionSnapshot`，不再直接依赖 `MarginBaseInfo` 持久化模型。
- `payment` 的反向引用主要反映付款/退款读侧需要保证金事实，不代表保证金应并入付款域。

处理建议：

- 短期保持独立 Maven 模块，把它作为 finance 大域下的干净子域。
- 优先治理外部 XML 直接读保证金表的问题，至少先集中到 reporting/payment 的明确读模型或 application adapter。
- 不要为了减少模块数量先合并 `margin`；否则会丢掉目前已经比较清楚的边界。
- 当前不满足物理合并条件：它不是薄读模型，也不是单纯依附于 `payment`、`collection` 或 `finance` 的页面能力，而是拥有独立写生命周期和比较干净的端口边界。

### blackgray

结论：`blackgray` 不应简单并入 risk-credit，它更像独立的名单/准入能力域。

依据：

- `zswl-mithras-blackgray` 自身 POM 低耦合，模块内没有直接 import 其他业务域。
- 代码层 import 粗分约为 `blackgray` 336 处、`foundation` 47 处、`api` 36 处、`dto` 12 处，没有直接引用 `riskcontrol`、`credit`、`assetclassify` 等风险相邻模块。
- 模块拥有名单库、入库任务、人工出库、失信名单、规则配置、审核和外部撞库查询等完整生命周期。
- 外部使用主要集中在 `web` 和 `application`：粗略搜索显示 `web` 约 153 处、`application` 约 25 处，另有 `riskcontrol` 资源层菜单引用。`application` 场景包括工作流 end handler、客户统一视图、第三方票据入库、材料附件 provider/checker 等。
- 模块已经通过 `BlackGrayCustomerPort`、`BlackGrayApprovalProcessPort` 把客户信息和流程启动隔离出去，方向是对的。
- 代码级复核显示 Java import 只指向自身、api、foundation；POM 低耦合不是假象。
- POM 依赖复核显示，`tk.mybatis mapper`、MyBatis-Plus、Spring、Redis、PageHelper、EasyExcel、POI、Fastjson、Jackson、validation、Swagger、Servlet、JPA、commons-lang3、commons-collections4、commons-io、commons-codec、Netty 和 Gruul starter 都能在源码中找到使用点。源码确实使用 MyBatis core 类型，例如 `BlackMysqlConfig` 的 `Slf4jImpl`、`StdOutImpl`、`Interceptor`、`SqlSessionFactory`、`JdbcType` 和 mapper `@Param`，但这些已由 MyBatis-Plus/tk-mybatis 依赖链提供，本轮移除无独立必要性的直接 `org.mybatis:mybatis` 依赖。
- 编译验证：`mvn -pl zswl-mithras-blackgray -am -DskipTests compile` 通过，实际 reactor 只包含 root、api、foundation、blackgray，说明 Maven 依赖链确实很薄。

发现的问题：

- `blackgray` 包内同时包含领域服务、外部接口、Excel 工具、Redis/鉴权服务、数据脱敏注解等，内部包可以继续整理，但这不影响它作为独立业务能力存在。
- `application` 中有直接调用 `BlackGrayLibraryService` 的场景，例如客户统一视图和第三方票据入库。这些属于跨域使用名单事实，短期合理；后续可在必要时补更窄的查询/入库接口。
- mapper XML 仍直接读取外部运行期表：`BlackGrayBreakBusinessMapper.xml`、`BlackGrayWarehouseRecordMapper.xml`、`BlackGrayWarehouseTaskMapper.xml`、`BlackGrayManualOutboundMapper.xml` 查询 `audit_task`，`BlackGrayLibraryMapper.xml` 查询 `concentration_report` 并 join `bifrost_org`。SQL 初始化脚本也写入 `bifrost_menu`、`bifrost_custom_tree` 等平台菜单表。这是平台/读侧表级耦合，不是 Java 业务域依赖，但需要记录，避免误判为完全无外部运行期依赖。

处理建议：

- 短期保持独立 Maven 模块。
- 在目标域视图中可以归入 risk-credit 大域治理，但物理上作为名单能力保留。
- 后续优化重点是模块内部包结构、审批任务列表查询和集中度行业读模型接口收敛，而不是合并到 `riskcontrol` 或 `credit`。

### policy

结论：`policy` 是保单/保险管理能力，不是制度政策域，也不应按名称误归入 risk-credit 核心链路；短期不建议并入 `payment`。

依据：

- `zswl-mithras-policy` 自身 Java import 很轻，除 `document` 材料版本处理外，基本只依赖 `api`、`foundation` 和自身模型；当前约 55 个 Java 文件。
- 代码层面复核显示，模块内 Java import 主要集中在 `policy` 约 61 处、`dto` 约 52 处、`foundation` 约 47 处、`api` 约 21 处，外部业务模块只有 `document` 约 3 处，POM 低耦合基本真实。
- POM 依赖复核后，`poi`、MyBatis-Plus、XXL Job、Hutool、commons-lang3、Spring context/web/tx、servlet、validation、annotation 都有源码使用；直接 `org.mybatis:mybatis` 未发现源码必要引用，已由 MyBatis-Plus starter 覆盖并移除。
- 模块拥有自己的保单主数据、暂存数据、版本、台账、导入导出和到期提醒 job，业务语义不是简单的付款附件。
- 资源中存在 `保单创建审批流程.bpmn20.xml`、`保单变更审批流程.bpmn20.xml`，说明它还承载保单自己的审批生命周期。
- `PolicyInfoTmpService` 已通过 `PolicyContractInfoPort` 读取合同信息，避免把合同模块直接带进 `policy`。
- 代码级复核显示 `PolicyProjectClientInfoPort`、`PolicyContractInfoPort`、`PolicyOperatorNamePort` 已由 application adapter 适配项目/客户、合同、用户姓名等外部事实。
- `PolicyInfoSupportService` 只操作 `policy_info` 及保单状态、续保、提醒等规则，属于可以留在 policy 内部的单域服务。
- 外部实现和 facade 大量放在 `application/orchestration/policy`，其中既有保单单域逻辑，也有项目、合同、付款、材料、流程的跨域编排。
- 反向依赖主要来自 `application`、`web`、`payment`、`dashboard` 和 `projectprocess`；其中 `application` 中包括 facade、workflow end handler、job、payment-policy 同步、document file provider 和维护脚本，属于保单与外部流程/付款/材料的装配协作。
- API 层仍有 `api.payment.dto.PaymentPoliceImportREQ`、`PaymentPoliceTmpImportREQ` 被 `PolicyInfoApi`、`PolicyInfoTmpApi` 和 policy controller 使用，属于历史命名/协议复用造成的语义味道，但不是 policy 对 payment 模块的 Maven 依赖。

发现的问题：

- 原 `PolicyInfoMapper.xml` 的台账和到期查询直接 join `proj_review_base_info`、`payment_policy_info`、`payment_base_info`、`contract_base_info`，POM 看起来低耦合，但读模型对项目、付款、合同表有实际耦合；静态复核命中这些外域表约 20 多处。本轮已先把只由 `PolicyLedgerService` 使用的 `ledgerList` 迁到 application 读侧 `PolicyLedgerQueryMapper`，policy mapper 不再承载这条台账列表 SQL；随后把批量保单号统计 `countPolicyCodes`、`countPolicyCodeNum` 一并迁到 application 读侧 mapper，并删除只有注释引用的 `countPolicyCode` 单值统计 SQL，减少 policy mapper 对付款保单表的直读。
- `application/orchestration/payment/PaymentPolicyInfoService` 直接使用 `policy` 的枚举、Excel importer/exporter、`PolicyInfoMapper` 和 `PolicyInfo`，说明付款侧保单和正式保单之间边界偏混。
- `application/orchestration/policy/PolicyInfoService` 直接注入项目、合同、收款、付款、材料等 mapper/service，里面同时包含保单维护规则和跨域组装逻辑。
- 当前 `policy` 与 `payment_policy_info` 是两个生命周期：付款阶段的保单信息会同步到正式保单，但二者又在保单台账里被合并展示。这个关系应被明确为“付款保单来源 -> 正式保单管理/台账”，而不是把两个域物理合并。

处理建议：

- 短期保持 `policy` 独立 Maven 模块，语义上归到 contract/finance 支撑能力或 records-adjacent 的保单管理，而不是 risk-credit；也不建议直接并入 `contract`，因为它拥有保单暂存、正式保单、续保、台账和到期提醒等独立生命周期。
- 后续继续分批迁出剩余跨域读模型：`myList`、`nearPolicyEndTimeList`、`paymentMaxTimeList`、`listPaymentNeedRenewInsurance` 仍会读付款/合同/项目表，但分别牵涉列表权限、job、到期提醒和续保查询，不能和台账列表一次性混搬。
- 不先合并 `policy` 和 `payment`；优先把付款保单与正式保单的同步关系梳理清楚。
- 后续可把 `application/orchestration/policy` 中只操作 policy 模型、状态、版本、导入导出的逻辑逐步回迁到 `policy`。
- 台账类 SQL 可以短期保留，但应标注为跨域读模型；若后续治理依赖，应改成 application 侧组装或 policy 自己的台账快照，而不是继续扩大 mapper 直连外域表。
- 当前不满足物理合并条件：它不是付款域的薄附件，也不是合同域的一个字段扩展，而是拥有正式保单、暂存保单、版本、审批、续保提醒和材料版本联动的独立生命周期。

### credit

结论：`credit` 是核心授信域，应保持独立；它比 `creditreport`、`rating`、`riskcontrol`、`assetclassify` 更适合作为 risk-credit 大域里的事实源。

依据：

- `zswl-mithras-credit` 模块内实际 import 外域很少，主要是 `document` 材料版本处理，以及一个参数校验异常类。Java import 分布约为 `credit` 112 处、`dto` 94 处、`foundation` 75 处、`api` 23 处、`document` 6 处、`validation` 1 处。
- mapper XML 基本只查询自己的授信立项、授信评审和额度表，没有像 `policy` 那样在模块内直接 join 大量外域业务表。
- 当前代码级复核显示，Java 层没有直接 import 客户、项目、资金、评级、征信、风控等业务域；`document` 依赖集中在 `MaterialsList`/`MaterialsListLib` 及材料版本 handler。
- `GroupCreditEstablishBaseInfoMapper.xml`、`GroupCreditReviewBaseInfoMapper.xml` 目前只围绕授信立项/评审本域表查询，SQL 层没有明显外域业务表 join。
- 模块拥有两条清晰主线：
  - 集团授信立项/评审：`group_credit_establish_*`、`group_credit_review_*` 及版本、材料、流程状态。
  - 授信额度管理：`credit_limit`、`credit_limit_detail`、`credit_business_ref`、`credit_limit_change_record`，负责额度创建、占用、释放、失效和查询。
- `GroupCreditEstablishService` 通过 `GroupCreditEstablishProcessPort` 和 `GroupCreditEstablishContractPort` 接入流程和合同风险敞口，方向基本符合“授信域定义需求，application 适配外部事实”。
- `CreditLimitManagerService` 的额度占用/释放规则自洽，外部资金域通过 BO 调用额度能力，说明它有独立领域服务价值。
- 按 Java/POM/Mapper/SQL 粗略反向搜索，主要消费方是 `application` 约 159 处、`web` 约 43 处、`blackgray` 约 12 处、`creditreport` 约 10 处、`dashboard` 约 9 处、`fund` 约 7 处、`projectprocess` 约 4 处。

发现的问题：

- `application` 中有大量 `groupcredit` 实现和 facade，许多类直接注入授信 mapper/model；其中一部分是跨域流程/材料/项目编排，另一部分可能是授信单域逻辑外溢。
- `fund` 相关服务大量直接调用 `CreditLimitManagerService`、`CreditLimitService`、`CreditBusinessRefService` 和额度 BO。语义上合理，因为融资授信需要占用/释放额度；但这是 finance -> credit 的核心跨域依赖，后续应保持为稳定应用服务接口，不要让 fund 直接理解授信持久化细节。
- `liquidity` 原直接使用 `CreditLimitDetailBO` 做可用授信指标计算，已改为在 `application` 装载数据时转换成 `LiquidityCreditLimitSnapshot`，避免 liquidity 依赖 credit 的读模型。
- `document/materialsfile`、workflow 动态表单和流程监听里直接引用授信材料枚举、授信模型和 mapper，属于横向能力装配层对业务域的直接认识，短期放在 `application` 可以接受，长期应通过更窄的 file/check/process adapter 收敛。

处理建议：

- 不把 `credit` 合并进 `riskcontrol`、`rating` 或 `creditreport`。这些模块更像消费授信事实的风险分析/报送/评级能力。
- 不把 `credit` 合并进 `application`。application 可以持有授信与项目、流程、材料、合同风险敞口之间的编排，但授信立项、授信评审和额度占用/释放规则属于 credit 自己的业务事实。
- 把 `credit` 定义为 risk-credit 大域中的核心事实源：授信立项、授信评审、授信额度。
- 后续整理优先级不是物理合并，而是区分 `application/orchestration/credit/groupcredit` 中哪些是单域授信逻辑，哪些是真正跨域编排。本轮已先把 application 里历史顶层 `groupcredit` 应用编排、adapter 和集团授信建立 facade 收敛到 `credit/groupcredit` 语义下，不改变 credit 模块边界。
- `fund` 对授信额度的调用可以保留，但应避免直接使用 credit mapper/model；额度服务和 BO 是目前较合适的边界。
- `creditreport` 对 `credit` 的依赖应视为报送读取授信事实，不反向推动 `credit` 合并到报送。

### creditreport

结论：`creditreport` 是征信查询/征信报送域，不是 `credit` 的子包，也不应并入核心授信域。

依据：

- 模块代码量较大，包含征信查询、征信报送、第三方接口、征信报告解析、还款计划、征信结果、报送材料、版本和大量征信 DTO。
- POM 当前只保留 `third`、`document` 等真实运行依赖；`customer`、`projectprocess`、`credit`、`contract`、`payment`、`workflow` 已无 Java 直接 import，也不再作为 `creditreport` POM 直接依赖。
- 代码层 import 证明剩余真实外域依赖主要集中在 `document` 材料文件/材料版本和 `third` 重试请求；项目、授信、客户、合同、付款等事实输入已经通过征信自有 port 或 application adapter 收窄。
- 关键服务 `CreditReportService` 保留征信工商比对和征信流程结束处理；客户、项目、付款、合同、授信上下文已通过征信自有 port 由 application 适配。
- 它拥有自己的征信报告、客户条目、结果、还款计划等模型，语义上是外部监管/第三方征信协作，不是授信本身。
- 模块已存在 `CreditReportPaymentPort`、`CreditReportMaterialPort`、`CreditReportClientSupportPort`，付款、材料、客户业务历史等协作已有部分 port 化。
- 本轮继续收窄 `CreditReportClientSupportPort` 的接口语言：新增征信自有 `CreditReportClientBusinessSnapshot`，port 不再返回 customer 模块的 `ClientBusinessHistoryBO`；application adapter 负责从 customer BO 转换为征信快照。
- 本轮继续收窄征信报送客户输入：新增征信自有 `CreditReportClientSnapshot`，`CreditSearchProjectDataServiceImpl` 不再直接注入 customer 的 `ClientMapper`、`CorpCommerceInfoMapper`，企业客户基础信息和中征码由 `CreditReportClientSupportPort` 输入，application adapter 负责从 customer 模型转换。随后 `CreditReportBaseInfoService.showCreditReportByClientId` 也改为通过 `CreditReportClientSupportPort.getClient` 获取客户快照，不再直接读取 customer 的客户/工商 mapper。
- 本轮继续收窄征信工商比对输入：新增征信自有 `CreditReportClientCommerceSnapshot`，`CreditReportSeriveImpl.compareBusiness` 不再直接读取 customer 的客户、工商、股东 mapper/model，也不再直接调用 `ClientBusinessHistoryService`；系统内工商快照和历史工商快照均通过 `CreditReportClientSupportPort` 输入，由 application adapter 转换 customer 模型。
- 本轮继续收窄 `CreditReportMaterialPort` 的接口语言：新增征信自有 `CreditReportMaterialSnapshot`，port 不再返回 document 模块的 `MaterialsList`；application adapter 负责从材料文件模型转换为征信材料快照。
- 本轮继续收窄合同输入：新增 `CreditReportContractPort`，`CreditReportBaseInfoService` 不再直接注入 `ContractBaseInfoService`、`ContractTradeStructureService` 或消费 `ContractBaseInfo`/合同状态枚举；合同 ID、合同租金到期日和合同客户集合由 `application.orchestration.adapter.creditreport.CreditReportContractPortAdapter` 适配。
- 本轮继续收窄项目/授信上下文输入：新增 `CreditReportProjectDataPort` 和 `CreditReportProjectSnapshot`，`CreditSearchProjectDataServiceImpl` 不再直接注入 projectprocess/credit 的 mapper/model；项目编号、项目名称和客户 ID 集合由 `application.orchestration.adapter.creditreport.CreditReportProjectDataPortAdapter` 适配。随后 `CreditReportBaseInfoService` 中用于合同到期计算的可用项目评审 ID、指定项目客户过滤、按客户反查项目列表，也统一改为通过该 port 获取。
- 本轮清理 `CreditReportSeriveImpl` 中已经不被当前 `CreditReportService` 接口调用的历史私有方法和字段，移除了由注释旧逻辑带来的 projectprocess/credit/document/customer 等假依赖噪音；该类真实运行路径现在聚焦 `compareBusiness` 与 `processEnd`。
- 已移除 `zswl-mithras-creditreport` 对 `zswl-mithras-contract` 的 POM 依赖；编译时暴露的 `OssClient` 隐藏依赖已改为显式声明 `oss-toolkit`，避免继续依靠 contract 的传递依赖。
- 反向依赖很广：粗略搜索显示 `report` 约 288 处、`web` 约 151 处、`application` 约 55 处、`api` 约 328 处。`report` 主要消费征信事实做征信报送快照，`application` 负责 workflow 结束事件和付款/材料/客户 port adapter。

发现的问题：

- `CreditReportSeriveImpl` 的历史注释块仍然很多，虽然不再形成编译依赖，但会干扰阅读；后续可以做一次更彻底的旧代码删除或把已迁移职责写清楚。
- `CreditReportSeriveImpl` 的工商比对规则仍留在征信域，这是合理的；customer/projectprocess/credit/contract/payment/workflow 的事实读取已经下沉到 application adapter 或征信自有 port。后续重点只剩 document 材料/版本事实读取。
- `CreditReportMysqlConfig` 使用 MyBatis-Spring 的 `@MapperScan`、MyBatis core 的 `Interceptor`、`SqlSessionFactory`、`JdbcType` 等类型，以及 MyBatis-Plus 的 `MybatisSqlSessionFactoryBean`；这些已由 `mybatis-plus-boot-starter` 依赖链覆盖，POM 中不再保留单独的 `mybatis-spring` 或 `org.mybatis:mybatis` 直接声明。
- `CreditReportBaseInfoService`、`CreditReportApiXJServiceImpl` 和 `versioning/CreditReportMaterialsListLibHandler` 仍直接使用 document 的 `MaterialsList`/mapper/query service；前面只收窄了 `CreditReportMaterialPort` 的返回类型，材料版本化和直接材料查询还需要后续更深治理。
- `CreditReportBaseInfoMapper.xml` 直接读取 `client`、`client_authority`，`CreditReportMapper.xml` 直接读取 `contract_base_info_lib`、`contract_tenantry_lib`、`payment_base_info`，属于 SQL 层读模型耦合。
- `creditreport` 对 `credit` 的依赖主要是读取集团授信立项/评审事实，用于征信报送上下文；这不代表二者同一生命周期。
- `creditreport` 曾直接依赖 payment/contract 只为从付款单定位合同和项目客户上下文；现在由 `CreditReportPaymentPort` 暴露“付款单对应合同 ID”和 `CreditReportPaymentProjectSnapshot`，由 `CreditReportContractPort` 暴露合同客户和到期日事实，application adapter 复用 payment/contract 现有 Mapper/Service 实现。
- 已移除 `creditreport -> workflow`：`CreditReportService` 不再继承 workflow 的 `FlowEndEventProcessor`，只暴露征信域自己的流程结束业务方法；workflow 结束事件由 application 的 `CreditReportSelectProcessEndHandler` 适配并委托到 creditreport。
- `versioning/CreditReportMaterialsListLibHandler` 仍直接依赖 document 材料版本 mapper/model/proxy，说明材料版本化逻辑还在征信域内；后续可评估是否改为 document 提供更窄的材料版本 port。

处理建议：

- 保持独立 Maven 模块，归入 risk-credit 大域中的外部征信/报送子域。
- 不并入 `credit`，也不把 `credit` 的核心授信规则搬进 `creditreport`。
- 后续治理重点是把 `CreditReportBaseInfoService` / `CreditReportApiXJServiceImpl` / `CreditReportMaterialsListLibHandler` 中剩余 document 材料事实读取继续收敛为明确的查询服务、快照 DTO 或 port。
- 编译验证：`mvn -pl zswl-mithras-creditreport -am -DskipTests compile` 通过，且 reactor 已收敛为 root、api、foundation、third、document、creditreport 6 个模块；`mvn -pl zswl-mithras-application -am -DskipTests compile` 通过，证明征信 contract/project data/payment/client port、application adapter、以及 `CreditReportSeriveImpl` 旧代码依赖清理均能闭环。
- 若后续需要拆分，应优先区分“征信查询/报告解析”和“征信报送流程”，而不是并入授信。

### rating

结论：`rating` 是客户评级/债项评级域，应保持独立，但当前对 `workflow`、`customer` 和 `basedata` 的直接依赖仍偏重。

依据：

- 模块拥有客户评级、债项评级、评级报告、评级快照、区域指标、外部决策引擎 client、版本记录等完整生命周期。
- `RatingClientService` 负责客户评级流程、评分结果、客户基础信息、审批和版本；`RatingAmountService` 负责债项评级、项目评审信息、评级额度和流程。
- 评级本质上消费客户、项目、基础数据和外部决策模型，不是 `riskcontrol` 的附属功能。
- 它通过 `RatingClientSupportPort`、`RatingNotificationPort` 等接口已经开始表达外部事实/通知需求，这是好的方向。
- 当前 POM 直接依赖 `basedata`、`customer`、`workflow`，未发现 `rating -> riskcontrol` 的直接 POM 依赖；`projectprocess` 直接依赖已移除。
- 代码层 import 粗分显示，除 `dto`、`rating` 自身、`foundation` 和 `api` 外，主要外域依赖集中在 `customer` 约 11 处、`workflow` 约 12 处、`basedata` 约 4 处；未发现 `rating -> riskcontrol`、`rating -> creditreport`、`rating -> projectprocess` 的直接 Java 依赖。
- mapper XML 主要围绕评级区域指标表 `rzy_dm_calculate_indicator` 查询，没有明显跨业务域 join；真正耦合集中在 Java service 层。
- 外部决策和数据 API 依赖是真实依赖：`DecisionApiClient` 使用 `decision-engine-api`，`RatingManagementClient` 使用 `fuxi-data-api`，`PublicInfoApiClient`、`ProvidenceCustomerUnifiedViewApiClient` 使用 `providence-api`。
- 反向依赖较广：粗略搜索显示 `web` 约 230 处、`application` 约 127 处、`kpi` 约 39 处、`budget` 约 17 处，另有 `customer`、`projectprocess`、`credit` 等少量消费。评级结果是下游经营、预算/ECL、项目评审和客户视图输入事实。

发现的问题：

- `RatingClientService` 仍直接注入客户、基础数据、workflow mapper/API、决策引擎 client，单类承担过多客户评级申请和跨域装配；`RatingAmountService` 已将项目评审上下文与债项评级客户事实收敛为端口。
- `rating` 直接实现 `FlowEndEventProcessor` 并使用 workflow 细节，说明流程装配逻辑仍在业务域内。
- `application`、项目评审、预算/ECL 等模块反向消费评级 service/model/feign，语义上大多合理，但对外暴露面仍偏粗。
- `FactoryMysqlConfig` 使用 MyBatis-Spring 的 `@MapperScan`、MyBatis core 的 `Interceptor`、`SqlSessionFactory`、`JdbcType` 等类型，以及 MyBatis-Plus 的 `MybatisSqlSessionFactoryBean`；这些已由 MyBatis-Plus/tk-mybatis 依赖链覆盖，POM 中不再保留单独的 `mybatis-spring` 或 `org.mybatis:mybatis` 直接声明。

处理建议：

- 保持独立 Maven 模块，归入 risk-credit 大域中的评级子域。
- 不并入 `riskcontrol`；评级产出可以被风控、授信、项目评审使用，但评级生命周期应独立。
- 优先治理 `rating -> workflow/customer` 的直接依赖，把流程结束、通知、客户评级申请页客户详情等组装逐步移到 application adapter 或更窄 port。债项评级客户基础快照、营业收入、集团成员和未结清客户筛选已通过 `RatingAmountClientFactPort` 输入。
- `basedata` 作为评级输入事实可以短期保留，但应避免 rating 直接理解过多基础资料表结构。
- rating 内部保留评级模型、评级快照、评级规则调用和评级结果生命周期。

### riskcontrol

结论：`riskcontrol` 是风险监控/策略/预警/报告集合域，当前像一个风险运营平台，不适合再合并别的风险模块；反而要防止它变成新的大而全。

依据：

- 模块包含风险策略、指标计算、指标订阅器、预警监控、舆情监控、相关客户、集中度、风险报告、评分卡、客户沙盘文件、动态表单和 job。
- POM 依赖 `workflow`、`basedata`、`customer`、`contract`、`projectprocess`、`assetclassify`、`payment`、`collection`，说明它以多域事实作为风险监控输入。
- 代码层 import 也证明这些不是空依赖：当前模块有 201 个 Java 文件，一级包分布约为 `metric` 49、`scorecard` 25、`report` 25、`common` 20、`concentration` 12、`strategy` 9、`controller` 8、`application` 8、`job` 7；外域直接 import 约 `customer` 86 处、`projectprocess` 30 处、`workflow` 14 处、`payment` 14 处、`contract` 13 处、`collection` 11 处、`assetclassify` 10 处、`basedata` 6 处。
- 技术依赖也有源码证据：Guava EventBus 用于指标事件订阅，JSch 用于客户名单 SFTP，flow-core/flowable 用于预警流程查询和动态表单，MapStruct、XXL Job、FastJSON、Swagger、validation、javax.annotation、commons-lang3 均有明确使用点；直接 `org.mybatis:mybatis` 无独立必要性已移除。
- `RiskControlStrategyService` 直接读取项目立项、付款、客户行业、项目价格信息，用于风险指标拦截和计算。
- `RiskControlWarnMonitorService` 管理预警处置、流程状态统计、外部风险系统链接和处理意见，语义上是风险运营闭环。
- `RemainingPrincipalServiceImpl`、`RiskControlRelatedClientService`、`RiskControlGljyReportService`、`RiskControlJzdReportService` 直接读取合同、付款、收款、客户、项目评审事实，说明风险报告/指标需要多域事实输入。
- Mapper XML 中 `RiskControlOpinionMonitorMapper.xml`、`RiskControlWarnMonitorMapper.xml` 直接 join `client`、`bifrost_org`，SQL 层也存在客户和组织读模型耦合。
- `sql/风控应用/dml.sql` 中包含黑灰名单菜单、入库/出库/综合查询等 UI 权限数据，说明历史上风控应用和黑灰名单入口有交叠；这只能说明菜单归属需要整理，不能证明 `blackgray` 应合并进 `riskcontrol`。
- 反向依赖很广：粗略搜索显示 `application`、`dashboard`、`web`、`api` 是主要消费方，另有 `customer`、`projectprocess`、`credit`、`contract`、`payment`、`budget`、`metric` 等少量引用。`application` 编排风控流程、job、客户监控、看板和文件校验；`dashboard`/`web` 消费风险监控和看板入口。
- 客户监控列表所需的风险客户集合、红黄灯数量和舆情数量已回收到 riskcontrol mapper/service；customer 不再为了客户监控列表直接读取 riskcontrol 表。

发现的问题：

- `riskcontrol` 内部语义过宽：策略指标、外部预警、舆情、集中度、风险报告、评分卡、动态表单都放在一个 Maven 模块里。
- 直接依赖 `assetclassify`，容易让风险监控域和资产五级分类流程互相缠住。资产分类更像独立资产质量评审域，不宜被吞进 riskcontrol。
- 模块里存在较多 workflow 动态表单/流程处理类，这些更像装配层或流程适配层，长期放在风险域内会让横向流程细节侵入业务域。
- 指标计算器直接读取外域模型/mapper 的风险较高，容易让风险策略和交易域表结构绑定。
- `metric -> riskcontrol` 与 `riskcontrol -> 多交易域` 的组合曾经使风险指标链路跨模块较深；当前 `metric -> riskcontrol.strategy` 已通过 `RiskStrategyCurrentValuePort` 输入和 application adapter 完成第一轮隔离。后续应明确 metric 负责指标事实/口径聚合，riskcontrol 负责风险策略和风险运营，不应互相吞并。

处理建议：

- 短期保持独立 Maven 模块，但不要再把 `credit`、`rating`、`assetclassify`、`blackgray` 合进来。
- 把它定位为 risk-credit 大域中的风险监控/风险运营平台，而不是整个风险大域本身。
- 后续优先做模块内分区治理：`strategy/metric`、`warning/opinion`、`concentration`、`report`、`scorecard` 应保持清晰边界。
- 逐步把外域事实读取收敛成风险指标输入模型或查询 port，避免风险指标直接追随项目、付款、合同、客户表结构变化。
- workflow 动态表单/流程结束适配长期应迁到 `application`；`riskcontrol` 只暴露预警/舆情处置、报告生成、策略计算等业务方法。
- 如果未来确实拆分，优先按 `strategy/metric`、`warning/opinion`、`concentration/relation`、`report`、`scorecard` 这些已成形边界拆，而不是把整个模块并入其他风险模块。

### assetclassify

结论：`assetclassify` 是资产五级分类/资产质量评审域，和 riskcontrol 相关但不应简单并入 `riskcontrol`。

依据：

- 模块拥有资产分类主表、分类客户、检查内容、节点记录、风险因子模板、版本与复核流程等模型。
- 业务生命周期是资产分类初始化、检查/复核、节点流转、材料留痕、自动通过和工作日提醒，和普通风险预警监控不同。
- POM 依赖 `document`，说明它需要材料事实判断资产质量；对客户关联关系、合同事实、收款核销事实和付款事实的读取已改为 port，由 application adapter 承接。
- 代码层面复核显示，Java import 主要集中在 `assetclassify`、`foundation`、`dto`，少量直接引用 `document`；原先直接读取 contract mapper/model 的 `AssetClassifyClientWithdrawalRatioService`、`AssetClassifyClientRiskFactorService` 已改为通过 `AssetClassifyContractFactPort` 获取合同编号、业务类型、租赁类型、剩余租金期数和借据存在性，原先唯一直接读取 payment mapper/model 的 `AssetClassifyClientWithdrawalRatioService` 已改为通过 `AssetClassifyPaymentFactPort` 获取付款事实。
- 模块内已出现 `AssetClassifyMarginAmountPort`、`AssetClassifyClientRelationPort`、`AssetClassifyContractFactPort`、`AssetClassifyCollectionWriteOffPort`、`AssetClassifyPaymentFactPort` 等端口，说明客户关联、合同事实、保证金金额、收款核销金额、付款事实这类跨域事实输入正在被隔离。
- 自动通过超时监听只表达“到期后尝试自动处理”的资产分类业务意图；流程是否运行中、当前节点任务查询和 flow-core 自动提交调用已收敛到 `AssetClassifyAutoPassExecutionPort` 的 `application` adapter 中，资产分类模块源码和 POM 都不再直接依赖 workflow 或 flow-core。
- 按 Java/POM/Mapper/SQL 粗略反向搜索，外部消费主要来自 `application` 约 120 处、`web` 约 33 处、`metric` 约 12 处、`riskcontrol` 约 11 处。metric/riskcontrol 消费资产分类结果做风险指标或风险监控，finance 在利润测算中消费风险分类版本数据，afterlease 读取最近分类结果作为租后检查输入。

发现的问题：

- 通知、流程模型 key 等剩余流程协议仍需继续评估，防止流程技术细节重新侵入资产分类域。
- 自动通过 listener 已不再直接使用 flow API/task mapper，流程运行状态判断、当前任务查询和自动提交调用迁到 application adapter；flow-core POM 依赖也已移除。后续仍要关注通知、流程模型 key 等剩余流程协议是否继续侵入资产分类域。
- `riskcontrol` 直接依赖 `assetclassify`，两者之间应明确为风险监控消费资产分类结果，而不是风险监控拥有资产分类流程。
- `sql/asset_classify/init.sql` 曾夹带 `payment_base_info`、`payment_base_info_lib`、`contract_base_info`、项目表索引、基础字典字段、租后检查模板和 FTP 指导表等外域结构变更；当前已将 payment 备注字段迁回 `payment`，将项目索引迁回 `projectprocess`，将合同索引迁回 `contract`，将岗位字典扩展迁回 `basedata`，将资产分类非公开租后检查模板迁回 `afterlease`，将老版 FTP 指导表迁回 `ftp`。

处理建议：

- 保持独立 Maven 模块，归入 risk-credit 大域中的资产质量/五级分类子域。
- 不并入 `riskcontrol`；二者语义相邻但生命周期不同。
- 不并入 `finance`；finance 只是消费资产分类版本作为利润测算输入。
- 已完成 workflow/flow-core 自动通过与节点任务处理的源码和 POM 解耦；后续治理重点是把通知、流程模型 key 等剩余流程协议继续收敛到 application 或更窄的 port adapter。
- 已完成 `assetclassify -> customer/contract/collection/payment` 依赖瘦身：客户关联查询、合同事实、收款核销金额和付款事实改由 `application` 中的 adapter 实现，`assetclassify` 模块自身不再直接依赖 customer/contract/collection/payment。
- 继续检查资源目录中的历史 SQL 和 mapper，防止外域 DDL 再次混入；已迁出的外域脚本由对应模块维护。

### capital

结论：`capital` 是财务资金大域的资金流水与核销子域，但当前还不是一个自足的业务域模块。

依据：

- `zswl-mithras-capital` 自身 POM 只依赖 `api`、`foundation` 和框架 provided 依赖，表面低耦合。
- 模块内有 controller、application interface、job interface、枚举、Excel model/exporter，以及 `finance_flow_write_off_detail` 这一类核销明细持久化。
- 代码层面看，capital 模块自身的 import 主要是 `dto`、`foundation`、`capital` 内部类型和 `api`，没有直接 import 其他业务域，说明当前模块壳边界很干净。Java import 分布约为 `dto` 52 处、`foundation` 21 处、`capital` 15 处、`api` 14 处。
- 模块源码约 45 个 Java 文件，资源层 Mapper XML 只围绕 `finance_flow_write_off_detail` 核销明细表。
- 但 controller 依赖的 `*ApplicationService` 基本只是继承 API 接口，真正实现放在 `application/orchestration/facade/capital`。
- 资金流水处理的大块核心逻辑也在 `application/orchestration/capital`，例如 `BankFlowProcessingCenterService`、`BankFlowProcessingCenterFinanceService`、`BusinessFlowService`、`FinanceFlowAutoWriteOffService`、`FinanceFlowRecordService`。
- `application/orchestration/capital` 当前有 13 个核心实现/模型类。
- 这些实现大量触达合同、收款、付款、保证金、融资、第三方财务共享、风险指标、Redis 锁等，说明它既有资金流水子域逻辑，也有跨域编排逻辑。
- 对 application 中 capital 编排代码做 import 粗分，外部依赖主要集中在 `third` 66 处、`foundation` 64 处、`capital` 63 处、`application` 59 处、`fund` 56 处、`dto` 30 处、`contract` 22 处、`payment` 20 处、`margin` 16 处、`collection` 16 处，另有少量 `system`、`customer`、`riskcontrol`、`finance`、`basedata`。这证明它不是一个可以直接整体回迁的单域实现。
- 已对 `application/orchestration/capital` 做按类标注：
  - `FinanceFlowRecordService` 最接近 capital 自身事实，但仍直接使用 `third` 的流水持久化模型、财资平台 API、宝融同步、基础账户和系统用户；可作为回迁候选，但要先定义 capital 自有流水模型和同步 port。
  - `BankFlowProcessingCenterService` 是项目侧流水处理中心，直接触达客户、合同、收款、付款、保证金、融资、财务经营、第三方财资和风险指标；整体应留在 application，只能抽出纯流水状态和核销明细写入小块。
  - `BankFlowProcessingCenterFinanceService` 是融资侧现金流读侧组装，直接读取 fund 的间融、直融、还本付息、费用和保证金模型；应留在 application 或改成 fund/capital 窄输入。
  - `BusinessFlowService` 围绕 fund 业务流水和 third 苍穹推送，属于 fund/capital/third 编排；应留在 application。
  - `FinanceFlowAutoWriteOffService` 是最复杂的跨域核销编排，直接写 collection、payment、margin、fund 并推送 third；不能整体回迁，只能后续抽出金额分配、流水状态、核销明细落库等 capital 自有规则。
  - `write_off` 下的自动/手工核销策略依赖 third match/tab 模型、collection、contract、customer、fund，当前也应留在 application。
- 按 Java/POM/Mapper/SQL 粗略反向搜索，主要消费方包括 `application` 约 119 处、`web` 约 104 处、`fund` 约 73 处、`payment` 约 45 处、`collection` 约 29 处、`third` 约 28 处、`margin` 约 10 处。

发现的问题：

- `capital` 模块目前更像“API/controller 壳 + 少量公共枚举/明细表”，而不是完整承载资金流水业务规则的模块。
- 很多模块直接 import `capital.enums`，尤其是 `FinanceCashFlowItemEnum`、`FinanceFlowDetailTableEnum`、核销排序/状态枚举。当前反向搜索显示，除 application 外，fund、payment、collection、margin、third、web 测试和若干读侧/报表场景仍会消费这些枚举或核销明细服务。这些枚举可能是资金大域公共语言，但也可能被过度共享；后续要判断它们应继续属于 capital，还是上移为 finance 大域的稳定协议。
- 已移除 `associationreport` 中对 `capital.enums` 的隐藏代码引用：原先仅为获取 `REPAY`/`PRINCIPAL` 这类报送口径稳定编码，现改为 associationreport 本地报送常量。
- 已移除 `liquidity -> capital`：原先仅为获取 `REPAY` 现金流项目编码引用 capital 枚举，现改为 liquidity 本地稳定编码常量。
- 已移除 `metric -> capital`：原先仅为获取 `REPAY` 现金流项目编码引用 capital 枚举，现改为 metric 本地稳定编码常量。
- `application/orchestration/capital` 中混有单域资金流水规则和跨域编排，后续需要拆分判断。
- 直接把 application 中 capital 实现搬回 capital 会把 third/fund/contract/payment/collection/margin/riskcontrol 等依赖带进 capital，反而污染当前干净的 POM 边界。

处理建议：

- 不建议第一步把 `capital` 物理并入 `finance`，也不建议先大规模搬代码。
- `capital` 可以作为 finance 大域下的“资金流水/核销子域”继续独立一段时间；是否最终并入 finance，要等资金流水事实和跨域编排拆清后再判断。
- 当前目标是补实 `capital` 自身事实，而不是为了减少 Maven 数量先合并。
- 下一步应先按类分层标注 `application/orchestration/capital`：
  - 只操作资金流水、核销明细、资金流水状态的逻辑，候选回迁到 `zswl-mithras-capital`。
  - 同时操作合同、收款、付款、保证金、融资、第三方财务共享的逻辑，继续留在 `application` 做跨域编排。
- 回迁前要先设计 `capital` 自己的 port，避免把合同/收款/付款/保证金依赖直接带进 `capital`。
- 收敛外部模块对 `capital.enums` 的直接使用：稳定编码可以在消费方本地表达，真正属于资金流水公共语言的枚举再保留。
- `capital` 是后续代码级重构优先级较高的模块，但不是当前第一批物理合并对象；短期目标是“补实边界”，不是“减少 Maven 模块数”。
- 下一轮如要真正动代码，优先从 `FinanceFlowRecordService` 开始拆：
  1. 在 capital 中定义自有的流水快照/流水记录接口，避免继续把 `third.financialshare.persistence.model.FinanceFlowRecord` 当作 capital 内部模型。
  2. 把 `saveFlowRecord` 的去重、默认展示状态和流水入库规则迁成 capital 自有服务。
  3. 把 `fullSync` 中的平台 API 查询、临时表、宝融同步留在 application/third adapter。
  4. 把 `move2NoHandle` 中系统用户和基础账户判断改成 application 输入的规则结果或 capital port，避免回迁时直接依赖 `gruul`、`basedata` 和 `system`。

### liquidity

结论：`liquidity` 是流动性风险/资金指标读侧模块，不是适合继续扩大写职责的核心交易域。

依据：

- `zswl-mithras-liquidity` 当前 POM 已只保留 `api`、`foundation` 和技术依赖；对 `basedata`、`collection`、`fund` 的直接 Maven 依赖已经移除。
- 代码层复核显示，Java import 中已无 `cn.zswltech.mithras.basedata`、`cn.zswltech.mithras.fund`、`cn.zswltech.mithras.collection`。基础账户和工作日差已经通过 `FundTransferBaseDataPort` 表达，具体 basedata mapper/service 调用迁到 `application.orchestration.adapter.liquidity.FundTransferBaseDataPortAdapter`。
- 模块内部有较完整的计算器体系：账户余额、流动性看板、错配、指标、现金流入/流出、短期借款等。
- `LiquidityIndicatorHolder`、`LiquidityIndicatorIndexHolder`、`LiquidityIndicatorBoardHolder` 等 holder 已不再直接持有 fund/basedata 持久化模型；合同、授信、收款、还本付息现金流、直融主体、直融实际还款、间融主体、融资机构、间融还款账户、质押监管、基础账户和特殊日期已先收敛为 liquidity 自有快照。
- `application/orchestration/liquidity/LiquidityDataService` 负责从合同、收款、融资、授信、基础数据等模块装载数据，转换为 liquidity 自有 snapshot 后再喂给计算器。
- 当前 application 侧 liquidity 相关实现约 18 个 Java 文件，import 粗分显示仍直接装载外域事实：`liquidity` 99、`fund` 78、`dto` 54、`application` 48、`foundation` 34、`contract` 29、`basedata` 18、`collection` 12，另有少量 `projectprocess`、`payment`、`workflow`、`system`、`capital`。这说明 application 仍是跨域事实装载层，而不是可以整体回迁到 liquidity 的单域实现。
- 部分 application service 实现在 `liquidity` 模块内，例如 `FundTransferService`；大量指标/日报/还款/风险配置 facade 和数据装载逻辑在 `application`。
- Mapper XML 中原 `FundFinancingAccountSettingMapper.xml` 直接 join `fund_financing_base_info`、`fund_financing_plan`、`fund_direct_financing_base_info`、`fund_financing_credit_ref`、`fund_organization` 和 `bifrost_user`，说明融资账户设置读模型仍有 fund/system 表级耦合。本轮代码级复核确认该 `queryList` 只被 `application/orchestration/liquidity/FundFinancingAccountSettingService.accountSettingList` 调用；该 service 本身已经直接依赖 fund、basedata 和 liquidity 的模型/service，更像 application 层跨域账户设置编排。本轮已把这条跨域列表查询迁到 application 的 `AccountSettingListQueryMapper`，liquidity 自身 mapper 只保留 `fund_financing_account_setting` 自表 CRUD；随后把 application 内历史 `liquiditymanage` 包名统一收敛为 `liquidity`，不改动外部 API/DTO 契约包名。
- 反向依赖较少，主要集中在 `application` 的 adapter、facade、数据装载和账户设置编排；`web`、`foundation` 仅少量引用。这说明 liquidity 更像被 application 装载数据后独立计算的分析模块，而不是被多个交易域强依赖的事实源。

发现的问题：

- `liquidity` 的计算器外域 model 输入已明显收敛；合同错配、授信额度、收款、还本付息现金流、直融主体、直融实际还款、间融主体、融资机构、间融还款账户、质押监管、基础账户和特殊日期已改为本模块快照对象。
- `LiquidityDataService` 是事实装载中心，但它在 `application` 里直接填充 `liquidity` 的静态 holder，边界上有明显历史耦合。
- `liquidity` 的业务语义横跨资金、融资、合同回款和授信，适合作为 finance/risk 之间的读侧分析域，不适合承担外域主数据维护；跨 fund/system 的融资账户设置列表已经迁到 application 读侧 mapper。
- 可用授信指标已先落地为 liquidity 自有 `LiquidityCreditLimitSnapshot`，授信 BO 只在 application 装载层出现。
- 合同错配指标已落地为 liquidity 自有 `LiquidityContractBaseSnapshot` 和 `LiquidityContractRentSnapshot`，合同 model 只在 application 装载层出现。
- 账户分类口径已先落地为 liquidity 自有 `LiquidityBankAccountType`，计算器不再直接引用 `BaseDataBankAccountTypeEnum`。
- 收款输入已落地为 liquidity 自有 `LiquidityCollectionPlanSnapshot` 和 `LiquidityCollectionRecordSnapshot`，collection 持久化模型只在 application 装载层出现。
- 还本付息现金流输入已落地为 liquidity 自有 `LiquidityFundReceiptRepaySnapshot`、`LiquidityFundReceiptFlowPlanSnapshot`、`LiquidityFundReceiptFlowDetailSnapshot`，fund receiptrepay 持久化模型只在 application 装载层出现。
- 直融主体和直融实际还款已落地为 liquidity 自有 `LiquidityDirectFinancingSnapshot`、`LiquidityDirectFinancingRepayActualSnapshot`，直融持久化模型只在 application 装载层出现。
- 间融还款账户已落地为 liquidity 自有 `LiquidityFinancingPayAccountSnapshot`，`FundFinancingPayAccount` 只在 application 装载层出现。
- 质押监管已落地为 liquidity 自有 `LiquidityFinancingPledgeSnapshot`，`FundFinancingPledgeInfo`、`FundDirectFinancingPledgeInfo` 只在 application 装载层出现。
- `liquidity` 已移除 `basedata`、`collection`、`fund` POM 依赖；源码层不再直接引用这些模块的持久化模型或 service。
- `FundTransferService` 已改为通过 `FundTransferBaseDataPort` 获取监管账户列表和工作日差，basedata 适配实现放在 application。
- 原资源层剩余耦合是 `FundFinancingAccountSettingMapper.xml` 直接 join fund/system 表，属于融资账户设置列表的读模型耦合。本轮已按现有 application 读侧 mapper 模式迁到 `application.orchestration.adapter.liquidity.mapper.AccountSettingListQueryMapper`，对应 XML 放入 `mapper/application/AccountSettingListQueryMapper.xml`；`liquidity` 模块不再维护这条跨域 SQL。

处理建议：

- 短期保持独立 Maven 模块，不建议合并进 `capital` 或 `fund`；它的上位归属可以放在 finance 大域治理。
- 后续优先把 application 中的融资账户设置列表 SQL 继续拆成显式快照组装：liquidity 只提供 `fund_financing_account_setting` 配置记录，fund/system 提供融资摘要、机构摘要和资金经理姓名快照。
- `LiquidityDataService` 可以继续留在 `application` 作为跨域事实装载器，但输出不应是外域 model holder，而应逐步转成 liquidity 的内部 snapshot。
- `liquidity` 的计算器体系值得保留，重构重点是隔离输入模型，不是合并模块。
- 已对 holder 和装载层做代码级复核：`LiquidityIndicatorHolder`、`LiquidityIndicatorIndexHolder`、`LiquidityIndicatorBoardHolder`、`LiquidityIndicatorMismatchHolder` 不再保存 fund/basedata 持久化模型；账户和特殊日期由 application 装载后转换成 `LiquidityBankAccountSnapshot`、`LiquiditySpecialDateSnapshot`。
- 下一轮如要真正动代码，优先按输入模型替换顺序推进：
  1. 收款输入已完成：`CollectionBaseInfo`、`CollectionRecordInfo` -> `LiquidityCollectionPlanSnapshot`、`LiquidityCollectionRecordSnapshot`。
  2. 融资还款输入已完成：`FundReceiptRepayBaseInfo`、`FundReceiptFlowPlan`、`FundReceiptFlowDetail` -> `LiquidityFundReceiptRepaySnapshot`、`LiquidityFundReceiptFlowPlanSnapshot`、`LiquidityFundReceiptFlowDetailSnapshot`。
  3. 直融和账户输入已完成：`FundDirectFinancingBaseInfo`、`FundDirectFinancingRepayActual`、`FundFinancingPayAccount` -> `LiquidityDirectFinancingSnapshot`、`LiquidityDirectFinancingRepayActualSnapshot`、`LiquidityFinancingPayAccountSnapshot`。
  4. 质押监管输入已完成：`FundFinancingPledgeInfo`、`FundDirectFinancingPledgeInfo` -> `LiquidityFinancingPledgeSnapshot`。
  5. 间融主体和机构输入已完成：`FundFinancingBaseInfo`、`FundOrganization` -> `LiquidityFinancingSnapshot`、`LiquidityFinancingOrganizationSnapshot`。
  6. 基础账户和特殊日期输入已完成：`BaseDataBankAccount`、`BaseDataSpecialDate` -> `LiquidityBankAccountSnapshot`、`LiquiditySpecialDateSnapshot`，租金回流计算改为 liquidity 本地 `LiquidityWorkdayCalendar`。
  7. 基础账户和工作日差服务依赖已完成：`BaseDataBankAccountMapper`、`BaseDataSpecialDateService` -> `FundTransferBaseDataPort`，adapter 放在 application。
- 融资账户设置列表的 fund/system join SQL 已迁到 application 读侧 mapper。后续若继续瘦身，应由 fund/system 提供查询快照，在 application 组装列表结果，逐步替代当前 application SQL。

### archives 与 filingmaterials

结论：二者同属 records 上位域，但短期不建议物理合并。

依据：

- `archives` 的核心语义是档案进入正式档案生命周期后的管理：档案模板、档案类型、档案审批、档案借阅、下载权限、下载原因和档案搜索。
- `archives` POM 低耦合，只依赖 `api`、`foundation` 和框架 provided 依赖；模块内没有直接 import 其他业务域。
- `archives` 通过 `ArchivesWorkflowPort`、`ArchivesNotificationPort`、`ArchivesSupportPort` 使用流程、消息、项目、材料文件和组织用户信息，adapter 放在 `application/orchestration/adapter/archives`，方向合理。
- `archives` 的 Java/POM 边界干净，但 mapper XML 仍直接读取 `materials_list` 和 `proj_establish_base_info`，这是正式档案读模型为了展示文件/项目事实保留的表级耦合。
- `filingmaterials` 的核心语义是资料进入正式档案前的归档准备：项目/资金/租后/其他资料归档、资料目录、归档流程、台账、批量下载和材料生成。
- `filingmaterials` 依赖 `document`，并包含 `gendoc/render`，说明它会使用文档模板能力生成归档材料。
- `filingmaterials` 的 Java/POM 边界较轻，但 mapper XML 仍直接读取项目、资金、合同和流程历史表，这是归档准备台账为了展示业务事实保留的表级耦合。
- `filingmaterials` 的主实现大量在 `application/orchestration/filingmaterials` 和相关 facade/end handler 中，因为它需要接入项目、资金、租后、付款、流程等业务场景。
- `document` 是 records 上位域中的横向文件能力，负责文件、材料清单、模板、OnlyOffice、OCR、材料版本和文件导出。代码层复核显示，`document` 自身没有直接 import `archives`、`filingmaterials` 或其他业务域；`filingmaterials` 对 document 的依赖主要集中在 4 个 gendoc render 对 `FileTemplateService` 的使用。
- 三者的源码规模和包结构也体现出不同生命周期：`document` 约 59 个 Java 文件，集中在模板、文件、材料和 OnlyOffice；`filingmaterials` 约 53 个 Java 文件，集中在资料归档模型、枚举、台账、job 和 gendoc；`archives` 约 27 个 Java 文件，集中在档案模板、档案管理、下载权限和 archive persistence。

发现的问题：

- 两个模块在命名上容易混淆：`filingmaterials` 里有 `ArchivedMaterialsDownloadRecord`、台账 DTO 中也有 `ArchivedMaterials` 表述，容易让人以为它已经是正式档案域。
- `archives` 的 mapper XML 直接读 `materials_list where business_type = "ARCHIVES"`，说明档案文件仍复用 document/materials 的文件清单表。短期可以接受，但应明确：文件存储和材料清单不是 archives 的主生命周期。
- `filingmaterials` 对 document 是直接依赖，这符合“材料生成/下载需要文档能力”，但要避免把 document 模板渲染细节继续扩散到更多业务域；同时它的项目/资金/合同/流程台账 SQL 应记录为后续收敛项。
- `document` 的 `FileTemplateEnum` 和若干模板名称包含具体业务模板名，这是横向文件能力承载业务模板资源造成的历史痕迹；新增业务语义时应避免把业务规则也放进 document。

处理建议：

- 保持 `document`、`archives` 与 `filingmaterials` 独立 Maven 模块，先在 records 上位域内治理。
- 后续可以考虑重命名或文档上明确：
  - `document` = 文件/材料/模板/OnlyOffice/OCR 横向能力。
  - `filingmaterials` = 归档准备/资料归档流程。
  - `archives` = 正式档案管理/借阅/下载权限。
- 暂不把 `filingmaterials` 合并进 `archives`，也不把二者并入 `document`；三者生命周期不同，合并后容易把“文件能力”“归档前流程”和“归档后管理”搅在一起。
- `filingmaterials` 更高优先级的工作是从 `application` 中区分单域资料归档逻辑和真正跨域流程编排，而不是物理合并。

## 反模式

后续整理时要避免这些情况：

- 为减少模块数量，把所有财务相关代码粗暴塞进 `finance`，导致 `finance` 变成新的大而全。
- 横向能力模块直接适配业务域，例如 `message` 直接依赖 `workflow` 或具体业务模块。
- 核心业务域反向依赖 read-side 聚合模块，例如 `contract` 依赖 `dashboard`。
- 用 `api` 承载所有跨模块 DTO，导致 `api` 变成业务模型垃圾桶。
- 用 `foundation` 承载业务枚举和业务规则，导致底座污染。
- 物理合并后不清理包名，留下历史模块名作为假边界。

## 下一步建议

## 本轮推进记录

本轮继续按“业务语义 + 代码依赖 + 资源表耦合 + 编译验证”推进，结论如下：

| 模块 | 本轮结论 | 验证 |
| --- | --- | --- |
| `api` | 公共契约模块保持独立，不参与业务域物理合并；本轮确认源码无业务模块 import、无 resources 文件，POM 依赖大多有契约表达证据。已移除无必要的 `slf4j-api` 依赖，并清理 `DashboardFundFinanceBaseREQ` 中 DTO 层 `@Slf4j` 日志副作用。剩余风险是 `api` 体量过大，且少量 DTO 仍混入 MyBatis-Plus 注解/分页类型，后续应逐步下沉单域 DTO、消除持久化味道。 | `mvn -pl zswl-mithras-api -am -DskipTests compile` 通过，reactor 仅 root、api；静态统计约 2600 个 `dto` 文件、490 个 `api` 文件，源码 import 未发现业务模块包。 |
| `foundation` | 技术底座保持独立，不参与业务域物理合并；本轮补齐代码级边界复核，确认源码没有具体业务域 import，POM 技术依赖均有使用证据。剩余风险不是 Maven 依赖，而是业务语义下沉：`Constant`、`FileExportEnum`、`CacheEnum`、`JobEnum`、`RiskControlIndustryClassify`、`CommonMapper.xml` 权限 SQL 和客户/合同 resolver 都带有不同程度业务口径。短期不硬迁，后续按引用面从小到大迁出。 | `mvn -pl zswl-mithras-foundation -am -DskipTests compile` 通过；静态搜索确认 foundation 源码没有 import 具体业务域模块，资源层 `CommonMapper.xml` 暴露项目字段权限 SQL，典型共享业务语义引用面包括 `JobEnum` 164 个文件、`RiskControlIndustryClassify` 122 个文件、`FileExportEnum` 38 个文件、`CacheEnum` 28 个文件。 |
| `system` | 系统域保持独立，不参与业务域物理合并；它负责用户、组织、角色、权限、系统配置、审计、登录态、表达式、下载和系统运行基础设施。源码无具体业务域 import，POM 技术依赖均有系统能力使用证据。本轮只清理了 `ProjCodeStoreService` 中无效的事务 import。剩余观察点是 `ProjCodeStoreService` / `proj_code_store` 带有项目编号语义，但当前更像全局编号存储/唯一性服务，暂不迁到 `projectprocess`。 | `mvn -pl zswl-mithras-system -am -DskipTests compile` 通过，reactor 为 root、api、foundation、system；静态复核确认 system 源码内部 import 只涉及 api/foundation/system，resources 主要是 Gruul/系统表、功能组和项目编号存储 SQL。 |
| `application` | 应用装配层保持独立，不参与业务域物理合并；依赖多符合定位，负责跨域 facade、port adapter、流程动态表单、流程结束处理、事件监听、消息/文档/第三方系统接线。本轮补齐代码级画像，确认当前包结构已基本收敛到 `orchestration/<domain>`、`orchestration/facade/<domain>`、`orchestration/adapter/<domain>`、`orchestration/job/<domain>`、`orchestration/listener/<domain>` 等语义归组。剩余风险是 application 继续承载过多单域实现，以及 `src/main/resources/mapper/application` 成为新的跨域 SQL 集中地；后续应把只操作单域 mapper/model/service 的实现回迁，把跨域读模型继续拆成 owner domain 快照输入。 | 本轮静态复核显示 POM 直接依赖 34 个业务/横向模块，符合装配层定位；源码 import 主要集中在 `foundation` 2333、`application` 2107、`contract` 1772、`dto` 1756、`fund` 979、`customer` 887、`projectprocess` 881、`workflow` 674、`payment` 458、`document` 412 等。resources 目前只有 6 个 application mapper，分别承接融资账户设置、预算付款事实、收付中心付款流、保证金列表、付款流程列表、保单台账等跨域读模型。此前 `mvn -pl zswl-mithras-application -am -DskipTests compile` 已通过，38 个 reactor 模块 SUCCESS。 |
| `web` | 启动装配模块保持独立，短期不并入 application；它负责 Spring Boot 启动、环境配置、最终运行包、监控、Flyway 迁移和运行期资源装配。本轮删除未启用、未引用的废弃 `ResponseBodyHandler`，清掉 web 对合同提醒逻辑和 contract service 的假依赖信号；随后复核 POM 与生产源码 import，发现 `GlobalExceptionHandler` 直接使用 system 的系统开关、审计日志、用户名称解析和事件，已补显式 `web -> system` 依赖，避免继续依赖 application/report 的传递依赖。剩余风险是 web 内存在 `cn.zswltech.flow.core.api.FlowExecutionApiService` 这种 flow-core 覆写/补丁类，以及 `TestController`、`ApprovalTestInterceptor` 等调试入口；长期应迁回 workflow/flow-core 或受 profile/测试边界约束。 | `mvn -pl zswl-mithras-web -am -DskipTests compile` 通过，reactor 40 个模块全部成功；生产源码从 6 个 Java 文件降为 5 个。 |
| `liquidity` | 流动性风险/资金指标读侧模块属于 finance 大域的长期合并候选，但当前不物理并入 finance。Java/POM 层已经很干净，源码没有直接 import 其他业务域模块包；POM 中 MyBatis-Plus、Spring、`javax.annotation`、validation、Hutool、POI、Fastjson、Swagger annotations 均有源码使用证据，不是空依赖。`FundTransferService` 的基础账户和工作日差已通过 `FundTransferBaseDataPort` 由 application 适配。本轮继续把 `FundFinancingAccountSettingMapper.xml` 中直接 join fund 融资表、fund 机构表和 `bifrost_user` 的融资账户设置列表迁到 application 读侧 `AccountSettingListQueryMapper`，liquidity 自身 mapper 只保留自表 CRUD。当前问题不是 Maven 模块壳过细，而是读侧列表需要跨域事实组装。 | `mvn -pl zswl-mithras-liquidity -am -DskipTests compile`、`mvn -pl zswl-mithras-application -am -DskipTests compile` 均通过；本轮静态复核确认 `zswl-mithras-liquidity/src/main/java` 无 `cn.zswltech.mithras.fund/customer/collection/contract/payment/basedata` 等业务域 import；resources 层也已无 fund/system 表 join 残留，并已修复 web 测试代码中旧 `application.orchestration.liquiditymanage` import。下一步若继续治理，应把 application 中的融资账户设置列表 SQL 进一步拆为 liquidity 自有配置 + fund/system 快照输入，并由 application 组装；在此之前不建议合并。 |
| `message` | 横向消息/通知能力边界基本成立：POM 只依赖 `api`、`foundation`，源码/resources 无 workflow 或业务域模块 import，resources 只围绕消息自有表；业务通知调用主要由 application adapter/编排层接线，方向正确。当前不建议合并或拆分 message。剩余问题是 `NoticeSourceENUM`、`MessageTypeEnum` 仍承载大量业务通知类型，属于语义耦合，后续应逐步迁为业务域/application 提供的通知类型或配置注册。 | `mvn -pl zswl-mithras-message -am -DskipTests compile` 通过，reactor 仅 root、api、foundation、message；静态搜索确认 message 源码/resources 无 `cn.zswltech.mithras.workflow` 或业务域包 import。 |
| `document` | 横向文档/材料/模板/OCR/OnlyOffice 能力边界基本成立：POM 只依赖 `api`、`foundation`，源码/resources 无 workflow/message 或业务域模块 import；反向消费广泛，说明它不适合并入 contract/payment/afterlease/filingmaterials/archives 等单一业务域。本轮移除无独立必要性的直接 `org.mybatis:mybatis` 依赖，MyBatis 注解由 MyBatis-Plus starter 传递满足。剩余问题是业务模板和材料类型硬编码在 `FileTemplateEnum`、`FileTemplateKeyEnum`、`FileDownloadZipPathEnum`、`MaterialsEnum`、`MaterialsType` 等枚举，以及 `MaterialsListMapper.queryNeedSignFile`、`contractMaterialList`、`paymentMaterialList` 等接口中，属于语义耦合。 | `mvn -pl zswl-mithras-document -am -DskipTests compile` 通过，reactor 仅 root、api、foundation、document；静态搜索确认 document 源码/resources 无业务域包 import。 |
| `workflow` | 横向流程能力边界在 POM/源码 import 层基本成立：POM 只依赖 `api`、`foundation` 和流程/框架技术依赖，源码/resources 无 message/document/业务域包 import。当前不建议合并 workflow。剩余问题是业务流程语义集中在 workflow 内部：`ProcessModelTypeEnum`、`init_flow.sql`、审批流菜单 SQL、跟踪事项字段、流程变量/常量和若干流程准备 handle 都承载合同、付款、项目、授信、租后、资金、KPI、黑灰名单、预算、报送、保证金等业务流程注册。后续应把业务流程模型 key/准备数据/结束动作逐步迁到业务域或 application 注册/adapter 层，workflow 只保留流程引擎与通用扩展点。 | `mvn -pl zswl-mithras-workflow -am -DskipTests compile` 通过，reactor 仅 root、api、foundation、workflow。静态搜索确认 workflow 源码 import 只涉及 `api`、`foundation` 两个内部底座包；资源层发现业务流程 SQL/BPMN/菜单和字段语义耦合。反向搜索显示 application 是主要 workflow 装配层，方向正确；finance/contract/creditreport 仍有直接使用 workflow/flow-core 的治理候选。 |
| `basedata` | 基础业务数据底座边界成立，保持独立，不并入 system 或具体业务域。模块负责通用字典、特殊日期、LPR、汇率、银行账户、基础数据同步/提醒任务；源码无具体业务域 import，外部消息/待办/用户/项目校验/同步通过 basedata 自有 port 表达。 | `mvn -pl zswl-mithras-basedata -am -DskipTests compile` 通过，reactor 仅 root、api、foundation、basedata。静态复核确认 POM 只依赖 api/foundation 和真实技术依赖：MyBatis-Plus 用于 mapper/model/service，Hutool 用于日期/集合/Excel，`oss-toolkit` 用于 LPR 模板预览，XXL Job 用于汇率/特殊日期/LPR 提醒任务；resources 只维护基础数据 SQL。 |
| `customer` | 客户主数据域是多个交易、风险、报送、看板模块的上游事实源，保持独立，不并入 projectprocess/contract/riskcontrol/finance。POM 只依赖 `api`、`foundation` 和真实框架能力，源码无其他业务域 import；内部拥有法人/自然人、工商、股东、联系人、地址、银行账户、客户权限、客户移交、外部客户、沙盘记录、移动拜访、版本等完整生命周期。剩余问题是客户流程 BPMN、风控应用/风控策略 SQL、客户监控 DTO 和移动拜访/面签等子能力让 customer 语义较宽；短期可接受，后续应避免承载风险监控页面和跨域展示规则。 | `mvn -pl zswl-mithras-customer -am -DskipTests compile` 通过，reactor 仅 root、api、foundation、customer。静态搜索确认 customer 源码无业务域 import；resources 中未发现对外域业务表的 mapper join，但存在客户流程 BPMN 与风控应用/策略 SQL。POM 中直接 `org.mybatis:mybatis` 不是同类空依赖：源码直接使用 `org.apache.ibatis.type.*` TypeHandler 和 `MapperBuilderAssistant` 等 MyBatis 核心类型，应保留直接声明。反向依赖主要来自 application、web、riskcontrol、report、contract、rating、dashboard、finance 等事实消费方。 |
| `projectprocess` | 项目立项/评审/定价/生命周期域保持独立，不并入 contract/payment/riskcontrol/finance；它是核心交易主链路和多个下游域的事实源。本轮校准了依赖证据：`flow-core` 是 `ILifecycleProcessor` 的真实流程结束耦合点，不能当空依赖删除；同时移除无独立必要性的直接 `org.mybatis:mybatis` 依赖。application 侧已先做语义归组：把顶层 `facade/projestablish`、`facade/projpricing`、`facade/projreview` 收敛到 `facade/projectprocess/*`，把重复的 `facade/projectprocess/projectprocess/utils/CashFlowGenerationFacade` 收敛到 `facade/projectprocess/utils`，并把顶层 `adapter/projlifecycle` 收敛到 `adapter/projectprocess/projlifecycle`，与已有 `orchestration/projectprocess/*` 服务目录一致，避免立项/定价/评审/现金流工具/项目生命周期 adapter 被误读成 application 顶层业务域。 | `mvn -pl zswl-mithras-projectprocess -am -DskipTests compile` 通过，reactor 为 root、api、foundation、document、projectprocess。静态复核确认源码没有直接 import contract/payment/customer/riskcontrol 等业务域；document 用于材料/版本处理，flow-core 用于流程结束后写项目生命周期事件。本轮 application 归组后，`rg` 确认旧 `application.orchestration.facade.projestablish/projpricing/projreview`、旧 `facade.projectprocess.projectprocess` 和旧 `application.orchestration.adapter.projlifecycle` 包已无残留引用。 |
| `third` | 第三方集成防腐层保持独立，不并入 finance/capital/riskcontrol/customer。POM/源码 import 层只依赖 `api`、`foundation`，内部聚合财务共享、苍穹、宝融、天眼查、企查查、阿里 OCR、契约锁、Providence、XInsight、云湖、金控、数据共享、异常重试等外部系统能力。它体量很大，未来可以按外部系统拆分 third 子模块，但这是横向能力内部拆分，不是并入业务域。剩余风险是 `financialshare` 与 capital/application 资金流水语义贴近，`BRFlowRecordMapper.xml`、`xinsight` mapper 等资源层仍有少量跨表耦合；长期应通过业务域 port/application adapter 输入输出业务快照。本轮移除无独立必要性的直接 `org.mybatis:mybatis-spring` 与 `org.mybatis:mybatis` 依赖，MyBatis-Spring 注解和 MyBatis core 类型均由 MyBatis-Plus starter 依赖链覆盖。 | `mvn -pl zswl-mithras-third -am -DskipTests compile` 通过，reactor 仅 root、api、foundation、third；本轮追加 `mvn -pl zswl-mithras-creditreport,zswl-mithras-third,zswl-mithras-rating,zswl-mithras-application -am -DskipTests compile` 通过，38 个 reactor 模块 SUCCESS。构建中仅提示 Aspose 三个 jar 缺 POM 元数据。静态搜索确认 third 源码无业务域 import；资源层包含外部同步/查询适配表和少量业务表耦合。 |
| `afterlease` | 租后域保持独立，不并入 contract/collection/riskcontrol；前序已把 workflow、collection、customer、projectprocess、basedata 等直接依赖收敛到租后 port 和 application adapter，剩余重点是 document 材料/模板协议和 SQL 层跨域读模型。 | `mvn -pl zswl-mithras-afterlease,zswl-mithras-creditreport -am -DskipTests compile` 通过；本次 reactor 中 afterlease 随 root、api、foundation、third、document、creditreport 一起编译，说明 POM 已明显收敛。文档记录的剩余风险主要是 `NewAfterLeaseCheckExternalQueryMapper.xml`、`NewAfterLeaseCheckPlanBaseMapper.xml`、`RentCollectionIndexMapper.xml` 中的 SQL 跨域 join。 |
| `blackgray` | 名单/准入能力语义独立，Java/POM 低耦合且通过 `BlackGrayCustomerPort`、`BlackGrayApprovalProcessPort` 接入客户事实和流程启动。保持独立，不并入 riskcontrol/credit/assetclassify；它可以归入 risk-credit 大域治理，但物理模块上仍应作为名单事实和准入判断能力保留。本轮复核确认 POM 技术依赖均有源码使用证据，源码没有其他业务域 import；剩余耦合集中在 resources：审批列表 SQL 直接读 `audit_task`，名单行业统计读 `concentration_report`/`bifrost_org`，初始化 SQL 写 `bifrost_menu`、`bifrost_custom_tree` 等平台菜单表。 | `mvn -pl zswl-mithras-blackgray -am -DskipTests compile` 通过，reactor 仅 root、api、foundation、blackgray。静态复核显示 Java import 粗分约为 blackgray 336、foundation 47、api 36、dto 12；反向使用主要在 application 的黑灰名单 adapter、workflow end handler、客户统一视图和第三方票据入库场景，web 只做 mapper scan/入口装配。 |
| `archives` | 正式档案管理生命周期独立，不等同于 document 文件能力或 filingmaterials 归档准备流程。保持独立，不做 records 域内物理合并。本轮复核确认 Java/POM 层无其他业务域 import，POM 依赖均有源码证据：Gruul 用于登录用户，MyBatis-Plus 用于 mapper/model/service，Spring Web/validation 用于 controller，Spring TX 用于档案模板/档案管理事务，Hutool 用于集合、字符串、ID 等工具。资源层 `ArchiveTemplateMapper.xml` 仍直接读取 `materials_list where business_type = "ARCHIVES"`，这是正式档案读模型对文件清单的表级耦合；`ArchivesManagementMapper.xml` 仍直接 join `proj_establish_base_info`，用于列表展示项目名称、主办人、业务部门、客户和业务类型。材料文件读取、流程和通知已经通过 `ArchivesSupportPort`、`ArchivesWorkflowPort`、`ArchivesNotificationPort` 由 application 适配。 | `mvn -pl zswl-mithras-archives -am -DskipTests compile` 通过，reactor 仅 root、api、foundation、archives。静态搜索确认 archives 源码无其他业务域 Java import，Java import 粗分约为 dto 46、archives 43、foundation 19、api 8；反向引用主要来自 application 的 archives adapter、workflow end handler 和材料校验 handler，web 只负责 mapper scan。 |
| `filingmaterials` | 归档准备/归档资料流程域保持独立，不并入 archives/document；它负责正式归档前的资料目录、归档配置、项目/资金/租后/其他资料归档、归档台账、批量下载、归档材料生成和归档流程接入。复杂度主要在 application 侧跨域编排，物理合并不能消除这些项目/合同/付款/授信/租后/流程/消息依赖。本轮继续复核 application 的资料台账切片，确认原 `application/orchestration/document/materialsledger` 同时使用 filingmaterials、fund、document/materials 和 system 名称服务，不适合迁回 document 或 filingmaterials 模块；已将包名调整为 `application/orchestration/filingmaterials/materialsledger`，避免把归档资料台账误标为 document 横向能力。随后把错拼的 `facade/materialsdger` 归到 `facade/filingmaterials/materialsledger`，与归档资料台账语义保持一致。 | `mvn -pl zswl-mithras-filingmaterials -am -DskipTests compile` 通过，reactor 为 root、api、foundation、document、filingmaterials；本轮移除无独立必要性的直接 `org.mybatis:mybatis` 依赖。静态复核确认源码外部业务模块 import 只有 document 模板服务，Mapper XML 仍直接读取 `proj_review_base_info`、fund 相关表、`contract_base_info` 和 Flowable 历史表，属于后续需收敛的跨域读模型。本轮包名整理后，`rg` 确认 application 中旧 `document.materialsledger` 与旧 `facade.materialsdger` 包已无残留引用。 |
| `capital` | 本体边界干净但偏薄，核心复杂度在 application 的跨域核销编排。短期目标是补实 capital 自身流水/核销事实，不是并入 finance，也不是直接搬回 application 代码。本轮复核确认 capital Java/POM/resources 层无其他业务域 import 或跨域 mapper SQL；POM 只依赖 `api`、`foundation` 和真实框架能力，`xxl-job-core` 用于资金流水/核销任务入口，`poi` 用于资金端 Excel exporter，`spring-web`/validation 用于 controller/API 实现，MyBatis-Plus 用于 `FinanceFlowWriteOffDetail` mapper/model/service。本轮已把 `FinanceFlowRecordService.saveFlowRecord` 中“按已存在流水 id 判断新增、以及新增流水默认展示状态”的纯保存规则下沉为 `CapitalBankFlowSaveRuleService`；把 `move2NoHandle` 中“自动转为无需处理”的纯判定规则下沉为 `CapitalBankFlowNoHandleRuleService`，application 只传入流水快照、员工姓名和我方账号等外部事实；把 `withdrawBankFlow` 中“反核销后核销状态/处理中心状态计算”下沉为 `CapitalBankFlowWriteOffRuleService`；继续把 `fullSync` 中“已保存流水与远端临时流水按 `billno` 判断新增/删除”的同步差异规则下沉为 `CapitalBankFlowSyncRuleService`。这些服务通过 capital 自有 decision/snapshot/diff 模型避免把 third 持久化模型带入 capital。application 仍负责平台 API、临时表、third 流水持久化、删除核销明细和宝融同步。 | `mvn -pl zswl-mithras-capital -am -DskipTests compile` 通过，reactor 仅 root、api、foundation、capital，编译 50 个 capital 源文件；`mvn -pl zswl-mithras-application -am -DskipTests compile` 通过，38 个 reactor 模块全成功，耗时约 1 分 35 秒。反向搜索显示 application 是主要消费方，另外 web 启动装配、fund/payment/collection/third/margin 等主要消费资金流水枚举、job 接口和核销明细服务。`FinanceFlowRecordService` 仍直接使用 third 流水持久化模型，完整回迁前还需要 capital 自有流水模型/port。 |
| `ftp` | 内部资金转移定价/收益分解域保持独立，不并入 finance/budget/fund/capital；oldftp 与 newftp 当前是历史版本和新版定价体系并存，不能为了模块名干净直接删除 oldftp。FTP 拥有定价规则、指导价审批、版本库、取价服务、计息/收益结果和模板资源，其他模块应消费 FTP 价格/收益结果，而不是拥有 FTP 定价生命周期。 | `mvn -pl zswl-mithras-ftp -am -DskipTests compile` 通过，reactor 为 root、api、foundation、basedata、ftp；本轮移除无独立必要性的直接 `org.mybatis:mybatis` 依赖。静态复核确认 Java 层内部 import 主要是 ftp/foundation/dto/api/basedata，`basedata` 依赖真实用于 LPR 和日期工具；资源层 `FTP计息.sql` 仍修改 payment/client/fund 表字段，属于历史脚本层耦合，后续应避免继续扩大。 |
| `fund` | 融资事实源语义明确，拥有资金方、授信、融资、直融、还本付息、计划、质押、费用、财务系统提交、BPMN 流程和版本归档等独立生命周期。保持独立，不并入 finance/capital/liquidity；本轮复核确认 Java/POM 层只依赖 `api`、`foundation` 和真实框架能力，POM 中 MyBatis-Plus、Hutool、commons-lang3/collections4、POI、Spring、Servlet、Swagger、validation、Fastjson、MapStruct、XXL Job 均有源码使用证据，未发现可安全删除依赖。直接 `org.mybatis:mybatis` 已移除，MyBatis 注解由 MyBatis-Plus starter 传递满足。 | `mvn -pl zswl-mithras-fund -am -DskipTests compile` 通过，reactor 仅 root、api、foundation、fund，编译 402 个 fund 源文件并复制 24 个资源；Java/POM 无外域直接依赖。剩余重点是 `PropertyMapper.xml` 对 payment/contract/customer 表的读模型耦合，`FundFinancingBaseInfoMapper` 返回 `dto.capital` 查询 DTO 的跨域契约痕迹，以及 metric/dashboard/liquidity 等读侧消费 fund 持久化模型/表。 |
| `finance` | 财务经营/核算聚合域保持独立，不作为 payment/collection/fund/ftp/budget/capital/margin/liquidity 的物理合并目标。本轮复核确认 finance 直接依赖基本都有源码证据；源码根包已统一到 `cn.zswltech.mithras.finance`，但 `finance.view`/`DashboardFv*` 仍承载资金财务看板快照语义。原 `finance/adapter/metric` 两个 metric port adapter 已迁到 application，finance 不再直接承载 metric 接线类；`finance -> metric` 暂时仍保留，因为 `JinKongMonthlyReportService` 直接写 metric 因子服务和模型。metric 中旧 `YunHuMonthlyReportService` 全仓无外部引用，且行为比 finance 现有金控同步更旧，已删除；后续不应拿它替换 finance 实现，而应抽 metric 因子写入快照/port。`finance -> dashboard` 已切除：finance 源码无 dashboard 模块 import，资金看板 DTO 只来自 api；`finance -> assetclassify` 是真实依赖，利润测算通过 `AssetClassifyClientAuxiliaryLibService` 读取资产分类辅助库。本轮顺手修正 `ProfitCalculateResultMapper.xml` 利润测算列表客户关联，由合同 id 错连客户 id 改为通过 `contract_base_info.client_id` 关联客户。 | `mvn -pl zswl-mithras-finance,zswl-mithras-application -am -DskipTests compile` 通过，38 个 reactor 模块 SUCCESS。静态搜索确认 `zswl-mithras-finance` 无 `cn.zswltech.mithras.dashboard` 引用，`finance/adapter/metric` 已无 Java 文件；finance 剩余 metric 直接引用集中在 `JinKongMonthlyReportService`。本轮静态搜索确认旧 `YunHuMonthlyReportService` 无引用并已删除。 |
| `payment` | 付款域保持独立，不并入 finance/collection/contract/policy。本轮确认 payment Java/POM 层已收敛到 api、foundation、document 和技术依赖，当前约 135 个 Java 文件，源码外部业务 import 只剩 document 的材料版本处理；Java import 粗分约为 api 164、payment 154、foundation 136、dto 31、document 4。合同、客户、流程、third、projectprocess 等输入已通过 payment 自有 port/snapshot 和 application adapter 收口。POM 中 Gruul、OSS、EasyExcel、Spring、Servlet、validation、Swagger、MyBatis-Plus、Hutool、MapStruct、Fastjson、XXL Job、SLF4J、commons-collections4、Guava、poi-tl 均有源码使用证据，暂未发现可安全删除项。原 `PaymentBaseInfoMapper.paymentFlowList` 已迁到 `application.orchestration.collection.mapper.CollectionPaymentFlowCenterMapper`，付款域不再直接承载收付中心/现金流聚合 mapper；原 `PaymentActualDetailMapper.listContractPayInfo*` 已迁为 budget 自有 `BudgetPaymentFactPort`，application 负责跨域 SQL；原 `PaymentBaseInfoMapper.queryListWithContractId` 和 `myList` 均已迁到 application 的 `PaymentProcessQueryMapper`，payment XML 不再读取 Flowable 历史表或 `contract_base_info`；`PaymentBaseInfoMapper.xml` 已删除，剩余自有表更新改为注解 SQL。剩余主要问题转为 application 读侧 SQL 是否继续拆成 payment/contract/flow 快照输入。 | 本轮 `mvn -pl zswl-mithras-payment,zswl-mithras-application -am -DskipTests compile` 通过，38 个 reactor 模块 SUCCESS；此前 `mvn -pl zswl-mithras-payment,zswl-mithras-budget,zswl-mithras-application -am -DskipTests compile`、`mvn -pl zswl-mithras-payment,zswl-mithras-application -am -DskipTests compile` 和 `mvn -pl zswl-mithras-payment -am -DskipTests compile` 也通过。静态搜索确认旧 `ContractPayInfoDTO` 已无引用，`listContractPayInfo*` 只存在于 budget port、application adapter/mapper 和预算收益测算调用点；静态搜索也确认 `queryListWithContractId`、`myList`、`ACT_HI_PROCINST`、`ACT_RE_PROCDEF` 与 `contract_base_info` 已无 payment 源码/resources 残留。 |
| `kpi` | KPI/绩效/ECL/项目分配与测算语义独立，有自己的写生命周期。保持独立，不并入 dashboard/metric/budget/finance；Java/POM 层低耦合不是假象，源码无其他业务域 import。本轮复核到 206 个 Java 文件，一级包约为 application 38、mapper 27、model 27、excel 26、enums 23、controller 17、dto 17、bo 13、distribution 10、job 4、convert 2、datacompare 1、constant 1。删除历史流程迁移后留下的空目录 `application/process/prepare/handle`、`application/process/prepare`、`application/process`。POM 技术依赖均有源码证据：Gruul 用于用户/组织/当前账号，POI/EasyExcel 用于导入导出，MyBatis-Plus 用于持久化，Spring/Servlet/validation/swagger 用于 Web/API/事务，Hutool/Fastjson 用于工具和 JSON，QLExpress 用于 KPI 参数公式，XXL Job 用于绩效任务。 | `mvn -pl zswl-mithras-kpi -am -DskipTests compile` 通过，reactor 仅 root、api、foundation、kpi，编译 206 个 kpi 源文件；静态搜索确认源码/POM 无 `cn.zswltech.flow` 直接引用，也无其他业务域 Java import。Java import 粗分约为 kpi 263、foundation 193、dto 125、api 71。剩余风险集中在资源层：`KpiProjectDistributionMapper.xml`/`KpiProjGuessBaseInfoMapper.xml` 读取 contract/project 表，`PerformanceMainInfoMapper.xml` 读取 `gruul_user_org_job`，`sql/绩效管理.sql` 通过 `contract_base_info` 回填部门字段，KPI/ECL 初始化 SQL 写平台菜单表，`sql/kpi/v1_ddl.sql` 仍修改 `materials_list`/`materials_list_lib`。下一步不应通过删 POM 做假瘦身，而应把这些资源层读模型和外部模块对 KPI mapper/model 的直接消费收敛为查询服务、快照 DTO 或明确 port。 |
| `margin` | 保证金/质保金域语义清楚，拥有保证金、质保金、收退记录和核销记录等独立写模型；Java/POM 只依赖 `api`、`foundation` 和框架能力，合同/收款/付款/权限输入已通过 margin 自有 port 表达。保持独立，不并入 finance/payment/collection；本轮移除无独立必要性的直接 `org.mybatis:mybatis` 依赖，并把保证金列表跨域 SQL、收款事件计划应收写模型、collection 侧保证金查询模型分别收敛到 application 读侧或 margin 自有接口/snapshot。 | `mvn -pl zswl-mithras-margin -am -DskipTests compile` 通过，reactor 仅 root、api、foundation、margin；`mvn -pl zswl-mithras-margin,zswl-mithras-application -am -DskipTests compile` 通过，38 个 reactor 模块全成功。静态搜索确认 margin 源码无 contract/collection/payment/finance/workflow/customer/projectprocess/document 直接 import，`ContractCollectionMarginPortAdapter` 不再直接引用 `MarginBaseInfo` 或 MyBatis wrapper。剩余风险是外部读侧对 margin 表/模型的直接消费。 |
| `workbench` | 工作台是 reporting/入口体验域中的轻量聚合模块，有公告、快捷入口、用户卡片关系、指标缓存、初始化任务和图表指标计算等独立生命周期。保持独立，不并入 dashboard/application，也不被核心业务域反向依赖。本轮复核确认模块没有 `src/main/resources` mapper/XML/SQL，资源层无跨域表 join；POM 中 Gruul starter 用于 `AccountUtil`、`UserService`、菜单树和 Response，MyBatis-Plus 用于 mapper/model/service，Hutool 用于判空/日期/Excel 导出，MapStruct 用于 converter，Fastjson 用于快捷入口和指标 JSON，XXL Job 用于工作台初始化和指标刷新任务。 | `mvn -pl zswl-mithras-workbench -am -DskipTests compile` 通过，reactor 仅 root、api、foundation、workbench，编译 105 个 workbench 源文件；静态搜索确认 workbench 源码无其他业务域 Java import。反向引用主要来自 application 的 workbench adapter/facade，web 仅有测试/入口引用。跨域事实读取通过 workbench port，由 application adapter 装配。 |
| `assetclassify` | 资产五级分类域保持独立，不并入 riskcontrol/credit/finance；它拥有分类发起、客户分类、检查内容、复核/会议节点、分类结果、版本归档和 BPMN 流程资源。客户/合同/收款/付款/保证金/流程自动处理等外部事实已通过 assetclassify 自有 port 或 application adapter 输入。本轮复核确认源码只使用 MyBatis 注解，直接 `org.mybatis:mybatis` 无独立必要性并已移除。 | `mvn -pl zswl-mithras-assetclassify -am -DskipTests compile` 通过，reactor 为 root、api、foundation、document、assetclassify；静态搜索确认 assetclassify 源码无 customer/contract/collection/payment/workflow/riskcontrol 等业务域直接 import。剩余 direct dependency 是 document，集中在两个材料版本 handler；resources 主要围绕 asset_classify 自有表和 BPMN，未发现外域表 join。 |
| `leaseholdproperty` | 租赁物/评估/车辆登记/VAT 发票/评估公司白名单域保持独立，语义上归 contract 大域治理，但当前不物理并入 contract。源码已无 contract/workflow/projectprocess 等业务域 Java import；合同上下文和流程能力通过租赁物自有 port 由 application adapter 装配。本轮移除无独立必要性的直接 `org.mybatis:mybatis` 依赖。 | `mvn -pl zswl-mithras-leaseholdproperty -am -DskipTests compile` 通过，reactor 为 root、api、foundation、third、document、basedata、leaseholdproperty。静态复核确认 `document` 用于白名单材料版本，`basedata` 用于字典/日期，`third` 用于天眼查评估公司信息；资源层 `LeaseItemListRowDataMapper.xml` 仍读取 `contract_lease_item`，属于合同/租赁物读模型耦合。 |
| `policy` | 保单/保险管理能力保持独立，不并入 payment/contract；它拥有正式保单、暂存保单、版本、台账、导入导出、续保提醒、到期提醒和保单审批流程。POM/Java import 层低耦合基本真实，但 `PolicyInfoMapper.xml` 通过 SQL 直接读取项目、付款和合同表，说明当前干净程度不能只按 POM 判断。本轮已把保单台账列表 `ledgerList` 迁到 application 读侧 `PolicyLedgerQueryMapper`，policy mapper 不再承载这条正式保单 + 付款保单 + 合同 + 项目的聚合 SQL；批量保单号统计 `countPolicyCodes`、`countPolicyCodeNum` 也已迁到 application 读侧 mapper，同时删除无活代码调用的 `countPolicyCode` 单值统计 SQL。 | `mvn -pl zswl-mithras-policy -am -DskipTests compile` 通过，reactor 为 root、api、foundation、document、policy；`mvn -pl zswl-mithras-policy,zswl-mithras-application -am -DskipTests compile` 通过，38 个 reactor 模块全成功。POM 中未发现除 document 外的业务模块依赖，Java import 外部业务模块仅 document 材料版本处理。资源层剩余风险是 `PolicyInfoMapper.xml` 的 `myList`、到期提醒、付款最大保险到期日、付款待续保等查询仍读 `proj_review_base_info`、`payment_policy_info`、`payment_base_info`、`contract_base_info`。 |
| `associationreport` | 金融局/协会报送域保持独立，不并入 report/dashboard/metric；它拥有报送模板、报送申请、报送任务、分项报表、报送版本和 SFTP 文件报送生命周期。外域事实通过 associationreport 自有 port 由 application adapter 装配。 | 已删除无源码依据的 workflow/rating/customer/contract/payment/collection/assetclassify/fund/dashboard/metric/flow-core 直接依赖；`mvn -pl zswl-mithras-associationreport -am -DskipTests compile` 通过，reactor 为 root、api、foundation、basedata、associationreport。静态搜索确认源码/resources 无 dashboard/customer/workflow/flow 等错误方向引用，也未发现外域表 join；`basedata` 依赖来自 `DateUtil` 的季度/月末日期工具，`jsch` 来自 SFTP 文件报送，`xxl-job-core` 来自 `AssociationReportJob`。 |
| `report` | 征信报送处理域保持独立，不并入 creditreport/dashboard/交易域；它是监管口径下的重读侧快照生成模块，拥有 draft/formal/fullsnap/procsnap/base 快照、批次、修改快照、审批处理和流程资源。本轮确认 POM 中 contract/payment/customer/collection/projectprocess/workflow/system/basedata 等依赖均有源码或资源证据，暂不硬删；Druid、Flow core、Flowable、XXL Job、AspectJ、Guava、JetBrains annotations、Servlet、Spring JDBC 也都有源码使用证据。已移除无独立必要性的直接 `org.mybatis:mybatis`、`org.mybatis:mybatis-spring` 技术依赖；本轮继续移除无源码引用的 `crypto-open-starter`。 | `mvn -pl zswl-mithras-report -am -DskipTests compile` 通过，reactor 为 root、api、foundation、third、document、basedata、customer、workflow、payment、projectprocess、contract、collection、system、report 共 14 个模块。静态复核显示 report 有 240 个 Java 文件，自身约 806 个 import，外域事实输入集中在 contract/payment/customer/collection/projectprocess；资源层 `cr_*` 表和快照 SQL 保留大量合同、付款、客户、收款字段，属于报送快照字段。 |
| `dashboard` | 看板/管报是 reporting 大域下的跨域读侧聚合模块，保持独立，不并入 finance/metric/report/workbench；它拥有看板配置、管苑/管理报表、运营/项目/付款/资金/租后看板、导入导出和大量展示查询模型。本轮复核确认 POM 中业务依赖都有源码或资源层使用证据，另补充显式 `commons-codec`，因为 `GuanYuanSsoUtil` 直接使用 `org.apache.commons.codec.binary.Base64`，不应继续依赖传递引入。 | `mvn -pl zswl-mithras-dashboard -am -DskipTests compile` 通过，reactor 15 个模块；当前 POM 已去掉 riskcontrol、assetclassify、workflow 直接依赖，但 workflow 仍会通过 afterlease 等依赖链进入 reactor。静态搜索确认 dashboard 源码无 riskcontrol/assetclassify/workflow 模块包 import，搜到的 `risk_control_*` 与 `AssetClassify*` 是字段名、本地枚举/DTO 或 SQL 展示口径。flow-core/flowable 仍被 `DashboardOperateTodoService`、`DashboardOperationConversionService`、`GuanYuanOperationService` 和转换器直接使用，暂不能删除；后续治理重点是把流程查询结果和外域模型继续转成 dashboard 本地快照/port。 |
| `collection` | 收款域是应收、实收、核销、逾期和催收事实源，应保持独立；本轮确认源码/resources 已无 `customer` 模块类型引用，删除 `collection -> customer` POM 历史依赖；继续复核 `collection -> contract/payment`，确认 `CollectionBaseInfoService`、`CollectionOverdueRecordInfoService`、`ContractCollectionPaymentService` 仍有真实合同/付款事实读取，暂不删 POM、不做物理合并；同时清理了 `CollectionBaseInfoService` 和 `ContractCollectionPaymentService` 中无真实使用的注入/import 和过时注释，减少假依赖信号。本轮复核确认源码只使用 MyBatis 注解，直接 `org.mybatis:mybatis` 无独立必要性并已移除。 | `mvn -pl zswl-mithras-collection -am -DskipTests compile` 通过，reactor 12 个模块，说明 `payment/contract` 仍是当前构建路径；此前 `mvn -pl zswl-mithras-application -am -DskipTests compile` 也通过。静态搜索确认 `zswl-mithras-collection` 源码/resources/POM 无 `cn.zswltech.mithras.customer` 或 `zswl-mithras-customer` 引用。 |
| `budget` | 预算/ECL 是财务大域下的经营计划、预算考核和风险准备测算域，语义独立但实际输入依赖较重，当前不适合并入 finance/kpi。POM 直接业务依赖已收敛为 0；`finance`、`contract`、`payment`、workflow/flow-core、collection、projectprocess、KPI 年度目标和 ECL 配置结构已分别通过 `BudgetFinanceFactPort`、`BudgetContractFactPort`、`BudgetPaymentFactPort`、`BudgetExamineWorkflowPort`、`BudgetCollectionFactPort`、`BudgetProjectFactPort`、`BudgetKpiFactPort`、预算自有导入模型或 `BudgetEclConfigEnum`/`BudgetEcl*BO` 隔离。本轮复核确认 mapper XML 基本只读预算自有表，但历史 SQL 夹带 `finance_bcm_balance_mf` 建表、`performance_base_info` 字段变更和平台菜单初始化，属于资源脚本归属不纯。 | `mvn -pl zswl-mithras-budget -am -DskipTests compile` 通过，reactor 收敛为 root、api、foundation、budget 4 个模块，编译 155 个 budget 源文件；`mvn -pl zswl-mithras-application -am -DskipTests compile` 通过，38 个 reactor 模块 SUCCESS。静态搜索确认 budget 源码/resources/POM 无 `cn.zswltech.mithras.projectprocess`、`zswl-mithras-projectprocess`、`cn.zswltech.mithras.finance`、`zswl-mithras-finance`、`FinanceProjectProfitDetail`、`FinanceSubjectBalanceAssist`、`FinanceBcmBalanceMf`、`cn.zswltech.mithras.contract`、`zswl-mithras-contract`、`ContractBaseInfo`、`ContractBaseInfoService`、`cn.zswltech.mithras.payment`、`zswl-mithras-payment`、`PaymentActualDetail`、`PaymentActualDetailMapper`、旧 `ContractPayInfoDTO`、`ProjEstablishBaseInfoMapper`、`ProjPricingBaseInfoMapper`、`ProjPricingBaseInfo`、`cn.zswltech.mithras.kpi` 或 `zswl-mithras-kpi` 残留。预算收益测算和预算考核执行需要的合同/付款/财务/项目/KPI 年度目标事实现在由 `BudgetContractFactSnapshot`、`BudgetContractPaymentFactSnapshot`、`BudgetPaymentActualSnapshot`、`BudgetContractFactPort`、`BudgetPaymentFactPort`、`BudgetFinanceProjectProfitSnapshot`、`BudgetFinanceFactPort`、`BudgetProjectFactPort`、`BudgetPerformanceTargetSnapshot` 和 `BudgetKpiFactPort` 表达，application adapter 承接跨域查询和转换。 |
| `credit` | 授信域是 risk-credit 大域里的核心事实源，拥有集团授信立项/评审、授信额度创建/占用/释放/失效和授信流程资源；当前 Java/POM 低耦合，不并入 creditreport/rating/riskcontrol/assetclassify。本轮复核确认源码只使用 MyBatis 注解，直接 `org.mybatis:mybatis` 无独立必要性并已移除；继续移除无源码引用的 `commons-collections4` 直接依赖。本轮还清理了历史迁移后遗留的空 `adapter`、`flow` 目录，避免形成假边界信号。 | `mvn -pl zswl-mithras-credit -am -DskipTests compile` 通过，reactor 为 root、api、foundation、document、credit；静态搜索确认源码只直接引用 document 的材料版本模型/handler，没有 customer/projectprocess/fund/rating/riskcontrol/creditreport 等业务域 Java import。Mapper XML 主要围绕 `group_credit_*` 自有表，未发现明显外域表 join；BPMN 中保留授信审批自身的风险敞口和风控行业分类条件。 |
| `creditreport` | 征信查询/征信报送域保持独立，不并入 credit；客户、项目、授信、合同、付款、workflow 等事实读取已经通过征信自有 port 和 application adapter 收窄，剩余真实依赖主要是 third 与 document。本轮移除无独立必要性的直接 `org.mybatis:mybatis-spring` 与 `org.mybatis:mybatis` 依赖，MyBatis-Spring 注解和 MyBatis core 类型均由 MyBatis-Plus starter 依赖链覆盖。 | `mvn -pl zswl-mithras-afterlease,zswl-mithras-creditreport -am -DskipTests compile` 通过，reactor 为 root、api、foundation、third、document、creditreport、afterlease 共 7 个模块；本轮追加 `mvn -pl zswl-mithras-creditreport,zswl-mithras-third,zswl-mithras-rating,zswl-mithras-application -am -DskipTests compile` 通过，38 个 reactor 模块 SUCCESS。后续重点是 `CreditReportBaseInfoService`、`CreditReportApiXJServiceImpl`、`CreditReportMaterialsListLibHandler` 中剩余 document 材料/版本读取，以及 Mapper XML 对 client/contract/payment 表的读模型耦合。 |
| `riskcontrol` | 风控模块是风险监控/策略/预警/报告/评分卡集合域，当前像风险运营平台，保持独立但不再吸收 credit/rating/assetclassify/blackgray。当前 202 个 Java 文件，主体包括 `metric`、`scorecard`、`report`、`concentration`、`strategy`、`warning/opinion` 等内部子域。POM 中 customer、contract、projectprocess、payment、collection、jsch、guava 等依赖都有源码或资源使用证据，暂不硬删。本轮复核确认源码只使用 MyBatis 注解，直接 `org.mybatis:mybatis` 无独立必要性并已移除；`metric -> riskcontrol.strategy` 也已通过 metric 自有 port/application adapter 完成第一轮隔离。本轮继续把 4 个风险指标计算器中仅用于 `endOfDay` 的 `basedata.util.DateUtil` 替换为 JDK `LocalDate.atTime(23, 59, 59)`；评分卡客户注册地址解析迁到 application 的 `ScoreCardClientAddressResolverAdapter`，riskcontrol 只保留 `ScoreCardClientAddressResolver` port，因此 `riskcontrol` Java/POM 层已无 `basedata` 直接依赖。资产分类当前批次、后三类客户集合和客户最新分类结果已改为 `RiskControlAssetClassifyPort` 输入，由 application adapter 适配 `assetclassify`，因此 `riskcontrol` Java/POM 层已无 `assetclassify` 依赖。本轮还把区域风险限额指标从 projectprocess 的 `ProjRegionalClassify` 切到 riskcontrol 自有 `RegionalProjectClassify`，把预警统计中两个流程模型 key 从 workflow 枚举改为风险域稳定字符串常量，并将 `RiskOpinionFileHandler` 改为使用 riskcontrol 本地舆情/预警流程 key 集合判断读表。风控动态表单处理类已迁到 application；预警流程查询和处置变量写入已改为 `RiskControlWarnWorkflowPort`，由 application 的 `RiskControlWarnWorkflowPortAdapter` 调用 flow-core/Flowable。金控关联交易关联方名单同步中的客户 ID 解析已扩展到 `RiskControlGljyReportExternalPort`，由 application adapter 查询 customer；`JC049/JC050`、`ZL002/ZL003/ZL004/ZL005`、`MD002/MD003`、`JC47548/JC47558`、`JC030/JC031`、风控行业分类系列指标、`FJC47608`、区域风险限额指标、策略拦截、关联方名录导入和风险集中度所需的客户集合、客户到集团/行业映射、客户行业分类或生效客户 ID 已收敛到 `RiskControlClientFactPort`；关联方交易列表所需已核销付款/收款分页事实已收敛到 `RiskControlRelatedTransactionPort`；`A10000396_ZL044` 所需合同版本授信金额事实已收敛到 `RiskControlContractFactPort`；区域风险限额指标所需项目评审版本与合同版本查询已收敛到 `RiskControlProjectReviewFactPort`。至此 `riskcontrol` Java/resources/POM 层已无 workflow、flow-core、flowable、basedata、assetclassify 直接依赖。 | `mvn -pl zswl-mithras-riskcontrol -am -DskipTests compile` 通过，reactor 为 root、api、foundation、third、document、basedata、customer、workflow、payment、projectprocess、contract、collection、riskcontrol 共 13 个模块；`mvn -pl zswl-mithras-application -am -DskipTests compile` 通过，38 个 reactor 模块 SUCCESS。静态搜索确认 riskcontrol 源码/resources/POM 无 `cn.zswltech.flow`、`org.flowable`、`cn.zswltech.mithras.workflow`、`cn.zswltech.mithras.basedata`、`cn.zswltech.mithras.assetclassify` 或对应 Maven artifact 直接引用；Java import 分布约为 `customer` 11、`projectprocess` 2、`contract` 6、`collection` 7、`payment` 6。resources 层 `RiskControlWarnMonitorMapper.xml`、`RiskControlOpinionMonitorMapper.xml` 直接 join `client`/`bifrost_org`，属于风险运营读模型耦合。剩余源码耦合主要集中在 `RiskControlJzdReportService`、`RiskControlClientListFileService` 和 `RemainingPrincipalServiceImpl`，都属于大报表、名单同步或核心敞口计算，不宜硬迁。 |
| `metric` | 指标域保持独立，不并入 riskcontrol/dashboard/finance；当前 409 个 Java 文件，`financialcloudmetric` 和 `aggregator` 是主体。已清理 `metric -> riskcontrol` 直接依赖，风险策略当前值改为 `RiskStrategyCurrentValuePort`/`RiskStrategyCurrentValueSnapshot` 输入；也已清理 `metric -> customer` 隐藏源码依赖，客户/工商/行业分类输入改为 `MetricCustomerInfoPort`/metric 自有 snapshot，由 application adapter 查询 customer。当前主要问题是大量金融云 calculator 仍直接读取 fund/contract/payment/collection/projectprocess/assetclassify/kpi 的 mapper/model，属于指标计算输入耦合，不适合通过合并解决。本轮复核未发现可安全删除的技术依赖：crypto、XXL Job、POI、JUEL、MapStruct、Gruul、MyBatis-Plus、Hutool 均有源码使用证据。 | 本轮静态复核确认 metric 源码/POM 无 riskcontrol 直接引用，源码/resources 无 `cn.zswltech.mithras.customer`、`cn.zswltech.mithras.liquidity`、`cn.zswltech.mithras.capital` 引用；Java import 分布约为 `metric` 717、`fund` 64、`contract` 45、`payment` 34、`collection` 14、`assetclassify` 11、`kpi` 10、`third` 3、`basedata` 2。`mvn -pl zswl-mithras-metric -am -DskipTests compile` 通过，reactor 为 root、api、foundation、third、document、basedata、customer、workflow、payment、projectprocess、contract、collection、fund、kpi、assetclassify、metric 共 16 个模块。 |
| `rating` | 评级域保持独立，不并入 credit/riskcontrol/creditreport；已清理 `rating -> projectprocess`，本轮将 `RatingAmountService` 对客户基础、营业收入、集团成员、未结清客户筛选的直接读取改为 `RatingAmountClientFactPort` 输入，由 application adapter 适配 customer。`RatingClientService` 仍保留客户评级申请页的深耦合，后续单独治理。本轮移除无独立必要性的直接 `org.mybatis:mybatis-spring` 与 `org.mybatis:mybatis` 依赖，MyBatis-Spring 注解和 MyBatis core 类型由 MyBatis-Plus/tk-mybatis 依赖链覆盖；`tk.mybatis:mapper` 仍需保留，因为 `RatingClientService` 使用 `tk.mybatis.mapper.entity.Example`。 | `mvn -pl zswl-mithras-rating -am -DskipTests compile` 通过，reactor 为 root、api、foundation、basedata、customer、workflow、rating，验证去掉直接 MyBatis core 依赖后 `FactoryMysqlConfig` 和 mapper 注解仍可解析。此前 `mvn -pl zswl-mithras-application -am -DskipTests compile` 与 38 模块组合编译也通过。静态统计 `rating -> customer` 降到约 11 个 import、1 个文件。 |
| `contract` | 合同域保持独立，不并入 finance/risk/reporting；本轮先切掉一个低风险错误职责：`ContractBaseInfoConverter` 不再声明 `ProjPricingBaseInfo -> ContractBaseInfo`，项目定价生成合同初始信息的映射留在 application 合同创建编排中；合同持久化模型里的项目过程枚举 Javadoc 链接也已改为普通编码说明，避免 model 仅因注释 import `projectprocess.enums`。`BusinessDataRepository` 的通配符 import 已展开，确认它是真实的合同文档渲染数据聚合点，后续应评估迁到 application 或改为合同渲染输入 port。`ContractBaseInfoMapper.myList` 的分页参数泛型已从项目评审模型改为合同实体，删除一个错误的 `ProjReviewBaseInfo` import。`ContractConstitutionFileService` 已改用 contract 自有 `ContractConstitutionFileCommand`，application 合同编排调用方同步替换，projectprocess 中旧 `ContractConstitutionFileBO` 已删除。本轮复核确认源码只使用 MyBatis 注解，直接 `org.mybatis:mybatis` 无独立必要性并已移除。`contract -> projectprocess` 仍有现金流 Excel、还款/利率枚举、项目评审信息等真实依赖，暂不能删 POM。 | `mvn -pl zswl-mithras-contract -am -DskipTests compile` 通过；此前同轮 `mvn -pl zswl-mithras-application -am -DskipTests compile` 也通过；静态搜索确认 `ContractBaseInfoConverter` 已无 `ProjPricingBaseInfo` 和 `reviewToContract`，`contract/model` 已无 `projectprocess.enums` import，`ContractConstitutionFileBO` 已无残留引用。 |

本轮进一步确认：目标中包含合并可合并模块，但当前这些模块都不满足低风险物理合并条件。真正有收益的下一步是继续瘦身错误方向依赖和 application 外溢，而不是为了减少 Maven 数量先合并。

下一步不要先合并模块，优先做三条依赖治理线：

1. 治理明确错误方向：`report -> application`、`report -> assetclassify`、`finance -> dashboard`、`associationreport -> dashboard/customer`、`rating -> riskcontrol/projectprocess`、`afterlease -> riskcontrol/filingmaterials`、`dashboard -> riskcontrol/workflow`、`associationreport -> workflow`、`assetclassify -> customer/contract/collection/payment`、`collection -> customer`、`metric -> riskcontrol/customer` 已完成第一轮治理；`rating -> customer` 已先收窄债项评级切片。下一步继续查找剩余业务域之间的反向依赖。
2. 治理聚合模块对事实源的表级直读：`paymentFlowList`、`PaymentActualDetailMapper.listContractPayInfo*`、`PaymentActualDetailMapper` 付款实缴读取、`PaymentBaseInfoMapper.queryListWithContractId`、`PaymentBaseInfoMapper.myList`、`liquidity` 融资账户设置列表都已从 owning domain mapper 迁到 application 读侧 mapper 或消费方 port。下一步继续拆这些 application SQL 为 payment/margin/warranty/contract/collection/flow/fund/system 快照输入；同时继续治理 `metric` 对 fund/contract/payment/collection/assetclassify 外域 model/mapper 的直接绑定。
3. 收敛 `application`：把只操作单一业务域模型和 mapper 的实现逐步回迁，保留真正跨域编排和 adapter；`capital` 优先从 third 流水模型隔离和自有流水/核销事实模型开始。

这些完成后，再重新评估是否存在真正适合物理合并的小模块。
