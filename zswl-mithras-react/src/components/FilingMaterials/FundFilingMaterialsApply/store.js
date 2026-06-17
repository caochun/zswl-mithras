import { makeAutoObservable } from '@zswl/admin'
import { PageStore } from '@zswl/components'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  enumType = {}
  page = new PageStore({
    request: (params) => {
      if (!params?.id) return {}
      return Api.getOperationsDirDict(params.id)
    },
  })
  getEnumType = async (id) => {
    const result = await Api.getOperationsDirDict(id)
    this.enumType = result
  }
}
export default Store
