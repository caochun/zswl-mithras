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
  getMaterialsDesc = async (id) => {
    const result = await Api.getMaterialsDesc(id)
    return result
  }
}
export default Store
