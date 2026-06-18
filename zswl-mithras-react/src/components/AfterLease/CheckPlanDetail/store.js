import { PageStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { message } from 'antd'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({
    request: async (params) => {
      const res = await Api.getCheckPlanDetail({
        id: params.id,
        businessVersion: params.businessVersion,
      })
      return res ?? {}
    },
  })

  onSubmit = async () => {
    await Api.processFinish({
      id: this.page.getData().id,
    })
    message.success('提交成功')
  }
}
export default Store
