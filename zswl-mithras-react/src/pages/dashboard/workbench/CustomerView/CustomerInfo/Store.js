import { DrawerStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { mergeArray } from '@/utils/dashboard'
import { initFieldsConfig } from './Config'
import Api from '@/api/dashboard/customerOverview'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  curCardData = {}
  setCurCardData = (data) => {
    this.curCardData = data
  }
  customerListDrawer = new DrawerStore({})

  getFieldsApi = async () => {
    const res = await Api.postDashboardClientOverviewStatistics()
    return mergeArray(res, initFieldsConfig)
  }
}
export default Store
