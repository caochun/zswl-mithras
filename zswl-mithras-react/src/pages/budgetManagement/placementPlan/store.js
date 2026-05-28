import deliveryPlanListApi from '@/api/budgetManagement/deliveryPlanListApi'
import weeklyReportApi from '@/api/budgetManagement/weeklyReportApi'
import { TableStore } from '@zswl/components'
import { makeAutoObservable } from 'mobx'

class Store {
  // 投放计划列表相关状态

  constructor() {
    makeAutoObservable(this)
  }
  planList = new TableStore({
    request: async (params) => {
      const res = await deliveryPlanListApi.postPayPageList(params)
      return res
    },
  })
  reportList = new TableStore({
    request: async (params) => {
      const res = await weeklyReportApi.postReportList(params)
      return res
    },
  })
}

export default Store
