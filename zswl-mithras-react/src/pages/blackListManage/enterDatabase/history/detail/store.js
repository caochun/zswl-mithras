import { FormStore, PageStore, TableStore } from '@zswl/components'
import { history, makeAutoObservable } from '@zswl/admin'
import recordTableApi from '@/api/blackGray/recordTableApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: (params) => {
      if (!params?.id) return {}
      return recordTableApi.postRecordDetail({ blackGrayRecordId: params?.id })
    },
  })
}
export default new Store()
