import { message } from 'antd'
import { history, makeAutoObservable } from '@zswl/admin'
import { PageStore } from '@zswl/components'
import policyLedgerApi from '@/api/afterLease/policyLedgerApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async ({ id }) => {
      return await policyLedgerApi.postContractDetail({ contractId: id, dataSource: 1 })
    },
  })
  submit = async () => {
    await policyLedgerApi.postTmpSync({ contractId: this.page.getData().id })
    message.success('同步成功')
    history.push('/afterLease/policyManage')
  }
}
export default Store
