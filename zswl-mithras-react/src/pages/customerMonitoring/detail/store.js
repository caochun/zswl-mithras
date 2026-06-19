import { PageStore, TableStore } from '@zswl/components'
import { getQuery, makeAutoObservable } from '@zswl/admin'
import Api from '@/api/customerMonitoring/customerMonitoringApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  yqDetail = []
  yjDetail = []
  riskControlOpinionHandleStatus = []
  detail = {}
  enterpriseName = ''
  uscc = ''
  page = new PageStore({
    request: async (params) => {
      const { enterpriseName, uscc } = getQuery()
      this.enterpriseName = enterpriseName
      this.uscc = uscc
      const data = await Api.postClientUnifiedViewDetail({ clientId: params.id })
      this.detail = data

      const { riskControlOpinionHandleStatus } = await Api.selectAll(params)

      console.log(riskControlOpinionHandleStatus, 'riskControlOpinionHandleStatus')

      this.riskControlOpinionHandleStatus = riskControlOpinionHandleStatus
      const res = await Api.postclientMonitorOpinionDetail({ clientId: params.id })
      this.yqDetail = res
      this.yjDetail = await Api.postYjDetail({ clientId: params.id })
      console.log(this.yjDetail, 'this.yjDetail')

      return data
    },
  })
}
export default new Store()
