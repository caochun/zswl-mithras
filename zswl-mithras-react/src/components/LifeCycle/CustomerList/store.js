import { TableStore, PageStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import _ from 'lodash'
import customCycleApi from '@/api/lifeCycle/customCycleApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  selectedType = 'TOTAL'
  setSelectedType = (val) => {
    this.selectedType = val
    this.table.search({ page: 1 })
  }
  page = new PageStore({
    request: async (params) => {
      const res = await customCycleApi.postLifecycleCard(params)
      return res
    },
  })
  table = new TableStore({
    request: (searchData) => {
      const { registerAddress = [], industryType = [], ...rest } = searchData

      const currentData = {
        ...rest,
        cardName: this.selectedType,
        provinceCode: registerAddress[0],
        cityCode: registerAddress[1],
        districtCode: registerAddress[2],
        industryType: _.last(industryType),
      }
      return customCycleApi.postLifecycleClientlist(currentData)
    },
  })
  toDetail = (id, bizType) => {
    if (id) {
      history.push(`/customer/maintain/detail/${id}?bizType=${bizType}`)
    }
  }
}
export default Store
