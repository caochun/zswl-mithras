import { PageStore, Modal, TableStore, ModalStore } from '@zswl/components'
import { history, makeAutoObservable } from '@zswl/admin'
import creditInformationApi from '@/api/credit/creditInformationApi'
import repaymentResponsibilityApi from '@/api/credit/repaymentResponsibilityApi'
import payableLoansApi from '@/api/credit/payableLoansApi'
import creditLimitApi from '@/api/credit/creditLimitApi'
import informationSummaryTableApi from '@/api/credit/informationSummaryTableApi'

class Store {
  constructor({ }) {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async (params) => {
      const baseParams = { ...params, page: 1, pageSize: 9999 }
      // 信息概要详情
      this.infoSummaryDetail = await informationSummaryTableApi.postSummaryDetail({
        ...baseParams,
      })
      const res = await creditInformationApi.postSummaryList({
        ...baseParams,
      })
      const payableLoans = (res ?? []).map((item) => {
        const { bodyList = [] } = item
        const newBodyList = bodyList.reduce((prev, cur) => {
          if (!prev[cur.paymentType]) {
            prev[cur.paymentType] = {}
          }
          prev[cur.paymentType][`${cur.fundClassification}_AccountNumber`] = cur.accountNumber
          prev[cur.paymentType][`${cur.fundClassification}_AccountAmount`] = cur.accountAmount
          prev[cur.paymentType].paymentTypeName = cur.paymentTypeName
          return prev
        }, {})
        return Object.values(newBodyList)
      })
      console.log('payableLoans: ', payableLoans)
      this.unsettledSummaryList = payableLoans
      return {}
    },
  })
  /**
   * 信息概要详情原始数据
   */
  infoSummaryDetail = null

  /**
   * 信息概要-顶部四项 表
   */
  infoSummaryHeaderTable = new TableStore({
    request: async () => {
      const d = this.infoSummaryDetail || {}
      return { list: [d] }
    },
  })

  /**
   * 信息概要-末尾统计项 表
   */
  infoSummaryStatTable = new TableStore({
    request: async () => {
      const d = this.infoSummaryDetail || {}
      const row = {
        nonCreditTransactionNumber: d.nonCreditTransactionNumber,
        taxArrearsRecordsNumber: d.taxArrearsRecordsNumber,
        civilJudgmentRecordsNumber: d.civilJudgmentRecordsNumber,
        mandatoryExecutionRecordsNumber: d.mandatoryExecutionRecordsNumber,
        administrativePenaltyRecordsNumber: d.administrativePenaltyRecordsNumber,
      }
      return { list: [row] }
    },
  })
  unsettledSummaryList = []
  /**
   * 未结清信贷及授信信息概要表 TableStore
   */
  unsettledSummaryTable = new TableStore({
    request: async (params) => {
      const pageParams = this.page.getParams() ?? {}
      return creditInformationApi.postSummaryList({
        ...params,
        ...pageParams,
      })
    },
  })
  /**
   * 授信额度信息概要表 TableStore
   */
  creditSummaryTable = new TableStore({
    request: async (params) => {
      const pageParams = this.page.getParams() ?? {}
      return creditLimitApi.postLimitList({
        ...params,
        ...pageParams,
      })
    },
  })
  /**
   * 相关还款责任信息概要表 TableStore
   */
  responsibilitySummaryTable = new TableStore({
    request: async (params) => {
      const pageParams = this.page.getParams() ?? {}
      return repaymentResponsibilityApi.postResponsibilityList({
        ...params,
        ...pageParams,
      })
    },
  })
  /**
   * 应期借款概要表 TableStore
   */
  payableLoansTable = new TableStore({
    request: async (params) => {
      const pageParams = this.page.getParams() ?? {}
      return payableLoansApi.postDetailsList({
        ...params,
        ...pageParams,
      })
    },
  })
}
export default Store
