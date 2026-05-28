import { TableStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  deptIdList = []

  $table = new TableStore({
    request: async (params) => {
      const res = await Api.postList({
        ...params,
        deadLineFrom: params.deadLineFrom ? params.deadLineFrom[0] : undefined,
        deadLineTo: params.deadLineFrom ? params.deadLineFrom[1] : undefined,
      })
      this.deptIdList = res.others?.deptIdList
      return res
    },
  })
}
export default Store
