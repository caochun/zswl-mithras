import { makeAutoObservable } from '@zswl/admin'
import { PageStore } from '@zswl/components'
import baseInfoApi from '@/api/budget/profitDistributionBaseInfo'
import weightApi from '@/api/budget/profitDistributionWeight'
import { hasValue } from '@/utils'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({
    request: async (params) => {
      const { projectDistributionId, businessVersion } = params
      const res = await baseInfoApi.postBaseinfoDetail({ projectDistributionId, businessVersion })
      return res ?? {}
    },
  })

  saveData = async (values) => {
    const { belongDeptId, teamLeaderId, remark } = values
    const { projectDistributionId } = this.page.getParams()
    await baseInfoApi.postBaseinfoModify({
      projectDistributionId,
      teamLeaderId,
      remark,
      profitBelongDeptId: belongDeptId,
    })
    this.page.init()
  }

  allocateInfoDetail = {}
  setAllocateInfoDetail = (data) => {
    this.allocateInfoDetail = data
  }
  getAllocateInfoDetail = async () => {
    const { projectDistributionId, businessVersion } = this.page.getParams()
    let res = await weightApi.postWeightDetail({ projectDistributionId, businessVersion })
    if (res?.deptWeightInfoList?.length > 0) {
      res.deptWeightInfoList = res.deptWeightInfoList.map((item, i) => {
        if (!item.weightType) {
          item.weightType = 'BUSINESS_DEPT'
        }
        if (!item.weightTypeName) {
          item.weightTypeName = '业务部门'
        }
        return item
      })
    }

    if (res?.deptLaunchWeightInfoList?.length > 0) {
      res.deptLaunchWeightInfoList = res.deptLaunchWeightInfoList.map((item, i) => {
        if (!item.weightType) {
          item.weightType = 'BUSINESS_DEPT'
        }
        if (!item.weightTypeName) {
          item.weightTypeName = '业务部门'
        }
        return item
      })
    }

    const { effectMonth, effectYear } = res
    this.setAllocateInfoDetail({
      ...res,
      effectMonth: effectMonth ? moment(`${effectYear}/${effectMonth}`, 'yyyy/MM') : undefined,
    })
  }
  transformDeptData = (data = []) => {
    const newDeptList = data
      .filter((item) => {
        return item.weightValue && +item.weightValue > 0
      })
      .map((item) => {
        if (!item.weightTarget || !hasValue(item.weightValue)) {
          item.weightTargetName = null
        }
        return item
      })
    return newDeptList
  }
  saveDepAllocateInfoDetail = async (values) => {
    const { projectDistributionId } = this.page.getParams()
    let { deptWeightInfoList, deptLaunchWeightInfoList } = values

    const newDeptWeightInfoList = this.transformDeptData(deptWeightInfoList)
    const newDeptLaunchWeightInfoList = this.transformDeptData(deptLaunchWeightInfoList)
    await weightApi.postDeptWeightSave({
      projectDistributionId,
      deptWeightInfoList: newDeptWeightInfoList,
      deptLaunchWeightInfoList: newDeptLaunchWeightInfoList,
    })
    this.getAllocateInfoDetail()
  }

}
export default Store
