import { ModalStore, TableStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import predictListApi from '@/api/budget/provisioning/predictListApi'
import { message } from 'antd'
import { registerProvisionForecastRefresh } from '@/utils/domains/budget/ProvisionForecastUtils'

/**
 * 设备预测计划页面状态管理类
 * 管理表格数据、弹窗状态和相关操作
 */
class Store {
  constructor() {
    makeAutoObservable(this)
    registerProvisionForecastRefresh(() => this.table.search())
  }

  // 表格状态管理
  table = new TableStore({
    request: (searchData) => {
      return predictListApi.postInfoList(searchData)
    },
  })

  /**
   * 删除设备预测计划
   */
  remove = async (record) => {
    await predictListApi.postInfoRemove({ id: record.id })
    message.success('删除成功')
    this.table.search()
  }

  // 创建弹窗状态管理
  createModal = new ModalStore({
    onFinish: async (values) => {
      const id = await predictListApi.postInfoAdd(values)
      this.createModal.close()
      // 刷新表格数据
      this.table.search()
      if (id) {
        history.push(`/budgetManagement/provisionForecast/detail/${id}`)
      }
    },
  })
}

export default new Store()
