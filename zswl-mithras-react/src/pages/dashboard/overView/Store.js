import { makeAutoObservable } from '@zswl/admin'
import { PageStore } from '@zswl/components'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  pageData = []
  page = new PageStore({
    request: async () => {
      const result = await Api.postDashboardList()
      this.pageData = result
      return result
    },
  })
}
export default Store
