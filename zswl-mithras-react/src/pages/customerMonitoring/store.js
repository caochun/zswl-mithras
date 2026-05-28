import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from './api'
import { uniqueId } from 'lodash'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  statistics = {}
  postQuantityChange = {}
  pie = []
  riskControlOpinionHandleStatus = {}
  page = new PageStore({
    request: async (params) => {
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
      return Api.postWarnlist({ id: params?.id, ...params })
    },
  })
  clientMonitorWarnlist = new TableStore({
    request: async (params) => {
      console.log(params, 'params')
      const { list, ...rest } = await Api.postClientMonitorWarnlist({ id: params?.id, ...params })
      return { list: list.map((item) => ({ ...item, id: uniqueId() })), ...rest }
    },
  })
  opinionlist = new TableStore({
    request: async (params) => {
      console.log(params, 'params')
      const { list, ...rest } = await Api.postOpinionlist({ id: params?.id, ...params })
      return { list: list.map((item) => ({ ...item, id: uniqueId() })), ...rest }
    },
  })
  monitorList = new TableStore({
    request: (params) => {
      return Api.postMonitorList({ id: params?.id, ...params })
    },
  })
}
export default new Store()
