import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/budget/flowCenter/interfaceManageApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  table = new TableStore({
    request: (params) => {
      return Api.postRecordPagelist(params)
      return []
    },
  })
  rePush = async (record) => {
    await Api.postRecordPush({ id: record.id })
    this.table.search()
  }
  rebuildingMasses = async (record) => {
    await Api.postRecordIgnore({ id: record.id })
    this.table.search()
  }
}
export default new Store()
