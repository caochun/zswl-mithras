import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/blackGray/recordTableApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  table = new TableStore({
    request: (params) => {
      return Api.postRecordList({ isStock: 1, source: 'INTERNAL_APPROVAL', ...params })
    },
  })
}
export default new Store()
