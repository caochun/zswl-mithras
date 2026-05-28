import { makeAutoObservable } from '@zswl/admin'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  activeKey = 'dept'
  setActiveKey = (key) => {
    this.activeKey = key
  }
}
export default Store
