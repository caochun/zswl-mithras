# zswl-mithras-document

`zswl-mithras-document` 是文件与文档能力模块，负责材料清单、文件模板、OnlyOffice、OCR、文件迁移、材料版本快照、文件导出和权限配置。

本模块的核心语义是“文件、模板、材料和文档处理能力”。它不应持有具体业务域规则，而应通过 port 获取业务变量、字典和上下文。

后续整理重点是保持横向能力属性，避免 document 直接适配具体业务域流程。
