# 模块内部包名与类名体检

本文基于当前工作区源码，对每个后端模块内部的 package、class 命名和目录语义做一次体检。结论只针对“是否需要重构、优先级如何”，不把所有历史命名都视为必须立即修改。

## 判断口径

- package 根是否与模块语义一致。
- 模块内部是否同时按技术层、业务子域、历史名称混排。
- class 名是否出现 `New`、`Old`、`Tmp`、`Temp`、`Copy`、`Test`、`newimpl`、`implnew` 等历史信号。
- `model`、`bo`、`vo`、`entity`、`dto.persistence`、`common`、`util/utils` 等命名是否让职责变模糊。
- 是否存在空目录或已迁移后残留目录。

扫描结果：源码 package 声明与文件路径一致，没有 package/path 不一致的问题。真正的问题主要是业务语义命名和历史目录结构。

## 优先级结论

| 优先级 | 问题 | 建议 |
| --- | --- | --- |
| P0 | `dashboard` 模块曾存在 `cn.zswltech.mithras.guanbao` 根包 | 已统一到 `cn.zswltech.mithras.dashboard.guanbao`。后续再判断是否拆成独立管理报表模块。 |
| P0 | `web` 模块存在 `cn.zswltech.flow.core.api.FlowExecutionApiService` | 已标注为 flow-core runtime override/patch，暂不改包；迁移前必须验证 flow-core bean 覆写机制。 |
| P0 | `web` 模块有 `TestController`、`ApprovalTestInterceptor` | 已加 `@Profile({"local", "dev"})` 隔离。后续仍可考虑彻底迁到 test source 或运维工具包。 |
| P1 | `application` 全部压在 `application.orchestration` 下 | `orchestration` 已经变成万能目录。建议逐步收敛为 `adapter`、`facade`、`listener`、`job`、`workflow`、`document`、`auth`，并继续把单域实现回迁业务模块。 |
| P1 | `api` 同时存在 `cn.zswltech.mithras.api.*` 和 `cn.zswltech.mithras.dto.*` | 公共契约包过大，且顶层命名混合。短期不建议机械迁移，新增契约应按业务域归档，长期按 domain contract 拆分。 |
| P1 | 多模块根下使用泛化 `model`/`mapper.model` | MyBatis entity 和应用 DTO 混在一起时语义不清。建议新模块统一用 `persistence.model`，旧模块逐步迁移。 |
| P1 | `report.handler.impl.newimpl`、若干 `*CheckerNew` | `report.handler.impl.newimpl` 已改为 `report.handler.impl.current`；类名中的 `New` 需要确认旧实现是否已下线，再改为按场景命名。 |
| P2 | `Tmp`/`Temp` 类名较多 | 有些是业务“暂存/模板”，不是临时代码。需要逐个区分：业务暂存建议改为 `Staging`，文件模板保持 `Template`。 |
| P2 | `util/utils/common/bo/vo` 泛化包 | 不急着全局改，但新增代码应避免继续扩散。 |
| P2 | `associationreport` 根包直接放工具/选择器/异常 | 已归入 `support`、`storedata`、`exception`，根包不再承载散落支撑类。 |
| P2 | `workbench.application.job.Initjob` 类名格式不规范 | 已改为 `InitJob`，XXL-Job handler 名保持 `workbenchInitJob` 不变。 |
| P3 | 已空目录 | 可删除，不影响编译。 |

## 全局命名建议

后续新增和迁移时建议统一下面几个约定：

| 当前常见命名 | 建议 |
| --- | --- |
| `model` 承载数据库实体 | `persistence.model` |
| `mapper.model` | `persistence.model`，mapper 放 `persistence.mapper` |
| `dto.persistence` | 如果是 mapper 查询投影，改为 `persistence.projection` 或 `persistence.query` |
| `bo`/`vo` | 优先按用途命名：`command`、`query`、`result`、`snapshot`、`view` |
| `common` | 拆成具体语义，如 `constant`、`enums`、`support`、`shared` |
| `util/utils` | 只保留无状态纯工具；业务规则 helper 应放回业务 package |
| `Tmp` | 业务暂存用 `Staging`，临时代码应删除 |
| `New`/`Old` | 如果是业务版本，改成 `legacy/current` package 或明确版本名；如果是历史迁移残留，逐步去掉 |
| `Lib` | 如果表示版本库/历史库，可以保留；如果只是复制表，建议用 `versioning` 或 `history` 语义表达 |

## 逐模块判断

