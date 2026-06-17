import { TableStore, PageStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from '@/api/customer/maintainApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  tableLog = new TableStore({
    request: async (res) => {
      return await Api.getVersionList({ mainId: res.cId, module: 'CLIENT' })
    },
  })
  //变更日志详情
  toDifferentInfo = ({ id, clientId }) => {
    history.push(`/customer/maintain/detail/log/diffInfo/${id}?clientId=${clientId}`)
  }
  page = new PageStore({})
}
export default Store
