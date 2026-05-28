import { TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from './api'
import DetailStore from '../detail/store'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  table = new TableStore({
    request: ({ rest }) => {
      return Api.writeoffList({ id: DetailStore.getClientId(), ...rest })
    },
  })
  writeoffList = async (id) => {
    await Api.writeoffList({ id })
  }
}
export default new Store()
