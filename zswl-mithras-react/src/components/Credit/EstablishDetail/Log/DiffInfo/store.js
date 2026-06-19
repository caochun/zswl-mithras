import { makeAutoObservable } from '@zswl/admin'
import { hasValue } from '@/utils'
import { PageStore } from '@zswl/components'
import Api from '@/api/credit/groupCreditEstablishVersionApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({})

  compareData = {}

  init = async (id) => {
    const res = await Api.postComparePreVersion({ id })
    this.compareData = res
    // this.setCompareData(res ?? {})
  }

  formatPercent = (val) => {
    return hasValue(val) ? val / 10000 : undefined
  }
}
export default new Store()
