import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import approvalBreakthroughApi from '@/api/blackList/approvalBreakthroughApi'
import approvalControlApi from '@/api/blackList/approvalControlApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  table = new TableStore({
    request: (params) => {
      return approvalControlApi.getBusinessAuditList({ ...params })
    },
  })
}
export default new Store()
