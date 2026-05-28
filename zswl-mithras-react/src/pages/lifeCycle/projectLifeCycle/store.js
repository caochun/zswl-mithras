import { TableStore, Modal, ModalStore, App, PageStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from './api'
import { timeSecondFormat } from '@/utils'
import { debounce as _debounce } from 'lodash'
import { processTypeList } from './Context'

const { getData } = App
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  selectedType = ''
  setSelectedType = (val) => {
    this.selectedType = val
    this.table.reset()
    // this.table.search({ page: 1 })
  }

  page = new PageStore({
    request: async (params) => {
      const allTableRequest = processTypeList.map((item) => {
        return Api.getList({
          projStage: item.params,
        })
      })
      const allTableListData = await Promise.all(allTableRequest)
      const allTableCount = await Api.getQuantityCount(params)
      return {
        allTableListData,
        allTableCount,
      }
    },
  })
  table = new TableStore({
    request: (searchData) => {
      const currentData = {
        ...searchData,
        projStage: this.selectedType,
        projestablishFrom: searchData.projestablish
          ? timeSecondFormat(searchData.projestablish[0].startOf('D'))
          : undefined,
        projestablishTo: searchData.projestablish
          ? timeSecondFormat(searchData.projestablish[1].endOf('D'))
          : undefined,
      }
      return Api.getList(currentData)
    },
  })

  toDetail = (id, bizType) => {
    if (id) {
      history.push(`/customer/maintain/detail/${id}?bizType=${bizType}`)
    }
  }
}
export default new Store()
