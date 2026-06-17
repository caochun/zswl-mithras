import { makeAutoObservable } from '@zswl/admin'
import { TableStore } from '@zswl/components'
import Api from '@/api/dashboard/performance'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  personData = {}
  setPersonData = (data) => {
    this.personData = data
  }
  personTableStore = new TableStore({
    pagination: false,
    request: async () => {
      const res = await Api.postDashboardPerformancePersonal()
      this.setPersonData(res)
      return res.dataList ?? []
    },
  })
}
export default Store
