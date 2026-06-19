import { TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import approvalBreakthroughApi from '@/api/blackGray/approvalBreakthroughApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  table = new TableStore({
    request: (params) => {
      return approvalBreakthroughApi.postBusinessList({ ...params })
    },
  })
}
export default new Store()
