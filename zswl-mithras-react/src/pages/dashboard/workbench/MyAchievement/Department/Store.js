import { makeAutoObservable } from '@zswl/admin'
import { TableStore } from '@zswl/components'
import Api from '../api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  deptData = {}
  setDeptData = (data) => {
    this.deptData = data
  }

  updateTime = ''
  deptTableStore = new TableStore({
    pagination: false,
    request: async () => {
      const res = await Api.postDashboardPerformanceDept()
      this.setDeptData(res)
      this.updateTime = res.dataUpdateTime
      return res.dataList ?? []
    },
  })
}
export default Store
