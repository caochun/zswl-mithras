# zswl-mithras-report

`zswl-mithras-report` 是报表装配模块，负责最终报表能力的装配、报表资源和部分报表相关流程资源。

本模块的核心语义是“面向应用交付的报表组合”。它依赖 application、contract、workflow，更多承担装配角色，而不是单一业务域。

后续整理重点是评估它与 dashboard、metric、associationreport 的边界，避免报表逻辑分散到多个含义重叠的位置。
