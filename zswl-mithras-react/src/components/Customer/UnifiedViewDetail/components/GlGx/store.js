import { makeAutoObservable, getQuery } from '@zswl/admin'
import { Modal, Table } from '@zswl/components'
import { DescStore } from '@zswl/components'
import { message } from 'antd'
import { getRelation } from '@/api/customerView/customerDetailApi'
class Store {
  constructor() {
    makeAutoObservable(this)
  }

  table = new Table.Store({
    request: async (params) => {
      const { uscc, enterpriseName } = getQuery()
      return await getRelation({
        enterpriseName: enterpriseName,
        uscc: uscc,
        ...params,
      })
    },
  })
}

export default new Store()
