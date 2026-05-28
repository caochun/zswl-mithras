import { makeAutoObservable } from '@zswl/admin'
import { message } from 'antd'
import { ModalStore, Modal } from '@zswl/components'
import { downFile } from '@/utils'
import Api from './api'
class Store {
  constructor(props) {
    this.baseStore = props?.baseStore
    makeAutoObservable(this)
  }

  adjustId = ''
  dataDetail = []
  getDataDetail = async (adjustId) => {
    if (adjustId) {
      const res = await Api.getReportList({
        adjustId,
        processInstanceId: undefined,
        businessVersion: this.baseStore?.page.getParams().businessVersion,
      })
      this.dataDetail = res ?? []
    }
  }
}
export default Store
