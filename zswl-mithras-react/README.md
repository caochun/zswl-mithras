# Mithras React 前端

`zswl-mithras-react` 是 Mithras 系统的前端工程，承载客户、项目、合同、付款、收款、风控、报表、流程、文件等业务页面和用户交互。

前端的核心语义是“用户界面与前端交互编排”。业务规则、审批状态、权限判断和数据一致性以后端接口为准；前端负责展示、输入校验、页面流转、轻量状态管理和操作反馈。

## 分层边界

前端目录不需要和后端 Maven 模块一比一对应。前端更贴近菜单、路由和用户工作流，但代码复用关系必须清楚。

- `src/pages`：路由入口和页面私有代码。页面目录内的组件、配置、`api.js` 默认只服务当前页面或当前业务域内部。
- `src/components`：跨业务域复用的通用 UI、表格、表单、金额、文件、流程图、操作按钮等组件。
- `src/api`：跨页面复用的接口封装。若某个 `pages/**/api.js` 被其他业务域引用，应迁入这里或对应稳定领域入口。
- `src/utils`：无页面语义、无业务归属的工具函数、格式化、hooks、校验逻辑。
- `src/layout`：应用壳能力，包括菜单、消息、登录、布局和全局导航交互。

后续若新增 `src/features/<domain>`，它应承载某个业务域可复用的前端能力，例如领域组件、领域 hooks、领域配置和领域 API 包装；`pages` 只作为路由装配层使用。

## 依赖规则

- 禁止一个业务页面长期直接复用另一个业务页面目录下的组件、配置或 API。
- `src/pages/<domainA>` 引用 `src/pages/<domainB>` 时，应先判断被引用内容是否应迁到 `src/components`、`src/api`、`src/utils` 或未来的 `src/features/<domain>`。
- 工作台、报表、流程这类聚合页面可以编排多个业务域页面，但聚合逻辑应显式，不能让聚合页面变成公共组件库。
- `process` 负责流程壳和审批交互；业务详情组件应尽量由业务域提供，再由流程页面进行装配。
- 通用组件应保持无业务名称、无菜单名称、无特定页面状态依赖。

## 当前边界收敛

- 表格合计行统一使用 `src/components/Table/Summary`。
- 文件导出按钮统一使用 `src/components/Actions/FileExport` 或 `src/components/Actions` 导出的 `FileExportAction`。
- 财务、预算等外部页面不再从 `dashboard/workbench/components` 取通用表格合计和文件导出能力。
- `dashboard/workbench/components` 暂时保留工作台内部私有组件；后续只处理确实跨业务域复用的部分。

## 整理优先级

1. 先统计真实 import 关系，优先处理被跨域引用次数高的 `pages/**` 代码。
2. 先迁移语义清楚、行为稳定的公共组件和工具，避免大规模搬目录。
3. API 入口逐步从 `pages/**/api.js` 收敛到 `src/api/<domain>`。
4. 对 `process`、`dashboard`、`report` 这类聚合页面，先明确它们是“编排/展示面”，再拆出被复用的业务能力。
5. 每轮整理后进行构建验证，并避免无关格式化和大面积重排。

## 项目命令

- `npm run start`：启动本地开发服务。
- `npm run build`：构建当前环境。
- `npm run buildAll`：执行多环境打包。
- `npm run api`：根据 `admin.config.js` 中的 YApi 配置生成接口。
- `npm run page`：生成页面脚手架。

## 项目约定

1. 金额展示使用千分位并保留 2 位小数；表格列中的金额右对齐。
2. 按钮权限根据后端返回的接口标识判断，没有权限时隐藏或禁用。
3. 客户名称、项目主办、项目协办、业务部门、风控经理等公共下拉优先使用 `src/components/Select`。
4. 描述组件使用 `src/components/Table/EditTable`。
5. 文件分组列表使用 `src/components/Table/FileTable`；普通文件列表使用 `src/components/Table/NoEnumFileTable`。
6. 表单编辑能力优先使用 `src/components/Format/editable.js`。
7. 详情页锚点布局优先使用 `src/components/DetailLayout`。

## 外部信息

- 代码地址：http://gitlab.zswl.cn:8888/frontend/mithras-react/
- 公司前端规范：http://wiki.zswltech.cn:8888/pages/viewpage.action?pageId=2818303
- Jenkins 发布地址：http://172.16.200.57:8099/view/%E7%A7%9F%E8%B5%81/job/mithras-frontend/
