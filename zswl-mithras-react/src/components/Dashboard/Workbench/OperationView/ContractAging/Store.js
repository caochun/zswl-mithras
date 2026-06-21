import { DrawerStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { initYearQueryDate, formatQueryDate } from '@/utils/domains/dashboard/DashboardUtilsOperation'
import Api from '@/api/dashboard/contractAging'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  queryDate = initYearQueryDate
  setQueryDate = (date) => {
    this.queryDate = date
  }
  onValuesChange = (changedValues, allValues) => {
    const { queryDate } = allValues
    this.setQueryDate(queryDate || [])
    this.getApprovalStatistics()
    this.getReturnStatistics()
  }

  approvalStatisticsLoading = false
  setApprovalStatisticsLoading = (flag) => {
    this.approvalStatisticsLoading = flag
  }
  approvalStatistics = {}
  setApprovalStatistics = (data) => {
    this.approvalStatistics = data
  }
  getApprovalStatistics = async () => {
    this.setApprovalStatisticsLoading(true)
    const res = await Api.postDashboardOperationApprovalStatistics({
      ...formatQueryDate({ dateRange: this.queryDate }),
    })
    this.setApprovalStatistics(res)
    this.setApprovalStatisticsLoading(false)
  }

  returnStatisticsLoading = false
  setReturnStatisticsLoading = (flag) => {
    this.returnStatisticsLoading = flag
  }
  returnStatistics = {}
  setReturnStatistics = (data) => {
    this.returnStatistics = data
  }
  getReturnStatistics = async () => {
    this.setReturnStatisticsLoading(true)
    const res = await Api.postDashboardOperationContractReturnStatistics({
      ...formatQueryDate({ dateRange: this.queryDate }),
    })
    this.setReturnStatistics(res)
    this.setReturnStatisticsLoading(false)
  }

  approvalDrawer = new DrawerStore({
    onOpen: () => {
      this.approvalDrawerTable.setParams({
        endTime: this.queryDate,
      })
    },
  })

  approvalDrawerTable = new TableStore({
    request: async (params) => {
      return await Api.postDashboardOperationApprovalList(params)
    },
  })

  returnDrawer = new DrawerStore({
    onOpen: () => {
      this.returnDrawerTable.setParams({
        endTime: this.queryDate,
      })
    },
  })

  returnDrawerTable = new TableStore({
    request: async (params) => {
      return await Api.postDashboardOperationContractReturnList(params)
    },
  })
}
export default Store
