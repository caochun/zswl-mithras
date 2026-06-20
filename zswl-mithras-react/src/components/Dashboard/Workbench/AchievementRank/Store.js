import { makeAutoObservable } from '@zswl/admin'
import { TableStore } from '@zswl/components'
import Api from '@/api/dashboard/performance'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  activeKey = 'deptShip'
  setActiveKey = (key) => {
    this.activeKey = key
  }

  updateTime = ''
  deptShipTableStore = new TableStore({
    pagination: false,
    request: async () => {
      const res = await Api.postDashboardPerformanceDeptShipSort()
      this.updateTime = res.dataUpdateTime
      return res.dataList ?? []
    },
  })

  deptInTableStore = new TableStore({
    pagination: false,
    request: async () => {
      const res = await Api.postDashboardPerformanceDeptInSort()
      return res.dataList ?? []
    },
  })
}
export default Store
