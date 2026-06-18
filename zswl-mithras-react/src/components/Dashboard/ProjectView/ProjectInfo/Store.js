import { DrawerStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { initFieldsConfig } from './Config'
import { mergeArray } from '@/dashboard/DashboardUtils'
import Api from '@/api/dashboard/projectView'
import { isFundDept, isRiskManager } from '@/utils'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  // 项目信息
  curInfoData = {}
  setInfoData = (data) => {
    this.curInfoData = data
  }
  infoDrawer = new DrawerStore({})

  queryParams = {
    permissionType: isRiskManager() ? 'own' : 'all', // own: '我负责的', all: '全部'
  }
  setQueryParams = (value) => {
    this.queryParams = {
      ...this.queryParams,
      permissionType: value,
    }
  }

  getFieldsApi = async (params) => {
    const res = await Api.postProjectInfoStatistics({ ...params })
    const result = mergeArray(res, initFieldsConfig)
    if (isFundDept()) {
      return [result?.[0]]
    }
    return result
  }
}
export default Store
