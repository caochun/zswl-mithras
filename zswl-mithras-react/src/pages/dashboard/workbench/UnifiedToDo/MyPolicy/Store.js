import { DrawerStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { message } from 'antd'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  activityKey = 'NOT_OVERDUE'
  setActivityKey = (value) => {
    this.activityKey = value
  }

  policyRecord = {}
  setPolicyRecord = (data) => {
    this.policyRecord = data
  }

  createDrawer = new DrawerStore({})

  // 操作：新增保单
  handleCreate = (record) => {
    this.setPolicyRecord(record)
    this.createDrawer.open()
  }

  // 抽屉确认
  handleDrawerConfirm = async () => {
    await Api.policySubmit({
      parentId: this.policyRecord.id,
    })
    message.success('操作成功')
    this.handleDrawerClose()
  }
  // 抽屉关闭
  handleDrawerClose = () => {
    this.createDrawer.close()
    this.table.search()
  }

  table = new TableStore({
    pagination: {
      pageSize: 5,
    },
    request: (tableParams) => {
      return Api.postPolicyProjList({ ...tableParams, policyOverdueType: this.activityKey })
    },
  })

  batchDown = async () => {
    const { rows } = this.table.getSelected()
    const policyIds = []
    const paymentPolicyIds = []
    rows.forEach((item) => {
      if (item.dataSource === 'policy') {
        policyIds.push(item.id)
      }
      if (item.dataSource === 'payment') {
        paymentPolicyIds.push(item.id)
      }
    })
    await Api.postMaintenancePolicyExport({
      policyIds,
      paymentPolicyIds,
      policyOverdueType: this.activityKey,
    })
  }
}
export default Store
