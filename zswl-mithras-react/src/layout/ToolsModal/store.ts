import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/common/fileList'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: (params) => {
      return Api.getDetail({ id: params?.id })
    },
  })
  table = new TableStore({
    request: (params) => {
      return Api.getDetail({ id: params?.id })
      return []
    },
  })
}
export default new Store()
