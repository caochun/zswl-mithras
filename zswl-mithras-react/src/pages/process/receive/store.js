import { makeAutoObservable } from '@zswl/admin'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  loading = true
  receiveData = {}
  myReceiveCount = async () => {
    this.receiveData = await Api.myReceiveCount()
    this.loading = false
  }
}
export default new Store()
