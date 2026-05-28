import { makeAutoObservable } from '@zswl/admin'
import { TableStore } from '@zswl/components'
import Api from './api'
class Store {
  constructor() {
    makeAutoObservable(this, { clientId: false })
  }
  //地址信息
  table = new TableStore({
    request: async (parameter) => {
      const { projName, projCode, contractCode, ...rest } = parameter
      return await Api.getList({
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
