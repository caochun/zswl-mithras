import { downLoadExcel } from '@/components/Excel'
import { downFile } from '@/utils'
import { makeAutoObservable } from '@zswl/admin'
import { FormStore, ModalStore, TableStore } from '@zswl/components'
import { Modal, message } from 'antd'
import documentManagementLedgerApi from '@/api/archives/documentManagementLedger'
class Store {
  constructor() {
    makeAutoObservable(this)
  }

  data = []
  expandedRowKeys = []

  // 权限状态：null-初始状态，true-有权限，false-无权限
  hasPermission = null
  // 无权限提示信息
  noPermissionMessage = ''

  setExpandedRowKeys = (keys) => {
    this.expandedRowKeys = keys
  }

  table = new TableStore({
    pagination: { pageSize: 20 },
    request: async (searchData) => {
      try {
        const result = await documentManagementLedgerApi.fund.getList(searchData)
        this.hasPermission = true
        return result
      } catch (error) {
        this.hasPermission = false
        this.noPermissionMessage = error?.message || '暂无数据权限'
        throw error
      }
    },
  })

  /**
   * 申请下载Modal
   */
  createModal = new ModalStore()

  form = new FormStore()

  /**
   * 下载列表Modal
   */
  downloadListModal = new ModalStore()
  downloadListTable = new TableStore({
    pagination: { pageSize: 10 },
    request: async (searchData) => {
      return await documentManagementLedgerApi.fund.downloadRecordsQuery(searchData)
    },
  })

  // 导出列配置
  exportColumns = []

  setExportColumns = (columns) => {
    this.exportColumns = columns
  }

  /**
   * 导出excel
   */
  exportData = async () => {
    const { keys } = this.table.getSelected()
    try {
      let dataSource
      if (keys && keys.length > 0) {
        const tableList = this.table.getList()
        const selectedKeys = new Set(keys)
        dataSource = tableList.filter((record) => selectedKeys.has(record.filingMaterialsId))
      } else {
        const params = { ...this.table.getParams(), pageSize: 10000, pageNum: 1 }
        const result = await documentManagementLedgerApi.fund.getList(params)
        dataSource = result?.rows || result?.list || []
      }
      dataSource = dataSource.map((item, index) => ({ ...item, index: index + 1 }))
      await downLoadExcel({
        fileName: '资金端归档资料',
        dataSource,
        columns: this.exportColumns,
      })
    } catch (error) {
      message.error('导出失败！')
    }
  }

  /**
   * 批量下载文件（异步）
   */
  batchDownloadFile = async () => {
    const { rows } = this.table.getSelected()

    try {
      let params = {}
      if (rows && rows.length > 0) {
        // 如果有选中数据，下载选中的归档文件
        params.filingMaterialsIds = rows.map((item) => item.filingMaterialsId)
      } else {
        // 如果没有选中，下载全部归档文件
        params = { filingMaterialsIds: [], ...this.table.getParams(), pageSize: 1000 }
      }
      await documentManagementLedgerApi.fund.batchDownload(params)
      // 弹窗提示
      Modal.info({
        title: '提示',
        content: '批量下载已发起，请点击【下载列表】查看数据下载情况！',
        okText: '关闭',
      })
    } catch (error) {
      message.error('批量下载发起失败！')
    }
  }

  /**
   * 打开下载列表弹窗
   */
  openDownloadList = () => {
    this.downloadListModal.open()
    this.downloadListTable.refresh()
  }

  /**
   * 下载文件
   */
  downloadFile = async (record) => {
    try {
      message.loading('正在下载...', 0)
      downFile(record.filePath)
      message.destroy()
    } catch (error) {
      message.destroy()
      message.error('下载失败！')
    }
  }
}

export default new Store()
