import { makeAutoObservable } from '@zswl/admin'
import { hasValue } from '@/utils'
import { PageStore } from '@zswl/components'
import groupCreditReviewVersionApi from '@/api/credit/groupCreditReviewVersionApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({})

  compareData = {}

  init = async (id) => {
    const res = await groupCreditReviewVersionApi.postComparePreVersion({ id })
    this.compareData = res
  }

  formatPercent = (val) => {
    return hasValue(val) ? val / 10000 : undefined
  }
}
export default new Store()
