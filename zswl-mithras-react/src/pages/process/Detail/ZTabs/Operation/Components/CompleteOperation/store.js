import { makeAutoObservable } from '@zswl/admin'
import { FormStore } from '@zswl/components'
import Api from './api'
class Store {
  constructor(props) {
    makeAutoObservable(this)
  }
  completeOperation = new FormStore({})
  founderList = []

  //创建人搜索
  searchFounder = async (e, s) => {
    if (s == '1') {
      this.founderList = await Api.searchFounder({ name: e, sameDept: false, job: 'riskmanager' })
    } else {
      this.founderList = await Api.searchFounder({ name: e, sameDept: false })
    }
  }
}
export default new Store()
