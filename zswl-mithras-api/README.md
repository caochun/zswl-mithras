# zswl-mithras-api

`zswl-mithras-api` 是公共契约模块，负责承载跨模块共享的 DTO、REQ/RSP、注解、校验器和少量公共 API 类型。

本模块的核心语义是“稳定的模块间接口契约”，不是业务实现层。它可以定义跨模块通信需要的结构，但不应承载业务服务、持久化模型或具体业务流程逻辑。

## 边界

本模块适合放：

- 跨模块共享的 API 接口。
- 对外或跨模块稳定使用的 DTO、REQ、RSP。
- 通用响应结构、分页请求、公共校验注解和校验器。
- 极少量为了契约表达必须共享的注解。

本模块不适合放：

- 业务服务实现、流程编排、持久化 mapper/model。
- 单一业务域内部使用的 DTO。
- 具体业务规则、业务枚举膨胀和业务流程状态机。
- 为某个业务实现服务的日志、缓存、数据库或第三方系统逻辑。

## 当前状态

`api` 当前没有依赖其他业务模块，也没有 resources 资源文件，是最底层的契约模块之一。它的主要风险不是业务模块依赖，而是源码规模过大：当前约 2600 个 `dto` 文件、490 个 `api` 文件，已经承载了很多业务域的请求/响应对象。

POM 依赖基本都有源码证据：

- `swagger-annotations` 用于 API/DTO 文档注解。
- `validation-api`、`hibernate-validator` 用于请求参数校验。
- `spring-web`、`spring-context` 用于 API 接口上的 Spring MVC 注解、文件上传和少量 Spring 注解。
- `jackson-annotations`、`jackson-databind` 用于 JSON 注解和自定义序列化/反序列化。
- `hutool-all` 当前仍被部分 DTO/API 使用。
- `mybatis-plus` 当前仍被少量 DTO 使用 `@TableField`、`@TableId`、`Page`，这是契约层的持久化味道，后续应逐步消除。

本轮已移除 `slf4j-api` 依赖，并清理 `DashboardFundFinanceBaseREQ` 中 DTO 层的 `@Slf4j` 日志副作用。契约对象解析失败时只做兜底赋值，不在契约层写日志。

## 后续整理方向

后续整理重点是收敛过度膨胀的业务 DTO：只有真正被多个模块共同使用、且适合作为公开契约的类型才应留在这里。

对于单一业务域内部 DTO，应优先下沉到对应业务域模块；对于跨域读模型，应明确它是稳定契约还是 application 装配层临时结构，避免 `api` 变成新的业务模型垃圾桶。
