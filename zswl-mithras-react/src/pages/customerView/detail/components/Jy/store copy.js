import { makeAutoObservable, http, getQuery } from '@zswl/admin'
import { Modal, Table } from '@zswl/components'
import { getDatabaseList } from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  // 初始化卡片数据容器
  CardContainer = {}
  uscc = ''
  enterpriseName = ''
  async fetchTableData(dataKey, params) {
    this.queryRfsh()
    const response = await getDatabaseList({
      ...params,
      companyName: this.enterpriseName,
    })
    if (!response) return []
    const cardData = response[dataKey]
    if (!cardData) return []
    this.CardContainer[dataKey] = {
      current: cardData.cur_count || 0,
      total: cardData.total_count || 0,
    }
    const result = cardData.mortgage_list.map((item) => {
      return item.basic_info
    })

    return result || cardData || []
  }

  async fetchMajorIllegalList(params) {
    this.queryRfsh()
    const response = await getDatabaseList({
      ...params,
      companyName: this.enterpriseName,
    })
    const cardData = response['major_illegal_list'] || []
    this.CardContainer['major_illegal_list'] = {
      current: cardData.length,
      total: cardData.length,
    }
    return cardData
  }
  // 没办法只能这样写了只怪当初写的烂
  queryRfsh() {
    const { uscc, enterpriseName } = getQuery()
    this.uscc = uscc
    this.enterpriseName = enterpriseName
  }
  async fetchCaseInfoList(key, params) {
    this.queryRfsh()
    const response = await getDatabaseList({
      ...params,
      companyName: this.enterpriseName,
    })
    const cardData = response[key]
    if (cardData) {
      this.CardContainer[key] = {
        current: cardData?.cur_count || 0,
        total: cardData?.total_count || 0,
      }
    }
    return cardData[key] || []
  }

  // 1. bizException	object   企业经营异常
  bizException = new Table.Store({
    request: async (params) => {
      return await this.fetchCaseInfoList('exception_list', params)
    },
  })

  // 2. case_info_list	object 	行政处罚案件信息
  caseInfoList = new Table.Store({
    request: async (params) => {
      return await this.fetchCaseInfoList('case_info_list', params)
    },
  })
  // 单独处理 illegal_list	object
  illegalList = new Table.Store({
    request: async (params) => {
      return await this.fetchCaseInfoList('illegal_list', params)
    },
  })

  // 单独处理 他只是一个数组
  majorIllegalList = new Table.Store({
    request: async (params) => {
      const data = await this.fetchMajorIllegalList(params)
      return data
    },
  })
  //
  // 担保事件列表
  guarantEventList = new Table.Store({
    request: async (params) => {
      return await this.fetchCaseInfoList('guarant_event_list', params)
    },
  })

  mortgageList = new Table.Store({
    request: async (params) => {
      return await this.fetchCaseInfoList('mortgage_list', params)
    },
  })

  // 重大税收违法详情
  JudicialAidModuleStore = new Modal.Store({
    onOpen: async (id) => {
      return await http.post('/customer/view/detail/majortax', { id })
    },
    onFinish: () => {
      this.JudicialAidModuleStore.close()
    },
  })

  //担保事件
  InstrumentModuleStore = new Modal.Store({
    onOpen: async (id) => {
      return await http.post('/customer/view/detail/queryGuaranteeEventDetail', { id })
    },
    onFinish: () => {
      this.InstrumentModuleStore.close()
    },
  })

  // 动产抵押详情
  ImitHighModuleStore = new Modal.Store({
    onOpen: async (id) => {
      return await http.post('/customer/view/detail/queryChattelMortageDetail', { id })
    },
    onFinish: () => {
      this.ImitHighModuleStore.close()
    },
  })
}

export default new Store()
