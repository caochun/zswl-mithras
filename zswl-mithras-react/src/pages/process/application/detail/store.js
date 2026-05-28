import { makeAutoObservable, getQuery } from '@zswl/admin'
//import { TableStore } from '@zswl/components'
//import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this, { clientId: false })
  }
}
export default new Store()
