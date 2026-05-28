import { makeAutoObservable, history } from '@zswl/admin'
import { message } from 'antd'
import { PageStore } from '@zswl/components'
import Api from './api'
class Store {
  constructor(clientName, canEdit) {
    makeAutoObservable(this)
    this.clientName = clientName
    this.canEdit = canEdit
  }

  depostInfo
  contractId
  contractCode
  __id
  page = new PageStore({
    request: async ({ contractId, bizType, planType, businessKey }) => {
      this.contractId = contractId
      this.depostInfo = await Api.depostInfo(businessKey)
        .then((res) => res)
        .catch(() => {
          return [{}, {}]
        })
      if(this.depostInfo.deductionAmount === 0 && this.depostInfo.returnedAmount === 0){
        this.depostInfo = {
          ...this.depostInfo,
          returnedAmount:this.depostInfo.collectionAmount,
        }
      }
      if(this.depostInfo.returnedAmount/10000>0){
        this.depostInfo = {
          ...this.depostInfo,
          recyclingFlag:'0',
        }
      }
      if (this.depostInfo.contractId) {
        this.contractId = this.depostInfo.contractId
        this.contractCode = this.depostInfo.contractCode
        this.__id = this.depostInfo.id
      }
      const res = await Api.getBaseInfo({ id: this.depostInfo.contractId })
      return { retreatInfoId: this.depostInfo?.id, detail: res, depostInfo: this.depostInfo }
    },
    afterInit: async () => {
      this.canEdit && this.saveRefund(undefined,{
        ...this.depostInfo,
        contractId: this.contractId,
        id: this.__id,
        clientName: this.clientName,
        contractCode: this.contractCode,
      }, false)
    },
  })
  rentListSize = 0
  setRentListSize = (size) => {
    if(typeof size === 'number'){
      this.rentListSize = size
    }
  }
  approvalLoading
  submit = async () => {
    try {
      this.approvalLoading = true
      await Api.submit({
        ...this.depostInfo,
        contractId: this.contractId,
        id: this.__id,
      })
      this.approvalLoading = false
      message.success('提交成功')
      setTimeout(() => {
        history.push(`/contract/list`)
      }, 500)
    } catch (e) {
      this.approvalLoading = false
    }
  }
  saveRefund = async (cb, values, validate = true) => {
    try {
      if(validate && (values.deductionAmount/10000>0 && this.rentListSize === 0 || values.deductionAmount/10000 === 0 && this.rentListSize > 0)){
        message.warn('抵扣租金与内扣金额不匹配，请检查！')
        return;
      }
      await Api.refundSchemeSave({
        ...values,
        contractId: this.contractId,
        id: this.__id,
        clientName: this.clientName,
        contractCode: this.contractCode,
      })
      this.depostInfo = {
        ...this.depostInfo,
        ...values,
      }
      cb&&cb(false)
      page.init()
    } catch (e) {}
  }
}
export default Store
