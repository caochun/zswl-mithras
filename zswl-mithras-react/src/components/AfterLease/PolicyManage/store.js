import { TableStore, ModalStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import { message } from 'antd'
import policyLedgerApi from '@/api/afterLease/policyLedgerApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  $table = new TableStore({
    request: async (params) => {
      return policyLedgerApi.postLedgerList({ ...params, expires: this.expires })
    },
  })

  exportList = async () => {
    const params = this.$table.getParams()
    const { keys, rows } = this.$table.getSelected()
    const policyExportIds = []
    const paymentExportIds = []
    rows.map((item) => {
      if (item.dataSource === 'payment') {
        paymentExportIds.push(item.id)
      }
      if (item.dataSource === 'policy') {
        policyExportIds.push(item.id)
      }
    })
    await policyLedgerApi.postListExport({
      ...params,
      expires: this.expires,
      policyExportIds,
      paymentExportIds,
    })
  }

  expires = false
  onCheckboxChange = (e) => {
    const value = e.target.checked
    this.expires = value
    this.$table.search({ page: 1 })
  }
  addModal = new ModalStore({
    onOpen: async () => {},
    onFinish: async ({ paymentId }) => {
      // const id = await policyManageApi.postInfoAdd(values)
      this.addModal.close()
      history.push(`/afterLease/policyManage/addDetail/${paymentId}`)
    },
  })
  handleCreate = () => {
    this.addModal.open()
  }
}
export default Store
