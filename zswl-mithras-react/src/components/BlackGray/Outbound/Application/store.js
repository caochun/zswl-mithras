import { TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import manualOutboundFormApi from '@/api/blackGray/manualOutboundFormApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  table = new TableStore({
    request: (params) => {
      return manualOutboundFormApi.postOutboundList({ ...params })
    },
  })
}
export default new Store()
