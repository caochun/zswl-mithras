import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import approvalBreakthroughApi from '@/api/blackGray/approvalBreakthroughApi'
import approvalControlApi from '@/api/blackGray/approvalControlApi'

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
