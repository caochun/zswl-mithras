# 模块宏观业务语义

`zswl-mithras-react` 是 Mithras 系统的前端工程，负责承载客户、项目、合同、付款、收款、风控、报表、流程、文件等业务页面和交互体验。

本模块的核心语义是“用户界面与前端交互编排”，不承载后端业务规则。业务规则、审批状态、权限判断和数据一致性应以后端接口为准，前端负责展示、输入校验、页面流转和用户操作反馈。

前端页面应跟随后端业务域划分保持清晰目录结构；跨域页面可以按业务场景组织，但不应把不同业务域的规则硬编码成难以维护的隐式逻辑。

## 基本信息 222

[代码地址](http://gitlab.zswl.cn:8888/frontend/mithras-react/)
[公司前端规范](http://wiki.zswltech.cn:8888/pages/viewpage.action?pageId=2818303)
[jenkins 发布地址](http://172.16.200.57:8099/view/%E7%A7%9F%E8%B5%81/job/mithras-frontend/)

## 项目约定写法

1. 金额展示
   > `千分位`且保留 `2 位`小数，如在表格列，金额`右对齐`。
2. 按钮权限控制
   > 根据后端返回的接口标识判断，如没有，则`隐藏`按钮。
3. 公用下拉组件
   > 客户名称、项目主办、项目协办、业务部门、风控经理等下拉，路径在`src/components/Select`组件中，注意传权限标识`functionCode`。
4. 搜索框、表格
   > 使用参考`src/pages/financial/fund/index.js`文件。
5. 描述组件(基本信息模块)
   > 组件在 `src/components/Table/EditTable`, 使用参考`src/pages/financial/fund/detail/BaseInfo.js`文件。
6. 文件列表
   > ` 文件分组列表`，组件在 `src/components/Table/FileTable`。
   > ` 文件列表`，组件在 `src/components/Table/NoEnumFileTable`。
7. 表单编辑
   > 组件在 `src/components/Format/editable.js`，使用参考`src/pages/financial/fund/Column.js`文件。
8. 锚点组件
   > 组件在 `src/components/DetailLayout`，使用参考`src/pages/financial/fund/detail/[id$].js`文件。

# 项目打包部署

> 根目录执行`npm run buildAll` 包含预发、生产环境的包。
> ～
