# zswl-mithras-system

`zswl-mithras-system` 是系统能力模块，负责用户、组织、角色、权限、系统配置、审计日志、下载、表达式和后台基础能力。

本模块的核心语义是“系统运行所需的身份、组织、权限和配置实现”。它可以作为 foundation 中用户/组织相关 port 的实现方。

## 边界

本模块适合放：

- 用户、组织、岗位、角色、权限和登录态相关能力。
- 系统配置、用户自定义配置、系统开关和审计日志。
- 系统运行期基础设施配置，如 Jackson、Swagger、校验器、线程池、数据源、Redis 登录态、XXL Job 执行器等。
- 面向全局系统能力的表达式执行、下载入口、API 注册辅助和 OSS 预览封装。

本模块不适合放：

- 客户、项目、合同、付款、收款、风控等具体业务域规则。
- 业务流程准备、业务审批动作和跨域编排。
- 具体业务域的 mapper/model 或业务表读写。

## 当前状态

`system` 当前源码没有直接 import 具体业务域模块，POM 只依赖 `api`、`foundation` 和系统运行所需技术依赖。模块内约 72 个 Java 文件，资源文件主要是系统表、Gruul 体系初始化、系统功能组和项目编号存储表。

技术依赖均有源码使用证据：Gruul 用于用户组织权限，MyBatis-Plus 用于 mapper/service，Hutool/Fastjson/Jackson 用于配置和 JSON 处理，Servlet/Spring Web 用于 Web 适配，Redis 用于登录态，Druid/JDBC 用于数据源和删除审计，AspectJ 用于系统 AOP，XXL Job 用于任务执行器，QLExpress 用于表达式能力，OSS toolkit 用于预览地址。

一个需要持续观察的边界味道是 `ProjCodeStoreService` / `proj_code_store`。它名称上是“项目编号”，但当前实现只在 system 内部出现，承担更像全局编号存储/唯一性服务的职责；暂不迁到 `projectprocess`，后续若只服务项目主链路，可再评估是否下沉。

后续整理重点是保持 system 与具体业务域分离：system 提供身份、组织、权限、配置和系统运行事实，不承载客户、项目、合同等业务规则。
