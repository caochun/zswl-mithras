import { makeAutoObservable, history } from '@zswl/admin'
import { PageStore, ModalStore } from '@zswl/components'
import baseInfoApi from '@/api/kpi/projectAllot/baseInfo'
import weightApi from '@/api/kpi/projectAllot/weight'
import { hasValue } from '@/utils'
import Api from '@/api/kpi/projectAllot'

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

  saveExtraData = async (values) => {
    const { teamLeaderId, belongDeptId, remark } = this.page.getData()
    const { suppleDescribe } = values
    const { projectDistributionId } = this.page.getParams()
    await baseInfoApi.postBaseinfoModify({
      remark,
      projectDistributionId,
      teamLeaderId,
      suppleDescribe,
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

  saveAllocateInfoDetail = async (values) => {
    const { projectDistributionId } = this.page.getParams()
    let { weightInfoList } = values
    weightInfoList = weightInfoList.filter((item) => {
      return item.weightValue && +item.weightValue > 0
    })
    const newWeightInfoList = weightInfoList.map((item) => {
      if (!item.weightTarget || !hasValue(item.weightValue)) {
        item.weightTargetName = null
      }
      return item
    })
    await weightApi.postWeightSave({
      projectDistributionId,
      ...values,
      weightInfoList: newWeightInfoList,
    })
    this.getAllocateInfoDetail()
  }

  processInstanceId = ''
  setProcessInstanceId = (id) => {
    this.processInstanceId = id
  }
  getProcessInstanceId = async () => {
    const { projectDistributionId } = this.page.getParams()
    try {
      const res = await Api.postProjectdistributionGetProcess({ projectDistributionId })
      this.setProcessInstanceId(res.processInstanceId)
    } catch (err) {
      this.setProcessInstanceId('')
    }
  }

  // 变更前
  beforeAllocateInfoData = []
  setBeforeAllocateInfoData = (data) => {
    this.beforeAllocateInfoData = data
  }
  beforeAllocateInfoModal = new ModalStore({
    onOpen: async () => {
      const { projectDistributionId, businessVersion } = this.page.getParams()
      const res = await Api.postProjectdistributionCompare({
        projectDistributionId,
        businessVersion,
      })
      this.setBeforeAllocateInfoData(res)
    },
  })
}
export default Store
