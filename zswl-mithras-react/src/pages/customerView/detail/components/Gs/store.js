import { makeAutoObservable, getQuery } from '@zswl/admin'
import { Modal, Table } from '@zswl/components'
import { DescStore } from '@zswl/components'
import { queryCompanyBasicInfo, detailqueryStockHolders, deleteDatabase } from './api'
import { message } from 'antd'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  // 新增
  writeTypeEnum = []

  async init(params) {
    const { data } = await queryCompanyBasicInfoByKeyword({})
    this.writeTypeEnum = data
  }
  uscc = ''
  enterpriseName = ''
  queryRfsh() {
    const { uscc, enterpriseName } = getQuery()
    this.uscc = uscc
    this.enterpriseName = enterpriseName
  }
  base = new DescStore({
    request: async (params) => {
      this.queryRfsh()
      const res = await queryCompanyBasicInfo({
        ...params,
        enterpriseName: this.enterpriseName,
      })
      return res
    },
  })

  // 发债信息
  bondInformationStore = new Table.Store({
    request: async (params) => {
      this.queryRfsh()

      return await deleteDatabase({ uscc: this.uscc, ...params })
    },
  })
  // 股东信息
  principalShareholderStore = new Table.Store({
    request: async (params) => {
      this.queryRfsh()
      return await detailqueryStockHolders({ enterpriseName: this.enterpriseName, ...params })
    },
  })
}

export default new Store()
