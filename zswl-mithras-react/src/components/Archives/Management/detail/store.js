import { makeAutoObservable } from '@zswl/admin'
import { PageStore } from '@zswl/components'
import documentManagementLedgerApi from '@/api/archives/documentManagementLedger'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  enumType = {}
  page = new PageStore({
    request: (params) => {
      if (!params?.id) return {}
      return documentManagementLedgerApi.detail.getOperationsDirDict(params.id)
    },
  })
  getEnumType = async (id) => {
    const result = await documentManagementLedgerApi.detail.getOperationsDirDict(id)
    this.enumType = result
  }
}
export default Store
