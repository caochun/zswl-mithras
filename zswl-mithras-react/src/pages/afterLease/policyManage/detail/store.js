import { PageStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import policyLedgerApi from '@/api/afterLease/policyLedgerApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({
    request: async (params) => {
      return await policyLedgerApi.postLedgerDetail(params)
    },
  })
}
export default Store
