# TaskFloat 长时任务管理组件

## 概述

TaskFloat 是一个悬浮按钮组件，用于展示和管理应用中的长时任务。它提供了一个直观的界面来监控任务进度，并支持多种任务状态管理。

## 功能特性

- 🎯 **悬浮按钮**: 固定在页面右下角的悬浮按钮
- 📊 **任务进度**: 实时显示任务执行进度
- 🔔 **状态提醒**: 通过徽章显示运行中的任务数量
- 📱 **响应式设计**: 适配移动端和桌面端
- 🎨 **美观界面**: 现代化的UI设计
- ⚡ **高性能**: 基于 MobX 的响应式更新

## 使用方法

### 1. 基本使用

组件已经集成到 Layout 中，会自动显示。当有任务时，悬浮按钮会出现在页面右下角。

### 2. 添加任务

```javascript
import store from '@/layout/store'

// 添加一个简单任务
store.addTask({
  name: '数据导出',
  description: '正在导出用户数据到Excel文件',
  status: 'running',
  progress: 50
})
```

### 3. 执行长时任务

```javascript
// 执行一个模拟的长时任务
const result = await store.executeTask({
  name: '文件上传',
  description: '正在上传大文件到服务器',
  stepDelay: 300, // 每步延迟300ms
  autoRemove: true, // 完成后自动移除
  autoRemoveDelay: 5000 // 5秒后移除
})
```

### 4. 手动管理任务

```javascript
// 更新任务状态
store.updateTask(taskId, { 
  status: 'completed', 
  progress: 100 
})

// 删除任务
store.removeTask(taskId)

// 清理已完成的任务
store.clearCompletedTasks()
```

## API 参考

### Store 方法

#### `addTask(task)`
添加新任务到任务列表

**参数:**
- `task.id` (string, 可选): 任务ID，不提供时自动生成
- `task.name` (string, 必需): 任务名称
- `task.description` (string, 可选): 任务描述
- `task.status` (string, 可选): 任务状态，默认 'pending'
- `task.progress` (number, 可选): 任务进度 0-100，默认 0

#### `updateTask(taskId, updates)`
更新指定任务的状态

**参数:**
- `taskId` (string): 任务ID
- `updates` (object): 要更新的字段

#### `removeTask(taskId)`
删除指定任务

**参数:**
- `taskId` (string): 任务ID

#### `executeTask(taskConfig)`
执行一个模拟的长时任务

**参数:**
- `taskConfig.name` (string): 任务名称
- `taskConfig.description` (string, 可选): 任务描述
- `taskConfig.stepDelay` (number, 可选): 每步延迟时间，默认 500ms
- `taskConfig.autoRemove` (boolean, 可选): 完成后是否自动移除
- `taskConfig.autoRemoveDelay` (number, 可选): 自动移除延迟时间，默认 3000ms

#### `clearCompletedTasks()`
清空所有已完成的任务

#### `clearAllTasks()`
清空所有任务

#### `getTaskById(taskId)`
根据ID获取任务对象

### Store 属性

#### `taskList`
当前所有任务的数组

#### `runningTasksCount`
正在运行的任务数量（计算属性）

## 任务状态

- `pending`: 等待中
- `running`: 进行中
- `completed`: 已完成
- `failed`: 失败

## 样式定制

组件使用 Less 编写样式，可以通过修改 `index.less` 文件来定制外观：

```less
.task-float-wrapper {
  // 修改悬浮按钮位置
  bottom: 80px;
  right: 30px;
  
  .task-float-button {
    // 修改按钮样式
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
}
```

## 注意事项

1. 组件基于 MobX 实现响应式更新，确保在 observer 包装的组件中使用
2. 任务数据存储在内存中，页面刷新后会丢失
3. 建议为长时任务设置合理的 `autoRemove` 策略，避免任务列表过长
4. 在生产环境中，可以结合后端API实现真实的任务管理

## 示例代码

更多使用示例请参考 `example.js` 文件。