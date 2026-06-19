import { TableStore, App } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from '@/api/cpm/collectionWriteOffApi'
import { timeFormat } from '@/utils'
import { debounce as _debounce } from 'lodash'
import { message } from 'antd'

const { getData } = App
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: (searchData) => {
      const currentData = {
        ...searchData,
        amountFrom:
          searchData.amount && searchData.amount[0] ? searchData.amount[0] * 10000 : undefined,
        amountTo:
          searchData.amount && searchData.amount[1] ? searchData.amount[1] * 10000 : undefined,
        planCollectionDateFrom: searchData.planCollectionDate
          ? timeFormat(searchData.planCollectionDate[0])
          : undefined,
        planCollectionDateTo: searchData.planCollectionDate
          ? timeFormat(searchData.planCollectionDate[1])
          : undefined,
        collectionDateFrom: searchData.collectionDate
          ? timeFormat(searchData.collectionDate[0])
          : undefined,
        collectionDateTo: searchData.collectionDate
          ? timeFormat(searchData.collectionDate[1])
          : undefined,
      }
      return Api.getList(currentData)
    },
  })

  options = getData().optionsType
  getKeyOptionsLabelMap = (key) => {
    const obj = {}
    if (this.options && this.options[key]) {
      this.options[key].forEach((item) => {
        const { label, value } = item
        obj[value] = label
      })
    }
    return obj
  }

  toDetail = (id, bizType) => {
    if (id) {
      history.push(`/customer/maintain/detail/${id}?bizType=${bizType}`)
    }
  }
  export = async () => {
    const params = this.table.getParams()
    const { amount, planCollectionDate, collectionDate, ...rest } = params

    const currentData = {
      amount: undefined,
      collectionDate: undefined,
      planCollectionDate: undefined,

      amountFrom: amount?.length ? amount[0] * 10000 : undefined,
      amountTo: amount?.length ? amount[1] * 10000 : undefined,
      planCollectionDateFrom: planCollectionDate?.length
        ? timeFormat(planCollectionDate[0])
        : undefined,
      planCollectionDateTo: planCollectionDate ? timeFormat(planCollectionDate[1]) : undefined,
      collectionDateFrom: collectionDate ? timeFormat(collectionDate[0]) : undefined,
      collectionDateTo: collectionDate ? timeFormat(collectionDate[1]) : undefined,
    }
    await Api.exportList({ ...rest, ...currentData, pageSize: 5000 })
  }
}
export default new Store()
