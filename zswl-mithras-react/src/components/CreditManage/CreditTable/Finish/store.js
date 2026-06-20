import { TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { timeSecondFormat } from '@/utils'
import { getHeaderWithFunctionCode } from '../../CreditTableConfig/CreditTableConfig'
import Api from '@/api/credit/creditTableFinish'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  $batchTable = new TableStore({
    request: (params) => {
      const { reportTime } = params
      return Api.getBatchList({
        ...params,
        reportTime: undefined,
        reportTimeFrom: reportTime ? timeSecondFormat(reportTime[0]) : undefined,
        reportTimeTo: reportTime ? timeSecondFormat(reportTime[1]) : undefined,
      })
    },
  })
  $accountTable = new TableStore({
    request: (params) => {
      return Api.getAccountList(
        {
          ...params,
          channel: 'EFFECT',
        },
        getHeaderWithFunctionCode({
          channel: 'EFFECT',
          humpPath: 'crAccountList',
        })
      )
    },
  })

  channel = 'PROC_BATCH' // PROC_BATCH | EFFECT
  setChannel = (e) => {
    this.channel = e.target.value
  }
  batchId = '' // PROC_BATCH 场景
  accountId = '' // EFFECT 场景

  showDrawer = false
  setShowDrawer = () => {
    this.showDrawer = !this.showDrawer
  }
  batchNo = ''
  onRowClick = ({ record }) => {
    if (this.channel === 'PROC_BATCH') {
      this.batchId = record.id
      this.batchNo = record.batchNo
    } else {
      this.accountId = record.id?.value
      this.batchNo = ''
    }
    this.setShowDrawer()
  }
}
export default Store
