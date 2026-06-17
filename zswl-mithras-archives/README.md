# zswl-mithras-archives

`zswl-mithras-archives` 是档案管理域模块，负责档案模板、档案管理、档案借阅、档案下载审批、档案下载权限等档案生命周期能力。

本模块的核心语义是“业务资料进入档案后的管理与使用”。它不同于 document 的文件能力，也不同于 filingmaterials 的归档资料清单和归档准备过程。

当前模块 POM 和 Java import 都保持低耦合，直接依赖只有 api、foundation 和框架 provided 依赖。直接 `org.mybatis:mybatis` 只为 mapper `@Param` 注解提供显式依赖，已由 MyBatis-Plus starter 传递覆盖并移除。流程、通知、项目/客户/组织用户信息、材料文件读取已经通过 `archives.application.port` 下的 `ArchivesWorkflowPort`、`ArchivesNotificationPort`、`ArchivesSupportPort` 由 application 适配。

需要注意的是，资源层还有表级读模型耦合：`ArchiveTemplateMapper.xml` 直接读取 `materials_list where business_type = "ARCHIVES"`，用于档案文件展示和必传文件统计；`ArchivesManagementMapper.xml` 直接 join `proj_establish_base_info`，用于档案列表展示项目名称、主办人、业务部门、客户和业务类型。材料文件读取、流程和通知已经通过 `ArchivesSupportPort`、`ArchivesWorkflowPort`、`ArchivesNotificationPort` 由 application 适配。短期可以接受，但应明确：文件存储、材料清单和项目主数据不是 archives 的主生命周期。

当前判断是：保持独立，不并入 `document` 或 `filingmaterials`。后续整理重点是保持档案生命周期边界清晰：文件存储、模板渲染、业务材料校验应由相应横向能力或业务域提供，档案模块只拥有正式档案、档案模板、借阅/下载权限和审批状态。
