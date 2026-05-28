import { PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
// import fundReceiptRepayBaseInfoApi from '@/api/financial/fundReceiptRepayBaseInfoApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  // page = new PageStore({
  //   request: async ({ isFormApproval, ...rest }) => {
  //     const params = {
  //       ...rest,
  //     }
  //     const res = await fundReceiptRepayBaseInfoApi.postInfoDetail({ id: params?.id })
  //     return { newDetail: res }
  //   },
  // })
  // table = new TableStore({
  //   request: async (params) => {
  //     return await fundReceiptRepayBaseInfoApi.postInfoList(params)
  //   },
  // })
}
export default new Store()
