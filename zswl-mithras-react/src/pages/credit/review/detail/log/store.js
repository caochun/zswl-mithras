import { TableStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from './api'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  projectId

  setProjectId = (id) => {
    this.projectId = id
    this.tableLog.search()
  }
  tableLog = new TableStore({
    request: async () => {
      return await Api.getVersionList({ mainId: this.projectId })
    },
  })
  //变更日志详情
  toDifferentInfo = (id) => {
    history.push(`/credit/review/detail/log/diffInfo/${id}`)
  }
}
export default new Store()
