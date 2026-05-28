import { FormStore, PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import recordTableApi from '@/api/blackList/recordTableApi'

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

  form = new FormStore({})
}
export default new Store()
