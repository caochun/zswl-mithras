# zswl-mithras-application

`zswl-mithras-application` 是跨域编排与适配模块，负责把多个业务域、横向能力和外部系统组装成完整应用行为。

本模块的核心语义是“应用级编排层”。它可以依赖多个业务域，实现 port adapter、facade、流程动态表单、流程结束处理、事件监听、文档/消息/流程衔接等跨域逻辑。

本模块不应成为新业务域的长期归宿。能归属明确业务域的代码应迁回对应模块；确实需要跨域协作的逻辑可以留在 application。
