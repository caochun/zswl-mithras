import { TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/cpm/marginManagementApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  table = new TableStore({
    request: ({ Date, amount, ...rest }) => {
      const [start, end] = Date || []
      const searchData = {
        collectionDateFrom: start?.format('yyyy-MM-DD'),
        collectionDateTo: end?.format('yyyy-MM-DD'),
        amountFrom: (amount?.length > 0 && amount[0]) * 10000 || null,
        amountTo: (amount?.length > 0 && amount[1]) * 10000 || null,
      }
      return Api.getList({ ...searchData, ...rest })
    },
  })

  exportList = async () => {
    const params = this.table.getParams()
    const { Date, amount, ...rest } = params
    const [start, end] = Date || []
    const searchData = {
      amount: undefined,
      Date: undefined,
      collectionDateFrom: start?.format('yyyy-MM-DD'),
      collectionDateTo: end?.format('yyyy-MM-DD'),
      amountFrom: (amount?.length > 0 && amount[0]) * 10000 || null,
      amountTo: (amount?.length > 0 && amount[1]) * 10000 || null,
    }
    await Api.exportList({ ...rest, ...searchData, pageSize: 5000 })
  }
}
export default new Store()
