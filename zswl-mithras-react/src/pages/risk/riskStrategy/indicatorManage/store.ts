import { PageStore, TableStore } from '@zswl/components'
import Api from '@/api/risk/riskControlStrategyApi'
import { message } from 'antd'

class Store {
  page = new PageStore({
    request: (params) => {
      return Api.postStrategyList(params)
    },
  })
  table = new TableStore({
    request: async (params) => {
      const res = await Api.postStrategyList(params)
      return res
    },
  })
  calc = async () => {
    const params = this.table.getParams()
    await Api.postSnapshotRecalculate(params)
    message.success('计算成功')
    this.table.search()
  }
}
export default new Store()
