import { makeAutoObservable, history } from '@zswl/admin'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  //变更日志详情
  toDifferentInfo = (id, bizType) => {
    history.push(`/credit/establish/detail/log/diffInfo/${id}?bizType=${bizType}`)
  }
}
export default new Store()
