import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/common/fileList'
import trackingApi from '@/api/lease/trackingApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async (params) => {
      return await trackingApi.getTrackEventDetail({ id: params?.id })
    },
  })
  table = new TableStore({
    request: (params) => {
      return Api.getDetail({ id: params?.id })
      return []
    },
  })
}
export default Store
