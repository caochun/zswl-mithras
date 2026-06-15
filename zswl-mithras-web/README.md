# zswl-mithras-web

`zswl-mithras-web` 是后端应用启动与最终装配模块，负责 Spring Boot main、运行环境配置、迁移资源、监控探针和最终运行包。

本模块的核心语义是“应用装配与启动”。它不应承载业务逻辑，业务能力来自 application、report 和各业务域模块。

后续整理重点是保持 web 轻量，只做启动、配置和运行期资源装配。
