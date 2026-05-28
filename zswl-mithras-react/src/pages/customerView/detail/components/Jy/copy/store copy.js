import { makeAutoObservable, http } from '@zswl/admin'
import { Modal, Table } from '@zswl/components'
import { getDatabaseList } from './api' // 引入封装后的请求方法

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  // 初始化卡片数据容器
  CardContainer = {}

  /**
   * 公共方法：获取表格数据并处理卡片数据
   * @param {string} dataKey 后端返回数据的字段名
   * @param {object} params 请求参数
   */
  async fetchTableData(dataKey, params) {
    const response = await getDatabaseList({ ...params, uscc: '1001' })
    if (!response) return []

    // 如果有 `cur_count` 和 `total_count`，直接使用
    const cardData = response[dataKey] || {}
    if (cardData.cur_count !== undefined && cardData.total_count !== undefined) {
      this.CardContainer[dataKey] = {
        current: cardData.cur_count,
        total: cardData.total_count,
      }
    } else if (Array.isArray(cardData)) {
      // 如果是数组，根据数组长度计算 total
      this.CardContainer[dataKey] = {
        total: cardData.length,
        current: cardData.length, // 当前默认为数组长度
      }
    }

    // 返回表格数据
    return cardData?.[`${dataKey}_list`] || cardData || []
  }

  // 经营异常信息
  bizException = new Table.Store({
    request: async (params) => {
      return await this.fetchTableData('bizException', params)
    },
  })

  // 行政处罚
  caseInfoList = new Table.Store({
    request: async (params) => {
      return await this.fetchTableData('case_info_list', params)
    },
  })

  // 严重违法信息
  illegalList = new Table.Store({
    request: async (params) => {
      return await this.fetchTableData('illegal_list', params)
    },
  })

  // 重大税收违法
  majorIllegalList = new Table.Store({
    request: async (params) => {
      return await this.fetchTableData('major_illegal_list', params)
    },
  })

  // 担保事件
  guarantEventList = new Table.Store({
    request: async (params) => {
      return await this.fetchTableData('guarant_event_list', params)
    },
  })

  // 动产抵押（特别情况，不计算 total）
  mortgageList = new Table.Store({
    request: async (params) => {
      return await this.fetchTableData('mortgage_list', params)
    },
  })

  // 司法协助弹窗
  JudicialAidModuleStore = new Modal.Store({
    onOpen: async (id) => {
      const data = await http.post('/judicialAssistanceDetail', { id })
      return data
    },
    onFinish: async () => {
      this.JudicialAidModuleStore.close()
    },
  })

  // 裁判文书弹窗
  InstrumentModuleStore = new Modal.Store({
    onOpen: async (id) => {
      const data = await http.post('/judgementDocumentDetail', { id })
      return data
    },
    onFinish: async () => {
      this.InstrumentModuleStore.close()
    },
  })

  // 限制高消费弹窗
  ImitHighModuleStore = new Modal.Store({
    onOpen: async (id) => {
      const data = await http.post('/limitHighConsumeDetail', { id })
      return data
    },
    onFinish: async () => {
      this.ImitHighModuleStore.close()
    },
  })
}

export default new Store()
