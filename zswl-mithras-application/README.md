# zswl-mithras-application

`zswl-mithras-application` 是跨域编排与适配模块，负责把多个业务域、横向能力和外部系统组装成完整应用行为。

本模块的核心语义是“应用级编排层”。它可以依赖多个业务域，实现 port adapter、facade、流程动态表单、流程结束处理、事件监听、文档/消息/流程衔接等跨域逻辑。

本模块不应成为新业务域的长期归宿。能归属明确业务域的代码应迁回对应模块；确实需要跨域协作的逻辑可以留在 application。

从代码证据看，`application` 是当前全系统依赖最重的装配层，POM 直接依赖几乎所有业务域模块。源码 import 分布也符合这个定位：除了 `application` 自身和 `foundation`，主要集中在 `contract`、`dto`、`fund`、`customer`、`projectprocess`、`workflow`、`payment`、`document`、`afterlease`、`third`、`system`、`collection` 等模块。

当前包结构已经从早期“假顶层业务包”逐步收敛到 `orchestration/<domain>`、`orchestration/facade/<domain>`、`orchestration/adapter/<domain>`、`orchestration/job/<domain>`、`orchestration/listener/<domain>` 等形态。这个方向是合理的：跨域 facade、port adapter、流程动态表单、流程结束处理、消息/文档/第三方系统接线可以留在 application；只操作单个业务域自有 mapper/model/service 的实现应继续回迁到对应业务模块。

`src/main/resources/mapper/application` 中保留的是从业务域迁出的跨域读模型，例如付款流程列表、保单台账、预算付款事实、保证金列表、融资账户设置等。这些 mapper 放在 application 比放在 payment/policy/budget/margin/liquidity 等单域模块里更合理，因为它们直接 join 多个业务域或流程/系统表；但它们也不应无限增长，后续优先拆成 owner domain 快照输入，再由 application 做装配。

当前不建议把 `application` 和任何业务域合并。它的长期目标是成为薄装配层：依赖多可以接受，业务规则和持久化事实所有权不应留在这里。

本轮依赖复核确认：`application` 源码存在少量 MyBatis 注解和异常类型引用，主要集中在 application 读侧 mapper 和 `TrackEventService` 的 `MyBatisSystemException` 处理；直接 `org.mybatis:mybatis` / `org.mybatis:mybatis-spring` 依赖无独立必要性，已删除，相关类型由 MyBatis-Plus/tk-mybatis 等依赖链提供。`dependencyManagement` 中的 MyBatis/MyBatis-Spring 版本管理暂保留给运行装配和传递依赖约束使用。
