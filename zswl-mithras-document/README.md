# zswl-mithras-document

`zswl-mithras-document` 是文件与文档能力模块，负责材料清单、文件模板、OnlyOffice、OCR、文件迁移、材料版本快照、文件导出和权限配置。

本模块的核心语义是“文件、模板、材料和文档处理能力”。它不应持有具体业务域规则，而应通过 port 获取业务变量、字典和上下文。

代码层复核显示，`document` 当前 POM 只依赖 `api`、`foundation` 和框架/文件处理类依赖，模块内 Java import 没有直接指向其他业务域；`oss-toolkit`、MyBatis-Plus、Spring、MapStruct、Swagger、validation、XXL job 等依赖都能在源码中找到实际使用点。直接 `org.mybatis:mybatis` 只为 mapper `@Param` 注解提供显式依赖，已由 MyBatis-Plus starter 传递覆盖并移除。模块内约 59 个 Java 文件，主要分布在 `enums`、`persistence`、`file/template`、`application`、`onlyoffice`、`job` 和 `materialsfile` 等包。

`document` 与 `filingmaterials`、`archives` 同属 records 上位语义，但生命周期不同：`document` 管文件、材料清单、模板、OnlyOffice 和 OCR；`filingmaterials` 管业务资料进入正式档案前的归档准备、资料台账和流程；`archives` 管进入正式档案后的档案模板、借阅、下载权限和审批。当前不建议把三者物理合并，否则 document 会被迫理解归档流程和档案管理状态，横向能力边界会被污染。

需要注意的是，`document` 的 `FileTemplateEnum`、材料类型和模板名称里存在大量业务模板名称，这是横向能力向业务场景提供模板资源的结果。后续新增模板时，应优先让业务域或 application 提供业务变量和上下文，document 只负责模板、文件和材料清单能力。

结论：保持独立横向能力模块。后续整理重点是保持 document 不直接适配具体业务域流程；复杂的业务材料校验、归档准备、档案审批和跨域文件组装应放在业务域或 application 中，通过 port/模板服务调用 document。
