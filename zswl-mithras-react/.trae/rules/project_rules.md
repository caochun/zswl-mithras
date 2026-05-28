# AI 开发规则 - Mithras React 项目

基于对整个项目的深度分析，生成以下 AI 开发规则，用于指导后续的开发工作。使用中文回复

## 1. 项目架构与技术栈规则

### 1.1 技术栈约束

- **React 版本**: 使用 React 17.0.2，避免使用 React 18+ 的新特性
- **状态管理**: 强制使用 MobX 进行状态管理，减少 useState 和 useMemo 的使用
- **UI 组件库**: 基于 Ant Design 4.24.9 的自研组件库 @zswl/components
- **构建工具**: 使用 @zswl/admin 1.3.4 作为构建和开发工具
- **样式处理**: 使用 Less 进行样式编写，支持主题色配置

### 1.2 项目结构规范

```
src/
├── api/           # API接口定义，按业务模块分类
├── components/    # 通用组件库
├── pages/         # 页面组件，按业务模块分类
├── utils/         # 工具函数和通用逻辑
└── layout/        # 布局组件
```

## 2. 组件开发规则

### 2.1 通用组件使用规范

- **表格组件**: 优先使用 `src/components/Format/defaultColumn.js` 中的预定义列配置
  - 金额列: 使用 `AmountColumn`
  - 选择列: 使用 `MatchOptionColumn`
  - 日期列: 使用 `DateColumn`
  - 上传列: 使用 `UploadColumn`
  - 自定义列: 使用 `CustomColumn`

### 2.2 组件设计原则

- **单一职责**: 每个组件只负责一个功能
- **可复用性**: 通用组件放在 `src/components/` 下，页面特定组件放在对应页面的 `components/` 目录
- **Props 设计**: 支持灵活的配置，提供合理的默认值

### 2.3 组件导出规范

```javascript
// src/components/index.js 中统一导出通用组件
export { default as ComponentName } from './ComponentName'
```

## 3. 状态管理规则

### 3.1 Store 类设计规范

- **TableStore**: 用于表格数据管理

  ```javascript
  table = new TableStore({
    request: (params) => Api.getList(params),
  })
  ```

- **ModalStore**: 用于弹窗状态管理

  ```javascript
  modal = new ModalStore({
    onFinish: async (values) => {
      // 处理提交逻辑
    },
  })
  ```

- **FormStore**: 用于表单状态管理

  ```javascript
  form = new FormStore({})
  ```

- **PageStore**: 用于页面级状态管理
  ```javascript
  page = new PageStore({
    request: (params) => Api.getDetail(params),
  })
  ```

### 3.2 Store 使用规范

- 每个页面/组件的 Store 类必须使用 `makeAutoObservable(this)` 进行响应式处理
- Store 实例化时传入必要的初始化参数（如 id, businessVersion 等）
- 复杂页面可以组合多个 Store 实例

## 4. API 接口规则

### 4.1 接口定义规范

- **接口文档**: 使用 yapi-to-typescript 自动生成 TypeScript 接口定义
- **文件结构**:
  ```
  src/api/模块名/
  ├── interface/     # TypeScript 接口定义
  ├── api.js        # 接口实现
  └── index.js      # 导出
  ```

### 4.2 接口实现规范

```javascript
// 标准接口实现格式
export default {
  // 获取列表
  getList: (data) => http.post('/api/list', data, { mock }),

  // 获取详情
  getDetail: (data) => http.post('/api/detail', data, { mock }),

  // 新增
  postAdd: (data) => http.post('/api/add', data, { mock }),

  // 修改
  postModify: (data) => http.post('/api/modify', data, { mock }),

  // 删除
  postDelete: (data) => http.post('/api/delete', data, { mock }),
}
```

### 4.3 接口命名规范

- GET 请求: `get + 功能名`
- POST 请求: `post + 功能名`
- 列表接口: `getList` 或 `postPageList`
- 详情接口: `getDetail`
- 新增接口: `postAdd`
- 修改接口: `postModify`
- 删除接口: `postDelete`

## 5. 工具函数规则

### 5.1 工具函数分类

- **base.js**: 基础工具函数（登录、格式化、验证等）
- **amount.js**: 金额相关处理
- **date.js**: 日期时间处理
- **file.js**: 文件处理
- **table.js**: 表格相关工具
- **transform.js**: 数据转换
- **hooks/**: 自定义 React Hooks

### 5.2 Hooks 使用规范

- **useGetColumns**: 获取列配置选项
- **useGetStatus**: 获取状态选项
- **useSearch**: 搜索功能封装
- **useInterval**: 定时器封装
- **useMergedState**: 状态合并

### 5.3 工具函数导出规范

```javascript
// src/utils/index.js 中统一导出
export * from './base'
export * from './amount'
// ... 其他模块
```

## 6. 代码质量规则

### 6.1 代码注释规范

- **函数级注释**: 所有函数必须添加功能说明注释
- **复杂逻辑注释**: 复杂的业务逻辑必须添加详细注释
- **接口注释**: API 接口调用处添加用途说明

### 6.2 命名规范

- **组件名**: 使用 PascalCase（如 `UserList`）
- **文件名**: 使用 camelCase（如 `userList.js`）
- **变量名**: 使用 camelCase（如 `userName`）
- **常量名**: 使用 UPPER_SNAKE_CASE（如 `API_BASE_URL`）

### 6.3 代码组织规范

- **单一职责**: 一个函数只做一件事
- **函数长度**: 单个函数不超过 50 行
- **文件长度**: 单个文件不超过 500 行
- **依赖管理**: 合理组织 import 语句，按类型分组

## 7. 业务开发规则

### 7.1 表格开发规范

- 使用 `TableStore` 管理表格状态
- 使用 `defaultColumn.js` 中的预定义列配置
- 表单的搜索用 columns 中的 search 字段维护
- 表格操作按钮统一使用 Actions 组件

### 7.2 表单开发规范

- 使用 `FormStore` 管理表单状态
- 表单验证使用 `src/utils/rules.js` 中的规则
- 支持表单联动和动态验证
- 表单提交统一错误处理

### 7.3 弹窗开发规范

- 使用 `ModalStore` 管理弹窗状态
- 弹窗内容支持表单、表格等复杂组件
- 统一的确认/取消操作处理
- 支持弹窗嵌套和数据传递

## 8. 性能优化规则

### 8.1 渲染优化

- 使用 MobX 的 `observer` 包装组件
- 避免不必要的组件重新渲染
- 合理使用 `React.memo` 和 `useMemo`

### 8.2 数据处理优化

- 大数据量表格使用虚拟滚动
- 图表数据使用 ECharts 进行渲染
- 文件上传支持分片和断点续传

### 8.3 网络请求优化

- 使用统一的 HTTP 拦截器
- 支持请求缓存和重试机制
- 合理使用 Mock 数据进行开发
