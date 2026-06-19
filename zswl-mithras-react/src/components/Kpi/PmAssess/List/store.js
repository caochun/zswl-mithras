import { TableStore, ModalStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import moment from 'moment'
import Api from '@/api/kpi/pmAssess'

class Store {
  constructor({ baseParams }) {
    this.baseParams = baseParams
    makeAutoObservable(this)
  }
  $table = new TableStore({
    request: (params) => {
      const { quarterInfo, ...rest } = params
      const year = quarterInfo ? moment(quarterInfo).get('year') : undefined
      const quarter = quarterInfo ? moment(quarterInfo).get('quarter') : undefined
      return Api.getList({
        ...rest,
        year,
        quarter,
      })
    },
  })
  currentItem = {}
  $editModal = new ModalStore({
    onOpen: async (record) => {
      this.currentItem = record
      return record
    },
  })
}
export default Store
