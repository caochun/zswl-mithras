import { makeAutoObservable, getQuery } from '@zswl/admin'
import { Modal, Table } from '@zswl/components'
import {
  judgementDocumentDetail,
  judicialAssistanceDetail,
  limitHighConsumeDetail,
  queryLitigation,
} from '@/api/customerView/customerDetailApi'
import { uniqueId } from 'lodash'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  // 缓存请求的 Map
  requestCache = new Map()

  // 缓存所有数据
  allData = null

  // 立案信息
  caseInformations = new Table.Store({
    request: async (params, { filters }) => {
      return await this.getTableData('caseInformations', params)
    },
  })

  // 被执行信息
  executedInformations = new Table.Store({
    request: async (params) => {
      return await this.getTableData('executedInformations', params)
    },
  })

  // 司法协助
  judicialAssistances = new Table.Store({
    request: async (params) => {
      return await this.getTableData('judicialAssistances', params)
    },
  })

  // 裁判文书
  judgmentDocuments = new Table.Store({
    request: async (params) => {
      return await this.getTableData('judgmentDocuments', params)
    },
  })

  // 限制高消费
  restrictionHighConsumptions = new Table.Store({
    request: async (params) => {
      return await this.getTableData('restrictionHighConsumptions', params)
    },
  })

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
    const requestPromise = queryLitigation({ ...params, uscc })
      .then((response) => {
        if (!response) return null

        const newResponse = { ...response }
        this.CardContainer = {}
        Object.entries(newResponse).forEach(([key, value]) => {
          if (Array.isArray(value)) {
            this.CardContainer[key] = value.length
            newResponse[key] = value.map((item) => ({ ...item, uuid: uniqueId() }))
          }
        })

        this.allData = newResponse
        return newResponse
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
    return this.allData?.[dataKey] || []
  }

  JudicialAidModuleStore = new Modal.Store({
    onOpen: async (id) => {
      return await judicialAssistanceDetail({ id })
    },
    onFinish: async () => {
      this.JudicialAidModuleStore.close()
    },
  })

  InstrumentModuleStore = new Modal.Store({
    onOpen: async (id) => {
      return await judgementDocumentDetail({ id })
    },
    onFinish: async () => {
      this.InstrumentModuleStore.close()
    },
  })

  ImitHighModuleStore = new Modal.Store({
    onOpen: async (id) => {
      return await limitHighConsumeDetail({ id })
    },
    onFinish: async () => {
      this.ImitHighModuleStore.close()
    },
  })
}

export default new Store()
