import { makeAutoObservable } from '@zswl/admin'
import { DrawerStore, PageStore } from '@zswl/components'
import customCycleApi from '@/api/lifeCycle/customCycleApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async (params) => {
      const res = await customCycleApi.postLifecycleClientdetail(params)
      return res
    },
  })

  $projectDetailDrawer = new DrawerStore({})
}
export default new Store()
