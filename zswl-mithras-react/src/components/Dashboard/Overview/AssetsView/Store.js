import { TableStore, DrawerStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/dashboard/overview'
import { message } from 'antd'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  currentTab = 'province'
  setCurrentTab = (value) => {
    this.currentTab = value
  }

  // 省份排名
  provinceRankingTableData = []
  provinceRankingTable = new TableStore({
    pagination: false,
    request: async (params) => {
      const result = await Api.postDashboardAssetsDetailByProvince()
      this.provinceRankingTableData = result
      // 截取前5条
      return result.slice(0, 5)
    },
  })
  // 经济区排名
  areaRankingTable = new TableStore({
    pagination: false,
    request: async (params) => {
      return Api.postDashboardAssetsDetailByArea()
    },
  })

  allRankingDrawer = new DrawerStore({})
  allRankingTable = new TableStore({
    request: async (params) => {
      return await Api.postDashboardAssetsDetailByProvince()
    },
  })

  downLoadRank = async () => {
    await Api.postDashboardAssetsDetailByProvinceExport()
  }
}
export default Store
