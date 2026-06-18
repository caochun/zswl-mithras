import { TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/kpi/projProfit/profitCalculateTool'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  tableData = {}
  $table = new TableStore({
    request: async (params) => {
      const data = await Api.postProfitcalculatePagelist(params)
      this.tableData = data
      return data?.pageResult || []
    },
  })
  export = async () => {
    const params = this.$table.getParams()
    await Api.postProfitcalculateExport(params)
  }
}
export default new Store()
