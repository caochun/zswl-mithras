import { TableStore, PageStore } from '@zswl/components'
import { makeAutoObservable, history, getSessionStorage, setSessionStorage } from '@zswl/admin'
import { timeSecondFormat } from '@/utils'
import _, { debounce as _debounce } from 'lodash'
import customCycleApi from '@/api/lifeCycle/customCycleApi'
import Api from '@/api/common/selectApi'

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
  industry = []
  industryEnum = []
  initIndustry = async () => {
    const getLocalIndustryMap = getSessionStorage('industryMap')
    const getLocalIndustryEnum = getSessionStorage('industryEnum')
    if (getLocalIndustryMap) {
      this.industry = getLocalIndustryMap
      this.industryEnum = getLocalIndustryEnum
      return
    }
    const list = await Api.getAllIndustry()
    this.industry = list
    const formatIndustryList = (list) => {
      const result = []
      const dfs = (data, parentLabels = []) => {
        data.forEach((item) => {
          if (item.children?.length) {
            dfs(item.children, [...parentLabels, item.label])
          } else {
            result.push({
              label: [...parentLabels, item.label].join('/'),
              value: item.value,
            })
          }
        })
      }
      dfs(list)
      return result
    }
    this.industryEnum = formatIndustryList(list)
    setSessionStorage('industryEnum', JSON.stringify(this.industryEnum))
    setSessionStorage('industryMap', JSON.stringify(list))
  }
  regionList = []
  initRegionList = async () => {
    const list = await Api.getRegionList({ code: '156' })
    this.regionList = list.map((item) => {
      if (item.value == '810000' || item.value == '820000' || item.value == '710000') {
        return {
          ...item,
          isLeaf: true,
        }
      }
      return {
        ...item,
        isLeaf: item.leaf,
      }
    })
  }
  loadRegionListChild = async (selectedOptions) => {
    const targetOption = selectedOptions[selectedOptions.length - 1]
    targetOption.loading = true
    const data = await Api.getRegionList({ code: targetOption.value })
    let arr = data.map((v) => {
      return { ...v, isLeaf: v.leaf }
    })
    targetOption.loading = false
    targetOption.children = arr
    this.regionList = [...this.regionList]
  }
  toDetail = (id, bizType) => {
    if (id) {
      history.push(`/customer/maintain/detail/${id}?bizType=${bizType}`)
    }
  }
}
export default Store