| 模块 | 当前观察 | 是否建议重构 |
| --- | --- | --- |
| `api` | 3094 个 Java 文件，`dto.*` 远大于 `api.*`；顶层还存在 `annotation`、`validation`。业务 DTO 横跨所有域。 | 需要治理，但不建议一次改包。新增契约应按业务域收口，长期拆分公共 API。 |
| `foundation` | 已基本统一到 `foundation.*`，结构有 `port`、`auth`、`persistence`、`version`、`datacompare`。 | 暂不大改。少量 `ApprovalTestUtil`、泛化 `util/common` 后续可清理。 |
| `system` | `infrastructure`、`application`、`audit`、`user` 并存，语义可接受；`mapper/model` 仍是旧式。 | 小幅优化即可，优先把 `mapper.model` 逐步迁到 `persistence.model`。 |
| `basedata` | 包结构较清楚：`dictionary`、`job`、`application.bankaccount`、`persistence`。 | 基本合理。`util.DateUtil` 命名泛，可后续改成更具体的日期规则服务。 |
| `workflow` | `flow`、`process.prepare`、`persistence`、`controller` 清楚。 | 基本合理。`CommonProcessPrepare` 属于通用流程准备概念，可保留。 |
| `document` | `file.template`、`materialsfile`、`onlyoffice`、`persistence` 清楚。 | 基本合理。`FileTemplate` 是业务模板，不属于临时代码。 |
| `message` | `client`、`service`、`persistence`、`model` 清楚，体量小。 | 基本合理。可把通道实现进一步按 `client.ding/email/cico` 保持。 |
| `third` | 按外部系统分包，符合第三方集成模块特点。 | 基本合理。`Temp` 类如果表示财务共享临时记录，应改为 `Staging` 或 `Buffer` 更清楚。 |
| `customer` | `application.client`、`model.client`、`mapper.client/corp/lib`、`versioning` 并存，`NewCorp*` 很多。 | 需要中期整理。`NewCorp` 多半是历史客户模型版本，不建议盲改；先统一 persistence 包结构。 |
| `projectprocess` | 按 `projestablish/projreview/projpricing/projlifecycle` 子域分包，整体合理。 | 基本合理。`dto.persistence` 建议改成 `persistence.query/projection`。 |
| `contract` | `core`、`overdue`、`versioning`、`application`、`model/mapper.contract` 并存；合同域复杂但语义成立。 | 需要渐进整理。优先统一 `model/mapper` 到 `persistence`，并把 `overdue.domain/share` 继续打磨。 |
| `payment` | `application`、`pubinfo`、`versioning`、`gendoc`、`event` 清楚。 | 基本合理。`model`/`mapper` 可后续迁到 `persistence`。 |
| `collection` | `application.job` 占比较高，`overdue`、`gendoc`、`contractcp` 语义清楚。 | 小幅整理。删除空 `adapter/*` 残留目录，`convert.contractcp` 空目录可删。 |
| `fund` | `financing`、`receiptrepay`、`directfinancing`、`versioning` 比较清楚。 | 基本合理。旧式 `persistence.mapper/model` 已存在，可作为其他模块参考。 |
| `capital` | 体量小，`application`、`service`、`job`、`persistence` 分散。 | 建议后续按“银行流水/业务流水/核销”业务子域重排，先不急。 |
| `finance` | `monthly`、`projectdistribution`、`view`、`service.dashboard`、`mapper.finance` 混合，聚合味重。 | 需要 P1 分析。先区分财务自有写模型和 dashboard/view 读模型。 |
| `ftp` | `oldftp`、`newftp`、`common` 三分法清楚，且已确认 old/new 需要并存。 | 保持。不要为了去掉 `New` 破坏业务版本边界；后续可把 `newftp` 里的 `utils` 改为具体 helper。 |
| `margin` | `application.port`、`persistence`、`service` 简洁。 | 基本合理。可作为小模块样板。 |
| `liquidity` | `service.cal` 占比较大，`bo` 里很多 snapshot。 | 基本合理但命名可优化：snapshot 建议放 `application.snapshot` 或 `port.model`。 |
| `credit` | `creditlimit` 与 `groupcredit.establish/review` 子域清楚。 | 基本合理。`dto.persistence` 后续改 projection/query。 |
| `rating` | `model/mapper/service/application/versioning` 结构中规中矩。 | 基本合理。`ContractReceiptBottom` 出现在 rating 中语义偏外域，后续确认是否仍应留在 rating。 |
| `creditreport` | `dto.credit` 很大，`client.xj`、`service`、`versioning` 清楚。 | 基本合理。可考虑把 `dto.credit` 下的内部 DTO 按 report section 细分。 |
| `riskcontrol` | 按 `metric/report/scorecard/concentration/strategy` 分包，`common` 较大。 | 需要中期整理。`metric.subscriber.MetricComputer数字编码` 是指标编码驱动，业务上可接受，但建议增加注册表文档或按指标分类分包。 |
| `assetclassify` | `application`、`versioning`、`model/mapper` 清楚。 | 基本合理。`RiskFactorTemplate` 是模板业务，不属于临时代码。 |
| `blackgray` | `dto.req/rsp` 很完整，但 `service` 与 `application.audit` 并存；有自带 `RedisService`、Excel 工具。 | 小幅整理。基础 Redis/Excel 工具若通用应下沉，否则改成黑灰名单专用命名。 |
| `leaseholdproperty` | `application`、`facade`、`review`、`port`、`versioning` 清楚。 | 基本合理。空 `application.contract` 可删。 |
| `policy` | `info`、`ledger`、`staging`、`versioning` 语义清楚。 | 基本合理。`Tmp` 如表示暂存，建议长期改名为 `Staging`，但不是优先级最高。 |
| `afterlease` | `application` 过大，`NewAfterLease*` 多，且有空的洋葱架构目录残留。 | 需要整理但谨慎。`NewAfterLease` 很可能是新版租后检查业务名，先不要机械改类名；可先删空目录、按检查/报告/催收子域拆 application。 |
| `filingmaterials` | `application`、`ledger`、`gendoc`、`model/mapper` 清楚。 | 基本合理。空 `email` 目录可删。 |
| `archives` | `application`、`port`、`persistence`、`projection` 清楚。 | 合理。模板是业务概念，不是 Temp 问题。 |
| `budget` | `application`、`mapper/model`、`bo/ecl`、`application.port` 清楚。 | 基本合理。后续统一 persistence 包即可。 |
| `kpi` | `distribution.versioning`、`application.config/ecl/performance/projguess` 清楚。 | 基本合理。`dto.persistence` 后续改 projection/query。 |
| `metric` | `financialcloudmetric.calculator` 有 248 个类，`aggregator`、`emit`、`enums.risk` 清楚但局部过密。 | 需要专题整理。优先把 calculator 按指标族分包，而不是改类名。 |
| `dashboard` | 管理报表/观远相关代码已收敛到 `dashboard.guanbao.*`。 | 根包污染已处理。后续评估是否拆成独立管理报表模块。 |
| `report` | `mapper.base/draft/formal/fullsnap/procsnap` 与 `service.*` 对齐；`handler.impl.current` 承载当前版征信报送 handler。 | 需要小幅整理。后续重点是确认 `*NewHandler` 是否仍需与旧 handler 并存。 |
| `associationreport` | `mapper`、`service`、`controller`、`storedata`、`excel` 清楚；根包散落支撑类已清理。 | 基本合理。后续可继续把 `mapper/model` 统一到 persistence 语义。 |
| `workbench` | `application.cardcal`、`mapper`、`controller.metric` 清楚；`Initjob` 已改为 `InitJob`。 | 基本合理。`cardcal` 可改为 `cardmetric` 或 `calculator`，不是急事。 |
| `application` | `orchestration.adapter`、`document`、`workflow`、`facade` 占大头；仍有不少单域实现。 | P1。不要一次改包，后续每次迁移单域实现时同步整理 package。 |
| `web` | 启动类之外有异常处理、测试 controller/interceptor、flow-core override。 | 测试入口已用 profile 隔离，flow-core override 已加说明；后续再评估是否迁出 web。 |

