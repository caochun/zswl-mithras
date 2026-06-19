import { FormStore, PageStore } from '@zswl/components'
import { history, makeAutoObservable } from '@zswl/admin'
import recordTableApi from '@/api/blackGray/recordTableApi'
import { message } from 'antd'
import manualOutboundFormApi from '@/api/blackGray/manualOutboundFormApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: (params) => {
      if (!params?.id) return {}
      return manualOutboundFormApi.postOutboundDetail(params)
    },
  })

  form = new FormStore({})
  save = async () => {
    const { id } = this.page.getParams()
    const params = await this.form.submit()
    const func = id ? recordTableApi.postRecordModify : recordTableApi.postRecordAdd
    await func({ ...params, id, source: 'INTERNAL' })
    message.success(`保存成功`)
    history.push('/blackListManage/enterDatabase/application')
  }
}
export default new Store()
