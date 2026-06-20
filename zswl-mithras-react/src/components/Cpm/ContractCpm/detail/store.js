import { TableStore, Modal, ModalStore, App, PageStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  contractD
  getContractD = () => this.contractD || '8'
  setContractD = (id) => {
    this.contractD = id
  }
  page = new PageStore({})
}
export default new Store()
