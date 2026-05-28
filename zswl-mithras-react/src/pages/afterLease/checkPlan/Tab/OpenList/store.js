import { TableStore, ModalStore, PageStore, Modal } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import { yearFormat } from '@/utils'
import { message } from 'antd'
import moment from 'moment'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  staticInfo = {}
  getStatistics = async (currentData) => {
    const res = await Api.getStatistics(currentData)
    this.staticInfo = res ?? {}
  }

  $table = new TableStore({
    request: async (searchData) => {
      const currentData = {
        ...searchData,
        targetMonth: searchData.targetMonth
          ? moment(searchData.targetMonth).format('yyyy-MM') + '-01'
          : undefined,
      }
      this.getStatistics(currentData)
      return Api.getExternalList(currentData)
    },
  })
}
export default new Store()
