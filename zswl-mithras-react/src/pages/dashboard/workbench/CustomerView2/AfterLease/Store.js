import { DrawerStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { mergeArray } from '@/pages/dashboard/workbench/utils'
import { initFieldsConfig } from './Config'
import Api from '@/pages/dashboard/workbench/CustomerView2/api'

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
