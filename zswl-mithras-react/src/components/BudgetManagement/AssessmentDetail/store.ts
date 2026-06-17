import { makeAutoObservable } from '@zswl/admin'
import { PageStore, TableStore } from '@zswl/components'
import api from '@/api/budgetManagement/assessmentApi'
import { message } from 'antd'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async (params) => {
      const res = await api.postExamineDetail(params)
      return res
    },
  })
  submit = async () => {
    const { id } = this.page.getParams()
    await api.postExamineSubmit({ id })
    message.success('提交成功')
    this.page.init()
  }
}

export default new Store()
