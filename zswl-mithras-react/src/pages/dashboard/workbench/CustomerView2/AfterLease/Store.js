import { DrawerStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { mergeArray } from '@/utils/dashboard'
import { initFieldsConfig } from './Config'
import Api from '@/api/dashboard/customerAfterLease'

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
    const res = await Api.postDashboardClientAfterleaseStatistics()
    return mergeArray(res, initFieldsConfig)
  }
}
export default Store
