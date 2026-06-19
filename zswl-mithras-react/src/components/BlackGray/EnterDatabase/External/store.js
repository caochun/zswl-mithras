import { TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/blackGray/recordTableApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  table = new TableStore({
    request: (params) => {
      return Api.postRecordList({ source: 'EXTERNAL_APPROVAL', ...params })
    },
  })
}
export default new Store()
