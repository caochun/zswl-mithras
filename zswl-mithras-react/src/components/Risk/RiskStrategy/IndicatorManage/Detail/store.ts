import { makeAutoObservable } from '@zswl/admin'
import { FormStore, PageStore } from '@zswl/components'
import Api from '@/api/risk/riskControlStrategyApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async ({ id, date }) => {
      const res = await Api.postStrategyDetail({ id, date })
      const earlyWarningValue = [
        {
          comparisonMethod: res.comparisonMethodOne,
          earlyWarningValue: res.earlyWarningValueOne,
          valueUnit: res.valueUnitOne,
        },
      ]
      if (res.comparisonMethodTwo) {
        earlyWarningValue.push({
          comparisonMethod: res.comparisonMethodTwo,
          earlyWarningValue: res.earlyWarningValueTwo,
          valueUnit: res.valueUnitTwo,
        })
      }

      res.earlyWarningValue = earlyWarningValue

      const limitValue = [
        {
          comparisonMethod: res.comparisonMethodOne,
          earlyWarningValue: res.limitValueOne,
          valueUnit: res.valueUnitOne,
        },
      ]
      if (res.comparisonMethodTwo) {
        limitValue.push({
          comparisonMethod: res.comparisonMethodTwo,
          earlyWarningValue: res.limitValueTwo,
          valueUnit: res.valueUnitTwo,
        })
      }

      res.limitValue = limitValue

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
