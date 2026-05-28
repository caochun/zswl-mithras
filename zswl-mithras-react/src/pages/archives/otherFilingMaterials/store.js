import { makeAutoObservable } from '@zswl/admin'
import { FormStore, ModalStore, TableStore } from '@zswl/components'
import { message } from 'antd'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  data = []
  expandedRowKeys = []

  setExpandedRowKeys = (keys) => {
    this.expandedRowKeys = keys
  }

  table = new TableStore({
    pagination: { pageSize: 20 },
    request: async (searchData) => {
      return await Api.getList(searchData)
    },
  })

  /**
   * 申请下载Modal
   */
  createModal = new ModalStore()

  form = new FormStore()




  /**
   * 其他资料归档Modal
   */
  fileModal = new ModalStore()
  fileForm = new FormStore()

  /**
   * 批量导出
   */
  exportData = async () => {
    const { rows } = this.table.getSelected()

    try {
      let params = {}
      if (rows && rows.length > 0) {
        // 如果有选中数据，导出选中的
        params.ids = rows.map((item) => item.id)
        message.loading('正在导出选中数据...', 0)
      } else {
        // 如果没有选中，导出全部
        params = { ids: [] }
        message.loading('正在导出全部数据...', 0)
      }
      await Api.export(params)
      setTimeout(() => {
        message.destroy()
      }, 500)
     // message.destroy()
    } catch (error) {
      message.destroy()
      message.error('导出失败！')
    }
  }
}

export default new Store()
