import { TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import approvalControlApi from '@/api/blackGray/approvalControlApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  table = new TableStore({
    request: (params) => {
      return approvalControlApi.getOutboundAuditList({ ...params })
    },
  })
}
export default new Store()
