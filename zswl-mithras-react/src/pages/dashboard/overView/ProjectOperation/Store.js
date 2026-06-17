import { TableStore, DrawerStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/dashboard/overview'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  currentTab = 'ALL'
  setCurrentTab = (value) => {
    this.currentTab = value
  }

  cardData = []
  setCardData = (data) => {
    this.cardData = data
  }

  cardLoading = false
  setCardLoading = (value) => {
    this.cardLoading = value
  }

  getCardData = async () => {
    this.setCardLoading(true)
    const res = await Api.postDashboardOperationefficiencyStatistics({
      type: this.currentTab,
    })
    this.setCardData(res)
    this.setCardLoading(false)
  }

  departmentDrawer = new DrawerStore({})
  departmentTable = new TableStore({
    request: async (params) => {
      const result = await Api.postDashboardOperationefficiencyList({
        type: this.currentTab,
      })
      // 这三个部门现在没有了
      return result.filter(
        (item) => !['化工建材业务部', '机械和加工业务部', '冷链物流团队'].includes(item.deptName)
      )
    },
  })
}
export default Store
