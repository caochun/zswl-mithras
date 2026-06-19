import { TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import checkPlanApi from '@/api/afterLease/checkPlan'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  deptIdList = []

  $table = new TableStore({
    request: async (params) => {
      const res = await checkPlanApi.postLedgerList({
        ...params,
        deadLineFrom: params.deadLineFrom ? params.deadLineFrom[0] : undefined,
        deadLineTo: params.deadLineFrom ? params.deadLineFrom[1] : undefined,
      })
      this.deptIdList = res.others?.deptIdList
      return res
    },
  })
}
export default Store
