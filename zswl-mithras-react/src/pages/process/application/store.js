import { makeAutoObservable } from '@zswl/admin'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  loading = true
  processData = {}
  myProcessCount = async () => {
    this.processData = await Api.myProcessCount()
    this.loading = false
  }
}
export default new Store()
