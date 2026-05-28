import paymentApprovalApi from '@/api/financial/paymentApprovalApi'
import { makeAutoObservable } from '@zswl/admin'
import { PageStore } from '@zswl/components'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({
    request: ({ id }) => {
      // return paymentApprovalApi.postComparePreVersion({ id })
    },
  })
}
export default new Store()
