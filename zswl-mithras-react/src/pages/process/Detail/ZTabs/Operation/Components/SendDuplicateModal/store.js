import { makeAutoObservable } from '@zswl/admin'
import { debounce as _debounce } from 'lodash'
import Api from './api'
class Store {
  constructor() {
    makeAutoObservable(this, { clientId: false })
  }

  founderList = []
  searchFounder = _debounce(async (e) => {
    this.founderList = await Api.searchFounder({ name: e, sameDept: false })
  }, 500)
}
export default new Store()
