import { Modal, PageStore, TableStore } from '@zswl/components'
import { history, makeAutoObservable } from '@zswl/admin'
import Api from '@/api/financial/payableInterestApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: (params) => {
      return Api.postInterestBasicDetail({
        financingId: params.financingId,
        type: params.type,
      })
    },
  })
  table = new TableStore({
    request: async (params) => {
      return Api.postInterestCalDetail({
        ...params,
        financingId: this.page.getParams().financingId,
        type: this.page.getParams().type,
      })
    },
  })
}
export default Store
