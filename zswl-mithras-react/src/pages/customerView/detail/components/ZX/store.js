import { makeAutoObservable } from '@zswl/admin'
import { Modal, Table } from '@zswl/components'
import {
  getDatabaseList,
  getPublicEnums,
  addData,
  deleteDatabase,
  saveModify,
  dispatchExecute,
  getDloagDataSourceList,
  DloagDataSaveAdd,
  DloagDataSaveModify,
  DloagDeleted,
  getInstanceList,
} from './api'
import { message } from 'antd'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  // 新增
  writeTypeEnum = []

  async init(params) {
    const { data } = await deleteDatabase()
    this.writeTypeEnum = data
  }

  Base = new Table.Store({
    request: async (params) => {
      return this.init(params)
    },
  })
  Summary = new Table.Store({
    request: async (params) => {
      return [
        {
          key: '1',
          firstCreditYear: '2021',
          institutionCount: '42',
          unsettledCreditInstitutionCount: '36',
          firstRepaymentYear: '2012',
        },
      ]
    },
  })

  UnresolvedCredit = new Table.Store({
    request: async (params) => {
      return [
        {
          key: '1',
          type: '中长期借款',
          normalAccounts: 8,
          normalAmount: 65169.67,
          attentionAccounts: 0,
          attentionAmount: 0,
          nonPerformingAccounts: 0,
          nonPerformingAmount: 0,
          totalAccounts: 8,
          totalAmount: 65169.67,
        },
        {
          key: '2',
          type: '短期借款',
          normalAccounts: 3,
          normalAmount: 29000,
          attentionAccounts: 0,
          attentionAmount: 0,
          nonPerformingAccounts: 0,
          nonPerformingAmount: 0,
          totalAccounts: 3,
          totalAmount: 29000,
        },
        {
          key: '3',
          type: '合计',
          normalAccounts: 11,
          normalAmount: 94169.69,
          attentionAccounts: 0,
          attentionAmount: 0,
          nonPerformingAccounts: 0,
          nonPerformingAmount: 0,
          totalAccounts: 11,
          totalAmount: 94169.69,
        },
      ]
    },
  })

  // 多级表头不支持
  CreditAgreement = new Table.Store({
    request: async (params) => {
      return [
        {
          key: '1',
          nonRevolvingTotal: 1300900,
          nonRevolvingUsed: 848200,
          nonRevolvingRemaining: 452700,
          revolvingTotal: 600000,
          revolvingUsed: 600000,
          revolvingRemaining: 0,
        },
      ]
    },
  })
  BaRepaymentResponsibilitye = new Table.Store({
    request: async (params) => {
      return this.init(params)
    },
  })
  ClosedCredit = new Table.Store({
    request: async (params) => {
      return this.init(params)
    },
  })

  DebtHistory = new Table.Store({
    request: async (params) => {
      return this.init(params)
    },
  })
}

export default new Store()
