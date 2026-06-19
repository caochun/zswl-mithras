import { TableStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import groupCreditReviewVersionApi from '@/api/credit/groupCreditReviewVersionApi'
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
      return await groupCreditReviewVersionApi.postVersionList({ mainId: this.projectId })
    },
  })
  //变更日志详情
  toDifferentInfo = (id) => {
    history.push(`/credit/review/detail/log/diffInfo/${id}`)
  }
}
export default new Store()
