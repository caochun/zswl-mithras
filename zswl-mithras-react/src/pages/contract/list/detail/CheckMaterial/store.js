import { makeAutoObservable } from '@zswl/admin'
import { message, Modal } from 'antd'
import { ModalStore } from '@zswl/components'
import Api from './api'
class Store {
  constructor({ contractId, businessVersion }) {
    this.contractId = contractId
    this.businessVersion = businessVersion
    makeAutoObservable(this)
  }

  dataDetail = []
  getDataDetail = async (contractId) => {
    const res = await Api.postProjectDataDetail({
      contractId,
      businessVersion: this.businessVersion,
    })
    this.dataDetail = res ?? []
  }
}
export default Store
