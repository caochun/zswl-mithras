import { makeAutoObservable, http, getQuery } from '@zswl/admin'
import { Modal, Table } from '@zswl/components'
import { getDatabaseList } from './api' // 引入封装后的请求方法

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  // 立案信息
  caseInformations = new Table.Store({
    request: async (params) => {
      return await this.fetchTableData('caseInformations', params)
    },
  })

  // 被执行信息
  executedInformations = new Table.Store({
    request: async (params) => {
      return await this.fetchTableData('executedInformations', params)
    },
  })

  // 司法协助
  judicialAssistances = new Table.Store({
    request: async (params) => {
      return await this.fetchTableData('judicialAssistances', params)
    },
  })

  // 裁判文书
  judgmentDocuments = new Table.Store({
    request: async (params) => {
      return await this.fetchTableData('judgmentDocuments', params)
    },
  })

  // 限制高消费
  restrictionHighConsumptions = new Table.Store({
    request: async (params) => {
      return await this.fetchTableData('restrictionHighConsumptions', params)
    },
  })

  CardContainer = {}
  uscc = ''
  enterpriseName = ''
  /**
   * @param {string} dataKey 后端返回数据的字段名
   * @param {object} params 请求参数
   */
  async fetchTableData(dataKey, params) {
    const { uscc, enterpriseName } = getQuery()
    this.uscc = uscc
    this.enterpriseName = enterpriseName
    const response = await getDatabaseList({ ...params, uscc: uscc })
    if (!response) {
      return []
    }
    this.CardContainer = this.CardContainer || {}
    Object.entries(response).forEach(([key, value]) => {
      if (Array.isArray(value)) {
        this.CardContainer[key] = value.length
      }
    })
    return response[dataKey].length ? response[dataKey] : []
    // return response[dataKey] || []
  }

  JudicialAidModuleStore = new Modal.Store({
    onOpen: async (id) => {
      return await http.post('/customer/view/detail/judicialAssistanceDetail', { id })
    },
    onFinish: async () => {
      this.JudicialAidModuleStore.close()
    },
  })

  InstrumentModuleStore = new Modal.Store({
    onOpen: async (id) => {
      return await http.post('/customer/view/detail/judgementDocumentDetail', { id })
    },
    onFinish: async () => {
      this.InstrumentModuleStore.close()
    },
  })

  ImitHighModuleStore = new Modal.Store({
    onOpen: async (id) => {
      return await http.post('/customer/view/detail/limitHighConsumeDetail', { id })
    },
    onFinish: async () => {
      this.ImitHighModuleStore.close()
    },
  })
}

export default new Store()
