/**
 * TaskFloat 使用示例
 * 展示如何在其他组件中使用长时任务管理功能
 */

import store from '../store'

// 示例1: 添加一个简单任务
export const addSimpleTask = () => {
  store.addTask({
    name: '数据导出',
    description: '正在导出用户数据到Excel文件',
    status: 'running',
    progress: 0
  })
}

// 示例2: 执行一个模拟的长时任务
export const executeExampleTask = async () => {
  const result = await store.executeTask({
    name: '文件上传',
    description: '正在上传大文件到服务器',
    stepDelay: 300, // 每步延迟300ms
    autoRemove: true, // 完成后自动移除
    autoRemoveDelay: 5000 // 5秒后移除
  })
  
  if (result.success) {
    console.log('任务执行成功:', result.taskId)
  } else {
    console.error('任务执行失败:', result.error)
  }
  
  return result
}

// 示例3: 批量处理任务
export const executeBatchTasks = async () => {
  const tasks = [
    { name: '处理订单1', description: '正在处理订单数据' },
    { name: '处理订单2', description: '正在处理订单数据' },
    { name: '处理订单3', description: '正在处理订单数据' }
  ]
  
  const promises = tasks.map(task => store.executeTask({
    ...task,
    stepDelay: 200,
    autoRemove: true,
    autoRemoveDelay: 3000
  }))
  
  const results = await Promise.all(promises)
  return results
}

// 示例4: 手动管理任务状态
export const manualTaskManagement = () => {
  // 添加任务
  const taskId = Date.now().toString()
  store.addTask({
    id: taskId,
    name: '数据同步',
    description: '正在同步远程数据',
    status: 'pending'
  })
  
  // 开始任务
  setTimeout(() => {
    store.updateTask(taskId, { status: 'running', progress: 0 })
  }, 1000)
  
  // 更新进度
  let progress = 0
  const interval = setInterval(() => {
    progress += 20
    store.updateTask(taskId, { progress })
    
    if (progress >= 100) {
      clearInterval(interval)
      store.updateTask(taskId, { status: 'completed' })
      
      // 3秒后移除任务
      setTimeout(() => {
        store.removeTask(taskId)
      }, 3000)
    }
  }, 500)
  
  return taskId
}

// 示例5: 错误处理
export const taskWithError = () => {
  const taskId = Date.now().toString()
  store.addTask({
    id: taskId,
    name: '网络请求',
    description: '正在请求远程API',
    status: 'running',
    progress: 0
  })
  
  // 模拟网络错误
  setTimeout(() => {
    store.updateTask(taskId, { 
      status: 'failed',
      description: '网络连接失败，请检查网络设置'
    })
  }, 2000)
  
  return taskId
}

// 示例6: 清理任务
export const cleanupTasks = () => {
  // 清理已完成的任务
  store.clearCompletedTasks()
  
  // 或者清理所有任务
  // store.clearAllTasks()
}

// 示例7: 获取任务信息
export const getTaskInfo = (taskId) => {
  const task = store.getTaskById(taskId)
  if (task) {
    console.log('任务信息:', task)
    console.log('任务状态:', task.status)
    console.log('任务进度:', task.progress)
  }
  return task
}

// 示例8: 监听任务状态变化（在React组件中使用）
export const TaskStatusMonitor = () => {
  const { taskList, runningTasksCount } = store
  
  console.log('当前任务列表:', taskList)
  console.log('运行中的任务数量:', runningTasksCount)
  
  return {
    taskList,
    runningTasksCount,
    hasRunningTasks: runningTasksCount > 0
  }
}