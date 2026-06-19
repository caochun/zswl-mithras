import { DrawerStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { mergeArray } from '@/utils/domains/dashboard/DashboardUtils'
import { initFieldsConfig } from './Config'
import Api from '@/api/dashboard/afterLeaseCheck'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  curCardData = {}
  setCurCardData = (data) => {
    this.curCardData = data
  }
  listDrawer = new DrawerStore({})

  getFieldsApi = async () => {
    const res = await Api.postDashboardAfterLeaseCheckStatistics()
    return mergeArray(res, initFieldsConfig)
  }
}
export default Store
