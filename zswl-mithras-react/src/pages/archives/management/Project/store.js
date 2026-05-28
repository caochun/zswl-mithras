import { downLoadExcel } from '@/components/Excel'
import { makeAutoObservable } from '@zswl/admin'
import { TableStore } from '@zswl/components'
import { message } from 'antd'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  // 权限状态：null-初始状态，true-有权限，false-无权限
  hasPermission = null
  // 无权限提示信息
  noPermissionMessage = ''

  table = new TableStore({
    pagination: { pageSize: 20 },
    request: async (searchData) => {
      try {
        const result = await Api.getList(searchData)
        this.hasPermission = true
        return result
      } catch (error) {
        this.hasPermission = false
        this.noPermissionMessage = error?.message || '暂无数据权限'
        throw error
      }
    },
  })

  // 导出列配置
  exportColumns = []

  setExportColumns = (columns) => {
    this.exportColumns = columns
  }

  /**
   * 批量导出excel
   */
  exportData = async () => {
    const { keys } = this.table.getSelected()

    try {
      let dataSource
      if (keys && keys.length > 0) {
        const tableList = this.table.getList()
        const selectedKeys = new Set(keys)
        dataSource = tableList.filter((record, index) => {
          const rowKey = `${record.processInstanceId}_${record.projCode}_${index}`
          return selectedKeys.has(rowKey)
        })
      } else {
        const params = { ...this.table.getParams(), pageSize: 10000, pageNum: 1 }
        const result = await Api.getList(params)
        dataSource = result?.rows || result?.list || []
      }
      dataSource = dataSource.map((item, index) => ({ ...item, index: index + 1 }))
      await downLoadExcel({
        fileName: '项目端归档资料',
        dataSource,
        columns: this.exportColumns,
      })
    } catch (error) {
      message.error('导出失败！')
    }
  }
}

export default new Store()
