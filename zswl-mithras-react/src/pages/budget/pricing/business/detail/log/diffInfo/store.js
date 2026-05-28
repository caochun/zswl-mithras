import { makeAutoObservable } from '@zswl/admin'
import { hasValue } from '@/utils'
import { FormStore, PageStore } from '@zswl/components'
import mathjs from '@/utils/math'
import Api from '@/api/baseData/ftpQuarterlyGuidance'
import newFtpBaseInfoApi from '@/api/newFtp/newFtpBaseInfoApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({
    request: async ({ id }) => {
      const res = await newFtpBaseInfoApi.postFtpPreVersion({ id })
      return res
    },
  })
  form = new FormStore()
  formatPercent = (val) => {
    return hasValue(val) ? val / 10000 : undefined
  }
}
export default new Store()
