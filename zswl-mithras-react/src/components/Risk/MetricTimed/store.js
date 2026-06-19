import { makeAutoObservable, setSessionStorage, getSessionStorage } from '@zswl/admin'
import { TableStore } from '@zswl/components'
import Api from '@/api/risk/metricTimed'
import moment from 'moment'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: async (params) => {
      const { industryType, dataTime } = params
      const industryTypes = industryType ? industryType[industryType.length - 1] : ''
      return Api.list({
        ...params,
        industryType: industryTypes,
        dataTime: dataTime ? moment(dataTime).format('yyyy-MM') + '-01' : undefined,
      })
    },
  })

  allSelect = {}
  getSelect = async () => {
    const res = await Api.allSelect()
    this.allSelect = {
      ...res,
      needReportEumn: [
        {
          value: true,
          label: '是',
        },
        {
          value: false,
          label: '否',
        },
      ],
    }
  }
  industry = []
  initIndustry = async () => {
    const getLocalIndustryMap = getSessionStorage('industryMap')
    if (getLocalIndustryMap) {
      this.industry = getLocalIndustryMap
      return
    }
    const list = await Api.getAllIndustry()
    this.industry = list
    setSessionStorage('industryMap', JSON.stringify(list))
  }
}
export default new Store()
