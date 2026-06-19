import { makeAutoObservable, getQuery } from '@zswl/admin'
import { message } from 'antd'
import { compareDetail, formatPercent } from '@/utils'
import Api from '@/api/project/component/ReviewDetail/QuotationScheme/api'

const detailMap = {
  ZL: 'leasePriceDetailRSP',
  BL: 'factoringPriceDetailRSP',
  ZZ: 'leasePriceDetailRSP',
  ZR: 'aocPriceDetailRSP',
}

class Store {
  constructor(props) {
    this.baseForm = props?.baseForm
    this.rootStore = props?.rootStore
    makeAutoObservable(this)
  }
  isFormAdjust
  projectQSDetail
  newLeaseCredit = false
  setProjectQSDetail = (val) => {
    this.projectQSDetail = val
  }
  bizType
  getProjectQSDetail = async (id) => {
    const isFormApproval = getQuery('typeId') == 'approval'
    const { processInstanceId, modelKey, businessVersion } = this.rootStore?.page.getParams()
    if (isFormApproval && !this.isFormAdjust) {
      const isCreate = modelKey === 'ProjReviewCreateFlow'
      const api = isCreate ? Api.postProjectPricingQSDetailCompare : Api.postProjectQSDetailCompare
      const res = await api({
        id,
        processInstanceId,
        businessVersion,
      })
      const detail = isCreate ? res : res[detailMap[this.bizType]]
      const { newDetail, isLog } = compareDetail(detail)
      this.projectQSDetail = { ...newDetail, isLog }
      this.baseForm?.setFieldValue('irr', formatPercent(newDetail.irrPercent))
    } else {
      const res = await Api.postProjectQSDetail({ id })
      this.projectQSDetail = res[detailMap[this.bizType]]
      this.baseForm?.setFieldValue('irr', formatPercent(this.projectQSDetail?.irrPercent))
    }
  }

  postProjectQSModify = async (params, callback) => {
    const { code, msg } = await Api.postProjectQSModify(params)
    if (code === 200) {
      message.success('保存成功')
      callback && callback()
      return Promise.resolve()
    } else {
      message.info(msg)
      return Promise.reject()
    }
  }
}
export default Store
