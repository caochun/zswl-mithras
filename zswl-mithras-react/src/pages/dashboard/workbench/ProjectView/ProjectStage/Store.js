import { DrawerStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { mergeArray } from '@/utils/dashboard'
import { initFieldsConfig } from './Config'
import Api from '../api'
import { isRiskManager } from '@/utils'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  // 项目阶段
  curStageData = {}
  setStageData = (data) => {
    this.curStageData = data
  }
  stageDrawer = new DrawerStore({})

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
    const res = await Api.postProjectStageStatistics({
      ...params,
    })
    return mergeArray(res, initFieldsConfig)
  }
}
export default Store
