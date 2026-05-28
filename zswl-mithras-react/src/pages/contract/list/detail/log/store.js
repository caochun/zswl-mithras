import { TableStore, PageStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({})

  id
  $table = new TableStore({
    request: async () => {
      return await Api.getVersionList({ mainId: this.id })
    },
  })
}
export default new Store()
