import { Modal, ModalStore, TableStore } from '@zswl/components'
import { history, makeAutoObservable } from '@zswl/admin'
import moment from 'moment'
import Api from '@/api/financial/payableInterestApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: async (params) => {
      return Api.postInterestPayList(params)
    },
  })
  chooseMonthModal = new ModalStore({
    onFinish: async (values) => {
      const { months } = values

      await Api.postInterestPayCalculate({
        startYearAndMonth: moment(months).format('YYYY-MM'),
      })
      this.chooseMonthModal.close()
      this.table.search()
    },
  })
}
export default Store
