import { PageStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/cpm/marginManagementApi'

class Store {
  constructor() {
    makeAutoObservable(this, { targetID: false })
  }

  targetID
  getClientId = () => this.targetID || '8'
  setClientId = (id) => {
    this.targetID = id
  }

  marginDetailData = {}
  marginDetail = async (id) => {
    this.marginDetailData = await Api.marginDetail({ id })
  }
  page = new PageStore({})
}
export default new Store()
