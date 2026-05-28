import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: (params) => {},
  })
  table = new TableStore({
    request: (params) => {
      return Api.postLogList(params)
    },
  })
}
export default new Store()
