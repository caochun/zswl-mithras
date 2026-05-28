import { TableStore, DrawerStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  activityKey = 'MONTH'
  setActivityKey = (key) => {
    this.activityKey = key
  }

  companyData = {}
  setCompanyData = (data) => {
    this.companyData = data
  }
  getCompanyData = async () => {
    const res = await Api.postDashboardPlanStatistics({
      queryType: this.activityKey,
    })
    res && this.setCompanyData(res)
  }

  deptTableStore = new TableStore({
    pagination: false,
    request: async (params) => {
      const list = await Api.postDashboardPlanStatisticsByDept({
        ...params,
        queryType: this.activityKey,
      })
      return list
    },
  })

  investmentDrawer = new DrawerStore({})
}
export default Store
