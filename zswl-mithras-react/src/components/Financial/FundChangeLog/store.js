import { makeAutoObservable, history } from '@zswl/admin'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  toDifferentInfo = (id) => {
    history.push(`/financial/fund/detail/log/diffInfo/${id}`)
  }
}

export default new Store()
