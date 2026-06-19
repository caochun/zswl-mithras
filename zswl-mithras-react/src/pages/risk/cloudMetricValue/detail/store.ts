import { makeAutoObservable } from '@zswl/admin'
import { FormStore, PageStore } from '@zswl/components'
import Api from '@/api/risk/cloudMetricValue'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async ({ id, date }) => {
      const res = await Api.postMetricDetail({ id, date })
      return res
    },
  })

  save = async (data: any, field: string) => {
    const detaiData = this.page.getData()
    const defaultParams = {
      earlyWarningValueOne: detaiData?.earlyWarningValueOne,
      earlyWarningValueTwo: detaiData?.earlyWarningValueTwo,
      limitValueOne: detaiData?.limitValueOne,
      limitValueTwo: detaiData?.limitValueTwo,
    }
    const id = this.page.getParams()?.id
    const dataOne = data[field][0].earlyWarningValue
    const dataTwo = data[field][1]?.earlyWarningValue
    const earlyWarningState = data?.earlyWarningState
    const params = {
      ...defaultParams,
      id,
      [`${field}One`]: dataOne,
      [`${field}Two`]: dataTwo,
      earlyWarningState,
    }
    await Api.postStrategyModify(params)
    await this.page.init()
  }
  form = new FormStore({})
}
export default new Store()
