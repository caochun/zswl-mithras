import { DrawerStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/common/customerOverview'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  curCardData = {}
  setCurCardData = (data) => {
    this.curCardData = data
  }
  customerListDrawer = new DrawerStore({
    onOpen: (data) => {
      this.customerListTable.search({ title: data.name })
    },
  })
  customerListTable = new TableStore({
    request: async (params) => {
      return Api.postClientList({ ...params })
    },
  })
}
export default Store
