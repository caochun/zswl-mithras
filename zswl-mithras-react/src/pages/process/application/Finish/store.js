import { makeAutoObservable } from '@zswl/admin'
import { TableStore, Modal } from '@zswl/components'
import Api from '@/api/process/application/myProcessApi'

class Store {
  constructor() {
    makeAutoObservable(this, { clientId: false })
  }
  //地址信息
  table = new TableStore({
    request: async (parameter) => {
      const { projName, projCode, contractCode, ...rest } = parameter
      return await Api.getFinishList({
        ...rest,
        extra: {
          projName,
          projCode,
          contractCode,
        },
      })
    },
  })
}
export default new Store()
