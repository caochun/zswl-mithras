import { makeAutoObservable, history, getQuery } from '@zswl/admin'
import { PageStore, TableStore } from '@zswl/components'
import { message } from 'antd'
import { userIsProjSponsor } from '@/utils'
import Api from '../api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({
    request: async (params) => {
      const { id, modelKey, businessVersion } = params
      const res = await Api.getBaseInfo({
        id,
        processType: modelKey,
        businessVersion,
      })
      return { ...res, isProjSponsor: userIsProjSponsor(res.belongSponsorId) } ?? {}
    },
  })

  // 初分修改
  modifyBaseInfo = async (params) => {
    await Api.modifyBaseInfo({ ...params, id: this.page.getParams().id })
    this.page.init()
  }

  historyTable = new TableStore({
    request: async (params) => {
      return Api.getHistory({
        ...params,
        assetClassifyClientId: this.page.getParams().id,
      })
    },
  })

  submitApproval = async () => {
    await Api.submitReview({ id: this.page.getParams().id })
    message.success('提交成功')
  }
}
export default Store
