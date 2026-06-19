import { TableStore, ModalStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import moment from 'moment'
import newFtpBaseInfoApi from '@/api/budget/pricing/ftp/newFtpBaseInfoApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: async (searchData) => {
      try {
        return await newFtpBaseInfoApi.postInfoList(searchData)
      } catch (err) {
        return []
      }
    },
  })

  createModal = new ModalStore({
    onFinish: async (values) => {
      const { date, pricingFrequency } = values
      const month = moment(date).format('yyyy-MM-01')
      const params = {
        month,
        pricingFrequency,
      }
      const data = await newFtpBaseInfoApi.postInfoAdd(params)
      this.createModal.close()
      this.table.search()
      history.push(`/budget/pricing/business/detail/${data}?newProject=true&month=${month}`)
    },
  })
  calc = async ({ id }) => {
    return await newFtpBaseInfoApi.postInfoCalculate({ id })
  }
}
export default Store
