import { PageStore } from '@zswl/components'
import Api from './api'

class Store {
  page = new PageStore({
    request(params) {
      return Api.getBaseInfo(params)
    },
  })
}

export default Store
