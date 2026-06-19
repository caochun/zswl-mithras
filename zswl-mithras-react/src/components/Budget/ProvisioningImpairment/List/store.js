import { TableStore, ModalStore, PageStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from '@/api/budget/provisioning/provisioning'
import moment from 'moment'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({})

  $table = new TableStore({
    request: async (params) => {
      const data = await Api.postInfoList({
        ...params,
      })
      return data
    },
  })
  createModal = new ModalStore({
    onFinish: async (values) => {
      const provisionDate = moment(values.provisionDate).endOf('month').format('YYYY-MM-DD')
      const data = await Api.postInfoAdd({ provisionDate })
      this.createModal.close()
      const { pathname } = this.page.getParams()
      history.push(`${pathname}/detail/${data.id}`)
    },
  })
}
export default new Store()
