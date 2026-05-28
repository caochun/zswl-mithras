import { makeAutoObservable } from '@zswl/admin'
import { TableStore, Modal } from '@zswl/components'
import { message } from 'antd'
import Api from './api'
class Store {
  constructor() {
    makeAutoObservable(this, { modelId: false })
  }

  getDetail = async (modelId) => {
    await Api.getDetail({ modelId })
  }
  //保存数据
  save = async (bpmnXml) => {
    await Api.save({ bpmnXml })
  }

  searchFounderList = []
  searchFounder = async () => {
    this.searchFounderList = await Api.searchFounder({ sameDept: false, pageSize: 5000 })
  }
}
//export default new Store()
export default Store
