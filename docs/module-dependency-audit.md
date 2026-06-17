# 模块依赖审计

本文记录当前模块依赖的证据表，用来决定下一轮优先整理哪个模块。它和 [当前模块地图](module-map.md) 的关系是：模块地图描述全局语义，本文更偏向“依赖证据”和“下一步队列”。

审计口径：

- POM 直接依赖：来自各模块 `pom.xml` 中的内部模块依赖。
- 主源码实际引用：来自 `src/main/java` 和 `src/main/resources` 中的 `cn.zswltech.mithras.*` 引用。
- 暂不把 `zswl-mithras-web/src/test/java/cn/zswltech/mithras/others/**` 计入依赖判断；这批历史手工脚本已隔离，详见 `zswl-mithras-web/src/test/README.md`。
- `api`、`foundation` 是底座依赖，默认不作为业务域耦合问题。

## 一句话结论

当前可以继续按“低耦合模块先闭环、高耦合模块先拆错误方向依赖”的策略推进。下一轮不建议先碰 `contract`、`finance`、`metric`、`report` 这类大块，而应优先从 finance 大域中较小、较干净的 `margin`、`capital`、`liquidity` 继续做闭环。

## POM 直接依赖分组

### 构建层低耦合

这些模块除 `api`、`foundation` 外没有其他内部 POM 依赖：

| 模块 | 判断 |
| --- | --- |
| `archives` | 档案域，低耦合。 |
| `basedata` | 基础数据域，低耦合。 |
| `blackgray` | 黑灰名单域，Java/POM 层低耦合。 |
| `budget` | 预算域，已进入低耦合观察。 |
| `capital` | 资金流水/核销域，模块本体低耦合，复杂编排仍在 application。 |
| `customer` | 客户主数据域，低耦合但被大量外域读取。 |
| `document` | 横向文档能力，低耦合。 |
| `fund` | 融资资金域，低耦合但被指标/财务读取。 |
| `kpi` | KPI 域，低耦合。 |
| `liquidity` | 流动性域，低耦合。 |
| `margin` | 保证金域，低耦合。 |
| `message` | 横向消息能力，低耦合。 |
| `system` | 用户组织权限和系统配置，低耦合。 |
| `third` | 横向第三方集成，低耦合。 |
| `workbench` | 工作台域，低耦合。 |
| `workflow` | 横向流程能力，低耦合。 |

### 轻依赖模块

这些模块只依赖少量横向能力或上游基础事实：

| 模块 | 额外内部依赖 | 判断 |
| --- | --- | --- |
| `afterlease` | `document` | 租后域依赖文档能力，合理。 |
| `assetclassify` | `document` | 资产分类依赖材料/文档能力，合理。 |
| `associationreport` | `basedata` | 协会报送依赖基础数据，合理。 |
| `credit` | `document` | 授信依赖文档能力，合理。 |
| `creditreport` | `document`, `third` | 征信依赖文档和外部接口，合理。 |
| `filingmaterials` | `document` | 归档准备依赖文件能力，合理。 |
| `ftp` | `basedata` | 定价依赖基础数据，合理。 |
| `payment` | `document` | 付款依赖材料版本处理，暂可接受。 |
| `policy` | `document` | 保单材料依赖文档能力，合理。 |
| `projectprocess` | `document` | 项目材料/模板依赖文档能力，合理。 |
| `leaseholdproperty` | `document`, `basedata`, `third` | 租赁物评估/OCR/基础字典，暂可接受。 |
| `rating` | `basedata`, `customer`, `workflow` | 评级仍读取客户/流程事实，后续可继续 port 化。 |

### 高耦合或读侧聚合

| 模块 | POM 依赖特征 | 当前处理方式 |
| --- | --- | --- |
| `collection` | 依赖 `payment`、`contract` | 收款和合同/付款事实交叉，先收窄直接 model/mapper 读取。 |
| `contract` | 依赖 `document`、`customer`、`basedata`、`workflow`、`projectprocess`、`third` | 核心交易模块，先拆错误方向依赖，不做大迁移。 |
| `dashboard` | 依赖客户、合同、付款、资金、项目、KPI、租后等 | 看板读侧聚合，依赖多是业务特性。 |
| `finance` | 依赖 collection/contract/customer/third/metric/basedata/workflow/fund/kpi/ftp/payment/assetclassify | 财务聚合域，治理重点是事实输入快照化。 |
| `metric` | 依赖 third/basedata/projectprocess/contract/fund/kpi/payment/collection/assetclassify | 指标聚合域，先承认聚合属性，再拆核心写域反向泄漏。 |
| `report` | 依赖 system/basedata/contract/workflow/customer/projectprocess/payment/collection | 征信/监管报送聚合，依赖多是读侧特性。 |
| `riskcontrol` | 依赖 customer/contract/projectprocess/payment/collection | 风控运营域，剩余外域读取应继续收敛。 |
| `application` | 依赖几乎所有业务域 | 装配层，依赖多符合定位，但不能承载单域实现。 |
| `web` | 依赖 `application`、`system`、`report` | 启动装配模块；`web -> report` 当前主源码无明显引用，但可能是运行装配依赖，不能只凭 import 删除。 |

