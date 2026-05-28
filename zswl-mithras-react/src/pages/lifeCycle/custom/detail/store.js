import { timeFormat } from '@/utils'
import { makeAutoObservable } from '@zswl/admin'
import { DrawerStore, ModalStore, PageStore, TableStore } from '@zswl/components'
import customCycleApi from '@/api/lifeCycle/customCycleApi'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  actualDetail = {}
  page = new PageStore({
    request: async (params) => {
      const res = await customCycleApi.postLifecycleClientdetail(params)
      return res
    },
  })

  getCommerceData = async () => {}

  $riskStrategy = new ModalStore({
    onOpen: async () => {},
  })

  clientId
  classifyData = {}
  clientInfo = {}
  addressList = []

  $projectDetailDrawer = new DrawerStore({})
}
export default new Store()
