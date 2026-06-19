import { TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import moment from 'moment'
import checkPlanApi from '@/api/afterLease/checkPlan'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  staticInfo = {}
  getStatistics = async (currentData) => {
    const res = await checkPlanApi.getStatistics(currentData)
    this.staticInfo = res ?? {}
  }

  $table = new TableStore({
    request: async (searchData) => {
      const currentData = {
        ...searchData,
        targetMonth: searchData.targetMonth
          ? moment(searchData.targetMonth).format('yyyy-MM') + '-01'
          : undefined,
      }
      this.getStatistics(currentData)
      return checkPlanApi.getExternalList(currentData)
    },
  })
}
export default new Store()
