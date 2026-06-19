import { makeAutoObservable } from '@zswl/admin'
import { TableStore, ModalStore } from '@zswl/components'
import moment from 'moment'
import Api from '@/api/kpi/performanceManage'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: async (params) => {
      return await Api.postKpiPerformanceManageMainList(params)
    },
  })

  createModal = new ModalStore({
    onFinish: async (values) => {
      const { year, status } = values
      const params = {
        year: year && moment(year).format('yyyy'),
        status,
      }
      await Api.postKpiPerformanceManageAdd(params)
      this.createModal.close()
      this.table.search()
    },
  })
}
export default Store
