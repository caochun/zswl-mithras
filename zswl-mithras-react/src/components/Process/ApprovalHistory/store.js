import { makeAutoObservable } from '@zswl/admin'
import { TableStore } from '@zswl/components'
import Api from '@/api/process/processHistoryApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  table = new TableStore({
    pagination: false,
    request: async ({ processInstanceId }) => {
      const res = await Api.getList({ processInstanceId, pageSize: 500 })
      return res.list
    },
  })
}
export default new Store()
