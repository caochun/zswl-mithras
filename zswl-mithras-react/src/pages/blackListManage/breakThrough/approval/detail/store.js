import { FormStore, PageStore, TableStore } from '@zswl/components'
import { history, makeAutoObservable } from '@zswl/admin'
import recordTableApi from '@/api/blackGray/recordTableApi'
import { message } from 'antd'
import queryExternalDataApi from '@/api/blackGray/queryExternalDataApi'
import approvalControlApi from '@/api/blackGray/approvalControlApi'
import approvalBreakthroughApi from '@/api/blackGray/approvalBreakthroughApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: (params) => {
      if (!params?.id) return {}
      return approvalBreakthroughApi.postBusinessDetail({ id: +params?.id })
    },
  })

  form = new FormStore({})

  submit = async (values) => {
    const { id } = this.page.getParams()
    await approvalControlApi.postBreakBusiness({ ...values, id })
    message.success(`提交成功`)
    history.push('/blackListManage/breakThrough/approval')
  }
}
export default new Store()
