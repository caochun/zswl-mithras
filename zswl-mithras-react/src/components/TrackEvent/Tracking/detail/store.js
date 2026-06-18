import { PageStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import trackingApi from '@/api/trackEvent/trackingApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async (params) => {
      return await trackingApi.getTrackEventDetail({ id: params?.id })
    },
  })
}
export default Store
