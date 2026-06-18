import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable, getQuery } from '@zswl/admin'
import Api from '@/api/risk/monitorEarly'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  statistics = {}
  postQuantityChange = []
  pie = []
  riskControlOpinionHandleStatus = []
  page = new PageStore({
    request: async (params) => {
      const { enterpriseName } = getQuery()
      this.enterpriseName = enterpriseName

      const { riskControlOpinionHandleStatus } = await Api.selectAll(params)
      this.riskControlOpinionHandleStatus = riskControlOpinionHandleStatus

      this.statistics = await Api.postMonitorStatistics(params)
      this.postQuantityChange = await Api.postQuantityChange(params)
      this.pie = await Api.postQuantityPie(params) // right pie
    },
  })
  warningList = new TableStore({
    request: (params) => {
      console.log(params, 'params')
      return Api.postWarnlist({ id: params?.id, chiName: this.enterpriseName, ...params })
    },
  })

  monitorList = new TableStore({
    request: (params) => {
      return Api.postMonitorList({ id: params?.id, chiName: this.enterpriseName, ...params })
    },
  })
}
export default new Store()
