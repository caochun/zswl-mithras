import { TableStore, Modal, ModalStore, BlockStore, Page, PageStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import moment from 'moment'
import fundTransferApi from '@/api/financial/fundTransfer'
import { debounce } from 'lodash'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  blockStore = new PageStore({
    request: async () => {
      return await this.getSumChartsData()
    },
  })

  commonQueryParams = {
    timeRange: [moment().subtract(5, 'days'), moment()],
    accountBank: undefined,
  }

  onValuesChange = debounce((changedValues, allValues) => {
    const { timeRange, accountBank } = allValues
    this.commonQueryParams = {
      timeRange,
      accountBank,
    }
    this.blockStore.init()
  }, 500)

  getCommonQueryParams = () => {
    return {
      queryDateStart: moment(this.commonQueryParams.timeRange[0]).format('YYYY-MM-DD'),
      queryDateEnd: moment(this.commonQueryParams.timeRange[1]).format('YYYY-MM-DD'),
      accountBank: this.commonQueryParams.accountBank,
    }
  }

  //监管户待转资金汇总-图表数据
  sumChartsData = []
  setSumChartsData = (data) => {
    this.sumChartsData = data
  }
  getSumChartsData = async () => {
    const result = await fundTransferApi.postGraphList({
      ...this.getCommonQueryParams(),
    })
    const chartsData = []
    result?.sum?.map((item) => {
      const currentDate = item.allSum.pendingBalanceAmount / 10000
      chartsData.push({
        date: item.date,
        pendingBalanceAmount: currentDate < 0.01 ? 0 : currentDate,
      })
    })
    this.setSumChartsData(chartsData)
  }

  dailyChartsData = []
  setDailyChartsData = (data) => {
    this.dailyChartsData = data
  }

  dailyChartsModal = new ModalStore({
    onOpen: async (params) => {
      const data = await fundTransferApi.postGraphDaily({
        ...this.getCommonQueryParams(),
        currentDate: params.name,
      })
      return {
        date: params.name,
        data,
      }
    },
    onFinish: () => {},
  })

  // 开户银行-银行弹窗信息
  bankInfoModal = new ModalStore({
    onOpen: async (record) => {
      const data = await fundTransferApi.postDetailList({
        accountId: record.accountId,
        ...this.getCommonQueryParams(),
      })
      return {
        data,
        record,
      }
    },
  })
}
export default new Store()
