import { makeAutoObservable, getQuery } from '@zswl/admin'
import { TableStore } from '@zswl/components'
import { message } from 'antd'
import { compareDetail } from '@/utils'
import Api from './api'

const detailMap = {
  ZL: 'leasePriceRSP',
  BL: 'factoringPriceRSP',
  ZZ: 'leasePriceRSP',
  ZR: 'aocPriceRSP',
}

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  businessVersion
  projectQSDetail
  newLeaseCredit = false
  setProjectQSDetail = (val) => {
    this.projectQSDetail = val
  }
  bizType
  getProjectQSDetail = async (id) => {
    const isFormApproval = getQuery('typeId') == 'approval'
    if (isFormApproval) {
      const res = await Api.postProjectQSDetailCompare({
        projEstablishId: id,
        businessVersion: this.businessVersion,
      })
      this.projectQSDetail = compareDetail(res[detailMap[this.bizType]]).newDetail
    } else {
      const res = await Api.postProjectQSDetail({ projEstablishId: id })
      this.projectQSDetail = res[detailMap[this.bizType]]
    }
  }

  setNewLeaseCredit = (val) => {
    this.newLeaseCredit = val
  }
  postProjectQSModify = async (params, callback) => {
    const { code, msg } = await Api.postProjectQSModify(params)
    if (code === 200) {
      message.success('保存成功')

      callback && callback()
    } else {
      message.info(msg)
    }
  }
}
export default Store
