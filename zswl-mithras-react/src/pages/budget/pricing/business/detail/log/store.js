import { makeAutoObservable, history } from '@zswl/admin'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  //变更日志详情
  toDifferentInfo = (id, month) => {
    history.push(`/budget/pricing/business/detail/log/diffInfo/${id}?month=${month}`)
  }
}
export default new Store()
