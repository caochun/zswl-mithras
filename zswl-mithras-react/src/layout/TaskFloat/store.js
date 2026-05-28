import { makeAutoObservable } from '@zswl/admin'

class TaskFloatStore {
  constructor() {
    makeAutoObservable(this)
  }

  // 长时任务管理
  taskList = []

  /**
   * 获取正在运行的任务数量
   * @returns {number} 运行中的任务数量
   */
  get runningTasksCount() {
    return this.taskList.filter((task) => task.status === 'running').length
  }

  /**
   * 添加新任务
   * @param {Object} task - 任务对象
   * @param {string} task.id - 任务ID
   * @param {string} task.name - 任务名称
   * @param {string} task.description - 任务描述
   * @param {string} task.status - 任务状态 (pending|running|completed|failed)
   * @param {number} task.progress - 任务进度 (0-100)
   */
  addTask = (task) => {
    const newTask = {
      id: task.id || Date.now().toString(),
      name: task.name,
      description: task.description || '',
      status: task.status || 'pending',
      progress: task.progress || 0,
      createTime: new Date().toLocaleString(),
      ...task,
    }
    this.taskList.push(newTask)
  }

  /**
   * 更新任务状态
   * @param {string} taskId - 任务ID
   * @param {Object} updates - 更新的字段
   */
  updateTask = (taskId, updates) => {
    const taskIndex = this.taskList.findIndex((task) => task.id === taskId)
    if (taskIndex !== -1) {
      this.taskList[taskIndex] = { ...this.taskList[taskIndex], ...updates }
    }
  }

  /**
   * 删除任务
   * @param {string} taskId - 任务ID
   */
  removeTask = (taskId) => {
    this.taskList = this.taskList.filter((task) => task.id !== taskId)
  }

  /**
   * 清空已完成的任务
   */
  clearCompletedTasks = () => {
    this.taskList = this.taskList.filter((task) => task.status !== 'completed')
  }

  /**
   * 清空所有任务
   */
  clearAllTasks = () => {
    this.taskList = []
  }

  /**
   * 根据ID获取任务
   * @param {string} taskId - 任务ID
   * @returns {Object|null} 任务对象
   */
  getTaskById = (taskId) => {
    return this.taskList.find((task) => task.id === taskId) || null
  }

  /**
   *
   * @param {Object} taskConfig - 任务配置
   * @returns {Promise} 任务执行Promise
   */
  executeTask = async (taskConfig) => {
    const taskId = taskConfig.id || Date.now().toString()

    // 添加任务到列表
    this.addTask({
      ...taskConfig,
      id: taskId,
      status: 'running',
      progress: 0,
    })

    try {
      // 模拟任务执行过程
      await taskConfig.currentTask()

      // 任务完成
      this.updateTask(taskId, { status: 'completed', progress: 100 })

      // 可选：自动清理已完成任务
      if (taskConfig.autoRemove) {
        setTimeout(() => {
          this.removeTask(taskId)
        }, taskConfig.autoRemoveDelay || 3000)
      }

      return { success: true, taskId }
    } catch (error) {
      // 任务失败
      this.updateTask(taskId, { status: 'failed' })
      return { success: false, error, taskId }
    }
  }
}

export default new TaskFloatStore()