## 主源码实际引用热点

下面只列业务/横向模块之间的主要引用，不列 `api`、`foundation`。

| 模块 | 主要外域引用计数 | 判断 |
| --- | --- | --- |
| `application` | `contract` 1793、`fund` 988、`customer` 917、`projectprocess` 910、`workflow` 740、`payment` 469、`document` 413、`afterlease` 365、`collection` 322、`third` 322、`system` 318 | 装配层热点正常，但仍要继续回迁单域实现。 |
| `contract` | `projectprocess` 33、`customer` 32、`third` 22、`document` 8、`workflow` 8、`basedata` 4 | 核心交易高耦合，先做切片分析。 |
| `finance` | `contract` 30、`third` 21、`collection` 14、`workflow` 13、`kpi` 12、`metric` 8、`payment` 6 | 财务聚合高耦合，适合事实快照化。 |
| `metric` | `fund` 64、`contract` 45、`payment` 34、`projectprocess` 17、`collection` 14、`assetclassify` 11、`kpi` 10 | 指标聚合高耦合，不能按普通业务写域治理。 |
| `report` | `contract` 129、`payment` 64、`customer` 43、`collection` 39、`projectprocess` 12、`workflow` 8、`system` 5 | 报送读侧聚合，高依赖可接受但需避免被核心域反向依赖。 |
| `dashboard` | `afterlease` 14、`contract` 14、`kpi` 14、`payment` 14、`projectprocess` 11、`customer` 9 | 看板读侧聚合。 |
| `riskcontrol` | `customer` 13、`collection` 7、`contract` 6、`payment` 6、`projectprocess` 2 | 已明显收敛，适合继续做小切片。 |
| `collection` | `contract` 14、`payment` 6 | 收款域需要继续和合同/付款解耦。 |
| `rating` | `workflow` 14、`customer` 11、`basedata` 4 | 评级对客户/流程输入仍可继续 port 化。 |

## resources 层跨域引用

resources/XML 中的类型引用较少，但很关键，因为它们通常代表跨域 SQL 或 mapper resultType：

| 模块 | resources 引用 | 判断 |
| --- | --- | --- |
| `application` | `budget` 3、`policy` 3、`payment` 2、`liquidity` 1、`margin` 1 | application 正在承接跨域读侧 SQL，短期合理；长期要防止它变成新的 SQL 聚合垃圾桶。 |
| `contract` | `customer` 1 | 合同 mapper 仍有客户 DTO 结果类型。 |
| `dashboard` | `customer` 2 | 看板读侧 mapper 直接映射客户模型，符合聚合特性但可后续投影化。 |

## POM 依赖复查候选

当前只有一个明显候选：

| 候选 | 证据 | 建议 |
| --- | --- | --- |
| `web -> report` | `zswl-mithras-web/pom.xml` 声明依赖 `report`，但 `web/src/main` 未发现 `cn.zswltech.mithras.report` 主源码或资源引用。 | 先保留。`web` 是最终启动装配模块，依赖可能用于 Spring Bean 扫描和运行时装配；只有启动验证证明不需要时再删除。 |

## 下一轮建议

下一轮建议从 finance 大域的小模块继续推进，而不是先动高耦合大块：

1. `margin`：模块小，POM/Java 层已经低耦合；适合做一次 package/class/mapper/resource 复核，确认没有旧包名、无效 import、POM 冗余和 application 外溢。
2. `capital`：模块本体低耦合，但 application 中还有大量资金流水/核销编排；适合继续区分“capital 自有规则”和“跨域核销编排”，只回迁能直接落地的单域规则。
3. `liquidity`：低耦合但包名里存在 `liquidityrisk` 历史语义；适合复核 package 命名、snapshot 输入和 resources 查询，避免它重新直接依赖 fund/contract/payment。

暂缓对象：

- `contract`、`finance`、`metric`、`report`、`dashboard`：都属于核心交易或读侧聚合，先做切片分析和 port/快照化，不适合做一轮大改包。
- `web -> report`：先保留，等启动装配验证再判断是否能删。
