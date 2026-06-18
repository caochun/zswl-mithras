import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import approvalControlApi from '@/api/blackGray/approvalControlApi'

class Store {
  constructor({ source }) {
    makeAutoObservable(this)
    this.source = source
  }
  source
  table = new TableStore({
    request: async (params) => {
      if (this.source === 'EXTERNAL_APPROVAL') {
        return await approvalControlApi.getWarehouseAuditList({
          ...params,
        })
      }
      return approvalControlApi.getTaskAuditList({ ...params, businessSource: this.source })
    },
  })
}
export default Store
