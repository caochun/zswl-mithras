import { DrawerStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { mergeArray } from '@/utils/domains/dashboard/DashboardUtils'
import { initFieldsConfig } from './Config'
import Api from '@/api/dashboard/finance'
import moment from 'moment'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  date = '' // 日期
  setDate = (date) => {
    this.date = date
  }
  curCardData = {}
  setCurCardData = (data) => {
    this.curCardData = data
  }
  listDrawer = new DrawerStore({})

  getFieldsApi = async (val) => {
    // console.log('val: ', val) //下面这个接口可以改成传参的那种
    const res = await Api.postDashboardFinanceStatisticsList(
      val?.date ? { queryDate: val?.date } : {}
    )
    return mergeArray(res, initFieldsConfig)
  }
}
export default Store
