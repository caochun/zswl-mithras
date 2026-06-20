import { makeAutoObservable, getQuery } from '@zswl/admin'
import { Modal, Table } from '@zswl/components'
import {
  chattelMortgageDetail,
  guaranteeEventDetail,
  majorTaxDetail,
  queryBizRisk,
} from '@/api/customerView/customerDetailApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  // 缓存请求的 Map
  requestCache = new Map()

  // 缓存所有数据
  allData = null

  // 初始化卡片数据容器
  CardContainer = {}
  uscc = ''
  enterpriseName = ''

  /**
   * 获取所有数据
   */
  async fetchAllData(params) {
    const { uscc, enterpriseName } = getQuery()
    this.uscc = uscc
    this.enterpriseName = enterpriseName

    // 请求唯一标识符
    const requestKey = `all-data-${JSON.stringify(params)}`

    // 如果请求正在进行中，直接返回缓存的 Promise
    if (this.requestCache.has(requestKey)) {
      return this.requestCache.get(requestKey)
    }

    // 新的请求
    const requestPromise = queryBizRisk({ ...params, companyName: this.enterpriseName })
      .then((response) => {
        if (!response) return null

        // 处理卡片数据
        Object.entries(response).forEach(([key, value]) => {
          if (value?.cur_count !== undefined) {
            this.CardContainer[key] = {
              current: value.cur_count || 0,
              total: value.total_count || 0,
            }
          } else if (key === 'major_illegal_list' && Array.isArray(value)) {
            this.CardContainer[key] = {
              current: value.length,
              total: value.length,
            }
          }
        })

        this.allData = response
        return response
      })
      .finally(() => {
        // 请求完成后移除缓存
        this.requestCache.delete(requestKey)
      })

    // 将请求加入缓存
    this.requestCache.set(requestKey, requestPromise)

    return requestPromise
  }

  /**
   * 从缓存数据中获取表格数据
   */
  async getTableData(dataKey, params) {
    await this.fetchAllData(params)
    const data = this.allData?.[dataKey]
    if (!data) return []

    if (dataKey === 'mortgage_list') {
      return data.mortgage_list?.map((item) => item.basic_info) || []
    }

    return data[dataKey] || data || []
  }

  // 1. bizException	object   企业经营异常
  bizException = new Table.Store({
    request: async (params) => {
      return await this.getTableData('exception_list', params)
    },
  })

  // 2. case_info_list	object 	行政处罚案件信息
  caseInfoList = new Table.Store({
    request: async (params) => {
      return await this.getTableData('case_info_list', params)
    },
  })

  // 单独处理 illegal_list	object
  illegalList = new Table.Store({
    request: async (params) => {
      return await this.getTableData('illegal_list', params)
    },
  })

  // 单独处理 他只是一个数组
  majorIllegalList = new Table.Store({
    request: async (params) => {
      await this.fetchAllData(params)
      return this.allData?.major_illegal_list || []
    },
  })

  // 担保事件列表
  guarantEventList = new Table.Store({
    request: async (params) => {
      return await this.getTableData('guarant_event_list', params)
    },
  })

  mortgageList = new Table.Store({
    request: async (params) => {
      await this.fetchAllData(params)

      return this.allData?.mortgage_list.mortgage_list || []
    },
  })

  // 重大税收违法详情
  JudicialAidModuleStore = new Modal.Store({
    onOpen: async (id) => {
      return await majorTaxDetail({ id })
    },
    onFinish: () => {
      this.JudicialAidModuleStore.close()
    },
  })

  //担保事件
  InstrumentModuleStore = new Modal.Store({
    onOpen: async (id) => {
      return await guaranteeEventDetail({ id })
    },
    onFinish: () => {
      this.InstrumentModuleStore.close()
    },
  })

  // 动产抵押详情
  ImitHighModuleStore = new Modal.Store({
    onOpen: async (id) => {
      return await chattelMortgageDetail({ id })
    },
    onFinish: () => {
      this.ImitHighModuleStore.close()
    },
  })
}

export default new Store()