## 空目录

迁移后残留的空目录已清理。当前扫描 `src/main/java` 下没有空目录残留。

```text
zswl-mithras-afterlease/src/main/java/cn/zswltech/mithras/afterlease/adapter
zswl-mithras-afterlease/src/main/java/cn/zswltech/mithras/afterlease/application/process/prepare/handle
zswl-mithras-afterlease/src/main/java/cn/zswltech/mithras/afterlease/application/workflow/dynamicform
zswl-mithras-afterlease/src/main/java/cn/zswltech/mithras/afterlease/application/workflow/listener/endhandler
zswl-mithras-afterlease/src/main/java/cn/zswltech/mithras/afterlease/domain
zswl-mithras-afterlease/src/main/java/cn/zswltech/mithras/afterlease/infrastructure
zswl-mithras-api/src/main/java/cn/zswltech/mithras/api/cvicse
zswl-mithras-api/src/main/java/cn/zswltech/mithras/dto/cvicse
zswl-mithras-collection/src/main/java/cn/zswltech/mithras/collection/adapter/collection
zswl-mithras-collection/src/main/java/cn/zswltech/mithras/collection/adapter/margin
zswl-mithras-collection/src/main/java/cn/zswltech/mithras/collection/adapter/workflow
zswl-mithras-collection/src/main/java/cn/zswltech/mithras/collection/convert/contract
zswl-mithras-filingmaterials/src/main/java/cn/zswltech/mithras/filingmaterials/email
zswl-mithras-finance/src/main/java/cn/zswltech/mithras/finance/adapter/metric
zswl-mithras-leaseholdproperty/src/main/java/cn/zswltech/mithras/leaseholdproperty/application/contract
zswl-mithras-report/src/main/java/cn/zswltech/mithras/report/port
zswl-mithras-riskcontrol/src/main/java/cn/zswltech/mithras/riskcontrol/scorecard/adapter
```

## 建议执行顺序

1. 给 `application` 制定更明确的包边界：`adapter` 只做端口适配，`facade` 只做跨域门面，单域实现继续回迁。
2. 以一个低耦合模块为试点，把 `model/mapper` 改成 `persistence.model/persistence.mapper`，验证成本和收益后再推广。
3. 最后再处理 `New/Tmp` 类名。只改历史残留，不改真实业务版本名。
