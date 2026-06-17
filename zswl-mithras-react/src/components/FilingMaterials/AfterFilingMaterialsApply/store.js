import { makeAutoObservable } from '@zswl/admin'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  enumType = {}

  getEnumType = async (id) => {
    const result = await Api.getOperationsDirDict(id)
    this.enumType = result
  }
}
export default Store
