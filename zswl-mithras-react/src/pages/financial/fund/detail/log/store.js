import { makeAutoObservable, history } from '@zswl/admin'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  //变更日志详情
  toDifferentInfo = (id) => {
    history.push(`/financial/fund/detail/log/diffInfo/${id}`)
  }
}
export default new Store()
