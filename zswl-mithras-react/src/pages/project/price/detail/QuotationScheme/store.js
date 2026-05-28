import { makeAutoObservable, getQuery } from '@zswl/admin'
import { TableStore } from '@zswl/components'
import { message } from 'antd'
import { compareDetail, formatPercent } from '@/utils'
import Api from './api'

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
    const { processInstanceId, businessVersion, modelKey } = this.rootStore?.page.getParams()
    if (isFormApproval && !this.isFormAdjust) {
      const isCreate = modelKey === 'ProjReviewPricingApprovalFlow'
      const api = isCreate ? Api.postProjectPricingQSDetailCompare : Api.postProjectQSDetailCompare
      const res = await api({ id, processInstanceId, businessVersion })
      const data = isCreate ? res : res[detailMap[this.bizType]]
      const { newDetail, isLog } = compareDetail(data)
      this.projectQSDetail = { ...newDetail, isLog }
      this.baseForm?.setFieldValue('irr', formatPercent(newDetail.irrPercent))
    } else {
      const res = await Api.postProjectQSDetail({ id })
      this.projectQSDetail = res[detailMap[this.bizType]]
      this.baseForm?.setFieldValue('irr', formatPercent(this.projectQSDetail.irrPercent))
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
