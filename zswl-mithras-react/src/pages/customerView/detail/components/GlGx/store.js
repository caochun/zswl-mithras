import { makeAutoObservable, http, getQuery } from '@zswl/admin'
import { Modal, Table } from '@zswl/components'
import { DescStore } from '@zswl/components'
import { message } from 'antd'
class Store {
  constructor() {
    makeAutoObservable(this)
  }

  table = new Table.Store({
    request: async (params) => {
      const { uscc, enterpriseName } = getQuery()
      return await http.post('/customer/view/detail/relation', {
        enterpriseName: enterpriseName,
        uscc: uscc,
        ...params,
      })
    },
  })
}

export default new Store()
