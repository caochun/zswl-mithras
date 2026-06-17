# zswl-mithras-policy

`zswl-mithras-policy` 是保单/保险管理域模块，负责保单信息、暂存、台账、版本、导入导出、续保提醒和保单相关材料。

本模块的核心语义是“保单事实及其生命周期管理”。它依赖 document 处理材料版本是合理的，也会读取付款阶段保单、合同和项目事实来形成保单台账，但不应吸收付款、合同、项目等业务域规则。

当前 Java 层已经通过 `PolicyProjectClientInfoPort`、`PolicyContractInfoPort`、`PolicyOperatorNamePort` 获取外部事实，避免直接依赖 projectprocess、contract、system。原 `PolicyInfoMapper.xml` 中用于保单台账列表的 `ledgerList` 会同时读取正式保单、付款保单、合同和项目字段，本轮已迁出到 application 读侧 `PolicyLedgerQueryMapper`，policy 自身不再承载这条台账聚合 SQL；批量保单号统计 `countPolicyCodes`、`countPolicyCodeNum` 也已迁到同一个 application 读侧 mapper，避免 policy mapper 继续读取付款保单表做校验聚合。

需要注意的是 `PolicyInfoMapper.xml` 仍有保单列表 `myList`、到期提醒 `nearPolicyEndTimeList`、付款保单最大到期日 `paymentMaxTimeList`、付款待续保 `listPaymentNeedRenewInsurance` 等查询直接 join `proj_review_base_info`、`payment_policy_info`、`payment_base_info`、`contract_base_info`。这些也属于保单与付款/合同/项目的读模型耦合，但牵涉列表权限、job、续保提醒和付款保单来源关系，后续应分批迁出，不适合一次性硬搬。本轮已删除只有注释引用的 `countPolicyCode` 单值统计 SQL，减少一条无运行时用途的付款表直读。

当前判断是：保持独立，不直接并入 `payment` 或 `contract`。付款阶段保单、合同关联和正式保单有来源关系，但生命周期不同；后续整理重点是把付款保单到正式保单的同步关系明确为接口或事件，并继续把剩余跨域读模型从 policy mapper 收敛到 application 或明确的查询 port。

从代码证据看，模块内 Java import 主要集中在 `policy` 约 61 处、`dto` 约 52 处、`foundation` 约 47 处、`api` 约 21 处，外部业务模块只有 `document` 约 3 处，说明 POM 低耦合基本真实。反向依赖主要来自 `application`、`web`、`payment`、`dashboard` 和 `projectprocess`；其中 `application` 的 facade、workflow end handler、job、payment-policy 同步、document file provider 和维护脚本更像装配层协作，不构成把 policy 合并进其他业务模块的理由。

POM 依赖复核后，`poi`、MyBatis-Plus、XXL Job、Hutool、commons-lang3、Spring context/web/tx、servlet、validation、annotation 都有源码使用；直接 `org.mybatis:mybatis` 未发现源码必要引用，已由 MyBatis-Plus starter 覆盖并移除。

当前不满足物理合并条件：它不是付款域的薄附件，也不是合同域的一个字段扩展，而是拥有正式保单、暂存保单、版本、审批、续保提醒和材料版本联动的独立生命周期。
