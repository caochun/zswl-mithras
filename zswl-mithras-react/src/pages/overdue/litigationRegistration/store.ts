import { ModalStore, PageStore, TableStore } from '@zswl/components'
import { observer, http, history, makeAutoObservable } from '@zswl/admin'
import litigationRegistrationApi from '@/api/overdue/litigationRegistrationApi'
import { message } from 'antd'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({})
  table = new TableStore({
    request: async (params) => {
      const res = await litigationRegistrationApi.postLitigationPageList(params)
      return res
    },
  })
  createModal = new ModalStore({
    onFinish: async (params) => {
      const res = await litigationRegistrationApi.postLitigationAdd(params)
      this.table.search()
      message.success('新增成功')
      this.createModal.close()
      history.push(`/overdue/litigationRegistration/detail/${res}`)
    },
  })
}
export default new Store()
