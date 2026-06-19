import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/budget/stampDutyApi'

export default class CreateStore {
  constructor() {
    makeAutoObservable(this)
  }
  constractList = []
  fundContractsList = []
  receiptCodeList = []
  type
  updateBelongCode = async (type) => {
    this.type = type
    //mmht与ht共用字段，先注释掉
    // if (type === 'ht' && this.constractList.length > 0) {
    //   return
    // }
    if (type === 'rzht' && this.fundContractsList.length > 0) {
      return
    }
    const res = await Api.getContractList({ type })
    if (type === 'ht' || type === 'mmht') {
      if (res?.contracts?.list) {
        this.constractList = res?.contracts?.list.map((item) => ({ ...item, label: item['contractCode'], value: item['contractCode'] }))
      }
    } else {
      if (res?.fundContracts?.list) {
        this.fundContractsList = res?.fundContracts?.list.map((item) => ({ ...item, label: item['financingCode'], value: item['financingCode'] }))
      }
    }
  }
  getClientItem = async (belongId, name) => {
    const item = this[name].find((itm) => itm.value === belongId)
    if (item) {
      const res = await Api.getContractList({ belongId: item.id, type: this.type })
      if (res?.records?.list && res?.records?.list.length > 0) {
        return res?.records?.list
      }
    }
    return
  }
  add = async (data, setOpen) => {
    const res = await Api.add(data)
    setOpen(false)
    return res
  }
}
