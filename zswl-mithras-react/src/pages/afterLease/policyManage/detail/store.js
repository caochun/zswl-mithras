import { PageStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '../api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({
    request: async (params) => {
      return await Api.postLengerDetail(params)
    },
  })
}
export default Store
