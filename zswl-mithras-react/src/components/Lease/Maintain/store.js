import { TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/lease/maintainApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  $table = new TableStore({
    request: async (params) => {
      return Api.postList(params)
    },
  })

  batchExport = async () => {
    const { keys } = this.$table.getSelected()
    const param = this.$table.getParams()
    await Api.postListDown({
      ...param,
      ids: keys,
    })
  }
}
export default Store
