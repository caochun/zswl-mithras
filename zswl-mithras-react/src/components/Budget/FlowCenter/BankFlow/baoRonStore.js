import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import guaranteedFinancialFlow from '@/api/budget/flowCenter/guaranteedFinancialFlow'
import { message } from 'antd'

class Store {
  constructor({ getCount }) {
    makeAutoObservable(this)
    this.getCount = getCount
  }

  table = new TableStore({
    request: async (params) => {
      const res = await guaranteedFinancialFlow.postRecordList(params)
      this.getCount()
      return res
    },
  })
  ignore = async (id) => {
    await guaranteedFinancialFlow.postRecordIgnore({ id })
    message.success('操作成功')
    this.table.search()
  }
}
export default Store
