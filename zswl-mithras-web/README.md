# zswl-mithras-web

`zswl-mithras-web` 是后端应用启动与最终装配模块，负责 Spring Boot main、运行环境配置、迁移资源、监控探针和最终运行包。

本模块的核心语义是“应用装配与启动”。它不应承载业务逻辑，业务能力来自 application、report 和各业务域模块。

## 边界

本模块适合放：

- Spring Boot 启动类、健康探针、环境 profile 配置和日志配置。
- 最终运行包所需的依赖聚合。
- Flyway/运行期迁移资源、静态调试资源和运维监控配置。
- 非业务语义的全局 Web advice/filter/interceptor。

本模块不适合放：

- 单一业务域服务、mapper、model 或业务规则。
- 跨域编排逻辑；这类逻辑应放在 `application`。
- 横向能力实现；流程、消息、文档、第三方集成应分别归属对应模块。

## 当前状态

`web` 当前生产源码很少，主要是启动类、全局异常处理、测试/调试入口和少量 Web 拦截器。它通过 POM 聚合 `application`、`system` 与 `report`，并承载 Actuator、Prometheus、RocketMQ、环境配置、Flyway 迁移和测试工具依赖。

本轮已删除一个未启用、未引用的废弃 `ResponseBodyHandler`，避免启动模块继续保留合同提醒逻辑和 `contract` service 的假依赖信号。

仍需关注的历史点：

- `src/main/java/cn/zswltech/flow/core/api/FlowExecutionApiService.java` 是位于 web 模块内的 flow-core 包覆写/补丁类，实际被 workflow/application 使用。长期看它不应属于 web，应该回到 workflow/flow-core 扩展治理线。
- `TestController` 和 `ApprovalTestInterceptor` 属于本地调试/审批测试入口，应避免进入生产路径或继续扩散业务触发逻辑。
- `web -> system` 是全局异常处理对系统开关、审计日志、用户名称解析和系统开关刷新事件的直接生产依赖，当前显式声明；长期如果全局 Web advice 下沉到 application 或 system 提供 Web starter，再重新评估。
- `web -> report` 是最终装配依赖，短期保留；若未来把 `report` 纳入 `application` 装配，可再评估。

`web` 从“能否合并”角度看，确实可以作为 application 的启动子包存在；但 Maven 独立模块仍有最终打包、运行配置、迁移资源和测试工具隔离价值。短期保持独立，更重要的是继续确保业务逻辑不往 web 增长。
