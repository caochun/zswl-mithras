import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/blackGray/recordTableApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  table = new TableStore({
    request: async (params) => {
      this.table.setParams({ isHistory: 0, isStock: this.isStock ? 0 : 1 })
      return await Api.postRecordList({ isHistory: 0, ...params, isStock: this.isStock ? 0 : 1 })
    },
  })
  isStock = false
  checkChange = (e) => {
    this.isStock = e.target.checked
    this.table.search()
  }
}
export default new Store()
