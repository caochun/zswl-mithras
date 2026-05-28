import { makeAutoObservable } from '@zswl/admin'
import { hasValue, compareDetail } from '@/utils'
import { PageStore } from '@zswl/components'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({})

  compareData = {}
  setCompareData = (data) => {
    this.compareData = data
  }
  init = async (id) => {
    const res = await Api.compareVersionList({ id })
    this.setCompareData(res ?? {})
  }

  formatPercent = (val) => {
    return hasValue(val) ? val / 10000 : undefined
  }

  formatNewData = (values, key, formatFn = (v) => v) => {
    return {
      beforeValue: values[key]?.beforeValue,
      isChange: values[key]?.isChange,
      value: formatFn(values[key]?.value),
    }
  }

  getBaseInfoData = (values, type) => {
    if (type === 'old') {
      return { detail: values }
    } else {
      return compareDetail(values)
    }
  }
  getBoajiaData = (values, type) => {
    if (type === 'old') {
      return { detail: values }
    } else {
      return compareDetail(values)
    }
  }
}
export default new Store()
