import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  activeKey = 'inside'
  activeKeyChange = (e) => {
    this.activeKey = e
  }
}
export default new Store()
