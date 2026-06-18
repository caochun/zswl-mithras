import { DrawerStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { mergeArray } from '@/utils/domains/dashboard/DashboardUtils'
import { initFieldsConfig } from './Config'
import Api from './api'
import { isRiskManager } from '@/utils'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  curCardData = {}
  queryParams = {
    permissionType: isRiskManager() ? 'own' : 'all', // own: '我负责的', all: '全部'
  }

  setCurCardData = (data) => {
    this.curCardData = data
  }
  customerListDrawer = new DrawerStore({})

  getFieldsApi = async () => {
    const res = await Api.postProjectInfoStatisticsRentInfoList()
    return mergeArray(res, initFieldsConfig)
  }
}
export default Store
