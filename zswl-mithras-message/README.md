# zswl-mithras-message

`zswl-mithras-message` 是消息通知能力模块，负责站内消息、待办、通知、邮件、钉钉、OA 消息同步和消息初始化任务。

本模块的核心语义是“消息通道与通知投递”。它不应理解具体业务域流程，也不应直接适配 workflow 或业务域。

理想协作方式是：业务域或 workflow 暴露通知需求，由 application 把这些需求转换为 message 可发送的消息。

邮件发送需要读取系统配置时，通过 `message.application.port.MessageSystemConfigPort` 表达出站依赖，由 application 适配 system 配置查询，message 不直接依赖 system。
