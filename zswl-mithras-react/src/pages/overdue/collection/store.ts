import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import collectionManagementApi from '@/api/overdue/collectionManagementApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: (params) => {
      return collectionManagementApi.getDetail({ id: params?.id })
    },
  })
  table = new TableStore({
    request: async (params) => {
      return await collectionManagementApi.postOverduecollectionList({ ...params })
    },
  })
}
export default new Store()
