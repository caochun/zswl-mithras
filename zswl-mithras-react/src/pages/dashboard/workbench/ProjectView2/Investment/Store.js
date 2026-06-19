import { TableStore, DrawerStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/dashboard/pay'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  activityKey = 'MONTH'
  setActivityKey = (key) => {
    this.activityKey = key
  }

  companyData = {}
  setCompanyData = (data) => {
    this.companyData = data
  }
  getCompanyData = async () => {
    const res = await Api.postDashboardPayStatistics({
      queryType: this.activityKey,
    })
    res && this.setCompanyData(res)
  }

  deptTableStore = new TableStore({
    pagination: false,
    request: async (params) => {
      const list = await Api.postDashboardPayStatisticsByDept({
        ...params,
        queryType: this.activityKey,
      })
      return list
    },
  })

  queryDimension = 'CONTRACT'
  setQueryDimension = (str) => {
    this.queryDimension = str
  }
  investmentDrawer = new DrawerStore({
    onOpen: (key) => {
      // 是合同维度 否则 借据维度
      const isContractDimension = ['payContractQuantity', 'payAmount'].includes(key)
      this.setQueryDimension(isContractDimension ? 'CONTRACT' : 'RECEIPT')
    },
  })
}
export default Store
