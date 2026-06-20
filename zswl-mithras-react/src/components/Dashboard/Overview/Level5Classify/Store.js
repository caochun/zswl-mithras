import { TableStore, DrawerStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/dashboard/overview'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  tableData = []
  table = new TableStore({
    request: async (params) => {
      const result = await Api.postDashboardAssetfiveclassifyStatistics()
      this.tableData = result
      return result
    },
  })

  level5ClassifyDrawerDrawer = new DrawerStore({})
}
export default Store
