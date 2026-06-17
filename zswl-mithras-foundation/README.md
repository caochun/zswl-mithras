# zswl-mithras-foundation

`zswl-mithras-foundation` 是技术底座和跨域基础能力模块，负责通用异常、通用注解、基础权限抽象、上下文、Redis/异步、版本、数据对比、通用 mapper/model、基础工具、Excel/HTTP/模板工具和少量跨域 port。

本模块的核心语义是“业务无关或弱业务相关的底层能力”。它可以提供公共抽象和稳定基础设施，但不应拥有具体业务域规则、业务主表生命周期或单一业务域的专有词表。

## 边界

适合放在 foundation：

- 通用异常、通用注解、通用上下文、技术工具类。
- 数据权限、主键提取、版本、异步任务、数据比较等跨域机制。
- Redis 锁、延迟队列、HTTP、Excel、FreeMarker 等基础设施封装。
- 不绑定具体业务流程的基础 port，例如当前用户、组织、岗位、流程启动等稳定抽象。

不适合继续下沉到 foundation：

- 某个业务域专属的枚举、评分规则、导出类型、缓存 key。
- 依赖具体业务表字段命名的 SQL 片段。
- 为了让两个业务域互相调用而放进来的 resolver。
- 只有单个业务域理解或维护的常量、字典、模板配置。

## 当前遗留问题

- `Constant` 中包含航运评级模型、租赁物查重字段、五级分类等具体业务规则，语义上更接近 rating、leaseholdproperty 或风险/资产分类相关域。
- `FileExportEnum` 主要是 dashboard/workbench/finance 导出项，不是技术底座枚举。
- `CacheEnum` 包含合同起租、立项、集团授信、租金催收、流动性、KPI 等业务锁 key。
- `JobEnum` 是岗位编码共享词表，使用面很广，更接近 system 主数据契约；短期留在 foundation 是为了兼容，长期应评估收敛到 system 契约或配置。
- `RiskControlIndustryClassify` 是风控行业分类，当前被 riskcontrol、afterlease、contract/application 等多处共享；短期不能直接迁移，长期应明确为风险口径词表或客户/行业分类契约。
- `CommonMapper.xml` 的权限 SQL 直接使用 `proj_sponsor_user_id`、`proj_cosponsor_user_ids` 等项目字段，属于项目口径权限片段，不是纯 foundation SQL。
- `ClientNameResolver`、`ClientInfoResolver`、`ContractInfoResolver` 等 port 承载客户/合同事实解析，短期作为跨域兼容抽象存在，后续应由 owning domain 自有 port 或 application adapter 收窄。

## 后续方向

`foundation` 当前不建议物理合并到任何模块，也不建议大规模一次性搬迁。更稳的路线是：新增内容严格守住底座边界；遗留业务语义按引用面和归属逐步迁出；迁移前先用业务域自有 port、system/basedata 契约或 application adapter 承接调用方。
