import { TableStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from '@/api/project/component/EstablishmentDetail/Log/api'
import detailStore from '../store'
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
  toDifferentInfo = (id, bizType) => {
    console.log(id, 'idid')
    history.push(`/project/establishment/detail/log/diffInfo/${id}?bizType=${bizType}`)
  }
}
export default new Store()
