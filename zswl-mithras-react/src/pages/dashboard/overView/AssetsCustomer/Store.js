import { DrawerStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/dashboard/overview'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  // 资产行业分布
  assetIndustryDistribution = []
  getAssetIndustryDistribution = async () => {
    const res = await Api.postDashboardDistributionAssetsindustry()
    this.assetIndustryDistribution = res
  }

  // 客户部门分布
  customerDepartment = []
  getCustomerDepartmentData = async () => {
    const res = await Api.postDashboardDistributionClientdepartment()
    this.customerDepartment = res
  }

  // 客户全周期
  customerLifeCycleData = []
  getCustomerLifeCycleData = async () => {
    const res = await Api.postDashboardDistributionClientstatistics()
    this.customerLifeCycleData = res
  }
  customerType = ''
  customerDrawer = new DrawerStore({
    onOpen: (data) => {
      this.customerType = data?.type
    },
  })
}
export default Store
