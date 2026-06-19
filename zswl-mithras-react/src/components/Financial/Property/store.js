import { TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/financial/propertyApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  deptIdList = []

  $table = new TableStore({
    request: async (params) => {
      const res = await Api.postList({ ...params })
      this.deptIdList = res.others?.deptIdList
      return res
    },
  })

  export = async () => {
    const params = this.$table.getParams()
    Api.postListExport(params)
  }
}
export default Store
